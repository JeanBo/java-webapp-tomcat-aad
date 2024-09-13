package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.Calendar;
import java.util.Date;


public class DateValidationUtil {
	
	public enum DatePart {
		DAY(Calendar.DAY_OF_MONTH), 
		MONTH(Calendar.MONTH);
		
		private int calendarValue;
		private DatePart(int calendarValue) {
			this.calendarValue=calendarValue;
		}
		
		public int getCalendarValue() {
			return calendarValue;
		}
	}
	
	/**
	 * Add time to a date
	 */
	public static Date addToDate(Date date,DatePart datePart,int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(datePart.getCalendarValue(), amount);
		return c.getTime();
	}
	

}
