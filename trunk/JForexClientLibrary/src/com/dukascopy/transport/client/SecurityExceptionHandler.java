package com.dukascopy.transport.client;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract interface SecurityExceptionHandler
{
  public abstract boolean isIgnoreSecurityException(X509Certificate[] paramArrayOfX509Certificate, String paramString, CertificateException paramCertificateException);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.SecurityExceptionHandler
 * JD-Core Version:    0.6.0
 */