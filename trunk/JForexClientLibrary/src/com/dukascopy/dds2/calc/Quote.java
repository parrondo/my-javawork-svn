/*     */ package com.dukascopy.dds2.calc;
/*     */ 
/*     */ public enum Quote
/*     */ {
/*  11 */   AUD_JPY_BID("AUD/JPY", BID_ASK.BID, 1000, 2000), 
/*  12 */   AUD_JPY_ASK("AUD/JPY", BID_ASK.ASK, 1001, 2000), 
/*  13 */   AUD_USD_BID("AUD/USD", BID_ASK.BID, 1002, 2002), 
/*  14 */   AUD_USD_ASK("AUD/USD", BID_ASK.ASK, 1003, 2002), 
/*  15 */   CAD_JPY_BID("CAD/JPY", BID_ASK.BID, 1004, 2004), 
/*  16 */   CAD_JPY_ASK("CAD/JPY", BID_ASK.ASK, 1005, 2004), 
/*  17 */   CHF_JPY_BID("CHF/JPY", BID_ASK.BID, 1006, 2006), 
/*  18 */   CHF_JPY_ASK("CHF/JPY", BID_ASK.ASK, 1007, 2006), 
/*  19 */   EUR_CHF_BID("EUR/CHF", BID_ASK.BID, 1008, 2008), 
/*  20 */   EUR_CHF_ASK("EUR/CHF", BID_ASK.ASK, 1009, 2008), 
/*  21 */   EUR_DKK_BID("EUR/DKK", BID_ASK.BID, 1010, 2010), 
/*  22 */   EUR_DKK_ASK("EUR/DKK", BID_ASK.ASK, 1011, 2010), 
/*  23 */   EUR_GBP_BID("EUR/GBP", BID_ASK.BID, 1012, 2012), 
/*  24 */   EUR_GBP_ASK("EUR/GBP", BID_ASK.ASK, 1013, 2012), 
/*  25 */   EUR_JPY_BID("EUR/JPY", BID_ASK.BID, 1014, 2014), 
/*  26 */   EUR_JPY_ASK("EUR/JPY", BID_ASK.ASK, 1015, 2014), 
/*  27 */   EUR_NOK_BID("EUR/NOK", BID_ASK.BID, 1016, 2016), 
/*  28 */   EUR_NOK_ASK("EUR/NOK", BID_ASK.ASK, 1017, 2016), 
/*  29 */   EUR_SEK_BID("EUR/SEK", BID_ASK.BID, 1018, 2018), 
/*  30 */   EUR_SEK_ASK("EUR/SEK", BID_ASK.ASK, 1019, 2018), 
/*  31 */   EUR_USD_BID("EUR/USD", BID_ASK.BID, 1020, 2020), 
/*  32 */   EUR_USD_ASK("EUR/USD", BID_ASK.ASK, 1021, 2020), 
/*  33 */   GBP_CHF_BID("GBP/CHF", BID_ASK.BID, 1022, 2022), 
/*  34 */   GBP_CHF_ASK("GBP/CHF", BID_ASK.ASK, 1023, 2022), 
/*  35 */   GBP_JPY_BID("GBP/JPY", BID_ASK.BID, 1024, 2024), 
/*  36 */   GBP_JPY_ASK("GBP/JPY", BID_ASK.ASK, 1025, 2024), 
/*  37 */   GBP_USD_BID("GBP/USD", BID_ASK.BID, 1026, 2026), 
/*  38 */   GBP_USD_ASK("GBP/USD", BID_ASK.ASK, 1027, 2026), 
/*  39 */   USD_CAD_BID("USD/CAD", BID_ASK.BID, 1028, 2028), 
/*  40 */   USD_CAD_ASK("USD/CAD", BID_ASK.ASK, 1029, 2028), 
/*  41 */   USD_CHF_BID("USD/CHF", BID_ASK.BID, 1030, 2030), 
/*  42 */   USD_CHF_ASK("USD/CHF", BID_ASK.ASK, 1031, 2030), 
/*  43 */   USD_JPY_BID("USD/JPY", BID_ASK.BID, 1032, 2032), 
/*  44 */   USD_JPY_ASK("USD/JPY", BID_ASK.ASK, 1033, 2032), 
/*  45 */   NZD_USD_BID("NZD/USD", BID_ASK.BID, 1034, 2034), 
/*  46 */   NZD_USD_ASK("NZD/USD", BID_ASK.ASK, 1035, 2034), 
/*  47 */   EUR_AUD_BID("EUR/AUD", BID_ASK.BID, 1036, 2036), 
/*  48 */   EUR_AUD_ASK("EUR/AUD", BID_ASK.ASK, 1037, 2036), 
/*  49 */   AUD_CAD_BID("AUD/CAD", BID_ASK.BID, 1050, 2050), 
/*  50 */   AUD_CAD_ASK("AUD/CAD", BID_ASK.ASK, 1051, 2050), 
/*  51 */   AUD_CHF_BID("AUD/CHF", BID_ASK.BID, 1052, 2052), 
/*  52 */   AUD_CHF_ASK("AUD/CHF", BID_ASK.ASK, 1053, 2052), 
/*  53 */   AUD_EUR_BID("AUD/EUR", BID_ASK.BID, 1054, 2054), 
/*  54 */   AUD_EUR_ASK("AUD/EUR", BID_ASK.ASK, 1055, 2054), 
/*  55 */   AUD_GBP_BID("AUD/GBP", BID_ASK.BID, 1056, 2056), 
/*  56 */   AUD_GBP_ASK("AUD/GBP", BID_ASK.ASK, 1057, 2056), 
/*  57 */   CAD_AUD_BID("CAD/AUD", BID_ASK.BID, 1058, 2058), 
/*  58 */   CAD_AUD_ASK("CAD/AUD", BID_ASK.ASK, 1059, 2058), 
/*  59 */   CAD_CHF_BID("CAD/CHF", BID_ASK.BID, 1060, 2060), 
/*  60 */   CAD_CHF_ASK("CAD/CHF", BID_ASK.ASK, 1061, 2060), 
/*  61 */   CAD_EUR_BID("CAD/EUR", BID_ASK.BID, 1062, 2062), 
/*  62 */   CAD_EUR_ASK("CAD/EUR", BID_ASK.ASK, 1063, 2062), 
/*  63 */   CAD_GBP_BID("CAD/GBP", BID_ASK.BID, 1064, 2064), 
/*  64 */   CAD_GBP_ASK("CAD/GBP", BID_ASK.ASK, 1065, 2064), 
/*  65 */   CAD_USD_BID("CAD/USD", BID_ASK.BID, 1066, 2066), 
/*  66 */   CAD_USD_ASK("CAD/USD", BID_ASK.ASK, 1067, 2066), 
/*  67 */   CHF_AUD_BID("CHF/AUD", BID_ASK.BID, 1068, 2068), 
/*  68 */   CHF_AUD_ASK("CHF/AUD", BID_ASK.ASK, 1069, 2068), 
/*  69 */   CHF_CAD_BID("CHF/CAD", BID_ASK.BID, 1070, 2070), 
/*  70 */   CHF_CAD_ASK("CHF/CAD", BID_ASK.ASK, 1071, 2070), 
/*  71 */   CHF_EUR_BID("CHF/EUR", BID_ASK.BID, 1072, 2072), 
/*  72 */   CHF_EUR_ASK("CHF/EUR", BID_ASK.ASK, 1073, 2072), 
/*  73 */   CHF_GBP_BID("CHF/GBP", BID_ASK.BID, 1074, 2074), 
/*  74 */   CHF_GBP_ASK("CHF/GBP", BID_ASK.ASK, 1075, 2074), 
/*  75 */   CHF_USD_BID("CHF/USD", BID_ASK.BID, 1076, 2076), 
/*  76 */   CHF_USD_ASK("CHF/USD", BID_ASK.ASK, 1077, 2076), 
/*  77 */   EUR_CAD_BID("EUR/CAD", BID_ASK.BID, 1078, 2078), 
/*  78 */   EUR_CAD_ASK("EUR/CAD", BID_ASK.ASK, 1079, 2078), 
/*  79 */   GBP_AUD_BID("GBP/AUD", BID_ASK.BID, 1080, 2080), 
/*  80 */   GBP_AUD_ASK("GBP/AUD", BID_ASK.ASK, 1081, 2080), 
/*  81 */   GBP_CAD_BID("GBP/CAD", BID_ASK.BID, 1082, 2082), 
/*  82 */   GBP_CAD_ASK("GBP/CAD", BID_ASK.ASK, 1083, 2082), 
/*  83 */   GBP_EUR_BID("GBP/EUR", BID_ASK.BID, 1084, 2084), 
/*  84 */   GBP_EUR_ASK("GBP/EUR", BID_ASK.ASK, 1085, 2084), 
/*  85 */   JPY_AUD_BID("JPY/AUD", BID_ASK.BID, 1086, 2086), 
/*  86 */   JPY_AUD_ASK("JPY/AUD", BID_ASK.ASK, 1087, 2086), 
/*  87 */   JPY_CAD_BID("JPY/CAD", BID_ASK.BID, 1088, 2088), 
/*  88 */   JPY_CAD_ASK("JPY/CAD", BID_ASK.ASK, 1089, 2088), 
/*  89 */   JPY_CHF_BID("JPY/CHF", BID_ASK.BID, 1090, 2090), 
/*  90 */   JPY_CHF_ASK("JPY/CHF", BID_ASK.ASK, 1091, 2090), 
/*  91 */   JPY_EUR_BID("JPY/EUR", BID_ASK.BID, 1092, 2092), 
/*  92 */   JPY_EUR_ASK("JPY/EUR", BID_ASK.ASK, 1093, 2092), 
/*  93 */   JPY_GBP_BID("JPY/GBP", BID_ASK.BID, 1094, 2094), 
/*  94 */   JPY_GBP_ASK("JPY/GBP", BID_ASK.ASK, 1095, 2094), 
/*  95 */   JPY_USD_BID("JPY/USD", BID_ASK.BID, 1096, 2096), 
/*  96 */   JPY_USD_ASK("JPY/USD", BID_ASK.ASK, 1097, 2096), 
/*  97 */   USD_AUD_BID("USD/AUD", BID_ASK.BID, 1098, 2098), 
/*  98 */   USD_AUD_ASK("USD/AUD", BID_ASK.ASK, 1099, 2098), 
/*  99 */   USD_EUR_BID("USD/EUR", BID_ASK.BID, 1100, 2100), 
/* 100 */   USD_EUR_ASK("USD/EUR", BID_ASK.ASK, 1101, 2100), 
/* 101 */   USD_GBP_BID("USD/GBP", BID_ASK.BID, 1102, 2102), 
/* 102 */   USD_GBP_ASK("USD/GBP", BID_ASK.ASK, 1103, 2102), 
/* 103 */   EUR_NZD_BID("EUR/NZD", BID_ASK.BID, 1104, 2104), 
/* 104 */   EUR_NZD_ASK("EUR/NZD", BID_ASK.ASK, 1105, 2104), 
/* 105 */   GBP_NZD_BID("GBP/NZD", BID_ASK.BID, 1106, 2106), 
/* 106 */   GBP_NZD_ASK("GBP/NZD", BID_ASK.ASK, 1107, 2106), 
/* 107 */   NZD_CAD_BID("NZD/CAD", BID_ASK.BID, 1108, 2108), 
/* 108 */   NZD_CAD_ASK("NZD/CAD", BID_ASK.ASK, 1109, 2108), 
/* 109 */   NZD_CHF_BID("NZD/CHF", BID_ASK.BID, 1110, 2110), 
/* 110 */   NZD_CHF_ASK("NZD/CHF", BID_ASK.ASK, 1111, 2110), 
/* 111 */   NZD_JPY_BID("NZD/JPY", BID_ASK.BID, 1112, 2112), 
/* 112 */   NZD_JPY_ASK("NZD/JPY", BID_ASK.ASK, 1113, 2112), 
/* 113 */   USD_SEK_BID("USD/SEK", BID_ASK.BID, 1114, 2114), 
/* 114 */   USD_SEK_ASK("USD/SEK", BID_ASK.ASK, 1115, 2114), 
/* 115 */   USD_NOK_BID("USD/NOK", BID_ASK.BID, 1116, 2116), 
/* 116 */   USD_NOK_ASK("USD/NOK", BID_ASK.ASK, 1117, 2116), 
/* 117 */   AUD_NZD_BID("AUD/NZD", BID_ASK.BID, 1118, 2118), 
/* 118 */   AUD_NZD_ASK("AUD/NZD", BID_ASK.ASK, 1119, 2118), 
/* 119 */   USD_SGD_BID("USD/SGD", BID_ASK.BID, 1120, 2120), 
/* 120 */   USD_SGD_ASK("USD/SGD", BID_ASK.ASK, 1121, 2120);
/*     */ 
/*     */   private final String name;
/*     */   private final BID_ASK bid_ask;
/*     */   private final int code;
/*     */   private final int quoteId;
/*     */ 
/* 128 */   private Quote(String name, BID_ASK bid_ask, int code, int qouteId) { this.name = name;
/* 129 */     this.bid_ask = bid_ask;
/* 130 */     this.code = code;
/* 131 */     this.quoteId = qouteId; }
/*     */ 
/*     */   public static Quote getByInstrument(String instrument, BID_ASK bid_ask)
/*     */   {
/* 135 */     Quote[] quotes = values();
/* 136 */     for (Quote quote : quotes) {
/* 137 */       if ((instrument.equalsIgnoreCase(quote.name)) && (bid_ask.equals(quote.bid_ask))) {
/* 138 */         return quote;
/*     */       }
/*     */     }
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getQuoteIdByInstrument(String instrument) {
/* 145 */     Quote[] quotes = values();
/* 146 */     for (Quote quote : quotes) {
/* 147 */       if (instrument.equalsIgnoreCase(quote.name)) {
/* 148 */         return quote.quoteId;
/*     */       }
/*     */     }
/* 151 */     return 0;
/*     */   }
/*     */ 
/*     */   public static Quote getByCode(int code)
/*     */   {
/* 156 */     Quote[] quotes = values();
/* 157 */     for (Quote quote : quotes) {
/* 158 */       if (code == quote.code) {
/* 159 */         return quote;
/*     */       }
/*     */     }
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 166 */     return this.name;
/*     */   }
/*     */ 
/*     */   public int getCode() {
/* 170 */     return this.code;
/*     */   }
/*     */ 
/*     */   public static enum BID_ASK {
/* 174 */     BID, 
/* 175 */     ASK;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.calc.Quote
 * JD-Core Version:    0.6.0
 */