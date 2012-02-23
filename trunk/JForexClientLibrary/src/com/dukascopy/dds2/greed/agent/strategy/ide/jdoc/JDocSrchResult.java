/*    */ package com.dukascopy.dds2.greed.agent.strategy.ide.jdoc;
/*    */ 
/*    */ public class JDocSrchResult
/*    */ {
/*    */   private String text;
/*    */   private String fileName;
/*    */   private String filePath;
/*    */   private String query;
/*    */ 
/*    */   public JDocSrchResult(String text, String fileName, String filePath, String query)
/*    */   {
/* 15 */     this.text = text;
/* 16 */     this.fileName = fileName;
/* 17 */     this.filePath = filePath;
/* 18 */     this.query = query;
/*    */   }
/*    */ 
/*    */   public String getText() {
/* 22 */     return this.text;
/*    */   }
/*    */ 
/*    */   public String getFileName() {
/* 26 */     return this.fileName;
/*    */   }
/*    */ 
/*    */   public String getFilePath() {
/* 30 */     return this.filePath;
/*    */   }
/*    */ 
/*    */   public String getQuery() {
/* 34 */     return this.query;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.JDocSrchResult
 * JD-Core Version:    0.6.0
 */