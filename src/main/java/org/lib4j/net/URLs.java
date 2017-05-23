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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import org.lib4j.lang.Paths;

public final class URLs {
  private static String formatWindowsPath(final String absolutePath) {
    return absolutePath.replace('\\', '/');
  }

  public static boolean isAbsolute(final String path) {
    if (path.charAt(0) == '/' || (Character.isLetter(path.charAt(0)) && path.charAt(1) == ':' && path.charAt(2) == '\\' && Character.isLetter(path.charAt(3))))
      return true;

    if (path.startsWith("file:/"))
      return true;

    return path.matches("^([a-zA-Z0-9]+:)?//.*$");
  }

  public static String decode(final URL url) {
    try {
      return URLDecoder.decode(url.getPath(), "UTF-8");
    }
    catch (final UnsupportedEncodingException e) {
      return url.getPath();
    }
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

    return URLs.canonicalizeURL(url);
  }

  public static URL makeUrlFromPath(final URL baseURL, final String path) throws MalformedURLException {
    if (baseURL == null || path == null)
      return null;

    final String externalForm = URLs.toExternalForm(baseURL);
    return new URL(externalForm.endsWith("/") ? externalForm + path : externalForm + "/" + path);
  }

  public static URL makeUrlFromPath(final String basedir, final String path) throws MalformedURLException {
    return makeUrlFromPath(Paths.newPath(basedir, path));
  }

  public static String toExternalForm(final CachedURL url) throws MalformedURLException {
    return toExternalForm(url.url);
  }

  public static String toExternalForm(final URL url) throws MalformedURLException {
    if (url == null)
      return null;

    try {
      return url.toURI().toASCIIString();
    }
    catch (final URISyntaxException e) {
      throw new MalformedURLException(url.toString() + e.getMessage());
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

  private URLs() {
  }
}