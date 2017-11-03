package fx_ui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.LongStringConverter;
import lg.Client;
import lg.Session;
import multex.Exc;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.logging.Logger;

/**Encapsulates a Scene for management of clients. Uses a JavaFX UI.
 * This class displays a table view of all registered clients with buttons for creating, editing, or deleting a selected client.
 * @author Christoph Knabe
 * @since 2016-09-19
 */
public class ClientTableUI {

    private static final Logger logger = Logger.getLogger(ClientEditUI.class.getName());
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public final String title = "Client List";
    private final Session session;
    private final TableView<Client> table = new TableView<>();
    /**The list of clients to be displayed.*/
    private final ObservableList<Client> observableClientList = FXCollections.observableArrayList();
    private final VBox mainPane = new VBox();
    //Add the layout pane to a scene:
    /*package*/ final Scene scene = new Scene(mainPane);

    //Dynamic Initialization of this object:
    {
        table.setEditable(false);

        //Create and configure ID column:
        final TableColumn<Client,Long> idColumn = new TableColumn<>("ID");
        idColumn.setMinWidth(20);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));

        //Create and configure first name column:
        final TableColumn<Client,String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setMinWidth(40);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        //Create and configure last name column:
        final TableColumn<Client,String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setMinWidth(40);
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        //Create and configure birth date column:
        final TableColumn<Client,Date> birthDateColumn = new TableColumn<>("Birth Date");
        birthDateColumn.setMinWidth(40);
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        birthDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter(df)));

        //Create and configure phone number column:
        final TableColumn<Client,String> phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setMinWidth(120);
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        table.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, birthDateColumn, phoneNumberColumn);
    }

    public ClientTableUI(final Session session) {
        this.session = session;
        //Create and configure the action buttons:
        final Button createButton = _createButton(createAction);
        final Button editButton = _createButton(editAction);
        final Button deleteButton = _createButton(deleteAction);

        //Make the lay out:
        final HBox buttonPane = new HBox();
        buttonPane.setSpacing(8);
        buttonPane.getChildren().addAll(createButton, editButton, deleteButton);
        mainPane.setSpacing(10);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.getChildren().addAll(table, buttonPane);
        this.table.setItems(observableClientList);
        reload();
    }

    /**Creates a Button with the name and behavior of the passed action.*/
    private Button _createButton(final ExceptionReportingFxAction action){
        final Button result = new Button(action.name);
        result.setPrefWidth(80);
        result.setOnAction(action);
        return result;
    }

    final ExceptionReportingFxAction createAction = new ExceptionReportingFxAction("Create", ev -> {
        System.out.println("Create ...");
        new ClientEditUI(this, getSession());
    });

	private Session getSession() {
		return ClientTableUI.this.session;
	}

    final ExceptionReportingFxAction editAction = new ExceptionReportingFxAction("Edit", ev -> {
        System.out.println("Edit ...");
        final Client client = table.getSelectionModel().getSelectedItem();
        if(client==null){
            throw new Exc("No row selected");
        }
        System.out.println("Edit " + client + " in new UI.");
        new ClientEditUI(this, getSession(), client);
    });

    final ExceptionReportingFxAction deleteAction = new ExceptionReportingFxAction("Delete", ev -> {
        System.out.println("Delete ...");
        final ObservableList<Client> selectedItems = table.getSelectionModel().getSelectedItems();
        if(selectedItems.isEmpty()){
            throw new Exc("No row selected");
        }
        for(final Client client: selectedItems){
            System.out.println("Delete " + client + " from UI.");
            observableClientList.remove(client);
            System.out.println("Delete " + client + " from database.");
            getSession().delete(client);
        }
        getSession().commit();
    });

    /*package*/ void reload() throws Exc {
        final List<Client> clientList = this.session.searchAllClients();
        System.out.println("Clients: " + clientList);
        //Workaround to TableView Refresh Problem after editing, if item class does not have JavaFX bindable properties.
        //See http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        observableClientList.removeAll(observableClientList);
        observableClientList.addAll(clientList);
    }

}
