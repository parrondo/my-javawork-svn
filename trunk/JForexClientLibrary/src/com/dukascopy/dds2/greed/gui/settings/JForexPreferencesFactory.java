/*    */ package com.dukascopy.dds2.greed.gui.settings;
/*    */ 
/*    */ import java.util.prefs.Preferences;
/*    */ import java.util.prefs.PreferencesFactory;
/*    */ 
/*    */ public class JForexPreferencesFactory
/*    */   implements PreferencesFactory
/*    */ {
/*    */   public Preferences systemRoot()
/*    */   {
/* 10 */     return JForexPreferences.systemRoot;
/*    */   }
/*    */ 
/*    */   public Preferences userRoot() {
/* 14 */     return JForexPreferences.userRoot;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.JForexPreferencesFactory
 * JD-Core Version:    0.6.0
 */