import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Model {
  Controller control;

  public enum ModelActions {
    // Set username and password to values and verify they are valid
    MAL_LOGIN_INPUT,
    // Set username and password to values and verify they are valid
    MW_LOGIN_INPUT,
    // Begin transfering information from MW to MAL
    TRANSFER_MANGA,
    // Should this be in here? It is a Model action and isn't controlled by the View.
    SEARCH_MANGA
  }

  public void registerController(Controller c) {
    control = c;
    c.register(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {

      }
    });
  }
}
