package wicket.examples.datepick;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.datetime.StyleDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.extensions.yui.calendar.TimeField;

public class DatesPage extends WebPage{
	
	/** the backing object for DateTextField demo */

	//String date1 =new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	
    private final Date date = new Date();
   
    /** the backing object for DateTimeField demo */
    private final Date date2 = new Date();

    /** the backing object for TimeField demo */
    private final Date time = new Date();
    
    private Locale selectedLocale = new Locale("en");
    
    public DatesPage() {
    	
    	DateTextField dateTextField = new DateTextField("dateTextField", new PropertyModel<Date>(
                this, "date"), new StyleDateConverter("S-", true))
            {
                @Override
                public Locale getLocale()
                {
                    return selectedLocale;
                }
            };
    	
    	Form<?> form = new Form<Void>("form")
        {
            @Override
            protected void onSubmit()
            {
                info("set date to " + new SimpleDateFormat("dd-MM-yyyy").format(date));
            }
        };
        add(form);
        form.add(dateTextField);
        
        DatePicker datePicker = new DatePicker()
        {
            @Override
            protected String getAdditionalJavaScript()
            {
                return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
            }
        };
        
        datePicker.setShowOnFieldClick(true);
        datePicker.setAutoHide(true);
        dateTextField.add(datePicker);
        add(new FeedbackPanel("feedback"));
        
        Form<?> form2 = new Form<Void>("form2")
        {
            @Override
            protected void onSubmit()
            {
                info("set date2 to " + date2);
            }
        };
        add(form2);
        form2.add(new DateTimeField("dateTimeField", new PropertyModel<Date>(this, "date2")));
        
        Form<?> form3 = new Form<Void>("form3")
        {
            @Override
            protected void onSubmit()
            {
                info("set time to " + time);
            }
        };
        add(form3);
        form3.add(new TimeField("timeField", new PropertyModel<Date>(this, "time")));
    	
    }
}
