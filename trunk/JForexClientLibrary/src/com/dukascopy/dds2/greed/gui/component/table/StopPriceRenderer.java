/*     */ package com.dukascopy.dds2.greed.gui.component.table;
/*     */ 
/*     */ import com.dukascopy.dds2.calc.PriceUtil;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.awt.Color;
/*     */ import java.awt.SystemColor;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.MessageFormat;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StopPriceRenderer extends DefaultTableCellRenderer
/*     */ {
/*  29 */   private static final Logger LOGGER = LoggerFactory.getLogger(StopPriceRenderer.class);
/*     */ 
/*  31 */   private final DecimalFormat slippageFormat = new DecimalFormat("#,##0.0");
/*  32 */   private final DecimalFormat trailingStepFormat = new DecimalFormat("0.0000");
/*     */ 
/*     */   public void setValue(Object value) {
/*  35 */     if (null == value) {
/*  36 */       super.setValue(null);
/*  37 */       setText("");
/*  38 */       return;
/*     */     }
/*  40 */     if ((value instanceof OrderMessage)) {
/*  41 */       OrderMessage order = (OrderMessage)value;
/*  42 */       Money stopPrice = order.getPriceStop();
/*  43 */       if (stopPrice == null) {
/*  44 */         if (!order.isPlaceOffer()) {
/*  45 */           clearCell();
/*     */         } else {
/*  47 */           String[] str = GuiUtilsAndConstants.splitPriceForRendering(order.getPriceClient().getValue());
/*  48 */           Color bgColor = getBackground();
/*     */           try {
/*  50 */             setText(MessageFormat.format(bgColor.equals(SystemColor.textHighlight) ? "<html><font color=#ffffff>{0}{1}<font size=-2>{2}</font></font></html>" : "<html><font color=#000000>{0}{1}<font size=-2 color=\"gray\">{2}</font></font></html>", new Object[] { str[0], str[1], str[3] }));
/*     */           }
/*     */           catch (Exception e) {
/*  53 */             LOGGER.error(e.getMessage(), e);
/*  54 */             clearCell();
/*     */           }
/*  56 */           setHorizontalAlignment(4);
/*  57 */           return;
/*     */         }
/*     */       }
/*     */ 
/*  61 */       boolean stopDirExists = (null != order.getStopDirection()) && (null != stopPrice);
/*  62 */       if (stopDirExists) {
/*     */         try {
/*  64 */           StopDirection dir = order.getStopDirection();
/*  65 */           if ((null != order.getPriceTrailingLimit()) && (order.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) == 0) && (((StopDirection.ASK_LESS.equals(order.getStopDirection())) && (OrderSide.BUY.equals(order.getSide()))) || ((StopDirection.BID_GREATER.equals(order.getStopDirection())) && (OrderSide.SELL.equals(order.getSide())))))
/*     */           {
/*  70 */             dir = null;
/*     */           }
/*  72 */           StringBuffer sb = new StringBuffer(GuiUtilsAndConstants.getStopDirectionString(dir)).append(stopPrice.getValue().stripTrailingZeros().toPlainString());
/*     */ 
/*  74 */           if ((null != dir) && (null != order.getPriceTrailingLimit()))
/*     */           {
/*  78 */             BigDecimal priceStop = order.getPriceStop().getValue();
/*     */ 
/*  80 */             BigDecimal slippage = order.getPriceTrailingLimit().getValue();
/*     */ 
/*  82 */             BigDecimal priceWithSlippage = OrderSide.BUY == order.getSide() ? priceStop.add(slippage) : priceStop.subtract(slippage);
/*  83 */             sb.append(" (").append(priceWithSlippage.stripTrailingZeros().toPlainString()).append(")");
/*     */           }
/*     */ 
/*  87 */           if (null != order.getPriceLimit()) {
/*  88 */             BigDecimal trailingStepValue = order.getPriceLimit().getValue();
/*  89 */             sb.append(" (step ").append(this.trailingStepFormat.format(trailingStepValue)).append(")");
/*     */           }
/*     */ 
/*  93 */           setText(sb.toString());
/*     */         } catch (Exception e) {
/*  95 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       } else {
/*  98 */         StringBuffer sb = new StringBuffer("@MKT");
/*     */ 
/* 100 */         if (null != order.getPriceTrailingLimit()) {
/* 101 */           MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 102 */           OfferSide offerSide = OrderSide.BUY == order.getSide() ? OfferSide.ASK : OfferSide.BID;
/* 103 */           CurrencyOffer bestOffer = marketView.getBestOffer(order.getInstrument(), offerSide);
/* 104 */           BigDecimal slippageValue = order.getPriceTrailingLimit().getValue().divide(PriceUtil.pipValue(bestOffer.getPrice().getValue()), RoundingMode.HALF_EVEN);
/*     */ 
/* 108 */           sb.append(" ( LIMIT ").append(this.slippageFormat.format(slippageValue)).append(" pps)");
/*     */         }
/*     */ 
/* 112 */         if (null != order.getPriceLimit()) {
/* 113 */           BigDecimal trailingStepValue = order.getPriceLimit().getValue();
/* 114 */           sb.append(" (step ").append(this.trailingStepFormat.format(trailingStepValue)).append(")");
/*     */         }
/*     */ 
/* 118 */         setText(sb.toString());
/*     */       }
/*     */     } else {
/* 121 */       setText("");
/*     */     }
/* 123 */     setHorizontalAlignment(4);
/*     */   }
/*     */ 
/*     */   private void clearCell() {
/* 127 */     super.setValue(null);
/* 128 */     setText("");
/* 129 */     setIcon(null);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.StopPriceRenderer
 * JD-Core Version:    0.6.0
 */