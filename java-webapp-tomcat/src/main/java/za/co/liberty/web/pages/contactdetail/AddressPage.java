package za.co.liberty.web.pages.contactdetail;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;

import za.co.liberty.dto.party.contactdetail.AddressDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IModalMaintenancePageModel;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.contactdetail.model.ContactDetailsPanelModel;
import za.co.liberty.web.wicket.modal.SRSModalWindow;

/**
 * Page for the popup for adding/editing of an address
 * 
 * @author DZS2610
 * 
 */
public class AddressPage extends BaseWindowPage {
	private static final long serialVersionUID = 1L;

	private ContactDetailsPanelModel panelModel;

	private AddressPanel addressPanel;	

	private EditStateType editState;

	private SRSModalWindow modalWindow;
	
	public enum ButtonSelected{OK,CANCEL};
	
	private ButtonSelected currentAction;
	
	private IModalMaintenancePageModel<AddressDTO> pageModel;
	
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param panelModel
	 * @param editState
	 * @param dtoToUse
	 * @param feedBackPanel
	 */
	public AddressPage(ContactDetailsPanelModel panelModel,
			EditStateType editState, AddressDTO address, SRSModalWindow modalWindow) {
		this.panelModel = panelModel;		
		this.modalWindow = modalWindow;
//		if (editState.isAdd()) {
//			
//		}
		this.editState = EditStateType.MODIFY;
		pageModel = new IModalMaintenancePageModel<AddressDTO>() {
			private static final long serialVersionUID = 1L;
			private AddressDTO selectedItem;
			private boolean success = false;
			
			@Override
			public void setSelectedItem(AddressDTO selected) {
				selectedItem = selected;
			}

			@Override
			public AddressDTO getSelectedItem() {
				return selectedItem;
			}

			@Override
			public boolean isModalWizardSucces() {
				return success;
			}

			@Override
			public void setModalWizardSuccess(boolean success) {
				this.success = success;
			}

			@Override
			public String getModalWizardMessage() {
				return null;
			}
		};
		pageModel.setSelectedItem(address);
		modalWindow.setSessionModelForPage(pageModel);
		add(new MyForm("addressForm",address));

	}
	
	@Override
	public String getPageName() {
		return "Add Address";
	}

	/**
	 * Form for the adding of an address
	 * 
	 * @author DZS2610
	 * 
	 */
	private class MyForm extends Form {
		private static final long serialVersionUID = 1L;

		public MyForm(String id, AddressDTO startingAddress) {
			super(id);
			add(addOkButton("okButton"));
			add(addCancelButton("cancelButton"));
			// Default a new add to physicall address
			add(addressPanel = new AddressPanel("addressPanel", editState,
					panelModel, startingAddress, pageModel));
		}

		/**
		 * Ok button
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		private Button addOkButton(String id) {
			Button button = new Button(id);
			button.add(new AjaxFormSubmitBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onError(AjaxRequestTarget target) {
					pageModel.setModalWizardSuccess(false);
					modalWindow.setSessionModelForPage(pageModel);
					// error occured, add to feedback and display
					target.add(getFeedBackPanel());

				}

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					pageModel.setModalWizardSuccess(true);
					modalWindow.setSessionModelForPage(pageModel);
					do_Ok(target);
				}
			});
			return button;
		}
		
		/**
		 * Cancel button
		 * 
		 * @return
		 */
		@SuppressWarnings("unused")
		private Button addCancelButton(String id) {			
			Button button = new Button(id);
			button.add(new AjaxFormComponentUpdatingBehavior("click") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {					
					currentAction = ButtonSelected.CANCEL;
					pageModel.setModalWizardSuccess(false);
					modalWindow.setSessionModelForPage(pageModel);
					modalWindow.close(target);
				}				
				
			});
			return button;
		}
		
		

		/**
		 * Process the ok click
		 * 
		 */
		private void do_Ok(AjaxRequestTarget target) {
			// add the new address to the pagemodel
			try {
				AddressDTO currentAddress = addressPanel.getAddressObjectOnPanel();
				panelModel.getContactDetailsController().validateAddressDetail(currentAddress);
//				panelModel.addAddress(currentAddress);
				currentAction = ButtonSelected.OK;
				modalWindow.close(target);
			} catch (ValidationException e) {
				for(String error : e.getErrorMessages()){
					error(error);
				}
			}			
		}		
	}
	
	/**
	 * Will return what action happened on the screen and if nothing happened then will return null
	 * @return
	 */
	public ButtonSelected getButtonClicked(){
		return currentAction;
	}
}
