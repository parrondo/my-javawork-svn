/*     */ package com.dukascopy.dds2.greed.gui.component.orders;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.StopPriceRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.util.List;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ 
/*     */ public class OrdersTableRenderer extends DefaultTableCellRenderer
/*     */   implements PlatformSpecific
/*     */ {
/*  24 */   private static final Color HIGHLIGHT_COLOR = new Color(219, 232, 255);
/*     */   private OrderCommonTableModel ordersModel;
/*     */   private TableSorter tableSorter;
/*     */   private DefaultTableCellRenderer cellRenderer;
/*     */   private static Font defaultFont;
/*     */ 
/*     */   public OrdersTableRenderer(OrderCommonTableModel ordersModel, TableSorter tableSorter)
/*     */   {
/*  33 */     this.ordersModel = ordersModel;
/*  34 */     this.tableSorter = tableSorter;
/*     */   }
/*     */ 
/*     */   public OrdersTableRenderer(OrderCommonTableModel ordersModel, TableSorter tableSorter, DefaultTableCellRenderer chainedCellRenderer) {
/*  38 */     this.ordersModel = ordersModel;
/*  39 */     this.tableSorter = tableSorter;
/*  40 */     this.cellRenderer = chainedCellRenderer;
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/*  44 */     switch (column)
/*     */     {
/*     */     }
/*     */ 
/*  50 */     return getDefaultRenderer(table, value, isSelected, hasFocus, row, column);
/*     */   }
/*     */ 
/*     */   private Component getDefaultRenderer(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/*  56 */     DefaultTableCellRenderer renderer = this;
/*     */ 
/*  58 */     if (defaultFont == null)
/*     */     {
/*  60 */       int size = renderer.getFont().getSize();
/*  61 */       if (MACOSX) {
/*  62 */         size = renderer.getFont().getSize() - 1;
/*     */       }
/*  64 */       defaultFont = new Font(renderer.getFont().getFamily(), 0, size);
/*     */     }
/*  66 */     renderer.setFont(defaultFont);
/*  67 */     if (this.cellRenderer != null)
/*  68 */       renderer = this.cellRenderer;
/*     */     else {
/*  70 */       renderer.setText((String)value);
/*     */     }
/*     */ 
/*  73 */     Color defaultForegroundActiveOrderColor = table.getForeground();
/*  74 */     Color defaultForegroundSelectedActiveOrderColor = table.getSelectionForeground();
/*  75 */     Color defaultForegroundInactiveOrderColor = Color.DARK_GRAY;
/*  76 */     Color defaultForegroundSelectedInactiveOrderColor = Color.DARK_GRAY;
/*     */ 
/*  78 */     boolean isInactive = false;
/*  79 */     int rowPaintedTranslated = -1;
/*  80 */     if (row > -1) {
/*  81 */       rowPaintedTranslated = this.tableSorter.modelIndex(row);
/*  82 */       if (rowPaintedTranslated > -1) {
/*  83 */         OrderMessage order = this.ordersModel.getOrder(rowPaintedTranslated);
/*  84 */         if ((null != order) && ((order.isStopLoss()) || (order.isTakeProfit()))) {
/*  85 */           OrderGroupMessage group = this.ordersModel.getGroup(rowPaintedTranslated);
/*  86 */           if (null != group) {
/*  87 */             OrderMessage openingOrder = group.getOpeningOrder();
/*  88 */             if ((null != openingOrder) && (!OrderState.FILLED.equals(openingOrder.getOrderState()))) {
/*  89 */               isInactive = true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  96 */     if (isSelected) {
/*  97 */       renderer.setBackground(table.getSelectionBackground());
/*  98 */       if (isInactive)
/*  99 */         renderer.setForeground(defaultForegroundSelectedInactiveOrderColor);
/*     */       else
/* 101 */         renderer.setForeground(defaultForegroundSelectedActiveOrderColor);
/*     */     }
/*     */     else {
/* 104 */       renderer.setBackground(table.getBackground());
/* 105 */       if (isInactive)
/* 106 */         renderer.setForeground(defaultForegroundInactiveOrderColor);
/*     */       else {
/* 108 */         renderer.setForeground(defaultForegroundActiveOrderColor);
/*     */       }
/*     */     }
/*     */ 
/* 112 */     if (this.cellRenderer != null) {
/* 113 */       ((StopPriceRenderer)this.cellRenderer).setValue(value);
/*     */     }
/* 115 */     renderer.setOpaque(true);
/*     */ 
/* 117 */     int rowSelected = table.getSelectedRow();
/* 118 */     if (rowSelected > -1) {
/* 119 */       int rowSelectedTranslated = this.tableSorter.modelIndex(rowSelected);
/* 120 */       if ((rowSelectedTranslated > -1) && 
/* 121 */         (rowPaintedTranslated > -1))
/*     */       {
/* 123 */         OrderMessage orderSelected = this.ordersModel.getOrder(rowSelectedTranslated);
/* 124 */         OrderMessage orderPainted = this.ordersModel.getOrder(rowPaintedTranslated);
/* 125 */         if ((orderSelected != null) && (orderPainted != null) && 
/* 126 */           (orderSelected.getOrderGroupId().equals(orderPainted.getOrderGroupId())) && (
/* 126 */           (testOrderDependence(orderSelected, orderPainted)) || (testOcoOrderDependence(orderSelected, orderPainted))))
/*     */         {
/* 132 */           if (!isSelected)
/* 133 */             renderer.setBackground(HIGHLIGHT_COLOR);
/*     */           else {
/* 135 */             renderer.setBackground(GreedContext.SELECTION_COLOR);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 142 */       List selectedGroupIds = ((OrdersTable)table).getSelectedGroupIds();
/* 143 */       if ((rowPaintedTranslated > -1) && (selectedGroupIds != null) && (!selectedGroupIds.isEmpty())) {
/* 144 */         OrderMessage orderPainted = this.ordersModel.getOrder(rowPaintedTranslated);
/* 145 */         if ((orderPainted != null) && (((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()) && (selectedGroupIds.contains(orderPainted.getOrderGroupId()))) || ((!GreedContext.isGlobal()) && (GreedContext.isGlobalExtended()) && (selectedGroupIds.contains(orderPainted.getOrderId())))))
/*     */         {
/* 148 */           renderer.setBackground(HIGHLIGHT_COLOR);
/*     */         }
/*     */       }
/*     */     }
/* 152 */     return renderer;
/*     */   }
/*     */ 
/*     */   private boolean testOcoOrderDependence(OrderMessage order1, OrderMessage order2)
/*     */   {
/* 157 */     boolean isOco = (order1.isOco().booleanValue()) && (order2.isOco().booleanValue()) && (order1.isOpening()) && (order2.isOpening());
/*     */ 
/* 159 */     if ((GreedContext.isGlobalExtended()) && (order1.getOcoGroup() != null) && (order2.getOcoGroup() != null))
/*     */     {
/* 163 */       isOco = order1.getOcoGroup().equals(order2.getOcoGroup());
/*     */     }
/*     */ 
/* 167 */     return isOco;
/*     */   }
/*     */ 
/*     */   private boolean testOrderDependence(OrderMessage order1, OrderMessage order2)
/*     */   {
/* 172 */     return ((order2.getIfdParentOrderId() != null) && (order2.getIfdParentOrderId().equals(order1.getOrderId()))) || ((order1.getIfdParentOrderId() != null) && (order1.getIfdParentOrderId().equals(order2.getOrderId()))) || ((order1.getIfdParentOrderId() != null) && (order2.getIfdParentOrderId() != null) && (order1.getIfdParentOrderId().length() > 1) && (order1.getIfdParentOrderId().equals(order2.getIfdParentOrderId())));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.OrdersTableRenderer
 * JD-Core Version:    0.6.0
 */