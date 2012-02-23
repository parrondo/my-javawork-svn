/*    */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ 
/*    */ public class CurrencyTreeNode extends WorkspaceTreeNode
/*    */ {
/*    */   private Instrument instrument;
/*  9 */   private boolean tradable = true;
/*    */ 
/*    */   CurrencyTreeNode(Instrument instrument, CurrenciesTreeNode parent)
/*    */   {
/* 13 */     super(true, "");
/* 14 */     this.instrument = instrument;
/* 15 */     setParent(parent);
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 20 */     StringBuilder name = new StringBuilder();
/* 21 */     if (this.instrument == null)
/* 22 */       name.append("undefined");
/*    */     else {
/* 24 */       name.append(this.instrument.toString());
/*    */     }
/* 26 */     return name.toString();
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument() {
/* 30 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public void setInstrument(Instrument instrument) {
/* 34 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public boolean isTradable() {
/* 38 */     return this.tradable;
/*    */   }
/*    */ 
/*    */   public void setTradable(boolean tradable) {
/* 42 */     this.tradable = tradable;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrencyTreeNode
 * JD-Core Version:    0.6.0
 */