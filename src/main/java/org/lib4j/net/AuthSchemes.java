/* Copyright (c) 2016 lib4j
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

package org.lib4j.net;

import javax.xml.bind.DatatypeConverter;

import org.lib4j.security.Credentials;

public final class AuthSchemes {
  public static Credentials parseBasicAuthHeader(final String authorization) {
    if (authorization == null)
      return null;

    if (!authorization.startsWith("Basic "))
      throw new IllegalArgumentException("Auth header is expected to be 'Basic', but was found to be: '" + authorization + "'");

    final String login = new String(DatatypeConverter.parseBase64Binary(authorization.substring(6)));
    final int index = login.indexOf(":");
    if (index == -1)
      throw new IllegalArgumentException("Auth header is malformed: missing ':'");

    return new Credentials(login.substring(0, index), login.substring(index + 1));
  }

  private AuthSchemes() {
  }
}