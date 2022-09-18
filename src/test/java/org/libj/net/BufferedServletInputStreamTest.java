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

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import org.junit.Test;

public class BufferedServletInputStreamTest {
  private static class StringInputStream extends ServletInputStream {
    private final String str;
    private int pos;

    private StringInputStream(final String str) {
      this.str = str;
    }

    @Override
    public int readLine(final byte[] b, final int off, final int len) throws IOException {
      for (int i = 0, j = off; i < len; ++i) { // [X]
        int by = read();
        if (by == -1)
          return i;

        b[j++] = (byte)by;
        if (by == '\n')
          return i;
      }

      return len;
    }

    @Override
    public boolean isFinished() {
      return pos == str.length();
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(final ReadListener readListener) {
    }

    @Override
    public int read() throws IOException {
      return pos == str.length() ? -1 : str.charAt(pos++);
    }
  }

  private static final String testString = "Test_All_Tests\nTest_java_io_BufferedInputStream\nTest_java_io_BufferedOutputStream\nTest_java_io_ByteArrayInputStream\nTest_java_io_ByteArrayOutputStream\nTest_java_io_DataInputStream\nTest_java_io_File\nTest_java_io_FileDescriptor\nTest_java_io_FileInputStream\nTest_java_io_FileNotFoundException\nTest_java_io_FileOutputStream\nTest_java_io_FilterInputStream\nTest_java_io_FilterOutputStream\nTest_java_io_InputStream\nTest_java_io_IOException\nTest_java_io_OutputStream\nTest_java_io_PrintStream\nTest_java_io_RandomAccessFile\nTest_java_io_SyncFailedException\nTest_java_lang_AbstractMethodError\nTest_java_lang_ArithmeticException\nTest_java_lang_ArrayIndexOutOfBoundsException\nTest_java_lang_ArrayStoreException\nTest_java_lang_Boolean\nTest_java_lang_Byte\nTest_java_lang_Character\nTest_java_lang_Class\nTest_java_lang_ClassCastException\nTest_java_lang_ClassCircularityError\nTest_java_lang_ClassFormatError\nTest_java_lang_ClassLoader\nTest_java_lang_ClassNotFoundException\nTest_java_lang_CloneNotSupportedException\nTest_java_lang_Double\nTest_java_lang_Error\nTest_java_lang_Exception\nTest_java_lang_ExceptionInInitializerError\nTest_java_lang_Float\nTest_java_lang_IllegalAccessError\nTest_java_lang_IllegalAccessException\nTest_java_lang_IllegalArgumentException\nTest_java_lang_IllegalMonitorStateException\nTest_java_lang_IllegalThreadStateException\nTest_java_lang_IncompatibleClassChangeError\nTest_java_lang_IndexOutOfBoundsException\nTest_java_lang_InstantiationError\nTest_java_lang_InstantiationException\nTest_java_lang_Integer\nTest_java_lang_InternalError\nTest_java_lang_InterruptedException\nTest_java_lang_LinkageError\nTest_java_lang_Long\nTest_java_lang_Math\nTest_java_lang_NegativeArraySizeException\nTest_java_lang_NoClassDefFoundError\nTest_java_lang_NoSuchFieldError\nTest_java_lang_NoSuchMethodError\nTest_java_lang_NullPointerException\nTest_java_lang_Number\nTest_java_lang_NumberFormatException\nTest_java_lang_Object\nTest_java_lang_OutOfMemoryError\nTest_java_lang_RuntimeException\nTest_java_lang_SecurityManager\nTest_java_lang_Short\nTest_java_lang_StackOverflowError\nTest_java_lang_String\nTest_java_lang_StringBuffer\nTest_java_lang_StringIndexOutOfBoundsException\nTest_java_lang_System\nTest_java_lang_Thread\nTest_java_lang_ThreadDeath\nTest_java_lang_ThreadGroup\nTest_java_lang_Throwable\nTest_java_lang_UnknownError\nTest_java_lang_UnsatisfiedLinkError\nTest_java_lang_VerifyError\nTest_java_lang_VirtualMachineError\nTest_java_lang_vm_Image\nTest_java_lang_vm_MemorySegment\nTest_java_lang_vm_ROMStoreException\nTest_java_lang_vm_VM\nTest_java_lang_Void\nTest_java_net_BindException\nTest_java_net_ConnectException\nTest_java_net_DatagramPacket\nTest_java_net_DatagramSocket\nTest_java_net_DatagramSocketImpl\nTest_java_net_InetAddress\nTest_java_net_NoRouteToHostException\nTest_java_net_PlainDatagramSocketImpl\nTest_java_net_PlainSocketImpl\nTest_java_net_Socket\nTest_java_net_SocketException\nTest_java_net_SocketImpl\nTest_java_net_SocketInputStream\nTest_java_net_SocketOutputStream\nTest_java_net_UnknownHostException\nTest_java_util_ArrayEnumerator\nTest_java_util_Date\nTest_java_util_EventObject\nTest_java_util_HashEnumerator\nTest_java_util_Hashtable\nTest_java_util_Properties\nTest_java_util_ResourceBundle\nTest_java_util_tm\nTest_java_util_Vector\n";

  @Test
  public void testReadLineSe1parators() throws IOException {
    assertLines("A\nB\nC", "A\n", "B\n", "C");
    assertLines("A\n\n", "A\n", "\n");
  }

  @SuppressWarnings("cast")
  private static void assertLines(final String in, final String ... lines) throws IOException {
    final BufferedServletInputStream bufferedReader = new BufferedServletInputStream(new StringInputStream(in), in.length());
    final byte[] b = new byte[10];
    final int off = 2;
    final int len = 5;
    for (final String line : lines) { // [A]
      assertEquals(line.length(), bufferedReader.readLine(b, off, len));
      assertEquals((char)line.charAt(0), (char)b[off]);
    }

    assertEquals(-1, bufferedReader.readLine(b, off, len));
    assertTrue(bufferedReader.isFinished());
  }

  /**
   * @tests BufferedServletInputStream#BufferedReader(java.io.Reader)
   */
  @Test
  public void testConstructor() {
    // Test for method BufferedServletInputStream(java.io.Reader)
    assertTrue("Used in tests", true);
  }

  /**
   * @tests BufferedServletInputStream#BufferedReader(java.io.Reader, int)
   */
  @Test
  public void testConstructorI() {
    // Test for method BufferedServletInputStream(java.io.Reader, int)
    assertTrue("Used in tests", true);
  }

  /**
   * @tests BufferedServletInputStream#close()
   */
  @Test
  public void testClose() {
    // Test for method void BufferedServletInputStream.close()
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      in.close();
      in.read();
      fail("Read on closed stream");
    }
    catch (final IOException x) {
    }
  }

  /**
   * @throws IOException If an I/O error has occurred.
   * @tests BufferedServletInputStream#mark(int)
   */
  @Test
  public void testMarkI() throws IOException {
    final byte[] buf = new byte[testString.length()];

    // Test for method void BufferedServletInputStream.mark(int)
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      in.skip(500);
      in.mark(1000);
      in.skip(250);
      in.reset();
      in.read(buf, 0, 500);
      assertTrue("Failed to set mark properly", testString.substring(500, 1000).equals(new String(buf, 0, 500)));
    }

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), 800)) {
      in.skip(500);
      in.mark(250);
      in.read(buf, 0, 1000);
      in.reset();
      fail("Failed to invalidate mark properly");
    }
    catch (final IOException x) {
    }

    final byte[] bytes = new byte[256];
    for (int i = 0; i < 256; ++i) // [A]
      bytes[i] = (byte)i;

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(new String(bytes)), 12)) {
      in.skip(6);
      in.mark(14);
      in.read(new byte[14], 0, 14);
      in.reset();
      assertTrue("Wrong bytes", in.read() == (byte)6 && in.read() == (byte)7);
    }

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(new String(bytes)), 12)) {
      in.skip(6);
      in.mark(8);
      in.skip(7);
      in.reset();
      assertTrue("Wrong bytes 2", in.read() == (byte)6 && in.read() == (byte)7);
    }

    final String str = "01234";
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(str), 2)) {
      in.mark(3);
      final byte[] carray = new byte[3];
      final int result = in.read(carray);
      assertEquals(3, result);
      assertEquals("Assert 0:", '0', carray[0]);
      assertEquals("Assert 1:", '1', carray[1]);
      assertEquals("Assert 2:", '2', carray[2]);
      assertEquals("Assert 3:", '3', in.read());
    }

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(str), 2)) {
      in.mark(3);
      final byte[] carray = new byte[4];
      final int result = in.read(carray);
      assertEquals("Assert 4:", 4, result);
      assertEquals("Assert 5:", '0', carray[0]);
      assertEquals("Assert 6:", '1', carray[1]);
      assertEquals("Assert 7:", '2', carray[2]);
      assertEquals("Assert 8:", '3', carray[3]);
      assertEquals("Assert 9:", '4', in.read());
      assertEquals("Assert 10:", -1, in.read());
    }

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(str), str.length())) {
      in.mark(Integer.MAX_VALUE - 2);
      in.read();
    }
  }

  /**
   * @throws IOException If an I/O error has occurred.
   * @tests BufferedServletInputStream#markSupported()
   */
  @Test
  public void testMarkSupported() throws IOException {
    // Test for method boolean BufferedServletInputStream.markSupported()
    final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length());
    assertTrue("markSupported returned false", in.markSupported());
  }

  /**
   * @throws IOException If an I/O error has occurred.
   * @tests BufferedServletInputStream#read()
   */
  @Test
  public void testRead() throws IOException {
    // Test for method int BufferedServletInputStream.read()
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      int r = in.read();
      assertTrue("Byte read improperly", testString.charAt(0) == r);
    }
    catch (final IOException e) {
      fail("Exception during read test");
    }

    final byte[] bytes = new byte[256];
    for (int i = 0; i < 256; ++i) // [A]
      bytes[i] = (byte)i;

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(new String(bytes)), 12)) {
      assertEquals("Wrong initial byte", 0, in.read()); // Fill the
      // buffer
      final byte[] buf = new byte[14];
      in.read(buf, 0, 14); // Read greater than the buffer
      assertTrue("Wrong block read data", new String(buf).equals(new String(bytes, 1, 14)));
      assertEquals("Wrong bytes", 15, in.read()); // Check next byte
    }
    catch (final IOException e) {
      fail("Exception during read test 2:" + e);
    }
  }

  /**
   * @throws IOException If an I/O error has occurred.
   * @tests BufferedServletInputStream#read(byte[], int, int)
   */
  @Test
  public void testRead$CII() throws IOException {
    final byte[] ca = new byte[2];
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(""), 1)) {
      /* Null buffer should throw NPE even when len == 0 */
      try {
        in.read(null, 1, 0);
        fail("null buffer reading zero bytes should throw NPE");
      }
      catch (final NullPointerException e) {
      }

      try {
        in.close();
      }
      catch (final IOException e) {
        fail("Unexpected 1: " + e);
      }

      try {
        in.read(null, 1, 0);
        fail("null buffer reading zero bytes on closed stream should throw IOException");
      }
      catch (final IOException e) {
      }

      /* Closed reader should throw IOException reading zero bytes */
      try {
        in.read(ca, 0, 0);
        fail("Reading zero bytes on a closed reader should not work");
      }
      catch (final IOException e) {
      }

      /*
       * Closed reader should throw IOException in preference to index out of bounds
       */
      try {
        // Read should throw IOException before
        // ArrayIndexOutOfBoundException
        in.read(ca, 1, 5);
        fail("IOException should have been thrown");
      }
      catch (final IOException e) {
      }
    }

    // Test to ensure that a drained stream returns 0 at EOF
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream("  "), 2)) {
      try {
        assertEquals("Emptying the reader should return two bytes", 2, in.read(ca, 0, 2));
        assertEquals("EOF on a reader should be -1", -1, in.read(ca, 0, 2));
        assertEquals("Reading zero bytes at EOF should work", 0, in.read(ca, 0, 0));
      }
      catch (final IOException ex) {
        fail("Unexpected IOException: " + ex.getLocalizedMessage());
      }
    }

    // Test for method int BufferedServletInputStream.read(byte[], int, int)
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      final byte[] buf = new byte[testString.length()];
      in.read(buf, 50, 500);
      assertTrue("Bytes read improperly", new String(buf, 50, 500).equals(testString.substring(0, 500)));
    }
    catch (final IOException e) {
      fail("Exception during read test");
    }
  }

  /**
   * @throws IOException If an I/O error has occurred.
   * @tests BufferedServletInputStream#read(byte[], int, int)
   */
  @Test
  public void testRead$CIIException() throws IOException {
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      final byte[] nullBytes = null;
      final byte[] bytes = testString.getBytes();
      try {
        in.read(nullBytes, -1, -1);
        fail();
      }
      catch (final NullPointerException expected) {
      }
      catch (final IndexOutOfBoundsException expected) {
      }

      try {
        in.read(nullBytes, -1, 0);
        fail();
      }
      catch (final NullPointerException expected) {
      }
      catch (final IndexOutOfBoundsException expected) {
      }

      try {
        in.read(nullBytes, 0, -1);
        fail("should throw NullPointerException");
      }
      catch (final NullPointerException e) {
      }

      try {
        in.read(nullBytes, 0, 0);
        fail("should throw NullPointerException");
      }
      catch (final NullPointerException e) {
      }

      try {
        in.read(nullBytes, 0, 1);
        fail("should throw NullPointerException");
      }
      catch (final NullPointerException e) {
      }

      try {
        in.read(bytes, -1, -1);
        fail("should throw IndexOutOfBoundsException");
      }
      catch (final IndexOutOfBoundsException e) {
      }

      try {
        in.read(bytes, -1, 0);
        fail("should throw IndexOutOfBoundsException");
      }
      catch (final IndexOutOfBoundsException e) {
      }

      in.read(bytes, 0, 0);
      in.read(bytes, 0, bytes.length);
      in.read(bytes, bytes.length, 0);

      try {
        in.read(bytes, bytes.length + 1, 0);
        fail("should throw IndexOutOfBoundsException");
      }
      catch (final IndexOutOfBoundsException e) {
      }

      try {
        in.read(bytes, bytes.length + 1, 1);
        fail("should throw IndexOutOfBoundsException");
      }
      catch (final IndexOutOfBoundsException e) {
      }

      in.close();

      try {
        in.read(nullBytes, -1, -1);
        fail("should throw IOException");
      }
      catch (final IOException e) {
      }

      try {
        in.read(bytes, -1, 0);
        fail("should throw IOException");
      }
      catch (final IOException e) {
      }

      try {
        in.read(bytes, 0, -1);
        fail("should throw IOException");
      }
      catch (final IOException e) {
      }
    }
  }

  /**
   * @tests BufferedServletInputStream#readLine()
   */
  @Test
  public void testReadLine() {
    // Test for method java.lang.String BufferedServletInputStream.readLine()
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      final byte[] b = new byte[27];
      final int off = 3;
      final int len = 19;
      final int count = in.readLine(b, off, len);
      final String expected = "Test_All_Tests\n";
      assertEquals(expected.length(), count);
      final String actual = new String(b, off, count);
      assertEquals("readLine returned incorrect string", expected, actual);
    }
    catch (final IOException e) {
      fail("Exception during readLine test");
    }
  }

  /**
   * @throws IOException If an I/O error has occurred.
   * @tests BufferedServletInputStream#ready()
   */
  @Test
  public void testReady() throws IOException {
    // Test for method boolean BufferedServletInputStream.isReady()
    final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length());
    assertTrue("ready returned false", in.isReady());
  }

  /**
   * @tests BufferedServletInputStream#reset()
   */
  @Test
  public void testReset() {
    // Test for method void BufferedServletInputStream.reset()
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      in.skip(500);
      in.mark(900);
      in.skip(500);
      in.reset();
      final byte[] buf = new byte[testString.length()];
      in.read(buf, 0, 500);
      assertTrue("Failed to reset properly", testString.substring(500, 1000).equals(new String(buf, 0, 500)));
    }
    catch (final IOException e) {
      fail("Exception during reset test");
    }

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      in.skip(500);
      in.reset();
      fail("Reset succeeded on unmarked stream");
    }
    catch (final IOException x) {
    }
  }

  @Test
  public void testResetIOException() throws Exception {
    final int[] expected = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', -1};
    final String str = "1234567890";
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(str), 9)) {
      in.mark(9);

      for (int i = 0; i < 11; ++i) // [A]
        assertEquals(expected[i], in.read());

      try {
        in.reset();
        fail("should throw IOException");
      }
      catch (final IOException e) {
      }

      for (int i = 0; i < 11; ++i) // [N]
        assertEquals(-1, in.read());
    }

    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(str), str.length())) {
      in.mark(10);
      for (int i = 0; i < 10; ++i) // [A]
        assertEquals(expected[i], in.read());

      in.reset();
      for (int i = 0; i < 11; ++i) // [A]
        assertEquals(expected[i], in.read());
    }
  }

  /**
   * @tests BufferedServletInputStream#skip(long)
   */
  @Test
  public void testSkipJ() {
    // Test for method long BufferedServletInputStream.skip(long)
    try (final BufferedServletInputStream in = new BufferedServletInputStream(new StringInputStream(testString), testString.length())) {
      in.skip(500);
      final byte[] buf = new byte[testString.length()];
      in.read(buf, 0, 500);
      assertTrue("Failed to set skip properly", testString.substring(500, 1000).equals(new String(buf, 0, 500)));
    }
    catch (final IOException e) {
      fail("Exception during skip test");
    }
  }
}