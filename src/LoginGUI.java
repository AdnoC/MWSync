import javax.swing.JPanel;
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
    user = null;
    pass = null;
    JLabel uLabel = new JLabel("Username:");
    userField = new JTextField(10);
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
    int option = JOptionPane.showOptionDialog(null, this, title,
                             JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                             null, options, options[1]);
    // pressing OK button
    if(option == 0) {
      user = userField.getText();
      pass = new String(passField.getPassword());
      //System.out.println("Your password is: " + new String(pass));
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
