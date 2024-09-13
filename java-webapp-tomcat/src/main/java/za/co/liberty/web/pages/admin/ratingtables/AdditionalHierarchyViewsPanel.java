package za.co.liberty.web.pages.admin.ratingtables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.ratingtable.IMIRatingTableGUIController;
import za.co.liberty.dto.rating.AdditionalHierarchyViewDTO;
import za.co.liberty.dto.rating.BranchTypeDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
import za.co.liberty.interfaces.agreements.RoleKindType;
import za.co.liberty.interfaces.rating.IGuiRatingRow;
import za.co.liberty.interfaces.salesCategory.SalesCategoryType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.admin.models.RatingTablePageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;

/**
 * Main filter panel for Rating table admin. Shows a filter section, table and a
 * selection panel.
 * 
 * @author rxs1408
 * 
 */
public class AdditionalHierarchyViewsPanel extends BasePanel {

	private static final long serialVersionUID = 9032746185200994039L;
	private static final List<String> ROLEKIND_FLAG = Arrays.asList(new String[] {
			"Include", "Exclude" });

	private static final String EDIT_PANEL_ID = "editPanel";
	private transient IMIRatingTableGUIController guiController;

	private SRSTextField oidLabel = null;
	private DropDownChoice agreementKindChoice = null;
	private DropDownChoice branchTypeChoice = null;
	private DropDownChoice salesCategoryChoice = null;
	private DropDownChoice createRoleKindField = null;
	private DropDownChoice excludeRoleKindField = null;
	private DropDownChoice lowerLevelField = null;
	private DropDownChoice levelField = null;
	private SRSTextField descriptionTextField = null;
	String levelValue = "";
	boolean onChange = false;

	RatingTablePageModel pageModel;

	public AdditionalHierarchyViewsPanel(String id, EditStateType editState,
			RatingTablePageModel pageModel, Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel = pageModel;
		initialise();
		setAllLists();
	}

	private void initialise() {
		add(oidLabel = createOIDField("oid"));
		add(agreementKindChoice = createAgreementKindField("agreementKind"));
		add(branchTypeChoice = createBranchTypeField("branchType"));
		add(descriptionTextField = createDescriptionTextField("description"));
		add(salesCategoryChoice = createSalesCategoryField("salesCategory"));
		add(createRoleKindField = createRoleKindField("roleKind"));
		add(excludeRoleKindField = createExcludeRoleKindField("excludedRoleKind"));
		add(lowerLevelField = createLowerLevelField("lowerLevel"));
		add(levelField = createLevelField("level"));
		if (getEditState() != EditStateType.ADD) {
			IGuiRatingRow beforeImage = (IGuiRatingRow) SerializationUtils
					.clone((AdditionalHierarchyViewDTO) pageModel
							.getSelectionRow());
			pageModel.setGuiRatingRowBeforeImage(beforeImage);
		}
	}

	private void setAllLists() {
		if (pageModel.getAllAgreementKinds().isEmpty()) {
			pageModel.getAllAgreementKinds().addAll(
					getGUIController().getAgreementKinds());
		}
		if (pageModel.getAllBranchTypes().isEmpty()) {
			pageModel.getAllBranchTypes().addAll(
					getGUIController().geBranchTypes());
			pageModel.getAllOrganisationExternalType().addAll(
					getGUIController().getOrganisationTypes());
		}
		if (pageModel.getAllSalesCategories().isEmpty()) {
			pageModel.getAllSalesCategories().addAll(
					getGUIController().getSalesCatgories());
		}
		if (pageModel.getAllRoleKinds().isEmpty()) {
			pageModel.getAllRoleKinds().addAll(
					getGUIController().getRoleKinds());
		}
		if (pageModel.getLevel().isEmpty()) {
			// Just to avoid another dbase call using the same dto to create
			// level list
			pageModel.getLevel().addAll(getGUIController().getLevels());
		}
		onChange = true;
		pageModel.getLowerLevels().clear();
		pageModel.getLowerLevels().addAll(
				getGUIController().getLowerLevels(levelValue, onChange));
	}

	private DropDownChoice createAgreementKindField(String id) {
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(
				pageModel.getSelectionRow(), "agreementKind"),
				pageModel.getAllAgreementKinds(), new SRSAbstractChoiceRenderer<Object>() {
	
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null
								: ((AgreementKindType) value).getDescription();
					}

					public String getIdValue(Object arg0, int arg1) {
						return (arg0 == null) ? null : ""
								+ ((AgreementKindType) arg0).getKind();
					}
				});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if (getEditState() == EditStateType.MODIFY) {
			field.setEnabled(true);
		}

		return field;
	}

	private DropDownChoice createRoleKindField(String id) {
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(
				pageModel.getSelectionRow(), "roleKind"),
				pageModel.getAllRoleKinds(), new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null : ((RoleKindType) value)
								.getDescription();
					}

					public String getIdValue(Object arg0, int arg1) {
						return (arg0 == null) ? null : ""
								+ ((RoleKindType) arg0).getKind();
					}

				});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if (getEditState() == EditStateType.MODIFY) {
			field.setEnabled(true);
		}

		return field;
	}

	private DropDownChoice createBranchTypeField(String id) {
		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return (((AdditionalHierarchyViewDTO) pageModel
						.getSelectionRow()).getBranchType() != null ? ((AdditionalHierarchyViewDTO) pageModel
						.getSelectionRow()).getBranchType()
						: new BranchTypeDTO());
			}

			public void setObject(Object arg0) {
				((AdditionalHierarchyViewDTO) pageModel.getSelectionRow())
						.setBranchType((BranchTypeDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice field = new DropDownChoice(id, model,
				pageModel.getAllBranchTypes(), new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null : ((BranchTypeDTO) value)
								.getName();
					}

					public String getIdValue(Object arg0, int arg1) {
						return ((BranchTypeDTO) arg0).getValue() + "";
					}
				});

		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if (getEditState() == EditStateType.MODIFY) {
			field.setEnabled(true);
		}

		return field;
	}

	private DropDownChoice createSalesCategoryField(String id) {
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(
				pageModel.getSelectionRow(), "salesCategory"),
				pageModel.getAllSalesCategories(), new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null
								: ((SalesCategoryType) value).getTypeName();
					}

					public String getIdValue(Object arg0, int arg1) {
						if (arg0 instanceof SalesCategoryType) {
							return (arg0 == null) ? null : ""
									+ ((SalesCategoryType) arg0).getTypeID();
						}
						return (arg0 == null) ? null : "" + ((String) arg0);
					}

				});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if (getEditState() == EditStateType.MODIFY) {
			field.setEnabled(true);
		}

		return field;
	}

	private DropDownChoice createLevelField(String id) {
		final DropDownChoice field = new DropDownChoice(id, new PropertyModel(
				pageModel.getSelectionRow(), "level"), pageModel.getLevel(),
				new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null : (((String) value));
					}

					public String getIdValue(Object arg0, int arg1) {
						return (arg0 == null) ? null : "" + ((String) arg0);
					}
				});
		field.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget requestTarget) {
				String levelValue = ((String) field.getValue());
				onChange = true;
				pageModel.getLowerLevels().clear();
				pageModel.getLowerLevels()
						.addAll(getGUIController().getLowerLevels(levelValue,
								onChange));
				requestTarget.add(lowerLevelField);
			}
		});
		field.setOutputMarkupId(true);
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if (getEditState() == EditStateType.MODIFY) {
			field.setEnabled(true);
		}
		levelValue = field.getValue();
		return field;
	}

	private DropDownChoice createLowerLevelField(String id) {
		DropDownChoice field = new DropDownChoice(id, new PropertyModel(
				pageModel.getSelectionRow(), "usesLowerLevel"),
				pageModel.getLowerLevels(), new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? null : ((String) value);
					}

					public String getIdValue(Object arg0, int arg1) {
						if (arg0 instanceof AdditionalHierarchyViewDTO) {
							return (arg0 == null) ? null : "" + ((String) arg0);
						}
						return (arg0 == null) ? null : "" + ((String) arg0);
					}

				});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if (getEditState() == EditStateType.MODIFY) {
			field.setEnabled(true);
		}
		field.setOutputMarkupId(true);
		return field;
	}

	public void setvalue(String val) {

	}

	private SRSTextField createDescriptionTextField(String id) {
		SRSTextField tempSRSTextField = new SRSTextField(id, new PropertyModel(
				pageModel.getSelectionRow(), "description"));
		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		tempSRSTextField.setRequired(true);
		return tempSRSTextField;
	}

	private SRSTextField createOIDField(String id) {
		SRSTextField tempSRSTextField = new SRSTextField(id, new PropertyModel(
				pageModel.getSelectionRow(), "oid"));
		tempSRSTextField.setEnabled(false);
		return tempSRSTextField;
	}

	private DropDownChoice createExcludeRoleKindField(String id) {

		DropDownChoice field = new DropDownChoice(id, new PropertyModel(
				pageModel.getSelectionRow(), "excludedRoleKind"), ROLEKIND_FLAG,
				new SRSAbstractChoiceRenderer<Object>() {
					private static final long serialVersionUID = -4367276358153378234L;

					public Object getDisplayValue(Object value) {
						return (value == null) ? "Include" : ((String) value);
					}

					public String getIdValue(Object arg0, int arg1) {
						return (arg0 == null) ? null : "" + ((String) arg0);
					}

				});
		field.setNullValid(true);
		field.setEnabled(!getEditState().isViewOnly());
		if (getEditState() == EditStateType.MODIFY) {
			field.setEnabled(true);
		}
		field.setOutputMarkupId(true);
		return field;
	}

	private List getFlagChoices() {
		List choices = new ArrayList();
		choices.add("Include");
		choices.add("Not");
		return choices;
	}

	protected IMIRatingTableGUIController getGUIController() {
		if (guiController == null) {
			try {
				guiController = ServiceLocator
						.lookupService(IMIRatingTableGUIController.class);
			} catch (NamingException namingErr) {
				CommunicationException comm = new CommunicationException(
						"IMIRatingTableGUIController can not be looked up!");
				throw new CommunicationException(comm);
			}
		}
		return guiController;
	}
}
