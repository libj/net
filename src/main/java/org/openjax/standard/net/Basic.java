/* Copyright (c) 2017 OpenJAX
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

package org.openjax.standard.net;

import java.util.Base64;
import java.util.Objects;

public class Basic extends AuthScheme {
  private final String username;
  private final String password;

  public Basic(final String username, final String password) {
    this.username = Objects.requireNonNull(username);
    this.password = Objects.requireNonNull(password);
  }

  protected Basic() {
    this.username = null;
    this.password = null;
  }

  public String getUsername() {
    return this.username;
  }

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
    final int index = login.indexOf(":");
    if (index == -1)
      throw new IllegalArgumentException("Authorization header is malformed: missing ':'");

    return new Basic(login.substring(0, index), login.substring(index + 1));
  }
}