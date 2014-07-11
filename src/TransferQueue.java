import java.util.Iterator;
public class TransferQueue implements Iterable<MWItem>{
  protected TransferIterator myIter;

  public TransferQueue() {
    myIter = new TransferIterator();
  }
  public MWItem current() {
    return myIter.current();
  }
  public Iterator<MWItem> iterator() {
    return myIter;
  }

  private class TransferIterator implements Iterator<MWItem> {
    protected  MWItem current;
    protected  MWRequest mwReq;
    protected  Iterator<MWItem> queue;

    public TransferIterator() {
      current = null;
      mwReq = new MWRequest();
      updateQueue();
    }

    public MWItem next() {
      ensureQueue();
      MWItem ret = queue.next();
      current = ret;
      return ret;
    }

    public boolean hasNext() {
      ensureQueue();
      return queue.hasNext();
    }

    protected void ensureQueue() {
      if(queue == null || !queue.hasNext()) {
        updateQueue();
      }
    }
    public void updateQueue() {
      queue = mwReq.getMangas().iterator();
    }
    public MWItem current() {
      return current;
    }
    public void remove() {
      // Not implemented
    }
  }


  //public void transfer() {
    //if(queue.hasNext()) {
      //MWItem it = queue.next();
    //}
  //}
  //public void transfer2() {
    //if(queue == null || !queue.hasNext()) {
      //updateQueue();
    //}
    //while(queue.hasNext()) {
      //MWItem it = queue.next();
      //String title = it.getTitle();
      //MWItem malSearch = malCli.searchMangas(title);
      //int index = malSearch.geIdForTitle(title);
      //if(index < 0) {

      //}
    //}
  //}
}
