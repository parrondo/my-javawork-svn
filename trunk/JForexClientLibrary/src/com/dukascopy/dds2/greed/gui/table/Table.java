/*     */ package com.dukascopy.dds2.greed.gui.table;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionAdapter;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableRowSorter;
/*     */ 
/*     */ public class Table<ColumnBean extends Enum<ColumnBean>, Info> extends JTable
/*     */ {
/*     */   public Table(AnnotatedTableModel<ColumnBean, Info> tableModel)
/*     */   {
/*  25 */     super(tableModel);
/*  26 */     init();
/*     */   }
/*     */ 
/*     */   public Table(AnnotatedTableModel<ColumnBean, Info> tableModel, TableColumnModel tableColumnModel) {
/*  30 */     super(tableModel, tableColumnModel);
/*  31 */     setAutoCreateColumnsFromModel(true);
/*  32 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  37 */     AnnotatedTableModel tableModel = (AnnotatedTableModel)getModel();
/*     */ 
/*  39 */     this.tableHeader.setReorderingAllowed(false);
/*  40 */     setAutoResizeMode(1);
/*     */ 
/*  42 */     TableRowSorter rowSorter = new TableRowSorter(tableModel);
/*  43 */     setRowSorter(rowSorter);
/*  44 */     setSelectionMode(0);
/*     */ 
/*  46 */     for (int i = 0; i < getColumnCount(); i++) {
/*  47 */       ColumnDescriptor columnDescriptor = tableModel.getColumnDescriptor(i);
/*     */ 
/*  49 */       TableColumn column = getColumnModel().getColumn(convertColumnIndexToView(i));
/*     */ 
/*  51 */       int columnWidth = columnDescriptor.width();
/*  52 */       if (columnWidth > 0) {
/*  53 */         column.setMinWidth(columnWidth);
/*  54 */         column.setPreferredWidth(columnWidth);
/*  55 */         column.setWidth(columnWidth);
/*  56 */         column.sizeWidthToFit();
/*     */       }
/*     */ 
/*  59 */       int columnMaxWidth = columnDescriptor.maxWidth();
/*  60 */       if (columnMaxWidth > 0) {
/*  61 */         column.setMaxWidth(columnMaxWidth);
/*     */       }
/*     */ 
/*  64 */       column.setResizable(columnDescriptor.resizable());
/*  65 */       rowSorter.setSortable(i, columnDescriptor.sortable());
/*     */     }
/*     */ 
/*  69 */     addMouseListener(new TableMouseListener(null));
/*  70 */     addMouseMotionListener(new MouseMotionAdapter() {
/*     */       public void mouseMoved(MouseEvent e) {
/*  72 */         Table.this.setCursor(Cursor.getDefaultCursor());
/*  73 */         Table.this.handleEvent(e, false);
/*     */       }
/*     */     });
/*  76 */     getTableHeader().addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e) {
/*  79 */         Table.this.forwardEventToHeader(e);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   protected boolean showPopup(Point point, int row, int column) {
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   private void handleEvent(MouseEvent mouseEvent, boolean convert) {
/*  89 */     Point point = mouseEvent.getPoint();
/*  90 */     int column = columnAtPoint(point);
/*  91 */     int row = rowAtPoint(point);
/*     */ 
/*  93 */     if ((row >= getRowCount()) || (row < 0) || (column >= getColumnCount()) || (column < 0)) {
/*  94 */       return;
/*     */     }
/*     */ 
/*  97 */     if ((mouseEvent.isPopupTrigger()) && (showPopup(point, row, column))) {
/*  98 */       return;
/*     */     }
/*     */ 
/* 101 */     Component rendererComponent = getCellRenderer(row, column).getTableCellRendererComponent(this, getValueAt(row, column), false, false, row, column);
/*     */ 
/* 108 */     if (convert)
/* 109 */       rendererComponent.dispatchEvent(SwingUtilities.convertMouseEvent(this, mouseEvent, rendererComponent));
/*     */     else
/* 111 */       rendererComponent.dispatchEvent(mouseEvent);
/*     */   }
/*     */ 
/*     */   private void forwardEventToHeader(MouseEvent mouseEvent)
/*     */   {
/* 116 */     int columnIndex = columnAtPoint(mouseEvent.getPoint());
/*     */ 
/* 118 */     if ((columnIndex < 0) || (columnIndex >= getColumnCount())) {
/* 119 */       return;
/*     */     }
/*     */ 
/* 122 */     TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
/*     */ 
/* 124 */     if ((tableColumn != null) && (tableColumn.getHeaderRenderer() != null)) {
/* 125 */       Component rendererComponent = tableColumn.getHeaderRenderer().getTableCellRendererComponent(this, null, false, false, -1, columnIndex);
/*     */ 
/* 132 */       rendererComponent.dispatchEvent(SwingUtilities.convertMouseEvent(this, mouseEvent, rendererComponent));
/*     */     }
/*     */   }
/*     */   private class TableMouseListener implements MouseListener {
/*     */     private TableMouseListener() {
/*     */     }
/*     */ 
/*     */     public void mouseClicked(MouseEvent e) {
/* 139 */       Table.this.handleEvent(e, true);
/*     */     }
/*     */ 
/*     */     public void mouseEntered(MouseEvent e) {
/* 143 */       Table.this.handleEvent(e, true);
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent e) {
/* 147 */       Table.this.handleEvent(e, true);
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent e) {
/* 151 */       Table.this.handleEvent(e, true);
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent e) {
/* 155 */       Table.this.handleEvent(e, true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.table.Table
 * JD-Core Version:    0.6.0
 */