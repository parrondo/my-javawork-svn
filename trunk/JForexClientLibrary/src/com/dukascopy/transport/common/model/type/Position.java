/*     */ package com.dukascopy.transport.common.model.type;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ 
/*     */ public class Position extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "position";
/*     */   public static final String POSITION_ID = "positionId";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String AMOUNT = "amount";
/*     */   public static final String POSITION_SIDE = "side";
/*     */   public static final String PROFIT_LOSS = "p_l";
/*     */   public static final String PRICE_OPEN = "priceOpen";
/*     */   public static final String PRICE_OPEN_MARGIN = "priceOpenMargin";
/*     */   public static final String PRICE_CURRENT = "priceCurrent";
/*     */   public static final String COMMISSION = "comm";
/*     */   private OrderGroupMessage orderGroup;
/*  28 */   private boolean isDisabled = false;
/*  29 */   private boolean isSelected = false;
/*  30 */   private boolean isInClosingState = false;
/*     */ 
/*     */   public Position()
/*     */   {
/*  34 */     setType("position");
/*     */   }
/*     */ 
/*     */   public Position(ProtocolMessage message)
/*     */     throws ParseException
/*     */   {
/*  43 */     this(message.toProtocolString());
/*     */   }
/*     */ 
/*     */   public Position(String s)
/*     */     throws ParseException
/*     */   {
/*  53 */     super(s);
/*  54 */     setType("position");
/*     */   }
/*     */ 
/*     */   public String getPositionID() {
/*  58 */     return getString("positionId");
/*     */   }
/*     */ 
/*     */   public void setPositionID(String posID) {
/*  62 */     put("positionId", posID);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  66 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/*  75 */     return getString("instrument").substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/*  84 */     return getString("instrument").substring(4);
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instr)
/*     */   {
/*  89 */     put("instrument", instr);
/*     */   }
/*     */ 
/*     */   public Money getAmount() {
/*  93 */     String amountString = getString("amount");
/*  94 */     if (amountString != null) {
/*  95 */       return new Money(amountString, getCurrencyPrimary());
/*     */     }
/*  97 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAmount(Money amount)
/*     */   {
/* 102 */     put("amount", amount.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public PositionSide getPositionSide() {
/* 106 */     return PositionSide.fromString(getString("side"));
/*     */   }
/*     */ 
/*     */   public void setPositionSide(PositionSide side) {
/* 110 */     put("side", side.asString());
/*     */   }
/*     */ 
/*     */   public Money getProfitLoss() {
/* 114 */     String profitLoss = getString("p_l");
/* 115 */     if (profitLoss != null) {
/* 116 */       return new Money(profitLoss, getCurrencySecondary());
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   public void setProfitLoss(Money profitLoss)
/*     */   {
/* 123 */     if (profitLoss != null)
/* 124 */       put("p_l", profitLoss.getValue().toPlainString());
/*     */     else
/* 126 */       put("p_l", null);
/*     */   }
/*     */ 
/*     */   public Money getPriceOpen()
/*     */   {
/* 131 */     String priceOpen = getString("priceOpen");
/* 132 */     if (priceOpen != null) {
/* 133 */       return new Money(priceOpen, getCurrencySecondary());
/*     */     }
/* 135 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPriceOpen(Money priceOpen)
/*     */   {
/* 140 */     if ((priceOpen != null) && (priceOpen.getValue() != null))
/* 141 */       put("priceOpen", priceOpen.getValue().toString());
/*     */     else
/* 143 */       put("priceOpen", null);
/*     */   }
/*     */ 
/*     */   public Money getPriceOpenMargin()
/*     */   {
/* 148 */     String priceOpen = getString("priceOpenMargin");
/* 149 */     if (priceOpen != null) {
/* 150 */       return new Money(priceOpen, getCurrencySecondary());
/*     */     }
/* 152 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPriceOpenMargin(Money priceOpenMargin)
/*     */   {
/* 157 */     if ((priceOpenMargin != null) && (priceOpenMargin.getValue() != null))
/* 158 */       put("priceOpenMargin", priceOpenMargin.getValue().toString());
/*     */     else
/* 160 */       put("priceOpenMargin", null);
/*     */   }
/*     */ 
/*     */   public Money getPriceCurrent()
/*     */   {
/* 165 */     String priceClose = getString("priceCurrent");
/* 166 */     if (priceClose != null) {
/* 167 */       return new Money(priceClose, getCurrencySecondary());
/*     */     }
/* 169 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPriceCurrent(Money priceCurrent)
/*     */   {
/* 174 */     put("priceCurrent", priceCurrent.getValue().toString());
/* 175 */     if (getPriceOpen() != null) {
/* 176 */       Money valueOpen = getPriceOpen().multiply(getAmount().getValue());
/* 177 */       Money valueClose = priceCurrent.multiply(getAmount().getValue());
/* 178 */       Money profitLoss = valueClose.subtract(valueOpen);
/* 179 */       if (getPositionSide() == PositionSide.SHORT) {
/* 180 */         profitLoss = new Money(profitLoss.getValue().negate(), profitLoss.getCurrency());
/*     */       }
/* 182 */       setProfitLoss(profitLoss);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Money getCommission() {
/* 187 */     String commission = getString("comm");
/* 188 */     if (commission != null) {
/*     */       try {
/* 190 */         return Money.of(commission);
/*     */       } catch (Exception e) {
/* 192 */         return null;
/*     */       }
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCommission(Money commission)
/*     */   {
/* 200 */     if (commission != null)
/* 201 */       put("comm", commission.toString());
/*     */     else
/* 203 */       put("comm", null);
/*     */   }
/*     */ 
/*     */   public void setOrderGroup(OrderGroupMessage orderGroup)
/*     */   {
/* 208 */     this.orderGroup = orderGroup;
/*     */   }
/*     */ 
/*     */   public OrderGroupMessage getOrderGroup() {
/* 212 */     return this.orderGroup;
/*     */   }
/*     */ 
/*     */   public boolean isDisabled() {
/* 216 */     return this.isDisabled;
/*     */   }
/*     */ 
/*     */   public void setDisabled(boolean disabled) {
/* 220 */     this.isDisabled = disabled;
/*     */   }
/*     */ 
/*     */   public boolean isSelected() {
/* 224 */     return this.isSelected;
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean selected) {
/* 228 */     this.isSelected = selected;
/*     */   }
/*     */ 
/*     */   public boolean isInClosingState() {
/* 232 */     return this.isInClosingState;
/*     */   }
/*     */ 
/*     */   public void setInClosingState(boolean inClosingState) {
/* 236 */     this.isInClosingState = inClosingState;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 240 */     return "ID: " + getString("positionId") + "; instrument: " + getString("instrument") + "; amount: " + getAmount().toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 249 */     if (o == null) return false;
/* 250 */     if (getPositionID() == null) return false;
/* 251 */     return getPositionID().equalsIgnoreCase(((Position)o).getPositionID());
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 255 */     return getPositionID() != null ? getPositionID().hashCode() : 0;
/*     */   }
/*     */ 
/*     */   public OrderMessage createClosingOrder(Money currentPrice) {
/* 259 */     OrderSide closeSide = getPositionSide() == PositionSide.LONG ? OrderSide.SELL : OrderSide.BUY;
/* 260 */     OrderMessage result = new OrderMessage();
/* 261 */     result.setAmount(getAmount());
/* 262 */     result.setInstrument(getInstrument());
/*     */ 
/* 264 */     result.setOrderDirection(OrderDirection.CLOSE);
/* 265 */     result.setOrderGroupId(getPositionID());
/* 266 */     result.setOrderState(OrderState.CREATED);
/* 267 */     result.setSide(closeSide);
/* 268 */     result.setPriceClient(currentPrice);
/* 269 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.model.type.Position
 * JD-Core Version:    0.6.0
 */