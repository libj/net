/* Copyright (c) 2006 FastJAX
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

package org.fastjax.net;

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
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import org.fastjax.util.Paths;
import org.fastjax.util.Strings;

/**
 * Utility functions for operations pertaining to {@link URL}.
 */
public final class URLs {
  /** A regular expression pattern that matches URL strings. */
  public static final String REGEX = "^([a-z][a-z0-9+\\-.]*):(\\/\\/([a-z0-9\\-._~%!$&amp;'()*+,;=]+@)?([a-z0-9\\-._~%]+|\\[[a-f0-9:.]+\\]|\\[v[a-f0-9][a-z0-9\\-._~%!$&amp;'()*+,;=:]+\\])(:[0-9]+)?(\\/[a-z0-9\\-._~%!$&amp;'()*+,;=:@]+)*\\/?|(\\/?[a-z0-9\\-._~%!$&amp;'()*+,;=:@]+(\\/[a-z0-9\\-._~%!$&amp;'()*+,;=:@]+)*\\/?)?)(\\?[a-z0-9\\-._~%!$&amp;'()*+,;=:@/?]*)?(#[a-z0-9\\-._~%!$&amp;'()*+,;=:@/?]*)?$";

  /**
   * Converts an array of {@code File} objects into an array of {@code URL}
   * objects. {@code File} objects that are {@code null} will be {@code null} in
   * the resulting {@code URL[]} array.
   *
   * @param files The array of {@code File} objects.
   * @return An array of {@code URL} objects.
   * @throws IllegalArgumentException If a protocol handler for the URL could
   *           not be found, or if some other error occurred while constructing
   *           the URL.
   * @throws NullPointerException If {@code files} is null.
   */
  public static URL[] toURL(final File ... files) {
    try {
      final URL[] urls = new URL[files.length];
      for (int i = 0; i < files.length; ++i)
        urls[i] = files[i].toURI().toURL();

      return urls;
    }
    catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Tests whether the specified {@link URL} represents a file path. An URL is
   * considered a file if its protocol is "file" (case-insensitive), and its
   * host value is empty or equal to {@code "localhost"}.
   *
   * @param url The {@link URL}.
   * @return {@code true} if the specified {@link URL} represents a file path;
   *         otherwise {@code false}.
   * @throws NullPointerException If {@code url} is null.
   */
  public static boolean isLocalFile(final URL url) {
    final String host = url.getHost();
    return "file".equalsIgnoreCase(url.getProtocol()) && (host == null || host.length() == 0 || "localhost".equals(host));
  }

  /**
   * Tests whether the specified {@link URL} represents a location that is a
   * local Jar file with protocol {@code "jar:file:"}.
   * <p>
   * The compound protocol definition is unwrapped in order to determine if the
   * root resource locator in the URL is local. This method then uses
   * {@link URLs#isLocalFile(URL)} to check whether {@code url} is local.
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} represents a location
   *         that is local; otherwise {@code false}.
   * @throws NullPointerException If {@code url} is null.
   */
  public static boolean isLocalJarFile(URL url) {
    do {
      if (!"jar".equalsIgnoreCase(url.getProtocol()))
        return false;

      final String path = url.getPath();
      final int bang = path.lastIndexOf('!');
      try {
        if (isLocalFile(url = new URL(bang == -1 ? path : path.substring(0, bang))))
          return true;
      }
      catch (final MalformedURLException e) {
        return false;
      }
    }
    while (true);
  }

  /**
   * Tests whether the specified {@link URL} represents a location that is
   * either a local file with protocol {@code "jar:file:"}, or a local Jar file
   * with protocol {@code "jar:file:"}.
   * <p>
   * URLs with compound protocol definitions, such as {@code "jar:file:"} are
   * first unwrapped in order to digest the root resource locator in the URL.
   * This method then uses {@link URLs#isLocalFile(URL)} to check whether
   * {@code url} is local.
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} represents a location
   *         that is either a local file with protocol {@code "jar:file:"}, or a
   *         local Jar file with protocol {@code "jar:file:"}; otherwise
   *         {@code false}.
   * @throws NullPointerException If {@code url} is null.
   */
  public static boolean isLocal(final URL url) {
    return isLocalFile(url) || isLocalJarFile(url);
  }

  /**
   * Returns an {@link URL} created from the specified string, or {@code null}
   * if the specified string is null.
   * <ul>
   * <li>If the specified path contains the string {@code ":/"}, the resulting
   * {@code URL} is created with {@link URL#URL(String)}.</li>
   * <li>Otherwise, if the specified path does not have a leading {@code '/'},
   * then it is prepended to the path.</li>
   * </ul>
   * <p>
   * This method assumes the specified string is an absolute path, and detects
   * Windows paths using {@link Paths#isAbsoluteLocalWindows(String)}.
   *
   * @param absolutePath The string from which to create an {@link URL}.
   * @return An {@link URL} created from the specified string, or {@code null}
   *         if the specified string is null.
   * @throws MalformedURLException If a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the
   *           specific syntax of the associated protocol.
   * @see Paths#isAbsoluteLocalWindows(String)
   */
  public static URL toURL(String absolutePath) throws MalformedURLException {
    if (absolutePath == null)
      return null;

    if (absolutePath.contains(":/") && absolutePath.length() > 0 && absolutePath.charAt(0) != '/')
      return new URL(absolutePath);

    if (Paths.isAbsoluteLocalWindows(absolutePath))
      absolutePath = "/" + absolutePath.replace('\\', '/');

    return new URL("file", "", absolutePath.length() > 0 && absolutePath.charAt(0) != '/' ? "/" + absolutePath : absolutePath);
  }

  /**
   * Returns a canonical {@link URL} created from the specified string, or
   * {@code null} if the specified string is null ({@code ".."} and {@code "."}
   * path names are dereferenced in a canonical {@link URL}, and redundant
   * {@code '/'} path separators are removed).
   * <ul>
   * <li>If the specified path contains the string {@code ":/"}, the resulting
   * {@code URL} is created with {@link URL#URL(String)}.</li>
   * <li>Otherwise, if the specified path does not have a leading {@code '/'},
   * then it is prepended to the path.</li>
   * </ul>
   * <p>
   * This method assumes the specified string is an absolute path, and detects
   * Windows paths using {@link Paths#isAbsoluteLocalWindows(String)}.
   *
   * @param absolutePath The string from which to create an {@link URL}.
   * @return A canonical {@link URL} created from the specified string, or
   *         {@code null} if the specified string is null ({@code ".."} and
   *         {@code "."} path names are dereferenced in a canonical {@link URL},
   *         and redundant {@code '/'} path separators are removed).
   * @throws MalformedURLException If a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the
   *           specific syntax of the associated protocol.
   * @see URLs#canonicalize(URL)
   * @see Paths#isAbsoluteLocalWindows(String)
   */
  public static URL toCanonicalURL(final String absolutePath) throws MalformedURLException {
    return canonicalize(toURL(absolutePath));
  }

  /**
   * Returns an {@link URL} created from the specified {@code basedir} parent
   * directory, and {@code path} child path.
   *
   * @param basedir The base directory of the path in the resulting {@link URL}.
   * @param path The child path off of {@code basedir} in the resulting
   *          {@link URL}.
   * @return An {@link URL} created from the specified {@code basedir} parent
   *         directory, and {@code path} child path.
   * @throws MalformedURLException If a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the
   *           specific syntax of the associated protocol.
   * @throws NullPointerException If {@code basedir} or {@code path} is null.
   * @see URLs#toURL(String)
   * @see Paths#newPath(String,String)
   * @see Paths#isAbsoluteLocalWindows(String)
   */
  public static URL toURL(final String basedir, final String path) throws MalformedURLException {
    return toURL(Paths.newPath(basedir, path));
  }

  /**
   * Returns a canonical {@link URL} created from the specified {@code basedir}
   * parent directory, and {@code path} child path ({@code ".."} and {@code "."}
   * path names are dereferenced in a canonical {@link URL}, and redundant
   * {@code '/'} path separators are removed).
   *
   * @param basedir The base directory of the path in the resulting {@link URL}.
   * @param path The child path off of {@code basedir} in the resulting
   *          {@link URL}.
   * @return A canonical {@link URL} created from the specified {@code basedir}
   *         parent directory, and {@code path} child path ({@code ".."} and
   *         {@code "."} path names are dereferenced in a canonical {@link URL},
   *         and redundant {@code '/'} path separators are removed).
   * @throws MalformedURLException If a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the
   *           specific syntax of the associated protocol.
   * @throws NullPointerException If {@code basedir} or {@code path} is null.
   * @see URLs#toURL(String)
   * @see URLs#canonicalize(URL)
   * @see Paths#newPath(String,String)
   * @see Paths#isAbsoluteLocalWindows(String)
   */
  public static URL toCanonicalURL(final String basedir, final String path) throws MalformedURLException {
    return toCanonicalURL(Paths.newPath(basedir, path));
  }

  /**
   * Returns an {@link URL} created from the specified {@code baseURL} parent
   * URL, and {@code path} child path.
   * <p>
   * If the specified {@code baseURL} contains a query string, this method will
   * throw a {@link IllegalArgumentException}.
   *
   * @param baseURL The base URL of the path in the resulting {@link URL}.
   * @param path The child path off of {@code baseURL} in the resulting
   *          {@link URL}.
   * @return An {@link URL} created from the specified {@code baseURL} parent
   *         directory, and {@code path} child path.
   * @throws MalformedURLException If a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the
   *           specific syntax of the associated protocol.
   * @throws IllegalArgumentException If the specified {@code baseURL} contains
   *           a query string.
   * @throws NullPointerException If {@code baseURL} or {@code path} is null.
   */
  public static URL toURL(final URL baseURL, String path) throws MalformedURLException {
    if (baseURL.getQuery() != null)
      throw new IllegalArgumentException("Base URL with query string is not allowed");

    path = path.replace('\\', '/');
    if (baseURL.getPath().length() > 0 && baseURL.getPath().charAt(baseURL.getPath().length() - 1) == '/') {
      if (path.length() > 0 && path.charAt(0) == '/')
        path = path.substring(1);
    }
    else if (path.length() > 0 && path.charAt(0) != '/') {
      path = "/" + path;
    }

    return new URL(baseURL.getProtocol(), baseURL.getHost(), baseURL.getPort(), baseURL.getPath() + path);
  }

  /**
   * Returns a canonical {@link URL} created from the specified {@code baseURL}
   * parent URL, and {@code path} child path ({@code ".."} and {@code "."} path
   * names are dereferenced in a canonical {@link URL}, and redundant
   * {@code '/'} path separators are removed).
   * <p>
   * If the specified {@code baseURL} contains a query string, this method will
   * throw a {@link IllegalArgumentException}.
   *
   * @param baseURL The base URL of the path in the resulting {@link URL}.
   * @param path The child path off of {@code baseURL} in the resulting
   *          {@link URL}.
   * @return A canonical {@link URL} created from the specified {@code baseURL}
   *         parent directory, and {@code path} child path ({@code ".."} and
   *         {@code "."} path names are dereferenced in a canonical {@link URL},
   *         and redundant {@code '/'} path separators are removed).
   * @throws MalformedURLException If a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the
   *           specific syntax of the associated protocol.
   * @throws IllegalArgumentException If the specified {@code baseURL} contains
   *           a query string.
   * @throws NullPointerException If {@code baseURL} or {@code path} is null.
   */
  public static URL toCanonicalURL(final URL baseURL, final String path) throws MalformedURLException {
    return canonicalize(toURL(baseURL, path));
  }

  /**
   * Returns the canonical version of the specified {@link URL}, where redundant
   * names such as {@code "."} and {@code ".."} are dereferenced and removed
   * from the path.
   *
   * @param url The {@link URL}.
   * @return The canonical version of the specified {@link URL}, where redundant
   *         names such as {@code "."} and {@code ".."} are dereferenced and
   *         removed from the path.
   * @throws MalformedURLException If a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the
   *           specific syntax of the associated protocol.
   */
  public static URL canonicalize(final URL url) throws MalformedURLException {
    return url == null ? null : new URL(Paths.canonicalize(url.toString()));
  }

  /**
   * Tests whether the specified {@link URL} references a resource that exists.
   * <p>
   * This method performs the following tests to check for the existence of the
   * resource at the specified {@link URL}:
   * <ol>
   * <li>If the protocol of the specified {@link URL} is {@code "file"}, this
   * method converts the {@link URL} to a {@link File} and delegates to
   * {@link File#exists()}.</li>
   * <li>Otherwise, the method attempts to open a connection to the resource. If
   * the connection is successful, the method returns {@code true}, and
   * otherwise {@code false}.</li>
   * </ol>
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} references a resource
   *         that exists; otherwise {@code false}.
   * @throws NullPointerException If {@code url} is null.
   */
  public static boolean exists(final URL url) {
    try {
      if ("file".equals(url.getProtocol()))
        return new File(url.toURI()).exists();
    }
    catch (final URISyntaxException e) {
    }

    try {
      url.openStream().close();
    }
    catch (final IOException e) {
      return false;
    }

    return true;
  }

  /**
   * Tests whether the specified {@link URL} references a Jar resource,
   * otherwise {@code false}.
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} references a Jar
   *         resource; otherwise {@code false}.
   * @throws NullPointerException If {@code url} is null.
   */
  public static boolean isJar(final URL url) {
    try {
      return url.toURI().toString().startsWith("jar:");
    }
    catch (final URISyntaxException e) {
      return false;
    }
  }

  /**
   * Returns an {@link URL} representing the location of the Jar in {@code url},
   * if {@code url} is "Jar URL" resembling the {@code "jar:<url>...!..."}
   * semantics; or {@code null} if {@code url} is not a "Jar URL".
   *
   * @param url The {@link URL}.
   * @return An {@link URL} representing the location of the Jar in {@code url},
   *         if {@code url} is "Jar URL" resembling the
   *         {@code "jar:<url>...!..."} semantics; or {@code null} if
   *         {@code url} is not a "Jar URL".
   * @throws NullPointerException If {@code url} is null.
   */
  public static URL getJarURL(final URL url) {
    if (!isJar(url))
      return null;

    try {
      return new URL(url.getFile().substring(0, url.getFile().indexOf('!')));
    }
    catch (final MalformedURLException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Returns the path portion of the resource referenced inside a Jar of the
   * specified {@code url} "Jar URL" (if {@code url} is "Jar URL" resembling the
   * {@code "jar:<url>...!..."} semantics), or {@code null} if {@code url} is
   * not a "Jar URL".
   *
   * @param url The {@link URL}.
   * @return The path portion of the resource referenced inside a Jar of the
   *         specified {@code url} "Jar URL" (if {@code url} is "Jar URL"
   *         resembling the {@code "jar:<url>...!..."} semantics), or
   *         {@code null} if {@code url} is not a "Jar URL".
   * @throws NullPointerException If {@code url} is null.
   */
  public static String getJarPath(final URL url) {
    return !isJar(url) ? null : url.getFile().substring(url.getFile().indexOf('!') + 2);
  }

  /**
   * Returns the name of the file or directory denoted by the specified
   * {@link URL}. This is just the last name in the name sequence of
   * {@code url}. If the name sequence of {@code url} is empty, then the empty
   * string is returned.
   *
   * @param url The {@link URL}.
   * @return The name of the file or directory denoted by the specified
   *         {@link URL}, or the empty string if the name sequence of
   *         {@code url} is empty.
   * @throws NullPointerException If {@code url} is null.
   */
  public static String getName(final URL url) {
    return Paths.getName(url.toString());
  }

  /**
   * Returns the short name of the file or directory denoted by the specified
   * {@link URL}. This is just the last name in the name sequence of
   * {@code url}, with its dot-extension removed if present. If the name
   * sequence of {@code url} is empty, then the empty string is returned.
   *
   * @param url The {@link URL}.
   * @return The short name of the file or directory denoted by the specified
   *         {@link URL}, or the empty string if the name sequence of
   *         {@code url} is empty.
   * @throws NullPointerException If {@code url} is null.
   */
  public static String getShortName(final URL url) {
    return Paths.getShortName(url.toString());
  }

  /**
   * Returns the last modified timestamp of the resource at the specified
   * {@link URL}. This function works for URLs that point to local files,
   * resources in jars, and resources behind HTTP/HTTPS connections. For all
   * other types of URLs, this function throws {@link IllegalArgumentException}.
   *
   * @param url The {@link URL}.
   * @return The last modified timestamp of the resource at the specified
   *         {@link URL}.
   * @throws IllegalArgumentException If {@code url} does not specify the
   *           {@code "file"}, {@code "http"}, or {@code "https"} protocols.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code url} is null.
   */
  public static long getLastModified(final URL url) throws IOException {
    if (isLocalFile(url))
      return new File(url.getFile()).lastModified();

    final URLConnection urlConnection = url.openConnection();
    if (urlConnection instanceof HttpURLConnection)
      return urlConnection.getLastModified();

    if (urlConnection instanceof JarURLConnection)
      return urlConnection.getLastModified();

    return -1;
  }

  /**
   * Returns the URL representing the parent of the specified {@link URL}, or
   * {@code null} if {@code url} is null or does not name a parent directory.
   *
   * @param url The {@link URL}.
   * @return The URL representing the parent of the specified {@link URL}, or
   *         {@code null} if {@code url} is null or does not name a parent
   *         directory.
   * @see Paths#getParent(String)
   */
  public static URL getParent(final URL url) {
    if (url == null)
      return null;

    try {
      final String parentPath = Paths.getParent(url.toString());
      return parentPath == null ? null : new URL(parentPath);
    }
    catch (final MalformedURLException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Returns the canonical URL representing the parent of the specified
   * {@link URL}, or {@code null} if {@code url} is null or does not name a
   * parent directory ({@code ".."} and {@code "."} path names are dereferenced
   * in a canonical {@link URL})).
   *
   * @param url The {@link URL}.
   * @return The URL representing the parent of the specified {@link URL}, or
   *         {@code null} if {@code url} is null or does not name a parent
   *         directory ({@code ".."} and {@code "."} path names are dereferenced
   *         in a canonical {@link URL}, and redundant {@code '/'} path
   *         separators are removed).
   * @see Paths#getParent(String)
   */
  public static URL getCanonicalParent(final URL url) {
    if (url == null)
      return null;

    try {
      return new URL(Paths.getCanonicalParent(url.toString()));
    }
    catch (final MalformedURLException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Translates a string into {@code application/x-www-form-urlencoded} format.
   * This method uses UTF-8 as the character encoding.
   *
   * @param s {@code String} to be translated.
   * @return The translated {@code String}.
   */
  public static String encode(final String s) {
    return encode(s, StandardCharsets.UTF_8.name());
  }

  /**
   * Translates a string into {@code application/x-www-form-urlencoded} format
   * using a specific encoding scheme.
   * <p>
   * This method behaves the same as {@link URLEncoder#encode(String,String)}
   * except that, instead of throwing a {@link UnsupportedEncodingException} if
   * the named encoding is not supported, this method will instead will instead
   * throw a {@link UnsupportedOperationException}.
   * <p>
   * This method is intended purely to remove the need to catch the
   * {@link UnsupportedEncodingException} if using
   * {@link URLEncoder#encode(String,String)} directly.
   *
   * @param s {@code String} to be translated.
   * @param enc The name of a supported character encoding.
   * @return The translated {@code String}.
   * @throws UnsupportedOperationException If the named encoding is not
   *           supported.
   * @throws NullPointerException If {@code s} or {@code enc} is null.
   * @see URLs#decode(String,String)
   */
  public static String encode(final String s, final String enc) {
    try {
      return URLEncoder.encode(s, enc);
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Translates a string into {@code application/x-www-form-urlencoded} format
   * using a specific {@link Charset}. This method uses the supplied charset to
   * obtain the bytes for unsafe characters.
   * <p>
   * <em><strong>Note:</strong> The
   * <a href= "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
   * World Wide Web Consortium Recommendation</a> states that UTF-8 should be
   * used. Not doing so may introduce incompatibilities.</em>
   * <p>
   * This method is intended purely to remove the need to catch the
   * {@link UnsupportedEncodingException} if using
   * {@link URLEncoder#encode(String,String)} directly.
   *
   * @param s {@code String} to be translated.
   * @param charset The given {@link Charset}.
   * @return The translated {@code String}.
   * @throws UnsupportedOperationException If the named encoding is not
   *           supported.
   * @throws NullPointerException If {@code s} or {@code charset} is null.
   * @see URLs#decode(String,Charset)
   */
  public static String encode(final String s, final Charset charset) {
    try {
      return URLEncoder.encode(s, charset.name());
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Decodes a {@code application/x-www-form-urlencoded} string. This method
   * uses UTF-8 as the character encoding.
   *
   * @param s The {@code String} to decode.
   * @return The decoded {@code String}.
   * @throws IllegalArgumentException If the implementation encounters illegal
   *           path separators.
   * @throws NullPointerException If {@code s} is null.
   */
  public static String decode(final String s) {
    return decode(s, StandardCharsets.UTF_8, false);
  }

  /**
   * Decodes an {@code application/x-www-form-urlencoded} string using a
   * specific {@link Charset}. The supplied charset is used to determine what
   * characters are represented by any consecutive sequences of the form
   * "<i>{@code %xy}</i>".
   * <p>
   * <i><b>Note:</b> The
   * <a href= "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
   * World Wide Web Consortium Recommendation</a> states that UTF-8 should be
   * used. Not doing so may introduce incompatibilities.</i>
   *
   * @param s The {@code String} to decode.
   * @param charset The given {@link Charset}.
   * @return The decoded {@code String}.
   * @throws NullPointerException If {@code s} or {@code charset} is null.
   * @throws IllegalArgumentException If the implementation encounters illegal
   *           characters.
   * @see URLs#encode(String,Charset)
   */
  public static String decode(final String s, final Charset charset) {
    return decode(s, charset, false);
  }

  /**
   * Decodes an {@code application/x-www-form-urlencoded} string using a
   * specific encoding scheme.
   * <p>
   * This method behaves the same as {@link URLs#decode(String,Charset)} except
   * that it will {@linkplain Charset#forName look up the charset} using the
   * given encoding name.
   *
   * @param s The {@code String} to decode.
   * @param enc The name of a supported encoding.
   * @return The decoded {@code String}.
   * @throws NullPointerException If {@code s} or {@code charset} is null.
   * @throws IllegalArgumentException If the implementation encounters illegal
   *           characters, or if the given charset name is null.
   * @throws IllegalCharsetNameException If the given charset name is illegal.
   * @throws UnsupportedCharsetException If no support for the named charset is
   *           available in this instance of the Java virtual machine.
   * @see URLs#encode(String,String)
   */
  public static String decode(final String s, final String enc) {
    return decode(s, Charset.forName(enc), false);
  }

  private static String decode(final String s, final Charset charset, final boolean isPath) {
    boolean needDecode = false;
    int escapesCount = 0;
    final int length = s.length();
    for (int i = 0; i < length; ++i) {
      final char ch = s.charAt(i);
      if (ch == '%') {
        escapesCount += 1;
        i += 2;
        needDecode = true;
      }
      else if (!isPath && ch == '+') {
        needDecode = true;
      }
    }

    if (needDecode) {
      final ByteBuffer in = ByteBuffer.wrap(Strings.getBytes(s, charset.name()));
      final ByteBuffer out = ByteBuffer.allocate(in.capacity() - (2 * escapesCount) + 1);
      while (in.hasRemaining()) {
        final int b = in.get();
        if (!isPath && b == '+') {
          out.put((byte)' ');
        }
        else if (b == '%') {
          try {
            final int u = digit16(in.get());
            final int l = digit16(in.get());
            out.put((byte)((u << 4) + l));
          }
          catch (final BufferUnderflowException e) {
            throw new IllegalArgumentException("Invalid URL encoding: Incomplete trailing escape (%) pattern", e);
          }
        }
        else {
          out.put((byte)b);
        }
      }

      out.flip();
      return charset.decode(out).toString();
    }

    return s;
  }

  private static int digit16(final byte b) {
    final int d = Character.digit((char)b, 16);
    if (d == -1)
      throw new IllegalArgumentException("Invalid URL encoding: not a valid digit (radix 16): " + b);

    return d;
  }

  private static StringBuilder componentEncode(final String reservedChars, final String value) {
    final StringBuilder builder = new StringBuilder();
    final StringBuilder builderToEncode = new StringBuilder();

    for (int i = 0; i < value.length(); ++i) {
      final char ch = value.charAt(i);
      if (reservedChars.indexOf(ch) != -1) {
        if (builderToEncode.length() > 0) {
          builder.append(encode(builderToEncode.toString()));
          builderToEncode.setLength(0);
        }

        builder.append(ch);
      }
      else {
        builderToEncode.append(ch);
      }
    }

    if (builderToEncode.length() > 0)
      builder.append(encode(builderToEncode.toString()));

    return builder;
  }

  private static final String PATH_RESERVED_CHARACTERS = "=@/:!$&\'(),;~";

  /**
   * Returns the URL-encoded path string.
   * <p>
   * URL path segments may contain {@code '+'} symbols which should not be
   * decoded into {@code ' '}. This method delegates to URLEncoder, then
   * replaces {@code '+'} with {@code "%20"}.
   *
   * @param path The path to decode.
   * @return The URL-decoded path string.
   */
  public static String encodePath(final String path) {
    final StringBuilder result = componentEncode(PATH_RESERVED_CHARACTERS, path);
    // URLEncoder will encode '+' to %2B but will turn ' ' into '+'
    // We need to retain '+' and encode ' ' as %20
    Strings.replace(result, "+", "%20");
    Strings.replace(result, "%2B", "+");
    return result.toString();
  }

  /**
   * Returns the URL-decoded path string.
   * <p>
   * URL path segments may contain {@code '+'} symbols which should not be
   * decoded into {@code ' '}. This method replaces {@code '+'} with
   * {@code "%2B"} and delegates to URLDecoder.
   *
   * @param path The path to decode.
   * @return The URL-decoded path string.
   */
  public static String decodePath(final String path) {
    return decode(path, StandardCharsets.UTF_8, true);
  }

  private URLs() {
  }
}