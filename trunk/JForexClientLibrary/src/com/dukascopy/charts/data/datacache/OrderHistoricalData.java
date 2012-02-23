/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.transport.util.Bits;
/*     */ import com.dukascopy.transport.util.Bits.BitsSerializable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class OrderHistoricalData
/*     */   implements Bits.BitsSerializable
/*     */ {
/*     */   public static final int VERSION = 3;
/*     */   protected static final String HEADER = "OhD";
/*  27 */   public static final BigDecimal NEG_ONE = new BigDecimal("-1");
/*     */   private String mergedToGroupId;
/*     */   private long mergedToTime;
/*     */   private String orderGroupId;
/*     */   protected Map<String, CloseData> closeDataMap;
/*     */   protected OpenData entryOrder;
/*     */   protected List<OpenData> pendingOrders;
/*     */   private boolean opened;
/*     */   private boolean closed;
/*     */   private boolean isOco;
/*     */   private long historyStart;
/*     */   private long historyEnd;
/* 533 */   private BigDecimal commission = BigDecimal.ZERO;
/*     */ 
/*     */   public OrderHistoricalData() {
/* 536 */     this.mergedToTime = -9223372036854775808L;
/* 537 */     this.closeDataMap = new LinkedHashMap();
/* 538 */     this.pendingOrders = new ArrayList(1);
/* 539 */     this.historyStart = 9223372036854775807L;
/* 540 */     this.historyEnd = -9223372036854775808L;
/*     */   }
/*     */ 
/*     */   public OrderHistoricalData(OrderHistoricalData orderHistoricalData) {
/* 544 */     this.mergedToGroupId = orderHistoricalData.mergedToGroupId;
/* 545 */     this.mergedToTime = orderHistoricalData.mergedToTime;
/* 546 */     this.orderGroupId = orderHistoricalData.orderGroupId;
/* 547 */     setCloseDataMap(orderHistoricalData.closeDataMap);
/* 548 */     setEntryOrder(orderHistoricalData.entryOrder);
/* 549 */     setPendingOrders(orderHistoricalData.pendingOrders);
/* 550 */     this.opened = orderHistoricalData.opened;
/* 551 */     this.closed = orderHistoricalData.closed;
/* 552 */     this.isOco = orderHistoricalData.isOco;
/* 553 */     this.historyStart = orderHistoricalData.historyStart;
/* 554 */     this.historyEnd = orderHistoricalData.historyEnd;
/* 555 */     this.commission = orderHistoricalData.commission;
/*     */   }
/*     */ 
/*     */   public String getMergedToGroupId() {
/* 559 */     return this.mergedToGroupId;
/*     */   }
/*     */ 
/*     */   protected void setMergedToGroupId(String mergedToGroupId) {
/* 563 */     this.mergedToGroupId = mergedToGroupId;
/*     */   }
/*     */ 
/*     */   public long getMergedToTime() {
/* 567 */     return this.mergedToTime;
/*     */   }
/*     */ 
/*     */   protected void setMergedToTime(long mergedToTime) {
/* 571 */     this.mergedToTime = mergedToTime;
/*     */   }
/*     */ 
/*     */   public String getOrderGroupId() {
/* 575 */     return this.orderGroupId;
/*     */   }
/*     */ 
/*     */   protected void setOrderGroupId(String orderGroupId) {
/* 579 */     this.orderGroupId = orderGroupId;
/*     */   }
/*     */ 
/*     */   public Map<String, CloseData> getCloseDataMap() {
/* 583 */     return Collections.unmodifiableMap(this.closeDataMap);
/*     */   }
/*     */ 
/*     */   protected void setCloseDataMap(Map<String, CloseData> closeDataMap) {
/* 587 */     LinkedHashMap closeDatas = new LinkedHashMap(closeDataMap.size());
/* 588 */     for (Map.Entry entry : closeDataMap.entrySet()) {
/* 589 */       CloseData closeData = (CloseData)entry.getValue();
/* 590 */       if (closeData.getClass().equals(CloseData.class))
/* 591 */         closeDatas.put(entry.getKey(), closeData);
/*     */       else {
/* 593 */         closeDatas.put(entry.getKey(), new CloseData(closeData));
/*     */       }
/*     */     }
/* 596 */     this.closeDataMap = closeDatas;
/*     */   }
/*     */ 
/*     */   public OpenData getEntryOrder() {
/* 600 */     return this.entryOrder;
/*     */   }
/*     */ 
/*     */   protected void setEntryOrder(OpenData entryOrder) {
/* 604 */     this.entryOrder = (entryOrder == null ? null : new OpenData(entryOrder));
/*     */   }
/*     */ 
/*     */   public List<OpenData> getPendingOrders() {
/* 608 */     return Collections.unmodifiableList(this.pendingOrders);
/*     */   }
/*     */ 
/*     */   protected void setPendingOrders(List<OpenData> pendingOrders) {
/* 612 */     ArrayList orders = new ArrayList(pendingOrders.size());
/* 613 */     for (OpenData openData : pendingOrders) {
/* 614 */       if (openData.getClass().equals(OpenData.class))
/* 615 */         orders.add(openData);
/*     */       else {
/* 617 */         orders.add(new OpenData(openData));
/*     */       }
/*     */     }
/* 620 */     this.pendingOrders = orders;
/*     */   }
/*     */ 
/*     */   public boolean isOpened() {
/* 624 */     return this.opened;
/*     */   }
/*     */ 
/*     */   protected void setOpened(boolean opened) {
/* 628 */     this.opened = opened;
/*     */   }
/*     */ 
/*     */   public boolean isClosed() {
/* 632 */     return this.closed;
/*     */   }
/*     */ 
/*     */   protected void setClosed(boolean closed) {
/* 636 */     this.closed = closed;
/*     */   }
/*     */ 
/*     */   public boolean isOco() {
/* 640 */     return this.isOco;
/*     */   }
/*     */ 
/*     */   protected void setOco(boolean oco) {
/* 644 */     this.isOco = oco;
/*     */   }
/*     */ 
/*     */   public long getHistoryStart() {
/* 648 */     return this.historyStart;
/*     */   }
/*     */ 
/*     */   protected void setHistoryStart(long historyStart) {
/* 652 */     this.historyStart = historyStart;
/*     */   }
/*     */ 
/*     */   public long getHistoryEnd() {
/* 656 */     return this.historyEnd;
/*     */   }
/*     */ 
/*     */   protected void setHistoryEnd(long historyEnd) {
/* 660 */     this.historyEnd = historyEnd;
/*     */   }
/*     */ 
/*     */   public BigDecimal getCommission()
/*     */   {
/* 667 */     return this.commission;
/*     */   }
/*     */ 
/*     */   public void setCommission(BigDecimal commission)
/*     */   {
/* 674 */     this.commission = (commission == null ? BigDecimal.ZERO : commission);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 679 */     StringBuilder str = new StringBuilder();
/* 680 */     str.append("Order info:\n");
/* 681 */     str.append("orderGroupId: ").append(this.orderGroupId).append("\n");
/* 682 */     str.append("opened: ").append(this.opened).append("\n");
/* 683 */     str.append("closed: ").append(this.closed).append("\n");
/* 684 */     str.append("isOco: ").append(this.isOco).append("\n");
/* 685 */     str.append("mergedToGroupId: ").append(this.mergedToGroupId).append("\n");
/* 686 */     str.append("mergedToTime: ").append(this.mergedToTime).append("\n");
/*     */ 
/* 688 */     str.append("pendingOrders: {");
/* 689 */     for (OpenData openData : this.pendingOrders) {
/* 690 */       str.append("\n");
/* 691 */       StringBuilder strOrder = new StringBuilder();
/* 692 */       strOrder.append("[");
/* 693 */       strOrder.append(openData.toString());
/* 694 */       strOrder.append("]");
/* 695 */       ident(strOrder.toString(), str);
/*     */     }
/* 697 */     str.append("}\n");
/* 698 */     str.append("entryOrder: ");
/* 699 */     if (this.entryOrder != null) {
/* 700 */       str.append("\n");
/* 701 */       ident(this.entryOrder.toString(), str);
/*     */     } else {
/* 703 */       str.append(this.entryOrder);
/*     */     }
/* 705 */     str.append("\n");
/* 706 */     str.append("close data map: {");
/* 707 */     for (Map.Entry entry : this.closeDataMap.entrySet()) {
/* 708 */       String orderId = (String)entry.getKey();
/* 709 */       CloseData closeData = (CloseData)entry.getValue();
/* 710 */       str.append("\n");
/* 711 */       StringBuilder strOrder = new StringBuilder();
/* 712 */       strOrder.append("orderId: ").append(orderId).append("->").append("[");
/* 713 */       strOrder.append("\n");
/* 714 */       ident(closeData.toString(), strOrder);
/* 715 */       strOrder.append("]");
/* 716 */       ident(strOrder.toString(), str);
/*     */     }
/* 718 */     str.append("}");
/* 719 */     str.append("\n");
/* 720 */     str.append("commission: ").append(this.commission);
/* 721 */     return str.toString();
/*     */   }
/*     */ 
/*     */   public static void ident(String str, StringBuilder strBuff) {
/* 725 */     StringTokenizer strTok = new StringTokenizer(str, "\n");
/* 726 */     while (strTok.hasMoreTokens()) {
/* 727 */       String tok = strTok.nextToken();
/* 728 */       strBuff.append("    ").append(tok);
/* 729 */       if (strTok.hasMoreTokens())
/* 730 */         strBuff.append("\n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getOpenPriceText(String orderGroupId, boolean isOco, IEngine.OrderCommand orderCommand, BigDecimal openPrice, BigDecimal openSlippage)
/*     */   {
/* 736 */     if (orderCommand == IEngine.OrderCommand.PLACE_BID)
/* 737 */       return new StringBuilder().append("BID ").append(openPrice.toPlainString()).toString();
/* 738 */     if (orderCommand == IEngine.OrderCommand.PLACE_OFFER) {
/* 739 */       return new StringBuilder().append("OFFER ").append(openPrice.toPlainString()).toString();
/*     */     }
/*     */ 
/* 742 */     StringBuilder openPriceText = new StringBuilder(orderGroupId);
/* 743 */     if (isOco) {
/* 744 */       openPriceText.append(" OCO");
/*     */     }
/* 746 */     openPriceText.append(" ENTRY");
/* 747 */     if (orderCommand.isConditional()) {
/* 748 */       switch (1.$SwitchMap$com$dukascopy$api$IEngine$OrderCommand[orderCommand.ordinal()]) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/* 753 */         if ((openSlippage != null) && (openSlippage.compareTo(BigDecimal.ZERO) == 0))
/* 754 */           openPriceText.append(" LIMIT");
/*     */         else {
/* 756 */           openPriceText.append(" MIT");
/*     */         }
/* 758 */         break;
/*     */       case 5:
/* 760 */         openPriceText.append(" ASK ≥");
/* 761 */         break;
/*     */       case 6:
/* 763 */         openPriceText.append(" BID ≥");
/* 764 */         break;
/*     */       case 7:
/* 766 */         openPriceText.append(" BID ≤");
/* 767 */         break;
/*     */       case 8:
/* 769 */         openPriceText.append(" ASK ≤");
/*     */       }
/*     */ 
/* 772 */       openPriceText.append(" ").append(openPrice.stripTrailingZeros().toPlainString());
/* 773 */       if (openSlippage != null) {
/* 774 */         BigDecimal priceWithSlippage = orderCommand.isLong() ? openPrice.add(openSlippage) : openPrice.subtract(openSlippage);
/* 775 */         openPriceText.append(" (").append(priceWithSlippage.stripTrailingZeros().toPlainString()).append(")");
/*     */       }
/* 777 */       return openPriceText.toString();
/*     */     }
/* 779 */     return openPriceText.append(" @ MARKET").toString();
/*     */   }
/*     */ 
/*     */   public static String getStopLossPriceText(String stopLossOrderId, IEngine.OrderCommand orderCommand, BigDecimal stopLossPrice, BigDecimal stopLossSlippage, boolean stopLossByBid)
/*     */   {
/* 784 */     StringBuilder openPriceText = new StringBuilder(stopLossOrderId);
/* 785 */     openPriceText.append(" SL");
/* 786 */     if (stopLossByBid)
/* 787 */       openPriceText.append(" BID");
/*     */     else {
/* 789 */       openPriceText.append(" ASK");
/*     */     }
/* 791 */     if (orderCommand.isLong())
/* 792 */       openPriceText.append(" ≤");
/*     */     else {
/* 794 */       openPriceText.append(" ≥");
/*     */     }
/* 796 */     openPriceText.append(" ").append(stopLossPrice.stripTrailingZeros().toPlainString());
/* 797 */     if (stopLossSlippage != null) {
/* 798 */       BigDecimal priceWithSlippage = orderCommand.isLong() ? stopLossPrice.subtract(stopLossSlippage) : stopLossPrice.add(stopLossSlippage);
/* 799 */       openPriceText.append(" (").append(priceWithSlippage.stripTrailingZeros().toPlainString()).append(")");
/*     */     }
/* 801 */     return openPriceText.toString();
/*     */   }
/*     */ 
/*     */   public static String getTakeProfitPriceText(String takeProfitOrderId, IEngine.OrderCommand orderCommand, BigDecimal takeProfitPrice, BigDecimal takeProfitSlippage) {
/* 805 */     StringBuilder priceText = new StringBuilder(takeProfitOrderId);
/* 806 */     priceText.append(" TP ");
/* 807 */     priceText.append(takeProfitPrice.stripTrailingZeros().toPlainString());
/* 808 */     if (takeProfitSlippage != null) {
/* 809 */       BigDecimal priceWithSlippage = orderCommand.isLong() ? takeProfitPrice.subtract(takeProfitSlippage) : takeProfitPrice.add(takeProfitSlippage);
/* 810 */       priceText.append(" (").append(priceWithSlippage.stripTrailingZeros().toPlainString()).append(")");
/*     */     }
/* 812 */     return priceText.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 817 */     if (this == o) return true;
/* 818 */     if ((o == null) || (getClass() != o.getClass())) return false;
/*     */ 
/* 820 */     OrderHistoricalData that = (OrderHistoricalData)o;
/*     */ 
/* 822 */     if (this.closed != that.closed) return false;
/* 823 */     if (this.historyEnd != that.historyEnd) return false;
/* 824 */     if (this.historyStart != that.historyStart) return false;
/* 825 */     if (this.isOco != that.isOco) return false;
/* 826 */     if (this.opened != that.opened) return false;
/* 827 */     if (this.closeDataMap != null ? !this.closeDataMap.equals(that.closeDataMap) : that.closeDataMap != null) return false;
/* 828 */     if (this.entryOrder != null ? !this.entryOrder.equals(that.entryOrder) : that.entryOrder != null) return false;
/* 829 */     if (this.mergedToGroupId != null ? !this.mergedToGroupId.equals(that.mergedToGroupId) : that.mergedToGroupId != null) return false;
/* 830 */     if (this.mergedToTime != that.mergedToTime) return false;
/* 831 */     if (this.orderGroupId != null ? !this.orderGroupId.equals(that.orderGroupId) : that.orderGroupId != null) return false;
/* 832 */     if (this.pendingOrders != null ? !this.pendingOrders.equals(that.pendingOrders) : that.pendingOrders != null) {
/* 833 */       return false;
/*     */     }
/*     */ 
/* 836 */     return this.commission != null ? this.commission.equals(that.commission) : that.commission == null;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 843 */     int result = this.mergedToGroupId != null ? this.mergedToGroupId.hashCode() : 0;
/* 844 */     result = 31 * result + (int)(this.mergedToTime ^ this.mergedToTime >>> 32);
/* 845 */     result = 31 * result + (this.orderGroupId != null ? this.orderGroupId.hashCode() : 0);
/* 846 */     result = 31 * result + (this.closeDataMap != null ? this.closeDataMap.hashCode() : 0);
/* 847 */     result = 31 * result + (this.entryOrder != null ? this.entryOrder.hashCode() : 0);
/* 848 */     result = 31 * result + (this.pendingOrders != null ? this.pendingOrders.hashCode() : 0);
/* 849 */     result = 31 * result + (this.opened ? 1 : 0);
/* 850 */     result = 31 * result + (this.closed ? 1 : 0);
/* 851 */     result = 31 * result + (this.isOco ? 1 : 0);
/* 852 */     result = 31 * result + (int)(this.historyStart ^ this.historyStart >>> 32);
/* 853 */     result = 31 * result + (int)(this.historyEnd ^ this.historyEnd >>> 32);
/* 854 */     result = 31 * result + (this.commission != null ? this.commission.hashCode() : 0);
/* 855 */     return result;
/*     */   }
/*     */ 
/*     */   public void writeObject(OutputStream os) throws IOException {
/* 859 */     os.write("OhD".getBytes());
/* 860 */     os.write(3);
/* 861 */     Bits.writeObject(os, this.mergedToGroupId);
/* 862 */     os.write(Bits.longBytes(this.mergedToTime));
/* 863 */     Bits.writeObject(os, this.orderGroupId);
/* 864 */     Bits.writeObject(os, this.closeDataMap);
/* 865 */     Bits.writeObject(os, this.entryOrder);
/* 866 */     Bits.writeObject(os, this.pendingOrders);
/* 867 */     os.write(Bits.booleanBytes(this.opened));
/* 868 */     os.write(Bits.booleanBytes(this.closed));
/* 869 */     os.write(Bits.booleanBytes(this.isOco));
/* 870 */     os.write(Bits.longBytes(this.historyStart));
/* 871 */     os.write(Bits.longBytes(this.historyEnd));
/* 872 */     Bits.writeObject(os, this.commission);
/*     */   }
/*     */ 
/*     */   public void readObject(InputStream is) throws IOException
/*     */   {
/* 877 */     byte[] header = Bits.read(is, new byte["OhD".length()]);
/* 878 */     if (!Arrays.equals(header, "OhD".getBytes())) {
/* 879 */       throw new StreamCorruptedException("Deserialization error, unknown header");
/*     */     }
/* 881 */     int version = is.read();
/* 882 */     if (version != 3) {
/* 883 */       throw new StreamCorruptedException(new StringBuilder().append("Versions doesn't match, stream version [").append(version).append("], class version [").append(3).append("]").toString());
/*     */     }
/* 885 */     this.mergedToGroupId = ((String)Bits.readObject(is, String.class));
/* 886 */     this.mergedToTime = Bits.getLong(Bits.read(is, new byte[8]));
/* 887 */     this.orderGroupId = ((String)Bits.readObject(is, String.class));
/* 888 */     this.closeDataMap = ((Map)Bits.readObject(is, LinkedHashMap.class, String.class, CloseData.class));
/* 889 */     this.entryOrder = ((OpenData)Bits.readObject(is, OpenData.class));
/* 890 */     this.pendingOrders = ((List)Bits.readObject(is, ArrayList.class, OpenData.class));
/* 891 */     this.opened = Bits.getBoolean((byte)is.read());
/* 892 */     this.closed = Bits.getBoolean((byte)is.read());
/* 893 */     this.isOco = Bits.getBoolean((byte)is.read());
/* 894 */     this.historyStart = Bits.getLong(Bits.read(is, new byte[8]));
/* 895 */     this.historyEnd = Bits.getLong(Bits.read(is, new byte[8]));
/* 896 */     this.commission = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/*     */   }
/*     */ 
/*     */   public static class OpenData
/*     */     implements Bits.BitsSerializable
/*     */   {
/*     */     protected static final String HEADER = "OHD.OD";
/*     */     private String orderId;
/*     */     private BigDecimal openPrice;
/*     */     private long creationTime;
/*     */     private long fillTime;
/*     */     private BigDecimal openSlippage;
/*     */     private IEngine.OrderCommand side;
/*     */     private BigDecimal stopLossPrice;
/*     */     private BigDecimal stopLossSlippage;
/*     */     private String stopLossOrderId;
/*     */     private boolean stopLossByBid;
/*     */     private BigDecimal trailingStep;
/*     */     private BigDecimal takeProfitPrice;
/*     */     private BigDecimal takeProfitSlippage;
/*     */     private String takeProfitOrderId;
/*     */     private long goodTillTime;
/*     */     private boolean executing;
/*     */     private String label;
/*     */     private String comment;
/*     */     private BigDecimal amount;
/*     */     private String[] mergedFrom;
/*     */ 
/*     */     public OpenData()
/*     */     {
/* 167 */       this.openPrice = OrderHistoricalData.NEG_ONE;
/* 168 */       this.stopLossPrice = OrderHistoricalData.NEG_ONE;
/* 169 */       this.takeProfitPrice = OrderHistoricalData.NEG_ONE;
/* 170 */       this.amount = BigDecimal.ZERO;
/*     */     }
/*     */ 
/*     */     public OpenData(OpenData openData) {
/* 174 */       this.orderId = openData.orderId;
/* 175 */       this.openPrice = openData.openPrice;
/* 176 */       this.creationTime = openData.creationTime;
/* 177 */       this.fillTime = openData.fillTime;
/* 178 */       this.openSlippage = openData.openSlippage;
/* 179 */       this.side = openData.side;
/* 180 */       this.stopLossPrice = openData.stopLossPrice;
/* 181 */       this.stopLossSlippage = openData.stopLossSlippage;
/* 182 */       this.stopLossOrderId = openData.stopLossOrderId;
/* 183 */       this.stopLossByBid = openData.stopLossByBid;
/* 184 */       this.trailingStep = openData.trailingStep;
/* 185 */       this.takeProfitPrice = openData.takeProfitPrice;
/* 186 */       this.takeProfitSlippage = openData.takeProfitSlippage;
/* 187 */       this.takeProfitOrderId = openData.takeProfitOrderId;
/* 188 */       this.goodTillTime = openData.goodTillTime;
/* 189 */       this.executing = openData.executing;
/* 190 */       this.label = openData.label;
/* 191 */       this.comment = openData.comment;
/* 192 */       this.amount = openData.amount;
/* 193 */       if (openData.mergedFrom == null)
/* 194 */         this.mergedFrom = null;
/*     */       else
/* 196 */         this.mergedFrom = ((String[])Arrays.copyOf(openData.mergedFrom, openData.mergedFrom.length));
/*     */     }
/*     */ 
/*     */     public String getOrderId()
/*     */     {
/* 201 */       return this.orderId;
/*     */     }
/*     */ 
/*     */     protected void setOrderId(String orderId) {
/* 205 */       this.orderId = orderId;
/*     */     }
/*     */ 
/*     */     public BigDecimal getOpenPrice() {
/* 209 */       return this.openPrice;
/*     */     }
/*     */ 
/*     */     protected void setOpenPrice(BigDecimal openPrice) {
/* 213 */       this.openPrice = openPrice;
/*     */     }
/*     */ 
/*     */     public long getCreationTime() {
/* 217 */       return this.creationTime;
/*     */     }
/*     */ 
/*     */     protected void setCreationTime(long creationTime) {
/* 221 */       this.creationTime = creationTime;
/*     */     }
/*     */ 
/*     */     public long getFillTime() {
/* 225 */       return this.fillTime;
/*     */     }
/*     */ 
/*     */     protected void setFillTime(long fillTime) {
/* 229 */       this.fillTime = fillTime;
/*     */     }
/*     */ 
/*     */     public BigDecimal getOpenSlippage() {
/* 233 */       return this.openSlippage;
/*     */     }
/*     */ 
/*     */     protected void setOpenSlippage(BigDecimal openSlippage) {
/* 237 */       this.openSlippage = openSlippage;
/*     */     }
/*     */ 
/*     */     public IEngine.OrderCommand getSide() {
/* 241 */       return this.side;
/*     */     }
/*     */ 
/*     */     protected void setSide(IEngine.OrderCommand side) {
/* 245 */       this.side = side;
/*     */     }
/*     */ 
/*     */     public BigDecimal getStopLossPrice() {
/* 249 */       return this.stopLossPrice;
/*     */     }
/*     */ 
/*     */     protected void setStopLossPrice(BigDecimal stopLossPrice) {
/* 253 */       this.stopLossPrice = stopLossPrice;
/*     */     }
/*     */ 
/*     */     public BigDecimal getStopLossSlippage() {
/* 257 */       return this.stopLossSlippage;
/*     */     }
/*     */ 
/*     */     protected void setStopLossSlippage(BigDecimal stopLossSlippage) {
/* 261 */       this.stopLossSlippage = stopLossSlippage;
/*     */     }
/*     */ 
/*     */     public String getStopLossOrderId() {
/* 265 */       return this.stopLossOrderId;
/*     */     }
/*     */ 
/*     */     protected void setStopLossOrderId(String stopLossOrderId) {
/* 269 */       this.stopLossOrderId = stopLossOrderId;
/*     */     }
/*     */ 
/*     */     public boolean isStopLossByBid() {
/* 273 */       return this.stopLossByBid;
/*     */     }
/*     */ 
/*     */     protected void setStopLossByBid(boolean stopLossByBid) {
/* 277 */       this.stopLossByBid = stopLossByBid;
/*     */     }
/*     */ 
/*     */     public BigDecimal getTrailingStep() {
/* 281 */       return this.trailingStep;
/*     */     }
/*     */ 
/*     */     protected void setTrailingStep(BigDecimal trailingStep) {
/* 285 */       this.trailingStep = trailingStep;
/*     */     }
/*     */ 
/*     */     public BigDecimal getTakeProfitPrice() {
/* 289 */       return this.takeProfitPrice;
/*     */     }
/*     */ 
/*     */     protected void setTakeProfitPrice(BigDecimal takeProfitPrice) {
/* 293 */       this.takeProfitPrice = takeProfitPrice;
/*     */     }
/*     */ 
/*     */     public BigDecimal getTakeProfitSlippage() {
/* 297 */       return this.takeProfitSlippage;
/*     */     }
/*     */ 
/*     */     protected void setTakeProfitSlippage(BigDecimal takeProfitSlippage) {
/* 301 */       this.takeProfitSlippage = takeProfitSlippage;
/*     */     }
/*     */ 
/*     */     public String getTakeProfitOrderId() {
/* 305 */       return this.takeProfitOrderId;
/*     */     }
/*     */ 
/*     */     protected void setTakeProfitOrderId(String takeProfitOrderId) {
/* 309 */       this.takeProfitOrderId = takeProfitOrderId;
/*     */     }
/*     */ 
/*     */     public long getGoodTillTime() {
/* 313 */       return this.goodTillTime;
/*     */     }
/*     */ 
/*     */     protected void setGoodTillTime(long goodTillTime) {
/* 317 */       this.goodTillTime = goodTillTime;
/*     */     }
/*     */ 
/*     */     public boolean isExecuting() {
/* 321 */       return this.executing;
/*     */     }
/*     */ 
/*     */     protected void setExecuting(boolean executing) {
/* 325 */       this.executing = executing;
/*     */     }
/*     */ 
/*     */     public String getLabel() {
/* 329 */       return this.label;
/*     */     }
/*     */ 
/*     */     protected void setLabel(String label) {
/* 333 */       this.label = label;
/*     */     }
/*     */ 
/*     */     public String getComment() {
/* 337 */       return this.comment;
/*     */     }
/*     */ 
/*     */     protected void setComment(String comment) {
/* 341 */       this.comment = comment;
/*     */     }
/*     */ 
/*     */     public BigDecimal getAmount() {
/* 345 */       return this.amount;
/*     */     }
/*     */ 
/*     */     protected void setAmount(BigDecimal amount) {
/* 349 */       this.amount = amount;
/*     */     }
/*     */ 
/*     */     public String[] getMergedFrom() {
/* 353 */       return this.mergedFrom;
/*     */     }
/*     */ 
/*     */     protected void setMergedFrom(String[] mergedFrom) {
/* 357 */       this.mergedFrom = mergedFrom;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 362 */       StringBuilder str = new StringBuilder();
/* 363 */       str.append("orderId: ").append(this.orderId).append("\n");
/* 364 */       str.append("label: ").append(this.label).append("\n");
/* 365 */       str.append("side: ").append(this.side).append("\n");
/* 366 */       str.append("openPrice: ").append(this.openPrice).append("\n");
/* 367 */       str.append("creationTime: ").append(this.creationTime).append("\n");
/* 368 */       str.append("fillTime: ").append(this.fillTime).append("\n");
/* 369 */       str.append("openSlippage: ").append(this.openSlippage).append("\n");
/* 370 */       str.append("amount: ").append(this.amount).append("\n");
/* 371 */       str.append("stopLossPrice: ").append(this.stopLossPrice).append("\n");
/* 372 */       str.append("stopLossSlippage: ").append(this.stopLossSlippage).append("\n");
/* 373 */       str.append("stopLossOrderId: ").append(this.stopLossOrderId).append("\n");
/* 374 */       str.append("stopLossByBid: ").append(this.stopLossByBid).append("\n");
/* 375 */       str.append("takeProfitPrice: ").append(this.takeProfitPrice).append("\n");
/* 376 */       str.append("takeProfitSlippage: ").append(this.takeProfitSlippage).append("\n");
/* 377 */       str.append("takeProfitOrderId: ").append(this.takeProfitOrderId).append("\n");
/* 378 */       str.append("goodTillTime: ").append(this.goodTillTime).append("\n");
/* 379 */       str.append("executing: ").append(this.executing).append("\n");
/* 380 */       str.append("comment: ").append(this.comment).append("\n");
/* 381 */       str.append("mergedFrom: ");
/* 382 */       if (this.mergedFrom != null) {
/* 383 */         str.append("{");
/* 384 */         for (String openData : this.mergedFrom) {
/* 385 */           str.append(openData);
/* 386 */           str.append(", ");
/*     */         }
/* 388 */         str.append("}");
/*     */       } else {
/* 390 */         str.append(this.mergedFrom);
/*     */       }
/* 392 */       return str.toString();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/* 397 */       if (this == o) return true;
/* 398 */       if ((o == null) || (getClass() != o.getClass())) return false;
/*     */ 
/* 400 */       OpenData openData = (OpenData)o;
/*     */ 
/* 402 */       if (this.creationTime != openData.creationTime) return false;
/* 403 */       if (this.executing != openData.executing) return false;
/* 404 */       if (this.fillTime != openData.fillTime) return false;
/* 405 */       if (this.goodTillTime != openData.goodTillTime) return false;
/* 406 */       if (this.stopLossByBid != openData.stopLossByBid) return false;
/* 407 */       if (this.amount != null ? !this.amount.equals(openData.amount) : openData.amount != null) return false;
/* 408 */       if (this.comment != null ? !this.comment.equals(openData.comment) : openData.comment != null) return false;
/* 409 */       if (this.label != null ? !this.label.equals(openData.label) : openData.label != null) return false;
/* 410 */       if (!Arrays.equals(this.mergedFrom, openData.mergedFrom)) return false;
/* 411 */       if (this.openPrice != null ? !this.openPrice.equals(openData.openPrice) : openData.openPrice != null) return false;
/* 412 */       if (this.openSlippage != null ? !this.openSlippage.equals(openData.openSlippage) : openData.openSlippage != null)
/* 413 */         return false;
/* 414 */       if (this.orderId != null ? !this.orderId.equals(openData.orderId) : openData.orderId != null) return false;
/* 415 */       if (this.side != openData.side) return false;
/* 416 */       if (this.stopLossOrderId != null ? !this.stopLossOrderId.equals(openData.stopLossOrderId) : openData.stopLossOrderId != null)
/* 417 */         return false;
/* 418 */       if (this.stopLossPrice != null ? !this.stopLossPrice.equals(openData.stopLossPrice) : openData.stopLossPrice != null)
/* 419 */         return false;
/* 420 */       if (this.stopLossSlippage != null ? !this.stopLossSlippage.equals(openData.stopLossSlippage) : openData.stopLossSlippage != null)
/* 421 */         return false;
/* 422 */       if (this.takeProfitOrderId != null ? !this.takeProfitOrderId.equals(openData.takeProfitOrderId) : openData.takeProfitOrderId != null)
/* 423 */         return false;
/* 424 */       if (this.takeProfitPrice != null ? !this.takeProfitPrice.equals(openData.takeProfitPrice) : openData.takeProfitPrice != null)
/* 425 */         return false;
/* 426 */       if (this.takeProfitSlippage != null ? !this.takeProfitSlippage.equals(openData.takeProfitSlippage) : openData.takeProfitSlippage != null) {
/* 427 */         return false;
/*     */       }
/* 429 */       return this.trailingStep != null ? this.trailingStep.equals(openData.trailingStep) : openData.trailingStep == null;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 436 */       int result = this.orderId != null ? this.orderId.hashCode() : 0;
/* 437 */       result = 31 * result + (this.openPrice != null ? this.openPrice.hashCode() : 0);
/* 438 */       result = 31 * result + (int)(this.creationTime ^ this.creationTime >>> 32);
/* 439 */       result = 31 * result + (int)(this.fillTime ^ this.fillTime >>> 32);
/* 440 */       result = 31 * result + (this.openSlippage != null ? this.openSlippage.hashCode() : 0);
/* 441 */       result = 31 * result + (this.side != null ? this.side.hashCode() : 0);
/* 442 */       result = 31 * result + (this.stopLossPrice != null ? this.stopLossPrice.hashCode() : 0);
/* 443 */       result = 31 * result + (this.stopLossSlippage != null ? this.stopLossSlippage.hashCode() : 0);
/* 444 */       result = 31 * result + (this.stopLossOrderId != null ? this.stopLossOrderId.hashCode() : 0);
/* 445 */       result = 31 * result + (this.stopLossByBid ? 1 : 0);
/* 446 */       result = 31 * result + (this.trailingStep != null ? this.trailingStep.hashCode() : 0);
/* 447 */       result = 31 * result + (this.takeProfitPrice != null ? this.takeProfitPrice.hashCode() : 0);
/* 448 */       result = 31 * result + (this.takeProfitSlippage != null ? this.takeProfitSlippage.hashCode() : 0);
/* 449 */       result = 31 * result + (this.takeProfitOrderId != null ? this.takeProfitOrderId.hashCode() : 0);
/* 450 */       result = 31 * result + (int)(this.goodTillTime ^ this.goodTillTime >>> 32);
/* 451 */       result = 31 * result + (this.executing ? 1 : 0);
/* 452 */       result = 31 * result + (this.label != null ? this.label.hashCode() : 0);
/* 453 */       result = 31 * result + (this.comment != null ? this.comment.hashCode() : 0);
/* 454 */       result = 31 * result + (this.amount != null ? this.amount.hashCode() : 0);
/* 455 */       result = 31 * result + (this.mergedFrom != null ? Arrays.hashCode(this.mergedFrom) : 0);
/* 456 */       return result;
/*     */     }
/*     */ 
/*     */     public void writeObject(OutputStream os) throws IOException {
/* 460 */       os.write("OHD.OD".getBytes());
/* 461 */       os.write(3);
/* 462 */       Bits.writeObject(os, this.orderId);
/* 463 */       Bits.writeObject(os, this.openPrice);
/* 464 */       os.write(Bits.longBytes(this.creationTime));
/* 465 */       os.write(Bits.longBytes(this.fillTime));
/* 466 */       Bits.writeObject(os, this.openSlippage);
/* 467 */       Bits.writeObject(os, this.side);
/* 468 */       Bits.writeObject(os, this.stopLossPrice);
/* 469 */       Bits.writeObject(os, this.stopLossSlippage);
/* 470 */       Bits.writeObject(os, this.stopLossOrderId);
/* 471 */       os.write(Bits.booleanBytes(this.stopLossByBid));
/* 472 */       Bits.writeObject(os, this.trailingStep);
/* 473 */       Bits.writeObject(os, this.takeProfitPrice);
/* 474 */       Bits.writeObject(os, this.takeProfitSlippage);
/* 475 */       Bits.writeObject(os, this.takeProfitOrderId);
/* 476 */       os.write(Bits.longBytes(this.goodTillTime));
/* 477 */       os.write(Bits.booleanBytes(this.executing));
/* 478 */       Bits.writeObject(os, this.label);
/* 479 */       Bits.writeObject(os, this.comment);
/* 480 */       Bits.writeObject(os, this.amount);
/* 481 */       Bits.writeObject(os, this.mergedFrom);
/*     */     }
/*     */ 
/*     */     public void readObject(InputStream is) throws IOException
/*     */     {
/* 486 */       byte[] header = Bits.read(is, new byte["OHD.OD".length()]);
/* 487 */       if (!Arrays.equals(header, "OHD.OD".getBytes())) {
/* 488 */         throw new StreamCorruptedException(new StringBuilder().append("Deserialization error, unknown header [").append(new String(header, "UTF-8")).append("]").toString());
/*     */       }
/* 490 */       int version = is.read();
/* 491 */       if (version != 3) {
/* 492 */         throw new StreamCorruptedException(new StringBuilder().append("Versions doesn't match, stream version [").append(version).append("], class version [").append(3).append("]").toString());
/*     */       }
/* 494 */       this.orderId = ((String)Bits.readObject(is, String.class));
/* 495 */       this.openPrice = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 496 */       this.creationTime = Bits.getLong(Bits.read(is, new byte[8]));
/* 497 */       this.fillTime = Bits.getLong(Bits.read(is, new byte[8]));
/* 498 */       this.openSlippage = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 499 */       this.side = ((IEngine.OrderCommand)Bits.readObject(is, IEngine.OrderCommand.class));
/* 500 */       this.stopLossPrice = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 501 */       this.stopLossSlippage = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 502 */       this.stopLossOrderId = ((String)Bits.readObject(is, String.class));
/* 503 */       this.stopLossByBid = Bits.getBoolean((byte)is.read());
/* 504 */       this.trailingStep = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 505 */       this.takeProfitPrice = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 506 */       this.takeProfitSlippage = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 507 */       this.takeProfitOrderId = ((String)Bits.readObject(is, String.class));
/* 508 */       this.goodTillTime = Bits.getLong(Bits.read(is, new byte[8]));
/* 509 */       this.executing = Bits.getBoolean((byte)is.read());
/* 510 */       this.label = ((String)Bits.readObject(is, String.class));
/* 511 */       this.comment = ((String)Bits.readObject(is, String.class));
/* 512 */       this.amount = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 513 */       this.mergedFrom = ((String[])Bits.readObject(is, [Ljava.lang.String.class));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class CloseData
/*     */     implements Bits.BitsSerializable
/*     */   {
/*     */     protected static final String HEADER = "OHD.CD";
/*     */     private long closeTime;
/*     */     private BigDecimal closePrice;
/*     */     private BigDecimal amount;
/*     */ 
/*     */     public CloseData()
/*     */     {
/*  38 */       this.closePrice = OrderHistoricalData.NEG_ONE;
/*  39 */       this.amount = BigDecimal.ZERO;
/*     */     }
/*     */ 
/*     */     public CloseData(CloseData closeData) {
/*  43 */       this.closeTime = closeData.closeTime;
/*  44 */       this.closePrice = closeData.closePrice;
/*  45 */       this.amount = closeData.amount;
/*     */     }
/*     */ 
/*     */     public long getCloseTime() {
/*  49 */       return this.closeTime;
/*     */     }
/*     */ 
/*     */     protected void setCloseTime(long closeTime) {
/*  53 */       this.closeTime = closeTime;
/*     */     }
/*     */ 
/*     */     public BigDecimal getClosePrice() {
/*  57 */       return this.closePrice;
/*     */     }
/*     */ 
/*     */     protected void setClosePrice(BigDecimal closePrice) {
/*  61 */       this.closePrice = closePrice;
/*     */     }
/*     */ 
/*     */     public BigDecimal getAmount() {
/*  65 */       return this.amount;
/*     */     }
/*     */ 
/*     */     protected void setAmount(BigDecimal amount) {
/*  69 */       this.amount = amount;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  74 */       StringBuilder str = new StringBuilder();
/*  75 */       str.append("closeTime: ").append(this.closeTime).append("\n");
/*  76 */       str.append("closePrice: ").append(this.closePrice).append("\n");
/*  77 */       str.append("amount: ").append(this.amount).append("\n");
/*     */ 
/*  85 */       return str.toString();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/*  90 */       if (this == o) return true;
/*  91 */       if ((o == null) || (getClass() != o.getClass())) return false;
/*     */ 
/*  93 */       CloseData closeData = (CloseData)o;
/*     */ 
/*  95 */       if (this.closeTime != closeData.closeTime) return false;
/*  96 */       if (this.amount != null ? !this.amount.equals(closeData.amount) : closeData.amount != null) return false;
/*     */ 
/*  98 */       return this.closePrice != null ? this.closePrice.equals(closeData.closePrice) : closeData.closePrice == null;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 105 */       int result = (int)(this.closeTime ^ this.closeTime >>> 32);
/* 106 */       result = 31 * result + (this.closePrice != null ? this.closePrice.hashCode() : 0);
/* 107 */       result = 31 * result + (this.amount != null ? this.amount.hashCode() : 0);
/* 108 */       return result;
/*     */     }
/*     */ 
/*     */     public void writeObject(OutputStream os) throws IOException {
/* 112 */       os.write("OHD.CD".getBytes());
/* 113 */       os.write(3);
/* 114 */       os.write(Bits.longBytes(this.closeTime));
/* 115 */       Bits.writeObject(os, this.closePrice);
/* 116 */       Bits.writeObject(os, this.amount);
/*     */     }
/*     */ 
/*     */     public void readObject(InputStream is) throws IOException
/*     */     {
/* 121 */       byte[] header = Bits.read(is, new byte["OHD.CD".length()]);
/* 122 */       if (!Arrays.equals(header, "OHD.CD".getBytes())) {
/* 123 */         throw new StreamCorruptedException("Deserialization error, unknown header");
/*     */       }
/* 125 */       int version = is.read();
/* 126 */       if (version != 3) {
/* 127 */         throw new StreamCorruptedException("Versions doesn't match, stream version [" + version + "], class version [" + 3 + "]");
/*     */       }
/* 129 */       this.closeTime = Bits.getLong(Bits.read(is, new byte[8]));
/* 130 */       this.closePrice = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/* 131 */       this.amount = ((BigDecimal)Bits.readObject(is, BigDecimal.class));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.OrderHistoricalData
 * JD-Core Version:    0.6.0
 */