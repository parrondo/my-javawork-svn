/*    */ package com.dukascopy.charts.dialogs.indicators;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.event.ItemListener;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class IndicatorGeneralParamsPanel extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 6289245333768998708L;
/*    */   private final IndicatorWrapper indicatorWrapper;
/*    */   private static final int PANEL_WIDTH = 500;
/*    */   private static final int ROW_HEIGHT = 23;
/*    */ 
/*    */   public IndicatorGeneralParamsPanel(IndicatorWrapper indicatorWrapper)
/*    */   {
/* 24 */     this.indicatorWrapper = indicatorWrapper;
/*    */ 
/* 26 */     setLayout(new GridBagLayout());
/* 27 */     setBorder(new JLocalizableRoundedBorder(this, "title.general.params.panel"));
/*    */ 
/* 29 */     JLocalizableLabel labelRecalculateOnNewCandleOnly = new JLocalizableLabel("label.recalculate.on.new.candle.only");
/* 30 */     GridBagConstraints labelConstraints = new GridBagConstraints();
/* 31 */     labelConstraints.fill = 0;
/* 32 */     labelConstraints.weightx = 1.0D;
/* 33 */     labelConstraints.anchor = 17;
/* 34 */     add(labelRecalculateOnNewCandleOnly, labelConstraints);
/*    */ 
/* 36 */     JCheckBox checkBoxRecalculateOnNewCandleOnly = new JCheckBox();
/* 37 */     checkBoxRecalculateOnNewCandleOnly.setSelected(indicatorWrapper.isRecalculateOnNewCandleOnly());
/* 38 */     checkBoxRecalculateOnNewCandleOnly.addItemListener(new ItemListener()
/*    */     {
/*    */       public void itemStateChanged(ItemEvent e) {
/* 41 */         IndicatorGeneralParamsPanel.this.indicatorWrapper.setRecalculateOnNewCandleOnly(e.getStateChange() == 1);
/*    */       }
/*    */     });
/* 45 */     GridBagConstraints checkBoxConstraints = new GridBagConstraints();
/* 46 */     checkBoxConstraints.fill = 0;
/* 47 */     checkBoxConstraints.gridx = 1;
/* 48 */     checkBoxConstraints.weightx = 1.0D;
/* 49 */     checkBoxConstraints.anchor = 13;
/* 50 */     add(checkBoxRecalculateOnNewCandleOnly, checkBoxConstraints);
/*    */   }
/*    */ 
/*    */   public Dimension getPreferredSize()
/*    */   {
/* 55 */     return getDimension();
/*    */   }
/*    */ 
/*    */   public Dimension getMinimumSize()
/*    */   {
/* 60 */     return getPreferredSize();
/*    */   }
/*    */ 
/*    */   private Dimension getDimension() {
/* 64 */     return new Dimension(500, getComponentCount() * 23 + 15);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorGeneralParamsPanel
 * JD-Core Version:    0.6.0
 */