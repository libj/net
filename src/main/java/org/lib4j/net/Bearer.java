package org.lib4j.net;

public class Bearer extends AuthScheme {
  private static final long serialVersionUID = 7583813022443974432L;

  public Bearer(final String username, final String password) {
    super(username, password);
  }
}