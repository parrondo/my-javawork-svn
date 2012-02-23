/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.api.IContext;
/*     */ import com.dukascopy.api.IHistory;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.JFUtils;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Currency;
/*     */ import java.util.Formatter;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class JForexUtils
/*     */   implements JFUtils
/*     */ {
/*     */   private IHistory history;
/*     */   private IContext context;
/*     */ 
/*     */   public JForexUtils(IHistory history, IContext context)
/*     */   {
/*  72 */     this.history = history;
/*  73 */     this.context = context;
/*     */   }
/*     */ 
/*     */   public double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount) throws JFException
/*     */   {
/*  78 */     return convert(instrumentFrom, instrumentTo, amount, instrumentTo.getPipScale() + 1, null);
/*     */   }
/*     */ 
/*     */   public double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount, int decimalPlaces) throws JFException {
/*  82 */     return convert(instrumentFrom, instrumentTo, amount, decimalPlaces, null);
/*     */   }
/*     */ 
/*     */   public double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount, int decimalPlaces, OfferSide offerSide)
/*     */     throws JFException
/*     */   {
/*  89 */     if (!isSubscribed(instrumentFrom))
/*  90 */       throw new JFException("Unable to make the conversion. ".concat(instrumentFrom.toString()).concat(" is null or not subscribed."));
/*  91 */     if (!isSubscribed(instrumentTo))
/*  92 */       throw new JFException("Unable to make the conversion. ".concat(instrumentTo.toString()).concat(" is null or not subscribed."));
/*  93 */     if (amount <= 0.0D) {
/*  94 */       throw new JFException("Unable to make the conversion. Amount has to be greater than 0.");
/*     */     }
/*     */ 
/*  97 */     if (instrumentFrom.getSecondaryCurrency() == instrumentTo.getSecondaryCurrency()) {
/*  98 */       return decimalPlaces == -1 ? amount : round(amount, decimalPlaces);
/*     */     }
/*     */ 
/* 101 */     Map interInstrMap = new HashMap();
/* 102 */     interInstrMap.put(new DirectionSetting(true, true), new StringBuilder().append(instrumentFrom.getSecondaryCurrency()).append("/").append(instrumentTo.getPrimaryCurrency()).toString());
/* 103 */     interInstrMap.put(new DirectionSetting(false, true), new StringBuilder().append(instrumentFrom.getPrimaryCurrency()).append("/").append(instrumentTo.getPrimaryCurrency()).toString());
/* 104 */     interInstrMap.put(new DirectionSetting(true, false), new StringBuilder().append(instrumentFrom.getSecondaryCurrency()).append("/").append(instrumentTo.getSecondaryCurrency()).toString());
/* 105 */     interInstrMap.put(new DirectionSetting(false, false), new StringBuilder().append(instrumentFrom.getPrimaryCurrency()).append("/").append(instrumentTo.getSecondaryCurrency()).toString());
/*     */ 
/* 108 */     for (DirectionSetting directionSetting : interInstrMap.keySet()) {
/* 109 */       Double interPrice = Double.valueOf(getInterPrice((String)interInstrMap.get(directionSetting), offerSide));
/*     */ 
/* 111 */       if (!Double.isNaN(interPrice.doubleValue())) {
/* 112 */         double priceFromApplied = directionSetting.fromIsStraight ? 1.0D : 1.0D / getLastMarketPrice(instrumentFrom, offerSide);
/* 113 */         double priceToApplied = directionSetting.toIsStraight ? getLastMarketPrice(instrumentTo, offerSide) : 1.0D;
/* 114 */         double result = amount * priceFromApplied * interPrice.doubleValue() * priceToApplied;
/* 115 */         return decimalPlaces == -1 ? result : round(result, decimalPlaces);
/*     */       }
/*     */     }
/*     */ 
/* 119 */     throw new JFException("Unable to make the conversion. None of those are active subscribed instruments: ".concat(getInstrumentString(instrumentFrom, instrumentTo)).concat("."));
/*     */   }
/*     */ 
/*     */   public double convertPipToCurrency(Instrument instrument, Currency currency)
/*     */     throws JFException
/*     */   {
/* 127 */     return convertPipToCurrency(instrument, currency, null);
/*     */   }
/*     */ 
/*     */   public double convertPipToCurrency(Instrument instrument, Currency currency, OfferSide offerSide) throws JFException
/*     */   {
/* 132 */     if ((instrument == null) || (currency == null)) {
/* 133 */       throw new JFException(String.format("Parameters could not be null: Instrument=%s Currency=%s", new Object[] { instrument, currency }));
/*     */     }
/* 135 */     if (!isSubscribed(instrument)) {
/* 136 */       throw new JFException(String.format("Unable to make the conversion. %s is not subscribed.", new Object[] { instrument }));
/*     */     }
/*     */ 
/* 139 */     Set instruments = this.context.getSubscribedInstruments();
/* 140 */     Set currInstruments = new HashSet();
/*     */ 
/* 142 */     for (Instrument instr : instruments) {
/* 143 */       if (instr.getPrimaryCurrency() == currency) {
/* 144 */         currInstruments.add(instr);
/*     */       }
/* 146 */       if (instr.getSecondaryCurrency() == currency) {
/* 147 */         currInstruments.add(instr);
/*     */       }
/*     */     }
/*     */ 
/* 151 */     if (currInstruments.size() == 0) {
/* 152 */       throw new JFException(String.format("Unable to make the conversion. There are no subscirbed instruments containing %s.", new Object[] { currency }));
/*     */     }
/*     */ 
/* 155 */     for (Instrument currInstrument : currInstruments) {
/*     */       try
/*     */       {
/* 158 */         double currRate = convert(instrument, currInstrument, 1.0D, -1);
/* 159 */         if (currency == currInstrument.getPrimaryCurrency()) {
/* 160 */           currRate = 1.0D / currRate * getLastMarketPrice(currInstrument, null);
/*     */         }
/* 162 */         return instrument.getPipValue() * currRate;
/*     */       }
/*     */       catch (JFException e)
/*     */       {
/*     */       }
/*     */     }
/* 168 */     throw new JFException(String.format("Unable to make the conversion. There are no active, subscirbed instruments containing %s.", new Object[] { currency }));
/*     */   }
/*     */ 
/*     */   private double getInterPrice(String instrumentStr, OfferSide offerSide) throws JFException
/*     */   {
/* 173 */     if (instrumentStr.substring(0, 3).equalsIgnoreCase(instrumentStr.substring(4, 7))) {
/* 174 */       return 1.0D;
/*     */     }
/* 176 */     Instrument instrStraight = Instrument.fromString(instrumentStr);
/* 177 */     Instrument instrInverted = Instrument.fromInvertedString(instrumentStr);
/*     */ 
/* 179 */     if (instrStraight != null)
/* 180 */       return getLastMarketPrice(instrStraight, offerSide);
/* 181 */     if (instrInverted != null) {
/* 182 */       double price = getLastMarketPrice(instrInverted, offerSide);
/* 183 */       return Double.isNaN(price) ? (0.0D / 0.0D) : 1.0D / price;
/*     */     }
/*     */ 
/* 186 */     return (0.0D / 0.0D);
/*     */   }
/*     */ 
/*     */   private double round(double amount, int decimalPlaces) {
/* 190 */     return new BigDecimal(amount).setScale(decimalPlaces, 4).doubleValue();
/*     */   }
/*     */ 
/*     */   protected double getLastMarketPrice(Instrument instrument, OfferSide side) throws JFException
/*     */   {
/* 195 */     ITick lastTick = null;
/*     */ 
/* 198 */     if (!this.context.getSubscribedInstruments().contains(instrument)) {
/* 199 */       return (0.0D / 0.0D);
/*     */     }
/* 201 */     lastTick = this.history.getLastTick(instrument);
/*     */ 
/* 203 */     if (lastTick == null) {
/* 204 */       throw new JFException(new StringBuilder().append("Can't retrieve price for instrument ").append(instrument).toString());
/*     */     }
/*     */ 
/* 207 */     if (side == OfferSide.ASK)
/* 208 */       return lastTick.getAsk();
/* 209 */     if (side == OfferSide.BID) {
/* 210 */       return lastTick.getBid();
/*     */     }
/* 212 */     return (lastTick.getBid() + lastTick.getAsk()) / 2.0D;
/*     */   }
/*     */ 
/*     */   protected boolean isSubscribed(Instrument instrument)
/*     */   {
/* 217 */     if (instrument == null)
/* 218 */       return false;
/* 219 */     return this.context.getSubscribedInstruments().contains(instrument);
/*     */   }
/*     */ 
/*     */   private String getInstrumentString(Instrument instrument1, Instrument instrument2) {
/* 223 */     String str = "";
/* 224 */     String[] instrStrings = { new StringBuilder().append(instrument1.getPrimaryCurrency()).append("/").append(instrument2.getPrimaryCurrency()).toString(), new StringBuilder().append(instrument1.getPrimaryCurrency()).append("/").append(instrument2.getSecondaryCurrency()).toString(), new StringBuilder().append(instrument1.getSecondaryCurrency()).append("/").append(instrument2.getPrimaryCurrency()).toString(), new StringBuilder().append(instrument1.getSecondaryCurrency()).append("/").append(instrument2.getSecondaryCurrency()).toString() };
/*     */ 
/* 228 */     for (int i = 0; i < instrStrings.length; i++) {
/* 229 */       Instrument instrStraight = Instrument.fromString(instrStrings[i]);
/* 230 */       Instrument instrInverted = Instrument.fromInvertedString(instrStrings[i]);
/* 231 */       str = new StringBuilder().append(str).append(instrStraight == null ? "" : new StringBuilder().append(instrStraight.toString()).append(", ").toString()).append(instrInverted == null ? "" : new StringBuilder().append(instrInverted.toString()).append(", ").toString()).toString();
/*     */     }
/* 233 */     return str.length() > 0 ? str.substring(0, str.length() - 2) : "";
/*     */   }
/*     */ 
/*     */   private boolean subscribeInstrument(Instrument instrument)
/*     */   {
/* 243 */     if (!this.context.getSubscribedInstruments().contains(instrument))
/*     */     {
/* 245 */       this.context.setSubscribedInstruments(new HashSet(instrument)
/*     */       {
/*     */       });
/* 247 */       int i = 10;
/* 248 */       while (!this.context.getSubscribedInstruments().contains(instrument)) {
/*     */         try {
/* 250 */           Thread.sleep(100L);
/*     */         } catch (InterruptedException e) {
/*     */         }
/* 253 */         i--;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 258 */     return this.context.getSubscribedInstruments().contains(instrument);
/*     */   }
/*     */ 
/*     */   private double convertWithUsdAsMediator(Instrument instrumentFrom, Instrument instrumentTo, double amount, int decimalPlaces, OfferSide offerSide)
/*     */     throws JFException
/*     */   {
/* 270 */     double priceInterMedFrom = getInterPrice(new StringBuilder().append(instrumentFrom.getSecondaryCurrency()).append("/").append(Currency.getInstance("USD")).toString(), offerSide);
/* 271 */     double priceInterMedTo = getInterPrice(new StringBuilder().append(Currency.getInstance("USD")).append("/").append(instrumentTo.getPrimaryCurrency()).toString(), offerSide);
/*     */ 
/* 273 */     if ((Double.isNaN(priceInterMedFrom)) || (Double.isNaN(priceInterMedTo))) {
/* 274 */       Formatter formatter = new Formatter();
/* 275 */       formatter.format("Unable to make the conversion. There are no active subscribed instruments %1$s/%3$s, %1$s/%4$s, %2$s/%3$s, %2$s/%4$s or their inverted versions. Also there are no active subscribed instruments containing USD and both %2$s and %3$s.", new Object[] { instrumentFrom.getPrimaryCurrency(), instrumentFrom.getSecondaryCurrency(), instrumentTo.getPrimaryCurrency(), instrumentTo.getSecondaryCurrency() });
/*     */ 
/* 280 */       throw new JFException(formatter.toString());
/*     */     }
/*     */ 
/* 283 */     double priceTo = getLastMarketPrice(instrumentTo, offerSide);
/* 284 */     return round(amount * priceInterMedFrom * priceInterMedTo * priceTo, decimalPlaces);
/*     */   }
/*     */ 
/*     */   class DirectionSetting
/*     */   {
/*     */     private final boolean fromIsStraight;
/*     */     private final boolean toIsStraight;
/*     */ 
/*     */     DirectionSetting(boolean fromIsStraight, boolean toIsStraight)
/*     */     {
/*  28 */       this.fromIsStraight = fromIsStraight;
/*  29 */       this.toIsStraight = toIsStraight;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  37 */       int prime = 31;
/*  38 */       int result = 1;
/*  39 */       result = 31 * result + getOuterType().hashCode();
/*  40 */       result = 31 * result + (this.fromIsStraight ? 1231 : 1237);
/*  41 */       result = 31 * result + (this.toIsStraight ? 1231 : 1237);
/*  42 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/*  50 */       if (this == obj)
/*  51 */         return true;
/*  52 */       if (obj == null)
/*  53 */         return false;
/*  54 */       if (getClass() != obj.getClass())
/*  55 */         return false;
/*  56 */       DirectionSetting other = (DirectionSetting)obj;
/*  57 */       if (!getOuterType().equals(other.getOuterType()))
/*  58 */         return false;
/*  59 */       if (this.fromIsStraight != other.fromIsStraight) {
/*  60 */         return false;
/*     */       }
/*  62 */       return this.toIsStraight == other.toIsStraight;
/*     */     }
/*     */ 
/*     */     private JForexUtils getOuterType()
/*     */     {
/*  67 */       return JForexUtils.this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.JForexUtils
 * JD-Core Version:    0.6.0
 */