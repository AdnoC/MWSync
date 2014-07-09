import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
/**
 * Provides a layer of abstraction between the model and the view so that both 
 * GUI and command line interfaces can be easily implemented.
 */
public class Controller implements ActionListener {
  // Keep a list of actionlisteners that have registered for events.
  protected ArrayList<ActionListener> listeners;

  /**
   * Constructor.
   */
  public Controller() {
    listeners = new ArrayList<ActionListener>();
  }

  /**
   * Registers listeners to recieve events
   * @param al
   *  The listener to register
   */
  public void register(ActionListener al) {
    listeners.add(al);
  }

  /**
   * Propogates an event to registered listeners.
   * @param ae
   *  The event to propogate.
   */
  public void actionPerformed(ActionEvent ae) {
    EventDispatcher ed = new EventDispatcher(ae);
    (new Thread(ed)).start();
  }

  /**
   * Takes care of propogating an event to registered listeners on a new thread.
   */
  private class EventDispatcher implements Runnable {
    // The event to propogate.
    private ActionEvent actionEvent;
    /**
     * Constroctor.
     * @param ae
     *  The event to propogate.
     */
    public EventDispatcher(ActionEvent ae) {
      actionEvent = ae;
    }
    /**
     * Actually propogates the action.
     */
    public void run() {
      for(ActionListener al : Controller.this.listeners) {
        al.actionPerformed(actionEvent);
      }
    }
  }
}
