
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
