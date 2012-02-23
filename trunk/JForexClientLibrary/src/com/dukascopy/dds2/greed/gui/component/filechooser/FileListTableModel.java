/*     */ package com.dukascopy.dds2.greed.gui.component.filechooser;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ public class FileListTableModel extends AbstractTableModel
/*     */ {
/*     */   private static final long serialVersionUID = 8924385043916674399L;
/*  20 */   protected List<FileItem> tableData = new ArrayList();
/*     */   protected FileType fileType;
/*  23 */   private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*     */ 
/*     */   public FileListTableModel(List<FileItem> data, FileType ft) {
/*  26 */     this.tableData = data;
/*  27 */     this.fileType = ft;
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/*  32 */     if ((this.fileType == FileType.WORKSPACE) || (this.fileType == FileType.CHART) || (this.fileType == FileType.STRATEGY) || (this.fileType == FileType.INDICATOR) || (this.fileType == FileType.WIDGET)) {
/*  33 */       return 4;
/*     */     }
/*  35 */     return 0;
/*     */   }
/*     */ 
/*     */   public void setData(List<FileItem> data) {
/*  39 */     this.tableData = data;
/*  40 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public void clearData() {
/*  44 */     this.tableData = new ArrayList();
/*  45 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/*  50 */     return this.tableData.size();
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */   {
/*  55 */     if (column == 0)
/*  56 */       return LocalizationManager.getText("chooser.file.name");
/*  57 */     if (column == 1)
/*  58 */       return LocalizationManager.getText("chooser.date.modified");
/*  59 */     if (column == 2)
/*  60 */       return LocalizationManager.getText("chooser.is.shared");
/*  61 */     if (column == 3) {
/*  62 */       return LocalizationManager.getText("chooser.description");
/*     */     }
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   public FileItem getRow(int index)
/*     */   {
/*  69 */     return (FileItem)this.tableData.get(index);
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/*  74 */     if (this.tableData.size() == 0)
/*  75 */       return null;
/*  76 */     FileItem object = getRow(rowIndex);
/*     */ 
/*  78 */     if (columnIndex == 0)
/*  79 */       return object.getFileName();
/*  80 */     if (columnIndex == 1)
/*     */     {
/*  82 */       Date lastModified = object.getLastModified();
/*     */ 
/*  84 */       if (lastModified != null) {
/*  85 */         return this.df.format(lastModified);
/*     */       }
/*  87 */       return "";
/*     */     }
/*  89 */     if (columnIndex == 2)
/*  90 */       return object.isShared().booleanValue() ? LocalizationManager.getText("yes.option") : LocalizationManager.getText("no.option");
/*  91 */     if (columnIndex == 3) {
/*  92 */       return object.getDescription();
/*     */     }
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 100 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.filechooser.FileListTableModel
 * JD-Core Version:    0.6.0
 */