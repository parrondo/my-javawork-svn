/*    */ package com.dukascopy.dds2.greed.agent.strategy;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FilenameFilter;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class ParseFilenameFilter
/*    */   implements FilenameFilter
/*    */ {
/*    */   final String template;
/*    */ 
/*    */   public ParseFilenameFilter(String template)
/*    */   {
/* 16 */     this.template = template;
/*    */   }
/*    */ 
/*    */   public boolean accept(File dir, String name) {
/* 20 */     StringTokenizer st = new StringTokenizer(this.template, "*");
/*    */ 
/* 22 */     if (!this.template.startsWith("*")) {
/* 23 */       String start = st.nextToken();
/* 24 */       if (name.startsWith(start))
/* 25 */         name = name.substring(name.indexOf(start) + start.length());
/*    */       else {
/* 27 */         return false;
/*    */       }
/*    */     }
/*    */ 
/* 31 */     while (st.hasMoreTokens()) {
/* 32 */       String shouldContain = st.nextToken();
/* 33 */       if (name.contains(shouldContain))
/* 34 */         name = name.substring(name.indexOf(shouldContain) + shouldContain.length());
/*    */       else {
/* 36 */         return false;
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 42 */     return (this.template.endsWith("*")) || 
/* 41 */       (name.length() == 0);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ParseFilenameFilter
 * JD-Core Version:    0.6.0
 */