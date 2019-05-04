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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.List;
import java.util.Map;

/**
 * A {@code FilterURLConnection} contains some other {@link URLConnection},
 * which it uses as its basic source of data, possibly transforming the data
 * along the way or providing additional functionality. The class
 * {@code FilterURLConnection} itself simply overrides all methods of
 * {@link URLConnection} with versions that pass all requests to the contained
 * input stream. Subclasses of {@code FilterURLConnection} may further override
 * some of these methods and may also provide additional methods and fields.
 */
public class FilterURLConnection extends URLConnection {
  protected volatile URLConnection connection;

  /**
   * Creates a new {@code FilterURLConnection} with the specified connection.
   *
   * @param connection The connection.
   * @throws NullPointerException If {@code connection} is null.
   */
  public FilterURLConnection(final URLConnection connection) {
    super(connection.getURL());
    this.connection = connection;
  }

  /**
   * Creates a new {@code FilterHttpServletRequest} with a null connection.
   */
  protected FilterURLConnection() {
    super(null);
  }

  @Override
  public void connect() throws IOException {
    connection.connect();
  }

  @Override
  public void setConnectTimeout(final int timeout) {
    connection.setConnectTimeout(timeout);
  }

  @Override
  public int getConnectTimeout() {
    return connection.getConnectTimeout();
  }

  @Override
  public void setReadTimeout(final int timeout) {
    connection.setReadTimeout(timeout);
  }

  @Override
  public int getReadTimeout() {
    return connection.getReadTimeout();
  }

  @Override
  public URL getURL() {
    return connection.getURL();
  }

  @Override
  public int getContentLength() {
    return connection.getContentLength();
  }

  @Override
  public long getContentLengthLong() {
    return connection.getContentLengthLong();
  }

  @Override
  public String getContentType() {
    return connection.getContentType();
  }

  @Override
  public String getContentEncoding() {
    return connection.getContentEncoding();
  }

  @Override
  public long getExpiration() {
    return connection.getExpiration();
  }

  @Override
  public long getDate() {
    return connection.getDate();
  }

  @Override
  public long getLastModified() {
    return connection.getLastModified();
  }

  @Override
  public String getHeaderField(final String name) {
    return connection.getHeaderField(name);
  }

  @Override
  public Map<String,List<String>> getHeaderFields() {
    return connection.getHeaderFields();
  }

  @Override
  public int getHeaderFieldInt(final String name, final int Default) {
    return connection.getHeaderFieldInt(name, Default);
  }

  @Override
  public long getHeaderFieldLong(final String name, final long Default) {
    return connection.getHeaderFieldLong(name, Default);
  }

  @Override
  public long getHeaderFieldDate(final String name, final long Default) {
    return connection.getHeaderFieldDate(name, Default);
  }

  @Override
  public String getHeaderFieldKey(final int n) {
    return connection.getHeaderFieldKey(n);
  }

  @Override
  public String getHeaderField(final int n) {
    return connection.getHeaderField(n);
  }

  @Override
  public Object getContent() throws IOException {
    return connection.getContent();
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object getContent(final Class[] classes) throws IOException {
    return connection.getContent(classes);
  }

  @Override
  public Permission getPermission() throws IOException {
    return connection.getPermission();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return connection.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return connection.getOutputStream();
  }

  @Override
  public void setDoInput(final boolean doinput) {
    connection.setDoInput(doinput);
  }

  @Override
  public boolean getDoInput() {
    return connection.getDoInput();
  }

  @Override
  public void setDoOutput(final boolean dooutput) {
    connection.setDoOutput(dooutput);
  }

  @Override
  public boolean getDoOutput() {
    return connection.getDoOutput();
  }

  @Override
  public void setAllowUserInteraction(final boolean allowuserinteraction) {
    connection.setAllowUserInteraction(allowuserinteraction);
  }

  @Override
  public boolean getAllowUserInteraction() {
    return connection.getAllowUserInteraction();
  }

  @Override
  public void setUseCaches(final boolean usecaches) {
    connection.setUseCaches(usecaches);
  }

  @Override
  public boolean getUseCaches() {
    return connection.getUseCaches();
  }

  @Override
  public void setIfModifiedSince(final long ifmodifiedsince) {
    connection.setIfModifiedSince(ifmodifiedsince);
  }

  @Override
  public long getIfModifiedSince() {
    return connection.getIfModifiedSince();
  }

  @Override
  public boolean getDefaultUseCaches() {
    return connection.getDefaultUseCaches();
  }

  @Override
  public void setDefaultUseCaches(final boolean defaultusecaches) {
    connection.setDefaultUseCaches(defaultusecaches);
  }

  @Override
  public void setRequestProperty(final String key, final String value) {
    connection.setRequestProperty(key, value);
  }

  @Override
  public void addRequestProperty(final String key, final String value) {
    connection.addRequestProperty(key, value);
  }

  @Override
  public String getRequestProperty(final String key) {
    return connection.getRequestProperty(key);
  }

  @Override
  public Map<String,List<String>> getRequestProperties() {
    return connection.getRequestProperties();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof FilterURLConnection))
      return false;

    final FilterURLConnection that = (FilterURLConnection)obj;
    return connection != null ? connection.equals(that.connection) : that.connection == null;
  }

  @Override
  public int hashCode() {
    return connection.hashCode();
  }

  @Override
  public String toString() {
    return connection.toString();
  }
}