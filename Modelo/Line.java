package Modelo;

public class Line {
  private String offset, value, size;
  
  public Line(String offset, String value, String size){
    this.offset = offset;
    this.value = value;
    this.size = size;
  }

  public String getOffset() { return offset; }

  public String getSize() { return size; }

  public String getValue() { return value; }

  @Override
  public String toString() { return offset + " -" + value + " -" + size; }
}
