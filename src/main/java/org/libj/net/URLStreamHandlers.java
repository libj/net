/* Copyright (c) 2020 LibJ
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

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * A utility class for registering and loading {@link URLStreamHandlerFactory} via SPI. This class is also a
 * {@link URLStreamHandlerFactory}, which it uses to register via {@link URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)} in
 * the event that the approach based on the {@code "java.protocol.handler.pkgs"} property does not work.
 */
public final class URLStreamHandlers implements URLStreamHandlerFactory {
  // TODO: Saving for future move to support jdk9+
//  private static boolean canLookupViaProvider(final String className) throws IOException {
//    final Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources("META-INF/services/java.net.spi.URLStreamHandlerProvider");
//    while (resources.hasMoreElements()) {
//      final URL url = resources.nextElement();
//      if (new String(URLs.readBytes(url)).contains(className)) {
//        return true;
//      }
//    }
//
//    return false;
//  }

  private static boolean canLookupViaProperty(final String className) {
    try {
      ClassLoader.getSystemClassLoader().loadClass(className);
      return true;
    }
    catch (final ClassNotFoundException e) {
      return false;
    }
  }

  /**
   * Uses Java's SPI to load all {@link URLStreamHandlerFactory} classes defined in
   * {@code META-INF/services/java.net.URLStreamHandlerFactory}.
   */
  public static void loadSPI() {
    final Iterator<URLStreamHandlerFactory> iterator = ServiceLoader.load(URLStreamHandlerFactory.class).iterator();
    while (iterator.hasNext())
      iterator.next();
  }

  /**
   * Registers the provided {@link URLStreamHandler} and its corresponding {@link URLStreamHandlerFactory} classes. This method
   * first attempts to register the handler with the {@code "java.protocol.handler.pkgs"} property definition. If this fails, this
   * method will then register the {@link URLStreamHandlers} class via
   * {@link URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)} to manage {@link URLStreamHandler} lookups.
   *
   * @param handlerClass The {@link URLStreamHandler} class to register.
   * @param factoryClass The corresponding {@link URLStreamHandlerFactory} class.
   */
  public static void register(final Class<? extends URLStreamHandler> handlerClass, final Class<? extends URLStreamHandlerFactory> factoryClass) {
    final String className = handlerClass.getName();
    // FIXME: jdk9+ code commented out
    // if (!canLookupViaProvider(className)) {
    String pkg = handlerClass.getPackage().getName();
    pkg = pkg.substring(0, pkg.lastIndexOf('.'));

    final String property = "java.protocol.handler.pkgs";
    final String pkgs = System.getProperty(property);
    if (pkgs == null || !pkgs.contains(pkg))
      System.setProperty(property, pkgs != null && pkgs.length() > 0 ? pkgs + "|" + pkg : pkg);

    if (!canLookupViaProperty(className)) {
      try {
        URL.setURLStreamHandlerFactory(new URLStreamHandlers());
      }
      catch (final Error e) {
      }
    }
    // }
  }

  private URLStreamHandlers() {
  }

  private final ThreadLocal<Object> lock = new ThreadLocal<>();

  @Override
  public URLStreamHandler createURLStreamHandler(final String protocol) {
    if (lock.get() != null)
      return null;

    lock.set(Boolean.TRUE);
    try {
      final Iterator<URLStreamHandlerFactory> iterator = ServiceLoader.load(URLStreamHandlerFactory.class).iterator();
      while (iterator.hasNext()) {
        final URLStreamHandler handler = iterator.next().createURLStreamHandler(protocol);
        if (handler != null)
          return handler;
      }

      return null;
    }
    finally {
      lock.set(null);
    }
  }
}