/* Copyright (c) 2016 LibJ
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

import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;

import org.libj.lang.Assertions;
import org.libj.lang.Strings;

/**
 * The {@link AuthScheme} class represents a strong type representation of the
 * "Authorization" header schemes. This class allows an "Authorization" scheme
 * to be parsed to an instance of {@link AuthScheme}.
 *
 * @see Basic
 * @see Bearer
 */
public abstract class AuthScheme implements Serializable {
  /**
   * The "Authorization: Basic" header schemes.
   */
  public static class Basic extends AuthScheme {
    private static final String name = "Basic";
    private static final Basic prototype = new Basic();

    /**
     * Returns a header string encoding of the provided {@code username} and
     * {@code password}.
     *
     * @param username The username.
     * @param password The password.
     * @return A header string encoding of the provided {@code username} and
     *         {@code password}.
     */
    public static String encode(final String username, final String password) {
      return name + " " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    /**
     * Returns a {@link Basic} instance by decoding the {@code authorization}
     * header string, or {@code null} if the provided {@code authorization} does
     * not match.
     *
     * @param authorization The "Authorization" header string.
     * @return A {@link Basic} instance by decoding the {@code authorization}
     *         header string, or {@code null} if the provided
     *         {@code authorization} does not match.
     * @throws IllegalArgumentException If {@code authorization} is null.
     */
    public static Basic decode(final String authorization) {
      if (!prototype.matches(Assertions.assertNotNull(authorization)))
        return null;

      final String login = new String(Base64.getDecoder().decode(authorization.substring(6)));
      final int index = login.indexOf(':');
      if (index == -1)
        throw new IllegalArgumentException("Authorization header is malformed: missing ':'");

      return new Basic(login.substring(0, index), login.substring(index + 1));
    }

    private final String username;
    private final String password;

    /**
     * Creates a new {@link Basic} instance with the specified username and
     * password.
     *
     * @param username The username.
     * @param password The password.
     * @throws IllegalArgumentException If the specified {@code username} or
     *           {@code password} is null.
     */
    public Basic(final String username, final String password) {
      super(name);
      this.username = Assertions.assertNotNull(username);
      this.password = Assertions.assertNotNull(password);
    }

    /**
     * Creates a new {@link Basic} instance with a null username and password..
     */
    private Basic() {
      super(name);
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
    public String encode() {
      return encode(username, password);
    }

    @Override
    Basic newInstance(final String authorization) {
      return decode(authorization);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this)
        return true;

      if (!(obj instanceof Basic))
        return false;

      final Basic that = (Basic)obj;
      if (!Objects.equals(username, that.username))
        return false;

      if (!Objects.equals(password, that.password))
        return false;

      return true;
    }

    @Override
    public int hashCode() {
      int hashCode = 1;
      if (username != null)
        hashCode = 31 * hashCode + username.hashCode();

      if (password != null)
        hashCode = 31 * hashCode + password.hashCode();

      return hashCode;
    }
  }

  /**
   * The "Authorization: Bearer" header schemes.
   */
  public static class Bearer extends AuthScheme {
    private static final String name = "Bearer";
    private static final Bearer prototype = new Bearer();

    /**
     * Returns a header string encoding of the provided {@code token}.
     *
     * @param token The token.
     * @return A header string encoding of the provided {@code token}.
     */
    public static String encode(final String token) {
      return name + " " + Base64.getEncoder().encodeToString(token.getBytes());
    }

    /**
     * Returns a {@link Bearer} instance by decoding the {@code authorization}
     * header string, or {@code null} if the provided {@code authorization} does
     * not match.
     *
     * @param authorization The "Authorization" header string.
     * @return A {@link Bearer} instance by decoding the {@code authorization}
     *         header string, or {@code null} if the provided
     *         {@code authorization} does not match.
     * @throws IllegalArgumentException If {@code authorization} is null.
     */
    public static Bearer decode(final String authorization) {
      return prototype.matches(Assertions.assertNotNull(authorization)) ? new Bearer(new String(Base64.getDecoder().decode(authorization.substring(7)))) : null;
    }

    private final String token;

    /**
     * Creates a new {@link Bearer} instance with the specified token.
     *
     * @param token The token.
     * @throws IllegalArgumentException If the specified token is null.
     */
    public Bearer(final String token) {
      super(name);
      this.token = Assertions.assertNotNull(token);
    }

    /**
     * Creates a new {@link Bearer} instance with a null token.
     */
    private Bearer() {
      super(name);
      this.token = null;
    }

    /**
     * Returns the token.
     *
     * @return The token.
     */
    public String getToken() {
      return this.token;
    }

    @Override
    public String encode() {
      return encode(token);
    }

    @Override
    Bearer newInstance(final String authorization) {
      return decode(authorization);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this)
        return true;

      if (!(obj instanceof Bearer))
        return false;

      return Objects.equals(token, ((Bearer)obj).token);
    }

    @Override
    public int hashCode() {
      int hashCode = 1;
      if (token != null)
        hashCode = 31 * hashCode + token.hashCode();

      return hashCode;
    }
  }


  /**
   * Returns an instance of an {@link AuthScheme} matching the provided
   * {@code authorization} header string, or {@code null} if
   * {@code authorization} is null or no match is found.
   *
   * @param authorization The "Authorization" header string to match.
   * @return An instance of an {@link AuthScheme} matching the provided
   *         {@code authorization} header string, or {@code null} if
   *         {@code authorization} is null or no match is found.
   * @throws UnsupportedOperationException If an {@link AuthScheme} class in
   *           {@code schemes} does not implement a private default constructor,
   *           or if the constructor throws an exception when invoked.
   */
  public static AuthScheme parse(final String authorization) {
    if (authorization == null)
      return null;

    if (Basic.prototype.matches(authorization))
      return Basic.prototype.newInstance(authorization);

    if (Bearer.prototype.matches(authorization))
      return Bearer.prototype.newInstance(authorization);

    return null;
  }

  private final String name;
  private final int len;

  private AuthScheme(final String name) {
    this.name = name;
    this.len = name.length();
  }

  /**
   * Tests whether the {@code authorization} header string matches this
   * {@link AuthScheme} subclass's implementation.
   *
   * @param authorization The "Authorization" header string.
   * @return {@code true} if the {@code authorization} header string matches
   *         this {@link AuthScheme} subclass's implementation; otherwise
   *         {@code false}.
   */
  public final boolean matches(final String authorization) {
    return authorization != null && authorization.length() > len && authorization.charAt(len) == ' ' && Strings.regionMatches(authorization, true, 0, name, 0, len);
  }

  /**
   * Returns a header string encoding of this {@link AuthScheme}.
   *
   * @return A header string encoding of this {@link AuthScheme}.
   */
  public abstract String encode();

  abstract AuthScheme newInstance(String authorization);
}