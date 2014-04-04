package struts_ui;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lg.Client;
import lg.Session;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;


/** This class contains all actions, that can be done with a Client list. 
 * 
 * @author Christoph Knabe 2007-05-22
 */
public class ClientListAction extends DispatchAction {
	
	
	public ActionForward list(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest request, final HttpServletResponse arg3) throws Exception {
		final Session session = StrutsUtil.getOrCreateLgSession(request);
		final List<Client> clientList = session.searchAllClients();
        request.setAttribute("clientList", clientList);
		return StrutsUtil.getInputForward(actionMapping);
	}	

	public ActionForward delete(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest request, final HttpServletResponse arg3) throws Exception {
		final Session session = StrutsUtil.getOrCreateLgSession(request);
		final long id = Long.parseLong(request.getParameter("id"));
		final Client client = session.findClient(id);
		session.delete(client);
		session.commit();
		return list(actionMapping, actionForm, request, arg3);
	}
	
	
}
