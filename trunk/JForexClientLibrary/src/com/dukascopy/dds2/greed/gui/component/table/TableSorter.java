/*     */ package com.dukascopy.dds2.greed.gui.component.table;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.RowSorter.SortKey;
/*     */ import javax.swing.SortOrder;
/*     */ import javax.swing.event.TableModelEvent;
/*     */ import javax.swing.event.TableModelListener;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class TableSorter extends AbstractTableModel
/*     */ {
/*     */   protected TableModel tableModel;
/*  82 */   private static RowSorter.SortKey EMPTY_SORT_KEY = new RowSorter.SortKey(-1, SortOrder.UNSORTED);
/*  83 */   private Map<Object, Object> keyIndex = new HashMap();
/*  84 */   private int keyColumn = 0;
/*     */ 
/*  86 */   private List NoSortableData_KeyObjects = null;
/*  87 */   private int NoSortableData_KeyColumn = -1;
/*  88 */   private int NoSortableData_Direction = -1;
/*     */ 
/*  90 */   private List<Integer> NoSortableColumns = null;
/*     */ 
/*  92 */   public static final Comparator COMPARABLE_COMAPRATOR = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/*  94 */       if ((o1 == null) || (o2 == null)) return 1;
/*  95 */       if (o1.getClass() != o2.getClass()) return 1;
/*  96 */       return ((Comparable)o1).compareTo(o2);
/*     */     }
/*  92 */   };
/*     */ 
/*  99 */   public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/* 101 */       if ((o1 == null) || (o2 == null)) return 1;
/* 102 */       return o1.toString().compareTo(o2.toString());
/*     */     }
/*  99 */   };
/*     */   private Row[] viewToModel;
/*     */   private int[] modelToView;
/*     */   private JTableHeader tableHeader;
/*     */   private MouseListener mouseListener;
/*     */   private TableModelListener tableModelListener;
/* 112 */   private Map columnComparators = new HashMap();
/* 113 */   private Map<Integer, RowSorter.SortKey> sortKeys = new HashMap();
/*     */ 
/*     */   public TableSorter() {
/* 116 */     this.mouseListener = new MouseHandler(null);
/* 117 */     this.tableModelListener = new TableModelHandler(null);
/*     */   }
/*     */   public TableSorter(TableModel tableModel) {
/* 120 */     this(tableModel, 0);
/*     */   }
/*     */   public TableSorter(TableModel tableModel, int keyColumn) {
/* 123 */     this();
/* 124 */     this.keyColumn = keyColumn;
/* 125 */     setTableModel(tableModel);
/*     */   }
/*     */ 
/*     */   public TableSorter(TableModel tableModel, JTableHeader tableHeader) {
/* 129 */     this(tableModel, tableHeader, 0);
/*     */   }
/*     */   public TableSorter(TableModel tableModel, JTableHeader tableHeader, int keyColumn) {
/* 132 */     this();
/* 133 */     this.keyColumn = keyColumn;
/* 134 */     setTableHeader(tableHeader);
/* 135 */     setTableModel(tableModel);
/*     */   }
/*     */ 
/*     */   public void setNoSortableData(List objects, int column, int direction) {
/* 139 */     this.NoSortableData_KeyObjects = objects;
/* 140 */     this.NoSortableData_KeyColumn = column;
/* 141 */     this.NoSortableData_Direction = direction;
/*     */   }
/*     */ 
/*     */   public void setNoSortableColumns(List<Integer> columns) {
/* 145 */     this.NoSortableColumns = columns;
/*     */   }
/*     */ 
/*     */   private void clearSortingState() {
/* 149 */     this.viewToModel = null;
/* 150 */     this.modelToView = null;
/*     */   }
/*     */ 
/*     */   public TableModel getTableModel()
/*     */   {
/* 155 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 156 */     return this.tableModel;
/*     */   }
/*     */ 
/*     */   public void setTableModel(TableModel tableModel) {
/* 160 */     if (this.tableModel != null) {
/* 161 */       this.tableModel.removeTableModelListener(this.tableModelListener);
/*     */     }
/*     */ 
/* 164 */     this.tableModel = tableModel;
/* 165 */     if (this.tableModel != null) {
/* 166 */       this.tableModel.addTableModelListener(this.tableModelListener);
/*     */     }
/*     */ 
/* 169 */     clearSortingState();
/* 170 */     fireTableStructureChanged();
/*     */   }
/*     */ 
/*     */   public JTableHeader getTableHeader() {
/* 174 */     return this.tableHeader;
/*     */   }
/*     */ 
/*     */   public void setTableHeader(JTableHeader tableHeader) {
/* 178 */     if (this.tableHeader != null) {
/* 179 */       this.tableHeader.removeMouseListener(this.mouseListener);
/* 180 */       TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
/* 181 */       if ((defaultRenderer instanceof SortableHeaderRenderer)) {
/* 182 */         this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer)defaultRenderer).tableCellRenderer);
/*     */       }
/*     */     }
/* 185 */     this.tableHeader = tableHeader;
/* 186 */     if (this.tableHeader != null) {
/* 187 */       this.tableHeader.addMouseListener(this.mouseListener);
/* 188 */       this.tableHeader.setDefaultRenderer(new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isSorting()
/*     */   {
/* 194 */     return this.sortKeys.size() != 0;
/*     */   }
/*     */ 
/*     */   private RowSorter.SortKey getSortKey(int column) {
/* 198 */     if (this.sortKeys.containsKey(Integer.valueOf(column))) {
/* 199 */       return (RowSorter.SortKey)this.sortKeys.get(Integer.valueOf(column));
/*     */     }
/* 201 */     return EMPTY_SORT_KEY;
/*     */   }
/*     */ 
/*     */   public SortOrder getSortingOrder(int column) {
/* 205 */     return getSortKey(column).getSortOrder();
/*     */   }
/*     */ 
/*     */   private void sortingStatusChanged() {
/* 209 */     clearSortingState();
/* 210 */     fireTableDataChanged();
/* 211 */     if (this.tableHeader != null)
/* 212 */       this.tableHeader.repaint();
/*     */   }
/*     */ 
/*     */   public void setSortingStatus(List<? extends RowSorter.SortKey> sortKeys)
/*     */   {
/* 217 */     if (sortKeys != null)
/* 218 */       for (RowSorter.SortKey sortKey : sortKeys)
/* 219 */         setSortingStatus(sortKey);
/*     */   }
/*     */ 
/*     */   public void setSortingStatus(RowSorter.SortKey sortKey)
/*     */   {
/* 225 */     if (sortKey != null)
/* 226 */       setSortingStatus(sortKey.getColumn(), sortKey.getSortOrder());
/*     */   }
/*     */ 
/*     */   public void setSortingStatus(int column, SortOrder sortOrder)
/*     */   {
/* 231 */     RowSorter.SortKey sortKey = getSortKey(column);
/* 232 */     if (sortKey != EMPTY_SORT_KEY) {
/* 233 */       this.sortKeys.remove(sortKey);
/*     */     }
/* 235 */     if (sortOrder != SortOrder.UNSORTED) {
/* 236 */       this.sortKeys.put(Integer.valueOf(column), new RowSorter.SortKey(column, sortOrder));
/*     */     }
/* 238 */     sortingStatusChanged();
/*     */   }
/*     */ 
/*     */   protected Icon getHeaderRendererIcon(int column, int size) {
/* 242 */     RowSorter.SortKey sortKey = getSortKey(column);
/* 243 */     if ((sortKey == EMPTY_SORT_KEY) || (sortKey.getSortOrder() == SortOrder.UNSORTED)) {
/* 244 */       return null;
/*     */     }
/* 246 */     return new Arrow(sortKey.getSortOrder() != SortOrder.DESCENDING, size, 1);
/*     */   }
/*     */ 
/*     */   private void cancelSorting() {
/* 250 */     this.sortKeys.clear();
/* 251 */     sortingStatusChanged();
/*     */   }
/*     */ 
/*     */   public void setColumnComparator(Class type, Comparator comparator) {
/* 255 */     if (comparator == null)
/* 256 */       this.columnComparators.remove(type);
/*     */     else
/* 258 */       this.columnComparators.put(type, comparator);
/*     */   }
/*     */ 
/*     */   protected Comparator getComparator(int column)
/*     */   {
/* 263 */     Class columnType = this.tableModel.getColumnClass(column);
/* 264 */     Comparator comparator = (Comparator)this.columnComparators.get(columnType);
/* 265 */     if (comparator != null) {
/* 266 */       return comparator;
/*     */     }
/* 268 */     if (Comparable.class.isAssignableFrom(columnType)) {
/* 269 */       return COMPARABLE_COMAPRATOR;
/*     */     }
/* 271 */     return LEXICAL_COMPARATOR;
/*     */   }
/*     */ 
/*     */   private Row[] getViewToModel() {
/* 275 */     if (this.viewToModel == null) {
/* 276 */       int tableModelRowCount = this.tableModel.getRowCount();
/* 277 */       this.viewToModel = new Row[tableModelRowCount];
/* 278 */       for (int row = 0; row < tableModelRowCount; row++) {
/* 279 */         this.viewToModel[row] = new Row(row);
/*     */       }
/* 281 */       if (isSorting()) {
/* 282 */         Arrays.sort(this.viewToModel);
/*     */       }
/* 284 */       this.keyIndex.clear();
/* 285 */       for (int row = 0; row < tableModelRowCount; row++) {
/* 286 */         Row rowobj = this.viewToModel[row];
/* 287 */         this.keyIndex.put(this.tableModel.getValueAt(rowobj.modelIndex, this.keyColumn), new Integer(row));
/*     */       }
/*     */     }
/* 290 */     return this.viewToModel;
/*     */   }
/*     */   public int viewIndex(int modelIndex) {
/* 293 */     if (modelIndex >= getViewToModel().length)
/* 294 */       modelIndex = getViewToModel().length - 1;
/* 295 */     return getModelToView()[modelIndex];
/*     */   }
/*     */   public int modelIndex(int viewIndex) {
/* 298 */     Row[] rowList = getViewToModel();
/* 299 */     if ((viewIndex >= 0) && (viewIndex < rowList.length)) {
/* 300 */       return rowList[viewIndex].modelIndex;
/*     */     }
/* 302 */     return -1;
/*     */   }
/*     */ 
/*     */   private int[] getModelToView() {
/* 306 */     if (this.modelToView == null) {
/* 307 */       int n = getViewToModel().length;
/* 308 */       this.modelToView = new int[n];
/* 309 */       for (int i = 0; i < n; i++) {
/* 310 */         this.modelToView[modelIndex(i)] = i;
/*     */       }
/*     */     }
/* 313 */     return this.modelToView;
/*     */   }
/*     */   public Object getIndexByKey(Object key) {
/* 316 */     if (this.keyIndex.isEmpty()) getModelToView();
/* 317 */     return this.keyIndex.get(key);
/*     */   }
/*     */   public boolean keyIndexContainsKey(Object key) {
/* 320 */     return this.keyIndex.containsKey(key);
/*     */   }
/*     */ 
/*     */   public void reload() {
/* 324 */     clearSortingState();
/* 325 */     getModelToView();
/* 326 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   private boolean updateTable() {
/* 330 */     if ((this.NoSortableData_KeyColumn != -1) && (this.NoSortableData_KeyObjects != null)) {
/* 331 */       this.keyIndex.clear();
/* 332 */       int tableModelRowCount = this.tableModel.getRowCount();
/* 333 */       this.viewToModel = new Row[tableModelRowCount];
/* 334 */       for (int row = 0; row < tableModelRowCount; row++) {
/* 335 */         this.viewToModel[row] = new Row(row);
/*     */       }
/*     */ 
/* 338 */       Arrays.sort(this.viewToModel);
/* 339 */       for (int row = 0; row < tableModelRowCount; row++) {
/* 340 */         Row rowobj = this.viewToModel[row];
/* 341 */         this.keyIndex.put(this.tableModel.getValueAt(rowobj.modelIndex, this.keyColumn), new Integer(row));
/*     */       }
/* 343 */       int n = this.viewToModel.length;
/* 344 */       this.modelToView = new int[n];
/* 345 */       for (int i = 0; i < n; i++) {
/* 346 */         this.modelToView[modelIndex(i)] = i;
/*     */       }
/* 348 */       return true;
/*     */     }
/* 350 */     return false;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 354 */     if (this.tableModel != null) {
/* 355 */       if ((this.tableModel instanceof DefaultTableModel)) ((DefaultTableModel)this.tableModel).setRowCount(0);
/*     */ 
/* 357 */       this.keyIndex.clear();
/* 358 */       reload();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 366 */     return this.tableModel == null ? 0 : this.tableModel.getRowCount();
/*     */   }
/*     */ 
/*     */   public int getColumnCount() {
/* 370 */     return this.tableModel == null ? 0 : this.tableModel.getColumnCount();
/*     */   }
/*     */ 
/*     */   public Class getColumnClass(int column)
/*     */   {
/* 378 */     return this.tableModel.getColumnClass(column);
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int row, int column) {
/* 382 */     return this.tableModel.isCellEditable(modelIndex(row), column);
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int row, int column) {
/* 386 */     return this.tableModel.getValueAt(modelIndex(row), column);
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object aValue, int row, int column) {
/* 390 */     this.tableModel.setValueAt(aValue, modelIndex(row), column);
/*     */   }
/*     */ 
/*     */   public void addRow(Vector rowData) {
/* 394 */     if ((this.tableModel instanceof DefaultTableModel)) {
/* 395 */       ((DefaultTableModel)this.tableModel).addRow(rowData);
/* 396 */       reload();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addRow(Object[] rowData) {
/* 401 */     if ((this.tableModel instanceof DefaultTableModel)) {
/* 402 */       ((DefaultTableModel)this.tableModel).addRow(rowData);
/* 403 */       reload();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeRow(int index) {
/* 408 */     if ((this.tableModel instanceof DefaultTableModel)) {
/* 409 */       ((DefaultTableModel)this.tableModel).removeRow(index);
/* 410 */       reload();
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<RowSorter.SortKey> getSortKeys()
/*     */   {
/* 632 */     return new ArrayList(this.sortKeys.values());
/*     */   }
/*     */ 
/*     */   private class SortableHeaderRenderer
/*     */     implements TableCellRenderer
/*     */   {
/*     */     private TableCellRenderer tableCellRenderer;
/*     */ 
/*     */     public SortableHeaderRenderer(TableCellRenderer tableCellRenderer)
/*     */     {
/* 609 */       this.tableCellRenderer = tableCellRenderer;
/*     */     }
/*     */ 
/*     */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */     {
/* 618 */       Component c = this.tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*     */ 
/* 620 */       if ((c instanceof JLabel)) {
/* 621 */         JLabel l = (JLabel)c;
/* 622 */         l.setHorizontalTextPosition(2);
/* 623 */         int modelColumn = table.convertColumnIndexToModel(column);
/* 624 */         l.setIcon(TableSorter.this.getHeaderRendererIcon(modelColumn, l.getFont().getSize() + 2));
/*     */       }
/* 626 */       return c;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Arrow
/*     */     implements Icon
/*     */   {
/*     */     private boolean descending;
/*     */     private int size;
/*     */     private int priority;
/*     */ 
/*     */     public Arrow(boolean descending, int size, int priority)
/*     */     {
/* 556 */       this.descending = descending;
/* 557 */       this.size = size;
/* 558 */       this.priority = priority;
/*     */     }
/*     */ 
/*     */     public void paintIcon(Component c, Graphics g, int x, int y) {
/* 562 */       Color color = c == null ? Color.GRAY : c.getBackground();
/*     */ 
/* 567 */       int dx = this.size / 2;
/* 568 */       int dy = this.descending ? dx : -dx;
/*     */ 
/* 570 */       y = y + 5 * this.size / 6 + (this.descending ? -dy : 0);
/* 571 */       int shift = this.descending ? 1 : -1;
/* 572 */       g.translate(x, y);
/*     */ 
/* 575 */       g.setColor(color.darker());
/* 576 */       g.drawLine(dx / 2, dy, 0, 0);
/* 577 */       g.drawLine(dx / 2, dy + shift, 0, shift);
/*     */ 
/* 580 */       g.setColor(color.brighter());
/* 581 */       g.drawLine(dx / 2, dy, dx, 0);
/* 582 */       g.drawLine(dx / 2, dy + shift, dx, shift);
/*     */ 
/* 585 */       if (this.descending)
/* 586 */         g.setColor(color.darker().darker());
/*     */       else {
/* 588 */         g.setColor(color.brighter().brighter());
/*     */       }
/* 590 */       g.drawLine(dx, 0, 0, 0);
/*     */ 
/* 592 */       g.setColor(color);
/* 593 */       g.translate(-x, -y);
/*     */     }
/*     */ 
/*     */     public int getIconWidth() {
/* 597 */       return this.size;
/*     */     }
/*     */ 
/*     */     public int getIconHeight() {
/* 601 */       return this.size;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MouseHandler extends MouseAdapter
/*     */   {
/*     */     private MouseHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void mouseClicked(MouseEvent e)
/*     */     {
/* 524 */       JTableHeader h = (JTableHeader)e.getSource();
/* 525 */       TableColumnModel columnModel = h.getColumnModel();
/* 526 */       int viewColumn = columnModel.getColumnIndexAtX(e.getX());
/* 527 */       int column = columnModel.getColumn(viewColumn).getModelIndex();
/* 528 */       if ((column != -1) && (
/* 529 */         (TableSorter.this.NoSortableColumns == null) || (!TableSorter.this.NoSortableColumns.contains(Integer.valueOf(column))))) {
/* 530 */         SortOrder sortOrder = TableSorter.this.getSortingOrder(column);
/* 531 */         if (!e.isControlDown()) {
/* 532 */           TableSorter.this.cancelSorting();
/*     */         }
/*     */ 
/* 536 */         if (e.isShiftDown()) {
/* 537 */           sortOrder = sortOrder == SortOrder.DESCENDING ? SortOrder.ASCENDING : sortOrder == SortOrder.UNSORTED ? SortOrder.DESCENDING : SortOrder.UNSORTED;
/*     */         }
/*     */         else
/*     */         {
/* 541 */           sortOrder = sortOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING : sortOrder == SortOrder.UNSORTED ? SortOrder.ASCENDING : SortOrder.UNSORTED;
/*     */         }
/*     */ 
/* 544 */         TableSorter.this.setSortingStatus(column, sortOrder);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TableModelHandler
/*     */     implements TableModelListener
/*     */   {
/*     */     private TableModelHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void tableChanged(TableModelEvent e)
/*     */     {
/* 465 */       if (!TableSorter.this.isSorting()) {
/* 466 */         TableSorter.this.clearSortingState();
/* 467 */         if (TableSorter.this.updateTable())
/* 468 */           TableSorter.this.fireTableDataChanged();
/*     */         else {
/* 470 */           TableSorter.this.fireTableChanged(e);
/*     */         }
/* 472 */         return;
/*     */       }
/*     */ 
/* 478 */       if (e.getFirstRow() == -1) {
/* 479 */         TableSorter.this.cancelSorting();
/* 480 */         TableSorter.this.fireTableChanged(e);
/* 481 */         return;
/*     */       }
/*     */ 
/* 502 */       int column = e.getColumn();
/* 503 */       if ((e.getFirstRow() == e.getLastRow()) && (column != -1) && (TableSorter.this.getSortingOrder(column) == SortOrder.UNSORTED) && (TableSorter.this.modelToView != null))
/*     */       {
/* 507 */         int viewIndex = TableSorter.this.getModelToView()[e.getFirstRow()];
/* 508 */         TableSorter.this.fireTableChanged(new TableModelEvent(TableSorter.this, viewIndex, viewIndex, column, e.getType()));
/*     */ 
/* 511 */         return;
/*     */       }
/*     */ 
/* 515 */       TableSorter.this.clearSortingState();
/* 516 */       TableSorter.this.fireTableDataChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Row
/*     */     implements Comparable
/*     */   {
/*     */     private int modelIndex;
/*     */ 
/*     */     public Row(int index)
/*     */     {
/* 420 */       this.modelIndex = index;
/*     */     }
/*     */ 
/*     */     public int compareTo(Object o) {
/* 424 */       int row1 = this.modelIndex;
/* 425 */       int row2 = ((Row)o).modelIndex;
/*     */ 
/* 427 */       if ((TableSorter.this.NoSortableData_KeyColumn != -1) && (TableSorter.this.NoSortableData_KeyObjects != null)) {
/* 428 */         Object o1 = TableSorter.this.tableModel.getValueAt(row1, TableSorter.this.NoSortableData_KeyColumn);
/* 429 */         Object o2 = TableSorter.this.tableModel.getValueAt(row2, TableSorter.this.NoSortableData_KeyColumn);
/* 430 */         if ((TableSorter.this.NoSortableData_KeyObjects.contains(o1)) && (TableSorter.this.NoSortableData_KeyObjects.contains(o2))) {
/* 431 */           int dif = TableSorter.this.NoSortableData_KeyObjects.indexOf(o2) - TableSorter.this.NoSortableData_KeyObjects.indexOf(o1);
/* 432 */           return dif > 0 ? -1 : dif < 0 ? 1 : 0;
/*     */         }
/* 434 */         if (TableSorter.this.NoSortableData_KeyObjects.contains(o1)) return -TableSorter.this.NoSortableData_Direction;
/* 435 */         if (TableSorter.this.NoSortableData_KeyObjects.contains(o2)) return TableSorter.this.NoSortableData_Direction;
/*     */       }
/*     */ 
/* 438 */       for (RowSorter.SortKey sortKey : TableSorter.this.sortKeys.values()) {
/* 439 */         int column = sortKey.getColumn();
/* 440 */         Object o1 = TableSorter.this.tableModel.getValueAt(row1, column);
/* 441 */         Object o2 = TableSorter.this.tableModel.getValueAt(row2, column);
/*     */ 
/* 443 */         int comparison = 0;
/*     */ 
/* 445 */         if ((o1 == null) && (o2 == null))
/* 446 */           comparison = 0;
/* 447 */         else if (o1 == null)
/* 448 */           comparison = -1;
/* 449 */         else if (o2 == null)
/* 450 */           comparison = 1;
/*     */         else {
/* 452 */           comparison = TableSorter.this.getComparator(column).compare(o1, o2);
/*     */         }
/* 454 */         if (comparison != 0) {
/* 455 */           return sortKey.getSortOrder() == SortOrder.DESCENDING ? -comparison : comparison;
/*     */         }
/*     */       }
/* 458 */       return 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.TableSorter
 * JD-Core Version:    0.6.0
 */