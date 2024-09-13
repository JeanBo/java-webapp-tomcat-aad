package za.co.liberty.web.wicket.validation;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * <p>Does validation on a TabbedPanel by iterating through 
 * all the tabs, visiting each {@link FormComponent} on
 * each tab and then applying the validation on that 
 * component.  If a tab contains an error it will
 * be set to be the active tab.</p>
 * 
 * <p>Validation is not done on the currently selected tab as
 * it would already have been validated when the form was
 * submitted.  All validators for all formComponents on a tab 
 * is processed even if an error occurs, but no other tab will
 * be validated. </p>
 * 
 * @author JZB0608 (Jean Bodemer) - 13 May 2008
 * @author JZB0608 (Jean Bodemer) - 22 Sept 2008 - All types of 
 * 			component validations are now catered for.
 */
public class TabbedPanelValidator {
	
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	private TabbedPanel tabbedPanel;
	
	private boolean isError;
	
	/**
	 * Default constructor 
	 * 
	 * @param tabbedPanel
	 */
	public TabbedPanelValidator(TabbedPanel tabbedPanel) {
		this.tabbedPanel = tabbedPanel;
	}
	
	/**
	 * Validate each tab.  No other tabs are validated once
	 * an error is found but validation will continue for 
	 * the current panel.  The current panel will be set 
	 * to the selected panel.
	 * 
	 * @return false if an error is found
	 */
	public boolean validate() {
		isError = false;
		
		if (logger.isDebugEnabled()) logger.debug("Start validation of tabs");
		
		int selected = tabbedPanel.getSelectedTab();
		
		/* Process each tab */
		@SuppressWarnings("unchecked")
		List<ITab> tabList = tabbedPanel.getTabs();
		for (int i  =0; i < tabList.size(); ++i) {
			ITab tab = tabList.get(i); 
			// ignore the current as its already been validated
			if (selected == i) {
				if (logger.isDebugEnabled()) logger.debug("panel already validated - " +tab.getTitle());
				continue;
			}
			
			if (logger.isDebugEnabled()) logger.debug("validating tab " +tab.getTitle());
			Panel panel =(Panel) tab.getPanel(TabbedPanel.TAB_PANEL_ID);
			
			panel.visitChildren(FormComponent.class, new ValidatingVisitor());
			
			if (isError) { 
				tabbedPanel.setSelectedTab(i);
				return false;
			}
		}
		
		/* No errors */
		return true;
	}
	
	/**
	 * Validate each visited {@linkplain FormComponent}
	 * 
	 * @author JZB0608 - 13 May 2008
	 *
	 */
	public class ValidatingVisitor implements IVisitor<Component, Void> {
		
		@Override
		public void component(Component component, IVisit<Void> visit) {
			
			FormComponent formComponent = (FormComponent) component;
			final Form form = formComponent.getForm();

			if (logger.isDebugEnabled()) {
				logger.debug(" > visiting " + component 
						+ "\n     isEnabled="+form.isEnabled()
						+ ", isEnableAllowed="+form.isEnableAllowed()
						+ ", isVisibleInHierarchy="+form.isVisibleInHierarchy()
						+ ", isValid=" + formComponent.isValid()
						+ ", goingToValidateNow="+(form.isEnabled() && form.isEnableAllowed() 
								&& form.isVisibleInHierarchy())	
						+ "\n     modelValue=" + formComponent.getDefaultModelObjectAsString()
						);
			}
			if (form.isEnabled() && form.isEnableAllowed() 
					&& form.isVisibleInHierarchy()) {
				formComponent.validate();
				
			
				/* Do is required validation */
				if (formComponent.isRequired()
						&& Strings.isEmpty(formComponent
								.getDefaultModelObjectAsString())) {
					formComponent.error((IValidationError)new ValidationError().addKey("Required"));
					isError = true;
				}
				
				/* Do all other validation */		
				List valList = formComponent.getValidators();
			
				ValidatableAdapter valAdapter = new ValidatableAdapter(formComponent);
				for (int i = 0; valList != null && i < valList.size(); ++i) {
					((IValidator)valList.get(i)).validate(valAdapter);
				}
				if (formComponent.isValid()==false) {
					isError = true;
				}
			}
			// No longer required, default is to continue
//			return IVisitor.CONTINUE_TRAVERSAL;
		}


	}
	
	/**
	 * Adapter that makes this component appear as {@link IValidatable}
	 * 
	 * @author Jean Bodemer (JZB0608) - 19 Sep 2008
	 */
	private class ValidatableAdapter implements IValidatable<Object> {

		FormComponent formComponent;
		
		public ValidatableAdapter(FormComponent formComponent) {
			this.formComponent = formComponent;
		}
		
		/**
		 * @see IValidatable#error(IValidationError)
		 */
		public void error(IValidationError error){
			formComponent.error(error);
		}

		/**
		 * @see IValidatable#getValue()
		 */
		public Object getValue(){
			return formComponent.getConvertedInput();
		}

		public boolean isValid(){
			return formComponent.isValid();
		}

		@Override
		public IModel<Object> getModel() {
			return formComponent.getModel();
		}

	}
}
