/*    */ package com.dukascopy.dds2.greed.gui.l10n.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import javax.swing.event.TableModelEvent;
/*    */ import javax.swing.event.TableModelListener;
/*    */ import javax.swing.table.TableModel;
/*    */ 
/*    */ public class JLocalizableTableModelListener
/*    */   implements TableModelListener
/*    */ {
/*    */   String localizationKey;
/*    */   int panelId;
/*    */ 
/*    */   public JLocalizableTableModelListener(int panelId, String localizationKey)
/*    */   {
/* 27 */     this.panelId = panelId;
/* 28 */     this.localizationKey = localizationKey;
/*    */   }
/*    */ 
/*    */   public void tableChanged(TableModelEvent e) {
/* 32 */     if (GreedContext.isStrategyAllowed())
/* 33 */       jForexUpdater(e);
/*    */     else
/* 35 */       jClientUpdater(e);
/*    */   }
/*    */ 
/*    */   private void jForexUpdater(TableModelEvent e)
/*    */   {
/* 40 */     if (!(GreedContext.get("layoutManager") instanceof JForexClientFormLayoutManager)) {
/* 41 */       return;
/*    */     }
/*    */ 
/* 44 */     JForexClientFormLayoutManager jForexLayout = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 45 */     TabsAndFramesTabbedPane tabbedPane = jForexLayout.getTabbedPane();
/* 46 */     String localizedTitle = LocalizationManager.getText(this.localizationKey);
/*    */ 
/* 48 */     int rowCount = ((TableModel)e.getSource()).getRowCount();
/* 49 */     if (rowCount > 0) {
/* 50 */       localizedTitle = localizedTitle + " (" + rowCount + ")";
/*    */     }
/*    */ 
/* 53 */     tabbedPane.setTitleForPanelId(this.panelId, localizedTitle);
/*    */   }
/*    */ 
/*    */   private void jClientUpdater(TableModelEvent e)
/*    */   {
/* 58 */     int rowCount = ((TableModel)e.getSource()).getRowCount();
/* 59 */     ClientForm client = (ClientForm)GreedContext.get("clientGui");
/*    */ 
/* 61 */     if (client == null) {
/* 62 */       return;
/*    */     }
/*    */ 
/* 65 */     if (this.panelId == 0)
/* 66 */       client.getExposurePanel().getHeader().setRowCount(rowCount);
/* 67 */     else if (this.panelId == 1)
/* 68 */       client.getPositionsPanel().getHeader().setRowCount(rowCount);
/* 69 */     else if (this.panelId == 2)
/* 70 */       client.getOrdersPanel().getHeader().setRowCount(rowCount);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTableModelListener
 * JD-Core Version:    0.6.0
 */