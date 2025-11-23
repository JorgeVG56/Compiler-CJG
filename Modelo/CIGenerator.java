package Modelo;
import java.util.*;

public class CIGenerator {
  private ArrayList<Instruction> data, text;
  private ArrayList<Token> tokens;
  private STM stm;
  
  private int position;
  private int cnt, cntJmp;
  
  public CIGenerator(STM stm){
    this.stm = stm;
    data = new ArrayList<>();
    text = new ArrayList<>();
  }
  
  public void reset(){
    data.clear();
    text.clear();
    data.add(new Instruction(null,"section", ".data"));
    data.add(new Instruction("nl","DB", "10"));
    data.add(new Instruction("msg_0","DB", "'FIN', 0xA, 0xD"));
    data.add(new Instruction("msgLen_0","EQU", "$- msg_0"));
    text.add(new Instruction(null, "section", ".text"));
    text.add(new Instruction(null, "global", "_start"));
    text.add(new Instruction("_start:", null, null));
    cnt = cntJmp = 1;
    position = 0;
  }
  
  public void generator(ArrayList<Token> tokens){
    reset();
    this.tokens = tokens;
    main_function();
    addEnd();
  }
  
  private void nxtPos(int n){ position += n; }
  
  private void move(String l, String r){ text.add(new Instruction(null, "MOV", l + ", " + r)); }
  
  private void swap(String l, String r){ text.add(new Instruction(null, "xchg", l + ", " + r)); }
  
  private void addVariable(String id, String ambit){
    String name = stm.getSymbol(id, ambit).getId() + "_" + stm.getSymbol(id, ambit).getAmbit();
    String command = stm.getSymbol(id, ambit).getRes();
    data.add(new Instruction(name, command, "0"));
  }
  
  private void setVariable(String id, String ambit, String value){
    move("  " + stm.getSymbol(id, ambit).getSize() + " " + stm.getSymbol(id, ambit).toString(), value);
  }
  
  private void addString(String string){
    data.add(new Instruction("msg_" + cnt, "DB", "'" + string + "', 0xA"));
    data.add(new Instruction("msgLen_" + cnt, "EQU", "$- msg_" + cnt));
  }
  
  private void addToStack(){ text.add(new Instruction(null, "PUSH", "eax")); }
  
  private void popFromStack(){ text.add(new Instruction(null, "POP", "ebx")); }
  
  private void main_function(){
    nxtPos(5);
    while(tokens.get(position).getId() != 9) statement("main");
    nxtPos(1);
  }
  
  private void statement(String ambit){
    if(tokens.get(position).getId() == 7){
      decl(ambit);
      nxtPos(1);
    }
    if(tokens.get(position).getId() == 21){
      assign(ambit);
      nxtPos(1);
    }
    if(tokens.get(position).getId() == 2) _if(ambit);
    if(tokens.get(position).getId() == 4) _while(ambit);
    if(tokens.get(position).getId() == 5){
      cout(ambit);
      nxtPos(1);
    }
  }
  
  private void decl(String ambit){
    nxtPos(1);
    String id = tokens.get(position++).getLexema();
    addVariable(id, ambit);
    if(tokens.get(position).getId() != 19) return;
    nxtPos(1);
    String value = expr(ambit);
    setVariable(id, ambit, value);
  }
  
  private void assign(String ambit){
    String id = tokens.get(position++).getLexema();
    nxtPos(1);
    String value = expr(ambit);
    setVariable(id, ambit, value);
  }
  
  private void _if(String ambit){
    nxtPos(2);
    String value = expr(ambit);
    int elseLabel = cntJmp++;
    int endLabel = cntJmp++;
    text.add(new Instruction(null, "CMP", value + ", 0"));
    text.add(new Instruction(null, "JE", "L" + elseLabel + ""));
    nxtPos(2);
    while(tokens.get(position).getId() != 9) statement(ambit + (cnt++));
    nxtPos(1);
    text.add(new Instruction(null, "JMP", "L" + endLabel));
    text.add(new Instruction("L" + elseLabel + ":", null, null));
    if(tokens.get(position).getId() != 3){
      text.add(new Instruction("L" + endLabel + ":", null, null));
      return;
    }
    nxtPos(2);
    while(tokens.get(position).getId() != 9) statement(ambit + (cnt++));
    nxtPos(1);
    text.add(new Instruction("L" + endLabel + ":", null, null));
  }
  
  private void _while(String ambit){
    nxtPos(2);
    int jmpLabel = cntJmp++;
    int endLabel = cntJmp++;
    text.add(new Instruction("L" + jmpLabel + ":", null, null));
    String value = expr(ambit);
    text.add(new Instruction(null, "CMP", value + ", 0"));
    text.add(new Instruction(null, "JE", "L" + endLabel));
    nxtPos(2);
    while(tokens.get(position).getId() != 9) statement(ambit+"_"+(cnt++));
    nxtPos(1);
    text.add(new Instruction(null, "JMP", "L" + jmpLabel));
    text.add(new Instruction("L" + endLabel + ":", null, null));
  }
  
  private boolean cout(String ambit){
    nxtPos(1);
    nxtPos(1);
    string();
    move("edx", "msgLen_" + cnt);
    move("ecx", "msg_" + cnt);
    move("ebx", "1");
    move("eax", "4");
    text.add(new Instruction(null, "INT", "0x80"));
    cnt++;
    return true;
  }
  
  private String expr(String ambit){ return condition(ambit); }
  
  private String condition(String ambit){
    String valueLeft = comparison(ambit);
    if(tokens.get(position).getId() != 20) return valueLeft;
    if(!valueLeft.equals("eax")) move("eax", valueLeft);
    addToStack();
    String op = tokens.get(position++).getType();
    String valueRight = comparison(ambit);
    if(!valueRight.equals("eax")) move("eax", valueRight);
    popFromStack();
    swap("eax", "ebx");
    if(op.equals("AND")) text.add(new Instruction(null, "AND", "eax, ebx"));
    if(op.equals("OR")) text.add(new Instruction(null, "OR", "eax, ebx"));
    return "eax";
  }
  
  private String comparison(String ambit){
    String valueLeft = term(ambit);
    if(tokens.get(position).getId() != 17) return valueLeft;
    if(!valueLeft.equals("eax")) move("eax", valueLeft);
    addToStack();
    String op = tokens.get(position++).getType();
    String valueRight = term(ambit);
    if(!valueRight.equals("eax")) move("eax", valueRight);
    popFromStack();
    swap("eax", "ebx");
    text.add(new Instruction(null, "CMP", "eax, ebx"));
    if(op.equals("LT")) text.add(new Instruction(null, "SETL", "al"));
    if(op.equals("MT")) text.add(new Instruction(null, "SETG", "al"));
    if(op.equals("LET")) text.add(new Instruction(null, "SETLE", "al"));
    if(op.equals("MET")) text.add(new Instruction(null, "SETGE", "al"));
    if(op.equals("EQUAL")) text.add(new Instruction(null, "SETE", "al"));
    text.add(new Instruction(null, "MOVZX", "eax, al"));
    return "eax";
  }
  
  private String term(String ambit){
    String valueLeft = factor(ambit);
    if(tokens.get(position).getId() != 13) return valueLeft;
    if(!valueLeft.equals("eax")) move("eax", valueLeft);
    addToStack();
    String op = tokens.get(position++).getType();
    String valueRight = factor(ambit);
    if(!valueRight.equals("eax")) move("eax", valueRight);
    popFromStack();
    swap("eax", "ebx");
    if(op.equals("PLUS")) text.add(new Instruction(null, "ADD", "eax, ebx"));
    if(op.equals("MINUS")) text.add(new Instruction(null, "SUB", "eax, ebx"));
    return "eax";
  }
  
  private String factor(String ambit){
    String valueLeft = primary(ambit);
    if(tokens.get(position).getId() != 14) return valueLeft;
    if(!valueLeft.equals("eax")) move("eax", valueLeft);
    addToStack();
    String op = tokens.get(position++).getType();
    String valueRight = primary(ambit);
    if(!valueRight.equals("eax")) move("eax", valueRight);
    popFromStack();
    swap("eax", "ebx");
    if(op.equals("MUL")) text.add(new Instruction(null, "MUL", "ebx")); 
    if(op.equals("DIV")) { 
      text.add(new Instruction(null, "XOR", "edx, edx")); 
      text.add(new Instruction(null, "DIV", "ebx"));
    }
    return "eax";
  }
  
  private String primary(String ambit){
    if(tokens.get(position).getId() == 10){
      nxtPos(1);
      String type = expr(ambit);
      nxtPos(1);
      return type;
    }
    if(tokens.get(position).getId() == 21){
      String id = tokens.get(position++).getLexema();
      return stm.getSymbol(id, ambit).toString();
    }
    if(tokens.get(position).getId() == 22){
      return number();
    }
    if(tokens.get(position).getId() == 6){
      String fl = tokens.get(position++).getLexema();
      return (fl.equals("true") ? "1" : "0");
    }
    return "error";
  }
  
  private String string(){
    nxtPos(1);
    String string = tokens.get(position++).getLexema();
    addString(string);
    nxtPos(1);
    return string;
  }

  private void addEnd(){
    text.add(new Instruction(null, "MOV", "edx, msgLen_0"));
    text.add(new Instruction(null, "MOV", "ecx, msg_0"));
    text.add(new Instruction(null, "MOV", "ebx, 1"));
    text.add(new Instruction(null, "MOV", "eax, 4"));
    text.add(new Instruction(null, "INT", "0x80"));
    text.add(new Instruction(null, "MOV", "ebx, 0"));
    text.add(new Instruction(null, "MOV", "eax, 1"));
    text.add(new Instruction(null, "INT", "0x80"));
  }
  
  private String number(){ return tokens.get(position++).getLexema(); }  
  
  public ArrayList<Instruction> getData() { return data; }
  
  public ArrayList<Instruction> getText() { return text; }
  
  @Override
  public String toString() {
    String string = "";
    for(Instruction s : data) string += s + "\n";
    for(Instruction s : text) string += s + "\n";
    return string;
  }
}
