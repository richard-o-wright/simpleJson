package net.wrightnz.simple.json;

import java.util.Base64;

/**
 *
 * @author Richard Wright
 */
public class BasicCredentials extends AuthHeader {

  public BasicCredentials(String username, String password) {
    super(AuthHeader.Type.Basic, asBasic(username, password));
  }

  private static String asBasic(String username, String password) {
    StringBuilder sb = new StringBuilder();
    sb.append("Basic ");
    byte[] credential = (username + ":" + password).getBytes();
    sb.append(Base64.getEncoder().encodeToString(credential));
    return sb.toString();
  }
}
