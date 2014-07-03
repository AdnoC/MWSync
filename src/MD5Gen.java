import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Gen {
  public static void main(String[] args) {
    String str = encryptPass("email@address.com", "password");
    System.out.println(str);
  }
  public static String encryptPass(String user, String pass) {
    String str = getWrongStringToMD5(pass);
    str = getWrongStringToMD5(user + str);
    return str;

  }

  public static String getWrongStringToMD5(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(paramString.getBytes());
      byte[] arrayOfByte = localMessageDigest.digest();
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0;; i++)
      {
        if (i >= arrayOfByte.length) {
          return localStringBuffer.toString();
        }
        localStringBuffer.append(Integer.toHexString(0xFF & arrayOfByte[i]));
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace();
    }
      return "";
  }
}
