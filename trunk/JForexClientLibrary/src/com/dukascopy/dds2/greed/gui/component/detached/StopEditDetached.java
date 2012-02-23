/*     */ package com.dukascopy.dds2.greed.gui.component.detached;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.AStopOrderEditPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.PartCloseOrderEditPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.StopOrderEditPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableFrame;
/*     */ import com.dukascopy.dds2.greed.model.AccountInfoListener;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.ImageIcon;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StopEditDetached extends JLocalizableFrame
/*     */   implements MarketStateWrapperListener, AccountInfoListener
/*     */ {
/*  39 */   private static Logger LOGGER = LoggerFactory.getLogger(StopEditDetached.class);
/*     */   private AStopOrderEditPanel panel;
/*     */   public static final String ID_JF_STOPEDITDETACHED = "ID_JF_STOPEDITDETACHED";
/*  44 */   private ActionListener cancelActionListener = null;
/*     */ 
/*  46 */   private static Map<String, StopEditDetached> openedFramesForOrders = new HashMap();
/*     */ 
/*     */   public StopEditDetached(OrderGroupMessage group, StopOrderType orderType) {
/*  49 */     this(group, orderType, null);
/*     */   }
/*     */ 
/*     */   public StopEditDetached(OrderGroupMessage group, StopOrderType orderType, String editableOrderId)
/*     */     throws HeadlessException
/*     */   {
/*  56 */     this(group, orderType, editableOrderId, null, null);
/*     */   }
/*     */ 
/*     */   public StopEditDetached(OrderGroupMessage group, StopOrderType orderType, String editableOrderId, ActionListener cancelActionListener)
/*     */     throws HeadlessException
/*     */   {
/*  64 */     this(group, orderType, editableOrderId, null, null);
/*  65 */     if ((this.panel instanceof StopOrderEditPanel))
/*  66 */       this.cancelActionListener = cancelActionListener;
/*     */   }
/*     */ 
/*     */   public StopEditDetached(OrderGroupMessage group, StopOrderType orderType, String editableOrderId, double price, ActionListener cancelActionListener)
/*     */     throws HeadlessException
/*     */   {
/*  76 */     this(group, orderType, editableOrderId, null, null);
/*  77 */     if ((this.panel instanceof StopOrderEditPanel)) {
/*  78 */       ((StopOrderEditPanel)this.panel).setStopPrice(BigDecimal.valueOf(price).toPlainString(), null);
/*  79 */       ((StopOrderEditPanel)this.panel).drawLine();
/*  80 */       this.cancelActionListener = cancelActionListener;
/*     */     }
/*     */   }
/*     */ 
/*     */   public StopEditDetached(OrderGroupMessage group, StopOrderType orderType, String editableOrderId, String openingOrderId, BigDecimal priceStop)
/*     */     throws HeadlessException
/*     */   {
/*  91 */     setName("ID_JF_STOPEDITDETACHED");
/*  92 */     setLocationRelativeTo((ClientForm)GreedContext.get("clientGui"));
/*  93 */     openedFramesForOrders.put(editableOrderId, this);
/*     */     try {
/*  95 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */     } catch (Exception e) {
/*  97 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*  99 */     if (orderType.equals(StopOrderType.PART_CLOSE)) {
/* 100 */       this.panel = new PartCloseOrderEditPanel(this, group);
/* 101 */       setParams(new Object[] { GreedContext.getOrderGroupIdForView(group.getOrderGroupId()) });
/* 102 */       setTitle("frame.cond.close");
/* 103 */     } else if (StopOrderType.OPEN_IF.equals(orderType)) {
/* 104 */       this.panel = new StopOrderEditPanel(this, group, orderType, editableOrderId, null, null);
/* 105 */       setTitle("frame.edit.entry.order");
/*     */     } else {
/* 107 */       this.panel = new StopOrderEditPanel(this, group, orderType, editableOrderId, openingOrderId, priceStop);
/* 108 */       setTitle("frame.edit.order");
/*     */     }
/* 110 */     setContentPane(this.panel);
/* 111 */     setResizable(false);
/* 112 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 113 */     StopEditDetached stopEditDetached = this;
/* 114 */     gui.addMarketWatcher(stopEditDetached);
/* 115 */     gui.addAccountInfoWatcher(stopEditDetached);
/* 116 */     addWindowListener(new WindowAdapter(gui, stopEditDetached, editableOrderId) {
/*     */       public void windowClosed(WindowEvent e) {
/* 118 */         if (StopEditDetached.this.cancelActionListener != null) {
/* 119 */           StopEditDetached.this.cancelActionListener.actionPerformed(null);
/*     */         }
/* 121 */         this.val$gui.removeMarketWatcher(this.val$stopEditDetached);
/* 122 */         this.val$gui.removeAccountInfoWatcher(this.val$stopEditDetached);
/* 123 */         if ((StopEditDetached.this.panel instanceof StopOrderEditPanel)) {
/* 124 */           ((StopOrderEditPanel)StopEditDetached.this.panel).removeLine();
/*     */         }
/*     */ 
/* 127 */         StopEditDetached.openedFramesForOrders.remove(this.val$editableOrderId);
/*     */       }
/*     */     });
/* 130 */     if ((this.panel instanceof StopOrderEditPanel)) {
/* 131 */       ((StopOrderEditPanel)this.panel).drawLine();
/*     */     }
/*     */ 
/* 134 */     display();
/*     */   }
/*     */ 
/*     */   private void display() {
/* 138 */     pack();
/* 139 */     setDefaultCloseOperation(2);
/* 140 */     setLocationRelativeTo((ClientForm)GreedContext.get("clientGui"));
/* 141 */     setAlwaysOnTop(true);
/* 142 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market) {
/* 146 */     this.panel.onMarketState(market);
/*     */   }
/*     */ 
/*     */   public void onAccountInfo(AccountInfoMessage accountInfo) {
/* 150 */     this.panel.onAccountInfo(accountInfo);
/*     */   }
/*     */ 
/*     */   public AStopOrderEditPanel getPanel() {
/* 154 */     return this.panel;
/*     */   }
/*     */ 
/*     */   public static StopEditDetached getOpenedFrame(String orderId) {
/* 158 */     return (StopEditDetached)openedFramesForOrders.get(orderId);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.detached.StopEditDetached
 * JD-Core Version:    0.6.0
 */