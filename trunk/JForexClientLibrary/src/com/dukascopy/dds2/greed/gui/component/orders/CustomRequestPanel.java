/*    */ package com.dukascopy.dds2.greed.gui.component.orders;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.dialog.CustomRequestDialog;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*    */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*    */ import com.dukascopy.transport.common.model.type.OfferSide;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class CustomRequestPanel extends JPanel
/*    */ {
/*    */   private String instrument;
/* 25 */   private JLocalizableButton bPlaceBid = new JLocalizableButton("button.place.bid");
/* 26 */   private JLocalizableButton bPlaceOfr = new JLocalizableButton("button.place.offer");
/*    */   private CustomRequestDialog dialog;
/*    */ 
/*    */   public CustomRequestPanel(String inst)
/*    */   {
/* 31 */     build(inst);
/*    */   }
/*    */ 
/*    */   private void build(String inst) {
/* 35 */     if ((PlatformSpecific.LINUX) || (PlatformSpecific.MACOSX)) {
/* 36 */       setMaximumSize(new Dimension(250, 15));
/*    */     }
/* 38 */     this.bPlaceBid.addActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 40 */         CustomRequestPanel.access$002(CustomRequestPanel.this, new CustomRequestDialog(OfferSide.BID, CustomRequestPanel.this.instrument, null));
/* 41 */         CustomRequestPanel.this.showDialog(CustomRequestPanel.this.bPlaceBid);
/*    */       }
/*    */     });
/* 44 */     this.bPlaceOfr.addActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 46 */         CustomRequestPanel.access$002(CustomRequestPanel.this, new CustomRequestDialog(OfferSide.ASK, CustomRequestPanel.this.instrument, null));
/* 47 */         CustomRequestPanel.this.showDialog(CustomRequestPanel.this.bPlaceOfr);
/*    */       }
/*    */     });
/* 50 */     this.instrument = inst;
/* 51 */     setLayout(new GridLayout(1, 2, 0, 0));
/* 52 */     add(this.bPlaceBid);
/* 53 */     add(this.bPlaceOfr);
/*    */ 
/* 55 */     this.bPlaceOfr.setOpaque(false);
/* 56 */     this.bPlaceBid.setOpaque(false);
/*    */ 
/* 58 */     if ((PlatformSpecific.LINUX) || (PlatformSpecific.MACOSX)) {
/* 59 */       this.bPlaceOfr.setPreferredSize(new Dimension(115, 27));
/* 60 */       this.bPlaceBid.setPreferredSize(new Dimension(115, 27));
/*    */ 
/* 62 */       this.bPlaceOfr.setMinimumSize(new Dimension(105, 25));
/* 63 */       this.bPlaceBid.setMinimumSize(new Dimension(105, 25));
/*    */     }
/*    */ 
/* 66 */     setOpaque(false);
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 70 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   private void showDialog(Component parent) {
/* 74 */     this.dialog.setLocation(parent.getLocationOnScreen());
/* 75 */     this.dialog.setVisible(true);
/*    */   }
/*    */   public CustomRequestDialog getCustomRequestDialog() {
/* 78 */     return this.dialog;
/*    */   }
/*    */ 
/*    */   public void setSubmitEnabled(boolean isEnabled) {
/* 82 */     this.bPlaceOfr.setEnabled(isEnabled);
/* 83 */     this.bPlaceBid.setEnabled(isEnabled);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.CustomRequestPanel
 * JD-Core Version:    0.6.0
 */