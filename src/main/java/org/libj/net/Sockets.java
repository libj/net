/* Copyright (c) 2016 LibJ
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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Utility functions for operations pertaining to {@link Socket} and
 * {@link ServerSocket} classes.
 */
public final class Sockets {
  /**
   * Returns the first available port, evaluating ports sequentially from
   * {@code from} to {@code to}.
   *
   * @param from The lowest port number to evaluate, inclusive.
   * @param to The highest port number to evaluate, exclusive.
   * @return The first available port.
   * @throws IOException If no available ports were found.
   * @throws IllegalArgumentException If {@code from < 1}, {@code from > to}, or
   *           {@code to > 65536}.
   */
  public static int findOpenPort(final int from, final int to) throws IOException {
    if (from < 1)
      throw new IllegalArgumentException("from < 1: " + from);

    if (from > to)
      throw new IllegalArgumentException("from (" + from + ") > to (" + to + ")");

    if (to > 65536)
      throw new IllegalArgumentException("to > 65536: " + to);

    for (int port = from; port < to; ++port) {
      try (final ServerSocket socket = new ServerSocket(port)) {
        return socket.getLocalPort();
      }
      catch (final IOException e) {
      }
    }

    throw new IOException("No available port found");
  }

  /**
   * Returns a random open port.
   *
   * @return A random open port.
   */
  public static int findRandomOpenPort() {
    while (true) {
      try (final ServerSocket socket = new ServerSocket(1 + (int)(Math.random() * 65535))) {
        return socket.getLocalPort();
      }
      catch (final IOException e) {
      }
    }
  }

  private Sockets() {
  }
}