import java.io.Console;
import java.util.ArrayList;
public class CLI extends UserInterface {
  protected Console console;
  protected boolean waitingForSearchInput;
  // NOTE: '' is a platform-independant newline char when using System.out.println
  public CLI() {
    console = System.console();
    waitingForSearchInput = false;
  }
  protected void displayWelcome() {
    System.out.print("Please begin by typing 'mal' or 'mw' to input your login information ");
    System.out.println("for MyAnimeList and MangaWatcher.");
    System.out.println("Type 'help' to display a list of commands.");
  }
  protected void displayHelp() {
    System.out.println("Note that none of these commands are case-sensative.");
    System.out.println("help: Displays this screen.");
    System.out.println("exit|quit: quits the program.");
    System.out.println("mal: Input your MyAnimeList login information.");
    System.out.println("mw: Input your MangaWatcher login information.");
    System.out.println("status: Display your login status for both services.");
    System.out.println("start: Begins transfering data from MW to MAL.");
  }
  public void startThread() {
    Thread uiThread = new Thread(this);
    uiThread.start();
  }
  public void run() {
    displayWelcome();
    String input;
    while((input = console.readLine()) != null) {
      // Give top priority to exiting when asked.
      if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
        System.exit(0);
      } else if(input.equalsIgnoreCase("MAL")) {
        promptMALLogin();
      } else if(input.equalsIgnoreCase("MW")) {
        promptMWLogin();
      } else if(input.equalsIgnoreCase("status")) {
        controls.fireEvent(new ControlEvent(ControlAction.LOGIN_STATUS, null));
      } else if(input.equalsIgnoreCase("start")) {
        controls.fireEvent(new ControlEvent(ControlAction.TRANSFER_MANGA, null));
      } else if(waitingForSearchInput && isInt(input)) {
        waitingForSearchInput = false;
        int index = Integer.parseInt(input);
        controls.fireEvent(new ControlEvent(ControlAction.SEARCH_RESULT_SELECTED, index));
      }
    }
  }
  protected boolean isInt(String str) {
    try {
      Integer.parseInt(str);
    } catch(NumberFormatException nfe) {
      return false;
    }
    return true;
  }
  protected void promptLogin(String service, ControlAction act) {
    String user = console.readLine("Enter your %s username: ", service);
    String pass = new String(console.readPassword("Enter your %s password: ", service));
    ArrayList<String> loginDets = new ArrayList<String>();
    loginDets.add(user);
    loginDets.add(pass);

    controls.fireEvent(new ControlEvent(act, loginDets));

  }
  protected void promptMWLogin() {
    promptLogin("MangaWatcher", ControlAction.MW_LOGIN_INPUT);
  }
  protected void promptMALLogin() {
    promptLogin("MyAnimeList", ControlAction.MAL_LOGIN_INPUT);
  }
  protected void promptSearchInput(MALSearchResults search) {
    waitingForSearchInput = true;
    System.out.println("Could not find a perfect match withfor the manga: " + search.getQueryString());
    System.out.println("Please select one of the options from this list:");
    for(int i = 0; i < search.size(); i++) {
      System.out.format("[%d] %s", i, search.getTitle(i));
      System.out.println();
    }
    System.out.println("Input the number of the correct manga. Input '-1' to skip");
  }
  protected void displayProcessed(MWItem it) {
    System.out.format("Processed %s", it.toString());
    System.out.println();
  }
  protected void displayDropped(MWItem it) {
    System.out.format("---DROPPED %s", it.toString());
    System.out.println();
  }
  public void registerController(Controller c) {
    controls = c;
    c.register(new CLIControlListener());
  }

  private class CLIControlListener implements ControlListener {
    @SuppressWarnings("unchecked")
    public void fireEvent(ControlEvent ce) {
      switch(ce.getMessage()) {
        case CORRECT_MAL_LOGIN: {
          System.out.println("Login to MyAnimeList was successful.");
          break;
        }
        case INCORRECT_MAL_LOGIN: {
          System.out.println("ERROR: Unable to log into MyAnimeList.");
          break;
        }
        case CORRECT_MW_LOGIN: {
          System.out.println("Login to MangaWatcher was successful.");
          break;
        }
        case INCORRECT_MW_LOGIN: {
          System.out.println("ERROR: Unable to log into MangaWatcher.");
          break;
        }
        case DISPLAY_SEARCH: {
          promptSearchInput((MALSearchResults) ce.getData());
          break;
        }
        case ITEM_PROCESSED: {
          displayProcessed((MWItem) ce.getData());
          break;
        }
        case ITEM_DROPPED: {
          displayDropped((MWItem) ce.getData());
          break;
        }
      }
    }
  }
}
