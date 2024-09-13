package za.co.liberty.business.request.tree;

import java.util.Collection;
import java.util.Map;

import za.co.liberty.account.domain.vo.AccountEntryVO;
import za.co.liberty.account.domain.vo.AccountVO;
import za.co.liberty.agreement.client.vo.RequestVO;
import za.co.liberty.agreement.client.vo.RequestVOImpl;
import za.co.liberty.agreement.common.AgreementObjectReference;
import za.co.liberty.common.domain.ApplicationContext;
import za.co.liberty.common.domain.ObjectReference;
import za.co.liberty.ftx.domain.vo.ParticularMoneyProvisionVO;

import javax.ejb.CreateException;

public class IntermediaryManager implements IIntermediaryManager{

	@Override
	public ParticularMoneyProvisionVO getMoneyProvision(ApplicationContext arg0, ObjectReference arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getPaymentHierarchies(ApplicationContext arg0, ObjectReference arg1) throws CreateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getPaymentsByReferences(ApplicationContext arg0, Collection arg1, int arg2)
			throws CreateException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Only gets called from one function for SRS tree properties view, this is the first call for the 
	 * Root RequestVO
	 */
	@Override
	public RequestVO getRequest(ApplicationContext arg0, ObjectReference arg1) {
		RequestVO r = new RequestVOImpl(arg1.getTypeOid());
//		if (arg1.getObjectOid() == 200L) {
//			new RequestEnquer
//		}
		r.setTargetActual(new AgreementObjectReference(0,101,10101));
		r.setRequestDate(new java.sql.Date(System.currentTimeMillis()));
		r.setExecutedDate(new java.sql.Date(System.currentTimeMillis()));
		r.setRequestedDate(new java.sql.Date(System.currentTimeMillis()));
	
		
		return r;
	}

	@Override
	public RequestVO[] getRequestChildren(ApplicationContext arg0, RequestVO arg1) {
	
		return new RequestVO[0];
	}

	@Override
	public Collection getRequests(ApplicationContext arg0, Collection arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeDescription(long arg0) {
		return "TypeDesc " + arg0;
	}

	@Override
	public AccountEntryVO resolveAccountEntryReference(ApplicationContext arg0, ObjectReference arg1)
			throws CreateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection resolveAccountEntryReferences(ApplicationContext arg0, Collection arg1) throws CreateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountVO resolveAccountReference(ApplicationContext arg0, ObjectReference arg1) throws CreateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection resolveMoneyProvisionElementReferences(ApplicationContext arg0, Collection arg1)
			throws CreateException {
		// TODO Auto-generated method stub
		return null;
	}

}
