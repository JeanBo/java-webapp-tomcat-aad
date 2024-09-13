package za.co.liberty.web.pages.transactions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2Choice;

import za.co.liberty.business.guicontrollers.transactions.IRequestTransactionGuiController;
import za.co.liberty.common.domain.CurrencyAmount;
import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.dto.transaction.VEDTransactionDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.EarningAndDeductionParentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.MaintenanceBasePage.SelectionForm;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;
import za.co.liberty.web.wicket.markup.html.form.CurrencyAmountTextField;
import za.co.liberty.web.wicket.markup.html.form.SRSDateField;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Transaction Panel for VED's
 * 
 * @author jzb0608 2023-03-27
 *
 */
@SuppressWarnings("unused")
public class TransactionVariableEarningDeductionPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final int PAGE_SIZE = 10;
	
	private static final Logger logger = Logger.getLogger(TransactionVariableEarningDeductionPanel.class);
	
	private RequestTransactionModel pageModel = null;
	
	private SRSDateField  requestedDateField = null;

	private SRSTextField  descriptionTextField = null;
	private SRSTextField  amountTextField = null;

	private DropDownChoice transactionTypeDropDownField = null;
	
	private Select2Choice  earningTypeAutocompleteField = null;

	protected SelectionForm selectionForm;
	public EditStateType pageEditState;
	private ModalWindow createSearchWindow;

	private boolean initialised = false;
	
	private List<TypeDTO> currentEarningTypeList = new ArrayList<TypeDTO>();
	
	private Map<Long, List<TypeDTO>> earningMaps = new HashMap<Long, List<TypeDTO>>();
	private Map<Long,TypeDTO> fullEarningMap = new HashMap<Long, TypeDTO>();
	
	private transient IRequestTransactionGuiController guiController;
	
	@SuppressWarnings("unchecked")
	public TransactionVariableEarningDeductionPanel(String id, EditStateType editState, 
			RequestTransactionModel pageModel, MaintenanceBasePage parentPage) {
		super(id, editState,parentPage);
		this.pageModel = pageModel;
		initialise();
	}
	
	
	private void initialise(){
		// Initialise the lists of values required
		if (!initialised) {
			initEarningTypeList();
			initialised = true;
		}
		if (getEditState().isAdd() && getSelectedObject() != null) {
			// Set the requestedDate and startDate and default to today
			getSelectedObject().setRequestedDate(new java.sql.Date( 
					DateUtil.getInstance().getTodayDatePart().getTime()));
			getSelectedObject().setStartDate(getSelectedObject().getRequestedDate());
		}
		if (getEditState().isViewOnly() || getEditState().isModify()) {
			// Set the superType/parentType for auth or view
			if (getSelectedObject().getSuperType()==null && getSelectedObject().getEarningType()!=null) {
				TypeDTO t = fullEarningMap.get(getSelectedObject().getEarningType());
				if (t!=null) {
					getSelectedObject().setSuperType(t.getParentType());
				} else {
					warn("Earning type description for type " + getSelectedObject().getEarningType()  + " can not be found.");
				}
			} else {
				if (getEditState().isModify()) {
					warn("Earning type not defined and must be provided for reject.");
				}
			}
		}
		add(requestedDateField = createRequestedDateField("requestedDate"));
		add(transactionTypeDropDownField=createTransactionSupertypeDropDownField("transactionType"));
		add(earningTypeAutocompleteField = createEarningTypeAutoCompleteField("earningType"));
		add(descriptionTextField = createDescriptionField("description"));
		add(amountTextField = createAmountField("amount"));

	}
	
	/*
	 * Initialise the earning type list
	 */
	private void initEarningTypeList() {
		long start = System.currentTimeMillis();
		List<TypeDTO> earningList = getGuiController().getVEDEarningTypesForType(EarningAndDeductionParentType.EARNING);
		List<TypeDTO> fringeList = getGuiController().getVEDEarningTypesForType(EarningAndDeductionParentType.FRINGE_BENEFIT);
		List<TypeDTO> deductionList = getGuiController().getVEDEarningTypesForType(EarningAndDeductionParentType.DEDUCTION);
		
		earningMaps.put(405L,  earningList);
		earningMaps.put(406L,  fringeList);
		earningMaps.put(407L,  deductionList);
		
		List<TypeDTO> fullEarningTypeList = new ArrayList<TypeDTO>();
		fullEarningTypeList.addAll(earningList);
		fullEarningTypeList.addAll(fringeList);
		fullEarningTypeList.addAll(deductionList);
		
		for (TypeDTO t : fullEarningTypeList) {
			fullEarningMap.put(t.getOid(), t);
		}
		
		currentEarningTypeList.clear();
		logger.info("Init Earning type list took " + (System.currentTimeMillis() - start) + " milliseconds");
		
	}
	
	


	/**
	 * Internal method to retrieve the selected object of the correct type.
	 * 
	 * @return
	 */
	protected VEDTransactionDTO getSelectedObject() {
		return (VEDTransactionDTO) pageModel.getSelectedItem();
	}
	
	/**
	 * Create reqeuestedDateField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSDateField createRequestedDateField(String id) {
		
		IModel<Date> fieldModel = new IModel<Date>() {
			private static final long serialVersionUID = 1L;

			public Date getObject() {
				return getSelectedObject().getRequestedDate();
			}

			public void setObject(Date arg0) {
				java.sql.Date dte = null;
				if (arg0!=null) {
					if (arg0 instanceof Date) {
						dte = new java.sql.Date(((Date)arg0).getTime());
					} else {
						dte = (java.sql.Date) arg0;
					}
				}
				// Set both fields (requested and startDate)
				//   They have similar behaviours
				getSelectedObject().setRequestedDate(dte);
				getSelectedObject().setStartDate(dte);
							
			}

			public void detach() {
			}
		};
		
		SRSDateField text = new SRSDateField(id, fieldModel);
		
		AjaxFormComponentUpdatingBehavior a = new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		};
		
		text.setOutputMarkupId(true);
		if(getEditState().isViewOnly()){
			text.setEnabled(false);
		} else {
			text.addNewDatePicker();
		}
		return text;
	}

	/**
	 * Create transaction type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DropDownChoice createTransactionSupertypeDropDownField(String id) {
		// Model for getting and setting the value in the selected object
		//   from the list of objects shown.
		IModel<EarningAndDeductionParentType> model = new IModel<EarningAndDeductionParentType>() {
			private static final long serialVersionUID = 1L;
			
			public EarningAndDeductionParentType getObject() {
				if (getSelectedObject()!=null && getSelectedObject().getSuperType()!=null) {
					return EarningAndDeductionParentType.getEarningAndDeductionTypeForKind(getSelectedObject().getSuperType());
				}
				return null;
			}
			public void setObject(EarningAndDeductionParentType type) {
				getSelectedObject().setSuperType((type==null)?null:type.getType());
				System.out.println("#JB - setSuperType=" + getSelectedObject().getSuperType());
			}
			public void detach() {	
			}
		};
		List<EarningAndDeductionParentType> validList = Arrays.asList(EarningAndDeductionParentType.values());
		
		DropDownChoice field = new DropDownChoice(id, model, validList);
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (getSelectedObject().getSuperType()==null) {
					getSelectedObject().setEarningType(null);
					currentEarningTypeList.clear();
					earningTypeAutocompleteField.clearInput();
					earningTypeAutocompleteField.setEnabled(false);
					
				} else {
					currentEarningTypeList.clear();
					currentEarningTypeList.addAll(earningMaps.get(getSelectedObject().getSuperType()));
					earningTypeAutocompleteField.setEnabled(true);
				}
				target.add(earningTypeAutocompleteField);
			}		
		});
		// Must be shown when editing rejects (MODIFY)
		if(getEditState().isViewOnly()){
			field.setEnabled(false);
		}
		return field;
	}
	
	/**
	 * Create transaction type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Select2Choice createEarningTypeAutoCompleteField(String id) {
		// Model for getting and setting the value in the selected object
		//   from the list of objects shown.
		IModel<TypeDTO> model = new IModel<TypeDTO>() {
			private static final long serialVersionUID = 1L;
			
			public TypeDTO getObject() {
				if (getSelectedObject()!=null && getSelectedObject().getEarningType()!=null) {
					return fullEarningMap.get(getSelectedObject().getEarningType());
				}
				return null;
			}
			public void setObject(TypeDTO arg0) {
				getSelectedObject().setEarningType((arg0==null)?null:arg0.getOid());
			}
			public void detach() {	
			}
		};
		
		Select2Choice<TypeDTO> field = new Select2Choice<>(id, model,
				new ChoiceProvider<TypeDTO>() {

					@Override
					public String getDisplayValue(TypeDTO choice) {
						return choice.getDescription() + " (" + choice.getName()
							.replace("FringeBenefit", "")
							.replace("VariableEarning","")
							.replace("VariableDeduction","") + ")";
					}

					@Override
					public String getIdValue(TypeDTO choice) {
						return choice.getName();
					}

					@Override
					public void query(String term, int page, org.wicketstuff.select2.Response<TypeDTO> response) {
						response.addAll(queryMatches(term, page, PAGE_SIZE));
						response.setHasMore(response.size() == PAGE_SIZE);
					}

					@Override
					public Collection<TypeDTO> toChoices(Collection<String> ids) {
						ArrayList<TypeDTO> list = new ArrayList<>();
						for (String id : ids) {
							for (TypeDTO t : currentEarningTypeList) {
								if (t.getName().equalsIgnoreCase(id)) {
									list.add(t);
									break;
								}
							}
						}
						return list;
					}
		});
		
		field.getSettings()
			.setPlaceholder("Select Type")
			.setAllowClear(true);
		field.setOutputMarkupId(true);
		field.setEnabled(false);
		
		return field;
	}
	
	/**
	 * Queries {@code pageSize} worth of countries from the {@link Country} enum, starting with
	 * {@code page * pageSize} offset. Countries are matched on their {@code displayName} containing
	 * {@code term}
	 *
	 * @param term
	 *            search term
	 * @param page
	 *            starting page
	 * @param pageSize
	 *            items per page
	 * @return list of matches
	 */
	private List<TypeDTO> queryMatches(String term, int page, int pageSize) {
		List<TypeDTO> result = new ArrayList<>();
		term = term == null ? "" : term.toUpperCase();
		final int offset = page * pageSize;

		int matched = 0;
		for (TypeDTO type : currentEarningTypeList) {
			if (result.size() == pageSize)	{
				break;
			}

			if (type.getDescription().toUpperCase().contains(term))	{
				matched++;
				if (matched > offset)	{
					result.add(type);
				}
			}
		}
		return result;
	}
	
	/**
	 * Render the results shown on the Earning Type field
	 * 
	 * @return
	 */
	final protected IAutoCompleteRenderer getAutoCompleteRenderer() {
		return new AbstractAutoCompleteRenderer<TypeDTO>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected String getTextValue(TypeDTO object) {
				if (object == null) {
					return "";
				}
				return object.getDescription();
			}

			@Override
			protected void renderChoice(TypeDTO object, Response response,
					String criteria) {
				response.write(object.getDescription());
			}
		};
	}
	
	/**
	 * Create createfullAmountField
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createAmountField(String id) {
		
		IModel<CurrencyAmount> fieldModel = new IModel<CurrencyAmount>() {
			private static final long serialVersionUID = 1L;

			public CurrencyAmount getObject() {
				return getSelectedObject().getAmount();
			}

			public void setObject(CurrencyAmount arg0) {
				getSelectedObject().setAmount(arg0);
			}

			public void detach() {
			}
		};
		final CurrencyAmountTextField tempSRSTextField = new CurrencyAmountTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
//		tempSRSTextField.setRequired(true);
		tempSRSTextField.add(new AjaxFormComponentUpdatingBehavior("change") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
				// Force format to be updated
				arg0.add(tempSRSTextField);
			}
			
			
		});
		tempSRSTextField.setOutputMarkupId(true);
		
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}

	
	/**
	 * Create description
	 * 
	 * @param id
	 * @return
	 */
	protected SRSTextField createDescriptionField(String id) {
		
		IModel<String> fieldModel = new IModel<String>() {
			private static final long serialVersionUID = 1L;

			public String getObject() {
				return getSelectedObject().getDescription();
			}

			public void setObject(String arg0) {
				getSelectedObject().setDescription(arg0.toUpperCase());
			}

			public void detach() {
			}
		};
		SRSTextField tempSRSTextField = new SRSTextField(id,fieldModel);
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		if(getEditState()== EditStateType.MODIFY){
			tempSRSTextField.setEnabled(false);
		}
		return tempSRSTextField;
	}
	
	public String getPageName() {
		return "Variable Earnings and Deductions";
	}
	
	/**
	 * Return the gui controller for this page
	 * @return
	 */
	protected IRequestTransactionGuiController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator.lookupService(IRequestTransactionGuiController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " IRequestTransactionGuiController can not be lookedup:"
						+ namingErr.getMessage());
				CommunicationException comm = new CommunicationException("IRequestTransactionGuiController can not be looked up!");
				throw new CommunicationException(comm);
			} 
		}
		return guiController;
	}
	
	
}
