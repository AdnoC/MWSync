import java.util.ArrayList;
/**
 * Provides a layer of abstraction between the model and the view so that both 
 * GUI and command line interfaces can be easily implemented.
 */
public class Controller implements ControlListener {
  // Keep a list of actionlisteners that have registered for events.
  protected ArrayList<ControlListener> listeners;

  public enum ControlAction {
    // VIEW ACTIONS
    // Display a list
    DISPLAY_LIST,
    // Update a progress bar/message or something
    ITEM_PROCESSED,
    // Finished doing stuff so remove the progress bar/message and show completion message
    DONE_PROCESSING,
    // Prompt the user to input their MAL login details
    PROMPT_MAL_LOGIN,
    // Prompt the user to input ther MW login details
    PROMT_MW_LOGIN,
    // Display an error and ask for the user to input correct login details
    INCORRECT_MAL_LOGIN,
    // Display a success message
    CORRECT_MAL_LOGIN,
    // Display an error and ask for the user to input correct login details
    INCORRECT_MW_LOGIN,
    // Display a success message
    CORRECT_MW_LOGIN,

    // MODEL ACTION
    // Set username and password to values and verify they are valid
    MAL_LOGIN_INPUT,
    // Set username and password to values and verify they are valid
    MW_LOGIN_INPUT,
    // Begin transfering information from MW to MAL
    TRANSFER_MANGA,
    // Should this be in here? It is a Model action and isn't controlled by the View.
    SEARCH_MANGA
  };

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
