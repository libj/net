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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Utility functions pertaining to the {@link HTTP} protocol.
 */
public final class HTTP {
  /**
   * Create an {@link URL} for the specified {@code url} with the provided parameter map which will be encoded as UTF-8. It is
   * highly recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@code url} to be invoked.
   * @param parameters The parameters to be processed as query parameters.
   * @return The result of the GET request as an InputStream.
   * @throws MalformedURLException If the specified {@code url} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static URL get(final String url, final Map<String,String[]> parameters) throws IOException {
    return get(url, parameters, "UTF-8");
  }

  /**
   * Invoke a GET request on the specified {@code url} with the provided parameter map which will be encoded as UTF-8. It is highly
   * recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@code url} to be invoked.
   * @param parameters The parameters to be processed as query parameters.
   * @return The result of the GET request as an InputStream.
   * @throws MalformedURLException If the specified {@code url} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static InputStream getAsStream(final String url, final Map<String,String[]> parameters) throws IOException {
    return getAsStream(url, parameters, "UTF-8");
  }

  /**
   * Invoke a GET request on the specified {@code url} with the provided parameter map and charset encoding. It is highly
   * recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@code url} to be invoked.
   * @param parameters The parameters to be processed as query parameters.
   * @param charset The encoding to be applied.
   * @return The result of the GET request as an InputStream.
   * @throws MalformedURLException If the specified {@code url} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   * @throws UnsupportedEncodingException If the provided charset is not supported.
   */
  public static InputStream getAsStream(final String url, final Map<String,String[]> parameters, final String charset) throws IOException, UnsupportedEncodingException {
    final URLConnection urlConnection = get(url, parameters, charset).openConnection();
    urlConnection.setUseCaches(false);
    return urlConnection.getInputStream();
  }

  /**
   * Invoke a GET request on the specified {@link URL} with the provided parameter map and charset encoding. It is highly
   * recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@link URL} to be invoked.
   * @return The result of the GET request as an InputStream.
   * @throws MalformedURLException If the specified {@link URL} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   * @throws UnsupportedEncodingException If the provided charset is not supported.
   */
  public static InputStream getAsStream(final URL url) throws IOException, UnsupportedEncodingException {
    final URLConnection urlConnection = assertNotNull(url).openConnection();
    urlConnection.setUseCaches(false);
    return urlConnection.getInputStream();
  }

  /**
   * Create an {@link URL} for a GET request on the specified {@code url} with the provided parameter map and charset encoding. It
   * is highly recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@code url} to be invoked.
   * @param parameters The parameters to be processed as query parameters.
   * @param charset The encoding to be applied.
   * @return An {@link URL} for a GET request on the specified {@code url} with the provided parameter map and charset encoding.
   * @throws MalformedURLException If the specified {@code url} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   * @throws UnsupportedEncodingException If the provided charset is not supported.
   */
  public static URL get(final String url, final Map<String,String[]> parameters, final String charset) throws IOException, UnsupportedEncodingException {
    final String query = createQuery(parameters, charset);
    return new URL(assertNotNull(url) + "?" + query);
  }

  /**
   * Invoke a POST request on the specified {@link URL} with the provided parameter map which will be encoded as UTF-8. It is highly
   * recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@link URL} to be invoked.
   * @param parameters The parameters to be processed as query parameters.
   * @return The result of the POST request as an InputStream.
   * @throws MalformedURLException If the specified {@link URL} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static InputStream postAsStream(final URL url, final Map<String,String[]> parameters) throws IOException {
    return postAsStream(url, parameters, null);
  }

  /**
   * Invoke a POST request on the specified {@link URL} with the provided parameter map which will be encoded as UTF-8. It is highly
   * recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@link URL} to be invoked.
   * @param parameters The parameters to be processed as query parameters.
   * @param properties The request properties to be processed as header properties.
   * @return The result of the POST request as an InputStream.
   * @throws MalformedURLException If the specified {@link URL} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static InputStream postAsStream(final URL url, final Map<String,String[]> parameters, final Properties properties) throws IOException {
    return postAsStream(url, parameters, properties, null);
  }

  /**
   * Invoke a POST request on the specified {@link URL} with the provided parameter map which will be encoded as UTF-8. It is highly
   * recommended to close the obtained {@link InputStream} after processing.
   *
   * @param url The {@link URL} to be invoked.
   * @param parameters The parameters to be processed as query parameters.
   * @param properties The request properties to be processed as header properties.
   * @param cookies The cookies to be injected into the header.
   * @return The result of the POST request as an InputStream.
   * @throws MalformedURLException If the specified {@link URL} is invalid.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static InputStream postAsStream(final URL url, final Map<String,String[]> parameters, final Properties properties, final List<String> cookies) throws IOException {
    assertNotNull(url);
    String charset = properties != null ? properties.getProperty("accept-charset") : null;
    if (charset == null)
      charset = "UTF-8";

    final String query = createQuery(parameters, charset);
    final URLConnection urlConnection = url.openConnection();
    urlConnection.setUseCaches(false);
    urlConnection.setDoOutput(true); // Triggers POST
    // urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
    if (properties != null)
      for (final Map.Entry<Object,Object> property : properties.entrySet()) // [S]
        urlConnection.setRequestProperty((String)property.getKey(), (String)property.getValue());

    if (cookies != null) {
      final Map.Entry<String,String> cookie = Cookies.createCookieHeader(cookies);
      urlConnection.setRequestProperty(cookie.getKey(), cookie.getValue());
    }

    try (final OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream())) {
      writer.write(query);
    }

    return urlConnection.getInputStream();
  }

  /**
   * Create a query string based on the specified parameter map and the provided charset encoding.
   *
   * @param parameters The parameter map to be processed as query parameters.
   * @param charset The encoding to be applied.
   * @return The parameter map as query string.
   * @throws IllegalArgumentException If {@code charset} is null.
   * @throws UnsupportedEncodingException If the provided charset is not supported.
   */
  public static String createQuery(final Map<String,String[]> parameters, final String charset) throws UnsupportedEncodingException {
    assertNotNull(charset);
    if (parameters == null || parameters.size() == 0)
      return "";

    final StringBuilder builder = new StringBuilder();
    final Iterator<Map.Entry<String,String[]>> iterator = parameters.entrySet().iterator();
    final StringBuilder temp = new StringBuilder();
    for (int i = 0; iterator.hasNext(); ++i) { // [I]
      final Map.Entry<String,String[]> entry = iterator.next();
      final String name = entry.getKey();
      final String[] values = entry.getValue();
      if (i > 0)
        builder.append('&');

      for (int j = 0; j < values.length; ++j) { // [A]
        if (j > 0)
          temp.append('&');

        temp.append(URLEncoder.encode(name, charset));
        temp.append('=');
        temp.append(URLEncoder.encode(values[j], charset));
      }

      builder.append(temp);
      temp.setLength(0);
    }

    return builder.toString();
  }

  private HTTP() {
  }
}