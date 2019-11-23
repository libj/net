/* Copyright (c) 2008 LibJ
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class URLsTest {
  @Test
  public void testIsLocal() throws Exception {
    assertTrue(URLs.isLocal(new URL("jar:file:///C:/proj/parser/jar/parser.jar!/test.xml")));
    assertTrue(URLs.isLocal(new URL("file:///c:/path/to/the%20file.txt")));
    assertTrue(URLs.isLocal(new URL("file:///tmp.txt")));
    assertTrue(URLs.isLocal(new URL("jar:file:/root/app.jar!/repository")));
    assertTrue(URLs.isLocal(new URL("file://localhost/etc/fstab")));
    assertTrue(URLs.isLocal(new URL("file://localhost/c|/WINDOWS/clock.avi")));
    assertTrue(URLs.isLocal(new URL("file://localhost/c:/WINDOWS/clock.avi")));
    assertFalse(URLs.isLocal(new URL("http://127.0.0.1:8080/a.properties")));
    assertFalse(URLs.isLocal(new URL("file://hostname/path/to/the%20file.txt")));
    assertFalse(URLs.isLocal(new URL("ftp://user:password@server:80/path")));
    assertFalse(URLs.isLocal(new URL("https://mail.google.com/mail/u/0/?zx=gc46uk9snw66#inbox")));
    assertFalse(URLs.isLocal(new URL("jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class")));
  }

  public static void assertCanonicalURL(final URL expected, final String path) throws Exception {
    assertEquals(expected, URLs.toCanonicalURL(path));
  }

  public static void assertCanonicalURL(final URL expected, final String dir, final String path) throws Exception {
    assertEquals(expected, URLs.toCanonicalURL(dir, path));
    assertEquals(expected, URLs.toCanonicalURL(URLs.toURL(dir), path));
  }

  @Test
  public void testToCanonicalURL() throws Exception {
    assertCanonicalURL(new URL("file", "", "/c:/Windows"), "c:\\Windows");
    assertCanonicalURL(new URL("file", "", "/c:/Windows/system32"), "c:\\Windows", "system32");
    assertCanonicalURL(new URL("file", "", "/c:/Windows/system32"), "c:\\Windows", "\\foo\\..\\bar\\..\\system32");
    assertCanonicalURL(new URL("file", "", "/c:/Windows/system32"), "c:\\Windows\\", "system32");
    assertCanonicalURL(new URL("file", "", "/c:/Windows/system32"), "c:\\Windows\\", "\\bar\\..\\system32");

    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "/etc/resolv.conf");
    assertCanonicalURL(new URL("file", "", "/initrd.img"), "/initrd.img");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "", "etc/resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "", "/etc/resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "var/../etc/resolv.conf", "");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "/etc/resolv.conf", "");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "etc", "resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "etc", "foo/../resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "etc/", "resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "etc/", "/bar/../foo/../resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "/etc", "resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "/etc", "/resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "/etc/", "resolv.conf");
    assertCanonicalURL(new URL("file", "", "/etc/resolv.conf"), "/etc/", "/resolv.conf");

    assertCanonicalURL(new URL("http://www.google.com/webhp"), "http://www.google.com/webhp");
    assertCanonicalURL(new URL("http://www.google.com/webhp"), "http://www.google.com/webhp", "");
    assertCanonicalURL(new URL("http://www.google.com/webhp"), "http://www.google.com/", "webhp");
    assertCanonicalURL(new URL("http://www.google.com/webhp"), "http://www.google.com", "webhp");
    assertCanonicalURL(new URL("http://www.google.com/webhp"), "http://www.google.com", "/foo/../bar/../webhp");
    assertCanonicalURL(new URL("http://www.google.com/webhp"), "http://www.google.com/", "/foo/../webhp");
  }

  @Test
  public void testExists() throws Exception {
//    if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
//      assertTrue(URLs.exists(new URL("file", "", "/c:/")));
//    else
//      assertTrue(URLs.exists(new URL("file", "", "/usr")));
//
//    // FIXME: Some machines may not be connected to the web!
////      assertTrue(URLs.exists(new URL("http://www.google.com/")));
//
//    assertFalse(URLs.exists(new URL("file", "", "/ngfodbbgfid")));
    assertFalse(URLs.exists(new URL("http://fndos:9876/")));
  }

  @Test
  public void testCanonicalizeURL() throws Exception {
    final Map<URL,URL> map = new HashMap<>();
    map.put(new URL("file:///usr/share"), new URL("file:///usr/share/../share"));
    map.put(new URL("file:///usr/lib"), new URL("file:///usr/share/../share/../lib"));
    map.put(new URL("file:///var"), new URL("file:///usr/share/../share/../lib/../../var"));

    for (final Map.Entry<URL,URL> entry : map.entrySet())
      assertEquals(entry.getKey(), URLs.canonicalize(entry.getValue()));

    assertNull(URLs.canonicalize(null));
  }

  @Test
  public void testJarURL() throws Exception {
    assertNull(URLs.getJarURL(new URL("http://www.google.com/webhp")));
    assertEquals(new URL("file:///C:/proj/parser/jar/parser.jar"), URLs.getJarURL(new URL("jar:file:///C:/proj/parser/jar/parser.jar!/test.xml")));
    assertEquals(new URL("file:/root/app.jar"), URLs.getJarURL(new URL("jar:file:/root/app.jar!/repository")));
    assertEquals(new URL("http://www.foo.com/bar/baz.jar"), URLs.getJarURL(new URL("jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class")));
  }

  @Test
  public void testGetName() throws Exception {
    assertNull(URLs.canonicalize(null));
    assertEquals("share.txt", URLs.getName(new URL("file:///usr/share/../share.txt")));
    assertEquals("lib", URLs.getName(new URL("file:///usr/share/../share/../lib")));
    assertEquals("var", URLs.getName(new URL("file:///usr/share/../share/../lib/../../var")));
    assertEquals("resolv.conf", URLs.getName(new URL("file:///etc/resolv.conf")));
  }

  @Test
  public void testGetShortName() throws Exception {
    assertNull(URLs.canonicalize(null));
    assertEquals("share", URLs.getShortName(new URL("file:///usr/share/../share")));
    assertEquals("lib", URLs.getShortName(new URL("file:///usr/share/../share/../lib")));
    assertEquals("var", URLs.getShortName(new URL("file:///usr/share/../share/../lib/../../var")));
    assertEquals("resolv", URLs.getShortName(new URL("file:///etc/resolv.conf")));
  }

  @Test
  public void testGetParent() throws Exception {
    assertNull(URLs.getCanonicalParent(null));
    assertNull(URLs.getParent(new URL("file:///")));
    assertEquals(new URL("file:///"), URLs.getParent(new URL("file:///usr/")));
    assertEquals(new URL("file:///usr/share/../"), URLs.getParent(new URL("file:///usr/share/../share")));
    assertEquals(new URL("file:///usr/local/bin/../lib/../"), URLs.getParent(new URL("file:///usr/local/bin/../lib/../bin")));
  }

  @Test
  public void testGetCanonicalParent() throws Exception {
    assertNull(URLs.getCanonicalParent(null));
    assertEquals(new URL("file:///usr"), URLs.getCanonicalParent(new URL("file:///usr/share/../share")));
    assertEquals(new URL("file:///usr/local"), URLs.getCanonicalParent(new URL("file:///usr/local/bin/../lib/../bin")));
  }

  @Test
  public void testUrlDecode() {
    assertEquals("+ ", URLs.decode("%2B+"));
  }

  @Test
  public void testUrlEncode() {
    assertEquals("%2B+", URLs.encode("+ "));
  }

  @Test
  public void testEncodePath() {
    // rfc3986.txt 3.3
    // segment-nz = 1*pchar
    // pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
    // sub-delims = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
    // unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"

    // '&' has to be represented as &amp; in WADL

    final String pathChars = ":@!$&'()*+,;=-._~";
    final String str = URLs.encodePath(pathChars);
    assertEquals(str, pathChars);
  }

  @Test
  public void testPathEncodeWithPlusAndSpace() {
    assertEquals("+%20", URLs.encodePath("+ "));
  }

  @Test
  public void testURLEncode() {
    assertEquals("%2B+", URLs.encode("+ "));
  }

  @Test
  public void testUrlDecodeReserved() {
    assertEquals("!$&'()*,;=", URLs.decode("!$&'()*,;="));
  }

  @Test
  public void testDecodePath() {
    assertEquals("+++", URLs.decodePath("+%2B+"));
  }
}