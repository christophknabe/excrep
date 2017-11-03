package fx_ui;

import db.Persistence;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lg.Client;
import lg.Session;
import multex.Failure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

/**A dialog for creating/editing a Client, implemented as a Stage. Uses a JavaFX UI.
 * @author Christoph Knabe
 * @since 2016-08-17
 */
public class ClientEditUI {


    private static final Logger logger = Logger.getLogger(ClientEditUI.class.getName());
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**Reference to the owner UI of this UI*/
    private final ClientTableUI owner;

    /**Reference to a business logic session.*/
    protected final Session session;

    /**Will display, what the current dialog will do.*/
    private final Text heading = new Text();

    /**Will display the ID of the currently displayed Client object.*/
    private final TextField id = new TextField();
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField birthDate = new TextField();
    private final TextField phone = new TextField();

    final BorderPane mainPane = new BorderPane();
    private final Scene scene = new Scene(mainPane);
    private final Stage stage = new Stage();

    /**Displays a Client Creation UI.
     * @see #ClientEditUI(ClientTableUI, Session, Client)
     */
    public ClientEditUI(@Nonnull final ClientTableUI owner, @Nonnull final Session session) {
        this(owner, session, null);
    }

    /**Displays a Client Create/Edit UI.
     * @param owner The UI which owns this modal UI.
     * @param session The business logic session to be used.
     * @param client The Client object to be edited. Pass null, if a new Client object must be created by the UI.
     **/
    public ClientEditUI(@Nonnull final ClientTableUI owner, @Nonnull final Session session, @Nullable final Client client) {
        //Layout:

        logger.info("");
        this.owner = owner;
        this.session = session;

        //Configure the heading:
        heading.setFont(new Font(20));
        final HBox headingPane = _createHBox(heading);

        //Create the client data pane:
        final HBox idPane = _createTextFieldHBox("ID", id, Optional.empty());
        final HBox firstNamePane = _createTextFieldHBox("First Name", firstName, Optional.of("Enter the client's first name here."));
        final HBox lastNamePane = _createTextFieldHBox("Last Name", lastName, Optional.of("Enter the client's last name here."));
        final HBox phonePane = _createTextFieldHBox("Phone Number", phone, Optional.of("Enter the client's phone number here."));
        final HBox birthDatePane = _createTextFieldHBox("Birth Date", birthDate, Optional.of("YYYY-MM-DD"));
        final VBox dataPane = new VBox(10, idPane, firstNamePane, lastNamePane, phonePane, birthDatePane);

        //Configure the buttons:
        final Button saveButton = _createButton(saveAction);
        final Button clearButton = _createButton(clearAction);
        final Button deleteButton = _createButton(deleteAction);
        final Button listButton = _createButton(closeAction);

        //Add the buttons into the right side into a layout pane:
        final Region spacer = new Region();
        final HBox buttonPane = _createHBox(spacer, saveButton, clearButton, deleteButton, listButton);
        HBox.setHgrow(spacer, Priority.ALWAYS);


        mainPane.setTop(headingPane);
        mainPane.setCenter(dataPane);
        mainPane.setBottom(buttonPane);
        if(client==null){
            _clearFields();
        }else{
            _copyFields(client);
        }
        _showModal(stage);
    }

    /**Creates a horizontal box with the given nodes, and spacing and insets of 10.*/
    private HBox _createHBox(final Node... nodes) {
        final HBox result = new HBox(10, nodes);
        result.setPadding(new Insets(10));
        return result;
    }

    /**Creates an HBox with a Label and a TextField. The prompt is displayed as light gray help text inside the TextField, as long as it is empty.
     * If no prompt is passed, the TextField will not be editable.*/
    private HBox _createTextFieldHBox(final String labelText, final TextField textField, final Optional<String> prompt) {
        final Label label = new Label(labelText + ":");
        label.setPrefWidth(110);
        //Configure the text field:
        textField.setPrefColumnCount(20);
        textField.setMaxWidth(Double.MAX_VALUE);
        if(prompt.isPresent()){
            textField.setPromptText(prompt.get());
        }else {
            textField.setDisable(true);
        }
        return _createHBox(label, textField);
    }

    private Button _createButton(final ExceptionReportingFxAction action){
        final Button result = new Button(action.name);
        result.setPrefWidth(80);
        result.setOnAction(action);
        return result;
    }

    /**Clears all fields in order to create a new Client.*/
    private void _clearFields() {
        setInternalId(Persistence.INEXISTENT_ID);
        firstName.setText("");
        lastName.setText("");
        birthDate.setText("");
        phone.setText("");
    }

    /**Sets all fields to a value taken from the corresponding field in the Client in order to edit it.*/
    private void _copyFields(final Client c) {
        setInternalId(c.getId());
        firstName.setText(c.getFirstName());
        lastName.setText(c.getLastName());
        setInternalBirthDate(c.getBirthDate());
        phone.setText(c.getPhone());
    }

    /**Display the scene onto the stage as a modal dialog.*/
    private void _showModal(final Stage stage) {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Single Client");
        stage.setScene(scene);
        stage.showAndWait();
    }

    private final ExceptionReportingFxAction saveAction = new ExceptionReportingFxAction("Save", ev -> {
        System.out.println("Save ...");
        final long id = getInternalId();
        try {
            if(id==Persistence.INEXISTENT_ID){
                final Client client = getSession().createClient(firstName.getText(), lastName.getText(), getInternalBirthDate(), phone.getText());
                setInternalId(client.getId());
            }else{
                final Client client = getSession().findClient(id);
                client.setAttributes(firstName.getText(), lastName.getText(), getInternalBirthDate(), phone.getText());
            }
            getSession().commit();
            getOwner().reload();
        } catch (Exception e) {
            throw new Failure("Cannot save client {0}", e, lastName.getText());
        }
    });

	private ClientTableUI getOwner() {
		return ClientEditUI.this.owner;
	}

	private Session getSession() {
		return this.session;
	}

    private final ExceptionReportingFxAction clearAction = new ExceptionReportingFxAction("Clear", ev -> {
        System.out.println("Clear ...");
        _clearFields();
    });

    private final ExceptionReportingFxAction deleteAction = new ExceptionReportingFxAction("Delete", ev -> {
        System.out.println("Delete ...");
        final Client client = getSession().findClient(getInternalId());
        getSession().delete(client);
        getSession().commit();
        getOwner().reload();
        stage.close();
    });

    private final ExceptionReportingFxAction closeAction = new ExceptionReportingFxAction("Close", ev -> {
        System.out.println("Close ...");
        stage.close();
    });

    //Conversion methods to/from internal types:

    private long getInternalId(){
        return this.id.getText().length()==0 ? 0 : Long.parseLong(this.id.getText());
    }
    private void setInternalId(final long id){
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
    private Date getInternalBirthDate() throws ParseException {
        return df.parse(this.birthDate.getText());
    }
    private void setInternalBirthDate(final Date internalBirthDate) {
        this.birthDate.setText(df.format(internalBirthDate));
    }


}
