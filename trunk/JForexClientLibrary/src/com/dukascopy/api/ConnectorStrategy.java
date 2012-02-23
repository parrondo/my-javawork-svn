/*     */ package com.dukascopy.api;
/*     */ 
/*     */ import com.dukascopy.api.connector.AbstractConnectorStrategyImpl;
/*     */ import com.dukascopy.api.connector.IConnector;
/*     */ import java.awt.Color;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class ConnectorStrategy extends AbstractConnectorStrategyImpl
/*     */ {
/*  17 */   public boolean mqldbg = false;
/*     */ 
/*  19 */   protected long LastTickTimeMQLFormat = 0L;
/*  20 */   private volatile int ticks = 0;
/*     */ 
/*  22 */   protected IContext context = null;
/*  23 */   protected IConsole console = null;
/*  24 */   protected IEngine engine = null;
/*     */ 
/*  26 */   protected IIndicators indicators = null;
/*  27 */   protected List<IOrder> closeOrders = new ArrayList();
/*     */ 
/*     */   public void onStart(IContext context) throws JFException {
/*  30 */     setInitialized(false);
/*  31 */     setConnector(getConnectorInstance());
/*  32 */     getConnector().setStrategy(this);
/*  33 */     this.console = context.getConsole();
/*  34 */     this.context = context;
/*  35 */     this.engine = context.getEngine();
/*     */ 
/*  37 */     getConnector().setIHistory(context.getHistory());
/*  38 */     this.indicators = context.getIndicators();
/*  39 */     if (this.currentChart != null) {
/*  40 */       if (this.currentChart.getInstrument() != null) {
/*  41 */         this.currentInstrument = this.currentChart.getInstrument();
/*     */       }
/*  43 */       if (this.currentChart.getSelectedPeriod() != null) {
/*  44 */         this.currentPeriod = this.currentChart.getSelectedPeriod();
/*     */       }
/*     */     }
/*     */ 
/*  48 */     if (this.currentInstrument == null) {
/*  49 */       this.console.getErr().print("Instrument is not defined.");
/*  50 */       context.stop();
/*  51 */       return;
/*     */     }
/*  53 */     if (this.currentPeriod == null) {
/*  54 */       this.console.getErr().print("Period is not defined.");
/*  55 */       context.stop();
/*  56 */       return;
/*     */     }
/*     */ 
/*  59 */     int currentPeriodMQL = (int)(this.currentPeriod.getInterval() / 60000L);
/*  60 */     if (!isPeriodAllowed(currentPeriodMQL)) {
/*  61 */       throw new JFException("Period [" + currentPeriodMQL + "] is not allowed.");
/*     */     }
/*     */ 
/*  64 */     this.Point = getCurrentInstrument().getPipValue();
/*  65 */     this.Digits = (this.Point == 0.01D ? 3 : 5);
/*  66 */     this.LastTickTimeMQLFormat = (this.lastTickTime / 1000L);
/*     */ 
/*  68 */     this.closeOrders.clear();
/*     */ 
/*  77 */     super.onStart(context);
/*  78 */     init();
/*  79 */     OnInit();
/*  80 */     setInitialized(true);
/*     */   }
/*     */ 
/*     */   public IChart getChart() {
/*  84 */     return getChart((Instrument)null);
/*     */   }
/*     */ 
/*     */   public IChart getChart(Instrument instrument) {
/*  88 */     return getConnector().getChart();
/*     */   }
/*     */ 
/*     */   public void setChart(IChart chart) {
/*  92 */     this.currentChart = chart;
/*     */   }
/*     */ 
/*     */   public synchronized void onAccount(IAccount account) throws JFException {
/*  96 */     Print(new Object[] { "onAccount(" + account + ")" });
/*     */ 
/*  98 */     this.account = account;
/*  99 */     setIAccount(account);
/* 100 */     setInitialized(true);
/*     */   }
/*     */ 
/*     */   public void onMessage(IMessage message) throws JFException {
/* 104 */     Print(new Object[] { "onMessage ------------- " + message });
/*     */ 
/* 106 */     if ((message.getType() != IMessage.Type.ORDER_SUBMIT_OK) || (
/* 108 */       (message.getType() != IMessage.Type.ORDER_FILL_OK) || 
/* 110 */       (message.getType() == IMessage.Type.ORDER_CLOSE_OK))) {
/* 111 */       IOrder order = message.getOrder();
/* 112 */       if (order != null) {
/* 113 */         Iterator i$ = this.closeOrders.iterator();
/*     */         while (true) if (i$.hasNext()) { IOrder co = (IOrder)i$.next();
/* 114 */             if ((order.getId() != null) && (order.getId().equals(co.getId())))
/*     */               break;
/* 116 */             continue;
/*     */           } else
/*     */           {
/* 119 */             this.closeOrders.add(order);
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onStop()
/*     */     throws JFException
/*     */   {
/* 133 */     setRunning(false);
/* 134 */     deinit();
/* 135 */     OnDeinit();
/* 136 */     super.onStop();
/*     */   }
/*     */ 
/*     */   public void onTick(Instrument instrument, ITick tick) throws JFException {
/* 140 */     synchronized (this) {
/* 141 */       this.ticks += 1;
/* 142 */       this.Ask = tick.getAsk();
/* 143 */       this.Bid = tick.getBid();
/* 144 */       this.Point = getCurrentInstrument().getPipValue();
/* 145 */       this.Digits = (this.Point == 0.01D ? 3 : 5);
/* 146 */       setLastTickTime(tick.getTime());
/* 147 */       this.LastTickTimeMQLFormat = (this.lastTickTime / 1000L);
/*     */     }
/* 149 */     if (this.currentInstrument != instrument) {
/* 150 */       return;
/*     */     }
/* 152 */     if ((getIAccount() == null) || (getIAccount().getCreditLine() == 0.0D))
/*     */     {
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     setRunning(true);
/*     */ 
/* 166 */     if (getChart() != null)
/* 167 */       this.Bars = getChart(instrument).getBarsCount();
/*     */     else {
/* 169 */       this.Bars = 2000;
/*     */     }
/*     */ 
/* 172 */     if (isInitialized()) {
/* 173 */       start();
/* 174 */       OnStart();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int init()
/*     */     throws JFException
/*     */   {
/* 182 */     return 0;
/*     */   }
/*     */ 
/*     */   protected int deinit() throws JFException {
/* 186 */     return 0;
/*     */   }
/*     */   protected abstract int start() throws JFException;
/*     */ 
/*     */   protected int OnInit() {
/* 192 */     return 0;
/*     */   }
/*     */ 
/*     */   protected int OnDeinit() {
/* 196 */     return 0;
/*     */   }
/*     */ 
/*     */   protected int OnStart() {
/* 200 */     return 0;
/*     */   }
/*     */ 
/*     */   public static final Color colorFromHex(String hex) {
/* 204 */     int intValue = Integer.parseInt(hex, 16);
/* 205 */     Color color = new Color(intValue);
/* 206 */     return color;
/*     */   }
/*     */ 
/*     */   public Instrument getCurrentInstrument() {
/* 210 */     return this.currentInstrument;
/*     */   }
/*     */ 
/*     */   public void setCurrentInstrument(Instrument currentInstrument) {
/* 214 */     this.currentInstrument = currentInstrument;
/*     */   }
/*     */ 
/*     */   protected double Open(Number shift)
/*     */     throws JFException
/*     */   {
/* 227 */     return getConnector().iOpen(null, 0, shift.intValue(), OfferSide.BID);
/*     */   }
/*     */ 
/*     */   protected double Close(Number shift) throws JFException
/*     */   {
/* 232 */     return getConnector().iClose(null, 0, shift.intValue(), OfferSide.BID);
/*     */   }
/*     */ 
/*     */   protected double Low(Number shift) throws JFException
/*     */   {
/* 237 */     return getConnector().iLow(null, 0, shift.intValue(), OfferSide.BID);
/*     */   }
/*     */ 
/*     */   protected double High(Number shift) throws JFException
/*     */   {
/* 242 */     return getConnector().iHigh(null, 0, shift.intValue(), OfferSide.BID);
/*     */   }
/*     */ 
/*     */   protected double Volume(Number shift) throws JFException
/*     */   {
/* 247 */     return getConnector().iVolume(null, 0, shift.intValue());
/*     */   }
/*     */ 
/*     */   protected long Time(Number shift) throws JFException
/*     */   {
/* 252 */     return getConnector().iTime(null, 0, shift.intValue());
/*     */   }
/*     */   protected int GetTickCount() {
/* 255 */     synchronized (this) {
/* 256 */       return this.ticks;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.ConnectorStrategy
 * JD-Core Version:    0.6.0
 */