package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.inmethod.grid.IGridColumn;

import za.co.liberty.dto.userprofiles.AllowableRequestActionDTO;
import za.co.liberty.dto.userprofiles.IUserAdminLink;
import za.co.liberty.dto.userprofiles.PartyProfileDTO;
import za.co.liberty.dto.userprofiles.ProfileRoleDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;
import za.co.liberty.web.pages.panels.AbstractLinkingPanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

/**
 * 
 * 
 * Abstract class to handle linking between allowable requests to user's,
 * and allowable requests to role's profiles.
 * @param <MODEL>
 */
public class AllowableRequestLinkingPanel<MODEL extends IMaintenancePageModel> 
		extends AbstractLinkingPanel<MODEL, RequestKindType,AllowableRequestActionDTO> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AllowableRequestLinkingPanel(String id, MODEL pageModel,
			EditStateType editState){
		super(id, pageModel, editState, new ChoiceRenderer("description","description"));
		
	}
	
	@Override
	protected List<AllowableRequestActionDTO> getCurrentlyLinkedItemList() {
		return ((IUserAdminLink) bean).getAllowableRequestActionList(); 
	}
	
	
	@Override
	protected AllowableRequestActionDTO createNewLinkedItem(RequestKindType dto) {
		AllowableRequestActionDTO newDto = new AllowableRequestActionDTO();
		newDto.setRequestKind(dto);
		return newDto;
	}
	
	@Override
	protected Comparator<? super RequestKindType> getAvailableItemComparator() {
			 return new Comparator<RequestKindType>() {
				 public int compare(RequestKindType o1, RequestKindType o2) {
					 //changed to compare on names
					 return o1.getDescription().compareToIgnoreCase(o2.getDescription());
					//return o1.getRequestKind()-o2.getRequestKind();
				}
			 };
	}
	
	@Override
	protected List<RequestKindType> getCompleteAvailableItemList() {
		List<RequestKindType> allRequestKindTypes = new ArrayList<RequestKindType>(); 
		for(RequestKindType type: RequestKindType.values()){
			allRequestKindTypes.add(type);
		}
		return  allRequestKindTypes;
	}

	

	@Override
	protected Object getKeyForAvailableItem(RequestKindType item) {
		return item.getRequestKind();
	}

	@Override
	protected Object getKeyForLinkedItem(AllowableRequestActionDTO item) {
		return item.getRequestKind().getRequestKind();
	}

	@Override
	protected Comparator<? super AllowableRequestActionDTO> getLinkedItemComparator() {
		return new Comparator<AllowableRequestActionDTO>() {
			 public int compare(AllowableRequestActionDTO o1, AllowableRequestActionDTO o2) {
					return o1.getRequestKind().getRequestKind();
				 	 // return (o1.getOid().intValue());
			}
		 };
	}
	
	@Override
	protected List<IGridColumn> getLinkedItemGridColumns() {
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		columns.add(new SRSDataGridColumn<AllowableRequestActionDTO>("requestKind",new Model("Request Kind"),"requestKind","requestKind",getEditState()).setInitialSize(280));
				
		columns.add(new SRSDataGridColumn<AllowableRequestActionDTO>("allowRaise",new Model("Raise"),"allowRaise",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AllowableRequestActionDTO data) {
				CheckBox box = new CheckBox("value",new PropertyModel(rowModel.getObject(),objectProperty));
				updateComponentToUpdateWithAjax(box,"click");
				Panel panel = HelperPanel.getInstance(componentId,box,true);
				boolean isEditable = state != EditStateType.VIEW;
				box.setEnabled(isEditable);
				panel.setEnabled(isEditable && data.getRequestKind() != null);				
				return panel;
			}			
		}.setInitialSize(100));	
		
		columns.add(new SRSDataGridColumn<AllowableRequestActionDTO>("allowAuthorise",new Model("Authorise"),"allowAuthorise",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AllowableRequestActionDTO data) {
				CheckBox box = new CheckBox("value",new PropertyModel(rowModel.getObject(),objectProperty));
				updateComponentToUpdateWithAjax(box,"click");
				Panel panel = HelperPanel.getInstance(componentId,box,true);
				boolean isEditable = state != EditStateType.VIEW;
				box.setEnabled(isEditable);
				panel.setEnabled(isEditable && data.getRequestKind() != null);
				return panel;
			}			
		}.setInitialSize(100));	
		
		columns.add(new SRSDataGridColumn<AllowableRequestActionDTO>("allowDecline",new Model("Decline"),"allowDecline",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AllowableRequestActionDTO data) {
				CheckBox box = new CheckBox("value",new PropertyModel(rowModel.getObject(),objectProperty));
				updateComponentToUpdateWithAjax(box,"click");
				Panel panel = HelperPanel.getInstance(componentId,box,true);
				boolean isEditable = state != EditStateType.VIEW;
				box.setEnabled(isEditable);
				panel.setEnabled(isEditable && data.getRequestKind() != null);
				return panel;
			}			
		}.setInitialSize(100));	
		
		columns.add(new SRSDataGridColumn<AllowableRequestActionDTO>("allowView",new Model("View"),"allowView",editState){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, AllowableRequestActionDTO data) {
				CheckBox box = new CheckBox("value",new PropertyModel(rowModel.getObject(),objectProperty));
				updateComponentToUpdateWithAjax(box,"click");
				Panel panel = HelperPanel.getInstance(componentId,box,true);
				boolean isEditable = state != EditStateType.VIEW;
				box.setEnabled(isEditable);
				panel.setEnabled(isEditable && data.getRequestKind() != null);
				return panel;
			}			
		}.setInitialSize(100));	
		
		return columns;
	}

		
	
}