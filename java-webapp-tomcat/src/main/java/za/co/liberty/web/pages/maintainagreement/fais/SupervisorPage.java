package za.co.liberty.web.pages.maintainagreement.fais;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementRoleDTO;
import za.co.liberty.dto.agreement.maintainagreement.AgreementRoleGridDTO;
import za.co.liberty.dto.party.fais.supervision.FAISCategorySupervisionDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.maintainagreement.OtherPartyRolesPanel;
import za.co.liberty.web.pages.maintainagreement.model.SupervisorModificationPageModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Modify supervisor for a specific category
 * 
 * @author JZB0608
 *
 */
public class SupervisorPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;
	String categoryId="";
	OtherPartyRolesPanel otherPartyRolesPanel;
	private List<AgreementRoleDTO> adjustedCurrentAndFutureOtherPartyRoles;
	private SRSModalWindow window;
	private static Logger logger = Logger.getLogger(SupervisorPage.class);
	private transient  IAgreementGUIController guiController;
	private List<AgreementRoleDTO> originalCurrentAndFutureOtherPartyRoles;	
	private List<AgreementRoleDTO> rolesOfdifferentType;	
	private long typeOID;	
	private long agreementNo;
	private long agreementKind;
	private Date agreementStartDate;
	
	private SupervisorModificationPageModel pageModel;
	
	
	public SupervisorPage(EditStateType editStateType , List<AgreementRoleDTO> currentAndFutureSupervisionRoles, 
			SRSModalWindow window , long typeOID, long agreementNo, long agreementKind, Date agreementStartDate) {
		super();
		this.categoryId=categoryId;
		this.window = window;
		this.typeOID = typeOID;
		this.agreementNo = agreementNo;
		this.agreementKind = agreementKind;
		this.agreementStartDate = agreementStartDate;
		
		// Create page model
		pageModel = new SupervisorModificationPageModel();
		pageModel.setSelectedItem(currentAndFutureSupervisionRoles);
		
		// Split fields
		adjustedCurrentAndFutureOtherPartyRoles = new ArrayList<AgreementRoleDTO>();
		if(currentAndFutureSupervisionRoles != null) {
			for (AgreementRoleDTO roleDTO : currentAndFutureSupervisionRoles) {
				adjustedCurrentAndFutureOtherPartyRoles.add((AgreementRoleDTO)SerializationUtils.clone(roleDTO));
			}	
		}
		rolesOfdifferentType = pageModel.splitOutRolesNotOfType(currentAndFutureSupervisionRoles,typeOID);		
		
		// Store pagemodel
		window.setSessionModelForPage(pageModel);
		
		
		// Create panel
		otherPartyRolesPanel=new OtherPartyRolesPanel("otherPartyRoles", editStateType, 
					adjustedCurrentAndFutureOtherPartyRoles, typeOID , false, agreementNo);
			
		originalCurrentAndFutureOtherPartyRoles = currentAndFutureSupervisionRoles;
		add(new SupervisorForm("supervisorform"));
	}
	
	

	@Override
	public String getPageName() {		
		return "Supervison for category "+ categoryId;
	}
	
	
	/**
	 * Form definition
	 * 
	 * @author JZB0608
	 *
	 */
	public class SupervisorForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public SupervisorForm(String id) {
			super(id);
			add(otherPartyRolesPanel );
			

			Button button = new Button("saveSuperVisor");
			
			button.add(new AjaxFormSubmitBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onError(AjaxRequestTarget target) {
					pageModel.setModalWizardSuccess(false);
//					window.setSessionModelForPage(pageModel);
					target.add(getFeedBackPanel());
					if (logger.isDebugEnabled())
						logger.debug("SaveButton.onError - success = false");
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					if (logger.isDebugEnabled())
						logger.debug("SupervisorForm.onSubmit");
					
					//name change from Supervisor Roles to other Party Roles
					List<AgreementRoleGridDTO> agreementOtherPartyRoles = otherPartyRolesPanel.getAgreementOtherPartyRoles();
					adjustedCurrentAndFutureOtherPartyRoles = new ArrayList<AgreementRoleDTO>();		
					for (AgreementRoleGridDTO gridDTO : agreementOtherPartyRoles) {											
						adjustedCurrentAndFutureOtherPartyRoles.add(gridDTO.getRole());						
					}
					try {
						if(adjustedCurrentAndFutureOtherPartyRoles != null && adjustedCurrentAndFutureOtherPartyRoles.size() > 0) {
							getGuiController().validateAgreementRolesOfKind(agreementNo, agreementKind, agreementStartDate, 
									adjustedCurrentAndFutureOtherPartyRoles, new ArrayList<AgreementRoleDTO>(),  
									adjustedCurrentAndFutureOtherPartyRoles.get(0).getAgreementRoleKind()
//									(long)RoleKindType.SUPERVISEDBY.getKind()
									);							
						}						
						originalCurrentAndFutureOtherPartyRoles.clear();
						originalCurrentAndFutureOtherPartyRoles.addAll(adjustedCurrentAndFutureOtherPartyRoles);			
						if(rolesOfdifferentType != null){
							 originalCurrentAndFutureOtherPartyRoles.addAll(rolesOfdifferentType);			
						}
						 
						// Update pagemodel
						pageModel.setSelectedItem(originalCurrentAndFutureOtherPartyRoles);
						pageModel.setModalWizardSuccess(true);
						window.setSessionModelForPage(pageModel);
						
						if (logger.isDebugEnabled())
							logger.debug("SupervisorForm.onSubmit.success");
						
						target.add(getFeedBackPanel());
						window.close(target);
					
					} catch (ValidationException e) {
						for(String msg:e.getErrorMessages()){								
							getFeedBackPanel().error(msg);
						}
						target.add(getFeedBackPanel());
					}			

//					do_Ok(target);
				}
			});
			add(button);	
		}
	}



//	public OtherPartyRolesPanel getOtherPartyRolesPanel() {
//		return otherPartyRolesPanel;
//	}
//
//	public void setOtherPartyRolesPanel(OtherPartyRolesPanel otherPartyRolesPanel) {
//		this.otherPartyRolesPanel = otherPartyRolesPanel;
//	}
		
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	protected IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}


//	public List<AgreementRoleDTO> getOriginalCurrentAndFutureOtherPartyRoles() {
//		return originalCurrentAndFutureOtherPartyRoles;
//	}
//
//	public void setOriginalCurrentAndFutureOtherPartyRoles(
//			List<AgreementRoleDTO> originalCurrentAndFutureOtherPartyRoles) {
//		this.originalCurrentAndFutureOtherPartyRoles = originalCurrentAndFutureOtherPartyRoles;
//	}	
}
