/* Copyright (c) 2010 OpenJAX
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

package org.openjax.net;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

/**
 * Utility functions for operations pertaining to file downloads.
 */
public final class Downloads {
  private static final int BUFFER_SIZE = 4096;

  /**
   * Downloads a file from the specified {@code url} to the provided
   * {@code file}. If the provided {@code file} exists, its lastModified
   * timestamp is used to specify the {@code If-Modified-Since} header in the
   * GET request. Content is not downloaded if the file at the specified
   * {@code url} is not modified.
   *
   * @param url The {@code URL} from which to download.
   * @param file The destination {@code File}.
   * @return The HTTP response code.
   * @throws IOException If an I/O error has occurred.
   */
  public static int downloadFile(final String url, final File file) throws IOException {
    final HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
    try {
      connection.setIfModifiedSince(file.lastModified());
      final int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_NOT_MODIFIED && responseCode == HttpURLConnection.HTTP_OK) {
        try (
          final InputStream in = connection.getInputStream();
          final FileOutputStream out = new FileOutputStream(file);
        ) {
          final byte[] buffer = new byte[BUFFER_SIZE];
          for (int read; (read = in.read(buffer)) != -1; out.write(buffer, 0, read));
        }
      }

      return responseCode;
    }
    finally {
      connection.disconnect();
    }
  }

  /**
   * Send the given file as a byte array to the servlet response. If attachment
   * is set to true, then show a "Save as" dialogue, else show the file inline
   * in the browser or let the operating system open it in the right
   * application.
   *
   * @param response The {@code HttpServletResponse}.
   * @param bytes The file contents in a byte array.
   * @param fileName The file name.
   * @param attachment If {@code true}, "Content-Disposition" will be
   *          "attachment"; otherwise, "inline".
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code response}, {@code bytes}, or
   *           {@code fileName} is null.
   */
  public static void downloadFile(final HttpServletResponse response, final byte[] bytes, final String fileName, final boolean attachment) throws IOException {
    try (final InputStream in = new ByteArrayInputStream(bytes)) {
      downloadFile(response, in, fileName, attachment);
    }
  }

  /**
   * Send the given file as a File object to the servlet response. If attachment
   * is set to true, then show a "Save as" dialogue, else show the file inline
   * in the browser or let the operating system open it in the right
   * application.
   *
   * @param response The {@code HttpServletResponse}.
   * @param file The file as a File object.
   * @param attachment If {@code true}, "Content-Disposition" will be
   *          "attachment"; otherwise, "inline".
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code response} or {@code file} is null.
   */
  public static void downloadFile(final HttpServletResponse response, final File file, final boolean attachment) throws IOException {
    try (final InputStream in = new FileInputStream(file)) {
      downloadFile(response, in, file.getName(), attachment);
    }
  }

  /**
   * Send the given file as an InputStream to the servlet response. If
   * attachment is set to true, then show a "Save as" dialogue, else show the
   * file inline in the browser or let the operating system open it in the right
   * application.
   *
   * @param response The {@code HttpServletResponse}.
   * @param in The file contents in an InputStream.
   * @param fileName The file name.
   * @param attachment If {@code true}, "Content-Disposition" will be
   *          "attachment"; otherwise, "inline".
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code response}, {@code in}, or
   *           {@code fileName} is null.
   */
  public static void downloadFile(final HttpServletResponse response, final InputStream in, final String fileName, final boolean attachment) throws IOException {
    String contentType = URLConnection.guessContentTypeFromName(fileName);
    if (contentType == null)
      contentType = "application/octet-stream";

    int contentLength = in.available();

    response.reset();
    response.setContentLength(contentLength);
    response.setContentType(contentType);
    response.setHeader("Content-disposition", (attachment ? "attachment" : "inline") + "; filename=\"" + fileName + "\"");
    try (final OutputStream out = response.getOutputStream()) {
      while (contentLength-- > 0)
        out.write(in.read());

      out.flush();
    }
  }

  private Downloads() {
  }
}