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

package org.libj.net;

import java.net.URL;
import java.net.URLStreamHandler;
import java.util.concurrent.ConcurrentHashMap;

import org.libj.net.memory.Handler;

/**
 * A {@link URLStreamHandler} that implements the "memory" protocol. This class
 * can be used to create {@code memory:/...} URLs that are resolvable in the JVM
 * in which they are created.
 */
public abstract class MemoryURLStreamHandler extends URLStreamHandler {
  static {
    URLStreamHandlers.register(Handler.class, Handler.Factory.class);
  }

  protected static final ConcurrentHashMap<String,byte[]> idToData = new ConcurrentHashMap<>();

  /**
   * Returns a "memory" protocol {@link URL} for the specified {@code data}.
   *
   * @param data The data {@code byte} array.
   * @return A "memory" protocol {@link URL} for the specified {@code data}.
   */
  public static URL createURL(final byte[] data) {
    final String path = "/" + Integer.toHexString(System.identityHashCode(data));
    final URL url = URLs.create("memory", null, path);
    idToData.put(path, data);
    return url;
  }

  /**
   * Returns the data for the provided {@link URL}.
   *
   * @implNote This method only supports URLs with {@code "memory"} protocol,
   *           and a {@code null} or empty host.
   * @param url The {@link URL}.
   * @return The data for the provided {@link URL}.
   * @throws IllegalArgumentException If the provided {@link URL} specifies a
   *           protocol that is not {@code "memory"}, or a host that is not
   *           {@code null} or empty.
   * @throws IllegalArgumentException If the provided {@link URL} is null.
   */
  public static byte[] getData(final URL url) {
    if (!"memory".equals(url.getProtocol()))
      throw new IllegalArgumentException("Illegal protocol: " + url.getProtocol());

    if (url.getHost() != null && url.getHost().length() > 0)
      throw new IllegalArgumentException("Illegal host: " + url.getHost());

    return idToData.get(url.getPath());
  }
}