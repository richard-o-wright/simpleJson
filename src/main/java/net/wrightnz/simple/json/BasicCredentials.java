package net.wrightnz.simple.json;

import java.util.Base64;

/**
 *
 * @author Richard Wright
 */
public class BasicCredentials {

  private String username;
  private String password;

  public BasicCredentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  BasicCredentials() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  public String asBasic() {
    StringBuilder sb = new StringBuilder();
    sb.append("Basic ");
    byte[] credential = (username + ":" + password).getBytes();    
    sb.append(Base64.getEncoder().encodeToString(credential));
    return sb.toString();
  }

  @Override
  public String toString() {
    return "BasicCredentials{username=" + username + ", password=" + password + '}';
  }

}
