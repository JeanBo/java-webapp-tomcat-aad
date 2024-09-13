package za.co.liberty.web.pages.admin;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.persistence.srs.IBroadcastMessagesEntityManager;
import za.co.liberty.persistence.srs.entity.BroadcastMessagesEntity;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.MaintenanceBasePage;
import za.co.liberty.web.pages.admin.models.BroadcastMessagesAdminModel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.form.SRSTextField;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.repeater.data.ListDataProvider;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;

/**
 * Admin panel for broadcast messages
 * @author jzb0608
 *
 */
public class BroadcastMessagesAdminPanel extends BasePanel {
	
	private SRSDataGrid broadcastGrid = null;
	private SRSTextField agreementNrText = null;
	private SRSTextField partyOidText = null;

	private static final long serialVersionUID = 1L;
	
	private BroadcastMessagesAdminModel pageModel;
	
	public BroadcastMessagesAdminPanel(String id, EditStateType editState) {
		super(id, editState);

	}

	public BroadcastMessagesAdminPanel(String id, EditStateType editState, BroadcastMessagesAdminModel page, MaintenanceBasePage parentPage) {
		super(id, editState,parentPage);
		pageModel = page;
		pageModel.setMessagesList(new ArrayList<BroadcastMessagesEntity>());
		initialize();
	}
	

	private void initialize(){
		try {
			
			IBroadcastMessagesEntityManager entityManager = ServiceLocator.lookupService(IBroadcastMessagesEntityManager.class);
			pageModel.getMessagesList().addAll(entityManager.findBroadcastMessagesForAgreementOrParty(612L, 500L));
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
		
		add(agreementNrText = createAgreementNrField("agreementNr"));
		add(partyOidText = createPartyOidField("partyOid"));
//		add()
		add(broadcastGrid= createBroadcastGrid("broadcastGrid"));
	}
	

	
	private SRSTextField createAgreementNrField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"agreementNr" ));
		tempSRSTextField.setOutputMarkupId(true);
//		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		
		return tempSRSTextField;
	}
	
	private SRSTextField createPartyOidField(String id){
		SRSTextField tempSRSTextField = new SRSTextField(id,new PropertyModel(pageModel,"partyOid" ));
		tempSRSTextField.setOutputMarkupId(true);
//		tempSRSTextField.setEnabled(!getEditState().isViewOnly());
		
		return tempSRSTextField;
	}


	private SRSDataGrid createBroadcastGrid(String name){
		SRSDataGrid tempDataGrid = new SRSDataGrid(name,new DataProviderAdapter(
					new ListDataProvider<BroadcastMessagesEntity>(pageModel.getMessagesList())),
					createSearchResultColumns(),getEditState());
		tempDataGrid.setAutoResize(true);
		tempDataGrid.setOutputMarkupId(true);
		tempDataGrid.setCleanSelectionOnPageChange(false);
		tempDataGrid.setClickRowToSelect(false);
		tempDataGrid.setAllowSelectMultiple(false);
		tempDataGrid.setGridWidth(900, GridSizeUnit.PIXELS);		
		tempDataGrid.setRowsPerPage(10);
		tempDataGrid.setContentHeight(100, SizeUnit.PX);

		return tempDataGrid;
	}
	protected List<IGridColumn> createSearchResultColumns() { 
		List<IGridColumn> columns = new ArrayList<IGridColumn>();
		
		columns.add( new PropertyColumn(new Model("Party OID"),"partyOid", "partyOid").setInitialSize(100));
		columns.add( new PropertyColumn(new Model("Agreement NR"),"agreementNumber", "agreementNumber").setInitialSize(100));
		columns.add( new PropertyColumn(new Model("Type"),"broadcastType", "broadcastType").setInitialSize(150));
		columns.add( new PropertyColumn(new Model("Time Sent"),"timeSent", "timeSent").setInitialSize(150));
		
		columns.add(new AbstractColumn("view", new Model("View XML")){
			
			private static final long serialVersionUID = 1L;
			@Override
			public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
				
				final BroadcastMessagesEntity entity = (BroadcastMessagesEntity) rowModel.getObject();
				
					final Button searchButton = new Button("value", new Model("View"));	
					searchButton.add(new AjaxFormComponentUpdatingBehavior("click"){									
						private static final long serialVersionUID = 1L;
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							System.out.println("Viewing XML:\n " + entity.getXmlMessage());
						}									
					});
					searchButton.setOutputMarkupId(true);
					return HelperPanel.getInstance(componentId,searchButton);		
//				}
			}				
		}.setInitialSize(70));
		
		return columns;
	}
		
	
	

}
