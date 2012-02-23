/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.text.ParseException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class DbWriteMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "dbWrite";
/*     */   public static final String QUEUE_NAME = "queue";
/*     */   public static final String QUEUE_ID = "quid";
/*     */   public static final String ACTION = "action";
/*     */   public static final String MESSAGE = "msg";
/*     */ 
/*     */   public DbWriteMessage()
/*     */   {
/*  25 */     setType("dbWrite");
/*     */   }
/*     */ 
/*     */   public DbWriteMessage(String json) throws ParseException {
/*  29 */     super(json);
/*  30 */     setType("dbWrite");
/*     */   }
/*     */ 
/*     */   public DbWriteMessage(ProtocolMessage msg) {
/*  34 */     super(msg);
/*  35 */     setType("dbWrite");
/*  36 */     setQueueName(msg.getString("queue"));
/*  37 */     setQueueId(msg.getString("quid"));
/*  38 */     setAction(msg.getString("action"));
/*  39 */     put("msg", msg.getJSONObject("msg"));
/*     */   }
/*     */ 
/*     */   public DbWriteMessage(String queue, String queueId, String action, ProtocolMessage msg)
/*     */   {
/*  44 */     this();
/*  45 */     setType("dbWrite");
/*  46 */     setQueueName(queue);
/*  47 */     setQueueId(queueId);
/*  48 */     setAction(action);
/*  49 */     setMessage(msg);
/*     */   }
/*     */ 
/*     */   public DbWriteMessage(String queue, String action, ProtocolMessage msg) {
/*  53 */     this();
/*  54 */     setType("dbWrite");
/*  55 */     setQueueName(queue);
/*  56 */     setAction(action);
/*  57 */     setMessage(msg);
/*     */   }
/*     */ 
/*     */   public void setQueueName(String name)
/*     */   {
/*  66 */     put("queue", name);
/*     */   }
/*     */ 
/*     */   public String getQueueName()
/*     */   {
/*  75 */     return getString("queue");
/*     */   }
/*     */ 
/*     */   public void setQueueId(String id)
/*     */   {
/*  84 */     put("quid", id);
/*     */   }
/*     */ 
/*     */   public String getQueueId()
/*     */   {
/*  93 */     return getString("quid");
/*     */   }
/*     */ 
/*     */   public void setAction(String action)
/*     */   {
/* 102 */     put("action", action);
/*     */   }
/*     */ 
/*     */   public String getAction()
/*     */   {
/* 112 */     return getString("action");
/*     */   }
/*     */ 
/*     */   public void setMessage(ProtocolMessage message)
/*     */   {
/* 122 */     put("msg", message);
/*     */   }
/*     */ 
/*     */   public ProtocolMessage getMessge()
/*     */   {
/* 131 */     ProtocolMessage ret = null;
/*     */     try {
/* 133 */       JSONObject msg = getJSONObject("msg");
/* 134 */       ret = ProtocolMessage.parse(msg.toString());
/*     */     } catch (Exception e) {
/* 136 */       e.printStackTrace();
/*     */     }
/* 138 */     return ret;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.DbWriteMessage
 * JD-Core Version:    0.6.0
 */