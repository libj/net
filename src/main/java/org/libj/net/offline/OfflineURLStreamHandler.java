/* Copyright (c) 2019 LibJ
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

package org.libj.net.offline;

import java.io.FileNotFoundException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * An "offline" stream protocol handler. This handler effectively throws
 * {@link FileNotFoundException} from the {@link #openConnection(URL)} and
 * {@link #openConnection(URL,Proxy)} methods.
 *
 * @see org.libj.net.offline.http.Handler
 * @see org.libj.net.offline.https.Handler
 */
public abstract class OfflineURLStreamHandler extends URLStreamHandler {
  /**
   * Registers the {@link OfflineURLStreamHandler} in this JVM.
   */
  public static void register() {
    final String property = "java.protocol.handler.pkgs";
    final String pkgs = System.getProperty(property);
    final String pkg = OfflineURLStreamHandler.class.getPackage().getName();
    if (pkgs == null || !pkgs.contains(pkg))
      System.setProperty(property, pkgs != null && pkgs.length() > 0 ? pkgs + "|" + pkg : pkg);
  }

  @Override
  protected URLConnection openConnection(final URL u) throws FileNotFoundException {
    throw new FileNotFoundException(u.toString());
  }

  @Override
  protected URLConnection openConnection(final URL u, final Proxy p) throws FileNotFoundException {
    throw new FileNotFoundException(u.toString());
  }
}