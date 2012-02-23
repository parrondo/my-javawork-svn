/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.api.IEngine.OrderCommand;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*    */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.CloseData;
/*    */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import com.dukascopy.transport.common.model.type.Money;
/*    */ import com.dukascopy.transport.common.model.type.OrderState;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.math.BigDecimal;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.Map;
/*    */ import java.util.TimeZone;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrderPrint extends AgentBase.CommonExecution
/*    */ {
/* 34 */   private static Logger log = LoggerFactory.getLogger(MOrderPrint.class);
/* 35 */   private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT0");
/* 36 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat() { } ;
/*    */ 
/*    */   public void execute(int id)
/*    */     throws MTAgentException
/*    */   {
/* 45 */     Integer mtId = new Integer(id);
/* 46 */     OrderGroupMessage msg = getOrderGroup(mtId);
/* 47 */     if (msg != null) {
/* 48 */       msg.getOpeningOrder().getSide();
/* 49 */       String swap = "0";
/*    */ 
/* 51 */       String closeTime = "";
/* 52 */       String closePrice = "";
/*    */ 
/* 54 */       if (msg.getOpeningOrder().getOrderState() == OrderState.FILLED)
/*    */       {
/* 56 */         OrderHistoricalData historicalData = MTAPIHelpers.getOrderGroupHistoricalData(msg.getOrderGroupId(), Instrument.fromString(msg.getInstrument()));
/*    */ 
/* 59 */         if (historicalData.isClosed()) {
/* 60 */           OrderHistoricalData.CloseData closeData = (OrderHistoricalData.CloseData)historicalData.getCloseDataMap().get(msg.getOrderGroupId());
/*    */ 
/* 63 */           closeTime = DATE_FORMAT.format(new Date(closeData.getCloseTime()));
/*    */ 
/* 65 */           closePrice = msg.getOpeningOrder().getPriceClient().getValue().toString();
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/* 70 */       String openTime = DATE_FORMAT.format(msg.getOpeningOrderTimestamp());
/*    */ 
/* 72 */       IEngine.OrderCommand tradeOperation = OrdersProvider.convert(msg.getOpeningOrder().getSide(), msg.getOpeningOrder().getStopDirection(), false, msg.getOpeningOrder().isPlaceOffer());
/*    */ 
/* 77 */       String commission = msg.getOpeningOrder().getOrderCommission() != null ? msg.getOpeningOrder().getOrderCommission().toString() : "0.0";
/*    */ 
/* 80 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(new StringBuilder().append("#").append(msg.getOpeningOrder().getOrderId()).append(" ").append(openTime).append(" ").append(tradeOperation.name()).append(" ").append(msg.getOpeningOrder().getAmount().getValue().toString()).append(" ").append(msg.getOpeningOrder().getPriceClient().getValue().toString()).append(" ").append(msg.getStopLossOrder() != null ? new StringBuilder().append(msg.getStopLossOrder().getAmount().getValue().toString()).append(" ").toString() : "").append(msg.getTakeProfitOrder() != null ? new StringBuilder().append(msg.getStopLossOrder().getAmount().getValue().toString()).append(" ").toString() : "").append(closeTime).append(" ").append(closePrice).append(" ").append(commission).append(" ").append(swap).append(" ").append(msg.getOpeningOrder().getProperty("trailingLimit")).append(" ").append(msg.getOpeningOrder().getTag()).append(" ").append(msg.getOpeningOrder().getExternalSysId()).append(" ").append("").toString());
/*    */ 
/* 138 */       setError(id, 0, "ERR_NO_ERROR_MSG");
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrderPrint
 * JD-Core Version:    0.6.0
 */