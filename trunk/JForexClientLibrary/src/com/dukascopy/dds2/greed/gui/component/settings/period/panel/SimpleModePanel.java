/*     */ package com.dukascopy.dds2.greed.gui.component.settings.period.panel;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.Unit;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.FormBuilder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class SimpleModePanel extends AbstractModePanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private JComboBox cmbSecs;
/*     */   private JComboBox cmbMins;
/*     */   private JComboBox cmbHours;
/*     */   private JComboBox cmbDays;
/*     */   private JComboBox cmbWeeks;
/*     */   private JComboBox cmbMonths;
/*     */   private JComboBox cmbPriceRanges;
/*     */   private JComboBox cmbRenkos;
/*     */   private JLocalizableRadioButton radioTicks;
/*     */   private JLocalizableRadioButton radioTickBars;
/*     */   private JLocalizableRadioButton radioSecs;
/*     */   private JLocalizableRadioButton radioMins;
/*     */   private JLocalizableRadioButton radioHours;
/*     */   private JLocalizableRadioButton radioDays;
/*     */   private JLocalizableRadioButton radioWeeks;
/*     */   private JLocalizableRadioButton radioMonths;
/*     */   private JLocalizableRadioButton radioRangeBars;
/*     */   private JLocalizableRadioButton radioRenkos;
/*     */   private JLocalizableRadioButton radioPointAndFigures;
/*     */   private List<JComboBox> allCombos;
/*     */   private JPanel combosPanel;
/*     */   private JComboBox cmbPnFBoxSize;
/*     */   private JComboBox cmbPnFReversalAmount;
/*     */   private JComboBox cmbTickBarSize;
/*     */   private JLocalizableLabel lblPnFBoxSize;
/*     */   private JLocalizableLabel lblPnFReversalAmount;
/*     */ 
/*     */   public SimpleModePanel(String title, List<JForexPeriod> defaultPeriods)
/*     */   {
/*  66 */     super(title, defaultPeriods);
/*     */   }
/*     */ 
/*     */   protected JPanel getLeftPanel()
/*     */   {
/*  71 */     setupRadioGrgoup();
/*  72 */     return getCombosPanel();
/*     */   }
/*     */ 
/*     */   private void setupRadioGrgoup()
/*     */   {
/*  79 */     ButtonGroup bg = new ButtonGroup();
/*  80 */     bg.add(getRadioTicks());
/*  81 */     bg.add(getRadioTickBars());
/*  82 */     bg.add(getRadioSecs());
/*  83 */     bg.add(getRadioMins());
/*  84 */     bg.add(getRadioHours());
/*  85 */     bg.add(getRadioDays());
/*  86 */     bg.add(getRadioWeeks());
/*  87 */     bg.add(getRadioMonths());
/*  88 */     bg.add(getRadioRangeBars());
/*  89 */     bg.add(getRadioRenkos());
/*  90 */     bg.add(getRadioPointAndFigures());
/*     */   }
/*     */ 
/*     */   private JComboBox createComboBox() {
/*  94 */     JComboBox cmb = new JComboBox();
/*     */ 
/*  96 */     getAllCombos().add(cmb);
/*     */ 
/*  98 */     cmb.setEnabled(false);
/*     */ 
/* 100 */     cmb.setPreferredSize(new Dimension(79, 22));
/*     */ 
/* 102 */     cmb.setRenderer(new DefaultListCellRenderer() {
/*     */       private static final long serialVersionUID = 1L;
/*     */ 
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 107 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 109 */         JLabel label = new JLabel();
/* 110 */         String text = "";
/*     */ 
/* 112 */         if ((value instanceof Period)) {
/* 113 */           Period period = (Period)value;
/* 114 */           text = ChartsLocalizator.getLocalized(period);
/*     */         }
/* 116 */         else if ((value instanceof ReversalAmount)) {
/* 117 */           text = String.valueOf(((ReversalAmount)value).getAmount());
/*     */         }
/* 119 */         else if ((value instanceof TickBarSize)) {
/* 120 */           text = ChartsLocalizator.getLocalized((TickBarSize)value);
/*     */         }
/* 122 */         else if ((value instanceof PriceRange)) {
/* 123 */           text = ChartsLocalizator.getLocalized((PriceRange)value);
/*     */         }
/* 125 */         else if (value != null) {
/* 126 */           throw new IllegalArgumentException("Unknow value type - " + value);
/*     */         }
/*     */ 
/* 129 */         label.setText(text);
/*     */ 
/* 131 */         label.setOpaque(true);
/* 132 */         label.setForeground(comp.getForeground());
/* 133 */         label.setBackground(comp.getBackground());
/* 134 */         return label;
/*     */       }
/*     */     });
/* 138 */     cmb.addItemListener(new ItemListener(cmb)
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/* 142 */         if (SimpleModePanel.this.getRadioPointAndFigures().isSelected()) {
/* 143 */           SimpleModePanel.this.getBtnAdd().setEnabled((SimpleModePanel.this.getCmbPnFBoxSize().getSelectedItem() != null) && (SimpleModePanel.this.getCmbPnFReversalAmount().getSelectedItem() != null));
/*     */         }
/*     */         else
/*     */         {
/* 149 */           SimpleModePanel.this.getBtnAdd().setEnabled(this.val$cmb.getSelectedItem() != null);
/*     */         }
/*     */       }
/*     */     });
/* 154 */     return cmb;
/*     */   }
/*     */ 
/*     */   private JLocalizableRadioButton createRadioButton(String key, JComponent[] enableDisableComponent)
/*     */   {
/* 159 */     JLocalizableRadioButton radio = createRadioButton(key);
/* 160 */     radio.addActionListener(new ActionListener(enableDisableComponent, radio)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 163 */         SimpleModePanel.this.setEnabledCombos(false);
/*     */ 
/* 165 */         if (this.val$enableDisableComponent != null) {
/* 166 */           for (JComponent component : this.val$enableDisableComponent) {
/* 167 */             component.setEnabled(this.val$radio.isSelected());
/*     */           }
/*     */         }
/*     */ 
/* 171 */         SimpleModePanel.this.getBtnAdd().setEnabled(SimpleModePanel.this.getRadioTicks().isSelected());
/*     */       }
/*     */     });
/* 174 */     return radio;
/*     */   }
/*     */ 
/*     */   private JLocalizableRadioButton createRadioButton(String key) {
/* 178 */     JLocalizableRadioButton radio = new JLocalizableRadioButton(key);
/* 179 */     return radio;
/*     */   }
/*     */ 
/*     */   private void fillWithPeriods(JComboBox cmb, Unit unit, int startUnitCount, int maxUnitCount) {
/* 183 */     List periods = Period.generateCompliantPeriods(unit, startUnitCount, maxUnitCount);
/* 184 */     fillWithPeriods(cmb, periods);
/*     */   }
/*     */ 
/*     */   private void fillWithPeriods(JComboBox cmb, List<Period> periods) {
/* 188 */     cmb.removeAllItems();
/* 189 */     cmb.addItem(null);
/*     */ 
/* 191 */     for (Period p : periods)
/* 192 */       cmb.addItem(p);
/*     */   }
/*     */ 
/*     */   private void fillWithPeriods(JComboBox cmb, Unit unit, int maxUnitCount)
/*     */   {
/* 198 */     fillWithPeriods(cmb, unit, 1, maxUnitCount);
/*     */   }
/*     */ 
/*     */   private void fillWithPriceRanges(JComboBox cmb) {
/* 202 */     cmb.removeAllItems();
/* 203 */     cmb.addItem(null);
/*     */ 
/* 205 */     List priceRanges = PriceRange.createJForexPriceRanges();
/*     */ 
/* 207 */     for (PriceRange pr : priceRanges)
/* 208 */       cmb.addItem(pr);
/*     */   }
/*     */ 
/*     */   private void fillWithReversalAmounts(JComboBox cmb)
/*     */   {
/* 214 */     cmb.removeAllItems();
/* 215 */     cmb.addItem(null);
/*     */ 
/* 217 */     List reversalAmounts = ReversalAmount.createJForexPriceRanges();
/*     */ 
/* 219 */     for (ReversalAmount ra : reversalAmounts)
/* 220 */       cmb.addItem(ra);
/*     */   }
/*     */ 
/*     */   private void setEnabledCombos(boolean flag)
/*     */   {
/* 226 */     for (JComboBox cmb : getAllCombos()) {
/* 227 */       cmb.setEnabled(flag);
/* 228 */       cmb.setSelectedItem(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbSecs()
/*     */   {
/* 235 */     if (this.cmbSecs == null) {
/* 236 */       this.cmbSecs = createComboBox();
/* 237 */       fillWithPeriods(this.cmbSecs, Unit.Second, 1, 59);
/*     */     }
/* 239 */     return this.cmbSecs;
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbMins() {
/* 243 */     if (this.cmbMins == null) {
/* 244 */       this.cmbMins = createComboBox();
/* 245 */       fillWithPeriods(this.cmbMins, Unit.Minute, 59);
/*     */     }
/* 247 */     return this.cmbMins;
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbHours() {
/* 251 */     if (this.cmbHours == null) {
/* 252 */       this.cmbHours = createComboBox();
/* 253 */       fillWithPeriods(this.cmbHours, Unit.Hour, 12);
/*     */     }
/* 255 */     return this.cmbHours;
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbDays() {
/* 259 */     if (this.cmbDays == null) {
/* 260 */       this.cmbDays = createComboBox();
/* 261 */       fillWithPeriods(this.cmbDays, Unit.Day, 6);
/*     */     }
/* 263 */     return this.cmbDays;
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbWeeks() {
/* 267 */     if (this.cmbWeeks == null) {
/* 268 */       this.cmbWeeks = createComboBox();
/* 269 */       fillWithPeriods(this.cmbWeeks, Unit.Week, 4);
/*     */     }
/* 271 */     return this.cmbWeeks;
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbMonths() {
/* 275 */     if (this.cmbMonths == null) {
/* 276 */       this.cmbMonths = createComboBox();
/* 277 */       fillWithPeriods(this.cmbMonths, Unit.Month, 11);
/*     */     }
/* 279 */     return this.cmbMonths;
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbPriceRanges() {
/* 283 */     if (this.cmbPriceRanges == null) {
/* 284 */       this.cmbPriceRanges = createComboBox();
/* 285 */       fillWithPriceRanges(this.cmbPriceRanges);
/*     */     }
/* 287 */     return this.cmbPriceRanges;
/*     */   }
/*     */ 
/*     */   protected JComboBox getCmbRenkos() {
/* 291 */     if (this.cmbRenkos == null) {
/* 292 */       this.cmbRenkos = createComboBox();
/* 293 */       fillWithPriceRanges(this.cmbRenkos);
/*     */     }
/* 295 */     return this.cmbRenkos;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioSecs() {
/* 299 */     if (this.radioSecs == null) {
/* 300 */       this.radioSecs = createRadioButton("label.caption.seconds", new JComponent[] { getCmbSecs() });
/*     */     }
/* 302 */     return this.radioSecs;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioMins() {
/* 306 */     if (this.radioMins == null) {
/* 307 */       this.radioMins = createRadioButton("label.caption.minutes", new JComponent[] { getCmbMins() });
/*     */     }
/* 309 */     return this.radioMins;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioHours() {
/* 313 */     if (this.radioHours == null) {
/* 314 */       this.radioHours = createRadioButton("label.caption.hours", new JComponent[] { getCmbHours() });
/*     */     }
/* 316 */     return this.radioHours;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioDays() {
/* 320 */     if (this.radioDays == null) {
/* 321 */       this.radioDays = createRadioButton("label.caption.days", new JComponent[] { getCmbDays() });
/*     */     }
/* 323 */     return this.radioDays;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioWeeks() {
/* 327 */     if (this.radioWeeks == null) {
/* 328 */       this.radioWeeks = createRadioButton("label.caption.weeks", new JComponent[] { getCmbWeeks() });
/*     */     }
/* 330 */     return this.radioWeeks;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioMonths() {
/* 334 */     if (this.radioMonths == null) {
/* 335 */       this.radioMonths = createRadioButton("label.caption.months", new JComponent[] { getCmbMonths() });
/*     */     }
/* 337 */     return this.radioMonths;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioTicks() {
/* 341 */     if (this.radioTicks == null) {
/* 342 */       this.radioTicks = createRadioButton("TICK", (JComponent[])null);
/*     */     }
/* 344 */     return this.radioTicks;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioTickBars() {
/* 348 */     if (this.radioTickBars == null) {
/* 349 */       this.radioTickBars = createRadioButton("label.caption.trade.bars", new JComponent[] { getCmbTickBarSize() });
/*     */     }
/* 351 */     return this.radioTickBars;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioRangeBars() {
/* 355 */     if (this.radioRangeBars == null) {
/* 356 */       this.radioRangeBars = createRadioButton("PRICE_RANGE_AGGREGATION", new JComponent[] { getCmbPriceRanges() });
/*     */     }
/* 358 */     return this.radioRangeBars;
/*     */   }
/*     */ 
/*     */   protected JLocalizableRadioButton getRadioRenkos() {
/* 362 */     if (this.radioRenkos == null) {
/* 363 */       this.radioRenkos = createRadioButton("RENKO", new JComponent[] { getCmbRenkos() });
/*     */     }
/* 365 */     return this.radioRenkos;
/*     */   }
/*     */ 
/*     */   protected JPanel getCombosPanel() {
/* 369 */     if (this.combosPanel == null) {
/* 370 */       this.combosPanel = new JPanel();
/*     */ 
/* 372 */       this.combosPanel.setLayout(new GridBagLayout());
/* 373 */       FormBuilder formBuilder = new FormBuilder(this.combosPanel, new Insets(2, 2, 2, 2));
/*     */ 
/* 375 */       formBuilder.addFirstField(getRadioTicks());
/* 376 */       formBuilder.addMiddleField(Box.createGlue());
/* 377 */       formBuilder.startNewRow();
/*     */ 
/* 379 */       formBuilder.addFirstField(getRadioTickBars());
/* 380 */       formBuilder.addMiddleField(getCmbTickBarSize());
/* 381 */       formBuilder.startNewRow();
/*     */ 
/* 383 */       formBuilder.addFirstField(getRadioSecs());
/* 384 */       formBuilder.addMiddleField(getCmbSecs());
/* 385 */       formBuilder.startNewRow();
/*     */ 
/* 387 */       formBuilder.addFirstField(getRadioMins());
/* 388 */       formBuilder.addMiddleField(getCmbMins());
/* 389 */       formBuilder.startNewRow();
/*     */ 
/* 391 */       formBuilder.addFirstField(getRadioHours());
/* 392 */       formBuilder.addMiddleField(getCmbHours());
/* 393 */       formBuilder.startNewRow();
/*     */ 
/* 395 */       formBuilder.addFirstField(getRadioDays());
/* 396 */       formBuilder.addMiddleField(getCmbDays());
/* 397 */       formBuilder.startNewRow();
/*     */ 
/* 399 */       formBuilder.addFirstField(getRadioWeeks());
/* 400 */       formBuilder.addMiddleField(getCmbWeeks());
/* 401 */       formBuilder.startNewRow();
/*     */ 
/* 403 */       formBuilder.addFirstField(getRadioMonths());
/* 404 */       formBuilder.addMiddleField(getCmbMonths());
/* 405 */       formBuilder.startNewRow();
/*     */ 
/* 407 */       formBuilder.addFirstField(getRadioRangeBars());
/* 408 */       formBuilder.addMiddleField(getCmbPriceRanges());
/* 409 */       formBuilder.startNewRow();
/*     */ 
/* 411 */       formBuilder.addFirstField(getRadioRenkos());
/* 412 */       formBuilder.addMiddleField(getCmbRenkos());
/* 413 */       formBuilder.startNewRow();
/*     */ 
/* 415 */       formBuilder.addFirstField(getRadioPointAndFigures());
/* 416 */       formBuilder.startNewRow();
/*     */ 
/* 418 */       formBuilder.addMiddleField(getLblPnFBoxSize());
/* 419 */       formBuilder.addLastField(getCmbPnFBoxSize());
/* 420 */       formBuilder.startNewRow();
/*     */ 
/* 422 */       formBuilder.addMiddleField(getLblPnFReversalAmount());
/* 423 */       formBuilder.addLastField(getCmbPnFReversalAmount());
/*     */     }
/*     */ 
/* 426 */     return this.combosPanel;
/*     */   }
/*     */ 
/*     */   protected List<JComboBox> getAllCombos() {
/* 430 */     if (this.allCombos == null) {
/* 431 */       this.allCombos = new ArrayList();
/*     */     }
/* 433 */     return this.allCombos;
/*     */   }
/*     */ 
/*     */   public JForexPeriod getSelectedPeriod()
/*     */   {
/* 438 */     if (getRadioTicks().isSelected()) {
/* 439 */       return new JForexPeriod(DataType.TICKS, Period.TICK);
/*     */     }
/* 441 */     if (getRadioRangeBars().isSelected()) {
/* 442 */       return new JForexPeriod(DataType.PRICE_RANGE_AGGREGATION, Period.TICK, (PriceRange)getCmbPriceRanges().getSelectedItem());
/*     */     }
/* 444 */     if (getRadioRenkos().isSelected()) {
/* 445 */       return new JForexPeriod(DataType.RENKO, Period.TICK, (PriceRange)getCmbRenkos().getSelectedItem());
/*     */     }
/* 447 */     if (getRadioPointAndFigures().isSelected()) {
/* 448 */       return new JForexPeriod(DataType.POINT_AND_FIGURE, Period.TICK, (PriceRange)getCmbPnFBoxSize().getSelectedItem(), (ReversalAmount)getCmbPnFReversalAmount().getSelectedItem());
/*     */     }
/*     */ 
/* 455 */     if (getRadioTickBars().isSelected()) {
/* 456 */       return new JForexPeriod(DataType.TICK_BAR, Period.TICK, (TickBarSize)getCmbTickBarSize().getSelectedItem());
/*     */     }
/*     */ 
/* 463 */     for (JComboBox cmb : getAllCombos()) {
/* 464 */       if (cmb.isEnabled()) {
/* 465 */         return new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, (Period)cmb.getSelectedItem());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 470 */     return null;
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbPnFBoxSize() {
/* 474 */     if (this.cmbPnFBoxSize == null) {
/* 475 */       this.cmbPnFBoxSize = createComboBox();
/* 476 */       fillWithPriceRanges(this.cmbPnFBoxSize);
/*     */     }
/* 478 */     return this.cmbPnFBoxSize;
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbPnFReversalAmount() {
/* 482 */     if (this.cmbPnFReversalAmount == null) {
/* 483 */       this.cmbPnFReversalAmount = createComboBox();
/* 484 */       fillWithReversalAmounts(this.cmbPnFReversalAmount);
/*     */     }
/* 486 */     return this.cmbPnFReversalAmount;
/*     */   }
/*     */ 
/*     */   private JComboBox getCmbTickBarSize() {
/* 490 */     if (this.cmbTickBarSize == null) {
/* 491 */       this.cmbTickBarSize = createComboBox();
/*     */ 
/* 493 */       this.cmbTickBarSize.addItem(null);
/* 494 */       for (TickBarSize size : TickBarSize.JFOREX_TRADE_BAR_SIZES) {
/* 495 */         this.cmbTickBarSize.addItem(size);
/*     */       }
/*     */     }
/* 498 */     return this.cmbTickBarSize;
/*     */   }
/*     */ 
/*     */   private JLocalizableRadioButton getRadioPointAndFigures() {
/* 502 */     if (this.radioPointAndFigures == null) {
/* 503 */       this.radioPointAndFigures = createRadioButton("label.caption.point.and.figure", new JComponent[] { getCmbPnFBoxSize(), getCmbPnFReversalAmount() });
/*     */     }
/*     */ 
/* 509 */     return this.radioPointAndFigures;
/*     */   }
/*     */ 
/*     */   private JLocalizableLabel getLblPnFBoxSize() {
/* 513 */     if (this.lblPnFBoxSize == null) {
/* 514 */       this.lblPnFBoxSize = new JLocalizableLabel("label.caption.box.size.in.pips");
/* 515 */       this.lblPnFBoxSize.setHorizontalAlignment(4);
/*     */     }
/* 517 */     return this.lblPnFBoxSize;
/*     */   }
/*     */ 
/*     */   private JLocalizableLabel getLblPnFReversalAmount() {
/* 521 */     if (this.lblPnFReversalAmount == null) {
/* 522 */       this.lblPnFReversalAmount = new JLocalizableLabel("label.caption.reversal.amount");
/* 523 */       this.lblPnFReversalAmount.setHorizontalAlignment(4);
/*     */     }
/* 525 */     return this.lblPnFReversalAmount;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.period.panel.SimpleModePanel
 * JD-Core Version:    0.6.0
 */