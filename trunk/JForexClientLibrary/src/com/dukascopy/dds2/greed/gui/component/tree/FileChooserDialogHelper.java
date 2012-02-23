/*     */ package com.dukascopy.dds2.greed.gui.component.tree;
/*     */ 
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceLanguage;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.IncorrectClassTypeException;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.datafeed.Location;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class FileChooserDialogHelper
/*     */ {
/*  49 */   private static final Logger LOGGER = LoggerFactory.getLogger(FileChooserDialogHelper.class);
/*     */ 
/*     */   public static List<StrategyWrapper> openStrategyFileChooser(ServiceSourceLanguage type, boolean isSourceOnly)
/*     */   {
/*  54 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/*  56 */     String lastOpenedPath = (String)GreedContext.get("lastOpenedStrategiesDirectoryPath");
/*  57 */     if (lastOpenedPath == null) {
/*  58 */       lastOpenedPath = storage.getMyStrategiesPath();
/*     */     }
/*  60 */     JFileChooser fileChooser = new JFileChooser(lastOpenedPath);
/*     */ 
/*  62 */     fileChooser.setFileSelectionMode(0);
/*  63 */     fileChooser.setMultiSelectionEnabled(true);
/*     */ 
/*  65 */     fileChooser.addChoosableFileFilter(new FileFilter(type, isSourceOnly) {
/*     */       public boolean accept(File dir) {
/*  67 */         if (this.val$type.equals(ServiceSourceLanguage.MQ4))
/*  68 */           return (dir.getName().endsWith(".mq4")) || (dir.isDirectory());
/*  69 */         if (this.val$type.equals(ServiceSourceLanguage.MQ5))
/*  70 */           return (dir.getName().endsWith(".mq5")) || (dir.isDirectory());
/*  71 */         if (this.val$type.equals(ServiceSourceLanguage.JFX))
/*  72 */           return (dir.getName().endsWith(".jfx")) || (dir.isDirectory());
/*  73 */         if (this.val$type.equals(ServiceSourceLanguage.JAVA))
/*  74 */           return (dir.getName().endsWith(".java")) || (dir.getName().endsWith(".jfx")) || (dir.isDirectory());
/*  75 */         if ((this.val$type.equals(ServiceSourceLanguage.ALL)) && (this.val$isSourceOnly)) {
/*  76 */           return (dir.getName().endsWith(".java")) || (dir.getName().endsWith(".mq4")) || (dir.getName().endsWith(".mq5")) || (dir.getName().endsWith(".c")) || (dir.getName().endsWith(".cpp")) || (dir.isDirectory());
/*     */         }
/*     */ 
/*  83 */         return (dir.getName().endsWith(".java")) || (dir.getName().endsWith(".jfx")) || (dir.getName().endsWith(".mq4")) || (dir.getName().endsWith(".mq5")) || (dir.getName().endsWith(".c")) || (dir.getName().endsWith(".h")) || (dir.getName().endsWith(".cpp")) || (dir.getName().endsWith(".hpp")) || (dir.isDirectory());
/*     */       }
/*     */ 
/*     */       public String getDescription()
/*     */       {
/*  96 */         if (this.val$type.equals(ServiceSourceLanguage.MQ4))
/*  97 */           return "MetaTrader 4 strategy";
/*  98 */         if (this.val$type.equals(ServiceSourceLanguage.MQ5))
/*  99 */           return "MetaTrader 5 strategy";
/* 100 */         if ((this.val$type.equals(ServiceSourceLanguage.JAVA)) || (this.val$type.equals(ServiceSourceLanguage.JFX))) {
/* 101 */           return "Dukascopy strategy";
/*     */         }
/* 103 */         return "All strategies";
/*     */       }
/*     */     });
/* 107 */     ChooserSelectionWrapper selection = TransportFileChooser.showOpenDialog(FileType.STRATEGY, fileChooser, (JFrame)GreedContext.get("clientGui"), Boolean.valueOf(true), GreedContext.CLIENT_MODE);
/*     */ 
/* 109 */     List files = new ArrayList();
/*     */ 
/* 111 */     if (selection != null)
/*     */     {
/* 113 */       Location location = selection.getLocation();
/* 114 */       List fileItems = selection.getFileItems();
/*     */ 
/* 116 */       if (location == Location.LOCAL) {
/* 117 */         files = Arrays.asList(fileChooser.getSelectedFiles());
/*     */       } else {
/* 119 */         List strategyWrappers = new ArrayList();
/* 120 */         for (FileItem fileItem : fileItems) {
/* 121 */           StrategyWrapper strategy = getRemoteStrategy(fileItem, lastOpenedPath);
/* 122 */           if (strategy != null) {
/* 123 */             strategyWrappers.add(strategy);
/*     */           }
/*     */         }
/* 126 */         return strategyWrappers;
/*     */       }
/*     */     }
/*     */ 
/* 130 */     if ((files != null) && (files.size() >= 10) && 
/* 131 */       (!conformChosenFileCount(files.size()))) {
/* 132 */       return null;
/*     */     }
/*     */ 
/* 135 */     List strategyWrappers = new ArrayList();
/* 136 */     if (files != null)
/*     */     {
/* 138 */       for (File file : files) {
/* 139 */         GreedContext.putInSingleton("lastOpenedStrategiesDirectoryPath", getDefaultPath(file));
/*     */ 
/* 141 */         StrategyWrapper rc = new StrategyWrapper();
/* 142 */         StrategyHelper.setStrategyFile(rc, file);
/* 143 */         strategyWrappers.add(rc);
/*     */       }
/*     */     }
/* 146 */     return strategyWrappers;
/*     */   }
/*     */ 
/*     */   private static StrategyWrapper getRemoteStrategy(FileItem item, String lastOpenedPath)
/*     */   {
/* 152 */     String name = item.getFileName();
/*     */ 
/* 154 */     RemoteStrategyWrapper wrapper = new RemoteStrategyWrapper();
/* 155 */     wrapper.setRemoteStrategyId(item.getFileId());
/* 156 */     wrapper.setRemoteRunAllowed(item.isRemoteRunAllowed());
/* 157 */     wrapper.setName(name);
/* 158 */     wrapper.setHaveAnnotations(item.isParametersDefined());
/*     */ 
/* 160 */     switch (3.$SwitchMap$com$dukascopy$transport$common$msg$strategy$FileItem$AccessType[item.getAccessType().ordinal()])
/*     */     {
/*     */     case 1:
/* 163 */       wrapper.setStrategyType(RemoteStrategyType.PUBLIC);
/*     */ 
/* 165 */       File directory = new File(lastOpenedPath);
/* 166 */       if (directory.isFile()) {
/* 167 */         directory = directory.getAbsoluteFile().getParentFile();
/*     */       }
/*     */ 
/* 170 */       File strategyFile = new File(directory, name);
/* 171 */       while (strategyFile.exists()) {
/* 172 */         String newName = JOptionPane.showInputDialog(LocalizationManager.getTextWithArguments("choose.another.name", new Object[] { strategyFile.getName(), strategyFile.getParent() }), strategyFile.getName());
/* 173 */         if (newName != null)
/* 174 */           strategyFile = new File(directory, newName);
/*     */         else {
/* 176 */           return null;
/*     */         }
/*     */       }
/*     */ 
/* 180 */       StrategyHelper.downloadStrategy(wrapper, strategyFile);
/* 181 */       return wrapper;
/*     */     case 2:
/* 186 */       wrapper.setStrategyType(RemoteStrategyType.PRIVATE);
/*     */       try
/*     */       {
/* 189 */         File strategyFile = StrategyHelper.createTempFile(item.getFileName());
/* 190 */         StrategyHelper.downloadStrategy(wrapper, strategyFile);
/*     */       } catch (IOException ex) {
/* 192 */         LOGGER.error("Error creating temporary directory.", ex);
/*     */       }
/* 194 */       return wrapper;
/*     */     case 3:
/* 200 */       wrapper.setStrategyType(RemoteStrategyType.PROTECTED);
/* 201 */       return wrapper;
/*     */     case 4:
/* 207 */       wrapper.setStrategyType(RemoteStrategyType.REMOTE);
/* 208 */       return wrapper;
/*     */     }
/*     */ 
/* 212 */     LOGGER.warn("Unknown access type: " + item.getFileType());
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */   private static String getDefaultPath(File tmp)
/*     */   {
/*     */     try
/*     */     {
/* 220 */       return tmp.getCanonicalPath();
/*     */     } catch (IOException e) {
/* 222 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 224 */     return null;
/*     */   }
/*     */ 
/*     */   public static CustIndicatorWrapper openCustIndFileChooser() throws IncorrectClassTypeException {
/* 228 */     CustIndicatorWrapper rc = null;
/*     */ 
/* 230 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 231 */     String indicatorsPath = storage.getMyIndicatorsPath();
/*     */ 
/* 233 */     JFileChooser fileChooser = new JFileChooser(new File(indicatorsPath));
/* 234 */     fileChooser.setFileSelectionMode(0);
/*     */ 
/* 236 */     fileChooser.addChoosableFileFilter(new FileFilter() {
/*     */       public boolean accept(File dir) {
/* 238 */         return (dir.getName().endsWith(".java")) || (dir.getName().endsWith(".mq4")) || (dir.getName().endsWith(".jfx")) || (dir.isDirectory());
/*     */       }
/*     */ 
/*     */       public String getDescription()
/*     */       {
/* 243 */         return "Dukascopy indicator";
/*     */       }
/*     */     });
/* 247 */     ChooserSelectionWrapper selection = TransportFileChooser.showOpenDialog(FileType.INDICATOR, fileChooser, (JFrame)GreedContext.get("clientGui"), Boolean.valueOf(false), GreedContext.CLIENT_MODE);
/*     */ 
/* 250 */     if (selection != null)
/*     */     {
/* 252 */       Location location = selection.getLocation();
/* 253 */       FileItem fileItem = selection.getFileItem();
/*     */ 
/* 255 */       if (location == Location.LOCAL) {
/* 256 */         File tmp = fileChooser.getSelectedFile();
/*     */ 
/* 258 */         if ((tmp != null) && (tmp.exists())) {
/* 259 */           rc = new CustIndicatorWrapper();
/* 260 */           if ((tmp.getName().endsWith(".java")) || (tmp.getName().endsWith(".mq4"))) {
/* 261 */             rc.setSourceFile(tmp);
/* 262 */           } else if (tmp.getName().endsWith(".jfx"))
/*     */           {
/* 264 */             JFXPack pack = null;
/*     */             try {
/* 266 */               pack = JFXPack.loadFromPack(tmp);
/*     */             } catch (Exception ex) {
/* 268 */               LOGGER.error(ex.getMessage(), ex);
/* 269 */               return null;
/*     */             }
/*     */ 
/* 272 */             if (!IIndicator.class.isAssignableFrom(pack.getTargetClass())) {
/* 273 */               throw new IncorrectClassTypeException("File is not implementing IIndicator interface.");
/*     */             }
/*     */ 
/* 276 */             rc.setBinaryFile(tmp);
/*     */           }
/*     */         }
/*     */       } else {
/* 280 */         String fileName = fileItem.getFileName();
/*     */ 
/* 282 */         File indicator = new File(indicatorsPath, fileName);
/*     */ 
/* 284 */         boolean continueLoop = true;
/*     */         do
/* 286 */           if (indicator.exists())
/*     */           {
/* 288 */             String newName = JOptionPane.showInputDialog(LocalizationManager.getTextWithArguments("choose.another.name", new Object[] { indicator.getName(), indicator.getParent() }), indicator.getName());
/*     */ 
/* 290 */             indicator = new File(indicatorsPath, newName);
/*     */           }
/*     */           else {
/* 293 */             continueLoop = false;
/*     */           }
/* 295 */         while (continueLoop);
/*     */ 
/* 297 */         byte[] data = null;
/*     */         try
/*     */         {
/* 300 */           data = FeedDataProvider.getCurvesProtocolHandler().downloadFile(fileItem.getFileId().longValue(), new FileProgressListener()).getFileData();
/*     */         } catch (Exception e) {
/* 302 */           LOGGER.error("Error retrieving remote indicator data: " + fileName);
/*     */         }
/*     */         try
/*     */         {
/* 306 */           IOUtils.writeByteArrayToFile(indicator, data);
/*     */         } catch (IOException e) {
/* 308 */           LOGGER.error("Error saving remote workspace data to local file: " + indicator.getAbsolutePath(), e);
/*     */         }
/*     */ 
/* 311 */         rc = new CustIndicatorWrapper();
/* 312 */         if ((fileName.endsWith(".java")) || (fileName.endsWith(".mq4")))
/* 313 */           rc.setSourceFile(indicator);
/* 314 */         else if (fileName.endsWith(".jfx")) {
/* 315 */           rc.setBinaryFile(indicator);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 320 */     return rc;
/*     */   }
/*     */ 
/*     */   public static File findOrCreateFileFor(String type)
/*     */   {
/*     */     File newFile;
/*     */     try {
/* 328 */       byte[] data = StratUtils.loadResource("rc/media/new" + type + ".jfs");
/* 329 */       String content = new String(data, "UTF-8");
/*     */ 
/* 331 */       newFile = File.createTempFile(type, ".java");
/* 332 */       newFile.deleteOnExit();
/*     */ 
/* 334 */       FileWriter to = new FileWriter(newFile);
/* 335 */       to.write(content);
/* 336 */       to.close();
/*     */     } catch (UnsupportedEncodingException e) {
/* 338 */       LOGGER.error(e.getMessage(), e);
/* 339 */       return null;
/*     */     } catch (IOException e) {
/* 341 */       LOGGER.error(e.getMessage(), e);
/* 342 */       return null;
/*     */     }
/*     */ 
/* 345 */     return newFile;
/*     */   }
/*     */ 
/*     */   public static File createEmptyFile(String extension) {
/*     */     File newFile;
/*     */     try {
/* 352 */       newFile = File.createTempFile("str", "." + extension);
/* 353 */       newFile.deleteOnExit();
/*     */ 
/* 355 */       FileWriter to = new FileWriter(newFile);
/* 356 */       to.write(" ");
/* 357 */       to.close();
/*     */     } catch (UnsupportedEncodingException e) {
/* 359 */       LOGGER.error(e.getMessage(), e);
/* 360 */       return null;
/*     */     } catch (IOException e) {
/* 362 */       LOGGER.error(e.getMessage(), e);
/* 363 */       return null;
/*     */     }
/* 365 */     return newFile;
/*     */   }
/*     */ 
/*     */   private static boolean conformChosenFileCount(int count)
/*     */   {
/* 370 */     int reply = JOptionPane.showConfirmDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getTextWithArguments("joption.pane.conform.file.cnt", new Object[] { Integer.valueOf(count) }), LocalizationManager.getText("joption.pane.conform.file.cnt.title"), 0, 2);
/*     */ 
/* 378 */     if (0 == reply) {
/* 379 */       return true;
/*     */     }
/*     */ 
/* 382 */     return 1 != reply;
/*     */   }
/*     */ 
/*     */   public static File adaptJavaClassFileName(File file)
/*     */   {
/* 389 */     Pattern pattern = Pattern.compile("\\W[^\\.mq4|\\.java|\\.mq4|\\.cpp|\\.c|\\.hpp|\\.h]", 0);
/*     */ 
/* 391 */     if (pattern.matcher(file.getName()).find()) {
/* 392 */       String newFileName = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\") + 1) + pattern.matcher(file.getName()).replaceAll("_");
/*     */ 
/* 394 */       File newFile = new File(newFileName);
/*     */ 
/* 396 */       if ((!newFile.exists()) && (file.renameTo(newFile)))
/* 397 */         file = newFile;
/*     */       else {
/* 399 */         NotificationUtils.getInstance().postWarningMessage(LocalizationManager.getTextWithArguments("file.exists.error", new Object[] { file.getAbsolutePath(), newFile.getName() }));
/*     */       }
/*     */     }
/* 402 */     return file;
/*     */   }
/*     */ 
/*     */   public static boolean conformFileSaving(ServiceWrapper serviceWrapper) {
/* 406 */     if ((serviceWrapper.isNewUnsaved()) || (serviceWrapper.isModified())) {
/* 407 */       int reply = JOptionPane.showConfirmDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getTextWithArguments("joption.pane.want.save", new Object[] { serviceWrapper.getName().substring(1) }), LocalizationManager.getText("joption.pane.want.save.title"), 1, 2);
/*     */ 
/* 414 */       if (0 == reply)
/*     */       {
/* 416 */         ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController().getEditorPanel(serviceWrapper).save();
/*     */ 
/* 422 */         return true;
/*     */       }
/* 424 */       if (1 == reply) {
/* 425 */         serviceWrapper.setIsModified(false);
/* 426 */         return true;
/*     */       }
/* 428 */       if (2 == reply) {
/* 429 */         return false;
/*     */       }
/*     */     }
/* 432 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.FileChooserDialogHelper
 * JD-Core Version:    0.6.0
 */