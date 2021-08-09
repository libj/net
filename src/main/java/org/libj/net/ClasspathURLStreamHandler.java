/* Copyright (c) 2020 LibJ
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

import java.net.URL;
import java.net.URLStreamHandler;

import org.libj.lang.Assertions;
import org.libj.net.classpath.Handler;

public abstract class ClasspathURLStreamHandler extends URLStreamHandler {
  static {
    URLStreamHandlers.register(Handler.class, Handler.Factory.class);
  }

  protected static boolean loadMe = true;

  /**
   * Returns a "classpath" protocol {@link URL} for the specified resource path.
   *
   * @param resourcePath The resource path.
   * @return A "classpath" protocol {@link URL} for the specified resource path.
   * @throws IllegalArgumentException If {@code resourcePath} is null.
   */
  public static URL createURL(final String resourcePath) {
    return URLs.create("classpath:" + Assertions.assertNotNull(resourcePath));
  }

  /**
   * Returns the first matching resource {@link URL} for the provided classpath
   * {@link URL}.
   * <p>
   * <b>Note:</b> This method only supports URLs with {@code "classpath"}
   * protocol, and a {@code null} or empty host.
   *
   * @param url The {@link URL}.
   * @return The data for the provided {@link URL}.
   * @throws IllegalArgumentException If {@code url} is null, or if {@code url}
   *           specifies a protocol that is not {@code "classpath"}, or a host
   *           that is not {@code null} or empty.
   */
  public static URL getResource(final URL url) {
    if (!"classpath".equals(Assertions.assertNotNull(url).getProtocol()))
      throw new IllegalArgumentException("Illegal protocol: " + url.getProtocol());

    if (url.getHost() != null && url.getHost().length() > 0)
      throw new IllegalArgumentException("Illegal host: " + url.getHost());

    return Thread.currentThread().getContextClassLoader().getResource(url.getPath());
  }
}