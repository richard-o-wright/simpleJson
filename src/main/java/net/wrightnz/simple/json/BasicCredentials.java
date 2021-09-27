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
    byte[] credential = (username + ":" + password).getBytes();
    return Base64.getEncoder().encodeToString(credential);
  }
}
