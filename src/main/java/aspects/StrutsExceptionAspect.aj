/**
 * 
 */
package aspects;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;

import struts_ui.CentralExceptionReporter;


/**
 * @author Siamak Haschemi sd&amp;m AG, 2007
 * 
 */
public aspect StrutsExceptionAspect {
    

    private static final ExceptionConfig exceptionConfig = new ExceptionConfig();
    static { //As I cannot control the number of Aspect objects, I use a static variable exceptionConfig
        exceptionConfig.setScope(CentralExceptionReporter.ASPECT);
        exceptionConfig.setKey(struts_ui.CentralExceptionReporter.class.getName() + ".inPageErrorMessage");
        //exceptionConfig.setHandler(struts_ui.CentralExceptionReporter.class.getName());
    }
    
	private CentralExceptionReporter centralExceptionReporter = new CentralExceptionReporter();

	pointcut op(ActionMapping am, ActionForm af, HttpServletRequest req,
			HttpServletResponse res) : execution(ActionForward org.apache.struts.actions.DispatchAction+.*(..)) 
			&& args(am, af, req, res);

	Object around(ActionMapping am, ActionForm af, HttpServletRequest req,
			HttpServletResponse res) : op(am, af, req, res) {
		try {
			return proceed(am, af, req, res);
		} catch (Exception exception) {
			try {
				return centralExceptionReporter.execute(exception,
						exceptionConfig, am, af, req, res);
			} catch (ServletException servletException) {
				throw new RuntimeException(servletException);
			}
		}
	}
}
