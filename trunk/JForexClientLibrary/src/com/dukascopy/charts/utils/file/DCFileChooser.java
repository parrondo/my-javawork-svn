/*     */ package com.dukascopy.charts.utils.file;
/*     */ 
/*     */ import com.dukascopy.charts.utils.file.filter.AbstractFileFilter;
/*     */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Component;
/*     */ import java.io.File;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.UIManager;
/*     */ 
/*     */ public class DCFileChooser extends JFileChooser
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   private DCFileChooser()
/*     */   {
/*     */   }
/*     */ 
/*     */   private DCFileChooser(String defaultPath)
/*     */   {
/*  29 */     super(defaultPath);
/*     */   }
/*     */ 
/*     */   private DCFileChooser(String defaultPath, File selectedFile) {
/*  33 */     super(defaultPath);
/*  34 */     setSelectedFile(selectedFile);
/*     */   }
/*     */ 
/*     */   public static DCFileChooser createDCFileChooser(String defaultPath, File selectedFile)
/*     */   {
/*  39 */     UIManager.put("FileChooser.openDialogTitleText", LocalizationManager.getText("open.button.text"));
/*  40 */     UIManager.put("FileChooser.saveDialogTitleText", LocalizationManager.getText("save.button.text"));
/*     */ 
/*  42 */     UIManager.put("FileChooser.cancelButtonText", LocalizationManager.getText("cancel.button.text"));
/*  43 */     UIManager.put("FileChooser.cancelButtonToolTipText", LocalizationManager.getText("cancel.button.tool.tip.text"));
/*     */ 
/*  45 */     UIManager.put("FileChooser.saveButtonText", LocalizationManager.getText("save.button.text"));
/*  46 */     UIManager.put("FileChooser.saveButtonToolTipText", LocalizationManager.getText("save.button.tool.tip.text"));
/*     */ 
/*  48 */     UIManager.put("FileChooser.openButtonText", LocalizationManager.getText("open.button.text"));
/*  49 */     UIManager.put("FileChooser.openButtonToolTipText", LocalizationManager.getText("open.button.tool.tip.text"));
/*     */ 
/*  51 */     UIManager.put("FileChooser.fileNameLabelText", LocalizationManager.getText("file.name.label.text") + ":");
/*  52 */     UIManager.put("FileChooser.filesOfTypeLabelText", LocalizationManager.getText("files.of.type.label.text") + ":");
/*     */ 
/*  54 */     UIManager.put("FileChooser.saveInLabelText", LocalizationManager.getText("save.in.label.text"));
/*  55 */     UIManager.put("FileChooser.lookInLabelText", LocalizationManager.getText("look.in.label.text"));
/*     */ 
/*  57 */     UIManager.put("FileChooser.upFolderToolTipText", LocalizationManager.getText("up.folder.tool.tip.text"));
/*  58 */     UIManager.put("FileChooser.homeFolderToolTipText", LocalizationManager.getText("home.folder.tool.tip.text"));
/*  59 */     UIManager.put("FileChooser.newFolderToolTipText", LocalizationManager.getText("new.folder.tool.tip.text"));
/*     */ 
/*  61 */     UIManager.put("FileChooser.detailsViewButtonToolTipText", LocalizationManager.getText("details.view.button.tool.tip.text"));
/*  62 */     UIManager.put("FileChooser.listViewButtonToolTipText", LocalizationManager.getText("list.view.button.tool.tip.text"));
/*     */ 
/*  64 */     DCFileChooser fc = null;
/*     */ 
/*  66 */     if (defaultPath != null) {
/*  67 */       fc = new DCFileChooser(defaultPath);
/*     */     }
/*     */     else {
/*  70 */       fc = new DCFileChooser();
/*     */     }
/*     */ 
/*  73 */     if (selectedFile != null) {
/*  74 */       fc.setSelectedFile(selectedFile);
/*     */     }
/*     */ 
/*  77 */     return fc;
/*     */   }
/*     */ 
/*     */   public static File saveFileWithReplacementConfirmation(Component parentComponent, String defaultPath, File file, AbstractFileFilter fileFilter, int selectionMode)
/*     */   {
/*  89 */     File selectedFile = null;
/*  90 */     boolean continueLoop = true;
/*     */     do {
/*  92 */       selectedFile = saveFile(defaultPath, file, fileFilter, selectionMode);
/*     */ 
/*  94 */       if ((selectedFile != null) && (selectedFile.exists())) {
/*  95 */         int doYouWantToReplace = LocalizedMessageHelper.showConfirmationMessage(null, selectedFile.getAbsolutePath() + " " + LocalizationManager.getText("already.exists").toLowerCase() + "\n" + LocalizationManager.getText("do.you.want.to.replace.it"));
/*     */ 
/* 102 */         continueLoop = 0 != doYouWantToReplace;
/*     */       }
/*     */       else {
/* 105 */         continueLoop = false;
/*     */       }
/*     */     }
/* 108 */     while (continueLoop);
/*     */ 
/* 110 */     return selectedFile;
/*     */   }
/*     */ 
/*     */   public static File saveFileWithReplacementConfirmation(Component parent, String defaultPath, AbstractFileFilter fileFilter) {
/* 114 */     return saveFileWithReplacementConfirmation(parent, defaultPath, null, fileFilter);
/*     */   }
/*     */ 
/*     */   public static File saveFileWithReplacementConfirmation(Component parent, String defaultPath, File selectedFile, AbstractFileFilter fileFilter) {
/* 118 */     return saveFileWithReplacementConfirmation(parent, defaultPath, selectedFile, fileFilter, 0);
/*     */   }
/*     */ 
/*     */   public static File saveFile(String defaultPath, File selectedFile, AbstractFileFilter fileFilter, int selectionMode)
/*     */   {
/* 129 */     DCFileChooser workspaceSettingsFileChooser = createFileChooser(defaultPath, selectedFile, fileFilter, selectionMode);
/* 130 */     int result = workspaceSettingsFileChooser.showSaveDialog(null);
/* 131 */     File file = getSelectedFile(result, workspaceSettingsFileChooser);
/* 132 */     if ((file != null) && (!file.getName().isEmpty()) && 
/* 133 */       (!fileFilter.accept(file))) {
/* 134 */       file = new File(file.getAbsolutePath() + "." + fileFilter.getExtension());
/*     */     }
/*     */ 
/* 137 */     return file;
/*     */   }
/*     */ 
/*     */   public static File saveFile(String defaultPath, File selectedFile, AbstractFileFilter fileFilter) {
/* 141 */     return saveFile(defaultPath, selectedFile, fileFilter, 0);
/*     */   }
/*     */ 
/*     */   private static File getSelectedFile(int result, DCFileChooser workspaceSettingsFileChooser) {
/* 145 */     if (0 == result) {
/* 146 */       File file = workspaceSettingsFileChooser.getSelectedFile();
/* 147 */       return file;
/*     */     }
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public static DCFileChooser createFileChooser(String defaultPath, File selectedFile, AbstractFileFilter fileFilter, int selectionMode) {
/* 153 */     DCFileChooser workspaceSettingsFileChooser = createDCFileChooser(defaultPath, selectedFile);
/* 154 */     workspaceSettingsFileChooser.setFileFilter(fileFilter);
/* 155 */     workspaceSettingsFileChooser.setFileSelectionMode(selectionMode);
/* 156 */     return workspaceSettingsFileChooser;
/*     */   }
/*     */ 
/*     */   public static File openFile(String defaultPath, AbstractFileFilter fileFilter, int selectionMode)
/*     */   {
/* 168 */     DCFileChooser fileChooser = createFileChooser(defaultPath, null, fileFilter, selectionMode);
/* 169 */     int result = fileChooser.showOpenDialog(null);
/* 170 */     File file = getSelectedFile(result, fileChooser);
/* 171 */     if ((file != null) && (file.exists())) {
/* 172 */       return file;
/*     */     }
/*     */ 
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   public static File openFile(String defaultPath, AbstractFileFilter fileFilter)
/*     */   {
/* 187 */     return openFile(defaultPath, fileFilter, 0);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.file.DCFileChooser
 * JD-Core Version:    0.6.0
 */