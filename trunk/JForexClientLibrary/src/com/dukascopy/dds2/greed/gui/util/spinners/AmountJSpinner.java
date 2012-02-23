/*     */ package com.dukascopy.dds2.greed.gui.util.spinners;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChangable;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import java.math.BigDecimal;
/*     */ import javax.swing.JSpinner;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AmountJSpinner extends CommonJSpinner
/*     */   implements LotAmountChangable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  19 */   private static final Logger LOGGER = LoggerFactory.getLogger(AmountJSpinner.class);
/*     */   protected BigDecimal currentLotAmount;
/*     */   protected Instrument currentInstrument;
/*     */ 
/*     */   public static AmountJSpinner getInstance()
/*     */   {
/*  25 */     return new AmountJSpinner(null);
/*     */   }
/*     */ 
/*     */   public static AmountJSpinner getInstance(Instrument instrument) {
/*  29 */     return new AmountJSpinner(instrument);
/*     */   }
/*     */ 
/*     */   protected AmountJSpinner(Instrument instrument) {
/*  33 */     super(LotAmountChanger.getDefaultAmountValue(instrument).doubleValue(), LotAmountChanger.getMinTradableAmount(instrument).doubleValue(), LotAmountChanger.getMaxTradableAmount(instrument).doubleValue(), LotAmountChanger.getAmountStepSize(instrument).doubleValue(), LotAmountChanger.getLotAmountForInstrument(instrument).intValue(), false, true);
/*     */ 
/*  41 */     LotAmountChanger.addChangable(this);
/*     */ 
/*  43 */     this.currentInstrument = (instrument != null ? instrument : LotAmountChanger.getSelectedInstrument());
/*  44 */     this.currentLotAmount = LotAmountChanger.getLotAmountForInstrument(this.currentInstrument);
/*     */ 
/*  46 */     super.setPrecision(LotAmountChanger.getAmountPrecision(this.currentInstrument, this.currentLotAmount));
/*     */ 
/*  48 */     if (((ClientSettingsStorage)GreedContext.get("settingsStorage")).isUserFirstLoading())
/*  49 */       setValue(LotAmountChanger.getMinTradableAmount(this.currentInstrument));
/*     */   }
/*     */ 
/*     */   public void setValue(Object value)
/*     */   {
/*     */     try
/*     */     {
/*  56 */       this.spinner.setValue(value);
/*     */     } catch (Exception e) {
/*  58 */       LOGGER.error(e.getMessage());
/*  59 */       this.spinner.setValue(LotAmountChanger.getDefaultAmount4CurrentLot());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void changeLot()
/*     */   {
/*  65 */     BigDecimal newLotAmount = LotAmountChanger.getLotAmountForInstrument(LotAmountChanger.getSelectedInstrument());
/*  66 */     BigDecimal newValue = LotAmountChanger.calculateAmountForDifferentLot((BigDecimal)getValue(), this.currentLotAmount, newLotAmount);
/*     */ 
/*  68 */     newValue = LotAmountChanger.getDefaultAmountValue(LotAmountChanger.getSelectedInstrument());
/*     */ 
/*  70 */     BigDecimal minValue = LotAmountChanger.getMinTradableAmount(LotAmountChanger.getSelectedInstrument());
/*     */ 
/*  72 */     setMinimum(minValue);
/*  73 */     setMaximum(LotAmountChanger.getMaxTradableAmount(LotAmountChanger.getSelectedInstrument()));
/*  74 */     setStepSize(LotAmountChanger.getAmountStepSizeForLot(LotAmountChanger.getSelectedInstrument(), newLotAmount));
/*  75 */     setPrecision(LotAmountChanger.getAmountPrecision(LotAmountChanger.getSelectedInstrument(), newLotAmount));
/*     */ 
/*  77 */     if (newValue.compareTo(minValue) == -1)
/*  78 */       setValue(minValue);
/*     */     else {
/*  80 */       setValue(newValue);
/*     */     }
/*     */ 
/*  83 */     this.currentLotAmount = newLotAmount;
/*  84 */     this.currentInstrument = LotAmountChanger.getSelectedInstrument();
/*     */   }
/*     */ 
/*     */   public void changeLot(BigDecimal newLotAmount) {
/*  88 */     BigDecimal newValue = LotAmountChanger.calculateAmountForDifferentLot((BigDecimal)getValue(), this.currentLotAmount, newLotAmount);
/*     */ 
/*  90 */     BigDecimal minValue = LotAmountChanger.getMinTradableAmountInMillions().multiply(GuiUtilsAndConstants.ONE_MILLION).divide(newLotAmount);
/*     */ 
/*  92 */     setMinimum(minValue);
/*  93 */     setMaximum(LotAmountChanger.getMaxTradableAmountInMillions().multiply(GuiUtilsAndConstants.ONE_MILLION).divide(newLotAmount));
/*  94 */     setStepSize(LotAmountChanger.getAmountStepSizeForLot(this.currentInstrument, newLotAmount));
/*  95 */     setPrecision(LotAmountChanger.getAmountPrecision(this.currentInstrument, newLotAmount));
/*     */ 
/*  97 */     if (newValue.compareTo(minValue) == -1)
/*  98 */       setValue(minValue);
/*     */     else {
/* 100 */       setValue(newValue);
/*     */     }
/*     */ 
/* 103 */     this.currentLotAmount = newLotAmount;
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmountValueInMillions(Instrument instrument) {
/* 107 */     return LotAmountChanger.calculateAmountForDifferentLot((BigDecimal)getValue(), LotAmountChanger.getLotAmountForInstrument(instrument), GuiUtilsAndConstants.ONE_MILLION);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.spinners.AmountJSpinner
 * JD-Core Version:    0.6.0
 */