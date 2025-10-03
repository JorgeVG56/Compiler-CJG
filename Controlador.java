import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import Modelo.*;

public class Controlador implements ActionListener{
  // Modelo
  private Scanner scanner;
  private Parser parser;
  private Semantico semantico;
  private CIGenerator cIGenerator;
  private ErrorHandler errorHandler;
  private STM stm;

  // Vista
  private Vista vista;

  private File file;
  
  public Controlador(Scanner scanner, Parser parser, Semantico semantico, CIGenerator cIGenerator, ErrorHandler errorHandler, STM stm, Vista vista){
    this.scanner = scanner;
    this.parser = parser;
    this.semantico = semantico;
    this.cIGenerator = cIGenerator;
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
  }

  private void upload(){
    file = vista.uploadFile();
    String content = "", fileName = "Archivo sin Subir";
    try(BufferedReader br = new BufferedReader(new FileReader(file));){
      String line;
      while((line = br.readLine()) != null) content += line + "\n";
      fileName = file.getName();
    } catch (Exception e) {
      // e.printStackTrace();
    }
    vista.setFile(fileName, content);
  }  

  private void save(){
    String fileName = "Archivo sin Subir";
    try {
      if(file == null) file = vista.saveFile();
      FileWriter fw = new FileWriter(file);
      fw.write(vista.getFileText().getText());
      fw.close();
      fileName = file.getName();
    } catch (Exception e) {
      // e.printStackTrace();
    }
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
    sleep(500);
    vista.setScannerButton(Color.YELLOW);
    return !errorHandler.hasErrors();
  }

  private boolean parser(){
    if(!scanner()) return false;
    parser.parser(scanner.getTokens());
    vista.setError(errorHandler.toString());
    vista.setParserButton(!errorHandler.hasErrors() ? Color.GREEN : Color.RED);
    sleep(500);
    vista.setParserButton(Color.YELLOW);
    return !errorHandler.hasErrors();
  }

  private boolean semantico(){
    if(!parser()) return false;
    stm.reset();
    semantico.semantico(scanner.getTokens());
    vista.setError(errorHandler.toString());
    vista.setSemanticButton(!errorHandler.hasErrors() ? Color.GREEN : Color.RED);
    sleep(500);
    vista.setSemanticButton(Color.YELLOW);
    return !errorHandler.hasErrors();
  }

  private boolean ciGenerator(){
    if(!semantico()) return false;
    cIGenerator.reset();
    cIGenerator.generator(scanner.getTokens());
    vista.setCIGenerator(cIGenerator.toString());
    vista.setError(errorHandler.toString());
    vista.setCIButton(!errorHandler.hasErrors() ? Color.GREEN : Color.RED);
    sleep(500);
    vista.setCIButton(Color.YELLOW);
    return true;
  }

  private void sleep(int millis){
    try {
      Thread.sleep(millis);
    } catch (Exception e) { }
  }
}
