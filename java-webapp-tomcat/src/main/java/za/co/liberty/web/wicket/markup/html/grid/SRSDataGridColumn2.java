package za.co.liberty.web.wicket.markup.html.grid;

import java.io.Serializable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.inmethod.grid.column.AbstractColumn;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.panels.HelperPanel;

/**
 * General Panel DataGrid column 
 * <p>USe this to display any panel in the data grid</p>
 * <p>The newCellPanel is called for each cell, A new panel should be produced as a result 
 * 
 * @author DZS2610
 *
 */
public class SRSDataGridColumn2<DATAObject extends Object> extends AbstractColumn {

	private static final long serialVersionUID = 4517876276206073661L;		
	private String objectProperty;	
	private EditStateType state;
	private DATAObject dataObject;
	private String defaultLabelStyle;
	

	public SRSDataGridColumn2(String columnId, IModel headerModel,String objectProperty, EditStateType state) {
		super(columnId, headerModel);		
		this.objectProperty = objectProperty;
		this.state = state;
	}	

	public SRSDataGridColumn2(String columnId, IModel headerModel, String sortProperty,String objectProperty, EditStateType state) {
		super(columnId, headerModel, sortProperty);			
		this.objectProperty = objectProperty;
		this.state = state;
	}
	
	/**
	 * Set the style to be used on the labels when the grid creates a label
	 * @param style
	 */
	public void setDefaultLabelStyle(String style){		
		this.defaultLabelStyle = style;
	}
	
	public String getDefaultLabelStyle() {
		return defaultLabelStyle;
	}
	
	@Override	
	public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {				
		dataObject = (DATAObject) rowModel.getObject();		
		Panel ret = newCellPanel(parent,componentId,rowModel,objectProperty,state, dataObject);
		if(ret.get("value") != null){
			Component comp = ret.get("value");
			if( comp instanceof CheckBox){
				//center box in screen
				ret.add(new AttributeModifier("class","imxt-select"));
			}
		}
		return ret;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCellCssClass(IModel rowModel, int rowNum) {		
		if(rowModel != null){
			try{
			Object obj1 = getPropertyValue((DATAObject) rowModel.getObject(),objectProperty);			
			if(obj1.getClass() == boolean.class || obj1 instanceof Boolean){
				return "imxt-select";
			}}
			catch(Exception ex){
				//do nothing, usual style will apply
			}
		}		
		return super.getCellCssClass(rowModel, rowNum);		
	}
	
	/**
	 * This method expects a panel so that the html is formatted
	 * Override method to insert any panel here and it will be rendered in the cell
	 * 
	 * @param parent
	 * @param componentId
	 * @param rowModel
	 * @return
	 */
	public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, DATAObject data){
		Object value = getPropertyValue(data, objectProperty);
		
		Label label = new Label("value",(Serializable) value);
		if(defaultLabelStyle != null){
			label.add(new AttributeModifier("style",defaultLabelStyle));
		}		
		
		if(value != null){
			label.add(new AttributeModifier("title", newCellPanelValue(value)));
		}
		return HelperPanel.getInstance(componentId, label);
	}
	
	/**
	 * Get the property value for the row, override if you need additional retrieval options that
	 * can not be supported by property request.  For example, complex map or other object retrievals
	 * 
	 * @param data
	 * @param objectProperty
	 * @return
	 */
	public Object getPropertyValue(DATAObject data, String objectProperty) {
		return PropertyResolver.getValue(objectProperty, data);
	}

	/**
	 * Get the string value from the object
	 * 
	 * @param value
	 * @return
	 */
	public String newCellPanelValue(Object value) {
		if (value==null) {
			return null;
		}
		return value.toString();
	}
}
