package nl.microsoft.adalauth;


import java.util.Map;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.microsoft.aad.adal4j.AuthenticationResult;
import nl.microsoft.adalauth.jaas.RolePrincipal;
import nl.microsoft.adalauth.jaas.UserPrincipal;


/*
    Author: Chris Vugrinec
*/
public class AzureADLoginModule implements LoginModule {

    private final static Logger logger = Logger.getLogger(AzureADLoginModule.class.getName());
    private CallbackHandler handler;
    private Subject subject;
    private UserPrincipal userPrincipal;
    private RolePrincipal rolePrincipal;
    private String login;
    private List<String> userGroups;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.handler = callbackHandler;
        this.subject = subject;
    }

    @Override
    public boolean login() throws LoginException {
        
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("login");
        callbacks[1] = new PasswordCallback("password", true);
        boolean result = false;

        try {
            handler.handle(callbacks);
        }catch (IOException e) {
            throw new LoginException(e.getMessage());
        } catch (UnsupportedCallbackException ex) { 
            Logger.getLogger(AzureADLoginModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        login = ((NameCallback) callbacks[0]).getName();
        String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
        
        logger.log(Level.INFO, "Trying to login user: {0} with password {1}",new String[]{login,password});
        AuthenticationResult aResult = null;

        try {
            aResult = AzureADRealm.getAccessTokenFromUserCredentials(login, password);
            logger.log(Level.INFO, "Access Token - {0}", aResult.getAccessToken());
            logger.log(Level.INFO, "Refresh Token - {0}", aResult.getRefreshToken());
            logger.log(Level.INFO, "ID Token - {0}", aResult.getIdToken());
            logger.log(Level.INFO, "Login succeeded for username - {0}", login);
            result = true;
        } catch (Exception ex) {
            logger.log(Level.INFO, "Login failed for user: - {0}", login);
            //  Result keeps initial value which is false
            return result;
        }
        //  If it gets here, then it's OK
        return result;
    }

    @Override
    public boolean commit() throws LoginException {

        userPrincipal = new UserPrincipal(login);
        subject.getPrincipals().add(userPrincipal);
        
        //  Setting the demo role needed for this demo
        //  as it maps with the demo role, defined in web.xml 
        //  in the security constraint
        rolePrincipal = new RolePrincipal("demo");
        subject.getPrincipals().add(rolePrincipal);

        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(userPrincipal);
        subject.getPrincipals().remove(rolePrincipal);
        return true;
    }

}
