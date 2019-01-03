/* Copyright (c) 2016 OpenJAX
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

package org.openjax.standard.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code AuthScheme} class represents a strong type representation of the
 * "Authorization" header schemes. This class allows an "Authorization" scheme
 * to be parsed to an instance of {@code AuthScheme}.
 *
 * @see Basic
 * @see Bearer
 */
public abstract class AuthScheme {
  private static final Logger logger = LoggerFactory.getLogger(AuthScheme.class);

  /**
   * Returns an instance of a {@code AuthScheme} subclass specified in
   * {@code schemes} that matches the spec of the {@code authorization} header
   * string.
   *
   * @param authorization The "Authorization" header string to match.
   * @param schemes The array of {@code AuthScheme} classes to attempt to match.
   * @return An instance of a {@code AuthScheme} subclass specified in
   *         {@code schemes} that matches the spec of the {@code authorization}
   *         header string.
   * @throws UnsupportedOperationException If a {@code AuthScheme} class in
   *           {@code schemes} does not implement a protected default
   *           constructor, or if the constructor throws an exception when
   *           invoked.
   */
  @SafeVarargs
  public static AuthScheme parse(final String authorization, final Class<? extends AuthScheme> ... schemes) {
    for (final Class<? extends AuthScheme> scheme : schemes) {
      final AuthScheme instance = getInstance(scheme);
      if (instance.matches(authorization)) {
        try {
          return instance.decode(authorization);
        }
        catch (final Exception e) {
          logger.debug(e.getMessage(), e);
        }
      }
    }

    return null;
  }

  private static final Map<Class<? extends AuthScheme>,AuthScheme> instances = new IdentityHashMap<>();

  private static AuthScheme getInstance(final Class<? extends AuthScheme> scheme) {
    AuthScheme instance = instances.get(scheme);
    if (instance != null)
      return instance;

    try {
      final Constructor<? extends AuthScheme> constructor = scheme.getDeclaredConstructor();
      constructor.setAccessible(true);
      instances.put(scheme, instance = constructor.newInstance());
      return instance;
    }
    catch (final IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  /**
   * Tests whether the {@code authorization} header string matches this
   * {@code AuthScheme} subclass's implementation.
   *
   * @param authorization The "Authorization" header string.
   * @return {@code true} if the {@code authorization} header string matches
   *         this {@code AuthScheme} subclass's implementation; otherwise
   *         {@code false}.
   */
  public final boolean matches(final String authorization) {
    return authorization != null && authorization.startsWith(name() + " ");
  }

  /**
   * Returns a {@code AuthScheme} instance by parsing the {@code authorization}
   * header string.
   *
   * @param authorization The "Authorization" header string.
   * @return A {@code AuthScheme} instance by parsing the {@code authorization}
   *         header string.
   */
  public final AuthScheme parse(final String authorization) {
    if (authorization == null)
      return null;

    if (!authorization.startsWith(name() + " "))
      throw new IllegalArgumentException("Authorization header is expected to be type '" + name() + "', but was found to be: '" + authorization + "'");

    return decode(authorization);
  }

  /**
   * Returns the name of this "Authorization" scheme. For example, "Basic" and
   * "Bearer" are common "Authorization" header schemes.
   *
   * @return The name of this "Authorization" scheme.
   */
  public abstract String name();

  /**
   * Returns a {@code AuthScheme} instance by decoding the {@code authorization}
   * header string. This method is required to be overridden by subclasses
   * implementing an "Authorization" scheme.
   *
   * @param authorization The "Authorization" header string.
   * @return A {@code AuthScheme} instance by decoding the {@code authorization}
   *         header string.
   */
  protected abstract AuthScheme decode(final String authorization);
}