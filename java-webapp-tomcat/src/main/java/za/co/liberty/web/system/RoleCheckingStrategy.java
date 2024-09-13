package za.co.liberty.web.system;

import java.util.Iterator;

import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

public class RoleCheckingStrategy implements IRoleCheckingStrategy {

	public boolean hasAnyRole(Roles roles) {
		for (Iterator iter = roles.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			if(SRSAuthWebSession.get().isUserInRole(element))
				return true;
		}
		return false;
	}

}
