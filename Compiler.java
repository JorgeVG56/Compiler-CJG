import Modelo.CIGenerator;
import Modelo.COGenerator;
import Modelo.ErrorHandler;
import Modelo.Parser;
import Modelo.STM;
import Modelo.Scanner;
import Modelo.Semantico;

public class Compiler {
  public static void main(String[] args) {
    ErrorHandler errorHandler = new ErrorHandler();
    STM stm = new STM(errorHandler); stm.addKeywords();
    Scanner sc = new Scanner(errorHandler, stm);
    Parser ps = new Parser(errorHandler);
    Semantico semantico = new Semantico(errorHandler, stm);
    CIGenerator cig = new CIGenerator(stm);
    COGenerator cog = new COGenerator();
    Vista vista = new Vista();
    Controlador controlador = new Controlador(sc, ps, semantico, cig, cog, errorHandler, stm, vista);
    vista.doListeners(controlador);
  }
}
