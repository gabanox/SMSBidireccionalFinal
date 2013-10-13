package com.bbvabancomer.alertas.helper;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public final class DateHelper {

	private DateHelper(){}
	
	public static int getDayOfTheMonth(Date d){
		return getCalendarFromDateAsTimestamp(d).get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getHourOfTheDay(Date d ){
		return getCalendarFromDateAsTimestamp(d).get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getMinuteOfTheHour(Date d){
		return getCalendarFromDateAsTimestamp(d).get(Calendar.MINUTE);
	}
	
	public static int getSecondOfTheMinute(Date d){
		return getCalendarFromDateAsTimestamp(d).get(Calendar.SECOND);	
	}
	
	public static Calendar getCalendarFromDateAsTimestamp(Date d){
	
		Calendar calendar = Calendar.getInstance();
		Timestamp timestampFromDb = new Timestamp( d.getTime() );
		calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timestampFromDb.getTime() );
			
		return calendar;
	}
	
	public static int getDifferenceBetweenDatesInSeconds(Date beforeDate, Date afterDate){
		return (int)(afterDate.getTime() - beforeDate.getTime() ) / 1000;
	}
	
	public static int getDifferenceBetweenDatesInSeconds(Calendar c1, Calendar c2){
		return (int)(c1.getTimeInMillis() - c2.getTimeInMillis() ) / 1000;
	}
	
	public static Date getEldestDate(Date d1, Date d2){
		return d1.after(d2) ? d1 : d2;
	}
	
	public static Calendar getEldestCalendar(Calendar c1, Calendar c2){
		return c1.after(c2) ? c1 : c2;
	}
	
	public static Date getMinorDate(Date d1, Date d2){
		return d1.before(d2) ? d1 : d2;
	}
	
	public static Calendar getMinorCalendar(Calendar c1, Calendar c2){
		return c1.before(c2) ? c1 : c2;
	}
	
}
