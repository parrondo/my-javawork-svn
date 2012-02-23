/*     */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.List;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ 
/*     */ public class AlerterPanel extends JPanel
/*     */   implements MarketStateWrapperListener
/*     */ {
/*     */   private AlerterTable alerterTable;
/*  39 */   private JLocalizableButton addButton = new JLocalizableButton("price.alert.button.add");
/*  40 */   private JLocalizableButton deleteButton = new JLocalizableButton("price.alert.button.delete");
/*     */ 
/*     */   public AlerterPanel() {
/*  43 */     this.alerterTable = new AlerterTable(new AlerterTableModel());
/*     */ 
/*  45 */     setLayout(new FlowLayout(3));
/*  46 */     JScrollPane scrollPane = new JScrollPane(this.alerterTable);
/*  47 */     add(scrollPane);
/*  48 */     JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
/*  49 */     buttonPanel.add(this.addButton);
/*  50 */     buttonPanel.add(this.deleteButton);
/*  51 */     add(buttonPanel);
/*     */ 
/*  53 */     this.addButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  56 */         AlerterPanel.this.alerterTable.stopCellEditing();
/*  57 */         AlerterTableModel model = (AlerterTableModel)AlerterPanel.this.alerterTable.getModel();
/*  58 */         model.addAlert(AlerterPanel.this.createAlert());
/*  59 */         AlerterPanel.this.updateButtonStates();
/*     */       }
/*     */     });
/*  63 */     this.deleteButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  66 */         AlerterPanel.this.alerterTable.stopCellEditing();
/*  67 */         AlerterTableModel model = (AlerterTableModel)AlerterPanel.this.alerterTable.getModel();
/*  68 */         int selectedRow = AlerterPanel.this.alerterTable.getSelectedRow();
/*  69 */         if (selectedRow >= 0) {
/*  70 */           model.deleteAlert(AlerterPanel.this.alerterTable.getSelectedRow());
/*     */         }
/*  72 */         AlerterPanel.this.updateButtonStates();
/*     */       }
/*     */     });
/*  76 */     this.alerterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e) {
/*  79 */         AlerterPanel.this.updateButtonStates();
/*     */       }
/*     */     });
/*  83 */     ((ClientForm)GreedContext.get("clientGui")).addMarketWatcher(this);
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market)
/*     */   {
/*  89 */     CurrencyOffer bestBid = market.getBestOffer(OfferSide.BID);
/*  90 */     CurrencyOffer bestAsk = market.getBestOffer(OfferSide.ASK);
/*  91 */     BigDecimal bestBidPrice = bestBid != null ? bestBid.getPrice().getValue() : BigDecimal.ZERO;
/*  92 */     BigDecimal bestAskPrice = bestAsk != null ? bestAsk.getPrice().getValue() : BigDecimal.ZERO;
/*     */ 
/*  94 */     AlerterTableModel alerterModel = (AlerterTableModel)this.alerterTable.getModel();
/*  95 */     List alerts = alerterModel.getAlertList();
/*     */ 
/*  97 */     for (Alert alert : alerts)
/*  98 */       if ((alert.getStatus() == AlerterStatus.ACTIVE) && (alert.getInstrument().toString().equals(market.getInstrument()))) {
/*  99 */         if ((Condition.BID_LESS == alert.getCondition()) && (bestBidPrice.doubleValue() < alert.getPrice().doubleValue())) {
/* 100 */           activateAlert(alert, bestBidPrice.doubleValue());
/*     */         }
/* 102 */         if ((Condition.BID_GREATER == alert.getCondition()) && (bestBidPrice.doubleValue() > alert.getPrice().doubleValue())) {
/* 103 */           activateAlert(alert, bestBidPrice.doubleValue());
/*     */         }
/* 105 */         if ((Condition.ASK_LESS == alert.getCondition()) && (bestAskPrice.doubleValue() < alert.getPrice().doubleValue())) {
/* 106 */           activateAlert(alert, bestBidPrice.doubleValue());
/*     */         }
/* 108 */         if ((Condition.ASK_GREATER == alert.getCondition()) && (bestAskPrice.doubleValue() > alert.getPrice().doubleValue()))
/* 109 */           activateAlert(alert, bestBidPrice.doubleValue());
/*     */       }
/*     */   }
/*     */ 
/*     */   private void updateButtonStates()
/*     */   {
/* 117 */     this.deleteButton.setEnabled(this.alerterTable.getSelectedRow() >= 0);
/*     */   }
/*     */ 
/*     */   private Alert createAlert() {
/* 121 */     return new Alert(Instrument.EURUSD);
/*     */   }
/*     */ 
/*     */   private void activateAlert(Alert alert, double price) {
/* 125 */     AlerterTableModel model = (AlerterTableModel)this.alerterTable.getModel();
/* 126 */     model.fireTableDataChanged();
/* 127 */     alert.complete();
/* 128 */     if (alert.getNotification() == AlerterNotification.POPUP)
/*     */     {
/* 130 */       StringBuffer sb = new StringBuffer();
/* 131 */       sb.append(alert.getInstrument());
/* 132 */       sb.append(" ");
/* 133 */       sb.append(LocalizationManager.getTextWithArguments(alert.getCondition().getLocalizationKey(), new Object[] { alert.getCondition().getLocalizationParam() }));
/*     */ 
/* 135 */       sb.append(" ");
/* 136 */       sb.append(alert.getPrice());
/*     */ 
/* 138 */       showPopup(LocalizationManager.getTextWithArgumentKeys("price.alert.notification", new Object[] { sb.toString(), String.valueOf(price) }));
/*     */     }
/* 140 */     else if (alert.getNotification() == AlerterNotification.BEEP)
/*     */     {
/* 142 */       synchronized (this) {
/* 143 */         for (int i = 0; i < 5; i++)
/*     */           try {
/* 145 */             Toolkit.getDefaultToolkit().beep();
/* 146 */             wait(1000L);
/*     */           }
/*     */           catch (InterruptedException ex)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void showPopup(String text)
/*     */   {
/* 157 */     JOptionPane optionPane = new JOptionPane(text, 1);
/* 158 */     JDialog dialog = optionPane.createDialog(SwingUtilities.getRoot(this), LocalizationManager.getText("price.alert.title"));
/* 159 */     dialog.setVisible(true);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.AlerterPanel
 * JD-Core Version:    0.6.0
 */