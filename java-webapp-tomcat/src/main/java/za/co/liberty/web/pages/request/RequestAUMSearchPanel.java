/**
 * 
 */
package za.co.liberty.web.pages.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import za.co.liberty.dto.gui.context.InfoKindType;
import za.co.liberty.dto.gui.request.FundCodeDTO;
import za.co.liberty.dto.gui.request.ProductCodeDTO;
import za.co.liberty.dto.gui.request.RequestEnquiryDTO;
import za.co.liberty.web.pages.request.model.RequestEnquiryModel;
import za.co.liberty.web.wicket.markup.html.form.SRSAbstractChoiceRenderer;

/**
 * @author zzt2108
 * 
 */
public class RequestAUMSearchPanel extends AbstractRequestEnquiryPanel {

	

	private static final long serialVersionUID = -245602607033962730L;

	protected DropDownChoice<?> infoKindField;
	protected DropDownChoice<?> productCodeField;
	protected DropDownChoice<?> fundCodeField;

	protected RequestEnquiryDTO dataModel;
	protected RequestEnquiryModel pageModel;

	protected static final List<InfoKindType> infoKindSearchTypeList;

	static {
		infoKindSearchTypeList = new ArrayList<InfoKindType>();
		for (InfoKindType infoKindSearchType : InfoKindType.values()) {
			infoKindSearchTypeList.add(infoKindSearchType);
		}
	}

	public RequestAUMSearchPanel(String id, IModel<?> model, FeedbackPanel feedbackPanel) {
		super(id, model, feedbackPanel);
		pageModel = (RequestEnquiryModel) model.getObject();
		dataModel = pageModel.getDataModel(RequestPolicyTransactionPanel.class);
		add(infoKindField = createInfoKindField("infoKind"));
		add(productCodeField = createProductCodeField("productCode"));
		add(fundCodeField = createFundCodeField("fundCode"));
	}

	private DropDownChoice<?> createFundCodeField(String id) {
		System.out.println("Creating Fund Code Field");
		IModel<Object> model = new IModel<Object>() {

			private static final long serialVersionUID = 1784178155243764510L;

			public Object getObject() {
				return dataModel.getFundCode();
			}

			public void setObject(Object arg0) {
				dataModel.setFundCode((FundCodeDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, model, pageModel.getAllFundCodes(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 147879826508304070L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((FundCodeDTO) value).getFundDescription() + " - " + ((FundCodeDTO) value).getBatch();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((FundCodeDTO) value).getId() + "";
			}
		});

		return field;
	}

	private DropDownChoice<?> createProductCodeField(String id) {
		System.out.println("Creating Product Code Field");
		IModel<Object> model = new IModel<Object>() {
			private static final long serialVersionUID = 1L;

			public Object getObject() {
				return dataModel.getProductCode();
			}

			public void setObject(Object arg0) {
				dataModel.setProductCode((ProductCodeDTO) arg0);
			}

			public void detach() {
			}
		};

		DropDownChoice<?> field = new DropDownChoice<Object>(id, model, pageModel.getAllProductCodes(), new SRSAbstractChoiceRenderer<Object>() {

			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(Object value) {
				return (value == null) ? null : ((ProductCodeDTO) value).getProductDescription();
			}

			public String getIdValue(Object value, int arg1) {
				return (value == null) ? null : ((ProductCodeDTO) value).getId() + "";
			}
		});

		return field;
	}

	private DropDownChoice<?> createInfoKindField(String id) {
		System.out.println("Creating Info Kind Field");
		DropDownChoice<?> field = new DropDownChoice<Object>(id, new IModel<Object>() {

			private static final long serialVersionUID = 489570069636191260L;

			public Object getObject() {
				return dataModel.getInfoKindSearchType();
			}

			public void setObject(Object arg0) {
				dataModel.setInfoKindSearchType((InfoKindType) arg0);
			}

			public void detach() {
			}

		}, infoKindSearchTypeList);
		field.setNullValid(true);
		/*
		 * field.add(new AjaxFormComponentUpdatingBehavior("change") { private
		 * static final long serialVersionUID = 1L;
		 * 
		 * @Override protected void onUpdate(AjaxRequestTarget target) {
		 * updateShowNextButton(target); } } );
		 */
		return field;
	}

}
