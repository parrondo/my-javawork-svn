/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.data;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ public class InstrumentsTableModel extends AbstractTableModel
/*     */ {
/*     */   static final int INDEX_INSTR = 0;
/*     */   static final int INDEX_CHECK = 1;
/*     */   static final int INDEX_FILE_NAME = 2;
/*  30 */   private HashMap<Integer, String> columnNames = new HashMap();
/*     */ 
/*  51 */   private ArrayList<InstrumentModelData> elements = new ArrayList();
/*     */ 
/*     */   public void setInstruments(List<InstrumentTableData> instruments)
/*     */   {
/*  58 */     this.elements.clear();
/*     */ 
/*  60 */     for (InstrumentTableData data : instruments) {
/*  61 */       InstrumentModelData modelData = new InstrumentModelData(data.instrument, true, data.fileName);
/*  62 */       this.elements.add(modelData);
/*     */     }
/*     */ 
/*  65 */     Collections.sort(this.elements, new Comparator()
/*     */     {
/*     */       public int compare(InstrumentsTableModel.InstrumentModelData o1, InstrumentsTableModel.InstrumentModelData o2) {
/*  68 */         return o1.instrument.toString().compareTo(o2.instrument.toString());
/*     */       }
/*     */     });
/*  72 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public List<InstrumentTableData> getInstruments()
/*     */   {
/*  81 */     List result = new ArrayList();
/*     */ 
/*  83 */     for (InstrumentModelData data : this.elements) {
/*  84 */       if (data.checked)
/*     */       {
/*  87 */         String fileName = data.fileName;
/*     */         String dataFile;
/*     */         String dataFile;
/*  88 */         if ((fileName != null) && (fileName.length() > 0))
/*  89 */           dataFile = fileName;
/*     */         else {
/*  91 */           dataFile = null;
/*     */         }
/*     */ 
/*  94 */         result.add(new InstrumentTableData(data.instrument, dataFile));
/*     */       }
/*     */     }
/*     */ 
/*  98 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isSelected(int rowIndex)
/*     */   {
/* 103 */     Boolean value = (Boolean)getValueAt(rowIndex, 1);
/* 104 */     return (value != null) && (value.booleanValue());
/*     */   }
/*     */ 
/*     */   public void selectAll()
/*     */   {
/* 111 */     for (int i = 0; i < getRowCount(); i++)
/* 112 */       setValueAt(Boolean.TRUE, i, 1);
/*     */   }
/*     */ 
/*     */   public void unselectAll()
/*     */   {
/* 120 */     for (int i = 0; i < getRowCount(); i++)
/* 121 */       setValueAt(Boolean.FALSE, i, 1);
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/* 127 */     return this.columnNames.size();
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */   {
/* 132 */     String name = (String)this.columnNames.get(Integer.valueOf(column));
/* 133 */     return name == null ? "" : name;
/*     */   }
/*     */ 
/*     */   public Class<?> getColumnClass(int columnIndex)
/*     */   {
/* 138 */     switch (columnIndex) {
/*     */     case 0:
/* 140 */       return String.class;
/*     */     case 1:
/* 142 */       return Boolean.class;
/*     */     case 2:
/* 144 */       return String.class;
/*     */     }
/* 146 */     return String.class;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 152 */     switch (columnIndex) {
/*     */     case 0:
/* 154 */       return false;
/*     */     case 1:
/* 157 */       return true;
/*     */     case 2:
/* 160 */       return true;
/*     */     }
/*     */ 
/* 163 */     return false;
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 169 */     return this.elements.size();
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/* 174 */     InstrumentModelData data = (InstrumentModelData)this.elements.get(rowIndex);
/* 175 */     switch (columnIndex) {
/*     */     case 0:
/* 177 */       return data.instrument;
/*     */     case 1:
/* 180 */       return Boolean.valueOf(data.checked);
/*     */     case 2:
/* 183 */       return data.fileName;
/*     */     }
/*     */ 
/* 186 */     return Boolean.valueOf(false);
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
/*     */   {
/* 192 */     InstrumentModelData data = (InstrumentModelData)this.elements.get(rowIndex);
/* 193 */     switch (columnIndex)
/*     */     {
/*     */     case 0:
/* 196 */       break;
/*     */     case 1:
/* 199 */       if (aValue == null) break;
/* 200 */       data.checked = ((Boolean)aValue).booleanValue();
/* 201 */       fireTableCellUpdated(rowIndex, columnIndex);
/* 202 */       fireTableCellUpdated(rowIndex, 2); break;
/*     */     case 2:
/* 207 */       data.fileName = ((String)aValue);
/* 208 */       fireTableCellUpdated(rowIndex, columnIndex);
/* 209 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 218 */     this.columnNames.clear();
/* 219 */     this.columnNames.put(Integer.valueOf(0), LocalizationManager.getText("dialog.save.data.instrument"));
/* 220 */     this.columnNames.put(Integer.valueOf(1), " ");
/* 221 */     this.columnNames.put(Integer.valueOf(2), LocalizationManager.getText("dialog.save.data.file.name"));
/* 222 */     fireTableStructureChanged();
/*     */   }
/*     */ 
/*     */   private static class InstrumentModelData
/*     */   {
/*     */     Instrument instrument;
/*     */     boolean checked;
/*     */     String fileName;
/*     */ 
/*     */     public InstrumentModelData(Instrument instrument, boolean checked, String fileName)
/*     */     {
/*  45 */       this.instrument = instrument;
/*  46 */       this.checked = checked;
/*  47 */       this.fileName = fileName;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.data.InstrumentsTableModel
 * JD-Core Version:    0.6.0
 */