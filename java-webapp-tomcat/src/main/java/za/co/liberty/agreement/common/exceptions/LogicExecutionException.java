package za.co.liberty.agreement.common.exceptions;
 
import java.io.Serializable;
import za.co.liberty.exceptions.ApplicationException;
import za.co.liberty.exceptions.ChainDetailI;
import za.co.liberty.common.logging.HeaderVO;

/**
 * @author MDL2506
 * @version %I
 *
 * State the object of this class
 */
/* Change Log
 * Date			Name				Description
 * 2003-03-19	Martin Vojtko		'Moved' into the chained exception hierarchy
 * @modelguid {9F84081F-CB3C-44FF-A2FE-ACBF7A0EB375}
 */

public class LogicExecutionException extends ApplicationException implements Serializable {

	/**
	 * @see ApplicationException#ApplicationException()
	 * @modelguid {FC89AD6F-25DF-45B6-AB09-3FB936508994}
	 */
	public LogicExecutionException() {
		super();
	}

	/** 
	 * @see ApplicationException#ApplicationException(ChainDetailI)
	 * @modelguid {D94EFD80-CDD1-4176-A137-1085229BA7C0}
	 */
	public LogicExecutionException(ChainDetailI chainDetail) {
		super(chainDetail);
	}

    /**
	 * @modelguid {695F3425-D278-4F01-A5E1-5D7FEC3C2A73}
	 */
    public LogicExecutionException(String contextDependentMessage, int descriptionCodeID, int severityID) {
        super(contextDependentMessage, descriptionCodeID, severityID);
    }
    
	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID, severityID, logHeader)
	 */
//    public LogicExecutionException(String contextDependentMessage, int descriptionCodeID, int severityID, HeaderVO logHeader) {
//        super(contextDependentMessage, descriptionCodeID, severityID, logHeader);
//    }   
   	/**
	 * @modelguid {FE8749C8-CED7-4818-87E0-242A5A95DFA7}
	 */
    public LogicExecutionException(String contextDependentMessage, int descriptionCodeID, int severityID, Throwable caughtException) {
        super(contextDependentMessage, descriptionCodeID, severityID, caughtException);
    }
    
	/**
	 * @see ApplicationException#ApplicationException(caughtException, contextDependentMessage, descriptionCodeID, logHeader)
	 */
//    public LogicExecutionException(String contextDependentMessage, int descriptionCodeID, int severityID, Throwable caughtException, HeaderVO logHeader) {
//        super(contextDependentMessage, descriptionCodeID, severityID, caughtException, logHeader);
//    }
}
