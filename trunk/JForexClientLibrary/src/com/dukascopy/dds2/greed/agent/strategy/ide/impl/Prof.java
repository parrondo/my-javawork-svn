/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ class Prof
/*     */ {
/*     */   static final boolean DEBUG = true;
/* 133 */   static Map<String, Long> map = new HashMap();
/*     */ 
/*     */   static void start(String n)
/*     */   {
/* 137 */     map.put(n, Long.valueOf(System.currentTimeMillis()));
/*     */   }
/*     */ 
/*     */   static void end(String n)
/*     */   {
/* 143 */     Long begin = (Long)map.get(n);
/* 144 */     float time = (float)(System.currentTimeMillis() - begin.longValue()) / 1000.0F;
/* 145 */     System.err.println(n + ": " + time + " seconds");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.Prof
 * JD-Core Version:    0.6.0
 */