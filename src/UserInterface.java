public abstract class UserInterface implements Runnable {
  protected Controller controls;
  public UserInterface() {
    startThread();
  }
  /**
   * Starts a new thread for the GUI. As swing will need a SwingWorker this is abstract
   */
  public abstract void startThread();
  public abstract void registerController(Controller c);
}
