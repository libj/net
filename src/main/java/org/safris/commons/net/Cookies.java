/* Copyright (c) 2010 lib4j
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

package org.safris.commons.net;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class Cookies {
  /**
   * Retrieve the cookie value from the given servlet request based on the given
   * cookie name.
   * @param request The HttpServletRequest to be used.
   * @param name The cookie name to retrieve the value for.
   * @return The cookie value associated with the given cookie name.
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
   * Set the cookie value in the given servlet response based on the given cookie
   * name and expiration interval.
   * @param response The HttpServletResponse to be used.
   * @param name The cookie name to associate the cookie value with.
   * @param value The actual cookie value to be set in the given servlet response.
   * @param maxAge The expiration interval in seconds. If this is set to 0,
   * then the cookie will immediately expire.
   */
  public static void setCookieValue(final HttpServletResponse response, final String name, String value, final int maxAge) {
    final Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  /**
   * Remove the cookie from the given servlet response based on the given cookie
   * name. It actually sets the cookie expiration interval to zero, resulting the
   * cookie being expired immediately.
   * @param response The HttpServletResponse to be used.
   * @param name The cookie name of the cookie to be removed.
   */
  public static void removeCookie(final HttpServletResponse response, final String name) {
    setCookieValue(response, name, null, 0);
  }

  public static Map.Entry<String,String> createCookieHeader(final Collection<String> cookies) {
    final StringBuffer cookieStringBuffer = new StringBuffer();
    for (final String cookie : cookies)
      cookieStringBuffer.append("; ").append(cookie);

    final String value = cookieStringBuffer.length() != 0 ? cookieStringBuffer.substring(2) : "";
    return Collections.<String,String>singletonMap("Cookie", value).entrySet().iterator().next();
  }

  private Cookies() {
  }
}