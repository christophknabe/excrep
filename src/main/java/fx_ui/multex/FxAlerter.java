package fx_ui.multex;


//Change history:
//2016-08-13  Knabe  Created as FxAlerter replacement for multex.Swing.

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import multex.Msg;
import multex.Util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

/**
 * Service for reporting onto a JavaFX Stage the messages for any exception
 * with dynamic or no localization.
 * The exception can be:
 * <ul>
 *   <li>with parameters (subclass of {@link multex.Exc})</li>
 *   <li>with cause chain and parameters (subclass of {@link multex.Failure})</li>
 *   <li>or any standard Java exception (other subclass of {@link Throwable} with cause chain and one String parameter</li>
 * </ul>
 * You have to create an FxAlerter object and then you can call repeatedly the {@link #report(Throwable, Node)} method on it.
 * @author Christoph Knabe
 * @since 2016-08-13
 */
public class FxAlerter {

    /**The name of this class in the format package.subpackage.Class*/
    private static final String _className = FxAlerter.class.getName();

    private final ResourceBundle _resourceBundle;

    /**Creates a reporting service for exceptions with the capability for localization of exception messages.
     * @param resourceBundle the bundle to be used for localization. If null, the message text pattern contained in the exception objects will be used.
     *                       If this is null, a standard representation will be used.
     */
    public FxAlerter(final ResourceBundle resourceBundle){
        _resourceBundle = resourceBundle;
    }

    /**Reports an exception into an FxAlerter Stage.
     * <br>Firstly reports the same as {@link multex.Msg#printMessages(StringBuffer, Throwable, ResourceBundle)}
     * into a pop-up Stage of appropriate size.
     * Accepting the message is done by <ul>
     *     <li> clicking on the "close window" icon, or</li>
     *     <li> clicking on the OK-button, or</li>
     *     <li> typing &lt;RETURN&gt; on the OK-button </li>
     * </ul>
     * Each of these will close the dialog box.
     * <p>
     *     The Button "Show Stack Trace" will add the compactified
     *     stack trace including the chain of all causing exceptions
     *     to the message dialog box. The contents of the compact
     *     stack trace are described at method
     *     {@link multex.Msg#printStackTrace(StringBuffer,Throwable)}.
     * </p>
     *
     * @param throwable The exception to be reported along with its causal chain
     * @param causingNode the JavaFX Node causing this message to re reported. The message dialog will appear near to its Window.
     *                    This Window is blocked in javafx.stage.Modality.WINDOW_MODAL mode, during a message is shown by this service.
     *
     * @see multex.Msg#printReport(StringBuffer,Throwable, ResourceBundle)
     */
    public void report(final Throwable throwable, final Node causingNode){
        try{
            final Stage stage = new Stage();
            stage.setTitle("Error message");
            if(causingNode != null){
                final Scene causingScene = causingNode.getScene();
                if(causingScene != null){
                    final Window causingWindow = causingScene.getWindow();
                    stage.initOwner(causingWindow);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setX(causingWindow.getX()+25);
                    stage.setY(causingWindow.getY()+25);
                    if(causingWindow instanceof Stage){
                        final Stage causingStage = (Stage)causingWindow;
                        stage.setTitle(stage.getTitle() + " for " + causingStage.getTitle());
                    }
                }
            }
            final BorderPane mainPane = new BorderPane();
            final StringBuffer messageChain = new StringBuffer();
            Msg.printMessages(messageChain, throwable, _resourceBundle);
            final Text text = new Text(messageChain.toString());
            final HBox centerPane = _createHBox(text);

            final Button detailsButton = new Button("Details");
            detailsButton.setDefaultButton(true);
            final Button copyButton = new Button("Copy");
            final Button cancelButton = new Button("Cancel");
            cancelButton.setCancelButton(true);

            //Add the buttons into the right side into a layout pane:
            final Region spacer = new Region();
            final HBox buttonPane = _createHBox(spacer, detailsButton, copyButton, cancelButton);
            buttonPane.setHgrow(spacer, Priority.ALWAYS);

            mainPane.setCenter(centerPane);
            mainPane.setBottom(buttonPane);

            //Add the behavior of the buttons:
            detailsButton.setOnAction(ev -> {
                detailsButton.setText("Print");
                detailsButton.setOnAction(ev2 -> System.err.println(text.getText()));
                final StringBuffer buffer = new StringBuffer(text.getText());
                buffer.append(Util.lineSeparator);   buffer.append(Util.lineSeparator);
                buffer.append(Msg.stackTraceFollows);
                buffer.append(Util.lineSeparator);
                Msg.printStackTrace(buffer, throwable);
                text.setText(buffer.toString());
                mainPane.setBottom(null);
                mainPane.setTop(buttonPane);
                stage.sizeToScene();
            });
            copyButton.setOnAction(e -> _copyToClipboard(text.getText()));
            cancelButton.setOnAction(e -> stage.close());

            //Add the layout pane to a scene
            final Scene scene = new Scene(mainPane);
            stage.setScene(scene);
            stage.showAndWait();
        }catch(final Throwable ex){
            _reportNoWindow(ex, throwable);
        }
        //end try
    }

    /**Reports to System.err, that the exception problem occured
     *  when trying to report the exception reportee.
     */
    private void _reportNoWindow(
        final Throwable problem, final Throwable reportee
    ){
        final PrintWriter out = new PrintWriter(new StringWriter());
        out.println();
        out.append(_className);
        out.println(": THE FOLLOWING EXCEPTION OCCURED:");
        problem.printStackTrace(out);
        out.println("WHEN CONSTRUCTING THE MESSAGE WINDOW FOR EXCEPTION:");
        reportee.printStackTrace(out);
        out.flush();
        System.err.print(out);
        System.err.flush();
        out.close();
    }

    /**Creates a horizontal box with the given nodes, and spacing and insets of 10.*/
    private HBox _createHBox(final Node... nodes) {
        final HBox result = new HBox(10, nodes);
        result.setPadding(new Insets(10));
        return result;
    }

    private void _copyToClipboard(final String string){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
    }
    /**Appends the stack trace of i_throwable to io_text control and disables the io_detailsButton.*/
    private void _fireDetailsButton(final Stage io_stage, BorderPane mainPane, final Button io_detailsButton, final Text io_text, final Throwable i_throwable){
        io_detailsButton.setVisible(false);
        final StringBuffer buffer = new StringBuffer(io_text.getText());
        buffer.append(Util.lineSeparator);   buffer.append(Util.lineSeparator);
        buffer.append(Msg.stackTraceFollows);
        buffer.append(Util.lineSeparator);
        Msg.printStackTrace(buffer, i_throwable);
        //_append(io_text, stackTrace);
        io_text.setText(buffer.toString());
        io_stage.sizeToScene();
        //TODO port from here:
        //io_text.insert("", 0); //should scroll back upwards to character first line
        //_dialog.pack();
        //_stackTraceShown = true;
    }

    /**Prints i_node and the chain of its parents for test purposes.*/
    private static void _printParents(final Node node){
        System.err.print("Node: ");
        for(Node c=node;; c=c.getParent()){
            System.err.println(c);
            System.err.println("getParent(): ");
            if(c==null){break;}
        }
    }//_printParents


}
