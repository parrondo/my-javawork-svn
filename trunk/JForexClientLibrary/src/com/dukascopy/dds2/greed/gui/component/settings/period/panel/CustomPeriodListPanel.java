/*     */ package com.dukascopy.dds2.greed.gui.component.settings.period.panel;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.BorderLayout;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.DefaultListSelectionModel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ 
/*     */ public class CustomPeriodListPanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private JList periodsList;
/*     */   private JScrollPane listScrollPane;
/*     */   private PeriodListModel<JForexPeriod> periodsListModel;
/*     */   private PeriodListSelectionModel selectionModel;
/*     */ 
/*     */   public CustomPeriodListPanel()
/*     */   {
/*  33 */     init();
/*     */   }
/*     */ 
/*     */   private void init() {
/*  37 */     setLayout(new BorderLayout());
/*     */ 
/*  39 */     add(getListScrollPane(), "Center");
/*     */   }
/*     */ 
/*     */   public List<JForexPeriod> getPeriods() {
/*  43 */     List list = new ArrayList();
/*  44 */     for (int i = 0; i < getPeriodsListModel().size(); i++) {
/*  45 */       list.add(getPeriodsListModel().get(i));
/*     */     }
/*  47 */     return list;
/*     */   }
/*     */ 
/*     */   public void setPeriods(List<JForexPeriod> subscribedPeriods) {
/*  51 */     getPeriodsListModel().clear();
/*  52 */     getPeriodsListModel().addAll(subscribedPeriods);
/*     */   }
/*     */ 
/*     */   public void addPeriod(JForexPeriod period) {
/*  56 */     getPeriodsListModel().addElement(period);
/*     */ 
/*  60 */     List periods = getPeriods();
/*  61 */     periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).sortChartPeriods(periods);
/*  62 */     setPeriods(periods);
/*     */   }
/*     */ 
/*     */   public boolean periodAlreadyAdded(JForexPeriod period) {
/*  66 */     for (int i = 0; i < getPeriodsListModel().size(); i++) {
/*  67 */       JForexPeriod p = (JForexPeriod)getPeriodsListModel().get(i);
/*  68 */       if (p.equals(period)) {
/*  69 */         return true;
/*     */       }
/*     */     }
/*  72 */     return false;
/*     */   }
/*     */ 
/*     */   public void removePeriods(List<JForexPeriod> periods) {
/*  76 */     for (JForexPeriod period : periods)
/*  77 */       removePeriod(period);
/*     */   }
/*     */ 
/*     */   public void removePeriod(JForexPeriod period)
/*     */   {
/*  82 */     for (int i = 0; i < getPeriodsListModel().size(); i++)
/*  83 */       if (((JForexPeriod)getPeriodsListModel().get(i)).equals(period))
/*  84 */         getPeriodsListModel().remove(i);
/*     */   }
/*     */ 
/*     */   public List<JForexPeriod> getPresentedPeriods()
/*     */   {
/*  90 */     List result = new ArrayList();
/*  91 */     for (int i = 0; i < getPeriodsListModel().size(); i++) {
/*  92 */       result.add(getPeriodsListModel().get(i));
/*     */     }
/*  94 */     return result;
/*     */   }
/*     */ 
/*     */   public List<JForexPeriod> getSelectedPeriods() {
/*  98 */     List periodsToRemove = new ArrayList();
/*  99 */     for (int i = 0; i < getPeriodsList().getSelectedValues().length; i++) {
/* 100 */       periodsToRemove.add((JForexPeriod)getPeriodsList().getSelectedValues()[i]);
/*     */     }
/* 102 */     return periodsToRemove;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 106 */     getPeriodsListModel().clear();
/*     */   }
/*     */ 
/*     */   public void addPeriods(List<JForexPeriod> periods) {
/* 110 */     getPeriodsListModel().addAll(periods);
/*     */   }
/*     */ 
/*     */   public JList getPeriodsList() {
/* 114 */     if (this.periodsList == null) {
/* 115 */       this.periodsList = new JList(getPeriodsListModel());
/* 116 */       this.periodsList.setSelectionMode(2);
/* 117 */       this.periodsList.setCellRenderer(new CustomPeriodsRenderer());
/* 118 */       this.periodsList.setSelectionModel(getSelectionModel());
/*     */     }
/* 120 */     return this.periodsList;
/*     */   }
/*     */ 
/*     */   public PeriodListSelectionModel getSelectionModel() {
/* 124 */     if (this.selectionModel == null) {
/* 125 */       this.selectionModel = new PeriodListSelectionModel();
/*     */     }
/* 127 */     return this.selectionModel;
/*     */   }
/*     */ 
/*     */   public PeriodListModel<JForexPeriod> getPeriodsListModel() {
/* 131 */     if (this.periodsListModel == null) {
/* 132 */       this.periodsListModel = new PeriodListModel();
/*     */     }
/* 134 */     return this.periodsListModel;
/*     */   }
/*     */ 
/*     */   protected JScrollPane getListScrollPane()
/*     */   {
/* 199 */     if (this.listScrollPane == null) {
/* 200 */       this.listScrollPane = new JScrollPane(getPeriodsList());
/*     */     }
/* 202 */     return this.listScrollPane;
/*     */   }
/*     */ 
/*     */   public class PeriodListSelectionModel extends DefaultListSelectionModel
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public PeriodListSelectionModel()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean isSelectedIndex(int index)
/*     */     {
/* 186 */       List periods = CustomPeriodsRenderer.getInUsePeriods();
/* 187 */       JForexPeriod periodForIndex = (JForexPeriod)CustomPeriodListPanel.this.getPeriodsListModel().get(index);
/* 188 */       if (periods.contains(periodForIndex)) {
/* 189 */         return false;
/*     */       }
/*     */ 
/* 192 */       return super.isSelectedIndex(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class PeriodListModel<T> extends DefaultListModel
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public PeriodListModel()
/*     */     {
/*     */     }
/*     */ 
/*     */     public List<T> getAllElements()
/*     */     {
/* 142 */       List list = new ArrayList();
/* 143 */       for (int i = 0; i < size(); i++) {
/* 144 */         Object t = get(i);
/* 145 */         list.add(t);
/*     */       }
/* 147 */       return list;
/*     */     }
/*     */ 
/*     */     public void addAll(List<T> list) {
/* 151 */       for (Iterator i$ = list.iterator(); i$.hasNext(); ) { Object t = i$.next();
/* 152 */         addElement(t); }
/*     */     }
/*     */ 
/*     */     public void setAll(List<T> list)
/*     */     {
/* 157 */       removeAllElements();
/* 158 */       for (Iterator i$ = list.iterator(); i$.hasNext(); ) { Object t = i$.next();
/* 159 */         addElement(t); }
/*     */     }
/*     */ 
/*     */     public List<T> removeElements(Object[] selected)
/*     */     {
/* 164 */       List removed = new ArrayList();
/* 165 */       for (Object t : selected) {
/* 166 */         removed.add(t);
/* 167 */         removeElement(t);
/*     */       }
/* 169 */       return removed;
/*     */     }
/*     */ 
/*     */     public T get(int index) {
/* 173 */       return super.get(index);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.period.panel.CustomPeriodListPanel
 * JD-Core Version:    0.6.0
 */