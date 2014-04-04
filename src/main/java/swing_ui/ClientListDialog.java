package swing_ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import lg.Client;
import lg.Session;
import multex.Exc;
import multex.Failure;


/**The list view of all registered clients as a Swing dialog.
 * @author Christoph Knabe 2007-06-05
 */
public class ClientListDialog extends JDialog {

	private static final int ID_COL = 0;
	private static final int FIRST_NAME_COL = 1;
	private static final int LAST_NAME_COL = 2;
	private static final int BIRTH_DATE_COL = 3;
	private static final int PHONE_NUMBER_COL = 4;

	private static final long serialVersionUID = -8591653225035745301L;

	protected final ClientSwingApplication owner;
	protected final Session session;
	private final AbstractTableModel dataModel = new DataModel();
	private final JTable table = new JTable();
	protected int selectedRow = -1; //initially no row is selected
	
	protected List<Client> clientList;
	
	public ClientListDialog(final ClientSwingApplication owner, final Session session){
		super(owner.getEditFrame(), "Client List");
		this.owner = owner;
		this.session = session;	    

        this.table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        final JPopupMenu popupMenu = new JPopupMenu();
        initPopupMenu(popupMenu);

        //Add listener to the table so the popup menu can come up.
        final MouseListener popupListener = new PopupListener(popupMenu);
        this.table.addMouseListener(popupListener);

        
        final JScrollPane scrollPane = new JScrollPane(this.table);
        final java.awt.Container contentPane = getContentPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
	    final JPanel buttonPanel = new JPanel(new FlowLayout());
	    initButtons(buttonPanel);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
	}

	protected void initButtons(final JPanel buttonPanel) {
		buttonPanel.add(new JButton(this.createAction));
	    buttonPanel.add(new JButton(this.editAction));
	    buttonPanel.add(new JButton(this.deleteAction));
	}

	protected void initPopupMenu(final JPopupMenu popupMenu) {
		popupMenu.add(new JMenuItem(this.editAction));
        popupMenu.add(new JMenuItem(this.deleteAction));
	}
	
	/*package*/ void reload() throws Exc {
		this.clientList = this.session.searchAllClients();
		this.table.setModel(this.dataModel);
		this.dataModel.fireTableStructureChanged();
	    
	}
	private final class DataModel extends AbstractTableModel {
		private static final long serialVersionUID = 3272605639423905267L;
		
		public int getColumnCount() { return 5; }
        @Override public String getColumnName(final int column){
        	switch(column){
	        	case ID_COL: return "ID";
	        	case FIRST_NAME_COL: return "First Name";
	        	case LAST_NAME_COL: return "Last Name";
	        	case BIRTH_DATE_COL: return "Birth Date";
	        	case PHONE_NUMBER_COL: return "Phone Number";
	        	default: throw new Failure("Unknown column number {0}", null, new Integer(column));
	        }
        }
        @Override public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public int getRowCount() { return ClientListDialog.this.clientList.size();}
        public Object getValueAt(final int row, final int column) { 
        	final Client client = ClientListDialog.this.clientList.get(row);
        	switch(column){
	        	case ID_COL: return new Long(client.getId());
	        	case FIRST_NAME_COL: return client.getFirstName();
	        	case LAST_NAME_COL: return client.getLastName();
	        	case BIRTH_DATE_COL: return client.getBirthDate();
	        	case PHONE_NUMBER_COL: return client.getPhone();
	        	default: throw new Failure("Unknown column number {0}", null, new Integer(column));
	        }
        }
		
	}

    final Action editAction = new ExceptionReportingSwingAction("Edit"){
		@Override public void actionPerformedWithThrows(final ActionEvent evt) throws Exc {
            System.out.println("Edit row " + ClientListDialog.this.selectedRow);
            if(ClientListDialog.this.selectedRow<0){
            	throw new Exc("You must select one row for editing it.");
            }
    		final Client client = clientList.get(selectedRow);
    		owner.edit(client);
    		setVisible(false);
        }
    };
    
    final Action deleteAction = new ExceptionReportingSwingAction("Delete"){
        @Override public void actionPerformedWithThrows(final ActionEvent ev) throws Exc {
            System.out.println("Delete row " + selectedRow);
    		final Client client = clientList.get(selectedRow);
    		session.delete(client);
    		session.commit();
    		reload();
        }
    };

    final Action createAction = new ExceptionReportingSwingAction("Create"){
        @Override public void actionPerformedWithThrows(final ActionEvent ev) throws Exc {
            System.out.println("Create ...");
    		owner.reset();
    		setVisible(false);
        }
    };
    
    class PopupListener extends MouseAdapter {
    	
        final JPopupMenu popup;

        PopupListener(final JPopupMenu popupMenu) {
            this.popup = popupMenu;
        }

        @Override public void mousePressed(final MouseEvent e) {
        	final Point mousePoint = e.getPoint();
        	selectedRow = table.rowAtPoint(mousePoint);
            final ListSelectionModel rowSM = table.getSelectionModel();
            rowSM.setSelectionInterval(selectedRow, selectedRow);
            maybeShowPopup(e);
        }

        @Override public void mouseReleased(final MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(final MouseEvent e) {
            if (e.isPopupTrigger()) {
				popup.show(
                	e.getComponent(), e.getX(), e.getY()
                );
            }
        }
    }//PopupListener

}
