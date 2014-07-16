import javax.swing.JFrame;
import javax.swing.JDialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


import java.util.ArrayList;
public class GUI extends UserInterface {
  public static final int COMMON_SCROLLBAR_WIDTH = 15;
  protected JFrame frame;
  // The login persists so that users do not have to repeat typing in their
  // username if login fails. It is deleted on successful login.
  protected LoginGUI login;
  protected JLabel mwName;
  protected JLabel malName;
  protected ArrayList<ItemPane> list;

  protected JPanel transferLog;

  // @TODO: Have the login buttons change color. Default when starting. Red on login
  // failure. Green on login success.
  public GUI() {
    login = null;
    list = new ArrayList<ItemPane>();
  }
  public void run() {
    frame = new JFrame("MWSync");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent ce) {
        Dimension dim = new Dimension(frame.getWidth() - 3*COMMON_SCROLLBAR_WIDTH, frame.getHeight() / 4);
        for(ItemPane ip : list) {
          ip.resize(dim);
        }
      }
    });

    // Set up panel to show account names and make the prompt for login details
    JPanel accountPanel = new JPanel(new BorderLayout());
    JPanel serviceNamePanel = new JPanel(new BorderLayout());
    serviceNamePanel.add(new JLabel("MangaWatcher: ", SwingConstants.LEFT), BorderLayout.NORTH);
    serviceNamePanel.add(new JLabel("MyAnimeList: ", SwingConstants.LEFT), BorderLayout.SOUTH);
    accountPanel.add(serviceNamePanel, BorderLayout.WEST);

    JPanel accountNamePanel = new JPanel(new BorderLayout());
    mwName = new JLabel("Not Logged In", SwingConstants.LEFT);
    malName = new JLabel("Not Logged In", SwingConstants.LEFT);
    accountNamePanel.add(mwName, BorderLayout.NORTH);
    accountNamePanel.add(malName, BorderLayout.SOUTH);
    accountPanel.add(accountNamePanel, BorderLayout.CENTER);

    JPanel loginButtonPanel = new JPanel(new BorderLayout());
    JButton mwButton = new JButton("Log In");
    JButton malButton = new JButton("Log In");
    loginButtonPanel.add(mwButton, BorderLayout.NORTH);
    loginButtonPanel.add(malButton, BorderLayout.SOUTH);
    accountPanel.add(loginButtonPanel, BorderLayout.EAST);

    mwButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        promptLogin("Manga Watcher", ControlAction.MW_LOGIN_INPUT);
      }
    });
    malButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        promptLogin("MyAnimeList", ControlAction.MAL_LOGIN_INPUT);
      }
    });

    frame.add(accountPanel, BorderLayout.NORTH);


    transferLog = new JPanel();
    BoxLayout boxLay = new BoxLayout(transferLog, BoxLayout.Y_AXIS);
    transferLog.setLayout(boxLay);
    JScrollPane jsp = new JScrollPane(transferLog);
    jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    frame.add(jsp, BorderLayout.CENTER);



    frame.setSize(500, 500);
    frame.setVisible(true);
    controls.fireEvent(new ControlEvent(ControlAction.TRANSFER_MANGA));
  }

  protected void displayLoginSuccess(String serviceName) {
    // @TODO: Display a success message
  }
  protected void displayLoginFailure(String serviceName) {
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
      switch(act) {
        case MAL_LOGIN_INPUT: {
          malName.setText("Loggin in...");
          break;
        }
        case MW_LOGIN_INPUT: {
          mwName.setText("Loggin in...");
          break;
        }
      }
    }
  }
  public void showSearch(MALSearchResults mals) {
    ArrayList<JButton> buttons = new ArrayList<JButton>();

    for(int i = 0; i < mals.size(); i++) {
      JComponent jc = ItemPane.newSearch(mals.get(i)).getContainer();;
      if(jc instanceof JButton) {
        JButton but = (JButton) jc;
        buttons.add(but);
      }
    }
    buttons.add(new JButton("Cancel"));

//(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue)
                //JOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options, Object initialValue)

    JButton[] buts = buttons.toArray(new JButton[buttons.size()]);
    final JOptionPane jop = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, buts, buts[0]);
    for(int i = 0; i < buts.length; i++) {
      final int val = i;
      buts[i].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          jop.setValue(val);
        }
      });
    }
    JDialog dialog = jop.createDialog(null, null);
    dialog.setVisible(true);
    Object ret = null;
    while((ret = jop.getValue()) == JOptionPane.UNINITIALIZED_VALUE) {
      try {
        Thread.sleep(150);
      } catch(InterruptedException ie) {
        // Do nothing
      }
    }
    Integer index = (Integer) ret;
    System.out.println("Selected option: " + ret);
    controls.fireEvent(new ControlEvent(ControlAction.SEARCH_RESULT_SELECTED, index));
    //int value = JOptionPane.showOptionDialog(
      //null,
      //null,
      //"Get",
      //JOptionPane.DEFAULT_OPTION,
      //JOptionPane.QUESTION_MESSAGE,
      //null,
      //buts,
      //buts[0]);

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
          malName.setText((String) ce.getData());
          login = null;
          displayLoginSuccess("MyAnimeList");
          break;
        }
        case CORRECT_MW_LOGIN: {
          mwName.setText((String) ce.getData());
          login = null;
          displayLoginSuccess("Manga Watcher");
          break;
        }
        case INCORRECT_MAL_LOGIN: {
          malName.setText("Login Failed");
          displayLoginFailure("MyAnimeList");
          break;
        }
        case INCORRECT_MW_LOGIN: {
          mwName.setText("Login Failed");
          displayLoginFailure("Manga Watcher");
          break;
        }
        case ITEM_PROCESSED: {
                               //@TODO: when adding items, add them to the top
          ItemPane ip = ItemPane.newSuccess((MALSearchResults.MALSearchResult) ce.getData());
          list.add(ip);
          transferLog.add(ip.getContainer());
          transferLog.revalidate();
          break;
        }
        case ITEM_DROPPED: {
          ItemPane ip = ItemPane.newFailure((MALSearchResults.MALSearchResult) ce.getData());
          list.add(ip);
          transferLog.add(ip.getContainer());
          transferLog.revalidate();
          break;
        }
        // FOR DEBUG ONLY
        case DISPLAY_SEARCH: {
          showSearch((MALSearchResults) ce.getData());
          //controls.fireEvent(new ControlEvent(ControlAction.SEARCH_RESULT_SELECTED, -1));
          break;
        }
      }
    }
  }
}
