import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
public class LoginGUI extends JPanel {
  private static final long serialVersionUID = 784058209485L;
  protected String user;
  protected String pass;
  protected String title;
  JPasswordField passField;
  JTextField userField;
  public LoginGUI(String title) {
    this.title = title;
    user = Settings.SETTINGS.getName(title);
    pass = null;
    JLabel uLabel = new JLabel("Username:");
    userField = new JTextField(10);
    userField.setText(user);
    JLabel pLabel = new JLabel("Password: ");
    passField = new JPasswordField(10);
    this.add(uLabel);
    this.add(userField);
    this.add(pLabel);
    this.add(passField);
  }
  public LoginGUI() {
    this("");
  }
  public int prompt() {
    passField.setText("");
    String[] options = new String[]{"OK", "Cancel"};
    //int option = JOptionPane.showOptionDialog(null, this, title,
                             //JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                             //null, options, options[0]);
    JOptionPane jop = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.NO_OPTION, null, options, options[0]);
    //jop.setPreferredSize(new Dimension(500, 500));
    JDialog dialog = jop.createDialog(null, null);
    dialog.setVisible(true);

    // This doesn't work :( I have no idea how to have the user field grab focus 
    //dialog.addWindowListener(new java.awt.event.WindowAdapter() {
      //@Override
      //public void windowOpened(java.awt.event.WindowEvent e) {
        //javax.swing.SwingUtilities.invokeLater(new Runnable() {
          //@Override
          //public void run() {
            //userField.requestFocusInWindow();
            //userField.requestFocus();
            //userField.grabFocus();
          //}
        //});
      //}
    //});

    Object ret = null;
    while((ret = jop.getValue()) == JOptionPane.UNINITIALIZED_VALUE) {
      try {
        Thread.sleep(150);
      } catch(InterruptedException ie) {
        // Do nothing
      }
    }
    int option = 1;
    if(ret == options[0]) {
      option = 0;
    }

    // pressing OK button
    if(option == 0) {
      user = userField.getText();
      pass = new String(passField.getPassword());
    }
    return option;
  }
  public String getUser() {
    return user;
  }
  public String getPass() {
    return pass;
  }
  public String getTitle() {
    return title;
  }
}
