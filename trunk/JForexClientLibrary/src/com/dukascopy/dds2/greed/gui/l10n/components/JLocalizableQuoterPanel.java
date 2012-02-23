/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.gui.component.quoter.QuoterPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*    */ 
/*    */ public class JLocalizableQuoterPanel extends QuoterPanel
/*    */   implements Localizable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public JLocalizableQuoterPanel(Instrument instrument, QuickieOrderSupport amountHolder)
/*    */   {
/* 14 */     super(instrument, amountHolder);
/* 15 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public JLocalizableQuoterPanel(String instrument, QuickieOrderSupport amountHolder) {
/* 19 */     super(instrument, amountHolder);
/* 20 */     LocalizationManager.addLocalizable(this);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 25 */     repaint();
/* 26 */     revalidate();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableQuoterPanel
 * JD-Core Version:    0.6.0
 */