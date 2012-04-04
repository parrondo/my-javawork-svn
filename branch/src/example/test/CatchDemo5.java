package example.test;

public class CatchDemo5 {
	static void throwProcess(){
		try{
//			throw new NullPointerException("空指针异常");
//			throw new RuntimeException("运行时异常");
			throw new IllegalAccessException();
		}
		catch(IllegalAccessException e){
//			System.out.print("\n at throwProcess 方法中捕获");
//			throw e;
		}
		System.out.print("\n I am OK");
	}
	
	public static void main(String args[]){
		try{
			throwProcess();
			
		}
		catch(RuntimeException e){
//			System.out.print("\n 再次 throwProcess 方法中捕");
		}
//		System.out.print("\n I am OK");
	}

}
