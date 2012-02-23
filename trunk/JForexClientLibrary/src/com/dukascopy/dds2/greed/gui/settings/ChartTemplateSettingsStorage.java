/*     */ package com.dukascopy.dds2.greed.gui.settings;
/*     */ 
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.impl.IndicatorContext;
/*     */ import com.dukascopy.api.impl.IndicatorHolder;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler;
/*     */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.IndicatorBean;
/*     */ import com.dukascopy.charts.persistence.ThemeManager;
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.AbstractTesterIndicator;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterIndicatorWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.file.filter.TemplateFileFilter;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.utils.FastByteArrayOutputStream;
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.datafeed.Location;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.List;
/*     */ import java.util.prefs.BackingStoreException;
/*     */ import java.util.prefs.InvalidPreferencesFormatException;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ChartTemplateSettingsStorage
/*     */   implements IChartTemplateSettingsStorage
/*     */ {
/*  60 */   private static Logger LOGGER = LoggerFactory.getLogger(ChartTemplateSettingsStorage.class);
/*     */   private PreferencesStorage preferencesStorage;
/*     */   private Preferences chartTemplateRoot;
/*     */ 
/*     */   private PreferencesStorage getPreferencesStorage()
/*     */   {
/*  66 */     if (this.preferencesStorage == null) {
/*  67 */       this.preferencesStorage = new PreferencesStorage();
/*     */     }
/*  69 */     return this.preferencesStorage;
/*     */   }
/*     */ 
/*     */   private Preferences getChartTemplateRootNode() {
/*  73 */     if (this.chartTemplateRoot == null) {
/*  74 */       this.chartTemplateRoot = JForexPreferences.systemRoot.node("ChartTemplate");
/*     */     }
/*  76 */     return this.chartTemplateRoot;
/*     */   }
/*     */ 
/*     */   private Preferences getChartNode(int chartId) {
/*  80 */     return getChartTemplateRootNode().node(String.valueOf(chartId));
/*     */   }
/*     */ 
/*     */   private void saveChartTemplate(OutputStream os, ChartBean chartBean, List<IChartObject> mainChartDrawings, List<IndicatorBean> indicatorsBeans) {
/*  84 */     Preferences prefs = getChartTemplateRootNode();
/*  85 */     cleanUpNode(prefs);
/*     */ 
/*  88 */     Preferences chartNode = getChartNode(chartBean.getId());
/*     */ 
/*  90 */     getPreferencesStorage().saveChartBean(chartBean, prefs, chartNode);
/*  91 */     getPreferencesStorage().saveChartObjects(mainChartDrawings, chartNode);
/*  92 */     getPreferencesStorage().saveIndicatorBeans(indicatorsBeans, chartNode);
/*     */ 
/*  96 */     ITheme theme = chartBean.getTheme();
/*  97 */     if ((theme != null) && (!ThemeManager.isDefault(theme.getName()))) {
/*  98 */       getPreferencesStorage().saveTheme(theme, chartNode);
/*     */     }
/*     */ 
/* 102 */     saveSettings(os, prefs);
/*     */   }
/*     */ 
/*     */   private void saveChartTemplate(OutputStream os, int chartId) {
/* 106 */     if (os == null) {
/* 107 */       return;
/*     */     }
/*     */ 
/* 110 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 111 */     IChart chart = ddsChartsController.getIChartBy(Integer.valueOf(chartId));
/*     */ 
/* 113 */     if (chart == null) {
/* 114 */       return;
/*     */     }
/*     */ 
/* 117 */     ChartBean chartBean = ddsChartsController.synchronizeAndGetChartBean(Integer.valueOf(chartId));
/*     */ 
/* 119 */     List mainChartDrawings = ddsChartsController.getMainChartDrawings(Integer.valueOf(chartId));
/* 120 */     List indicators = ddsChartsController.getIndicators(Integer.valueOf(chartId));
/* 121 */     List indicatorsBeans = DDSChartsActionAdapter.convertToIndicatorBeans(indicators);
/* 122 */     if (indicatorsBeans != null) {
/* 123 */       for (IndicatorBean indicatorBean : indicatorsBeans) {
/* 124 */         List subChartDrawings = ddsChartsController.getSubChartDrawings(Integer.valueOf(chartId), indicatorBean.getSubPanelId().intValue(), indicatorBean.getId().intValue());
/* 125 */         indicatorBean.setChartObjects(subChartDrawings);
/*     */       }
/*     */     }
/*     */ 
/* 129 */     saveChartTemplate(os, chartBean, mainChartDrawings, indicatorsBeans);
/*     */   }
/*     */ 
/*     */   public void saveChartTemplate(File file, int chartId)
/*     */   {
/* 134 */     if (file == null) {
/* 135 */       return;
/*     */     }
/* 137 */     FileOutputStream fos = null;
/*     */     try {
/* 139 */       fos = new FileOutputStream(file);
/* 140 */       saveChartTemplate(fos, chartId);
/*     */     } catch (FileNotFoundException e) {
/* 142 */       LOGGER.error("Failed to create File Output Stream - " + file.getPath(), e);
/*     */     } finally {
/* 144 */       closeOutputStream(fos);
/*     */     }
/*     */   }
/*     */ 
/*     */   private ChartBean loadChartTemplate(InputStream is) {
/* 149 */     if (is == null) {
/* 150 */       return null;
/*     */     }
/*     */ 
/* 153 */     Preferences prefs = getChartTemplateRootNode();
/*     */ 
/* 155 */     cleanUpNode(prefs);
/* 156 */     loadSettings(is, prefs);
/*     */ 
/* 158 */     List chartBeans = getPreferencesStorage().getChartBeans(prefs);
/* 159 */     ChartBean chartBean = chartBeans.size() > 0 ? (ChartBean)chartBeans.get(0) : null;
/*     */ 
/* 161 */     if (chartBean == null) {
/* 162 */       return null;
/*     */     }
/*     */ 
/* 165 */     int chartId = chartBean.getId();
/* 166 */     Preferences chartNode = getChartNode(chartId);
/*     */ 
/* 168 */     List chartObjects = getPreferencesStorage().getDrawingsFor(chartNode);
/* 169 */     List indicatorWrappers = getPreferencesStorage().getIndicatorWrappers(chartNode);
/*     */ 
/* 171 */     chartBean.setChartObjects(chartObjects);
/* 172 */     chartBean.setReadOnly(GreedContext.isReadOnly());
/* 173 */     chartBean.setIndicatorWrappers(indicatorWrappers);
/*     */ 
/* 176 */     List themesList = getPreferencesStorage().loadThemes(chartNode);
/* 177 */     if (!themesList.isEmpty()) {
/* 178 */       if (themesList.size() == 1) {
/* 179 */         ITheme theme = (ITheme)themesList.get(0);
/* 180 */         if (ThemeManager.isExist(theme.getName())) {
/* 181 */           ITheme existedTheme = ThemeManager.getTheme(theme.getName());
/* 182 */           if (!existedTheme.equals(theme)) {
/* 183 */             String themeName = theme.getName();
/*     */             do
/* 185 */               themeName = themeName + " *";
/* 186 */             while (ThemeManager.isExist(themeName));
/*     */ 
/* 188 */             theme.setName(themeName);
/*     */ 
/* 190 */             ThemeManager.add(theme);
/*     */ 
/* 192 */             chartBean.setTheme(theme);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 197 */           ThemeManager.add(theme);
/*     */ 
/* 199 */           chartBean.setTheme(theme);
/*     */         }
/*     */       }
/*     */       else {
/* 203 */         throw new IllegalStateException("More than 1 theme in template");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 210 */     return chartBean;
/*     */   }
/*     */ 
/*     */   public ChartBean loadChartTemplate(File file)
/*     */   {
/* 215 */     if ((file == null) || (!file.exists())) {
/* 216 */       return null;
/*     */     }
/*     */ 
/* 219 */     ChartBean chartBean = null;
/* 220 */     FileInputStream fis = null;
/*     */     try
/*     */     {
/* 223 */       fis = new FileInputStream(file);
/* 224 */       chartBean = loadChartTemplate(fis);
/*     */     } catch (FileNotFoundException e) {
/* 226 */       LOGGER.error("Failed to create File Input Stream - " + file.getPath(), e);
/*     */     } finally {
/* 228 */       closeInputStream(fis);
/*     */     }
/*     */ 
/* 231 */     return chartBean;
/*     */   }
/*     */ 
/*     */   public ChartBean cloneChart(int selectedPanelId)
/*     */   {
/* 236 */     ChartBean newChartBean = null;
/*     */     try
/*     */     {
/* 239 */       DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 240 */       ChartBean oldChartBean = ddsChartsController.synchronizeAndGetChartBean(Integer.valueOf(selectedPanelId));
/*     */ 
/* 242 */       List oldIndicators = ddsChartsController.getIndicators(Integer.valueOf(selectedPanelId));
/*     */ 
/* 244 */       FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
/* 245 */       ObjectOutputStream out = new ObjectOutputStream(fbos);
/* 246 */       saveChartTemplate(out, selectedPanelId);
/* 247 */       closeOutputStream(out);
/*     */ 
/* 249 */       ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
/* 250 */       newChartBean = loadChartTemplate(in);
/* 251 */       closeInputStream(in);
/*     */ 
/* 253 */       if (oldChartBean != null) {
/* 254 */         newChartBean.setFeedDataProvider(oldChartBean.getFeedDataProvider());
/*     */       }
/* 256 */       if (oldIndicators != null)
/* 257 */         for (IndicatorWrapper wrapper : oldIndicators)
/*     */         {
/* 260 */           if ((wrapper instanceof TesterIndicatorWrapper)) {
/* 261 */             AbstractTesterIndicator indicator = ((TesterIndicatorWrapper)wrapper).getIndicator();
/* 262 */             IndicatorContext ctx = ((TesterIndicatorWrapper)wrapper).getIndicatorHolder().getIndicatorContext();
/* 263 */             TesterIndicatorWrapper copy = new TesterIndicatorWrapper(indicator.clone(), ctx);
/*     */ 
/* 265 */             copy.copySettingsFrom(wrapper);
/* 266 */             copy.setSubPanelId(Integer.valueOf(-1));
/* 267 */             newChartBean.addIndicatorWrapper(copy);
/*     */           }
/*     */         }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 273 */       LOGGER.error("Exception while cloning - ", e);
/*     */     }
/*     */ 
/* 276 */     return newChartBean;
/*     */   }
/*     */ 
/*     */   public ChartBean cloneChartBean(ChartBean chartBean)
/*     */   {
/* 281 */     if (chartBean == null) {
/* 282 */       return null;
/*     */     }
/*     */ 
/* 285 */     ChartBean clone = null;
/*     */     try
/*     */     {
/* 288 */       FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
/* 289 */       ObjectOutputStream out = new ObjectOutputStream(fbos);
/* 290 */       saveChartTemplate(out, chartBean, chartBean.getChartObjects(), DDSChartsActionAdapter.convertToIndicatorBeans(chartBean.getIndicatorWrappers()));
/* 291 */       closeOutputStream(out);
/*     */ 
/* 293 */       ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
/* 294 */       clone = loadChartTemplate(in);
/* 295 */       closeInputStream(in);
/*     */     }
/*     */     catch (Throwable e) {
/* 298 */       LOGGER.error("Exception while cloning - ", e);
/*     */     }
/*     */ 
/* 301 */     return clone;
/*     */   }
/*     */ 
/*     */   public void cleanUpChartTemplateRootNode()
/*     */   {
/* 306 */     cleanUpNode(getChartTemplateRootNode());
/*     */   }
/*     */ 
/*     */   private void cleanUpNode(Preferences prefs) {
/*     */     try {
/* 311 */       prefs.clear();
/* 312 */       String[] childNames = prefs.childrenNames();
/* 313 */       if (childNames != null)
/* 314 */         for (String childName : childNames) {
/* 315 */           Preferences childNode = prefs.node(childName);
/* 316 */           childNode.removeNode();
/*     */         }
/*     */     }
/*     */     catch (BackingStoreException e) {
/* 320 */       LOGGER.error("Failed to clear settings - ", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void printSettings(Preferences preferences) {
/*     */     try {
/* 326 */       preferences.exportSubtree(System.out);
/*     */     } catch (IOException e) {
/* 328 */       e.printStackTrace();
/*     */     } catch (BackingStoreException e) {
/* 330 */       e.printStackTrace();
/*     */     }
/*     */     finally {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void saveSettings(OutputStream os, Preferences preferences) {
/* 337 */     if (os == null) {
/* 338 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 342 */       preferences.exportSubtree(os);
/*     */     } catch (IOException e) {
/* 344 */       LOGGER.error("Failed to save settings into input stream - " + os, e);
/*     */     } catch (BackingStoreException e) {
/* 346 */       LOGGER.error("Failed to save settings - ", e);
/*     */     } finally {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void loadSettings(InputStream is, Preferences preferences) {
/* 352 */     if (is == null) {
/* 353 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 357 */       Preferences.importPreferences(is);
/* 358 */       LOGGER.info("Settings successfully loaded from input stream: " + is);
/*     */     } catch (InvalidPreferencesFormatException e) {
/* 360 */       LOGGER.info("Settings import error: " + e);
/*     */     } catch (IOException e) {
/* 362 */       LOGGER.info("Input Straem exception: " + e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void closeOutputStream(OutputStream os) {
/*     */     try {
/* 368 */       os.flush();
/* 369 */       os.close();
/*     */     } catch (Throwable e) {
/* 371 */       LOGGER.error("Failed to close File Output Stream - ", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void closeInputStream(InputStream is) {
/*     */     try {
/* 377 */       is.close();
/*     */     } catch (Throwable e) {
/* 379 */       LOGGER.error("Failed to close File Input Stream - ", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private <T> T clone(T original) {
/* 384 */     if (original == null) {
/* 385 */       return null;
/*     */     }
/*     */ 
/* 388 */     Object clone = null;
/*     */     try {
/* 390 */       FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
/* 391 */       ObjectOutputStream out = new ObjectOutputStream(fbos);
/* 392 */       out.writeObject(original);
/* 393 */       closeOutputStream(out);
/*     */ 
/* 395 */       ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
/* 396 */       clone = in.readObject();
/* 397 */       closeInputStream(in);
/*     */     }
/*     */     catch (IOException e) {
/* 400 */       LOGGER.error("Failed to clone original object - ", e);
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/* 403 */       LOGGER.error("Failed to clone original object - ", e);
/*     */     }
/* 405 */     return clone;
/*     */   }
/*     */ 
/*     */   public ChartBean openChartTemplate()
/*     */   {
/* 411 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 412 */     TemplateFileFilter templateFileFilter = new TemplateFileFilter(LocalizationManager.getText("jforex.chart.template.files"));
/* 413 */     JFileChooser chooser = DCFileChooser.createFileChooser(clientSettingsStorage.getMyChartTemplatesPath(), null, templateFileFilter, 0);
/*     */ 
/* 415 */     ChooserSelectionWrapper selection = TransportFileChooser.showOpenDialog(FileType.CHART, chooser, (JFrame)GreedContext.get("clientGui"), Boolean.valueOf(false), GreedContext.CLIENT_MODE);
/*     */ 
/* 417 */     ChartBean chartBean = null;
/*     */ 
/* 419 */     if (selection != null)
/*     */     {
/* 421 */       Location loc = selection.getLocation();
/* 422 */       FileItem f = selection.getFileItem();
/*     */ 
/* 424 */       String fileName = f.getFileName();
/*     */ 
/* 426 */       if (loc == Location.LOCAL) {
/* 427 */         chartBean = loadChartTemplate(new File(fileName));
/*     */       }
/*     */       else {
/* 430 */         byte[] data = null;
/*     */         try
/*     */         {
/* 433 */           data = FeedDataProvider.getCurvesProtocolHandler().downloadFile(f.getFileId().longValue(), new FileProgressListener()).getFileData();
/*     */         } catch (Exception e) {
/* 435 */           LOGGER.error("Error retrieving remote chart template data: " + f.getFileName());
/* 436 */           return null;
/*     */         }
/*     */ 
/* 439 */         chartBean = loadChartTemplate(data);
/*     */       }
/*     */ 
/* 442 */       if (chartBean == null) {
/* 443 */         String errorMessage = LocalizationManager.getText("jforex.chart.template.file") + " '" + f.getFileName() + "' " + LocalizationManager.getText("is.corrupted.or.has.wrong.format");
/*     */ 
/* 445 */         LocalizedMessageHelper.showErrorMessage(null, errorMessage);
/* 446 */         return null;
/*     */       }
/*     */ 
/* 449 */       return chartBean;
/*     */     }
/*     */ 
/* 452 */     return null;
/*     */   }
/*     */ 
/*     */   public void saveChartTemplate(int chartId)
/*     */   {
/* 458 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 459 */     TemplateFileFilter templateFileFilter = new TemplateFileFilter(LocalizationManager.getText("jforex.chart.template.files"));
/* 460 */     JFileChooser chooser = DCFileChooser.createFileChooser(clientSettingsStorage.getMyChartTemplatesPath(), null, templateFileFilter, 0);
/*     */ 
/* 462 */     ChooserSelectionWrapper selection = TransportFileChooser.showSaveDialog(FileType.CHART, chooser, (JFrame)GreedContext.get("clientGui"), "");
/*     */ 
/* 464 */     if (selection != null)
/*     */     {
/* 466 */       Location loc = selection.getLocation();
/* 467 */       FileItem f = selection.getFileItem();
/*     */ 
/* 469 */       String fileName = f.getFileName();
/*     */ 
/* 471 */       String extension = StratUtils.getExtension(fileName);
/*     */ 
/* 473 */       if ((extension == null) || (!extension.equals(templateFileFilter.getExtension()))) {
/* 474 */         fileName = fileName + "." + templateFileFilter.getExtension();
/*     */       }
/*     */ 
/* 477 */       f.setFileName(fileName);
/*     */ 
/* 480 */       if (loc == Location.LOCAL) {
/* 481 */         saveChartTemplate(new File(fileName), chartId);
/*     */       }
/*     */       else {
/* 484 */         Preferences prefs = getChartAsPrefs(chartId);
/*     */ 
/* 486 */         ByteArrayOutputStream os = null;
/*     */         try {
/* 488 */           os = new ByteArrayOutputStream();
/* 489 */           prefs.exportSubtree(os);
/*     */ 
/* 491 */           f.setFileData(os.toByteArray());
/*     */ 
/* 493 */           FeedDataProvider.getCurvesProtocolHandler().uploadFile(f, GreedContext.CLIENT_MODE, new FileProgressListener());
/*     */         } catch (Exception e) {
/* 495 */           LOGGER.error("Error saving chart template in remote storage: " + fileName);
/*     */         } finally {
/* 497 */           IOUtils.closeQuietly(os);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Preferences getChartAsPrefs(int chartId)
/*     */   {
/* 506 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 507 */     IChart chart = ddsChartsController.getIChartBy(Integer.valueOf(chartId));
/*     */ 
/* 509 */     if (chart == null) {
/* 510 */       return null;
/*     */     }
/*     */ 
/* 513 */     ChartBean chartBean = ddsChartsController.synchronizeAndGetChartBean(Integer.valueOf(chartId));
/*     */ 
/* 515 */     List mainChartDrawings = ddsChartsController.getMainChartDrawings(Integer.valueOf(chartId));
/* 516 */     List indicators = ddsChartsController.getIndicators(Integer.valueOf(chartId));
/* 517 */     List indicatorsBeans = DDSChartsActionAdapter.convertToIndicatorBeans(indicators);
/* 518 */     if (indicatorsBeans != null) {
/* 519 */       for (IndicatorBean indicatorBean : indicatorsBeans) {
/* 520 */         List subChartDrawings = ddsChartsController.getSubChartDrawings(Integer.valueOf(chartId), indicatorBean.getSubPanelId().intValue(), indicatorBean.getId().intValue());
/* 521 */         indicatorBean.setChartObjects(subChartDrawings);
/*     */       }
/*     */     }
/*     */ 
/* 525 */     Preferences prefs = getChartTemplateRootNode();
/* 526 */     cleanUpNode(prefs);
/*     */ 
/* 528 */     Preferences chartNode = getChartNode(chartBean.getId());
/*     */ 
/* 530 */     getPreferencesStorage().saveChartBean(chartBean, prefs, chartNode);
/* 531 */     getPreferencesStorage().saveChartObjects(mainChartDrawings, chartNode);
/* 532 */     getPreferencesStorage().saveIndicatorBeans(indicatorsBeans, chartNode);
/*     */ 
/* 534 */     return chartNode;
/*     */   }
/*     */ 
/*     */   private ChartBean loadChartTemplate(byte[] data) {
/* 538 */     if (data == null) {
/* 539 */       return null;
/*     */     }
/*     */ 
/* 542 */     ByteArrayInputStream is = null;
/*     */     try
/*     */     {
/* 545 */       is = new ByteArrayInputStream(data);
/*     */ 
/* 547 */       ChartBean chartBean = loadChartTemplate(is);
/*     */ 
/* 549 */       ChartBean localChartBean1 = chartBean;
/*     */       return localChartBean1; } finally { IOUtils.closeQuietly(is); } throw localObject;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.ChartTemplateSettingsStorage
 * JD-Core Version:    0.6.0
 */