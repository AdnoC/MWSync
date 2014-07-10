public class MessageEvent<T, H> {
  // The message. Will usually be an enum value
  private T message;
  // Any data passed.
  private H data;
  public MessageEvent(T m, H data) {
    message = m;
    this.data = data;
  }
  public T getMessage() {
  return message;
  }
  public H getData() {
    return data;
  }
}
