package net.wrightnz.simple.json;

/**
 *
 * @author Richard Wright
 */
public class AuthHeader {

  public static final String NAME = "Authorization";

  public enum Type {
    OAuth,
    Bearer,
    Basic
  }

  private final Type type;
  private final String uniquePart;

  public AuthHeader(Type type, String uniquePart) {
    this.type = type;
    this.uniquePart = uniquePart;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return type.name() + " " + uniquePart;
  }

}
