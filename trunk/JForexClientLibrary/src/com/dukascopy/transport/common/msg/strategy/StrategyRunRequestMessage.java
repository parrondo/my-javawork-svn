/*     */ package com.dukascopy.transport.common.msg.strategy;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.RequestMessage;
/*     */ import com.dukascopy.transport.util.Base64;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.zip.Adler32;
/*     */ import java.util.zip.CheckedInputStream;
/*     */ import java.util.zip.CheckedOutputStream;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class StrategyRunRequestMessage extends RequestMessage
/*     */   implements IStrategyMessage
/*     */ {
/*     */   private static final long MAX_CONTENT_SIZE = 10485760L;
/*     */   public static final String TYPE = "strategy_run";
/*     */   private static final String PASSWORD_DIGEST = "password_digest";
/*     */   private static final String FILE_ID = "file_id";
/*     */   private static final String FILE_NAME = "file_name";
/*     */   private static final String FILE_CONTENT = "file_content";
/*     */   private static final String PARAMETERS = "parameters";
/*     */   private static final String INSTRUMENTS = "instruments";
/*     */ 
/*     */   public StrategyRunRequestMessage()
/*     */   {
/*  40 */     setType("strategy_run");
/*     */   }
/*     */ 
/*     */   public StrategyRunRequestMessage(ProtocolMessage msg) {
/*  44 */     super(msg);
/*  45 */     setType("strategy_run");
/*     */ 
/*  47 */     setAccountName(msg.getString("account_name"));
/*  48 */     setPasswordDigest(msg.getString("password_digest"));
/*  49 */     setFileId(msg.getLong("file_id"));
/*  50 */     setFileName(msg.getString("file_name"));
/*  51 */     put("file_content", msg.getString("file_content"));
/*  52 */     put("parameters", msg.getJSONArray("parameters"));
/*  53 */     put("instruments", msg.getJSONArray("instruments"));
/*     */   }
/*     */ 
/*     */   public void setAccountName(String accountName) {
/*  57 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/*  58 */       put("account_name", accountName);
/*     */   }
/*     */ 
/*     */   public String getAccountName()
/*     */   {
/*  63 */     return getString("account_name");
/*     */   }
/*     */ 
/*     */   public void setPasswordDigest(String passwordDigest) {
/*  67 */     if ((passwordDigest == null) || (passwordDigest.isEmpty())) {
/*  68 */       throw new IllegalArgumentException("Password digest is empty");
/*     */     }
/*  70 */     put("password_digest", passwordDigest);
/*     */   }
/*     */ 
/*     */   public String getPasswordDigest() {
/*  74 */     return getString("password_digest");
/*     */   }
/*     */ 
/*     */   public void setFileId(Long fileId) {
/*  78 */     if (fileId != null)
/*  79 */       put("file_id", fileId.toString());
/*     */   }
/*     */ 
/*     */   public Long getFileId()
/*     */   {
/*  84 */     return getLong("file_id");
/*     */   }
/*     */ 
/*     */   public void setFileName(String fileName) {
/*  88 */     if ((fileName == null) || (fileName.isEmpty())) {
/*  89 */       throw new IllegalArgumentException("File name is empty");
/*     */     }
/*  91 */     put("file_name", fileName);
/*     */   }
/*     */ 
/*     */   public String getFileName() {
/*  95 */     return getString("file_name");
/*     */   }
/*     */ 
/*     */   public void setFileContent(byte[] data) throws IOException {
/*  99 */     put("file_content", compress(data));
/*     */   }
/*     */ 
/*     */   public byte[] getFileContent() throws IOException {
/* 103 */     String value = getString("file_content");
/* 104 */     if ((value == null) || (value.isEmpty())) {
/* 105 */       return null;
/*     */     }
/* 107 */     return decompress(value);
/*     */   }
/*     */ 
/*     */   public Collection<StrategyParameter> getParameters()
/*     */   {
/* 112 */     Collection parameters = new ArrayList();
/* 113 */     JSONArray array = getJSONArray("parameters");
/* 114 */     if (array != null) {
/* 115 */       for (int i = 0; i < array.length(); i++) {
/* 116 */         parameters.add(new StrategyParameter(array.getJSONObject(i)));
/*     */       }
/*     */     }
/* 119 */     return parameters;
/*     */   }
/*     */ 
/*     */   public void setParameters(Collection<StrategyParameter> parameters) {
/* 123 */     put("parameters", new JSONArray(parameters));
/*     */   }
/*     */ 
/*     */   public Collection<String> getInstruments() {
/* 127 */     Collection instruments = new LinkedHashSet();
/* 128 */     JSONArray array = getJSONArray("instruments");
/* 129 */     if (array != null) {
/* 130 */       for (int i = 0; i < array.length(); i++) {
/* 131 */         instruments.add(array.getString(i));
/*     */       }
/*     */     }
/* 134 */     return instruments;
/*     */   }
/*     */ 
/*     */   public void setInstruments(Collection<String> instruments) {
/* 138 */     put("instruments", new JSONArray(instruments));
/*     */   }
/*     */ 
/*     */   private static String compress(byte[] data)
/*     */     throws IOException
/*     */   {
/* 144 */     if ((data == null) || (data.length == 0)) {
/* 145 */       throw new NullPointerException("Data");
/*     */     }
/* 147 */     if (data.length > 10485760L) {
/* 148 */       throw new IllegalArgumentException("Max content size exceeded :" + data.length + "/" + 10485760L);
/*     */     }
/*     */ 
/* 151 */     ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
/* 152 */     CheckedOutputStream checksum = new CheckedOutputStream(bufferOut, new Adler32());
/* 153 */     ZipOutputStream zipOut = new ZipOutputStream(checksum);
/* 154 */     zipOut.setLevel(9);
/*     */ 
/* 156 */     ZipEntry zipEntry = new ZipEntry("x");
/* 157 */     zipEntry.setSize(data.length);
/* 158 */     zipOut.putNextEntry(zipEntry);
/* 159 */     zipOut.write(data);
/* 160 */     zipOut.closeEntry();
/* 161 */     zipOut.flush();
/* 162 */     zipOut.close();
/*     */ 
/* 164 */     return Base64.encode(bufferOut.toByteArray());
/*     */   }
/*     */ 
/*     */   private static byte[] decompress(String data) throws IOException {
/* 168 */     if (data == null) {
/* 169 */       throw new NullPointerException("Data");
/*     */     }
/*     */ 
/* 172 */     byte[] binaryData = Base64.decode(data);
/* 173 */     ByteArrayInputStream bufferInput = new ByteArrayInputStream(binaryData);
/* 174 */     CheckedInputStream checksum = new CheckedInputStream(bufferInput, new Adler32());
/* 175 */     ZipInputStream zipInput = new ZipInputStream(checksum);
/* 176 */     zipInput.getNextEntry();
/* 177 */     ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
/*     */ 
/* 179 */     byte[] buffer = new byte[1024];
/* 180 */     int bytesRead = 0;
/* 181 */     while ((bytesRead = zipInput.read(buffer)) > 0) {
/* 182 */       bufferOut.write(buffer, 0, bytesRead);
/*     */     }
/* 184 */     zipInput.close();
/* 185 */     if (bufferOut.size() <= 0) {
/* 186 */       throw new IllegalArgumentException("Content size : " + bufferOut.size());
/*     */     }
/* 188 */     return bufferOut.toByteArray();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyRunRequestMessage
 * JD-Core Version:    0.6.0
 */