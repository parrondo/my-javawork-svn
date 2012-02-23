/*     */ package com.dukascopy.charts.tablebuilder;
/*     */ 
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.DataChangeListenerAdapter;
/*     */ import com.dukascopy.charts.tablebuilder.component.model.DataTablePresentationAbstractModel;
/*     */ import com.dukascopy.charts.tablebuilder.component.table.DataTablePresentationAbstractJTable;
/*     */ import java.awt.BorderLayout;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public abstract class AbstractDataTablePresentationManager<T extends Data, DataSequenceClass extends AbstractDataSequence<T>> extends DataChangeListenerAdapter
/*     */   implements ITablePresentationManager
/*     */ {
/*     */   private final AbstractDataSequenceProvider<DataSequenceClass, T> dataSequenceProvider;
/*     */   private JComponent tablePresentationComponent;
/*  31 */   private Boolean running = Boolean.valueOf(false);
/*     */   private String quickFilter;
/*     */ 
/*     */   public AbstractDataTablePresentationManager(AbstractDataSequenceProvider<DataSequenceClass, T> dataSequenceProvider)
/*     */   {
/*  40 */     this.dataSequenceProvider = dataSequenceProvider;
/*     */   }
/*     */   protected abstract DataTablePresentationAbstractModel<? extends Enum<?>, T> getTableModel();
/*     */ 
/*     */   protected abstract boolean matched(T paramT, String paramString);
/*     */ 
/*  48 */   private T[] performQuickFilter(DataSequenceClass dataSequence, String quickFilter) { List result = new ArrayList();
/*  49 */     for (int i = 0; i < dataSequence.size(); i++) {
/*  50 */       Data data = dataSequence.getData(i);
/*  51 */       if (matched(data, quickFilter)) {
/*  52 */         result.add(data);
/*     */       }
/*     */     }
/*     */ 
/*  56 */     Data[] array = new Data[result.size()];
/*  57 */     result.toArray(array);
/*  58 */     return (Data[])array;
/*     */   }
/*     */ 
/*     */   public void applyQuickFilter(String pattern)
/*     */   {
/*  63 */     setQuickFilter(pattern);
/*  64 */     getDataPresentationTable().setPattern(pattern);
/*  65 */     refresh();
/*     */   }
/*     */ 
/*     */   public void dataChanged(long from, long to, Period period, OfferSide offerSide)
/*     */   {
/*  70 */     refresh();
/*     */   }
/*     */ 
/*     */   private synchronized void refresh() {
/*  74 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/*  77 */         AbstractDataSequence dataSequence = (AbstractDataSequence)AbstractDataTablePresentationManager.this.getDataSequenceProvider().getDataSequence();
/*     */ 
/*  79 */         Data[] rawData = dataSequence.getData();
/*  80 */         Data[] datas = (Data[])Arrays.copyOfRange(rawData, dataSequence.getExtraBefore(), dataSequence.getExtraBefore() + dataSequence.size());
/*     */ 
/*  85 */         int selectedColumn = AbstractDataTablePresentationManager.this.getDataPresentationTable().getSelectedColumn();
/*  86 */         int selectedRow = AbstractDataTablePresentationManager.this.getDataPresentationTable().getSelectedRow();
/*     */ 
/*  91 */         if ((AbstractDataTablePresentationManager.this.getQuickFilter() == null) || (AbstractDataTablePresentationManager.this.getQuickFilter().isEmpty())) {
/*  92 */           AbstractDataTablePresentationManager.this.getTableModel().clear();
/*  93 */           AbstractDataTablePresentationManager.this.getTableModel().addAll(datas);
/*     */         }
/*     */         else {
/*  96 */           Data[] filteredDatas = AbstractDataTablePresentationManager.this.performQuickFilter(dataSequence, AbstractDataTablePresentationManager.this.getQuickFilter());
/*  97 */           AbstractDataTablePresentationManager.this.getTableModel().clear();
/*  98 */           AbstractDataTablePresentationManager.this.getTableModel().addAll(filteredDatas);
/*     */         }
/*     */ 
/* 104 */         if ((selectedColumn > -1) && (selectedRow > -1))
/* 105 */           AbstractDataTablePresentationManager.this.getDataPresentationTable().changeSelection(selectedRow, selectedColumn, false, false);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 113 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/* 116 */         AbstractDataTablePresentationManager.this.getTableModel().clear();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public AbstractDataSequenceProvider<DataSequenceClass, T> getDataSequenceProvider() {
/* 122 */     return this.dataSequenceProvider;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 127 */     stop();
/*     */ 
/* 129 */     setRunning(Boolean.valueOf(true));
/*     */ 
/* 131 */     getDataSequenceProvider().addDataChangeListener(this);
/* 132 */     refresh();
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 137 */     setRunning(Boolean.valueOf(false));
/*     */ 
/* 139 */     getDataSequenceProvider().removeDataChangeListener(this);
/* 140 */     clear();
/*     */   }
/*     */ 
/*     */   public JComponent getTablePresentationComponent()
/*     */   {
/* 145 */     if (this.tablePresentationComponent == null) {
/* 146 */       this.tablePresentationComponent = new JPanel(new BorderLayout());
/* 147 */       this.tablePresentationComponent.add(new JScrollPane(getDataPresentationTable()), "Center");
/*     */     }
/*     */ 
/* 150 */     return this.tablePresentationComponent;
/*     */   }
/*     */ 
/*     */   public Boolean isRunning() {
/* 154 */     return this.running;
/*     */   }
/*     */ 
/*     */   private void setRunning(Boolean running) {
/* 158 */     this.running = running;
/*     */   }
/*     */ 
/*     */   protected String getQuickFilter() {
/* 162 */     return this.quickFilter;
/*     */   }
/*     */ 
/*     */   protected void setQuickFilter(String quickFilter) {
/* 166 */     this.quickFilter = quickFilter;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.AbstractDataTablePresentationManager
 * JD-Core Version:    0.6.0
 */