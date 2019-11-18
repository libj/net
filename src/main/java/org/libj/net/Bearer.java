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
 * The "Authorization: Bearer" header schemes.
 */
public class Bearer extends AuthScheme {
  private static final long serialVersionUID = -1331849915362570916L;

  private final String token;

  /**
   * Creates a new {@link Bearer} instance with the specified token.
   *
   * @param token The token.
   * @throws NullPointerException If the specified token is null.
   */
  public Bearer(final String token) {
    this.token = Objects.requireNonNull(token);
  }

  /**
   * Creates a new {@link Bearer} instance with a null token.
   */
  protected Bearer() {
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
  public String name() {
    return "Bearer";
  }

  @Override
  protected Bearer decode(final String authorization) {
    return new Bearer(new String(Base64.getDecoder().decode(authorization.substring(7))));
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Bearer))
      return false;

    final Bearer that = (Bearer)obj;
    return token != null ? that.token != null : token.equals(that.token);
  }

  @Override
  public int hashCode() {
    return token == null ? 3 : token.hashCode();
  }
}