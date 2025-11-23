package Modelo;
import java.util.ArrayList;

public class Semantico {
  private ArrayList<Token> tokens;
  private ErrorHandler errorHandler;
  private STM stm;
  
  private int position;
  private int cnt;
  
  public Semantico(ErrorHandler errorHandler, STM stm){
    this.errorHandler = errorHandler;
    this.stm = stm;
  }
  
  public boolean semantico(ArrayList<Token> tokens){
    cnt = 0;
    position = 0;
    this.tokens = tokens;
    return main_function();
  }
  
  private boolean nxtPos(int n){
    position += n;
    return true;
  }
  
  private boolean addError(String message){
    errorHandler.addError(new Error("ERROR SEMANTICO", -1, -1));
    errorHandler.addError(new Error(message, tokens.get(position).getLine(), tokens.get(position).getColumn()));
    return false;
  }
  
  private boolean main_function(){
    nxtPos(5);
    while(tokens.get(position).getId() != 9) if(!statement("main")) return false;
    return nxtPos(1);
  }
  
  private boolean statement(String ambit){
    if(tokens.get(position).getId() == 7){
      if(!decl(ambit)) return false;
      return nxtPos(1);
    }
    if(tokens.get(position).getId() == 21){
      if(!assign(ambit)) return false;
      return nxtPos(1);
    }
    if(tokens.get(position).getId() == 2) return _if(ambit);
    if(tokens.get(position).getId() == 4) return _while(ambit);
    if(tokens.get(position).getId() == 5){
      if(!cout(ambit)) return false;
      return nxtPos(1);
    }
    return false;
  }
  
  private boolean decl(String ambit){
    String type = tokens.get(position++).getLexema();
    String id = tokens.get(position++).getLexema();
    if(!stm.addSymbol(id, type, ambit)) return false;
    if(tokens.get(position).getId() != 19) return true;
    nxtPos(1);
    String value = expr(ambit);
    return stm.assignValue(id, value, ambit);
  }
  
  private boolean assign(String ambit){
    String id = tokens.get(position++).getLexema();
    nxtPos(1);
    String value = expr(ambit);
    return stm.assignValue(id, value, ambit);
  }
  
  private boolean _if(String ambit){
    nxtPos(2);
    String value = expr(ambit);
    if(value.equals("error")) return false;
    if(!value.equals("bool")) return addError("La expresion del if debe ser booleana");
    nxtPos(2);
    while(tokens.get(position).getId() != 9) if(!statement(ambit + (cnt++))) return false;
    nxtPos(1);
    if(tokens.get(position).getId() != 3) return true;
    nxtPos(2);
    while(tokens.get(position).getId() != 9) if(!statement(ambit + (cnt++))) return false;
    return nxtPos(1);
  }
  
  private boolean _while(String ambit){
    nxtPos(2);
    String value = expr(ambit);
    if(value.equals("error")) return false;
    if(!value.equals("bool")) return addError("La expresion del while debe ser booleana");
    nxtPos(2);
    while(tokens.get(position).getId() != 9) if(!statement(ambit+"_"+(cnt++))) return false;
    return nxtPos(1);
  }
  
  private boolean cout(String ambit){
    nxtPos(1);
    nxtPos(1);
    string();
    return true;
  }
  
  private String expr(String ambit){
    return condition(ambit);
  }

  private String condition(String ambit){
    String valueLeft = comparison(ambit);
    if(tokens.get(position).getId() != 20) return valueLeft;
    nxtPos(1);
    String valueRight = comparison(ambit);
    if(valueLeft.equals("error") || valueRight.equals("error")) return "error";
    if(valueLeft.equals("bool") && valueRight.equals("bool")) return "bool";
    addError("Ambos lados de la operacion deben ser booleanos");
    return "error";
  }
  
  private String comparison(String ambit){
    String valueLeft = term(ambit);
    if(tokens.get(position).getId() != 17) return valueLeft;
    nxtPos(1);
    String valueRight = term(ambit);
    if(valueLeft.equals("error") || valueRight.equals("error")) return "error";
    if(!valueLeft.equals("bool") && !valueRight.equals("bool")) return "bool";
    addError("Ambos lados de la operacion deben ser enteros");
    return "error";
  }
  
  private String term(String ambit){
    String valueLeft = factor(ambit);
    if(tokens.get(position).getId() != 13) return valueLeft;
    nxtPos(1);
    String valueRight = factor(ambit);
    if(valueLeft.equals("error") || valueRight.equals("error")) return "error";
    if(!valueLeft.equals("bool") && !valueRight.equals("bool")) return "int";
    addError("Ambos lados de la operacion deben ser enteros");
    return "error";
  }
  
  private String factor(String ambit){
    String valueLeft = primary(ambit);
    if(tokens.get(position).getId() != 14) return valueLeft;
    nxtPos(1);
    String valueRight = primary(ambit);
    if(valueLeft.equals("error") || valueRight.equals("error")) return "error";
    if(!valueLeft.equals("bool") && !valueRight.equals("bool")) return "int";
    addError("Ambos lados de la operacion deben ser enteros");
    return "error";
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
      if(stm.getSymbol(id, ambit) != null) return stm.getSymbol(id, ambit).getType();
      addError("Variable " + id + " no encontrada");
      return "error";
    }
    if(tokens.get(position).getId() == 22){
      return number();
    }
    if(tokens.get(position).getId() == 6){
      nxtPos(1);
      return "bool";
    }
    addError("Hubo un error inesperado");
    return "error";
  }
  
  private String string(){
    nxtPos(3);
    return "string";
  }
  
  private String number(){
    nxtPos(1);
    return "int";
  }
}
