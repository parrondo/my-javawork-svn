package basic;

import example.test.heysung;

public class TestException {

	public TestException() throws Exception{
		System.out.println("构造开始 ");
		System.out.print("\007"); 
		System.out.println("\7"); 
	
			throw new Exception("构造出现异常 ");
	}
	protected int age;
	String name;

	int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public static void main(String[] args) {
		TestException great=null;
		try {
			great = new TestException();
		} 
		
		catch (Exception e) {
			// TODO: handle exception
		}
		if (great == null)
			System.out.println("对象为NULL ");

	}
}
