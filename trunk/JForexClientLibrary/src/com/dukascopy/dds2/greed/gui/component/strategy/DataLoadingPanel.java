/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.CardLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.text.MessageFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.AbstractListModel;
/*     */ import javax.swing.ComboBoxModel;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class DataLoadingPanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private JComboBox instrumentComboBox;
/*     */   private InstrumentComboBoxModel instrumentComboBoxModel;
/*     */   private JButton instrumentsButton;
/*     */   private JPanel rangePanel;
/*     */   private SimpleLocalizableComboBox<Range> rangeComboBox;
/*     */   private JLocalizableLabel lblTickLoadingMethod;
/*     */   private SimpleLocalizableComboBox<ITesterClient.DataLoadingMethod> dataLoadingMethodComboBox;
/*     */   private JPanel interpolationPanel;
/*     */   private JLocalizableButton getDataButton;
/*     */   private JLocalizableButton saveDataButton;
/*     */   private JLocalizableButton cancelButton;
/*     */   private CardLayoutPanel getDataCancelButtonsPanel;
/*     */   private JButton rangeButton;
/*     */   private JLocalizableLabel selectedInstrumentsLabel;
/*  86 */   private boolean ignoreInstrumentSelectActions = true;
/*     */   private long dateFrom;
/*     */   private Long dateTo;
/*  90 */   private Set<Instrument> selectedInstruments = new LinkedHashSet();
/*     */   private Instrument accountCurrencyConversionInstrument;
/*     */ 
/*     */   public DataLoadingPanel()
/*     */   {
/*  94 */     this.dateFrom = getTodayStart();
/*     */   }
/*     */ 
/*     */   public void build()
/*     */   {
/*  99 */     JRoundedBorder border = new JLocalizableRoundedBorder(this, "border.data.loading");
/* 100 */     setBorder(border);
/*     */ 
/* 102 */     List availableInstrumentSet = new ArrayList();
/* 103 */     IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/* 104 */     for (Instrument instrument : Instrument.values()) {
/* 105 */       if (feedDataProvider.getTimeOfFirstCandle(instrument, Period.TICK) != 9223372036854775807L) {
/* 106 */         availableInstrumentSet.add(instrument);
/*     */       }
/*     */     }
/* 109 */     Collections.sort(availableInstrumentSet, new Comparator()
/*     */     {
/*     */       public int compare(Instrument o1, Instrument o2) {
/* 112 */         return o1.toString().compareTo(o2.toString());
/*     */       }
/*     */     });
/* 115 */     Instrument[] enabledInstruments = (Instrument[])availableInstrumentSet.toArray(new Instrument[availableInstrumentSet.size()]);
/*     */ 
/* 117 */     this.instrumentComboBoxModel = new InstrumentComboBoxModel(enabledInstruments);
/* 118 */     this.instrumentComboBox = new JComboBox(this.instrumentComboBoxModel);
/* 119 */     this.instrumentComboBox.setSelectedItem(Instrument.EURUSD);
/* 120 */     this.selectedInstruments.add(Instrument.EURUSD);
/* 121 */     Instrument[] instruments = new Instrument[enabledInstruments.length + 1];
/* 122 */     System.arraycopy(enabledInstruments, 0, instruments, 1, enabledInstruments.length);
/* 123 */     this.instrumentsButton = new JButton("+");
/* 124 */     this.selectedInstrumentsLabel = new JLocalizableLabel();
/* 125 */     this.selectedInstrumentsLabel.setVisible(false);
/* 126 */     this.lblTickLoadingMethod = new JLocalizableLabel("label.ticks.generation.method", "tooltip.ticks.generation.method");
/* 127 */     this.dataLoadingMethodComboBox = initDataLoadingComboBox();
/* 128 */     this.rangeComboBox = new SimpleLocalizableComboBox(Range.values());
/* 129 */     this.rangeComboBox.setRenderer(new SimpleLocalizableCellRenderer() {
/* 130 */       private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
/*     */ 
/*     */       protected String getLocalizedText(Object value)
/*     */       {
/* 134 */         if (value != DataLoadingPanel.Range.CUSTOM_PERIOD_TEMPLATE) {
/* 135 */           return LocalizationManager.getText(value.toString());
/*     */         }
/*     */ 
/* 138 */         String stringFrom = this.format.format(Long.valueOf(DataLoadingPanel.this.dateFrom));
/*     */         String stringTo;
/*     */         String stringTo;
/* 140 */         if (DataLoadingPanel.this.dateTo == null)
/* 141 */           stringTo = this.format.format(Long.valueOf(System.currentTimeMillis()));
/*     */         else {
/* 143 */           stringTo = this.format.format(DataLoadingPanel.this.dateTo);
/*     */         }
/* 145 */         return MessageFormat.format(LocalizationManager.getText(value.toString()), new Object[] { stringFrom, stringTo });
/*     */       }
/*     */     });
/* 149 */     this.rangeComboBox.setSelectedItem(Range.LAST_DAY);
/*     */ 
/* 152 */     ImageIcon icon = StratUtils.loadImageIcon("com/toedter/calendar/demo/images/JDateChooserColor16.gif");
/* 153 */     this.rangeButton = new JButton(icon);
/* 154 */     this.rangeButton.setMargin(new Insets(0, 0, 0, 0));
/* 155 */     this.rangeButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 158 */         DataLoadingPanel.this.rangeButtonPressed(e);
/*     */       }
/*     */     });
/* 161 */     this.rangePanel = new JPanel(new BorderLayout(5, 0));
/* 162 */     this.rangePanel.add(this.rangeComboBox, "Center");
/* 163 */     this.rangePanel.add(this.rangeButton, "East");
/* 164 */     this.getDataButton = new JLocalizableButton("button.get.data");
/* 165 */     this.saveDataButton = new JLocalizableButton("button.save.data");
/* 166 */     this.cancelButton = new JLocalizableButton("button.cancel");
/*     */ 
/* 169 */     setLayout(new GridBagLayout());
/* 170 */     GridBagConstraints gbc = new GridBagConstraints();
/* 171 */     gbc.fill = 2;
/* 172 */     gbc.anchor = 10;
/*     */ 
/* 175 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, this, this.instrumentComboBox);
/* 176 */     GridBagLayoutHelper.add(2, 0, 1.0D, 0.0D, 3, 1, 5, 0, 0, 0, gbc, this, this.rangePanel);
/* 177 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 0, 5, 0, 0, gbc, this, this.lblTickLoadingMethod);
/*     */ 
/* 179 */     this.interpolationPanel = new JPanel(new CardLayout());
/* 180 */     this.interpolationPanel.add(this.dataLoadingMethodComboBox, "label.interpolation");
/*     */ 
/* 182 */     JPanel interpolationContainer = new JPanel(new BorderLayout(5, 0));
/* 183 */     interpolationContainer.add(this.interpolationPanel, "Center");
/* 184 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 4, 1, 5, 5, 0, 0, gbc, this, interpolationContainer);
/*     */ 
/* 186 */     gbc.fill = 0;
/* 187 */     gbc.anchor = 17;
/* 188 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, this, this.selectedInstrumentsLabel);
/* 189 */     gbc.anchor = 10;
/* 190 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.instrumentsButton);
/*     */ 
/* 192 */     gbc.fill = 0;
/* 193 */     gbc.anchor = 16;
/* 194 */     this.getDataCancelButtonsPanel = new CardLayoutPanel();
/* 195 */     this.getDataCancelButtonsPanel.add(this.getDataButton, "button.get.data");
/* 196 */     this.getDataCancelButtonsPanel.add(this.cancelButton, "button.cancel");
/* 197 */     setCancelEnabled(false);
/* 198 */     JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 5, 0));
/* 199 */     buttonsPanel.add(this.getDataCancelButtonsPanel);
/* 200 */     buttonsPanel.add(this.saveDataButton);
/* 201 */     GridBagLayoutHelper.add(0, 2, 0.0D, 1.0D, 5, 1, 0, 5, 0, 0, gbc, this, buttonsPanel);
/*     */ 
/* 203 */     this.instrumentsButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 205 */         DataLoadingPanel.access$402(DataLoadingPanel.this, DataLoadingPanel.this.getInstruments());
/* 206 */         InstrumentSelectionDialog dialog = new InstrumentSelectionDialog(DataLoadingPanel.this.selectedInstruments, DataLoadingPanel.this.accountCurrencyConversionInstrument);
/* 207 */         if (!dialog.isCanceled()) {
/* 208 */           DataLoadingPanel.access$402(DataLoadingPanel.this, dialog.getSelectedInstruments());
/* 209 */           DataLoadingPanel.this.updateComboBoxes();
/*     */         }
/*     */       }
/*     */     });
/* 214 */     ActionListener instrumentComBoxesListener = new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 216 */         if (!DataLoadingPanel.this.ignoreInstrumentSelectActions) {
/* 217 */           DataLoadingPanel.access$402(DataLoadingPanel.this, DataLoadingPanel.this.getInstruments());
/* 218 */           Set toAdd = new HashSet();
/* 219 */           for (Instrument instrument : DataLoadingPanel.this.selectedInstruments) {
/* 220 */             Set conversionDeps = AbstractCurrencyConverter.getConversionDeps(instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency());
/* 221 */             for (Instrument instrumentDep : conversionDeps) {
/* 222 */               toAdd.add(instrumentDep);
/*     */             }
/*     */           }
/* 225 */           toAdd.removeAll(DataLoadingPanel.this.selectedInstruments);
/* 226 */           if (!toAdd.isEmpty()) {
/* 227 */             DataLoadingPanel.this.selectedInstruments.addAll(toAdd);
/* 228 */             DataLoadingPanel.this.updateComboBoxes();
/*     */           }
/*     */         }
/*     */       }
/*     */     };
/* 233 */     this.instrumentComboBox.addActionListener(instrumentComBoxesListener);
/*     */   }
/*     */ 
/*     */   private void rangeButtonPressed(ActionEvent event)
/*     */   {
/* 238 */     long from = getFromDate();
/* 239 */     long to = getToDate();
/* 240 */     RangeSelectionDialog dialog = RangeSelectionDialog.createDialog(this, "dialog.select.range", Long.valueOf(from), Long.valueOf(to));
/* 241 */     if (dialog.showModal()) {
/* 242 */       this.dateFrom = getTimeInMillis(dialog.getDateFrom());
/* 243 */       this.dateTo = Long.valueOf(getTimeInMillis(dialog.getDateTo()));
/* 244 */       this.rangeComboBox.setSelectedItem(Range.CUSTOM_PERIOD_TEMPLATE);
/* 245 */       this.rangeComboBox.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void set(StrategyTestBean strategyTestBean)
/*     */   {
/* 251 */     this.selectedInstruments = strategyTestBean.getInstruments();
/* 252 */     updateComboBoxes();
/*     */ 
/* 254 */     this.dataLoadingMethodComboBox.setSelectedItem(strategyTestBean.getDataLoadingMethod());
/* 255 */     if (strategyTestBean.isRangeSelected())
/* 256 */       this.rangeComboBox.setSelectedItem(Range.valueOf(strategyTestBean.getRange()));
/*     */     else {
/* 258 */       this.rangeComboBox.setSelectedItem(Range.CUSTOM_PERIOD_TEMPLATE);
/*     */     }
/* 260 */     long aFromDate = strategyTestBean.getFromDate();
/* 261 */     if ((aFromDate == -9223372036854775808L) || (aFromDate == 0L))
/* 262 */       this.dateFrom = getTodayStart();
/*     */     else {
/* 264 */       this.dateFrom = aFromDate;
/*     */     }
/* 266 */     long aToDate = strategyTestBean.getToDate();
/* 267 */     if ((aToDate == -9223372036854775808L) || (aToDate == 0L))
/* 268 */       this.dateTo = null;
/*     */     else
/* 270 */       this.dateTo = Long.valueOf(aToDate);
/*     */   }
/*     */ 
/*     */   public void save(StrategyTestBean strategyTestBean)
/*     */   {
/* 276 */     strategyTestBean.setDataLoadingMethod(getDataLoadingMethod());
/* 277 */     strategyTestBean.setRange(((Range)this.rangeComboBox.getSelectedItem()).name());
/* 278 */     strategyTestBean.setRangeSelected(this.rangeComboBox.getSelectedItem() != Range.CUSTOM_PERIOD_TEMPLATE);
/* 279 */     strategyTestBean.setFromDate(this.dateFrom);
/* 280 */     strategyTestBean.setToDate(this.dateTo == null ? -9223372036854775808L : this.dateTo.longValue());
/*     */   }
/*     */ 
/*     */   protected void updateComboBoxes()
/*     */   {
/*     */     try {
/* 286 */       if (this.accountCurrencyConversionInstrument != null) {
/* 287 */         this.selectedInstruments.remove(this.accountCurrencyConversionInstrument);
/*     */       }
/* 289 */       Set selectedInstruments = new HashSet(this.selectedInstruments);
/* 290 */       if (((this.accountCurrencyConversionInstrument == null) && (selectedInstruments.size() < 2)) || ((this.accountCurrencyConversionInstrument != null) && (selectedInstruments.size() < 2)))
/*     */       {
/* 292 */         if (!this.instrumentComboBox.isVisible()) {
/* 293 */           this.instrumentComboBox.setVisible(true);
/* 294 */           this.selectedInstrumentsLabel.setVisible(false);
/*     */         }
/* 296 */         this.instrumentComboBox.setEnabled(true);
/* 297 */         if (selectedInstruments.size() == 0) {
/* 298 */           if (this.accountCurrencyConversionInstrument != null) {
/* 299 */             this.instrumentComboBox.setSelectedItem(this.accountCurrencyConversionInstrument);
/* 300 */             this.instrumentComboBox.setEnabled(false);
/*     */           } else {
/* 302 */             this.instrumentComboBox.setSelectedItem(Instrument.EURUSD);
/*     */           }
/*     */ 
/*     */         }
/* 306 */         else if (this.accountCurrencyConversionInstrument != null) {
/* 307 */           this.instrumentComboBox.setSelectedItem(this.accountCurrencyConversionInstrument);
/* 308 */           this.instrumentComboBox.setEnabled(false);
/*     */         } else {
/* 310 */           this.instrumentComboBox.setSelectedItem(selectedInstruments.iterator().next());
/*     */         }
/*     */       }
/*     */       else {
/* 314 */         if (this.instrumentComboBox.isVisible()) {
/* 315 */           this.instrumentComboBox.setVisible(false);
/* 316 */           this.selectedInstrumentsLabel.setVisible(true);
/*     */         }
/*     */ 
/* 319 */         this.selectedInstrumentsLabel.setTextParams(new Object[] { Integer.valueOf(this.accountCurrencyConversionInstrument == null ? selectedInstruments.size() : selectedInstruments.size() + 1) });
/*     */ 
/* 321 */         this.selectedInstrumentsLabel.setText("label.instr.selected.template");
/*     */ 
/* 324 */         StringBuffer toolTipText = new StringBuffer();
/* 325 */         if (this.accountCurrencyConversionInstrument != null) {
/* 326 */           toolTipText.append(this.accountCurrencyConversionInstrument.toString());
/*     */         }
/* 328 */         for (Instrument instrument : selectedInstruments) {
/* 329 */           if (toolTipText.length() > 0) {
/* 330 */             toolTipText.append(", ");
/*     */           }
/* 332 */           toolTipText.append(instrument.toString());
/*     */         }
/*     */ 
/* 335 */         if (toolTipText.length() > 0)
/* 336 */           this.selectedInstrumentsLabel.setToolTip(toolTipText.toString());
/*     */         else
/* 338 */           this.selectedInstrumentsLabel.setToolTip(null);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private SimpleLocalizableComboBox<ITesterClient.DataLoadingMethod> initDataLoadingComboBox()
/*     */   {
/* 350 */     ITesterClient.DataLoadingMethod[] methods = { ITesterClient.DataLoadingMethod.ALL_TICKS, ITesterClient.DataLoadingMethod.DIFFERENT_PRICE_TICKS, ITesterClient.DataLoadingMethod.PIVOT_TICKS };
/*     */ 
/* 356 */     SimpleLocalizableComboBox comboBox = new SimpleLocalizableComboBox(methods);
/* 357 */     comboBox.setSelectedIndex(2);
/* 358 */     comboBox.setRenderer(new SimpleLocalizableCellRenderer(null));
/* 359 */     return comboBox;
/*     */   }
/*     */ 
/*     */   public void addGetDataListener(ActionListener actionListener)
/*     */   {
/* 364 */     this.getDataButton.addActionListener(actionListener);
/*     */   }
/*     */ 
/*     */   public void addSaveDataListener(ActionListener actionListener) {
/* 368 */     this.saveDataButton.addActionListener(actionListener);
/*     */   }
/*     */ 
/*     */   public void addCancelButtonListener(ActionListener actionListener) {
/* 372 */     this.cancelButton.addActionListener(actionListener);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getInstruments() {
/* 376 */     if (this.instrumentComboBox.isVisible()) {
/* 377 */       this.selectedInstruments.clear();
/* 378 */       this.selectedInstruments.add((Instrument)this.instrumentComboBox.getSelectedItem());
/*     */     }
/* 380 */     if (this.accountCurrencyConversionInstrument != null) {
/* 381 */       this.selectedInstruments.add(this.accountCurrencyConversionInstrument);
/*     */     }
/* 383 */     return this.selectedInstruments;
/*     */   }
/*     */ 
/*     */   public ITesterClient.DataLoadingMethod getDataLoadingMethod() {
/* 387 */     return (ITesterClient.DataLoadingMethod)this.dataLoadingMethodComboBox.getSelectedItem();
/*     */   }
/*     */ 
/*     */   public long getFromDate() {
/* 391 */     Range range = (Range)this.rangeComboBox.getSelectedItem();
/* 392 */     if (range == Range.CUSTOM_PERIOD_TEMPLATE) {
/* 393 */       return this.dateFrom;
/*     */     }
/*     */ 
/* 396 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 397 */     cal.set(14, 0);
/* 398 */     cal.set(13, 0);
/* 399 */     cal.set(12, 0);
/* 400 */     cal.set(11, 0);
/* 401 */     switch (6.$SwitchMap$com$dukascopy$dds2$greed$gui$component$strategy$DataLoadingPanel$Range[range.ordinal()]) {
/*     */     case 1:
/* 403 */       break;
/*     */     case 2:
/* 405 */       cal.add(5, -1);
/* 406 */       break;
/*     */     case 3:
/* 408 */       cal.set(7, 2);
/* 409 */       cal.add(3, -1);
/* 410 */       break;
/*     */     case 4:
/* 412 */       cal.set(5, 1);
/* 413 */       cal.add(2, -1);
/* 414 */       break;
/*     */     case 5:
/* 416 */       cal.set(5, 1);
/* 417 */       cal.add(2, -3);
/* 418 */       break;
/*     */     case 6:
/* 420 */       cal.set(5, 1);
/* 421 */       cal.add(2, -6);
/* 422 */       break;
/*     */     case 7:
/* 424 */       cal.set(5, 1);
/* 425 */       cal.add(2, -12);
/*     */     }
/*     */ 
/* 428 */     return cal.getTimeInMillis();
/*     */   }
/*     */ 
/*     */   public long getToDate()
/*     */   {
/* 433 */     Range range = (Range)this.rangeComboBox.getSelectedItem();
/* 434 */     if (range != Range.CUSTOM_PERIOD_TEMPLATE) {
/* 435 */       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 436 */       cal.set(14, 0);
/* 437 */       cal.set(13, 0);
/* 438 */       cal.set(12, 0);
/* 439 */       cal.set(11, 0);
/* 440 */       switch (6.$SwitchMap$com$dukascopy$dds2$greed$gui$component$strategy$DataLoadingPanel$Range[range.ordinal()]) {
/*     */       case 1:
/* 442 */         IFeedDataProvider feedDataProvider = (IFeedDataProvider)GreedContext.get("feedDataProvider");
/* 443 */         return feedDataProvider.getCurrentTime();
/*     */       case 2:
/* 445 */         break;
/*     */       case 3:
/* 447 */         cal.set(7, 2);
/* 448 */         break;
/*     */       case 4:
/* 450 */         cal.set(5, 1);
/* 451 */         break;
/*     */       case 5:
/* 453 */         cal.set(5, 1);
/* 454 */         break;
/*     */       case 6:
/* 456 */         cal.set(5, 1);
/* 457 */         break;
/*     */       case 7:
/* 459 */         cal.set(5, 1);
/*     */       }
/*     */ 
/* 462 */       return cal.getTimeInMillis();
/*     */     }
/* 464 */     if (this.dateTo == null) {
/* 465 */       return new Date().getTime();
/*     */     }
/* 467 */     return this.dateTo.longValue();
/*     */   }
/*     */ 
/*     */   private long getTodayStart()
/*     */   {
/* 473 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 474 */     Calendar localCal = Calendar.getInstance();
/*     */ 
/* 477 */     localCal.setTimeInMillis(System.currentTimeMillis());
/* 478 */     int year = localCal.get(1);
/* 479 */     int month = localCal.get(2);
/* 480 */     int day = localCal.get(5);
/*     */ 
/* 482 */     cal.set(14, 0);
/* 483 */     cal.set(13, 0);
/* 484 */     cal.set(12, 0);
/* 485 */     cal.set(11, 0);
/* 486 */     cal.set(5, day);
/* 487 */     cal.set(2, month);
/* 488 */     cal.set(1, year);
/* 489 */     return cal.getTimeInMillis();
/*     */   }
/*     */ 
/*     */   private long getTimeInMillis(Date date) {
/* 493 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 494 */     cal.setTime(date);
/* 495 */     return cal.getTimeInMillis();
/*     */   }
/*     */ 
/*     */   public void enableControls(boolean b)
/*     */   {
/* 500 */     this.instrumentComboBox.setEnabled((b) && (this.accountCurrencyConversionInstrument == null));
/* 501 */     this.instrumentsButton.setEnabled(b);
/* 502 */     this.saveDataButton.setEnabled(b);
/* 503 */     this.getDataButton.setEnabled(b);
/* 504 */     this.cancelButton.setEnabled(false);
/* 505 */     this.getDataCancelButtonsPanel.showComponent("button.get.data");
/* 506 */     this.rangeButton.setEnabled(b);
/* 507 */     this.rangeComboBox.setEnabled(b);
/* 508 */     this.dataLoadingMethodComboBox.setEnabled(b);
/*     */   }
/*     */ 
/*     */   public void setCancelEnabled(boolean enabled) {
/* 512 */     this.cancelButton.setEnabled(enabled);
/* 513 */     if (enabled)
/* 514 */       this.getDataCancelButtonsPanel.showComponent("button.cancel");
/*     */     else
/* 516 */       this.getDataCancelButtonsPanel.showComponent("button.get.data");
/*     */   }
/*     */ 
/*     */   public void setAccountCurrency(Currency accountCurrency)
/*     */   {
/*     */   }
/*     */ 
/*     */   private class SimpleLocalizableCellRenderer extends DefaultListCellRenderer
/*     */   {
/*     */     private SimpleLocalizableCellRenderer()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */     {
/*     */       String text;
/*     */       String text;
/* 698 */       if (value != null)
/* 699 */         text = getLocalizedText(value);
/*     */       else {
/* 701 */         text = "";
/*     */       }
/* 703 */       return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
/*     */     }
/*     */ 
/*     */     protected String getLocalizedText(Object value) {
/* 707 */       return LocalizationManager.getText(value.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SimpleComboBoxModel<D> extends AbstractListModel
/*     */     implements ComboBoxModel
/*     */   {
/*     */     private D selectedItem;
/*     */     private List<D> elements;
/*     */ 
/*     */     public SimpleComboBoxModel(D[] items)
/*     */     {
/* 613 */       this.elements = new ArrayList(items.length);
/* 614 */       for (Object d : items)
/* 615 */         this.elements.add(d);
/*     */     }
/*     */ 
/*     */     public void setItems(List<D> items)
/*     */     {
/* 620 */       int oldSelectedIndex = getSelectedIndex();
/*     */ 
/* 623 */       this.elements.clear();
/* 624 */       this.elements.addAll(items);
/* 625 */       fireContentsChanged(this, 0, this.elements.size() - 1);
/*     */ 
/* 628 */       if (oldSelectedIndex >= 0)
/* 629 */         if (getSize() < 1) {
/* 630 */           this.selectedItem = null;
/* 631 */           fireContentsChanged(this, -1, -1);
/*     */         }
/*     */         else
/*     */         {
/* 635 */           int newSelectedIndex = getSelectedIndex();
/* 636 */           if (oldSelectedIndex != newSelectedIndex) {
/* 637 */             if (newSelectedIndex < 0)
/*     */             {
/* 639 */               if (oldSelectedIndex < getSize())
/* 640 */                 this.selectedItem = this.elements.get(oldSelectedIndex);
/*     */               else {
/* 642 */                 this.selectedItem = this.elements.get(this.elements.size() - 1);
/*     */               }
/*     */             }
/* 645 */             fireContentsChanged(this, -1, -1);
/*     */           }
/*     */         }
/*     */     }
/*     */ 
/*     */     private int getSelectedIndex()
/*     */     {
/* 652 */       if (this.selectedItem == null) {
/* 653 */         return -1;
/*     */       }
/* 655 */       for (int i = 0; i < this.elements.size(); i++) {
/* 656 */         Object element = this.elements.get(i);
/* 657 */         if (this.selectedItem.equals(element)) {
/* 658 */           return i;
/*     */         }
/*     */       }
/* 661 */       return -1;
/*     */     }
/*     */ 
/*     */     public D getElementAt(int index)
/*     */     {
/* 667 */       return this.elements.get(index);
/*     */     }
/*     */ 
/*     */     public int getSize()
/*     */     {
/* 672 */       return this.elements.size();
/*     */     }
/*     */ 
/*     */     public D getSelectedItem()
/*     */     {
/* 677 */       return this.selectedItem;
/*     */     }
/*     */ 
/*     */     public void setSelectedItem(Object anItem)
/*     */     {
/* 683 */       if (((this.selectedItem != null) && (!this.selectedItem.equals(anItem))) || ((this.selectedItem == null) && (anItem != null)))
/*     */       {
/* 686 */         this.selectedItem = anItem;
/* 687 */         fireContentsChanged(this, -1, -1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SimpleLocalizableComboBox<D> extends JComboBox
/*     */     implements Localizable
/*     */   {
/*     */     private D[] items;
/*     */ 
/*     */     public SimpleLocalizableComboBox(D[] items)
/*     */     {
/* 576 */       this.items = items;
/* 577 */       setModel(new DataLoadingPanel.SimpleComboBoxModel(items));
/* 578 */       LocalizationManager.addLocalizable(this);
/*     */     }
/*     */ 
/*     */     public void setItems(List<D> items)
/*     */     {
/* 583 */       ((DataLoadingPanel.SimpleComboBoxModel)super.getModel()).setItems(items);
/*     */     }
/*     */ 
/*     */     public D getSelectedItem()
/*     */     {
/* 589 */       return super.getSelectedItem();
/*     */     }
/*     */ 
/*     */     public D getItemAt(int index)
/*     */     {
/* 595 */       return super.getItemAt(index);
/*     */     }
/*     */ 
/*     */     public void localize()
/*     */     {
/* 600 */       Object selected = getSelectedItem();
/* 601 */       setModel(new DataLoadingPanel.SimpleComboBoxModel(this.items));
/* 602 */       setSelectedItem(selected);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class InstrumentComboBoxModel extends AbstractListModel
/*     */     implements ComboBoxModel
/*     */   {
/*     */     private Instrument selectedInstrument;
/*     */     private Instrument[] instruments;
/*     */ 
/*     */     public InstrumentComboBoxModel(Instrument[] instruments)
/*     */     {
/* 538 */       this.instruments = instruments;
/*     */     }
/*     */ 
/*     */     public Object getSelectedItem()
/*     */     {
/* 552 */       return this.selectedInstrument;
/*     */     }
/*     */ 
/*     */     public void setSelectedItem(Object anItem) {
/* 556 */       this.selectedInstrument = ((Instrument)anItem);
/* 557 */       fireContentsChanged(this, -1, -1);
/*     */     }
/*     */ 
/*     */     public Object getElementAt(int index) {
/* 561 */       return this.instruments[index];
/*     */     }
/*     */ 
/*     */     public int getSize() {
/* 565 */       return this.instruments.length;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum Range
/*     */   {
/*  68 */     INTRADAY, LAST_DAY, LAST_WEEK, LAST_MONTH, LAST_3_MONTHS, LAST_6_MONTHS, LAST_YEAR, CUSTOM_PERIOD_TEMPLATE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.DataLoadingPanel
 * JD-Core Version:    0.6.0
 */