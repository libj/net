/* Copyright (c) 2017 LibJ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.libj.net;

import java.util.Base64;
import java.util.Objects;

/**
 * The "Authorization: Basic" header schemes.
 */
public class Basic extends AuthScheme {
  private static final long serialVersionUID = 3306364066082327042L;

  private final String username;
  private final String password;

  /**
   * Creates a new {@link Basic} instance with the specified username and
   * password.
   *
   * @param username The username.
   * @param password The password.
   * @throws NullPointerException If the specified username and password is
   *           null.
   */
  public Basic(final String username, final String password) {
    this.username = Objects.requireNonNull(username);
    this.password = Objects.requireNonNull(password);
  }

  /**
   * Creates a new {@link Basic} instance with a null username and password..
   */
  protected Basic() {
    this.username = null;
    this.password = null;
  }

  /**
   * Returns the username.
   *
   * @return The username.
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Returns the password.
   *
   * @return The password.
   */
  public String getPassword() {
    return this.password;
  }

  @Override
  public String name() {
    return "Basic";
  }

  @Override
  protected Basic decode(final String authorization) {
    final String login = new String(Base64.getDecoder().decode(authorization.substring(6)));
    final int index = login.indexOf(':');
    if (index == -1)
      throw new IllegalArgumentException("Authorization header is malformed: missing ':'");

    return new Basic(login.substring(0, index), login.substring(index + 1));
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Basic))
      return false;

    final Basic that = (Basic)obj;
    if (username != null ? that.username == null : !username.equals(that.username))
      return false;

    if (password != null ? that.password == null : !password.equals(that.password))
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return (username == null ? 3 : username.hashCode()) * (password == null ? 7 : password.hashCode());
  }
}