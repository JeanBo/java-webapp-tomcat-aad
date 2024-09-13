package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.dto.hierarchy.MIReportingDTO;
import za.co.liberty.dto.hierarchy.MiHierarchyNodeAddressClassificationDTO;
import za.co.liberty.dto.hierarchy.MiHierarchyNodeCharacteristicsDTO;
import za.co.liberty.dto.party.contactdetail.ContactDetailDTO;
import za.co.liberty.dto.party.contactdetail.PhysicalAddressDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.persistence.rating.entity.MiHierarchyNodeAddressClassificationEntity;
import za.co.liberty.web.data.enums.ComponentType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.hierarchy.model.MaintainHierarchyPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.panels.HelperPanel;

public class MIReportingDetailsPanel extends BasePanel {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULTCHOICE = "Select one";

	private static final List<String> TYPES = Arrays
			.asList(new String[] { "Metro", "Outlying" });

	
	private MiRegionChoicesType miRegionChoicesType;

	private GUIFieldPanel miRegionTypePanel;
	private GUIFieldPanel reportFriendlyNamePanel;
	private GUIFieldPanel miDivisionPanel;
	private GUIFieldPanel branchTypePanel;
	private GUIFieldPanel channelGroupPanel;
	private GUIFieldPanel branchCategoryPanel;
	private GUIFieldPanel superDivisionPanel;
	private GUIFieldPanel townPanel;
	private GUIFieldPanel mainTownPanel;
	private GUIFieldPanel libertyAreaPanel;

	private WebMarkupContainer miDetailsContainer;

	private MIReportingDTO miReportingDTO;

	private boolean existingMiDetailsRequest;

	private transient IHierarchyGUIController hierarchyGUIController;

	private MaintainHierarchyPageModel maintainHierarchyPageModel;

	public MIReportingDetailsPanel(String tabPanelId,
			MaintainHierarchyPageModel maintainHierarchyPageModel,
			EditStateType editState, FeedbackPanel feedBackPanel, Page page) {
		super(tabPanelId, editState, page);
		this.maintainHierarchyPageModel = maintainHierarchyPageModel;

		this.maintainHierarchyPageModel
				.setOrganisationTypeList(getHierarchyGUIController()
						.getOrganisationTypes());
		this.maintainHierarchyPageModel
				.setBranchCategories(getHierarchyGUIController()
						.getBranchCategories());

		add(new FeedbackPanel("messages"));
		this.miReportingDTO = maintainHierarchyPageModel.getHierarchyNodeDTO()
				.getMiReportingDTO();
		if (this.maintainHierarchyPageModel.getHierarchyNodeDTO()
				.getContactPreferences() != null && this.maintainHierarchyPageModel.getHierarchyNodeDTO()
				.getContactPreferences().getContactPreferences().size() != 0) {
			List<ContactDetailDTO> contactDetails = this.maintainHierarchyPageModel
					.getHierarchyNodeDTO().getContactPreferences()
					.getContactPreferences().get(0).getContactDetails();
			ContactDetailDTO vContactDetailDTO = null;
			for (ContactDetailDTO contactDetailDTO : contactDetails) {
				if (contactDetailDTO.getType() == contactDetailDTO.getType().PHYSICAL_ADDRESS) {
					vContactDetailDTO = contactDetailDTO;
					break;
				}
			}

			List<MiHierarchyNodeAddressClassificationEntity> miHierNodeAddressClassifications = new ArrayList<MiHierarchyNodeAddressClassificationEntity>();
			if(vContactDetailDTO != null && ((PhysicalAddressDTO) vContactDetailDTO).getSuburb() != null){
				 miHierNodeAddressClassifications = getHierarchyGUIController().findByMIHierNodeAddressClassBySuburb(((PhysicalAddressDTO) vContactDetailDTO).getSuburb());

			}
			if (this.miReportingDTO
					.getMiHierarchyNodeAddressClassificationDTO() == null) {
				MiHierarchyNodeAddressClassificationDTO vMiHierarchyNodeAddressClassificationDTO = new MiHierarchyNodeAddressClassificationDTO();
				vMiHierarchyNodeAddressClassificationDTO.setMetro(new Short((short) 0));
				vMiHierarchyNodeAddressClassificationDTO.setOutlying(new Short((short)0));
				this.miReportingDTO
						.setMiHierarchyNodeAddressClassificationDTO(vMiHierarchyNodeAddressClassificationDTO);
			}

			if (miHierNodeAddressClassifications.size() != 0) {
				this.miReportingDTO
						.setMiHierarchyNodeAddressClassificationDTO(getHierarchyGUIController().buildMiHierarchyNodeAddressClassificationDTO(miHierNodeAddressClassifications));
				this.miReportingDTO.setReportFriendlyName(this.maintainHierarchyPageModel.getHierarchyNodeDTO().getKnownAsName());
			}
		}


		if (this.miReportingDTO == null) {
			this.miReportingDTO = new MIReportingDTO();
			this.miReportingDTO
					.setMiHierarchyNodeCharacteristicsDTO(new MiHierarchyNodeCharacteristicsDTO());
			this.miReportingDTO.getMiHierarchyNodeCharacteristicsDTO()
					.setMiDivision("");
			this.maintainHierarchyPageModel.getHierarchyNodeDTO().setMiReportingDTO(this.miReportingDTO);
		}

		RadioChoice<String> miType = new RadioChoice<String>("mitype",
				new PropertyModel<String>(this, "selected"), TYPES);

		Form<?> form = new Form<Void>("miReportingDetailsForm") {
			// miReportingDetailsForm
			@Override
			protected void onSubmit() {

			}
		};

		add(form);

		// miRegionTypePanel = createMiRegionType();
		reportFriendlyNamePanel = createReportFriendlyName();
		miDivisionPanel = createMiDivision();
		branchTypePanel = createBranchType();
		channelGroupPanel = createChannelGroup();
		branchCategoryPanel = createBranchCategory();
		superDivisionPanel = createSuperDivision();
		townPanel = createTown();
		mainTownPanel = createMainTown();
		libertyAreaPanel = createLibertyArea();

		form.add(getMiDetailsContainer());
	}


	private GUIFieldPanel createSuperDivision() {
		superDivisionPanel = createGUIFieldPanel(
				"Super Division",
				"Super Division",
				"Super Division",
				isView(getEditStateTypesMIDetails()) ? createPageField(
						this.miReportingDTO, "Super Division", "panel",
						"miHierarchyNodeCharacteristicsDTO.superDivision",
						ComponentType.TEXTFIELD, true, true,
						getEditStateTypesMIDetails()) : createPageField(
						this.miReportingDTO, "Super Division", "panel",
						"miHierarchyNodeCharacteristicsDTO.superDivision",
						ComponentType.TEXTFIELD, true, true,
						getViewOnlyStates()), 3);
		superDivisionPanel.setOutputMarkupId(true);
		superDivisionPanel.setOutputMarkupPlaceholderTag(true);

		return superDivisionPanel;
	}

	private GUIFieldPanel createTown() {
		townPanel = createGUIFieldPanel(
				"Town",
				"Town",
				"Town",
				isView(getEditStateTypesMIDetails()) ? createPageField(
						this.miReportingDTO, "Town", "panel",
						"miHierarchyNodeAddressClassificationDTO.town",
						ComponentType.TEXTFIELD, true, true,
						getEditStateTypesMIDetails()) : createPageField(
						this.miReportingDTO, "Town", "panel",
						"miHierarchyNodeAddressClassificationDTO.town",
						ComponentType.TEXTFIELD, true, true,
						getViewOnlyStates()), 3);
		townPanel.setOutputMarkupId(true);
		townPanel.setOutputMarkupPlaceholderTag(true);

		return townPanel;
	}

	private GUIFieldPanel createChannelGroup() {

		channelGroupPanel = createGUIFieldPanel(
				"Channel Group",
				"Channel Group",
				"Channel Group",
				isView(getEditStateTypesMIDetails()) ? createPageField(
						this.miReportingDTO, "Channel Group", "panel",
						"miHierarchyNodeCharacteristicsDTO.channelGroup",
						ComponentType.TEXTFIELD, true, true,
						getEditStateTypesMIDetails()) : createPageField(
						this.miReportingDTO, "Channel Group", "panel",
						"miHierarchyNodeCharacteristicsDTO.channelGroup",
						ComponentType.TEXTFIELD, true, true,
						getViewOnlyStates()), 3);
		channelGroupPanel.setOutputMarkupId(true);
		channelGroupPanel.setOutputMarkupPlaceholderTag(true);

		return channelGroupPanel;
	}

	private GUIFieldPanel createLibertyArea() {
		libertyAreaPanel = createGUIFieldPanel(
				"Liberty Area",
				"Liberty Area",
				"Liberty Area",
				isView(getEditStateTypesMIDetails()) ? createPageField(
						this.miReportingDTO, "Liberty Area", "panel",
						"miHierarchyNodeAddressClassificationDTO.libertyArea",
						ComponentType.TEXTFIELD, true, true,
						getEditStateTypesMIDetails()) : createPageField(
						this.miReportingDTO, "Liberty Area", "panel",
						"miHierarchyNodeAddressClassificationDTO.libertyArea",
						ComponentType.TEXTFIELD, true, true,
						getViewOnlyStates()), 3);
		libertyAreaPanel.setOutputMarkupId(true);
		libertyAreaPanel.setOutputMarkupPlaceholderTag(true);

		return libertyAreaPanel;
	}

	private GUIFieldPanel createMainTown() {

		mainTownPanel = createGUIFieldPanel(
				"Main Town",
				"Main Town",
				"Main Town",
				isView(getEditStateTypesMIDetails()) ? createPageField(
						this.miReportingDTO, "Main Town", "panel",
						"miHierarchyNodeAddressClassificationDTO.mainTown",
						ComponentType.TEXTFIELD, true, true,
						getEditStateTypesMIDetails()) : createPageField(
						this.miReportingDTO, "Main Town", "panel",
						"miHierarchyNodeAddressClassificationDTO.mainTown",
						ComponentType.TEXTFIELD, true, true,
						getViewOnlyStates()), 3);
		mainTownPanel.setOutputMarkupId(true);
		mainTownPanel.setOutputMarkupPlaceholderTag(true);

		return mainTownPanel;
	}

	private GUIFieldPanel createBranchCategory() {

		if (branchCategoryPanel == null) {
			branchCategoryPanel = createGUIFieldPanel(
					"Branch Category",
					"Branch Category",
					"Branch Category",
					createDropdownField(this.miReportingDTO, "Branch Category",
							"panel", "branchCategory",
							maintainHierarchyPageModel.getBranchCategories(),
							new ChoiceRenderer<Object>("name", "key"),
							DEFAULTCHOICE, true, true,
							getEditStateTypesMIDetails()), 5);
		}

		return branchCategoryPanel;
	}

	private GUIFieldPanel createBranchType() {

		if (branchTypePanel == null) {
			branchTypePanel = createGUIFieldPanel(
					"Branch Type",
					"Branch Type",
					"Branch Type",
					isView(getEditStateTypesMIDetails()) ? createDropdownField(this.maintainHierarchyPageModel
							.getHierarchyNodeDTO(), "Branch Type", "panel",
							"externalType", maintainHierarchyPageModel
									.getOrganisationTypeList(),
							new ChoiceRenderer<Object>("name", "key"),
							DEFAULTCHOICE, true, true,
							getEditStateTypesMIDetails()) : createDropdownField(this.maintainHierarchyPageModel
									.getHierarchyNodeDTO(), "Branch Type", "panel",
									"externalType", maintainHierarchyPageModel
											.getOrganisationTypeList(),
									new ChoiceRenderer<Object>("name", "key"),
									DEFAULTCHOICE, true, true,
									getViewOnlyStates()), 5);
					
		}

		return branchTypePanel;
	}

	private GUIFieldPanel createMiDivision() {

		miDivisionPanel = createGUIFieldPanel(
				"MI Division",
				"MI Division",
				"MI Division",
				isView(getEditStateTypesMIDetails()) ? createPageField(
						this.miReportingDTO, "MI Division", "panel",
						"miHierarchyNodeCharacteristicsDTO.miDivision",
						ComponentType.TEXTFIELD, true, true,
						getEditStateTypesMIDetails()) : createPageField(
						this.miReportingDTO, "MI Division", "panel",
						"miHierarchyNodeCharacteristicsDTO.miDivision",
						ComponentType.TEXTFIELD, true, true,
						getViewOnlyStates()), 3);
		miDivisionPanel.setOutputMarkupId(true);
		miDivisionPanel.setOutputMarkupPlaceholderTag(true);

		return miDivisionPanel;
	}

	private EditStateType[] getViewOnlyStates(){
		EditStateType[] viewOnlyStates = {EditStateType.VIEW} ;
		return viewOnlyStates;
		
	}
	
	private GUIFieldPanel createReportFriendlyName() {

		reportFriendlyNamePanel = createGUIFieldPanel(
				"report Friendly Name",
				getLabelForMandatory("Report Friendly Name"),
				"report Friendly Name",
				!isView(getEditStateTypesMIDetails()) ? createPageField(
						this.miReportingDTO, "Report Friendly Name", "panel",
						"reportFriendlyName", ComponentType.TEXTFIELD, true,
						true, getEditStateTypesMIDetails())
						: createPageField(
								this.miReportingDTO, "Report Friendly Name", "panel",
								"reportFriendlyName",
								ComponentType.TEXTFIELD, true, true,
								getEditStateTypesMIDetails()), 3);

		reportFriendlyNamePanel.setOutputMarkupId(true);
		reportFriendlyNamePanel.setOutputMarkupPlaceholderTag(true);

		return reportFriendlyNamePanel;
	}

	private GUIFieldPanel createMiRegionType() {

		IModel model = new IModel() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {

				if (MIReportingDetailsPanel.this.miReportingDTO
						.getMiHierarchyNodeAddressClassificationDTO() != null
						&& MIReportingDetailsPanel.this.miReportingDTO
								.getMiHierarchyNodeAddressClassificationDTO()
								.getMetro() == 0) {
					miRegionChoicesType = MiRegionChoicesType.OUTLYING;
				} else {
					miRegionChoicesType = MiRegionChoicesType.METRO;
				}
				return miRegionChoicesType;
			}

			public void setObject(Object arg0) {
				miRegionChoicesType = (MiRegionChoicesType) arg0;
			}

			public void detach() {
			}
		};

		List<MiRegionChoicesType> choices = Arrays.asList(
				MiRegionChoicesType.METRO, MiRegionChoicesType.OUTLYING);

		RadioChoice miRegionChoices = new RadioChoice("panel", model, choices) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("colspan", 2);
				tag.put("style",
						"padding-left: 50px; padding-right: 50px;padding-bottom:15px;");
			}
		};
		miRegionChoices.setRequired(true);
		miRegionChoices.setLabel(new Model("MI Region Choice"));
		miRegionChoices.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});

		miRegionChoices.setOutputMarkupId(true);
		miRegionChoices.setOutputMarkupPlaceholderTag(true);
		miRegionChoices.setEnabled(false);

		Label label = new Label("label", new Model(""));
		label.setVisible(false);

		miRegionTypePanel = new GUIFieldPanel("miRetionTypes", label,
				miRegionChoices);
		miRegionTypePanel.setOutputMarkupId(true);
		miRegionTypePanel.setOutputMarkupPlaceholderTag(true);

		return miRegionTypePanel;
	}

	private String getLabelForMandatory(String str) {
		StringBuilder builder = new StringBuilder(str);
		if ((EditStateType.MODIFY == getEditState() || EditStateType.ADD == getEditState())
				&& str.indexOf("*") == -1)
			return builder.append("*").toString();
		else
			return str;

	}

	@SuppressWarnings("serial")
	private HelperPanel createCustomLabel(Object propObject, String labelId,
			String componentID, String attribute, final IConverter converter) {

		Label viewLabel = new Label("value", new PropertyModel<Object>(
				propObject, attribute)) {
			@Override
			public IConverter getConverter(Class arg0) {
				return converter;
			}
		};
		return HelperPanel.getInstance(componentID, viewLabel, false);

	}

	public WebMarkupContainer getMiDetailsContainer() {
		if (miDetailsContainer == null) {
			miDetailsContainer = new WebMarkupContainer("miDetailsContainer");
			RepeatingView leftPanel = new RepeatingView("leftPanel");
			/**
			 * Left panel content
			 */
			leftPanel.add(createReportFriendlyName());
			leftPanel.add(createBranchType());
			leftPanel.add(createBranchCategory());
			leftPanel.add(createTown());
			leftPanel.add(createMainTown());
			leftPanel.add(createLibertyArea());
			leftPanel.add(createMiRegionType());

			miDetailsContainer.setOutputMarkupId(true);
			miDetailsContainer.add(leftPanel);

			RepeatingView rightPanel = new RepeatingView("rightPanel");
			rightPanel.add(createMiDivision());
			rightPanel.add(createChannelGroup());
			rightPanel.add(createSuperDivision());
			miDetailsContainer.add(rightPanel);

		}
		return miDetailsContainer;
	}

	private EditStateType[] getEditStateTypesMIDetails() {

		// will disable any modification if there are any requests pending auth
		if (existingMiDetailsRequest) {
			return new EditStateType[] {};
		}

		EditStateType[] editstateTypes = new EditStateType[] {
				EditStateType.MODIFY, EditStateType.ADD };

		return editstateTypes;
	}

	/**
	 * Get the HierarchyGUIController bean
	 * 
	 * @return
	 */
	private IHierarchyGUIController getHierarchyGUIController() {
		if (hierarchyGUIController == null) {
			try {
				hierarchyGUIController = ServiceLocator
						.lookupService(IHierarchyGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return hierarchyGUIController;
	}

	private void typeSelected(AjaxRequestTarget target) {
		// changeExternalTypeField(branchTypePanel);
	}
}
