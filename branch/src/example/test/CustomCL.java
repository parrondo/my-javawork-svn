package example.test;
import java.util.*;
import java.io.*;

public class CustomCL extends ClassLoader {

	private String basedir; // 需要该类加载器直接加载的类文件的基目录
	private HashSet dynaclazns; // 需要由该类加载器直接加载的类名

	public CustomCL(String basedir, String[] clazns) {
		super(null); // 指定父类加载器为 null
		this.basedir = basedir;
		dynaclazns = new HashSet();
		loadClassByMe(clazns);
	}

	private void loadClassByMe(String[] clazns) {
		for (int i = 0; i < clazns.length; i++) {
			loadDirectly(clazns[i]);
			dynaclazns.add(clazns[i]);
		}
	}

	private Class loadDirectly(String name) {
		Class cls = null;
		StringBuffer sb = new StringBuffer(basedir);
		String classname = name.replace('.', File.separatorChar) + ".class";
		
//		String classname = name+".class";
		
		sb.append(File.separator + classname);
		File classF = new File(sb.toString());
		try {
			cls = instantiateClass(name, new FileInputStream(classF),
					classF.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cls;
	}

	private Class instantiateClass(String name, InputStream fin, long len) {
		byte[] raw = new byte[(int) len];
		try {
			fin.read(raw);
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return defineClass(name, raw, 0, raw.length);
	}

	protected Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class cls = null;
		cls = findLoadedClass(name);
		if (!this.dynaclazns.contains(name) && cls == null)
			cls = getSystemClassLoader().loadClass(name);
		if (cls == null)
			throw new ClassNotFoundException(name);
		if (resolve)
			resolveClass(cls);
		return cls;
	}

}
