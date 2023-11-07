package nl.microsoft.adalauth;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

import javax.naming.ServiceUnavailableException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Author: Chris Vugrinec
*/

public class AzureADRealm { 


    // "https://login.windows.net/common";

    // Application ID
    private final static String CLIENT_ID = "2a9f1f97-3fef-4be3-88c1-e64eb5e053db";

    // Tenant ID
    private final static String DIRECTORY_ID = "7f409ca5-62d4-414e-bffb-2858fbfdd8ff";

    private final static String AUTHORITY = "https://login.microsoftonline.com/" + DIRECTORY_ID + "/";
    // "https://login.windows.net";


    private final static String TOKEN_URL = "https://graph.windows.net";
    
    private final static Logger logger = Logger.getLogger(AzureADRealm.class.getName());

    private boolean validate(String username, char[] password) {
        String pass = String.valueOf(password);

        try {
            AuthenticationResult result = getAccessTokenFromUserCredentials(username, pass);
            logger.log(Level.INFO, "Access Token - {0}", result.getAccessToken());
            logger.log(Level.INFO, "Refresh Token - {0}", result.getRefreshToken());
            logger.log(Level.INFO, "ID Token - {0}", result.getIdToken());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Login failed ", ex);
            return false;
        }
        logger.log(Level.INFO, "Username : {0}\tPASSWORD : {1}", new Object[]{username, pass});
        return true;

    }

    protected static AuthenticationResult getAccessTokenFromUserCredentials(String username, String password) throws Exception {
        
        AuthenticationContext context = null;
        AuthenticationResult result = null;
        ExecutorService service = null;
        
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(AUTHORITY, false, service);
            Future<AuthenticationResult> future = context.acquireToken(TOKEN_URL, CLIENT_ID, username, password, null);
            result = future.get();
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new ServiceUnavailableException("authentication result was null");
        }
        return result;
    }

}
