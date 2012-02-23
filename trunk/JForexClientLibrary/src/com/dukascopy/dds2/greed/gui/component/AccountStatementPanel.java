/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.CumulativePositionsInfo;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.MoneyCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.Currency;
/*     */ import java.util.Formatter;
/*     */ import java.util.Locale;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.JTextField;
/*     */ 
/*     */ public class AccountStatementPanel extends JPanel
/*     */   implements PlatformSpecific
/*     */ {
/*  47 */   private static final Color COLOR_OF_MONEY = new Color(0, 150, 0);
/*  48 */   private static final Color COLOR_OF_MONEY_YELLOW = Color.YELLOW;
/*  49 */   private static final Color COLOR_OF_MONEY_RED = Color.RED;
/*     */   private static final int FIELD_LENGTH = 10;
/*     */   private JTextField marginField;
/*     */   private JLabel equityFieldLabel;
/*     */   private JLocalizableCheckBox oneClickCheckbox;
/*     */   private JTextField creditLineTotalField;
/*     */   private JLabel creditLineFreeFieldLabel;
/*     */   private JLabel useOfLeverageFieldLabel;
/*     */   private Color defaultBackgroundColor;
/*     */   private JLabel margin;
/*     */   private JLabel freeMargin;
/*  68 */   private JLabel labProLoss = new JLabel();
/*     */ 
/*  70 */   private AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/*     */   private Currency prevCurrency;
/*     */   private String prevSymbol;
/*  74 */   private static DecimalFormat profitLossFormat = new DecimalFormat("#,##0.00");
/*     */ 
/* 218 */   private static final BigDecimal HUNDRED = new BigDecimal("100");
/* 219 */   private static final BigDecimal TWO = new BigDecimal("2").setScale(2, RoundingMode.HALF_EVEN);
/* 220 */   private static final DecimalFormat equityDf = new DecimalFormat("#,##0.00");
/* 221 */   private static final DecimalFormat percentageDf = new DecimalFormat("#0");
/*     */ 
/*     */   public AccountStatementPanel()
/*     */   {
/*  85 */     build();
/*     */   }
/*     */ 
/*     */   private void build() {
/*  89 */     JPanel container = new JPanel();
/*  90 */     container.setOpaque(false);
/*  91 */     setLayout(new BoxLayout(this, 2));
/*  92 */     container.setLayout(new BoxLayout(container, 2));
/*  93 */     container.add(Box.createHorizontalStrut(5));
/*     */ 
/*  95 */     JLocalizableLabel equityLabel = new JLocalizableLabel("label.equity");
/*  96 */     container.add(equityLabel);
/*     */ 
/*  98 */     container.add(Box.createHorizontalStrut(5));
/*     */ 
/* 100 */     StringBuilder sb = new StringBuilder(12);
/* 101 */     Formatter formatter = new Formatter(sb, Locale.US);
/* 102 */     formatter.format("%10s", new Object[] { "" });
/* 103 */     this.equityFieldLabel = new JLabel(sb.toString(), 4);
/*     */ 
/* 106 */     this.equityFieldLabel.setForeground(COLOR_OF_MONEY);
/* 107 */     container.add(this.equityFieldLabel);
/*     */ 
/* 109 */     addSeparator(container);
/*     */ 
/* 111 */     this.marginField = new JTextField(10);
/* 112 */     this.marginField.setEditable(false);
/* 113 */     this.marginField.setHorizontalAlignment(4);
/* 114 */     this.marginField.setForeground(COLOR_OF_MONEY);
/*     */ 
/* 116 */     this.creditLineTotalField = new JTextField(13);
/* 117 */     this.creditLineTotalField.setEditable(false);
/* 118 */     this.creditLineTotalField.setHorizontalAlignment(4);
/* 119 */     this.creditLineTotalField.setForeground(COLOR_OF_MONEY);
/*     */ 
/* 122 */     JLocalizableLabel freeTradingLineLabel = new JLocalizableLabel("label.free.trading.line");
/* 123 */     container.add(freeTradingLineLabel);
/* 124 */     container.add(Box.createHorizontalStrut(5));
/* 125 */     this.creditLineFreeFieldLabel = new JLabel(sb.toString(), 4);
/* 126 */     this.creditLineFreeFieldLabel.setForeground(COLOR_OF_MONEY);
/* 127 */     container.add(this.creditLineFreeFieldLabel);
/*     */ 
/* 129 */     addSeparator(container);
/*     */ 
/* 131 */     if ((!GreedContext.IS_FXDD_LABEL) && (!GreedContext.IS_ALPARI_LABEL)) {
/* 132 */       JLocalizableLabel profitLossLabel = new JLocalizableLabel("label.profit.loss");
/* 133 */       container.add(profitLossLabel);
/*     */ 
/* 135 */       container.add(Box.createHorizontalStrut(5));
/*     */ 
/* 137 */       container.add(this.labProLoss);
/*     */ 
/* 139 */       addSeparator(container);
/*     */     }
/*     */ 
/* 142 */     JLocalizableLabel useOfLeverageLabel = new JLocalizableLabel("label.use.of.leverage");
/* 143 */     container.add(useOfLeverageLabel);
/*     */ 
/* 145 */     container.add(Box.createHorizontalStrut(5));
/*     */ 
/* 147 */     this.useOfLeverageFieldLabel = new JLabel(sb.toString(), 4);
/* 148 */     this.useOfLeverageFieldLabel.setForeground(COLOR_OF_MONEY);
/* 149 */     container.add(this.useOfLeverageFieldLabel);
/*     */ 
/* 152 */     if ((GreedContext.IS_FXDD_LABEL) || (GreedContext.IS_ALPARI_LABEL)) {
/* 153 */       JLabel marginLabel = new JLabel("Margin:");
/* 154 */       JLabel freeMarginLabel = new JLabel("Free margin:");
/*     */ 
/* 156 */       this.margin = new JLabel("", 4);
/* 157 */       this.margin.setForeground(COLOR_OF_MONEY);
/* 158 */       this.margin.setBackground(this.defaultBackgroundColor);
/*     */ 
/* 160 */       this.freeMargin = new JLabel("", 4);
/* 161 */       this.freeMargin.setForeground(COLOR_OF_MONEY);
/* 162 */       this.freeMargin.setBackground(this.defaultBackgroundColor);
/*     */ 
/* 164 */       addSeparator(container);
/*     */ 
/* 166 */       container.add(marginLabel);
/* 167 */       container.add(Box.createHorizontalStrut(5));
/* 168 */       container.add(this.margin);
/*     */ 
/* 170 */       addSeparator(container);
/*     */ 
/* 172 */       container.add(freeMarginLabel);
/* 173 */       container.add(Box.createHorizontalStrut(5));
/* 174 */       container.add(this.freeMargin);
/*     */     }
/*     */ 
/* 178 */     addSeparator(container);
/*     */ 
/* 180 */     this.oneClickCheckbox = new JLocalizableCheckBox("check.one.click");
/* 181 */     if (MACOSX) {
/* 182 */       this.oneClickCheckbox.putClientProperty("JComponent.sizeVariant", "small");
/*     */ 
/* 184 */       this.useOfLeverageFieldLabel.putClientProperty("JComponent.sizeVariant", "small");
/*     */ 
/* 186 */       this.creditLineFreeFieldLabel.putClientProperty("JComponent.sizeVariant", "small");
/* 187 */       this.creditLineTotalField.putClientProperty("JComponent.sizeVariant", "small");
/* 188 */       this.marginField.putClientProperty("JComponent.sizeVariant", "small");
/*     */ 
/* 190 */       this.equityFieldLabel.putClientProperty("JComponent.sizeVariant", "small");
/* 191 */       useOfLeverageLabel.putClientProperty("JComponent.sizeVariant", "small");
/* 192 */       equityLabel.putClientProperty("JComponent.sizeVariant", "small");
/* 193 */       freeTradingLineLabel.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/* 195 */     this.oneClickCheckbox.setSelected(true);
/* 196 */     container.add(this.oneClickCheckbox);
/* 197 */     container.add(Box.createHorizontalStrut(15));
/*     */ 
/* 200 */     add(container);
/*     */ 
/* 202 */     this.oneClickCheckbox.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 204 */         if (GreedContext.get("clientGui") != null) {
/* 205 */           ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 206 */           storage.saveOneClickState(AccountStatementPanel.this.oneClickCheckbox.isSelected());
/*     */         }
/*     */       }
/*     */     });
/* 211 */     if (GreedContext.isContest()) {
/* 212 */       this.oneClickCheckbox.setSelected(true);
/* 213 */       this.oneClickCheckbox.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onAccountInfo(AccountInfoMessage accountInfo)
/*     */   {
/* 224 */     if (accountInfo != null) {
/* 225 */       Currency currency = accountInfo.getCurrency();
/*     */ 
/* 228 */       String symbol = currency.getSymbol();
/*     */ 
/* 231 */       if (symbol == null) return;
/*     */ 
/* 235 */       if (accountInfo.getUsableMargin() == null) return;
/*     */ 
/* 237 */       String formatted = symbol + " " + equityDf.format(accountInfo.getEquity().getValue());
/*     */ 
/* 242 */       this.equityFieldLabel.setText(formatted);
/*     */ 
/* 244 */       formatted = symbol + " " + equityDf.format(accountInfo.getUsableMargin().getValue());
/*     */ 
/* 248 */       this.marginField.setText(formatted);
/*     */ 
/* 250 */       formatted = symbol + " " + equityDf.format(accountInfo.getEquity().getValue().multiply(new BigDecimal(accountInfo.getLeverage().intValue())));
/*     */ 
/* 254 */       this.creditLineTotalField.setText(formatted);
/*     */ 
/* 256 */       formatted = symbol + " " + equityDf.format(accountInfo.getUsableMargin().getValue().multiply(new BigDecimal(accountInfo.getLeverage().intValue())));
/*     */ 
/* 261 */       this.creditLineFreeFieldLabel.setText(formatted);
/*     */ 
/* 263 */       if (accountInfo.getEquity().getValue().compareTo(BigDecimal.ZERO) <= 0) {
/* 264 */         formatted = " N/A";
/* 265 */         this.useOfLeverageFieldLabel.setForeground(COLOR_OF_MONEY);
/* 266 */         this.useOfLeverageFieldLabel.setBackground(this.defaultBackgroundColor);
/* 267 */       } else if (accountInfo.getEquity().getValue().subtract(accountInfo.getUsableMargin().getValue()).divide(TWO, 2, RoundingMode.FLOOR).compareTo(accountInfo.getEquity().getValue()) > 0)
/*     */       {
/* 270 */         formatted = " 200%";
/*     */ 
/* 273 */         this.useOfLeverageFieldLabel.setBackground(COLOR_OF_MONEY_RED);
/* 274 */         this.useOfLeverageFieldLabel.setForeground(Color.black);
/*     */       }
/*     */       else {
/* 277 */         BigDecimal useOfLeverage = null;
/*     */ 
/* 279 */         if (BigDecimal.ZERO.equals(accountInfo.getUsableMargin().getValue()))
/* 280 */           useOfLeverage = BigDecimal.ZERO;
/*     */         else {
/* 282 */           useOfLeverage = accountInfo.getEquity().getValue().subtract(accountInfo.getUsableMargin().getValue()).divide(accountInfo.getEquity().getValue(), 2, RoundingMode.HALF_EVEN).multiply(HUNDRED);
/*     */         }
/*     */ 
/* 286 */         formatted = " " + percentageDf.format(useOfLeverage) + "%";
/*     */ 
/* 292 */         if (useOfLeverage.compareTo(new BigDecimal(100)) < 0) {
/* 293 */           this.useOfLeverageFieldLabel.setForeground(COLOR_OF_MONEY);
/* 294 */           this.useOfLeverageFieldLabel.setBackground(this.defaultBackgroundColor);
/* 295 */         } else if (useOfLeverage.compareTo(new BigDecimal(150)) < 0) {
/* 296 */           this.useOfLeverageFieldLabel.setBackground(COLOR_OF_MONEY_YELLOW);
/* 297 */           this.useOfLeverageFieldLabel.setForeground(Color.black);
/*     */         }
/*     */         else
/*     */         {
/* 301 */           this.useOfLeverageFieldLabel.setBackground(COLOR_OF_MONEY_RED);
/* 302 */           this.useOfLeverageFieldLabel.setForeground(Color.black);
/*     */         }
/*     */ 
/* 305 */         if ((GreedContext.IS_FXDD_LABEL) || (GreedContext.IS_ALPARI_LABEL)) {
/* 306 */           BigDecimal freeMargin = accountInfo.getUsableMargin().getValue();
/* 307 */           BigDecimal margin = accountInfo.getEquity().getValue().subtract(accountInfo.getUsableMargin().getValue());
/*     */ 
/* 310 */           this.margin.setText(equityDf.format(margin));
/* 311 */           this.freeMargin.setText(equityDf.format(freeMargin));
/*     */         }
/*     */       }
/*     */ 
/* 315 */       this.useOfLeverageFieldLabel.setText(formatted);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setProfitLost(BigDecimal value)
/*     */   {
/* 325 */     if (value == null) {
/* 326 */       return;
/*     */     }
/* 328 */     Color color = value.compareTo(BigDecimal.ZERO) < 0 ? MoneyCellRenderer.COLOR_NEGATIVE : MoneyCellRenderer.COLOR_POSITIVE;
/* 329 */     if (!color.equals(this.labProLoss.getForeground()))
/*     */     {
/* 331 */       this.labProLoss.setForeground(color);
/*     */     }
/*     */ 
/* 335 */     if ((this.accountStatement.getLastAccountState() != null) && (this.accountStatement.getLastAccountState().getCurrency() != null))
/*     */     {
/* 337 */       Currency currency = this.accountStatement.getLastAccountState().getCurrency();
/*     */ 
/* 339 */       if ((this.prevCurrency == null) || (currency != this.prevCurrency)) {
/* 340 */         this.prevCurrency = currency;
/* 341 */         this.prevSymbol = currency.getSymbol();
/*     */       }
/* 343 */       String formatted = new StringBuilder().append(this.prevSymbol).append(" ").append(profitLossFormat.format(value)).toString();
/* 344 */       if (!formatted.equals(this.labProLoss.getText())) {
/* 345 */         this.labProLoss.setText(formatted);
/*     */       }
/*     */     }
/*     */ 
/* 349 */     if ((GreedContext.IS_FXDD_LABEL) || (GreedContext.IS_ALPARI_LABEL))
/* 350 */       ((ClientForm)GreedContext.get("clientGui")).getPositionsPanel().setProfitLost(value);
/*     */   }
/*     */ 
/*     */   private void addSeparator(JPanel panel)
/*     */   {
/* 355 */     JSeparator separator = new JSeparator(1);
/* 356 */     separator.setMaximumSize(new Dimension(5, 15));
/* 357 */     panel.add(Box.createHorizontalStrut(7));
/* 358 */     panel.add(separator);
/* 359 */     panel.add(Box.createHorizontalStrut(7));
/*     */   }
/*     */ 
/*     */   public void update(CumulativePositionsInfo bag)
/*     */   {
/* 364 */     BigDecimal profitLoss = bag.getProfitLoss();
/*     */ 
/* 367 */     setProfitLost(profitLoss);
/*     */   }
/*     */ 
/*     */   public void updateLabel(LabelType selector, Object value) {
/* 371 */     if (LabelType.PL == selector)
/* 372 */       setProfitLost((BigDecimal)value);
/*     */   }
/*     */ 
/*     */   public JTextField getMarginField()
/*     */   {
/* 377 */     return this.marginField;
/*     */   }
/*     */ 
/*     */   public JCheckBox getOneClickCheckbox() {
/* 381 */     return this.oneClickCheckbox;
/*     */   }
/*     */ 
/*     */   public static enum LabelType
/*     */   {
/*  77 */     NUMBER, LONG, SHORT, AMOUNT, PL, COMISSION, TIME;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.AccountStatementPanel
 * JD-Core Version:    0.6.0
 */