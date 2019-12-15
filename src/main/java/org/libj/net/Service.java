/* Copyright (c) 2017 LibJ
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

import java.util.Collection;

import org.libj.util.CollectionUtil;

/**
 * Class representing an IANA Network Service, as available in
 * {@code /etc/services}.
 */
public class Service {
  /** Official service name */
  private final String serviceName;
  /** TCP or UDP port number */
  private final int port;

  /** All the aliases for this service */
  private final Collection<String> aliases;

  /**
   * Creates a new {@link Service} with the specified service name, port, and
   * aliases.
   *
   * @param serviceName The service name.
   * @param port The port.
   * @param aliases The aliases.
   */
  protected Service(final String serviceName, final int port, final Collection<String> aliases) {
    this.serviceName = serviceName;
    this.port = port;
    this.aliases = aliases;
  }

  /**
   * Returns the name.
   *
   * @return The name.
   */
  public String getName() {
    return this.serviceName;
  }

  /**
   * Returns the port.
   *
   * @return The port.
   */
  public int getPort() {
    return this.port;
  }

  /**
   * Returns the aliases.
   *
   * @return The aliases.
   */
  public Collection<String> getAliases() {
    return this.aliases;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Service))
      return false;

    final Service that = (Service)obj;
    if (port != that.port)
      return false;

    if (serviceName != null ? that.serviceName == null : !serviceName.equals(that.serviceName))
      return false;

    if (aliases != null ? that.aliases == null : !aliases.equals(that.aliases))
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = port;
    hashCode = 31 * hashCode + (serviceName == null ? 0 : serviceName.hashCode());
    hashCode = 31 * hashCode + (aliases == null ? 0 : aliases.hashCode());
    return hashCode;
  }

  @Override
  public String toString() {
    final String alias = CollectionUtil.toString(aliases, "\", \"");
    return "{\n  name: \"" + serviceName + "\",\n  port: " + port + ",\n  aliases: [" + (alias.length() > 0 ? "\"" + alias + "\"" : "") + "]\n}";
  }
}