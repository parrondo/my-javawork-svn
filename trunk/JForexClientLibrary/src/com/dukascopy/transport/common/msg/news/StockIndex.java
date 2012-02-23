/*    */ package com.dukascopy.transport.common.msg.news;
/*    */ 
/*    */ public enum StockIndex
/*    */ {
/*  6 */   AEX("AEX Index Components", "XAEX"), 
/*  7 */   ATX("ATX Components", "XATX"), 
/*  8 */   BEL20("Bel20 Components", "XB20"), 
/*  9 */   CAC("CAC 40 Components", "XCA4"), 
/* 10 */   DAXMC("DAX MidCap Index", "XMDAX"), 
/* 11 */   DAXSC("DAX SmallCap Index", "XSDAX"), 
/* 12 */   DJAT("Dow Jones Asian Titans Index Components", "XATI"), 
/* 13 */   DJG("Dow Jones Global Index Components", "XGDW"), 
/* 14 */   DJGT("Dow Jones Global Titans Index Components", "XGTI"), 
/* 15 */   DJIA("Dow Jones Industrial Average Components", "XDJI"), 
/* 16 */   DJI("Dow Jones Islamic Index Components", "XISL"), 
/* 17 */   DJS("Dow Jones Stoxx 50 Components", "XST5"), 
/* 18 */   DJSTFIN("Dow Jones Sector Titan Index - Financial Components", "XSTF"), 
/* 19 */   DJSTTECH("Dow Jones Sector Titan Index - Technology Components", "XSTX"), 
/* 20 */   DJSTTEL("Dow Jones Sector Titan Index - Tel Co", "XSTT"), 
/* 21 */   EURONEXT("Euronext 100", "XENX"), 
/* 22 */   EUROSTOXX50("EuroSTOXX50", "XES"), 
/* 23 */   FORTUNE("Fortune 500 Index", "XFFX"), 
/* 24 */   FTSE("FTSE 100 Components", "XFT1"), 
/* 25 */   HEX("HEX Components", "XHEX"), 
/* 26 */   HSC("Hang Seng Components", "XHSG"), 
/* 27 */   IBEX35("IBEX-35 Components", "XIBEX"), 
/* 28 */   KFX("KFX Components", "XKFX"), 
/* 29 */   MIB("MIB 40 Components", "XMIB"), 
/* 30 */   NASDAQ("NASDAQ 100 Components", "XNQ1"), 
/* 31 */   NIKKEI("Nikkei 225 Components", "X225"), 
/* 32 */   NYSE("NYSE Composite Index Components", "XNYA"), 
/* 33 */   OBX("OBX Total Components", "XOBX"), 
/* 34 */   PSI20("PSI-20 Components", "XP20"), 
/* 35 */   RUSSELL("Russell 3000 component", "XRUS"), 
/* 36 */   SMI("SMI Components", "XSMI"), 
/* 37 */   SP("S&P 500 Index component", "XSP5"), 
/* 38 */   SX("SX All Share Components", "XSXA"), 
/* 39 */   TECDAX("TecDAX Components", "XTDX"), 
/* 40 */   US("US Small Cap Index", "XSCI"), 
/* 41 */   XETRADAX("XETRA DAX Components", "XDAX");
/*    */ 
/*    */   public static final String PREFIX = "I";
/*    */   String description;
/*    */   String codeBody;
/*    */ 
/* 49 */   private StockIndex(String description, String codeBody) { this.description = description;
/* 50 */     this.codeBody = codeBody; }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 54 */     return this.description;
/*    */   }
/*    */ 
/*    */   public String getCodeBody() {
/* 58 */     return this.codeBody;
/*    */   }
/*    */ 
/*    */   public String getCode() {
/* 62 */     return "I" + '/' + this.codeBody;
/*    */   }
/*    */ 
/*    */   public static StockIndex toStockIndex(String value) {
/* 66 */     if (value != null) {
/* 67 */       value = value.trim();
/*    */     }
/* 69 */     for (StockIndex index : values()) {
/* 70 */       if (index.codeBody.equals(value)) {
/* 71 */         return index;
/*    */       }
/*    */     }
/* 74 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.StockIndex
 * JD-Core Version:    0.6.0
 */