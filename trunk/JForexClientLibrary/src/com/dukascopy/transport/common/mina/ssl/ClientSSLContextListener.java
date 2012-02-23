package com.dukascopy.transport.common.mina.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract interface ClientSSLContextListener
{
  public abstract void securityException(X509Certificate[] paramArrayOfX509Certificate, String paramString, CertificateException paramCertificateException);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.ssl.ClientSSLContextListener
 * JD-Core Version:    0.6.0
 */