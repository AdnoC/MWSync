import java.util.ArrayList;
/**
 * Provides a layer of abstraction between the model and the view so that both 
 * GUI and command line interfaces can be easily implemented.
 */
public class Controller implements ControlListener {
  // Keep a list of actionlisteners that have registered for events.
  protected ArrayList<ControlListener> listeners;


  /**
   * Constructor.
   */
  public Controller() {
    listeners = new ArrayList<ControlListener>();
  }

  /**
   * Registers listeners to recieve events
   * @param al
   *  The listener to register
   */
  public void register(ControlListener al) {
    listeners.add(al);
  }

  /**
   * Propogates an event to registered listeners.
   * @param ae
   *  The event to propogate.
   */
  public void fireEvent(ControlEvent ae) {
    System.out.println("Controller event fired: " + ae.getMessage().name());
    EventDispatcher ed = new EventDispatcher(ae);
    (new Thread(ed)).start();
  }

  /**
   * Takes care of propogating an event to registered listeners on a new thread.
   */
  private class EventDispatcher implements Runnable {
    // The event to propogate.
    private ControlEvent actionEvent;
    /**
     * Constroctor.
     * @param ae
     *  The event to propogate.
     */
    public EventDispatcher(ControlEvent ae) {
      actionEvent = ae;
    }
    /**
     * Actually propogates the action.
     */
    public void run() {
      for(ControlListener al : Controller.this.listeners) {
        al.fireEvent(actionEvent);
      }
    }
  }
}
