/*    */ package com.dukascopy.api.impl.util;
/*    */ 
/*    */ import com.dukascopy.api.JFException;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.text.MessageFormat;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ComponentDownloader
/*    */ {
/*    */   public static final String mainDownloadLink = "http://arizona.rix.dukascopy.com:8080/strategystorageserver/getCompiledFile?id=";
/*    */   public static final String userDownloadLink = "http://arizona.rix.dukascopy.com:8080/strategystorageserver/getUserStrategy?id=";
/* 23 */   private static final Logger LOGGER = LoggerFactory.getLogger(ComponentDownloader.class);
/*    */   private static ComponentDownloader downloaderObject;
/*    */ 
/*    */   public static synchronized ComponentDownloader getInstance()
/*    */   {
/* 28 */     if (downloaderObject == null) {
/* 29 */       downloaderObject = new ComponentDownloader();
/*    */     }
/* 31 */     return downloaderObject;
/*    */   }
/*    */ 
/*    */   public byte[] getFileSource(String id, String cookie, String filePath) throws IOException, JFException {
/* 35 */     URLConnection hpCon = getURLConnectionFromPath(id, null, filePath);
/* 36 */     String fileNameHeader = hpCon.getHeaderField("Content-Disposition");
/* 37 */     String fileName = parseNameFromHeader(fileNameHeader);
/* 38 */     if (fileName == null) {
/* 39 */       String message = MessageFormat.format("File with id={0} not found ", new Object[] { id });
/* 40 */       LOGGER.error(message);
/* 41 */       throw new JFException(message);
/*    */     }
/* 43 */     LOGGER.debug("File name: " + fileName);
/*    */ 
/* 45 */     return createByteArray(hpCon, id);
/*    */   }
/*    */ 
/*    */   private URLConnection getURLConnectionFromPath(String id, String cookie, String filePath) throws MalformedURLException, IOException {
/* 49 */     String urlPath = filePath + id;
/* 50 */     LOGGER.debug("URL: " + urlPath);
/* 51 */     URL httpUrl = new URL(urlPath);
/* 52 */     URLConnection hpCon = httpUrl.openConnection();
/* 53 */     setCookie(cookie, hpCon);
/* 54 */     return hpCon;
/*    */   }
/*    */ 
/*    */   private void setCookie(String cookie, URLConnection hpCon) {
/* 58 */     if (cookie != null)
/* 59 */       hpCon.setRequestProperty("Cookie", cookie);
/*    */   }
/*    */ 
/*    */   private byte[] createByteArray(URLConnection hpCon, String componentId)
/*    */     throws IOException, JFException
/*    */   {
/* 66 */     ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
/*    */ 
/* 68 */     int len = hpCon.getContentLength();
/* 69 */     if (len > 0) {
/* 70 */       InputStream input = hpCon.getInputStream();
/*    */       int c;
/* 71 */       while ((c = input.read()) != -1) {
/* 72 */         byteOutputStream.write(c);
/*    */       }
/* 74 */       byte[] byteArray = byteOutputStream.toByteArray();
/* 75 */       input.close();
/* 76 */       byteOutputStream.close();
/*    */     } else {
/* 78 */       String message = "No Content Available";
/* 79 */       LOGGER.info(message);
/* 80 */       throw new JFException(message);
/*    */     }
/*    */     int c;
/*    */     InputStream input;
/*    */     byte[] byteArray;
/* 83 */     return byteArray;
/*    */   }
/*    */ 
/*    */   private String parseNameFromHeader(String fileNameHeader) {
/* 87 */     if (fileNameHeader == null) {
/* 88 */       return null;
/*    */     }
/* 90 */     String token = "filename=";
/* 91 */     String fullFileName = fileNameHeader.substring(fileNameHeader.indexOf(token) + token.length());
/* 92 */     return fullFileName;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.util.ComponentDownloader
 * JD-Core Version:    0.6.0
 */