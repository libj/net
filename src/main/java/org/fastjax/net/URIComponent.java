/* Copyright (c) 2016 FastJAX
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

package org.fastjax.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class URIComponent {
  /**
   * Decodes the passed UTF-8 String using a specification that's compatible
   * with JavaScript's {@code decodeURIComponent} function. Returns {@code null}
   * if the String is {@code null}.
   *
   * @param uri The UTF-8 encoded String to be decoded
   * @return the decoded String
   */
  public static String decode(final String uri) {
    try {
      return decode(uri, "UTF-8");
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Decodes the passed String using a specification that's compatible with
   * JavaScript's {@code decodeURIComponent} function. Returns {@code null} if
   * the String is {@code null}.
   *
   * @param uri The encoded String to be decoded.
   * @param encoding The name of a supported character encoding.
   * @return The decoded String.
   * @throws UnsupportedEncodingException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
   */
  public static String decode(final String uri, final String encoding) throws UnsupportedEncodingException {
    return uri != null ? URLDecoder.decode(uri, encoding) : null;
  }

  /**
   * Encodes the passed String as UTF-8 using a specification that's compatible
   * with JavaScript's {@code encodeURIComponent} function. Returns {@code null}
   * if the String is {@code null}.
   *
   * @param uri The String to be encoded
   * @return the encoded String
   */
  public static String encode(final String uri) {
    try {
      return encode(uri, "UTF-8");
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Encodes the passed String using a specification that's compatible with
   * JavaScript's {@code encodeURIComponent} function. Returns {@code null} if
   * the String is {@code null}.
   *
   * @param uri The String to be encoded.
   * @param encoding The name of a supported character encoding.
   * @return The encoded String.
   * @throws UnsupportedEncodingException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
   */
  public static String encode(final String uri, final String encoding) throws UnsupportedEncodingException {
    return uri != null ? URLEncoder.encode(uri, encoding).replace("+", "%20") : null; //.replace("%21", "!").replace("%27", "'").replace("%28", "(").replace("%29", ")").replace("%7E", "~") : null;
  }

  private URIComponent() {
  }
}