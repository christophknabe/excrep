package struts_ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

public class ActionPlugIn implements PlugIn {

	/**Will be called when the Struts action servlet is initialized. */
	public void init(final ActionServlet servlet, final ModuleConfig config) /*throws javax.servlet.ServletException*/ {
		//This will initialize the logging framework as a side effect. Thus avoiding some errors with WebApp reloading.
		Logger.getLogger("").setLevel(Level.FINEST);
	}
	
	public void destroy(){
		//Finalization here not necessary
	}
	
}
