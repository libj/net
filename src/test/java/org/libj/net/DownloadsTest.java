/* Copyright (c) 2021 LibJ
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

public class DownloadsTest {
  @Test
  public void testCopyAttributes() throws IOException {
    final URL url = new URL("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
    final File file = Files.createTempFile(getClass().getName(), ".png").toFile();
    file.delete();
    Downloads.downloadFile(url, file, StandardCopyOption.COPY_ATTRIBUTES);
    final URLConnection connection = url.openConnection();
    assertEquals(connection.getLastModified(), file.lastModified());
    file.delete();
  }
}