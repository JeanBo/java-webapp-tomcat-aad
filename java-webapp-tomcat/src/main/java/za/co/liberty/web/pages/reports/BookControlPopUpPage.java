package za.co.liberty.web.pages.reports;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.report.BookControlDto;
import za.co.liberty.dto.report.ReportEnquiryResponseDto;

public class BookControlPopUpPage extends WebPage{
	
	public BookControlPopUpPage(BookControlDto bookControlDto){
		Form form = new ControlPopupPageForm("controlPopUpPage",bookControlDto); 
		add(form);
	}
	
	class ControlPopupPageForm extends Form {
		ReportEnquiryResponseDto ReportEnquiryResponseDto=null;
		private Component requestId;
		private Component systemName;
		private Component controlRecord;
		private Component receivedRecord;
		BookControlDto bookControlDto=null;
		
		
		public ControlPopupPageForm(String id,BookControlDto bookControlDto) {
			super(id);
			this.bookControlDto=bookControlDto;
			add(requestId = getRequestId());
			add(systemName = getsystemName());
			add(controlRecord = getControlRecord());
			add(receivedRecord = getReceivedRecord());
       
		}
		private Component getRequestId() {
			PropertyModel propertyModel = new PropertyModel(this,
					"bookControlDto.getRequestId()");
			Label viewLabel = new Label("requestId", propertyModel);
			viewLabel.setOutputMarkupId(true);
			return viewLabel;
		}
		private Component getsystemName() {
			PropertyModel propertyModel = new PropertyModel(this,
					"bookControlDto.getSystemName()");
			Label viewLabel = new Label("systemName", propertyModel);
			viewLabel.setOutputMarkupId(true);
			return viewLabel;
		}
		private Component getControlRecord() {
			PropertyModel propertyModel = new PropertyModel(this,
					"bookControlDto.getControlRecord()");
			Label viewLabel = new Label("controlRecord", propertyModel);
			viewLabel.setOutputMarkupId(true);
			return viewLabel;
		}
		private Component getReceivedRecord() {
			PropertyModel propertyModel = new PropertyModel(this,
					"bookControlDto.getReceivedRecord()");
			Label viewLabel = new Label("receivedRecord", propertyModel);
			viewLabel.setOutputMarkupId(true);
			return viewLabel;
		}
		
		
		
	}

	

}
