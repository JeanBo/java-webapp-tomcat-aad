package za.co.liberty.web.pages.maintainagreement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.dto.agreement.properties.TemporalPropertyDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.persistence.agreement.entity.fastlane.TemporalPropertyFLO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.maintainagreement.model.AgreementDetailsPanelModel;
import za.co.liberty.web.pages.maintainagreement.popup.HistoryTablePopupPage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.convert.converters.SRSDateConverter;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

/**
 * A mini panel used in the GUI panel field on agreement details.
 * 
 * @author jzb0608
 *
 */
public class AgreementDetailsHasMedicalPanel extends Panel {

	private static final long serialVersionUID = 8291156270559784238L;

	AgreementDetailsPanelModel panelModel = null;
	EditStateType editState;
	CheckBox medicalCheckBox;
	/* can be a DropDownChoice or empty panel */
	WebMarkupContainer dateCombo;
	Label dateLabel;
	// Needed to facilitate add and end logic.
	boolean propertyUnselected = false;
	boolean propertyOriginalValue = false;
	Date originalStartDate = null;
	Date originalEndDate = null;
	Button historyButton;
	ModalWindow historyPopup;
	private transient IAgreementGUIController guiController;
	
	private String dateLabelValue = "";
	
	private static String DATE_FORMAT = SRSDateConverter.DATE_FORMAT_PATTERN;
	
	public AgreementDetailsHasMedicalPanel(String id, AgreementDetailsPanelModel panelModel, EditStateType editState) {
		super(id);
		this.panelModel = panelModel;
		this.editState = editState;
		TemporalPropertyDTO<Boolean> originalProperty = panelModel.getAgreement().getHasMedicalAidCredits();
		originalStartDate = originalProperty.getEffectiveFrom();
		originalEndDate = originalProperty.getEffectiveTo();
		if (originalProperty.getValue()==null) {
			// never been set
			propertyUnselected = true;
		} else if (originalProperty.getValue().equals(Boolean.TRUE)) { 
			propertyOriginalValue=true;
		}
		
		Logger.getLogger(this.getClass()).debug("Original Property = " + originalProperty.getValue() + "  - propOrigVal=" + propertyOriginalValue
				+ "   - propertyUnselected="+propertyUnselected
				+ "  effFrom = " + originalStartDate
				+ "  effTo = " + originalEndDate);
		
		if (!editState.isViewOnly()) {
			// Calculate the combo values.
			// TODO
		}
		add(createMedicalCheck("check"));
		add(dateLabel = createDateLabel("dateLabel"));
		add(dateCombo = createDateCombo("dateCombo"));
		add(historyButton = createHistoryButton("historyButton"));
		add(historyPopup = createHistoryPopup("historyPopup"));
	}	
	
	/**
	 * Create the history popup
	 * @param id
	 * @return
	 */
	private ModalWindow createHistoryPopup(String id) {
		final ModalWindow window = new ModalWindow(id);
		window.setTitle("Person Hierarchy Roles");		
		
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {					
				return new HistoryTablePopupPage(window) {

					@Override
					protected void updateGridDimensions(SRSDataGrid grid) {
				        grid.setAllowSelectMultiple(true);
				        grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
				       	grid.setRowsPerPage(10);
				        grid.setContentHeight(200, SizeUnit.PX);        	
					}

					/**
					 * Return the columns
					 * 
					 * @return
					 */
					protected List<IGridColumn> getViewableRolesColumns() {
						Vector<IGridColumn> cols = new Vector<IGridColumn>(2);
						cols.add(new SRSDataGridColumn<Object>("effectiveFrom",new Model("Effective From"),"effectiveFrom","effectiveFrom", EditStateType.VIEW).setInitialSize(130)); 
						cols.add(new SRSDataGridColumn<Object>("effectiveTo",new Model("Effective To"),"effectiveTo","effectiveTo", EditStateType.VIEW).setInitialSize(130));
//						cols.add(new SRSDataGridColumn<Object>("value",new Model("Value"),"value","value", EditStateType.VIEW).setInitialSize(80));
						
						SRSDataGridColumn c = new SRSDataGridColumn<TemporalPropertyFLO> ("value",new Model("Value"),"value","value", EditStateType.VIEW) {
							private static final long serialVersionUID = 1L;
							@Override
							public Panel newCellPanel(WebMarkupContainer parent, String componentId, 
									IModel rowModel, String objectProperty, EditStateType state, final TemporalPropertyFLO data) {
					
								CheckBox box = new CheckBox("value", new IModel<Boolean>() {
									public void detach() {	
									}
									public Boolean getObject() {
										return (data.getValue() != null && ((Number)data.getValue()).intValue()==1);
									}
									public void setObject(Boolean arg0) {	
									}
								});
								box.setEnabled(false);
								return HelperPanel.getInstance(componentId, box);
							}
						
						};
						cols.add(c.setInitialSize(70));
						return cols;
					}
					
					
					
					/**
					 * Get the table data
					 * @return
					 */
					protected List<Object> getTableDetails(){		
						List l = getGuiController().getHistoricLinkedMedicalDetails(panelModel.getAgreement().getId());
						return l;
					}
					
					@Override
					public String getPageName() {		
						return "Linked Medical Aid";
					}	
					
				};			
			}
		});		

		// Initialise window settings
		window.setMinimalHeight(320);
		window.setInitialHeight(320);
		window.setMinimalWidth(450);
		window.setInitialWidth(450);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);		
		return window;
	}

	/**
	 * Show the history button but only for viewing.
	 * 
	 * @param id
	 * @return
	 */
	private Button createHistoryButton(String id) {
		if (editState!=EditStateType.VIEW || panelModel.getAgreement()== null || panelModel.getAgreement().getId() == 0L) {
			return (Button) new Button(id).setVisible(false);
		}
		Button but = new AjaxButton(id) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				historyPopup.show(target);
			}
		};
		but.setOutputMarkupId(true);
		but.setOutputMarkupPlaceholderTag(true);	
		but.setEnabled(true);
		return but;
	}

	/** 
	 * Create the has medical aid checkbox
	 * 
	 * @param id
	 * @return
	 */
	public CheckBox createMedicalCheck(String id) {
		@SuppressWarnings("unchecked")
		CheckBox checkBox = new CheckBox(id,
				new PropertyModel(panelModel.getAgreement(),AgreementGUIField.HAS_MEDICAL_CREDITS.getFieldId()));
		checkBox.setOutputMarkupId(true);
		checkBox.setOutputMarkupPlaceholderTag(true);
		checkBox.add(new AjaxFormComponentUpdatingBehavior("click") {
			private static final long serialVersionUID = 1L;
			@Override
			
			protected void onUpdate(AjaxRequestTarget target) {
				if (!editState.isViewOnly()) {
					
					WebMarkupContainer tmp = createDateCombo("dateCombo");
					dateCombo.replaceWith(tmp);
					dateCombo = tmp;
					
					TemporalPropertyDTO<Boolean> originalProperty = panelModel.getAgreement().getHasMedicalAidCredits();
					if (isNoChangeInValue()) {
						// Value is set back to original						
						originalProperty.setEffectiveFrom(originalStartDate);
						originalProperty.setEffectiveTo(originalEndDate);
					} else {
						originalProperty.setEffectiveFrom(null);
						originalProperty.setEffectiveTo(null);
					}
//					panelModel.setHasMedicalLinkedChanged(!isNoChangeInValue());
					target.add(dateLabel);
					target.add(dateCombo);
					
					
				}
				Logger.getLogger(this.getClass()).debug("Value changed to " + panelModel.getAgreement().getHasMedicalAidCredits().getValue()
						+ "  effFrom = " + panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveFrom()
						+ "  effTo = " + panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveTo());
			}
		});			   
		checkBox.setEnabled(editState.equals(EditStateType.MODIFY) || editState.equals(EditStateType.ADD) || editState.equals(EditStateType.TERMINATE));
		return checkBox;
	}
	
	/** 
	 * Create date label
	 * 
	 * @param id
	 * @return
	 */
	public Label createDateLabel(String id) {
//		String value = "";
//		if (!editState.isViewOnly() && true ) {
//			value = (propertyOriginalValue) ? "End on" : "Start on";
//		}
		
		IModel<String> model = new IModel<String>() {
			private static final long serialVersionUID = 1L;
			Date date = null;	
//			DateUtil.getInstance().getTodayDatePart();
			
			public String getObject() {
								
//				/*
//				 * View  
//				 * 		- Unselected = ""
//				 * 		- True && startDate !=null  = "Started on dd/mm/yyyy"
//				 * 		- False ** endDate != null  = "Ended on dd/mm/yyyy"
//				 * 
//				 * Modify 
//				 * 	
//				 * 
//				 */
//			
				String resp = "";
				if (editState.isViewOnly()) {
					// Viewing
					if (!propertyUnselected) {
						SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
						if (propertyOriginalValue && panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveFrom()!=null) {
							resp = "Started on " + f.format(panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveFrom());
						} else if (!propertyOriginalValue && panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveTo()!=null) {
							resp = "Ended on " + f.format(panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveTo());
						}
					}
					
				} else {
					// Modifying
					if (!isNoChangeInValue()) {
						resp = (propertyOriginalValue) ? "End on " : "Start on";
					}
				}
				
				return resp;
			}
			
			public void setObject(String arg0) {
				// Not implemented
			}
			public void detach() {	
			}
		};
		
		Label lbl = new Label(id, model);
		lbl.setOutputMarkupId(true);
		return lbl;
	}
	
	/**
	 * Create the request kind type field
	 * 
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private WebMarkupContainer createDateCombo(String id) {
		
		if (editState.isViewOnly() || isNoChangeInValue()) {
			EmptyPanel p =new EmptyPanel(id);
			p.setOutputMarkupId(true);
			return p;
		}
		
		/**
		 * The combo selection fluctuates depending on the 
		 * original state of the property.
		 * if original true then end date is selected
		 * if original false then start date is selected 
		 */
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;
			Date date = null;	
//			DateUtil.getInstance().getTodayDatePart();
			
			public Object getObject() {
				return (propertyOriginalValue) ? panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveTo()
						: panelModel.getAgreement().getHasMedicalAidCredits().getEffectiveFrom();
//				return date;
			}
			public void setObject(Object arg0) {
				if (propertyOriginalValue) {
					panelModel.getAgreement().getHasMedicalAidCredits().setEffectiveTo(((Date) arg0));
				} else {
					panelModel.getAgreement().getHasMedicalAidCredits().setEffectiveFrom(((Date) arg0));
				}
			}
			public void detach() {	
			}
		};

		
		DropDownChoice field = new DropDownChoice("value", model, panelModel.getValidAgreementValues().getValidHasMedicalDates()) {
			@Override
			public boolean isEnabled() {
				return !editState.isViewOnly();
			}
		};
		
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}		
		});
		HelperPanel panel = HelperPanel.getInstance(id, field);
		panel.setOutputMarkupId(true);
		return panel;
	}
		
	/**
	 * True if there is no change
	 * 
	 * @return
	 */
	private boolean isNoChangeInValue() {
		Boolean newValue = panelModel.getAgreement().getHasMedicalAidCredits().getValue();
		if (newValue==null) {
			newValue = false;
		}
		return ((Boolean)propertyOriginalValue).equals(newValue);
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
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
				
			}
		}
		return guiController;
	}
}