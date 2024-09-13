package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.GUIFieldPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

public class BankingDetailsChoicePanel extends BasePanel implements ISecurityPanel{

	private MaintainAgreementPageModel pageModel;
	private RepeatingView bankDetChoicePanel;
	private GUIFieldPanel bankDetChoices;
	private IChoiceRenderer choiceRenderer;
	private boolean initialised;
	protected Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	public Class getPanelClass() {
		
		return BankingDetailsChoicePanel.class;
	}
	
	public BankingDetailsChoicePanel(String id, MaintainAgreementPageModel pageModel, 
			FeedbackPanel feedBackPanel, EditStateType editState) {
		this(id,pageModel,feedBackPanel,editState,null);
	}
	
	public BankingDetailsChoicePanel(String id, MaintainAgreementPageModel pageModel, 
			FeedbackPanel feedBackPanel, EditStateType editState,
			Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel=pageModel;
	}
	
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			initialised=true;
			add(new BankingDetailsChoiceForm("bankingDetailsChoiceForm"));			
		}
	}
	
	private class BankingDetailsChoiceForm extends Form {
		
		public BankingDetailsChoiceForm(String id) {
			super(id);
			add(getBankingDetialsChoicePanel());	
			
			// Add validation due to issues with form validation in wizards
			add(new AbstractFormValidator() {
				


				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {		
						return null;
				}

				@Override
				public void validate(Form<?> arg0) {
					validateFormComponents(validationComponents, getFeedBackPanel());
					
				}
				
			});
		}
	}
	
	public RepeatingView getBankingDetialsChoicePanel() {
		if (bankDetChoicePanel == null) {
			bankDetChoicePanel = new RepeatingView("bankPanel");
			bankDetChoicePanel.add(getBankDetChoices());
		}
		return bankDetChoicePanel;
	}
	
	private GUIFieldPanel getBankDetChoices() {
		if (bankDetChoices==null) {
			PropertyModel model = new PropertyModel(pageModel,"bankingDetailsRequiredSelection");
			List choices = new ArrayList(Arrays.asList(MaintainAgreementPageModel.BankingDetailsRequiredSelection.values()));
			
			/**
			 * Create component
			 */
			RadioChoice bankDetChoice = new RadioChoice("panel", model, choices, getChoiceRenderer()) {
				@Override
				protected void onComponentTag(ComponentTag tag) {
					super.onComponentTag(tag);
					tag.put("colspan", 2);
					tag.put("style", "padding-left: 50px; padding-right: 50px;padding-bottom:15px;");
				}
			};
			final RadioGroup gp = new RadioGroup("JippoGroup");
			bankDetChoice.setRequired(true);
			validationComponents.add(bankDetChoice);
			bankDetChoice.setLabel(new Model("Banking Details Choice"));
//			Radio radio = new Radio("radio1",model){
//			private static final long serialVersionUID = 1L;
//
//				@Override
//				protected RadioGroup getGroup() {
//					return gp;
//				}
//				
//			};
//			bankDetChoice.add(radio);
			bankDetChoice.setOutputMarkupId(true);
			bankDetChoice.setOutputMarkupPlaceholderTag(true);
//			radio.setOutputMarkupId(true);
//			radio.setOutputMarkupPlaceholderTag(true);
			Label label = new Label("label",new Model(""));
			label.setVisible(false);
			bankDetChoices = new GUIFieldPanel("bankDetChoices",label,bankDetChoice);
			bankDetChoices.setOutputMarkupId(true);
			bankDetChoices.setOutputMarkupPlaceholderTag(true);
		}
		return bankDetChoices;
	}

	private IChoiceRenderer getChoiceRenderer() {
		if (choiceRenderer == null) {
			choiceRenderer = new SRSAbstractChoiceRenderer<Object>() {
				public Object getDisplayValue(Object value) {
					assert(value instanceof MaintainAgreementPageModel.BankingDetailsRequiredSelection);
					return ((MaintainAgreementPageModel.BankingDetailsRequiredSelection)value).getDescription();
				}
				public String getIdValue(Object value, int index) {
					return ""+index;
				}
			};
		}
		return choiceRenderer;
	}
}
