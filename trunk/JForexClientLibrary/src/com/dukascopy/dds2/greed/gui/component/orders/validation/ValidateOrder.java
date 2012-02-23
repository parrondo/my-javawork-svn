/*      */ package com.dukascopy.dds2.greed.gui.component.orders.validation;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OfferSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.PositionSide;
/*      */ import com.dukascopy.transport.common.model.type.StopDirection;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*      */ import java.math.BigDecimal;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Currency;
/*      */ import java.util.List;
/*      */ 
/*      */ public class ValidateOrder
/*      */ {
/*      */   public static final String SLIPPAGE = "SLIPPAGE";
/*      */   public static final String AT_MARKET = "@market";
/*   69 */   private static ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*      */   private static ValidateOrder instance;
/*      */ 
/*      */   public static ValidateOrder getInstance()
/*      */   {
/*   76 */     if (instance == null) {
/*   77 */       instance = new ValidateOrder();
/*      */     }
/*   79 */     return instance;
/*      */   }
/*      */ 
/*      */   public static String getBuySide() {
/*   83 */     return OrderSide.BUY.asString();
/*      */   }
/*      */ 
/*      */   public static String getSellSide() {
/*   87 */     return OrderSide.SELL.asString();
/*      */   }
/*      */ 
/*      */   public static double getCurrentPriceByOfferSide(OfferSide offerSide, String instrument) {
/*   91 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/*   92 */     CurrencyOffer bestOffer = marketView.getBestOffer(instrument, offerSide);
/*   93 */     return bestOffer.getPrice().getValue().doubleValue();
/*      */   }
/*      */ 
/*      */   public static OrderValidationBean validateOrderFromPosTable(String positionSide, String instrument, double stopPrice, String orderType, String orderSubType)
/*      */   {
/*  103 */     ValidateOrder vo = new ValidateOrder();
/*      */     ValidateOrder tmp15_13 = vo; tmp15_13.getClass(); OrderValidationBean result = new OrderValidationBean();
/*      */ 
/*  107 */     double bidPrice = getCurrentPriceByOfferSide(OfferSide.BID, instrument);
/*  108 */     double askPrice = getCurrentPriceByOfferSide(OfferSide.ASK, instrument);
/*  109 */     Instrument currInstr = Instrument.fromString(instrument);
/*      */ 
/*  111 */     if (PositionSide.LONG.name().equals(positionSide)) {
/*  112 */       if ((StopOrderType.STOP_LOSS.name().equals(orderType)) || (StopOrderType.IFD_STOP.name().equals(orderType))) {
/*  113 */         if (StopDirection.BID_LESS.name().equals(orderSubType)) {
/*  114 */           if (stopPrice > bidPrice) {
/*  115 */             if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */             {
/*      */               ValidateOrder tmp133_131 = vo; tmp133_131.getClass(); result.add(new ValidationMessage("validation.close.limit.greater.than.bid", stopPrice, bidPrice));
/*      */             }
/*      */             else
/*      */             {
/*      */               ValidateOrder tmp160_158 = vo; tmp160_158.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.bid", stopPrice, bidPrice));
/*      */             }
/*      */           }
/*  121 */           if (!isContestOrderValid(stopPrice, bidPrice, currInstr)) {
/*  122 */             double minBidPrice = bidPrice - bidPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp220_218 = vo; tmp220_218.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.bm", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, bidPrice), getFormatedPrice(currInstr, minBidPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  131 */         if (StopDirection.ASK_LESS.name().equals(orderSubType)) {
/*  132 */           if (stopPrice > askPrice) {
/*  133 */             if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */             {
/*      */               ValidateOrder tmp322_320 = vo; tmp322_320.getClass(); result.add(new ValidationMessage("validation.close.stop.greater.than.ask", stopPrice, askPrice));
/*      */             }
/*      */             else
/*      */             {
/*      */               ValidateOrder tmp349_347 = vo; tmp349_347.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.ask", stopPrice, askPrice));
/*      */             }
/*      */           }
/*      */ 
/*  140 */           if (!isContestOrderValid(stopPrice, askPrice, currInstr))
/*      */           {
/*  142 */             double minAskPrice = askPrice - askPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp409_407 = vo; tmp409_407.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.am", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, askPrice), getFormatedPrice(currInstr, minAskPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  152 */       if ((StopOrderType.TAKE_PROFIT.name().equals(orderType)) || (StopOrderType.IFD_LIMIT.name().equals(orderType))) {
/*  153 */         if (stopPrice < bidPrice) {
/*  154 */           if (StopOrderType.IFD_LIMIT.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp525_523 = vo; tmp525_523.getClass(); result.add(new ValidationMessage("validation.close.limit.less.than.bid", stopPrice, bidPrice));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp552_550 = vo; tmp552_550.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.bid", stopPrice, bidPrice));
/*      */           }
/*      */         }
/*  160 */         if (!isContestOrderValid(stopPrice, bidPrice, currInstr)) {
/*  161 */           double maxBidPrice = bidPrice + bidPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */           ValidateOrder tmp612_610 = vo; tmp612_610.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.bm", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, bidPrice), getFormatedPrice(currInstr, maxBidPrice) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*  170 */     else if (PositionSide.SHORT.name().equals(positionSide))
/*      */     {
/*  172 */       if ((StopOrderType.STOP_LOSS.name().equals(orderType)) || (StopOrderType.IFD_STOP.name().equals(orderType))) {
/*  173 */         if (StopDirection.BID_GREATER.name().equals(orderSubType)) {
/*  174 */           if (stopPrice < bidPrice) {
/*  175 */             if (StopOrderType.IFD_LIMIT.name().equals(orderType))
/*      */             {
/*      */               ValidateOrder tmp758_756 = vo; tmp758_756.getClass(); result.add(new ValidationMessage("validation.close.stop.less.than.bid", stopPrice, bidPrice));
/*      */             }
/*      */             else
/*      */             {
/*      */               ValidateOrder tmp785_783 = vo; tmp785_783.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.bid", stopPrice, bidPrice));
/*      */             }
/*      */           }
/*  181 */           if (!isContestOrderValid(stopPrice, bidPrice, currInstr)) {
/*  182 */             double maxBidPrice = bidPrice + bidPrice * storage.restoreContestRateByInstrument(currInstr).doubleValue();
/*      */             ValidateOrder tmp841_839 = vo; tmp841_839.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.bm", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, bidPrice), getFormatedPrice(currInstr, maxBidPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  193 */         if (StopDirection.ASK_GREATER.name().equals(orderSubType)) {
/*  194 */           if (stopPrice < askPrice) {
/*  195 */             if (StopOrderType.IFD_LIMIT.name().equals(orderType))
/*      */             {
/*      */               ValidateOrder tmp943_941 = vo; tmp943_941.getClass(); result.add(new ValidationMessage("validation.close.stop.less.than.ask", stopPrice, askPrice));
/*      */             }
/*      */             else
/*      */             {
/*      */               ValidateOrder tmp970_968 = vo; tmp970_968.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.ask", stopPrice, askPrice));
/*      */             }
/*      */           }
/*  201 */           if (!isContestOrderValid(stopPrice, askPrice, currInstr)) {
/*  202 */             double maxPrice = askPrice + askPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp1030_1028 = vo; tmp1030_1028.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.am", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, askPrice), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  214 */       if ((StopOrderType.TAKE_PROFIT.name().equals(orderType)) || (StopOrderType.IFD_LIMIT.name().equals(orderType))) {
/*  215 */         if (stopPrice > askPrice) {
/*  216 */           if (StopOrderType.IFD_LIMIT.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp1146_1144 = vo; tmp1146_1144.getClass(); result.add(new ValidationMessage("validation.close.limit.greater.than.ask", stopPrice, askPrice));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp1173_1171 = vo; tmp1173_1171.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.ask", stopPrice, askPrice));
/*      */           }
/*      */         }
/*  222 */         if (!isContestOrderValid(stopPrice, askPrice, currInstr)) {
/*  223 */           double minPrice = askPrice - askPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */           ValidateOrder tmp1233_1231 = vo; tmp1233_1231.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.am", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, askPrice), getFormatedPrice(currInstr, minPrice) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  235 */     return result;
/*      */   }
/*      */ 
/*      */   public static OrderValidationBean validateOrderAddEditFromTable(String side, String instrument, double stopPrice, double entryVal, double slippage, String orderType, String orderSubType, OrderGroupMessage orderGroup)
/*      */   {
/*  248 */     ValidateOrder vo = new ValidateOrder();
/*      */     ValidateOrder tmp15_13 = vo; tmp15_13.getClass(); OrderValidationBean result = new OrderValidationBean();
/*      */ 
/*  251 */     double bidPrice = getCurrentPriceByOfferSide(OfferSide.BID, instrument);
/*  252 */     double askPrice = getCurrentPriceByOfferSide(OfferSide.ASK, instrument);
/*  253 */     Instrument currInstr = Instrument.fromString(instrument);
/*      */ 
/*  255 */     OrderMessage stopLossOrder = GreedContext.isGlobalExtended() ? orderGroup.getCloseStopOrder() : orderGroup.getStopLossOrder();
/*  256 */     OrderMessage takeProfitOrder = GreedContext.isGlobalExtended() ? orderGroup.getCloseLimitOrder() : orderGroup.getTakeProfitOrder();
/*      */ 
/*  258 */     if (!StopOrderType.OPEN_IF.name().equals(orderType)) {
/*  259 */       side = getBuySide().equals(side) ? getSellSide() : getBuySide();
/*      */     }
/*      */ 
/*  262 */     double entryD = stopPrice;
/*  263 */     BigDecimal slip = slippage == BigDecimal.ZERO.doubleValue() ? null : new BigDecimal(slippage).divide(BigDecimal.valueOf(currInstr.getPipValue()), 0, 1);
/*      */ 
/*  268 */     if (side.equals(getBuySide()))
/*      */     {
/*  270 */       if ((slip != null) && (StopDirection.BID_GREATER.name().equals(orderSubType))) {
/*  271 */         entryD += slip.doubleValue() * currInstr.getPipValue();
/*  272 */         entryD = round(entryD, 5);
/*      */       }
/*      */ 
/*  276 */       if (StopOrderType.OPEN_IF.name().equals(orderType)) {
/*  277 */         if (StopDirection.BID_GREATER.name().equals(orderSubType)) {
/*  278 */           if (stopPrice != entryVal) {
/*  279 */             if ((stopPrice < bidPrice) && (entryVal > bidPrice))
/*      */             {
/*      */               ValidateOrder tmp279_277 = vo; tmp279_277.getClass(); result.add(new ValidationMessage("validation.entry.plus.slippage.greater", entryD, bidPrice));
/*      */             }
/*  282 */           } else if (entryVal < bidPrice)
/*      */           {
/*      */             ValidateOrder tmp315_313 = vo; tmp315_313.getClass(); result.add(new ValidationMessage("validation.entry.less.than.market", stopPrice, bidPrice));
/*      */           }
/*      */ 
/*  286 */           if ((stopLossOrder != null) && 
/*  287 */             (stopLossOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp360_358 = vo; tmp360_358.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  291 */           if ((takeProfitOrder != null) && 
/*  292 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp414_412 = vo; tmp414_412.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  297 */           if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  298 */             double minPrice = entryVal - entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp483_481 = vo; tmp483_481.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, minPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  308 */         if (StopDirection.ASK_GREATER.name().equals(orderSubType)) {
/*  309 */           if (stopPrice != entryVal) {
/*  310 */             if ((stopPrice < askPrice) && (entryVal > askPrice))
/*      */             {
/*      */               ValidateOrder tmp586_584 = vo; tmp586_584.getClass(); result.add(new ValidationMessage("validation.entry.plus.slippage.greater", entryD, askPrice));
/*      */             }
/*  313 */           } else if (entryVal < askPrice)
/*      */           {
/*      */             ValidateOrder tmp625_623 = vo; tmp625_623.getClass(); result.getMessages().add(new ValidationMessage("validation.entry.less.than.market", stopPrice, askPrice));
/*      */           }
/*      */ 
/*  317 */           if ((stopLossOrder != null) && 
/*  318 */             (stopLossOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp673_671 = vo; tmp673_671.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  322 */           if ((takeProfitOrder != null) && 
/*  323 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp727_725 = vo; tmp727_725.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  328 */           if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  329 */             double minPrice = entryVal - entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp796_794 = vo; tmp796_794.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, minPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  339 */         if (StopDirection.ASK_LESS.name().equals(orderSubType)) {
/*  340 */           if (entryVal > askPrice)
/*      */           {
/*      */             ValidateOrder tmp885_883 = vo; tmp885_883.getClass(); result.add(new ValidationMessage("validation.entry.greater.than.market", stopPrice, askPrice));
/*      */           }
/*  342 */           if ((stopLossOrder != null) && 
/*  343 */             (stopLossOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp930_928 = vo; tmp930_928.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  348 */           if ((takeProfitOrder != null) && 
/*  349 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp984_982 = vo; tmp984_982.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  354 */           if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  355 */             double minPrice = entryVal - entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp1053_1051 = vo; tmp1053_1051.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, minPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  365 */         if (StopDirection.ASK_EQUALS.name().equals(orderSubType)) {
/*  366 */           if ((stopLossOrder != null) && 
/*  367 */             (stopLossOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp1155_1153 = vo; tmp1155_1153.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  371 */           if ((takeProfitOrder != null) && 
/*  372 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp1209_1207 = vo; tmp1209_1207.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  381 */       if ((StopOrderType.STOP_LOSS.name().equals(orderType)) || (StopOrderType.IFD_STOP.name().equals(orderType))) {
/*  382 */         if ((StopDirection.BID_LESS.name().equals(orderSubType)) && 
/*  383 */           (stopPrice > entryVal))
/*  384 */           if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp1305_1303 = vo; tmp1305_1303.getClass(); result.add(new ValidationMessage("validation.close.stop.greater.than.entry", stopPrice, entryVal));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp1332_1330 = vo; tmp1332_1330.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopPrice, entryVal));
/*      */           }
/*  389 */         if ((StopDirection.ASK_LESS.name().equals(orderSubType)) && 
/*  390 */           (stopPrice > entryVal)) {
/*  391 */           if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp1391_1389 = vo; tmp1391_1389.getClass(); result.add(new ValidationMessage("validation.close.stop.greater.than.entry", stopPrice, entryVal));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp1418_1416 = vo; tmp1418_1416.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopPrice, entryVal));
/*      */           }
/*      */         }
/*      */ 
/*  398 */         if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  399 */           double minPrice = entryVal - entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */           ValidateOrder tmp1478_1476 = vo; tmp1478_1476.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, minPrice) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  409 */       if (((StopOrderType.TAKE_PROFIT.name().equals(orderType)) || (StopOrderType.IFD_STOP.name().equals(orderType))) && 
/*  410 */         (StopDirection.BID_GREATER.name().equals(orderSubType))) {
/*  411 */         if (stopPrice < entryVal)
/*  412 */           if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp1608_1606 = vo; tmp1608_1606.getClass(); result.add(new ValidationMessage("validation.close.limit.less.than.entry", stopPrice, entryVal));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp1635_1633 = vo; tmp1635_1633.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", stopPrice, entryVal));
/*      */           }
/*  417 */         if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  418 */           double maxPrice = entryVal + entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */           ValidateOrder tmp1695_1693 = vo; tmp1695_1693.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, maxPrice) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  429 */       if ((slip != null) && (!StopDirection.BID_GREATER.equals(orderGroup.getOpeningOrder().getStopDirection()))) {
/*  430 */         entryD -= slip.doubleValue() * currInstr.getPipValue();
/*  431 */         entryD = round(entryD, 5);
/*      */       }
/*      */ 
/*  434 */       if (StopOrderType.OPEN_IF.name().equals(orderType)) {
/*  435 */         if (StopDirection.BID_LESS.name().equals(orderSubType)) {
/*  436 */           if (stopPrice != entryVal) {
/*  437 */             if ((stopPrice > bidPrice) && (entryVal < bidPrice))
/*      */             {
/*      */               ValidateOrder tmp1861_1859 = vo; tmp1861_1859.getClass(); result.add(new ValidationMessage("validation.entry.minus.slippage.less", entryD, bidPrice));
/*      */             }
/*  439 */           } else if (entryVal > bidPrice)
/*      */           {
/*      */             ValidateOrder tmp1897_1895 = vo; tmp1897_1895.getClass(); result.add(new ValidationMessage("validation.entry.greater.than.market", stopPrice, bidPrice));
/*      */           }
/*  441 */           if ((stopLossOrder != null) && 
/*  442 */             (stopLossOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp1942_1940 = vo; tmp1942_1940.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  446 */           if ((takeProfitOrder != null) && 
/*  447 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp1996_1994 = vo; tmp1996_1994.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  452 */           if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  453 */             double maxPrice = entryVal + entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp2065_2063 = vo; tmp2065_2063.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  464 */         if (StopDirection.ASK_LESS.name().equals(orderSubType)) {
/*  465 */           if (stopPrice != entryVal) {
/*  466 */             if ((stopPrice > askPrice) && (entryVal < askPrice))
/*      */             {
/*      */               ValidateOrder tmp2168_2166 = vo; tmp2168_2166.getClass(); result.add(new ValidationMessage("validation.entry.minus.slippage.less", entryD, askPrice));
/*      */             }
/*  468 */           } else if (entryVal > askPrice)
/*      */           {
/*      */             ValidateOrder tmp2204_2202 = vo; tmp2204_2202.getClass(); result.add(new ValidationMessage("validation.entry.greater.than.market", stopPrice, askPrice));
/*      */           }
/*      */ 
/*  471 */           if ((stopLossOrder != null) && 
/*  472 */             (stopLossOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp2249_2247 = vo; tmp2249_2247.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  477 */           if ((takeProfitOrder != null) && 
/*  478 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp2303_2301 = vo; tmp2303_2301.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  483 */           if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  484 */             double maxPrice = entryVal + entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp2372_2370 = vo; tmp2372_2370.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  496 */         if (StopDirection.BID_GREATER.name().equals(orderSubType)) {
/*  497 */           if (entryVal < bidPrice)
/*      */           {
/*      */             ValidateOrder tmp2461_2459 = vo; tmp2461_2459.getClass(); result.add(new ValidationMessage("validation.entry.less.than.market", stopPrice, bidPrice));
/*      */           }
/*  499 */           if ((stopLossOrder != null) && 
/*  500 */             (stopLossOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp2506_2504 = vo; tmp2506_2504.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  505 */           if ((takeProfitOrder != null) && 
/*  506 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp2560_2558 = vo; tmp2560_2558.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  511 */           if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  512 */             double maxPrice = entryVal - entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp2629_2627 = vo; tmp2629_2627.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  522 */         if (StopDirection.BID_EQUALS.name().equals(orderSubType)) {
/*  523 */           if ((stopLossOrder != null) && 
/*  524 */             (stopLossOrder.getPriceStop().getValue().doubleValue() < stopPrice))
/*      */           {
/*      */             ValidateOrder tmp2731_2729 = vo; tmp2731_2729.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */ 
/*  528 */           if ((takeProfitOrder != null) && 
/*  529 */             (takeProfitOrder.getPriceStop().getValue().doubleValue() > stopPrice))
/*      */           {
/*      */             ValidateOrder tmp2785_2783 = vo; tmp2785_2783.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), stopPrice));
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  536 */       if ((StopOrderType.STOP_LOSS.name().equals(orderType)) || (StopOrderType.IFD_STOP.name().equals(orderType))) {
/*  537 */         if ((StopDirection.BID_LESS.name().equals(orderSubType)) && 
/*  538 */           (stopPrice < entryVal)) {
/*  539 */           if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp2881_2879 = vo; tmp2881_2879.getClass(); result.add(new ValidationMessage("validation.close.stop.less.than.entry", stopPrice, entryVal));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp2908_2906 = vo; tmp2908_2906.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopPrice, entryVal));
/*      */           }
/*      */         }
/*      */ 
/*  546 */         if (((StopDirection.ASK_GREATER.name().equals(orderSubType)) || (StopDirection.BID_GREATER.name().equals(orderSubType))) && 
/*  547 */           (stopPrice < entryVal)) {
/*  548 */           if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp2984_2982 = vo; tmp2984_2982.getClass(); result.getMessages().add(new ValidationMessage("validation.close.stop.less.than.entry", stopPrice, entryVal));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp3017_3015 = vo; tmp3017_3015.getClass(); result.getMessages().add(new ValidationMessage("validation.stop.loss.less.than.entry", stopPrice, entryVal));
/*      */           }
/*      */         }
/*  554 */         if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  555 */           double maxPrice = entryVal + entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */           ValidateOrder tmp3080_3078 = vo; tmp3080_3078.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, maxPrice) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  565 */       if (((StopOrderType.TAKE_PROFIT.name().equals(orderType)) || (StopOrderType.IFD_LIMIT.name().equals(orderType))) && 
/*  566 */         (StopDirection.ASK_LESS.name().equals(orderSubType))) {
/*  567 */         if (stopPrice > entryVal)
/*  568 */           if (StopOrderType.IFD_STOP.name().equals(orderType))
/*      */           {
/*      */             ValidateOrder tmp3210_3208 = vo; tmp3210_3208.getClass(); result.add(new ValidationMessage("validation.close.limit.greater.than.entry", stopPrice, entryVal));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp3237_3235 = vo; tmp3237_3235.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", stopPrice, entryVal));
/*      */           }
/*  573 */         if (!isContestOrderValid(stopPrice, entryVal, currInstr)) {
/*  574 */           double minPrice = entryVal - entryVal * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */           ValidateOrder tmp3297_3295 = vo; tmp3297_3295.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopPrice), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryVal), getFormatedPrice(currInstr, minPrice) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  586 */     return result;
/*      */   }
/*      */ 
/*      */   public static OrderValidationBean validateBidOfferPlace(String side, String instrument, double stopPrice)
/*      */   {
/*  593 */     ValidateOrder vo = new ValidateOrder();
/*      */     ValidateOrder tmp15_13 = vo; tmp15_13.getClass(); OrderValidationBean result = new OrderValidationBean();
/*      */ 
/*  596 */     double askPrice = getCurrentPriceByOfferSide(OfferSide.ASK, instrument);
/*  597 */     double bidPrice = getCurrentPriceByOfferSide(OfferSide.BID, instrument);
/*      */ 
/*  599 */     if ((OfferSide.BID.asString().equals(side)) && 
/*  600 */       (stopPrice > askPrice))
/*      */     {
/*      */       ValidateOrder tmp71_69 = vo; tmp71_69.getClass(); result.add(new ValidationMessage("validation.place.bid.greater", stopPrice, askPrice));
/*      */     }
/*      */ 
/*  604 */     if ((OfferSide.ASK.asString().equals(side)) && 
/*  605 */       (stopPrice < bidPrice))
/*      */     {
/*      */       ValidateOrder tmp115_113 = vo; tmp115_113.getClass(); result.add(new ValidationMessage("validation.place.offer.less", stopPrice, tmp115_113));
/*      */     }
/*      */ 
/*  611 */     return result;
/*      */   }
/*      */ 
/*      */   public static OrderValidationBean validateBidOfferFromOrdersTable(StopOrderType type, String side, String instrument, double stopPrice, double parentValue)
/*      */   {
/*  619 */     ValidateOrder vo = new ValidateOrder();
/*      */     ValidateOrder tmp15_13 = vo; tmp15_13.getClass(); OrderValidationBean result = new OrderValidationBean();
/*      */ 
/*  622 */     side = getBuySide().equals(side) ? getSellSide() : getBuySide();
/*      */ 
/*  624 */     if (getBuySide().equals(side))
/*      */     {
/*  626 */       if ((StopOrderType.STOP_LOSS.equals(type)) || (StopOrderType.IFD_STOP.equals(type))) {
/*  627 */         if (stopPrice >= parentValue)
/*  628 */           if (StopOrderType.IFD_STOP.equals(type))
/*      */           {
/*      */             ValidateOrder tmp100_98 = vo; tmp100_98.getClass(); result.add(new ValidationMessage("validation.close.stop.greater.than.order", stopPrice, parentValue));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp127_125 = tmp100_98; tmp127_125.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.order", stopPrice, parentValue));
/*      */           }
/*  632 */       } else if (((StopOrderType.TAKE_PROFIT.equals(type)) || (StopOrderType.IFD_LIMIT.equals(type))) && 
/*  633 */         (stopPrice <= parentValue))
/*  634 */         if (StopOrderType.IFD_LIMIT.equals(type))
/*      */         {
/*      */           ValidateOrder tmp191_189 = tmp100_98; tmp191_189.getClass(); tmp127_125.add(new ValidationMessage("validation.close.limit.less.than.order", stopPrice, parentValue));
/*      */         }
/*      */         else
/*      */         {
/*      */           ValidateOrder tmp218_216 = tmp100_98; tmp218_216.getClass(); tmp127_125.add(new ValidationMessage("validation.take.profit.less.than.order", stopPrice, parentValue));
/*      */         }
/*      */     }
/*  640 */     else if (getSellSide().equals(side))
/*      */     {
/*  642 */       if ((StopOrderType.STOP_LOSS.equals(type)) || (StopOrderType.IFD_STOP.equals(type))) {
/*  643 */         if (stopPrice <= parentValue)
/*  644 */           if (StopOrderType.IFD_STOP.equals(type))
/*      */           {
/*      */             ValidateOrder tmp292_290 = tmp100_98; tmp292_290.getClass(); tmp127_125.add(new ValidationMessage("validation.close.stop.less.than.order", stopPrice, parentValue));
/*      */           }
/*      */           else
/*      */           {
/*      */             ValidateOrder tmp319_317 = tmp100_98; tmp319_317.getClass(); tmp127_125.add(new ValidationMessage("validation.stop.loss.less.than.order", stopPrice, parentValue));
/*      */           }
/*  648 */       } else if (((StopOrderType.TAKE_PROFIT.equals(type)) || (StopOrderType.IFD_LIMIT.equals(type))) && 
/*  649 */         (stopPrice >= parentValue)) {
/*  650 */         if (StopOrderType.IFD_LIMIT.equals(type))
/*      */         {
/*      */           ValidateOrder tmp383_381 = tmp100_98; tmp383_381.getClass(); tmp127_125.add(new ValidationMessage("validation.close.limit.greater.than.order", stopPrice, parentValue));
/*      */         }
/*      */         else
/*      */         {
/*      */           ValidateOrder tmp410_408 = tmp100_98; tmp410_408.getClass(); tmp127_125.add(new ValidationMessage("validation.take.profit.greater.than.order", stopPrice, parentValue));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  658 */     return tmp127_125;
/*      */   }
/*      */ 
/*      */   private static boolean isContestOrderValid(double stopPrice, double entryPrice, Instrument instrument)
/*      */   {
/*  679 */     if (!GreedContext.isContest()) return true;
/*      */ 
/*  681 */     double difference = entryPrice - stopPrice;
/*  682 */     double rate = storage.restoreContestRateByInstrument(instrument).doubleValue();
/*      */ 
/*  685 */     if (rate <= 0.0D) return true;
/*      */ 
/*  688 */     return (rate > 0.0D) && (Math.abs(difference) < entryPrice * (rate / 100.0D));
/*      */   }
/*      */ 
/*      */   public OrderValidationBean validateOrder(OrderGroupMessage orderGroup)
/*      */   {
/*  695 */     ValidateOrder vo = new ValidateOrder();
/*      */     ValidateOrder tmp13_12 = vo; tmp13_12.getClass(); OrderValidationBean result = new OrderValidationBean();
/*      */ 
/*  698 */     double bidPrice = getCurrentPriceByOfferSide(OfferSide.BID, orderGroup.getInstrument());
/*  699 */     double askPrice = getCurrentPriceByOfferSide(OfferSide.ASK, orderGroup.getInstrument());
/*      */ 
/*  701 */     OrderMessage openingOrder = orderGroup.getOpeningOrder();
/*  702 */     OrderMessage stopLossOrder = GreedContext.isGlobalExtended() ? orderGroup.getCloseStopOrder() : orderGroup.getStopLossOrder();
/*  703 */     OrderMessage takeProfitOrder = GreedContext.isGlobalExtended() ? orderGroup.getCloseLimitOrder() : orderGroup.getTakeProfitOrder();
/*      */ 
/*  705 */     OrderState state = openingOrder.getOrderState();
/*  706 */     StopDirection entryStopDirection = openingOrder.getStopDirection();
/*  707 */     Instrument currInstr = Instrument.fromString(openingOrder.getInstrument());
/*      */ 
/*  709 */     if (openingOrder.getSide() == OrderSide.BUY) {
/*  710 */       if (((state == OrderState.CREATED) || (state == OrderState.PENDING) || (state == OrderState.EXECUTING)) && (openingOrder.getPriceStop() != null) && (entryStopDirection != null))
/*      */       {
/*  712 */         double oldEntry = openingOrder.getPriceStop().getValue().doubleValue();
/*  713 */         double entryD = oldEntry;
/*  714 */         BigDecimal slippage = openingOrder.getPriceTrailingLimit() == null ? null : openingOrder.getPriceTrailingLimit().getValue().divide(BigDecimal.valueOf(Instrument.fromString(orderGroup.getInstrument()).getPipValue()), 0, 1);
/*      */ 
/*  719 */         if ((slippage != null) && (!StopDirection.ASK_LESS.equals(openingOrder.getStopDirection()))) {
/*  720 */           entryD += slippage.doubleValue() * Instrument.fromString(orderGroup.getInstrument()).getPipValue();
/*  721 */           entryD = round(entryD, 5);
/*      */         }
/*      */ 
/*  724 */         if (entryStopDirection == StopDirection.BID_GREATER) {
/*  725 */           if (slippage != null) {
/*  726 */             if ((oldEntry < bidPrice) && (entryD > bidPrice))
/*      */             {
/*      */               ValidateOrder tmp302_301 = vo; tmp302_301.getClass(); result.add(new ValidationMessage("validation.entry.plus.slippage.greater", entryD, bidPrice));
/*      */             }
/*      */           }
/*  730 */           else if (entryD < bidPrice)
/*      */           {
/*      */             ValidateOrder tmp336_335 = vo; tmp336_335.getClass(); result.add(new ValidationMessage("validation.entry.less.than.market", entryD, bidPrice));
/*      */           }
/*      */ 
/*  735 */           if (stopLossOrder != null) {
/*  736 */             if (stopLossOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */             {
/*      */               ValidateOrder tmp381_380 = vo; tmp381_380.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*      */ 
/*  740 */             if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  743 */               double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp459_458 = vo; tmp459_458.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  752 */           if (takeProfitOrder != null) {
/*  753 */             if (takeProfitOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */             {
/*      */               ValidateOrder tmp556_555 = vo; tmp556_555.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*      */ 
/*  757 */             if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  760 */               double maxPrice = entryD + entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp634_633 = vo; tmp634_633.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  770 */         if (entryStopDirection == StopDirection.ASK_GREATER) {
/*  771 */           if (slippage != null) {
/*  772 */             if ((oldEntry < askPrice) && (entryD > askPrice))
/*      */             {
/*      */               ValidateOrder tmp738_737 = vo; tmp738_737.getClass(); result.add(new ValidationMessage("validation.entry.plus.slippage.greater", entryD, askPrice));
/*      */             }
/*      */           }
/*  776 */           else if (entryD < askPrice)
/*      */           {
/*      */             ValidateOrder tmp772_771 = vo; tmp772_771.getClass(); result.add(new ValidationMessage("validation.entry.less.than.market", entryD, askPrice));
/*      */           }
/*      */ 
/*  782 */           if (stopLossOrder != null) {
/*  783 */             if (stopLossOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */             {
/*      */               ValidateOrder tmp817_816 = vo; tmp817_816.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*  786 */             if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  789 */               double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp895_894 = vo; tmp895_894.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  798 */           if (takeProfitOrder != null) {
/*  799 */             if (takeProfitOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */             {
/*      */               ValidateOrder tmp992_991 = vo; tmp992_991.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*  802 */             if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  805 */               double maxPrice = entryD + entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp1070_1069 = vo; tmp1070_1069.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  815 */         if (entryStopDirection == StopDirection.ASK_LESS) {
/*  816 */           if (entryD > askPrice)
/*      */           {
/*      */             ValidateOrder tmp1161_1160 = vo; tmp1161_1160.getClass(); result.add(new ValidationMessage("validation.entry.greater.than.market", entryD, askPrice));
/*      */           }
/*      */ 
/*  820 */           if (stopLossOrder != null) {
/*  821 */             if (stopLossOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */             {
/*      */               ValidateOrder tmp1206_1205 = vo; tmp1206_1205.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*  824 */             if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  827 */               double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp1284_1283 = vo; tmp1284_1283.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  836 */           if (takeProfitOrder != null) {
/*  837 */             if (takeProfitOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */             {
/*      */               ValidateOrder tmp1381_1380 = vo; tmp1381_1380.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*  840 */             if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  844 */               double maxPrice = entryD + entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp1459_1458 = vo; tmp1459_1458.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  854 */         if (entryStopDirection == StopDirection.ASK_EQUALS) {
/*  855 */           if (entryD > askPrice)
/*      */           {
/*      */             ValidateOrder tmp1550_1549 = vo; tmp1550_1549.getClass(); result.add(new ValidationMessage("validation.entry.greater.than.market", oldEntry, askPrice));
/*      */           }
/*      */ 
/*  859 */           if (stopLossOrder != null) {
/*  860 */             if (stopLossOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */             {
/*      */               ValidateOrder tmp1595_1594 = vo; tmp1595_1594.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*  863 */             if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  866 */               double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp1673_1672 = vo; tmp1673_1672.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  875 */           if (takeProfitOrder != null) {
/*  876 */             if (takeProfitOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */             {
/*      */               ValidateOrder tmp1770_1769 = vo; tmp1770_1769.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */             }
/*  879 */             if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */             {
/*  883 */               double maxPrice = entryD + entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp1848_1847 = vo; tmp1848_1847.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*  894 */       else if (entryStopDirection == null)
/*      */       {
/*  896 */         if (stopLossOrder != null) {
/*  897 */           if (stopLossOrder.getStopDirection() == StopDirection.BID_LESS) {
/*  898 */             if (stopLossOrder.getPriceStop().getValue().doubleValue() > bidPrice)
/*      */             {
/*      */               ValidateOrder tmp1964_1963 = vo; tmp1964_1963.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.bid", stopLossOrder.getPriceStop().getValue().doubleValue(), bidPrice));
/*      */             }
/*  901 */             if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), bidPrice, currInstr))
/*      */             {
/*  905 */               double minBidPrice = bidPrice - bidPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp2042_2041 = vo; tmp2042_2041.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.bm", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, bidPrice), getFormatedPrice(currInstr, minBidPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  914 */           if (stopLossOrder.getStopDirection() == StopDirection.ASK_LESS) {
/*  915 */             if (stopLossOrder.getPriceStop().getValue().doubleValue() > askPrice)
/*      */             {
/*      */               ValidateOrder tmp2145_2144 = vo; tmp2145_2144.getClass(); result.add(new ValidationMessage("validation.stop.loss.greater.than.ask", stopLossOrder.getPriceStop().getValue().doubleValue(), askPrice));
/*      */             }
/*  918 */             if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), askPrice, currInstr))
/*      */             {
/*  922 */               double minAskPrice = askPrice - askPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */               ValidateOrder tmp2223_2222 = vo; tmp2223_2222.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.am", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, askPrice), getFormatedPrice(currInstr, minAskPrice) }));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  932 */         if (takeProfitOrder != null) {
/*  933 */           if (takeProfitOrder.getPriceStop().getValue().doubleValue() < bidPrice)
/*      */           {
/*      */             ValidateOrder tmp2320_2319 = vo; tmp2320_2319.getClass(); result.add(new ValidationMessage("validation.take.profit.less.than.bid", takeProfitOrder.getPriceStop().getValue().doubleValue(), bidPrice));
/*      */           }
/*  936 */           if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), bidPrice, currInstr))
/*      */           {
/*  940 */             double maxBidPrice = bidPrice + bidPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp2398_2397 = vo; tmp2398_2397.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.bm", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, bidPrice), getFormatedPrice(currInstr, maxBidPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*  950 */     else if (((state == OrderState.CREATED) || (state == OrderState.PENDING) || (state == OrderState.EXECUTING)) && (openingOrder.getPriceStop() != null) && (entryStopDirection != null)) {
/*  951 */       double oldEntry = openingOrder.getPriceStop().getValue().doubleValue();
/*  952 */       double entryD = oldEntry;
/*      */ 
/*  954 */       BigDecimal slippage = openingOrder.getPriceTrailingLimit() == null ? null : openingOrder.getPriceTrailingLimit().getValue().divide(BigDecimal.valueOf(Instrument.fromString(orderGroup.getInstrument()).getPipValue()), 0, 1);
/*      */ 
/*  958 */       if ((slippage != null) && (!StopDirection.BID_GREATER.equals(openingOrder.getStopDirection()))) {
/*  959 */         entryD -= slippage.doubleValue() * Instrument.fromString(orderGroup.getInstrument()).getPipValue();
/*  960 */         entryD = round(entryD, 5);
/*      */       }
/*      */ 
/*  963 */       if (entryStopDirection == StopDirection.BID_LESS)
/*      */       {
/*  965 */         if (slippage != null) {
/*  966 */           if ((oldEntry > bidPrice) && (entryD < bidPrice))
/*      */           {
/*      */             ValidateOrder tmp2647_2646 = vo; tmp2647_2646.getClass(); result.add(new ValidationMessage("validation.entry.minus.slippage.less", entryD, bidPrice));
/*      */           }
/*  969 */         } else if (entryD > bidPrice)
/*      */         {
/*      */           ValidateOrder tmp2681_2680 = vo; tmp2681_2680.getClass(); result.add(new ValidationMessage("validation.entry.greater.than.market", entryD, bidPrice));
/*      */         }
/*      */ 
/*  973 */         if (stopLossOrder != null) {
/*  974 */           if (stopLossOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */           {
/*      */             ValidateOrder tmp2726_2725 = vo; tmp2726_2725.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/*  977 */           if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/*  981 */             double maxPrice = entryD + entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp2804_2803 = vo; tmp2804_2803.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  990 */         if (takeProfitOrder != null) {
/*  991 */           if (takeProfitOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */           {
/*      */             ValidateOrder tmp2901_2900 = vo; tmp2901_2900.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/*  994 */           if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/*  998 */             double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp2979_2978 = vo; tmp2979_2978.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1008 */       if (entryStopDirection == StopDirection.ASK_LESS) {
/* 1009 */         if (slippage != null) {
/* 1010 */           if ((oldEntry > askPrice) && (entryD < askPrice))
/*      */           {
/*      */             ValidateOrder tmp3083_3082 = vo; tmp3083_3082.getClass(); result.add(new ValidationMessage("validation.entry.minus.slippage.less", entryD, askPrice));
/*      */           }
/* 1013 */         } else if (entryD > askPrice)
/*      */         {
/*      */           ValidateOrder tmp3117_3116 = vo; tmp3117_3116.getClass(); result.add(new ValidationMessage("validation.entry.greater.than.market", entryD, askPrice));
/*      */         }
/*      */ 
/* 1018 */         if (stopLossOrder != null) {
/* 1019 */           if (stopLossOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */           {
/*      */             ValidateOrder tmp3162_3161 = vo; tmp3162_3161.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/* 1022 */           if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/* 1026 */             double maxPrice = entryD + entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp3240_3239 = vo; tmp3240_3239.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1036 */         if (takeProfitOrder != null) {
/* 1037 */           if (takeProfitOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */           {
/*      */             ValidateOrder tmp3337_3336 = vo; tmp3337_3336.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/* 1040 */           if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/* 1044 */             double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp3415_3414 = vo; tmp3415_3414.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1054 */       if (entryStopDirection == StopDirection.BID_GREATER) {
/* 1055 */         if (entryD < bidPrice)
/*      */         {
/*      */           ValidateOrder tmp3506_3505 = vo; tmp3506_3505.getClass(); result.add(new ValidationMessage("validation.entry.less.than.market", entryD, bidPrice));
/*      */         }
/*      */ 
/* 1059 */         if (stopLossOrder != null) {
/* 1060 */           if (stopLossOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */           {
/*      */             ValidateOrder tmp3551_3550 = vo; tmp3551_3550.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/* 1063 */           if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/* 1067 */             double maxPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp3629_3628 = vo; tmp3629_3628.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1076 */         if (takeProfitOrder != null) {
/* 1077 */           if (takeProfitOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */           {
/*      */             ValidateOrder tmp3726_3725 = vo; tmp3726_3725.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/* 1080 */           if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/* 1084 */             double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp3804_3803 = vo; tmp3804_3803.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1093 */       if (entryStopDirection == StopDirection.BID_EQUALS) {
/* 1094 */         if (entryD < bidPrice)
/*      */         {
/*      */           ValidateOrder tmp3895_3894 = vo; tmp3895_3894.getClass(); result.add(new ValidationMessage("validation.entry.less.than.market", oldEntry, bidPrice));
/*      */         }
/*      */ 
/* 1098 */         if (stopLossOrder != null) {
/* 1099 */           if (stopLossOrder.getPriceStop().getValue().doubleValue() < entryD)
/*      */           {
/*      */             ValidateOrder tmp3940_3939 = vo; tmp3940_3939.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.entry", stopLossOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/* 1102 */           if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/* 1106 */             double maxPrice = entryD + entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp4018_4017 = vo; tmp4018_4017.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1116 */         if (takeProfitOrder != null) {
/* 1117 */           if (takeProfitOrder.getPriceStop().getValue().doubleValue() > entryD)
/*      */           {
/*      */             ValidateOrder tmp4115_4114 = vo; tmp4115_4114.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.entry", takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD));
/*      */           }
/* 1120 */           if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), entryD, currInstr))
/*      */           {
/* 1124 */             double minPrice = entryD - entryD * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp4193_4192 = vo; tmp4193_4192.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.entry", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, entryD), getFormatedPrice(currInstr, minPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/* 1134 */     else if (entryStopDirection == null) {
/* 1135 */       if (stopLossOrder != null) {
/* 1136 */         if (stopLossOrder.getStopDirection() == StopDirection.BID_GREATER) {
/* 1137 */           if (stopLossOrder.getPriceStop().getValue().doubleValue() < bidPrice)
/*      */           {
/*      */             ValidateOrder tmp4309_4308 = vo; tmp4309_4308.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.bid", stopLossOrder.getPriceStop().getValue().doubleValue(), bidPrice));
/*      */           }
/* 1140 */           if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), bidPrice, currInstr))
/*      */           {
/* 1144 */             double maxBidPrice = bidPrice + bidPrice * storage.restoreContestRateByInstrument(currInstr).doubleValue();
/*      */             ValidateOrder tmp4383_4382 = vo; tmp4383_4382.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.bm", new Object[] { Double.valueOf(stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), Double.valueOf(bidPrice), Double.valueOf(maxBidPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1153 */         if (stopLossOrder.getStopDirection() == StopDirection.ASK_GREATER) {
/* 1154 */           if (stopLossOrder.getPriceStop().getValue().doubleValue() < askPrice)
/*      */           {
/*      */             ValidateOrder tmp4480_4479 = vo; tmp4480_4479.getClass(); result.add(new ValidationMessage("validation.stop.loss.less.than.ask", stopLossOrder.getPriceStop().getValue().doubleValue(), askPrice));
/*      */           }
/* 1157 */           if (!isContestOrderValid(stopLossOrder.getPriceStop().getValue().doubleValue(), askPrice, currInstr))
/*      */           {
/* 1161 */             double maxPrice = askPrice + askPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */             ValidateOrder tmp4558_4557 = vo; tmp4558_4557.getClass(); result.add(new ValidationMessage("validation.sl.gap.to.large.am", new Object[] { getFormatedPrice(currInstr, stopLossOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, askPrice), getFormatedPrice(currInstr, maxPrice) }));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1171 */       if (takeProfitOrder != null) {
/* 1172 */         if (takeProfitOrder.getPriceStop().getValue().doubleValue() > askPrice)
/*      */         {
/*      */           ValidateOrder tmp4655_4654 = vo; tmp4655_4654.getClass(); result.add(new ValidationMessage("validation.take.profit.greater.than.ask", takeProfitOrder.getPriceStop().getValue().doubleValue(), askPrice));
/*      */         }
/* 1175 */         if (!isContestOrderValid(takeProfitOrder.getPriceStop().getValue().doubleValue(), askPrice, currInstr))
/*      */         {
/* 1179 */           double minPrice = askPrice - askPrice * (storage.restoreContestRateByInstrument(currInstr).doubleValue() / 100.0D);
/*      */           ValidateOrder tmp4733_4732 = vo; tmp4733_4732.getClass(); result.add(new ValidationMessage("validation.tp.gap.to.large.am", new Object[] { getFormatedPrice(currInstr, takeProfitOrder.getPriceStop().getValue().doubleValue()), storage.restoreContestRateByInstrument(currInstr), getFormatedPrice(currInstr, askPrice), getFormatedPrice(currInstr, minPrice) }));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1189 */     return result;
/*      */   }
/*      */ 
/*      */   private static String getFormatedPrice(Instrument instrument, double price) {
/* 1193 */     String PRICE_TEMPLATE = "{0}</b><font size=-2>{1}</font><b>";
/* 1194 */     boolean isInstrumentJPY = (Currency.getInstance("JPY").equals(instrument.getSecondaryCurrency())) || (Currency.getInstance("HUF").equals(instrument.getSecondaryCurrency()));
/*      */ 
/* 1196 */     DecimalFormat PRICE_TOTAL_FORMAT = isInstrumentJPY ? new DecimalFormat("0.000") : new DecimalFormat("0.00000");
/* 1197 */     int lastNumber = isInstrumentJPY ? 3 : 5;
/*      */ 
/* 1199 */     String formatedPrice = PRICE_TOTAL_FORMAT.format(price);
/* 1200 */     lastNumber += formatedPrice.indexOf(".");
/*      */ 
/* 1202 */     String priceBig = formatedPrice.substring(0, lastNumber);
/* 1203 */     String priceSmall = formatedPrice.substring(lastNumber);
/*      */ 
/* 1205 */     return MessageFormat.format(PRICE_TEMPLATE, new Object[] { priceBig, priceSmall });
/*      */   }
/*      */ 
/*      */   public static double round(double value, int decimalPlace) {
/* 1209 */     double powerOfTen = 1.0D;
/* 1210 */     while (decimalPlace-- > 0)
/* 1211 */       powerOfTen *= 10.0D;
/* 1212 */     return Math.round(value * powerOfTen) / powerOfTen;
/*      */   }
/*      */ 
/*      */   public class OrderValidationBean
/*      */   {
/* 1263 */     private List<ValidateOrder.ValidationMessage> messages = new ArrayList();
/*      */ 
/*      */     public OrderValidationBean()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean validationOk() {
/* 1270 */       return this.messages.isEmpty();
/*      */     }
/*      */ 
/*      */     public void add(ValidateOrder.ValidationMessage message)
/*      */     {
/* 1275 */       this.messages.add(message);
/*      */     }
/*      */ 
/*      */     public List<ValidateOrder.ValidationMessage> getMessages() {
/* 1279 */       return this.messages;
/*      */     }
/*      */ 
/*      */     public void setMessages(List<ValidateOrder.ValidationMessage> messages) {
/* 1283 */       this.messages = messages;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class ValidationMessage
/*      */   {
/*      */     private String messageKey;
/*      */     private Object[] params;
/*      */ 
/*      */     public ValidationMessage(String messageKey, double firstParam, double secondParam)
/*      */     {
/* 1225 */       this.messageKey = messageKey;
/* 1226 */       this.params = new Object[2];
/* 1227 */       this.params[0] = Double.valueOf(firstParam);
/* 1228 */       this.params[1] = Double.valueOf(secondParam);
/*      */     }
/*      */ 
/*      */     ValidationMessage(String messageKey) {
/* 1232 */       this.messageKey = messageKey;
/*      */     }
/*      */ 
/*      */     public ValidationMessage(String messageKey, Object[] params) {
/* 1236 */       this.messageKey = messageKey;
/* 1237 */       this.params = params;
/*      */     }
/*      */ 
/*      */     public String getMessageKey() {
/* 1241 */       return this.messageKey;
/*      */     }
/*      */ 
/*      */     public void setMessageKey(String messageKey)
/*      */     {
/* 1246 */       this.messageKey = messageKey;
/*      */     }
/*      */ 
/*      */     public Object[] getParams()
/*      */     {
/* 1251 */       return this.params;
/*      */     }
/*      */ 
/*      */     public void setParams(Object[] params)
/*      */     {
/* 1256 */       this.params = params;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.validation.ValidateOrder
 * JD-Core Version:    0.6.0
 */