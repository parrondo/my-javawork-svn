/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Random;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ 
/*     */ public class FilePathManager
/*     */ {
/*     */   private static FilePathManager instance;
/*  18 */   public String WORKSPACE_LOCK_FILE_NAME = "workspace.lck";
/*  19 */   public String DEFAULT_WORKSPACE_FILE_NAME = "Workspace.xml";
/*  20 */   public String SYSTEM_SETTING_FILE_NAME = "system_settings.xml";
/*     */ 
/*  22 */   public String LOCAL_SETTINGS_FOLDER_NAME = "Local Settings";
/*  23 */   public String JFOREX_FOLDER_NAME = "JForex";
/*  24 */   public String DEFAULT_WORKSPACES_FOLDER_NAME = "Workspaces";
/*  25 */   public String DEFAULT_STRATEGIES_FOLDER_NAME = "Strategies";
/*  26 */   public String DEFAULT_TEMPLATES_FOLDER_NAME = "Templates";
/*  27 */   public String DEFAULT_INDICATORS_FOLDER_NAME = "Indicators";
/*     */ 
/*  29 */   public String DEFAULT_CACHE_FOLDER_NAME = ".cache";
/*  30 */   public String DEFAULT_SYSTEM_SETTINGS_FOLDER_NAME = ".settings";
/*  31 */   public String FILES = "files";
/*     */ 
/*  33 */   private String TEMP_FOLDER_NAME = "temp";
/*     */ 
/*  35 */   private String METADATA_DIR_NAME = "metadata";
/*  36 */   private String HISTORY_START_DIR_NAME = "historystart";
/*     */   private String systemSettingsFolderPath;
/*     */   private String workspacesFolderPath;
/*     */   private String workspacesFileName;
/*     */   private String strategiesFolderPath;
/*     */   private String templatesFolderPath;
/*     */   private String indicatorsFolderPath;
/*     */   private String cacheFolderPath;
/*     */   private String cacheTempFolderPath;
/*     */   private String cachedCacheDirectory;
/*     */   private boolean foldersAccessible;
/*     */ 
/*     */   public static FilePathManager getInstance()
/*     */   {
/*  57 */     if (instance == null) {
/*  58 */       reset();
/*     */     }
/*  60 */     return instance;
/*     */   }
/*     */ 
/*     */   public String getSystemSettingsFilePath() {
/*  64 */     return getSystemSettingsFolderPath() + getPathSeparator() + this.SYSTEM_SETTING_FILE_NAME;
/*     */   }
/*     */ 
/*     */   public String getSystemSettingsFolderPath()
/*     */   {
/*  70 */     return this.systemSettingsFolderPath;
/*     */   }
/*     */ 
/*     */   public void setSystemSettingsFolderPath(String systemSettingsFolderPath) {
/*  74 */     this.systemSettingsFolderPath = systemSettingsFolderPath;
/*     */   }
/*     */ 
/*     */   private String getCacheFolderPath() {
/*  78 */     return this.cacheFolderPath;
/*     */   }
/*     */ 
/*     */   public void setCacheFolderPath(String cacheFolderPath) {
/*  82 */     this.cachedCacheDirectory = null;
/*  83 */     this.cacheFolderPath = cacheFolderPath;
/*     */   }
/*     */ 
/*     */   public void setCacheTempFolderPath(String cacheTempFolderPath) {
/*  87 */     this.cacheTempFolderPath = cacheTempFolderPath;
/*     */   }
/*     */ 
/*     */   public boolean isFoldersAccessible() {
/*  91 */     return this.foldersAccessible;
/*     */   }
/*     */ 
/*     */   public void setFoldersAccessible(boolean foldersAccessible) {
/*  95 */     this.foldersAccessible = foldersAccessible;
/*     */   }
/*     */ 
/*     */   public String getWorkspacesFolderPath() {
/*  99 */     return this.workspacesFolderPath;
/*     */   }
/*     */ 
/*     */   public void setWorkspacesFolderPath(String workspacesFolderPath) {
/* 103 */     this.workspacesFolderPath = workspacesFolderPath;
/*     */   }
/*     */ 
/*     */   public String getStrategiesFolderPath() {
/* 107 */     return this.strategiesFolderPath;
/*     */   }
/*     */ 
/*     */   public void setStrategiesFolderPath(String strategiesFolderPath) {
/* 111 */     this.strategiesFolderPath = strategiesFolderPath;
/*     */   }
/*     */ 
/*     */   public String getTemplatesFolderPath() {
/* 115 */     return this.templatesFolderPath;
/*     */   }
/*     */ 
/*     */   public void setTemplatesFolderPath(String templatesFolderPath) {
/* 119 */     this.templatesFolderPath = templatesFolderPath;
/*     */   }
/*     */ 
/*     */   public String getIndicatorsFolderPath() {
/* 123 */     return this.indicatorsFolderPath;
/*     */   }
/*     */ 
/*     */   public void setIndicatorsFolderPath(String indicatorsFolderPath) {
/* 127 */     this.indicatorsFolderPath = indicatorsFolderPath;
/*     */   }
/*     */ 
/*     */   public String getPathSeparator()
/*     */   {
/* 137 */     return File.separator;
/*     */   }
/*     */ 
/*     */   public char getPathSeparatorChar() {
/* 141 */     return File.separatorChar;
/*     */   }
/*     */ 
/*     */   private String getDefaultUserDocumentsFolderPath() {
/* 145 */     return FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
/*     */   }
/*     */ 
/*     */   public String getDefaultTempFolderPath() {
/* 149 */     String defaultTempFolderPath = System.getProperty("user.home");
/* 150 */     if (PlatformSpecific.WINDOWS) {
/* 151 */       defaultTempFolderPath = addPathSeparatorIfNeeded(defaultTempFolderPath);
/* 152 */       defaultTempFolderPath = defaultTempFolderPath + this.LOCAL_SETTINGS_FOLDER_NAME;
/*     */     }
/*     */ 
/* 155 */     defaultTempFolderPath = addPathSeparatorIfNeeded(defaultTempFolderPath);
/* 156 */     defaultTempFolderPath = defaultTempFolderPath + this.JFOREX_FOLDER_NAME;
/*     */ 
/* 158 */     return defaultTempFolderPath;
/*     */   }
/*     */ 
/*     */   public String addPathSeparatorIfNeeded(String path) {
/* 162 */     if (path == null) {
/* 163 */       return null;
/*     */     }
/* 165 */     if (path.isEmpty()) {
/* 166 */       return getPathSeparator();
/*     */     }
/* 168 */     if (path.charAt(path.length() - 1) != getPathSeparatorChar()) {
/* 169 */       return path + getPathSeparatorChar();
/*     */     }
/* 171 */     return path;
/*     */   }
/*     */ 
/*     */   public String getDefaultJForexFolderPath() {
/* 175 */     return getDefaultUserDocumentsFolderPath() + getPathSeparator() + this.JFOREX_FOLDER_NAME;
/*     */   }
/*     */ 
/*     */   private String getDefaultFolder(String clientMode, String userAccountId, String folder)
/*     */   {
/* 181 */     return getDefaultJForexFolderPath() + getPathSeparator() + folder + getPathSeparator() + clientMode + getPathSeparator() + userAccountId;
/*     */   }
/*     */ 
/*     */   private String getDefaultFolder(String folder)
/*     */   {
/* 191 */     return getDefaultJForexFolderPath() + getPathSeparator() + folder;
/*     */   }
/*     */ 
/*     */   public String getDefaultWorkspaceFolderPath(String clientMode, String userAccountId)
/*     */   {
/* 197 */     return getDefaultFolder(clientMode, userAccountId, this.DEFAULT_WORKSPACES_FOLDER_NAME);
/*     */   }
/*     */ 
/*     */   public String getDefaultStrategiesFolderPath()
/*     */   {
/* 202 */     return getDefaultJForexFolderPath();
/*     */   }
/*     */ 
/*     */   public String getDefaultIndicatorsFolderPath()
/*     */   {
/* 207 */     return getDefaultJForexFolderPath();
/*     */   }
/*     */ 
/*     */   public String getDefaultTemplatesFolderPath() {
/* 211 */     return getDefaultFolder(this.DEFAULT_TEMPLATES_FOLDER_NAME);
/*     */   }
/*     */ 
/*     */   public String getDefaultCacheFolderPath() {
/* 215 */     return getDefaultTempFolderPath() + getPathSeparator() + this.DEFAULT_CACHE_FOLDER_NAME;
/*     */   }
/*     */ 
/*     */   public String getDefaultSystemSettingsFolderPath() {
/* 219 */     return getDefaultTempFolderPath() + getPathSeparator() + this.DEFAULT_SYSTEM_SETTINGS_FOLDER_NAME;
/*     */   }
/*     */ 
/*     */   public String getDefaultSystemSettingsFilePath() {
/* 223 */     return getDefaultSystemSettingsFolderPath() + getPathSeparator() + this.SYSTEM_SETTING_FILE_NAME;
/*     */   }
/*     */ 
/*     */   public String getDefaultWorkspaceSettingsFilePath(String clientMode, String userAccountId) {
/* 227 */     return getDefaultWorkspaceFolderPath(clientMode, userAccountId) + getPathSeparator() + this.DEFAULT_WORKSPACE_FILE_NAME;
/*     */   }
/*     */ 
/*     */   public String getAlternativeDefaultWorkspaceSettingsFilePath(String clientMode, String userAccountId) {
/* 231 */     return getAlternativeDefaultWorkspacesFolderPath(clientMode, userAccountId) + getPathSeparator() + this.DEFAULT_WORKSPACE_FILE_NAME;
/*     */   }
/*     */ 
/*     */   private String getRootForAlternativePathes() {
/* 235 */     return "";
/*     */   }
/*     */ 
/*     */   private String getAlternativeUserDocumentsFolderPath() {
/* 239 */     return getRootForAlternativePathes();
/*     */   }
/*     */ 
/*     */   private String getAlternativeTempFolderPath() {
/* 243 */     return getRootForAlternativePathes() + getPathSeparator() + this.TEMP_FOLDER_NAME;
/*     */   }
/*     */ 
/*     */   public String getAlternativeJForexFolderPath() {
/* 247 */     return getAlternativeUserDocumentsFolderPath() + getPathSeparator() + this.JFOREX_FOLDER_NAME;
/*     */   }
/*     */ 
/*     */   private String getAlternateDefaultFolder(String clientMode, String userAccountId, String folder) {
/* 251 */     return getAlternativeJForexFolderPath() + getPathSeparator() + folder + getPathSeparator() + clientMode + getPathSeparator() + userAccountId;
/*     */   }
/*     */ 
/*     */   private String getAlternateDefaultFolder(String folder)
/*     */   {
/* 261 */     return getAlternativeJForexFolderPath() + getPathSeparator() + folder;
/*     */   }
/*     */ 
/*     */   public String getAlternativeDefaultWorkspacesFolderPath(String clientMode, String userAccountId)
/*     */   {
/* 267 */     return getAlternateDefaultFolder(clientMode, userAccountId, this.DEFAULT_WORKSPACES_FOLDER_NAME);
/*     */   }
/*     */ 
/*     */   public String getAlternativeDefaultTemplatesFolderPath() {
/* 271 */     return getAlternateDefaultFolder(this.DEFAULT_TEMPLATES_FOLDER_NAME);
/*     */   }
/*     */ 
/*     */   public String getAlternativeDefaultIndicatorsFolderPath() {
/* 275 */     return getAlternativeJForexFolderPath();
/*     */   }
/*     */ 
/*     */   public String getAlternativeStrategiesFolderPath()
/*     */   {
/* 280 */     return getAlternativeJForexFolderPath();
/*     */   }
/*     */ 
/*     */   public String getAlternativeSystemSettingsFolderPath()
/*     */   {
/* 286 */     return getAlternativeTempFolderPath() + getPathSeparator() + this.DEFAULT_SYSTEM_SETTINGS_FOLDER_NAME;
/*     */   }
/*     */ 
/*     */   public String getAlternativeSystemSettingsFilePath() {
/* 290 */     return getAlternativeSystemSettingsFolderPath() + getPathSeparator() + this.SYSTEM_SETTING_FILE_NAME;
/*     */   }
/*     */ 
/*     */   public String getAlternativeCacheFolderPath() {
/* 294 */     return getAlternativeTempFolderPath() + getPathSeparator() + this.DEFAULT_CACHE_FOLDER_NAME;
/*     */   }
/*     */ 
/*     */   public boolean isFileFolderAccessible(String filePath) {
/* 298 */     File file = new File(filePath);
/* 299 */     return isFolderAccessible(file.getParent());
/*     */   }
/*     */ 
/*     */   public boolean isFolderAccessible(String folderPath) {
/*     */     try {
/* 304 */       File folder = checkAndGetFolder(folderPath);
/*     */ 
/* 306 */       Random r = new Random();
/* 307 */       File file = null;
/* 308 */       String fileName = folder.getAbsolutePath() + getPathSeparator();
/*     */ 
/* 310 */       while ((file == null) || (file.exists())) {
/* 311 */         fileName = fileName + String.valueOf(Math.abs(r.nextInt()));
/* 312 */         file = new File(fileName);
/*     */       }
/*     */ 
/* 315 */       if (file.createNewFile()) {
/* 316 */         file.delete();
/* 317 */         return true;
/*     */       }
/*     */ 
/* 320 */       file.delete();
/* 321 */       return false;
/*     */     } catch (Throwable e) {
/*     */     }
/* 324 */     return false;
/*     */   }
/*     */ 
/*     */   public void checkFolderStructure(String path)
/*     */   {
/* 330 */     File file = new File(path);
/* 331 */     if (!file.exists())
/* 332 */       file.mkdirs();
/*     */   }
/*     */ 
/*     */   public File checkAndGetFolder(String path)
/*     */   {
/* 337 */     if (path == null) {
/* 338 */       return null;
/*     */     }
/*     */ 
/* 341 */     File file = new File(path);
/*     */ 
/* 343 */     if (!file.exists()) {
/* 344 */       file.mkdirs();
/*     */     }
/* 346 */     return file;
/*     */   }
/*     */ 
/*     */   public File getIndicatorsFolder() {
/* 350 */     return checkAndGetFolder(getIndicatorsFolderPath());
/*     */   }
/*     */ 
/*     */   public File getStrategiesFolder() {
/* 354 */     return checkAndGetFolder(getStrategiesFolderPath());
/*     */   }
/*     */ 
/*     */   public File getFilesForStrategiesDir() {
/* 358 */     File folder = getStrategiesFolder();
/* 359 */     if (folder == null) {
/* 360 */       return null;
/*     */     }
/* 362 */     return checkAndGetFolder(folder.getAbsolutePath() + getPathSeparator() + this.FILES);
/*     */   }
/*     */ 
/*     */   public File getWorkspacesFolder()
/*     */   {
/* 367 */     return checkAndGetFolder(getWorkspacesFolderPath());
/*     */   }
/*     */ 
/*     */   public File getTemplatesFolder() {
/* 371 */     return checkAndGetFolder(getTemplatesFolderPath());
/*     */   }
/*     */ 
/*     */   public String getCacheDirectory() {
/* 375 */     if (this.cachedCacheDirectory == null) {
/* 376 */       String cacheDirectory = getCacheFolderPath();
/*     */ 
/* 378 */       if (cacheDirectory == null) {
/* 379 */         cacheDirectory = getDefaultCacheFolderPath();
/*     */       }
/*     */ 
/* 382 */       if ((!this.foldersAccessible) && (!isFolderAccessible(cacheDirectory))) {
/* 383 */         cacheDirectory = getAlternativeCacheFolderPath();
/*     */       }
/*     */ 
/* 386 */       String result = cacheDirectory;
/*     */       try {
/* 388 */         result = new File(cacheDirectory).getCanonicalPath();
/* 389 */         result = addPathSeparatorIfNeeded(result);
/*     */       }
/*     */       catch (Throwable t) {
/*     */       }
/* 393 */       this.cachedCacheDirectory = result;
/* 394 */       return result;
/*     */     }
/* 396 */     return this.cachedCacheDirectory;
/*     */   }
/*     */ 
/*     */   public String getCacheMetadataDirectory()
/*     */   {
/* 401 */     StringBuilder fileName = new StringBuilder(getCacheDirectory());
/* 402 */     fileName.append(File.separatorChar);
/* 403 */     fileName.append(this.METADATA_DIR_NAME);
/* 404 */     return fileName.toString();
/*     */   }
/*     */ 
/*     */   public String getCacheMetadataFirstTimesDirectory() {
/* 408 */     StringBuilder fileName = new StringBuilder(getCacheMetadataDirectory());
/* 409 */     fileName.append(File.separatorChar);
/* 410 */     fileName.append(this.HISTORY_START_DIR_NAME);
/* 411 */     String directoryName = fileName.toString();
/*     */ 
/* 413 */     File directory = new File(directoryName);
/* 414 */     directory.mkdirs();
/*     */ 
/* 416 */     return directoryName;
/*     */   }
/*     */ 
/*     */   public File getFeedDataFirstTimesFile() {
/* 420 */     String clientMode = getClientModePrefix();
/* 421 */     StringBuilder fileName = new StringBuilder(getCacheMetadataFirstTimesDirectory());
/* 422 */     fileName.append(File.separatorChar);
/* 423 */     fileName.append(clientMode + "ft.txt");
/* 424 */     return new File(fileName.toString());
/*     */   }
/*     */ 
/*     */   public File getFeedDataFirstTimesLockFile() {
/* 428 */     String clientMode = getClientModePrefix();
/* 429 */     StringBuilder fileName = new StringBuilder(getCacheMetadataFirstTimesDirectory());
/* 430 */     fileName.append(File.separatorChar);
/* 431 */     fileName.append(clientMode + "ft.lck");
/* 432 */     return new File(fileName.toString());
/*     */   }
/*     */ 
/*     */   private String getClientModePrefix() {
/* 436 */     String clientMode = System.getProperty("jnlp.client.mode");
/* 437 */     if ((clientMode == null) || (clientMode.isEmpty()) || ("DEMO".equals(clientMode.toUpperCase())) || ("LIVE".equals(clientMode.toUpperCase())))
/*     */     {
/* 443 */       clientMode = "";
/*     */     }
/*     */     else {
/* 446 */       clientMode = String.valueOf(clientMode.charAt(0));
/*     */     }
/* 448 */     return clientMode.toLowerCase();
/*     */   }
/*     */ 
/*     */   public String getCacheTempDirectory()
/*     */   {
/* 453 */     if (this.cacheTempFolderPath == null) {
/* 454 */       return getCacheDirectory() + File.separatorChar + "jftemp";
/*     */     }
/* 456 */     return this.cacheTempFolderPath;
/*     */   }
/*     */ 
/*     */   public static void main(String[] str)
/*     */   {
/* 461 */     String temp = "c:\\My Documents\\App data\\JForex\\";
/* 462 */     getInstance().setCacheFolderPath(temp + ".cache");
/* 463 */     getInstance().setIndicatorsFolderPath(temp + "Indicators");
/* 464 */     getInstance().setStrategiesFolderPath(temp + "Strategies");
/* 465 */     getInstance().setSystemSettingsFolderPath(temp + ".forex");
/* 466 */     getInstance().setTemplatesFolderPath(temp + "Templates");
/* 467 */     getInstance().setWorkspacesFolderPath(temp + "Workspaces");
/*     */ 
/* 469 */     for (Method m : FilePathManager.class.getMethods()) {
/* 470 */       if (!m.getName().startsWith("get")) continue;
/*     */       try {
/* 472 */         System.out.println(m.getName() + " - " + m.invoke(getInstance(), (Object[])null));
/*     */       } catch (IllegalArgumentException e) {
/* 474 */         e.printStackTrace();
/*     */       } catch (IllegalAccessException e) {
/* 476 */         e.printStackTrace();
/*     */       } catch (InvocationTargetException e) {
/* 478 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getWorkspacesFileName()
/*     */   {
/* 485 */     return this.workspacesFileName;
/*     */   }
/*     */ 
/*     */   public void setWorkspacesFileName(String workspacesFileName) {
/* 489 */     this.workspacesFileName = workspacesFileName;
/*     */   }
/*     */ 
/*     */   public static void reset() {
/* 493 */     instance = new FilePathManager();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.FilePathManager
 * JD-Core Version:    0.6.0
 */