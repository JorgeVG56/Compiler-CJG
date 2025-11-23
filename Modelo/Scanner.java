package Modelo;
import java.io.*;
import java.util.ArrayList;

public class Scanner{
  private ErrorHandler errorHandler;
  private ArrayList<Token> tokens = new ArrayList<>();
  private STM stm;
  
  public Scanner(ErrorHandler errorHandler, STM stm){
    this.errorHandler = errorHandler;
    this.stm = stm;
    tokens = new ArrayList<>();
  }
  
  public void readFile(File file, STM stm){
    try(BufferedReader br = new BufferedReader(new FileReader(file))){
      String line;
      int lineNumber = 0;
      String current = "";
      
      while((line = br.readLine()) != null){
        lineNumber++;
        for(int i = 1; i <= line.length(); i++){
          char c = line.charAt(i - 1);
          
          boolean added = true;
          
          switch (c) {
            case '{':
              addToken(lineNumber, i, current);
              tokens.add(new Token("LBRA", "{", lineNumber, i));
              break;
            case '}':
              addToken(lineNumber, i, current);
              tokens.add(new Token("RBRA", "}", lineNumber, i));
              break;
            case '(':
              addToken(lineNumber, i, current);
              tokens.add(new Token("LPAR", "(", lineNumber, i));
              break;
            case ')':
              addToken(lineNumber, i, current);
              tokens.add(new Token("RPAR", ")", lineNumber, i));
              break;
            case ';':
              addToken(lineNumber, i, current);
              tokens.add(new Token("SEMI", ";", lineNumber, i));
              break;
            case '+':
              addToken(lineNumber, i, current);
              tokens.add(new Token("PLUS", "+", lineNumber, i));
              break;
            case '-':
              addToken(lineNumber, i, current);
              tokens.add(new Token("MINUS", "-", lineNumber, i));
              break;
            case '*':
              addToken(lineNumber, i, current);
              tokens.add(new Token("MUL", "*", lineNumber, i));
              break;
            case '/':
              addToken(lineNumber, i, current);
              tokens.add(new Token("DIV", "/", lineNumber, i));
              break;
            case '"':
              addToken(lineNumber, i, current);
              tokens.add(new Token("QUOTE", "\"", lineNumber, i));
              break;
            case ' ':
              addToken(lineNumber, i, current);
              break;
            default:
              added = false;
              break;
          }

          if(added){
            current = "";
            continue;
          }
          
          switch (c) {
            case '<':
              if(!current.equals("<")){
                addToken(lineNumber, i, current);
                current = "" + c;
              } else{
                current += c;
                addToken(lineNumber, i, current);
                current = "";
              }
              break;
            case '=':
              if(!current.equals("<") && !current.equals(">") && !current.equals("=")){
                addToken(lineNumber, i, current);
                current = "" + c;
              } else{
                current += c;
                addToken(lineNumber, i, current);
                current = "";
              }
              break;
            case '>':
              addToken(lineNumber, i, current);
              current = "" + c;
              break;
            case '|':
              if(!current.equals("|")){
                addToken(lineNumber, i, current);
                current = "" + c;
              } else{
                current += c;
                addToken(lineNumber, i, current);
                current = "";
              }
              break;
            case '&':
            if(!current.equals("&")){
                addToken(lineNumber, i, current);
                current = "" + c;
              } else{
                current += c;
                addToken(lineNumber, i, current);
                current = "";
              }
              break;
            default:
              if(current.equals("<")){
                addToken(lineNumber, i, current);
                current = "";
                break;
              } 
              if(!(Character.isDigit(c) || Character.isAlphabetic(c))){
                addToken(lineNumber, i, current);
                errorHandler.addError(new Error("Caracter " + c + " no reconocido", lineNumber, i));
                current = "";
                break;
              }
              try {
                Integer.valueOf(current);
                if(Character.isDigit(c)){
                  current += c;
                } else if(Character.isAlphabetic(c)){
                  addToken(lineNumber, i, current);
                  current = "" + c;
                }
              } catch (Exception e) { 
                current += c;
              }
              break;
            }
        }
      }
      addToken(lineNumber, 0, current);
    } catch (Exception e) {
      System.out.println("Hubo un error escaneando el archivo");
      e.printStackTrace();
    }    
  }
  
  private void addToken(int lineNumber, int column, String current){
    if(current.isEmpty()) return;
    
    if(stm.getToken(current) != null) tokens.add(new Token(stm.getToken(current), current, lineNumber, column));
    else if(current.equals("<")) tokens.add(new Token("LT", current, lineNumber, column));
    else if(current.equals(">")) tokens.add(new Token("MT", current, lineNumber, column));
    else if(current.equals("<=")) tokens.add(new Token("LET", current, lineNumber, column));
    else if(current.equals(">=")) tokens.add(new Token("MET", current, lineNumber, column));
    else if(current.equals("==")) tokens.add(new Token("EQUAL", current, lineNumber, column));
    else if(current.equals("<<")) tokens.add(new Token("OUT", current, lineNumber, column));
    else if(current.equals("=")) tokens.add(new Token("ASSIGN", current, lineNumber, column));
    else if(current.equals("||")) tokens.add(new Token("OR", current, lineNumber, column));
    else if(current.equals("&&")) tokens.add(new Token("AND", current, lineNumber, column));
    else if(current.equals("&") || current.equals("|")) errorHandler.addError(new Error("Token " + current + " no valido", lineNumber, column));
    else{
      try{
        Integer.valueOf(current);
        tokens.add(new Token("NUM", current, lineNumber, column));
      } catch(NumberFormatException e){
        tokens.add(new Token("ID", current, lineNumber, column));
      }
    }
  }
  
  public void reset(){ tokens.clear(); }
  
  public ArrayList<Token> getTokens() { return tokens; }
  
  @Override
  public String toString(){
    String string = "";
    for (Token token : tokens) string += token + "\n";
    return string;
  }
}
