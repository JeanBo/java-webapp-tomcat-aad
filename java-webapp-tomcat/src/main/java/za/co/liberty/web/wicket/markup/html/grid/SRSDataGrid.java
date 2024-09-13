package za.co.liberty.web.wicket.markup.html.grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.datagrid.DefaultDataGrid;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.IStatefullComponent;

/**
 * <p>This grid is just an extension of DefaultDataGrid<br/>
 * It has the added ability to hold the panel state and adjust the columns and table size<br/>
 * It will also default to a size 0f 300px hight with 15 rows per page.<br/>
 * If setGridWidth is called using percentages then the columns will not be resized.<br/>
 * If you would not like the table to resize call setAutoResize(false) as it is defaulted to true.<br/>
 * One can also specify extra data in the grid which if used in conjunction with SRSGridSelectionCheckBox will
 * not allow these items to be selected.
 * </p>  
 *  
 * <p>Table height calculation: call {@linkplain this#setAutoCalculateTableHeight(boolean)} 
 * to set height calculation.  This will ensure that the table size shrinks to fit the data.</p>
 * 
 * @author DZS2610 Dean Scott 21 July 2008
 *
 */
public class SRSDataGrid extends DefaultDataGrid implements IStatefullComponent {
	
	private static final long serialVersionUID = -7806072316929129000L;

	private EditStateType currentState;	
	
	private boolean autoResizeColumns = true;
	
	private boolean autoResizeTableHeight = false;
	
	private int rowHeightInPx = 22;
	
	private int scrollbarHeightInPx = 15;  
	
	private int maxTableHeightInPx = 0;
	
	private int width;
	
	private GridSizeUnit sizeUnit;
	
	private Map<Object, Object> nonSelectableRowObjects = new HashMap<Object, Object>();
	
	private int rowCount = 0;
	
	private boolean preLightWanted = true;
	
	/* Keep reference that the columns have been resized already */
	private boolean resized;
	
	private int selectionCount = 0;
	
	private Form frm;
	
	private boolean initialised = false;

	
	public enum GridSizeUnit{
		PERCENTAGE,PIXELS
	}
	
	private void init(){		
		this.setRowsPerPage(15);
		this.setContentHeight(300, SizeUnit.PX);
		autoResizeTableHeight = (currentState!=null && currentState.isViewOnly());
//		calculateTableHeight();		
	}
	
	public SRSDataGrid(String id, IDataSource dataSource, List<IGridColumn> columns , EditStateType state) {
		super(id, dataSource, columns);	
		this.currentState = state;
		init();
	}	

	public SRSDataGrid(String id, IModel model, List<IGridColumn> columns, EditStateType state) {
		super(id, model, columns);
		this.currentState = state;
		init();
	}
	
	@SuppressWarnings("unchecked")
	public SRSDataGrid(String id, IModel model, List<IGridColumn> columns, EditStateType state, List nonSelectableRowObjects) {
		super(id, model, columns);
		this.currentState = state;
		this.nonSelectableRowObjects = new HashMap();
		if(nonSelectableRowObjects != null){
			for(Object in : nonSelectableRowObjects){
				this.nonSelectableRowObjects.put(in, in);
			}
		}
		init();	
	}
	
	@SuppressWarnings("unchecked")
	public SRSDataGrid(String id, IDataSource dataSource, List<IGridColumn> columns , EditStateType state, List nonSelectableRowObjects) {
		super(id, dataSource, columns);	
		this.currentState = state;
		this.nonSelectableRowObjects = new HashMap();
		if(nonSelectableRowObjects != null){
			for(Object in : nonSelectableRowObjects){
				this.nonSelectableRowObjects.put(in, in);
			}
		}
		init();
	}
	
	

//	@Override
//	protected void onBeforeRender() {
////		remove the form as grid will already be in one
//		if(!initialised){
//			initialised = true;
//			Component parent = this.getParent();
//			if(parent instanceof Form){
//				frm = (Form) parent;
//				Component gridCmp = get("form");
//				if(gridCmp instanceof Form){
//					Form gridForm = (Form) gridCmp;
//					Component cmp = gridForm.get("bodyContainer");
//					WebMarkupContainer cont = new WebMarkupContainer("form"){
//						private static final long serialVersionUID = 1L;
//						@Override
//						protected void onComponentTag(ComponentTag tag) {					
//							super.onComponentTag(tag);
//							//change the form to a span
//							tag.setName("span");
//						}				
//					};
//					gridForm.remove(cmp);
//					remove(gridForm);
//					cont.add(cmp);
//					add(cont);
//				}
//			}
//		}		
//		super.onBeforeRender();
//	}

	/**
	 * Recalculate 
	 *
	 */
	public void calculateTableHeight() {
		if (autoResizeTableHeight) {
			if (getTotalRowCount()>=getRowsPerPage()) {
				super.setContentHeight(maxTableHeightInPx, SizeUnit.PX);
				return;
			}
			int gridSize = (int) (getTotalRowCount() * rowHeightInPx);
			gridSize+= scrollbarHeightInPx;
			super.setContentHeight(gridSize, SizeUnit.PX);
		}
		
	}
	
	@Override
	public void setContentHeight(Integer val, SizeUnit unit) {
		super.setContentHeight(val, unit);
		if (unit == SizeUnit.PX) {
			maxTableHeightInPx = val;
		} else if (autoResizeTableHeight){
			// this is percentage based, do not calculate
			autoResizeTableHeight=false;
		}
	}

	/**
	 * Set the table height to auto calculate, forces a {@linkplain #calculateTableHeight()}
	 * when the value is true.
	 * 
	 * @param val
	 */
	public void setAutoCalculateTableHeight(boolean val) {
		autoResizeTableHeight = val;
		if (autoResizeColumns) {
			calculateTableHeight();
		}
	}
	
	public EditStateType getEditState() {		
		return currentState;
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {	
		if(!resized && this.autoResizeColumns && sizeUnit != GridSizeUnit.PERCENTAGE){
			resizeColumns(tag);
			resized = true;
		}
		rowCount = 0;
		super.onComponentTag(tag);		
	}
	
	@Override
	protected void onRowPopulated(WebMarkupContainer arg0) {	
		if(!preLightWanted){
			String classString = (rowCount % 2 == 0) ? "imxt-even" : "imxt-odd";						
			arg0.add(new AttributeModifier("class",classString));
		}
		super.onRowPopulated(arg0);
		rowCount++;
	}
	
	/**
	 * If one wants the prelight to be swiched off for performance then set to false<br/>
	 * By default it is set to true;
	 * @param wanted
	 */
	public void setPreLight(boolean wanted){
		preLightWanted = wanted;
	}

	/**
	 * Resize the colums to the width of the style tag but style must be in the following format<code>style=&quot;width:640px&quot;</code>
	 * otherwise resize the table to the column sizes added up
	 * this is enabled by default but can be turned off by using the setAutoResize to false
	 * @param tag
	 */
	private void resizeColumns(ComponentTag tag){
		int tableSize = 0;
		if(width == 0){
		String attribute = (String)tag.getAttributes().get("style");		
		if(attribute != null){
			String[] split = attribute.split("[width\\:,px]");
			for(String spl : split){
				if(spl.trim().length() > 0){
					//try get int value
					try{
						tableSize = Integer.parseInt(spl);
						break;
					}catch(Exception ex){
						//do nothing, value will just be replaced
						break;
					}
				}
			}
		}
		}else{
			tableSize = width;
		}
		boolean newSize = (tableSize == 0);
		Collection<IGridColumn> cols = this.getActiveColumns();
		if(newSize){
			resizeTableSizeToFitColumns(tag,cols);
		}
		else{
			resizeColumsToTableSize(cols,tableSize);
		}
	}
	
	/**
	 * Resize the colums using their given sizes as percentages to fit into the tables given size
	 * @param cols
	 * @param tableSize
	 */
	private void resizeColumsToTableSize(Collection<IGridColumn> cols, int tableSize){
		//int columns = cols.size();
		int colsTotal = 0;
		for(IGridColumn col : cols){
			if(col instanceof AbstractColumn){
				int initSize = col.getInitialSize();
				colsTotal += initSize;				
			}
		}			
		int newTotal = 0;
		int selectionexcess = 0;
		AbstractColumn lastCol = null;		
		for(IGridColumn col : cols){
			if(col instanceof AbstractColumn){
				int initSize = col.getInitialSize();				
				double perc = (double)tableSize / (double)colsTotal;				
				int newSize = (int)Math.round((double)initSize * perc);				
				lastCol = (AbstractColumn)col;
				newTotal += newSize;				
				if(col instanceof SRSGridRowSelectionCheckBox){
					//keep this col at its specified size, it should be 30px too look good
					if(lastCol.getInitialSize() > newSize){
						selectionexcess = lastCol.getInitialSize() - newSize;						
					}
				}else{
					lastCol.setInitialSize((newSize + selectionexcess));					
					selectionexcess = 0;
				}				
			}
		}
		if(lastCol != null){
		//snip off the excess on the last column to get rid of scrollbar
			int excess = (tableSize + 3) - newTotal;			
			lastCol.setInitialSize(lastCol.getInitialSize() - excess);			
		}		
	}
	
	/**
	 * Resize the table by using the given column sizes
	 * @param tag
	 * @param cols
	 */
	private void resizeTableSizeToFitColumns(ComponentTag tag, Collection<IGridColumn> cols){
		int colsTotal = 0;
		for(IGridColumn col : cols){			
				int initSize = col.getInitialSize();
				colsTotal += initSize;
		}	
		tag.put("style", "width:"+(colsTotal + 1)+"px");
	}
	
	/**
	 * Sets whether this grid should automatically resize or not
	 * @param autoResize
	 */
	public void setAutoResize(boolean autoResize){
		this.autoResizeColumns = autoResize;
	}
	
	/**
	 * Returns a list of the Objects inside the models of each column
	 * 
	 * @return
	 */
	public List<Object> getSelectedItemObjects(){
		Collection<IModel> selectedItems = this.getSelectedItems();
		Object modelObject =  this.getDefaultModelObject();
		HashMap<Object, Object> currentItems = new HashMap<Object, Object>();
		boolean doCheck = false;
		if(modelObject instanceof SRSDataProviderAdapter){			
			//check that the selected items still exist in the provider
			//bug found where selected items contain all selections even if the objects dont exist in the grid model anymore
			SRSDataProviderAdapter dataProviderAd = ((SRSDataProviderAdapter)modelObject);
			if(dataProviderAd.getDataProvider() instanceof ListDataProvider){
				doCheck = true;
				ListDataProvider dataProvider = (ListDataProvider) dataProviderAd.getDataProvider();
				Iterator<Object> iterator  = dataProvider.iterator(0,dataProvider.size());
				while(iterator.hasNext()){
					Object obj = iterator.next();	
					currentItems.put(obj, obj);
				}
			}
		}		
		List<Object> ret = new Vector<Object>();
		if(selectedItems != null){
			for(IModel model : selectedItems){
				if(!doCheck || currentItems.get(model.getObject()) != null){
					ret.add(model.getObject());
				}
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public void setNonSelectableRowObjects(List nonSelectableRowObjects){
		if(nonSelectableRowObjects != null){
			this.nonSelectableRowObjects = new HashMap();
			for(Object ob : nonSelectableRowObjects){
				this.nonSelectableRowObjects.put(ob, ob);
			}
		}		 
	}
	
	public void setNonSelectableRowObjects(Map<Object, Object> nonSelectableRowObjects){
		if(nonSelectableRowObjects != null){
			this.nonSelectableRowObjects = nonSelectableRowObjects;
		}
	}
	
	
		
	@Override
	public void selectItem(IModel itemModel, boolean selected) {
		if(getNonSelectableRowObjects().get(itemModel.getObject()) == null){
			if(selected){
				selectionCount++;
			}else{
				selectionCount--;
			}
			super.selectItem(itemModel, selected);
		}		
	}	

	/**
	 * When this method is called the style of the grid will be overritten to hold the value sent in
	 * @param widthInPixels
	 */
	public void setGridWidth(int width, GridSizeUnit sizeUnit){
		this.width = width;
		this.sizeUnit = sizeUnit;		
		this.add(new AttributeModifier("style","width:"+width+ ((sizeUnit == GridSizeUnit.PERCENTAGE) ? "%" : "px")));
	}

	/**
	 * Get the underlying non selectable row objects map.
	 * 
	 * @return
	 */
	public Map<Object, Object> getNonSelectableRowObjects() {
		return nonSelectableRowObjects;
	}
	
	/**
	 * Add additional items to the non selectable map
	 * 
	 * @param list
	 */
	public void addAllToNonSelectableRowObjectMap(List<? extends Object> list) {
		for (Object o : list) {
			nonSelectableRowObjects.put(o, o);
		}
	}

	/**
	 * Returns the number of selected items in the grid
	 * @return
	 */
	public int getSelectionCount() {
		return selectionCount;
	}

	@Override
	public Form getForm() {			
		if(frm != null){
			return frm;
		}
		return super.getForm();
	}

	/* (non-Javadoc)
	 * @see com.inmethod.grid.datagrid.DataGrid#findRowComponent(org.apache.wicket.model.IModel)
	 */
	@Override
	public WebMarkupContainer findRowComponent(IModel rowModel) {
		
		return super.findRowComponent(rowModel);
	}
	
	
}
