package za.co.liberty.web.pages.testing;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.domain.scheduling.DPEDebatchSchedule;
import za.co.liberty.srs.domain.scheduling.DPEReportSchedule;
import za.co.liberty.srs.domain.scheduling.PolicyInfoDebatchSchedule;
import za.co.liberty.srs.domain.scheduling.PolicyInfoReportSchedule;
import za.co.liberty.srs.domain.scheduling.SchedulingManager;
import za.co.liberty.srs.domain.scheduling.SchedulingManagerHome;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.HelperPanel;

/**
 * A test page used for displaying data stored in the GuiRequest table. 
 * 
 * @author JZB0608 - 09 Apr 2009
 *
 */
public class ZweliTestPage extends BasePage {
	
	private static final long serialVersionUID = 1L;
	String processLabel = "Process AUM File";
	String reportLabel = "Report on AUM transactions";
	String rejectsLabel = "Process Rejects";
	
	String oylProcessLabel = "Process OYL File";
	String oylReportLabel = "Report on OYL Ttransactions";
	

	public ZweliTestPage() {
		this(null);
	}
	public ZweliTestPage(PageParameters parms) {

		
		initialiseTest();
		initializePanel2();
	}	
	
	private void initializePanel2() {
		//System.out.println("Initializing OYL Test Panel");
		Form<?> form = new Form("form") {
			 @Override
		     protected void onSubmit() {
				 System.out.println("form.onSubmit - Start");
				 System.out.println("form.onSubmit - End");
			 }
		};
		
		
		
		//this.add(form);
		
	}
	
	protected void initialiseTest() {
		System.out.println("Initializing AUM Test Panel");
		Form<?> form = new Form("form") {
			 @Override
		     protected void onSubmit() {
				 System.out.println("form.onSubmit - Start");
				 System.out.println("form.onSubmit - End");
			 }
		};
	
		
		Button but = new AjaxButton("value", new PropertyModel<String>(this, "processLabel"), form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				
				PolicyInfoDebatchSchedule debatchSchedule = new PolicyInfoDebatchSchedule();
				debatchSchedule.executePolicyInfoDebatchProcess(new ApplicationContext());
				
				System.out.println("AjaxButton.onsubmit - End");
			}

		};
		

		but.setOutputMarkupId(true);
		form.setOutputMarkupId(true);
		
		form.add(HelperPanel.getInstance("panel1", but));
		
		/*
		 * ADD THE SECOND BUTTON
		 */
		
		Button but2 = new AjaxButton("value", new PropertyModel<String>(this, "reportLabel"), form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				
			PolicyInfoReportSchedule reportSchedule = new PolicyInfoReportSchedule();
			reportSchedule.executePolicyInfoReport(new ApplicationContext());
				
			}

		};
		

		but2.setOutputMarkupId(true);
		but2.add(new AjaxEventBehavior("onsubmit") {
			
			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				System.out.println("button.onclick - Start");
				try {
					System.out.println("Queue depth:=" + ServiceLocator.lookupService(ITaxGuiController.class).getCatchQueueCount());
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("button.onclick - End");
				
			}
		});
		
		form.add(HelperPanel.getInstance("panel2", but2));
		
		/*
		 * ADD THE REJECTS BUTTON
		 */
		
		Button but3 = new AjaxButton("value", new PropertyModel<String>(this, "rejectsLabel"), form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				
				Context ctx;

				try {
					Properties props = new Properties();

					ctx = new InitialContext(props);

					Object obj = ctx.lookup("ejb/za/co/liberty/srs/domain/scheduling/SchedulingManagerHome");
					SchedulingManagerHome home = (SchedulingManagerHome) javax.rmi.PortableRemoteObject.narrow(obj, SchedulingManagerHome.class);

					SchedulingManager schedulingManager = home.create();
					schedulingManager.processRejects();
				} catch (ClassCastException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CreateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		};
		

		but3.setOutputMarkupId(true);
		but3.add(new AjaxEventBehavior("onsubmit") {
			
			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				System.out.println("button.onclick - Start");
				try {
					System.out.println("Queue depth:=" + ServiceLocator.lookupService(ITaxGuiController.class).getCatchQueueCount());
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("button.onclick - End");
				
			}
		});
		
		form.add(HelperPanel.getInstance("panel3", but3));
		
		
		
		form.add(new EmptyPanel("panel4"));
		
		Button but4 = new AjaxButton("value", new PropertyModel<String>(this, "oylProcessLabel"), form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				
				DPEDebatchSchedule debatchSchedule = new DPEDebatchSchedule();
				debatchSchedule.executeDPEDebatchProcess(new ApplicationContext());
				
				System.out.println("AjaxButton.onsubmit - End");
			}

		};

		but4.setOutputMarkupId(true);
		form.setOutputMarkupId(true);
		
		form.add(HelperPanel.getInstance("panel5", but4));
		
		/*
		 * ADD THE SECOND BUTTON
		 */
		
		Button but5 = new AjaxButton("value", new PropertyModel<String>(this, "oylReportLabel"), form) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				System.out.println("AjaxButton.onsubmit - Start");
				
			DPEReportSchedule reportSchedule = new DPEReportSchedule();
			reportSchedule.executeDPEReport(new ApplicationContext());
				
			}

		};

		but5.setOutputMarkupId(true);
		but5.add(new AjaxEventBehavior("onsubmit") {
			
			@Override
			protected void onEvent(AjaxRequestTarget arg0) {
				System.out.println("button.onclick - Start");
				try {
					System.out.println("Queue depth:=" + ServiceLocator.lookupService(ITaxGuiController.class).getCatchQueueCount());
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("button.onclick - End");
				
			}
		});
		
		form.add(HelperPanel.getInstance("panel6", but5));		
		
		form.add(new EmptyPanel("panel7"));
		
		this.add(form);
		
	}
	
	@Override
	protected boolean isCheckAuthentication() {
		/* Disable authentication as we are logging on */
		return true;
	}
	
	@Override
	public String getPageName() {
		return "General Test";
	}
	
	@Override
	protected Panel getContextPanel() {
		/* Does not require a panel */
		return new EmptyPanel(CONTEXT_PANEL_NAME);
	}
	
	
	public static void main(String[] args) {
		String name = "aumManagerFeeProduct";
		System.out.println(name.hashCode());
	}
}


