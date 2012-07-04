package basic;

public class BeepTest {
	  public static void main(String[] args)
	   {  
	      AlertBeep.beeping();
	   }

	   static
	   {  
	      System.loadLibrary("AlertBeep");
	   }

}
