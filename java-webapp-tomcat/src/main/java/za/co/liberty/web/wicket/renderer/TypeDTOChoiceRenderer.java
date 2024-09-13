package za.co.liberty.web.wicket.renderer;

import za.co.liberty.dto.spec.TypeDTO;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * This class represents a renderer for a TypeDTO instance object
 * 
 * @author kxd1203
 *
 */
public class TypeDTOChoiceRenderer extends SRSAbstractChoiceRenderer {

	private static final long serialVersionUID = -8718020752351716396L;

	public Object getDisplayValue(Object object) {
		if (object instanceof TypeDTO) {
			return ((TypeDTO)object).getDescription();
		}
		return "";
	}

	public String getIdValue(Object object, int index) {
		if (object == null) 
			return "";
		return ""+index;
	}
	

}
