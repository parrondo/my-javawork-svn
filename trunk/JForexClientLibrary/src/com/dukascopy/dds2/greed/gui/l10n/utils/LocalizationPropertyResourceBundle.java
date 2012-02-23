/*    */ package com.dukascopy.dds2.greed.gui.l10n.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.util.PropertyResourceBundle;
/*    */ 
/*    */ public class LocalizationPropertyResourceBundle extends PropertyResourceBundle
/*    */ {
/*    */   public LocalizationPropertyResourceBundle(InputStream stream)
/*    */     throws IOException
/*    */   {
/* 15 */     super(stream);
/*    */   }
/*    */ 
/*    */   public LocalizationPropertyResourceBundle(InputStreamReader inputStreamReader) throws IOException {
/* 19 */     super(inputStreamReader);
/*    */   }
/*    */ 
/*    */   public Object handleGetObject(String key)
/*    */   {
/* 24 */     Object object = super.handleGetObject(key);
/* 25 */     object = checkSymbols(object);
/* 26 */     return object;
/*    */   }
/*    */ 
/*    */   private Object checkSymbols(Object object) {
/* 30 */     if ((object instanceof String)) {
/* 31 */       String targetString = (String)object;
/* 32 */       if ((targetString.contains("{")) && (targetString.contains("}"))) {
/* 33 */         object = targetString.replace("'", "''");
/*    */       }
/*    */     }
/* 36 */     return object;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.utils.LocalizationPropertyResourceBundle
 * JD-Core Version:    0.6.0
 */