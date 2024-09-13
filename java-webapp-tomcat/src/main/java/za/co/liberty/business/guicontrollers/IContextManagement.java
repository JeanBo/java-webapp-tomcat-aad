package za.co.liberty.business.guicontrollers;

import java.util.List;

import javax.ejb.Local;

import za.co.liberty.dto.contracting.ResultAgreementDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.gui.context.ContextSearchModelDTO;
import za.co.liberty.dto.gui.context.ContextSearchOptionsDTO;
import za.co.liberty.dto.gui.context.IContextSearchType;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.dto.gui.context.ResultContextSearchDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;

/**
 * <p>Management bean of User Context related functions.</p>
 * 
 * <p>The context relates to a page (that requires it) and depends on 
 * an Agreement and/or Party to be selected. The context displays
 * configured properties about the currently selected records.</p>
 * 
 * @author jzb0608 - 27 Jun 2008
 *
 */
@Local
public interface IContextManagement {
	
	/**
	 * Get the context for the context item
	 * 
	 * @param resultContextItemDTO
	 * @return
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultContextItemDTO resultContextItemDTO) throws CommunicationException;
	
	/**
	 * Get the context for party only.
	 * 
	 * @param resultPartyDTO
	 * @return 
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultPartyDTO resultPartyDTO) throws CommunicationException;
	
	/**
	 * Get the context for agreement only.
	 * 
	 * @param resultAgreementDTO
	 * @return
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultAgreementDTO resultAgreementDTO) throws CommunicationException;
	
	/**
	 * Get the context for both party and agreement.
	 * 
	 * @param resultPartyDTO
	 * @param resultAgreementDTO
	 * @return
	 * @throws CommunicationException 
	 */
	public ContextDTO getContext(ResultPartyDTO resultPartyDTO,
			ResultAgreementDTO resultAgreementDTO) throws CommunicationException;
	
	/**
	 * Do a context search.  Results will be limited to the number indicated
	 * in the options.  The {@link #next()} method can be called to retrieve
	 * the next group of records (if there are any).
	 * 
	 * @param sessionUser
	 * @param searchType
	 * @param value must be an instance of {@link IContextSearchType#getValueClassType())
	 * 		the relevant search type.
	 * @param options defines the number and type of records returned
	 * 	
	 * @return
	 * @throws CommunicationException
	 */
	public ResultContextSearchDTO searchForContext(
			ISessionUserProfile sessionUser,
			IContextSearchType searchType,
			Object value, ContextSearchOptionsDTO options) 
				throws CommunicationException;
	
	/**
	 * Call this function to retrieve the next group of results.  The number of 
	 * records returned will depend on the original number 
	 * 
	 * @param sessionUser
	 * @param beanSearchModel
	 * @return
	 * @throws CommunicationException
	 */
	public ResultContextSearchDTO next(ISessionUserProfile sessionUser,
			ContextSearchModelDTO beanSearchModel) throws CommunicationException;
	
	/**
	 * Gets a List of ServicedBy Or managedBY advisors list for logged in User
	 * @return List of agreement numbers.
	 * @throws DataNotFoundException
	 */
	public List<Long> getServicedAndManagedByAdvisorList() throws CommunicationException;
	
	/**
	 * Gets the ResultAgreementDTO for a given agreement number
	 * @param srsAgreementNr
	 * @return
	 * @throws DataNotFoundException
	 * @throws CommunicationException
	 */
	public ResultAgreementDTO findAgreementWithSRSAgreementNr(
			long srsAgreementNr) throws 
			CommunicationException,DataNotFoundException;
	
	/**
	 * Gets the ResultPartyDTO list for a given UACF id
	 * @param uacfID
	 * @return List<ResultPartyDTO>
	 * @throws CommunicationException
	 * @throws DataNotFoundException
	 */
	public List<ResultPartyDTO> findPartyWithUacfID(String uacfID) throws CommunicationException, DataNotFoundException;
	
}