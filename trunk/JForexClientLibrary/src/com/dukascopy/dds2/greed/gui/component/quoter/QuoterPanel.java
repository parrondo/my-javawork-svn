/*    */ package com.dukascopy.dds2.greed.gui.component.quoter;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*    */ import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
/*    */ import com.dukascopy.transport.common.model.type.OrderSide;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridLayout;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class QuoterPanel extends JPanel
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private TickButton bidButton;
/*    */   private TickButton askButton;
/* 19 */   public static final Dimension SIZE = new Dimension(230, 108);
/* 20 */   public static final Dimension LINUX_SIZE = new Dimension(230, 98);
/*    */ 
/*    */   public QuoterPanel(String instrument, QuickieOrderSupport amountHolder) {
/* 23 */     this(Instrument.fromString(instrument), amountHolder);
/*    */   }
/*    */ 
/*    */   public QuoterPanel(Instrument instrument, QuickieOrderSupport amountHolder) {
/* 27 */     setLayout(new GridLayout(1, 2, 0, 0));
/*    */ 
/* 29 */     this.bidButton = TickButton.getDefaultTickerInstance(OrderSide.SELL, instrument, amountHolder);
/* 30 */     this.askButton = TickButton.getDefaultTickerInstance(OrderSide.BUY, instrument, amountHolder);
/*    */ 
/* 32 */     add(this.bidButton);
/* 33 */     add(this.askButton);
/*    */ 
/* 35 */     Dimension quoterSize = (PlatformSpecific.LINUX) || (PlatformSpecific.MACOSX) ? LINUX_SIZE : SIZE;
/*    */ 
/* 37 */     setPreferredSize(quoterSize);
/* 38 */     setMinimumSize(quoterSize);
/* 39 */     setSize(quoterSize);
/*    */   }
/*    */ 
/*    */   public void onTick(Instrument instrument)
/*    */   {
/* 44 */     this.bidButton.onTick(instrument);
/* 45 */     this.askButton.onTick(instrument);
/*    */   }
/*    */ 
/*    */   public void updateTradability(Instrument instrument, boolean tradable) {
/* 49 */     this.bidButton.updateTradability(instrument, tradable);
/* 50 */     this.askButton.updateTradability(instrument, tradable);
/*    */   }
/*    */ 
/*    */   public void setInstrument(Instrument instrument) {
/* 54 */     this.bidButton.setInstrument(instrument);
/* 55 */     this.askButton.setInstrument(instrument);
/*    */   }
/*    */ 
/*    */   public boolean isTradingPossible() {
/* 59 */     return (this.bidButton.isTradingPossible()) && (this.askButton.isTradingPossible());
/*    */   }
/*    */ 
/*    */   public void setTradable(boolean tradable) {
/* 63 */     this.bidButton.setTradable(tradable);
/* 64 */     this.askButton.setTradable(tradable);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.quoter.QuoterPanel
 * JD-Core Version:    0.6.0
 */