package za.co.liberty.web.wicket.markup.html.grid;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.datagrid.DataGrid;

import za.co.liberty.web.pages.panels.HelperPanel;

/**
 * <p>This is a column for use with the SRSDataGrid It is use to select items in
 * the grid It has a select all picture at the top of the column and row and
 * column tooltips already built in.<br/>
 * Default header was overriten to add in the selection and deselection picture.<br/>
 * Should be used with SRSDataGrid as this will allow more functionality like extra data rows
 * that do not allow selection</p>
 * 
 * @author DZS2610 Dean Scott 25 July 2008
 * @deprecated  Use {@linkplain SRSCheckBoxColumn} instead
 * 
 */
public class SRSGridRowSelectionCheckBox extends CheckBoxColumn {

	private static final long serialVersionUID = 1L;
	
	private transient Logger logger = Logger.getLogger(SRSGridRowSelectionCheckBox.class);

	/**
	 * Keeps track of selection
	 */
	private boolean checked = false;
	
	String selectionImage = "/SRSAppWeb/images/wtable_select_all.gif";
	String deselectionImage = "/SRSAppWeb/images/wtable_deselect_all.gif";
	
	
	/**
	 * @param columnId
	 */
	public SRSGridRowSelectionCheckBox(String columnId) {
		super(columnId);
		//set up image path
		Properties prop = new Properties();
		try {
			prop.load(SRSGridRowSelectionCheckBox.class.getResourceAsStream("srsgrid.properties"));
			selectionImage = prop.getProperty("srs.grid.selection.image");
			deselectionImage =  prop.getProperty("srs.grid.deselection.image");
		} catch (IOException e) {	
			//dont need to worry in this app as the default images will be used
			//TODO if moved into separate jar then handle error
			logger.warn("srsgrid.properties could not be loaded, defaults will be used",e);
		}
	}	
	
	

	@Override	
	public IModel getHeaderTooltipModel() {
		return null;
	}

	/**
	 * Override this method to change the selected tooltip
	 * 
	 * @return
	 */
	public String getSelectedTooltip() {
		return "Click here to deselect all the items";
	}

	/**
	 * Override this method to change the unselected tooltip
	 * 
	 * @return
	 */
	public String getUnselectedTooltip() {
		return "Click here to select all the items";
	} 

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
		Panel panel = (Panel) super.newCell(parent, componentId, rowModel);
//		Map<Object, Object> nonSelectableRowObjects = null;
//		
//		if(this.getGrid() instanceof SRSDataGrid){
//			nonSelectableRowObjects = ((SRSDataGrid)this.getGrid()).getNonSelectableRowObjects();
//		}
//		if(rowModel != null && nonSelectableRowObjects != null && nonSelectableRowObjects.get(rowModel.getObject()) != null){
//			Component old = panel.get("checkbox");
//			System.err.println("checkbox = " + old);
//			old.setOutputMarkupId(true);
//			old.setOutputMarkupPlaceholderTag(true);			
//			old.setEnabled(false);
//			old.setVisible(false);					
//		}		
		return panel;
	}
	
	@Override
	protected boolean isCheckBoxVisible(IModel rowModel) {		
		Map<Object, Object> nonSelectableRowObjects = null;
		if(this.getGrid() instanceof SRSDataGrid){
			nonSelectableRowObjects = ((SRSDataGrid)this.getGrid()).getNonSelectableRowObjects();
		}
		if(rowModel != null && nonSelectableRowObjects != null && nonSelectableRowObjects.get(rowModel.getObject()) != null){
			return false;					
		}	
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component newHeader(String componentId) {
		final Panel panel = (Panel) super.newHeader(componentId);
	
		// WICKETTEST WICKETFIX
		// Added the select as 2nd parm, unsure of this
		final ImageButton button = new ImageButton("value", "Select") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {				
				super.onSubmit();
			}

			@Override
			public boolean isVisible() {
				return getGrid().isAllowSelectMultiple();
			}

			@Override
			public Form<?> getForm() {		
				Form frm = null;
				try{
					frm = super.getForm();
				}catch(Exception e){
					//leave as form will be null
				}
				if(frm == null){
					return new Form("TEMPFORM");
				}
				return frm;
			}	
			
			
		};		
		String title = getUnselectedTooltip();		
		button.add(new AttributeModifier("title", title));
		button.add(new AttributeModifier("src",selectionImage));		
		button.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				checked = !checked;
				if (checked) {
					selectAllItems(checked);									
					button.add(new AttributeModifier("src",
							deselectionImage));
					button.add(new AttributeModifier("title",
							getSelectedTooltip()));
				} else {
					selectAllItems(checked);										
					button.add(new AttributeModifier("src",
							selectionImage));
					button.add(new AttributeModifier("title",
							getUnselectedTooltip()));
				}
				getGrid().update();
			}
			// WICKETSTUFF 

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new CancelEventIfNoAjaxDecorator();
//			}
		});
		
		panel.replace(HelperPanel.getInstance("checkbox", button));
		return panel;
	}
	
	/**
	 * Selects all the items in the grid based on the boolean sent in
	 *
	 */
	protected void selectAllItems(boolean checked){
		final DataGrid grid = (DataGrid)getGrid();
		IDataSource source = grid.getDataSource();
		if(source instanceof SRSDataProviderAdapter){			
			IDataProvider provider = ((SRSDataProviderAdapter)source).getDataProvider();
			Iterator objs = provider.iterator(0, provider.size());
			while(objs.hasNext()){
				Object rowObject = objs.next();
				IModel model = provider.model(rowObject);
				grid.selectItem(model, checked);
			}			
		}else{
			if(checked){
				grid.selectAllVisibleItems();
			}else{
				grid.resetSelectedItems();
			}
		}		
	}

	@Override
	protected IModel getRowTooltipModel(IModel itemModel) {
		return new Model("Click on the checkbox to select this item");
	}

}
