/* Copyright (c) 2016 lib4j
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

package org.lib4j.net;

import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class AuthScheme {
  @SafeVarargs
  public static AuthScheme parse(final String authorization, final Class<? extends AuthScheme> ... schemes) {
    for (final Class<? extends AuthScheme> scheme : schemes) {
      final AuthScheme instance = getInstance(scheme);
      if (instance.matches(authorization))
        return instance.decode(authorization);
    }

    return null;
  }

  private static final Map<Class<? extends AuthScheme>,AuthScheme> instances = new IdentityHashMap<Class<? extends AuthScheme>,AuthScheme>();

  private static AuthScheme getInstance(final Class<? extends AuthScheme> scheme) {
    AuthScheme instance = instances.get(scheme);
    if (instance != null)
      return instance;

    try {
      instances.put(scheme, instance = scheme.getDeclaredConstructor().newInstance());
      return instance;
    }
    catch (final IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public final boolean matches(final String authorization) {
    return authorization != null && authorization.startsWith(name() + " ");
  }

  public AuthScheme parse(final String authorization) {
    if (authorization == null)
      return null;

    if (!authorization.startsWith(name() + " "))
      throw new IllegalArgumentException("Authorization header is expected to be type '" + name() + "', but was found to be: '" + authorization + "'");

    return decode(authorization);
  }

  public abstract String name();
  protected abstract AuthScheme decode(final String authorization);
}