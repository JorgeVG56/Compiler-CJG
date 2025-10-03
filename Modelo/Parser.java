package Modelo;
import java.util.ArrayList;

public class Parser {
  private ArrayList<Token> tokens;
  private ErrorHandler errorHandler;
  
  private int position;
  
  public Parser(ErrorHandler errorHandler){
    this.errorHandler = errorHandler;
  }
  
  public boolean parser(ArrayList<Token> tokens){
    this.tokens = tokens;
    position = 0;
    return main_function();
  }

  private boolean addError(String expected, Token found){
    errorHandler.addError(new Error("Error Sintactico", -1, -1));
    errorHandler.addError(new Error("Se esperaba <" + expected + ">, se encontro " + found.getType(), found.getLine(), found.getColumn()));
    return false;
  }

  private boolean main_function(){
    if(!tokens.get(position++).getType().equals("INT")) return addError("INT", tokens.get(position - 1));
    if(tokens.get(position++).getId() != 1) return addError("MAIN", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 10) return addError("LPAR", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 11) return addError("RPAR", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 8) return addError("LBRA", tokens.get(position - 1));
    while(tokens.get(position).getId() != 9) if(!statement()) return false;
    if(tokens.get(position++).getId() != 9) return addError("RBRA", tokens.get(position - 1));
    return position == tokens.size();
  }
  
  private boolean statement(){
    if(tokens.get(position).getId() == 7){
      if(!decl()) return false;
      if(tokens.get(position++).getId() != 12) return addError("SEMI", tokens.get(position - 1)); 
      return true;
    }
    if(tokens.get(position).getId() == 21){
      if(!assign()) return false;
      if(tokens.get(position++).getId() != 12) return addError("SEMI", tokens.get(position - 1));
      return true;
    }
    if(tokens.get(position).getId() == 2) return _if();
    if(tokens.get(position).getId() == 4) return _while();
    if(tokens.get(position).getId() == 5){
      if(!cout()) return false;
      if(tokens.get(position++).getId() != 12) return addError("SEMI", tokens.get(position - 1)); 
      return true;
    }
    errorHandler.addError(new Error("SYNTAX ERROR", -1, -1));
    errorHandler.addError(new Error("Invalid statement", tokens.get(position - 1).getLine(), tokens.get(position - 1).getColumn()));
    return false;
  }
  
  private boolean decl(){
    if(tokens.get(position++).getId() != 7) return addError("TYPE", tokens.get(position - 1));
    if(tokens.get(position++).getId() != 21) return addError("ID", tokens.get(position - 1)); 
    if(tokens.get(position).getId() != 19) return true;
    if(tokens.get(position++).getId() != 19) return addError("ASSIGN", tokens.get(position - 1));
    return expr();
  }
  
  private boolean assign(){
    if(tokens.get(position++).getId() != 21) return addError("ID", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 19) return addError("ASSIGN", tokens.get(position - 1)); 
    return expr();
  }
  
  private boolean _if(){
    if(tokens.get(position++).getId() != 2) return addError("IF", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 10) return addError("LPAR", tokens.get(position - 1)); 
    if(!expr()) return false;
    if(tokens.get(position++).getId() != 11) return addError("RPAR", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 8) return addError("LBRA", tokens.get(position - 1)); 
    while(tokens.get(position).getId() != 9) if(!statement()) return false;
    if(tokens.get(position++).getId() != 9) return addError("RBRA", tokens.get(position - 1)); 
    if(tokens.get(position).getId() != 3) return true;
    if(tokens.get(position++).getId() != 3) return addError("ELSE", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 8) return addError("LBRA", tokens.get(position - 1)); 
    while(tokens.get(position).getId() != 9) if(!statement()) return false;
    if(tokens.get(position++).getId() != 9) return addError("RBRA", tokens.get(position - 1)); 
    return true;
  }
  
  private boolean _while(){
    if(tokens.get(position++).getId() != 4) return addError("WHILE", tokens.get(position - 1));
    if(tokens.get(position++).getId() != 10) return addError("LPAR", tokens.get(position - 1)); 
    if(!expr()) return false;
    if(tokens.get(position++).getId() != 11) return addError("RPAR", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 8) return addError("LBRA", tokens.get(position - 1)); 
    while(tokens.get(position).getId() != 9) if(!statement()) return false;
    if(tokens.get(position++).getId() != 9) return addError("RBRA", tokens.get(position - 1)); 
    return true;
  }
  
  private boolean cout(){
    if(tokens.get(position++).getId() != 5) return addError("COUT", tokens.get(position - 1));
    if(tokens.get(position++).getId() != 18) return addError("OUT", tokens.get(position - 1)); 
    if(tokens.get(position).getId() != 16) return expr();
    else return string(); 
  }
  
  private boolean expr(){
    return condition();
  }
  
  private boolean condition(){
    if(!comparison()) return false;
    if(tokens.get(position).getId() != 20) return true;
    if(tokens.get(position++).getId() != 20) return addError("AND-OR", tokens.get(position - 1));
    return comparison();
  }
  
  private boolean comparison(){
    if(!term()) return false;
    if(tokens.get(position).getId() != 17) return true;
    if(tokens.get(position++).getId() != 17) return addError("LT-MT-LET-MET-EQUAL", tokens.get(position - 1));
    return term();
  }
  
  private boolean term(){
    if(!factor()) return false;
    if(tokens.get(position).getId() != 13) return true;
    if(tokens.get(position++).getId() != 13) return addError("PLUS-MINUS", tokens.get(position - 1));
    return factor();
  }
  
  private boolean factor(){
    if(!primary()) return false;
    if(tokens.get(position).getId() != 14) return true;
    if(tokens.get(position++).getId() != 14) return addError("MUL-DIV", tokens.get(position - 1));
    return primary();
  }
  
  private boolean primary(){
    if(tokens.get(position).getId() == 10){
      if(tokens.get(position++).getId() != 10) return addError("LPAR", tokens.get(position - 1)); 
      if(!expr()) return false;
      if(tokens.get(position++).getId() != 11) return addError("RPAR", tokens.get(position - 1)); 
      return true;
    }
    if(tokens.get(position).getId() == 21){
      if(tokens.get(position++).getId() != 21) return addError("ID", tokens.get(position - 1));
      return true;
    }
    if(tokens.get(position).getId() == 22) return number();
    if(tokens.get(position).getId() == 6){
      if(tokens.get(position++).getId() != 6) return addError("TRUE-FALSE", tokens.get(position - 1));
      return true;
    }
    errorHandler.addError(new Error("SYNTAX ERROR", -1, -1));
    errorHandler.addError(new Error("Invalid primary", tokens.get(position - 1).getLine(), tokens.get(position - 1).getColumn()));
    return false;
  }

  private boolean string(){
    if(tokens.get(position++).getId() != 16) return addError("QUOTE", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 21) return addError("ID", tokens.get(position - 1)); 
    if(tokens.get(position++).getId() != 16) return addError("QUOTE", tokens.get(position - 1)); 
    return true;
  }
  
  private boolean number(){
    if(tokens.get(position++).getId() != 22) return addError("NUM", tokens.get(position - 1)); 
    return true;
  }
}
