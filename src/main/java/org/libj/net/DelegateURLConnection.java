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

import static org.libj.lang.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link DelegateURLConnection} contains some other {@link URLConnection}, which it uses as its basic source of data, possibly
 * transforming the data along the way or providing additional functionality. The class {@link DelegateURLConnection} itself simply
 * overrides all methods of {@link URLConnection} with versions that pass all requests to the contained input stream. Subclasses of
 * {@link DelegateURLConnection} may further override some of these methods and may also provide additional methods and fields.
 */
public abstract class DelegateURLConnection extends URLConnection {
  /** The target {@link URLConnection}. */
  protected volatile URLConnection target;

  /**
   * Creates a new {@link DelegateURLConnection} with the specified target {@link URLConnection}.
   *
   * @param target The target {@link URLConnection}.
   * @throws IllegalArgumentException If the target {@link URLConnection} is null.
   */
  public DelegateURLConnection(final URLConnection target) {
    super(assertNotNull(target).getURL());
    this.target = target;
  }

  /**
   * Creates a new {@link DelegateURLConnection} with a null target.
   */
  protected DelegateURLConnection() {
    super(null);
  }

  @Override
  public void connect() throws IOException {
    target.connect();
  }

  @Override
  public void setConnectTimeout(final int timeout) {
    target.setConnectTimeout(timeout);
  }

  @Override
  public int getConnectTimeout() {
    return target.getConnectTimeout();
  }

  @Override
  public void setReadTimeout(final int timeout) {
    target.setReadTimeout(timeout);
  }

  @Override
  public int getReadTimeout() {
    return target.getReadTimeout();
  }

  @Override
  public URL getURL() {
    return target.getURL();
  }

  @Override
  public int getContentLength() {
    return target.getContentLength();
  }

  @Override
  public long getContentLengthLong() {
    return target.getContentLengthLong();
  }

  @Override
  public String getContentType() {
    return target.getContentType();
  }

  @Override
  public String getContentEncoding() {
    return target.getContentEncoding();
  }

  @Override
  public long getExpiration() {
    return target.getExpiration();
  }

  @Override
  public long getDate() {
    return target.getDate();
  }

  @Override
  public long getLastModified() {
    return target.getLastModified();
  }

  @Override
  public String getHeaderField(final String name) {
    return target.getHeaderField(name);
  }

  @Override
  public Map<String,List<String>> getHeaderFields() {
    return target.getHeaderFields();
  }

  @Override
  public int getHeaderFieldInt(final String name, final int Default) {
    return target.getHeaderFieldInt(name, Default);
  }

  @Override
  public long getHeaderFieldLong(final String name, final long Default) {
    return target.getHeaderFieldLong(name, Default);
  }

  @Override
  public long getHeaderFieldDate(final String name, final long Default) {
    return target.getHeaderFieldDate(name, Default);
  }

  @Override
  public String getHeaderFieldKey(final int n) {
    return target.getHeaderFieldKey(n);
  }

  @Override
  public String getHeaderField(final int n) {
    return target.getHeaderField(n);
  }

  @Override
  public Object getContent() throws IOException {
    return target.getContent();
  }

  @Override
  public Object getContent(final Class<?>[] classes) throws IOException {
    return target.getContent(classes);
  }

  @Override
  public Permission getPermission() throws IOException {
    return target.getPermission();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return target.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return target.getOutputStream();
  }

  @Override
  public void setDoInput(final boolean doInput) {
    target.setDoInput(doInput);
  }

  @Override
  public boolean getDoInput() {
    return target.getDoInput();
  }

  @Override
  public void setDoOutput(final boolean doOutput) {
    target.setDoOutput(doOutput);
  }

  @Override
  public boolean getDoOutput() {
    return target.getDoOutput();
  }

  @Override
  public void setAllowUserInteraction(final boolean allowUserInteraction) {
    target.setAllowUserInteraction(allowUserInteraction);
  }

  @Override
  public boolean getAllowUserInteraction() {
    return target.getAllowUserInteraction();
  }

  @Override
  public void setUseCaches(final boolean useCaches) {
    target.setUseCaches(useCaches);
  }

  @Override
  public boolean getUseCaches() {
    return target.getUseCaches();
  }

  @Override
  public void setIfModifiedSince(final long ifModifiedSince) {
    target.setIfModifiedSince(ifModifiedSince);
  }

  @Override
  public long getIfModifiedSince() {
    return target.getIfModifiedSince();
  }

  @Override
  public boolean getDefaultUseCaches() {
    return target.getDefaultUseCaches();
  }

  @Override
  public void setDefaultUseCaches(final boolean defaultUseCaches) {
    target.setDefaultUseCaches(defaultUseCaches);
  }

  @Override
  public void setRequestProperty(final String key, final String value) {
    target.setRequestProperty(key, value);
  }

  @Override
  public void addRequestProperty(final String key, final String value) {
    target.addRequestProperty(key, value);
  }

  @Override
  public String getRequestProperty(final String key) {
    return target.getRequestProperty(key);
  }

  @Override
  public Map<String,List<String>> getRequestProperties() {
    return target.getRequestProperties();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof DelegateURLConnection))
      return false;

    final DelegateURLConnection that = (DelegateURLConnection)obj;
    return Objects.equals(target, that.target);
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    if (target != null)
      hashCode = 31 * hashCode + target.hashCode();

    return hashCode;
  }

  @Override
  public String toString() {
    return String.valueOf(target);
  }
}