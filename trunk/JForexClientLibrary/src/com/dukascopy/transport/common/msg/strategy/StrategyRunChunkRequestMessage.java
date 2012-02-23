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
/*     */ public class StrategyRunChunkRequestMessage extends RequestMessage
/*     */   implements IStrategyAsyncMessage, Comparable<StrategyRunChunkRequestMessage>
/*     */ {
/*     */   private static final long MAX_CONTENT_SIZE = 10485760L;
/*     */   public static final String TYPE = "strategy_run_chunk";
/*     */   private static final String PASSWORD_DIGEST = "password_digest";
/*     */   private static final String FILE_ID = "file_id";
/*     */   private static final String FILE_NAME = "file_name";
/*     */   private static final String FILE_CONTENT = "file_content";
/*     */   private static final String PARAMETERS = "parameters";
/*     */   private static final String INSTRUMENTS = "instruments";
/*     */   private static final String REQUEST_ID = "id";
/*     */   private static final String MSG_ORDER = "ord";
/*     */   private static final String FINISHED = "f";
/*     */ 
/*     */   public StrategyRunChunkRequestMessage()
/*     */   {
/*  44 */     setType("strategy_run_chunk");
/*     */   }
/*     */ 
/*     */   public StrategyRunChunkRequestMessage(ProtocolMessage msg) {
/*  48 */     super(msg);
/*  49 */     setType("strategy_run_chunk");
/*     */ 
/*  51 */     setAccountName(msg.getString("account_name"));
/*  52 */     setPasswordDigest(msg.getString("password_digest"));
/*  53 */     setFileId(msg.getLong("file_id"));
/*  54 */     setFileName(msg.getString("file_name"));
/*  55 */     put("file_content", msg.getString("file_content"));
/*  56 */     put("parameters", msg.getJSONArray("parameters"));
/*  57 */     put("instruments", msg.getJSONArray("instruments"));
/*     */ 
/*  59 */     setRequestId(msg.getString("id"));
/*  60 */     setMessageOrder(msg.getInteger("ord"));
/*  61 */     setFinished(Boolean.valueOf(msg.getBoolean("f")));
/*     */   }
/*     */ 
/*     */   public void setAccountName(String accountName)
/*     */   {
/*  66 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/*  67 */       put("account_name", accountName);
/*     */   }
/*     */ 
/*     */   public String getAccountName()
/*     */   {
/*  72 */     return getString("account_name");
/*     */   }
/*     */ 
/*     */   public void setPasswordDigest(String passwordDigest) {
/*  76 */     if ((passwordDigest == null) || (passwordDigest.isEmpty())) {
/*  77 */       throw new IllegalArgumentException("Password digest is empty");
/*     */     }
/*  79 */     put("password_digest", passwordDigest);
/*     */   }
/*     */ 
/*     */   public String getPasswordDigest() {
/*  83 */     return getString("password_digest");
/*     */   }
/*     */ 
/*     */   public void setFileId(Long fileId) {
/*  87 */     if (fileId != null)
/*  88 */       put("file_id", fileId.toString());
/*     */   }
/*     */ 
/*     */   public Long getFileId()
/*     */   {
/*  93 */     return getLong("file_id");
/*     */   }
/*     */ 
/*     */   public void setFileName(String fileName) {
/*  97 */     if ((fileName == null) || (fileName.isEmpty())) {
/*  98 */       throw new IllegalArgumentException("File name is empty");
/*     */     }
/* 100 */     put("file_name", fileName);
/*     */   }
/*     */ 
/*     */   public String getFileName() {
/* 104 */     return getString("file_name");
/*     */   }
/*     */ 
/*     */   public void setFileContent(byte[] data) throws IOException {
/* 108 */     put("file_content", compress(data));
/*     */   }
/*     */ 
/*     */   public byte[] getFileContent() throws IOException {
/* 112 */     String value = getString("file_content");
/* 113 */     if ((value == null) || (value.isEmpty())) {
/* 114 */       return null;
/*     */     }
/* 116 */     return decompress(value);
/*     */   }
/*     */ 
/*     */   public Collection<StrategyParameter> getParameters()
/*     */   {
/* 121 */     Collection parameters = new ArrayList();
/* 122 */     JSONArray array = getJSONArray("parameters");
/* 123 */     if (array != null) {
/* 124 */       for (int i = 0; i < array.length(); i++) {
/* 125 */         parameters.add(new StrategyParameter(array.getJSONObject(i)));
/*     */       }
/*     */     }
/* 128 */     return parameters;
/*     */   }
/*     */ 
/*     */   public void setParameters(Collection<StrategyParameter> parameters) {
/* 132 */     put("parameters", new JSONArray(parameters));
/*     */   }
/*     */ 
/*     */   public Collection<String> getInstruments() {
/* 136 */     Collection instruments = new LinkedHashSet();
/* 137 */     JSONArray array = getJSONArray("instruments");
/* 138 */     if (array != null) {
/* 139 */       for (int i = 0; i < array.length(); i++) {
/* 140 */         instruments.add(array.getString(i));
/*     */       }
/*     */     }
/* 143 */     return instruments;
/*     */   }
/*     */ 
/*     */   public void setInstruments(Collection<String> instruments) {
/* 147 */     put("instruments", new JSONArray(instruments));
/*     */   }
/*     */ 
/*     */   public void setRequestId(String id) {
/* 151 */     put("id", id);
/*     */   }
/*     */ 
/*     */   public String getRequestId() {
/* 155 */     return getString("id");
/*     */   }
/*     */ 
/*     */   public Integer getMessageOrder() {
/* 159 */     return getInteger("ord");
/*     */   }
/*     */ 
/*     */   public void setMessageOrder(Integer msgOrder) {
/* 163 */     put("ord", msgOrder);
/*     */   }
/*     */ 
/*     */   public Boolean isFinished() {
/* 167 */     return Boolean.valueOf(getBoolean("f"));
/*     */   }
/*     */ 
/*     */   public void setFinished(Boolean finished) {
/* 171 */     put("f", finished);
/*     */   }
/*     */ 
/*     */   public int compareTo(StrategyRunChunkRequestMessage o)
/*     */   {
/* 179 */     if (o != null) {
/* 180 */       return getMessageOrder().compareTo(o.getMessageOrder());
/*     */     }
/* 182 */     return 1;
/*     */   }
/*     */ 
/*     */   private static String compress(byte[] data)
/*     */     throws IOException
/*     */   {
/* 188 */     if ((data == null) || (data.length == 0)) {
/* 189 */       throw new NullPointerException("Data");
/*     */     }
/* 191 */     if (data.length > 10485760L) {
/* 192 */       throw new IllegalArgumentException("Max content size exceeded :" + data.length + "/" + 10485760L);
/*     */     }
/*     */ 
/* 195 */     ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
/* 196 */     CheckedOutputStream checksum = new CheckedOutputStream(bufferOut, new Adler32());
/* 197 */     ZipOutputStream zipOut = new ZipOutputStream(checksum);
/* 198 */     zipOut.setLevel(9);
/*     */ 
/* 200 */     ZipEntry zipEntry = new ZipEntry("x");
/* 201 */     zipEntry.setSize(data.length);
/* 202 */     zipOut.putNextEntry(zipEntry);
/* 203 */     zipOut.write(data);
/* 204 */     zipOut.closeEntry();
/* 205 */     zipOut.flush();
/* 206 */     zipOut.close();
/*     */ 
/* 208 */     return Base64.encode(bufferOut.toByteArray());
/*     */   }
/*     */ 
/*     */   private static byte[] decompress(String data) throws IOException {
/* 212 */     if (data == null) {
/* 213 */       throw new NullPointerException("Data");
/*     */     }
/*     */ 
/* 216 */     byte[] binaryData = Base64.decode(data);
/* 217 */     ByteArrayInputStream bufferInput = new ByteArrayInputStream(binaryData);
/* 218 */     CheckedInputStream checksum = new CheckedInputStream(bufferInput, new Adler32());
/* 219 */     ZipInputStream zipInput = new ZipInputStream(checksum);
/* 220 */     zipInput.getNextEntry();
/* 221 */     ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
/*     */ 
/* 223 */     byte[] buffer = new byte[1024];
/* 224 */     int bytesRead = 0;
/* 225 */     while ((bytesRead = zipInput.read(buffer)) > 0) {
/* 226 */       bufferOut.write(buffer, 0, bytesRead);
/*     */     }
/* 228 */     zipInput.close();
/* 229 */     if (bufferOut.size() <= 0) {
/* 230 */       throw new IllegalArgumentException("Content size : " + bufferOut.size());
/*     */     }
/* 232 */     return bufferOut.toByteArray();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyRunChunkRequestMessage
 * JD-Core Version:    0.6.0
 */