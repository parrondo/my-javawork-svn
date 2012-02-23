/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.IStrategyPresetsController;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.IncorrectClassTypeException;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.StrategyBinaryLoader;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.datafeed.Location;
/*     */ import java.io.File;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ 
/*     */ public class StrategiesToolbarController
/*     */   implements IStrategiesToolbarController
/*     */ {
/*  36 */   private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
/*     */   private ClientSettingsStorage settingsStorage;
/*     */ 
/*     */   public StrategiesToolbarController(ClientSettingsStorage settingsStorage)
/*     */   {
/*  41 */     this.dateFormat.setTimeZone(TimeZone.getDefault());
/*  42 */     this.settingsStorage = settingsStorage;
/*     */   }
/*     */ 
/*     */   public List<StrategyNewBean> addStrategies(StrategiesTable table, StrategiesToolbar toolbar, IStrategyPresetsController presetsController)
/*     */   {
/*  47 */     List added = new ArrayList();
/*     */ 
/*  49 */     String lastOpenedPath = (String)GreedContext.get("lastOpenedStrategiesDirectoryPath");
/*  50 */     if (lastOpenedPath == null) {
/*  51 */       lastOpenedPath = this.settingsStorage.getMyStrategiesPath();
/*     */     }
/*  53 */     JFileChooser fileChooser = new JFileChooser(lastOpenedPath);
/*  54 */     fileChooser.setFileSelectionMode(0);
/*  55 */     fileChooser.setMultiSelectionEnabled(true);
/*     */ 
/*  57 */     fileChooser.addChoosableFileFilter(new FileFilter() {
/*     */       public boolean accept(File dir) {
/*  59 */         return (dir.getName().endsWith(".java")) || (dir.getName().endsWith(".jfx")) || (dir.getName().endsWith(".mq4")) || (dir.isDirectory());
/*     */       }
/*     */ 
/*     */       public String getDescription()
/*     */       {
/*  67 */         return "All strategies";
/*     */       }
/*     */     });
/*  71 */     ChooserSelectionWrapper selection = TransportFileChooser.showOpenDialog(FileType.STRATEGY, fileChooser, (JFrame)GreedContext.get("clientGui"), Boolean.valueOf(true), GreedContext.CLIENT_MODE);
/*     */ 
/*  77 */     List selectedFiles = new ArrayList();
/*     */ 
/*  79 */     if (selection != null)
/*     */     {
/*  81 */       Location location = selection.getLocation();
/*     */ 
/*  83 */       if (location == Location.LOCAL)
/*     */       {
/*  85 */         selectedFiles = Arrays.asList(fileChooser.getSelectedFiles());
/*     */ 
/*  87 */         for (File strategyFile : selectedFiles) {
/*  88 */           StrategyNewBean strategyBean = createStrategyBean(strategyFile, presetsController, null);
/*  89 */           if (strategyBean != null) {
/*  90 */             this.settingsStorage.saveStrategyNewBean(strategyBean);
/*  91 */             table.addStrategy(strategyBean);
/*  92 */             added.add(strategyBean);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  99 */     return added;
/*     */   }
/*     */ 
/*     */   public List<StrategyNewBean> deleteStrategies(StrategiesTable table)
/*     */   {
/* 104 */     List deleted = new ArrayList();
/* 105 */     StrategiesTableModel tableModel = (StrategiesTableModel)table.getModel();
/* 106 */     int[] selectedRows = table.getSelectedRows();
/*     */ 
/* 109 */     if (selectedRows.length != 0) {
/* 110 */       for (int i = selectedRows.length - 1; i >= 0; i--) {
/* 111 */         StrategyNewBean beanToRemove = (StrategyNewBean)tableModel.getStrategies().get(selectedRows[i]);
/* 112 */         this.settingsStorage.removeStrategyNewBean(beanToRemove);
/* 113 */         tableModel.removeRow(selectedRows[i]);
/* 114 */         deleted.add(beanToRemove);
/*     */       }
/*     */     }
/*     */ 
/* 118 */     return deleted;
/*     */   }
/*     */ 
/*     */   public StrategyNewBean createStrategyBean(File strategyFile, IStrategyPresetsController presetsController, String activePresetName)
/*     */   {
/* 124 */     StrategyNewBean strategyBean = new StrategyNewBean();
/* 125 */     strategyBean.resetDates();
/* 126 */     strategyBean.setType(StrategyType.LOCAL);
/* 127 */     strategyBean.setStatus(StrategyStatus.STOPPED);
/* 128 */     strategyBean.setId(Integer.valueOf(IdManager.getInstance().getNextServiceId()));
/* 129 */     strategyBean.setName(strategyFile.getName().substring(0, strategyFile.getName().lastIndexOf(46)));
/*     */ 
/* 131 */     File binaryFile = null;
/* 132 */     File sourceFile = null;
/*     */ 
/* 136 */     if (strategyFile.getName().endsWith(".jfx")) {
/* 137 */       binaryFile = strategyFile;
/*     */ 
/* 139 */       String sourceFilePath = strategyFile.getAbsolutePath().substring(0, strategyFile.getAbsolutePath().lastIndexOf(46)) + ".java";
/* 140 */       sourceFile = new File(sourceFilePath);
/* 141 */       if (!sourceFile.exists()) {
/* 142 */         sourceFilePath = strategyFile.getAbsolutePath().substring(0, strategyFile.getAbsolutePath().lastIndexOf(46)) + ".mq4";
/* 143 */         sourceFile = new File(sourceFilePath);
/* 144 */         if (!sourceFile.exists()) {
/* 145 */           sourceFile = null;
/*     */         }
/*     */       }
/*     */     }
/* 149 */     else if ((strategyFile.getName().endsWith(".java")) || (strategyFile.getName().endsWith(".mq4")))
/*     */     {
/* 151 */       sourceFile = strategyFile;
/*     */ 
/* 153 */       String binaryFilePath = strategyFile.getAbsolutePath().substring(0, strategyFile.getAbsolutePath().lastIndexOf(46)) + ".jfx";
/* 154 */       binaryFile = new File(binaryFilePath);
/* 155 */       if (!binaryFile.exists()) {
/* 156 */         binaryFile = null;
/*     */       }
/*     */     }
/*     */ 
/* 160 */     if (binaryFile != null) {
/*     */       try {
/* 162 */         StrategyBinaryLoader.loadStrategy(binaryFile, strategyBean);
/*     */       }
/*     */       catch (IncorrectClassTypeException ex) {
/* 165 */         return null;
/*     */       }
/*     */ 
/* 168 */       List strategyPresets = presetsController.loadPresets(strategyBean);
/* 169 */       strategyBean.setStrategyPresets(strategyPresets);
/*     */ 
/* 171 */       StrategyPreset activePreset = presetsController.getStrategyPresetBy(strategyPresets, activePresetName);
/* 172 */       if (activePreset == null) {
/* 173 */         activePreset = presetsController.getStrategyPresetBy(strategyPresets, "DEFAULT_PRESET_ID");
/*     */       }
/* 175 */       strategyBean.setActivePreset(activePreset);
/*     */     }
/*     */ 
/* 178 */     strategyBean.setStrategySourceFile(sourceFile);
/*     */ 
/* 180 */     return strategyBean;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbarController
 * JD-Core Version:    0.6.0
 */