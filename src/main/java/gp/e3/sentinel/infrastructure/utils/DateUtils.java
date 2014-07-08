package gp.e3.sentinel.infrastructure.utils;

import java.sql.Timestamp;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateUtils {

	public static Timestamp getTimestampFromDateTime(DateTime dateTime) {
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Timestamp timestamp = null;
		if( dateTime != null ){
			timestamp = new Timestamp(dateTime.getMillis());
		}
		
		return timestamp;
	}
	
	public static DateTime getDateTimeFromTimeStamp(Timestamp timestamp) {
		
		return new DateTime(timestamp.getTime(), DateTimeZone.UTC);
	}
}