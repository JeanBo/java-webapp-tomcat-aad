package za.co.liberty.web.pages.maintainagreement;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
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

public class AgreementTypePanel extends BasePanel {

	private static final AgreementKindType DEFAULT_AGREEMENT_KIND_TYPE = AgreementKindType.AGENT;
	
	private WebMarkupContainer agreementTypeContainer;
	private RepeatingView agreementTypePanel;
	private MaintainAgreementPageModel pageModel;
	private transient IAgreementGUIController guiController;
	private transient Logger logger = Logger.getLogger(AgreementTypePanel.class);

	private boolean divisionSelected = false;

	private SRSDropDownChoice dropdown;

	private GUIFieldPanel typePanel;

	public AgreementTypePanel(String id, MaintainAgreementPageModel pageModel, EditStateType editState) {
		super(id, editState);
		this.pageModel=pageModel;
		add(new AgreementTypeForm());
	}
	
	@Override
	protected void onBeforeRender() {
		initPageModel(this.pageModel);
		setVisibility();
		super.onBeforeRender();
	}

	private void setVisibility() {
		if (dropdown!=null) {
			dropdown.setEnabled(pageModel.isAgreementKindChangeEnabled());
		}
	}

	/**
	 * If no agreement kind has been selected yet, 
	 * select the default agreement kind type
	 * @param pageModel the page model
	 */
	private void initPageModel(MaintainAgreementPageModel pageModel) {
		if (pageModel==null) {
			return;
		}
		if (pageModel.getMaintainAgreementDTO().getAgreementKindType()==null) {
			pageModel.getMaintainAgreementDTO().setAgreementKindType(
					DEFAULT_AGREEMENT_KIND_TYPE);
			updateAgreementKind(DEFAULT_AGREEMENT_KIND_TYPE);
		} else {
			divisionSelected = true;
		}
	}
	
	private class AgreementTypeForm extends Form {

		public AgreementTypeForm() {
			super("agreementTypeForm");
			/**
			 * Components
			 */
			add(getAgreementTypeContainer());
		}

		
	}

	public WebMarkupContainer getAgreementTypeContainer() {
		if (agreementTypeContainer == null) {
			agreementTypeContainer = new WebMarkupContainer("agreementTypeContainer");
			/**
			 * Components
			 */
			agreementTypeContainer.add(getAgreementTypePanel());
		}
		return agreementTypeContainer;
	}

	private RepeatingView getAgreementTypePanel() {
		if (agreementTypePanel == null) {
			agreementTypePanel = new RepeatingView("agreementTypePanel");
			/**
			 * Components
			 */
			agreementTypePanel.add(getAgreementTypeDropdown());
		}
		return agreementTypePanel;
	}
	
	/**
	 * Update both the current and previous agreement DTO objects
	 * inside the page model to reflect the selected changes
	 * @param kind
	 */
	private void updateAgreementKind(AgreementKindType kind) {
		if (this.pageModel==null) {
			return;
		}
	}

	private GUIFieldPanel getAgreementTypeDropdown() {
		if (typePanel==null) {
			IChoiceRenderer renderer = new SRSAbstractChoiceRenderer<Object>() {

				public Object getDisplayValue(Object object) {
					if (object==null) {
						return "";
					}
					return ""+(((AgreementKindType)object).getDescription());
				}
	
				public String getIdValue(Object object, int index) {
					return ""+index;
				}
				
			};
			List<AgreementKindType> choices = Arrays.asList(AgreementKindType.values());
			dropdown = new SRSDropDownChoice("value",new PropertyModel(
					pageModel.getMaintainAgreementDTO(),"agreementKindType"),choices,renderer,null);
			dropdown.add(new AjaxFormComponentUpdatingBehavior("change") {
				@Override
				protected void onUpdate(AjaxRequestTarget arg0) {
					AgreementKindType kind  = 
						AgreementTypePanel.this.pageModel
						.getMaintainAgreementDTO().getAgreementKindType();
					updateAgreementKind(kind);
				}
			});
			typePanel = createGUIFieldPanel("Agreement Type", "Agreement Type", "kind", HelperPanel.getInstance("panel", dropdown));
		}
		return typePanel;
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
				logger.fatal("Could not lookup AgreementGUIController",e);
				throw new CommunicationException("Could not lookup AgreementGUIController",e);
			}
		}
		return guiController;
	}

}
