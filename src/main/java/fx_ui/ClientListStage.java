package fx_ui;


import javafx.stage.Stage;
import lg.Client;
import lg.Session;
import multex.Exc;

import java.util.List;

/**The list view of all registered clients as a JavaFX Stage. Displays all clients in a table.
 * @author Christoph Knabe
 * @since 2016-08-19
 */
public class ClientListStage extends Stage {

    protected final ClientFxApplication owner;
    protected final Session session;
    protected List<Client> clientList;

    public ClientListStage(final ClientFxApplication owner, final Session session) {
        super.initOwner(owner.getEditWindow());
        super.setTitle("Client List");
        this.owner = owner;
        this.session = session;
    }

    /*package*/ void reload() throws Exc {
        this.clientList = this.session.searchAllClients();
        System.out.println("Clients: " + clientList);
        //TODO: this.table.setModel(this.dataModel);
        //TODO: this.dataModel.fireTableStructureChanged();
    }

}
