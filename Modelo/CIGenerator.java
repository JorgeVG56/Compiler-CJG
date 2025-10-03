package Modelo;
import java.util.*;

public class CIGenerator {
  private ArrayList<String> data, bss, text;
  private ArrayList<Token> tokens;
  private ErrorHandler errorHandler;
  private STM stm;
  
  private int position;
  private int cnt, cntStack, cntJmp, maxStack;
  
  public CIGenerator(ErrorHandler errorHandler, STM stm){
    this.errorHandler = errorHandler;
    this.stm = stm;
    data = new ArrayList<>();
    bss = new ArrayList<>();
    text = new ArrayList<>();
  }

  public void reset(){
    data.clear();
    bss.clear();
    text.clear();
    data.add("section .data");
    data.add("  nl db 10");
    data.add("  msg_0 DB 'FIN', 0xA, 0xD");
    data.add("  msgLen_0 EQU $- msg_0");
    bss.add("section .bss");
    bss.add("  buf resb 32");
    text.add("section .text");
    text.add("  global _start");
    text.add("_start:");
    cnt = cntStack = cntJmp = maxStack = 1;
    position = 0;
  }
  
  public void generator(ArrayList<Token> tokens){
    reset();
    this.tokens = tokens;
    main_function();
    addEnd();
  }
  
  private boolean nxtPos(int n){
    position += n;
    return true;
  }
  
  private boolean addError(String message){
    errorHandler.addError(new Error("ERROR CI", -1, -1));
    errorHandler.addError(new Error(message, tokens.get(position).getLine(), tokens.get(position).getColumn()));
    return false;
  }

  private void move(String l, String r){
    text.add("  MOV " + l + ", " + r);
  }

  private void swap(String l, String r){
    text.add("  XOR " + l + ", " + r);
    text.add("  XOR " + r + ", " + l);
    text.add("  XOR " + l + ", " + r);
  }

  private void addVariable(String id, String ambit){
    bss.add("  " + stm.getSymbol(id, ambit).getId() + "_" + stm.getSymbol(id, ambit).getAmbit() + " " + stm.getSymbol(id, ambit).getRes() + " 1");
  }

  private void setVariable(String id, String ambit, String value){
    move("  " + stm.getSymbol(id, ambit).getSize() + " " + stm.getSymbol(id, ambit).toString(), value);
  }

  private void addString(String string){
    data.add("  msg_" + cnt + " DB '" + string + "', 0xA, 0xD");
    data.add("  msgLen_" + cnt + " EQU $- " + "msg_" + cnt);
  }

  private void addToStack(){
    move("  [tmp_" + cntStack + "]", "eax");
    cntStack++;
    if(cntStack > maxStack){
      bss.add("  tmp_" + maxStack + " RESD 1");
      maxStack ++;
    }
  }

  private void popFromStack(){
    cntStack--;
    move("ebx", "[tmp_" + cntStack + "]");
  }
  
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
    text.add("  CMP " + value + ", 0");
    text.add("  JE L" + elseLabel);
    nxtPos(2);
    while(tokens.get(position).getId() != 9) statement(ambit + (cnt++));
    nxtPos(1);
    text.add("  JMP L" + endLabel);
    text.add("\nL" + elseLabel + ":");
    if(tokens.get(position).getId() != 3){
      text.add("\nL" + endLabel + ":");
      return;
    }
    nxtPos(2);
    while(tokens.get(position).getId() != 9) statement(ambit + (cnt++));
    nxtPos(1);
    text.add("\nL" + endLabel + ":");
  }
  
  private void _while(String ambit){
    nxtPos(2);
    int jmpLabel = cntJmp++;
    int endLabel = cntJmp++;
    text.add("\nL" + jmpLabel + ":");
    String value = expr(ambit);
    text.add("  CMP " + value + ", 0");
    text.add("  JE L" + endLabel);
    nxtPos(2);
    while(tokens.get(position).getId() != 9) statement(ambit+"_"+(cnt++));
    nxtPos(1);
    text.add("  JMP L" + jmpLabel);
    text.add("\nL" + endLabel + ":");
  }
  
  private boolean cout(String ambit){
    nxtPos(1);
    do {
      nxtPos(1);
      if(tokens.get(position).getId() != 16){ 
        move("eax", expr(ambit));
        text.add("  CALL print_int");
      } else { 
        string();
        text.add("  MOV edx, msgLen_" + cnt);
        text.add("  MOV ecx, msg_" + cnt);
        text.add("  MOV ebx, 1");
        text.add("  MOV eax, 4");
        text.add("  INT 0x80");
        cnt++;
      }
    } while (tokens.get(position).getId() == 18);
    return true;
  }
  
  private String expr(String ambit){
    return condition(ambit);
  }
  
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
    if(op.equals("AND")) text.add("  AND eax, ebx");
    if(op.equals("OR")) text.add("  OR eax, ebx");
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
    text.add("  CMP eax, ebx"); 
    if(op.equals("LT")) text.add("  SETL al");
    if(op.equals("MT")) text.add("  SETG al");
    if(op.equals("LET")) text.add("  SETLE al");
    if(op.equals("MET")) text.add("  SETGE al");
    if(op.equals("EQUAL")) text.add("  SETE al");
    text.add("  MOVZX eax, al"); 
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
    if(op.equals("PLUS")) text.add("  ADD eax, ebx");
    if(op.equals("MINUS")) text.add("  SUB eax, ebx");
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
    if(op.equals("MULT")) text.add("  MUL ebx"); 
    if(op.equals("DIV")) { text.add("  XOR edx, edx"); text.add("  IDIV ebx"); }
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
    addError("Hubo un error inesperado");
    return "error";
  }
  
  private String string(){
    nxtPos(1);
    String string = tokens.get(position++).getLexema();
    addString(string);
    nxtPos(1);
    return string;
  }
  
  private String number(){
    String number = tokens.get(position++).getLexema();
    return number;
  }

  private void addEnd(){
    text.add("  MOV edx, msgLen_0");
    text.add("  MOV ecx, msg_0");
    text.add("  MOV ebx, 1");
    text.add("  MOV eax, 4");
    text.add("  INT 0x80");
    text.add("  MOV ebx, 0");
    text.add("  MOV eax, 1");
    text.add("  INT 0x80");
    text.add("\nprint_int:\n" + //
            "  push rbx\n" + //
            "  push rcx\n" + //
            "  push rdx\n" + //
            "  push rsi\n" + //
            "  push rdi\n" + //
            "  push r8\n" + //
            "  mov ecx, eax\n" + //
            "  xor r8b, r8b\n" + //
            "  cmp ecx, 0\n" + //
            "  jge .unsigned\n" + //
            "  neg ecx\n" + //
            "  mov r8b, 1\n" + //
            "\n" + //
            ".unsigned:\n" + //
            "  lea r9, [buf + 31]\n" + //
            "  xor r10, r10\n" + //
            "  mov eax, ecx\n" + //
            "  mov ebx, 10\n" + //
            "  cmp eax, 0\n" + //
            "  jne .loop\n" + //
            "  mov byte [r9], '0'\n" + //
            "  dec r9\n" + //
            "  inc r10\n" + //
            "  jmp .done\n" + //
            "\n" + //
            ".loop:\n" + //
            "  xor edx, edx\n" + //
            "  div ebx\n" + //
            "  add dl, '0'\n" + //
            "  mov [r9], dl\n" + //
            "  dec r9\n" + //
            "  inc r10\n" + //
            "  mov eax, eax\n" + //
            "  cmp eax, 0\n" + //
            "  jne .loop\n" + //
            "\n" + //
            ".done:\n" + //
            "  cmp r8b, 0\n" + //
            "  je .no_sign\n" + //
            "  mov byte [r9], '-'\n" + //
            "  dec r9\n" + //
            "  inc r10\n" + //
            "\n" + //
            ".no_sign:\n" + //
            "  lea rsi, [r9 + 1]\n" + //
            "  mov rdx, r10\n" + //
            "  mov rax, 1\n" + //
            "  mov rdi, 1\n" + //
            "  syscall\n" + //
            "  mov rax, 1\n" + //
            "  mov rdi, 1\n" + //
            "  lea rsi, [rel nl]\n" + //
            "  mov rdx, 1\n" + //
            "  syscall\n" + //
            "  pop r8\n" + //
            "  pop rdi\n" + //
            "  pop rsi\n" + //
            "  pop rdx\n" + //
            "  pop rcx\n" + //
            "  pop rbx\n" + //
            "  ret");
  }

  @Override
  public String toString() {
    String string = "";
    for(String s : data) string += s + "\n";
    for(String s : bss) string += s + "\n";
    for(String s : text) string += s + "\n";
    return string;
  }
}
