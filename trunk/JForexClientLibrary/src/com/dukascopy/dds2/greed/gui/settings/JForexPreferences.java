/*    */ package com.dukascopy.dds2.greed.gui.settings;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.prefs.AbstractPreferences;
/*    */ import java.util.prefs.BackingStoreException;
/*    */ 
/*    */ public class JForexPreferences extends AbstractPreferences
/*    */ {
/* 11 */   public static JForexPreferences systemRoot = new JForexPreferences(null, "");
/* 12 */   public static JForexPreferences userRoot = new JForexPreferences(null, "");
/*    */ 
/* 16 */   Map<String, String> values = new HashMap();
/* 17 */   Map<String, JForexPreferences> children = new HashMap();
/*    */ 
/*    */   protected JForexPreferences() {
/* 20 */     super(null, "");
/*    */   }
/*    */ 
/*    */   protected JForexPreferences(AbstractPreferences parent, String name) {
/* 24 */     super(parent, name);
/*    */   }
/*    */ 
/*    */   protected void putSpi(String key, String value)
/*    */   {
/* 30 */     this.values.put(key, value);
/*    */   }
/*    */ 
/*    */   protected String getSpi(String key) {
/* 34 */     return (String)this.values.get(key);
/*    */   }
/*    */ 
/*    */   protected void removeSpi(String key) {
/* 38 */     this.values.remove(key);
/*    */   }
/*    */ 
/*    */   protected void removeNodeSpi() throws BackingStoreException {
/* 42 */     ((JForexPreferences)parent()).removeNode(name());
/*    */   }
/*    */ 
/*    */   protected String[] keysSpi() throws BackingStoreException {
/* 46 */     Set stringSet = this.values.keySet();
/* 47 */     return (String[])stringSet.toArray(new String[stringSet.size()]);
/*    */   }
/*    */ 
/*    */   protected String[] childrenNamesSpi() throws BackingStoreException {
/* 51 */     Set stringSet = this.children.keySet();
/* 52 */     return (String[])stringSet.toArray(new String[stringSet.size()]);
/*    */   }
/*    */ 
/*    */   protected AbstractPreferences childSpi(String name) {
/* 56 */     JForexPreferences jForexPreferences = (JForexPreferences)this.children.get(name);
/* 57 */     if (jForexPreferences == null) {
/* 58 */       jForexPreferences = new JForexPreferences(this, name);
/* 59 */       this.children.put(name, jForexPreferences);
/*    */     }
/* 61 */     return jForexPreferences;
/*    */   }
/*    */ 
/*    */   protected void syncSpi()
/*    */     throws BackingStoreException
/*    */   {
/*    */   }
/*    */ 
/*    */   protected void flushSpi()
/*    */     throws BackingStoreException
/*    */   {
/*    */   }
/*    */ 
/*    */   private void removeNode(String child)
/*    */   {
/* 77 */     this.children.remove(child);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.JForexPreferences
 * JD-Core Version:    0.6.0
 */