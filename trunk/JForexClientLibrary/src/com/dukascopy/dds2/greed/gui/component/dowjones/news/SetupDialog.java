/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*     */ 
/*     */ import com.dukascopy.api.INewsFilter.Country;
/*     */ import com.dukascopy.api.INewsFilter.Currency;
/*     */ import com.dukascopy.api.INewsFilter.IndexRegion;
/*     */ import com.dukascopy.api.INewsFilter.MarketSector;
/*     */ import com.dukascopy.api.INewsFilter.Region;
/*     */ import com.dukascopy.api.INewsFilter.StockIndex;
/*     */ import com.dukascopy.api.NewsFilter;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.SpringUtil;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.KeywordsTextField;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.list.OptionsList;
/*     */ import com.dukascopy.dds2.greed.gui.tree.OptionsTree;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.SpringLayout;
/*     */ 
/*     */ class SetupDialog extends JDialog
/*     */   implements INewsFilterChangeListener
/*     */ {
/*  42 */   private static final Image DIALOG_ICON = StratUtils.loadImage("rc/media/preferences_advanced.png");
/*  43 */   private static final Dimension SIZE = new Dimension(600, 550);
/*     */   private NewsFilter newsFilter;
/*     */   private final CountriesTree countriesTree;
/*     */   private final StockIndiciesTree stockIndiciesTree;
/*     */   private final CurrenciesList currenciesList;
/*     */   private final MarketSectorsList marketSectorsList;
/*     */   private final TimeFrameComboBox timeFrameComboBox;
/*     */   private final FromDateChooser fromDateChooser;
/*     */   private final KeywordsTextField<NewsFilter> keywordsTextField;
/*     */   private final HotCheckBox hotCheckBox;
/*     */   private final ActionListener actionListener;
/*     */ 
/*     */   public SetupDialog(NewsFilter newsFilter, ActionListener actionListener)
/*     */   {
/*  57 */     super((JFrame)null, LocalizationManager.getText("title.setup"), true);
/*  58 */     setIconImage(DIALOG_ICON);
/*     */ 
/*  60 */     this.newsFilter = new NewsFilter(newsFilter);
/*  61 */     this.actionListener = actionListener;
/*     */ 
/*  63 */     setSize(SIZE);
/*  64 */     setResizable(false);
/*  65 */     setLayout(new BorderLayout());
/*  66 */     setDefaultCloseOperation(2);
/*     */ 
/*  68 */     this.countriesTree = new CountriesTree(null);
/*  69 */     this.stockIndiciesTree = new StockIndiciesTree(null);
/*  70 */     this.currenciesList = new CurrenciesList(null);
/*  71 */     this.marketSectorsList = new MarketSectorsList(null);
/*  72 */     this.timeFrameComboBox = new TimeFrameComboBox(this.newsFilter, this);
/*  73 */     this.fromDateChooser = new FromDateChooser(this.newsFilter);
/*  74 */     this.keywordsTextField = new KeywordsTextField(this.newsFilter);
/*  75 */     this.hotCheckBox = new HotCheckBox();
/*     */ 
/*  77 */     add(createCenterPanel(), "Center");
/*  78 */     add(createBottomPanel(), "Last");
/*     */ 
/*  80 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/*  81 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private JPanel createCenterPanel() {
/*  85 */     return new JPanel(new BorderLayout())
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   private JPanel createBottomPanel()
/*     */   {
/* 135 */     return new JPanel(new SpringLayout())
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public void newsFilterChanged()
/*     */   {
/* 169 */     this.fromDateChooser.checkTimeFrame();
/* 170 */     this.keywordsTextField.checkTimeFrame();
/*     */   }
/*     */ 
/*     */   private void reset() {
/* 174 */     this.newsFilter = new NewsFilter();
/* 175 */     this.countriesTree.reset();
/* 176 */     this.stockIndiciesTree.reset();
/* 177 */     this.currenciesList.reset();
/* 178 */     this.marketSectorsList.reset();
/* 179 */     this.timeFrameComboBox.refresh(this.newsFilter);
/* 180 */     this.fromDateChooser.refresh(this.newsFilter);
/* 181 */     this.keywordsTextField.setText("");
/* 182 */     this.hotCheckBox.reset();
/*     */   }
/*     */ 
/*     */   private class HotCheckBox extends JCheckBox
/*     */   {
/*     */     public HotCheckBox()
/*     */     {
/* 263 */       setSelected(SetupDialog.this.newsFilter.isOnlyHot());
/* 264 */       setAction(new AbstractAction(LocalizationManager.getText("label.hot.news"), SetupDialog.this)
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 267 */           SetupDialog.this.newsFilter.setOnlyHot(SetupDialog.HotCheckBox.this.isSelected());
/*     */         } } );
/*     */     }
/*     */ 
/*     */     public void reset() {
/* 273 */       setSelected(SetupDialog.this.newsFilter.isOnlyHot());
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CurrenciesList extends OptionsList<INewsFilter.Currency>
/*     */   {
/*     */     private CurrenciesList()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected INewsFilter.Currency[] getItems()
/*     */     {
/* 251 */       return INewsFilter.Currency.values();
/*     */     }
/*     */ 
/*     */     protected Set<INewsFilter.Currency> getSelectedItems() {
/* 255 */       return SetupDialog.this.newsFilter.getCurrencies();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MarketSectorsList extends OptionsList<INewsFilter.MarketSector>
/*     */   {
/*     */     private MarketSectorsList()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected INewsFilter.MarketSector[] getItems()
/*     */     {
/* 240 */       return INewsFilter.MarketSector.values();
/*     */     }
/*     */ 
/*     */     protected Set<INewsFilter.MarketSector> getSelectedItems() {
/* 244 */       return SetupDialog.this.newsFilter.getMarketSectors();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class StockIndiciesTree extends OptionsTree<INewsFilter.IndexRegion, INewsFilter.StockIndex>
/*     */   {
/*     */     private StockIndiciesTree()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected INewsFilter.IndexRegion[] getRoots()
/*     */     {
/* 215 */       return INewsFilter.IndexRegion.values();
/*     */     }
/*     */ 
/*     */     protected Set<INewsFilter.StockIndex> getSelectedItems()
/*     */     {
/* 220 */       return SetupDialog.this.newsFilter.getStockIndicies();
/*     */     }
/*     */ 
/*     */     protected List<INewsFilter.StockIndex> getItems(INewsFilter.IndexRegion indexRegion)
/*     */     {
/* 225 */       return new ArrayList(indexRegion)
/*     */       {
/*     */       };
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CountriesTree extends OptionsTree<INewsFilter.Region, INewsFilter.Country>
/*     */   {
/*     */     private CountriesTree()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected INewsFilter.Region[] getRoots()
/*     */     {
/* 191 */       return INewsFilter.Region.values();
/*     */     }
/*     */ 
/*     */     protected Set<INewsFilter.Country> getSelectedItems()
/*     */     {
/* 196 */       return SetupDialog.this.newsFilter.getCountries();
/*     */     }
/*     */ 
/*     */     protected List<INewsFilter.Country> getItems(INewsFilter.Region region)
/*     */     {
/* 201 */       return new ArrayList(region)
/*     */       {
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.SetupDialog
 * JD-Core Version:    0.6.0
 */