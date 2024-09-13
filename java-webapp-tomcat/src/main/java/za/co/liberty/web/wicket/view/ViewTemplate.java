package za.co.liberty.web.wicket.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.fields.AgreementGUIField;

public abstract class ViewTemplate {
	
	public abstract boolean isModifiable(AgreementGUIField field);
	public abstract boolean isAddable(AgreementGUIField field);
	public abstract boolean isViewable(AgreementGUIField field);
	
	public EditStateType[] getEditStates(AgreementGUIField field) {
		List<EditStateType> list = new ArrayList<EditStateType>();
		if (isModifiable(field)) {
			list.add(EditStateType.MODIFY);
		}
		if (isAddable(field)) {
			list.add(EditStateType.ADD);
		}
		return list.toArray(new EditStateType[0]);
	}

}
