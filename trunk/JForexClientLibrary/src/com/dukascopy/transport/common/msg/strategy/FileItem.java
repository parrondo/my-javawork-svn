/*     */ package com.dukascopy.transport.common.msg.strategy;
/*     */ 
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.util.Base64;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ import org.json.JSONArray;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class FileItem extends ProtocolMessage
/*     */ {
/*  45 */   private static Logger LOGGER = LoggerFactory.getLogger(FileItem.class);
/*     */   public static final String TYPE = "fileitem";
/*     */   public static final String FILE_ID = "fid";
/*     */   public static final String FILE_NAME = "fname";
/*     */   public static final String LAST_MODIFIED = "lmod";
/*     */   public static final String IS_SHARED = "shared";
/*     */   public static final String CHECKSUM = "checksum";
/*     */   public static final String FILE_OWNER_ID = "ownerid";
/*     */   public static final String FILE_USER_ID = "f_userid";
/*     */   public static final String FILE_DATA = "fdata";
/*     */   public static final String DESCRIPTION = "fm_desc";
/*     */   public static final String FILE_TYPE = "fm_type";
/*     */   public static final String SHARE_KEY = "fm_key";
/*     */   public static final String USER_SCHEMA = "fm_schema";
/*     */   public static final String OWNER_SCHEMA = "fm_oschema";
/*     */   public static final String ACCESS_TYPE = "acctype";
/*     */   public static final String PARAMETERS = "parameters";
/*     */   public static final String PARAMETERS_DEFINED = "paramsdefined";
/*     */   public static final String REMOTE_RUN_ALLOWED = "remoteRunAllowed";
/*     */ 
/*     */   public FileItem()
/*     */   {
/*  76 */     setType("fileitem");
/*     */   }
/*     */ 
/*     */   public FileItem(ProtocolMessage message) {
/*  80 */     super(message);
/*     */ 
/*  82 */     setType("fileitem");
/*     */ 
/*  84 */     setFileId(message.getLong("fid"));
/*  85 */     setFileName(message.getString("fname"));
/*     */ 
/*  87 */     String date = message.getString("lmod");
/*  88 */     if (date != null) {
/*  89 */       setLastModified(new Date(new Long(date).longValue()));
/*     */     }
/*     */ 
/*  92 */     setShared(Boolean.valueOf(message.getBoolean("shared")));
/*  93 */     setCheckSum(message.getString("checksum"));
/*  94 */     setFileOwnerId(message.getLong("ownerid"));
/*  95 */     setFileUserId(message.getLong("f_userid"));
/*  96 */     put("fdata", message.getString("fdata"));
/*  97 */     setDescription(message.getString("fm_desc"));
/*  98 */     put("fm_type", message.getString("fm_type"));
/*  99 */     setShareKey(message.getString("fm_key"));
/* 100 */     setUserSchema(message.getString("fm_schema"));
/* 101 */     setOwnerSchema(message.getString("fm_oschema"));
/*     */ 
/* 103 */     String at = message.getString("acctype");
/* 104 */     if (at != null) {
/* 105 */       setAccessType(AccessType.valueOf(at));
/*     */     }
/* 107 */     put("parameters", message.getJSONArray("parameters"));
/*     */ 
/* 109 */     setParametersDefined(message.getBoolean("paramsdefined"));
/* 110 */     setRemoteRunAllowed(message.getBoolean("remoteRunAllowed"));
/*     */   }
/*     */ 
/*     */   public void setFileId(Long fileId)
/*     */   {
/* 118 */     if (fileId != null)
/* 119 */       put("fid", fileId.toString());
/*     */   }
/*     */ 
/*     */   public Long getFileId() {
/* 123 */     return getLong("fid");
/*     */   }
/*     */ 
/*     */   public void setFileType(FileType type) {
/* 127 */     put("fm_type", type.toString());
/*     */   }
/*     */ 
/*     */   public FileType getFileType() {
/* 131 */     String ft = getString("fm_type");
/*     */ 
/* 133 */     if (ft != null) {
/* 134 */       return FileType.valueOf(ft);
/*     */     }
/* 136 */     return null;
/*     */   }
/*     */ 
/*     */   public void setDescription(String desc) {
/* 140 */     put("fm_desc", desc);
/*     */   }
/*     */ 
/*     */   public String getDescription() {
/* 144 */     return getString("fm_desc");
/*     */   }
/*     */ 
/*     */   public byte[] getFileData() {
/* 148 */     return decompress(getString("fdata"));
/*     */   }
/*     */ 
/*     */   public void setFileData(byte[] fileData) {
/* 152 */     put("fdata", compress(fileData));
/*     */   }
/*     */ 
/*     */   public String getFileName() {
/* 156 */     return getString("fname");
/*     */   }
/*     */ 
/*     */   public void setFileName(String fileName) {
/* 160 */     put("fname", fileName);
/*     */   }
/*     */ 
/*     */   public void setLastModified(Date dt) {
/* 164 */     if (dt != null)
/* 165 */       put("lmod", new Long(dt.getTime()).toString());
/*     */   }
/*     */ 
/*     */   public Date getLastModified()
/*     */   {
/* 170 */     Long ts = getLong("lmod");
/* 171 */     if (ts != null) {
/* 172 */       return new Date(ts.longValue());
/*     */     }
/*     */ 
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   public void setShared(Boolean shared) {
/* 179 */     put("shared", shared);
/*     */   }
/*     */ 
/*     */   public Boolean isShared() {
/* 183 */     return Boolean.valueOf(getBoolean("shared"));
/*     */   }
/*     */ 
/*     */   public void setCheckSum(String hash) {
/* 187 */     put("checksum", hash);
/*     */   }
/*     */ 
/*     */   public String getCheckSum() {
/* 191 */     return getString("checksum");
/*     */   }
/*     */ 
/*     */   public void setFileOwnerId(Long owner) {
/* 195 */     if (owner != null)
/* 196 */       put("ownerid", owner.toString());
/*     */   }
/*     */ 
/*     */   public Long getFileOwnerId() {
/* 200 */     return getLong("ownerid");
/*     */   }
/*     */ 
/*     */   public void setFileUserId(Long userId) {
/* 204 */     if (userId != null)
/* 205 */       put("f_userid", userId.toString());
/*     */   }
/*     */ 
/*     */   public Long getFileUserId() {
/* 209 */     return getLong("f_userid");
/*     */   }
/*     */ 
/*     */   public void setShareKey(String key) {
/* 213 */     put("fm_key", key);
/*     */   }
/*     */ 
/*     */   public String getShareKey() {
/* 217 */     return getString("fm_key");
/*     */   }
/*     */ 
/*     */   public void setUserSchema(String schema) {
/* 221 */     put("fm_schema", schema);
/*     */   }
/*     */ 
/*     */   public String getUserSchema() {
/* 225 */     return getString("fm_schema");
/*     */   }
/*     */ 
/*     */   public void setOwnerSchema(String schema) {
/* 229 */     put("fm_oschema", schema);
/*     */   }
/*     */ 
/*     */   public String getOwnerSchema() {
/* 233 */     return getString("fm_oschema");
/*     */   }
/*     */ 
/*     */   public void setAccessType(AccessType type) {
/* 237 */     put("acctype", type.toString());
/*     */   }
/*     */ 
/*     */   public AccessType getAccessType() {
/* 241 */     String at = getString("acctype");
/* 242 */     if (at != null) {
/* 243 */       return AccessType.valueOf(at);
/*     */     }
/* 245 */     return null;
/*     */   }
/*     */ 
/*     */   private static String compress(byte[] data)
/*     */   {
/* 274 */     if (data != null) {
/*     */       try {
/* 276 */         ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 277 */         ZipOutputStream zip = new ZipOutputStream(out);
/*     */ 
/* 279 */         ZipEntry zipEntry = new ZipEntry("e");
/* 280 */         zipEntry.setSize(data.length);
/*     */ 
/* 282 */         zip.putNextEntry(zipEntry);
/* 283 */         zip.write(data);
/* 284 */         zip.closeEntry();
/* 285 */         zip.close();
/*     */ 
/* 287 */         return Base64.encode(out.toByteArray());
/*     */       } catch (IOException e) {
/* 289 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/* 292 */     return null;
/*     */   }
/*     */ 
/*     */   private static byte[] decompress(String compressedData)
/*     */   {
/* 297 */     if (compressedData != null) {
/* 298 */       byte[] bytes = Base64.decode(compressedData);
/*     */ 
/* 300 */       ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes));
/*     */       try
/*     */       {
/* 304 */         ZipEntry entry = zip.getNextEntry();
/* 305 */         int size = (int)entry.getSize();
/*     */         ByteArrayOutputStream os;
/*     */         ByteArrayOutputStream os;
/* 307 */         if (size == -1)
/* 308 */           os = new ByteArrayOutputStream();
/*     */         else {
/* 310 */           os = new ByteArrayOutputStream(size);
/*     */         }
/* 312 */         byte[] buff = new byte[512];
/*     */         int i;
/* 314 */         while ((i = zip.read(buff)) > -1) {
/* 315 */           os.write(buff, 0, i);
/*     */         }
/* 317 */         zip.close();
/* 318 */         return os.toByteArray();
/*     */       } catch (IOException e) {
/* 320 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 324 */     return null;
/*     */   }
/*     */ 
/*     */   public List<StrategyParameter> getParameters() {
/* 328 */     List parameters = new ArrayList();
/* 329 */     JSONArray array = getJSONArray("parameters");
/* 330 */     if (array != null) {
/* 331 */       for (int i = 0; i < array.length(); i++) {
/* 332 */         parameters.add(new StrategyParameter(array.getJSONObject(i)));
/*     */       }
/*     */     }
/* 335 */     return parameters;
/*     */   }
/*     */ 
/*     */   public void setParameters(List<StrategyParameter> parameters) {
/* 339 */     put("parameters", new JSONArray(parameters));
/*     */   }
/*     */ 
/*     */   public void setParametersDefined(boolean defined) {
/* 343 */     put("paramsdefined", defined);
/*     */   }
/*     */ 
/*     */   public boolean isParametersDefined() {
/* 347 */     return getBoolean("paramsdefined");
/*     */   }
/*     */ 
/*     */   public void setRemoteRunAllowed(boolean allowed) {
/* 351 */     put("remoteRunAllowed", allowed);
/*     */   }
/*     */ 
/*     */   public boolean isRemoteRunAllowed() {
/* 355 */     return getBoolean("remoteRunAllowed");
/*     */   }
/*     */ 
/*     */   public static enum AccessType
/*     */   {
/*  42 */     PRIVATE, PUBLIC, PROTECTED, REMOTE;
/*     */   }
/*     */ 
/*     */   public static enum Command
/*     */   {
/*  38 */     UPLOAD, DOWNLOAD, LIST, DELETE, USE_KEY, LIST_PARAMETERS;
/*     */   }
/*     */ 
/*     */   public static enum ErrorMessage
/*     */   {
/*  24 */     KEY_NOT_FOUND("key.not.found"), KEY_ALREADY_EXIST("key.already.exist"), FILE_ALREADY_EXIST("file.already.exist");
/*     */ 
/*     */     String message;
/*     */ 
/*  29 */     private ErrorMessage(String message) { this.message = message; }
/*     */ 
/*     */     public String getMessage()
/*     */     {
/*  33 */       return this.message;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.FileItem
 * JD-Core Version:    0.6.0
 */