package za.co.liberty.web.pages.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.agreement.core.CoreTransferDto;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.system.SRSAuthWebSession;

public class SegmentedTransferPage extends AdstractTransferPage {

	protected String pageName = "Segmented Transfer";

	public SegmentedTransferPage() {
		super(null);
	}

	@Override
	public Panel createContainerPanel() {
		Panel panel = null;
		try {
			FeedbackPanel feedbackPanel = getFeedbackPanel();
			panel = new SegmentedTransferPanel(CONTAINER_PANEL_NAME, pageModel,
					EditStateType.MODIFY, feedbackPanel, this);
		} catch (Exception e) {
			error(e.getMessage());
			panel = new EmptyPanel(CONTAINER_PANEL_NAME);
		}
		panel.setOutputMarkupId(true);
		return panel;
	}

	@Override
	public String getPageName() {
		return pageName;
	}

	@Override
	public void doSave_onSubmit() {
		if (new CoreHelper().validateSegmentGrid(pageModel, this.getFeedbackPanel()))
			raiseRequests(SRSAuthWebSession.get().getCurrentUserid());
		feedbackPanel.anyErrorMessage();
	}

	private void raiseRequests(String userId) {
		ISessionUserProfile userProfile = SRSAuthWebSession.get()
				.getSessionUser();
		Long partyOid = pageContextDTO != null
				&& pageContextDTO.getPartyContextDTO() != null ? pageContextDTO
				.getPartyContextDTO().getPartyOid() : null;
		try {
			List<CoreTransferDto> coreTransferList = pageModel
					.getCoreTransferDto();
			for (CoreTransferDto coreTransferDto : coreTransferList) {

				if (pageModel.getTransferType().equals("1"))
					coreTransferDto.setTransferTypeInd("S");
				else if (pageModel.getTransferType().equals("2"))
					coreTransferDto.setTransferTypeInd("C");
				coreTransferDto.setRequester(userId);
				coreTransferDto.setRowStatus(true);
				getGUIController().raiseCoreTransferRequest(userProfile,
						partyOid, coreTransferDto,
						getAllowableRequestsForContractTransfer());
			}

			List<CoreTransferDto> segList = pageModel.getSegTransferDto();
			List<CoreTransferDto> coreTransferList1 = pageModel
					.getCoreTransferDto();
			coreTransferList1.removeAll(coreTransferList1);
			int count = 1;
			for (CoreTransferDto dto : segList) {
				pageModel.getCoreTransferDto().add(dto);
				if (count++ >= CoreHelper.pageSize)
					break;
			}
			segList.removeAll(coreTransferList1);
			new CoreHelper().validateSegmentGrid(pageModel, this.getFeedbackPanel());

			if (count == 1) {
				getSession().info("Record was saved succesfully");
				setResponsePage(new SegmentedTransferPage());
			}

		} catch (ValidationException e) {
			for (String error : e.getErrorMessages()) {
				getFeedbackPanel().error(error);
			}
			setResponsePage(new SegmentedTransferPage());
		} catch (DataNotFoundException e) {
			getFeedbackPanel().error(
					"Data not found when trying to raise a request ");
			getSession().error("Could not raise the request - data not found");
			setResponsePage(new SegmentedTransferPage());
		} catch (RequestException e) {
			// getFeedbackPanel().error("Could not raise the request");
			getSession().error("Could not raise the request " + e.getMessage());
			setResponsePage(new SegmentedTransferPage());
		}

	}

	private Set<RequestKindType> getAllowableRequestsForContractTransfer() {
		Set<RequestKindType> ret = new HashSet<RequestKindType>();

		ret.add(RequestKindType.ProcessSegmentedContractTransfer);
		return ret;
	}
}
