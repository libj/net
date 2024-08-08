/* Copyright (c) 2024 LibJ
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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Utility functions for operations pertaining to {@link SSLContext}.
 */
public final class SSLContexts {
  /**
   * Returns a new {@link SSLContext} with the {@code TLSv1.2} secure socket protocol that trusts all valid and invalid clients and
   * servers.
   *
   * @return A new {@link SSLContext} that trusts all valid and invalid clients and servers.
   * @throws KeyManagementException If this operation fails.
   * @throws NoSuchAlgorithmException If no Provider supports a SSLContextSpi implementation for the {@code TLSv1.2} secure socket
   *           protocol.
   */
  public static SSLContext newNonValidatingSSLContext() throws KeyManagementException, NoSuchAlgorithmException {
    final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
    sslContext.init(null, new TrustManager[] {
      new X509TrustManager() {
        private final X509Certificate[] x509Certificates = {};

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return x509Certificates;
        }

        @Override
        public void checkClientTrusted(final X509Certificate[] certificate, final String str) {
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] certificate, final String str) {
        }
      }
    }, new SecureRandom());
    return sslContext;
  }

  private SSLContexts() {
  }
}