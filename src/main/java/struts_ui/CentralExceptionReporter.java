package struts_ui;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import multex.Msg;
import multex.Util;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

/** Service class for central exception reporting in a Struts 1.3.10 environment.
 * You have to specify usage of this class in struts-config.xml as for example:
 * <pre>
    <global-exceptions>
        <exception
            type="java.lang.Exception"
            handler="struts_ui.CentralExceptionReporter"
            key="struts_ui.CentralExceptionReporter.inPageErrorMessage"
        />
    </global-exceptions>
 * </pre>
 *
 * This would send any exceptions occuring during Struts actions to this class'es method
 * {@link #execute(Exception, ExceptionConfig, ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)}. *
 *
 * @author Christoph Knabe 2007-05-04 *
 */
public class CentralExceptionReporter extends ExceptionHandler {

    private static final String _myClassName = CentralExceptionReporter.class.getName();
    
    private static final Logger logger = Logger.getLogger(_myClassName);

    /**Key for storing the exception to be reported in the HttpSession*/
    private static final String EXCEPTION_KEY = _myClassName + ".exception";
    
    /**The way we caught the exception. Either ASPECT meaning caught by AspectJ or something else meaning caught by struts-config.xml.*/
    public static final String ASPECT = "AspectJ";

    /**The baseName for locating the exception message text resource bundle.
     *  Must be the same as in &lt;message-resources parameter="..."/&gt; in struts-config.xml
     *  TODO Extract it from struts-config.xml   2007-05-04
     */
    public static final String BASE_NAME = "MessageResources";

    /** Delegating class in order to make the protected method saveErrors visible */
    public static class ServiceAction extends Action {
        public void mySaveErrors(final HttpServletRequest request, final ActionMessages errors) {
            saveErrors(request, errors);
        }
    }

    private static final ServiceAction serviceAction = new ServiceAction();
    
	public static ResourceBundle getBundle(final String bundleName,
			final HttpServletRequest request) {
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName);
		return resourceBundle;
	}

    /**Returns from the session the last exception, or null.*/
    public static Throwable getException(
        final HttpSession session
    ){
        return (Throwable)session.getAttribute(EXCEPTION_KEY);
    }

    /**Stores the exception throwable in the session for future reporting.*/
    public static void setException(
        final HttpSession session,
        final Throwable throwable
    ){
        session.setAttribute(EXCEPTION_KEY, throwable);
    }

    /** Reports the exception exc.
     * Reports firstly to the input page, if specified in the ActionMapping, by multex.Msg.getMessages.
     * If there is no input page specified, the exception will be stored in the HttpSession and then the request will be forwarded to the error page "/system/errorPage.jsp".
     *
     * All JSPs must have a tag <html:errors/>, so that no exception will be lost.
     * In message text with key "struts_ui.CentralExceptionReporter.inPageErrorMessage" you can formulate the look of the in-page exception message.
     * For example:
     *
     * It should contain a "Details" link to the error page, so that the user can see the stack trace, too.
 */
@Override public ActionForward execute(final Exception ex, final ExceptionConfig ae, final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response)
throws ServletException
{
    //Log exception messages to file:
    final StringBuffer requestString = request.getRequestURL();
    final String query = request.getQueryString();
    if(query!=null){
        requestString.append('?');
        requestString.append(query);
    }
    final StringBuffer logMessage = new StringBuffer("Exception caught by ");
    logMessage.append(ASPECT.equals(ae.getScope()) ? "StrutsExceptionAspect" : "struts-config.xml");
    logMessage.append(". Occured when executing Struts action for request ");
    logMessage.append(requestString);
    logMessage.append("\n");
    final Locale defaultLocale = Locale.getDefault();
    final ResourceBundle logBundle = ResourceBundle.getBundle(BASE_NAME, defaultLocale);
    multex.Msg.printReport(logMessage, ex, logBundle);
    logger.warning(logMessage.toString());

    //Store exception for global error page:
    request.getSession().setAttribute(EXCEPTION_KEY, ex);

    //Store exception messages for Struts display on input page:
    final String contextRelativePath = "/system/errorPage.jsp";
    final String absolutePath = request.getContextPath() + contextRelativePath;
    final ResourceBundle bundle = getRequestLocaleBundle(BASE_NAME, request);
    final ActionMessages errors = new ActionErrors();
    errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
        ae.getKey(),
        getMessagesAsHtml(ex, bundle), absolutePath
    ));
    serviceAction.mySaveErrors(request, errors);
    
    //Forward to messages display:
    if(mapping.getInput()==null){return new ActionForward(contextRelativePath);}
    return mapping.getInputForward();
}

    /**Gets the named resource bundle suitable for the actual Locale selected by the user.
     * @param baseName baseName of the desired ResourceBundle, not null.
     * @param request from here is taken the preferred Locale by request.getLocale().
     *          request must not be null.
     *          If there is no such Locale in the request, the default Locale will be used.
     */
    public static ResourceBundle getRequestLocaleBundle(final String baseName, final HttpServletRequest request){
        final Locale requestLocale = request.getLocale();
        final Locale useLocale = requestLocale!=null ? requestLocale : Locale.getDefault();
        return ResourceBundle.getBundle(baseName, useLocale);
    }


    /**Returns the message chain for throwable in HTML format.
     * That is special characters are HTML-escaped, so that they will display correctly in a browser.
     * @param throwable exception to report along with the chain of its causing exceptions
     * @param bundle bundle to use for localization of the exception messages
     * @return typically some lines containing each the formatted message for an exception in the chain
     */
    public static String getMessagesAsHtml(final Throwable throwable, final ResourceBundle bundle){
        final StringBuffer result = new StringBuffer();
        Msg.printMessages(
            result, throwable, bundle
        );
        toHtml(result);
        return result.toString();
    }

    /**Modifies the plain text in io_buffer, so that it will be correctly
        displayed in a HTML page.
        It does the following substitutions:
        1) The "less than" sign &lt; by its HTML equivalent.
        2) The "greater than" sign &gt; by its HTML equivalent.
        3) The line separator by its HTML equivalent.
    */
    public static void toHtml(final StringBuffer io_buffer){
        _replaceAll(io_buffer, "<", "&lt;");
        _replaceAll(io_buffer, ">", "&gt;");
        _replaceAll(io_buffer, Util.lineSeparator, "<BR>\n");
    }

    private static void _replaceAll(
        final StringBuffer io_buffer,
        final String        i_pattern,
        final String        i_replacement
    ){
        for(int fromIndex=0;;){
            fromIndex = io_buffer.indexOf(i_pattern, fromIndex);
            if(fromIndex<0){break;}
            io_buffer.replace(fromIndex, fromIndex+i_pattern.length(), i_replacement);
            fromIndex += i_replacement.length();
        }
    }


}
