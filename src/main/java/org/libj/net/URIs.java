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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.libj.lang.Assertions;
import org.libj.util.StringPaths;

/**
 * Utility functions for operations pertaining to {@link URI}.
 */
public final class URIs {
  /**
   * Creates a {@link URI} from the specified {@link URL}.
   * <p>
   * This method functions in the same way as {@code new URI(url.toString())}.
   * <p>
   * Note, any URL instance that complies with RFC 2396 can be converted to a
   * URI. However, some URLs that are not strictly in compliance can not be
   * converted to a URI.
   *
   * @param url The {@link URL} to convert to a {@link URI}.
   * @return The new {@link URI}.
   * @throws IllegalArgumentException if this URL is not formatted strictly
   *           according to to RFC2396 and cannot be converted to a URI.
   * @throws IllegalArgumentException If {@code url} is null.
   */
  public static URI fromURL(final URL url) {
    try {
      return Assertions.assertNotNull(url).toURI();
    }
    catch (final URISyntaxException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * Constructs a relative path between the specified {@code from} and provided
   * {@code to}.
   *
   * @param from The {@link URI} from which to start.
   * @param to The {@link URI} to which to end up.
   * @return The relativized {@link URI}, or {@code null} if either specified
   *         URIs are opaque.
   * @throws IllegalArgumentException If {@code from} or {@code to} is null.
   */
  public static URI relativePath(final URI from, final URI to) {
    Assertions.assertNotNull(from);
    Assertions.assertNotNull(to);
    // quick bail-out
    if (!from.isAbsolute() || !to.isAbsolute())
      return to;

    if (from.isOpaque() || to.isOpaque()) {
      // Unlikely case of an URN which can't deal with
      // relative path, such as urn:isbn:0451450523
      return to;
    }

    // Check for common root
    final URI root = from.resolve("/");
    // Different scheme/auth/host/port, return as is
    if (!root.equals(to.resolve("/")))
      return to;

    // Ignore hostname bits for the following , but add "/" in the beginning
    // so that in worst case we'll still return "/fred" rather than
    // "http://example.com/fred".
    final URI baseRel = URI.create("/").resolve(root.relativize(from));
    final URI uriRel = URI.create("/").resolve(root.relativize(to));

    // Is it same path?
    if (baseRel.getPath().equals(uriRel.getPath()))
      return baseRel.relativize(uriRel);

    // Direct siblings? (i.e. in same folder)
    URI commonBase = baseRel.resolve("./");
    if (commonBase.equals(uriRel.resolve("./")))
      return commonBase.relativize(uriRel);

    // No, then just keep climbing up until we find a common base.
    URI relative = URI.create("");
    while (!uriRel.getPath().startsWith(commonBase.getPath()) && !"/".equals(commonBase.getPath())) {
      commonBase = commonBase.resolve("../");
      relative = relative.resolve("../");
    }

    // Now we can use URI.relativize
    final URI relToCommon = commonBase.relativize(uriRel);
    // and prepend the needed ../
    return relative.resolve(relToCommon);
  }

  /**
   * Tests whether the specified {@link URI} represents a file path. A URI is
   * considered a file if its scheme is "file" (case-insensitive), and its host
   * value is empty or equal to {@code "localhost"}.
   *
   * @param uri The {@link URI}.
   * @return {@code true} if the specified {@link URI} represents a file path;
   *         otherwise {@code false}.
   * @throws IllegalArgumentException If {@code uri} is null.
   */
  public static boolean isLocalFile(final URI uri) {
    final String host = Assertions.assertNotNull(uri).getHost();
    return "file".equalsIgnoreCase(uri.getScheme()) && (host == null || host.length() == 0 || "localhost".equals(host));
  }

  /**
   * Tests whether the specified {@link URI} represents a location that is a
   * local JAR file with scheme {@code "jar:file:"}.
   * <p>
   * The compound scheme definition is unwrapped in order to determine if the
   * root resource locator in the URI is local. This method then uses
   * {@link #isLocalFile(URI)} to check whether {@code uri} is local.
   *
   * @param uri The {@link URI} to test.
   * @return {@code true} if the specified {@link URI} represents a location
   *         that is local; otherwise {@code false}.
   * @throws IllegalArgumentException If {@code uri} is null.
   */
  public static boolean isLocalJarFile(URI uri) {
    Assertions.assertNotNull(uri);
    do {
      if (!uri.toString().startsWith("jar:"))
        return false;

      final String path = uri.toString().substring(4);
      final int bang = path.lastIndexOf('!');
      try {
        if (isLocalFile(uri = new URI(bang == -1 ? path : path.substring(0, bang))))
          return true;
      }
      catch (final URISyntaxException e) {
        return false;
      }
    }
    while (true);
  }

  /**
   * Tests whether the specified {@link URI} represents a location that is
   * either a local file with scheme {@code "file:"}, or a local JAR file with
   * scheme {@code "jar:file:"}.
   * <p>
   * URIs with compound scheme definitions, such as {@code "jar:file:"} are
   * first unwrapped in order to digest the root resource locator in the URI.
   * This method then uses {@link #isLocalFile(URI)} to check whether
   * {@code uri} is local.
   *
   * @param uri The {@link URI} to test.
   * @return {@code true} if the specified {@link URI} represents a location
   *         that is either a local file with scheme {@code "file:"}, or a
   *         local JAR file with scheme {@code "jar:file:"}; otherwise
   *         {@code false}.
   * @throws IllegalArgumentException If {@code uri} is null.
   */
  public static boolean isLocal(final URI uri) {
    return isLocalFile(uri) || isLocalJarFile(uri);
  }

  /**
   * Returns the name of the file or directory denoted by the specified
   * {@link URI}. This is just the last name in the name sequence of
   * {@code uri}. If the name sequence of {@code uri} is empty, then the empty
   * string is returned.
   *
   * @param uri The {@link URI}.
   * @return The name of the file or directory denoted by the specified
   *         {@link URI}, or the empty string if the name sequence of
   *         {@code uri} is empty.
   * @throws IllegalArgumentException If {@code uri} is null.
   */
  public static String getName(final URI uri) {
    return StringPaths.getName(Assertions.assertNotNull(uri).toString());
  }

  /**
   * Returns the simple name of the file or directory denoted by the specified
   * {@link URI}. This is just the last name in the name sequence of
   * {@code uri}, with its dot-extension removed if present. If the name
   * sequence of {@code uri} is empty, then the empty string is returned.
   *
   * @param uri The {@link URI}.
   * @return The simple name of the file or directory denoted by the specified
   *         {@link URI}, or the empty string if the name sequence of
   *         {@code uri} is empty.
   * @throws IllegalArgumentException If {@code uri} is null.
   */
  public static String getSimpleName(final URI uri) {
    return StringPaths.getSimpleName(Assertions.assertNotNull(uri).toString());
  }

  /**
   * Returns the URI representing the parent of the specified {@link URI}, or
   * {@code null} if {@code uri} is null or does not name a parent directory.
   *
   * @param uri The {@link URI}.
   * @return The URI representing the parent of the specified {@link URI}, or
   *         {@code null} if {@code uri} does not name a parent directory.
   * @see StringPaths#getParent(String)
   */
  public static URI getParent(final URI uri) {
    final String parentPath = StringPaths.getParent(Assertions.assertNotNull(uri).toString());
    return parentPath == null ? null : URI.create(parentPath);
  }

  /**
   * Returns the canonical URI representing the parent of the specified
   * {@link URI}, or {@code null} if {@code uri} is null or does not name a
   * parent directory ({@code ".."} and {@code "."} path names are dereferenced
   * in a canonical {@link URI})).
   *
   * @param uri The {@link URI}.
   * @return The URI representing the parent of the specified {@link URI}, or
   *         {@code null} if {@code uri} is null or does not name a parent
   *         directory ({@code ".."} and {@code "."} path names are dereferenced
   *         in a canonical {@link URI}, and redundant {@code '/'} path
   *         separators are removed).
   * @see StringPaths#getParent(String)
   */
  public static URI getCanonicalParent(final URI uri) {
    return uri == null ? null : URI.create(StringPaths.getCanonicalParent(uri.toString()));
  }

  /**
   * Returns an {@link URI} created from the specified {@code baseURI} parent
   * URI, and {@code path} child path.
   *
   * @param baseURI The base URI of the path in the resulting {@link URI}.
   * @param path The child path off of {@code baseURI} in the resulting
   *          {@link URI}.
   * @return An {@link URI} created from the specified {@code baseURI} parent
   *         directory, and {@code path} child path.
   * @throws IllegalArgumentException If the specified {@code baseURI} contains
   *           a query string; if both a scheme and a path are given but the
   *           path is relative, if the URI string constructed from the given
   *           components violates RFC 2396, or if the authority component of
   *           the string is present but cannot be parsed as a server-based
   *           authority.
   * @throws IllegalArgumentException If {@code baseURI} or {@code path} is null.
   */
  public static URI toURI(final URI baseURI, String path) {
    Assertions.assertNotNull(baseURI);
    Assertions.assertNotNull(path);
    final int slash = baseURI.getPath().lastIndexOf('/');
    if (slash != -1)
      path = baseURI.getPath().substring(0, slash + 1) + path;

    try {
      return new URI(baseURI.getScheme(), baseURI.getUserInfo(), baseURI.getHost(), baseURI.getPort(), path, baseURI.getQuery(), baseURI.getFragment());
    }
    catch (final URISyntaxException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  private URIs() {
  }
}