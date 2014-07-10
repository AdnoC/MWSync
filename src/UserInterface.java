public abstract class UserInterface {
  protected Controller controls;
  public enum UIActions {
    DISPLAY_LIST, ITEM_PROCESSED, DONE_PROCESSING, INCORRECT_MAL_LOGIN, INCORRECT_MW_LOGIN
  };
  public abstract void registerController(Controller c);
}
