/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class FileMngRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "fmngreq";
/*    */   public static final String FILE_ITEM = "item";
/*    */   public static final String COMMAND = "cmd";
/*    */   public static final String REQUEST_ID = "request_id";
/*    */ 
/*    */   public FileMngRequestMessage()
/*    */   {
/* 16 */     setType("fmngreq");
/*    */   }
/*    */ 
/*    */   public FileMngRequestMessage(ProtocolMessage msg) {
/* 20 */     super(msg);
/* 21 */     setType("fmngreq");
/*    */ 
/* 23 */     setCommand(FileItem.Command.valueOf(msg.getString("cmd")));
/* 24 */     setFileItem((FileItem)ProtocolMessage.parse(msg.getString("item")));
/* 25 */     setRequestId(msg.getInteger("request_id"));
/*    */   }
/*    */ 
/*    */   public void setCommand(FileItem.Command cmd) {
/* 29 */     put("cmd", cmd.toString());
/*    */   }
/*    */ 
/*    */   public FileItem.Command getCommand() {
/* 33 */     return FileItem.Command.valueOf(getString("cmd"));
/*    */   }
/*    */ 
/*    */   public void setFileItem(FileItem item) {
/* 37 */     put("item", item);
/*    */   }
/*    */ 
/*    */   public FileItem getFileItem() {
/* 41 */     return (FileItem)get("item");
/*    */   }
/*    */ 
/*    */   public void setRequestId(Integer count) {
/* 45 */     put("request_id", count);
/*    */   }
/*    */ 
/*    */   public Integer getRequestId() {
/* 49 */     return getInteger("request_id");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.FileMngRequestMessage
 * JD-Core Version:    0.6.0
 */