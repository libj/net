/* Copyright (c) 2016 lib4j
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

package org.lib4j.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.lib4j.io.input.RewindableInputStream;

public class CachedURL {
  private final URL url;
  private RewindableInputStream in;
  private URLConnection connection;

  public CachedURL(final URL url) {
    this.url = url;
  }

  public CachedURL(final String spec) throws MalformedURLException {
    this.url = new URL(spec);
  }

  public CachedURL(final String protocol, final String host, final String file) throws MalformedURLException {
    this.url = new URL(protocol, host, file);
  }

  public synchronized URLConnection openConnection() throws IOException {
    return connection == null ? connection = url.openConnection() : connection;
  }

  public synchronized final InputStream openStream() throws IOException {
    return in == null ? in = new RewindableInputStream(openConnection().getInputStream()) : in;
  }

  public void destroy() throws IOException {
    if (in != null)
      in.destroy();
  }

  public void reset() throws IOException {
    if (in != null)
      in.close();
  }

  public boolean isLocal() {
    return URLs.isLocal(url);
  }

  public URL toURL() {
    return url;
  }

  /**
   * Constructs a string representation of this {@code URL}. The
   * string is created by calling the {@code toExternalForm}
   * method of the stream protocol handler for this object.
   *
   * @return  a string representation of this object.
   * @see     URL#URL(String, String,
   *                  int, String)
   * @see     URLStreamHandler#toExternalForm(URL)
   */
  public String toExternalForm() {
    return url.toExternalForm();
  }

  @Override
  public String toString() {
    return url.toString();
  }
}