public class ControlEvent {
  // The message. Will usually be an enum value
  private ControlAction message;
  // Any data passed.
  private Object data;
  public ControlEvent(ControlAction m) {
    this(m, null);
  }
  public ControlEvent(ControlAction m, Object data) {
    message = m;
    this.data = data;
  }
  public ControlAction getMessage() {
  return message;
  }
  public Object getData() {
    return data;
  }
}
