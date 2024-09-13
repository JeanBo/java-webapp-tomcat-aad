package za.co.liberty.web.pages.party;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.party.bankingdetail.BankingVerificationResponseDTO;
import za.co.liberty.dto.party.bankingdetail.type.AVSStatusType;
import za.co.liberty.web.data.enums.EditStateType;

/**
 * Wicket panel containing the banking details verification response fields to display on the screen
 * @author moses.seloma
 */
public class AVSPanel extends Panel {

	private BankingVerificationResponseDTO responseDTO;

	public AVSPanel(String tab_panel_id, BankingVerificationResponseDTO model, EditStateType editState,
			Page parentPage) {
		super(tab_panel_id);
		this.responseDTO = model;
		add(createTotalRows(tab_panel_id, editState));
	}

	public ListView createTotalRows(String id, EditStateType editState) {
		ListView rows = new ListView<VerificationResultDTO>(id, convertToResultDTO(responseDTO, editState)) {

			private static final long serialVersionUID = 0L;

			@Override
			protected void populateItem(ListItem<VerificationResultDTO> item) {

				item.add(new Label("colDescription1", item.getModelObject().description1));
				item.add(new Label("colValue1", "" + item.getModelObject().getValue1()));
				item.add(new Label("colDescription2", item.getModelObject().description2));
				item.add(new Label("colValue2", "" + item.getModelObject().getValue2()));
				item.add(new Label("colDescription3", item.getModelObject().description3));
				item.add(new Label("colValue3", "" + item.getModelObject().getValue3()));
			}

		};
		return rows;
	}

	protected List<VerificationResultDTO> convertToResultDTO(BankingVerificationResponseDTO responseDTO, EditStateType editState) {
		List<VerificationResultDTO> list = new ArrayList<>();
		if(responseDTO == null || EditStateType.MODIFY.equals(editState)){
			return list;
		}

		VerificationResultDTO dto1 = new VerificationResultDTO();
		dto1.value1 = getAVSStringValue(responseDTO.getIsIdentityNumber());
		dto1.description1 = "ID/Passport/CompReg";
		dto1.value2 = getAVSStringValue(responseDTO.getIsLastName());
		dto1.description2 = "Last Name/Company Name";
		dto1.value3 = getAVSStringValue(responseDTO.getIsInitials());
		dto1.description3 = "Initials";
		list.add(dto1);
		
		VerificationResultDTO dto2 = new VerificationResultDTO();
		dto2.value1 = getAVSStringValue(responseDTO.getIsAccountThreeMonths());
		dto2.description1 = "Account Three Months";
		dto2.value2 = getAVSStringValue(responseDTO.getIsAccountVerified());
		dto2.description2 = "Account Verified";
		dto2.value3 = getAVSStringValue(responseDTO.getIsAccountActive());
		dto2.description3 = "Account Active";
		list.add(dto2);

		VerificationResultDTO dto3 = new VerificationResultDTO();
		dto3.value1 = getAVSStringValue(responseDTO.getIsAccountAcceptDebits());
		dto3.description1 = "Account Accept Debits";
		dto3.value2 = getAVSStringValue(responseDTO.getIsAccountAcceptCredits());
		dto3.description2 = "Account Accept Credits";
		dto3.value3 = getAVSStringValue(responseDTO.getIsAccountTypeMatch());
		dto3.description3 = "Account Type Match";
		list.add(dto3);

		VerificationResultDTO dto4 = new VerificationResultDTO();
		dto4.value1 = getAVSStringValue(responseDTO.getIsCellNumberMatch());
		dto4.description1 = "Cell Number Match";
		dto4.value2 = getAVSStringValue(responseDTO.getIsEmailMatch());
		dto4.description2 = "Email Match";
		list.add(dto4);

		return list;
	}

	private String getAVSStringValue(AVSStatusType avsStatusType) {
		return avsStatusType != null ? avsStatusType.getMqAvsStatus() : "";
	}

	class VerificationResultDTO implements Serializable {

		private static final long serialVersionUID = 1L;
		private String description1 = "";
		private String value1 = "";
		private String description2 = "";
		private String value2 = "";
		private String description3 = "";
		private String value3 = "";

		public String getDescription1() {
			return description1;
		}

		public void setDescription1(String description1) {
			this.description1 = description1;
		}

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
		}

		public String getDescription2() {
			return description2;
		}

		public void setDescription2(String description2) {
			this.description2 = description2;
		}

		public String getValue2() {
			return value2;
		}

		public void setValue2(String value2) {
			this.value2 = value2;
		}

		public String getDescription3() {
			return description3;
		}

		public void setDescription3(String description3) {
			this.description3 = description3;
		}

		public String getValue3() {
			return value3;
		}

		public void setValue3(String value3) {
			this.value3 = value3;
		}

	}
}
