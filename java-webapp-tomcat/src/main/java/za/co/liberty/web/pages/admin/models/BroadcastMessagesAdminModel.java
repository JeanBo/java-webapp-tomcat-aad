package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.persistence.srs.entity.BroadcastMessagesEntity;

/**
 * Model for broadcast messages
 * 
 * @author jzb0608
 *
 */
public class BroadcastMessagesAdminModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long agreementNr;
	private Long partyOid;
	private List<BroadcastMessagesEntity> messagesList;
	
	public Long getAgreementNr() {
		return agreementNr;
	}
	public void setAgreementNr(Long agreementNr) {
		this.agreementNr = agreementNr;
	}
	public Long getPartyOid() {
		return partyOid;
	}
	public void setPartyOid(Long partyOid) {
		this.partyOid = partyOid;
	}
	public List<BroadcastMessagesEntity> getMessagesList() {
		return messagesList;
	}
	public void setMessagesList(List<BroadcastMessagesEntity> messagesList) {
		this.messagesList = messagesList;
	}
	
	
}
