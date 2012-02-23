/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*     */ 
/*     */ import com.dukascopy.api.NewsFilter;
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
/*     */   implements INewsFilterChangeListener
/*     */ {
/*     */   private final TimeFrameComboBox timeFrameComboBox;
/*     */   private final FromDateChooser fromDateChooser;
/*     */   private final CountriesComboBox countriesComboBox;
/*     */   private final KeywordsTextField<NewsFilter> keywordsTextField;
/*     */ 
/*     */   public FilterPanel(NewsFilter newsFilter, ActionListener filterActionListener, ActionListener advancedActionListener, ActionListener quickFilterListener)
/*     */   {
/*  43 */     setLayout(new SpringLayout());
/*  44 */     setPreferredSize(new Dimension(1024, 35));
/*     */ 
/*  46 */     int x = 5;
/*  47 */     int y = 5;
/*  48 */     int w = 0;
/*  49 */     int h = 25;
/*     */ 
/*  51 */     this.timeFrameComboBox = new TimeFrameComboBox(newsFilter, this);
/*  52 */     this.fromDateChooser = new FromDateChooser(newsFilter);
/*  53 */     this.countriesComboBox = new CountriesComboBox(newsFilter);
/*  54 */     this.keywordsTextField = new KeywordsTextField(newsFilter);
/*     */ 
/*  56 */     SpringUtil.add(this, this.timeFrameComboBox, x, y, w = 125, h);
/*  57 */     SpringUtil.add(this, this.fromDateChooser, x += w + 5, y, w = '', h);
/*  58 */     SpringUtil.add(this, this.countriesComboBox, x += w + 5, y, w = 125, h);
/*     */ 
/*  60 */     SpringUtil.add(this, new JLabel(LocalizationManager.getText("label.keywords")), x += w + 15, y, w = 55, h);
/*  61 */     SpringUtil.add(this, this.keywordsTextField, x += w + 5, y, w = 'ú', h);
/*     */ 
/*  64 */     SpringUtil.add(this, new JLocalizableButton("button.apply", filterActionListener)
/*     */     {
/*     */     }
/*     */     , x += w + 5, y, w = 100, h);
/*     */ 
/*  67 */     SpringUtil.add(this, new JLocalizableButton("button.advanced", advancedActionListener)
/*     */     {
/*     */     }
/*     */     , x += w + 5, y, w = 100, h);
/*     */ 
/*  72 */     SpringUtil.add(this, new JLabel(LocalizationManager.getText("title.quick.search")), x += w + 15, y, w = 65, h);
/*  73 */     JTextField txtQuickSearch = new JTextField();
/*  74 */     txtQuickSearch.addKeyListener(new KeyAdapter(quickFilterListener, txtQuickSearch)
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {
/*  77 */         this.val$quickFilterListener.actionPerformed(new ActionEvent(this.val$txtQuickSearch.getText(), 1, ChartToolBar.Action.PERFORM_QUICK_FILTER.name()));
/*     */       }
/*     */     });
/*  80 */     SpringUtil.add(this, txtQuickSearch, x += w, y, w = 100, h);
/*  81 */     SpringUtil.add(this, new JButton(new ResizableIcon("toolbar_table_refresh.png"), txtQuickSearch, quickFilterListener)
/*     */     {
/*     */     }
/*     */     , x += w + 5, y, w = 30, h + 3);
/*     */   }
/*     */ 
/*     */   public void refresh(NewsFilter newsFilter)
/*     */   {
/*  94 */     this.timeFrameComboBox.refresh(newsFilter);
/*  95 */     this.fromDateChooser.refresh(newsFilter);
/*  96 */     this.countriesComboBox.refresh(newsFilter);
/*  97 */     this.keywordsTextField.refresh(newsFilter);
/*     */   }
/*     */ 
/*     */   public void newsFilterChanged()
/*     */   {
/* 102 */     this.fromDateChooser.checkTimeFrame();
/* 103 */     this.keywordsTextField.checkTimeFrame();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.FilterPanel
 * JD-Core Version:    0.6.0
 */