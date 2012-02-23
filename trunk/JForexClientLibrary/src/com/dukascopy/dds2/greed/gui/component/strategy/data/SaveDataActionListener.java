/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.data;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.charts.utils.file.filter.CsvFileFilter;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.ExportDataAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.DataLoadingPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class SaveDataActionListener
/*     */   implements ActionListener
/*     */ {
/*  38 */   private static final Logger LOGGER = LoggerFactory.getLogger(SaveDataActionListener.class);
/*     */   private DataLoadingPanel dataLoadingPanel;
/*     */   private StrategyTestPanel strategyTestPanel;
/*  43 */   private File currentDir = FileSystemView.getFileSystemView().getDefaultDirectory();
/*     */ 
/*     */   public SaveDataActionListener(StrategyTestPanel strategyTestPanel, DataLoadingPanel dataLoadingPanel)
/*     */   {
/*  49 */     this.strategyTestPanel = strategyTestPanel;
/*  50 */     this.dataLoadingPanel = dataLoadingPanel;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/*  55 */     long from = this.dataLoadingPanel.getFromDate();
/*  56 */     long to = this.dataLoadingPanel.getToDate();
/*     */ 
/*  58 */     if ((from == -9223372036854775808L) || (to == -9223372036854775808L)) {
/*  59 */       JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.incorrect.from.to.date"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  60 */       return;
/*     */     }
/*  62 */     Set selectedInstruments = this.dataLoadingPanel.getInstruments();
/*     */ 
/*  65 */     if (selectedInstruments.size() == 1)
/*  66 */       saveOneInstrumentData(from, to, (Instrument)selectedInstruments.iterator().next());
/*     */     else
/*  68 */       saveMultipleInstruments(from, to, selectedInstruments);
/*     */   }
/*     */ 
/*     */   private void saveOneInstrumentData(long from, long to, Instrument instrument)
/*     */   {
/*  76 */     String fileName = getFileName(from, to, instrument);
/*  77 */     File defaultFile = new File(this.currentDir, fileName);
/*     */ 
/*  79 */     File file = DCFileChooser.saveFileWithReplacementConfirmation((JFrame)GreedContext.get("clientGui"), this.currentDir.getAbsolutePath(), defaultFile, new CsvFileFilter());
/*  80 */     if (file != null)
/*     */     {
/*  82 */       this.currentDir = file.getParentFile();
/*     */ 
/*  84 */       ExportDataAction action = new ExportDataAction(this, this.strategyTestPanel, from, to, instrument, file);
/*     */ 
/*  86 */       GreedContext.publishEvent(action);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void saveMultipleInstruments(long from, long to, Set<Instrument> instruments)
/*     */   {
/*  94 */     List list = new ArrayList();
/*     */ 
/*  96 */     for (Instrument instrument : instruments) {
/*  97 */       String fileName = getFileName(from, to, instrument);
/*  98 */       list.add(new InstrumentTableData(instrument, fileName));
/*     */     }
/*     */ 
/* 101 */     list = SaveDataInstrumentListDialog.showModal("dialog.save.data.title", this.currentDir, list);
/* 102 */     if ((list != null) && (list.size() > 0))
/*     */     {
/* 104 */       this.currentDir = new File(((InstrumentTableData)list.get(0)).fileName).getParentFile();
/*     */ 
/* 106 */       for (InstrumentTableData saveData : list) {
/* 107 */         File file = new File(saveData.fileName);
/* 108 */         ExportDataAction action = new ExportDataAction(this, this.strategyTestPanel, from, to, saveData.instrument, file);
/*     */ 
/* 111 */         GreedContext.publishEvent(action);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getFileName(long from, long to, Instrument instrument)
/*     */   {
/* 120 */     SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
/* 121 */     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/* 123 */     StringBuilder result = new StringBuilder();
/* 124 */     if (instrument != null) {
/* 125 */       result.append(instrument.name());
/*     */     }
/* 127 */     result.append("_");
/* 128 */     result.append(format.format(Long.valueOf(from)));
/* 129 */     result.append("_");
/* 130 */     result.append(format.format(Long.valueOf(to)));
/* 131 */     result.append(".csv");
/*     */ 
/* 133 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.data.SaveDataActionListener
 * JD-Core Version:    0.6.0
 */