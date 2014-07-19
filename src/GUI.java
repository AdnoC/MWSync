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

  protected JButton transferButton;
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
        for(ItemPane ip : list.toArray(new ItemPane[list.size()])) {
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
        promptLogin(Settings.MW_NAME, ControlAction.MW_LOGIN_INPUT);
      }
    });
    malButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        promptLogin(Settings.MAL_NAME, ControlAction.MAL_LOGIN_INPUT);
      }
    });
    //accountPanel.setPreferredSize(new Dimension(350, 50));

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
    frame.add(controlPanel, BorderLayout.NORTH);
    controlPanel.add(accountPanel);

    transferButton = new JButton("Begin Transfer");
    transferButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        ControlEvent ce;
        if(transferButton.getActionCommand().equals("Start")) {
          transferButton.setActionCommand("Stop");
          transferButton.setText("Cancel");
          ce = new ControlEvent(ControlAction.TRANSFER_MANGA);
        } else {
          transferButton.setActionCommand("Start");
          transferButton.setText("Begin Transfer");
          ce = new ControlEvent(ControlAction.STOP_TRANSFER);
        }
        controls.fireEvent(ce);
      }
    });
    transferButton.setActionCommand("Start");
    transferButton.setMinimumSize(new Dimension(125, 50));
    controlPanel.add(transferButton);



    transferLog = new JPanel();
    BoxLayout boxLay = new BoxLayout(transferLog, BoxLayout.Y_AXIS);
    transferLog.setLayout(boxLay);
    JScrollPane jsp = new JScrollPane(transferLog);
    // Make it scroll faster
    jsp.getVerticalScrollBar().setUnitIncrement(20);
    jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    frame.add(jsp, BorderLayout.CENTER);


    frame.setSize(500, 500);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  protected void displayLoginSuccess(String serviceName) {
    JOptionPane.showMessageDialog(null, "You have successfully logged into " + serviceName);
  }
  protected void displayLoginFailure(String serviceName) {
    JOptionPane.showMessageDialog(null, "Invalid username or password for " + serviceName);
  }

  protected void promptLogin(String serviceName, ControlAction act) {
    if(login == null || ! login.getTitle().equals(serviceName)) {
      login = new LoginGUI(serviceName);
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
    final ArrayList<ItemPane> iPanes = new ArrayList<ItemPane>();

    for(int i = 0; i < mals.size(); i++) {
      ItemPane ip = ItemPane.newSearch(mals.get(i));
      ip.getContainer().setPreferredSize(new Dimension(450, 250));
      iPanes.add(ip);
      JComponent jc = ip.getContainer();;
      if(jc instanceof JButton) {
        JButton but = (JButton) jc;
        buttons.add(but);
      }
    }
    //buttons.add(new JButton("Cancel"));

//(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue)
                //JOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options, Object initialValue)

    JButton[] buts = buttons.toArray(new JButton[buttons.size()]);
    JPanel buttonPanel = new JPanel();
    //buttonPanel.setPreferredSize(new Dimension(500, 500));
    JScrollPane jsp = new JScrollPane(buttonPanel);
    jsp.getVerticalScrollBar().setUnitIncrement(20);
    //buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));
    //jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    //final JOptionPane jop = new JOptionPane(null, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, buts, buts[0]);

    JLabel jl = new JLabel("Searching for: " + mals.getQueryString());
    JPanel cont = new JPanel(new BorderLayout());
    cont.add(jl, BorderLayout.NORTH);
    cont.add(jsp, BorderLayout.CENTER);
    final JOptionPane jop = new JOptionPane(cont, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null, new String[]{"Not Found"});
    jop.setPreferredSize(new Dimension(500, 500));
    for(int i = 0; i < buts.length; i++) {
      final int val = i;
      buts[i].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          jop.setValue(val);
        }
      });
      JPanel jp = new JPanel();
      jp.add(buts[i]);
      buttonPanel.add(jp);
      buttonPanel.validate();
    }
    final JDialog dialog = jop.createDialog(null, null);
    dialog.setVisible(true);

    Object ret = null;
    while((ret = jop.getValue()) == JOptionPane.UNINITIALIZED_VALUE) {
      try {
        Thread.sleep(150);
      } catch(InterruptedException ie) {
        // Do nothing
      }
    }
    Integer index;
    if(ret == null || ret instanceof String) {
     index = -1;
    } else {
     index = (Integer) ret;
    }
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
          ItemPane ip = ItemPane.newSuccess((MangaItem) ce.getData());
          list.add(ip);
          // 0 supposedly makes it add in bottom-to-top order
          transferLog.add(ip.getContainer(), 0);
          transferLog.revalidate();
          break;
        }
        case ITEM_DROPPED: {
          ItemPane ip = ItemPane.newFailure((MangaItem) ce.getData());
          list.add(ip);
          // 0 supposedly makes it add in bottom-to-top order
          transferLog.add(ip.getContainer(), 0);
          transferLog.revalidate();
          break;
        }
        case DISPLAY_SEARCH: {
          showSearch((MALSearchResults) ce.getData());
          break;
        }
        case DONE_PROCESSING: {
          JOptionPane.showMessageDialog(null, "Done transfering.");
          transferButton.setActionCommand("Start");
          transferButton.setText("Begin Transfer");
        }
      }
    }
  }
}
