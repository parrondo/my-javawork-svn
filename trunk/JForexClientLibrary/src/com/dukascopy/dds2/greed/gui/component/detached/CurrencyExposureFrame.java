/*     */ package com.dukascopy.dds2.greed.gui.component.detached;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.MoneyCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.Container;
/*     */ import java.awt.Font;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Currency;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.border.Border;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CurrencyExposureFrame extends JFrame
/*     */ {
/*  50 */   private static Logger LOGGER = LoggerFactory.getLogger(CurrencyExposureFrame.class);
/*     */ 
/*  52 */   private Map<String, BigDecimal> exposures = new HashMap();
/*  53 */   private final DecimalFormat df = new DecimalFormat("+#,##0.00;-#,##0.00");
/*  54 */   private static final Border emptyBorder = BorderFactory.createEmptyBorder(4, 8, 4, 8);
/*  55 */   private static final Font VERDANA = new Font(null, 1, 22);
/*     */ 
/*  57 */   private final AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/*     */ 
/*  59 */   private final List<Money> currenciesSumExposureList = new ArrayList();
/*     */ 
/*  61 */   private Currency accountCurrency = this.accountStatement.getLastAccountState().getCurrency();
/*  62 */   private CurrencyConverter currencyConverter = (CurrencyConverter)GreedContext.get("currencyConverter");
/*     */ 
/*     */   public void display()
/*     */   {
/*  66 */     Toolkit.getDefaultToolkit().setDynamicLayout(true);
/*     */     try
/*     */     {
/*  69 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */     } catch (Exception e) {
/*  71 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/*  74 */     setDefaultCloseOperation(2);
/*  75 */     setTitle(GuiUtilsAndConstants.LABEL_SHORT_NAME);
/*     */ 
/*  78 */     ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/*     */ 
/*  80 */     List positionList = new ArrayList(((PositionsTableModel)clientGui.getPositionsPanel().getTable().getModel()).getPositions());
/*     */ 
/*  86 */     boolean showSummaryValue = true;
/*     */ 
/*  88 */     for (Position position : positionList) {
/*  89 */       PositionSide side = position.getPositionSide();
/*  90 */       BigDecimal price = position.getPriceOpen().getValue();
/*  91 */       String currPrim = position.getCurrencyPrimary();
/*  92 */       String currSec = position.getCurrencySecondary();
/*  93 */       BigDecimal amountPrim = getPrimaryAmount(position.getAmount().getValue(), side);
/*  94 */       BigDecimal amountSec = getSecodaryAmount(amountPrim.multiply(price), side);
/*     */ 
/*  97 */       BigDecimal exposure = (BigDecimal)this.exposures.get(currPrim);
/*  98 */       if (null == exposure) exposure = BigDecimal.ZERO;
/*  99 */       exposure = exposure.add(amountPrim);
/* 100 */       this.exposures.put(currPrim, exposure);
/*     */ 
/* 103 */       addExposure(currPrim, getPrimaryAmount(amountPrim, side));
/*     */ 
/* 106 */       exposure = (BigDecimal)this.exposures.get(currSec);
/* 107 */       if (null == exposure) exposure = BigDecimal.ZERO;
/* 108 */       exposure = exposure.add(amountSec);
/* 109 */       this.exposures.put(currSec, exposure);
/*     */ 
/* 111 */       addExposure(currSec, getSecodaryAmount(amountPrim.multiply(price), side));
/*     */     }
/*     */ 
/* 116 */     JPanel content = new JPanel();
/* 117 */     content.setLayout(new BoxLayout(content, 1));
/* 118 */     Font font = new JLabel().getFont();
/* 119 */     font = new Font(null, 1, font.getSize());
/* 120 */     JPanel header = new JPanel();
/* 121 */     header.setLayout(new BoxLayout(header, 0));
/* 122 */     header.setBorder(emptyBorder);
/* 123 */     JLabel labelHeader = new JLocalizableLabel("label.currency.exposure");
/* 124 */     labelHeader.setHorizontalAlignment(0);
/* 125 */     labelHeader.setFont(font);
/* 126 */     header.add(Box.createHorizontalGlue());
/* 127 */     header.add(labelHeader);
/* 128 */     header.add(Box.createHorizontalGlue());
/* 129 */     content.add(header);
/* 130 */     Set instruments = this.exposures.keySet();
/* 131 */     if (this.exposures.size() > 0)
/*     */     {
/* 133 */       for (String instrument : instruments) {
/* 134 */         JPanel slice = new JPanel();
/* 135 */         slice.setBorder(emptyBorder);
/* 136 */         slice.setLayout(new BoxLayout(slice, 0));
/* 137 */         slice.setBackground(SystemColor.control);
/*     */ 
/* 140 */         BigDecimal amount = (BigDecimal)this.exposures.get(instrument);
/* 141 */         JLabel labAmount = new JLabel(this.df.format(amount) + " " + instrument);
/* 142 */         labAmount.setForeground(BigDecimal.ZERO.compareTo(amount) < 0 ? MoneyCellRenderer.COLOR_POSITIVE : MoneyCellRenderer.COLOR_NEGATIVE);
/*     */ 
/* 145 */         labAmount.setFont(VERDANA);
/* 146 */         slice.add(Box.createHorizontalGlue());
/* 147 */         slice.add(labAmount);
/* 148 */         content.add(slice);
/*     */       }
/*     */ 
/* 151 */       if (showSummaryValue) {
/* 152 */         content.add(new JSeparator(0));
/*     */ 
/* 154 */         JPanel slice = new JPanel();
/* 155 */         slice.setBorder(emptyBorder);
/* 156 */         slice.setLayout(new BoxLayout(slice, 0));
/* 157 */         slice.setBackground(SystemColor.control);
/* 158 */         Money sumExposure = calculateSummaryExposure();
/*     */ 
/* 162 */         JLabel labAmount = new JLabel(this.df.format(sumExposure.getValue()) + " " + sumExposure.getCurrency());
/* 163 */         labAmount.setForeground(BigDecimal.ZERO.compareTo(sumExposure.getValue()) < 0 ? MoneyCellRenderer.COLOR_POSITIVE : MoneyCellRenderer.COLOR_NEGATIVE);
/*     */ 
/* 166 */         labAmount.setFont(VERDANA);
/* 167 */         slice.add(Box.createHorizontalGlue());
/* 168 */         slice.add(labAmount);
/* 169 */         content.add(slice);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 174 */       JPanel slice = new JPanel();
/* 175 */       slice.setBorder(emptyBorder);
/* 176 */       slice.setLayout(new BoxLayout(slice, 0));
/* 177 */       slice.setBackground(SystemColor.control);
/* 178 */       JLabel label = new JLabel("No Exposure");
/* 179 */       label.setFont(VERDANA);
/* 180 */       label.setForeground(MoneyCellRenderer.COLOR_NEGATIVE);
/* 181 */       slice.add(Box.createHorizontalGlue());
/* 182 */       slice.add(label);
/* 183 */       slice.add(Box.createHorizontalGlue());
/* 184 */       content.add(slice);
/*     */     }
/* 186 */     getContentPane().add(content);
/* 187 */     pack();
/* 188 */     setResizable(false);
/* 189 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 190 */     setVisible(true);
/*     */ 
/* 192 */     content.setToolTipText("Click to close");
/* 193 */     content.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent e) {
/* 195 */         CurrencyExposureFrame.this.dispose();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private OfferSide getOfferSide(BigDecimal amount, Currency equity, Currency another) {
/* 201 */     if (amount.compareTo(BigDecimal.ZERO) > 0) {
/* 202 */       if (isAccountCurrencyFirst(equity, another)) {
/* 203 */         return OfferSide.BID;
/*     */       }
/* 205 */       return OfferSide.ASK;
/*     */     }
/*     */ 
/* 208 */     if (isAccountCurrencyFirst(equity, another)) {
/* 209 */       return OfferSide.ASK;
/*     */     }
/* 211 */     return OfferSide.BID;
/*     */   }
/*     */ 
/*     */   private BigDecimal getPrimaryAmount(BigDecimal amount, PositionSide side)
/*     */   {
/* 217 */     if (PositionSide.LONG.equals(side))
/* 218 */       return amount.abs();
/* 219 */     if (PositionSide.SHORT.equals(side)) {
/* 220 */       return amount.abs().negate();
/*     */     }
/*     */ 
/* 223 */     return null;
/*     */   }
/*     */ 
/*     */   private BigDecimal getSecodaryAmount(BigDecimal amount, PositionSide side) {
/* 227 */     if (PositionSide.LONG.equals(side))
/* 228 */       return amount.abs().negate();
/* 229 */     if (PositionSide.SHORT.equals(side)) {
/* 230 */       return amount.abs();
/*     */     }
/*     */ 
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean isAccountCurrencyFirst(Currency equity, Currency another) {
/* 237 */     for (int i = 0; i < Instrument.values().length; i++) {
/* 238 */       if ((Instrument.contains(equity.getCurrencyCode())) && (Instrument.contains(another.getCurrencyCode())))
/*     */       {
/* 240 */         return Instrument.values()[i].getPrimaryCurrency().equals(equity);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   private Money getMoneyFromList(String currency) {
/* 251 */     for (Money money : this.currenciesSumExposureList) {
/* 252 */       if (money.getCurrency().equals(Currency.getInstance(currency))) {
/* 253 */         this.currenciesSumExposureList.remove(money);
/* 254 */         return money;
/*     */       }
/*     */     }
/*     */ 
/* 258 */     return null;
/*     */   }
/*     */ 
/*     */   private void addExposure(String currency, BigDecimal exposure) {
/* 262 */     Money money = getMoneyFromList(currency);
/*     */ 
/* 264 */     if (money != null) {
/* 265 */       money = money.add(exposure);
/* 266 */       this.currenciesSumExposureList.add(money);
/*     */     } else {
/* 268 */       this.currenciesSumExposureList.add(new Money(exposure, Currency.getInstance(currency)));
/*     */     }
/*     */   }
/*     */ 
/*     */   private List<Money> convertToAccountCurrency() {
/* 273 */     List result = new ArrayList();
/*     */ 
/* 275 */     for (Money money : this.currenciesSumExposureList) {
/* 276 */       OfferSide os = getOfferSide(money.getValue(), this.accountCurrency, money.getCurrency());
/* 277 */       BigDecimal value = this.currencyConverter.convert(money.getValue(), money.getCurrency(), this.accountCurrency, os);
/* 278 */       if (value != null) {
/* 279 */         Money accountCurrencyMoney = new Money(value, this.accountCurrency);
/* 280 */         result.add(accountCurrencyMoney);
/*     */       }
/*     */     }
/*     */ 
/* 284 */     return result;
/*     */   }
/*     */ 
/*     */   private Money calculateSummaryExposure() {
/* 288 */     Money negative = new Money(BigDecimal.ZERO, this.accountCurrency);
/* 289 */     Money positive = new Money(BigDecimal.ZERO, this.accountCurrency);
/*     */ 
/* 291 */     for (Money money : convertToAccountCurrency()) {
/* 292 */       if ((money != null) && (money.getValue() != null)) {
/* 293 */         if (BigDecimal.ZERO.compareTo(money.getValue()) < 0)
/* 294 */           positive = positive.add(money.getValue());
/*     */         else {
/* 296 */           negative = negative.add(money.getValue());
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 301 */     return positive.getValue().compareTo(new BigDecimal(Math.abs(negative.getValue().doubleValue()))) >= 0 ? positive : negative;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.detached.CurrencyExposureFrame
 * JD-Core Version:    0.6.0
 */