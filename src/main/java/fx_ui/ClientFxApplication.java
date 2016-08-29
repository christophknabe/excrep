package fx_ui;

import db.Persistence;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import lg.Client;
import lg.Session;
import lg.SessionImpl;
import multex.Failure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**An application for management of clients. Uses a JavaFX UI.
 * @author Christoph Knabe
 * @since 2016-08-17
 */
public class ClientFxApplication extends Application {


    private static final Logger logger = Logger.getLogger(ClientFxApplication.class.getName());
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(final String[] args) {
        launch(args);
    }

    /**Reference to a business logic session.*/
    protected final Session session = new SessionImpl();

    private final String firstNameTitle = "First Name";
    private final String lastNameTitle = "Last Name";
    private final String birthDateTitle = "Birth Date";
    private final String phoneTitle = "Phone Number";

    /**Will display, what the current dialog will do.*/
    private final Text heading = new Text();

    /**Will display the ID of the currently displayed Client object.*/
    private final Label id = new Label();
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField birthDate = new TextField();
    private final TextField phone = new TextField();

    private final Scene scene;

    /**Initializes the Client Management Application.*/
    public ClientFxApplication() {
        //Layout:

        logger.info("");

        //Configure the heading:
        heading.setFont(new Font(20));
        final HBox headingPane = _createHBox(heading);

        //Create the client data pane:
        final HBox firstNamePane = _createTextFieldHBox(firstNameTitle, firstName, "Enter the client's first name here.");
        final HBox lastNamePane = _createTextFieldHBox(lastNameTitle, lastName, "Enter the client's last name here.");
        final HBox phonePane = _createTextFieldHBox(phoneTitle, phone, "Enter the client's phone number here.");
        final HBox birthDatePane = _createTextFieldHBox(birthDateTitle, birthDate, "Enter the client's birth date here.");
        final VBox dataPane = new VBox(10, firstNamePane, lastNamePane, phonePane, birthDatePane);

        //Configure the buttons:
        final Button saveButton = _createButton(saveAction);
        final Button resetButton = _createButton(resetAction);
        final Button deleteButton = _createButton(deleteAction);
        final Button listButton = _createButton(listAction);

        //Add the buttons into the right side into a layout pane:
        final Region spacer = new Region();
        final HBox buttonPane = _createHBox(spacer, saveButton, resetButton, deleteButton, listButton);
        buttonPane.setHgrow(spacer, Priority.ALWAYS);


        final BorderPane mainPane = new BorderPane();
        mainPane.setTop(headingPane);
        mainPane.setCenter(dataPane);
        mainPane.setBottom(buttonPane);
        //Add the layout pane to a scene
        this.scene = new Scene(mainPane);
        reset();
    }

    /**The stage showing a list of clients.*/
    private final ClientListStage listStage = new ClientListStage(this, session);

    /*package*/ Window getEditWindow() {
        return scene.getWindow();
    }

    private HBox _createTextFieldHBox(final String labelText, final TextField textField, final String prompt) {
        final Label label = new Label(labelText + ":");
        label.setPrefWidth(110);
        //Configure the text field:
        textField.setPrefColumnCount(20);
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setPromptText(prompt);
        return _createHBox(label, textField);
    }

    private Button _createButton(final ExceptionReportingFxAction action){
        final Button result = new Button(action.name);
        result.setPrefWidth(80);
        result.setOnAction(action);
        return result;
    }

    /**Creates a horizontal box with the given nodes, and spacing and insets of 10.*/
    private HBox _createHBox(final Node... nodes) {
        final HBox result = new HBox(10, nodes);
        result.setPadding(new Insets(10));
        return result;
    }

    final ExceptionReportingFxAction saveAction = new ExceptionReportingFxAction("Save", ev -> {
        System.out.println("Save ...");
        final long id = getInternalId();
        try {
            if(id==Persistence.INEXISTENT_ID){
                final Client client = session.createClient(firstName.getText(), lastName.getText(), getInternalBirthDate(), phone.getText());
                setInternalId(client.getId());
            }else{
                final Client client = session.findClient(id);
                client.setAttributes(firstName.getText(), lastName.getText(), getInternalBirthDate(), phone.getText());
            }
            session.commit();
        } catch (Exception e) {
            throw new Failure("Cannot save client {0}", e, lastName.getText());
        }
    });

    final ExceptionReportingFxAction resetAction = new ExceptionReportingFxAction("Reset", ev -> {
        System.out.println("Reset ...");
        reset();
    });

    final ExceptionReportingFxAction deleteAction = new ExceptionReportingFxAction("Delete", ev -> {
        System.out.println("Delete ...");
        final Client client = session.findClient(getInternalId());
        session.delete(client);
        session.commit();
        /* TODO deleteAction implementieren! 16-08-19
            listStage.reload();
            //SwingUtil.show(listStage);
            listStage.pack();
            listStage.setVisible(true);
        */
    });

    final ExceptionReportingFxAction listAction = new ExceptionReportingFxAction("List", ev -> {
        System.out.println("List ...");
        listStage.reload();
        /*TODO listAction implementieren! 16-08-19
        listStage.pack();
        listStage.setVisible(true);
        */
    });

    @Override
    public void start(final Stage primaryStage) {
        //Finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Client Management");
        //cancelButton.setOnAction(e -> primaryStage.close());
        primaryStage.show();
    }

    //Conversion methods to/from internal types:

    /*package*/ long getInternalId(){
        return this.id.getText().length()==0 ? 0 : Long.parseLong(this.id.getText());
    }
    /*package*/ void setInternalId(final long id){
        final String action;
        if(id==Persistence.INEXISTENT_ID){
            this.id.setText("");
            action = "Create";
        }else{
            this.id.setText(Long.toString(id));
            action = "Edit";
        }
        heading.setText(action + " Client");
    }
    Date getInternalBirthDate() throws ParseException {
        return df.parse(this.birthDate.getText());
    }
    void setInternalBirthDate(final Date internalBirthDate) {
        this.birthDate.setText(df.format(internalBirthDate));
    }

    /**Sets all fields to the empty string value in order to create a new Client.*/
	/*package*/ void reset() {
        setInternalId(Persistence.INEXISTENT_ID);
        firstName.setText("");
        lastName.setText("");
        birthDate.setText("");
        phone.setText("");
    }


}
