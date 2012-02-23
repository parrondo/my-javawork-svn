/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class IdRezMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "idRez";
/*    */   public static final String SEQUENCE = "seq";
/*    */   public static final String ID_COUNT = "cnt";
/*    */   public static final String IDS = "ids";
/*    */ 
/*    */   public IdRezMessage()
/*    */   {
/* 21 */     setType("idRez");
/*    */   }
/*    */ 
/*    */   public IdRezMessage(ProtocolMessage message) {
/* 25 */     super(message);
/* 26 */     setType("idRez");
/* 27 */     put("seq", message.getString("seq"));
/* 28 */     put("cnt", message.getString("cnt"));
/* 29 */     put("ids", message.getString("ids"));
/*    */   }
/*    */ 
/*    */   public IdRezMessage(String sequence, Integer count, String ids)
/*    */   {
/* 34 */     setType("idRez");
/* 35 */     setSequence(sequence);
/* 36 */     setIdCount(count);
/* 37 */     setIds(ids);
/*    */   }
/*    */ 
/*    */   public String getSequence()
/*    */   {
/* 46 */     return getString("seq");
/*    */   }
/*    */ 
/*    */   public void setSequence(String name)
/*    */   {
/* 55 */     put("seq", name);
/*    */   }
/*    */ 
/*    */   public Integer getIdCount()
/*    */   {
/* 64 */     return getInteger("cnt");
/*    */   }
/*    */ 
/*    */   public void setIdCount(Integer idCount)
/*    */   {
/* 73 */     put("cnt", idCount);
/*    */   }
/*    */ 
/*    */   public List<Long> getIds()
/*    */   {
/* 78 */     List positions = new ArrayList();
/* 79 */     String positionString = getString("ids");
/* 80 */     if (positionString == null) {
/* 81 */       return positions;
/*    */     }
/* 83 */     StringTokenizer tokenizer = new StringTokenizer(positionString, ";");
/* 84 */     while (tokenizer.hasMoreTokens()) {
/* 85 */       positions.add(Long.valueOf(Long.parseLong(tokenizer.nextToken())));
/*    */     }
/* 87 */     return positions;
/*    */   }
/*    */ 
/*    */   public void setIds(String ids) {
/* 91 */     put("ids", ids);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.IdRezMessage
 * JD-Core Version:    0.6.0
 */