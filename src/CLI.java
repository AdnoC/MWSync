import java.io.Console;
public class CLI extends UserInterface {
  protected Console console;
  // NOTE: '%n' is a platform-independant newline char when using console.format
  public CLI() {
    console = System.console();
    displayWelcome();
    displayHelp();
  }
  protected void displayWelcome() {
    console.format("Hi%nWelcome");
  }
  protected void displayHelp() {
  }
  public void startThread() {
    Thread uiThread = new Thread(this);
    uiThread.start();
  }
  public void run() {
    String input;
    while((input = console.readLine()) != null) {
      // Give top priority to exiting when asked.
      if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
        System.exit(0);
      }
    }

  }
  public void registerController(Controller c) {
    controls = c;
    c.register(new CLIControlListener());

  }
private class CLIControlListener implements ControlListener {
  public void eventFired(ControlEvent ce) {

  }

}
}
