public class BadRequestParamsException extends RuntimeException {
  private static final long serialVersionUID = 4213231L;
  private String[] params;
  public BadRequestParamsException() {
    this(new String[0]);

  }
  public BadRequestParamsException(String[] params) {
    this.params = params;
  }

  @Override
  public String getMessage() {
    String mess = super.getMessage();
    mess += "|| Missing params: ";
    for(String p : params) {
      mess += p;
    }
    return mess;
  }
}
