/*     */ package com.dukascopy.dds2.greed.model;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*     */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MarketView
/*     */ {
/*  32 */   private Map<String, CurrencyMarketWrapper> currencyMarkets = new HashMap();
/*  33 */   private Map<String, InstrumentStatusUpdateMessage> instrumentStates = new HashMap();
/*  34 */   private Map<String, Date> lastReceivedTimes = new HashMap();
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper marketState)
/*     */   {
/*  49 */     String instrument = marketState.getInstrument();
/*  50 */     this.currencyMarkets.put(instrument, marketState);
/*  51 */     this.lastReceivedTimes.put(instrument, new Date());
/*     */   }
/*     */ 
/*     */   public CurrencyMarketWrapper getLastMarketState(String instrument)
/*     */   {
/*  61 */     return (CurrencyMarketWrapper)this.currencyMarkets.get(instrument);
/*     */   }
/*     */ 
/*     */   public Set<String> getInstruments()
/*     */   {
/*  70 */     return new HashSet(this.currencyMarkets.keySet());
/*     */   }
/*     */ 
/*     */   public boolean isAlreadySubscribed(String instrument) {
/*  74 */     Set subscribed = getInstruments();
/*  75 */     return subscribed.contains(instrument);
/*     */   }
/*     */ 
/*     */   public CurrencyOffer getBestOffer(String instrument, OfferSide side)
/*     */   {
/*  86 */     CurrencyMarketWrapper currencyMarket = (CurrencyMarketWrapper)this.currencyMarkets.get(instrument);
/*  87 */     if (currencyMarket != null) {
/*  88 */       return currencyMarket.getBestOffer(side);
/*     */     }
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   public void setLastReceivedTime(String instrument, Date date)
/*     */   {
/* 101 */     this.lastReceivedTimes.put(instrument, date);
/*     */   }
/*     */ 
/*     */   public Date getLastReceivedTime(String instrument)
/*     */   {
/* 111 */     return (Date)this.lastReceivedTimes.get(instrument);
/*     */   }
/*     */ 
/*     */   public void setMarketState(String instrument, CurrencyMarketWrapper marketState)
/*     */   {
/* 121 */     this.currencyMarkets.put(instrument, marketState);
/*     */   }
/*     */ 
/*     */   public void setInstrumentState(InstrumentStatusUpdateMessage state)
/*     */   {
/* 130 */     this.instrumentStates.put(state.getInstrument(), state);
/*     */   }
/*     */ 
/*     */   public InstrumentStatusUpdateMessage getInstrumentState(String instrument)
/*     */   {
/* 140 */     return (InstrumentStatusUpdateMessage)this.instrumentStates.get(instrument);
/*     */   }
/*     */ 
/*     */   public void cleanCurrencyMarkets() {
/* 144 */     this.currencyMarkets.clear();
/*     */   }
/*     */ 
/*     */   public void cleanCurrencyMarkets(Set<String> instruments) {
/* 148 */     for (String instrumentToClean : instruments)
/* 149 */       this.currencyMarkets.remove(instrumentToClean);
/*     */   }
/*     */ 
/*     */   public void cleanCurrencyMarketsExcept(Set<String> newSet)
/*     */   {
/* 154 */     Set instrumentsToRemove = new HashSet();
/* 155 */     for (String instrument : this.currencyMarkets.keySet()) {
/* 156 */       if (!newSet.contains(instrument)) {
/* 157 */         instrumentsToRemove.add(instrument);
/*     */       }
/*     */     }
/* 160 */     for (String instrument : instrumentsToRemove)
/* 161 */       this.currencyMarkets.remove(instrument);
/*     */   }
/*     */ 
/*     */   public Set<String> getAvailableInstruments()
/*     */   {
/* 170 */     Set resultSet = filterInstrumentsByState(new HashSet(), 0);
/* 171 */     return resultSet;
/*     */   }
/*     */ 
/*     */   public Set<String> getTemporaryBlockedInstruments() {
/* 175 */     Set resultSet = filterInstrumentsByState(new HashSet(), 1);
/* 176 */     return resultSet;
/*     */   }
/*     */ 
/*     */   public Set<String> getRestrictedInstruments()
/*     */   {
/* 183 */     Set resultSet = filterInstrumentsByState(new HashSet(), 2);
/* 184 */     return resultSet;
/*     */   }
/*     */ 
/*     */   private Set<String> filterInstrumentsByState(Set<String> targetSet, int filterCriterion) {
/* 188 */     for (String instrument : this.instrumentStates.keySet()) {
/* 189 */       InstrumentStatusUpdateMessage state = (InstrumentStatusUpdateMessage)this.instrumentStates.get(instrument);
/* 190 */       if ((state.getTradable() == filterCriterion) && 
/* 191 */         (InstrumentAvailabilityManager.getInstance().isAllowed(instrument))) {
/* 192 */         targetSet.add(instrument);
/*     */       }
/*     */     }
/* 195 */     return targetSet;
/*     */   }
/*     */ 
/*     */   public Set<String> getAllInstruments() {
/* 199 */     Set instrumentSet = new HashSet();
/* 200 */     for (String instrument : this.instrumentStates.keySet()) {
/* 201 */       instrumentSet.add(instrument);
/*     */     }
/* 203 */     return instrumentSet;
/*     */   }
/*     */ 
/*     */   public Map<String, CurrencyMarketWrapper> getCurrencyMarketSnapshot()
/*     */   {
/* 210 */     HashMap currentSnapshot = new HashMap();
/* 211 */     currentSnapshot.putAll(this.currencyMarkets);
/* 212 */     Set availableInstrumentSet = getAvailableInstruments();
/*     */ 
/* 214 */     currentSnapshot.keySet().retainAll(availableInstrumentSet);
/* 215 */     return currentSnapshot;
/*     */   }
/*     */ 
/*     */   public Instrument[] getActiveInstruments() {
/* 219 */     Set allInstrumentNames = getAllInstruments();
/* 220 */     Set temporaryBlockedInstrumentNames = getTemporaryBlockedInstruments();
/* 221 */     Set restrictedInstruments = getRestrictedInstruments();
/*     */ 
/* 223 */     allInstrumentNames.addAll(temporaryBlockedInstrumentNames);
/* 224 */     allInstrumentNames.removeAll(restrictedInstruments);
/*     */ 
/* 226 */     Set instruments = new HashSet();
/* 227 */     for (String instrumentName : allInstrumentNames) {
/* 228 */       Instrument instrument = Instrument.fromString(instrumentName);
/* 229 */       if (instrument != null) {
/* 230 */         instruments.add(instrument);
/*     */       }
/*     */     }
/*     */ 
/* 234 */     Instrument[] result = (Instrument[])instruments.toArray(new Instrument[instruments.size()]);
/*     */ 
/* 236 */     Arrays.sort(result, new Comparator() {
/*     */       public int compare(Instrument instrument1, Instrument instrument2) {
/* 238 */         return instrument1.name().compareTo(instrument2.name());
/*     */       }
/*     */     });
/* 242 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<String> fetchCurrenciesNeededForProfitlossCalculation(String selectedInstrument) {
/* 246 */     if (selectedInstrument == null) {
/* 247 */       return Collections.emptySet();
/*     */     }
/*     */ 
/* 250 */     String code = selectedInstrument.substring(4, 7);
/* 251 */     Currency currency = Currency.getInstance(code);
/* 252 */     Money moneyToConvert = new Money(new BigDecimal("1"), currency);
/*     */ 
/* 254 */     AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/*     */ 
/* 256 */     Currency accountCurrency = accountStatement.getLastAccountState().getCurrency();
/* 257 */     Set resultSet = CurrencyConverter.getConversionStringDeps(moneyToConvert.getCurrency(), accountCurrency);
/*     */ 
/* 259 */     return resultSet;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.model.MarketView
 * JD-Core Version:    0.6.0
 */