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

package org.libj.net.offline.http;

import org.libj.net.offline.OfflineURLStreamHandler;

/**
 * An "offline" stream handler for the {@code http} protocol.
 *
 * @see org.libj.net.offline.https.Handler
 * @see OfflineURLStreamHandler
 */
public class Handler extends OfflineURLStreamHandler {
  @Override
  protected int getDefaultPort() {
    return 80;
  }
}