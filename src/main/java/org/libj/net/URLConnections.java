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
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Utility functions operations pertaining to {@link URLConnection}.
 */
public final class URLConnections {
  private static final int[] REDIRECT_CODES = {HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_SEE_OTHER};

  /**
   * Sets the specified {@link Properties} in the provided
   * {@link URLConnection}.
   *
   * @param urlConnection The {@link URLConnection}.
   * @param properties The {@link Properties}.
   * @throws IllegalArgumentException If {@code urlConnection} or
   *           {@code properties} is null.
   */
  public static void setRequestProperties(final URLConnection urlConnection, final Properties properties) {
    assertNotNull(urlConnection);
    assertNotNull(properties);
    for (final Map.Entry<Object,Object> entry : properties.entrySet())
      urlConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
  }

  /**
   * Returns an {@link InputStream} to the specified url that may or may not
   * exist at a redirected location.
   *
   * @param url The {@link URL}.
   * @return An {@link InputStream} to the specified url that may or may not
   *         exist at a redirected location.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If the specified {@link URL} is null.
   */
  public static InputStream tryOpenRedirectStream(final URL url) throws IOException {
    final URLConnection connection = assertNotNull(url).openConnection();
    if (connection instanceof HttpURLConnection && Arrays.binarySearch(REDIRECT_CODES, ((HttpURLConnection)connection).getResponseCode()) > 0) {
      final String location = connection.getHeaderField("Location");
      return new URL(location).openConnection().getInputStream();
    }

    return connection.getInputStream();
  }

  private URLConnections() {
  }
}