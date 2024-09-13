package za.co.liberty.web.pages.search;

import java.util.Locale;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.context.IContextSearchType;
import za.co.liberty.web.pages.search.models.ContextSearchModel;


/**
 * Search for an Agreement/Party and once selected update
 * the context.
 * 
 * @author JZB0608 - 22 May 2008
 */
public class SearchValuePanel extends Panel {

	/* Constants */
	private static final long serialVersionUID = 4008008744919434971L;
	
	/* Attributes */
	ContextSearchModel pageModel;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 */
	public SearchValuePanel(String id, 
			ContextSearchModel pageModel) {
		super(id);
		this.pageModel = pageModel;
		add(createSearchValueField("searchValue"));
	}

	/**
	 * Create the search value field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField createSearchValueField(String id) {
		/* Create the field */
		TextField text = (TextField) new TextField(id, new PropertyModel(pageModel, "searchValueObject")){
					@Override
					public IConverter getConverter(Class arg0) {
						if(pageModel.getSearchType() == AgreementSearchType.CONSULTANT_CODE){
							return new IConverter(){

								public Object convertToObject(String string, Locale arg1) {									
									if(string != null){
										try{
											return Long.parseLong((String)string);
										}catch(NumberFormatException e){
											throw new ConversionException("'" + string + "' is not a valid thirteen digit code");
										}
									}
									throw new ConversionException("Thirteen digit code is value is required");
								}

								public String convertToString(Object value, Locale arg1) {	
									//pad with zeros
									if(value == null){
										return "";
									}else if(((Long)value).toString().length() >= 13){
										return ((Long)value).toString();
									}else{
										String val = ((Long)value).toString();
										StringBuilder ret = new StringBuilder(13);
										//ret.append(value);
										while(ret.length() != 13 - val.length()){
											ret.append("0");
										}
										ret.append(val);
										return ret.toString();										
									}									
								}
								
							};
						}
						return super.getConverter(arg0);						
					}
			
		}.setLabel(	new Model(pageModel.getSearchType().toString()))
				.setRequired(true).setOutputMarkupId(true);
				
		/* Set the type and determine validator if necessary */
		IContextSearchType e =pageModel.getSearchType();
		text.setType(e.getValueClassType());
		
		
		
		return text;
	}
	
}
