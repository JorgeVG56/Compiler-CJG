import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import Modelo.*;

public class Controlador implements ActionListener{
  // Modelo
  private Scanner scanner;
  private Parser parser;
  private Semantico semantico;
  private CIGenerator cIGenerator;
  private COGenerator cOGenerator;
  private ErrorHandler errorHandler;
  private STM stm;

  // Vista
  private Vista vista;

  private File file;
  
  public Controlador(Scanner scanner, Parser parser, Semantico semantico, CIGenerator cIGenerator, COGenerator cOGenerator, ErrorHandler errorHandler, STM stm, Vista vista){
    this.scanner = scanner;
    this.parser = parser;
    this.semantico = semantico;
    this.cIGenerator = cIGenerator;
    this.cOGenerator = cOGenerator;
    this.errorHandler = errorHandler;
    this.stm = stm;
    this.vista = vista;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == vista.getUploadButton()) { upload(); return; }
    if(e.getSource() == vista.getSaveButton()) { save(); return; }
    if(e.getSource() == vista.getScannerButton()) { scanner(); return; }
    if(e.getSource() == vista.getParserButton()) { parser(); return; }
    if(e.getSource() == vista.getSemanticButton()) { semantico(); return; }
    if(e.getSource() == vista.getcIButton()) { ciGenerator(); return; }
    if(e.getSource() == vista.getcOButton()) { coGenerator(); return; }
  }

  private void upload(){
    file = vista.uploadFile();
    String content = "", fileName = "Archivo sin Subir";
    try(BufferedReader br = new BufferedReader(new FileReader(file));){
      String line;
      while((line = br.readLine()) != null) content += line + "\n";
      fileName = file.getName();
    } catch (Exception e) { }
    vista.setFile(fileName, content);
  }  

  private void save(){
    String fileName = "Archivo sin Subir";
    try {
      if(file == null) file = vista.uploadFile();
      FileWriter fw = new FileWriter(file);
      fw.write(vista.getFileText().getText());
      fw.close();
      fileName = file.getName();
    } catch (Exception e) { }
    vista.setFile(fileName, vista.getFileText().getText());
  }
  
  private boolean scanner(){
    save();
    errorHandler.reset();
    scanner.reset();
    scanner.readFile(file, null);
    vista.setScanner(scanner.toString());
    vista.setError(errorHandler.toString());
    vista.setScannerButton(!errorHandler.hasErrors() ? Color.GREEN : Color.RED);
    sleep(250);
    vista.setScannerButton(Color.YELLOW);
    return !errorHandler.hasErrors();
  }

  private boolean parser(){
    if(!scanner()) return false;
    parser.parser(scanner.getTokens());
    vista.setError(errorHandler.toString());
    vista.setParserButton(!errorHandler.hasErrors() ? Color.GREEN : Color.RED);
    sleep(250);
    vista.setParserButton(Color.YELLOW);
    return !errorHandler.hasErrors();
  }

  private boolean semantico(){
    if(!parser()) return false;
    semantico.semantico(scanner.getTokens());
    vista.setError(errorHandler.toString());
    vista.setSemanticButton(!errorHandler.hasErrors() ? Color.GREEN : Color.RED);
    sleep(250);
    vista.setSemanticButton(Color.YELLOW);
    return !errorHandler.hasErrors();
  }

  private boolean ciGenerator(){
    if(!semantico()) return false;
    cIGenerator.generator(scanner.getTokens());
    ArrayList<Instruction> data = cIGenerator.getData();
    ArrayList<Instruction> text = cIGenerator.getText();
    String [][] content = new String[data.size() + text.size()][3];
    for (int i = 0; i < data.size(); i++) {
      content[i][0] = data.get(i).getName();
      content[i][1] = data.get(i).getCommand();
      content[i][2] = data.get(i).getOperands();
    }
    for (int i = 0; i < text.size(); i++) {
      content[data.size() + i][0] = text.get(i).getName();
      content[data.size() + i][1] = text.get(i).getCommand();
      content[data.size() + i][2] = text.get(i).getOperands();
    }
    vista.setCIGenerator(content);
    vista.setError(errorHandler.toString());
    vista.setCIButton(!errorHandler.hasErrors() ? Color.GREEN : Color.RED);
    sleep(500);
    vista.setCIButton(Color.YELLOW);
    return true;
  }

  private boolean coGenerator(){
    if(!ciGenerator()) return false;
    cOGenerator.generator(cIGenerator.getData(), cIGenerator.getText());
    return true;
  }

  private void sleep(int millis){
    try {
      Thread.sleep(millis);
    } catch (Exception e) { }
  }
}
