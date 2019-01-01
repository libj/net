/* Copyright (c) 2018 OpenJAX
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

package org.openjax.classic.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Enumeration;
import java.util.HashMap;

import org.openjax.classic.io.Streams;
import org.openjax.classic.net.memory.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link URLStreamHandler} that implements the "memory" protocol. This class
 * can be used to create {@code memory:/...} URLs that are resolvable in the JVM
 * in which they are created.
 */
public abstract class MemoryURLStreamHandler extends URLStreamHandler {
  private static final Logger logger = LoggerFactory.getLogger(MemoryURLStreamHandler.class);

  private static boolean canLookupViaProvider(final String className) throws IOException {
    final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources("META-INF/services/java.net.spi.URLStreamHandlerProvider");
    while (resources.hasMoreElements()) {
      final URL url = resources.nextElement();
      try (final InputStream in = url.openStream()) {
        if (new String(Streams.readBytes(in)).contains(className)) {
          return true;
        }
      }
    }

    return false;
  }

  private static boolean canLookupViaProperty(final String className) {
    try {
      ClassLoader.getSystemClassLoader().loadClass(className);
      return true;
    }
    catch (final ClassNotFoundException e) {
      return false;
    }
  }

  static {
    try {
      final String className = Handler.class.getName();
      if (!canLookupViaProvider(className)) {
        final String property = "java.protocol.handler.pkgs";
        final String pkgs = System.getProperty(property);
        final String pkg = MemoryURLStreamHandler.class.getPackage().getName();
        if (pkgs == null || !pkgs.contains(pkg))
          System.setProperty(property, pkgs != null && pkgs.length() > 0 ? pkgs + "|" + pkg : pkg);

        if (!canLookupViaProperty(className)) {
          logger.warn("Unable to register " + MemoryURLStreamHandler.class.getName() + " via \"provider\" nor \"property\" methods, so resorting to URL.setURLStreamHandlerFactory()");
          URL.setURLStreamHandlerFactory(new Handler.Provider());
        }
      }
    }
    catch (final IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  protected static final HashMap<String,byte[]> idToData = new HashMap<>();

  /**
   * Creates a "memory" protocol {@link URL} for the specified {@code data}.
   *
   * @param data The data {@code byte} array.
   * @return A "memory" protocol {@link URL} for the specified {@code data}.
   */
  public static URL createURL(final byte[] data) {
    try {
      final String path = "/" + Integer.toHexString(System.identityHashCode(data));
      final URL url = new URL("memory", null, path);

      idToData.put(path, data);
      return url;
    }
    catch (final MalformedURLException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Returns the data for the provided {@code URL}.
   * <p>
   * <i><b>Note</b>: This method only supports URLs with {@code "memory"}
   * protocol, and a {@code null} or empty host.</i>
   *
   * @param url The {@code URL}.
   * @return The data for the provided {@code URL}.
   * @throws IllegalArgumentException If the provided {@code URL} specifies a
   *           protocol that is not {@code "memory"}, or a host that is not
   *           {@code null} or empty.
   * @throws NullPointerException If {@code url} is null.
   */
  public static byte[] getData(final URL url) {
    if (!"memory".equals(url.getProtocol()))
      throw new IllegalArgumentException("Illegal protocol: " + url.getProtocol());

    if (url.getHost() != null && url.getHost().length() > 0)
      throw new IllegalArgumentException("Illegal host: " + url.getHost());

    return idToData.get(url.getPath());
  }
}