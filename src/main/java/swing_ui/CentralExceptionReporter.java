package swing_ui;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public class CentralExceptionReporter {
	/**The baseName for locating the exception message text resource bundle.*/ 
	public static final String BASE_NAME = "MessageResources";
	
	public static void reportException(final ActionEvent ev, Exception ex) {
		final java.awt.Component sourceComponent = (java.awt.Component)ev.getSource();
		final Locale defaultLocale = Locale.getDefault();
		final ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, defaultLocale);
		multex.Swing.report(sourceComponent, ex, bundle);
	}
}
