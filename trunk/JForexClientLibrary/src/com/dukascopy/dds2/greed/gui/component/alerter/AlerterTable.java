/*     */ package com.dukascopy.dds2.greed.gui.component.alerter;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.CellEditor;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ 
/*     */ public class AlerterTable extends JLocalizableTable
/*     */ {
/*     */   private TableCellRenderer renderer;
/*     */   private AlerterTableModel tableModel;
/*  37 */   private static DateFormat df = new SimpleDateFormat("hh:mm:ss");
/*     */ 
/*     */   public AlerterTable(AlerterTableModel model) {
/*  40 */     super(model);
/*  41 */     this.tableModel = model;
/*     */ 
/*  43 */     df.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/*  45 */     setAutoResizeMode(0);
/*  46 */     setSelectionMode(0);
/*  47 */     setRowSelectionAllowed(true);
/*  48 */     getTableHeader().setReorderingAllowed(false);
/*     */ 
/*  50 */     this.renderer = new DefaultTableCellRenderer()
/*     */     {
/*     */       public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */       {
/*  54 */         Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*  55 */         setHorizontalAlignment(0);
/*     */ 
/*  57 */         Alert alert = (Alert)AlerterTable.this.tableModel.getAlertList().get(row);
/*     */ 
/*  59 */         JLocalizableLabel label = new JLocalizableLabel();
/*     */ 
/*  61 */         switch (column) {
/*     */         case 1:
/*  63 */           Condition condition = alert.getCondition();
/*  64 */           label.setTextParams(new String[] { condition.getLocalizationParam() });
/*  65 */           label.setText(condition.getLocalizationKey());
/*  66 */           break;
/*     */         case 3:
/*  68 */           AlerterNotification event = alert.getNotification();
/*  69 */           label.setText(event.getLocalizationKey());
/*  70 */           break;
/*     */         case 4:
/*  72 */           AlerterStatus status = alert.getStatus();
/*  73 */           if (status == AlerterStatus.COMPLETED) {
/*  74 */             label.setTextParams(new String[] { AlerterTable.access$100().format(alert.getCompletedTime()) + " GMT" });
/*     */           }
/*  76 */           label.setText(status.getLocalizationKey());
/*  77 */           break;
/*     */         case 2:
/*     */         default:
/*  79 */           return comp;
/*     */         }
/*     */ 
/*  82 */         label.setOpaque(true);
/*  83 */         label.setForeground(comp.getForeground());
/*  84 */         label.setBackground(comp.getBackground());
/*  85 */         label.setHorizontalAlignment(0);
/*  86 */         return label;
/*     */       }
/*     */     };
/*  90 */     TableColumn column = this.columnModel.getColumn(0);
/*  91 */     column.setCellEditor(new ComboBoxEditor(Instrument.values(), this));
/*  92 */     column.setMinWidth(80);
/*  93 */     column.setMaxWidth(80);
/*     */ 
/*  95 */     column = this.columnModel.getColumn(1);
/*     */ 
/*  97 */     ListCellRenderer conditionRenderer = new DefaultListCellRenderer()
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 100 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 102 */         Condition cond = (Condition)value;
/* 103 */         JLocalizableLabel label = new JLocalizableLabel();
/* 104 */         label.setTextParams(new String[] { cond.getLocalizationParam() });
/* 105 */         label.setText(cond.getLocalizationKey());
/*     */ 
/* 107 */         label.setOpaque(true);
/* 108 */         label.setForeground(comp.getForeground());
/* 109 */         label.setBackground(comp.getBackground());
/*     */ 
/* 111 */         return label;
/*     */       }
/*     */     };
/* 114 */     column.setCellEditor(new ComboBoxEditor(Condition.values(), this, conditionRenderer));
/*     */ 
/* 116 */     column.setMinWidth(70);
/* 117 */     column.setMaxWidth(70);
/*     */ 
/* 119 */     column = this.columnModel.getColumn(2);
/* 120 */     column.setCellEditor(new SpinnerEditor());
/* 121 */     column.setMinWidth(80);
/* 122 */     column.setMaxWidth(80);
/*     */ 
/* 124 */     column = this.columnModel.getColumn(3);
/*     */ 
/* 126 */     ListCellRenderer notificationRenderer = new DefaultListCellRenderer()
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 129 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 131 */         AlerterNotification notification = (AlerterNotification)value;
/* 132 */         JLocalizableLabel label = new JLocalizableLabel();
/* 133 */         label.setText(notification.getLocalizationKey());
/*     */ 
/* 135 */         label.setOpaque(true);
/* 136 */         label.setForeground(comp.getForeground());
/* 137 */         label.setBackground(comp.getBackground());
/*     */ 
/* 139 */         return label;
/*     */       }
/*     */     };
/* 143 */     column.setCellEditor(new ComboBoxEditor(AlerterNotification.values(), this, notificationRenderer));
/* 144 */     column.setMinWidth(70);
/* 145 */     column.setMaxWidth(70);
/*     */ 
/* 147 */     column = this.columnModel.getColumn(4);
/*     */ 
/* 149 */     ListCellRenderer statusRenderer = new DefaultListCellRenderer()
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 152 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 154 */         AlerterStatus status = (AlerterStatus)value;
/* 155 */         JLocalizableLabel label = new JLocalizableLabel();
/* 156 */         label.setText(status.getLocalizationKey());
/*     */ 
/* 158 */         label.setOpaque(true);
/* 159 */         label.setForeground(comp.getForeground());
/* 160 */         label.setBackground(comp.getBackground());
/*     */ 
/* 162 */         return label;
/*     */       }
/*     */     };
/* 166 */     column.setCellEditor(new ComboBoxEditor(new AlerterStatus[] { AlerterStatus.INACTIVE, AlerterStatus.ACTIVE }, this, statusRenderer));
/* 167 */     column.setMinWidth(150);
/* 168 */     column.setMaxWidth(150);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredScrollableViewportSize() {
/* 172 */     return new Dimension(getPreferredSize().width, getRowHeight() * 10);
/*     */   }
/*     */ 
/*     */   public TableCellRenderer getCellRenderer(int row, int column) {
/* 176 */     return this.renderer;
/*     */   }
/*     */ 
/*     */   public void stopCellEditing() {
/* 180 */     CellEditor cellEditor = getCellEditor();
/* 181 */     if (cellEditor != null)
/* 182 */       cellEditor.stopCellEditing();
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 188 */     getColumnModel().getColumn(0).setHeaderValue(LocalizationManager.getText("price.alert.column.currency"));
/* 189 */     getColumnModel().getColumn(1).setHeaderValue(LocalizationManager.getText("price.alert.column.condition"));
/* 190 */     getColumnModel().getColumn(2).setHeaderValue(LocalizationManager.getText("price.alert.column.price"));
/* 191 */     getColumnModel().getColumn(3).setHeaderValue(LocalizationManager.getText("price.alert.column.event"));
/* 192 */     getColumnModel().getColumn(4).setHeaderValue(LocalizationManager.getText("price.alert.column.status"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.alerter.AlerterTable
 * JD-Core Version:    0.6.0
 */