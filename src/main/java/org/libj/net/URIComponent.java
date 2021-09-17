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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.libj.lang.Assertions;

/**
 * Utility functions for encoding and decoding URI strings using a specification
 * that is compatible with JavaScript's {@code decodeURIComponent} function.
 */
public final class URIComponent {
  /**
   * Decodes the provided string encoded in UTF-8 using a specification that is
   * compatible with JavaScript's {@code decodeURIComponent} function.
   *
   * @param uri The encoded string encoded in UTF-8 to be decoded.
   * @return The decoded string, or {@code null} if the provided string is null.
   * @throws UnsupportedOperationException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
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
   * Decodes the provided string using a specification that is compatible with
   * JavaScript's {@code decodeURIComponent} function.
   *
   * @param uri The encoded string to be decoded.
   * @param enc The name of a supported character encoding.
   * @return The decoded string, or {@code null} if the provided string is null.
   * @throws UnsupportedEncodingException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
   * @throws IllegalArgumentException If {@code enc} is null.
   */
  public static String decode(final String uri, final String enc) throws UnsupportedEncodingException {
    return uri != null ? URLDecoder.decode(uri, Assertions.assertNotNull(enc)) : null;
  }

  /**
   * Encodes the provided string as UTF-8 using a specification that is
   * compatible with JavaScript's {@code encodeURIComponent} function.
   *
   * @param uri The string to be encoded.
   * @return The encoded string, or {@code null} if the provided string is null.
   * @throws UnsupportedOperationException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
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
   * Encodes the provided {@code char} as UTF-8 using a specification that is
   * compatible with JavaScript's {@code encodeURIComponent} function.
   *
   * @param ch The {@code char} to be encoded.
   * @return The encoded string, or {@code null} if the provided string is null.
   * @throws UnsupportedOperationException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
   */
  public static String encode(final char ch) {
    try {
      return encode(ch, "UTF-8");
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Encodes the provided string using a specification that is compatible with
   * JavaScript's {@code encodeURIComponent} function.
   *
   * @param uri The string to be encoded.
   * @param enc The name of a supported character encoding.
   * @return The encoded string, or {@code null} if the provided string is null.
   * @throws UnsupportedEncodingException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
   * @throws IllegalArgumentException If {@code enc} is null.
   */
  public static String encode(final String uri, final String enc) throws UnsupportedEncodingException {
    return uri == null ? null : URLEncoder.encode(uri, Assertions.assertNotNull(enc)).replace("+", "%20"); //.replace("%21", "!").replace("%27", "'").replace("%28", "(").replace("%29", ")").replace("%7E", "~") : null;
  }

  /**
   * Encodes the provided {@code char} using a specification that is compatible
   * with JavaScript's {@code encodeURIComponent} function.
   *
   * @param ch The {@code char} to be encoded.
   * @param enc The name of a supported character encoding.
   * @return The encoded string, or {@code null} if the provided string is null.
   * @throws UnsupportedEncodingException If character encoding needs to be
   *           consulted, but named character encoding is not supported.
   * @throws IllegalArgumentException If {@code enc} is null.
   */
  // FIXME: Reimplement more efficiently
  public static String encode(final char ch, final String enc) throws UnsupportedEncodingException {
    return encode(String.valueOf(ch), enc);
  }

  private URIComponent() {
  }
}