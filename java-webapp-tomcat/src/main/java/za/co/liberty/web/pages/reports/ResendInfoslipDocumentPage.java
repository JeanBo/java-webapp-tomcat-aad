package za.co.liberty.web.pages.reports;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.dto.reports.HierarchyUsersEmailDTO;
import za.co.liberty.helpers.communication.MailHelper;
import za.co.liberty.helpers.config.HelperConfigParameterTypes;
import za.co.liberty.helpers.config.HelpersParameterFactory;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.html.grid.SRSGridRowSelectionCheckBox;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

public class ResendInfoslipDocumentPage extends BaseWindowPage {
	
	private AjaxButton closeButton;
	private AjaxButton sendButton;
	private SRSDataGrid emailInfoslipGrid;
	private ModalWindow window;
	private Form infoslipForm;
	private  List<IGridColumn> gridColumns;
	private List<HierarchyUsersEmailDTO> hierarchyUsersEmailDTOs;
	private URI uri;

	public ResendInfoslipDocumentPage(ModalWindow window, List<HierarchyUsersEmailDTO> list, URI uri) {
		this.window = window;
		this.hierarchyUsersEmailDTOs = list;
		this.uri = uri;
		this.add(getResendInfoslipForm());
	}

	private Form getResendInfoslipForm() {
		if (infoslipForm==null) {
			infoslipForm = new ResendInfoslipForm("infoslipForm");
		}
		return infoslipForm;
	}
	
	private class ResendInfoslipForm extends Form {

		public ResendInfoslipForm(String id) {
			super(id);
			this.add(getEmailInfoslipGrid());
			this.add(getSendButton());			
			this.add(getCloseButton());
			
		}
		
	}

	private AjaxButton getCloseButton() {
		if (closeButton==null) {
			closeButton = new AjaxButton("closeButton") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					window.close(target);
				}
				
			};
		}
		return closeButton;
	}
	
	private AjaxButton getSendButton() {
		if (sendButton==null) {
			sendButton = new AjaxButton("sendButton") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form form) {
					
					target.add(getFeedBackPanel());
					HierarchyUsersEmailDTO selectedItemDTO  = null;
					
					if (emailInfoslipGrid.getSelectedItems().size()==0) {
						warn("Please select a record for emailing !");
						target.add(getFeedBackPanel());
						return;
					}
					
					Collection<IModel> col = emailInfoslipGrid.getSelectedItems();
					for (IModel model : col) {
						selectedItemDTO = (HierarchyUsersEmailDTO) model.getObject();
					}
					
					if(uri == null){
						error("URI of file is null ! Cannot generate file to email");
						target.add(getFeedBackPanel());
						return;
					}
					
					//EMAIL THE DOCUMENT TO THE GIVEN EMAIL ID
					boolean b = emailDocument(selectedItemDTO);
					if(!b)
					{
						error("No Secure Email Address present to send email / Error caused while sending email");
						target.add(getFeedBackPanel());
						return;
					}else{
						info("Statement emailed succesfully to :"+selectedItemDTO.getSecureEmail());
						target.add(getFeedBackPanel());
					}
					
				}
				
			};
		}
		return sendButton;
	}
	
	/**
	 * @return the docSearchResultsGrid
	 */
	public SRSDataGrid getEmailInfoslipGrid() {
		if (emailInfoslipGrid == null) {
			
			if(this.hierarchyUsersEmailDTOs == null)
				hierarchyUsersEmailDTOs = new ArrayList<HierarchyUsersEmailDTO>();
			
			emailInfoslipGrid = new SRSDataGrid("emailInfoslipGrid",
					new DataProviderAdapter(new ListDataProvider<HierarchyUsersEmailDTO>(
							hierarchyUsersEmailDTOs)),
					getColumns(),EditStateType.VIEW);
			emailInfoslipGrid.setOutputMarkupId(true);
			emailInfoslipGrid.setCleanSelectionOnPageChange(false);
			emailInfoslipGrid.setAllowSelectMultiple(false);
			emailInfoslipGrid.setGridWidth(100, GridSizeUnit.PERCENTAGE);
			emailInfoslipGrid.setRowsPerPage(10);
			emailInfoslipGrid.setContentHeight(225, SizeUnit.PX);
			emailInfoslipGrid.setAutoCalculateTableHeight(true);
			emailInfoslipGrid.setAutoResize(true);
		}
		return emailInfoslipGrid;
	}
	
	
	
	private List<IGridColumn> getColumns() {
		if (gridColumns == null) {
			gridColumns = new ArrayList<IGridColumn>();
			
		
			SRSDataGridColumn<HierarchyUsersEmailDTO> roleCol = new SRSDataGridColumn<HierarchyUsersEmailDTO>("roleName",
					new Model("Role"),"roleName",EditStateType.VIEW){
				
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final HierarchyUsersEmailDTO data) {
					
					
						Label label = new Label("value",new PropertyModel(data,objectProperty));				
						return HelperPanel.getInstance(componentId, label);
					}
				
			};
			roleCol.setSizeUnit(SizeUnit.PX);
			roleCol.setMinSize(150);
			roleCol.setInitialSize(150);
			gridColumns.add(roleCol);
			
					
			SRSDataGridColumn<HierarchyUsersEmailDTO> advFullNameCol = new SRSDataGridColumn<HierarchyUsersEmailDTO>("name",
					new Model("Name"),"name",EditStateType.VIEW){
				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final HierarchyUsersEmailDTO data) {
					
					
					Label label = new Label("value",new PropertyModel(data,objectProperty));				
					return HelperPanel.getInstance(componentId, label);
				}
				
			};
			advFullNameCol.setSizeUnit(SizeUnit.PX);
			advFullNameCol.setMinSize(200);
			advFullNameCol.setInitialSize(200);
			gridColumns.add(advFullNameCol);
			
			SRSDataGridColumn<HierarchyUsersEmailDTO> secureEmailCol = new SRSDataGridColumn<HierarchyUsersEmailDTO>("secureEmail",
					new Model("Secure Email Address"),"secureEmail",EditStateType.VIEW){

				private static final long serialVersionUID = 1L;

				@Override
				public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, final HierarchyUsersEmailDTO data) {
					
					
					Label label = new Label("value",new PropertyModel(data,objectProperty));				
					return HelperPanel.getInstance(componentId, label);				
				}
				
			
			};
			secureEmailCol.setSizeUnit(SizeUnit.PX);
			secureEmailCol.setMinSize(300);
			secureEmailCol.setInitialSize(300);
			gridColumns.add(secureEmailCol);
			
			/**
			 * Select Column for selecting secure email to send
			 */
			
				SRSGridRowSelectionCheckBox col = new SRSGridRowSelectionCheckBox("checkBox");			
				gridColumns.add(3,col.setInitialSize(35));
			
				
		}
		return gridColumns;
	}

	@Override
	public String getPageName() {
		return "Resend Infoslip Document";
	}
	
	/**
	 * Method that sends the email the document
	 * @param selectedItemDTO 
	 */
	private boolean emailDocument(HierarchyUsersEmailDTO selectedItemDTO){	
		// INC4533878 - change in email body and fromAddress
		String toAddress = selectedItemDTO.getSecureEmail();
		boolean isProduction = HelpersParameterFactory.getInstance().getParameter(
		  		  HelperConfigParameterTypes.IS_PRODUCTION, Boolean.class);
		if(toAddress == null || "".equals(toAddress))
			return false;
						
		java.io.File file = new java.io.File(this.uri);
		MailHelper mailHelper = new MailHelper();		
		String subject = "InfoSlip/ECS Statement";
		String fromAddress = HelpersParameterFactory.getInstance().getParameter(
		  		  HelperConfigParameterTypes.HELPER_PARAM_RESENDINFOSLIP_EMAIL_FROM, String.class);
				
		String message  = "Dear valued partner,\n\nPlease find attached commission statement.\n\nShould you experience difficulty accessing the document, the most probable cause is that you do not have the correct software version.\n\nIf you have any queries, please contact your Liberty branch or region. Where necessary your branch will contact the relevant Commissions channel.\n\nWe value your continuous support.\n\nYours sincerely\nLiberty";
		
		if (!isProduction)
		{
				toAddress = "srsams@liberty.co.za";
		}
		try {
			mailHelper.sendMail(toAddress, fromAddress, subject, message,file);
		} catch (Exception ex) {
			ResendInfoslipDocumentPage.this.error("Error occured whilst sending out the email:"+ex );
			return false;
		}
		
		return true;
	
	}
		
}