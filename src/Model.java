import java.util.ArrayList;
public class Model {
  Controller control;
  protected TransferQueue queue;
  protected boolean stopTransfer = false;
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
      ce = new ControlEvent(ControlAction.CORRECT_MAL_LOGIN, user);
      Settings.SETTINGS.setMALName(user);
    } else {
      ce = new ControlEvent(ControlAction.INCORRECT_MAL_LOGIN, user);
    }
    control.fireEvent(ce);
  }

  public void setMWLogin(String user, String pass) {
    MWRequest.setAuth(user, pass);
    ControlEvent ce;
    if(MWRequest.isAuthorized()) {
      ce = new ControlEvent(ControlAction.CORRECT_MW_LOGIN, user);
      Settings.SETTINGS.setMWName(user);
    } else {
      ce = new ControlEvent(ControlAction.INCORRECT_MW_LOGIN, user);
    }
    control.fireEvent(ce);
  }

  public void messageLoginStatus() {
    ControlEvent ce;
    if(MALRequest.isAuthorized()) {
      ce = new ControlEvent(ControlAction.CORRECT_MAL_LOGIN, null);
    } else {
      ce = new ControlEvent(ControlAction.INCORRECT_MAL_LOGIN, null);
    }
    control.fireEvent(ce);
    if(MWRequest.isAuthorized()) {
      ce = new ControlEvent(ControlAction.CORRECT_MW_LOGIN, null);
    } else {
      ce = new ControlEvent(ControlAction.INCORRECT_MW_LOGIN, null);
    }
    control.fireEvent(ce);
  }

  public void transferManga() {
    if(stopTransfer) {
      return ;
    }
    if(!MWRequest.isAuthorized() || !MALRequest.isAuthorized()) {
      messageLoginStatus();
      return;
    }
    if(queue == null) {
      queue = new TransferQueue();
    }
    for(MWItem it : queue) {
      // Try to get the id quickly if we processed this once already
      String malId = Settings.SETTINGS.getMALId(it.getHash());
      MangaItem malsr;
      ControlEvent ce;

      // If we did not have this stored...
      if(malId == null) {
        // Get the title of the MW item
        String title = it.getDecodedTitle();
        // Search MAL for the title
        MALSearchResults malSearch = MALClient.searchMangas(title);
        // If the search was not successful, drop the item
        if(malSearch == null || malSearch.size() == 0) {
          control.fireEvent(new ControlEvent(ControlAction.ITEM_DROPPED, it));
          continue;
        }

        // Try to find an exact match for the title in the earch results
        int index = malSearch.getIdForTitle(title);
        // If we did not find one...
        if(index < 0) {
          // Present the user with a choice to select the correct search result
          ce = new ControlEvent(ControlAction.DISPLAY_SEARCH, malSearch);
          control.fireEvent(ce);
          return ;
        // If we did find a match...
        } else {
          // Get the match
          malsr = malSearch.get(index);
          // Set the chapter in the result
          ((MALSearchResults.MALSearchResult) malsr).chapter = it.getChapter();
          // Get the MAL id
          malId = ((MALSearchResults.MALSearchResult)malsr).getId();
          // Store this match for future searches
          Settings.SETTINGS.mapMangaPair(it.getHash(), malId);
        }
      // If we had a stored match...
      } else {
        // just set the current MW item as the search result
        malsr = it;
      }

      // Add the manga and set its chapter
      MALClient.addManga(malId);
      MALClient.updateManga(malId, String.valueOf(it.getChapter()));
      // Tell the user we proccessed the item
      ce = new ControlEvent(ControlAction.ITEM_PROCESSED, malsr);
      control.fireEvent(ce);

      // If we need to stop, do so
      if(stopTransfer) {
        return ;
      }
    }
  }

  public void addSearchResult(int index) {
    if(queue == null) {
      return ;
    }
    MWItem it = queue.current();
    String title = it.getDecodedTitle();
    MALSearchResults malSearch = MALClient.searchMangas(title);

    // If an invalid choice was selected, just pass over this item.
    if(index < 0 || index >= malSearch.size()) {
      ControlEvent ce = new ControlEvent(ControlAction.ITEM_DROPPED, it);
      control.fireEvent(ce);
      return ;
    }
    MALSearchResults.MALSearchResult malsr = malSearch.get(index);
    String malId = malsr.getId();
    Settings.SETTINGS.mapMangaPair(it.getHash(), malId);
    MALClient.addManga(malId);
    MALClient.updateManga(malId, String.valueOf(it.getChapter()));
    malsr.chapter = it.getChapter();
    ControlEvent ce = new ControlEvent(ControlAction.ITEM_PROCESSED, malsr);
    control.fireEvent(ce);
  }

  private class ModelControlListener implements ControlListener {
      @SuppressWarnings("unchecked")
      public void fireEvent(ControlEvent ce) {
        switch(ce.getMessage()) {
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
            stopTransfer = false;
            transferManga();
            break;
          }
          case SEARCH_RESULT_SELECTED: {
            addSearchResult((Integer)ce.getData());
            transferManga();
            break;
          }
          case LOGIN_STATUS: {
            messageLoginStatus();
            break;
          }
          case STOP_TRANSFER: {
            stopTransfer = true;
            break;
          }
        }

      }
  }
}
