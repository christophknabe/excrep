package struts_ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import lg.Session;
import lg.SessionImpl;
import multex.Exc;

/**
 * Contains services common to several Struts actions.
 * 
 * @author Christoph Knabe 2007-05-22
 *
 */
class StrutsUtil {


	static private final String SESSION_KEY = Session.class.getName();
	
	/**Returns the actual Session object from the HttpSession of the request.
	 * If there is not yet a Session object in the HttpSession, a new one will be created under the name SESSION_KEY.
	 */
	/*package*/ static Session getOrCreateLgSession(
		final HttpServletRequest request
	){
	    final HttpSession sessionMut = request.getSession();
		final Session testResult = (Session)sessionMut.getAttribute(StrutsUtil.SESSION_KEY );
		if(testResult!=null){return testResult;}
		//Here we do not yet have a LgSession
		final Session newResult = new SessionImpl();
		sessionMut.setAttribute(StrutsUtil.SESSION_KEY, newResult);
		return newResult;
	}

	/**Returns a forward to the JSP page specified as input for this action.
	 * @param actionMapping where to search for a local forward
	 * @return the not-null forward
	 * @throws Exc There is no input page defined in this action
	 */
	/*package*/ static ActionForward getInputForward(final ActionMapping actionMapping) throws Exc {
		final String input = actionMapping.getInput();
		if(input==null){
			throw new Exc("No input page (necessary for forwarding) specified in action {0}", actionMapping);
		}
		return actionMapping.getInputForward();
	}

	/**Returns the local or global forward with the given name.
	 * This method is necessary, in order not to return null. 
	 * A null result would be understood by Struts, that the request was already served by this class.
	 * As a consequence, the user would eternally wait for an answer.
	 * @param actionMapping where to search for a local forward
	 * @param forwardName The name of the forward to find.
	 * @return the not-null forward
	 * @throws Exc the searched forward was neither locally for this action nor globally defined.
	 */
	/*package*/ static ActionForward findForward(final ActionMapping actionMapping, final String forwardName) throws Exc {
		final ActionForward result = actionMapping.findForward(forwardName);
		if(null==result){
			throw new Exc("Missing forward \"{0}\" in action {1}", forwardName, actionMapping);
		}
		return result;
	}

}
