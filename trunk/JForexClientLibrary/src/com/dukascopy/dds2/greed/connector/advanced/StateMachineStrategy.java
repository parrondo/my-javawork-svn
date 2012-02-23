/*     */ package com.dukascopy.dds2.greed.connector.advanced;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IConsole;
/*     */ import com.dukascopy.api.IContext;
/*     */ import com.dukascopy.api.IEngine;
/*     */ import com.dukascopy.api.IHistory;
/*     */ import com.dukascopy.api.IIndicators;
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.connector.IBox;
/*     */ import com.dukascopy.dds2.greed.connector.impl.JFToolBox;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class StateMachineStrategy
/*     */   implements IStrategy
/*     */ {
/*     */ 
/*     */   @Configurable("Instrument")
/*  34 */   public Instrument instrument = Instrument.EURUSD;
/*     */ 
/*     */   @Configurable("Magic number")
/*  37 */   public int magic = 1127;
/*     */ 
/*     */   @Configurable("Verbose")
/*  40 */   public boolean verbose = true;
/*     */ 
/*  46 */   protected ITick tick = null;
/*  47 */   protected double exposure = 0.0D;
/*  48 */   protected double equity = 0.0D;
/*  49 */   private List<IOrder> entrys = new ArrayList();
/*  50 */   private List<IOrder> positions = new ArrayList();
/*  51 */   protected IContext context = null;
/*  52 */   protected IHistory history = null;
/*  53 */   protected IIndicators indicators = null;
/*  54 */   protected IEngine engine = null;
/*  55 */   protected IConsole console = null;
/*  56 */   protected IChart chart = null;
/*  57 */   protected IBox box = null;
/*     */ 
/*     */   public void onStart(IContext context) throws JFException
/*     */   {
/*  61 */     this.context = context;
/*  62 */     this.engine = context.getEngine();
/*  63 */     this.history = context.getHistory();
/*  64 */     this.indicators = context.getIndicators();
/*  65 */     this.chart = context.getChart(this.instrument);
/*  66 */     this.box = new JFToolBox(context);
/*     */   }
/*     */ 
/*     */   public void onAccount(IAccount account) throws JFException
/*     */   {
/*  71 */     this.equity = account.getEquity();
/*     */   }
/*     */ 
/*     */   public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar)
/*     */     throws JFException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onTick(Instrument instr, ITick tickOnTick) throws JFException
/*     */   {
/*  81 */     if (instr != this.instrument) {
/*  82 */       return;
/*     */     }
/*  84 */     if (this.equity == 0.0D) {
/*  85 */       log(new Object[] { "Waiting for account update." });
/*  86 */       return;
/*     */     }
/*  88 */     processStates(tickOnTick);
/*     */   }
/*     */ 
/*     */   protected void processStates() throws JFException {
/*  92 */     processStates(null);
/*     */   }
/*     */   protected void processStates(ITick tickOnTick) throws JFException {
/*  95 */     boolean canExitForNextTick = false;
/*  96 */     int loopAlert = 10;
/*  97 */     while ((!canExitForNextTick) && (loopAlert > 0)) {
/*  98 */       loopAlert--;
/*  99 */       boolean haveCreatedState = false;
/* 100 */       this.entrys.clear();
/* 101 */       this.positions.clear();
/* 102 */       this.exposure = 0.0D;
/* 103 */       for (IOrder order : this.engine.getOrders(this.instrument)) {
/* 104 */         if ((order.getState() == IOrder.State.OPENED) && (this.box.getMagicNumber(order) == this.magic)) {
/* 105 */           this.entrys.add(order);
/*     */         }
/* 107 */         if ((order.getState() == IOrder.State.FILLED) && (this.box.getMagicNumber(order) == this.magic)) {
/* 108 */           this.positions.add(order);
/*     */ 
/* 110 */           double amo = order.getAmount();
/* 111 */           if (order.isLong())
/* 112 */             this.exposure += amo;
/*     */           else {
/* 114 */             this.exposure -= amo;
/*     */           }
/*     */         }
/* 117 */         if (order.getState() == IOrder.State.CREATED) {
/* 118 */           haveCreatedState = true;
/*     */         }
/*     */       }
/* 121 */       if (haveCreatedState) {
/* 122 */         log(new Object[] { "Detected order in created state. Waiting." });
/* 123 */         return;
/*     */       }
/* 125 */       this.exposure = this.box.round(this.exposure, 2);
/*     */ 
/* 127 */       ITick hTick = this.history.getLastTick(this.instrument);
/* 128 */       if (hTick.getTime() > tickOnTick.getTime())
/* 129 */         this.tick = hTick;
/*     */       else {
/* 131 */         this.tick = tickOnTick;
/*     */       }
/*     */ 
/* 134 */       if (!preProcess(this.positions, this.entrys, this.exposure)) {
/* 135 */         return;
/*     */       }
/*     */ 
/* 138 */       if (this.positions.size() == 0) {
/* 139 */         if (this.entrys.size() == 0)
/* 140 */           canExitForNextTick = state00();
/* 141 */         else if (this.entrys.size() == 1)
/* 142 */           canExitForNextTick = state01((IOrder)this.entrys.get(0));
/*     */         else
/* 144 */           canExitForNextTick = state0M(this.entrys);
/*     */       }
/* 146 */       else if (this.positions.size() == 1) {
/* 147 */         if (this.entrys.size() == 0)
/* 148 */           canExitForNextTick = state10((IOrder)this.positions.get(0));
/* 149 */         else if (this.entrys.size() == 1)
/* 150 */           canExitForNextTick = state11((IOrder)this.positions.get(0), (IOrder)this.entrys.get(0));
/*     */         else {
/* 152 */           canExitForNextTick = state1M((IOrder)this.positions.get(0), this.entrys);
/*     */         }
/*     */       }
/* 155 */       else if (this.entrys.size() == 0) {
/* 156 */         canExitForNextTick = stateM0(this.positions);
/*     */       }
/* 158 */       else if (this.entrys.size() == 1) {
/* 159 */         canExitForNextTick = stateM1(this.positions, (IOrder)this.entrys.get(0));
/*     */       }
/*     */       else {
/* 162 */         canExitForNextTick = stateMM(this.positions, this.entrys);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 167 */     if (loopAlert <= 0) {
/* 168 */       log(new Object[] { "STRATEGY STOPPED BY LOOP ALERT." });
/* 169 */       this.context.stop();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void log(Object[] str)
/*     */   {
/* 176 */     if (this.verbose)
/* 177 */       this.box.print(str);
/*     */   }
/*     */ 
/*     */   public boolean preProcess(List<IOrder> positions, List<IOrder> entrys, double exposure)
/*     */   {
/* 182 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean state00()
/*     */     throws JFException
/*     */   {
/* 192 */     log(new Object[] { "STATE NOT OVERLOADED state00" });
/* 193 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean state01(IOrder order)
/*     */     throws JFException
/*     */   {
/* 204 */     log(new Object[] { "STATE NOT OVERLOADED state01" });
/* 205 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean state0M(List<IOrder> orders)
/*     */     throws JFException
/*     */   {
/* 216 */     log(new Object[] { "STATE NOT OVERLOADED state0M" });
/* 217 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean state10(IOrder position)
/*     */     throws JFException
/*     */   {
/* 227 */     log(new Object[] { "STATE NOT OVERLOADED state10" });
/* 228 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean state11(IOrder position, IOrder order)
/*     */     throws JFException
/*     */   {
/* 240 */     log(new Object[] { "STATE NOT OVERLOADED state11" });
/* 241 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean state1M(IOrder position, List<IOrder> orders)
/*     */     throws JFException
/*     */   {
/* 253 */     log(new Object[] { "STATE NOT OVERLOADED state1M" });
/* 254 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean stateM0(List<IOrder> positions)
/*     */     throws JFException
/*     */   {
/* 265 */     log(new Object[] { "STATE NOT OVERLOADED stateM0" });
/* 266 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean stateM1(List<IOrder> positions, IOrder order)
/*     */     throws JFException
/*     */   {
/* 277 */     log(new Object[] { "STATE NOT OVERLOADED stateM1" });
/* 278 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean stateMM(List<IOrder> positions, List<IOrder> orders)
/*     */     throws JFException
/*     */   {
/* 290 */     log(new Object[] { "STATE NOT OVERLOADED stateMM" });
/* 291 */     return true;
/*     */   }
/*     */ 
/*     */   public void onMessage(IMessage message) throws JFException
/*     */   {
/* 296 */     if (this.verbose)
/* 297 */       log(new Object[] { "message received for " + getClass().getSimpleName() + " " + message });
/*     */   }
/*     */ 
/*     */   public void onStop()
/*     */     throws JFException
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.advanced.StateMachineStrategy
 * JD-Core Version:    0.6.0
 */