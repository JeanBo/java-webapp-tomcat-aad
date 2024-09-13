package za.co.liberty.agreement.common.exceptions;

import za.co.liberty.common.logging.HeaderVO;
import za.co.liberty.exceptions.ChainDetailI;

/**
 * @author MDL2506
 * @version %I
 *
 * State the object of this class
 */
/* Change Log
 * Date			Name				Description
 * 2003-03-19	Martin Vojtko		'Moved' into the chained exception hierarchy
 * @modelguid {DC9D918C-D415-4D15-97BF-3861231702DA}
 */
 
public class ProductNotFoundException extends SpecNotFoundException {
    /**
	 * @see ApplicationException#ApplicationException()
	 * @modelguid {42289A1A-18B7-4972-A372-B330C951A7B4}
	 */
    public ProductNotFoundException() {
        super();
    }

	/**
	 * @see ApplicationException#ApplicationException(ChainDetailI)
	 * @modelguid {F4EECD95-4112-4B73-AC82-C56CA58AFE1A}
	 */
   public ProductNotFoundException(ChainDetailI chainDetail) {
		super(chainDetail);
	}

    /**
	 * @see ApplicationException#ApplicationException(contextDependentMessage, descriptionCodeID, severityID)
	 * @modelguid {7BB38A38-6916-4738-BAC4-57A4B4C1DE3E}
	 */
    public ProductNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID) {
        super(contextDependentMessage, descriptionCodeID, severityID);
    }
    
	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID, severityID, logHeader)
	 */
//    public ProductNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID, HeaderVO logHeader) {
//        super(contextDependentMessage, descriptionCodeID, severityID, logHeader);
//    }
       
   	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID)
	 * @modelguid {112B8479-9AD7-4A0C-81B2-B0BE64D29CDD}
	 */
    public ProductNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID, Throwable caughtException) {
        super(contextDependentMessage, descriptionCodeID, severityID, caughtException);
    }
    
	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID, logHeader)
	 */
//    public ProductNotFoundException(String contextDependentMessage, int descriptionCodeID, int severityID, Throwable caughtException, HeaderVO logHeader) {
//        super(contextDependentMessage, descriptionCodeID, severityID, caughtException, logHeader);
//    }
}
