package za.co.liberty.web.pages.maintainagreement.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.AgreementContextDTO;
import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO.PayToType;
import za.co.liberty.dto.spec.PropertySpecDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.data.enums.fields.AgreementGUIFieldRequestMapping;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

public class AgreementTemplate extends ContextDrivenViewTemplate<AgreementGUIField,AgreementDTO> {
	
	private static final long serialVersionUID = -5451421744583297306L;
	
	@SuppressWarnings("unchecked")
	private List<PropertySpecDTO> propertySpecs;
	
	private transient IAgreementGUIController guiController;
	
	private transient Logger logger;

	private List<AgreementGUIField> disabledFields;

	private AgreementContextDTO initialContext;
	
	/**
	 * Applicable agreement kinds to the PAYS TO ORGANISATION field
	 * (MXM1904) Added [AgreementKindType.AGENT]for FAVP Agent Transition To IFA (MXM1904)
	 */
	AgreementKindType[] payToOrgAgreementKindsContext = new AgreementKindType[] {
			AgreementKindType.FRANCHISE,
			AgreementKindType.BROKER,
			AgreementKindType.STOP_ORDER_BROKER,
			AgreementKindType.AGENT,
//			AgreementKindType.LIBERTY_ATWORK_COMMISSIONONLY  //TODO jzb0608 - remove this
			
	};

	/**
	 * Applicable agreement kinds to FRANCHISE POOL DETAILS 
	 */
	private AgreementKindType[] franchisePoolAgreementKindsContext = new AgreementKindType[] {
			AgreementKindType.FRANCHISE
	};
	
	/**
	 * Agreement Kinds that have independant FAIS license details
	 */
	private AgreementKindType[] agreementsWithIndependantFaisLicense = new AgreementKindType[] {
			AgreementKindType.BROKER,
			AgreementKindType.STOP_ORDER_BROKER,
			AgreementKindType.BROKER_BRANCH_FRANCHISE
	};
	
	/**
	 * Agreement Kinds that for which My Banking Number is to be displayed
	 */
	private AgreementKindType[] agreementsWithMyBankingNumber = new AgreementKindType[] {
			AgreementKindType.AGENT,
			AgreementKindType.FRANCHISE,
			AgreementKindType.FRANCHISE_MANAGER,
			AgreementKindType.BROKER_BRANCH_FRANCHISE
	};
		
	/**
	 * Agreement Kinds for which 'Corporate Commission Addendum Signed' & 'Hold Corporate Commsion' indicators 
	 * are to be displayed
	 * Added for LCB Accreditation -12/12/12-Pks
	 */
	private AgreementKindType[] agreementsWithCorpAddendumSigned = new AgreementKindType[] {
			AgreementKindType.AGENT,
			AgreementKindType.BROKER,
			AgreementKindType.FRANCHISE,
			AgreementKindType.DIRECT,
			AgreementKindType.BROKER_CONSULTANT
	};
	
	/**
	 * Agreement Kinds for which 'Sole Proprietor'is to be displayed   
	 * 
	 * Added for LCB Accreditation -12/12/12-Pks
	 */
	private AgreementKindType[] agreementsWithSoleProp = new AgreementKindType[] {
			AgreementKindType.BROKER,
			AgreementKindType.STOP_ORDER_BROKER			
	};
	
	/**
	 * Agreement Kinds that for which Production Club is displayed
	 */
	private AgreementKindType[] agreementsWithProductionClub = new AgreementKindType[] {
			AgreementKindType.AGENT			
	};
	
	
	/**
	 * Applicable agreement kinds to the Include In Manpower Reporting field
	 * (RXS1408) Added [AgreementKindTypes]for FR2 - RAVISH SEHGAL 08/07/14
	 */
	AgreementKindType[] includeInManpowerReporting = new AgreementKindType[] {
			AgreementKindType.FRANCHISE,
			AgreementKindType.AGENT,
			AgreementKindType.BROKER,
			AgreementKindType.BROKER_CONSULTANT,
			AgreementKindType.BROKER_BRANCH_FRANCHISE,
			AgreementKindType.FRANCHISE_MANAGER,
			AgreementKindType.DIRECT,
			AgreementKindType.STOP_ORDER_BROKER,
			AgreementKindType.LIBERTY_ATWORK_COMMISSIONONLY,
			AgreementKindType.LIBERTY_ATWORK_SALARIEDCONTRACTOR,
			AgreementKindType.LIBERTY_ATWORK_SALARIEDEMP
	};
	
	/**
	 * Applicable agreement kinds to the Manpower field - because Direct Agreement kind Shows the field twice
	 * (RXS1408) Added [AgreementKindTypes]for FR7 - RAVISH SEHGAL 06/08/14
	 */
	AgreementKindType[] manpower = new AgreementKindType[] {
			AgreementKindType.FRANCHISE,
			AgreementKindType.AGENT,
			// SSM2707 ADDED for FR15 Tenure SWETA MENON Begin
			/*Manpower set only for Agency and Franchise agreements*/
//			AgreementKindType.BROKER,
//			AgreementKindType.BROKER_CONSULTANT,
//			AgreementKindType.BROKER_BRANCH_FRANCHISE,
//			AgreementKindType.FRANCHISE_MANAGER,
//			AgreementKindType.STOP_ORDER_BROKER,
//			AgreementKindType.LIBERTY_ATWORK_COMMISSIONONLY,
//			AgreementKindType.LIBERTY_ATWORK_SALARIEDCONTRACTOR,
//			AgreementKindType.LIBERTY_ATWORK_SALARIEDEMP
			// SSM2707 ADDED for FR15 Tenure SWETA MENON End
	};
	
	// SSM2707 ADDED for FR15 Tenure SWETA MENON Begin
	/**
	 * Applicable agreement kinds to the Liberty Tenure field
	 * 
	 */
	AgreementKindType[] libertyTenure = new AgreementKindType[] {
			AgreementKindType.FRANCHISE,
			AgreementKindType.AGENT,
			AgreementKindType.BROKER,
			AgreementKindType.STOP_ORDER_BROKER,
	};
	// SSM2707 ADDED for FR15 Tenure SWETA MENON End
	
	/**
	 * Applicable agreement kinds to the Hierarchy FR3.6 EmployeeNumber field - RAVISH SEHGAL 06/08/14
	 */
	AgreementKindType[] employeeNumber = new AgreementKindType[] {						
			AgreementKindType.BROKER_CONSULTANT
	};
	
	
	/**
	 * Applicable agreement kinds to the Hierarchy FR2.8.3 LBF Remuneration field - MOHAMMED LORGAT 03/12/2014
	 */
	AgreementKindType[] lbfRemuneration = new AgreementKindType[] {						
			AgreementKindType.BROKER_BRANCH_FRANCHISE
	};
	
	/**
	 * Construct a new template with the initial context applicable to the template
	 * @param initialContext
	 */
	public AgreementTemplate(AgreementDTO initialContext) {
		AgreementDTO clonedAgreementDTO = (AgreementDTO) 
			(initialContext!=null?SerializationUtils.clone(initialContext):null);
		this.initialContext = new AgreementContextDTO(clonedAgreementDTO);
	}
	
	public AgreementTemplate(AgreementContextDTO initialContext) {
		super();
		this.initialContext = initialContext;
	}

	public void setOutstandingRequests(List<RequestKindType> outstandingRequests) {
		setDisabledFields(outstandingRequests);
	}
	
	/**
	 * Get the logger for this class, instantiating if it is null
	 * @return
	 */
	private Logger getLogger() {
		if (logger == null) {
			 logger = Logger.getLogger(AgreementTemplate.class);
		}
		return logger;
	}
	
	/**
	 * Sets which fields will be disabled due to oustanding authorisations 
	 * @param outstandingRequests
	 */
	private void setDisabledFields(List<RequestKindType> outstandingRequests) {
		disabledFields = new ArrayList<AgreementGUIField>();
		if (outstandingRequests==null) {
			return;
		}
		for (RequestKindType request : outstandingRequests) {
			AgreementGUIFieldRequestMapping mapping = 
				AgreementGUIFieldRequestMapping.getRequestMappingForRequestKind(
						request.getRequestKind());
			if (mapping==null) {
				/**
				 * No mapping available for the request kind
				 */
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("No field mapping available for request kind : "+request.getRequestKind());
				}
				continue;
			}
			disabledFields.addAll(Arrays.asList(mapping.getFields()));
		}
	}

	public void setDisabledFields(ISessionUserProfile userProfile) {
//		if ()
	}
	
	@Override
	public boolean isAddable(AgreementGUIField field, AgreementDTO context) {
		switch (field) {//FIELDS THAT CAN NOT BE ADDED
			case PAY_TO_EFFECTIVE_FROM: //THIS IS SYNCHRONISED TO AGREEMENT START
			case DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM: //THIS IS SYNCHRONISED TO AGREEMENT START
			case END_DATE:
			case STATUS:
			case STATUS_DATE: //THIS IS SYNCHRONISED TO AGREEMENT START
			case EARLY_DEBITS_INDICATOR:
			case EARLY_DEBITS_REASON:
			case EARLY_DEBITS_START_DATE:
			case FRANCHISE_POOL_ACCOUNT_EFFECTIVE_DATE: //AUTO VALUE
			case FRANCHISE_POOL_INTEREST_RATE_EFFECTIVE_DATE: //AUTO VALUE
			case FRANCHISE_POOL_TRANSFER_EFFECTIVE_DATE: //AUTO VALUE
			case PREAUTH_CATEGORY:
			case MANPOWER: //AUTOMATICALLY UPDATED
			case COST_CENTER: //DERIVED FROM HAS HOME ROLE HIERARCHY NODE
			case CONSULTANT_CODE: //MAINTAINED THROUGH AGREEMENT HIERARCHY
			case CALCULATED_PRODUCTION_CLUB_STATUS: //MAINTAINED DURING BATCH PROCESS
			case PRODUCTION_CLUB_STATUS://removing this field form the gui
			case LIBERTY_TENURE: //AUTOMATICALLY UPDATED // SSM2707 ADDED for FR15 Tenure SWETA MENON
				return false;
			/**
			 * FAIS License 
			 */
			case FAIS_LICENSE_EFFECTIVE_DATE:
			case FAIS_LICENSE_NUMBER:
				return isAgreementPartOfAgreementKindTypes(agreementsWithIndependantFaisLicense);
			case FAIS_LICENSE_CATEGORY: //MAINTAINABLE FOR REGULATORY PURPOSES 
			case FAIS_HEALTH_BENEFIT:
			case FAIS_PARTIC_COLL_INVESTMENTS:
			case FAIS_PENSION_BENFIT:
			case FAIS_RETAIL_PENSION_BENFIT:
				return true;
			case MY_BANKING_NUM : isAgreementPartOfAgreementKindTypes(agreementsWithMyBankingNumber);
			
//			Added for LCB Accreditation-pks
			case CORP_ADDENDUM_SIGNED : return isAgreementPartOfAgreementKindTypes(agreementsWithCorpAddendumSigned);
			case HOLD_CORP_COMMISSION : return false; //Should not be visible on Add Agreement page
			
			case STOPSTATEMENTDISTRIBUTION:		
				return true;
			
			case SCRIPTEDADVISORCHECK:		
				return true;

			/**
			 * Default is to allow all
			 */
			default: return true;
		}
	}

	

	@Override
	public boolean isModifiable(AgreementGUIField field, AgreementDTO context) {
//		/**
//		 * If the agreement in context has current status of InProgress, then this
//		 * agreement is not maintainable
//		 */
//		if (isInitialContextAgreementInProgress()) {
//			return false;
//		}
		/**
		 * Disable fields related to requests outstanding authorisation
		 */
		if (disabledFields!=null && disabledFields.contains(field)) {
			return false;
		}
		switch (field) {//fields that cannot be modified
			case PAY_TO_EFFECTIVE_FROM: //THIS IS UPDATED IN THE BEHAVIOUR LOGIC TO CURRENT DATE ON TEMPLATE CHANGE
			case EARLY_DEBITS_START_DATE:
			case EARLY_DEBITS_INDICATOR://this is not maintainable
			case END_DATE://this is related to QC7864 - terminate will move to a seperate process
			
			case MANPOWER: //AUTOMATICALLY UPDATED
			case LIBERTY_TENURE: //AUTOMATICALLY UPDATED // SSM2707 ADDED for FR15 Tenure SWETA MENON
			case PREAUTH_CATEGORY: //CAN NEVER BE MAINTAINED
			case START_DATE: //CAN NEVER BE MAINTAINED
			case FRANCHISE_POOL_ACCOUNT_EFFECTIVE_DATE: //CAN NEVER BE MAINTAINED
			case FRANCHISE_POOL_INTEREST_RATE_EFFECTIVE_DATE: //CAN NEVER BE MAINTAINED
			case FRANCHISE_POOL_TRANSFER_EFFECTIVE_DATE: //CAN NEVER BE MAINTAINED
			case STATUS_DATE:
			case COST_CENTER: //DERIVED FROM HAS HOME ROLE HIERARCHY NODE	
			case CONSULTANT_CODE: //MAINTAINED THROUGH AGREEMENT HIERARCHY
			case CALCULATED_PRODUCTION_CLUB_STATUS: //MAINTAINED DURING BATCH PROCESS
			case PRODUCTION_CLUB_STATUS://removing this field form the gui
				return false;
			/**
			 * DISTRIBUTION TEMPLATE EFFECTIVE FROM is only modifiable when
			 * changing to a new value so that the effective from is not changed
			 * on the currently selected template
			 */
			case DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM: return context!=null && context.getDistributionDetails()!=null &&
				context.getDistributionDetails().isDateChangeAllowed();
			/**
			 * FAIS License 
			 */
			case FAIS_LICENSE_EFFECTIVE_DATE:
			case FAIS_LICENSE_NUMBER: 
				return isAgreementPartOfAgreementKindTypes(agreementsWithIndependantFaisLicense);
			case FAIS_LICENSE_CATEGORY: //MAINTAINABLE FOR REGULATORY PURPOSES 
			case FAIS_HEALTH_BENEFIT:
			case FAIS_PARTIC_COLL_INVESTMENTS:
			case FAIS_PENSION_BENFIT:
			case FAIS_RETAIL_PENSION_BENFIT:
				return true;
				
			case MY_BANKING_NUM :return isAgreementPartOfAgreementKindTypes(agreementsWithMyBankingNumber);
			
			case STOPSTATEMENTDISTRIBUTION:
				return true;
			case SCRIPTEDADVISORCHECK:
				return true;
//			Added for LCB Accreditation-pks
			case CORP_ADDENDUM_SIGNED :
			case HOLD_CORP_COMMISSION :return isAgreementPartOfAgreementKindTypes(agreementsWithCorpAddendumSigned);
			
			case SOLE_PROPRIETOR: return isAgreementPartOfAgreementKindTypes(agreementsWithSoleProp);
			
			//RXS 1408 ADDED for FR2 INCLUDE In Manpower Reporting - RAVISH SEHGAL
			case INCLUDE_IN_MANPOWER_REPORTING:
				return isAgreementPartOfAgreementKindTypes(includeInManpowerReporting) ;
				
			case SALES_CATEGORY:
				return true;
			case LBF_REMUNERATION_CATEGORY:
				return isAgreementPartOfAgreementKindTypes(lbfRemuneration) ;
			case LBF_HOME_ADD_BUTTON:
				return isAgreementPartOfAgreementKindTypes(lbfRemuneration) ;
			// SSM2707 Added for Hierarchy FR3.5 PrimaryAgreement SWETA MENON
			case PRIMARY_AGREEMENT:
				return !isInitialContextAgreementInProgress();
			
			/**
			 * The default is to allow all
			 */
			default: return true;
		}
	}
	
	@Override
	public boolean isModifiableForTerminate(AgreementGUIField field, AgreementDTO context) {
		switch (field) {
			case STATUS:
			case STATUS_REASON:
			case END_DATE:
			case DISTRIBUTION_TEMPLATE:
			case FIXED_DEDUCTION_GRID:
			case FIXED_EARNING_BUTTONS:
				return true;
			/**
			 * DISTRIBUTION TEMPLATE EFFECTIVE FROM is only modifiable when
			 * changing to a new value so that the effective from is not changed
			 * on the currently selected template
			 */
			case DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM: return context!=null && context.getDistributionDetails()!=null &&
				context.getDistributionDetails().isDateChangeAllowed();
				
		}
		return false;
	}

	private boolean isInitialContextAgreementInProgress() {
		return initialContext!=null && initialContext.getCurrentStatus()!=null &&
				initialContext.getCurrentStatus().getName()!=null &&
				initialContext.getCurrentStatus().getName().equalsIgnoreCase("InProgress");
	}

	@Override
	public boolean isViewable(AgreementGUIField field, EditStateType editState, AgreementDTO context) {
//		if (context==null || context.getSpecificationId()==0) {
//			return false;
//		}
		if (editState.equals(EditStateType.TERMINATE)) {
			/**
			 * Don't show the following fields in the terminate state
			 */
			switch (field) {
				case EARLY_DEBITS_INDICATOR:
				case EARLY_DEBITS_REASON:
				case EARLY_DEBITS_START_DATE:
				case PAY_TO_CHOICE:
				case PAY_TO_EFFECTIVE_FROM:
				case PAY_TO_ORGANISATION:
					return false;
			}
		}
		
		switch (field) {
			case PAY_TO_CHOICE:
			case PAY_TO_EFFECTIVE_FROM:
//			case CONSULTANT_CODE:
			case END_DATE:
			case START_DATE:
//			case STATUS:
//			case STATUS_DATE:
//			case STATUS_REASON: 
			case DONOTCALCINTEREST:			
				return true;
			/**
			 * Don't show consultant code for add agreement
			 */
			case CONSULTANT_CODE: return !editState.equals(EditStateType.ADD);	
			case PRODUCTION_CLUB_STATUS: return false;//removing this field form the gui
			/**
			 * PAYS TO ORGANISATION IS ONLY ALLOWED FOR
			 * FRANCHISE
			 * BROKER
			 * STOP ORDER BROKER
			 */
			case PAY_TO_ORGANISATION: 
				return isAgreementPartOfAgreementKindTypes(
						payToOrgAgreementKindsContext);
			/**
			 * FRANCHISE POOL ACCOUNT DETAILS IS ONLY ALLOWED FOR
			 * FRANCHISE
			 */
			case FRANCHISE_POOL_ACCOUNT_PANEL:
				return isAgreementPartOfAgreementKindTypes(franchisePoolAgreementKindsContext);
			/**
			 * Franchise details only viewable if create pool account selected 
			 */
			case FRANCHISE_POOL_ACCOUNT_EFFECTIVE_DATE:
			case FRANCHISE_POOL_INTEREST_RATE:
			case FRANCHISE_POOL_INTEREST_RATE_EFFECTIVE_DATE:
			case FRANCHISE_POOL_TRANSFER_EFFECTIVE_DATE:
			case FRANCHISE_POOL_TRANSFER_PERCENTAGE:
				return isCreateFranchisePoolDetailsEnabled(context);
			/**
			 * Create pool account field only visible in modify/view if the 
			 * initial value is false, to allow account to be created, but to 
			 * ensure that gui does not allow account to be deselected. 
			 */
			case FRANCHISE_CREATE_POOL_ACCOUNT:
				return editState.equals(EditStateType.ADD) ||
				!initialContext.isCreateFranchisePoolAccountEnabled();
				
			case FIXED_DEDUCTION_CONGLOMERATE_WARNING:
			case FIXED_EARNING_CONGLOMERATE_WARNING:
				return isPaysToOrg(context);
				
			/**
			 * Don't show Cost Center for add agreement.Display only for Maintain
			 */
			case COST_CENTER: return !editState.equals(EditStateType.ADD);
			
			case MY_BANKING_NUM : isAgreementPartOfAgreementKindTypes(agreementsWithMyBankingNumber);
			
			//Added for LCB Accreditation-pks
			case CORP_ADDENDUM_SIGNED :
			case HOLD_CORP_COMMISSION : return isAgreementPartOfAgreementKindTypes(agreementsWithCorpAddendumSigned);
			
			case SOLE_PROPRIETOR: return isAgreementPartOfAgreementKindTypes(agreementsWithSoleProp);
			
			//RXS 1408 ADDED for FR7 Tenure - Manpower for All Agreement Kinds - RAVISH SEHGAL
			case MANPOWER:
				return isAgreementPartOfAgreementKindTypes(manpower) ;
			// SSM2707 ADDED for FR15 Tenure SWETA MENON Begin
			case LIBERTY_TENURE:
			    return isAgreementPartOfAgreementKindTypes(libertyTenure) ;
			// SSM2707 ADDED for FR15 Tenure SWETA MENON End
			//RXS 1408 ADDED for FR2 INCLUDE In Manpower Reporting - RAVISH SEHGAL
			case INCLUDE_IN_MANPOWER_REPORTING:
				return isAgreementPartOfAgreementKindTypes(includeInManpowerReporting) ;

			//RXS 1408 ADDED for Hierarchy FR3.2 SALES_CATEGORY - RAVISH SEHGAL - valid for all agreement types
			case SALES_CATEGORY:
				return true;
			//MZL 2611 ADDED for FR2.8.2 LBF Remuneration Category - MOHAMMED LORGAT
			case LBF_REMUNERATION_CATEGORY:
				return isAgreementPartOfAgreementKindTypes(lbfRemuneration);
				
				//MZL 2611 ADDED for FR2.8.2 LBF Remuneration Category - MOHAMMED LORGAT
			case LBF_HOME_ADD_BUTTON:
				return isAgreementPartOfAgreementKindTypes(lbfRemuneration);
				
			//RXS 1408 ADDED for Hierarchy FR3.6 EMPLOYEE NUMBER - RAVISH SEHGAL
			case EMPLOYEE_NUMBER:
			
			return isAgreementPartOfAgreementKindTypes(employeeNumber) ;
			//SSM2707 ADDED for Hierarchy FR3.5 Primary Agreement - SWETA MENON - valid for all agreement types
			case PRIMARY_AGREEMENT:
				return true;
				
			case INTO_POOL_RATE_OVERRIDE:
				return true;
		}
		/**
		 * Only show fields for valid properties for the specified agreement, based on the 
		 * agreement specifications. 
		 */
		if (field.getPropertyKind()!=null) {
			for (PropertySpecDTO dto : getPropertySpecs(initialContext)) {
					if (field.getPropertyKind()==dto.getPropertyKind()) {
						return true;
					}
			}
		}
		return false;
	}

	private boolean isPaysToOrg(AgreementDTO context) {
		return context!=null && context.getPaymentDetails()!=null &&
			context.getPaymentDetails().getPayto()!=null &&
			context.getPaymentDetails().getPayto().equals(PayToType.ORGANISATION);
	}

	private boolean isCreateFranchisePoolDetailsEnabled(AgreementDTO context) {
		boolean ret = context!=null && context.getFranchisePoolAccount()!=null
					&& context.getFranchisePoolAccount().isCreatePoolAccount();
		return ret;
	}

	@Override
	public boolean isRequired(AgreementGUIField field, AgreementDTO context) {
		switch (field) {
			case PAY_TO_CHOICE:
			case PAY_TO_EFFECTIVE_FROM:
			case STATUS:
			case STATUS_REASON:
			case BROKER_BRANCH_FRANCHISE_GROUP:
			case CONSULTANT_CODE:
//			case COST_CENTER: //DERIVED FROM HAS HOME ROLE HIERARCHY NODE
			case DEDICATED_SBFC_CONSULTANT_TYPE:
			case PRIMARY_COMPANY_CONTRACTED_TO:
			case SEGMENT:
			case FITPROPSEGMENT:
			case END_DATE: //IF CAPTURE IS ALLOWED, THEN CAPTURE IS REQUIRED (TERMINATE PROCESS)
			case START_DATE:
			case SUPPORT_TYPE: 
			case DISTRIBUTION_TEMPLATE:
			case DISTRIBUTION_TEMPLATE_EFFECTIVE_FROM: 
			case FAIS_LICENSE_CATEGORY:
			case FAIS_LICENSE_EFFECTIVE_DATE:
			case FAIS_LICENSE_NUMBER:
			case EFFECTIVE_DT_CUSTOM:
			case EMPLOYEE_NUMBER:
			case SALES_CATEGORY:
				return true;
			//case LBF_REMUNERATION_CATEGORY:
				
			default: return false;
		}
	}
	
	/**
	 * Get the allowed property kinds for the specified agreement
	 * @param context
	 * @return
	 * @throws CommunicationException
	 */
	@SuppressWarnings("unchecked")
	private List<PropertySpecDTO> getPropertySpecs(AgreementContextDTO context) throws CommunicationException {
		if (propertySpecs==null && context!=null && context.getSpecificationId()!=0) {
			if (guiController==null) {
				try {
					guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
				} catch (NamingException e) {
					throw new CommunicationException("Could not lookup a resource trying to get allowed property kinds",e);
				}
			}
			propertySpecs = guiController.getPropertySpecsForAgreementSpecification(context.getSpecificationId());
		}
		if (propertySpecs==null) {
			return Collections.EMPTY_LIST;
		}
		return propertySpecs;
	}
	
	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a transient variable.
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				getLogger().fatal("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}
	
	/**
	 * Test if an agreement is part of a specified list of agreement kinds
	 * 
	 * This test must be applied to the initial context of the view template
	 */
	boolean isAgreementPartOfAgreementKindTypes(AgreementKindType[] agreementKinds) {
		AgreementKindType current = getInitialContextAgreementKindType();
		if (current==null || agreementKinds==null) {
			return false;
		}
		for (AgreementKindType kind : agreementKinds) {
			if (kind==current) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the initial context agreement kind type
	 */
	AgreementKindType getInitialContextAgreementKindType() {
		AgreementKindType ret = null;
		if (initialContext!=null) {
			for (AgreementKindType type : AgreementKindType.values()) {
				if (type.getKind()==initialContext.getKind()) {
					ret = type;
					break;
				}
			}
		}
		return ret;
	}

}
