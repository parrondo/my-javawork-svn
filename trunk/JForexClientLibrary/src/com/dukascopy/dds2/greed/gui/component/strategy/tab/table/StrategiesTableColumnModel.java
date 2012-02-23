/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import javax.swing.table.DefaultTableColumnModel;
/*     */ import javax.swing.table.TableColumn;
/*     */ 
/*     */ public class StrategiesTableColumnModel extends DefaultTableColumnModel
/*     */ {
/*     */   public static final int ICON_COLUMN_INDEX = 0;
/*     */   public static final int NAME_COLUMN_INDEX = 1;
/*     */   public static final int START_COLUMN_INDEX = 2;
/*     */   public static final int END_COLUMN_INDEX = 3;
/*     */   public static final int TIME_COLUMN_INDEX = 4;
/*     */   public static final int TYPE_COLUMN_INDEX = 5;
/*     */   public static final int PRESET_COLUMN_INDEX = 6;
/*     */   public static final int STATUS_COLUMN_INDEX = 7;
/*     */   public static final int COMMENTS_COLUMN_INDEX = 8;
/*     */   public static final String ICON_COLUMN_IDENTIFIER = "ICON_COLUMN_IDENTIFIER";
/*     */   public static final String NAME_COLUMN_IDENTIFIER = "NAME_COLUMN_IDENTIFIER";
/*     */   public static final String START_COLUMN_IDENTIFIER = "START_COLUMN_IDENTIFIER";
/*     */   public static final String END_COLUMN_IDENTIFIER = "END_COLUMN_IDENTIFIER";
/*     */   public static final String TIME_COLUMN_IDENTIFIER = "TIME_COLUMN_IDENTIFIER";
/*     */   public static final String TYPE_COLUMN_IDENTIFIER = "TYPE_COLUMN_IDENTIFIER";
/*     */   public static final String PRESET_COLUMN_IDENTIFIER = "PRESET_COLUMN_IDENTIFIER";
/*     */   public static final String STATUS_COLUMN_IDENTIFIER = "STATUS_COLUMN_IDENTIFIER";
/*     */   public static final String COMMENTS_COLUMN_IDENTIFIER = "COMMENTS_COLUMN_IDENTIFIER";
/*     */ 
/*     */   public StrategiesTableColumnModel()
/*     */   {
/*  57 */     TableColumn iconColumn = new TableColumn(0);
/*  58 */     iconColumn.setIdentifier("ICON_COLUMN_IDENTIFIER");
/*  59 */     iconColumn.setCellRenderer(new StatusTableCellRenderer());
/*  60 */     iconColumn.setHeaderValue("");
/*  61 */     iconColumn.setMaxWidth(20);
/*     */ 
/*  63 */     TableColumn nameColumn = new TableColumn(1);
/*  64 */     nameColumn.setIdentifier("NAME_COLUMN_IDENTIFIER");
/*  65 */     nameColumn.setCellRenderer(new NameTableCellRenderer());
/*  66 */     nameColumn.setHeaderValue(LocalizationManager.getText("strategies.column.name"));
/*     */ 
/*  68 */     TableColumn startColumn = new TableColumn(2);
/*  69 */     startColumn.setIdentifier("START_COLUMN_IDENTIFIER");
/*  70 */     startColumn.setCellRenderer(new StringTableCellRenderer());
/*  71 */     startColumn.setHeaderValue(LocalizationManager.getText("strategies.column.start.date"));
/*     */ 
/*  73 */     TableColumn endColumn = new TableColumn(3);
/*  74 */     endColumn.setIdentifier("END_COLUMN_IDENTIFIER");
/*  75 */     endColumn.setCellRenderer(new StringTableCellRenderer());
/*  76 */     endColumn.setHeaderValue(LocalizationManager.getText("strategies.column.end.date"));
/*     */ 
/*  78 */     TableColumn timeColumn = new TableColumn(4);
/*  79 */     timeColumn.setIdentifier("TIME_COLUMN_IDENTIFIER");
/*  80 */     timeColumn.setCellRenderer(new StringTableCellRenderer());
/*  81 */     timeColumn.setHeaderValue(LocalizationManager.getText("strategies.column.duration"));
/*     */ 
/*  83 */     TableColumn typeColumn = new TableColumn(5);
/*  84 */     typeColumn.setIdentifier("TYPE_COLUMN_IDENTIFIER");
/*  85 */     typeColumn.setCellRenderer(new StringTableCellRenderer());
/*  86 */     typeColumn.setHeaderValue(LocalizationManager.getText("strategies.column.type"));
/*     */ 
/*  88 */     TableColumn paramsColumn = new TableColumn(6);
/*  89 */     paramsColumn.setIdentifier("PRESET_COLUMN_IDENTIFIER");
/*  90 */     paramsColumn.setCellRenderer(new StringTableCellRenderer());
/*  91 */     paramsColumn.setHeaderValue(LocalizationManager.getText("strategies.column.preset"));
/*     */ 
/*  93 */     TableColumn statusColumn = new TableColumn(7);
/*  94 */     statusColumn.setIdentifier("STATUS_COLUMN_IDENTIFIER");
/*  95 */     statusColumn.setCellRenderer(new StatusTableCellRenderer());
/*  96 */     statusColumn.setHeaderValue(LocalizationManager.getText("strategies.column.status"));
/*     */ 
/*  98 */     TableColumn commentsColumn = new TableColumn(8);
/*  99 */     commentsColumn.setIdentifier("COMMENTS_COLUMN_IDENTIFIER");
/* 100 */     commentsColumn.setCellRenderer(new StringTableCellRenderer());
/* 101 */     commentsColumn.setHeaderValue(LocalizationManager.getText("strategies.column.comments"));
/*     */ 
/* 103 */     addColumn(iconColumn);
/* 104 */     addColumn(nameColumn);
/* 105 */     addColumn(startColumn);
/* 106 */     addColumn(endColumn);
/* 107 */     addColumn(timeColumn);
/* 108 */     addColumn(typeColumn);
/* 109 */     addColumn(paramsColumn);
/* 110 */     addColumn(statusColumn);
/* 111 */     addColumn(commentsColumn);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableColumnModel
 * JD-Core Version:    0.6.0
 */