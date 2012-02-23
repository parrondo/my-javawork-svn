/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTable;
/*    */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*    */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*    */ import java.util.Map;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class UpdateGuiDefaultsAction extends AppActionEvent
/*    */ {
/* 19 */   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateGuiDefaultsAction.class);
/*    */   private String defaultAmount;
/*    */   private String defaultSlippage;
/*    */   private String defaultStopLossOffset;
/*    */   private String defaultOpenIfOffset;
/*    */   private String defaultTakeProfitOffset;
/*    */ 
/*    */   public UpdateGuiDefaultsAction(Object source, Map clientSettings)
/*    */   {
/* 28 */     super(source, false, true);
/* 29 */     this.defaultAmount = ((String)clientSettings.get("amount"));
/* 30 */     this.defaultSlippage = ((String)clientSettings.get("slippageVal"));
/* 31 */     this.defaultStopLossOffset = ((String)clientSettings.get("slVal"));
/* 32 */     this.defaultOpenIfOffset = ((String)clientSettings.get("entryVal"));
/* 33 */     this.defaultTakeProfitOffset = ((String)clientSettings.get("tpVal"));
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 48 */     if ((GreedContext.getConfig("backend.settings.updated") != null) && (GreedContext.getConfig("backend.settings.updated").equals("true")))
/*    */     {
/* 50 */       LOGGER.info("not saving: not config was set");
/* 51 */       return;
/*    */     }
/*    */ 
/* 54 */     LotAmountChanger.doChangeLotAmount();
/* 55 */     updateTablesData();
/*    */ 
/* 57 */     if ((this.defaultAmount != null) && (this.defaultAmount.trim().length() > 0)) {
/* 58 */       ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getOrderEntryPanel().setAmount(this.defaultAmount);
/* 59 */       ((MarketOverviewFrame)GreedContext.get("Dock")).setDefaultAmount();
/* 60 */       ((ClientForm)GreedContext.get("clientGui")).updateAmountOnMarketWatchers(this.defaultAmount);
/* 61 */       GreedContext.setConfig("backend.settings.updated", "true");
/*    */     }
/*    */ 
/* 64 */     if ((this.defaultSlippage != null) && (this.defaultSlippage.trim().length() > 0)) {
/* 65 */       ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getOrderEntryPanel().setSlippage(this.defaultSlippage);
/* 66 */       ((MarketOverviewFrame)GreedContext.get("Dock")).setDefaultSlippage();
/* 67 */       ((ClientForm)GreedContext.get("clientGui")).updateSlippageOnMarketWatchers(this.defaultSlippage);
/* 68 */       GreedContext.setConfig("backend.settings.updated", "true");
/*    */     }
/*    */ 
/* 71 */     if ((this.defaultStopLossOffset != null) && (this.defaultStopLossOffset.trim().length() > 0)) {
/* 72 */       GreedContext.setConfig("backend.settings.updated", "true");
/*    */     }
/* 74 */     if ((this.defaultOpenIfOffset != null) && (this.defaultOpenIfOffset.trim().length() > 0)) {
/* 75 */       GreedContext.setConfig("backend.settings.updated", "true");
/*    */     }
/* 77 */     if ((this.defaultTakeProfitOffset != null) && (this.defaultTakeProfitOffset.trim().length() > 0))
/* 78 */       GreedContext.setConfig("backend.settings.updated", "true");
/*    */   }
/*    */ 
/*    */   public static void updateTablesData()
/*    */   {
/* 86 */     OrdersPanel ordersPanel = ((ClientForm)GreedContext.get("clientGui")).getOrdersPanel();
/* 87 */     ordersPanel.getModel().fireTableDataChanged();
/*    */ 
/* 89 */     PositionsTable positionsTable = ((ClientForm)GreedContext.get("clientGui")).getPositionsPanel().getTable();
/* 90 */     positionsTable.getPositionsModel().fireTableDataChanged();
/*    */ 
/* 92 */     ExposureTable exposureTable = ((ClientForm)GreedContext.get("clientGui")).getExposurePanel().getTable();
/* 93 */     exposureTable.getExposureTableModel().fireTableDataChanged();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.UpdateGuiDefaultsAction
 * JD-Core Version:    0.6.0
 */