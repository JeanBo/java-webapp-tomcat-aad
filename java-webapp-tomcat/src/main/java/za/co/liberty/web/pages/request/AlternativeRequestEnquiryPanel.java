package za.co.liberty.web.pages.request;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.request.IRequestViewGuiController;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;
//import za.co.liberty.web.pages.request.alternative.AlternativeAuthPAYEPanel;
//import za.co.liberty.web.pages.request.alternative.AlternativeAuthVATPanel;
import za.co.liberty.web.pages.request.alternative.AlternativeAuthPAYEPanel;
import za.co.liberty.web.pages.request.alternative.AlternativeAuthVATPanel;

/**
 * A special kind of panel that shows enahnced views for certain request kinds
 * 
 * TODO jzb0608 - probably a temporary measure, just using it to show additional VAT info for now
 * 
 * @author jzb0608
 *
 */
public class AlternativeRequestEnquiryPanel extends Panel implements IStatefullComponent {
		
		private static final long serialVersionUID = 311514105636074L;
		
		private static final Logger logger = Logger.getLogger(AlternativeRequestEnquiryPanel.class);
		
		/* Attributes */
		private transient IRequestViewGuiController guiController;
		private EditStateType editState = EditStateType.AUTHORISE;
		private ViewRequestModelDTO pageModel;
		
		
		/**
		 * Default constructor 
		 * 
		 * @param id
		 * @param viewRequestPageModel
		 */
		public AlternativeRequestEnquiryPanel(String id, ViewRequestModelDTO viewRequestPageModel) {
			super(id);
			this.pageModel = viewRequestPageModel;
			add(createPanel("panel", pageModel));
		}

		// ========================================================================================
		// Web field section
		// ========================================================================================

		/**
		 * Create the authentication panel.
		 * 
		 * @param id
		 * @param panelModel
		 * @return
		 */
		protected Panel createPanel(String id, ViewRequestModelDTO pageModel) {

			if (pageModel.getRequestKindList().contains(RequestKindType.VAT)) {
				return new AlternativeAuthVATPanel(id, getEditState(), pageModel, null);
			}
			if (pageModel.getRequestKindList().contains(RequestKindType.PAYE)) {
				return new AlternativeAuthPAYEPanel(id, getEditState(), pageModel, null);
			}
			return new EmptyPanel(id);
		}
	
	

		// ========================================================================================
		// Other General methods
		// ========================================================================================
		/**
		 * Return the current edit state.
		 * 
		 */
		public EditStateType getEditState() {
			return editState;
		}

		/**
		 * Get the pageModel
		 * 
		 * @return
		 */
		protected ViewRequestModelDTO getPageModel() {
			return pageModel;
		}	
		
		/**
		 * Get the gui controller for this panel
		 * 
		 * @return
		 */
		protected IRequestViewGuiController getGuiController() {
			if (guiController == null) {
				try {
					guiController = ServiceLocator.lookupService(IRequestViewGuiController.class);
				} catch (NamingException e) {
					throw new CommunicationException(e);
				}
			}
			return guiController;
		}
		
		/**
		 * Decorate the style tag to hide the component
		 * 
		 * @param isHidden Hide component if true. 
		 * @param tag
		 */
		private void decorateComponentStyleToHide(boolean isHidden, ComponentTag tag) {
			String val = (String) tag.getAttributes().get("style");
			val = (val ==null) ? "" : val;
			val += (isHidden) ? " ;display:none;" : " ;display:block;";
			tag.put("style", val);
		}
		

		/**
		 * Get the target agreement number (might be null)
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		protected Long getTargetAgreementNumber() {
			return (pageModel.getViewRequestContextDto().getAgreementDto()!=null) ? 
					pageModel.getViewRequestContextDto().getAgreementDto().getAgreementNumber() : null;
		}
		
		/**
		 * Get the target party number (might be null)
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		protected Long getTargetPartyOid() {
			return (pageModel.getViewRequestContextDto().getPartyDto()!=null) ? 
					pageModel.getViewRequestContextDto().getPartyDto().getPartyOid() : null;
		}
		
	
	}
