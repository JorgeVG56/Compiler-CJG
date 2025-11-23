import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Vector;

public class Vista extends JFrame{
  private JMenuBar menuBar;
  private JMenu fileMenu, fileName;
  private JMenuItem saveButton, uploadButton;
  private JPanel topPanel, bottomPanel, scannerPanel, errorPanel, buttonsPanel, cIPanel, cOPanel;
  private JTextArea fileText, scannerText, errorText;
  private JTable cITable, cOTable;
  @SuppressWarnings("unused")
  private JScrollPane filePane, scannerPane, errorPane, cIPane, cOPane;
  private JButton scannerButton, parserButton, semanticButton, cIButton, cOButton;
  
  private JFileChooser fileChooser;
  private Font font, fontTable;
  private String[] columnNamesCI, columnNamesCO;
  
  public Vista(){
    super("Dev CJG");
    doInterface();
  }
  
  private void doInterface(){
    setSize(1000, 500);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new GridLayout(0, 1));
    setIconImage(new ImageIcon("CJG_Logo.png").getImage());
    
    font = new Font("JetBrains Mono", Font.PLAIN, 20);
    fontTable = new Font("JetBrains Mono", Font.PLAIN, 12);
    fileChooser = new JFileChooser("/home/jorgitox/Documentos/Programas/lya/CodigoCJG");
    
    setJMenuBar(menuBar = new JMenuBar());
    menuBar.setBackground(Color.PINK);
    menuBar.add(fileMenu = new JMenu("Archivo"));
    fileMenu.setFont(font);
    fileMenu.add(uploadButton = new JMenuItem("Subir"));
    uploadButton.setFont(font);
    fileMenu.add(saveButton = new JMenuItem("Guardar"));
    saveButton.setFont(font);
    menuBar.add(fileName = new JMenu("Archivo sin Subir"));
    fileName.setFont(font);
    fileName.setEnabled(false);
    
    add(topPanel = new JPanel(new GridLayout(0, 3)));
    topPanel.add(filePane = new JScrollPane(fileText = new JTextArea()));
    fileText.setFont(font);
    topPanel.add(scannerPanel = new JPanel(new BorderLayout()));
    scannerPanel.add(scannerButton = new JButton("Scanner"), BorderLayout.NORTH);
    scannerButton.setFont(font);
    scannerButton.setBackground(Color.YELLOW);
    scannerPanel.add(scannerPane = new JScrollPane(scannerText = new JTextArea()));
    scannerText.setFont(font);
    scannerText.setEnabled(false);
    scannerText.setDisabledTextColor(Color.BLACK);
    topPanel.add(errorPanel = new JPanel(new BorderLayout()));
    errorPanel.add(buttonsPanel = new JPanel(new GridLayout(0, 2)), BorderLayout.NORTH);
    buttonsPanel.add(parserButton = new JButton("Parser"));
    parserButton.setFont(font);
    parserButton.setBackground(Color.YELLOW);
    buttonsPanel.add(semanticButton = new JButton("Semantic"));
    semanticButton.setFont(font);
    semanticButton.setBackground(Color.YELLOW);
    errorPanel.add(errorPane = new JScrollPane(errorText = new JTextArea()));
    errorText.setFont(font);
    errorText.setEnabled(false);
    errorText.setDisabledTextColor(Color.BLACK);
    
    add(bottomPanel = new JPanel(new GridLayout(0, 2)));
    bottomPanel.add(cIPanel = new JPanel(new BorderLayout()));
    cIPanel.add(cIButton = new JButton("CI"), BorderLayout.NORTH);
    cIButton.setFont(font);
    cIButton.setBackground(Color.YELLOW);
    cIPanel.add(cIPane = new JScrollPane(cITable = new JTable(new String[][]{}, columnNamesCI = new String[]{"Etiqueta", "Comando", "Operandos"})));
    cITable.getTableHeader().setFont(fontTable);
    cITable.setFont(fontTable);
    bottomPanel.add(cOPanel = new JPanel(new BorderLayout()));
    cOPanel.add(cOButton = new JButton("CO"), BorderLayout.NORTH);
    cOButton.setFont(font);
    cOButton.setBackground(Color.YELLOW);
    cOPanel.add(cOPane = new JScrollPane(cOTable = new JTable(new String[][]{}, columnNamesCO = new String[]{"Offset", "Comando"})));
    cOTable.getTableHeader().setFont(fontTable);
    cOTable.setFont(fontTable);
    
    fileChooser = new JFileChooser("/home/jorgitox/Documentos/Programas/lya/CodigoCJG");
    
    setVisible(true);
  }
  
  public void doListeners(Controlador controlador){
    saveButton.addActionListener(controlador);
    uploadButton.addActionListener(controlador);
    scannerButton.addActionListener(controlador);
    parserButton.addActionListener(controlador);
    semanticButton.addActionListener(controlador);
    cIButton.addActionListener(controlador);
    cOButton.addActionListener(controlador);
  }
  
  public File uploadFile(){
    int result =  fileChooser.showOpenDialog(null);
    
    return (result == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null);
  }
  
  public File saveFile(){
    int result =  fileChooser.showSaveDialog(null);
    
    return (result == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null);
  }
  
  public void setFile(String name, String content){
    fileName.setText(name);
    fileText.setText(content);
  }

  private void setGenerator(JPanel p, JScrollPane sp, JTable t, String [][] content, String[] names){
    p.remove(sp);
    
    p.add(sp = new JScrollPane(t = new JTable(content, names) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    }));
    
    t.setFont(fontTable);
    t.getTableHeader().setFont(fontTable);
    t.getTableHeader().setReorderingAllowed(false);
    repaint();
    revalidate();
  }

  /*
  public void setCOGenerator(String [][] content){
    cOPanel.remove(cOPane);
    
    cOPanel.add(cOPane = new JScrollPane(cOTable = new JTable(content, columnNames) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    }));
    
    cOTable.setFont(fontTable);
    cOTable.getTableHeader().setFont(fontTable);
    cOTable.getTableHeader().setReorderingAllowed(false);
    repaint();
    revalidate();
  }
    */
  
  private void recolor(JButton b, Color c){
    b.setBackground(c);
    b.repaint();
    b.update(b.getGraphics());
  }
  
  public void setCIGenerator(String [][] content){ setGenerator(cIPanel, cIPane, cITable, content, columnNamesCI); }

  public void setCOGenerator(String [][] content){ setGenerator(cOPanel, cOPane, cOTable, content, columnNamesCO); }
  
  public void setScanner(String content){ scannerText.setText(content); }
  
  public void setError(String content){ errorText.setText(content); }
  
  public void setScannerButton(Color c){ recolor(scannerButton, c); }
  
  public void setParserButton(Color c){ recolor(parserButton, c); }
  
  public void setSemanticButton(Color c){ recolor(semanticButton, c); }
  
  public void setCIButton(Color c){ recolor(cIButton, c); }

  public void setCOButton(Color c){ recolor(cOButton, c); }
  
  public JMenuItem getSaveButton() { return saveButton; }
  
  public JMenuItem getUploadButton() { return uploadButton; }
  
  public JButton getScannerButton() { return scannerButton; }
  
  public JButton getParserButton() { return parserButton; }
  
  public JButton getSemanticButton() { return semanticButton; }
  
  public JButton getcIButton() { return cIButton; }
  
  public JButton getcOButton() { return cOButton; }
  
  public JTextArea getFileText() { return fileText; }
}
