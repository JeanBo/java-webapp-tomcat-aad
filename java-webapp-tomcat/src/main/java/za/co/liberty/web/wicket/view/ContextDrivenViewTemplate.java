package za.co.liberty.web.wicket.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;

public abstract class ContextDrivenViewTemplate<F,T> implements Serializable {
	
	/**
	 * Return true if the field can be modified for the given context
	 * @param field The GUI field
	 * @param context the context
	 * @return true if the field can be modified
	 */
	public abstract boolean isModifiable(F field,T context);
	
	/**
	 * Return true if the field can be added for the given context
	 * @param field the GUI field
	 * @param context the context
	 * @return true if the field can be modified for the given context
	 */
	public abstract boolean isAddable(F field,T context);
	
	/**
	 * Return true if the field can be modified when terminating the given context
	 * @param field the GUI field
	 * @param context the context
	 * @return true if the field can be modified for the given context
	 */
	public abstract boolean isModifiableForTerminate(F field,T context);
	
	/**
	 * Return true if the field can be viewed for the given context
	 * @param field the GUI field
	 * @param context the context
	 * @return true if the field can be viewed for the given context
	 */
	public abstract boolean isViewable(F field,EditStateType editState, T context);
	
	/**
	 * Return true if the field is required for the given context
	 * @param field the GUI field
	 * @param context the context
	 * @return true if the field is required for the given context
	 */
	public abstract boolean isRequired(F field,T context);
	
	
	public EditStateType[] getEditStates(F field,T context) {
		List<EditStateType> list = new ArrayList<EditStateType>();
		if (isModifiable(field,context)) {
			list.add(EditStateType.MODIFY);
		}
		if (isAddable(field,context)) {
			list.add(EditStateType.ADD);
		}
		if (isModifiableForTerminate(field,context)) {
			list.add(EditStateType.TERMINATE);
		}
		return list.toArray(new EditStateType[0]);
	}
	
	/**
	 * Set the outstanding requests, that may affect the way in 
	 * which components may be rendered.	 
	 */
	public abstract void setOutstandingRequests(List<RequestKindType> outstandingRequests);

}
