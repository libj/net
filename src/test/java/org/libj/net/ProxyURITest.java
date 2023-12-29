/* Copyright (c) 2023 LibJ
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

import static org.junit.Assert.*;

import org.junit.Test;

public class ProxyURITest {
  @Test
  @SuppressWarnings("unused")
  public void testMalformedUri() {
    final String[] uris = {"1.2.3.4:5678", "socks:/1.2.3.4:5678", "socks://foo&bar@1.2.3.4:5678", "socks://foo:bar&1.2.3.4:5678"};
    for (final String uri : uris) { // [A]
      try {
        new ProxyURI(uri);
        fail("Expected ProcessingException");
      }
      catch (final IllegalArgumentException e) {
        if (!e.getMessage().startsWith("Malformed Proxy URI: "))
          throw e;
      }
    }
  }
}