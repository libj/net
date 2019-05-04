/* Copyright (c) 2006 LibJ
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

import java.net.URI;

public final class URIs {
  public static URI relativize(final URI base, final URI uri) {
    // quick bail-out
    if (!base.isAbsolute() || !uri.isAbsolute())
      return uri;

    if (base.isOpaque() || uri.isOpaque()) {
      // Unlikely case of an URN which can't deal with
      // relative path, such as urn:isbn:0451450523
      return uri;
    }

    // Check for common root
    final URI root = base.resolve("/");
    // Different protocol/auth/host/port, return as is
    if (!root.equals(uri.resolve("/")))
      return uri;

    // Ignore hostname bits for the following , but add "/" in the beginning
    // so that in worst case we'll still return "/fred" rather than
    // "http://example.com/fred".
    final URI baseRel = URI.create("/").resolve(root.relativize(base));
    final URI uriRel = URI.create("/").resolve(root.relativize(uri));

    // Is it same path?
    if (baseRel.getPath().equals(uriRel.getPath()))
      return baseRel.relativize(uriRel);

    // Direct siblings? (ie. in same folder)
    URI commonBase = baseRel.resolve("./");
    if (commonBase.equals(uriRel.resolve("./")))
      return commonBase.relativize(uriRel);

    // No, then just keep climbing up until we find a common base.
    URI relative = URI.create("");
    while (!uriRel.getPath().startsWith(commonBase.getPath()) && !commonBase.getPath().equals("/")) {
      commonBase = commonBase.resolve("../");
      relative = relative.resolve("../");
    }

    // Now we can use URI.relativize
    final URI relToCommon = commonBase.relativize(uriRel);
    // and prepend the needed ../
    return relative.resolve(relToCommon);
  }

  private URIs() {
  }
}