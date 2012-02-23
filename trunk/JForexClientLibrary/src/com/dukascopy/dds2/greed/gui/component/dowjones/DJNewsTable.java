/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import com.dukascopy.api.ICalendarMessage.Detail;
/*    */ import com.dukascopy.api.INewsMessage;
/*    */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.Country;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTable;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import com.dukascopy.dds2.greed.gui.table.renderers.Link;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Date;
/*    */ import java.util.List;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.JPopupMenu;
/*    */ import javax.swing.RowSorter;
/*    */ import javax.swing.RowSorter.SortKey;
/*    */ 
/*    */ public class DJNewsTable<ColumnBean extends Enum<ColumnBean>,  extends IColumn<Info>, Info extends INewsMessage> extends JLocalizableAnnotatedTable<ColumnBean, Info>
/*    */ {
/*    */   public DJNewsTable(DJNewsTableModelFilter<ColumnBean, Info> tableModelFilter)
/*    */   {
/* 34 */     super(tableModelFilter);
/* 35 */     tableModelFilter.setTable(this);
/*    */ 
/* 37 */     setDefaultRenderer(Date.class, new DateTableCellRenderer(new SimpleDateFormat("dd-MMM HH:mm")
/*    */     {
/*    */     }));
/* 38 */     setDefaultRenderer(Country.class, new TextTableCellRenderer(0));
/* 39 */     setDefaultRenderer(Object.class, new TextTableCellRenderer(0));
/* 40 */     setDefaultRenderer(String.class, new TextTableCellRenderer(10));
/* 41 */     setDefaultRenderer(Link.class, new LinkTableCellRenderer());
/* 42 */     setDefaultRenderer(ICalendarMessage.Detail.class, new MultiLineDetailsRenderer());
/*    */ 
/* 44 */     List sortKeys = new ArrayList();
/*    */ 
/* 47 */     for (int i = 0; i < getColumnCount(); i++) {
/* 48 */       ColumnDescriptor columnDescriptor = tableModelFilter.getColumnDescriptor(i);
/* 49 */       if (columnDescriptor.sortable()) {
/* 50 */         sortKeys.add(new RowSorter.SortKey(i, columnDescriptor.sortOrder()));
/*    */       }
/*    */     }
/*    */ 
/* 54 */     getRowSorter().setSortKeys(sortKeys);
/*    */ 
/* 56 */     setComponentPopupMenu(new JPopupMenu(tableModelFilter)
/*    */     {
/*    */     });
/*    */   }
/*    */ 
/*    */   public void setPattern(String filter)
/*    */   {
/* 70 */     ((MultiLineDetailsRenderer)getDefaultRenderer(ICalendarMessage.Detail.class)).setPattern(filter);
/* 71 */     ((LinkTableCellRenderer)getDefaultRenderer(Link.class)).setPattern(filter);
/* 72 */     ((TextTableCellRenderer)getDefaultRenderer(Country.class)).setPattern(filter);
/* 73 */     ((TextTableCellRenderer)getDefaultRenderer(Object.class)).setPattern(filter);
/* 74 */     ((DateTableCellRenderer)getDefaultRenderer(Date.class)).setPattern(filter);
/* 75 */     ((TextTableCellRenderer)getDefaultRenderer(String.class)).setPattern(filter);
/* 76 */     ((DJNewsTableModelFilter)DJNewsTableModelFilter.class.cast(getModel())).setPattern(filter);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.DJNewsTable
 * JD-Core Version:    0.6.0
 */