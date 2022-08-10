/* Copyright (c) 2006 LibJ
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import org.libj.lang.Strings;
import org.libj.net.offline.OfflineURLStreamHandler;
import org.libj.util.StringPaths;

/**
 * Utility functions for operations pertaining to {@link URL}.
 */
public final class URLs {
  /** A regular expression pattern that matches URL strings. */
  public static final String REGEX = "^([a-z][a-z0-9+\\-.]*):(\\/\\/([a-z0-9\\-._~%!$&amp;'()*+,;=]+@)?([a-z0-9\\-._~%]+|\\[[a-f0-9:.]+\\]|\\[v[a-f0-9][a-z0-9\\-._~%!$&amp;'()*+,;=:]+\\])(:[0-9]+)?(\\/[a-z0-9\\-._~%!$&amp;'()*+,;=:@]+)*\\/?|(\\/?[a-z0-9\\-._~%!$&amp;'()*+,;=:@]+(\\/[a-z0-9\\-._~%!$&amp;'()*+,;=:@]+)*\\/?)?)(\\?[a-z0-9\\-._~%!$&amp;'()*+,;=:@/?]*)?(#[a-z0-9\\-._~%!$&amp;'()*+,;=:@/?]*)?$";
  private static final int DEFAULT_TIMEOUT = 1000;

  /**
   * Creates a {@link URL} by parsing the provided string.
   * <p>
   * This convenience factory method works as if by invoking the {@link URL#URL(String)} constructor; any
   * {@link MalformedURLException} thrown by the constructor is caught and wrapped in a new {@link IllegalArgumentException} object,
   * which is then thrown.
   * <p>
   * This method is provided for use in situations where it is known that the provided string is a legal URL, for example for URL
   * constants declared within in a program, and so it would be considered a programming error for the string not to parse as such.
   * The constructors, which throw {@link MalformedURLException} directly, should be used situations where a URL is being
   * constructed from user input or from some other source that may be prone to errors.
   *
   * @param str The string to parse.
   * @return The new {@link URL}.
   * @throws IllegalArgumentException If {@code str} is null, or if {@code str} declares a protocol that could not be found in a
   *           specification string, or if the string could not be parsed.
   */
  public static URL create(final String str) {
    try {
      return new URL(assertNotNull(str));
    }
    catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Creates a {@link URL} by parsing the provided {@link URL} and spec string.
   * <p>
   * This convenience factory method works as if by invoking the {@link URL#URL(URL,String)} constructor; any
   * {@link MalformedURLException} thrown by the constructor is caught and wrapped in a new {@link IllegalArgumentException} object,
   * which is then thrown.
   * <p>
   * This method is provided for use in situations where it is known that the provided string is a legal URL, for example for URL
   * constants declared within in a program, and so it would be considered a programming error for the string not to parse as such.
   * The constructors, which throw {@link MalformedURLException} directly, should be used situations where a URL is being
   * constructed from user input or from some other source that may be prone to errors.
   *
   * @param context The context in which to parse the specification.
   * @param spec The {@link String} to parse.
   * @return The new {@link URL}.
   * @throws IllegalArgumentException If {@code spec} is null, or if {@code str} declares a protocol that could not be found in a
   *           specification string, or if the string could not be parsed.
   */
  public static URL create(final URL context, final String spec) {
    return create(context, spec, null);
  }

  /**
   * Creates a {@link URL} by parsing the provided {@link URL} and spec string.
   * <p>
   * This convenience factory method works as if by invoking the {@link URL#URL(URL,String)} constructor; any
   * {@link MalformedURLException} thrown by the constructor is caught and wrapped in a new {@link IllegalArgumentException} object,
   * which is then thrown.
   * <p>
   * This method is provided for use in situations where it is known that the provided string is a legal URL, for example for URL
   * constants declared within in a program, and so it would be considered a programming error for the string not to parse as such.
   * The constructors, which throw {@link MalformedURLException} directly, should be used situations where a URL is being
   * constructed from user input or from some other source that may be prone to errors.
   *
   * @param context The context in which to parse the specification.
   * @param spec The {@link String} to parse.
   * @param handler The stream handler for the URL.
   * @return The new {@link URL}.
   * @throws IllegalArgumentException If {@code spec} is null, or if {@code str} declares a protocol that could not be found in a
   *           specification string, or if the string could not be parsed.
   */
  public static URL create(final URL context, final String spec, final URLStreamHandler handler) {
    try {
      return new URL(context, assertNotNull(spec), handler);
    }
    catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Creates a {@link URL} by parsing the provided protocol, host, and file strings.
   * <p>
   * This convenience factory method works as if by invoking the {@link URL#URL(String,String,String)} constructor; any
   * {@link MalformedURLException} thrown by the constructor is caught and wrapped in a new {@link IllegalArgumentException} object,
   * which is then thrown.
   * <p>
   * This method is provided for use in situations where it is known that the provided string is a legal URL, for example for URL
   * constants declared within in a program, and so it would be considered a programming error for the string not to parse as such.
   * The constructors, which throw {@link MalformedURLException} directly, should be used situations where a URL is being
   * constructed from user input or from some other source that may be prone to errors.
   *
   * @param protocol The name of the protocol to use.
   * @param host The name of the host.
   * @param file The file on the host.
   * @return The new {@link URL}.
   * @throws IllegalArgumentException If {@code file} is null, or if {@code str} declares a protocol that could not be found in a
   *           specification string, or if the string could not be parsed.
   */
  public static URL create(final String protocol, final String host, final String file) {
    return create(protocol, host, -1, file, null);
  }

  /**
   * Creates a {@link URL} by parsing the provided protocol, host, port, and file strings.
   * <p>
   * This convenience factory method works as if by invoking the {@link URL#URL(String,String,String)} constructor; any
   * {@link MalformedURLException} thrown by the constructor is caught and wrapped in a new {@link IllegalArgumentException} object,
   * which is then thrown.
   * <p>
   * This method is provided for use in situations where it is known that the provided string is a legal URL, for example for URL
   * constants declared within in a program, and so it would be considered a programming error for the string not to parse as such.
   * The constructors, which throw {@link MalformedURLException} directly, should be used situations where a URL is being
   * constructed from user input or from some other source that may be prone to errors.
   *
   * @param protocol The name of the protocol to use.
   * @param host The name of the host.
   * @param port The port number on the host.
   * @param file The file on the host.
   * @return The new {@link URL}.
   * @throws IllegalArgumentException If {@code file} is null, or if {@code str} declares a protocol that could not be found in a
   *           specification string, or if the string could not be parsed.
   */
  public static URL create(final String protocol, final String host, int port, final String file) {
    return create(protocol, host, port, file, null);
  }

  /**
   * Creates a {@link URL} by parsing the provided protocol, host, port, and file strings.
   * <p>
   * This convenience factory method works as if by invoking the {@link URL#URL(String,String,String)} constructor; any
   * {@link MalformedURLException} thrown by the constructor is caught and wrapped in a new {@link IllegalArgumentException} object,
   * which is then thrown.
   * <p>
   * This method is provided for use in situations where it is known that the provided string is a legal URL, for example for URL
   * constants declared within in a program, and so it would be considered a programming error for the string not to parse as such.
   * The constructors, which throw {@link MalformedURLException} directly, should be used situations where a URL is being
   * constructed from user input or from some other source that may be prone to errors.
   *
   * @param protocol The name of the protocol to use.
   * @param host The name of the host.
   * @param port The port number on the host.
   * @param file The file on the host.
   * @param handler The stream handler for the URL.
   * @return The new {@link URL}.
   * @throws IllegalArgumentException If {@code file} is null, or if {@code str} declares a protocol that could not be found in a
   *           specification string, or if the string could not be parsed.
   */
  public static URL create(final String protocol, final String host, int port, final String file, final URLStreamHandler handler) {
    try {
      return new URL(protocol, host, -1, assertNotNull(file), handler);
    }
    catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Creates a {@link URL} from the specified {@code basedir} parent directory, and {@code path} child path.
   *
   * @param basedir The base directory of the path in the resulting {@link URL}.
   * @param path The child path off of {@code basedir} in the resulting {@link URL}.
   * @return A {@link URL} created from the specified {@code basedir} parent directory, and {@code path} child path.
   * @throws IllegalArgumentException If {@code basedir} or {@code path} is null, or if a protocol is specified but is unknown, or
   *           the spec is null, or the parsed URL fails to comply with the specific syntax of the associated protocol.
   * @see URLs#fromStringPath(String)
   * @see StringPaths#newPath(String,String)
   * @see StringPaths#isAbsoluteLocalWindows(String)
   */
  public static URL create(final String basedir, final String path) {
    return fromStringPath(StringPaths.newPath(basedir, path));
  }

  /**
   * Returns a {@link URL} created from the specified string, or {@code null} if the specified string path is null.
   * <ul>
   * <li>If the specified string path contains the string {@code ":/"}, the resulting {@link URL} is created with
   * {@link URL#URL(String)}.</li>
   * <li>Otherwise, if the specified path does not have a leading {@code '/'}, then it is prepended to the path.</li>
   * </ul>
   * <p>
   * This method assumes the specified string path is an absolute path, and detects Windows paths using
   * {@link StringPaths#isAbsoluteLocalWindows(String)}.
   *
   * @param stringPath The string path from which to create a {@link URL}.
   * @return A {@link URL} created from the specified string, or {@code null} if the specified string is null.
   * @throws IllegalArgumentException If a protocol is specified but is unknown, or the spec is null, or the parsed URL fails to
   *           comply with the specific syntax of the associated protocol.
   * @see StringPaths#isAbsoluteLocalWindows(String)
   */
  public static URL fromStringPath(String stringPath) {
    if (stringPath == null)
      return null;

    if (stringPath.contains(":/") && stringPath.charAt(0) != '/')
      return create(stringPath);

    if (StringPaths.isAbsoluteLocalWindows(stringPath))
      stringPath = "/" + stringPath.replace('\\', '/');

    return create("file", "", stringPath);
  }

  /**
   * Creates a {@link URL} from the specified {@link URI}.
   * <p>
   * This convenience method works as if invoking it were equivalent to evaluating the expression {@code new URL(uri.toString())}
   * after first checking that this URI is absolute.
   *
   * @param uri The {@link URI} to convert to a {@link URL}.
   * @return The new {@link URL}.
   * @throws IllegalArgumentException If the specified {@link URI uri} is null, or if this {@link URL} is not absolute.
   * @throws UncheckedIOException If a protocol handler for the {@link URL} could not be found, or if some other error occurred
   *           while constructing the {@link URL}.
   */
  public static URL fromURI(final URI uri) {
    try {
      return assertNotNull(uri).toURL();
    }
    catch (final MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns a canonical {@link URL} created from the specified string, or {@code null} if the specified string is null
   * ({@code ".."} and {@code "."} path names are dereferenced in a canonical {@link URL}, and redundant {@code '/'} path separators
   * are removed).
   * <ul>
   * <li>If the specified path contains the string {@code ":/"}, the resulting {@link URL} is created with
   * {@link URL#URL(String)}.</li>
   * <li>Otherwise, if the specified path does not have a leading {@code '/'}, then it is prepended to the path.</li>
   * </ul>
   * <p>
   * This method assumes the specified string is an absolute path, and detects Windows paths using
   * {@link StringPaths#isAbsoluteLocalWindows(String)}.
   *
   * @param stringPath The string from which to create a {@link URL}.
   * @return A canonical {@link URL} created from the specified string, or {@code null} if the specified string is null
   *         ({@code ".."} and {@code "."} path names are dereferenced in a canonical {@link URL}, and redundant {@code '/'} path
   *         separators are removed).
   * @throws IllegalArgumentException If {@code stringPath} is null, or if a protocol is specified but is unknown, or the spec is
   *           null, or the parsed URL fails to comply with the specific syntax of the associated protocol.
   * @see URLs#canonicalize(URL)
   * @see StringPaths#isAbsoluteLocalWindows(String)
   */
  public static URL toCanonicalURL(String stringPath) {
    if (StringPaths.isAbsoluteLocalWindows(stringPath))
      stringPath = "/" + stringPath.replace('\\', '/');

    return toCanonicalURL0(stringPath);
  }

  /**
   * Returns a canonical {@link URL} created from the specified {@code basedir} parent directory, and {@code path} child path
   * ({@code ".."} and {@code "."} path names are dereferenced in a canonical {@link URL}, and redundant {@code '/'} path separators
   * are removed).
   *
   * @param basedir The base directory of the path in the resulting {@link URL}.
   * @param path The child path off of {@code basedir} in the resulting {@link URL}.
   * @return A canonical {@link URL} created from the specified {@code basedir} parent directory, and {@code path} child path
   *         ({@code ".."} and {@code "."} path names are dereferenced in a canonical {@link URL}, and redundant {@code '/'} path
   *         separators are removed).
   * @throws IllegalArgumentException If {@code basedir} is null, or if {@code basedir} or {@code path} is null, or if a protocol is
   *           specified but is unknown, or the spec is null, or the parsed URL fails to comply with the specific syntax of the
   *           associated protocol.
   * @see URLs#fromStringPath(String)
   * @see URLs#canonicalize(URL)
   * @see StringPaths#newPath(String,String)
   * @see StringPaths#isAbsoluteLocalWindows(String)
   */
  public static URL toCanonicalURL(String basedir, final String path) {
    if (StringPaths.isAbsoluteLocalWindows(basedir))
      basedir = "/" + basedir.replace('\\', '/');

    return toCanonicalURL0(StringPaths.newPath(basedir, path));
  }

  private static URL toCanonicalURL0(String stringPath) {
    final String canonicalPath = StringPaths.canonicalize(stringPath);
    if (canonicalPath.charAt(0) == '/' || !canonicalPath.contains(":/") && !canonicalPath.startsWith("file:") && !canonicalPath.startsWith("jar:file:"))
      return create("file", "", canonicalPath);

    return create(canonicalPath);
  }

  /**
   * Converts an array of {@link File} objects into an array of {@link URL} objects. {@link File} objects that are {@code null} will
   * be {@code null} in the resulting {@code URL[]} array.
   *
   * @param files The array of {@link File} objects.
   * @return An array of {@link URL} objects.
   * @throws IllegalArgumentException If {@code files} or any member of {@code files} is null, or if a protocol handler for the URL
   *           could not be found, or if some other error occurred while constructing the URL.
   */
  public static URL[] toURL(final File ... files) {
    try {
      final URL[] urls = new URL[assertNotNull(files).length];
      for (int i = 0; i < files.length; ++i) // [A]
        urls[i] = assertNotNull(files[i]).toURI().toURL();

      return urls;
    }
    catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Returns the canonical version of the specified {@link URL}, where redundant names such as {@code "."} and {@code ".."} are
   * dereferenced and removed from the path.
   *
   * @param url The {@link URL}.
   * @return The canonical version of the specified {@link URL}, where redundant names such as {@code "."} and {@code ".."} are
   *         dereferenced and removed from the path.
   * @throws IllegalArgumentException If a protocol is specified but is unknown, or the spec is null, or the parsed URL fails to
   *           comply with the specific syntax of the associated protocol.
   */
  public static URL canonicalize(final URL url) {
    return url == null ? null : create(url.getProtocol(), url.getHost(), url.getPort(), StringPaths.canonicalize(url.getPath().toString()));
  }

  /**
   * Tests whether the specified {@link URL} references a resource that exists.
   * <p>
   * This method performs the following tests to check for the existence of the resource at the specified {@link URL}:
   * <ol>
   * <li>If the protocol of the specified {@link URL} is {@code "file"}, this method converts the {@link URL} to a {@link File} and
   * delegates to {@link File#exists()}.</li>
   * <li>Otherwise, the method attempts to open a connection to the resource with the specified connection {@code timeout}. If the
   * connection is successful, the method returns {@code true}, and otherwise {@code false}.</li>
   * </ol>
   *
   * @param url The {@link URL} to test.
   * @param timeout The timeout to be used when attempting to open a connection to the {@code url}.
   * @return {@code true} if the specified {@link URL} references a resource that exists; otherwise {@code false}.
   * @throws IllegalArgumentException If {@code url} is null, or if {@code timeout} is negative.
   */
  public static boolean exists(final URL url, final int timeout) {
    try {
      if ("file".equals(assertNotNull(url).getProtocol()))
        return new File(url.toURI()).exists();
    }
    catch (final URISyntaxException ignored) {
    }

    try {
      final URLConnection connection = url.openConnection();
      connection.setConnectTimeout(timeout);
      connection.getInputStream().close();
      return true;
    }
    catch (final IOException e) {
      return false;
    }
  }

  /**
   * Tests whether the specified {@link URL} references a resource that exists.
   * <p>
   * This method performs the following tests to check for the existence of the resource at the specified {@link URL}:
   * <ol>
   * <li>If the protocol of the specified {@link URL} is {@code "file"}, this method converts the {@link URL} to a {@link File} and
   * delegates to {@link File#exists()}.</li>
   * <li>Otherwise, the method attempts to open a connection to the resource with the {@link #DEFAULT_TIMEOUT}. If the connection is
   * successful, the method returns {@code true}, and otherwise {@code false}.</li>
   * </ol>
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} references a resource that exists; otherwise {@code false}.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static boolean exists(final URL url) {
    return exists(url, DEFAULT_TIMEOUT);
  }

  /**
   * Tests whether the specified {@link URL} represents a file path. An URL is considered a file if its protocol is "file"
   * (case-insensitive), and its host value is empty or equal to {@code "localhost"}.
   *
   * @param url The {@link URL}.
   * @return {@code true} if the specified {@link URL} represents a file path; otherwise {@code false}.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static boolean isLocalFile(final URL url) {
    final String host = assertNotNull(url).getHost();
    return "file".equalsIgnoreCase(url.getProtocol()) && (host == null || host.length() == 0 || "localhost".equals(host));
  }

  /**
   * Tests whether the specified {@link URL} represents a location that is a local JAR file with protocol {@code "jar:file:"}.
   * <p>
   * The composite protocol definition is unwrapped in order to determine if the root resource locator in the URL is local. This
   * method then uses {@link URLs#isLocalFile(URL)} to check whether {@code url} is local.
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} represents a location that is local; otherwise {@code false}.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static boolean isLocalJarFile(URL url) {
    do {
      if (!assertNotNull(url).toString().startsWith("jar:"))
        return false;

      final String path = url.toString().substring(4);
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
   * Tests whether the specified {@link URL} represents a location that is either a local file with protocol {@code "file:"}, or a
   * local JAR file with protocol {@code "jar:file:"}.
   * <p>
   * URLs with composite protocol definitions, such as {@code "jar:file:"} are first unwrapped in order to digest the root resource
   * locator in the URL. This method then uses {@link URLs#isLocalFile(URL)} to check whether {@code url} is local.
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} represents a location that is either a local file with protocol
   *         {@code "file:"}, or a local JAR file with protocol {@code "jar:file:"}; otherwise {@code false}.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static boolean isLocal(final URL url) {
    return isLocalFile(url) || isLocalJarFile(url);
  }

  /**
   * Tests whether the specified {@link URL} references a Jar resource, otherwise {@code false}.
   *
   * @param url The {@link URL} to test.
   * @return {@code true} if the specified {@link URL} references a Jar resource; otherwise {@code false}.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static boolean isJar(final URL url) {
    try {
      return assertNotNull(url).toURI().toString().startsWith("jar:");
    }
    catch (final URISyntaxException e) {
      return false;
    }
  }

  /**
   * Returns a {@link URL} representing the location of the Jar in {@code url}, if {@code url} is "Jar URL" resembling the
   * {@code "jar:<url>...!..."} semantics; or {@code null} if {@code url} is not a "Jar URL".
   *
   * @param url The {@link URL}.
   * @return A {@link URL} representing the location of the Jar in {@code url}, if {@code url} is "Jar URL" resembling the
   *         {@code "jar:<url>...!..."} semantics; or {@code null} if {@code url} is not a "Jar URL".
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static URL getJarURL(final URL url) {
    return isJar(url) ? create(url.getFile().substring(0, url.getFile().indexOf('!'))) : null;
  }

  /**
   * Returns the path portion of the resource referenced inside a Jar of the specified {@code url} "Jar URL" (if {@code url} is "Jar
   * URL" resembling the {@code "jar:<url>...!..."} semantics), or {@code null} if {@code url} is not a "Jar URL".
   *
   * @param url The {@link URL}.
   * @return The path portion of the resource referenced inside a Jar of the specified {@code url} "Jar URL" (if {@code url} is "Jar
   *         URL" resembling the {@code "jar:<url>...!..."} semantics), or {@code null} if {@code url} is not a "Jar URL".
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static String getJarPath(final URL url) {
    return !isJar(url) ? null : url.getFile().substring(url.getFile().indexOf('!') + 2);
  }

  /**
   * Returns the name of the file or directory denoted by the specified {@link URL}. This is just the last name in the name sequence
   * of {@code url}. If the name sequence of {@code url} is empty, then the empty string is returned.
   *
   * @param url The {@link URL}.
   * @return The name of the file or directory denoted by the specified {@link URL}, or the empty string if the name sequence of
   *         {@code url} is empty.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static String getName(final URL url) {
    return StringPaths.getName(assertNotNull(url).toString());
  }

  /**
   * Returns the simple name of the file or directory denoted by the specified {@link URL}. This is just the last name in the name
   * sequence of {@code url}, with its dot-extension removed if present. If the name sequence of {@code url} is empty, then the
   * empty string is returned.
   *
   * @param url The {@link URL}.
   * @return The simple name of the file or directory denoted by the specified {@link URL}, or the empty string if the name sequence
   *         of {@code url} is empty.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static String getSimpleName(final URL url) {
    return StringPaths.getSimpleName(assertNotNull(url).toString());
  }

  /**
   * Returns the URL representing the parent of the specified {@link URL}, or {@code null} if {@code url} is null or does not name a
   * parent directory.
   *
   * @param url The {@link URL}.
   * @return The URL representing the parent of the specified {@link URL}, or {@code null} if {@code url} does not name a parent
   *         directory.
   * @throws IllegalArgumentException If {@code url} is null.
   * @see StringPaths#getParent(String)
   */
  public static URL getParent(final URL url) {
    final String parentPath = StringPaths.getParent(assertNotNull(url).toString());
    return parentPath == null ? null : create(parentPath);
  }

  /**
   * Returns the canonical URL representing the parent of the specified {@link URL}, or {@code null} if {@code url} is null or does
   * not name a parent directory ({@code ".."} and {@code "."} path names are dereferenced in a canonical {@link URL})).
   *
   * @param url The {@link URL}.
   * @return The URL representing the parent of the specified {@link URL}, or {@code null} if {@code url} is null or does not name a
   *         parent directory ({@code ".."} and {@code "."} path names are dereferenced in a canonical {@link URL}, and redundant
   *         {@code '/'} path separators are removed).
   * @see StringPaths#getParent(String)
   */
  public static URL getCanonicalParent(final URL url) {
    return url == null ? null : create(StringPaths.getCanonicalParent(url.toString()));
  }

  /**
   * Translates a string into {@code application/x-www-form-urlencoded} format. This method uses UTF-8 as the character encoding.
   *
   * @param s {@link String} to be translated.
   * @return The translated {@link String}.
   */
  public static String encode(final String s) {
    return encode(s, StandardCharsets.UTF_8.name());
  }

  /**
   * Translates a string into {@code application/x-www-form-urlencoded} format using a specific encoding scheme.
   * <p>
   * This method behaves the same as {@link URLEncoder#encode(String,String)} except that, instead of throwing a
   * {@link UnsupportedEncodingException} if the named encoding is not supported, this method will instead will instead throw a
   * {@link UnsupportedOperationException}.
   * <p>
   * This method is intended purely to remove the need to catch the {@link UnsupportedEncodingException} if using
   * {@link URLEncoder#encode(String,String)} directly.
   *
   * @param s {@link String} to be translated.
   * @param enc The name of a supported character encoding.
   * @return The translated {@link String}.
   * @throws UnsupportedOperationException If the named encoding is not supported.
   * @throws IllegalArgumentException If {@code s} or {@code enc} is null.
   * @see URLs#decode(String,String)
   */
  public static String encode(final String s, final String enc) {
    try {
      return URLEncoder.encode(assertNotNull(s), assertNotNull(enc));
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Translates a string into {@code application/x-www-form-urlencoded} format using a specific {@link Charset}. This method uses
   * the supplied charset to obtain the bytes for unsafe characters.
   * <p>
   * <em><strong>Note:</strong> The <a href= "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars"> World Wide Web
   * Consortium Recommendation</a> states that UTF-8 should be used. Not doing so may introduce incompatibilities.</em>
   * <p>
   * This method is intended purely to remove the need to catch the {@link UnsupportedEncodingException} if using
   * {@link URLEncoder#encode(String,String)} directly.
   *
   * @param s {@link String} to be translated.
   * @param charset The {@link Charset}.
   * @return The translated {@link String}.
   * @throws UnsupportedOperationException If the named encoding is not supported.
   * @throws IllegalArgumentException If {@code s} or {@code charset} is null.
   * @see URLs#decode(String,Charset)
   */
  public static String encode(final String s, final Charset charset) {
    try {
      return URLEncoder.encode(assertNotNull(s), assertNotNull(charset).name());
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Decodes a {@code application/x-www-form-urlencoded} string. This method uses UTF-8 as the character encoding.
   *
   * @param s The {@link String} to decode.
   * @return The decoded {@link String}.
   * @throws IllegalArgumentException If {@code s} is null, or if the implementation encounters illegal path separators.
   */
  public static String decode(final String s) {
    return decode(s, StandardCharsets.UTF_8, false);
  }

  /**
   * Decodes an {@code application/x-www-form-urlencoded} string using a specific {@link Charset}. The supplied charset is used to
   * determine what characters are represented by any consecutive sequences of the form "{@code %xy}".
   *
   * @implSpec The <a href= "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars"> World Wide Web Consortium
   *           Recommendation</a> states that UTF-8 should be used. Not doing so may introduce incompatibilities.
   * @param s The {@link String} to decode.
   * @param charset The {@link Charset}.
   * @return The decoded {@link String}.
   * @throws IllegalArgumentException If {@code s} or {@code charset} is null, or if the implementation encounters illegal
   *           characters.
   * @see URLs#encode(String,Charset)
   */
  public static String decode(final String s, final Charset charset) {
    return decode(s, assertNotNull(charset), false);
  }

  /**
   * Decodes an {@code application/x-www-form-urlencoded} string using a specific encoding scheme.
   * <p>
   * This method behaves the same as {@link URLs#decode(String,Charset)} except that it will {@linkplain Charset#forName look up the
   * charset} using the provided encoding name.
   *
   * @param s The {@link String} to decode.
   * @param enc The name of a supported encoding.
   * @return The decoded {@link String}.
   * @throws IllegalArgumentException If {@code s} or {@code charset} is null, or if the implementation encounters illegal
   *           characters, or if the provided charset name is null.
   * @throws IllegalCharsetNameException If the provided charset name is illegal.
   * @throws UnsupportedCharsetException If no support for the named charset is available in this instance of the Java virtual
   *           machine.
   * @see URLs#encode(String,String)
   */
  public static String decode(final String s, final String enc) {
    return decode(s, Charset.forName(assertNotNull(enc)), false);
  }

  private static String decode(final String s, final Charset charset, final boolean isPath) {
    boolean needDecode = false;
    int escapesCount = 0;
    final int length = s.length();
    for (int i = 0; i < length; ++i) { // [N]
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

      ((Buffer)out).flip();
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

    for (int i = 0, i$ = value.length(); i < i$; ++i) { // [N]
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

  private static final String PATH_RESERVED_CHARACTERS = "=@/:!$&'(),;~";

  /**
   * Returns the URL-encoded path string.
   * <p>
   * URL path segments may contain {@code '+'} symbols which should not be decoded into {@code ' '}. This method delegates to
   * URLEncoder, then replaces {@code '+'} with {@code "%20"}.
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
   * URL path segments may contain {@code '+'} symbols which should not be decoded into {@code ' '}. This method replaces
   * {@code '+'} with {@code "%2B"} and delegates to URLDecoder.
   *
   * @param path The path to decode.
   * @return The URL-decoded path string.
   */
  public static String decodePath(final String path) {
    return decode(path, StandardCharsets.UTF_8, true);
  }

  /**
   * Returns a {@link URL} with {@code http} and {@code https} protocols disabled.
   * <p>
   * If the specified {@link URL} is of a protocol that is {@code http} or {@code https}, a new {@link URL} is returned with the
   * {@link URL#openConnection()} and {@link URL#openConnection(Proxy)} methods disabled (i.e. they will throw
   * {@link FileNotFoundException} when the {@link InputStream#read()} or {@link OutputStream#write(int)} methods are invoked);
   * otherwise the specified {@link URL} is returned unmodified.
   *
   * @param url The {@link URL} for which {@code http} and {@code https} protocols are to be disabled.
   * @return A {@link URL} with {@code http} and {@code https} protocols disabled.
   * @throws IllegalArgumentException If the specified {@link URL url} is null.
   */
  public static URL disableHttp(final URL url) {
    if (!url.getProtocol().startsWith("http"))
      return url;

    try {
      return new URL(url, "", new URLStreamHandler() {
        @Override
        protected InetAddress getHostAddress(final URL u) {
          return null;
        }

        @Override
        protected URLConnection openConnection(final URL u) throws IOException {
          return openConnection(u, null);
        }

        @Override
        protected URLConnection openConnection(final URL u, final Proxy proxy) throws IOException {
          return new DelegateURLConnection(proxy != null ? url.openConnection(proxy) : url.openConnection()) {
            @Override
            public InputStream getInputStream() {
              return new InputStream() {
                @Override
                public int read() throws FileNotFoundException {
                  throw new FileNotFoundException(u.toString());
                }
              };
            }

            @Override
            public OutputStream getOutputStream() {
              return new OutputStream() {
                @Override
                public void write(final int b) throws FileNotFoundException {
                  throw new FileNotFoundException(u.toString());
                }
              };
            }
          };
        }
      });
    }
    catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Disables all {@code http} and {@code https} access from this JVM.
   *
   * @implNote Once called, this cannot be undone.
   */
  public static void disableRemote() {
    OfflineURLStreamHandler.register();
  }

  private static class LiteralHostStreamHandler extends URLStreamHandler {
    private URL url;

    private URL full(final URL url) throws MalformedURLException {
      return this.url == null ? this.url = new URL(url, "") : this.url;
    }

    @Override
    protected synchronized InetAddress getHostAddress(final URL u) {
      return null;
    }

    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
      return full(u).openConnection();
    }

    @Override
    protected URLConnection openConnection(final URL u, final Proxy proxy) throws IOException {
      return full(u).openConnection(proxy);
    }
  }

  /**
   * Returns a {@link URL} for which the {@linkplain URL#getHost() host} is treated as a literal string (as opposed to its resolved
   * IP address, as is dereferenced by default during the {@link URL#equals(Object)} and {@link URL#hashCode()} operations).
   *
   * @param spec The String to parse as a URL.
   * @return A {@link URL} for which the {@linkplain URL#getHost() host} is treated as a literal string.
   * @throws IllegalArgumentException If no protocol is specified, or an unknown protocol is found, or spec is null, or the parsed
   *           URL fails to comply with the specific syntax of the associated protocol.
   * @throws SecurityException If a security manager exists and its checkPermission method doesn't allow specifying a stream
   *           handler.
   */
  public static URL withLiteralHost(final String spec) {
    return create(null, spec, new LiteralHostStreamHandler());
  }

  /**
   * Reads all bytes from the provided {@link URL} and returns the resulting buffer array.
   *
   * @param url The {@link URL} from which to read.
   * @return The {@code byte[]} containing all bytes that were read from the provided {@link URL}.
   * @throws IOException If the first byte cannot be read for any reason other than the end of the file, if the input stream has
   *           been closed, or if some other I/O error occurs.
   * @throws IllegalArgumentException If {@code url} is null.
   * @see InputStream#read(byte[])
   */
  public static byte[] readBytes(final URL url) throws IOException {
    try (final InputStream in = assertNotNull(url).openStream()) {
      final ByteArrayOutputStream buf = new ByteArrayOutputStream(1024);
      final byte[] data = new byte[1024];
      for (int length; (length = in.read(data)) != -1; buf.write(data, 0, length)); // [X]
      return buf.toByteArray();
    }
  }

  private URLs() {
  }
}