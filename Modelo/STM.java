package Modelo;
import java.util.ArrayList;

public class STM {
  private ArrayList<String> keywords = new ArrayList<>();
  private ArrayList<Symbol> symbols = new ArrayList<>();
  private ErrorHandler errorHandler;

  public STM(ErrorHandler errorHandler){ this.errorHandler = errorHandler; }

  public void reset(){ symbols.clear(); }

  public void addKeywords(){
    keywords.add("main");   keywords.add("if");   keywords.add("else");
    keywords.add("while");  keywords.add("cout"); keywords.add("int");
    keywords.add("bool");   keywords.add("true"); keywords.add("false");
  }

  public String getToken(String s){ return (keywords.contains(s) ? s.toUpperCase() : null); }

  public Symbol getSymbol(String id, String ambit){
    for(Symbol s : symbols) if(s.equals(new Symbol(id, null, ambit))) return s;
    return null;
  }

  public boolean addSymbol(String id, String type, String ambit){
    if(getSymbol(id, ambit) != null){
      errorHandler.addError(new Error("ERROR SEMANTICO", -1, -1));
      errorHandler.addError(new Error("La variable " + id + " ya ha sido declarada en el ambito " + ambit, -1, -1));
      return false;
    }
    symbols.add(new Symbol(id, type, ambit));
    return true;
  }

  public boolean assignValue(String id, String type, String ambit){
    Symbol s = getSymbol(id, ambit);
    if(s == null){
      errorHandler.addError(new Error("ERROR SEMANTICO", -1, -1));
      errorHandler.addError(new Error("La variable " + id + " no ha sido declarada en el ambito " + ambit, -1, -1));
      return false;
    }
    if(s.getType().equals(type)) return true;
    errorHandler.addError(new Error("ERROR SEMANTICO", -1, -1));
    errorHandler.addError(new Error("La variable " + id + " no acepta valores de tipo " + type , -1, -1));
    return false;  
  }

  public ArrayList<Symbol> getSymbols() { return symbols; }
}