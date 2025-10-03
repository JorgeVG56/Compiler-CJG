package Modelo;
import java.util.ArrayList;

public class ErrorHandler {
  private ArrayList<Error> errors;

  public ErrorHandler(){
    errors = new ArrayList<>();
  }

  public void addError(Error e){
    errors.add(e);
  }

  public ArrayList<Error> getErrors() {
    return errors;
  }

  public void reset(){
    errors.clear();
  }

  public boolean hasErrors(){
    return !errors.isEmpty();
  }

  public String toString(){
    String string = "";

    for (Error error : errors) {
      string += error.toString() + "\n";
    }

    return string;
  }
}
