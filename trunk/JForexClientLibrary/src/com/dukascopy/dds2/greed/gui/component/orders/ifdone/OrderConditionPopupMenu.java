/*     */ package com.dukascopy.dds2.greed.gui.component.orders.ifdone;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.model.OrderCondition;
/*     */ import com.dukascopy.dds2.greed.model.OrderType;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPopupMenu;
/*     */ 
/*     */ public class OrderConditionPopupMenu extends JPopupMenu
/*     */ {
/*  15 */   private final ButtonGroup group = new ButtonGroup();
/*     */ 
/*  17 */   private final JMenuItem bidGreatItem = new JLocalizableMenuItem(OrderCondition.GREATER_BID.popupTextKey());
/*  18 */   private final JMenuItem bidLessItem = new JLocalizableMenuItem(OrderCondition.LESS_BID.popupTextKey());
/*  19 */   private final JMenuItem bidEqualsItem = new JLocalizableMenuItem(OrderCondition.LIMIT_BID.popupTextKey());
/*     */ 
/*  21 */   private final JMenuItem askGreatItem = new JLocalizableMenuItem(OrderCondition.GREATER_ASK.popupTextKey());
/*  22 */   private final JMenuItem askLessItem = new JLocalizableMenuItem(OrderCondition.LESS_ASK.popupTextKey());
/*  23 */   private final JMenuItem askEqualsItem = new JLocalizableMenuItem(OrderCondition.LIMIT_ASK.popupTextKey());
/*     */ 
/*  25 */   private final JMenuItem atMarketItem = new JLocalizableMenuItem(OrderCondition.MARKET.popupTextKey());
/*  26 */   private final JMenuItem bidEqualsMitItem = new JLocalizableMenuItem(OrderCondition.MIT_BID.popupTextKey());
/*  27 */   private final JMenuItem askEqualsMitItem = new JLocalizableMenuItem(OrderCondition.MIT_ASK.popupTextKey());
/*     */   private final JButton invoker;
/*     */   private final OrderType orderType;
/*     */   private IEntryOrderHolder holder;
/*     */   private OrderCondition selectedStopDirection;
/*     */ 
/*     */   public OrderConditionPopupMenu(JButton invoker, OrderType stopOrderType, IEntryOrderHolder holder)
/*     */   {
/*  39 */     this.invoker = invoker;
/*  40 */     this.orderType = stopOrderType;
/*  41 */     this.holder = holder;
/*  42 */     build();
/*     */   }
/*     */ 
/*     */   private void build() {
/*  46 */     createGUI();
/*  47 */     addListeners();
/*     */   }
/*     */ 
/*     */   private void createGUI() {
/*  51 */     addItem(this.atMarketItem, OrderCondition.MARKET);
/*     */ 
/*  53 */     addItem(this.bidGreatItem, OrderCondition.GREATER_BID);
/*  54 */     addItem(this.bidLessItem, OrderCondition.LESS_BID);
/*     */ 
/*  56 */     addItem(this.askGreatItem, OrderCondition.GREATER_ASK);
/*  57 */     addItem(this.askLessItem, OrderCondition.LESS_ASK);
/*     */ 
/*  59 */     addItem(this.bidEqualsItem, OrderCondition.LIMIT_BID);
/*  60 */     addItem(this.askEqualsItem, OrderCondition.LIMIT_ASK);
/*     */ 
/*  62 */     addItem(this.bidEqualsMitItem, OrderCondition.MIT_BID);
/*  63 */     addItem(this.askEqualsMitItem, OrderCondition.MIT_ASK);
/*     */ 
/*  65 */     filterPopup();
/*     */   }
/*     */ 
/*     */   private void addListeners() {
/*  69 */     MouseAdapter stopButtonMouseAdapter = new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent mouseEvent) {
/*  71 */         if (!OrderConditionPopupMenu.this.invoker.isEnabled()) return;
/*  72 */         OrderConditionPopupMenu.this.maybeShowPopup(mouseEvent);
/*     */       }
/*     */     };
/*  75 */     this.invoker.addMouseListener(stopButtonMouseAdapter);
/*     */   }
/*     */ 
/*     */   private void maybeShowPopup(MouseEvent mouseEvent) {
/*  79 */     filterPopup(true);
/*  80 */     show(this.invoker, mouseEvent.getX(), mouseEvent.getY());
/*     */   }
/*     */ 
/*     */   public void filterPopup() {
/*  84 */     filterPopup(false);
/*     */   }
/*     */ 
/*     */   private void filterPopup(boolean onButtonPress)
/*     */   {
/*  89 */     if ((onButtonPress) && 
/*  90 */       (!this.invoker.isEnabled())) return;
/*     */ 
/*  92 */     switch (3.$SwitchMap$com$dukascopy$dds2$greed$model$OrderType[this.orderType.ordinal()]) {
/*     */     case 1:
/*  94 */       buildIfDoneLimitConditions();
/*  95 */       break;
/*     */     case 2:
/*  97 */       buildIfDoneStopConditions();
/*  98 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void buildIfDoneStopConditions()
/*     */   {
/* 105 */     doInvisiblePopup();
/* 106 */     if (OrderSide.SELL == this.holder.getOrderSide()) {
/* 107 */       this.askGreatItem.setVisible(true);
/* 108 */       this.bidGreatItem.setVisible(true);
/* 109 */       setSelectedStopDirectionOnly(OrderCondition.GREATER_BID);
/*     */     } else {
/* 111 */       this.askLessItem.setVisible(true);
/* 112 */       this.bidLessItem.setVisible(true);
/* 113 */       setSelectedStopDirectionOnly(OrderCondition.LESS_ASK);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void buildIfDoneLimitConditions() {
/* 118 */     doInvisiblePopup();
/* 119 */     if (OrderSide.SELL == this.holder.getOrderSide()) {
/* 120 */       this.askEqualsItem.setVisible(true);
/* 121 */       setSelectedStopDirectionOnly(OrderCondition.LIMIT_ASK);
/*     */     } else {
/* 123 */       this.bidEqualsItem.setVisible(true);
/* 124 */       setSelectedStopDirectionOnly(OrderCondition.LIMIT_BID);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initItem(JMenuItem item, OrderCondition stopDirection) {
/* 129 */     item.setText(stopDirection.popupTextKey());
/* 130 */     item.addActionListener(new ActionListener(stopDirection) {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 132 */         OrderConditionPopupMenu.this.setSelectedStopDirection(this.val$stopDirection);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private final void addItem(JMenuItem item, OrderCondition stopDirection) {
/* 138 */     this.group.add(item);
/* 139 */     add(item);
/* 140 */     initItem(item, stopDirection);
/*     */   }
/*     */ 
/*     */   private void doInvisiblePopup() {
/* 144 */     for (Enumeration e = this.group.getElements(); e.hasMoreElements(); )
/* 145 */       ((JMenuItem)e.nextElement()).setVisible(false);
/*     */   }
/*     */ 
/*     */   public void show()
/*     */   {
/* 151 */     if (!this.invoker.isEnabled()) return;
/* 152 */     filterPopup();
/* 153 */     super.show(this.invoker, 0, 0);
/*     */   }
/*     */ 
/*     */   public OrderCondition getSelectedStopDirection() {
/* 157 */     return this.selectedStopDirection;
/*     */   }
/*     */ 
/*     */   public final void setSelectedStopDirectionOnly(OrderCondition selectedStopDirection) {
/* 161 */     this.selectedStopDirection = selectedStopDirection;
/* 162 */     this.invoker.setText(selectedStopDirection.buttonTextKey());
/*     */   }
/*     */ 
/*     */   public void setSelectedStopDirection(OrderCondition selectedStopDirection) {
/* 166 */     this.selectedStopDirection = selectedStopDirection;
/* 167 */     this.invoker.setText(selectedStopDirection.buttonTextKey());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.ifdone.OrderConditionPopupMenu
 * JD-Core Version:    0.6.0
 */