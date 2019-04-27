/* Copyright (c) 2017 OpenJAX
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

package org.openjax.standard.net;

import java.net.URI;

public final class Services {
  private static Service makeService(final jnr.netdb.Service service) {
    return service == null ? null : new Service(service.getName(), service.getPort(), service.getAliases());
  }

  public static Service getService(final int port) {
    final Service service = makeService(jnr.netdb.Service.getServiceByPort(port, "tcp"));
    if (service != null)
      return service;

    return makeService(jnr.netdb.Service.getServiceByPort(port, "udp"));
  }

  public static Service getService(final URI uri) {
    return getService(uri.getScheme());
  }

  public static Service getService(final String scheme) {
    final Service service = makeService(jnr.netdb.Service.getServiceByName(scheme, "tcp"));
    if (service != null)
      return service;

    return makeService(jnr.netdb.Service.getServiceByName(scheme, "udp"));
  }

  private Services() {
  }
}