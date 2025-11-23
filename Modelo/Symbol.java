package Modelo;
public class Symbol{
  private String id;
  private String type;
  private String ambit;

  public Symbol(String id, String type, String ambit){
    this.id = id;
    this.type = type;
    this.ambit = ambit;
  }

  public String getId() { return id; }

  public String getAmbit() { return ambit; }

  public String getType() { return type; }

  public String getRes() {
    if(type.equals("int")) return "DD";
    if(type.equals("bool")) return "DB";
    return null;
  }

  public String getSize() {
    if(type.equals("int")) return "DWORD";
    if(type.equals("bool")) return "BYTE";
    return null;
  }

  private boolean isSubAmbit(String subAmbit){
    String currentAmbit = "";
    for(int pos = 0; pos < subAmbit.length(); pos++){
      while(pos < subAmbit.length() && subAmbit.charAt(pos) != '_') currentAmbit += subAmbit.charAt(pos++);
      if(ambit.equals(currentAmbit)) return true;
      currentAmbit += '_';
    }
    return false;
  }

  @Override
  public String toString() { return "[" + id + "_" + ambit + "]"; }

  @Override
  public boolean equals(Object obj) {
    if(!id.equals(((Symbol)obj).getId())) return false;
    return isSubAmbit(((Symbol)obj).getAmbit());
  }
}
