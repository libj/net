/* Copyright (c) 2022 LibJ
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

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.libj.io.Streams;

/**
 * A {@link BufferedServletInputStream} wraps a {@link ServletInputStream} in order to provide input buffering.
 * <p>
 * This implementation offers better performance for servlet containers that rely on the
 * {@link ServletInputStream#readLine(byte[],int,int)} method, which has been documented to be slow in Tomcat 3.2, Tomcat 3.1, the
 * JSWDK 1.0 web server and the JSDK2.1 web server.
 * <p>
 * This implementation also allows servlet containers to get around a known shortcoming regarding
 * {@link HttpServletRequest#getParameterMap()}, whereby the entirety of the input stream returned by
 * {@link HttpServletRequest#getInputStream()} is consumed by the container if the {@code Content-Type} header is
 * "application/x-www-form-urlencoded".
 * <p>
 * This implementation also provides a workaround for a bug in the Servlet API 2.0 implementation of
 * {@link ServletInputStream#readLine(byte[],int,int)}, which contains a bug that results in a
 * {@code ArrayIndexOutOfBoundsExceptions} under certain conditions. Apache JServ is known to suffer from this bug.
 */
public class BufferedServletInputStream extends FilterServletInputStream {
  private static final int INVALIDATED = -2;
  private static final int UNMARKED = -1;

  private byte[] buf;
  private int count, pos = 0;

  private int markpos = UNMARKED;
  private int readlimit; /* Valid only when markedChar > 0 */

  /**
   * Creates a {@link BufferedServletInputStream} with the underlying provided {@link ServletInputStream}, and reads the full
   * contents of the provided {@link ServletInputStream} into the buffer.
   *
   * @param in The {@link ServletInputStream} to wrap.
   * @param maxLength The maximum number of bytes to read.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code in} is null.
   * @throws IllegalArgumentException If {@code size <= 0}.
   */
  public BufferedServletInputStream(final ServletInputStream in, final int maxLength) throws IOException {
    super(in);
    this.buf = Streams.readBytes(in, maxLength);
    this.count = buf.length;
  }

  /**
   * Check to make sure that underlying input stream has not been nulled out due to close; if not return it;
   */
  private ServletInputStream getInIfOpen() throws IOException {
    final ServletInputStream input = in;
    if (input == null)
      throw new IOException("Stream closed");

    return input;
  }

  /**
   * Check to make sure that buffer has not been nulled out due to close; if not return it;
   */
  private byte[] getBufIfOpen() throws IOException {
    final byte[] buf = this.buf;
    if (buf == null)
      throw new IOException("Stream closed");

    return buf;
  }

  /**
   * Fills the input buffer, taking the mark into account if it is valid.
   */
  private void fill(byte[] buf) throws IOException {
    int dst;
    if (markpos <= UNMARKED) {
      /* No mark */
      dst = 0;
    }
    else {
      /* Marked */
      final int delta = pos - markpos;
      if (delta >= readlimit) {
        /* Gone past read-ahead limit: Invalidate mark */
        markpos = INVALIDATED;
        readlimit = 0;
        dst = 0;
      }
      else {
        if (readlimit <= buf.length) {
          /* Shuffle in the current buffer */
          System.arraycopy(buf, markpos, buf, 0, delta);
          markpos = 0;
          dst = delta;
        }
        else {
          /* Reallocate buffer to accommodate read-ahead limit */
          final byte[] ncb = new byte[readlimit];
          System.arraycopy(buf, markpos, ncb, 0, delta);
          this.buf = buf = ncb;
          markpos = 0;
          dst = delta;
        }

        pos = count = delta;
      }
    }

    final ServletInputStream in = getInIfOpen();
    int n;
    do
      n = in.read(buf, dst, buf.length - dst);
    while (n == 0);
    if (n > 0) {
      count = dst + n;
      pos = dst;
    }
  }

  @Override
  public int read() throws IOException {
    final byte[] buf = getBufIfOpen();
    for (;;) { // [X]
      if (pos >= count) {
        fill(buf);
        if (pos >= count)
          return -1;
      }

      return buf[pos++];
    }
  }

  /**
   * Reads bytes into a portion of an array, reading from the underlying stream if necessary.
   */
  private int read1(final byte[] b, final int off, final int len) throws IOException {
    if (pos >= count) {
      /*
       * If the requested length is at least as large as the buffer, and if there is no mark/reset activity, and if line feeds are
       * not being skipped, do not bother to copy the characters into the local buffer. In this way buffered streams will cascade
       * harmlessly.
       */
      if (len >= buf.length && markpos <= UNMARKED)
        return in.read(b, off, len);

      fill(buf);
    }

    if (pos >= count)
      return -1;

    final int n = Math.min(len, count - pos);
    System.arraycopy(buf, pos, b, off, n);
    pos += n;
    return n;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    final ServletInputStream in = getInIfOpen();
    assertBoundsOffsetCount("b.length", b.length, "off", off, "len", len);
    if (len == 0)
      return 0;

    int n = read1(b, off, len);
    if (n <= 0)
      return n;

    while (n < len && in.isReady()) {
      final int n1 = read1(b, off + n, len - n);
      if (n1 <= 0)
        break;

      n += n1;
    }

    return n;
  }

  @Override
  public boolean isFinished() {
    return pos >= count;
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException If {@code off} is negative, {@code len} is negative, or {@code b.length} is less than
   *           {@code off + len}.
   */
  @Override
  public int readLine(final byte[] b, final int off, final int len) throws IOException {
    assertBoundsOffsetCount("b.length", b.length, "off", off, "len", len);
    final byte[] buf = getBufIfOpen();
    if (len == 0)
      return 0;

    int end = off + len;
    int j = off;

    for (;;) { // [X]
      if (pos >= count)
        fill(buf);

      if (pos >= count) /* EOF */
        return j > off ? j - off : -1;

      for (byte by; pos < count;) { // [A]
        by = b[j++] = buf[pos++];
        if (by == '\n')
          return j - off;

        if (j == end)
          return len;
      }
    }
  }

  @Override
  public long skip(final long n) throws IOException {
    assertPositive(n);
    final byte[] buf = getBufIfOpen();

    long r = n;
    while (r > 0) {
      if (pos >= count)
        fill(buf);

      if (pos >= count) /* EOF */
        break;

      final long d = count - pos;
      if (r <= d) {
        pos += r;
        r = 0;
        break;
      }

      r -= d;
      pos = count;
    }

    return n - r;
  }

  @Override
  public int available() throws IOException {
    return count - pos + in.available();
  }

  @Override
  public boolean isReady() {
    return in != null && pos < count || in.isReady();
  }

  /**
   * Tells whether this stream supports the mark() operation, which it does.
   */
  @Override
  public boolean markSupported() {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException If {@code readlimit <= 0}.
   */
  @Override
  @SuppressWarnings("sync-override")
  public void mark(final int readlimit) {
    assertPositive(readlimit);
    this.readlimit = readlimit;
    this.markpos = pos;
  }

  @Override
  @SuppressWarnings("sync-override")
  public void reset() throws IOException {
    if (markpos < 0)
      throw new IOException(markpos == INVALIDATED ? "Mark invalid" : "Stream not marked");

    pos = markpos;
  }

  @Override
  public void close() throws IOException {
    if (in == null)
      return;

    try {
      in.close();
    }
    finally {
      in = null;
      buf = null;
    }
  }
}