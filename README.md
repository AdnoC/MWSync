MWSync
======

This is a tool to sync MangaWatcher (http://www.manga-watcher.com/) and MyAnimeList
(http://www.myanimelist.net). It syncs read mangas from a MW account to one's MAL account.
At the moment it overwrites the number of chapter read on MAL with the value from MW.

To run:
* Go to Releases and download MWSync.jar.
* Double click the jar to run it with a graphical user interface.
* Run the jar from the command line to use a command line interface.

Status: Release 1.0 is complete. Both the GUI and CLI are up and running.

### Todo:
* Save search resolutions so that repeat syncs are faster.
* Add a check before updating to make sure you aren't setting MAL read chapters to a smaller value
  than is already set.
* Clean up all the sloppy coding I did in terms of multi-threading
* Write comments for EVERYTHING
