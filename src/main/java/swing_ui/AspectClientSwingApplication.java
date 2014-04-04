package swing_ui;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import lg.Client;
import multex.Exc;
import multex.Failure;
import db.Persistence;

/** An application for management of clients. Uses a Swing UI. */
public class AspectClientSwingApplication extends ClientSwingApplication {
    private static final Logger logger = Logger.getLogger(AspectClientSwingApplication.class.getName());
    
	private final AspectClientListDialog listDialog = new AspectClientListDialog(this, session);

	/** Creates a Swing user interface for managing clients. */
	public static void main(final String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new AspectClientSwingApplication();
			}
		});
	}

	@Override
	protected void addButtons(JPanel buttonPanel) {
		

		Action saveAction = new AbstractAction("Save*") {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("Save ...");
				final long id = getInternalId();
				try {
					if (id == Persistence.INEXISTENT_ID) {
						final Client client = session.createClient(firstName
								.getText(), lastName.getText(),
								getInternalBirthDate(), phone.getText());
						setInternalId(client.getId());
					} else {
						final Client client = session.findClient(id);
						client.setAttributes(firstName.getText(), lastName
								.getText(), getInternalBirthDate(), phone
								.getText());
					}
					session.commit();
				} catch (Exception e) {
					throw new Failure("Cannot save client {0}", e, lastName
							.getText());
				}
			}
		};

		Action resetAction = new AbstractAction("Reset*") {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("Reset ...");
				reset();
			}
		};

		Action deleteAction = new AbstractAction("Delete*") {
			public void actionPerformed(ActionEvent ev) {
				try {
					System.out.println("Delete ...");
					final Client client = session.findClient(getInternalId());
					session.delete(client);
					session.commit();
					listDialog.reload();
					// SwingUtil.show(listDialog);
					listDialog.pack();
					listDialog.setVisible(true);
				} catch (Exc e) {
					throw new Failure(e);
				}
			}
		};

		Action listAction = new AbstractAction("List*") {
			public void actionPerformed(ActionEvent ev) {
				try {
					System.out.println("List ...");
					listDialog.reload();
					// SwingUtil.show(listDialog);
					listDialog.pack();
					listDialog.setVisible(true);
				} catch (Exc e) {
					throw new Failure(e);
				}
			}
		};

		JButton saveButton = new JButton(saveAction);
		buttonPanel.add(saveButton);
		JButton resetButton = new JButton(resetAction);
		buttonPanel.add(resetButton);
		JButton deleteButton = new JButton(deleteAction);
		buttonPanel.add(deleteButton);
		JButton listButton = new JButton(listAction);
		buttonPanel.add(listButton);
	}

	/** Let only me create objects of me! */
	private AspectClientSwingApplication() {
        logger.info("");
	}
    
}