package example.test;

import java.lang.reflect.*;
import java.util.Timer;
import java.util.TimerTask;

public class MyClassLoaderTest {
	Timer timer;

	public static void main(String[] args) throws Exception {
		MyClassLoaderTest mytest = new MyClassLoaderTest();
		mytest.timer = new Timer();
		mytest.timer.schedule(new TimerTestTask(), 1 * 1000, 2000);
	}

}

class TimerTestTask extends TimerTask {

	public void run() {
	/*try {
			// 每次都创建出一个新的类加载器
			CustomCL cl = new CustomCL("bin\\example\\MyClassLoader",
					new String[] { "Foo" });
			Class cls = cl.loadClass("example.MyClassLoader.Foo", true);
			Object foo = cls.newInstance();

			Method m = foo.getClass().getMethod("sayHello", new Class[] {});
			m.invoke(foo, new Object[] {});

		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
		// timer.cancel();
		
		try { 
			CustomCL cl = new CustomCL("bin", new String[]{"example.test.Foo"}); 
	        Class cls = cl.loadClass("example.test.Foo"); 
	        IFoo foo = (IFoo)cls.newInstance(); 
	        foo.sayHello(); 
	    } catch(Exception ex) { 
	        ex.printStackTrace(); 
	    } 


	}
}
