/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.TesterDisclaimDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ExportDataAction extends AppActionEvent
/*     */ {
/*  42 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyTesterAction.class);
/*     */   private static final String COL_TITLES_FOR_TICK_DATA = "Time,Ask,Bid,AskVolume,BidVolume \r\n";
/*     */   private StrategyTestPanel testerPanel;
/*     */   private long from;
/*     */   private long to;
/*     */   private Instrument instrument;
/*     */   private volatile boolean doNotRunMe;
/*     */   private boolean loadData;
/*     */   private File file;
/*     */ 
/*     */   public ExportDataAction(Object source, StrategyTestPanel testerPanel, long from, long to, Instrument instrument, File file)
/*     */   {
/*  56 */     super(source, false, true);
/*  57 */     this.testerPanel = testerPanel;
/*     */ 
/*  59 */     this.from = from;
/*  60 */     this.to = to;
/*  61 */     if (!checkRange(from, to)) {
/*  62 */       this.doNotRunMe = true;
/*  63 */       return;
/*     */     }
/*  65 */     this.instrument = instrument;
/*  66 */     this.file = file;
/*     */ 
/*  68 */     testerPanel.lockGUI(false);
/*     */   }
/*     */ 
/*     */   private boolean checkRange(long from, long to) {
/*  72 */     if (to <= from) {
/*  73 */       JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.end.day.should.be.after.start.day"), LocalizationManager.getText("joption.pane.wrong"), 1);
/*     */ 
/*  77 */       return false;
/*     */     }
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  84 */     if (this.doNotRunMe) {
/*  85 */       return;
/*     */     }
/*  87 */     loadData();
/*  88 */     if (this.file != null)
/*  89 */       exportData();
/*     */   }
/*     */ 
/*     */   private void loadData()
/*     */   {
/*  94 */     this.loadData = true;
/*     */     try {
/*  96 */       SwingUtilities.invokeAndWait(new Runnable() {
/*     */         public void run() {
/*  98 */           if (!TesterDisclaimDialog.isAcceptState()) {
/*  99 */             TesterDisclaimDialog disclaimer = TesterDisclaimDialog.getInstance();
/* 100 */             disclaimer.showDialog();
/* 101 */             if (!disclaimer.isAccepted())
/* 102 */               ExportDataAction.access$002(ExportDataAction.this, false);
/*     */           }
/*     */         } } );
/*     */     } catch (Exception e) {
/* 107 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 110 */     if (!this.loadData) {
/* 111 */       return;
/*     */     }
/*     */ 
/* 114 */     int value = 0;
/* 115 */     value += DataCacheUtils.separateChunksForCache(Period.TICK, this.from, this.to).length;
/* 116 */     int totalValue = value;
/*     */ 
/* 118 */     IFeedDataProvider feedDataProvider = (IFeedDataProvider)GreedContext.get("feedDataProvider");
/* 119 */     LoadingProgressListener loadingProgressListener = new LoadingProgressListener(totalValue) {
/*     */       public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*     */         try {
/* 122 */           SwingUtilities.invokeAndWait(new Runnable(startTime, currentTime, information) {
/*     */             public void run() {
/* 124 */               long value = DataCacheUtils.separateChunksForCache(Period.TICK, this.val$startTime, this.val$currentTime).length * 100 / ExportDataAction.2.this.val$totalValue;
/* 125 */               ExportDataAction.this.testerPanel.updateProgressBar((int)value, this.val$information);
/*     */             } } );
/*     */         } catch (Exception e) {
/* 129 */           ExportDataAction.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex) {
/* 134 */         if ((!allDataLoaded) && (ex != null))
/* 135 */           ExportDataAction.LOGGER.error(ex.getMessage(), ex);
/*     */         try
/*     */         {
/* 138 */           SwingUtilities.invokeAndWait(new Runnable(allDataLoaded, currentTime) {
/*     */             public void run() {
/* 140 */               long value = this.val$allDataLoaded ? 100L : this.val$currentTime * 100L / ExportDataAction.2.this.val$totalValue;
/* 141 */               ExportDataAction.this.testerPanel.updateProgressBar((int)value, ExportDataAction.2.this.stopJob() ? "Downloading canceled" : this.val$allDataLoaded ? "Downloading finished" : "Downloading failed");
/*     */             }
/*     */           });
/* 144 */           ExportDataAction.access$002(ExportDataAction.this, allDataLoaded);
/*     */         } catch (Exception e) {
/* 146 */           ExportDataAction.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public boolean stopJob() {
/* 151 */         return ExportDataAction.this.testerPanel.dataLoadingCancelRequested();
/*     */       }
/*     */     };
/*     */     try {
/* 156 */       feedDataProvider.loadTicksDataInCacheSynched(this.instrument, this.from, this.to, loadingProgressListener);
/*     */ 
/* 158 */       if (!this.loadData)
/* 159 */         SwingUtilities.invokeAndWait(new Runnable() {
/*     */           public void run() {
/* 161 */             JOptionPane.showMessageDialog(null, LocalizationManager.getText("joption.pane.can.not.load.data"), LocalizationManager.getText("joption.pane.error"), 1);
/*     */           } } );
/*     */     }
/*     */     catch (Exception e) {
/* 166 */       LOGGER.error(e.getMessage(), e);
/* 167 */       SwingUtilities.invokeLater(new Runnable(e) {
/*     */         public void run() {
/* 169 */           JOptionPane.showMessageDialog(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.load.data", new Object[] { this.val$e.getMessage() }), LocalizationManager.getText("joption.pane.error"), 1);
/*     */         }
/*     */       });
/* 172 */       this.loadData = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void exportData() {
/* 177 */     if (!this.loadData) {
/* 178 */       return; } 
/*     */ int value = DataCacheUtils.separateChunksForCache(Period.TICK, this.from, this.to).length;
/* 182 */     int totalValue = value;
/*     */ 
/* 184 */     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
/* 185 */     dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 186 */     DecimalFormat priceFormat = new DecimalFormat("0.#####");
/*     */     Writer out;
/*     */     try { out = new BufferedWriter(new FileWriter(this.file));
/* 191 */       String colTitles = "Time,Ask,Bid,AskVolume,BidVolume \r\n";
/* 192 */       out.append(colTitles, 0, colTitles.length());
/*     */     } catch (IOException e) {
/* 194 */       LOGGER.error(e.getMessage(), e);
/* 195 */       SwingUtilities.invokeLater(new Runnable(e) {
/*     */         public void run() {
/* 197 */           JOptionPane.showMessageDialog(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.save.data", new Object[] { this.val$e.getMessage() }), LocalizationManager.getText("joption.pane.error"), 1);
/*     */         }
/*     */       });
/* 201 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 205 */       IFeedDataProvider feedDataProvider = (IFeedDataProvider)GreedContext.get("feedDataProvider");
/*     */ 
/* 207 */       LiveFeedListener liveFeedListener = new LiveFeedListener(out, dateFormat, priceFormat)
/*     */       {
/*     */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*     */           try {
/* 211 */             this.val$out.write(this.val$dateFormat.format(Long.valueOf(time)) + "," + this.val$priceFormat.format(ask) + "," + this.val$priceFormat.format(bid) + "," + this.val$priceFormat.format(askVol) + "," + this.val$priceFormat.format(bidVol) + "\r\n");
/*     */           }
/*     */           catch (IOException e) {
/* 214 */             ExportDataAction.LOGGER.error(e.getMessage(), e);
/* 215 */             ExportDataAction.access$002(ExportDataAction.this, false);
/*     */           }
/*     */         }
/*     */ 
/*     */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */         {
/*     */           try {
/* 222 */             this.val$out.write(this.val$dateFormat.format(Long.valueOf(time)) + "," + this.val$priceFormat.format(open) + "," + this.val$priceFormat.format(high) + "," + this.val$priceFormat.format(low) + "," + this.val$priceFormat.format(close) + "," + this.val$priceFormat.format(vol) + "\r\n");
/*     */           }
/*     */           catch (IOException e) {
/* 225 */             ExportDataAction.LOGGER.error(e.getMessage(), e);
/* 226 */             ExportDataAction.access$002(ExportDataAction.this, false);
/*     */           }
/*     */         }
/*     */       };
/* 230 */       LoadingProgressListener loadingProgressListener = new LoadingProgressListener(totalValue) {
/*     */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*     */           try {
/* 233 */             SwingUtilities.invokeAndWait(new Runnable(startTime, currentTime, information) {
/*     */               public void run() {
/* 235 */                 long value = DataCacheUtils.separateChunksForCache(Period.TICK, this.val$startTime, this.val$currentTime).length * 100 / ExportDataAction.7.this.val$totalValue;
/* 236 */                 ExportDataAction.this.testerPanel.updateProgressBar((int)value, this.val$information);
/*     */               } } );
/*     */           } catch (Exception e) {
/* 240 */             ExportDataAction.LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         }
/*     */ 
/*     */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex) {
/* 245 */           if ((!allDataLoaded) && (ex != null))
/* 246 */             ExportDataAction.LOGGER.error(ex.getMessage(), ex);
/*     */           try
/*     */           {
/* 249 */             SwingUtilities.invokeAndWait(new Runnable(allDataLoaded, currentTime) {
/*     */               public void run() {
/* 251 */                 long value = this.val$allDataLoaded ? 100L : this.val$currentTime * 100L / ExportDataAction.7.this.val$totalValue;
/* 252 */                 ExportDataAction.this.testerPanel.updateProgressBar((int)value, ExportDataAction.7.this.stopJob() ? "Downloading canceled" : this.val$allDataLoaded ? "Downloading finished" : "Downloading failed");
/*     */               }
/*     */             });
/* 255 */             ExportDataAction.access$002(ExportDataAction.this, allDataLoaded);
/*     */           } catch (Exception e) {
/* 257 */             ExportDataAction.LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         }
/*     */ 
/*     */         public boolean stopJob() {
/* 262 */           return (ExportDataAction.this.testerPanel.dataLoadingCancelRequested()) || (!ExportDataAction.this.loadData);
/*     */         } } ;
/*     */       try {
/* 266 */         feedDataProvider.loadTicksDataSynched(this.instrument, this.from, this.to, liveFeedListener, loadingProgressListener);
/* 267 */         if (!this.loadData)
/* 268 */           SwingUtilities.invokeLater(new Runnable() {
/*     */             public void run() {
/* 270 */               JOptionPane.showMessageDialog(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.save.data", new Object[] { "system error" }), LocalizationManager.getText("joption.pane.error"), 1);
/*     */             } } );
/*     */       }
/*     */       catch (Exception e) {
/* 275 */         LOGGER.error(e.getMessage(), e);
/* 276 */         SwingUtilities.invokeLater(new Runnable(e) {
/*     */           public void run() {
/* 278 */             JOptionPane.showMessageDialog(null, LocalizationManager.getTextWithArguments("joption.pane.can.not.save.data", new Object[] { this.val$e.getMessage() }), LocalizationManager.getText("joption.pane.error"), 1);
/*     */           } } );
/*     */       }
/*     */     } finally {
/*     */       try {
/* 284 */         out.close();
/*     */       } catch (IOException e) {
/* 286 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/* 293 */     this.testerPanel.unlockGUI();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ExportDataAction
 * JD-Core Version:    0.6.0
 */