package za.co.liberty.agreement.common.exceptions;

import za.co.liberty.exceptions.application.ObjectNotFoundChainedException;
import za.co.liberty.common.logging.HeaderVO;
import za.co.liberty.exceptions.ChainDetailI;

/**
 * @author MMC0807
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 * @modelguid {4C4A6B26-76B9-443A-83B8-03C30440FD65}
 */
public class SpecNotFoundException extends ObjectNotFoundChainedException {
	/** @modelguid {3F0E2601-2967-4C06-B022-2A2A151C2185} */
	public SpecNotFoundException() {
        super();
    }

	/** @modelguid {9D6ACA20-BC54-4E1F-998C-9105137C7FFA} */
    public SpecNotFoundException(ChainDetailI chainDetail) {
        super(chainDetail);
    }

    /**
	 * @see ApplicationException#ApplicationException(contextDependentMessage, descriptionCodeID, severityID)
	 * @modelguid {3882C895-358B-439B-8B7A-3ACED07F77A3}
	 */
    public SpecNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID) {
        super(contextDependentMessage, descriptionCodeID, severityID);
    }
    
	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID, severityID, logHeader)
	 */
//    public SpecNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID, HeaderVO logHeader) {
//        super(contextDependentMessage, descriptionCodeID, severityID, logHeader);
//    }
       
   	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID)
	 * @modelguid {37B9679D-41AB-4C4E-BECC-2D2C7E130CF0}
	 */
    public SpecNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID, Throwable caughtException) {
        super(contextDependentMessage, descriptionCodeID, severityID, caughtException);
    }
    
	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID, logHeader)
	 */
//    public SpecNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID, Throwable caughtException, HeaderVO logHeader) {
//        super(contextDependentMessage, descriptionCodeID, severityID, caughtException, logHeader);
//    }
}
