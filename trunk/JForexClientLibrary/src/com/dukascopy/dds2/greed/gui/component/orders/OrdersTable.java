/*     */ package com.dukascopy.dds2.greed.gui.component.orders;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellEditor;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxCellRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeaderItemListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.StopPriceRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTableModelListener;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.List;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class OrdersTable extends JLocalizableTable
/*     */ {
/*     */   public static final String ID_JT_ORDERSTABLE = "ID_JT_ORDERSTABLE";
/*     */   String highlightedGroupId;
/*  52 */   private List<String> selectedGroupIds = null;
/*     */ 
/*     */   public OrdersTable()
/*     */   {
/*  56 */     setName("ID_JT_ORDERSTABLE");
/*  57 */     ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
/*     */   }
/*     */ 
/*     */   public OrdersTable(TableModel dm) {
/*  61 */     super(dm);
/*  62 */     setName("ID_JT_ORDERSTABLE");
/*     */ 
/*  64 */     ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
/*     */ 
/*  66 */     setSelectionMode(0);
/*  67 */     setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  68 */     setSelectionBackground(GreedContext.SELECTION_COLOR);
/*  69 */     setSelectionForeground(GreedContext.SELECTION_FG_COLOR);
/*     */ 
/*  71 */     setDefaultRenderer(Object.class, new OrdersTableRenderer((OrderCommonTableModel)((TableSorter)dm).getTableModel(), (TableSorter)dm));
/*     */ 
/*  78 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_PRICE).setCellRenderer(new OrdersTableRenderer((OrderCommonTableModel)((TableSorter)dm).getTableModel(), (TableSorter)dm, new StopPriceRenderer()));
/*     */ 
/*  83 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_CHECK).setCellRenderer(new CheckBoxCellRenderer());
/*     */ 
/*  85 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_PRICE).setPreferredWidth(110);
/*  86 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_REQ_AMOUNT).setPreferredWidth(50);
/*  87 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_TYPE).setPreferredWidth(40);
/*  88 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_STATE).setPreferredWidth(60);
/*  89 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_SIDE).setPreferredWidth(30);
/*  90 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_POSITION).setPreferredWidth(40);
/*  91 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_ID).setPreferredWidth(30);
/*  92 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_TIMESTAMP).setPreferredWidth(100);
/*  93 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_INSTRUMENT).setPreferredWidth(50);
/*  94 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_PROPS).setPreferredWidth(70);
/*  95 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_EXP).setPreferredWidth(120);
/*     */ 
/*  97 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_CHECK).setPreferredWidth(40);
/*  98 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_CHECK).setMinWidth(40);
/*  99 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_CHECK).setMaxWidth(40);
/* 100 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_CHECK).setResizable(false);
/*     */ 
/* 102 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_CHECK).setCellEditor(new CheckBoxCellEditor());
/* 103 */     getColumnModel().getColumn(OrderCommonTableModel.COLUMN_CHECK).setHeaderRenderer(new CheckBoxHeader(new CheckBoxHeaderItemListener(this), this));
/*     */ 
/* 105 */     addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent e) {
/* 107 */         ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 108 */         gui.getPositionsPanel().clearPositionSelection();
/*     */       }
/*     */     });
/* 112 */     getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e) {
/* 115 */         if (OrdersTable.this.selectedGroupIds != null)
/* 116 */           OrdersTable.this.selectedGroupIds.clear();
/*     */       }
/*     */     });
/* 121 */     getModel().addTableModelListener(new JLocalizableTableModelListener(2, "tab.orders"));
/*     */   }
/*     */ 
/*     */   public void highlightOrderGroup(String highlightedGroupId)
/*     */   {
/* 130 */     this.highlightedGroupId = highlightedGroupId;
/* 131 */     TableSorter sorter = (TableSorter)getModel();
/* 132 */     OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*     */ 
/* 134 */     int[] rows = model.getGroupRows(highlightedGroupId, model.getPendingOrders());
/* 135 */     if ((rows[0] != -1) && (rows[1] != -1))
/*     */     {
/* 138 */       model.fireTableDataChanged();
/* 139 */       setRowSelectionInterval(sorter.viewIndex(rows[0]), sorter.viewIndex(rows[1]));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void refreshHighlight()
/*     */   {
/* 148 */     if (this.highlightedGroupId != null)
/* 149 */       highlightOrderGroup(this.highlightedGroupId);
/*     */   }
/*     */ 
/*     */   public void clearHighlight()
/*     */   {
/* 157 */     this.highlightedGroupId = null;
/* 158 */     getSelectionModel().clearSelection();
/*     */   }
/*     */ 
/*     */   public List<String> getSelectedGroupIds() {
/* 162 */     return this.selectedGroupIds;
/*     */   }
/*     */ 
/*     */   public void setSelectedGroupIds(List<String> selectedGroupIds) {
/* 166 */     getSelectionModel().clearSelection();
/* 167 */     this.selectedGroupIds = selectedGroupIds;
/* 168 */     repaint();
/*     */   }
/*     */ 
/*     */   public void valueChanged(ListSelectionEvent e)
/*     */   {
/* 173 */     super.valueChanged(e);
/* 174 */     repaint();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics graphics) {
/* 178 */     super.paint(graphics);
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 183 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 185 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_CHECK)).setHeaderValue("");
/* 186 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_TIMESTAMP)).setHeaderValue(LocalizationManager.getText("column.timestamp"));
/* 187 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_ID)).setHeaderValue(LocalizationManager.getText("column.order.id"));
/*     */ 
/* 189 */     if (GreedContext.isStrategyAllowed()) {
/* 190 */       model.getColumn(convertColumnIndexToView(2)).setHeaderValue(LocalizationManager.getText("column.ext.id"));
/* 191 */       model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_POSITION)).setHeaderValue(LocalizationManager.getText("column.position"));
/*     */     } else {
/* 193 */       model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_POSITION)).setHeaderValue(LocalizationManager.getText("column.position"));
/*     */     }
/*     */ 
/* 196 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_INSTRUMENT)).setHeaderValue(LocalizationManager.getText("column.instrument"));
/* 197 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_SIDE)).setHeaderValue(LocalizationManager.getText("column.side"));
/*     */ 
/* 199 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_REQ_AMOUNT)).setHeaderValue(LocalizationManager.getText("column.req.amount"));
/* 200 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_TYPE)).setHeaderValue(LocalizationManager.getText("column.type"));
/* 201 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_PRICE)).setHeaderValue(LocalizationManager.getText("column.price"));
/* 202 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_STATE)).setHeaderValue(LocalizationManager.getText("column.state"));
/* 203 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_PROPS)).setHeaderValue(LocalizationManager.getText("column.props"));
/* 204 */     model.getColumn(convertColumnIndexToView(OrderCommonTableModel.COLUMN_EXP)).setHeaderValue(LocalizationManager.getText("column.exp"));
/*     */   }
/*     */ 
/*     */   public String getTableId()
/*     */   {
/* 209 */     String id = super.getTableId();
/*     */ 
/* 211 */     if (GreedContext.isStrategyAllowed()) {
/* 212 */       id = id + "JClient";
/*     */     }
/*     */     else {
/* 215 */       id = id + "JForex";
/*     */     }
/*     */ 
/* 218 */     return id;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.OrdersTable
 * JD-Core Version:    0.6.0
 */