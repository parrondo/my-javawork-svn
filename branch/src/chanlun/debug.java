package chanlun;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CORBA.PUBLIC_MEMBER;

public class debug {

	public static void stop(long time) {
		Date stopDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); ;
		Date currBarTime = new Date();

		try {
			stopDate = sdf.parse("2011-12-02 00:55:00");
			currBarTime.setTime(time);
			if (currBarTime.compareTo(stopDate) >= 0) {
				System.out.println("Stop");
				;
			}
		} catch (Exception e) {
		}
	}

}
