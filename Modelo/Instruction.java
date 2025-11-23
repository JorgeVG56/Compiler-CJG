package Modelo;

public class Instruction {
  String name, command, operands;
  
  public Instruction(String name, String command, String operands){
    this.name = name;
    this.command = command;
    this.operands = operands;
  }
  
  public String getCommand() { return command == null ? "" : command; }
  
  public String getName() { return name == null ? "" : name; }
  
  public String getOperands() { return operands == null ? "" : operands; }
  
  @Override
  public String toString() { return getName() + " " + getCommand() + " " + getOperands(); }
}
