/* Copyright (c) 2010 LibJ
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

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.libj.util.CollectionUtil;

/**
 * Utility methods for management of cookies in {@link HttpServletRequest}s and
 * {@link HttpServletResponse}s.
 */
public final class Cookies {
  /**
   * Returns the cookie value associated with the given cookie name from the
   * given {@code request}.
   *
   * @param request The request.
   * @param name The cookie name.
   * @return The cookie value associated with the given cookie name, or null if
   *         the cookie {@code name} is not found in the {@code request}.
   * @throws NullPointerException If {@code request} or {@code name} is null.
   */
  public static String getCookieValue(final HttpServletRequest request, final String name) {
    final Cookie[] cookies = request.getCookies();
    if (cookies == null)
      return null;

    for (final Cookie cookie : cookies)
      if (cookie != null && name.equals(cookie.getName()))
        return cookie.getValue();

    return null;
  }

  /**
   * Set the cookie in the {@code response} with the provided {@code name},
   * {@code value}, and expiration {@code maxAge}.
   *
   * @param response The HttpServletResponse to be used.
   * @param name The cookie name to associate the cookie value with.
   * @param value The actual cookie value to be set in the given servlet
   *          response.
   * @param maxAge The expiration interval in seconds. If this is set to 0, then
   *          the cookie will immediately expire.
   * @throws NullPointerException If {@code response} is null.
   * @throws IllegalArgumentException If the cookie name is null or empty or
   *           contains any illegal characters (for example, a comma, space, or
   *           semicolon) or matches a token reserved for use by the cookie
   *           protocol.
   */
  public static void setCookieValue(final HttpServletResponse response, final String name, final String value, final int maxAge) {
    final Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  /**
   * Remove the cookie in the {@code response} with the provided {@code name}.
   * <p>
   * The cookie expiration interval is set to zero, resulting in the cookie
   * being expired immediately.
   *
   * @param response The HttpServletResponse to be used.
   * @param name The cookie name of the cookie to be removed.
   * @throws NullPointerException If {@code response} is null.
   * @throws IllegalArgumentException If the cookie name is null or empty or
   *           contains any illegal characters (for example, a comma, space, or
   *           semicolon) or matches a token reserved for use by the cookie
   *           protocol.
   */
  public static void removeCookie(final HttpServletResponse response, final String name) {
    setCookieValue(response, name, null, 0);
  }

  /**
   * Create a semicolon-delimited cookie header for the specified
   * {@code cookies}.
   *
   * @param cookies The collection of cookies.
   * @return A {@code Map.Entry<String,String>} with key set to
   *         {@code "Cookie"}, and value set to semicolon-delimited
   *         {@code cookies}.
   * @throws NullPointerException If {@code cookies} is null.
   */
  public static Map.Entry<String,String> createCookieHeader(final Collection<String> cookies) {
    return new AbstractMap.SimpleEntry<>("Cookie", CollectionUtil.toString(Objects.requireNonNull(cookies), ";"));
  }

  private Cookies() {
  }
}