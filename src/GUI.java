import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;

import java.util.ArrayList;
public class GUI extends UserInterface {
  protected JFrame frame;
  // The login persists so that users do not have to repeat typing in their
  // username if login fails. It is deleted on successful login.
  protected LoginGUI login;

  public GUI() {
    login = null;
  }
  public void run() {
    frame = new JFrame("MWSync");
          //promptLogin("Manga Watcher", ControlAction.MW_LOGIN_INPUT);
          //promptLogin("MyAnimeList", ControlAction.MAL_LOGIN_INPUT);
  }

  protected void displayLoginSuccess(String serviceName) {
    // @TODO: Display a success message
  }

  protected void promptLogin(String title, ControlAction act) {
    if(login == null || login.getTitle().equals(title)) {
      login = new LoginGUI(title);
    }
    int result = login.prompt();
    // If they filled in the forms
    if(result == 0) {
      ArrayList<String> loginDets = new ArrayList<String>();
      loginDets.add(login.getUser());
      loginDets.add(login.getPass());

      controls.fireEvent(new ControlEvent(act, loginDets));
    }
  }

  public void startThread() {
    javax.swing.SwingUtilities.invokeLater(this);
  }
  public void registerController(Controller c) {
    controls = c;
    c.register(new GUIControlListener());
  }
  private class GUIControlListener implements ControlListener {
    @SuppressWarnings("unchecked")
    public void fireEvent(ControlEvent ce) {
      switch(ce.getMessage()) {
        case CORRECT_MAL_LOGIN: {
          login = null;
          displayLoginSuccess("MyAnimeList");
          break;
        }
        case CORRECT_MW_LOGIN: {
          login = null;
          displayLoginSuccess("Manga Watcher");
          break;
        }
      }
    }
  }
}
