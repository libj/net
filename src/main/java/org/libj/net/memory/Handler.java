/* Copyright (c) 2018 LibJ
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

package org.libj.net.memory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandlerFactory;

import org.libj.net.MemoryURLConnection;
import org.libj.net.MemoryURLStreamHandler;

/**
 * Handler class extending {@link MemoryURLStreamHandler}. This class is used for handler registration with the
 * {@code "java.protocol.handler.pkgs"} system property.
 */
public class Handler extends MemoryURLStreamHandler {
  public static class Factory implements URLStreamHandlerFactory { // FIXME: jdk9+ URLStreamHandlerProvider
    static {
      // Force Handler class to be loaded
      if (Handler.idToData == null);
    }

    private static Handler handler;

    @Override
    public Handler createURLStreamHandler(final String protocol) {
      return !"memory".equals(protocol) ? null : handler == null ? handler = new Handler() : handler;
    }
  }

  /**
   * @throws MalformedURLException If the provided {@link URL} specifies a protocol that is not {@code "memory"}, or a host that is
   *           not {@code null} or empty.
   * @throws FileNotFoundException If no data is registered for the provided {@link URL}.
   * @throws IOException If an I/O error occurs while opening the connection.
   * @throws NullPointerException If {@code url} is null.
   */
  @Override
  protected URLConnection openConnection(final URL url) throws IOException {
    if (!"memory".equals(url.getProtocol()))
      throw new MalformedURLException("Unsupported protocol: " + url.getProtocol());

    if (url.getHost() != null && url.getHost().length() > 0)
      throw new MalformedURLException("Unsupported host: " + url.getHost());

    final byte[] data = idToData.get(url.getPath());
    if (data == null)
      throw new FileNotFoundException("URL not registered: " + url);

    return new MemoryURLConnection(url, data);
  }
}