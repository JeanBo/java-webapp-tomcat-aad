package za.co.liberty.web.pages.party;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.party.medicalaid.MedicalAidDetailDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PartyType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.model.MedicalAidDetailsPanelModel;

/**
 * New medical aid details panel for a party
 * @author dzs2610
 *
 */
public class MedicalAidDetailsPanel extends BasePanel implements ISecurityPanel{
	
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(MedicalAidDetailsPanel.class);
	
	private boolean initialized;
	
	private boolean existingMedicalAidRequests;	
	
	private MedicalAidDetailsPanelModel panelModel;
	
	private transient IPartyMaintenanceController partyMaintenanceController;	
	
	private EditStateType[] editstateTypes; 
	
	private List<Panel> allMedicalAidDetailsPanels = new ArrayList<Panel>();	
	private List<Panel> allPanels = new ArrayList<Panel>();	
	
	private ModalWindow historyWindow;
	
	private Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	private static final SimpleDateFormat dteFormatter = new SimpleDateFormat("dd/MM/yyyy");//09/03/2010
	
	
	public MedicalAidDetailsPanel(String id, EditStateType editState, 
			MedicalAidDetailsPanelModel panelModel, Page parentPage) {
		super(id, editState,parentPage);		
		this.panelModel = panelModel;		
	}	
	
	@Override
	protected void onBeforeRender() {
		if(!initialized)
		{
			List<RequestKindType> unAuthRequests = getOutStandingRequestKinds();			
			//check for existing requests FIRST as other panels use variables set here
			for (RequestKindType kind : unAuthRequests) {
				if(kind == RequestKindType.MaintainMedicalAidDetails)
					existingMedicalAidRequests = true;
				
			}
			
			editstateTypes = new EditStateType[]{EditStateType.MODIFY,EditStateType.ADD};
			if(getEditState().isViewOnly() || existingMedicalAidRequests){
				editstateTypes = new EditStateType[]{};				
			}
			
			MedicalAidDetailsPanelModel model = initPanelModel();
			add(new MedicalAidDetailsForm("medicalDetailsForm",model));
			add(historyWindow = createHistoryWindow("historyWindow"));
			initialized = true;
		}		
		super.onBeforeRender();		
	}
	
	/**
	 * Create a history button
	 * @param id
	 * @return
	 */
	private Button createHistoryButton(String id) {
		final Button button = new Button(id);
		button.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 0L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				historyWindow.show(target);
			}
		});
		if (!getEditState().isViewOnly() 
				|| getEditState() == EditStateType.AUTHORISE) {
			button.setEnabled(false);
			button.setVisible(false);
		}		
		button.setOutputMarkupId(true);
		button.setOutputMarkupPlaceholderTag(true);		
		return button;
	}
	
	
	/**
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private ModalWindow createHistoryWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("History");		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {					
				return new MedicalAidHistoryPage(window,panelModel.getCurrentPartyID());
				
			}			
		});			
		// Initialise window settings
		window.setMinimalHeight(400);
		window.setInitialHeight(400);
		window.setMinimalWidth(700);
		window.setInitialWidth(700);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);	
		window.setOutputMarkupId(true);
		window.setOutputMarkupPlaceholderTag(true);
		window.setCookieName("MedicalAidHistoryPage");//window.setPageMapName("MedicalAidHistoryPage");
		return window;
	}		
	
	/**
	 * Init the panle model for the panel
	 *
	 */
	private MedicalAidDetailsPanelModel initPanelModel() {
		//create the panelModel
		MedicalAidDetailDTO medicalAidDetails = null;	
		if(panelModel == null){
			panelModel = new MedicalAidDetailsPanelModel();			
		}		
		if(panelModel.getCurrentPartyType() != PartyType.PERSON.getType()){			
			error("Only People can have medical aid details");
			existingMedicalAidRequests = true;//disable all fields			
		}
		panelModel.setLibertyMonthStartDates(getPartyMaintenanceController().getLibertyMonthStartDatesfromTaxYearStart());
		medicalAidDetails = panelModel.getMedicalAidDetail();		
		if(medicalAidDetails == null 
				&& panelModel.getCurrentPartyID() > 0
				&& panelModel.getCurrentPartyType() == PartyType.PERSON.getType()){
			//get the medical aid detail from the DB
			medicalAidDetails = getPartyMaintenanceController().getMedicalAidDetailDTO(panelModel.getCurrentPartyID());
			if(medicalAidDetails != null){
				//panelModel.setCurrentMedicalAidEffectiveDate(medicalAidDetails.getEffectiveDate());
				//force user changes to start today
				if(panelModel.getLibertyMonthStartDates().size() != 0){
					medicalAidDetails.setEffectiveDate(
							panelModel.getLibertyMonthStartDates().get(
									panelModel.getLibertyMonthStartDates().size() - 1));
				}
			}
		}	
		if(medicalAidDetails == null){			
			medicalAidDetails = new MedicalAidDetailDTO();	
			medicalAidDetails.setPartyoid(panelModel.getCurrentPartyID());		
		}		
		panelModel.setMedicalAidDetail(medicalAidDetails);		
		panelModel.setMedicalAidDetailBeforeImage((MedicalAidDetailDTO) SerializationUtils.clone(medicalAidDetails));		
		return panelModel;
	}

	private class MedicalAidDetailsForm extends Form {
		private static final long serialVersionUID = 1L;
		
		@SuppressWarnings("serial")
		private MedicalAidDetailsForm(String id,final MedicalAidDetailsPanelModel panelModel) {
			super(id);		
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			add(leftPanel);	
			MedicalAidDetailDTO medicalAidDetailDTO = panelModel.getMedicalAidDetail();
			
//			has medical aid indicator
			HelperPanel hasMedicalAidPanel = createPageField(medicalAidDetailDTO,"Has Medical Aid","panel","hasMedicalAid",
					ComponentType.CHECKBOX, false,true,editstateTypes);
			GUIFieldPanel hasMedicalAid = createGUIFieldPanel("Has Medical Aid",
					"Has Medical Aid","Has Medical Aid",
					hasMedicalAidPanel,1);			
			leftPanel.add(hasMedicalAid);	
			if(hasMedicalAidPanel.getEnclosedObject() instanceof CheckBox){
				CheckBox check = (CheckBox) hasMedicalAidPanel.getEnclosedObject();
				check.add(new AjaxFormComponentUpdatingBehavior("click"){				
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						//if on then enable all medical aid fields							
						enableAllMedicalAidPanels(panelModel.getMedicalAidDetail().isHasMedicalAid(),target);
					}				
				});	
			}	
			allPanels.add(hasMedicalAidPanel);
			List<IDValueDTO> medicalAids = getPartyMaintenanceController().getAllMedicalAids();
			
			GUIFieldPanel medicalAidProvider = createGUIFieldPanel("Medical Aid Provider",
					"Medical Aid Provider","Medical Aid Provider",
					createDropdownField(medicalAidDetailDTO,"Medical Aid Provider","panel","medicalAidprovider",
							medicalAids,
							new ChoiceRenderer<IDValueDTO>("name","oid"),"Select one",
							true,true,editstateTypes),2);			
			leftPanel.add(medicalAidProvider);
			allMedicalAidDetailsPanels.add(medicalAidProvider);
			allPanels.add(medicalAidProvider);
			
			//medical aid number
			GUIFieldPanel medicalAidNumber = createGUIFieldPanel("Medical Aid Number",
					"Medical Aid Number","Medical Aid Number",
					createPageField(medicalAidDetailDTO,"Medical Aid Number","panel","medicalAidNumber",
							ComponentType.TEXTFIELD, true,true,editstateTypes),3);			
			leftPanel.add(medicalAidNumber);
			allMedicalAidDetailsPanels.add(medicalAidNumber);
			allPanels.add(medicalAidNumber);
			
			GUIFieldPanel currentEffectiveDate = createGUIFieldPanel("Current Medical Detail Effective Date",
					"Current Medical Detail Effective Date","Current Medical Detail Effective Date",
					createPageField(medicalAidDetailDTO,"Medical Detail Effective Date","panel","currentMedicalAidEffectiveDate",
							ComponentType.LABEL, false,false,new EditStateType[]{}),4);			
			leftPanel.add(currentEffectiveDate);
			allPanels.add(currentEffectiveDate);
			
//			GUIFieldPanel effectiveDate = createGUIFieldPanel("Medical Detail Effective Date",
//					"Medical Detail Effective Date","Medical Detail Effective Date",
//					createPageField(medicalAidDetailDTO,"Medical Detail Effective Date","panel","effectiveDate",
//							ComponentType.DATE_SELECTION_TEXTFIELD, true,true,editstateTypes),5);
			
			GUIFieldPanel effectiveDate = createGUIFieldPanel("Medical Detail Effective Date",
					"Medical Detail Effective Date","Medical Detail Effective Date",
					createDropdownField(medicalAidDetailDTO,"Medical Detail Effective Date","panel","effectiveDate",
							panelModel.getLibertyMonthStartDates(),
							new ChoiceRenderer<Date>("date","date"){

								@Override
								public Object getDisplayValue(Date arg0) {									
									return dteFormatter.format(arg0);
								}

								@Override
								public String getIdValue(Date arg0, int arg1) {									
									return dteFormatter.format(arg0);
									//return super.getIdValue(arg0, arg1);
								}
						
					},"Select one",
							false,true,editstateTypes),5);
			
			leftPanel.add(effectiveDate);
			effectiveDate.setOutputMarkupId(true);
			effectiveDate.setOutputMarkupPlaceholderTag(true);
			if(getEditState().isViewOnly() && getEditState() != EditStateType.AUTHORISE){
				effectiveDate.setVisible(false);
			}
			allPanels.add(effectiveDate);
			//allMedicalAidDetailsPanels.add(effectiveDate);
			
//			primary member indicator
			GUIFieldPanel primaryMember = createGUIFieldPanel("Primary Member",
					"Primary Member","Primary Member",
					createPageField(medicalAidDetailDTO,"Primary Member","panel","primaryMember",
							ComponentType.CHECKBOX, true,true,editstateTypes),6);			
			leftPanel.add(primaryMember);
			allMedicalAidDetailsPanels.add(primaryMember);
			allPanels.add(primaryMember);
			
			//number dependants
			GUIFieldPanel numberDependants = createGUIFieldPanel("Number Of Dependants",
					"Number Of Dependants","Number Of Dependants",
					createPageField(medicalAidDetailDTO,"Number Of Dependants","panel","numberDependants",
							ComponentType.TEXTFIELD, true,true,editstateTypes),7);			
			leftPanel.add(numberDependants);
			allMedicalAidDetailsPanels.add(numberDependants);
			allPanels.add(numberDependants);
			
			GUIFieldPanel currentContribution = createGUIFieldPanel("Contribution Amount",
					"Contribution Amount","Contribution Amount",
					createPageField(medicalAidDetailDTO,"Contribution Amount","panel","currentContribution",
							ComponentType.TEXTFIELD, true,true,editstateTypes),8);			
			leftPanel.add(currentContribution);
			allMedicalAidDetailsPanels.add(currentContribution);
			allPanels.add(currentContribution);
			
			add(new IFormValidator() {

				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {				
					return null;
				}

				public void validate(final Form form) {		
					if (logger.isDebugEnabled())
						logger.debug("Validate isViewOnly (will return)="+getEditState().isViewOnly());
					if (getEditState().isViewOnly()) {
						return;
					}
					boolean validate = true;
					for(FormComponent comp : validationComponents){
						if(!comp.isValid()){
							validate = false;
							if (logger.isDebugEnabled())
								logger.debug("Form validation issue, cancel business validation.");
							break;
						} 
					}
					if(validate){
						try{				
							//validate party without contact details
							getPartyMaintenanceController().validateMedicalAidDetail(panelModel.getMedicalAidDetail());					
						}catch(ValidationException ex){
							for(String error : ex.getErrorMessages()){
								MedicalAidDetailsForm.this.error(error);								
							}
						}
					}
				}
				
			});
			
			
			add(createHistoryButton("historyButton"));
						
//			set all panels disable or enabled
			enableAllMedicalAidPanels(panelModel.getMedicalAidDetail().isHasMedicalAid(),null);
			if(existingMedicalAidRequests){
				enablePanels(false,null,allPanels);
			}
		}
	}
	

	/**
	 * Enable or disable all medical aid details panels based on the value sent through
	 * @param enabled
	 */
	private void enableAllMedicalAidPanels(boolean enabled, AjaxRequestTarget ajaxRequest){
		enablePanels(enabled, ajaxRequest, allMedicalAidDetailsPanels);
	}
	
	/**
	 * Enable or disable all panels based on the value sent through
	 * @param enabled
	 */
	private void enablePanels(boolean enabled, AjaxRequestTarget ajaxRequest, List<Panel> panels){
		for(Panel panel : panels){
			if(panel != null){
				panel.setOutputMarkupId(true);
				panel.setOutputMarkupPlaceholderTag(true);
				panel.setEnabled(enabled);							
				setComponentsEnabled(panel.iterator(),enabled, ajaxRequest);				
				if(ajaxRequest != null){
					ajaxRequest.add(panel);//ajaxRequest.addComponent(panel);
				}
			}
		}
	}
	
	/**
	 * Set entire tree enabled or disabled
	 * @param component
	 */
	private void setComponentsEnabled(Iterator<? extends Component> componentsIterator, 
			boolean enabled, 
			AjaxRequestTarget ajaxRequest){
		if(componentsIterator == null || !componentsIterator.hasNext()){
			return;
		}
		while(componentsIterator.hasNext()){
			Component comp = componentsIterator.next();
			comp.setEnabled(enabled);			
			comp.setOutputMarkupId(true);
			comp.setOutputMarkupPlaceholderTag(true);
			if(comp instanceof MarkupContainer){			
				setComponentsEnabled(((MarkupContainer)comp).iterator(),enabled,ajaxRequest);
			}
			if(comp instanceof FormComponent){
				//((FormComponent)comp).setRequired(enabled);
			}
			if(ajaxRequest != null){
				ajaxRequest.add(comp);//ajaxRequest.addComponent(comp);
			}
		}		
	}
	

	public Class getPanelClass() {		
		return MedicalAidDetailsPanel.class;
	}

	/**
	 * Get the PartyMaintenanceController bean
	 * @return
	 */
	public IPartyMaintenanceController getPartyMaintenanceController() {
		if(partyMaintenanceController == null){
			try {
				partyMaintenanceController = ServiceLocator.lookupService(IPartyMaintenanceController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyMaintenanceController;
	}

}
