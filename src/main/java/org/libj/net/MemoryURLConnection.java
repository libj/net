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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A {@link URLConnection} wrapping data in the form of a {@code byte} array.
 */
public class MemoryURLConnection extends URLConnection {
  private final ByteArrayInputStream in;

  /**
   * Creates a new {@link MemoryURLConnection} with the specified {@code url} and {@code data}.
   *
   * @param url The {@link URL}.
   * @param data The data.
   * @throws NullPointerException If the {@link URL} or {@code data} is null.
   */
  public MemoryURLConnection(final URL url, final byte[] data) {
    super(url);
    this.in = new ByteArrayInputStream(data);
  }

  @Override
  public void connect() {
  }

  @Override
  public InputStream getInputStream() {
    return in;
  }
}