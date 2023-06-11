/* Copyright (c) 2009 LibJ
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

import static org.libj.lang.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.libj.util.function.ThrowingConsumer;

/**
 * Utility functions pertaining to {@link URLConnection}.
 */
public final class URLConnections {
  /**
   * Returns an {@link URLConnection} that represents the terminal end of all redirects followed, or the provided
   * {@link URLConnection} if a redirect is not present.
   *
   * @param connection The {@link URLConnection}.
   * @return An {@link InputStream} to the specified url that may or may not exist at a redirected location.
   * @throws IOException If an I/O error has occurred, or if the redirects are found to loop.
   * @throws NullPointerException If the provided {@link URLConnection} is null.
   */
  public static URLConnection checkFollowRedirect(final URLConnection connection) throws IOException {
    return checkFollowRedirect(connection, Integer.MAX_VALUE, null);
  }

  /**
   * Returns an {@link URLConnection} that represents the terminal end of all redirects followed, or the provided
   * {@link URLConnection} if a redirect is not present.
   *
   * @param connection The {@link URLConnection}.
   * @param beforeConnect The {@link Consumer} to be called before this method invokes {@link HttpURLConnection#getResponseCode()}
   *          on the provided {@link URLConnection}.
   * @return An {@link InputStream} to the specified url that may or may not exist at a redirected location.
   * @throws IOException If an I/O error has occurred, or if the redirects are found to loop.
   * @throws NullPointerException If the provided {@link URLConnection} is null.
   */
  public static URLConnection checkFollowRedirect(final URLConnection connection, final ThrowingConsumer<HttpURLConnection,IOException> beforeConnect) throws IOException {
    return checkFollowRedirect(connection, Integer.MAX_VALUE, beforeConnect);
  }

  /**
   * Returns an {@link URLConnection} that represents the terminal end of all redirects followed, or the provided
   * {@link URLConnection} if a redirect is not present.
   *
   * @param connection The {@link URLConnection}.
   * @param maxRedirects The maximum number of redirects to be followed.
   * @param beforeConnect The {@link Consumer} to be called before this method invokes {@link HttpURLConnection#getResponseCode()}
   *          on the provided {@link URLConnection}.
   * @return An {@link InputStream} to the specified url that may or may not exist at a redirected location.
   * @throws IllegalArgumentException If {@code maxRedirects} is negative.
   * @throws NullPointerException If the provided {@link URLConnection} is null.
   * @throws IOException If an I/O error has occurred, or if the redirects are found to loop.
   */
  public static URLConnection checkFollowRedirect(URLConnection connection, final int maxRedirects, final ThrowingConsumer<HttpURLConnection,IOException> beforeConnect) throws IOException {
    assertNotNegative(maxRedirects);

    if (!(connection instanceof HttpURLConnection))
      return connection;

    HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
    if (beforeConnect != null)
      beforeConnect.accept(httpURLConnection);

    int status = httpURLConnection.getResponseCode();
    if (status < HttpURLConnection.HTTP_MOVED_PERM || HttpURLConnection.HTTP_SEE_OTHER < status || 0 == maxRedirects)
      return connection;

    final String location0 = httpURLConnection.getURL().toString();
    String location = httpURLConnection.getHeaderField("Location");
    if (location.equals(location0))
      throw new IOException("Infinite redirection loop: " + location + " -> " + location);

    httpURLConnection.disconnect();
    try {
      connection = new URL(location).openConnection();
      if (!(connection instanceof HttpURLConnection))
        return connection;

      httpURLConnection = (HttpURLConnection)connection;
      if (beforeConnect != null)
        beforeConnect.accept(httpURLConnection);

      status = httpURLConnection.getResponseCode();
      if (status < HttpURLConnection.HTTP_MOVED_PERM || HttpURLConnection.HTTP_SEE_OTHER < status || 1 == maxRedirects)
        return connection;

      final LinkedHashSet<String> visited = new LinkedHashSet<>();
      visited.add(location0);
      visited.add(location);
      int i = 1; do {
        location = httpURLConnection.getHeaderField("Location");
        if (!visited.add(location))
          throw new IOException("Infinite redirection loop: " + visited.stream().collect(Collectors.joining(" -> ")) + " -> " + location);

        httpURLConnection.disconnect();
        connection = new URL(location).openConnection();
        if (!(connection instanceof HttpURLConnection))
          return connection;

        httpURLConnection = (HttpURLConnection)connection;
        if (beforeConnect != null)
          beforeConnect.accept(httpURLConnection);

        status = httpURLConnection.getResponseCode();
        if (status < HttpURLConnection.HTTP_MOVED_PERM || HttpURLConnection.HTTP_SEE_OTHER < status || ++i == maxRedirects)
          return connection;
      }
      while (true);
    }
    catch (final IOException e) {
      httpURLConnection.disconnect();
      throw e;
    }
  }

  /**
   * Sets the specified {@link Properties} in the provided {@link URLConnection}.
   *
   * @param urlConnection The {@link URLConnection}.
   * @param properties The {@link Properties}.
   * @throws NullPointerException If {@code urlConnection} or {@code properties} is null.
   */
  public static void setRequestProperties(final URLConnection urlConnection, final Properties properties) {
    if (properties.size() > 0)
      for (final Map.Entry<Object,Object> entry : properties.entrySet()) // [S]
        urlConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
  }

  private URLConnections() {
  }
}