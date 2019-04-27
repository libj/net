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
 * A {@code FilterHttpServletRequest} contains some other
 * {@link HttpServletRequest}, which it uses as its basic source of data,
 * possibly transforming the data along the way or providing additional
 * functionality. The class {@code FilterHttpServletRequest} itself simply
 * overrides all methods of {@link HttpServletRequest} with versions that pass
 * all requests to the contained input stream. Subclasses of
 * {@code FilterHttpServletRequest} may further override some of these methods
 * and may also provide additional methods and fields.
 */
public class FilterHttpServletRequest implements HttpServletRequest {
  protected volatile HttpServletRequest request;

  /**
   * Creates a new {@code FilterHttpServletRequest} with the specified request.
   *
   * @param request The request.
   * @throws NullPointerException If {@code request} is null.
   */
  public FilterHttpServletRequest(final HttpServletRequest request) {
    this.request = Objects.requireNonNull(request);
  }

  /**
   * Creates a new {@code FilterHttpServletRequest} with a null request.
   */
  protected FilterHttpServletRequest() {
  }

  @Override
  public Object getAttribute(final String name) {
    return request.getAttribute(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return request.getAttributeNames();
  }

  @Override
  public String getCharacterEncoding() {
    return request.getCharacterEncoding();
  }

  @Override
  public void setCharacterEncoding(final String env) throws UnsupportedEncodingException {
    request.setCharacterEncoding(env);
  }

  @Override
  public int getContentLength() {
    return request.getContentLength();
  }

  @Override
  public long getContentLengthLong() {
    return request.getContentLengthLong();
  }

  @Override
  public String getContentType() {
    return request.getContentType();
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return request.getInputStream();
  }

  @Override
  public String getParameter(final String name) {
    return request.getParameter(name);
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return request.getParameterNames();
  }

  @Override
  public String[] getParameterValues(final String name) {
    return request.getParameterValues(name);
  }

  @Override
  public Map<String,String[]> getParameterMap() {
    return request.getParameterMap();
  }

  @Override
  public String getProtocol() {
    return request.getProtocol();
  }

  @Override
  public String getScheme() {
    return request.getScheme();
  }

  @Override
  public String getServerName() {
    return request.getServerName();
  }

  @Override
  public int getServerPort() {
    return request.getServerPort();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return request.getReader();
  }

  @Override
  public String getRemoteAddr() {
    return request.getRemoteAddr();
  }

  @Override
  public String getRemoteHost() {
    return request.getRemoteHost();
  }

  @Override
  public void setAttribute(final String name, final Object o) {
    request.setAttribute(name, o);
  }

  @Override
  public void removeAttribute(final String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Locale getLocale() {
    return request.getLocale();
  }

  @Override
  public Enumeration<Locale> getLocales() {
    return request.getLocales();
  }

  @Override
  public boolean isSecure() {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestDispatcher getRequestDispatcher(final String path) {
    return request.getRequestDispatcher(path);
  }

  @Override
  @Deprecated
  public String getRealPath(final String path) {
    return request.getRealPath(path);
  }

  @Override
  public int getRemotePort() {
    return request.getRemotePort();
  }

  @Override
  public String getLocalName() {
    return request.getLocalName();
  }

  @Override
  public String getLocalAddr() {
    return request.getLocalAddr();
  }

  @Override
  public int getLocalPort() {
    return request.getLocalPort();
  }

  @Override
  public ServletContext getServletContext() {
    return request.getServletContext();
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
    return request.getAsyncContext();
  }

  @Override
  public DispatcherType getDispatcherType() {
    return request.getDispatcherType();
  }

  @Override
  public String getAuthType() {
    return request.getAuthType();
  }

  @Override
  public Cookie[] getCookies() {
    return request.getCookies();
  }

  @Override
  public long getDateHeader(final String name) {
    return request.getDateHeader(name);
  }

  @Override
  public String getHeader(final String name) {
    return request.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(final String name) {
    return request.getHeaders(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return request.getHeaderNames();
  }

  @Override
  public int getIntHeader(final String name) {
    return request.getIntHeader(name);
  }

  @Override
  public String getMethod() {
    return request.getMethod();
  }

  @Override
  public String getPathInfo() {
    return request.getPathInfo();
  }

  @Override
  public String getPathTranslated() {
    return request.getPathTranslated();
  }

  @Override
  public String getContextPath() {
    return request.getContextPath();
  }

  @Override
  public String getQueryString() {
    return request.getQueryString();
  }

  @Override
  public String getRemoteUser() {
    return request.getRemoteUser();
  }

  @Override
  public boolean isUserInRole(final String role) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Principal getUserPrincipal() {
    return request.getUserPrincipal();
  }

  @Override
  public String getRequestedSessionId() {
    return request.getRequestedSessionId();
  }

  @Override
  public String getRequestURI() {
    return request.getRequestURI();
  }

  @Override
  public StringBuffer getRequestURL() {
    return request.getRequestURL();
  }

  @Override
  public String getServletPath() {
    return request.getServletPath();
  }

  @Override
  public HttpSession getSession(final boolean create) {
    return request.getSession();
  }

  @Override
  public HttpSession getSession() {
    return request.getSession();
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
    return request.getParts();
  }

  @Override
  public Part getPart(final String name) throws IOException, ServletException {
    return request.getPart(name);
  }

  @Override
  public <T extends HttpUpgradeHandler>T upgrade(final Class<T> handlerClass) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }
}