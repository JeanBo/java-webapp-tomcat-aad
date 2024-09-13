package za.co.liberty.web.pages.tax;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.taxgui.ITaxGuiController;
import za.co.liberty.dto.taxxml.TaxLogDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.tax.PAYEReportPanel.TaxForm;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.xml.tax.Application;
import za.co.liberty.xml.tax.Parameter;

public class SDLReportPanel extends BasePanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextField startDate;
	private TextField endDate;
	private TextField prevDate;
	private Application pageModel;
	private Parameter execNode;
	private Form xmlForm;
	private Button execute;
	private String masterFullPath;
	private String masterFullDirectoryLogPath;
	
	
	public SDLReportPanel(String id, EditStateType editState, Application pageModel, BasePage parentPage,String  fullPathName, String sep){
		super(id, editState);
		this.pageModel = pageModel;
		//replace the log path to sdl logpath
		masterFullPath = fullPathName;
		
		
		
		//create new directory, set log path to this directory and set the master directory to the new path. 
		
		//get a string of the current timestamp
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date dates = new Date();
		String date = "_" + format.format(dates).toString() + "";
		
		//add the year and liberty year this ran for and action indicator and liberty start and end
		//Lookup the xml
//		String taxYear = mapXMLLookup("TaxYear").getValue();
//		String libStart = mapXMLLookup("Liberty_StartDate").getValue();
//		String libEnd = mapXMLLookup("Liberty_EndDate").getValue();
//		String action = mapXMLLookup("ActionIndicator").getValue();
//		String extraDirectoryInfo = "_"+taxYear+"_"+libStart+"_"+libEnd+"_"+action;
		String extraDirectoryInfo = "SDL_"+(mapXMLLookup("StartDate").getValue().substring(0,10).toString() +"_" + mapXMLLookup("EndDate").getValue().substring(0,10).toString())+"";
		String combinedMaster = extraDirectoryInfo + "_" + date;
		
		String newDirectoryPath = fullPathName + sep + combinedMaster;
		masterFullDirectoryLogPath = newDirectoryPath;
		System.out.println("Prepared a new directory:" + masterFullDirectoryLogPath);
//		boolean success = (new File(masterFullDirectoryLogPath)).mkdir();
//		if (success) {
//			System.out.println("Current log:" + mapXMLLookup("LogPath").getValue() + ": change to:" + masterFullDirectoryLogPath);
//			mapXMLLookup("LogPath").setValue(masterFullDirectoryLogPath);
//		}
		
		init();
	}
	
	private boolean createDirectory(){
		boolean success = (new File(masterFullDirectoryLogPath)).mkdir();
		if (success) {
			System.out.println("Current log:" + mapXMLLookup("LogPath").getValue() + ": change to:" + masterFullDirectoryLogPath);
			mapXMLLookup("LogPath").setValue(masterFullDirectoryLogPath);
		}
		return success;
	}
	
	private void init(){
		add(xmlForm = new TaxForm("xmlForm"));
	}
		public class TaxForm extends Form {

			private static final long serialVersionUID = 1L;
			
			public TaxForm(String id){
				super(id);
				add(startDate = createStartDate("startDate"));
				add(endDate = createEndDate("endDate"));
				add(prevDate = createPrevDate("prevDate"));
				add(execute = createExecuteButton("execute"));
			}

			@Override
			protected void onSubmit() {
				// TODO Auto-generated method stub
				super.onSubmit();
				createDirectory();
				getSessionBean().saveXML(pageModel);
				
				TaxLogDTO taxSessionLogDTO = new TaxLogDTO();
				
				taxSessionLogDTO.setProcess(pageModel.getName());
				taxSessionLogDTO.setUacfid(SRSAuthWebSession.get().getSessionUser().getUacfId()+"");
				getSessionBean().execute(pageModel, taxSessionLogDTO);
				
				getSessionBean().execute(pageModel,taxSessionLogDTO);
				setResponsePage(TaxGuiPage.class);	
//				setRedirect(true);
			}	
			
		}
		private Button createExecuteButton(String id){
			Button temp = new Button(id);
			temp.setOutputMarkupId(true);
			return temp;
		}
		private TextField createPrevDate(String id){
			TextField start = new TextField(id, new PropertyModel(mapXMLLookup("PreviousEndDate"),"value"));
			start.setOutputMarkupId(true);
			return start;
		}
		private TextField createStartDate(String id){
			TextField start = new TextField(id, new PropertyModel(mapXMLLookup("StartDate"),"value"));
			start.setOutputMarkupId(true);
			return start;
		}
		private TextField createEndDate(String id){
			TextField start = new TextField(id, new PropertyModel(mapXMLLookup("EndDate"),"value"));
			start.setOutputMarkupId(true);
			return start;
		}
		public Parameter mapXMLLookup(String name){
			for(Parameter instNode: pageModel.getParameters().getParameter()){
				if(instNode.getName().equalsIgnoreCase(name)){
					execNode = instNode;
				}
			}
			return execNode;
		}
		transient protected ITaxGuiController guiController;
		protected transient Logger logger;
		
		protected ITaxGuiController getSessionBean() {
			if (guiController == null) {
				try {
					guiController = ServiceLocator.lookupService(ITaxGuiController.class);
				} catch (NamingException namingErr) {
					logger.error(""
							+ " ITaxGuiController can not be lookedup:"
							+ namingErr.getMessage());
					CommunicationException comm = new CommunicationException("ITaxGuiController can not be looked up!");
					throw new CommunicationException(comm);
				} 
			}
			return guiController;
		}
}
