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
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * A {@link FilterServletInputStream} contains some other input stream, which it uses as its basic source of data, possibly
 * transforming the data along the way or providing additional functionality. The class {@link FilterServletInputStream} itself
 * simply overrides all methods of {@link InputStream} with versions that pass all requests to the contained input stream.
 * Subclasses of {@link FilterServletInputStream} may further override some of these methods and may also provide additional methods
 * and fields.
 */
public class FilterServletInputStream extends ServletInputStream {
  /** The input stream to be filtered. */
  protected ServletInputStream in;

  /**
   * Creates a {@link FilterServletInputStream} by assigning the argument {@code in} to the field {@code this.in} so as to remember
   * it for later use.
   *
   * @param in The underlying input stream, or {@code null} if this instance is to be created without an underlying stream.
   * @throws IllegalArgumentException If {@code in} is null.
   */
  protected FilterServletInputStream(final ServletInputStream in) {
    this.in = assertNotNull(in);
  }

  /**
   * Creates a {@link FilterServletInputStream} with {@code this.in} as {@code null}.
   */
  protected FilterServletInputStream() {
  }

  @Override
  public boolean isFinished() {
    return in.isFinished();
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is returned as an {@code int} in the range {@code 0} to
   * {@code 255}. If no byte is available because the end of the stream has been reached, the value {@code -1} is returned. This
   * method blocks until input data is available, the end of the stream is detected, or an exception is thrown.
   * <p>
   * This method simply performs {@link #read() in.read()} and returns the result.
   *
   * @return The next byte of data, or {@code -1} if the end of the stream is reached.
   * @throws IOException If an I/O error occurs.
   * @see FilterServletInputStream#in
   */
  @Override
  public int read() throws IOException {
    return in.read();
  }

  /**
   * Reads up to {@code b.length} bytes of data from this input stream into an array of bytes. This method blocks until some input
   * is available.
   * <p>
   * This method simply performs the call {@link #read(byte[],int,int) read(b, 0, b.length)} and returns the result. It is important
   * that it does <i>not</i> do {@link #read(byte[]) in.read(b)} instead; certain subclasses of {@link FilterServletInputStream}
   * depend on the implementation strategy actually used.
   *
   * @param b The buffer into which the data is read.
   * @return The total number of bytes read into the buffer, or {@code -1} if there is no more data because the end of the stream
   *         has been reached.
   * @throws IOException If an I/O error occurs.
   * @see FilterServletInputStream#read(byte[], int, int)
   */
  @Override
  public int read(final byte b[]) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Reads up to {@code len} bytes of data from this input stream into an array of bytes. If {@code len} is not zero, the method
   * blocks until some input is available; otherwise, no bytes are read and {@code 0} is returned.
   * <p>
   * This method simply performs {@link #read(byte[],int,int) in.read(b, off, len)} and returns the result.
   *
   * @param b The buffer into which the data is read.
   * @param off The start offset in the destination array {@code b}
   * @param len The maximum number of bytes read.
   * @return The total number of bytes read into the buffer, or {@code -1} if there is no more data because the end of the stream
   *         has been reached.
   * @throws NullPointerException If {@code b} is {@code null}.
   * @throws IndexOutOfBoundsException If {@code off} is negative, {@code len} is negative, or {@code len} is greater than
   *           {@code b.length - off}.
   * @throws IOException If an I/O error occurs.
   * @see FilterServletInputStream#in
   */
  @Override
  public int read(final byte b[], final int off, final int len) throws IOException {
    return in.read(b, off, len);
  }

  /**
   * Skips over and discards {@code n} bytes of data from the input stream. The {@code skip} method may, for a variety of reasons,
   * end up skipping over some smaller number of bytes, possibly {@code 0}. The actual number of bytes skipped is returned.
   * <p>
   * This method simply performs {@link #skip(long) in.skip(n)}.
   *
   * @param n The number of bytes to be skipped.
   * @return The actual number of bytes skipped.
   * @throws IOException If {@link #skip(long) in.skip(n)} throws an IOException.
   */
  @Override
  public long skip(final long n) throws IOException {
    return in.skip(n);
  }

  /**
   * Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking by the
   * next caller of a method for this input stream. The next caller might be the same thread or another thread. A single read or
   * skip of this many bytes will not block, but may read or skip fewer bytes.
   * <p>
   * This method returns the result of {@link #in in}.available().
   *
   * @return An estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public int available() throws IOException {
    return in.available();
  }

  /**
   * Closes this input stream and releases any system resources associated with the stream. This method simply performs
   * {@link #close() in.close()}.
   *
   * @throws IOException If an I/O error occurs.
   * @see FilterServletInputStream#in
   */
  @Override
  public void close() throws IOException {
    in.close();
  }

  /**
   * Marks the current position in this input stream. A subsequent call to the {@link #reset() reset()} method repositions this
   * stream at the last marked position so that subsequent reads re-read the same bytes.
   * <p>
   * The {@code readlimit} argument tells this input stream to allow that many bytes to be read before the mark position gets
   * invalidated.
   * <p>
   * This method simply performs {@link #mark(int) in.mark(readlimit)}.
   *
   * @param readlimit The maximum limit of bytes that can be read before the mark position becomes invalid.
   * @see FilterServletInputStream#in
   * @see FilterServletInputStream#reset()
   */
  @Override
  public synchronized void mark(final int readlimit) {
    in.mark(readlimit);
  }

  /**
   * Repositions this stream to the position at the time the {@code mark} method was last called on this input stream.
   * <p>
   * This method simply performs {@link #reset() in.reset()}.
   * <p>
   * Stream marks are intended to be used in situations where you need to read ahead a little to see what's in the stream. Often
   * this is most easily done by invoking some general parser. If the stream is of the type handled by the parse, it just chugs
   * along happily. If the stream is not of that type, the parser should toss an exception when it fails. If this happens within
   * readlimit bytes, it allows the outer code to reset the stream and try another parser.
   *
   * @throws IOException If the stream has not been marked or if the mark has been invalidated.
   * @see FilterServletInputStream#in
   * @see FilterServletInputStream#mark(int)
   */
  @Override
  public synchronized void reset() throws IOException {
    in.reset();
  }

  /**
   * Tests if this input stream supports the {@link #mark(int) mark()} and {@link #reset() reset()} methods. This method simply
   * performs {@link #markSupported() in.markSupported()}.
   *
   * @return {@code true} if this stream type supports the {@link #mark(int) mark} and {@link #reset() reset} method; {@code false}
   *         otherwise.
   * @see FilterServletInputStream#in
   * @see InputStream#mark(int)
   * @see InputStream#reset()
   */
  @Override
  public boolean markSupported() {
    return in.markSupported();
  }

  @Override
  public boolean isReady() {
    return in.isReady();
  }

  @Override
  public void setReadListener(final ReadListener readListener) {
    in.setReadListener(readListener);
  }
}