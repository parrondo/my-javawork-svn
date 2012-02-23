/*     */ package com.dukascopy.dds2.greed.gui.component.orders.ifdone;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.PriceAmountTextFieldM;
/*     */ import com.dukascopy.dds2.greed.gui.component.PriceSpinner;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.ConditionalOrderEntryPanel.DependandHeightButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.SlippageJSpinner;
/*     */ import com.dukascopy.dds2.greed.model.OrderType;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.math.BigDecimal;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class IfDonePanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  37 */   private final JLocalizableCheckBox stopCheckBox = new JLocalizableCheckBox("check.ifd.stop");
/*  38 */   private final PriceSpinner stopPriceField = new PriceSpinner();
/*     */   private ConditionalOrderEntryPanel.DependandHeightButton stopStopDirectionButton;
/*     */   private OrderConditionPopupMenu stopPopup;
/*  43 */   private final JLocalizableCheckBox slippageCheckBox = new JLocalizableCheckBox("check.slippage");
/*  44 */   private final SlippageJSpinner slippageSpinner = new SlippageJSpinner(5.0D, 1000.0D, 0.1D, 1, false);
/*     */ 
/*  47 */   private final JLocalizableCheckBox limitCheckBox = new JLocalizableCheckBox("check.ifd.limit");
/*  48 */   private final PriceSpinner limitPriceField = new PriceSpinner();
/*     */   private ConditionalOrderEntryPanel.DependandHeightButton limitStopDirectionButton;
/*     */   private OrderConditionPopupMenu limitPopup;
/*     */   private IEntryOrderHolder holder;
/*  54 */   private final ClientSettingsStorage settingsSaver = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */   private JLocalizableRoundedBorder border;
/*     */ 
/*     */   public IfDonePanel(IEntryOrderHolder holder)
/*     */   {
/*  60 */     this.holder = holder;
/*  61 */     build();
/*  62 */     initListaners();
/*  63 */     changeSideForPanel();
/*     */   }
/*     */ 
/*     */   private void build() {
/*  67 */     setLayout(new GridBagLayout());
/*  68 */     GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/*  70 */     int gridY = 0;
/*  71 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this, this.stopCheckBox);
/*     */ 
/*  73 */     this.stopStopDirectionButton = new ConditionalOrderEntryPanel.DependandHeightButton("", this.stopPriceField);
/*  74 */     this.stopPopup = new OrderConditionPopupMenu(this.stopStopDirectionButton, OrderType.IF_DONE_STOP, this.holder);
/*     */ 
/*  78 */     this.stopStopDirectionButton.setEnabled(this.stopCheckBox.isSelected());
/*  79 */     GridBagLayoutHelper.add(1, gridY, 0.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this, this.stopStopDirectionButton);
/*     */ 
/*  81 */     this.stopPriceField.setEnabled(false);
/*  82 */     this.stopPriceField.setMargin(new Insets(0, 0, 0, 1));
/*  83 */     this.stopPriceField.setHorizontalAlignment(4);
/*     */ 
/*  85 */     ((PriceAmountTextFieldM)this.stopPriceField.getEditor()).setColumns(7);
/*  86 */     GridBagLayoutHelper.add(2, gridY++, 1.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this, this.stopPriceField);
/*     */ 
/*  88 */     this.slippageCheckBox.setSelected(false);
/*  89 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 0, 1, 0, 0, 2, 21, gbc, this, this.slippageCheckBox);
/*     */ 
/*  91 */     this.slippageSpinner.setEnabled(false);
/*  92 */     this.slippageSpinner.setHorizontalAlignment(4);
/*  93 */     if (this.settingsSaver.restoreApplySlippageToAllMarketOrders()) {
/*  94 */       this.slippageSpinner.setValue(this.settingsSaver.restoreDefaultSlippage());
/*     */     }
/*  96 */     GridBagLayoutHelper.add(1, gridY++, 1.0D, 0.0D, 2, 1, 0, 1, 0, 0, 2, 21, gbc, this, this.slippageSpinner);
/*     */ 
/*  98 */     GridBagLayoutHelper.add(0, gridY, 0.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this, this.limitCheckBox);
/*     */ 
/* 100 */     this.limitStopDirectionButton = new ConditionalOrderEntryPanel.DependandHeightButton("", this.limitPriceField);
/* 101 */     this.limitPopup = new OrderConditionPopupMenu(this.limitStopDirectionButton, OrderType.IF_DONE_LIMIT, this.holder);
/*     */ 
/* 105 */     this.limitStopDirectionButton.setEnabled(this.stopCheckBox.isSelected());
/* 106 */     GridBagLayoutHelper.add(1, gridY, 0.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this, this.limitStopDirectionButton);
/*     */ 
/* 108 */     this.limitPriceField.setEnabled(false);
/* 109 */     this.limitPriceField.setMargin(new Insets(0, 0, 0, 1));
/* 110 */     this.limitPriceField.setHorizontalAlignment(4);
/*     */ 
/* 112 */     ((PriceAmountTextFieldM)this.limitPriceField.getEditor()).setColumns(7);
/* 113 */     GridBagLayoutHelper.add(2, gridY++, 1.0D, 0.0D, 1, 1, 0, 3, 0, 0, 2, 21, gbc, this, this.limitPriceField);
/*     */ 
/* 115 */     setBorder();
/*     */ 
/* 117 */     this.slippageCheckBox.setEnabled(false);
/* 118 */     this.slippageCheckBox.setSelected(false);
/*     */   }
/*     */ 
/*     */   private void setBorder()
/*     */   {
/* 123 */     this.border = new JLocalizableRoundedBorder(this, "header.ifd.buy");
/* 124 */     this.border.setTopInset(12);
/* 125 */     this.border.setRightInset(12);
/* 126 */     this.border.setLeftInset(9);
/* 127 */     setBorder(this.border);
/*     */   }
/*     */ 
/*     */   private void initListaners() {
/* 131 */     this.stopCheckBox.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 134 */         IfDonePanel.this.stopPriceField.setEnabled(IfDonePanel.this.stopCheckBox.isSelected());
/* 135 */         IfDonePanel.this.stopStopDirectionButton.setEnabled(IfDonePanel.this.stopCheckBox.isSelected());
/* 136 */         BigDecimal price = OrderUtils.calculateConditionalPrice(IfDonePanel.this.holder.getInstrument(), OrderType.STOP_LOSS, IfDonePanel.this.holder.getOrderSide(), IfDonePanel.this.stopPopup.getSelectedStopDirection(), IfDonePanel.this.holder.getEntryPrice());
/*     */ 
/* 141 */         IfDonePanel.this.stopPriceField.setText(String.valueOf(price));
/*     */ 
/* 143 */         IfDonePanel.this.slippageCheckBox.setSelected(IfDonePanel.this.stopCheckBox.isSelected());
/* 144 */         IfDonePanel.this.slippageCheckBox.setEnabled(IfDonePanel.this.stopCheckBox.isSelected());
/* 145 */         IfDonePanel.this.slippageSpinner.setEnabled(IfDonePanel.this.stopCheckBox.isSelected());
/*     */ 
/* 147 */         IfDonePanel.this.setDefaultSlipage();
/*     */       }
/*     */     });
/* 150 */     this.limitCheckBox.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 153 */         IfDonePanel.this.limitPriceField.setEnabled(IfDonePanel.this.limitCheckBox.isSelected());
/* 154 */         IfDonePanel.this.limitStopDirectionButton.setEnabled(IfDonePanel.this.limitCheckBox.isSelected());
/* 155 */         BigDecimal price = OrderUtils.calculateConditionalPrice(IfDonePanel.this.holder.getInstrument(), OrderType.TAKE_PROFIT, IfDonePanel.this.holder.getOrderSide(), IfDonePanel.this.limitPopup.getSelectedStopDirection(), IfDonePanel.this.holder.getEntryPrice());
/*     */ 
/* 160 */         IfDonePanel.this.limitPriceField.setText(String.valueOf(price));
/*     */       }
/*     */     });
/* 164 */     this.slippageCheckBox.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 167 */         IfDonePanel.this.slippageSpinner.setEnabled(IfDonePanel.this.slippageCheckBox.isSelected());
/* 168 */         IfDonePanel.this.setDefaultSlipage();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void clearEverything() {
/* 174 */     this.stopCheckBox.setSelected(false);
/*     */ 
/* 176 */     this.stopPriceField.clear();
/*     */ 
/* 178 */     this.slippageCheckBox.setSelected(false);
/* 179 */     this.slippageSpinner.clear();
/*     */ 
/* 181 */     this.limitCheckBox.setSelected(false);
/* 182 */     this.limitPriceField.clear();
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 188 */     clearEverything();
/*     */ 
/* 190 */     this.stopCheckBox.setEnabled(enabled);
/* 191 */     this.slippageCheckBox.setEnabled(enabled);
/* 192 */     this.limitCheckBox.setEnabled(enabled);
/*     */ 
/* 194 */     if (!enabled) {
/* 195 */       this.limitPriceField.setEnabled(false);
/* 196 */       this.limitStopDirectionButton.setEnabled(false);
/* 197 */       this.stopStopDirectionButton.setEnabled(false);
/* 198 */       this.stopPriceField.setEnabled(false);
/* 199 */       this.slippageSpinner.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public BigDecimal getStopPrice() {
/* 204 */     return new BigDecimal(this.stopPriceField.getText());
/*     */   }
/*     */ 
/*     */   public BigDecimal getLimitPrice() {
/* 208 */     return new BigDecimal(this.limitPriceField.getText());
/*     */   }
/*     */ 
/*     */   public BigDecimal getStopSlippage() {
/* 212 */     if (this.slippageCheckBox.isSelected()) {
/* 213 */       return (BigDecimal)this.slippageSpinner.getValue();
/*     */     }
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   public StopDirection getStopDirection()
/*     */   {
/* 220 */     switch (4.$SwitchMap$com$dukascopy$dds2$greed$model$OrderCondition[this.stopPopup.getSelectedStopDirection().ordinal()]) { case 1:
/* 221 */       return StopDirection.ASK_GREATER;
/*     */     case 2:
/* 222 */       return StopDirection.BID_GREATER;
/*     */     case 3:
/* 223 */       return StopDirection.ASK_LESS;
/*     */     case 4:
/* 224 */       return StopDirection.BID_LESS; }
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   public StopDirection getLimitDirection()
/*     */   {
/* 230 */     switch (4.$SwitchMap$com$dukascopy$dds2$greed$model$OrderCondition[this.limitPopup.getSelectedStopDirection().ordinal()]) { case 5:
/* 231 */       return StopDirection.ASK_EQUALS;
/*     */     case 6:
/* 232 */       return StopDirection.BID_EQUALS; }
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   public void changeSideForPanel()
/*     */   {
/* 238 */     this.stopPopup.filterPopup();
/* 239 */     this.limitPopup.filterPopup();
/* 240 */     changeHeader();
/*     */   }
/*     */ 
/*     */   public boolean isStopSelected() {
/* 244 */     return this.stopCheckBox.isSelected();
/*     */   }
/*     */ 
/*     */   public boolean isLimitSelected() {
/* 248 */     return this.limitCheckBox.isSelected();
/*     */   }
/*     */ 
/*     */   private void changeHeader() {
/* 252 */     if (OrderSide.SELL == this.holder.getOrderSide())
/* 253 */       this.border.setTextKey("header.ifd.buy");
/*     */     else
/* 255 */       this.border.setTextKey("header.ifd.sell");
/*     */   }
/*     */ 
/*     */   private void setDefaultSlipage()
/*     */   {
/* 260 */     if (this.slippageCheckBox.isSelected())
/* 261 */       this.slippageSpinner.setValue(this.settingsSaver.restoreDefaultSlippage());
/*     */     else
/* 263 */       this.slippageSpinner.clear();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.ifdone.IfDonePanel
 * JD-Core Version:    0.6.0
 */