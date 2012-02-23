/*     */ package com.dukascopy.dds2.greed.gui.table;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AnnotatedTableModel<ColumnBean extends Enum<ColumnBean>, Info> extends AbstractTableModel
/*     */ {
/*  20 */   private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedTableModel.class);
/*     */   private static final String METHOD_NAME = "getValue";
/*     */   private final List<ColumnDescriptor> columnDescriptors;
/*     */   private final ColumnBean[] columnBeanValues;
/*     */   private final Method columnBeanValueGetter;
/*     */   private final List<Info> rowData;
/*     */ 
/*     */   public AnnotatedTableModel(Class<ColumnBean> columnBeanClass, Class<Info> infoClass)
/*     */   {
/*  30 */     this.rowData = Collections.synchronizedList(new Vector());
/*  31 */     this.columnDescriptors = new ArrayList();
/*     */ 
/*  33 */     for (Field field : columnBeanClass.getFields()) {
/*  34 */       ColumnDescriptor columnDescriptor = (ColumnDescriptor)field.getAnnotation(ColumnDescriptor.class);
/*     */ 
/*  36 */       if (columnDescriptor == null) {
/*  37 */         throw new IllegalArgumentException(field + " don't annoted by " + ColumnDescriptor.class);
/*     */       }
/*     */ 
/*  40 */       this.columnDescriptors.add(columnDescriptor);
/*     */     }
/*     */ 
/*  43 */     this.columnBeanValues = ((Enum[])columnBeanClass.getEnumConstants());
/*     */     try
/*     */     {
/*  46 */       this.columnBeanValueGetter = columnBeanClass.getMethod("getValue", new Class[] { columnBeanClass, infoClass });
/*  47 */       this.columnBeanValueGetter.setAccessible(true);
/*     */     } catch (Exception e) {
/*  49 */       throw new IllegalArgumentException(columnBeanClass + " don't declare getValue(" + columnBeanClass.getSimpleName() + "," + infoClass.getSimpleName() + ") method");
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getColumnName(int columnIndex)
/*     */   {
/*  55 */     return getColumnDescriptor(columnIndex).title();
/*     */   }
/*     */ 
/*     */   public final Class<?> getColumnClass(int columnIndex)
/*     */   {
/*  60 */     return getColumnDescriptor(columnIndex).contentClass();
/*     */   }
/*     */ 
/*     */   public final int getColumnCount()
/*     */   {
/*  65 */     return this.columnDescriptors.size();
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/*  70 */     return this.rowData.size();
/*     */   }
/*     */ 
/*     */   public final Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/*  75 */     if (getRowCount() > 0) {
/*  76 */       return getValue(columnIndex, this.rowData.get(rowIndex));
/*     */     }
/*     */ 
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   public final void addAll(Info[] infos, int fromIndex, int toIndex)
/*     */   {
/*  89 */     if (infos == null) {
/*  90 */       throw new IllegalArgumentException("Infos is empty");
/*     */     }
/*     */ 
/*  93 */     if (infos.length <= 0) {
/*  94 */       return;
/*     */     }
/*     */ 
/*  97 */     if ((fromIndex > toIndex) || (toIndex > infos.length - 1)) {
/*  98 */       throw new IllegalArgumentException("Incorrect indecies passed!");
/*     */     }
/*     */ 
/* 101 */     for (int i = fromIndex; i <= toIndex; i++) {
/* 102 */       Object info = infos[i];
/* 103 */       addNoFire(info);
/*     */     }
/*     */ 
/* 106 */     doFireTableRowsInserted(0, toIndex - fromIndex);
/*     */   }
/*     */ 
/*     */   public final void addAll(Info[] infos) {
/* 110 */     addAll(infos, 0, infos.length - 1);
/*     */   }
/*     */ 
/*     */   private final void addNoFire(Info info) {
/* 114 */     if (info == null) {
/* 115 */       throw new IllegalArgumentException("Info is empty");
/*     */     }
/*     */ 
/* 118 */     this.rowData.add(0, info);
/*     */   }
/*     */ 
/*     */   private final void doFireTableRowsInserted(int firstRow, int lastRow) {
/*     */     try {
/* 123 */       fireTableRowsInserted(firstRow, lastRow);
/*     */     } catch (Exception ex) {
/* 125 */       LOGGER.warn("Error while updating table : " + firstRow + "/" + lastRow + " @ " + getRowCount(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void add(Info info) {
/* 130 */     addNoFire(info);
/* 131 */     doFireTableRowsInserted(0, 0);
/*     */   }
/*     */ 
/*     */   public final void update(int index, Info info) {
/* 135 */     this.rowData.set(index, info);
/* 136 */     fireTableRowsUpdated(index, index);
/*     */   }
/*     */ 
/*     */   public final Info get(int index) {
/* 140 */     return this.rowData.get(index);
/*     */   }
/*     */ 
/*     */   public final void remove(int index) {
/* 144 */     this.rowData.remove(index);
/* 145 */     fireTableRowsDeleted(index, index);
/*     */   }
/*     */ 
/*     */   public final void clear() {
/*     */     try {
/* 150 */       this.rowData.clear();
/* 151 */       fireTableDataChanged();
/*     */     } catch (Exception ex) {
/* 153 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Object getValue(int columnIndex, Info info)
/*     */   {
/*     */     try
/*     */     {
/* 161 */       return this.columnBeanValueGetter.invoke(null, new Object[] { this.columnBeanValues[columnIndex], info });
/*     */     } catch (Exception ex) {
/* 163 */       LOGGER.error(ex.getMessage(), ex);
/* 164 */     }throw new RuntimeException(ex);
/*     */   }
/*     */ 
/*     */   public ColumnDescriptor getColumnDescriptor(int columnIndex)
/*     */   {
/* 169 */     return (ColumnDescriptor)this.columnDescriptors.get(columnIndex);
/*     */   }
/*     */ 
/*     */   public final void refresh() {
/* 173 */     fireTableRowsUpdated(0, getRowCount());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.table.AnnotatedTableModel
 * JD-Core Version:    0.6.0
 */