package za.co.liberty.web.pages.maintainagreement;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.dto.agreement.AgreementDTO;
import za.co.liberty.dto.agreement.properties.PaysToDTO;
import za.co.liberty.interfaces.rating.FranchiseTemplateKindEnum;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.DistributionKindGroupPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.DistributionPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.FranchisePoolAccountPanelModel;
import za.co.liberty.web.pages.maintainagreement.model.MaintainAgreementPageModel;
import za.co.liberty.web.pages.maintainagreement.model.PaysToPanelModel;
import za.co.liberty.web.pages.panels.ViewTemplateBasePanel;
import za.co.liberty.web.wicket.view.ContextDrivenViewTemplate;

/**
 * This class represents the Distribution & Pays To Panel for the maintain agreement page
 * @author kxd1203
 *
 */
public class DistributionPaysToPanel extends ViewTemplateBasePanel<AgreementGUIField, AgreementDTO> 
									 implements ISecurityPanel {

	private static final long serialVersionUID = 2253771882080568353L;

	private MaintainAgreementPageModel pageModel;
	
	private DistributionPaysToForm pageForm;
	
	private transient Logger logger ;
	
	private boolean initialised = false;

	private FranchisePoolAccountPanel franchisePoolAccountPanel;

	private PaysToPanel paysToPanel;
	
	private DistributionKindGroupPanel distributionKindGroupPanel;

	private DistributionPanel distributionPanel;
	
	protected Collection<FormComponent> validationComponents = new ArrayList<FormComponent>();
	
	public DistributionPaysToPanel(String id, MaintainAgreementPageModel pageModel, 
			FeedbackPanel feedBackPanel, EditStateType editState) {
		this(id,pageModel,feedBackPanel,editState,null);
	}
	
	public DistributionPaysToPanel(String id, MaintainAgreementPageModel pageModel, 
			FeedbackPanel feedBackPanel, EditStateType editState,
			Page parentPage) {
		super(id, editState, parentPage);
		this.pageModel=pageModel;
	}
	
	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if (!initialised) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Adding components to the page on first render");
			}
			add(getDistributionPaysToForm());
			initialised=true;
		}
	}
	
	/**
	 * Set the page model - to be used prior to rendering if the model changes
	 * @param pageModel
	 */
	public void setPageModel(MaintainAgreementPageModel pageModel) {
		this.pageModel = pageModel;
	}
	
	/**
	 * Get the main page form
	 * @return
	 */
	private DistributionPaysToForm getDistributionPaysToForm() {
		if (pageForm==null) {
			pageForm = new DistributionPaysToForm("pageForm");
		}
		return pageForm;
	}
	
	/**
	 * This class represents the page form to be added to the panel
	 * @author kxd1203
	 */
	private class DistributionPaysToForm extends Form {

		public DistributionPaysToForm(String id) {
			super(id);
			initComponents();
			// Add validation due to issues with form validation in wizards
			add(new AbstractFormValidator() {
				


				private static final long serialVersionUID = 1L;

				@SuppressWarnings("unchecked")
				public FormComponent[] getDependentFormComponents() {		
						return null;
				}

				@Override
				public void validate(Form<?> arg0) {
					System.out.println("DistributionPaysToPanel.validate");
//					validateFormComponents(validationComponents, getFeedBackPanel());
					AgreementDTO dto = pageModel.getMaintainAgreementDTO().getAgreementDTO();
					
					System.out.println("  -- PaysTo=" + ((dto.getPaymentDetails() != null) ? dto.getPaymentDetails().getPayto() : null));
					 
					System.out.println("  -- Template=" + ((pageModel.getMaintainAgreementDTO() != null 
							&& pageModel.getMaintainAgreementDTO().getAgreementDTO() != null) ?
									pageModel.getMaintainAgreementDTO().getAgreementDTO().getDistributionDetails() : null));
					
//					pageModel.get
				}
				
			});
			
			
			
		}
		
		

		@Override
		protected void onError() {
			super.onError();
			System.out.println("DistributionPaysToPanel.onError ");
		}
		
		



		/**
		 * Add all components to the form
		 */
		private void initComponents() {
			/**
			 * Add components
			 */
			add(getPaysToPanel());
			add(getFranchisePoolPanel());
			add(getDistributionPanel());
			/**
			 * Check field visibility
			 */
			checkFieldVisibility();
			
		}
	}
	
	@Override
	protected void checkFieldVisibility() {
		super.checkFieldVisibility();
		boolean showLinkedDetails = (getContext()!=null &&
				getContext().getPaymentDetails()!=null &&
				getContext().getPaymentDetails().getPayto()!=null && 
				getContext().getPaymentDetails().getPayto().equals(
						PaysToDTO.PayToType.ORGANISATION));
		getFranchisePoolPanel().setVisible(
				isVisible(AgreementGUIField.FRANCHISE_POOL_ACCOUNT_PANEL));
	}
	
	public DistributionPanel getDistributionPanel() {
		if (distributionPanel == null) {
			DistributionPanelModel panelModel = new DistributionPanelModel(pageModel);
			int agmKind = 0;
			distributionPanel = new DistributionPanel("distributionPanel",getEditState(),panelModel,agmKind);
		}
		return distributionPanel;
	}

	public PaysToPanel getPaysToPanel() {
		if (paysToPanel == null) {
			PaysToPanelModel panelModel = new PaysToPanelModel(
					pageModel);
			paysToPanel = new PaysToPanel("paysToPanel",getEditState(),panelModel, getFeedBackPanel());
		}
		return paysToPanel;
	}
	
	
	public DistributionKindGroupPanel getDistributionKindGroupPanel() {
		if (distributionKindGroupPanel == null) {
			
			DistributionKindGroupPanelModel panelModel = new DistributionKindGroupPanelModel(pageModel);
			distributionKindGroupPanel  = new DistributionKindGroupPanel("distributionKindGroupPanel",getEditState(),panelModel);
		}
		return distributionKindGroupPanel;
	}

	public FranchisePoolAccountPanel getFranchisePoolPanel() {
		if (franchisePoolAccountPanel == null) {
			franchisePoolAccountPanel = new FranchisePoolAccountPanel(
					"franchisePoolAccountPanel",
					getEditState(),
					new FranchisePoolAccountPanelModel(pageModel));
			franchisePoolAccountPanel.setOutputMarkupId(true);
			franchisePoolAccountPanel.setOutputMarkupPlaceholderTag(true);
		}

		return franchisePoolAccountPanel;
	}

	@Override
	protected ContextDrivenViewTemplate<AgreementGUIField, AgreementDTO> getViewTemplate() {
		return pageModel.getViewTemplate();
	}

	@Override
	protected AgreementDTO getViewTemplateContext() {
		return pageModel.getMaintainAgreementDTO().getAgreementDTO();
	}
	
	private AgreementDTO getContext() {
		return pageModel.getMaintainAgreementDTO().getAgreementDTO();
	}

	public Class getPanelClass() {
		return getClass();
	}
	
	private Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(this.getClass());
		}
		return logger;
	}

}
