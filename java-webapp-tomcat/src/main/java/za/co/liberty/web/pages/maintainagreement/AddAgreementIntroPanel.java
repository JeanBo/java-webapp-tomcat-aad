package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.srs.type.SRSType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel.PartyTypeSelection;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.pages.party.PartyTypePanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

public class AddAgreementIntroPanel extends BasePanel {

	private MaintainAgreementPageModel pageModel;
	private MaintainPartyPageModel partyPageModel;
	private RepeatingView partyChoicePanel;
	private GUIFieldPanel partyChoices;
	private IChoiceRenderer partyTypeRenderer;
	private PartyTypePanel partyTypePanel;
	private WebMarkupContainer partyTypeContainer;
	private boolean initialised;

	public AddAgreementIntroPanel(String id, EditStateType editState, 
			MaintainAgreementPageModel pageModel,
			MaintainPartyPageModel partyPageModel) {
		super(id, editState);
		this.pageModel=pageModel;
		this.partyPageModel=partyPageModel;
	}
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			initialised=true;
			add(new IntroForm("introForm"));
			setVisibility(null);
		}
	}



	private class IntroForm extends Form {
		
		public IntroForm(String id) {
			super(id);
			add(getPartyChoicePanel());
			add(getPartyTypeContainer());   
			//add(new AgreementTypePanel("agreementTypePanel",pageModel,getEditState()));
			add(new SalesCategoryPanel("salesCategoryPanel",pageModel,getEditState()));
		}
	}
	
	private WebMarkupContainer getPartyTypeContainer() {
		if (partyTypeContainer == null) {
			partyTypeContainer = new WebMarkupContainer("partyTypeContainer");   
			partyTypeContainer.setOutputMarkupId(true);
			partyTypeContainer.setOutputMarkupPlaceholderTag(true);
			partyTypeContainer.add(getPartyTypePanel());
		}
		return partyTypeContainer;
	}
	
	private PartyTypePanel getPartyTypePanel() {
		if (partyTypePanel==null) {
			partyTypePanel = new PartyTypePanel("partyTypePanel",partyPageModel,getEditState());
		}
		return partyTypePanel;
	}

	public RepeatingView getPartyChoicePanel() {
		if (partyChoicePanel == null) {
			partyChoicePanel = new RepeatingView("leftPanel");
			partyChoicePanel.add(getPartyChoices());
		}
		return partyChoicePanel;
	}

	private GUIFieldPanel getPartyChoices() {
		if (partyChoices==null) {
			PropertyModel model = new PropertyModel(pageModel,"partyTypeSelection");
			List choices = new ArrayList(Arrays.asList(PartyTypeSelection.values()));
			/**
			 * if current party is a hierarchy node then do not allow USE CURRENT PARTY
			 */
			if (pageModel!=null && pageModel.getExistingPartyDetails()!=null &&
					!(pageModel.getExistingPartyDetails().getTypeOID()==SRSType.PERSON ||
							pageModel.getExistingPartyDetails().getTypeOID()==SRSType.ORGANISATION)) {
				choices.remove(PartyTypeSelection.CURRENT_PARTY);
			}
			/**
			 * Create component
			 */
			RadioChoice partyChoice = new RadioChoice("panel", model, choices, getPartyTypeRenderer()) {
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.put("colspan", 2);
					tag.put("style", "padding-left: 50px; padding-right: 50px;padding-bottom:15px;");
				}
			};
			Behavior changeBehaviour = new AjaxFormChoiceComponentUpdatingBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					setVisibility(target);
				}
			};
			partyChoice.add(changeBehaviour);
			partyChoice.setRequired(true);
			partyChoice.setLabel(new Model("Party Type"));
			final RadioGroup gp = new RadioGroup("JippoGroup");
//			Radio radio = new Radio("radio1",model){
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				protected RadioGroup getGroup() {					
//					return gp;
//				}
//				
//			};
//			gp.add(radio);
//			partyChoice.add(radio);
			partyChoice.setOutputMarkupId(true);
			partyChoice.setOutputMarkupPlaceholderTag(true);
//			radio.setOutputMarkupId(true);
//			radio.setOutputMarkupPlaceholderTag(true);
			Label label = new Label("label",new Model(""));
			label.setVisible(false);
			partyChoices = new GUIFieldPanel("partyChoices",label,partyChoice);
			partyChoices.setOutputMarkupId(true);
			partyChoices.setOutputMarkupPlaceholderTag(true);
		}
		return partyChoices;
	}

	protected void setVisibility(AjaxRequestTarget target) {
		boolean newPartySelected = 
			pageModel!=null && 
			pageModel.getPartyTypeSelection()!=null &&
			pageModel.getPartyTypeSelection().equals(
				PartyTypeSelection.NEW_PARTY);
		getPartyTypeContainer().setVisible(newPartySelected);
		if (target!=null) {
			target.add(getPartyTypeContainer());
		}
	}

	private IChoiceRenderer getPartyTypeRenderer() {
		if (partyTypeRenderer == null) {
			partyTypeRenderer = new SRSAbstractChoiceRenderer<Object>() {
				public Object getDisplayValue(Object value) {
					assert(value instanceof PartyTypeSelection);
					return ((PartyTypeSelection)value).getDescription();
				}
				public String getIdValue(Object value, int index) {
					return ""+index;
				}
			};
		}
		return partyTypeRenderer;
	}
	
	

}
