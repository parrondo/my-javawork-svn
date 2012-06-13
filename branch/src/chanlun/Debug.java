package chanlun;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.omg.CORBA.PUBLIC_MEMBER;

public class Debug {

	public static void stop(long currenttime,String stopTime) {
		Date stopDate = new Date();
		Date currBarTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"); 
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		

		try {
			stopDate = sdf.parse(stopTime);//format "2011-12-02 00:55:00"
			currBarTime.setTime(currenttime);
			if (currBarTime.compareTo(stopDate) >= 0) {
				System.out.println("Stop");
				;
			}
		} catch (Exception e) {
			System.out.println("Date String format is error!");
		}
	}

}
