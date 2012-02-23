/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.tabbedpane.MainTabbedPane;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public abstract class JLocalizableMainTabbedPane extends MainTabbedPane
/*    */   implements Localizable
/*    */ {
/*    */   public JLocalizableMainTabbedPane()
/*    */   {
/* 13 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 18 */     translate();
/*    */   }
/*    */ 
/*    */   public abstract void translate();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMainTabbedPane
 * JD-Core Version:    0.6.0
 */