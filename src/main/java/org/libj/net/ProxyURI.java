/* Copyright (c) 2023 LibJ
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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

import org.libj.lang.Numbers;

/**
 * An immutable class representing a proxy URI of the form {@code <http|socks>://[username:password@]<host>[:port]}.
 */
public class ProxyURI {
  private static IllegalArgumentException malformedProxyUriException(final String proxyUri) {
    return new IllegalArgumentException("Malformed Proxy URI: <http|socks>://[username:password@]<host>[:port]: " + proxyUri);
  }

  protected final String proxyUri;
  protected final Proxy.Type type;
  protected final String host;
  protected final int port;
  protected final String username;
  protected final char[] password;
  private InetSocketAddress inetSocketAddress;
  private Proxy proxy;

  private InetSocketAddress inetSocketAddress() {
    return inetSocketAddress == null ? inetSocketAddress = new InetSocketAddress(host, port) : inetSocketAddress;
  }

  /**
   * Creates a new {@link ProxyURI} from the provided {@code proxyUri}.
   *
   * @param proxyUri The proxy URI of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   * @throws NullPointerException If {@code proxyUri} is null.
   * @throws IllegalArgumentException If the {@code proxyUri} is malformed.
   */
  public ProxyURI(final String proxyUri) {
    this.proxyUri = Objects.requireNonNull(proxyUri);
    final int len = proxyUri.length();
    int i = proxyUri.indexOf(':');
    if (len < i + 3 || proxyUri.charAt(i + 1) != '/' || proxyUri.charAt(i + 2) != '/')
      throw malformedProxyUriException(proxyUri);

    final String scheme = proxyUri.substring(0, i);
    type = scheme.startsWith("socks") ? Proxy.Type.SOCKS : Proxy.Type.HTTP;

    int j = i + 3;
    i = proxyUri.indexOf('@', j);

    if (i < 0) {
      username = null;
      password = null;
    }
    else {
      final int k = i;
      i = proxyUri.lastIndexOf(':', k - 1);
      if (i < j)
        throw malformedProxyUriException(proxyUri);

      username = proxyUri.substring(j, i);
      proxyUri.getChars(i + 1, k, password = new char[k - i - 1], 0);
      j = k + 1;
    }

    i = proxyUri.indexOf(':', j);
    if (i > -1) {
      host = proxyUri.substring(j, i);
      port = Numbers.parseInt(proxyUri.substring(i + 1), -1);
      if (port == -1)
        throw malformedProxyUriException(proxyUri);
    }
    else {
      host = proxyUri.substring(j);
      port = type == Proxy.Type.SOCKS ? 1080 : 3128;
    }
  }

  /**
   * Returns the type of the proxy.
   *
   * @return the type of the proxy.
   */
  public Proxy.Type getType() {
    return type;
  }

  /**
   * Returns the {@code host} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   *
   * @return The {@code host} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   */
  public String getHost() {
    return host;
  }

  /**
   * Returns the {@code port} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   *
   * @return The {@code port} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   */
  public int getPort() {
    return port;
  }

  /**
   * Returns the {@code username} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   *
   * @return The {@code username} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the {@code password} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   *
   * @return The {@code password} part of the proxy URI provided of the form {@code <http|socks>://[username:password@]<host>[:port]}.
   */
  public char[] getPassword() {
    return password;
  }

  /**
   * Returns the {@link InetAddress} resolved from the {@code host} part of the proxy URI provided of the form
   * {@code <http|socks>://[username:password@]<host>[:port]}, or {@code null} if the address is unresolved.
   *
   * @return The {@link InetAddress} resolved from the {@code host} part of the proxy URI provided of the form
   *         {@code <http|socks>://[username:password@]<host>[:port]}, or {@code null} if the address is unresolved.
   */
  public InetAddress getInetAddress() {
    return inetSocketAddress().getAddress();
  }

  /**
   * Returns and retains a {@link Proxy} instance initialized from this {@link ProxyURI}.
   *
   * @return A {@link Proxy} instance (which is also retained) initialized from this {@link ProxyURI}.
   */
  public Proxy getProxy() {
    return proxy == null ? proxy = new Proxy(type, inetSocketAddress()) : proxy;
  }

  @Override
  public int hashCode() {
    return 31 + proxyUri.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return obj == this || obj instanceof ProxyURI && proxyUri.equals(((ProxyURI)obj).proxyUri);
  }

  @Override
  public String toString() {
    return proxyUri;
  }
}