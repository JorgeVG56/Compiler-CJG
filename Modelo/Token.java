package Modelo;
public class Token {
  private String type, lexema;
  private int id, line, column;

  public Token(String type, String lexema, int line, int column){
    this.type = type;
    this.lexema = lexema;
    this.line = line;
    this.column = column;
    this.id = findId(type);
  }

  private int findId(String s){
    if(s.equals("MAIN")) return 1;
    if(s.equals("IF")) return 2;
    if(s.equals("ELSE")) return 3;
    if(s.equals("WHILE")) return 4;
    if(s.equals("COUT")) return 5;
    if(s.equals("TRUE")) return 6;
    if(s.equals("FALSE")) return 6;
    if(s.equals("INT")) return 7;
    if(s.equals("BOOL")) return 7;
    if(s.equals("LBRA")) return 8;
    if(s.equals("RBRA")) return 9;
    if(s.equals("LPAR")) return 10;
    if(s.equals("RPAR")) return 11;
    if(s.equals("SEMI")) return 12;
    if(s.equals("PLUS")) return 13;
    if(s.equals("MINUS")) return 13;
    if(s.equals("MUL")) return 14;
    if(s.equals("DIV")) return 14;
    if(s.equals("QUOTE")) return 16;
    if(s.equals("LT")) return 17;
    if(s.equals("MT")) return 17;
    if(s.equals("LET")) return 17;
    if(s.equals("MET")) return 17;
    if(s.equals("EQUAL")) return 17;
    if(s.equals("OUT")) return 18;
    if(s.equals("ASSIGN")) return 19;
    if(s.equals("OR")) return 20;
    if(s.equals("AND")) return 20;
    if(s.equals("ID")) return 21;
    if(s.equals("NUM")) return 22;
    return -1;
  }

  public int getId() {
    return id;
  }

  public String getLexema() {
    return lexema;
  }

  public String getType() {
    return type;
  }

  public int getColumn() {
    return column;
  }

  public int getLine() {
    return line;
  }

  @Override
  public String toString(){
    return "<" + type + "> " + lexema;
  }
}
