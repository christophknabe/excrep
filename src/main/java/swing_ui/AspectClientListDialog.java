package swing_ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import lg.Client;
import lg.Session;
import multex.Exc;
import multex.Failure;

/**
 * The list view of all registered clients as a Swing dialog.
 * 
 * @author Christoph Knabe 2007-06-05
 */
public class AspectClientListDialog extends ClientListDialog {
	Action editAction;

	Action deleteAction;

	Action createAction;

	public AspectClientListDialog(final AspectClientSwingApplication owner,
			final Session session) {
		super(owner, session);
	}

	@Override
	protected void initButtons(JPanel buttonPanel) {
		buttonPanel.add(new JButton(createAction));
		buttonPanel.add(new JButton(editAction));
		buttonPanel.add(new JButton(deleteAction));
	}

	@Override
	protected void initPopupMenu(JPopupMenu popupMenu) {
		editAction = new AbstractAction("Edit*") {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Edit row "
						+ AspectClientListDialog.this.selectedRow);
				if (AspectClientListDialog.this.selectedRow < 0) {
					throw new Failure(
							"You must select one row for editing it.",
							new RuntimeException());
				}
				final Client client = clientList.get(selectedRow);
				owner.edit(client);
				setVisible(false);
			}
		};

		deleteAction = new AbstractAction("Delete*") {
			public void actionPerformed(ActionEvent ev) {
				System.out.println("ADelete row " + selectedRow);
				final Client client = clientList.get(selectedRow);
				session.delete(client);
				session.commit();
				try {
					reload();
				} catch (Exc e) {
					throw new Failure(e);
				}
			}
		};

		createAction = new AbstractAction("Create*") {
			public void actionPerformed(final ActionEvent ev) {
				System.out.println("ACreate ...");
				owner.reset();
				setVisible(false);
			}
		};

		popupMenu.add(new JMenuItem(editAction));
		popupMenu.add(new JMenuItem(deleteAction));
	}
}
