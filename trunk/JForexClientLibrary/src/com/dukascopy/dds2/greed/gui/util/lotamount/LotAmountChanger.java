/*     */ package com.dukascopy.dds2.greed.gui.util.lotamount;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.MathContext;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class LotAmountChanger
/*     */ {
/*  44 */   private static final BigDecimal MINIFX_LOWER_AMOUNT_LIMIT = BigDecimal.valueOf(0.001D);
/*  45 */   private static final BigDecimal LOWER_AMOUNT_LIMIT = BigDecimal.valueOf(0.01D);
/*  46 */   private static final BigDecimal CONTEST_LOWER_AMOUNT_LIMIT = BigDecimal.valueOf(0.1D);
/*  47 */   private static final BigDecimal MAX_TRADABLE_AMOUNT = BigDecimal.valueOf(10000L);
/*  48 */   private static final BigDecimal MAX_CONTEST_TRADABLE_AMOUNT = BigDecimal.valueOf(5L);
/*  49 */   private static final BigDecimal MAX_COMMODITY_TRADE_AMOUNT = BigDecimal.valueOf(10000000L);
/*     */ 
/*  51 */   private static final BigDecimal AMOUNT_STEP_SIZE_THUSANDS_LOT = BigDecimal.valueOf(10L);
/*  52 */   private static final BigDecimal AMOUNT_STEP_SIZE_MILLIONS_LOT = BigDecimal.valueOf(0.05D);
/*  53 */   private static final BigDecimal AMOUNT_STEP_SIZE_UNITS_LOT = BigDecimal.valueOf(1000L);
/*  54 */   private static final BigDecimal AMOUNT_STEP_SIZE_XAU_XAG_LOT = BigDecimal.valueOf(1L);
/*     */ 
/*  56 */   public static final BigDecimal AMOUNT_COMMODITY_PRECISION = BigDecimal.valueOf(0L);
/*  57 */   public static final BigDecimal AMOUNT_LOT_MIL_PRECISION = BigDecimal.valueOf(3L);
/*  58 */   public static final BigDecimal AMOUNT_LOT_TH_PRECISION = BigDecimal.valueOf(2L);
/*  59 */   public static final BigDecimal AMOUNT_LOT_UNIT_PRECISION = BigDecimal.valueOf(0L);
/*     */ 
/*  61 */   private static final List<WeakReference<LotAmountChangable>> CACHE = new ArrayList();
/*     */ 
/*     */   public static synchronized void addChangable(LotAmountChangable lotChangable) {
/*  64 */     CACHE.add(new WeakReference(lotChangable));
/*     */   }
/*     */ 
/*     */   public static synchronized void doChangeLotAmount()
/*     */   {
/*  69 */     for (WeakReference amountSpinner : CACHE)
/*  70 */       if (amountSpinner.get() != null)
/*  71 */         ((LotAmountChangable)amountSpinner.get()).changeLot();
/*     */   }
/*     */ 
/*     */   public static void clearCache()
/*     */   {
/*  76 */     CACHE.clear();
/*     */   }
/*     */ 
/*     */   public static BigDecimal calculateAmountForDifferentLot(BigDecimal amount, BigDecimal lotFrom, BigDecimal lotTo) {
/*  80 */     return amount == null ? null : amount.multiply(lotFrom).divide(lotTo);
/*     */   }
/*     */ 
/*     */   public static BigDecimal getCurrentLotAmount()
/*     */   {
/*  88 */     return getLotAmountForInstrument(getSelectedInstrument());
/*     */   }
/*     */ 
/*     */   public static BigDecimal getLotAmountForInstrument(Instrument instrument) {
/*  92 */     return isCommodity(instrument) ? GuiUtilsAndConstants.ONE : ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreAmountLot();
/*     */   }
/*     */ 
/*     */   public static BigDecimal getAmountStepSize(Instrument instrument)
/*     */   {
/*  98 */     return getAmountStepSizeForLot(instrument, null);
/*     */   }
/*     */ 
/*     */   public static BigDecimal getAmountStepSizeForLot(Instrument instrument, BigDecimal newLot) {
/* 102 */     BigDecimal currentLot = newLot == null ? getLotAmountForInstrument(instrument) : newLot;
/*     */ 
/* 104 */     Instrument currentInstrument = instrument == null ? getSelectedInstrument() : instrument;
/* 105 */     if (isCommodity(currentInstrument)) {
/* 106 */       return AMOUNT_STEP_SIZE_XAU_XAG_LOT;
/*     */     }
/*     */ 
/* 109 */     if (GuiUtilsAndConstants.ONE_MILLION.equals(currentLot)) return AMOUNT_STEP_SIZE_MILLIONS_LOT;
/* 110 */     if (GuiUtilsAndConstants.ONE_THUSAND.equals(currentLot)) return AMOUNT_STEP_SIZE_THUSANDS_LOT;
/* 111 */     if (GuiUtilsAndConstants.ONE.equals(currentLot)) return AMOUNT_STEP_SIZE_UNITS_LOT;
/* 112 */     return AMOUNT_STEP_SIZE_MILLIONS_LOT;
/*     */   }
/*     */ 
/*     */   public static BigDecimal getDefaultAmount4CurrentLot()
/*     */   {
/* 118 */     return getMinTradableAmount().multiply(GuiUtilsAndConstants.ONE_MILLION).divide(getLotAmountForInstrument(Instrument.EURUSD));
/*     */   }
/*     */ 
/*     */   public static BigDecimal getDefaultAmountValue(Instrument instrument)
/*     */   {
/* 123 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 124 */     Instrument currentInstrument = instrument == null ? getSelectedInstrument() : instrument;
/*     */ 
/* 126 */     if (storage != null) {
/* 127 */       if (Instrument.XAUUSD.equals(currentInstrument))
/* 128 */         return storage.restoreDefaultXAUAmount();
/* 129 */       if (Instrument.XAGUSD.equals(currentInstrument)) {
/* 130 */         return storage.restoreDefaultXAGAmount();
/*     */       }
/* 132 */       return storage.restoreDefaultAmount();
/*     */     }
/*     */ 
/* 135 */     return BigDecimal.ZERO;
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMinTradableAmount()
/*     */   {
/* 142 */     return getMinTradableAmount(null);
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMinTradableAmount(Instrument instrument)
/*     */   {
/* 149 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 150 */     BigDecimal result = null;
/* 151 */     if (GreedContext.isContest())
/* 152 */       result = CONTEST_LOWER_AMOUNT_LIMIT;
/* 153 */     else if (GreedContext.isMiniFxAccount())
/* 154 */       result = MINIFX_LOWER_AMOUNT_LIMIT;
/*     */     else {
/* 156 */       result = LOWER_AMOUNT_LIMIT;
/*     */     }
/*     */ 
/* 159 */     Instrument currentInstrument = instrument == null ? getSelectedInstrument() : instrument;
/* 160 */     if (Instrument.XAUUSD.equals(currentInstrument))
/* 161 */       return GuiUtilsAndConstants.ONE;
/* 162 */     if (Instrument.XAGUSD.equals(currentInstrument)) {
/* 163 */       return GuiUtilsAndConstants.MIN_XAG_AMOUNT;
/*     */     }
/*     */ 
/* 166 */     return result.multiply(GuiUtilsAndConstants.ONE_MILLION).divide(storage.restoreAmountLot()).round(new MathContext(4));
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMinTradableAmountInUnits(Instrument instrument)
/*     */   {
/* 174 */     if (Instrument.XAUUSD.equals(instrument))
/* 175 */       return GuiUtilsAndConstants.ONE;
/* 176 */     if (Instrument.XAGUSD.equals(instrument)) {
/* 177 */       return GuiUtilsAndConstants.MIN_XAG_AMOUNT;
/*     */     }
/*     */ 
/* 180 */     return getMinTradableAmountInUnits();
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMinTradableAmountInUnits() {
/* 184 */     BigDecimal result = null;
/* 185 */     if (GreedContext.isContest())
/* 186 */       result = CONTEST_LOWER_AMOUNT_LIMIT;
/* 187 */     else if (GreedContext.isMiniFxAccount())
/* 188 */       result = MINIFX_LOWER_AMOUNT_LIMIT;
/*     */     else {
/* 190 */       result = LOWER_AMOUNT_LIMIT;
/*     */     }
/*     */ 
/* 193 */     return result.multiply(GuiUtilsAndConstants.ONE_MILLION).round(new MathContext(4));
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMinTradableAmountInMillions() {
/* 197 */     BigDecimal result = null;
/* 198 */     if (GreedContext.isContest())
/* 199 */       result = CONTEST_LOWER_AMOUNT_LIMIT;
/* 200 */     else if (GreedContext.isMiniFxAccount())
/* 201 */       result = MINIFX_LOWER_AMOUNT_LIMIT;
/*     */     else {
/* 203 */       result = LOWER_AMOUNT_LIMIT;
/*     */     }
/*     */ 
/* 206 */     return result;
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMaxTradableAmount()
/*     */   {
/* 211 */     return getMaxTradableAmount(null);
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMaxTradableAmount(Instrument instrument)
/*     */   {
/* 216 */     if (isCommodity(instrument)) {
/* 217 */       return MAX_COMMODITY_TRADE_AMOUNT;
/*     */     }
/* 219 */     BigDecimal maxTradableAmount = getDefaultMaxTradableAmount().multiply(GuiUtilsAndConstants.ONE_MILLION);
/* 220 */     return maxTradableAmount.divide(((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreAmountLot());
/*     */   }
/*     */ 
/*     */   public static BigDecimal getMaxTradableAmountInMillions()
/*     */   {
/* 225 */     return getDefaultMaxTradableAmount();
/*     */   }
/*     */ 
/*     */   private static BigDecimal getDefaultMaxTradableAmount() {
/* 229 */     if (GreedContext.isContest()) {
/* 230 */       return MAX_CONTEST_TRADABLE_AMOUNT;
/*     */     }
/* 232 */     return MAX_TRADABLE_AMOUNT;
/*     */   }
/*     */ 
/*     */   public static BigDecimal getAmountStepSizeInMillions() {
/* 236 */     if (isCommodity(getSelectedInstrument())) {
/* 237 */       return AMOUNT_STEP_SIZE_XAU_XAG_LOT;
/*     */     }
/* 239 */     return AMOUNT_STEP_SIZE_MILLIONS_LOT;
/*     */   }
/*     */ 
/*     */   public static BigDecimal getAmountPrecision(Instrument instr, BigDecimal newLotAmount) {
/* 243 */     if (isCommodity(instr))
/* 244 */       return AMOUNT_COMMODITY_PRECISION;
/* 245 */     if (GuiUtilsAndConstants.ONE_MILLION.equals(newLotAmount)) return AMOUNT_LOT_MIL_PRECISION;
/* 246 */     if (GuiUtilsAndConstants.ONE_THUSAND.equals(newLotAmount)) return AMOUNT_LOT_TH_PRECISION;
/* 247 */     if (GuiUtilsAndConstants.ONE.equals(newLotAmount)) return AMOUNT_LOT_UNIT_PRECISION;
/* 248 */     return AMOUNT_LOT_MIL_PRECISION;
/*     */   }
/*     */ 
/*     */   public static Instrument getSelectedInstrument()
/*     */   {
/* 253 */     if ((ClientForm)GreedContext.get("clientGui") != null) {
/* 254 */       return Instrument.fromString(((ClientForm)GreedContext.get("clientGui")).getDealPanel().getSelectedInstrument());
/*     */     }
/* 256 */     return Instrument.EURUSD;
/*     */   }
/*     */ 
/*     */   public static boolean isCommodity(Instrument instrument)
/*     */   {
/* 261 */     return (Instrument.XAUUSD.equals(instrument)) || (Instrument.XAGUSD.equals(instrument));
/*     */   }
/*     */ 
/*     */   public static boolean isSelectedInstrCommodity() {
/* 265 */     return isCommodity(getSelectedInstrument());
/*     */   }
/*     */ 
/*     */   public static enum AmountLot
/*     */   {
/*  21 */     MILLIONS(GuiUtilsAndConstants.ONE_MILLION), 
/*  22 */     THOUSANDS(GuiUtilsAndConstants.ONE_THUSAND), 
/*  23 */     UNITS(GuiUtilsAndConstants.ONE);
/*     */ 
/*     */     public final BigDecimal mode;
/*     */ 
/*  27 */     private AmountLot(BigDecimal mode) { this.mode = mode; }
/*     */ 
/*     */     public static AmountLot fromValue(BigDecimal value)
/*     */     {
/*  31 */       for (AmountLot amountLot : values()) {
/*  32 */         if (amountLot.value().compareTo(value) == 0) {
/*  33 */           return amountLot;
/*     */         }
/*     */       }
/*  36 */       return null;
/*     */     }
/*     */ 
/*     */     public BigDecimal value() {
/*  40 */       return this.mode;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger
 * JD-Core Version:    0.6.0
 */