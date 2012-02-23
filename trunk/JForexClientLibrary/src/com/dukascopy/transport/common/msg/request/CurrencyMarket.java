/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.mina.Base64Encoder;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.RequestMessage;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.MathContext;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class CurrencyMarket extends RequestMessage
/*     */ {
/*     */   public static final String TYPE = "cm";
/*     */   public static final String OLD_TYPE = "marketState";
/*     */   public static final String CREATION_TIMESTAMP = "t";
/*     */   public static final String METER = "mt";
/*     */   public static final String DATA = "d";
/*     */   public static final String TOTAL_LIQUIDITY_ASK = "tla";
/*     */   public static final String AVERAGE_PRICE_ASK = "apa";
/*     */   public static final String TOTAL_LIQUIDITY_BID = "tlb";
/*     */   public static final String AVERAGE_PRICE_BID = "apb";
/*  52 */   private List<CurrencyOffer> asks = new ArrayList();
/*     */ 
/*  54 */   private List<CurrencyOffer> bids = new ArrayList();
/*     */ 
/*  56 */   private static Map<String, Integer> instrumentAlias = new HashMap();
/*     */ 
/*  58 */   private static Map<Integer, String> instrumentAliasReverse = new HashMap();
/*     */   private String instrumentPrimary;
/*     */   private String instrumentSecondary;
/*     */   private static final byte byteSeparator = -128;
/*     */   private static final byte multiBytePrefix = 127;
/*     */ 
/*     */   static void addAlias(String currency, int alias)
/*     */   {
/*  69 */     instrumentAlias.put(currency, Integer.valueOf(alias));
/*  70 */     instrumentAliasReverse.put(Integer.valueOf(alias), currency);
/*     */   }
/*     */ 
/*     */   public CurrencyMarket(String currencyPrimary, String currencySecondary, List<CurrencyOffer> bids, List<CurrencyOffer> asks)
/*     */   {
/* 273 */     this(currencyPrimary, currencySecondary, bids, asks, false);
/*     */   }
/*     */ 
/*     */   public CurrencyMarket(String currencyPrimary, String currencySecondary, List<CurrencyOffer> bids, List<CurrencyOffer> asks, boolean identifable)
/*     */   {
/* 279 */     setPriority(-1000000L);
/* 280 */     setType("cm");
/* 281 */     Integer primCode = (Integer)instrumentAlias.get(currencyPrimary);
/* 282 */     Integer secCode = (Integer)instrumentAlias.get(currencySecondary);
/* 283 */     if (primCode == null) {
/* 284 */       throw new IllegalArgumentException("Currency not mapped " + currencyPrimary);
/*     */     }
/*     */ 
/* 287 */     if (secCode == null) {
/* 288 */       throw new IllegalArgumentException("Currency not mapped " + currencySecondary);
/*     */     }
/*     */ 
/* 291 */     this.instrumentPrimary = currencyPrimary;
/* 292 */     this.instrumentSecondary = currencySecondary;
/* 293 */     put("id", identifable);
/* 294 */     setBids(bids);
/* 295 */     setAsks(asks);
/*     */   }
/*     */ 
/*     */   public CurrencyMarket(ProtocolMessage message) {
/* 299 */     super(message);
/* 300 */     setType("cm");
/* 301 */     setPriority(-1000000L);
/* 302 */     put("t", message.getString("t"));
/* 303 */     put("mt", message.getString("mt"));
/* 304 */     put("tla", message.getString("tla"));
/* 305 */     put("tlb", message.getString("tlb"));
/* 306 */     put("apa", message.getString("apa"));
/* 307 */     put("apb", message.getString("apb"));
/* 308 */     put("d", message.getString("d"));
/*     */ 
/* 310 */     if (message.getString("d") != null)
/* 311 */       parseData(message.getString("d"));
/*     */   }
/*     */ 
/*     */   public CurrencyMarket(ProtocolMessage message, boolean oldFormat)
/*     */   {
/* 316 */     super(message);
/* 317 */     setType("cm");
/* 318 */     put("t", message.getString("t"));
/* 319 */     put("mt", message.getString("mt"));
/* 320 */     put("tla", message.getString("tla"));
/* 321 */     put("tlb", message.getString("tlb"));
/* 322 */     put("apa", message.getString("apa"));
/* 323 */     put("apb", message.getString("apb"));
/* 324 */     String instrument = message.getString("i");
/* 325 */     this.instrumentPrimary = instrument.substring(0, 3);
/* 326 */     this.instrumentSecondary = instrument.substring(4);
/* 327 */     setAsks(getOldTypeOffers("a", message, instrument));
/* 328 */     setBids(getOldTypeOffers("b", message, instrument));
/*     */   }
/*     */ 
/*     */   private static List<CurrencyOffer> getOldTypeOffers(String paramName, ProtocolMessage message, String instrument)
/*     */   {
/* 337 */     List asks = new ArrayList();
/* 338 */     String sideString = "ASK";
/* 339 */     if (paramName.equals("b")) {
/* 340 */       sideString = "BID";
/*     */     }
/* 342 */     StringTokenizer asksArray = new StringTokenizer(message.getString(paramName), ",");
/*     */ 
/* 344 */     while (asksArray.hasMoreTokens()) {
/* 345 */       CurrencyOffer offer = new CurrencyOffer(asksArray.nextToken(), "" + new BigDecimal(asksArray.nextToken()).multiply(ONE_MILLION).doubleValue(), instrument, sideString);
/*     */ 
/* 348 */       offer.setFokAmount(new Money(new BigDecimal(asksArray.nextToken()).multiply(ONE_MILLION).toPlainString(), null));
/*     */ 
/* 350 */       asks.add(offer);
/*     */     }
/*     */ 
/* 353 */     return asks;
/*     */   }
/*     */ 
/*     */   private void parseData(String encodedData) {
/* 357 */     byte[] data = Base64Encoder.decode(encodedData);
/* 358 */     if (data != null)
/*     */       try {
/* 360 */         DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
/*     */ 
/* 362 */         this.instrumentPrimary = ((String)instrumentAliasReverse.get(Integer.valueOf(dis.readByte() & 0xFF)));
/* 363 */         this.instrumentSecondary = ((String)instrumentAliasReverse.get(Integer.valueOf(dis.readByte() & 0xFF)));
/* 364 */         Currency primary = null;
/* 365 */         Currency secondary = null;
/*     */         try {
/* 367 */           primary = this.instrumentPrimary != null ? Currency.getInstance(this.instrumentPrimary) : null;
/*     */ 
/* 369 */           secondary = this.instrumentSecondary != null ? Currency.getInstance(this.instrumentSecondary) : null;
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/* 374 */         BigDecimal priceBase = new BigDecimal(String.valueOf(dis.readFloat()));
/*     */ 
/* 376 */         int askCounter = 0;
/* 377 */         int bidCounter = 0;
/* 378 */         BigDecimal pip = getPipsTypeDecimal(priceBase, 1).divide(new BigDecimal("10"), MathContext.DECIMAL32);
/*     */ 
/* 381 */         if (priceBase.compareTo(BigDecimal.ZERO) >= 0) {
/*     */           while (true) {
/* 383 */             int next = dis.readByte();
/*     */ 
/* 385 */             if (next == -128) {
/*     */               break;
/*     */             }
/* 388 */             askCounter++;
/* 389 */             int shift = getShift(dis, next);
/*     */ 
/* 391 */             BigDecimal f = pip.multiply(new BigDecimal(shift));
/* 392 */             BigDecimal p = priceBase.add(f);
/*     */ 
/* 394 */             p = setCorrectPriceScale(p);
/* 395 */             BigDecimal amount = new BigDecimal(String.valueOf(dis.readFloat())).multiply(ONE_MILLION);
/*     */ 
/* 398 */             CurrencyOffer offer = new CurrencyOffer(this.instrumentPrimary, this.instrumentSecondary, OfferSide.ASK, new Money(amount, primary), new Money(p, secondary));
/*     */ 
/* 402 */             this.asks.add(offer);
/*     */           }
/*     */           while (true)
/*     */           {
/* 406 */             int next = dis.readByte();
/*     */ 
/* 408 */             if (next == -128) {
/*     */               break;
/*     */             }
/* 411 */             bidCounter++;
/* 412 */             int shift = getShift(dis, next);
/*     */ 
/* 414 */             BigDecimal f = pip.multiply(new BigDecimal(shift));
/* 415 */             BigDecimal p = priceBase.add(f);
/*     */ 
/* 417 */             p = setCorrectPriceScale(p);
/* 418 */             BigDecimal amount = new BigDecimal(String.valueOf(dis.readFloat())).multiply(ONE_MILLION);
/*     */ 
/* 421 */             CurrencyOffer offer = new CurrencyOffer(this.instrumentPrimary, this.instrumentSecondary, OfferSide.BID, new Money(amount, primary), new Money(p, secondary));
/*     */ 
/* 425 */             this.bids.add(offer);
/*     */           }
/* 427 */           BigDecimal amount = BigDecimal.ZERO;
/*     */           try {
/* 429 */             int infdex = 0;
/* 430 */             if (dis.available() > 0) {
/*     */               while (true) {
/* 432 */                 int next = dis.readByte();
/* 433 */                 if (next == -128) {
/*     */                   break;
/*     */                 }
/* 436 */                 amount = new BigDecimal(String.valueOf(dis.readFloat())).multiply(ONE_MILLION);
/*     */ 
/* 438 */                 if (this.asks.size() > infdex) {
/* 439 */                   ((CurrencyOffer)this.asks.get(infdex)).setFokAmount(new Money(amount.toPlainString(), this.instrumentPrimary));
/*     */                 }
/*     */ 
/* 443 */                 infdex++;
/*     */               }
/*     */ 
/* 446 */               infdex = 0;
/*     */               while (true) {
/* 448 */                 int next = dis.readByte();
/* 449 */                 if (next == -128) {
/*     */                   break;
/*     */                 }
/* 452 */                 amount = new BigDecimal(String.valueOf(dis.readFloat())).multiply(ONE_MILLION);
/*     */ 
/* 454 */                 if (this.bids.size() > infdex) {
/* 455 */                   ((CurrencyOffer)this.bids.get(infdex)).setFokAmount(new Money(amount.toPlainString(), this.instrumentPrimary));
/*     */                 }
/*     */ 
/* 459 */                 infdex++;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */           }
/*     */         }
/* 467 */         dis.close();
/*     */       } catch (Exception e) {
/* 469 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   private int getShift(DataInputStream dis, int next)
/*     */     throws IOException
/*     */   {
/*     */     int shift;
/* 476 */     if (next == 127)
/*     */     {
/* 478 */       int multiplicand = dis.readByte();
/*     */ 
/* 480 */       int shift = dis.readByte();
/* 481 */       shift = multiplicand > 0 ? shift + 126 * multiplicand : 126 * multiplicand - shift;
/*     */     }
/*     */     else {
/* 484 */       shift = next;
/*     */     }
/* 486 */     return shift;
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 496 */     return this.instrumentPrimary + "/" + this.instrumentSecondary;
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/* 506 */     return this.instrumentPrimary;
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/* 515 */     return this.instrumentSecondary;
/*     */   }
/*     */ 
/*     */   public List<CurrencyOffer> getBids()
/*     */   {
/* 525 */     return this.bids;
/*     */   }
/*     */ 
/*     */   public void setBids(List<CurrencyOffer> bids)
/*     */   {
/* 535 */     this.bids = bids;
/*     */   }
/*     */ 
/*     */   public List<CurrencyOffer> getAsks()
/*     */   {
/* 545 */     return this.asks;
/*     */   }
/*     */ 
/*     */   public void setAsks(List<CurrencyOffer> asks)
/*     */   {
/* 555 */     this.asks = asks;
/*     */   }
/*     */ 
/*     */   public Long getCreationTimestamp()
/*     */   {
/*     */     try
/*     */     {
/* 565 */       return Long.valueOf(Long.parseLong(getString("t")));
/*     */     }
/*     */     catch (NumberFormatException e) {
/*     */     }
/* 569 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCreationTimestamp(Long timestamp)
/*     */   {
/* 579 */     put("t", timestamp.toString());
/*     */   }
/*     */ 
/*     */   public String getMeter() {
/* 583 */     return getString("mt");
/*     */   }
/*     */ 
/*     */   public void setMeter(String meter) {
/* 587 */     put("mt", meter);
/*     */   }
/*     */ 
/*     */   public BigDecimal getTotalLiquidityAsk() {
/* 591 */     String amountString = getString("tla");
/* 592 */     if (amountString != null) {
/* 593 */       return new BigDecimal(amountString).multiply(ONE_MILLION);
/*     */     }
/* 595 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTotalLiquidityAsk(BigDecimal amount)
/*     */   {
/* 600 */     if (amount != null)
/* 601 */       put("tla", amount.divide(ONE_MILLION).toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getAveragePriceAsk()
/*     */   {
/* 606 */     String priceString = getString("apa");
/* 607 */     if (priceString != null) {
/* 608 */       return new BigDecimal(priceString);
/*     */     }
/* 610 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAveragePriceAsk(BigDecimal price)
/*     */   {
/* 615 */     if (price != null)
/* 616 */       put("apa", price.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getTotalLiquidityBid()
/*     */   {
/* 621 */     String amountString = getString("tlb");
/* 622 */     if (amountString != null) {
/* 623 */       return new BigDecimal(amountString).multiply(ONE_MILLION);
/*     */     }
/* 625 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTotalLiquidityBid(BigDecimal amount)
/*     */   {
/* 630 */     if (amount != null)
/* 631 */       put("tlb", amount.divide(ONE_MILLION).toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getAveragePriceBid()
/*     */   {
/* 636 */     String priceString = getString("apb");
/* 637 */     if (priceString != null) {
/* 638 */       return new BigDecimal(priceString);
/*     */     }
/* 640 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAveragePriceBid(BigDecimal price)
/*     */   {
/* 645 */     if (price != null)
/* 646 */       put("apb", price.toPlainString());
/*     */   }
/*     */ 
/*     */   private static BigDecimal getPipsTypeDecimal(BigDecimal price, int pipsCount)
/*     */   {
/* 651 */     BigDecimal val = new BigDecimal("0.00010");
/* 652 */     int scale = 5;
/* 653 */     if (price.compareTo(new BigDecimal(20)) >= 0) {
/* 654 */       val = new BigDecimal("0.010");
/* 655 */       scale = 3;
/*     */     }
/* 657 */     return val.multiply(new BigDecimal(pipsCount)).setScale(scale, RoundingMode.FLOOR);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 668 */     return getOldFormat().toString();
/*     */   }
/*     */ 
/*     */   public String toProtocolString()
/*     */   {
/*     */     try
/*     */     {
/* 682 */       CurrencyOffer best = getBestOffer(OfferSide.ASK);
/* 683 */       if (best == null) {
/* 684 */         best = getBestOffer(OfferSide.BID);
/*     */       }
/* 686 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 687 */       DataOutputStream dos = new DataOutputStream(baos);
/* 688 */       List asksCopy = new ArrayList();
/* 689 */       List bidsCopy = new ArrayList();
/* 690 */       asksCopy.addAll(this.asks);
/* 691 */       bidsCopy.addAll(this.bids);
/* 692 */       int primAlias = ((Integer)instrumentAlias.get(this.instrumentPrimary)).intValue();
/*     */ 
/* 694 */       int secAlias = ((Integer)instrumentAlias.get(this.instrumentSecondary)).intValue();
/*     */ 
/* 696 */       dos.writeByte(primAlias);
/* 697 */       dos.writeByte(secAlias);
/* 698 */       if (best == null) {
/* 699 */         dos.writeFloat(-1.0F);
/* 700 */         dos.writeByte(-128);
/* 701 */         dos.writeByte(-128);
/* 702 */         dos.writeByte(-128);
/* 703 */         dos.writeByte(-128);
/*     */       } else {
/* 705 */         BigDecimal base = setCorrectPriceScale(best.getPrice().getValue());
/*     */ 
/* 707 */         BigDecimal pip = getPipsTypeDecimal(base, 1).divide(new BigDecimal("10"), MathContext.DECIMAL32);
/*     */ 
/* 709 */         float f = base.floatValue();
/*     */ 
/* 711 */         dos.writeFloat(f);
/* 712 */         for (CurrencyOffer o : asksCopy) {
/* 713 */           writeOfferShift(base, pip, dos, o);
/*     */         }
/* 715 */         dos.writeByte(-128);
/* 716 */         for (CurrencyOffer o : bidsCopy) {
/* 717 */           writeOfferShift(base, pip, dos, o);
/*     */         }
/* 719 */         dos.writeByte(-128);
/* 720 */         for (CurrencyOffer o : asksCopy) {
/* 721 */           dos.writeByte(12);
/* 722 */           Float amount = Float.valueOf(o.getFokAmount() != null ? o.getFokAmount().getValue().divide(ONE_MILLION, MathContext.DECIMAL32).stripTrailingZeros().floatValue() : 0.0F);
/*     */ 
/* 726 */           dos.writeFloat(amount.floatValue());
/*     */         }
/* 728 */         dos.writeByte(-128);
/* 729 */         for (CurrencyOffer o : bidsCopy) {
/* 730 */           dos.writeByte(12);
/* 731 */           Float amount = Float.valueOf(o.getFokAmount() != null ? o.getFokAmount().getValue().divide(ONE_MILLION, MathContext.DECIMAL32).stripTrailingZeros().floatValue() : 0.0F);
/*     */ 
/* 735 */           dos.writeFloat(amount.floatValue());
/*     */         }
/* 737 */         dos.writeByte(-128);
/*     */       }
/* 739 */       dos.flush();
/* 740 */       String s = new String(Base64Encoder.encode(baos.toByteArray()));
/* 741 */       dos.close();
/* 742 */       baos.close();
/*     */ 
/* 744 */       put("d", s);
/* 745 */       StringBuffer sb = new StringBuffer();
/* 746 */       sb.append(super.toString());
/* 747 */       return sb.toString();
/*     */     }
/*     */     catch (IOException e) {
/* 750 */       e.printStackTrace();
/*     */     }
/* 752 */     return null;
/*     */   }
/*     */ 
/*     */   private BigDecimal setCorrectPriceScale(BigDecimal price) {
/* 756 */     if (price.compareTo(new BigDecimal(20)) >= 0) {
/* 757 */       return price.setScale(3, RoundingMode.FLOOR);
/*     */     }
/* 759 */     return price.setScale(5, RoundingMode.FLOOR);
/*     */   }
/*     */ 
/*     */   private void writeOfferShift(BigDecimal base, BigDecimal oneOfTenPip, DataOutputStream dos, CurrencyOffer o)
/*     */     throws IOException
/*     */   {
/* 772 */     BigDecimal offerPrice = setCorrectPriceScale(o.getPrice().getValue());
/*     */ 
/* 774 */     BigDecimal shift = offerPrice.subtract(base);
/* 775 */     BigDecimal pipShift = shift.divide(oneOfTenPip, MathContext.DECIMAL32);
/* 776 */     Integer shiftInteger = Integer.valueOf(pipShift.intValue());
/*     */ 
/* 780 */     Float amount = Float.valueOf(o.getAmount().getValue().divide(ONE_MILLION, MathContext.DECIMAL32).stripTrailingZeros().floatValue());
/*     */ 
/* 788 */     if (Math.abs(shiftInteger.intValue()) <= 126) {
/* 789 */       int b = shiftInteger.byteValue();
/* 790 */       dos.writeByte(b);
/* 791 */       dos.writeFloat(amount.floatValue());
/* 792 */     } else if (Math.abs(shiftInteger.intValue()) > 126) {
/* 793 */       int multiplicand = shiftInteger.intValue() / 126;
/* 794 */       if ((multiplicand >= -126) && (multiplicand <= 126)) {
/* 795 */         dos.writeByte(127);
/*     */ 
/* 801 */         dos.writeByte(multiplicand);
/* 802 */         int b = Integer.valueOf(Math.abs(shiftInteger.intValue()) - 126 * Math.abs(multiplicand)).intValue();
/*     */ 
/* 805 */         dos.writeByte(b);
/* 806 */         dos.writeFloat(amount.floatValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ProtocolMessage getOldFormat() {
/* 812 */     ProtocolMessage cm = new ProtocolMessage();
/* 813 */     cm.setPriority(-1000000L);
/* 814 */     cm.setType("marketState");
/* 815 */     cm.put("t", getString("t"));
/* 816 */     cm.put("mt", getString("mt"));
/* 817 */     cm.put("i", getInstrument());
/* 818 */     StringBuffer asksArray = new StringBuffer();
/* 819 */     for (int i = 0; i < this.asks.size(); i++) {
/* 820 */       if (i == 0) {
/* 821 */         asksArray.append(((CurrencyOffer)this.asks.get(i)).getString("price"));
/* 822 */         asksArray.append(",").append(((CurrencyOffer)this.asks.get(i)).getAmount().getValue().divide(ONE_MILLION).doubleValue());
/*     */ 
/* 825 */         BigDecimal fokAmount = ((CurrencyOffer)this.asks.get(i)).getFokAmount() != null ? ((CurrencyOffer)this.asks.get(i)).getFokAmount().getValue() : BigDecimal.ZERO;
/*     */ 
/* 828 */         asksArray.append(",").append(fokAmount.divide(ONE_MILLION).doubleValue());
/*     */       }
/*     */       else {
/* 831 */         asksArray.append(",").append(((CurrencyOffer)this.asks.get(i)).getString("price"));
/* 832 */         asksArray.append(",").append(((CurrencyOffer)this.asks.get(i)).getAmount().getValue().divide(ONE_MILLION, RoundingMode.FLOOR).doubleValue());
/*     */ 
/* 836 */         BigDecimal fokAmount = ((CurrencyOffer)this.asks.get(i)).getFokAmount() != null ? ((CurrencyOffer)this.asks.get(i)).getFokAmount().getValue() : BigDecimal.ZERO;
/*     */ 
/* 839 */         asksArray.append(",").append(fokAmount.divide(ONE_MILLION).doubleValue());
/*     */       }
/*     */     }
/*     */ 
/* 843 */     cm.put("a", asksArray.toString());
/* 844 */     StringBuffer bidArray = new StringBuffer();
/* 845 */     for (int i = 0; i < this.bids.size(); i++) {
/* 846 */       if (i == 0) {
/* 847 */         bidArray.append(((CurrencyOffer)this.bids.get(i)).getString("price"));
/* 848 */         bidArray.append(",").append(((CurrencyOffer)this.bids.get(i)).getAmount().getValue().divide(ONE_MILLION).doubleValue());
/*     */ 
/* 851 */         BigDecimal fokAmount = ((CurrencyOffer)this.bids.get(i)).getFokAmount() != null ? ((CurrencyOffer)this.bids.get(i)).getFokAmount().getValue() : BigDecimal.ZERO;
/*     */ 
/* 854 */         bidArray.append(",").append(fokAmount.divide(ONE_MILLION).doubleValue());
/*     */       }
/*     */       else {
/* 857 */         bidArray.append(",").append(((CurrencyOffer)this.bids.get(i)).getString("price"));
/* 858 */         bidArray.append(",").append(((CurrencyOffer)this.bids.get(i)).getAmount().getValue().divide(ONE_MILLION).doubleValue());
/*     */ 
/* 861 */         BigDecimal fokAmount = ((CurrencyOffer)this.bids.get(i)).getFokAmount() != null ? ((CurrencyOffer)this.bids.get(i)).getFokAmount().getValue() : BigDecimal.ZERO;
/*     */ 
/* 864 */         bidArray.append(",").append(fokAmount.divide(ONE_MILLION).doubleValue());
/*     */       }
/*     */     }
/*     */ 
/* 868 */     cm.put("b", bidArray.toString());
/* 869 */     return cm;
/*     */   }
/*     */ 
/*     */   public CurrencyOffer getBestOffer(OfferSide side) {
/* 873 */     if (side == OfferSide.BID) {
/* 874 */       if (this.bids.size() > 0)
/* 875 */         return (CurrencyOffer)this.bids.get(0);
/*     */     }
/* 877 */     else if ((side == OfferSide.ASK) && 
/* 878 */       (this.asks.size() > 0)) {
/* 879 */       return (CurrencyOffer)this.asks.get(0);
/*     */     }
/*     */ 
/* 882 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     addAlias("EUR", 0);
/*  75 */     addAlias("USD", 1);
/*  76 */     addAlias("GBP", 2);
/*  77 */     addAlias("CHF", 3);
/*  78 */     addAlias("JPY", 4);
/*  79 */     addAlias("CAD", 5);
/*  80 */     addAlias("AUD", 6);
/*  81 */     addAlias("CZK", 7);
/*  82 */     addAlias("DKK", 8);
/*  83 */     addAlias("HKD", 9);
/*  84 */     addAlias("HUF", 10);
/*  85 */     addAlias("NOK", 11);
/*  86 */     addAlias("NZD", 12);
/*  87 */     addAlias("PLN", 13);
/*  88 */     addAlias("SEK", 14);
/*  89 */     addAlias("SGD", 15);
/*  90 */     addAlias("TRY", 16);
/*  91 */     addAlias("ZAR", 17);
/*  92 */     addAlias("ISK", 18);
/*  93 */     addAlias("MXN", 19);
/*  94 */     addAlias("RON", 20);
/*  95 */     addAlias("SKK", 21);
/*  96 */     addAlias("RUB", 22);
/*  97 */     addAlias("XAG", 23);
/*  98 */     addAlias("XAU", 24);
/*  99 */     addAlias("BKT", 25);
/* 100 */     addAlias("EUQ", 26);
/* 101 */     addAlias("USQ", 27);
/* 102 */     addAlias("ILS", 28);
/* 103 */     addAlias("BRL", 29);
/* 104 */     addAlias("COP", 30);
/* 105 */     addAlias("CLP", 31);
/* 106 */     addAlias("ARS", 32);
/* 107 */     addAlias("VEB", 33);
/* 108 */     addAlias("INR", 34);
/* 109 */     addAlias("KRW", 35);
/* 110 */     addAlias("MYR", 36);
/* 111 */     addAlias("TWD", 37);
/* 112 */     addAlias("THB", 38);
/* 113 */     addAlias("SAB", 39);
/* 114 */     addAlias("OMR", 40);
/* 115 */     addAlias("QAR", 41);
/* 116 */     addAlias("SAU", 42);
/* 117 */     addAlias("SAG", 43);
/* 118 */     addAlias("EEK", 44);
/* 119 */     addAlias("EGP", 45);
/* 120 */     addAlias("ERN", 46);
/* 121 */     addAlias("ETB", 47);
/* 122 */     addAlias("BZD", 48);
/* 123 */     addAlias("AED", 49);
/* 124 */     addAlias("FJD", 50);
/* 125 */     addAlias("FKP", 51);
/* 126 */     addAlias("ALL", 52);
/* 127 */     addAlias("GEL", 53);
/* 128 */     addAlias("GHS", 54);
/* 129 */     addAlias("GIP", 55);
/* 130 */     addAlias("GMD", 56);
/* 131 */     addAlias("GNF", 57);
/* 132 */     addAlias("GTQ", 58);
/* 133 */     addAlias("GYD", 59);
/* 134 */     addAlias("AZN", 60);
/* 135 */     addAlias("HNL", 61);
/* 136 */     addAlias("HRK", 62);
/* 137 */     addAlias("HTG", 63);
/* 138 */     addAlias("BAM", 64);
/* 139 */     addAlias("AWG", 65);
/* 140 */     addAlias("CDF", 66);
/* 141 */     addAlias("COU", 67);
/* 142 */     addAlias("AMD", 68);
/* 143 */     addAlias("IRR", 69);
/* 144 */     addAlias("BND", 70);
/* 145 */     addAlias("JMD", 71);
/* 146 */     addAlias("JOD", 72);
/* 147 */     addAlias("ANG", 73);
/* 148 */     addAlias("KES", 74);
/* 149 */     addAlias("KGS", 75);
/* 150 */     addAlias("KHR", 76);
/* 151 */     addAlias("KMF", 77);
/* 152 */     addAlias("KPW", 78);
/* 153 */     addAlias("CRC", 79);
/* 154 */     addAlias("AOA", 80);
/* 155 */     addAlias("KYD", 81);
/* 156 */     addAlias("KZT", 82);
/* 157 */     addAlias("LAK", 83);
/* 158 */     addAlias("LBP", 84);
/* 159 */     addAlias("LKR", 85);
/* 160 */     addAlias("LRD", 86);
/* 161 */     addAlias("LSL", 87);
/* 162 */     addAlias("LTL", 88);
/* 163 */     addAlias("LVL", 89);
/* 164 */     addAlias("LYD", 90);
/* 165 */     addAlias("MAD", 91);
/* 166 */     addAlias("MDL", 92);
/* 167 */     addAlias("MGA", 93);
/* 168 */     addAlias("MKD", 94);
/* 169 */     addAlias("MMK", 95);
/* 170 */     addAlias("MNT", 96);
/* 171 */     addAlias("MOP", 97);
/* 172 */     addAlias("MRO", 98);
/* 173 */     addAlias("MUR", 99);
/* 174 */     addAlias("MVR", 100);
/* 175 */     addAlias("MWK", 101);
/* 176 */     addAlias("BOB", 102);
/* 177 */     addAlias("MXV", 103);
/* 178 */     addAlias("CUC", 104);
/* 179 */     addAlias("MZN", 105);
/* 180 */     addAlias("NAD", 106);
/* 181 */     addAlias("NGN", 107);
/* 182 */     addAlias("NIO", 108);
/* 183 */     addAlias("BBD", 109);
/* 184 */     addAlias("NPR", 110);
/* 185 */     addAlias("BDT", 111);
/* 186 */     addAlias("DJF", 112);
/* 187 */     addAlias("PAB", 113);
/* 188 */     addAlias("PEN", 114);
/* 189 */     addAlias("PGK", 115);
/* 190 */     addAlias("PHP", 116);
/* 191 */     addAlias("PKR", 117);
/* 192 */     addAlias("BGN", 118);
/* 193 */     addAlias("PYG", 119);
/* 194 */     addAlias("KWD", 120);
/* 195 */     addAlias("BOV", 121);
/* 196 */     addAlias("RSD", 122);
/* 197 */     addAlias("BSD", 123);
/* 198 */     addAlias("RWF", 124);
/* 199 */     addAlias("IQD", 125);
/* 200 */     addAlias("DZD", 126);
/* 201 */     addAlias("SAR", 127);
/* 202 */     addAlias("DOP", 128);
/* 203 */     addAlias("SBD", 129);
/* 204 */     addAlias("SCR", 130);
/* 205 */     addAlias("SDG", 131);
/* 206 */     addAlias("BHD", 132);
/* 207 */     addAlias("BIF", 133);
/* 208 */     addAlias("SHP", 134);
/* 209 */     addAlias("IDR", 135);
/* 210 */     addAlias("SLL", 136);
/* 211 */     addAlias("SOS", 137);
/* 212 */     addAlias("SRD", 138);
/* 213 */     addAlias("STD", 139);
/* 214 */     addAlias("SVC", 140);
/* 215 */     addAlias("SYP", 141);
/* 216 */     addAlias("SZL", 142);
/* 217 */     addAlias("CNY", 143);
/* 218 */     addAlias("TJS", 144);
/* 219 */     addAlias("TMT", 145);
/* 220 */     addAlias("TND", 146);
/* 221 */     addAlias("TOP", 147);
/* 222 */     addAlias("BYR", 148);
/* 223 */     addAlias("TTD", 149);
/* 224 */     addAlias("CUP", 150);
/* 225 */     addAlias("TZS", 151);
/* 226 */     addAlias("UAH", 152);
/* 227 */     addAlias("UGX", 153);
/* 228 */     addAlias("AFN", 154);
/* 229 */     addAlias("USN", 155);
/* 230 */     addAlias("CVE", 156);
/* 231 */     addAlias("UYI", 157);
/* 232 */     addAlias("UYU", 158);
/* 233 */     addAlias("UZS", 159);
/* 234 */     addAlias("CLF", 160);
/* 235 */     addAlias("VEF", 161);
/* 236 */     addAlias("VND", 162);
/* 237 */     addAlias("VUV", 163);
/* 238 */     addAlias("WST", 164);
/* 239 */     addAlias("BTN", 165);
/* 240 */     addAlias("BWP", 166);
/* 241 */     addAlias("XBA", 167);
/* 242 */     addAlias("XBB", 168);
/* 243 */     addAlias("XBC", 169);
/* 244 */     addAlias("XBD", 170);
/* 245 */     addAlias("XDR", 171);
/* 246 */     addAlias("XEU", 172);
/* 247 */     addAlias("XFU", 173);
/* 248 */     addAlias("XPD", 174);
/* 249 */     addAlias("XPT", 175);
/* 250 */     addAlias("XXX", 176);
/* 251 */     addAlias("YER", 177);
/* 252 */     addAlias("BMD", 178);
/* 253 */     addAlias("ZMK", 179);
/* 254 */     addAlias("ZWL", 180);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.CurrencyMarket
 * JD-Core Version:    0.6.0
 */