
  public enum ControlAction {
    // VIEW ACTIONS
    // Display a list of search results
    DISPLAY_SEARCH,
    // Update a progress bar/message or something
    ITEM_PROCESSED,
    // When an item was not added/updated and we moved onto the next item
    ITEM_DROPPED,
    // Finished doing stuff so remove the progress bar/message and show completion message
    DONE_PROCESSING,
    // Tell the user that we can't transfer yet (probably not logged in)
    CANCEL_PROCESSING,
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
    // Stop transfering information from MW to MAL
    STOP_TRANSFER,
    // A search result was chosen as a match, even if the result was nothing
    SEARCH_RESULT_SELECTED,
    // Message the status of both services login information
    LOGIN_STATUS
  };
