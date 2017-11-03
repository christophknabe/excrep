package fx_ui;

import javafx.application.Application;
import javafx.stage.Stage;
import lg.Session;
import lg.SessionImpl;

/**Starts a JavaFX application for management of Clients.
 * @author Christoph Knabe
 * @since 2016-09-20
 */
public class ClientFxApplication extends Application {

    public static void main(final String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            multex.Msg.printReport(e);
        }
    }

    /**Reference to a business logic session.*/
    protected final Session session = new SessionImpl();

    @Override
    public void start(final Stage primaryStage) {
        final ClientTableUI clientTableUI = new ClientTableUI(session);
        primaryStage.setScene(clientTableUI.scene);
        primaryStage.setTitle(clientTableUI.title);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
