package za.co.liberty.web.wicket.convert.converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.string.Strings;

import za.co.liberty.helpers.util.DateUtil;

/**
 * Format dates for the SRS application.  Allows for the input of dates
 * in various formats i.e. "dd-MM-yyyy", "dd/MM/yyyy" etc. but always
 * displays in "dd/MM/yyyy".
 * 
 * @author JZB0608 - 23 May 2008
 *
 */
public class SRSDateConverter extends AbstractConverter {

	private static final long serialVersionUID = -2222271935848647781L;
	public static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
	
	/* Local variables */
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected Class getTargetType() {
		return Date.class;

	}

	transient static final ThreadLocal<SimpleDateFormat> FORMAT = new ThreadLocal<SimpleDateFormat>() {
		@Override
	    protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT_PATTERN);
	    }
	  };
	  
	  // Test the thread local
	public static void main(String [] args) throws ParseException {
			final Date firstDate = DateUtil.getInstance().getDateFromString("1978-08-08");
		 	Runnable t1 = new Runnable() {
		 		long count = 0;
		        public void run() {
		        	for (int i = 0; i < 100; ++i) {
		        		Date dte;
						try {
							dte = FORMAT.get().parse("08/08/1978");
							if (dte.getTime() != firstDate.getTime()) {
			        			System.out.println("Not the same, was " 
			        					+ dte.getTime() + "  cnt1=" + ++count);
			        		}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		
		            }
		        }
		    };

		    final Date secondDate = DateUtil.getInstance().getDateFromString("1998-01-01");
		    
		    Runnable t2 = new Runnable() {
		    	long count = 0;
		        public void run() {
		        	for (int i = 0; i < 100; ++i) {
		        		Date dte;
						try {
							dte = FORMAT.get().parse("01/01/1998");
							if (dte.getTime() != secondDate.getTime()) {
			        			System.out.println("Not the same, was " + dte.getTime() + "  cnt2=" + ++count);
			        		}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		
		            }

		        }
		    };

		    System.out.println("FirstDate = " + firstDate.getTime());
		    System.out.println("SecondDate = " + secondDate.getTime());
		    new Thread(t1).start();
		    new Thread(t2).start();
		
	}
	
	/**
	 * Create a date from the specified string value
	 */
	public Object convertToObject(String value, Locale locale) {
		if (Strings.isEmpty(value)) {
			return null;
		}
		
		/* Tidy input date */
		value = value.replaceAll("-|\\p{Blank}|,|\\\\", "/");
		if (value.length()==8 && value.indexOf("/")==-1) {
			// Fix non slash
			value = value.substring(0,2) + "/" + value.substring(2,4)+ "/" 
				+ value.substring(4,8);
		}
		
		/* Attempt a match */
		try {
			Date date = FORMAT.get().parse(value);
			return date;
		} catch (ParseException e) {

		}
		
		throw new ConversionException("")
			.setResourceKey("validator.invalid")
			.setSourceValue(value)
			.setVariable("validator.type", "date");

	}

	
	@Override
	public String convertToString(Object value, Locale locale) {
		if (value == null) {
			return null;
		}
		return FORMAT.get().format(value);
	}

}
