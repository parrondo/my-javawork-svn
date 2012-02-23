/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import javax.swing.JTabbedPane;
/*    */ 
/*    */ public abstract class JLocalizableTabbedPane extends JTabbedPane
/*    */   implements Localizable
/*    */ {
/*    */   public JLocalizableTabbedPane()
/*    */   {
/* 14 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 19 */     translate();
/*    */ 
/* 21 */     revalidate();
/* 22 */     repaint();
/*    */   }
/*    */ 
/*    */   public abstract void translate();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTabbedPane
 * JD-Core Version:    0.6.0
 */