/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.calendar;
/*     */ 
/*     */ import com.dukascopy.api.CalendarFilter;
/*     */ import com.dukascopy.api.INewsFilter.Country;
/*     */ import com.dukascopy.api.INewsFilter.EventCategory;
/*     */ import com.dukascopy.api.INewsFilter.IndexRegion;
/*     */ import com.dukascopy.api.INewsFilter.Region;
/*     */ import com.dukascopy.api.INewsFilter.StockIndex;
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
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.SpringLayout;
/*     */ 
/*     */ class SetupDialog extends JDialog
/*     */ {
/*  39 */   private static final Image DIALOG_ICON = StratUtils.loadImage("rc/media/preferences_advanced.png");
/*  40 */   private static final Dimension SIZE = new Dimension(600, 550);
/*     */   private CalendarFilter calendarFilter;
/*     */   private final CountriesTree countriesTree;
/*     */   private final StockIndiciesTree stockIndiciesTree;
/*     */   private final EventCategoriesList eventCategoriesList;
/*     */   private final TimeFrameComboBox timeFrameComboBox;
/*     */   private final CalendarTypeComboBox calendarTypeComboBox;
/*     */   private final FromDateChooser fromDateChooser;
/*     */   private final KeywordsTextField<CalendarFilter> keywordsTextField;
/*     */   private final ActionListener actionListener;
/*     */ 
/*     */   public SetupDialog(CalendarFilter calendarFilter, ActionListener actionListener)
/*     */   {
/*  53 */     super((JFrame)null, LocalizationManager.getText("title.setup"), true);
/*  54 */     setIconImage(DIALOG_ICON);
/*     */ 
/*  56 */     this.calendarFilter = new CalendarFilter(calendarFilter);
/*  57 */     this.countriesTree = new CountriesTree(null);
/*  58 */     this.stockIndiciesTree = new StockIndiciesTree(null);
/*  59 */     this.eventCategoriesList = new EventCategoriesList(null);
/*  60 */     this.timeFrameComboBox = new TimeFrameComboBox(this.calendarFilter);
/*  61 */     this.calendarTypeComboBox = new CalendarTypeComboBox(this.calendarFilter);
/*  62 */     this.fromDateChooser = new FromDateChooser(this.calendarFilter);
/*  63 */     this.keywordsTextField = new KeywordsTextField(this.calendarFilter);
/*  64 */     this.actionListener = actionListener;
/*     */ 
/*  66 */     setSize(SIZE);
/*  67 */     setResizable(false);
/*  68 */     setLayout(new BorderLayout());
/*  69 */     setDefaultCloseOperation(2);
/*     */ 
/*  71 */     add(createCenterPanel(), "Center");
/*  72 */     add(createBottomPanel(), "Last");
/*     */ 
/*  74 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/*  75 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private JPanel createCenterPanel() {
/*  79 */     return new JPanel(new BorderLayout())
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   private JPanel createBottomPanel()
/*     */   {
/* 108 */     return new JPanel(new SpringLayout())
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   private void reset()
/*     */   {
/* 140 */     this.calendarFilter = new CalendarFilter();
/* 141 */     this.countriesTree.reset();
/* 142 */     this.stockIndiciesTree.reset();
/* 143 */     this.eventCategoriesList.reset();
/* 144 */     this.timeFrameComboBox.refresh(this.calendarFilter);
/* 145 */     this.calendarTypeComboBox.refresh(this.calendarFilter);
/* 146 */     this.fromDateChooser.setDate(this.calendarFilter.getFrom());
/* 147 */     this.keywordsTextField.setText("");
/*     */   }
/*     */ 
/*     */   private class EventCategoriesList extends OptionsList<INewsFilter.EventCategory>
/*     */   {
/*     */     private EventCategoriesList()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected INewsFilter.EventCategory[] getItems()
/*     */     {
/* 234 */       return INewsFilter.EventCategory.values();
/*     */     }
/*     */ 
/*     */     protected Set<INewsFilter.EventCategory> getSelectedItems() {
/* 238 */       return SetupDialog.this.calendarFilter.getEventCategories();
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
/* 209 */       return INewsFilter.IndexRegion.values();
/*     */     }
/*     */ 
/*     */     protected Set<INewsFilter.StockIndex> getSelectedItems()
/*     */     {
/* 214 */       return SetupDialog.this.calendarFilter.getStockIndicies();
/*     */     }
/*     */ 
/*     */     protected List<INewsFilter.StockIndex> getItems(INewsFilter.IndexRegion indexRegion)
/*     */     {
/* 219 */       return new ArrayList(indexRegion)
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
/* 185 */       return INewsFilter.Region.values();
/*     */     }
/*     */ 
/*     */     protected Set<INewsFilter.Country> getSelectedItems()
/*     */     {
/* 190 */       return SetupDialog.this.calendarFilter.getCountries();
/*     */     }
/*     */ 
/*     */     protected List<INewsFilter.Country> getItems(INewsFilter.Region region)
/*     */     {
/* 195 */       return new ArrayList(region)
/*     */       {
/*     */       };
/*     */     }
/*     */   }
/*     */ 
/*     */   private class OptionsPanel extends JPanel
/*     */   {
/*     */     public OptionsPanel()
/*     */     {
/* 154 */       setLayout(new SpringLayout());
/* 155 */       setPreferredSize(new Dimension(600, 100));
/*     */ 
/* 157 */       SpringUtil.add(this, new JPanel(new SpringLayout(), SetupDialog.this)
/*     */       {
/*     */       }
/*     */       , 10, 0, 400, 100);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.calendar.SetupDialog
 * JD-Core Version:    0.6.0
 */