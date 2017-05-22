/* Copyright (c) 2016 lib4j
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

package org.safris.commons.net;

import java.io.IOException;
import java.net.ServerSocket;

public final class Ports {
  public static int findOpenPort(final int from, final int to) throws IOException {
    for (int port = from; port < to; port ++) {
      try (final ServerSocket socket = new ServerSocket(port)) {
        return socket.getLocalPort();
      }
      catch (final IOException e) {
        continue;
      }
    }

    throw new IOException("no available port found");
  }

  public static int findRandomOpenPort() throws IOException {
    try (final ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }

  private Ports() {
  }
}