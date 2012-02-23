/*     */ package com.dukascopy.transport.common.msg.strategy;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class FileMngResponseMessage extends ResponseMessage
/*     */ {
/*     */   public static final String TYPE = "fmngresp";
/*     */   public static final String FILE_COMMAND = "fmr_cmd";
/*     */   public static final String FILE_LIST = "fmr_lst";
/*     */   public static final String FILE_ITEM = "item";
/*     */   public static final String STRING_LIST = "str_lst";
/*     */   public static final String REQUEST_ID = "request_id";
/*     */ 
/*     */   public FileMngResponseMessage()
/*     */   {
/*  23 */     setType("fmngresp");
/*     */   }
/*     */ 
/*     */   public FileMngResponseMessage(ProtocolMessage msg) {
/*  27 */     super(msg);
/*  28 */     setType("fmngresp");
/*     */ 
/*  30 */     setCommand(FileItem.Command.valueOf(msg.getString("fmr_cmd")));
/*  31 */     setFileList(getFileList(msg));
/*  32 */     String fileItem = msg.getString("item");
/*  33 */     if (fileItem != null)
/*  34 */       setFileItem((FileItem)ProtocolMessage.parse(fileItem));
/*  35 */     put("str_lst", msg.getJSONArray("str_lst"));
/*  36 */     setRequestId(msg.getInteger("request_id"));
/*     */   }
/*     */ 
/*     */   public void setCommand(FileItem.Command cmd) {
/*  40 */     put("fmr_cmd", cmd.toString());
/*     */   }
/*     */ 
/*     */   public FileItem.Command getCommand() {
/*  44 */     return FileItem.Command.valueOf(getString("fmr_cmd"));
/*     */   }
/*     */ 
/*     */   public void setFileList(List<FileItem> files) {
/*  48 */     JSONArray a = new JSONArray();
/*     */ 
/*  50 */     for (FileItem file : files) {
/*  51 */       a.put(file);
/*     */     }
/*     */ 
/*  54 */     put("fmr_lst", a);
/*     */   }
/*     */ 
/*     */   public List<FileItem> getFileList() {
/*  58 */     return getFileList(this);
/*     */   }
/*     */ 
/*     */   public List<FileItem> getFileList(ProtocolMessage msg) {
/*  62 */     JSONArray a = msg.getJSONArray("fmr_lst");
/*  63 */     if (a != null) {
/*  64 */       List files = new ArrayList(a.length());
/*     */ 
/*  66 */       for (int i = 0; i < a.length(); i++) {
/*  67 */         FileItem fi = (FileItem)ProtocolMessage.parse(a.getString(i));
/*  68 */         if (fi == null) {
/*  69 */           return null;
/*     */         }
/*  71 */         files.add(fi);
/*     */       }
/*     */ 
/*  74 */       return files;
/*     */     }
/*     */ 
/*  77 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public void setFileItem(FileItem item) {
/*  81 */     put("item", item);
/*     */   }
/*     */ 
/*     */   public FileItem getFileItem() {
/*  85 */     return (FileItem)get("item");
/*     */   }
/*     */ 
/*     */   public void setStringList(List<String> lst) {
/*  89 */     JSONArray arr = new JSONArray();
/*  90 */     for (String s : lst) {
/*  91 */       arr.put(s);
/*     */     }
/*  93 */     put("str_lst", arr);
/*     */   }
/*     */ 
/*     */   public List<String> getStringList() {
/*  97 */     JSONArray arr = getJSONArray("str_lst");
/*  98 */     List strings = new ArrayList();
/*     */ 
/* 100 */     if (arr != null) {
/* 101 */       for (int i = 0; i < arr.length(); i++) {
/* 102 */         strings.add(arr.getString(i));
/*     */       }
/*     */     }
/*     */ 
/* 106 */     return strings;
/*     */   }
/*     */ 
/*     */   public void setRequestId(Integer count) {
/* 110 */     put("request_id", count);
/*     */   }
/*     */ 
/*     */   public Integer getRequestId() {
/* 114 */     return getInteger("request_id");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.FileMngResponseMessage
 * JD-Core Version:    0.6.0
 */