package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;//org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.party.validator.IPartyValidator;
import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.databaseenum.CountryCodeDBEnumDTO;
import za.co.liberty.dto.databaseenum.EthnicityDBEnumDTO;
import za.co.liberty.dto.databaseenum.GenderDBEnumDTO;
import za.co.liberty.dto.databaseenum.JobTitleDBEnumDTO;
import za.co.liberty.dto.databaseenum.LanguagePreferenceDBEnumDTO;
import za.co.liberty.dto.databaseenum.MaritalStatusDBEnumDTO;
import za.co.liberty.dto.databaseenum.PrefixTitleDBEnumDTO;
import za.co.liberty.dto.party.EmployeeDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.party.PersonDTO;
import za.co.liberty.dto.party.taxdetails.TaxAdvisorDTO;
import za.co.liberty.dto.userprofiles.ContextAgreementDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ContextPartyDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.PARTY_ID_TYPE;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;

/**
 * Panel containing only person details
 * 
 * @author DZS2610
 * 
 */
public class PersonDetailsPanel extends BasePartyDetailsPanel {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private PersonDetailsForm form;

	private MaintainPartyPageModel pageModel;

	private ModalWindow rolesPopup;

	private ModalWindow assistantPopup;

	private Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();

	private transient IPartyValidator partyValidator;

	private transient IAgreementManagement agreementManagement;

	private GUIFieldPanel passportCountryGUIPanel;

	private boolean addingAgreement = false;

	private AgreementKindType agreementKindBeingAdded;

	private boolean modifyingPartyOnAddAgreement = false;
	
	private boolean jobTittle = false;

	private GUIFieldPanel wcStartDatePanel;
	private GUIFieldPanel wcEndDatePanel;
	private GUIFieldPanel wcTestStartDatePanel;
	private GUIFieldPanel wcTestEndDatePanel;
	private GUIFieldPanel taxStartDatePanel;
	private GUIFieldPanel taxEndDatePanel;
	private GUIFieldPanel entityNamePanel;
	private GUIFieldPanel pracNoPanel;
	private FormComponent entityNameField;
	private FormComponent pracNoField;
	private GUIFieldPanel isWcUserPanel;
	private GUIFieldPanel isWcTestUserPanel;
	private WebMarkupContainer taxAdvisorDetailsSection;

	private static final Logger logger = Logger
			.getLogger(PersonDetailsPanel.class);

	/**
	 * Default constructor
	 * 
	 * @param arg0
	 */
	public PersonDetailsPanel(String id, MaintainPartyPageModel pageModel,
			EditStateType editState, Page parentPage) {
		this(id, pageModel, editState, parentPage, false, null);
	}

	/**
	 * If we are adding an agreement, we can only change the birthdate, id
	 * number
	 * 
	 * @param arg0
	 */
	public PersonDetailsPanel(String id, MaintainPartyPageModel pageModel,
			EditStateType editState, Page parentPage, boolean addingAgreement,
			AgreementKindType agreementKindBeingAdded) {
		super(id, editState, parentPage);
		this.pageModel = pageModel;
		this.addingAgreement = addingAgreement;
		this.agreementKindBeingAdded = agreementKindBeingAdded;
		modifyingPartyOnAddAgreement = addingAgreement && pageModel != null
				&& pageModel.getPartyDTO() != null
				&& pageModel.getPartyDTO().getOid() > 0;
		add(form = new PersonDetailsForm("personDetailsForm", pageModel));
		add(rolesPopup = createRolesWindow("roleModelWindow"));
		add(assistantPopup = createPAAdjustmentModalWindow("assistantModelWindow"));
	}

	private class PersonDetailsForm extends Form {
		private static final long serialVersionUID = 1L;

		private EmployeeDTO employeeDTO;// employee as standalone party actualy
										// captures liberty employees that don't
										// have agreements/ work for someone
										// that does
		private TaxAdvisorDTO taxAdvisorDTO;
		private PartyRoleDTO wealthConnectionRole;
		private PartyRoleDTO wealthConnectionTestRole;

		@SuppressWarnings("serial")
		private PersonDetailsForm(String id,
				final MaintainPartyPageModel pageModel) {
			super(id);
			if (pageModel == null || pageModel.getPartyDTO() == null
					|| !(pageModel.getPartyDTO() instanceof EmployeeDTO)) {
				PersonDetailsPanel.this
						.error("Person sent in is not an employee object");
				employeeDTO = new EmployeeDTO();
			} else {
				employeeDTO = (EmployeeDTO) pageModel.getPartyDTO();
			}

			ArrayList<PARTY_ID_TYPE> types = new ArrayList<PARTY_ID_TYPE>(2);
			for (PARTY_ID_TYPE type : PARTY_ID_TYPE.values()) {
				types.add(type);
			}
			// check for outstanding requests requiring authorisation
			boolean outstandingPartyWithApproval = false;
			boolean outstandingPartyStraight = false;
			boolean outstandingUserAccess = false;
			for (RequestKindType type : getOutStandingRequestKinds()) {
				if (type == RequestKindType.MaintainPartyWithApproval) {
					outstandingPartyWithApproval = true;
				} else if (type == RequestKindType.MaintainPartyDetails) {
					outstandingPartyStraight = true;
				} else if (type == RequestKindType.MaintainOtherSystemAccess) {
					outstandingUserAccess = true;
				}
			}
			// check the requests the user can rais and disable fields
			// accordingly
			boolean canRaiseUserAccess = true;
			boolean canPartyStraight = true;
			boolean canRaisePartyWithApproval = true;
			ISessionUserProfile loggedInUser = SRSAuthWebSession.get()
					.getSessionUser();
			canRaiseUserAccess = loggedInUser
					.isAllowRaise(RequestKindType.MaintainOtherSystemAccess);
			canPartyStraight = loggedInUser
					.isAllowRaise(RequestKindType.MaintainPartyDetails);
			canRaisePartyWithApproval = loggedInUser
					.isAllowRaise(RequestKindType.MaintainPartyWithApproval);

			EditStateType[] idEditstateTypes = new EditStateType[] {
					EditStateType.MODIFY, EditStateType.ADD };
			if (!(isViewOnly()) && pageModel.getPartyDTO() != null
					&& pageModel.getPartyDTO().getOid() > 0) {
				if (outstandingPartyWithApproval || !canRaisePartyWithApproval) {
					idEditstateTypes = new EditStateType[] {};
				}
			}

			EditStateType[] wcEditstateTypes = new EditStateType[] {
					EditStateType.MODIFY, EditStateType.ADD };
			if (!(isViewOnly()) && pageModel.getPartyDTO() != null
					&& pageModel.getPartyDTO().getOid() > 0) {
				if (outstandingUserAccess || !canRaiseUserAccess) {
					wcEditstateTypes = new EditStateType[] {};
				}
			}

			EditStateType[] partyStraightEditstateTypes = new EditStateType[] {
					EditStateType.MODIFY, EditStateType.ADD };
			if (!(isViewOnly()) && pageModel.getPartyDTO() != null
					&& pageModel.getPartyDTO().getOid() > 0) {
				if (outstandingPartyStraight || !canPartyStraight) {
					partyStraightEditstateTypes = new EditStateType[] {};
				}
			}
			if (modifyingPartyOnAddAgreement) {
				// only job title and uacfid can be changed on add agreement
				idEditstateTypes = new EditStateType[] {};
				partyStraightEditstateTypes = new EditStateType[] {};
			}

			WebMarkupContainer comp = createRolesView("rolesPopupView");
			comp.add(createViewRolesButton("rolesPopupButton"));
			add(comp);

			WebMarkupContainer comp2 = createRolesView("assistantPopupView");
			comp2.add(createViewAssistantButton("assistantPopupButton"));
			add(comp2);

			RepeatingView leftPanel = new RepeatingView("leftPanel");
			RepeatingView rightPanel = new RepeatingView("rightPanel");
			RepeatingView jobTitleTablePanel = new RepeatingView(
					"jobTitlePanel");
			RepeatingView bottomleftPanel = new RepeatingView("bottomleftPanel");
			RepeatingView bottomrightPanel = new RepeatingView(
					"bottomrightPanel");

			RepeatingView wcLeftPanel = new RepeatingView("wcLeftPanel");
			RepeatingView wcMiddlePanel = new RepeatingView("wcMiddlePanel");
			RepeatingView wcRightPanel = new RepeatingView("wcRightPanel");

			RepeatingView wcTestLeftPanel = new RepeatingView("wcTestLeftPanel");
			RepeatingView wcTestMiddlePanel = new RepeatingView(
					"wcTestMiddlePanel");
			RepeatingView wcTestRightPanel = new RepeatingView(
					"wcTestRightPanel");

			RepeatingView txLeftPanel = new RepeatingView("txLeftPanel");
			RepeatingView txMiddlePanel = new RepeatingView("txMiddlePanel");
			RepeatingView txRightPanel = new RepeatingView("txRightPanel");

			RepeatingView taxAdvisorDetailsPanel = new RepeatingView(
					"taxAdvisorDetailsPanel");

			taxAdvisorDetailsSection = new WebMarkupContainer(
					"taxAdvisorDetailsSection");
			taxAdvisorDetailsSection.setOutputMarkupId(true);
			taxAdvisorDetailsSection.setOutputMarkupPlaceholderTag(true);
			taxAdvisorDetailsSection.add(taxAdvisorDetailsPanel);

			add(leftPanel);
			add(rightPanel);
			add(jobTitleTablePanel);
			add(bottomleftPanel);
			add(bottomrightPanel);
			add(wcLeftPanel);
			add(wcMiddlePanel);
			add(wcRightPanel);
			add(wcTestLeftPanel);
			add(wcTestMiddlePanel);
			add(wcTestRightPanel);
			add(txLeftPanel);
			add(txMiddlePanel);
			add(txRightPanel);
			add(taxAdvisorDetailsSection);

			
			GUIFieldPanel titlePanel = createGUIFieldPanel(
					"Title",
					"Title",
					"Title",
					createDropdownField(employeeDTO, "Title", "panel", "title",
						 	DatabaseEnumHelper.getDatabaseDTO(
									PrefixTitleDBEnumDTO.class, true, true,
									true) , new ChoiceRenderer("name", "key"),
							"Select one", true, true,
							partyStraightEditstateTypes), 1);
			if (((HelperPanel) titlePanel.getComponent()).getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) titlePanel
								.getComponent()).getEnclosedObject());
			}
			leftPanel.add(titlePanel);

			GUIFieldPanel firstNamePanel = createGUIFieldPanel(
					"First Name",
					"First Name",
					"First Name",
					createPageField(employeeDTO, "First Name", "panel",
							"firstName", ComponentType.TEXTFIELD, true, true,
							partyStraightEditstateTypes), 2);
			if (((HelperPanel) firstNamePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) firstNamePanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(firstNamePanel);

			GUIFieldPanel middleNamePanel = createGUIFieldPanel(
					"Middle Name(s)",
					"Middle Name(s)",
					"Middle Name(s)",
					createPageField(employeeDTO, "Middle Name(s)", "panel",
							"middleName", ComponentType.TEXTFIELD, false, true,
							partyStraightEditstateTypes), 3);
			if (((HelperPanel) middleNamePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) middleNamePanel
								.getComponent()).getEnclosedObject());
			}
			leftPanel.add(middleNamePanel);

			GUIFieldPanel lastNamePanel = createGUIFieldPanel(
					"Last Name",
					"Last Name",
					"Last Name",
					createPageField(employeeDTO, "Last Name", "panel",
							"surname", ComponentType.TEXTFIELD, true, true,
							partyStraightEditstateTypes), 4);
			if (((HelperPanel) lastNamePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) lastNamePanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(lastNamePanel);

			GUIFieldPanel KnownAsNamePanel = createGUIFieldPanel(
					"Known As Name",
					"Known As Name",
					"Known As Name",
					createPageField(employeeDTO, "Known As Name", "panel",
							"knowAsName", ComponentType.TEXTFIELD, false, true,
							partyStraightEditstateTypes), 5);
			if (((HelperPanel) KnownAsNamePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) KnownAsNamePanel
								.getComponent()).getEnclosedObject());
			}
			leftPanel.add(KnownAsNamePanel);

			GUIFieldPanel maidenNamePanel = createGUIFieldPanel(
					"Maiden Name",
					"Maiden Name",
					"Maiden Name",
					createPageField(employeeDTO, "Maiden Name", "panel",
							"maidenName", ComponentType.TEXTFIELD, false, true,
							partyStraightEditstateTypes), 6);
			if (((HelperPanel) maidenNamePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) maidenNamePanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(maidenNamePanel);
// #WICKETTEST #WICKETFIX			
		
				
			GUIFieldPanel nationalityPanel = createGUIFieldPanel(
					"Nationality",
					"Nationality",
					"Nationality",
					createDropdownField(employeeDTO, "Nationality", "panel",
							"nationality", DatabaseEnumHelper.getDatabaseDTO(
									CountryCodeDBEnumDTO.class, true, true,
									true), new ChoiceRenderer("name", "key"),
							"Select one", true, true,
							partyStraightEditstateTypes), 7);
			if (((HelperPanel) nationalityPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent nationalityComp = (FormComponent) ((HelperPanel) nationalityPanel
						.getComponent()).getEnclosedObject();
				if (nationalityComp instanceof DropDownChoice) {
					nationalityComp.add(new AttributeModifier("style",
							"width: 200px;"));
				}
				validationComponents.add(nationalityComp);
			}
			leftPanel.add(nationalityPanel);

			EditStateType[] uacfEditStates = partyStraightEditstateTypes;
			if (modifyingPartyOnAddAgreement) {
				uacfEditStates = new EditStateType[] { EditStateType.MODIFY };
			}

			GUIFieldPanel uacfidPanel = createGUIFieldPanel(
					"Liberty UACF ID",
					"Liberty UACF ID",
					"Liberty UACF ID",
					createPageField(employeeDTO, "Liberty UACF ID", "panel",
							"securityId", ComponentType.TEXTFIELD, false, true,
							uacfEditStates), 8);
			if (((HelperPanel) uacfidPanel.getComponent()).getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) uacfidPanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(uacfidPanel);

			GUIFieldPanel idTypePanel = createGUIFieldPanel(
					"Id Type",
					"Id Type",
					"Id Type",
					createDropdownField(employeeDTO, "Id Type", "panel",
							"identificationNumberType", types,
							new ChoiceRenderer("description", "key"),
							"Select one", true, false, idEditstateTypes), 9);
			if (((HelperPanel) idTypePanel.getComponent()).getEnclosedObject() instanceof FormComponent) {
				FormComponent typeSelection = (FormComponent) ((HelperPanel) idTypePanel
						.getComponent()).getEnclosedObject();
				typeSelection.add(new AjaxFormComponentUpdatingBehavior(
						"change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						// update the passport country selection
						updatePassportCountrySelection(target);
					}
				});

				validationComponents.add(typeSelection);
			}
			leftPanel.add(idTypePanel);

			GUIFieldPanel idNumberPanel = createGUIFieldPanel(
					"Id Number",
					"Id Number",
					"Id Number",
					createPageField(employeeDTO, "Id Number", "panel",
							"identificationNumber", ComponentType.TEXTFIELD,
							true, true, idEditstateTypes), 10);
			if (((HelperPanel) idNumberPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) idNumberPanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(idNumberPanel);

			GUIFieldPanel dateofBirthPanel = createGUIFieldPanel(
					"Date Of Birth",
					"Date Of Birth",
					"Date Of Birth",
					createPageField(employeeDTO, "Date Of Birth", "panel",
							"dateOfBirth",
							ComponentType.DATE_SELECTION_TEXTFIELD, true, true,
							partyStraightEditstateTypes), 11);
			if (((HelperPanel) dateofBirthPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) dateofBirthPanel
								.getComponent()).getEnclosedObject());
			}
			if (((HelperPanel) dateofBirthPanel.getComponent())
					.getEnclosedObject() instanceof SRSDateField) {
				SRSDateField d =  (SRSDateField)
						((HelperPanel)dateofBirthPanel.getComponent()).getEnclosedObject();
				d.add(d.newDatePicker());
			}
			leftPanel.add(dateofBirthPanel);
			//SBS0510 for JML
			GUIFieldPanel upn = createGUIFieldPanel(
					"UPN",
					"UPN",
					"UPN",
					createPageField (employeeDTO, "UPN", "panel",
							"UPN", ComponentType.TEXTFIELD, false, true,
							partyStraightEditstateTypes),12);
			if (((HelperPanel) upn.getComponent()).getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) upn
								.getComponent()).getEnclosedObject());
			}
		rightPanel.add(upn);
// #WICKETTEST #WICKETFIX			
// MSK#Change this need to be removed,This logic temporarily used in place of DatabaseEnumHelper.getDatabaseDTO
			passportCountryGUIPanel = createGUIFieldPanel(
					"Passport Country",
					"Passport Country",
					"Passport Country",
					createDropdownField(employeeDTO, "Passport Country",
							"panel", "passportCountry", DatabaseEnumHelper
									.getDatabaseDTO(CountryCodeDBEnumDTO.class,
											true, true, true),
							new ChoiceRenderer("name", "key"), "Select one",
							true, true, idEditstateTypes), 13);
			passportCountryGUIPanel.setOutputMarkupId(true);
			passportCountryGUIPanel.setOutputMarkupPlaceholderTag(true);
			HelperPanel passportCountryPanel = (HelperPanel) passportCountryGUIPanel
					.getComponent();
			if (passportCountryPanel.getEnclosedObject() instanceof FormComponent) {
				FormComponent passportComp = (FormComponent) passportCountryPanel
						.getEnclosedObject();
				if (passportComp instanceof DropDownChoice) {
					passportComp.add(new AttributeModifier("style",
							"width: 200px;"));
				}
				validationComponents.add(passportComp);
			}
			rightPanel.add(passportCountryGUIPanel);

			GUIFieldPanel genderPanel = createGUIFieldPanel(
					"Gender",
					"Gender",
					"Gender",
					createDropdownField(employeeDTO, "Gender", "panel",
							"gender", DatabaseEnumHelper.getDatabaseDTO(
									GenderDBEnumDTO.class, true, true),
							new ChoiceRenderer("name", "key"), "Select one",
							true, true, partyStraightEditstateTypes), 14);
			if (((HelperPanel) genderPanel.getComponent()).getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) genderPanel
								.getComponent()).getEnclosedObject());
			}
			leftPanel.add(genderPanel);


			GUIFieldPanel racePanel = createGUIFieldPanel(
					"Race",
					"Race",
					"Race",
					createDropdownField(employeeDTO, "Race", "panel", "race",
							DatabaseEnumHelper.getDatabaseDTO(
									EthnicityDBEnumDTO.class, true, true),
							new ChoiceRenderer("name", "key"), "Select one",
							true, true, partyStraightEditstateTypes), 15);
			if (((HelperPanel) racePanel.getComponent()).getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) racePanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(racePanel);

			EditStateType[] jobTitleEditStates = partyStraightEditstateTypes;
			if (modifyingPartyOnAddAgreement) {
				jobTitleEditStates = new EditStateType[] { EditStateType.MODIFY };
			}

			
			GUIFieldPanel jobTitlePanel = createGUIFieldPanel(
					"Job Title",
					"Job Title",
					"Job Title",
					createDropdownField(employeeDTO, "Job Title", "panel",
							"jobTitle", DatabaseEnumHelper.getDatabaseDTO(
									JobTitleDBEnumDTO.class, true, true, true),
							new ChoiceRenderer("name", "key"), "Select one",
							true, true, jobTitleEditStates), 16);
			if (((HelperPanel) jobTitlePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent jbComp = (FormComponent) ((HelperPanel) jobTitlePanel
						.getComponent()).getEnclosedObject();
				if (jbComp instanceof DropDownChoice) {
					jbComp.add(new AttributeModifier("style",
							"width: 200px;"));
				}
				validationComponents.add(jbComp);
			}
			leftPanel.add(jobTitlePanel);


			GUIFieldPanel maritalStatusPanel = createGUIFieldPanel(
					"Marital Status",
					"Marital Status",
					"Marital Status",
					createDropdownField(employeeDTO, "Marital Status", "panel",
							"maritalStatus", DatabaseEnumHelper.getDatabaseDTO(
									MaritalStatusDBEnumDTO.class, false, true),
							new ChoiceRenderer("name", "key"), "Select one",
							true, true, partyStraightEditstateTypes), 16);
			if (((HelperPanel) maritalStatusPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) maritalStatusPanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(maritalStatusPanel);

			GUIFieldPanel jobTitleStartDatePanel = createGUIFieldPanel(
					"Job Title Start Date",
					"Job Title Start Date",
					"Job Title Start Date",
					createPageField(employeeDTO, "Job Title Start Date",
							"panel", "jobStartDate",
							ComponentType.DATE_SELECTION_TEXTFIELD, true, true,
							jobTitleEditStates), 17);
			if (((HelperPanel) jobTitleStartDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) jobTitleStartDatePanel
								.getComponent()).getEnclosedObject());
			}
			if (((HelperPanel) jobTitleStartDatePanel.getComponent())
					.getEnclosedObject() instanceof SRSDateField) {
				SRSDateField d =  (SRSDateField)
						((HelperPanel)jobTitleStartDatePanel.getComponent()).getEnclosedObject();
				d.add(d.newDatePicker());
			}
			leftPanel.add(jobTitleStartDatePanel);


			GUIFieldPanel languagePrefPanel = createGUIFieldPanel(
					"Language Preference",
					"Language Preference",
					"Language Preference",
					createDropdownField(employeeDTO, "Language Preference",
							"panel", "languagePreference",DatabaseEnumHelper
									.getDatabaseDTO(
											LanguagePreferenceDBEnumDTO.class,
											true, true), new ChoiceRenderer(
									"name", "key"), "Select one", false, true,
							partyStraightEditstateTypes), 18);
			if (((HelperPanel) languagePrefPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) languagePrefPanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(languagePrefPanel);

			GUIFieldPanel libertyEmployeeNumberPanel = createGUIFieldPanel(
					"Liberty Employee Number",
					"Liberty Employee Number",
					"Liberty Employee Number",
					createPageField(employeeDTO, "Liberty Employee Number",
							"panel", "employeeNumber", ComponentType.TEXTFIELD,
							false, true, partyStraightEditstateTypes), 19);
			if (((HelperPanel) libertyEmployeeNumberPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) libertyEmployeeNumberPanel
								.getComponent()).getEnclosedObject());
			}
			//
			leftPanel.add(libertyEmployeeNumberPanel);

			GUIFieldPanel stanlibUACFIDPanel = createGUIFieldPanel(
					"Stanlib UACF ID",
					"Stanlib UACF ID",
					"Stanlib UACF ID",
					createPageField(employeeDTO, "Stanlib UACF ID", "panel",
							"stanlibUACFID", ComponentType.TEXTFIELD, false,
							true, partyStraightEditstateTypes), 20);
			if (((HelperPanel) stanlibUACFIDPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) stanlibUACFIDPanel
								.getComponent()).getEnclosedObject());
			}
			rightPanel.add(stanlibUACFIDPanel);

			EditStateType[] contractingEditStates = partyStraightEditstateTypes;
			if (modifyingPartyOnAddAgreement) {
				contractingEditStates = new EditStateType[] { EditStateType.MODIFY };
			}
			GUIFieldPanel contractedDatePanel = createGUIFieldPanel(
					"Contracted Start Date",
					"Contracted Start Date",
					"Contracted Start Date",
					createPageField(employeeDTO, "Contracted Start Date",
							"panel", "contractedDate",
							ComponentType.DATE_SELECTION_TEXTFIELD, false,
							true, contractingEditStates), 21);
			if (((HelperPanel) contractedDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) contractedDatePanel
								.getComponent()).getEnclosedObject());
			}
			if (((HelperPanel) contractedDatePanel.getComponent())
					.getEnclosedObject() instanceof SRSDateField) {
				SRSDateField d =  (SRSDateField)
						((HelperPanel)contractedDatePanel.getComponent()).getEnclosedObject();
				d.add(d.newDatePicker());
			}
			//
			leftPanel.add(contractedDatePanel);

			EditStateType[] industryStartDateEditStates = partyStraightEditstateTypes;
			if (modifyingPartyOnAddAgreement) {
				industryStartDateEditStates = new EditStateType[] { EditStateType.MODIFY, EditStateType.ADD };
			}
			GUIFieldPanel industryStartDatePanel = createGUIFieldPanel(
					"Industry Start Date",
					"Industry Start Date",
					"Industry Start Date",
					createPageField(employeeDTO, "Industry Start Date",
							"panel", "industryStartDate",
							ComponentType.DATE_SELECTION_TEXTFIELD, false,
							true, industryStartDateEditStates), 22);
			if (((HelperPanel) industryStartDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) industryStartDatePanel
								.getComponent()).getEnclosedObject());
			}
			if (((HelperPanel) industryStartDatePanel.getComponent())
					.getEnclosedObject() instanceof SRSDateField) {
				SRSDateField d =  (SRSDateField)
						((HelperPanel)industryStartDatePanel.getComponent()).getEnclosedObject();
				d.add(d.newDatePicker());
			}
			//
			rightPanel.add(industryStartDatePanel);

			// SSM2707 ADDED for FR15 Tenure SWETA MENON Begin

			EditStateType[] tenureStartDateEditStates = partyStraightEditstateTypes;
			if (modifyingPartyOnAddAgreement) {
				tenureStartDateEditStates = new EditStateType[] { EditStateType.MODIFY };
			}
			GUIFieldPanel tenureStartDatePanel = createGUIFieldPanel(
					"Tenure Start Date",
					"Tenure Start Date",
					"Tenure Start Date",
					createPageField(employeeDTO, "Tenure Start Date", "panel",
							"tenureStartDate",
							ComponentType.DATE_SELECTION_TEXTFIELD, false,
							true, tenureStartDateEditStates), 34);
			if (((HelperPanel) tenureStartDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) tenureStartDatePanel
								.getComponent()).getEnclosedObject());
			}
			if (((HelperPanel) tenureStartDatePanel.getComponent())
					.getEnclosedObject() instanceof SRSDateField) {
				SRSDateField d =  (SRSDateField)
						((HelperPanel)tenureStartDatePanel.getComponent()).getEnclosedObject();
				d.add(d.newDatePicker());
			}
			rightPanel.add(tenureStartDatePanel);
			// SSM2707 ADDED for FR15 Tenure SWETA MENON End

			// get WC obj
			
	
			EditStateType[] tenureStartDateEditStates1 = partyStraightEditstateTypes;
			if (modifyingPartyOnAddAgreement) {
				tenureStartDateEditStates1 = new EditStateType[] { EditStateType.MODIFY };
			}
	
			if (employeeDTO.getWealthConnectionRole() != null) {
				wealthConnectionRole = employeeDTO.getWealthConnectionRole();

			} else {
				wealthConnectionRole = new PartyRoleDTO();
				wealthConnectionRole
						.setPartyRoleType(PartyRoleType.WEALTHCONNECTIONUSER);
				wealthConnectionRole.setEffectiveFrom(new Date());
				employeeDTO.setWealthConnectionRole(wealthConnectionRole);
			}
			if (wealthConnectionRole.getOid() <= 0) {
				wealthConnectionRole.setMainRolePlayerID(employeeDTO.getOid());
				// keep in sync
				PartyRoleDTO oldWCRole = (((EmployeeDTO) pageModel
						.getPartyBeforeImage()) != null) ? ((EmployeeDTO) pageModel
						.getPartyBeforeImage()).getWealthConnectionRole()
						: null;
				if (oldWCRole != null) {
					oldWCRole.setMainRolePlayerID(employeeDTO.getOid());
				}
			}

			isWcUserPanel = createGUIFieldPanel(
					"Blueprint Professional Production User",
					"Blueprint Professional Production User",
					"Blueprint Professional Production User",
					createPageField2(employeeDTO,
							"Blueprint Professional Production User", "panel",
							"wealthConnectionUser", ComponentType.CHECKBOX,
							false, false, wcEditstateTypes), 23);
			isWcUserPanel.setOutputMarkupId(true);
			isWcUserPanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) isWcUserPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent wcUsercheckbox = (FormComponent) ((HelperPanel) isWcUserPanel
						.getComponent()).getEnclosedObject();
				wcUsercheckbox.add(new AjaxFormComponentUpdatingBehavior(
						"click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						updateWcUserDateSelection(target, wcStartDatePanel,
								wcEndDatePanel, isWcUserPanel, false);
					}
				});
				validationComponents.add(wcUsercheckbox);
			}
			wcLeftPanel.add(isWcUserPanel);

			EditStateType[] startDateEditStates = wcEditstateTypes;
			if (employeeDTO.getWealthConnectionRole() != null
					&& employeeDTO.getWealthConnectionRole().getOid() > 0
					&& employeeDTO.getWealthConnectionRole().getEffectiveFrom() != null) {
				startDateEditStates = new EditStateType[] {};
			}

			wcStartDatePanel = createGUIFieldPanel(
					"Start Date",
					"Start Date",
					"Wealth Connection User Start Date",
					createPageField(employeeDTO,
							"Wealth Connection Start Date", "panel",
							"wealthConnectionRole.effectiveFrom",
							ComponentType.DATE_SELECTION_TEXTFIELD, true, true,
							startDateEditStates), 24);
			wcStartDatePanel.setOutputMarkupId(true);
			wcStartDatePanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) wcStartDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent startDate = (FormComponent) ((HelperPanel) wcStartDatePanel
						.getComponent()).getEnclosedObject();
				startDate.add(new AttributeModifier("readonly", "true"));
				validationComponents.add(startDate);
			}
			wcMiddlePanel.add(wcStartDatePanel);

			wcEndDatePanel = createGUIFieldPanel(
					"End Date",
					"End Date",
					"Wealth Connection User End Date",
					createPageField(employeeDTO, "Wealth Connection End Date",
							"panel", "wealthConnectionRole.effectiveTo",
							ComponentType.DATE_SELECTION_TEXTFIELD, false,
							true, wcEditstateTypes), 25);
			wcEndDatePanel.setOutputMarkupId(true);
			wcEndDatePanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) wcEndDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) wcEndDatePanel
								.getComponent()).getEnclosedObject());
			}
			wcRightPanel.add(wcEndDatePanel);

			// WC test indicator
			// get test WC obj
			if (employeeDTO.getWealthConnectionTestRole() != null) {
				wealthConnectionTestRole = employeeDTO
						.getWealthConnectionTestRole();

			} else {
				wealthConnectionTestRole = new PartyRoleDTO();
				wealthConnectionTestRole
						.setPartyRoleType(PartyRoleType.WEALTHCONNECTIONTESTUSER);
				wealthConnectionTestRole.setEffectiveFrom(new Date());
				employeeDTO
						.setWealthConnectionTestRole(wealthConnectionTestRole);
			}
			if (wealthConnectionTestRole.getOid() <= 0) {
				wealthConnectionTestRole.setMainRolePlayerID(employeeDTO
						.getOid());
				// keep in sync
				PartyRoleDTO oldWCRole = (((EmployeeDTO) pageModel
						.getPartyBeforeImage()) != null) ? ((EmployeeDTO) pageModel
						.getPartyBeforeImage()).getWealthConnectionTestRole()
						: null;
				if (oldWCRole != null) {
					oldWCRole.setMainRolePlayerID(employeeDTO.getOid());
				}
			}

			isWcTestUserPanel = createGUIFieldPanel(
					"Blueprint Professional Import Test User",
					"Blueprint Professional Import Test User",
					"Blueprint Professional Import Test User",
					createPageField2(employeeDTO,
							"Blueprint Professional Import Test User", "panel",
							"wealthConnectionTestUser", ComponentType.CHECKBOX,
							false, false, wcEditstateTypes), 26);
			isWcTestUserPanel.setOutputMarkupId(true);
			isWcTestUserPanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) isWcTestUserPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent wcUsercheckbox = (FormComponent) ((HelperPanel) isWcTestUserPanel
						.getComponent()).getEnclosedObject();
				wcUsercheckbox.add(new AjaxFormComponentUpdatingBehavior(
						"click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						updateWcUserDateSelection(target, wcTestStartDatePanel,
								wcTestEndDatePanel, isWcTestUserPanel, true);
					}
				});
				validationComponents.add(wcUsercheckbox);
			}
			wcTestLeftPanel.add(isWcTestUserPanel);

			if (employeeDTO.getWealthConnectionTestRole() != null
					&& employeeDTO.getWealthConnectionTestRole().getOid() > 0
					&& employeeDTO.getWealthConnectionTestRole()
							.getEffectiveFrom() != null) {
				startDateEditStates = new EditStateType[] {};
			}

			wcTestStartDatePanel = createGUIFieldPanel(
					"Start Date",
					"Start Date",
					"Wealth Connection Test User Start Date",
					createPageField(employeeDTO,
							"Wealth Connection Test User Start Date", "panel",
							"wealthConnectionTestRole.effectiveFrom",
							ComponentType.DATE_SELECTION_TEXTFIELD, true, true,
							startDateEditStates), 27);
			wcTestStartDatePanel.setOutputMarkupId(true);
			wcTestStartDatePanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) wcTestStartDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent startDate = (FormComponent) ((HelperPanel) wcTestStartDatePanel
						.getComponent()).getEnclosedObject();
				startDate.add(new AttributeModifier("readonly", "true"));
				validationComponents.add(startDate);
			}
			wcTestMiddlePanel.add(wcTestStartDatePanel);

			wcTestEndDatePanel = createGUIFieldPanel(
					"End Date",
					"End Date",
					"Wealth Connection Test User  End Date",
					createPageField(employeeDTO,
							"Wealth Connection Test User  End Date", "panel",
							"wealthConnectionTestRole.effectiveTo",
							ComponentType.DATE_SELECTION_TEXTFIELD, false,
							true, wcEditstateTypes), 28);
			wcTestEndDatePanel.setOutputMarkupId(true);
			wcTestEndDatePanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) wcTestEndDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) wcTestEndDatePanel
								.getComponent()).getEnclosedObject());
			}
			wcTestRightPanel.add(wcTestEndDatePanel);

			// get Tax Advisor obj
			if (employeeDTO.getTaxAdvisorDTO() != null) {
				taxAdvisorDTO = employeeDTO.getTaxAdvisorDTO();
			} else {
				taxAdvisorDTO = new TaxAdvisorDTO();
				taxAdvisorDTO.setEffectiveFrom(new Date());
				employeeDTO.setTaxAdvisorDTO(taxAdvisorDTO);
			}
			if (taxAdvisorDTO.getOid() <= 0) {
				taxAdvisorDTO.setMainRolePlayerID(employeeDTO.getOid());
				// keep in sync
				PartyRoleDTO oldtaxRole = ((EmployeeDTO) pageModel
						.getPartyBeforeImage() != null) ? ((EmployeeDTO) pageModel
						.getPartyBeforeImage()).getTaxAdvisorDTO() : null;
				if (oldtaxRole != null) {
					oldtaxRole.setMainRolePlayerID(employeeDTO.getOid());
				}
			}

			GUIFieldPanel isTaxAdvisorPanel = createGUIFieldPanel(
					"Registered TAX Advisor",
					"Registered TAX Advisor",
					"Registered TAX Advisor",
					createPageField2(employeeDTO, "Registered TAX Advisor",
							"panel", "taxAdvisor", ComponentType.CHECKBOX,
							false, false, partyStraightEditstateTypes), 29);
			if (((HelperPanel) isTaxAdvisorPanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent taxAdvCheckBox = (FormComponent) ((HelperPanel) isTaxAdvisorPanel
						.getComponent()).getEnclosedObject();
				taxAdvCheckBox.add(new AjaxFormComponentUpdatingBehavior(
						"click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						updateTaxAdvisorSelection(target);
					}
				});

				validationComponents.add(taxAdvCheckBox);
			}
			txLeftPanel.add(isTaxAdvisorPanel);

			startDateEditStates = partyStraightEditstateTypes;
			if (taxAdvisorDTO != null && taxAdvisorDTO.getOid() > 0
					&& taxAdvisorDTO.getEffectiveFrom() != null) {
				startDateEditStates = new EditStateType[] {};
			}
			taxStartDatePanel = createGUIFieldPanel(
					"Start Date",
					"Start Date",
					"Tax Advisor Start Date",
					createPageField(taxAdvisorDTO, "Tax Advisor Start Date",
							"panel", "effectiveFrom",
							ComponentType.DATE_SELECTION_TEXTFIELD, true, true,
							startDateEditStates), 30);
			taxStartDatePanel.setOutputMarkupId(true);
			taxStartDatePanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) taxStartDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				FormComponent startDate = (FormComponent) ((HelperPanel) taxStartDatePanel
						.getComponent()).getEnclosedObject();
				startDate.add(new AttributeModifier("readonly", "true"));
				validationComponents.add(startDate);
			}
			txMiddlePanel.add(taxStartDatePanel);

			taxEndDatePanel = createGUIFieldPanel(
					"End Date",
					"End Date",
					"Tax Advisor End Date",
					createPageField(taxAdvisorDTO, "Tax Advisor End Date",
							"panel", "effectiveTo",
							ComponentType.DATE_SELECTION_TEXTFIELD, false,
							true, partyStraightEditstateTypes), 31);
			taxEndDatePanel.setOutputMarkupId(true);
			taxEndDatePanel.setOutputMarkupPlaceholderTag(true);
			if (((HelperPanel) taxEndDatePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				validationComponents
						.add((FormComponent) ((HelperPanel) taxEndDatePanel
								.getComponent()).getEnclosedObject());
			}
			txRightPanel.add(taxEndDatePanel);

			// now we add the additional tax advisor details to its own panel

			entityNamePanel = createGUIFieldPanel(
					"Legal Entity Name",
					"Legal Entity Name",
					"Legal Entity Name",
					createPageField(taxAdvisorDTO, "Legal Entity Name",
							"panel", "taxLegalEntityName",
							ComponentType.TEXTFIELD, true, true,
							partyStraightEditstateTypes), 32);
			if (((HelperPanel) entityNamePanel.getComponent())
					.getEnclosedObject() instanceof FormComponent) {
				entityNameField = (FormComponent) ((HelperPanel) entityNamePanel
						.getComponent()).getEnclosedObject();
				validationComponents.add(entityNameField);
			}
			taxAdvisorDetailsPanel.add(entityNamePanel);

			pracNoPanel = createGUIFieldPanel(
					"Tax Practitioner Number",
					"Tax Practitioner Number",
					"Tax Practitioner Number",
					createPageField(taxAdvisorDTO, "Tax Practitioner Number",
							"panel", "taxPractitionerNumber",
							ComponentType.TEXTFIELD, true, true,
							partyStraightEditstateTypes), 33);
			if (((HelperPanel) pracNoPanel.getComponent()).getEnclosedObject() instanceof FormComponent) {
				pracNoField = (FormComponent) ((HelperPanel) pracNoPanel
						.getComponent()).getEnclosedObject();
				validationComponents.add(pracNoField);
			}
			taxAdvisorDetailsPanel.add(pracNoPanel);

			add(new AbstractFormValidator() {

				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {
					if (getEditState().isAdd() || addingAgreement) {
						// In add we have issues with showing form validation.
						return null;
					}
//					return validationComponents.toArray(new FormComponent[] {});	
					return null;
				}

				public void validate(final Form form) {
					if (logger.isDebugEnabled())
						logger.debug("Validated Party - viewOnly=" + isViewOnly()
								+ " ,addingAgreemnt=" + addingAgreement
								+ " first call.");
					if (isViewOnly() && !addingAgreement) {
						if (logger.isDebugEnabled())
							logger.debug("Validated Party - viewOnly=" + isViewOnly()
									+ " ,addingAgreemnt=" + addingAgreement
									+ "  - ignoring validation");
						return;
					}
					
					boolean validate = true;
					for (FormComponent comp : validationComponents) {
						if (!comp.isValid()) {
							validate = false;
							if (logger.isDebugEnabled())
								logger.debug("   not a valid component "  + comp);
								break;
//							if (!comp.checkRequired()) {
//								getFeedBackPanel().error(comp.getLabel().getObject() + " is required..");
//								continue;
//							} else {
////								comp.getValidatorKeyPrefix()
////								if (logger.isDebugEnabled())
//									logger.info("  -- (not required, different validation) -- validation error  " + comp
//											+ "   --keyPrefix=" + comp.getValidators());
//								getFeedBackPanel().error(comp.getLabel().getObject() + " issue processing form value.");
//								
//							}
						}
					}
					if (logger.isDebugEnabled())
						logger.debug("Validated Party - validateForm=" + validate
								+ " ,addingAgreemnt=" + addingAgreement);
					
					if (validate) {
						try {
							// validate party without contact details
							getPartyValidator().validate(
									pageModel.getPartyDTO(), null,false,
									addingAgreement, agreementKindBeingAdded);
						} catch (ValidationException ex) {
							for (String error : ex.getErrorMessages()) {
								if (logger.isDebugEnabled())
									logger.debug("Validation error:"+error);
								getFeedBackPanel().error(error);
//								PersonDetailsForm.this.error(error);
							}
						}
					}
				}

			});
			// update the passport country field
			updatePassportCountrySelection(null);

			// //update the Wealth connection user date fields
			updateWcUserDateSelection(null, wcStartDatePanel, wcEndDatePanel,
					isWcUserPanel, false);

			// // update the Wealth connection test user date fields
			updateWcUserDateSelection(null, wcTestStartDatePanel,
					wcTestEndDatePanel, isWcTestUserPanel, true);

			// update tax advisor section
			updateTaxAdvisorSelection(null);
		}

		/**
		 * Returns true if edit state is in one of the view states
		 * 
		 * @return
		 */
		private boolean isViewOnly() {
			return (getEditState() == EditStateType.VIEW || getEditState() == EditStateType.AUTHORISE);
		}

		/**
		 * Returns true if edit state is in one of the view states
		 * 
		 * @return
		 */
		private boolean isEditState() {
			return (getEditState() == EditStateType.ADD || getEditState() == EditStateType.MODIFY);
		}

	}

	/**
	 * Update the passport country selection based on what has been selected
	 * 
	 * @param target
	 */
	private void updatePassportCountrySelection(AjaxRequestTarget target) {
		if (passportCountryGUIPanel != null && pageModel.getPartyDTO() != null
				&& pageModel.getPartyDTO() instanceof PersonDTO) {
			HelperPanel passportCountryPanel = (HelperPanel) passportCountryGUIPanel
					.getComponent();
			FormComponent passportComp = (passportCountryPanel
					.getEnclosedObject() instanceof FormComponent) ? (FormComponent) passportCountryPanel
					.getEnclosedObject() : null;
			PersonDTO person = (PersonDTO) pageModel.getPartyDTO();
			if (person.getIdentificationNumberType() != null
					&& person.getIdentificationNumberType() == PARTY_ID_TYPE.PASSPORT) {
				// visible and required
				if (passportComp != null) {
					passportComp.setRequired(true);
				}
				passportCountryGUIPanel.setVisible(true);
			} else {
				// not visible and not required
				if (passportComp != null) {
					passportComp.setRequired(false);
				}
				passportCountryGUIPanel.setVisible(false);
			}
			// update with ajax
			if (target != null) {
				target.add(passportCountryGUIPanel);
			}
		}
	}

	/**
	 * Display the date fields once the Weath Connection User checkbox is
	 * selected
	 * 
	 * @param target
	 */
	private void updateWcUserDateSelection(AjaxRequestTarget target,
			GUIFieldPanel startDatePanel, GUIFieldPanel endDatePanel,
			GUIFieldPanel isWcUserPanel, boolean testRole) {
		if (startDatePanel != null && endDatePanel != null
				&& pageModel.getPartyDTO() != null
				&& pageModel.getPartyDTO() instanceof EmployeeDTO) {
			HelperPanel wcUserStartPanel = (HelperPanel) startDatePanel
					.getComponent();
			HelperPanel wcUserEndPanel = (HelperPanel) endDatePanel
					.getComponent();
			FormComponent startDateComp = (wcUserStartPanel.getEnclosedObject() instanceof FormComponent) ? (FormComponent) wcUserStartPanel
					.getEnclosedObject() : null;
			FormComponent endDateComp = (wcUserEndPanel.getEnclosedObject() instanceof FormComponent) ? (FormComponent) wcUserEndPanel
					.getEnclosedObject() : null;
			EmployeeDTO employeeDTO = (EmployeeDTO) pageModel.getPartyDTO();
			out: if ((!testRole && employeeDTO.isWealthConnectionUser())
					|| (testRole && employeeDTO.isWealthConnectionTestUser())) {
				if (target != null) {
					if (!testRole && employeeDTO.isWealthConnectionUser()) {
						if (employeeDTO.getWealthConnectionRole()
								.getRolePlayerReference() == null
								|| employeeDTO.getWealthConnectionRole()
										.getRolePlayerReference().getOid() <= 0) {
							// set the roleplayer reference to the agreement in
							// context then validate immediatly so users can
							// check the agreement selection
							ContextAgreementDTO agmtContext = SRSAuthWebSession
									.get().getContextDTO()
									.getAgreementContextDTO();
							if (agmtContext != null
									&& agmtContext.getAgreementNumber() != null) {
								// find agmt and set in role
								try {
									ResultAgreementDTO agmt = getAgreementManagement()
											.findAgreementWithSRSAgreementNr(
													agmtContext
															.getAgreementNumber());
									employeeDTO.getWealthConnectionRole()
											.setRolePlayerReference(agmt);
								} catch (DataNotFoundException e) {
									logger.error("Could not find agreement "
											+ agmtContext.getAgreementNumber(),
											e);
									// this should never hapen but ignor if it
									// does, this will not validate correctly
								}
							}
						}
						try {
							// validate WC requirements
							getPartyValidator()
									.validateWealthConnectionRole(
											employeeDTO,
											employeeDTO
													.getWealthConnectionRole(),
											true);
						} catch (ValidationException e) {
							employeeDTO.setWealthConnectionUser(false);
							for (String error : e.getErrorMessages()) {
								error(error);
							}
							break out;
						}
					} else if (testRole
							&& employeeDTO.isWealthConnectionTestUser()) {
						if (employeeDTO.getWealthConnectionTestRole()
								.getRolePlayerReference() == null
								|| employeeDTO.getWealthConnectionTestRole()
										.getRolePlayerReference().getOid() <= 0) {
							// set the roleplayer reference to the agreement in
							// context then validate immediatly so users can
							// check the agreement selection
							ContextAgreementDTO agmtContext = SRSAuthWebSession
									.get().getContextDTO()
									.getAgreementContextDTO();
							if (agmtContext != null
									&& agmtContext.getAgreementNumber() != null) {
								// find agmt and set in role
								try {
									ResultAgreementDTO agmt = getAgreementManagement()
											.findAgreementWithSRSAgreementNr(
													agmtContext
															.getAgreementNumber());
									employeeDTO.getWealthConnectionTestRole()
											.setRolePlayerReference(agmt);
								} catch (DataNotFoundException e) {
									logger.error("Could not find agreement "
											+ agmtContext.getAgreementNumber(),
											e);
									// this should never hapen but ignor if it
									// does, this will not validate correctly
								}
							}
						}
						try {
							// validate WC test user requirements
							getPartyValidator().validateWealthConnectionRole(
									employeeDTO,
									employeeDTO.getWealthConnectionTestRole(),
									true);
						} catch (ValidationException e) {
							employeeDTO.setWealthConnectionTestUser(false);
							for (String error : e.getErrorMessages()) {
								error(error);
							}
							break out;
						}
					}
				}
				if (startDateComp != null && endDateComp != null) {
					startDateComp.setRequired(true);
					endDateComp.setRequired(false);
				}
				startDatePanel.setVisible(true);
				endDatePanel.setVisible(true);
			} else {
				if (startDateComp != null && endDateComp != null) {
					startDateComp.setRequired(false);
					endDateComp.setRequired(false);
				}
				startDatePanel.setVisible(false);
				endDatePanel.setVisible(false);
			}
			// update with ajax
			if (target != null) {
				target.add(startDatePanel);
				target.add(endDatePanel);
				target.add(isWcUserPanel);
				target.add(getFeedBackPanel());
			}

		}
	}

	/**
	 * Display the tax advisor fields once the tax advisor checkbox is selected
	 * 
	 * @param target
	 */
	private void updateTaxAdvisorSelection(AjaxRequestTarget target) {
		if (taxStartDatePanel != null && taxEndDatePanel != null
				&& pageModel.getPartyDTO() != null
				&& pageModel.getPartyDTO() instanceof PersonDTO) {
			HelperPanel taxStartPanel = (HelperPanel) taxStartDatePanel
					.getComponent();
			HelperPanel taxEndPanel = (HelperPanel) taxEndDatePanel
					.getComponent();
			FormComponent startDateComp = (taxStartPanel.getEnclosedObject() instanceof FormComponent) ? (FormComponent) taxStartPanel
					.getEnclosedObject() : null;
			FormComponent endDateComp = (taxEndPanel.getEnclosedObject() instanceof FormComponent) ? (FormComponent) taxEndPanel
					.getEnclosedObject() : null;
			PersonDTO person = (PersonDTO) pageModel.getPartyDTO();
			if (person.isTaxAdvisor()) {
				if (startDateComp != null && endDateComp != null) {
					startDateComp.setRequired(true);
					endDateComp.setRequired(false);
				}
				if (entityNameField != null && pracNoField != null) {
					entityNameField.setRequired(true);
					pracNoField.setRequired(true);
				}
				taxStartDatePanel.setVisible(true);
				taxEndDatePanel.setVisible(true);
				taxAdvisorDetailsSection.setVisible(true);

			} else {
				if (startDateComp != null && endDateComp != null) {
					startDateComp.setRequired(false);
					endDateComp.setRequired(false);
				}
				if (entityNameField != null && pracNoField != null) {
					entityNameField.setRequired(false);
					pracNoField.setRequired(false);
				}
				taxStartDatePanel.setVisible(false);
				taxEndDatePanel.setVisible(false);
				taxAdvisorDetailsSection.setVisible(false);
			}
			// update with ajax
			if (target != null) {
				target.add(taxStartDatePanel);
				target.add(taxEndDatePanel);
				target.add(taxAdvisorDetailsSection);
			}

		}
	}

	/**
	 * Will only display the view roles link if a party is selected
	 * 
	 * @param id
	 * @return
	 */
	private WebMarkupContainer createRolesView(String id) {
		WebMarkupContainer comp = new WebMarkupContainer(id);
		comp.setOutputMarkupPlaceholderTag(true);
		comp.setOutputMarkupId(true);
		if (pageModel == null || pageModel.getPartyDTO() == null
				|| (pageModel.getPartyDTO() instanceof HierarchyNodeDTO)
				|| pageModel.getPartyDTO().getId() == 0
				|| getEditState() == EditStateType.AUTHORISE) {
			comp.setVisible(false);
		} else {
			comp.setVisible(true);
		}
		return comp;
	}

	/**
	 * create the button to popup the view roles popup
	 * 
	 * @return
	 */
	private Link createViewRolesButton(String buttonID) {
		Link but = new AjaxFallbackLink(buttonID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				rolesPopup.show(target);
			}
		};
		return but;
	}

	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createRolesWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Person Hierarchy Roles");

		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
				ContextPartyDTO selectedParty = null;
				if (dto != null) {
					selectedParty = dto.getPartyContextDTO();
				}
				return new HierarchyRolePage(window, selectedParty);
			}
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(420);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}


	/**
	 * Create the modal window for PA roles
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createPAAdjustmentModalWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		//window.setTitle("View PA roles");
		window.setTitle("View related roles");

		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				return new ViewPAToPage(window, pageModel);
			}
		});
		// Initialise window settings
		window.setMinimalHeight(350);
		window.setInitialHeight(350);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setCookieName("PA_List");//window.setPageMapName("PA_List");
		return window;
	}

	/**
	 * get the PartyValidator bean
	 * 
	 * @return
	 */
	private IPartyValidator getPartyValidator() {
		if (partyValidator == null) {
			try {
				partyValidator = ServiceLocator
						.lookupService(IPartyValidator.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyValidator;
	}

	/**
	 * get the Agreement Managment bean
	 * 
	 * @return
	 */
	private IAgreementManagement getAgreementManagement() {
		if (agreementManagement == null) {
			try {
				agreementManagement = ServiceLocator
						.lookupService(IAgreementManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return agreementManagement;
	}

	/**
	 * create the button to popup the view intermediaries the party assists
	 * popup
	 * 
	 * @return
	 */
	private Link createViewAssistantButton(String buttonID) {
		Link but = new AjaxFallbackLink(buttonID) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				assistantPopup.show(target);
			}
		};
		return but;
	}

	/**
	 * Create the modal window
	 * 
	 * @param id
	 * @return
	 */
	private ModalWindow createAssistantWindow(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("PA to the following Intermediaries");

		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;

			public Page createPage() {
				ContextDTO dto = SRSAuthWebSession.get().getContextDTO();
				ContextPartyDTO selectedParty = null;
				if (dto != null) {
					selectedParty = dto.getPartyContextDTO();
				}
				return new HierarchyRolePage(window, selectedParty);
			}
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(420);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		return window;
	}

}
