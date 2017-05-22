/* Copyright (c) 2009 lib4j
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public final class URLConnections {
  private static final int[] REDIRECT_CODES = new int[] {HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_SEE_OTHER};

  public static void setRequestProperties(final URLConnection urlConnection, final Properties properties) {
    if (urlConnection == null)
      throw new NullPointerException("urlConnection == null");

    if (properties == null)
      throw new NullPointerException("properties == null");

    for (final Map.Entry<Object,Object> entry : properties.entrySet())
      urlConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
  }

  public static InputStream checkOpenRedirectStream(final URL url) throws IOException {
    final URLConnection connection = url.openConnection();
    if (connection instanceof HttpURLConnection && Arrays.binarySearch(REDIRECT_CODES, ((HttpURLConnection)connection).getResponseCode()) > 0) {
      final String location = connection.getHeaderField("Location");
      return new URL(location).openConnection().getInputStream();
    }

    return connection.getInputStream();
  }

  private URLConnections() {
  }
}