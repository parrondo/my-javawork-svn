/*     */ package com.dukascopy.charts.math.indicators;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class IndicatorsFilter
/*     */ {
/*   8 */   private static final Map<String, String> titles = new HashMap();
/*   9 */   private static final Map<String, String[]> inputParams = new HashMap();
/*  10 */   private static final Map<String, String[]> optInputParams = new HashMap();
/*  11 */   private static final Map<String, String[]> outputParams = new HashMap();
/*     */ 
/*     */   public static String getTitle(String name)
/*     */   {
/* 185 */     return (String)titles.get(name);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  14 */     titles.put("ACOS", "Vector Trigonometric ACos");
/*     */ 
/*  16 */     titles.put("AD", "Chaikin A/D Line");
/*  17 */     inputParams.put("AD", new String[] { "param1" });
/*  18 */     optInputParams.put("AD", new String[] { "optParam1" });
/*  19 */     outputParams.put("AD", new String[] { "outputParam1" });
/*     */ 
/*  21 */     titles.put("ADD", "Vector Arithmetic Add");
/*     */ 
/*  23 */     titles.put("ADOSC", "Chaikin A/D Oscillator");
/*  24 */     inputParams.put("ADOSC", new String[0]);
/*  25 */     optInputParams.put("ADOSC", new String[0]);
/*  26 */     outputParams.put("ADOSC", new String[] { "outputParam1" });
/*     */ 
/*  28 */     titles.put("ADX", "Average Directional Movement Index");
/*  29 */     titles.put("ADXR", "Average Directional Movement Index Rating");
/*  30 */     titles.put("APO", "Absolute Price Oscillator");
/*  31 */     titles.put("AROON", "Aroon");
/*  32 */     titles.put("AROONOSC", "Aroon Oscillator");
/*  33 */     titles.put("ASIN", "Vector Trigonometric ASin");
/*  34 */     titles.put("ATAN", "Vector Trigonometric ATan");
/*  35 */     titles.put("ATR", "Average True Range");
/*  36 */     titles.put("AVGPRICE", "Average Price");
/*  37 */     titles.put("BBANDS", "Bollinger Bands");
/*  38 */     titles.put("BETA", "Beta");
/*  39 */     titles.put("BOP", "Balance Of Power");
/*  40 */     titles.put("CCI", "Commodity Channel Index");
/*  41 */     titles.put("CDL2CROWS", "Two Crows");
/*  42 */     titles.put("CDL3BLACKCROWS", "Three Black Crows");
/*  43 */     titles.put("CDL3INSIDE", "Three Inside Up/Down");
/*  44 */     titles.put("CDL3LINESTRIKE", "Three-Line Strike ");
/*  45 */     titles.put("CDL3OUTSIDE", "Three Outside Up/Down");
/*  46 */     titles.put("CDL3STARSINSOUTH", "Three Stars In The South");
/*  47 */     titles.put("CDL3WHITESOLDIERS", "Three Advancing White Soldiers");
/*  48 */     titles.put("CDLABANDONEDBABY", "Abandoned Baby");
/*  49 */     titles.put("CDLADVANCEBLOCK", "Advance Block");
/*  50 */     titles.put("CDLBELTHOLD", "Belt-hold");
/*  51 */     titles.put("CDLBREAKAWAY", "Breakaway");
/*  52 */     titles.put("CDLCLOSINGMARUBOZU", "Closing Marubozu");
/*  53 */     titles.put("CDLCONCEALBABYSWALL", "Concealing Baby Swallow");
/*  54 */     titles.put("CDLCOUNTERATTACK", "Counterattack");
/*  55 */     titles.put("CDLDARKCLOUDCOVER", "Dark Cloud Cover");
/*  56 */     titles.put("CDLDOJI", "Doji");
/*  57 */     titles.put("CDLDOJISTAR", "Doji Star");
/*  58 */     titles.put("CDLDRAGONFLYDOJI", "Dragonfly Doji");
/*  59 */     titles.put("CDLENGULFING", "Engulfing Pattern");
/*  60 */     titles.put("CDLEVENINGDOJISTAR", "Evening Doji Star");
/*  61 */     titles.put("CDLEVENINGSTAR", "Evening Star");
/*  62 */     titles.put("CDLGAPSIDESIDEWHITE", "Up/Down-gap side-by-side white lines");
/*  63 */     titles.put("CDLGRAVESTONEDOJI", "Gravestone Doji");
/*  64 */     titles.put("CDLHAMMER", "Hammer");
/*  65 */     titles.put("CDLHANGINGMAN", "Hanging Man");
/*  66 */     titles.put("CDLHARAMI", "Harami Pattern");
/*  67 */     titles.put("CDLHARAMICROSS", "Harami Cross Pattern");
/*  68 */     titles.put("CDLHIGHWAVE", "High-Wave Candle");
/*  69 */     titles.put("CDLHIKKAKE", "Hikkake Pattern");
/*  70 */     titles.put("CDLHIKKAKEMOD", "Modified Hikkake Pattern");
/*  71 */     titles.put("CDLHOMINGPIGEON", "Homing Pigeon");
/*  72 */     titles.put("CDLIDENTICAL3CROWS", "Identical Three Crows");
/*  73 */     titles.put("CDLINNECK", "In-Neck Pattern");
/*  74 */     titles.put("CDLINVERTEDHAMMER", "Inverted Hammer");
/*  75 */     titles.put("CDLKICKING", "Kicking");
/*  76 */     titles.put("CDLKICKINGBYLENGTH", "Kicking - bull/bear determined by the longer marubozu");
/*  77 */     titles.put("CDLLADDERBOTTOM", "Ladder Bottom");
/*  78 */     titles.put("CDLLONGLEGGEDDOJI", "Long Legged Doji");
/*  79 */     titles.put("CDLLONGLINE", "Long Line Candle");
/*  80 */     titles.put("CDLMARUBOZU", "Marubozu");
/*  81 */     titles.put("CDLMATCHINGLOW", "Matching Low");
/*  82 */     titles.put("CDLMATHOLD", "Mat Hold");
/*  83 */     titles.put("CDLMORNINGDOJISTAR", "Morning Doji Star");
/*  84 */     titles.put("CDLMORNINGSTAR", "Morning Star");
/*  85 */     titles.put("CDLONNECK", "On-Neck Pattern");
/*  86 */     titles.put("CDLPIERCING", "Piercing Pattern");
/*  87 */     titles.put("CDLRICKSHAWMAN", "Rickshaw Man");
/*  88 */     titles.put("CDLRISEFALL3METHODS", "Rising/Falling Three Methods");
/*  89 */     titles.put("CDLSEPARATINGLINES", "Separating Lines");
/*  90 */     titles.put("CDLSHOOTINGSTAR", "Shooting Star");
/*  91 */     titles.put("CDLSHORTLINE", "Short Line Candle");
/*  92 */     titles.put("CDLSPINNINGTOP", "Spinning Top");
/*  93 */     titles.put("CDLSTALLEDPATTERN", "Stalled Pattern");
/*  94 */     titles.put("CDLSTICKSANDWICH", "Stick Sandwich");
/*  95 */     titles.put("CDLTAKURI", "Takuri (Dragonfly Doji with very long lower shadow)");
/*  96 */     titles.put("CDLTASUKIGAP", "Tasuki Gap");
/*  97 */     titles.put("CDLTHRUSTING", "Thrusting Pattern");
/*  98 */     titles.put("CDLTRISTAR", "Tristar Pattern");
/*  99 */     titles.put("CDLUNIQUE3RIVER", "Unique 3 River");
/* 100 */     titles.put("CDLUPSIDEGAP2CROWS", "Upside Gap Two Crows");
/* 101 */     titles.put("CDLXSIDEGAP3METHODS", "Upside/Downside Gap Three Methods");
/* 102 */     titles.put("CEIL", "Vector Ceil");
/* 103 */     titles.put("CMO", "Chande Momentum Oscillator");
/* 104 */     titles.put("CORREL", "Pearson's Correlation Coefficient (r)");
/* 105 */     titles.put("COS", "Vector Trigonometric Cos");
/* 106 */     titles.put("COSH", "Vector Trigonometric Cosh");
/* 107 */     titles.put("DEMA", "Double Exponential Moving Average");
/* 108 */     titles.put("DIV", "Vector Arithmetic Div");
/* 109 */     titles.put("DX", "Directional Movement Index");
/* 110 */     titles.put("EMA", "Exponential Moving Average");
/* 111 */     titles.put("EXP", "Vector Arithmetic Exp");
/* 112 */     titles.put("FLOOR", "Vector Floor");
/* 113 */     titles.put("HT_DCPERIOD", "Hilbert Transform - Dominant Cycle Period");
/* 114 */     titles.put("HT_DCPHASE", "Hilbert Transform - Dominant Cycle Phase");
/* 115 */     titles.put("HT_PHASOR", "Hilbert Transform - Phasor Components");
/* 116 */     titles.put("HT_SINE", "Hilbert Transform - SineWave");
/* 117 */     titles.put("HT_TRENDLINE", "Hilbert Transform - Instantaneous Trendline");
/* 118 */     titles.put("HT_TRENDMODE", "Hilbert Transform - Trend vs Cycle Mode");
/* 119 */     titles.put("KAMA", "Kaufman Adaptive Moving Average");
/* 120 */     titles.put("LINEARREG", "Linear Regression");
/* 121 */     titles.put("LINEARREG_ANGLE", "Linear Regression Angle");
/* 122 */     titles.put("LINEARREG_INTERCEPT", "Linear Regression Intercept");
/* 123 */     titles.put("LINEARREG_SLOPE", "Linear Regression Slope");
/* 124 */     titles.put("LN", "Vector Log Natural");
/* 125 */     titles.put("LOG10", "Vector Log10");
/* 126 */     titles.put("MA", "Moving average");
/* 127 */     titles.put("MACD", "Moving Average Convergence/Divergence");
/* 128 */     titles.put("MACDEXT", "MACD with controllable MA type");
/* 129 */     titles.put("MACDFIX", "Moving Average Convergence/Divergence Fix 12/26");
/* 130 */     titles.put("MAMA", "MESA Adaptive Moving Average");
/* 131 */     titles.put("MAVP", "Moving average with variable period");
/* 132 */     titles.put("MAX", "Highest value over a specified period");
/*     */ 
/* 134 */     titles.put("MEDPRICE", "Median Price");
/* 135 */     titles.put("MFI", "Money Flow Index");
/* 136 */     titles.put("MIDPOINT", "MidPoint over period");
/* 137 */     titles.put("MIDPRICE", "Midpoint Price over period");
/* 138 */     titles.put("MIN", "Lowest value over a specified period");
/*     */ 
/* 140 */     titles.put("MINMAX", "Lowest and highest values over a specified period");
/*     */ 
/* 142 */     titles.put("MINUS_DI", "Minus Directional Indicator");
/* 143 */     titles.put("MINUS_DM", "Minus Directional Movement");
/* 144 */     titles.put("MOM", "Momentum");
/* 145 */     titles.put("MULT", "Vector Arithmetic Mult");
/* 146 */     titles.put("NATR", "Normalized Average True Range");
/* 147 */     titles.put("OBV", "On Balance Volume");
/* 148 */     titles.put("PLUS_DI", "Plus Directional Indicator");
/* 149 */     titles.put("PLUS_DM", "Plus Directional Movement");
/* 150 */     titles.put("PPO", "Percentage Price Oscillator");
/* 151 */     titles.put("ROC", "Rate of change : ((price/prevPrice)-1)*100");
/* 152 */     titles.put("ROCP", "Rate of change Percentage: (price-prevPrice)/prevPrice");
/* 153 */     titles.put("ROCR", "Rate of change ratio: (price/prevPrice)");
/* 154 */     titles.put("ROCR100", "Rate of change ratio 100 scale: (price/prevPrice)*100");
/* 155 */     titles.put("RSI", "Relative Strength Index");
/* 156 */     titles.put("SAR", "Parabolic SAR");
/* 157 */     titles.put("SAREXT", "Parabolic SAR - Extended");
/* 158 */     titles.put("SIN", "Vector Trigonometric Sin");
/* 159 */     titles.put("SINH", "Vector Trigonometric Sinh");
/* 160 */     titles.put("SMA", "Simple Moving Average");
/* 161 */     titles.put("SQRT", "Vector Square Root");
/* 162 */     titles.put("STDDEV", "Standard Deviation");
/* 163 */     titles.put("STOCH", "Stochastic");
/* 164 */     titles.put("STOCHF", "Stochastic Fast");
/* 165 */     titles.put("STOCHRSI", "Stochastic Relative Strength Index");
/* 166 */     titles.put("SUB", "Vector Arithmetic Substraction");
/* 167 */     titles.put("SUM", "Summation");
/* 168 */     titles.put("T3", "Triple Exponential Moving Average (T3)");
/* 169 */     titles.put("TAN", "Vector Trigonometric Tan");
/* 170 */     titles.put("TANH", "Vector Trigonometric Tanh");
/* 171 */     titles.put("TEMA", "Triple Exponential Moving Average");
/* 172 */     titles.put("TRANGE", "True Range");
/* 173 */     titles.put("TRIMA", "Triangular Moving Average");
/* 174 */     titles.put("TRIX", "1-day Rate-Of-Change (ROC) of a Triple Smooth EMA");
/* 175 */     titles.put("TSF", "Time Series Forecast");
/* 176 */     titles.put("TYPPRICE", "Typical Price");
/* 177 */     titles.put("ULTOSC", "Ultimate Oscillator");
/* 178 */     titles.put("VAR", "Variance");
/* 179 */     titles.put("WCLPRICE", "Weighted Close Price");
/* 180 */     titles.put("WILLR", "Williams' %R");
/* 181 */     titles.put("WMA", "Weighted Moving Average");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.indicators.IndicatorsFilter
 * JD-Core Version:    0.6.0
 */