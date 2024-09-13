package za.co.liberty.web.wicket.renderer;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import za.co.liberty.interfaces.agreements.FrequencyType;

public class FrequencyTypeChoiceRenderer implements IChoiceRenderer<FrequencyType> {

	private static final long serialVersionUID = -3263464165965480566L;

	@Override
	public Object getDisplayValue(FrequencyType object) {
		if (object instanceof FrequencyType) {
			return ((FrequencyType)object).getKey();
		}
		return "";
	}

	@Override
	public String getIdValue(FrequencyType object, int index) {
		if (object==null)
			return "";
		return ""+index;
	}

	@Override
	public FrequencyType getObject(String id, IModel<? extends List<? extends FrequencyType>> choices) {

		List<FrequencyType> _choices = (List<FrequencyType>) choices.getObject();
		for (int index = 0; index < _choices.size(); index++) {
			// Get next choice
			final FrequencyType choice = _choices.get(index);
			if (getIdValue(choice, index).equals(id)) {
				return choice;
			}
		}
		return null;
	}

}
