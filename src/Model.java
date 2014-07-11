import java.util.ArrayList;
public class Model {
  Controller control;

  public static String MWUsername = "";
  public static String MWPassword = "";

  public void registerController(Controller c) {
    control = c;
    c.register(new ModelControlListener());
  }
  public void setMALLogin(String user, String pass) {
    MALRequest.setAuth(user, pass);
    ControlEvent ce;
    if(MALRequest.isAuthorized()) {
      ce = new ControlEvent(Controller.ControlAction.CORRECT_MAL_LOGIN, null);
    } else {
      ce = new ControlEvent(Controller.ControlAction.INCORRECT_MAL_LOGIN, null);
    }
    control.fireEvent(ce);
  }
  public void setMWLogin(String user, String pass) {
    MWRequest.setAuth(user, pass);
    ControlEvent ce;
    if(MWRequest.isAuthorized()) {
      ce = new ControlEvent(Controller.ControlAction.CORRECT_MW_LOGIN, null);
    } else {
      ce = new ControlEvent(Controller.ControlAction.INCORRECT_MW_LOGIN, null);
    }
    control.fireEvent(ce);
  }

  private class ModelControlListener implements ControlListener {
      @SuppressWarnings("unchecked")
      public void fireEvent(ControlEvent ce) {
        switch(ce.getMessage()) {
    //MW_LOGIN_INPUT,
    //SEARCH_MANGA
          case MAL_LOGIN_INPUT: {
            ArrayList<String> loginDetails = (ArrayList<String>) ce.getData();
            setMALLogin(loginDetails.get(0), loginDetails.get(1));
            break;
          }
          case MW_LOGIN_INPUT: {
            ArrayList<String> loginDetails = (ArrayList<String>) ce.getData();
            setMWLogin(loginDetails.get(0), loginDetails.get(1));
            break;
          }
          case TRANSFER_MANGA: {
            //transferManga();
            break;
          }
        }

      }
  }
}
