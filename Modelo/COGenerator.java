package Modelo;

import java.util.*;

public class COGenerator {
  private ArrayList<Instruction> data, text;
  
  private ArrayList<Line> lineas;
  private int currentOffset;
  private HashMap<String, Line> mp;

  public COGenerator(){
    lineas = new ArrayList<>();
    mp = new HashMap<>();
  }

  public void reset(){
    lineas.clear();
    mp.clear();
  }
  
  public void generator(ArrayList<Instruction> data, ArrayList<Instruction> text){
    reset();
    this.data = data;
    this.text = text;
    currentOffset = 0;
    lineas.add(new Line("Data", "", ""));
    processData();
    currentOffset = 0;
    lineas.add(new Line("Text", "", ""));
    processText();
  }

  private void processData(){
    for(Instruction i : data) {
      if(i.getName().length() == 0 || i.getName().startsWith("_msgLen")) continue;

      if(i.getName().charAt(0) == '_') { processString(i.getOperands()); }
      else {lineas.add(new Line(convert(currentOffset, 32), convert(0, 32), convert(4, 32))); currentOffset += 4; } 

      // System.out.println(lineas.getLast());
      mp.put(i.getName(), lineas.getLast());
    }
  }

  private void processText(){
    boolean fl = false;

    for(Instruction i : text){
      if("_start:".equals(i.getName())){ fl = true; continue; }
      if(!fl) continue;

      
    }
  }

  private void processString(String s){
    String original = s.substring(1, s.indexOf('\'', 1));

    String binary = "";
    for(int i = 0; i < original.length(); i++) binary += convert(original.charAt(i), 8);
    binary += convert(10, 8);

    lineas.add(new Line(convert(currentOffset, 32), binary, convert(original.length() + 1, 32)));

    currentOffset += original.length() + 1;
  }

  private String convert(int n, int l){
    String s = "", group = "";
    for(int i = 0; i < l; i++){
      group = ((n >> i) & 1) + group;
      if(i % 4 == 3) group = ' ' + group;
      if(i % 8 == 7) { s += group; group = ""; }
    }
    return s;
  }
}
