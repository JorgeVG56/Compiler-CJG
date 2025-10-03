package Modelo;
public class Error {
  private String type;
  private int line, column;

  public Error(String type, int line, int column){
    this.type = type;
    this.line = line;
    this.column = column;
  }

  public int getColumn() {
    return column;
  }

  public int getLine() {
    return line;
  }

  public String getType() {
    return type;
  }

  public String toString(){
    if(line == -1) return type;
    return line + ":" + column + " - " + type;
  }
}
