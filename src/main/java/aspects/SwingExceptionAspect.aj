/**
 * 
 */
package aspects;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import swing_ui.CentralExceptionReporter;
import swing_ui.ExceptionReportingSwingAction;

/**
 * @author Siamak Haschemi sd&amp;m AG, 2007
 * 
 */
public aspect SwingExceptionAspect {
	pointcut op(ActionEvent ev) : execution(void AbstractAction+.actionPerformed(ActionEvent)) 
		&& args(ev) && !within(ExceptionReportingSwingAction+);

	void around(ActionEvent ev) : op(ev) {		
		try {
			proceed(ev);
		} catch (Exception exception) {
			CentralExceptionReporter.reportException(ev, exception);
		}
	}
}