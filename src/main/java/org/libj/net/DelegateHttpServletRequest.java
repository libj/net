/* Copyright (c) 2016 LibJ
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * A {@link DelegateHttpServletRequest} contains some other
 * {@link HttpServletRequest}, which it uses as its basic source of data,
 * possibly transforming the data along the way or providing additional
 * functionality. The class {@link DelegateHttpServletRequest} itself simply
 * overrides all methods of {@link HttpServletRequest} with versions that pass
 * all requests to the contained input stream. Subclasses of
 * {@link DelegateHttpServletRequest} may further override some of these methods
 * and may also provide additional methods and fields.
 */
public abstract class DelegateHttpServletRequest implements HttpServletRequest {
  /** The target {@link HttpServletRequest}. */
  protected volatile HttpServletRequest target;

  /**
   * Creates a new {@link DelegateHttpServletRequest} with the specified target
   * {@link HttpServletRequest}.
   *
   * @param target The target {@link HttpServletRequest}.
   * @throws NullPointerException If the specified {@link HttpServletRequest} is
   *           null.
   */
  public DelegateHttpServletRequest(final HttpServletRequest target) {
    this.target = Objects.requireNonNull(target);
  }

  /**
   * Creates a new {@link DelegateHttpServletRequest} with a null target.
   */
  protected DelegateHttpServletRequest() {
  }

  @Override
  public Object getAttribute(final String name) {
    return target.getAttribute(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return target.getAttributeNames();
  }

  @Override
  public String getCharacterEncoding() {
    return target.getCharacterEncoding();
  }

  @Override
  public void setCharacterEncoding(final String env) throws UnsupportedEncodingException {
    target.setCharacterEncoding(env);
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
  public ServletInputStream getInputStream() throws IOException {
    return target.getInputStream();
  }

  @Override
  public String getParameter(final String name) {
    return target.getParameter(name);
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return target.getParameterNames();
  }

  @Override
  public String[] getParameterValues(final String name) {
    return target.getParameterValues(name);
  }

  @Override
  public Map<String,String[]> getParameterMap() {
    return target.getParameterMap();
  }

  @Override
  public String getProtocol() {
    return target.getProtocol();
  }

  @Override
  public String getScheme() {
    return target.getScheme();
  }

  @Override
  public String getServerName() {
    return target.getServerName();
  }

  @Override
  public int getServerPort() {
    return target.getServerPort();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return target.getReader();
  }

  @Override
  public String getRemoteAddr() {
    return target.getRemoteAddr();
  }

  @Override
  public String getRemoteHost() {
    return target.getRemoteHost();
  }

  @Override
  public void setAttribute(final String name, final Object o) {
    target.setAttribute(name, o);
  }

  @Override
  public void removeAttribute(final String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Locale getLocale() {
    return target.getLocale();
  }

  @Override
  public Enumeration<Locale> getLocales() {
    return target.getLocales();
  }

  @Override
  public boolean isSecure() {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestDispatcher getRequestDispatcher(final String path) {
    return target.getRequestDispatcher(path);
  }

  @Override
  @Deprecated
  public String getRealPath(final String path) {
    return target.getRealPath(path);
  }

  @Override
  public int getRemotePort() {
    return target.getRemotePort();
  }

  @Override
  public String getLocalName() {
    return target.getLocalName();
  }

  @Override
  public String getLocalAddr() {
    return target.getLocalAddr();
  }

  @Override
  public int getLocalPort() {
    return target.getLocalPort();
  }

  @Override
  public ServletContext getServletContext() {
    return target.getServletContext();
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAsyncStarted() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAsyncSupported() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsyncContext getAsyncContext() {
    return target.getAsyncContext();
  }

  @Override
  public DispatcherType getDispatcherType() {
    return target.getDispatcherType();
  }

  @Override
  public String getAuthType() {
    return target.getAuthType();
  }

  @Override
  public Cookie[] getCookies() {
    return target.getCookies();
  }

  @Override
  public long getDateHeader(final String name) {
    return target.getDateHeader(name);
  }

  @Override
  public String getHeader(final String name) {
    return target.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(final String name) {
    return target.getHeaders(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return target.getHeaderNames();
  }

  @Override
  public int getIntHeader(final String name) {
    return target.getIntHeader(name);
  }

  @Override
  public String getMethod() {
    return target.getMethod();
  }

  @Override
  public String getPathInfo() {
    return target.getPathInfo();
  }

  @Override
  public String getPathTranslated() {
    return target.getPathTranslated();
  }

  @Override
  public String getContextPath() {
    return target.getContextPath();
  }

  @Override
  public String getQueryString() {
    return target.getQueryString();
  }

  @Override
  public String getRemoteUser() {
    return target.getRemoteUser();
  }

  @Override
  public boolean isUserInRole(final String role) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Principal getUserPrincipal() {
    return target.getUserPrincipal();
  }

  @Override
  public String getRequestedSessionId() {
    return target.getRequestedSessionId();
  }

  @Override
  public String getRequestURI() {
    return target.getRequestURI();
  }

  @Override
  public StringBuffer getRequestURL() {
    return target.getRequestURL();
  }

  @Override
  public String getServletPath() {
    return target.getServletPath();
  }

  @Override
  public HttpSession getSession(final boolean create) {
    return target.getSession();
  }

  @Override
  public HttpSession getSession() {
    return target.getSession();
  }

  @Override
  public String changeSessionId() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdValid() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdFromCookie() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdFromURL() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isRequestedSessionIdFromUrl() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean authenticate(final HttpServletResponse response) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void login(final String username, final String password) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void logout() throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    return target.getParts();
  }

  @Override
  public Part getPart(final String name) throws IOException, ServletException {
    return target.getPart(name);
  }

  @Override
  public <T extends HttpUpgradeHandler>T upgrade(final Class<T> handlerClass) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof DelegateHttpServletRequest))
      return false;

    final DelegateHttpServletRequest that = (DelegateHttpServletRequest)obj;
    return target != null ? target.equals(that.target) : that.target == null;
  }

  @Override
  public int hashCode() {
    return target == null ? 733 : target.hashCode();
  }

  @Override
  public String toString() {
    return String.valueOf(target);
  }
}