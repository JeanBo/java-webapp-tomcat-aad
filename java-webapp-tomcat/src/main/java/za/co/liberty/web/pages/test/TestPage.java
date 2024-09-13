package za.co.liberty.web.pages.test;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.time.Duration;

import za.co.liberty.web.data.enums.ContextType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.grid.GridToCSVHelper;

/**
 * Represents the default home screen.  
 * 
 * In future we will possibly show news here i.e. new functionality, changes etc.
 * 
 */
public class TestPage extends BasePage {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TestPage.class);
	private String warningMessage;
	private Form form;
	
	public TestPage() {
		add(createContentPanel("contentPanel"));
		add(form=new Form("form"));
		
	
		DateTextField dateTextField = new DateTextField("dateTextField", new PatternDateConverter("dd/MM/yyyy",false));
		form.add(dateTextField);		
		
		 DatePicker datePicker = new DatePicker() {

			@Override
			protected void configure(Map<String, Object> map1, IHeaderResponse headResp, Map<String, Object> map2) {
				super.configure(map1, headResp, map2);
			
				StringBuilder builder = new StringBuilder();
				builder.append("widgetProperties Map\n");
				for (String s : map1.keySet()) {
					builder.append("   Key:" + s + "  -   value:" + map1.get(s));
				}
				builder.append("\n================================");
				builder.append("\n\ninitVariables Map\n");
				for (String s : map2.keySet()) {
					builder.append("   Key:" + s + "  -   value:" + map1.get(s));
				}
				
				System.out.println("#JB  Output of DatePicker\n" + builder.toString());
			}

			@Override
			protected boolean enableMonthYearSelection() {
				return false;
			}
			 
		 };

	     datePicker.setShowOnFieldClick(true);
	     datePicker.setAutoHide(true);
	     dateTextField.add(datePicker);
	     

	     /*
	      * 
	      */
	     SRSDateField dateTextField2 = new SRSDateField(
	    		 "dateTextField2", new IModel<Date>() {

					@Override
					public void detach() {
						// TODO Auto-generated method stub
						
					}

					@Override
					public Date getObject() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void setObject(Date arg0) {
						// TODO Auto-generated method stub
						
					}});
	    
	    
			form.add(dateTextField2);		
			
			 DatePicker datePicker2 = new DatePicker() {

				@Override
				protected void configure(Map<String, Object> map1, IHeaderResponse headResp, Map<String, Object> map2) {
					super.configure(map1, headResp, map2);
				
					StringBuilder builder = new StringBuilder();
					builder.append("widgetProperties Map\n");
					for (String s : map1.keySet()) {
						builder.append("   Key:" + s + "  -   value:" + map1.get(s));
					}
					builder.append("\n================================");
					builder.append("\n\ninitVariables Map\n");
					for (String s : map2.keySet()) {
						builder.append("   Key:" + s + "  -   value:" + map1.get(s));
					}
					
					System.out.println("#JB  Output of DatePicker2\n" + builder.toString());
				}

				@Override
				protected boolean enableMonthYearSelection() {
					return true;
				}
				 
			 };

		     datePicker2.setShowOnFieldClick(true);
		     datePicker2.setAutoHide(true);
		     dateTextField2.add(datePicker2);

		     
		     
		add(new AjaxLink("download") {

			@Override
			public void onClick(AjaxRequestTarget arg) {
				System.out.println("ExportLink - onclick - start");
				
//				System.out.println("Export - submit begin");
//				super.onSubmit(target, form);
				ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(new StringResourceStream(
						"This is a silly file", "text/plain")) {
					
					@Override
			        public void respond(IRequestCycle requestCycle) {
						System.out.println("@RESPOND");
			            super.respond(requestCycle);
			        }
					
				};
			
				handler.setContentDisposition(ContentDisposition.ATTACHMENT);
				handler.setCacheDuration(Duration.ONE_MINUTE);
				handler.setFileName("file.txt");
						
//				ResourceStreamRequestTarget target = new ResourceStreamRequestTarget(new StringResourceStream(
//						builder.toString(), "text/plain"));
//				target.setFileName(fileName);
				
//				RequestCycle.get().getResponse().getOutputStream()
				RequestCycle.get().scheduleRequestHandlerAfterCurrent(handler);
//				System.out.println("Export - submit end");
				
				System.out.println("ExportLink - onclick - end");
			}
		});
		
   
		
//		form.add(behaviors)
		logger.info("Loaded page");
	}
	
	
	private Component createContentPanel(String id) {
		return new TestPanel(id);
//		return new EmptyPanel(id);
	}
	
	
	
	@Override
	public String getPageName() {
		return "Test Page - Testy testy";
	}
	
	
	/**
	 * Returns {@link ContextType#NONE} by default.  Override this to 
	 * indicate what Context Type you need. 
	 * 
	 * @return
	 */
	public ContextType getContextTypeRequired() {
		return ContextType.AGREEMENT;
	}
	
	
	
	public class TestPanel extends Panel implements Serializable {

		private static final long serialVersionUID = 1L;
		
		public TestPanel(String id) {
			super(id);
			
			add(new AjaxLink("button1") {

				@Override
				public void onClick(AjaxRequestTarget arg0) {
					System.out.println("I was clicked");
					throw new IllegalArgumentException("Test exception");
					
				}});
			

		}

		
		
	}

}


