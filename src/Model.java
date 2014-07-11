import java.util.ArrayList;
public class Model {
  Controller control;
  protected TransferQueue queue;
  public Model() {
    queue = null;
  }

  public void registerController(Controller c) {
    control = c;
    c.register(new ModelControlListener());
  }
  public void setMALLogin(String user, String pass) {
    MALRequest.setAuth(user, pass);
    ControlEvent ce;
    if(MALRequest.isAuthorized()) {
      ce = new ControlEvent(ControlAction.CORRECT_MAL_LOGIN, null);
    } else {
      ce = new ControlEvent(ControlAction.INCORRECT_MAL_LOGIN, null);
    }
    control.fireEvent(ce);
  }
  public void setMWLogin(String user, String pass) {
    MWRequest.setAuth(user, pass);
    ControlEvent ce;
    if(MWRequest.isAuthorized()) {
      ce = new ControlEvent(ControlAction.CORRECT_MW_LOGIN, null);
    } else {
      ce = new ControlEvent(ControlAction.INCORRECT_MW_LOGIN, null);
    }
    control.fireEvent(ce);
  }
  public void transferManga() {
    if(queue == null) {
      queue = new TransferQueue();
    }
    for(MWItem it : queue) {

      String title = it.getTitle();
      MALSearchResults malSearch = MALClient.searchMangas(title);
      int index = malSearch.getIdForTitle(title);

      ControlEvent ce;
      if(index < 0) {
        ce = new ControlEvent(ControlAction.DISPLAY_SEARCH, malSearch);
        control.fireEvent(ce);
        return ;
      } else {
        String malId = malSearch.getId(index);
        MALClient.addManga(malId);
        MALClient.updateManga(malId, String.valueOf(it.getChapter()));
        ce = new ControlEvent(ControlAction.ITEM_PROCESSED, it.toString());
        control.fireEvent(ce);
      }
    }
  }
  public void addSearchResult(int index) {
    MWItem it = queue.current();
    String title = it.getTitle();
    MALSearchResults malSearch = MALClient.searchMangas(title);

    // If an invalid choice was selected, just pass over this item.
    if(index < 0 || index >= malSearch.size()) {
      ControlEvent ce = new ControlEvent(ControlAction.ITEM_DROPPED, it.toString());
      control.fireEvent(ce);
      return ;
    }

    String malId = malSearch.getId(index);
    MALClient.addManga(malId);
    MALClient.updateManga(malId, String.valueOf(it.getChapter()));
    ControlEvent ce = new ControlEvent(ControlAction.ITEM_PROCESSED, it.toString());
    control.fireEvent(ce);
  }

  private class ModelControlListener implements ControlListener {
      @SuppressWarnings("unchecked")
      public void fireEvent(ControlEvent ce) {
        switch(ce.getMessage()) {
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
            transferManga();
            break;
          }
          case SEARCH_RESULT_SELECTED: {
            addSearchResult((Integer)ce.getData());
            transferManga();
          }
        }

      }
  }
}
