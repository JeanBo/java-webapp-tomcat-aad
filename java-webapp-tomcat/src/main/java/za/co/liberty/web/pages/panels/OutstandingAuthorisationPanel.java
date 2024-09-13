package za.co.liberty.web.pages.panels;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import za.co.liberty.agreement.common.enums.RequestKindEnumeration;
import za.co.liberty.dto.agreement.RequestDTO;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;

public class OutstandingAuthorisationPanel extends BasePanel {

	private Label requestTypeLabel;
	private RequestDTO context;
	protected IConverter requestTypeConverter;
	private Label requestOidLabel;

	public OutstandingAuthorisationPanel(String id, EditStateType editState,RequestDTO context) {
		super(id, editState);
		this.context=context;
		add(new OutstandingAuthorisationForm("outstandingRequestForm"));
	}
	
	private class OutstandingAuthorisationForm extends Form {

		public OutstandingAuthorisationForm(String id) {
			super(id);
			initComponents();
		}

		private void initComponents() {
			RepeatingView view = new RepeatingView("outstandingRequestFields");
			add(view);
			view.add(new GUIFieldPanel("requestOid",
					new Label("label",new Model("Request Oid")),
					getRequestOid()));
			view.add(new GUIFieldPanel("requestType",
					new Label("label",new Model("Request Type")),
					getRequestType()));
		}
		
	}

	public Label getRequestType() {
		if (requestTypeLabel == null) {
			requestTypeLabel = new Label("panel",new PropertyModel(context,"kind")) {
				@Override
				public IConverter getConverter(Class arg0) {
					return getRequestTypeConverter();
				}
			};
		}
		return requestTypeLabel;
	}
	
	public Label getRequestOid() {
		if (requestOidLabel == null) {
			requestOidLabel = new Label("panel",new PropertyModel(context,"id"));
		}
		return requestOidLabel;
	}

	private IConverter getRequestTypeConverter() {
		if (requestTypeConverter == null) {
			requestTypeConverter = new IConverter() {
				public Object convertToObject(String stringValue, Locale locale) {
					return 0;
				}

				public String convertToString(Object objectValue, Locale locale) {
					if (objectValue==null || !(objectValue instanceof Integer)) {
						return "";
					}
					RequestKindType requestKind = 
						RequestKindType.getRequestKindTypeForKind((Integer)objectValue);
					return requestKind.getDescription();
				}
				
			};
			
		}
		return requestTypeConverter;
	}

}
