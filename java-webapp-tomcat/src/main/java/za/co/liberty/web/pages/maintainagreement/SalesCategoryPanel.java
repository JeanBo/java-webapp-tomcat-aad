package za.co.liberty.web.pages.maintainagreement;


import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.maintainagreement.SalesCategoryDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;

public class SalesCategoryPanel extends BasePanel {

	
	private static final AgreementKindType DEFAULT_AGREEMENT_KIND_TYPE = AgreementKindType.AGENT;
	private WebMarkupContainer salesCategoryContainer;
	private RepeatingView agreementTypePanel;
	private RepeatingView divisionsPanel;
	private RepeatingView salesCategoriesPanel;

	private MaintainAgreementPageModel pageModel;
	private transient IAgreementGUIController guiController;
	private transient Logger logger = Logger.getLogger(SalesCategoryPanel.class);

	private SRSDropDownChoice dropdown;
	private SRSDropDownChoice divisionsDropDown;
	private SRSDropDownChoice salesCategoriesDropDown;

	private GUIFieldPanel typePanel;
	private GUIFieldPanel divisionPanel;
	private GUIFieldPanel salesCategoryPanel;
	
	
	public SalesCategoryPanel(String id, MaintainAgreementPageModel pageModel,
			EditStateType editState) {
		super(id, editState);
		this.pageModel = pageModel;
		add(new SalesCategoryForm());
	}

	@Override
	protected void onBeforeRender() {
		initPageModel(this.pageModel);
		setVisibility();
		setDivisionsList();
		super.onBeforeRender();
	}
	
	private void setVisibility() {
		if (dropdown != null && divisionsDropDown != null && salesCategoriesDropDown !=null) {
			dropdown.setEnabled(pageModel.isAgreementKindChangeEnabled());
			divisionsDropDown.setEnabled(pageModel.isAgreementKindChangeEnabled());
			salesCategoriesDropDown.setEnabled(pageModel.isAgreementKindChangeEnabled());
		}
	}

	
	private void setDivisionsList() {
		// LIST TO GET DIVISIONS
		if(pageModel.getDivisionsList().isEmpty()){
			pageModel.getDivisionsList().addAll(getGuiController().getDivisions());
		}
	}

	/**
	 * initialize page 
	 * @param pageModel
	 *            the page model
	 */
	private void initPageModel(MaintainAgreementPageModel pageModel) {
		if (pageModel == null) {
			return;
		}
		if (pageModel.getMaintainAgreementDTO().getAgreementKindType()==null) {
			pageModel.getMaintainAgreementDTO().setAgreementKindType(
					DEFAULT_AGREEMENT_KIND_TYPE);
			updateAgreementKind(DEFAULT_AGREEMENT_KIND_TYPE);
		} 
	}

	private class SalesCategoryForm extends Form {

		public SalesCategoryForm() {
			super("salesCategoryForm");
			/**
			 * Components
			 */
			add(getSalesCategoryContainer());
		}

	}

	public WebMarkupContainer getSalesCategoryContainer() {
		if (salesCategoryContainer == null) {
			salesCategoryContainer = new WebMarkupContainer("salesCategoryContainer");
			/**
			 * Components
			 */
			salesCategoryContainer.add(getDivisionsPanel());
			salesCategoryContainer.add(getSalesCategoriesPanel());
			salesCategoryContainer.add(getAgreementTypePanel());
		}
		return salesCategoryContainer;
	}
	
	
	// RXS 1408 ADDED for Hierarchy FR3.2 Sales Category - RAVISH SEHGAL
	private RepeatingView getDivisionsPanel() {
		if (divisionsPanel == null) {
			divisionsPanel = new RepeatingView("divisions");
			divisionsPanel.add(getDivisionsDropdown());
		}
		return divisionsPanel;
	}

	@SuppressWarnings({ "unchecked" })
	private GUIFieldPanel getDivisionsDropdown() {
		if (divisionPanel == null) {
			final IModel divModel = new IModel() {
				private static final long serialVersionUID = -4539669310139413695L;
				public Object getObject() {
					return pageModel.getMaintainAgreementDTO().getSalesCategoryDivisionsDTO(); 
				}

				public void setObject(Object arg0) {
					pageModel.getMaintainAgreementDTO().setSalesCategoryDivisionsDTO((SalesCategoryDTO) arg0);
				}
				
				/*public void setObject(Object arg0) {
					if(arg0 != null) {
						SalesCategoryDTO currSalesCategoryDTO = pageModel.getMaintainAgreementDTO().getSalesCategoryDTO();
						if(currSalesCategoryDTO != null) {
							currSalesCategoryDTO.setSrsDivision(((SalesCategoryDTO)arg0).getSrsDivision());
						} else {
							currSalesCategoryDTO = (SalesCategoryDTO)arg0;
						}
						pageModel.getMaintainAgreementDTO().setSalesCategoryDTO(currSalesCategoryDTO);
					}
				}*/

				public void detach() {
				}
			};
			divisionsDropDown = new SRSDropDownChoice("value", divModel,
					pageModel.getDivisionsList(), new SRSAbstractChoiceRenderer<Object>() {
						private static final long serialVersionUID = 3628568479684933449L;
						
						public Object getDisplayValue(Object value) {
							return (value == null) ? null : ((SalesCategoryDTO) value).getDivision();
						}
						
						public String getIdValue(Object arg0, int arg1) {
							return arg1 + "";
						}
					},"Select");
			divisionsDropDown.setOutputMarkupId(true);
			divisionsDropDown.setNullValid(true);
			divisionsDropDown.add(new AjaxFormComponentUpdatingBehavior(
					"change") {
				private static final long serialVersionUID = -6347930149876339644L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {									
					pageModel.getSalesCategoryList().clear();
					pageModel.getAgreementKindList().clear();
					if(pageModel.getMaintainAgreementDTO() != null && 
							pageModel.getMaintainAgreementDTO().getSalesCategoryDivisionsDTO() != null && 
							pageModel.getMaintainAgreementDTO().getSalesCategoryDivisionsDTO().getDivision() != null){
						String division = pageModel.getMaintainAgreementDTO().getSalesCategoryDivisionsDTO().getDivision();						
						pageModel.getSalesCategoryList().addAll(getGuiController().getSalesCategories(division));
					}
					target.add(salesCategoriesDropDown);
					target.add(dropdown);
				}
			});			
			divisionPanel = createGUIFieldPanel("Division", "Division","divisions",
					HelperPanel.getInstance("panel", divisionsDropDown));
		}
		return divisionPanel;
	}
	

	// RXS 1408 ADDED for Hierarchy FR3.2 Sales Category - RAVISH SEHGAL
	private RepeatingView getSalesCategoriesPanel() {
		if (salesCategoriesPanel == null) {
			salesCategoriesPanel = new RepeatingView("salesCategory");
			salesCategoriesPanel.add(getSalesCategoryDropdown());

		}
		return salesCategoriesPanel;
	}

	@SuppressWarnings("unchecked")
	private GUIFieldPanel getSalesCategoryDropdown() {
		if (salesCategoryPanel == null) {
			IModel model = new IModel() {
				private static final long serialVersionUID = -7036637395813602443L;

				public Object getObject() {
					return pageModel.getMaintainAgreementDTO().getSalesCategoryDTO();
				}
				
				public void setObject(Object arg0) {
					pageModel.getMaintainAgreementDTO().setSalesCategoryDTO((SalesCategoryDTO) arg0);
				}

				/*public void setObject(Object arg0) {
					if(arg0 != null) {
						SalesCategoryDTO currSalesCategoryDTO = pageModel.getMaintainAgreementDTO().getSalesCategoryDTO();
						currSalesCategoryDTO.setSalesCategory(((SalesCategoryDTO)arg0).getSalesCategory());
						pageModel.getMaintainAgreementDTO().setSalesCategoryDTO(currSalesCategoryDTO);
					}
				}*/

				public void detach() {
				}
			};
			salesCategoriesDropDown = new SRSDropDownChoice("value", model,
					pageModel.getSalesCategoryList(), new SRSAbstractChoiceRenderer<Object>() {
						private static final long serialVersionUID = 3880816096499874020L;

						public Object getDisplayValue(Object value) {
							return (value == null) ? null : ((SalesCategoryDTO) value).getSalesCategory();
						}

						public String getIdValue(Object arg0, int arg1) {
							return arg1 + "";
						}
					}, "Select");
			salesCategoriesDropDown.setOutputMarkupId(true);
			salesCategoriesDropDown.add(new AjaxFormComponentUpdatingBehavior(
					"change") {
						private static final long serialVersionUID = -1928503036773898138L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					pageModel.getAgreementKindList().clear();
					if(pageModel.getMaintainAgreementDTO() != null && 
							pageModel.getMaintainAgreementDTO().getSalesCategoryDTO() != null && 
							pageModel.getMaintainAgreementDTO().getSalesCategoryDTO().getSalesCategory() != null){
						String selectedSalesCategory = pageModel.getMaintainAgreementDTO().getSalesCategoryDTO().getSalesCategory();
						pageModel.getAgreementKindList().addAll(getGuiController().getKindTypeDescription(pageModel.getSalesCategoryList(), selectedSalesCategory));
					}
					target.add(dropdown);
				}
			});
			salesCategoryPanel = createGUIFieldPanel("Sales Category",
					"Sales Category", "salesCategory",
					HelperPanel.getInstance("panel", salesCategoriesDropDown));			
		}
		return salesCategoryPanel;
	}
	
	// RXS 1408 ADDED for Hierarchy FR3.2 Sales Category - RAVISH SEHGAL
		private RepeatingView getAgreementTypePanel() {
			if (agreementTypePanel == null) {
				agreementTypePanel = new RepeatingView("agreementType");
				agreementTypePanel.add(getAgreementTypeDropdown());
			}
			return agreementTypePanel;
		}
	    
		
	@SuppressWarnings("unchecked")
	private GUIFieldPanel getAgreementTypeDropdown() {
		if (typePanel == null) {
			IModel kindModel = new IModel() {
				private static final long serialVersionUID = 1L;

				public Object getObject() {
					return pageModel.getMaintainAgreementDTO().getSalesCategoryAgreementKindDTO();
				}
				public void setObject(Object arg0) {
					pageModel.getMaintainAgreementDTO().setSalesCategoryAgreementKindDTO((SalesCategoryDTO) arg0);
				}
				/*public void setObject(Object arg0) {
					if(arg0 != null) {
						SalesCategoryDTO currSalesCategoryDTO = pageModel.getMaintainAgreementDTO().getSalesCategoryDTO();
						currSalesCategoryDTO.setKindTypeDescription(((SalesCategoryDTO)arg0).getKindTypeDescription());
						pageModel.getMaintainAgreementDTO().setSalesCategoryDTO(currSalesCategoryDTO);
					}
				}*/
				public void detach() {
				}
			};
			dropdown = new SRSDropDownChoice("value", kindModel,
					pageModel.getAgreementKindList(), 
					new SRSAbstractChoiceRenderer<Object>() {
		 
						private static final long serialVersionUID = -2250292863913403179L;

						public Object getDisplayValue(Object value) {
							return (value == null) ? null : ((SalesCategoryDTO) value).getAgreementKind();
						}
						public String getIdValue(Object arg0, int arg1) {
							return arg1 + "";
						}
					},"Select");
			dropdown.setOutputMarkupId(true);
			dropdown.setRequired(true);
			dropdown.add(new AjaxFormComponentUpdatingBehavior(
					"change") {
						private static final long serialVersionUID = 390728926970403800L;
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					if(pageModel.getMaintainAgreementDTO() != null && 
							pageModel.getMaintainAgreementDTO().getSalesCategoryAgreementKindDTO() != null && 
							pageModel.getMaintainAgreementDTO().getSalesCategoryAgreementKindDTO().getAgreementKindId() != 0){
						int agreementKindId = pageModel.getMaintainAgreementDTO().getSalesCategoryAgreementKindDTO().getAgreementKindId();
						AgreementKindType kind = AgreementKindType.getAgreementKindType(agreementKindId);
						pageModel.getMaintainAgreementDTO().setAgreementKindType(kind);
						//AgreementKindType kind  = SalesCategoryPanel.this.pageModel.getMaintainAgreementDTO().getAgreementKindType();
						updateAgreementKind(kind);
					}
				}
			});
			typePanel = createGUIFieldPanel("Agreement Type",
					"Agreement Type", "agreementType",
					HelperPanel.getInstance("panel", dropdown));	
		}
		return typePanel;
	}
	
	/**
	 * Update both the current and previous agreement DTO objects
	 * inside the page model to reflect the selected changes
	 * @param kind
	 */
	
	private void updateAgreementKind(AgreementKindType kind){
		if (this.pageModel==null) {
			return;
		}
	}

	/**
	 * Load the AgreementGUIController dynamically if it is null as this is a
	 * transient variable.
	 * 
	 * @return {@link IAgreementGUIController}
	 */
	private IAgreementGUIController getGuiController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup AgreementGUIController", e);
				throw new CommunicationException(
						"Could not lookup AgreementGUIController", e);
			}
		}
		return guiController;
	}
}
