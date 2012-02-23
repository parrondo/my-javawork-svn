/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*     */ 
/*     */ import com.dukascopy.api.CalendarFilter;
/*     */ import com.dukascopy.dds2.greed.gui.SpringUtil;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.Action;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.CountriesComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.KeywordsTextField;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.SpringLayout;
/*     */ 
/*     */ class FilterPanel extends JPanel
/*     */ {
/*     */   private final TimeFrameComboBox timeFrameComboBox;
/*     */   private final FromDateChooser fromDateChooser;
/*     */   private final CalendarTypeComboBox calendarTypeComboBox;
/*     */   private final CountriesComboBox countriesComboBox;
/*     */   private final KeywordsTextField<CalendarFilter> keywordsTextField;
/*     */ 
/*     */   public FilterPanel(CalendarFilter calendarFilter, ActionListener filterActionListener, ActionListener advancedActionListener, ActionListener quickFilterListener)
/*     */   {
/*  44 */     setLayout(new SpringLayout());
/*  45 */     setPreferredSize(new Dimension(1024, 35));
/*     */ 
/*  47 */     int x = 5;
/*  48 */     int y = 5;
/*  49 */     int w = 0;
/*  50 */     int h = 25;
/*     */ 
/*  52 */     this.timeFrameComboBox = new TimeFrameComboBox(calendarFilter);
/*  53 */     this.fromDateChooser = new FromDateChooser(calendarFilter);
/*  54 */     this.calendarTypeComboBox = new CalendarTypeComboBox(calendarFilter);
/*  55 */     this.countriesComboBox = new CountriesComboBox(calendarFilter);
/*  56 */     this.keywordsTextField = new KeywordsTextField(calendarFilter);
/*     */ 
/*  58 */     SpringUtil.add(this, this.timeFrameComboBox, x, y, w = 125, h);
/*  59 */     SpringUtil.add(this, this.fromDateChooser, x += w + 5, y, w = 120, h);
/*  60 */     SpringUtil.add(this, this.calendarTypeComboBox, x += w + 5, y, w = 125, h);
/*  61 */     SpringUtil.add(this, this.countriesComboBox, x += w + 5, y, w = 125, h);
/*     */ 
/*  63 */     SpringUtil.add(this, new JLabel(LocalizationManager.getText("label.keywords")), x += w + 15, y, w = 55, h);
/*  64 */     SpringUtil.add(this, this.keywordsTextField, x += w, y, w = 'È', h);
/*     */ 
/*  66 */     SpringUtil.add(this, new JLocalizableButton("button.apply", filterActionListener)
/*     */     {
/*     */     }
/*     */     , x += w + 5, y, w = 100, h);
/*     */ 
/*  69 */     SpringUtil.add(this, new JLocalizableButton("button.advanced", advancedActionListener)
/*     */     {
/*     */     }
/*     */     , x += w + 5, y, w = 100, h);
/*     */ 
/*  74 */     SpringUtil.add(this, new JLabel(LocalizationManager.getText("title.quick.search")), x += w + 15, y, w = 65, h);
/*  75 */     JTextField txtQuickSearch = new JTextField();
/*  76 */     txtQuickSearch.addKeyListener(new KeyAdapter(quickFilterListener, txtQuickSearch)
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {
/*  79 */         this.val$quickFilterListener.actionPerformed(new ActionEvent(this.val$txtQuickSearch.getText(), 1, ChartToolBar.Action.PERFORM_QUICK_FILTER.name()));
/*     */       }
/*     */     });
/*  82 */     SpringUtil.add(this, txtQuickSearch, x += w, y, w = 100, h);
/*  83 */     SpringUtil.add(this, new JButton(new ResizableIcon("toolbar_table_refresh.png"), txtQuickSearch, quickFilterListener)
/*     */     {
/*     */     }
/*     */     , x += w + 5, y, w = 30, h + 3);
/*     */   }
/*     */ 
/*     */   public void refresh(CalendarFilter calendarFilter)
/*     */   {
/*  96 */     this.timeFrameComboBox.refresh(calendarFilter);
/*  97 */     this.fromDateChooser.refresh(calendarFilter);
/*  98 */     this.calendarTypeComboBox.refresh(calendarFilter);
/*  99 */     this.countriesComboBox.refresh(calendarFilter);
/* 100 */     this.keywordsTextField.refresh(calendarFilter);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.FilterPanel
 * JD-Core Version:    0.6.0
 */