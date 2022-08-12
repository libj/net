/* Copyright (c) 2015 LibJ
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

import java.net.InetAddress;

/**
 * Utility methods for {@link InetAddress}.
 */
public final class InetAddresses {
  /**
   * Returns the decimal representation of the IP of an {@link InetAddress}
   * <p>
   * (i.e. where each number is specified as a value in the range 0-255)
   *
   * @param address The {@link InetAddress}.
   * @return The decimal representation of the IP of an {@link InetAddress}.
   * @throws IllegalArgumentException If {@code address} is null.
   */
  public static String toStringIP(final InetAddress address) {
    final byte[] bytes = assertNotNull(address).getAddress();
    final StringBuilder builder = new StringBuilder();
    for (int i = 0, i$ = bytes.length; i < i$; ++i) { // [A]
      if (i > 0)
        builder.append('.');

      builder.append(bytes[i] & 0xFF);
    }

    return builder.toString();
  }

  private InetAddresses() {
  }
}