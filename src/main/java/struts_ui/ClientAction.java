package struts_ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lg.Client;
import lg.Session;
import multex.Exc;
import multex.Failure;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import db.Persistence;


/** This class contains all actions, that can be done with a Client.
 * 
 * @author Christoph Knabe 2007-05-22
 */
public class ClientAction extends DispatchAction {
	
	
    public ActionForward Save(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest request, final HttpServletResponse arg3) throws Exception {
		final ClientForm form = (ClientForm)actionForm;
		final Session session = StrutsUtil.getOrCreateLgSession(request);
		final long id = form.getInternalId();
		try {
			if(id==Persistence.INEXISTENT_ID){
				final Client client = session.createClient(form.getFirstName(), form.getLastName(), form.getInternalBirthDate(), form.getPhone());
				form.setInternalId(client.getId());			
			}else{
				final Client client = session.findClient(id);
				client.setAttributes(form.getFirstName(), form.getLastName(), form.getInternalBirthDate(), form.getPhone());
			}
			session.commit();
		} catch (Exception e) {
			throw new Failure("Cannot save client {0}", e, form.getLastName());
		}
		return StrutsUtil.getInputForward(actionMapping);
	}

	public ActionForward Reset(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest request, final HttpServletResponse arg3) throws Exception {
		final ClientForm form = (ClientForm)actionForm;
		form.setId(null);
		form.setFirstName(null);
		form.setLastName(null);
		form.setBirthDate(null);
		form.setPhone(null);
		return StrutsUtil.getInputForward(actionMapping);
	}
	
	public ActionForward Delete(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest request, final HttpServletResponse arg3) throws Exception {
		final ClientForm form = (ClientForm)actionForm;
		final Session session = StrutsUtil.getOrCreateLgSession(request);
		final Client client = session.findClient(form.getInternalId());
		session.delete(client);
		session.commit();
		return StrutsUtil.findForward(actionMapping, "list");
	}

	public ActionForward List(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest request, final HttpServletResponse arg3) throws Exception {
		return StrutsUtil.findForward(actionMapping, "list");
	}	

	public ActionForward edit(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest request, final HttpServletResponse arg3) throws Exception {
		final ClientForm form = (ClientForm)actionForm;
		final Session session = StrutsUtil.getOrCreateLgSession(request);
		final long id = Long.parseLong(request.getParameter("id"));
		final Client c = session.findClient(id);
		form.setFirstName(c.getFirstName());
		form.setLastName(c.getLastName());
		form.setInternalBirthDate(c.getBirthDate());
		form.setPhone(c.getPhone());
		return StrutsUtil.getInputForward(actionMapping);
	}

    /** Will be called, if the client form shall be displayed emptily, in order to create a new Client*/
    public ActionForward create(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest arg2, final HttpServletResponse arg3) throws Exception {
        return StrutsUtil.getInputForward(actionMapping);
    }
    
    /** Will be called, if the client form shall be displayed emptily, in order to create a new Client*/
    public ActionForward Throw(final ActionMapping actionMapping, final ActionForm actionForm, final HttpServletRequest arg2, final HttpServletResponse arg3) throws Exception {
        throw new Exc("Das ist ein Demo-Fehler ohne Ursache");
    }
	
	

}
