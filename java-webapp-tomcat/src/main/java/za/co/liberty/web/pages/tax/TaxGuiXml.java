package za.co.liberty.web.pages.tax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.tax.model.TaxGuiModel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;
import za.co.liberty.xml.tax.Action;
import za.co.liberty.xml.tax.Application;
import za.co.liberty.xml.tax.Parameter;
import za.co.liberty.xml.tax.Process;

/**
 * Basic page construct for the TAX XML processes
 * @author JWV2310
 *
 */
public class TaxGuiXml extends BasePage {
	
	transient protected ITaxGuiController guiController;
	protected TextField xmlNode;
	protected TextField actionIndicator;
	protected CheckBox saveXML;

	private Button save; 
	private SRSDropDownChoice xmlSelector;
	private Boolean SAVEXML; 
	private Form form;
	private Parameter execNode;
	private TextField exclude;
	//this pageModel is the page model. Application is the pageModel for the xml parsing and display/modify etc. 
	protected TaxGuiModel pageModel2;
	private Application pageModel;
	private Panel selectPanel;
	private BasePage parentPage;
	
	public TaxGuiXml(){
		super();
		pageModel2 = new TaxGuiModel();
//		paths = getSessionBean().getPaths();
		List<String> xmlFiles = getSessionBean().retrieveXMLFileNames();
		pageModel2.setXmlFilesAllLocal(xmlFiles);
		//before getting 
		initXML();
	}
	
	private Parameter getParameterExecNode(){
		return execNode;
	}
	
	Map<String,String> paths = new HashMap<String,String>();
	
//	paths = getSessionBean().getPaths();
	
	
	public void initXML(){
		add(selectPanel = emptyP("selectPanel"));
		
		
		
		//read contents from file and display
		if(pageModel2.getXmlFileSelectedLocal() == null){
			pageModel2.setXmlFileSelectedLocal("");
		}
		//parse the XML
		System.out.println("This is where the xml is read --------------------------");
		System.out.println("From pageModel2:" + pageModel2.getXmlFileSelectedLocal());
//		pageModel = getSessionBean().readXML(pageModel2.getXmlFileSelectedLocal());
		
		//now, after reading the xml, we will make sure/override that the LDAP is used instead of the xml path.
		//this is for PDB and Agm Numbers
		//String pdb = getSessionBean().getPdbPath();
		//String agmN = getSessionBean().getAgmnumPath();
		
		//over ride the pdb and agm numbers with ldap path
		List<Process> a = pageModel.getProcess();
		for(Process inst: a){
			List<Object> obj = inst.getMultiThreadOrLoopOrAction();
			
			for(Object instObj: obj){
				
				if(instObj.getClass() == Action.class){
					
					Action newAction = ((Action)instObj);
					
					if(newAction.getName() != null) {
						if(newAction.getName().equalsIgnoreCase("Initialize")){
							List<Parameter> b = newAction.getParameters().getParameter();
							for(Parameter objParam : b){
								
								objParam.setValue(paths.get("bankdet"));
							}
						}else if(newAction.getName().equalsIgnoreCase("TaxNumbers")){
							List<Parameter> b = newAction.getParameters().getParameter();
							for(Parameter objParam : b){
								
								objParam.setValue(paths.get("agreementnumbers"));
								
							}
						}
					}
				}
			}
		}
		
		if(pageModel == null){
			error("There is a problem with the xml parser");
		}
											 
		add(xmlSelector = createXmlSelector("xmlSelector"));
		
	}
	
	protected Panel emptyP(String id){
		selectPanel= new EmptyPanel("selectPanel");
		selectPanel.setOutputMarkupId(true);
		return selectPanel;
		
	}
	
	private SRSDropDownChoice<String> createXmlSelector(String id){
		SRSDropDownChoice tempSRSDropDown = new SRSDropDownChoice(id , 
				new PropertyModel(pageModel2 ,"xmlFileSelectedLocal"),
				pageModel2.getXmlFilesAllLocal(), new ChoiceRenderer() {
							public Object getDisplayValue(Object arg0){
								if (arg0==null) {
									   return null;
								   }
								return ((String)arg0).toString();
							}
							public Object getIdValue(Object arg0){
								if (arg0==null) {
									   return null;
								   }
								return ((String)arg0);
							}
						
				},"select one");
				tempSRSDropDown.setOutputMarkupId(true);
				
				tempSRSDropDown.add(new AjaxFormComponentUpdatingBehavior("change"){

					@Override
					protected void onUpdate(AjaxRequestTarget arg0) {
						System.out.println("Name of report: " + pageModel.getName());
						System.out.println("Ajax updated xml:"+ pageModel2.getXmlFileSelectedLocal());
						
//						pageModel = getSessionBean().readXML(pageModel2.getXmlFileSelectedLocal());
						//pageModel = getSessionBean().readXML(pageModel.getName());
						System.out.println(pageModel.getName());
						int report = pageModel.getName().indexOf("TCS", 0);
						int paye = pageModel.getName().indexOf("PAYE", 0);
						int sdl = pageModel.getName().indexOf("SDL",0);
						System.out.println("Report index:" + report);
						if(report == 0){
							System.out.println("TCS");
//							Panel tcs = new TCSPanel("selectPanel",getEditState(),pageModel,parentPage,pageModel2.getXmlFileSelectedLocal(),pageModel);   //paths.get("tcs")+paths.get("sep"));
//							
////							Panel taccountEntry1 = new AccountEntrySelectionAllPanel("accountEntrySelection",getEditState(),accEntryAllModel,parentPage);
////							taccountEntry1.setOutputMarkupId(true);
////							accountEntrySelection.replaceWith(taccountEntry1);
////							accountEntrySelection = taccountEntry1;
////							if(arg0 != null){
////								arg0.addComponent(accountEntrySelection);
////							}else{
////								System.out.println("Waor");
////							}
//							//now that we know tcs is choicen... bla bla
//							
//							tcs.setOutputMarkupId(true);
//							selectPanel.replaceWith(tcs);
//							selectPanel = tcs;
//							if(arg0 != null){
//								arg0.addComponent(selectPanel);
//							}else{
//								System.out.println("Waor");
//							}
						}else if (paye == 0) {
							
//							Panel payePanel = new PAYEReportPanel("selectPanel",getEditState(),pageModel,parentPage, paths.get("paye")+paths.get("sep"));
//							payePanel.setOutputMarkupId(true);
//							selectPanel.replaceWith(payePanel);
//							selectPanel = payePanel;
//							if(arg0 != null){
//								arg0.addComponent(selectPanel);
//							}else{
//								System.out.println("Waor");
//							}
							
						}else if (sdl ==0){
							System.out.println("sdl");
							
//							Panel sdlPanel = new SDLReportPanel("selectPanel",getEditState(),pageModel,parentPage, paths.get("sdl")+paths.get("sep"));
//							sdlPanel.setOutputMarkupId(true);
//							selectPanel.replaceWith(sdlPanel);
//							selectPanel = sdlPanel;
//							if(arg0 != null){
//								arg0.addComponent(selectPanel);
//							}else{
//								System.out.println("Waor");
//							}
						}
					} 
					
				});
	
				return (SRSDropDownChoice) tempSRSDropDown;
	
	}
	
	@Override
	public String getPageName() {
		return "Tax GUI";
	}
	
	protected ITaxGuiController getSessionBean() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(ITaxGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " ITaxGuiController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("ITaxGuiController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	

}
