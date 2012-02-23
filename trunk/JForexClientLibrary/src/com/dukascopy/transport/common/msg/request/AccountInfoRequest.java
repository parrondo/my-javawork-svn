/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ public class AccountInfoRequest extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "accInfoReq";
/* 14 */   private String ACTION = "action";
/*    */ 
/*    */   public AccountInfoRequest(String action)
/*    */   {
/* 18 */     setType("accInfoReq");
/* 19 */     put(this.ACTION, action);
/*    */   }
/*    */ 
/*    */   public AccountInfoRequest(ProtocolMessage message)
/*    */   {
/* 24 */     setType("accInfoReq");
/* 25 */     put(this.ACTION, message.get(this.ACTION));
/* 26 */     for (Iterator i = message.keys(); i.hasNext(); ) {
/* 27 */       String key = (String)i.next();
/* 28 */       if (!has(key))
/* 29 */         put(key, message.get(key));
/*    */     }
/*    */   }
/*    */ 
/*    */   public String getAction()
/*    */   {
/* 35 */     return (String)get(this.ACTION);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.AccountInfoRequest
 * JD-Core Version:    0.6.0
 */