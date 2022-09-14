/* Copyright (c) 2018 LibJ
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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.libj.util.MultiHashMap;

public class URIsTest {
  @Test
  public void testRelativePath() throws URISyntaxException {
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6226081
    final URI a = new URI("file:/c:/abc/def/myDocument/doc.xml");
    final URI b = new URI("file:/c:/abc/def/images/subdir/image.png");

    final URI c = URIs.relativePath(a, b);

    assertEquals("../images/subdir/image.png", c.toString());
  }

  @Test
  public void testIsLocal() throws Exception {
    assertTrue(URIs.isLocal(new URI("jar:file:///C:/proj/parser/jar/parser.jar!/test.xml")));
    assertTrue(URIs.isLocal(new URI("file:///c:/path/to/the%20file.txt")));
    assertTrue(URIs.isLocal(new URI("file:///tmp.txt")));
    assertTrue(URIs.isLocal(new URI("jar:file:/root/app.jar!/repository")));
    assertTrue(URIs.isLocal(new URI("file://localhost/etc/fstab")));
    assertTrue(URIs.isLocal(new URI("file://localhost/c:/WINDOWS/clock.avi")));
    assertFalse(URIs.isLocal(new URI("http://127.0.0.1:8080/a.properties")));
    assertFalse(URIs.isLocal(new URI("file://hostname/path/to/the%20file.txt")));
    assertFalse(URIs.isLocal(new URI("ftp://user:password@server:80/path")));
    assertFalse(URIs.isLocal(new URI("https://mail.google.com/mail/u/0/?zx=gc46uk9snw66#inbox")));
    assertFalse(URIs.isLocal(new URI("jar:http://www.foo.com/bar/baz.jar!/COM/foo/Quux.class")));
  }

  @Test
  public void testGetName() throws Exception {
    assertEquals("share.txt", URIs.getName(new URI("file:///usr/share/../share.txt")));
    assertEquals("lib", URIs.getName(new URI("file:///usr/share/../share/../lib")));
    assertEquals("var", URIs.getName(new URI("file:///usr/share/../share/../lib/../../var")));
    assertEquals("resolv.conf", URIs.getName(new URI("file:///etc/resolv.conf")));
  }

  @Test
  public void testGetSimpleName() throws Exception {
    assertEquals("share", URIs.getSimpleName(new URI("file:///usr/share/../share")));
    assertEquals("lib", URIs.getSimpleName(new URI("file:///usr/share/../share/../lib")));
    assertEquals("var", URIs.getSimpleName(new URI("file:///usr/share/../share/../lib/../../var")));
    assertEquals("resolv", URIs.getSimpleName(new URI("file:///etc/resolv.conf")));
  }

  @Test
  public void testGetParent() throws Exception {
    assertNull(URIs.getCanonicalParent(null));
    assertEquals(new URI("file:///"), URIs.getParent(new URI("file:///usr/")));
    assertEquals(new URI("file:///usr/share/../"), URIs.getParent(new URI("file:///usr/share/../share")));
    assertEquals(new URI("file:///usr/local/bin/../lib/../"), URIs.getParent(new URI("file:///usr/local/bin/../lib/../bin")));
  }

  @Test
  public void testGetCanonicalParent() throws Exception {
    assertNull(URIs.getCanonicalParent(null));
    assertEquals(new URI("file:///usr/"), URIs.getCanonicalParent(new URI("file:///usr/share/../share")));
    assertEquals(new URI("file:///usr/local/"), URIs.getCanonicalParent(new URI("file:///usr/local/bin/../lib/../bin")));
  }

  private static void assertMap(final Map<String,List<String>> actual, final String ... members) {
    final MultiHashMap<String,String,List<String>> expected = new MultiHashMap<>(ArrayList::new);
    for (int i = 0; i < members.length;)
      expected.add(members[i++], members[i++]);

    assertEquals(expected, actual);
  }

  @Test
  public void testDecodeParameters() throws Exception {
    try {
      URIs.decodeParameters(null, "UTF-8");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      URIs.decodeParameters("a=b", null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      URIs.decodeParameters("a=b", "bla");
      fail("Expected IllegalArgumentException");
    }
    catch (final UnsupportedEncodingException e) {
    }

    assertMap(URIs.decodeParameters("foo=bar", "UTF-8"), "foo", "bar");
    assertMap(URIs.decodeParameters("foo=bar&foo=bar", "UTF-8"), "foo", "bar", "foo", "bar");
    assertMap(URIs.decodeParameters("a=b&c=d", "UTF-8"), "a", "b", "c", "d");
    assertMap(URIs.decodeParameters("a%20a=b%20b&c%20c=d%20d", "UTF-8"), "a a", "b b", "c c", "d d");
  }
}