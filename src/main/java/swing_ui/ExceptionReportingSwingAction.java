package swing_ui;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;

/**An action for Swing, which is allowed to throw any Exception.
 * If an exception occurs during execution of the method {@link #actionPerformedWithThrows(ActionEvent)}, 
 * it will be reported to a new modal dialog blocking the causing UI component for further input, until the dialog is closed.
 * @author Christoph Knabe 2007-06-06
 */
public abstract class ExceptionReportingSwingAction extends AbstractAction {
	

	/**The baseName for locating the exception message text resource bundle.*/ 
	public static final String BASE_NAME = "MessageResources";
	
	/**Initializes an ExceptionReportingSwingAction.
	 * @param name how to name the button or menu item, which is associated with the action.
	*/
	public ExceptionReportingSwingAction(final String name){
		super(name);
	}

	public void actionPerformed(final ActionEvent ev) {
	    try{
	    	actionPerformedWithThrows(ev);
	    }catch(Exception ex){
	    	CentralExceptionReporter.reportException(ev, ex);
		}
	}
	
	/**This method is to be implemented by client actions. It may throw any Exception, 
	 * which will then be reported by MulTEx services.
	*/
	public abstract void actionPerformedWithThrows(final ActionEvent ev) throws Exception;

	
}
