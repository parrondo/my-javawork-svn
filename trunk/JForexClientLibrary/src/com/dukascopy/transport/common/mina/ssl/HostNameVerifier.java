/*     */ package com.dukascopy.transport.common.mina.ssl;
/*     */ 
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TreeSet;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.security.auth.x500.X500Principal;
/*     */ 
/*     */ public class HostNameVerifier
/*     */   implements HostnameVerifier
/*     */ {
/*  18 */   private static final String[] BAD_COUNTRY_2LDS = { "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org" };
/*     */ 
/*  20 */   private static final String[] LOCALHOSTS = { "::1", "127.0.0.1", "localhost", "localhost.localdomain" };
/*     */ 
/*     */   public boolean verify(String host, SSLSession session)
/*     */   {
/*     */     try
/*     */     {
/*  30 */       Certificate[] certs = session.getPeerCertificates();
/*  31 */       X509Certificate x509 = (X509Certificate)certs[0];
/*  32 */       check(new String[] { host }, x509);
/*  33 */       return true; } catch (SSLException e) {
/*     */     }
/*  35 */     return false;
/*     */   }
/*     */ 
/*     */   public void check(String[] host, X509Certificate cert) throws SSLException
/*     */   {
/*  40 */     String[] cns = getCNs(cert);
/*  41 */     String[] subjectAlts = getDNSSubjectAlts(cert);
/*  42 */     check(host, cns, subjectAlts);
/*     */   }
/*     */ 
/*     */   public String getCN(X509Certificate cert) {
/*  46 */     String[] cns = getCNs(cert);
/*  47 */     boolean foundSomeCNs = (cns != null) && (cns.length >= 1);
/*  48 */     return foundSomeCNs ? cns[0] : null;
/*     */   }
/*     */ 
/*     */   public void check(String[] hosts, String[] cns, String[] subjectAlts) throws SSLException
/*     */   {
/*  53 */     StringBuffer buf = new StringBuffer(32);
/*  54 */     buf.append('<');
/*  55 */     for (int i = 0; i < hosts.length; i++) {
/*  56 */       String h = hosts[i];
/*  57 */       h = h != null ? h.trim().toLowerCase() : "";
/*  58 */       hosts[i] = h;
/*  59 */       if (i > 0) {
/*  60 */         buf.append('/');
/*     */       }
/*  62 */       buf.append(h);
/*     */     }
/*  64 */     buf.append('>');
/*  65 */     String hostnames = buf.toString();
/*     */ 
/*  70 */     TreeSet names = new TreeSet();
/*  71 */     if ((cns != null) && (cns.length > 0) && (cns[0] != null)) {
/*  72 */       names.add(cns[0]);
/*     */     }
/*  74 */     if (subjectAlts != null) {
/*  75 */       for (int i = 0; i < subjectAlts.length; i++) {
/*  76 */         if (subjectAlts[i] != null) {
/*  77 */           names.add(subjectAlts[i]);
/*     */         }
/*     */       }
/*     */     }
/*  81 */     if (names.isEmpty()) {
/*  82 */       String msg = "Certificate for " + hosts[0] + " doesn't contain CN or DNS subjectAlt";
/*  83 */       throw new SSLException(msg);
/*     */     }
/*     */ 
/*  87 */     buf = new StringBuffer();
/*     */ 
/*  89 */     boolean match = false;
/*  90 */     for (Iterator it = names.iterator(); it.hasNext(); )
/*     */     {
/*  92 */       String cn = (String)it.next();
/*  93 */       cn = cn.toLowerCase();
/*     */ 
/*  95 */       buf.append(" <");
/*  96 */       buf.append(cn);
/*  97 */       buf.append('>');
/*  98 */       if (it.hasNext()) {
/*  99 */         buf.append(" OR");
/*     */       }
/*     */ 
/* 105 */       boolean doWildcard = (cn.startsWith("*.")) && (cn.lastIndexOf(46) >= 0) && (!isIP4Address(cn)) && (acceptableCountryWildcard(cn));
/*     */ 
/* 107 */       for (int i = 0; i < hosts.length; i++) {
/* 108 */         String hostName = hosts[i].trim().toLowerCase();
/* 109 */         if (doWildcard)
/* 110 */           match = hostName.endsWith(cn.substring(1));
/*     */         else {
/* 112 */           match = hostName.equals(cn);
/*     */         }
/* 114 */         if (match)
/*     */           break label421;
/*     */       }
/*     */     }
/* 119 */     label421: if (!match)
/* 120 */       throw new SSLException("hostname in certificate didn't match: " + hostnames + " !=" + buf);
/*     */   }
/*     */ 
/*     */   public static boolean acceptableCountryWildcard(String cn)
/*     */   {
/* 125 */     int cnLen = cn.length();
/* 126 */     if ((cnLen >= 7) && (cnLen <= 9))
/*     */     {
/* 128 */       if (cn.charAt(cnLen - 3) == '.')
/*     */       {
/* 130 */         String s = cn.substring(2, cnLen - 3);
/*     */ 
/* 132 */         int x = Arrays.binarySearch(BAD_COUNTRY_2LDS, s);
/* 133 */         return x < 0;
/*     */       }
/*     */     }
/* 136 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean isIP4Address(String cn) {
/* 140 */     boolean isIP4 = true;
/* 141 */     String tld = cn;
/* 142 */     int x = cn.lastIndexOf(46);
/*     */ 
/* 145 */     if ((x >= 0) && (x + 1 < cn.length())) {
/* 146 */       tld = cn.substring(x + 1);
/*     */     }
/* 148 */     for (int i = 0; i < tld.length(); i++) {
/* 149 */       if (!Character.isDigit(tld.charAt(0))) {
/* 150 */         isIP4 = false;
/* 151 */         break;
/*     */       }
/*     */     }
/* 154 */     return isIP4;
/*     */   }
/*     */ 
/*     */   public static String[] getDNSSubjectAlts(X509Certificate cert)
/*     */   {
/* 173 */     LinkedList subjectAltList = new LinkedList();
/* 174 */     Collection c = null;
/*     */     try {
/* 176 */       c = cert.getSubjectAlternativeNames();
/*     */     }
/*     */     catch (CertificateParsingException e) {
/* 179 */       e.printStackTrace();
/*     */     }
/* 181 */     if (c != null) {
/* 182 */       Iterator it = c.iterator();
/* 183 */       while (it.hasNext()) {
/* 184 */         List list = (List)it.next();
/* 185 */         int type = ((Integer)list.get(0)).intValue();
/*     */ 
/* 187 */         if (type == 2) {
/* 188 */           String s = (String)list.get(1);
/* 189 */           subjectAltList.add(s);
/*     */         }
/*     */       }
/*     */     }
/* 193 */     if (!subjectAltList.isEmpty()) {
/* 194 */       String[] subjectAlts = new String[subjectAltList.size()];
/* 195 */       subjectAltList.toArray(subjectAlts);
/* 196 */       return subjectAlts;
/*     */     }
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */   public String[] getCNs(X509Certificate cert)
/*     */   {
/* 203 */     LinkedList cnList = new LinkedList();
/*     */ 
/* 224 */     String subjectPrincipal = cert.getSubjectX500Principal().toString();
/* 225 */     StringTokenizer st = new StringTokenizer(subjectPrincipal, ",");
/* 226 */     while (st.hasMoreTokens()) {
/* 227 */       String tok = st.nextToken();
/* 228 */       int x = tok.indexOf("CN=");
/* 229 */       if (x >= 0) {
/* 230 */         cnList.add(tok.substring(x + 3));
/*     */       }
/*     */     }
/* 233 */     if (!cnList.isEmpty()) {
/* 234 */       String[] cns = new String[cnList.size()];
/* 235 */       cnList.toArray(cns);
/* 236 */       return cns;
/*     */     }
/* 238 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  24 */     Arrays.sort(BAD_COUNTRY_2LDS);
/*  25 */     Arrays.sort(LOCALHOSTS);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.ssl.HostNameVerifier
 * JD-Core Version:    0.6.0
 */