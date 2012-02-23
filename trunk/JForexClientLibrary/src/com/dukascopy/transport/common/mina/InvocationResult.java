/*     */ package com.dukascopy.transport.common.mina;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class InvocationResult
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 200706151042L;
/*     */   private Long requestId;
/*     */   private Serializable result;
/*     */   public static final int STATE_OK = 0;
/*     */   public static final int STATE_ERROR = 1;
/*     */   public static final int STATE_PROCESSING = 2;
/*  19 */   private int state = 0;
/*     */   private String errorReason;
/*     */   private Long receivedTime;
/*     */ 
/*     */   public InvocationResult(Serializable serializable, Long requestId)
/*     */   {
/*  32 */     this.result = serializable;
/*  33 */     this.requestId = requestId;
/*     */   }
/*     */ 
/*     */   public Serializable getResult()
/*     */   {
/*  40 */     return this.result;
/*     */   }
/*     */ 
/*     */   public void setResult(Serializable result)
/*     */   {
/*  48 */     this.result = result;
/*     */   }
/*     */ 
/*     */   public Long getRequestId()
/*     */   {
/*  55 */     return this.requestId;
/*     */   }
/*     */ 
/*     */   public void setRequestId(Long requestId)
/*     */   {
/*  63 */     this.requestId = requestId;
/*     */   }
/*     */ 
/*     */   public int getState()
/*     */   {
/*  70 */     return this.state;
/*     */   }
/*     */ 
/*     */   public void setState(int state)
/*     */   {
/*  77 */     this.state = state;
/*     */   }
/*     */ 
/*     */   public String getErrorReason()
/*     */   {
/*  84 */     return this.errorReason;
/*     */   }
/*     */ 
/*     */   public void setErrorReason(String errorReason)
/*     */   {
/*  91 */     this.errorReason = errorReason;
/*     */   }
/*     */ 
/*     */   public Long getReceivedTime()
/*     */   {
/*  98 */     return this.receivedTime;
/*     */   }
/*     */ 
/*     */   public void setReceivedTime(Long receivedTime)
/*     */   {
/* 105 */     this.receivedTime = receivedTime;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.InvocationResult
 * JD-Core Version:    0.6.0
 */