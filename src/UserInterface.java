public abstract class UserInterface {
  protected Controller controls;
  public enum UIActions {
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
    // Display an error and ask for the user to input correct login details
    INCORRECT_MW_LOGIN
  };
  public abstract void registerController(Controller c);
}
