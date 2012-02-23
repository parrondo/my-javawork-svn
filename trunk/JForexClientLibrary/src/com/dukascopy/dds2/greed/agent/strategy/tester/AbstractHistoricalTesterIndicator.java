/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.indicators.IndicatorResult;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo;
/*     */ import com.dukascopy.api.indicators.InputParameterInfo.Type;
/*     */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractHistoricalTesterIndicator extends AbstractTesterIndicator
/*     */ {
/*  22 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHistoricalTesterIndicator.class);
/*     */   private StrategyDataStorageImpl indicatorStorage;
/*     */   private StrategyDataStorageImpl.TesterIndicatorData initialElement;
/*     */   private IBar[] inputBars;
/*     */   protected double[] calculatedValues;
/*  35 */   private long firstCachedIndex = -1L;
/*  36 */   private long lastCachedIndex = -1L;
/*     */   private static final int CACHE_MAX_LENGTH = 2000;
/*  38 */   private StrategyDataStorageImpl.TesterIndicatorData[] cacheData = new StrategyDataStorageImpl.TesterIndicatorData[2000];
/*  39 */   private int cacheSize = 0;
/*     */   protected String name;
/*     */ 
/*     */   public AbstractHistoricalTesterIndicator(long initialTime, double deposit)
/*     */   {
/*  52 */     this.initialElement = new StrategyDataStorageImpl.TesterIndicatorData(initialTime, deposit, 0.0D, deposit);
/*     */ 
/*  54 */     prepareNameTitle();
/*  55 */     configure();
/*     */   }
/*     */ 
/*     */   public StrategyDataStorageImpl getIndicatorStorage()
/*     */   {
/*  60 */     return this.indicatorStorage;
/*     */   }
/*     */ 
/*     */   public void setIndicatorStorage(StrategyDataStorageImpl indicatorStorage) {
/*  64 */     this.indicatorStorage = indicatorStorage;
/*     */   }
/*     */ 
/*     */   protected abstract void prepareNameTitle();
/*     */ 
/*     */   protected String getName()
/*     */   {
/*  77 */     return this.name;
/*     */   }
/*     */ 
/*     */   protected String getTitle()
/*     */   {
/*  85 */     return this.name;
/*     */   }
/*     */ 
/*     */   protected InputParameterInfo[] createInputParamsInfo()
/*     */   {
/* 102 */     return new InputParameterInfo[] { new InputParameterInfo("Bar", InputParameterInfo.Type.BAR) };
/*     */   }
/*     */ 
/*     */   protected OptInputParameterInfo[] createOptionalParamsInfo()
/*     */   {
/* 109 */     return new OptInputParameterInfo[0];
/*     */   }
/*     */ 
/*     */   public void setInputParameter(int index, Object array)
/*     */   {
/* 115 */     switch (index) {
/*     */     case 0:
/* 117 */       this.inputBars = ((IBar[])(IBar[])array);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOptInputParameter(int index, Object value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setOutputParameter(int index, Object array)
/*     */   {
/* 128 */     this.calculatedValues = ((double[])(double[])array);
/*     */   }
/*     */ 
/*     */   public IndicatorResult calculate(int startIndex, int endIndex)
/*     */   {
/* 137 */     int outputIndex = 0;
/* 138 */     for (int inputIndex = startIndex; inputIndex <= endIndex; inputIndex++) {
/* 139 */       long time = this.inputBars[inputIndex].getTime();
/*     */ 
/* 141 */       StrategyDataStorageImpl.TesterIndicatorData data = getIndicatorData(time);
/* 142 */       if (this.calculatedValues != null) {
/* 143 */         this.calculatedValues[outputIndex] = getIndicatorDataValue(data);
/*     */       }
/*     */ 
/* 146 */       outputIndex++;
/*     */     }
/*     */ 
/* 149 */     return new IndicatorResult(startIndex, outputIndex);
/*     */   }
/*     */   protected abstract double getIndicatorDataValue(StrategyDataStorageImpl.TesterIndicatorData paramTesterIndicatorData);
/*     */ 
/*     */   private StrategyDataStorageImpl.TesterIndicatorData getIndicatorData(long time) {
/* 155 */     StrategyDataStorageImpl dataStorage = getIndicatorStorage();
/* 156 */     if ((dataStorage == null) || (time <= this.initialElement.time)) {
/* 157 */       return this.initialElement;
/*     */     }
/*     */ 
/* 160 */     StrategyDataStorageImpl.TesterIndicatorData result = dataStorage.getCachedData(time);
/* 161 */     if (result != null) {
/* 162 */       return result;
/*     */     }
/*     */ 
/* 166 */     return findInCache(dataStorage, time);
/*     */   }
/*     */ 
/*     */   private StrategyDataStorageImpl.TesterIndicatorData findInCache(StrategyDataStorageImpl dataStorage, long time)
/*     */   {
/* 173 */     if (this.cacheSize < 1) {
/* 174 */       fillCache(0, dataStorage, this.initialElement, 0L, 9223372036854775807L, 2000);
/*     */     }
/*     */ 
/*     */     while (true)
/*     */     {
/* 183 */       StrategyDataStorageImpl.TesterIndicatorData first = this.cacheData[0];
/* 184 */       StrategyDataStorageImpl.TesterIndicatorData last = this.cacheData[(this.cacheSize - 1)];
/*     */ 
/* 186 */       if (first.time == time) {
/* 187 */         return first;
/*     */       }
/* 189 */       if (last.time == time) {
/* 190 */         return last;
/*     */       }
/* 192 */       if ((first.time < time) && (time < last.time)) {
/* 193 */         StrategyDataStorageImpl.TesterIndicatorData key = new StrategyDataStorageImpl.TesterIndicatorData(time, 0.0D, 0.0D, 0.0D);
/*     */ 
/* 195 */         int index = Arrays.binarySearch(this.cacheData, key);
/* 196 */         if (index < 0)
/*     */         {
/* 198 */           index = -index - 2;
/*     */         }
/* 200 */         else index -= 1;
/*     */ 
/* 202 */         return this.cacheData[index];
/*     */       }
/*     */ 
/* 208 */       this.cacheSize = 0;
/* 209 */       if (time < first.time)
/*     */       {
/* 211 */         if ((this.firstCachedIndex < 1L) || (this.lastCachedIndex < 0L))
/*     */         {
/* 214 */           return this.initialElement;
/*     */         }
/*     */ 
/* 218 */         double blockDuration = dataStorage.getDataPeriod() * 2000.0D;
/* 219 */         long numberOfBlocks = (first.time - time) / ()blockDuration;
/* 220 */         if (numberOfBlocks <= 1L)
/*     */         {
/* 223 */           long fileIndex = this.firstCachedIndex - 2000L + 1L;
/* 224 */           if (fileIndex < 0L) {
/* 225 */             fileIndex = 0L;
/*     */           }
/* 227 */           fillCache(0, dataStorage, first, fileIndex, time, 2000);
/*     */         }
/*     */         else
/*     */         {
/* 234 */           long fileIndex = this.firstCachedIndex - 2000L * numberOfBlocks;
/* 235 */           if (fileIndex < 0L) {
/* 236 */             fileIndex = 0L;
/*     */           }
/* 238 */           fillCache(0, dataStorage, first, fileIndex, time, 2000);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 243 */         this.cacheData[0] = last;
/* 244 */         this.cacheSize += 1;
/* 245 */         fillCache(1, dataStorage, last, 1999);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fillCache(int fromIndex, StrategyDataStorageImpl dataStorage, StrategyDataStorageImpl.TesterIndicatorData defaultData, int maxCount)
/*     */   {
/* 252 */     fillCache(fromIndex, dataStorage, defaultData, this.lastCachedIndex + 1L, 9223372036854775807L, maxCount);
/*     */   }
/*     */ 
/*     */   private void fillCache(int fromIndex, StrategyDataStorageImpl dataStorage, StrategyDataStorageImpl.TesterIndicatorData defaultData, long filePosition, long maxTime, int maxCount) {
/*     */     try {
/* 257 */       int count = dataStorage.fillCache(this.cacheData, fromIndex, filePosition, maxTime, maxCount);
/* 258 */       this.cacheSize = (fromIndex + count);
/* 259 */       this.firstCachedIndex = filePosition;
/* 260 */       this.lastCachedIndex = (filePosition + count);
/*     */ 
/* 262 */       if (count < 1)
/*     */       {
/* 264 */         LOGGER.debug("No cache data is read from position " + filePosition, new Throwable());
/* 265 */         this.cacheData[0] = new StrategyDataStorageImpl.TesterIndicatorData(0L, defaultData.balance, defaultData.profitLoss, defaultData.equity);
/* 266 */         this.cacheData[1] = new StrategyDataStorageImpl.TesterIndicatorData(9223372036854775807L, defaultData.balance, defaultData.profitLoss, defaultData.equity);
/* 267 */         this.cacheSize = 2;
/* 268 */         this.lastCachedIndex = -1L;
/*     */       }
/*     */     }
/*     */     catch (IOException ex) {
/* 272 */       LOGGER.error("Error reading data from cache.", ex);
/* 273 */       this.cacheData[0] = new StrategyDataStorageImpl.TesterIndicatorData(0L, defaultData.balance, defaultData.profitLoss, defaultData.equity);
/* 274 */       this.cacheData[1] = new StrategyDataStorageImpl.TesterIndicatorData(9223372036854775807L, defaultData.balance, defaultData.profitLoss, defaultData.equity);
/* 275 */       this.cacheSize = 2;
/* 276 */       this.lastCachedIndex = -1L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public double[] getMinMax(int outputIdx, Object values, int firstVisibleValueIndex, int lastVisibleValueIndex)
/*     */   {
/* 283 */     return getMinMax((double[])(double[])values, firstVisibleValueIndex, lastVisibleValueIndex);
/*     */   }
/*     */ 
/*     */   public AbstractHistoricalTesterIndicator clone()
/*     */   {
/* 288 */     AbstractHistoricalTesterIndicator result = (AbstractHistoricalTesterIndicator)super.clone();
/* 289 */     result.setIndicatorStorage(getIndicatorStorage());
/* 290 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.AbstractHistoricalTesterIndicator
 * JD-Core Version:    0.6.0
 */