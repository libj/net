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

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class ClasspathURLStreamHandlerTest {
  static {
    URLStreamHandlers.loadSPI();
  }

  @Test
  public void test() throws MalformedURLException {
    final String resourcePath = ClasspathURLStreamHandlerTest.class.getName().replace('.', '/').concat(".class");
    assertNotNull(ClasspathURLStreamHandler.getResource(new URL("classpath:" + resourcePath)));

    try {
      ClasspathURLStreamHandler.createURL(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    final URL classpathUrl = ClasspathURLStreamHandler.createURL(resourcePath);
    assertNotNull(ClasspathURLStreamHandler.getResource(classpathUrl));
  }
}