/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.IInstrumentStatusMessage;
/*    */ import com.dukascopy.api.IMessage.Type;
/*    */ import com.dukascopy.api.Instrument;
/*    */ 
/*    */ class InstrumentStatusMessageImpl extends PlatformMessageImpl
/*    */   implements IInstrumentStatusMessage
/*    */ {
/*    */   private final Instrument instrument;
/*    */   private final boolean tradable;
/*    */ 
/*    */   public InstrumentStatusMessageImpl(Instrument instrument, boolean tradable, long creationTime)
/*    */   {
/* 16 */     super(null, null, IMessage.Type.INSTRUMENT_STATUS, creationTime);
/* 17 */     this.instrument = instrument;
/* 18 */     this.tradable = tradable;
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument()
/*    */   {
/* 23 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public boolean isTradable()
/*    */   {
/* 28 */     return this.tradable;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 33 */     return "MessageType : " + getType() + " Instument : " + this.instrument + " Tradable : " + this.tradable;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.InstrumentStatusMessageImpl
 * JD-Core Version:    0.6.0
 */