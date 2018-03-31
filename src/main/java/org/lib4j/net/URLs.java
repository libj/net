/* Copyright (c) 2006 lib4j
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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.lib4j.lang.Paths;
import org.lib4j.lang.Strings;

public final class URLs {
  private static String formatWindowsPath(final String absolutePath) {
    return absolutePath.replace('\\', '/');
  }

  public static boolean isAbsolute(final String path) {
    if (path.charAt(0) == '/' || (Character.isLetter(path.charAt(0)) && path.charAt(1) == ':' && path.charAt(2) == '\\' && Character.isLetter(path.charAt(3))))
      return true;

    if (path.startsWith("file:/") || path.startsWith("jar:file:/"))
      return true;

    return path.matches("^([a-zA-Z0-9]+:)?//.*$");
  }

  public static boolean isFile(final URL url) {
    final String host = url.getHost();
    return "file".equalsIgnoreCase(url.getProtocol()) && (host == null || host.length() == 0);
  }

  public static boolean isLocal(URL url) {
    do {
      if (isFile(url))
        return true;

      if (!"jar".equalsIgnoreCase(url.getProtocol()))
        return false;

      final String path = url.getPath();
      final int bang = path.lastIndexOf('!');
      try {
        url = new URL(bang == -1 ? path : path.substring(0, bang));
      }
      catch (final MalformedURLException e) {
        return false;
      }
    }
    while (true);
  }

  public static URL makeUrlFromPath(String absolutePath) throws MalformedURLException {
    if (absolutePath == null)
      return null;

    URL url;
    if (absolutePath.contains(":/") && absolutePath.charAt(0) != '/') {
      url = new URL(absolutePath);
    }
    else {
      if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
        absolutePath = formatWindowsPath(absolutePath);

      if (absolutePath.charAt(0) != '/')
        absolutePath = "/" + absolutePath;

      url = new URL("file", "", absolutePath);
    }

    return url;
  }

  public static URL makeUrlFromPath(final String parentDir, final String path) throws MalformedURLException {
    return makeUrlFromPath(Paths.newPath(parentDir, path));
  }

  public static URL makeCanonicalUrlFromPath(String absolutePath) throws MalformedURLException {
    return URLs.canonicalizeURL(makeUrlFromPath(absolutePath));
  }

  public static URL makeCanonicalUrlFromPath(final String parentDir, final String path) throws MalformedURLException {
    return makeCanonicalUrlFromPath(Paths.newPath(parentDir, path));
  }

  public static URL makeUrlFromPath(final URL baseURL, final String path) throws MalformedURLException {
    if (baseURL == null || path == null)
      return null;

    final String externalForm = URLs.toExternalForm(baseURL);
    return new URL(externalForm.endsWith("/") ? externalForm + path : externalForm + "/" + path);
  }

  public static URL makeCanonicalUrlFromPath(final URL baseURL, final String path) throws MalformedURLException {
    return URLs.canonicalizeURL(makeUrlFromPath(baseURL, path));
  }

  public static String toExternalForm(final CachedURL url) throws MalformedURLException {
    return toExternalForm(url.toURL());
  }

  public static String toExternalForm(final URL url) throws MalformedURLException {
    if (url == null)
      return null;

    try {
      return url.toURI().toASCIIString();
    }
    catch (final URISyntaxException e) {
      final MalformedURLException exception = new MalformedURLException(url.toString());
      exception.initCause(e);
      throw exception;
    }
  }

  public static boolean exists(final URL url) {
    try {
      if ("file".equals(url.getProtocol()))
        return new File(url.getFile()).exists();

      url.openStream().close();
    }
    catch (final IOException e) {
      return false;
    }

    return true;
  }

  public static URL canonicalizeURL(final URL url) throws MalformedURLException {
    if (url == null)
      return null;

    final String path = Paths.canonicalize(url.toString());
    if (path == null)
      return null;

    return new URL(path);
  }

  public static boolean isJar(final URL url) {
    try {
      return url.toURI().toASCIIString().startsWith("jar:");
    }
    catch (final URISyntaxException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static URL getParentJar(final URL url) {
    if (!isJar(url))
      return null;

    try {
      final String uri = url.toURI().toASCIIString();
      return new URL(uri.substring(4, uri.indexOf('!')));
    }
    catch (final MalformedURLException | URISyntaxException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static String getPathInJar(final URL url) {
    if (!isJar(url))
      return null;

    try {
      final String uri = url.toURI().toASCIIString();
      return uri.substring(uri.indexOf('!') + 2);
    }
    catch (final URISyntaxException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static String getName(final URL url) {
    return Paths.getName(url.toString());
  }

  public static String getShortName(final URL url) {
    return Paths.getShortName(url.toString());
  }

  /**
   * Get last modified timestamp of the resource at the <code>url</code>
   * location. This function works for urls that point to local files,
   * resources in jars, and resources behind HTTP/HTTPS connections. For all
   * other types of urls, this function returns -1.
   * @param url The location of the resource.
   * @return The last modified timestamp.
   * @throws IOException If an IO connectivity exception occurs.
   */
  public static long getLastModified(final URL url) throws IOException {
    if (URLs.isFile(url))
      return new File(url.getFile()).lastModified();

    final URLConnection urlConnection = url.openConnection();
    if (urlConnection instanceof HttpURLConnection)
      return ((HttpURLConnection)urlConnection).getLastModified();

    if (urlConnection instanceof JarURLConnection)
      return ((JarURLConnection)urlConnection).getLastModified();

    return -1;
  }

  public static URL getParent(final URL url) {
    if (url == null)
      return null;

    try {
      return new URL(Paths.getParent(url.toString()));
    }
    catch (final MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public static URL getCanonicalParent(final URL url) {
    if (url == null)
      return null;

    try {
      return new URL(Paths.getCanonicalParent(url.toString()));
    }
    catch (final MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static final int RADIX = 16;
  private static final byte ESCAPE_CHAR = '%';
  private static final byte PLUS_CHAR = '+';

  public static String urlEncode(final String value) {
    return urlEncode(value, StandardCharsets.UTF_8.name());
  }

  public static String urlEncode(final String value, final String enc) {
    try {
      return URLEncoder.encode(value, enc);
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static String decode(final URL url) {
    return decode(url.getPath(), StandardCharsets.UTF_8.name(), false);
  }

  public static String decode(final String url) {
    return decode(url, StandardCharsets.UTF_8.name(), false);
  }

  public static String decode(final String url, final Charset enc) {
    return decode(url, enc.name(), false);
  }

  public static String decode(final String value, final String enc) {
    return decode(value, enc, false);
  }

  private static String decode(final String value, final String enc, final boolean isPath) {
    boolean needDecode = false;
    int escapesCount = 0;
    int i = 0;
    final int length = value.length();
    while (i < length) {
      final char ch = value.charAt(i++);
      if (ch == ESCAPE_CHAR) {
        escapesCount += 1;
        i += 2;
        needDecode = true;
      }
      else if (!isPath && ch == PLUS_CHAR) {
        needDecode = true;
      }
    }

    if (needDecode) {
      final byte[] valueBytes = Strings.getBytes(value, enc);
      final ByteBuffer in = ByteBuffer.wrap(valueBytes);
      final ByteBuffer out = ByteBuffer.allocate(in.capacity() - (2 * escapesCount) + 1);
      while (in.hasRemaining()) {
        final int b = in.get();
        if (!isPath && b == PLUS_CHAR) {
          out.put((byte)' ');
        }
        else if (b == ESCAPE_CHAR) {
          try {
            final int u = digit16(in.get());
            final int l = digit16(in.get());
            out.put((byte)((u << 4) + l));
          }
          catch (final BufferUnderflowException e) {
            throw new IllegalArgumentException("Invalid URL encoding: Incomplete trailing escape (%) pattern");
          }
        }
        else {
          out.put((byte)b);
        }
      }

      out.flip();
      return Charset.forName(enc).decode(out).toString();
    }

    return value;
  }

  private static int digit16(final byte b) {
    final int d = Character.digit((char)b, RADIX);
    if (d == -1)
      throw new IllegalArgumentException("Invalid URL encoding: not a valid digit (radix " + RADIX + "): " + b);

    return d;
  }

  private static String componentEncode(final String reservedChars, final String value) {
    final StringBuilder buffer = new StringBuilder();
    final StringBuilder bufferToEncode = new StringBuilder();

    for (int i = 0; i < value.length(); i++) {
      final char ch = value.charAt(i);
      if (reservedChars.indexOf(ch) != -1) {
        if (bufferToEncode.length() > 0) {
          buffer.append(urlEncode(bufferToEncode.toString()));
          bufferToEncode.setLength(0);
        }

        buffer.append(ch);
      }
      else {
        bufferToEncode.append(ch);
      }
    }

    if (bufferToEncode.length() > 0)
      buffer.append(urlEncode(bufferToEncode.toString()));

    return buffer.toString();
  }

  private static final String PATH_RESERVED_CHARACTERS = "=@/:!$&\'(),;~";

  public static String pathEncode(final String value) {
    String result = componentEncode(PATH_RESERVED_CHARACTERS, value);
    // URLEncoder will encode '+' to %2B but will turn ' ' into '+'
    // We need to retain '+' and encode ' ' as %20
    if (result.indexOf('+') != -1)
      result = result.replace("+", "%20");

    if (result.indexOf("%2B") != -1)
      result = result.replace("%2B", "+");

    return result;
  }

  /**
   * URL path segments may contain '+' symbols which should not be decoded into ' '
   * This method replaces '+' with %2B and delegates to URLDecoder
   *
   * @param value
   *          The value to decode.
   */
  public static String pathDecode(final String value) {
    return decode(value, StandardCharsets.UTF_8.name(), true);
  }

  private URLs() {
  }
}