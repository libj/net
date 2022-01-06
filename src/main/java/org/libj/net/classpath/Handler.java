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

package org.libj.net.classpath;

import static org.libj.lang.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.libj.net.ClasspathURLStreamHandler;

/**
 * A {@link URLStreamHandler} that implements the "classpath" protocol. This class can be used to create {@code classpath:/...} URLs
 * that are resolvable as {@link ClassLoader} resources.
 */
public class Handler extends ClasspathURLStreamHandler {
  public static class Factory implements URLStreamHandlerFactory { // FIXME: jdk9+ URLStreamHandlerProvider
    static {
      // Force Handler class to be loaded
      if (Handler.loadMe);
    }

    private static Handler handler;

    @Override
    public Handler createURLStreamHandler(final String protocol) {
      return !"classpath".equals(protocol) ? null : handler == null ? handler = new Handler() : handler;
    }
  }

  /**
   * @throws MalformedURLException If the provided {@link URL} specifies a protocol that is not {@code "classpath"}.
   * @throws FileNotFoundException If no resource exists at the provided {@link URL}.
   * @throws IOException If an I/O error occurs while opening the connection.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  @Override
  protected URLConnection openConnection(final URL url) throws IOException {
    if (!"classpath".equals(assertNotNull(url).getProtocol()))
      throw new MalformedURLException("Unsupported protocol: " + url.getProtocol());

    final String resourcePath = url.toString().substring(12);
    final URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
    if (resourceUrl == null)
      throw new FileNotFoundException(resourcePath);

    return resourceUrl.openConnection();
  }
}