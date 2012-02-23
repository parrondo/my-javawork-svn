/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*     */ 
/*     */ import com.dukascopy.api.ICalendarMessage.Detail;
/*     */ import com.dukascopy.api.INewsMessage;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.lang.reflect.Array;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import java.util.Vector;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class DJNewsTableModelFilter<ColumnBean extends Enum<ColumnBean>,  extends IColumn<Info>, Info extends INewsMessage> extends DJNewsTableModel<ColumnBean, Info>
/*     */ {
/*  32 */   private static final long serialVersionUID = 3851254600645281612L;
/*  32 */   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat() { private static final long serialVersionUID = 2578890818044797824L; } ;
/*     */   private DJNewsTable<ColumnBean, Info> table;
/*     */   private final List<Info> allRowData;
/*     */   private final Class<Info> infoClass;
/*     */   private final Class<ColumnBean> columnBeanClass;
/*     */   private String pattern;
/*     */ 
/*     */   public DJNewsTableModelFilter(Class<ColumnBean> columnBeanClass, Class<Info> infoClass) {
/*  47 */     super(columnBeanClass, infoClass);
/*  48 */     this.infoClass = infoClass;
/*  49 */     this.columnBeanClass = columnBeanClass;
/*  50 */     this.allRowData = Collections.synchronizedList(new Vector());
/*     */   }
/*     */ 
/*     */   public void insert(Info newsMessage)
/*     */   {
/*  59 */     if (newsMessage != null) {
/*  60 */       this.allRowData.add(newsMessage);
/*  61 */       filterMessage(newsMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void remove(String id)
/*     */   {
/*  73 */     if (!ObjectUtils.isNullOrEmpty(id))
/*     */     {
/*     */       Iterator it;
/*  74 */       synchronized (this.allRowData) {
/*  75 */         for (it = this.allRowData.iterator(); it.hasNext(); ) {
/*  76 */           INewsMessage newsMessage = (INewsMessage)it.next();
/*  77 */           if (ObjectUtils.isEqual(newsMessage.getId(), id)) {
/*  78 */             it.remove();
/*     */           }
/*     */         }
/*     */       }
/*  82 */       super.remove(id);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearAll() {
/*  87 */     this.allRowData.clear();
/*  88 */     clear();
/*     */   }
/*     */ 
/*     */   public DJNewsTable<ColumnBean, Info> getTable()
/*     */   {
/*  95 */     return this.table;
/*     */   }
/*     */ 
/*     */   public void setTable(DJNewsTable<ColumnBean, Info> table)
/*     */   {
/* 102 */     this.table = table;
/*     */   }
/*     */ 
/*     */   public String getPattern()
/*     */   {
/* 109 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   public void setPattern(String pattern)
/*     */   {
/* 116 */     this.pattern = pattern.toLowerCase();
/* 117 */     refreshModel();
/*     */   }
/*     */ 
/*     */   protected void refreshModel()
/*     */   {
/* 123 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 127 */         int selectedColumn = DJNewsTableModelFilter.this.table.getSelectedColumn();
/* 128 */         int selectedRow = DJNewsTableModelFilter.this.table.getSelectedRow();
/*     */ 
/* 130 */         DJNewsTableModelFilter.this.clear();
/* 131 */         DJNewsTableModelFilter.this.addAll(DJNewsTableModelFilter.this.performQuickFilter());
/*     */ 
/* 133 */         if ((selectedColumn > -1) && (selectedRow > -1))
/* 134 */           DJNewsTableModelFilter.this.table.changeSelection(selectedRow, selectedColumn, false, false);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void filterMessage(Info candidate) {
/* 141 */     if (matched(candidate))
/* 142 */       super.insert(candidate);
/*     */   }
/*     */ 
/*     */   protected boolean matched(Info candidate)
/*     */   {
/* 147 */     if (ObjectUtils.isNullOrEmpty(getPattern())) {
/* 148 */       return true;
/*     */     }
/* 150 */     for (Enum columnBean : (Enum[])this.columnBeanClass.getEnumConstants()) {
/* 151 */       Object value = ((IColumn)columnBean).getValue(candidate);
/* 152 */       String str = getRenderedText(value);
/* 153 */       if (str.toLowerCase().contains(getPattern())) {
/* 154 */         return true;
/*     */       }
/*     */     }
/* 157 */     return false;
/*     */   }
/*     */ 
/*     */   protected Info[] performQuickFilter()
/*     */   {
/* 162 */     List result = new ArrayList();
/* 163 */     synchronized (this.allRowData) {
/* 164 */       for (INewsMessage candidate : this.allRowData) {
/* 165 */         if (matched(candidate)) {
/* 166 */           result.add(candidate);
/*     */         }
/*     */       }
/*     */     }
/* 170 */     INewsMessage[] array = (INewsMessage[])(INewsMessage[])Array.newInstance(this.infoClass, result.size());
/* 171 */     result.toArray(array);
/* 172 */     return array;
/*     */   }
/*     */ 
/*     */   protected String getRenderedText(Object value)
/*     */   {
/* 177 */     String text = "";
/* 178 */     if (!ObjectUtils.isNullOrEmpty(value)) {
/* 179 */       if ((value instanceof Date)) {
/* 180 */         text = DATE_FORMAT.format((Date)value);
/* 181 */       } else if ((value instanceof List)) {
/* 182 */         StringBuilder st = new StringBuilder();
/* 183 */         for (Iterator i$ = ((List)value).iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 184 */           if ((element instanceof ICalendarMessage.Detail)) {
/* 185 */             ICalendarMessage.Detail detail = (ICalendarMessage.Detail)element;
/* 186 */             if (!ObjectUtils.isNullOrEmpty(detail.getActual())) {
/* 187 */               st.append(detail.getActual());
/*     */             }
/* 189 */             if (!ObjectUtils.isNullOrEmpty(detail.getPrevious())) {
/* 190 */               st.append(detail.getPrevious());
/*     */             }
/* 192 */             if (!ObjectUtils.isNullOrEmpty(detail.getExpected())) {
/* 193 */               st.append(detail.getExpected());
/*     */             }
/* 195 */             if (!ObjectUtils.isNullOrEmpty(detail.getDescription())) {
/* 196 */               st.append(detail.getDescription());
/*     */             }
/*     */           }
/*     */         }
/* 200 */         text = st.toString();
/*     */       } else {
/* 202 */         text = String.valueOf(value);
/*     */       }
/*     */     }
/* 205 */     return text;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.DJNewsTableModelFilter
 * JD-Core Version:    0.6.0
 */