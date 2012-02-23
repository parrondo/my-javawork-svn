/*    */ package com.dukascopy.api;
/*    */ 
/*    */ public class Properties extends java.util.Properties
/*    */ {
/*    */   public boolean getProperty(String propertyName, boolean defaultValue)
/*    */   {
/* 15 */     return Boolean.valueOf(getProperty(propertyName, Boolean.toString(defaultValue))).booleanValue();
/*    */   }
/*    */ 
/*    */   public int getProperty(String propertyName, int defaultValue)
/*    */   {
/* 28 */     return Integer.parseInt(getProperty(propertyName, "" + defaultValue));
/*    */   }
/*    */ 
/*    */   public long getProperty(String propertyName, long defaultValue)
/*    */   {
/* 42 */     return Long.parseLong(getProperty(propertyName, "" + defaultValue));
/*    */   }
/*    */ 
/*    */   public double getProperty(String propertyName, double defaultValue)
/*    */   {
/* 56 */     return Double.parseDouble(getProperty(propertyName, "" + defaultValue));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.Properties
 * JD-Core Version:    0.6.0
 */