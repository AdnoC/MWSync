public class ControlEvent {
  // The message. Will usually be an enum value
  private Controller.ControlAction message;
  // Any data passed.
  private Object data;
  public ControlEvent(Controller.ControlAction m, Object data) {
    message = m;
    this.data = data;
  }
  public Controller.ControlAction getMessage() {
  return message;
  }
  public Object getData() {
    return data;
  }
}
