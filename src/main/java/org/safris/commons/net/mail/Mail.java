/* Copyright (c) 2009 lib4j
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

package org.safris.commons.net.mail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLSocketFactory;

import org.safris.commons.security.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Mail {
  private static final Logger logger = LoggerFactory.getLogger(Mail.class);

  public static enum Protocol {
    SMTP, SMTPS
  }

  public static class Message {
    private static InternetAddress[] toInternetAddress(final String ... emails) throws AddressException {
      if (emails == null)
        return null;

      final InternetAddress[] addresses = new InternetAddress[emails.length];
      for (int i = 0; i < emails.length; i++)
        addresses[i] = new InternetAddress(emails[i]);

      return addresses;
    }

    public final String subject;
    public final MimeContent content;
    public final InternetAddress from;
    public final InternetAddress[] to;
    public final InternetAddress[] cc;
    public final InternetAddress[] bcc;

    public Message(final String subject, final MimeContent content, final InternetAddress from, final String[] to, final String[] cc, final String[] bcc) throws AddressException {
      this(subject, content, from, toInternetAddress(to), toInternetAddress(cc), toInternetAddress(bcc));
    }

    public Message(final String subject, final MimeContent content, final InternetAddress from, final InternetAddress[] to, final InternetAddress[] cc, final InternetAddress[] bcc) {
      this.subject = subject;
      if (subject == null)
        throw new NullPointerException("subject == null");

      this.content = content;
      if (content == null)
        throw new NullPointerException("content == null");

      this.from = from;
      if (from == null)
        throw new NullPointerException("from == null");

      this.to = to;
      this.cc = cc;
      this.bcc = bcc;
      if ((to == null || to.length == 0) && (cc == null || cc.length == 0) && (bcc == null || bcc.length == 0))
        throw new IllegalArgumentException("(to == null || to.length == 0) && (cc == null || cc.length == 0) && (bcc == null || bcc.length == 0)");
    }

    public Message(final String subject, final MimeContent content, final InternetAddress from, final String ... to) throws AddressException {
      this(subject, content, from, to, null, null);
    }

    public void success() {
    }

    public void failure(final MessagingException e) {
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this)
        return true;

      if (!(obj instanceof Message))
        return false;

      final Message that = (Message)obj;
      return subject.equals(that.subject) && content.equals(that.content) && from.equals(that.from) && Arrays.equals(to, that.to) && Arrays.equals(cc, that.cc) && Arrays.equals(bcc, that.bcc);
    }

    @Override
    public int hashCode() {
      int hashCode = 0;
      hashCode += 2 * subject.hashCode();
      hashCode += 3 * content.hashCode();
      hashCode += 5 * from.hashCode();
      hashCode += 7 * (to != null ? to.hashCode() : -1323);
      hashCode += 11 * (cc != null ? cc.hashCode() : -1837);
      hashCode += 13 * (bcc != null ? bcc.hashCode() : -1121);
      return hashCode;
    }
  }

  public static final class Sender {
    private static final boolean debug = true;

    static {
      // System.setProperty("javax.net.debug", "ssl,handshake");
    }

    private static final Map<Sender,Sender> instances = new HashMap<Sender,Sender>();

    public static Sender instance(final Protocol protocol, final String host, final int port) {
      final Sender key = new Sender(protocol, host, port);
      Sender instance = instances.get(key);
      if (instance != null)
        return instance;

      synchronized (instances) {
        if ((instance = instances.get(key)) != null)
          return instance;

        instances.put(key, instance = key);
        return instance;
      }
    }

    private final Protocol protocol;
    private final String host;
    private final int port;
    private final Properties defaultProperties;

    private Sender(final Protocol protocol, final String host, final int port) {
      this.protocol = protocol;
      if (protocol == null)
        throw new NullPointerException("protocol == null");

      this.host = host;
      if (host == null)
        throw new NullPointerException("host == null");

      this.port = port;
      if (port < 1 || 65535 < port)
        throw new IllegalArgumentException("port [" + port + "] <> (1, 65535)");

      final String protocolString = this.protocol.toString().toLowerCase();

      this.defaultProperties = new Properties();
      defaultProperties.put("mail.debug", "true");
      defaultProperties.put("mail.transport.protocol", protocolString);

      defaultProperties.put("mail." + protocolString + ".debug", Boolean.toString(debug));

      defaultProperties.put("mail." + protocolString + ".host", host);
      defaultProperties.put("mail." + protocolString + ".port", port);

      defaultProperties.put("mail." + protocolString + ".quitwait", "false");

      defaultProperties.put("mail." + protocolString + ".ssl.trust", "*");
      defaultProperties.put("mail." + protocolString + ".starttls.enable", "true");

      if (this.protocol == Protocol.SMTPS) {
        defaultProperties.put("mail." + protocolString + ".ssl.enable", "true");
        defaultProperties.put("mail." + protocolString + ".ssl.protocols", "SSLv3 TLSv1");
        defaultProperties.put("mail." + protocolString + ".socketFactory.class", SSLSocketFactory.class.getName());
        defaultProperties.put("mail." + protocolString + ".socketFactory.port", port);
        defaultProperties.put("mail." + protocolString + ".socketFactory.fallback", "false");
      }
    }

    public void send(final Credentials credentials, final String subject, final MimeContent content, final InternetAddress from, final String ... to) throws MessagingException {
      send(credentials, new Message(subject, content, from, to, null, null));
    }

    public void send(final Credentials credentials, final String subject, final MimeContent content, final InternetAddress from, final String[] to, final String[] cc, final String[] bcc) throws MessagingException {
      send(credentials, new Message(subject, content, from, to, cc, bcc));
    }

    public void send(final Credentials credentials, final Message ... messages) throws MessagingException {
      final String protocolString = protocol.toString().toLowerCase();
      final Properties properties = new Properties(defaultProperties);
      final Session session;
      if (credentials != null) {
        properties.put("mail." + protocolString + ".auth", "true");
        // the following 2 lines were causing "Relaying denied. Proper authentication required." messages from sendmail
        // properties.put("mail." + protocolString + ".ehlo", "false");
        // properties.put("mail." + protocolString + ".user", credentials.username);

        session = Session.getInstance(properties, new Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(credentials.username, credentials.password);
          }
        });
      }
      else {
        session = Session.getInstance(properties);
      }

      session.setDebug(debug);
      final Transport transport = session.getTransport(protocolString);
      try {
        transport.connect(host, port, credentials.username, credentials.password);
        for (final Message message : messages) {
          logger.info("Email:\n  to: " + Arrays.toString(message.to) + "\n  cc: " + Arrays.toString(message.to) + "\n  bcc: " + Arrays.toString(message.bcc));
          session.getProperties().setProperty("mail." + protocolString + ".from", message.from.getAddress());
          final MimeMessage mimeMessage = new MimeMessage(session);

          try {
            mimeMessage.setFrom(message.from);

            if (message.to != null)
              mimeMessage.setRecipients(MimeMessage.RecipientType.TO, message.to);

            if (message.cc != null)
              mimeMessage.setRecipients(MimeMessage.RecipientType.CC, message.cc);

            if (message.bcc != null)
              mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, message.bcc);

            // Setting the Subject and Content Type
            mimeMessage.setSubject(message.subject);
            mimeMessage.setContent(message.content.getContent(), message.content.getType());

            mimeMessage.saveChanges();
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            message.success();
          }
          catch (final MessagingException e) {
            logger.error(Mail.class.getName() + ":send()", e);
            message.failure(e);
          }
        }
      }
      finally {
        transport.close();
      }
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this)
        return true;

      if (!(obj instanceof Sender))
        return false;

      final Sender that = (Sender)obj;
      return host.equals(that.host) && protocol == that.protocol && port == that.port;
    }

    @Override
    public int hashCode() {
      int hashCode = 0;
      hashCode += 2 * host.hashCode();
      hashCode *= protocol.ordinal() + 1;
      hashCode += 3 * port;
      return hashCode;
    }
  }
}