package swing_ui; 

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lg.Client;
import lg.Session;
import lg.SessionImpl;
import multex.Exc;
import multex.Failure;
import db.Persistence;

/**An application for management of clients. Uses a Swing UI.*/
public class ClientSwingApplication {

    private static final Logger logger = Logger.getLogger(ClientSwingApplication.class.getName());
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
	protected final Session session = new SessionImpl();
	
    private final JFrame editFrame = new JFrame();
    private final int width = 20;
    private final JLabel id =  new JLabel();
    protected final JTextField firstName = new JTextField("", width);
    protected final JTextField lastName  = new JTextField("", width);
    private final JTextField birthDate  = new JTextField("", width);
    protected final JTextField phone  = new JTextField("", width);

    /**The dialog showing a list of clients.*/
	private final ClientListDialog listDialog = new ClientListDialog(this, session);

	/**Creates a Swing user interface for managing clients.*/
    public static void main(final String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                new ClientSwingApplication();
            }
        });
    }
    
    /**Let only me or my descendants create objects of me!*/
    protected ClientSwingApplication(){
	    //Layout:
    	
    	logger.info("");
	    final java.awt.Container container = editFrame.getContentPane();
	    //container.setLayout(new java.awt.FlowLayout());
	    container.setLayout(new BorderLayout(5,5));
	    
	    final JPanel fieldPanel = new JPanel(new java.awt.GridLayout(0,2));
	    fieldPanel.add(new javax.swing.JLabel("ID:"));
	    fieldPanel.add(id);
	    fieldPanel.add(new javax.swing.JLabel("First Name:"));
	    fieldPanel.add(firstName);
	    fieldPanel.add(new javax.swing.JLabel("Last Name:"));
	    fieldPanel.add(lastName);
	    fieldPanel.add(new javax.swing.JLabel("Birth Date (yyyy-MM-dd):"));
	    fieldPanel.add(birthDate);
	    fieldPanel.add(new javax.swing.JLabel("Phone Number:"));
	    fieldPanel.add(phone);
	    container.add(fieldPanel, BorderLayout.CENTER);

	    final JPanel buttonPanel = new JPanel(new FlowLayout());
	    addButtons(buttonPanel);	    
	    container.add(buttonPanel, BorderLayout.SOUTH);
	    
	    reset();
	    	
	    //Behaviour:
		
	    editFrame.addWindowListener( new java.awt.event.WindowAdapter(){
		    @Override public void windowClosing(final java.awt.event.WindowEvent i_){
		        System.exit(0);
		    }
	    });

	    editFrame.pack();
	    editFrame.setVisible(true);

    }//SwingFile()

    protected void addButtons(JPanel buttonPanel) {
    	JButton saveButton = new JButton(saveAction);
	    buttonPanel.add(saveButton);
	    JButton resetButton = new JButton(resetAction);
	    buttonPanel.add(resetButton);
	    JButton deleteButton = new JButton(deleteAction);
	    buttonPanel.add(deleteButton);
	    JButton listButton = new JButton(listAction);
	    buttonPanel.add(listButton);
	}

	final Action saveAction = new ExceptionReportingSwingAction("Save") {
        @Override public void actionPerformedWithThrows(ActionEvent ev) {
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
        }
    };

    final Action resetAction = new ExceptionReportingSwingAction("Reset"){
        @Override public void actionPerformedWithThrows(ActionEvent evt) {
            System.out.println("Reset ...");
    		reset();
        }
    };
    
    final Action deleteAction = new ExceptionReportingSwingAction("Delete"){
        @Override public void actionPerformedWithThrows(ActionEvent ev) throws Exc {
            System.out.println("Delete ...");
    		final Client client = session.findClient(getInternalId());
    		session.delete(client);
    		session.commit();
            listDialog.reload();
    	    //SwingUtil.show(listDialog);
            listDialog.pack();
            listDialog.setVisible(true);
        }
    };
    
    final Action listAction = new ExceptionReportingSwingAction("List"){
        @Override public void actionPerformedWithThrows(ActionEvent ev) throws Exc {
            System.out.println("List ...");
            listDialog.reload();
    	    //SwingUtil.show(listDialog);
            listDialog.pack();
            listDialog.setVisible(true);
        }
    };
      

    /*package*/ JFrame getEditFrame() {
		return editFrame;
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
	    editFrame.setTitle(action + " Client");
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

	/**Sets all fields to a value taken from the corresponding field in the Client.*/
	/*package*/ void edit(final Client c) {
    	setInternalId(c.getId());
		firstName.setText(c.getFirstName());
		lastName.setText(c.getLastName());
		setInternalBirthDate(c.getBirthDate());
		phone.setText(c.getPhone());
	}

	
}