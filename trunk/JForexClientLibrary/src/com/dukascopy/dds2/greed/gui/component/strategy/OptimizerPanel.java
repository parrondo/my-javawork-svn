/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StrategyParameters;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ITesterReport;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.InstrumentReportData;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyReport;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterOrder;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterReportData.TesterEvent;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterReportData.TesterEvent.EventType;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import com.dukascopy.dds2.greed.gui.table.renderers.AbstractTableCellRenderer;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OptimizerPanel extends JPanel
/*     */ {
/*  57 */   private static final Logger LOGGER = LoggerFactory.getLogger(OptimizerPanel.class);
/*     */   private JLocalizableTable tblResults;
/*     */   private OptimizerTableModel mdlResults;
/*     */   private ActionListener showReportListener;
/*     */   private ActionListener setParemetersListener;
/*     */   private ActionListener copySelectedListener;
/*     */   private ActionListener copyAllListener;
/*     */   private ActionListener cancelListener;
/*     */   private ExecutionControl executionControl;
/*     */ 
/*     */   public OptimizerPanel()
/*     */   {
/*  73 */     initListeners();
/*  74 */     initUI();
/*     */   }
/*     */ 
/*     */   private void initListeners()
/*     */   {
/*  81 */     this.showReportListener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  84 */         OptimizerPanel.this.showReport();
/*     */       }
/*     */     };
/*  87 */     this.setParemetersListener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  90 */         OptimizerPanel.this.setInputParameters();
/*     */       }
/*     */     };
/*  93 */     this.copySelectedListener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  96 */         OptimizerPanel.this.copySelectedToClipboard();
/*     */       }
/*     */     };
/*  99 */     this.copyAllListener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 102 */         OptimizerPanel.this.copyAllToClipboard();
/*     */       }
/*     */     };
/* 105 */     this.cancelListener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 108 */         OptimizerPanel.this.cancelReport();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private void setInputParameters() {
/* 115 */     int selected = this.tblResults.getSelectedRow();
/* 116 */     if (selected >= 0) {
/* 117 */       HashMap vars = (HashMap)this.tblResults.getValueAt(selected, -97);
/* 118 */       ITesterReport report = (ITesterReport)this.tblResults.getValueAt(selected, -73);
/*     */ 
/* 120 */       StrategyParameters.putParameters(report.getStrategyName(), vars);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void cancelReport() {
/* 125 */     int rowIndex = this.tblResults.getSelectedRow();
/* 126 */     if (rowIndex >= 0) {
/* 127 */       OptimizerData optimizerData = this.mdlResults.getOptimizerData(rowIndex);
/* 128 */       TesterReportData.TesterEvent event = new TesterReportData.TesterEvent();
/* 129 */       event.type = TesterReportData.TesterEvent.EventType.CANCELED_BY_USER;
/* 130 */       event.text = "Canceled by user";
/*     */ 
/* 132 */       synchronized (optimizerData.report) {
/* 133 */         optimizerData.report.addEvent(event);
/*     */       }
/*     */ 
/* 136 */       if (this.mdlResults != null) {
/* 137 */         this.mdlResults.removeReport(rowIndex);
/*     */       }
/*     */ 
/* 140 */       SwingUtilities.invokeLater(new Runnable()
/*     */       {
/*     */         public void run() {
/* 143 */           OptimizerPanel.this.tblResults.addNotify();
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private void showReport() {
/* 151 */     int selected = this.tblResults.getSelectedRow();
/* 152 */     if (selected >= 0) {
/* 153 */       ITesterReport report = (ITesterReport)this.tblResults.getValueAt(selected, -73);
/* 154 */       TesterAccount account = (TesterAccount)this.tblResults.getValueAt(selected, -78);
/*     */       try {
/* 156 */         File file = File.createTempFile("jforex_optimizer", ".html");
/* 157 */         file.deleteOnExit();
/* 158 */         StrategyReport.createReport(file, report, account.getCurrency(), true);
/* 159 */         Desktop.getDesktop().browse(file.toURI());
/*     */       } catch (IOException e) {
/* 161 */         LOGGER.error("Error showing report file.", e);
/*     */ 
/* 163 */         String message = LocalizationManager.getTextWithArguments("optimizer.dialog.error.template.cannot.open.report", new Object[] { e.getLocalizedMessage() });
/* 164 */         String title = LocalizationManager.getText("error.title");
/* 165 */         JOptionPane.showMessageDialog(this, message, title, 1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void copySelectedToClipboard() {
/* 171 */     int selected = this.tblResults.getSelectedRow();
/* 172 */     if (selected >= 0) {
/* 173 */       StringSelection contents = new StringSelection(getRowAsString(selected));
/*     */ 
/* 175 */       Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 176 */       clipboard.setContents(contents, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void copyAllToClipboard() {
/* 181 */     StringBuffer result = new StringBuffer();
/* 182 */     for (int i = 0; i < this.tblResults.getRowCount(); i++) {
/* 183 */       if (i > 0) {
/* 184 */         result.append(System.getProperty("line.separator"));
/*     */       }
/* 186 */       result.append(getRowAsString(i));
/*     */     }
/* 188 */     if (result.length() > 0) {
/* 189 */       StringSelection contents = new StringSelection(result.toString());
/*     */ 
/* 191 */       Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 192 */       clipboard.setContents(contents, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getRowAsString(int rowIndex) {
/* 197 */     StringBuffer result = new StringBuffer();
/*     */ 
/* 199 */     for (int i = 0; i < this.tblResults.getColumnCount(); i++) {
/* 200 */       int columnIndex = this.tblResults.getColumnModel().getColumn(i).getModelIndex();
/*     */ 
/* 204 */       Object value = this.tblResults.getValueAt(rowIndex, i);
/* 205 */       String name = this.mdlResults.getColumnName(columnIndex);
/* 206 */       String valueAsString = this.mdlResults.getValueAsString(value, columnIndex);
/*     */ 
/* 208 */       if (i > 0) {
/* 209 */         result.append("; ");
/*     */       }
/* 211 */       result.append(name).append(": ").append(valueAsString);
/*     */     }
/*     */ 
/* 214 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private void initUI()
/*     */   {
/* 221 */     this.mdlResults = new OptimizerTableModel();
/* 222 */     TableSorter sorter = new TableSorter(this.mdlResults)
/*     */     {
/*     */       public String getColumnName(int column) {
/* 225 */         return this.tableModel.getColumnName(column);
/*     */       }
/*     */     };
/* 228 */     this.tblResults = new JLocalizableTable(sorter)
/*     */     {
/*     */       public void translate() {
/* 231 */         SwingUtilities.invokeLater(new Runnable()
/*     */         {
/*     */           public void run() {
/* 234 */             for (int i = 0; i < OptimizerPanel.8.this.getColumnCount(); i++) {
/* 235 */               TableColumn column = OptimizerPanel.8.this.columnModel.getColumn(i);
/* 236 */               column.setHeaderValue(OptimizerPanel.8.this.getColumnName(i));
/*     */             }
/* 238 */             OptimizerPanel.8.this.getTableHeader().repaint();
/*     */           }
/*     */         });
/*     */       }
/*     */     };
/* 244 */     OptimizerCellRenderer cellRenderer = new OptimizerCellRenderer();
/* 245 */     for (int i = 0; i < this.mdlResults.getColumnCount() - 1; i++) {
/* 246 */       TableColumn column = this.tblResults.getColumnModel().getColumn(i);
/* 247 */       column.setPreferredWidth(100);
/* 248 */       column.setIdentifier(Integer.valueOf(i));
/* 249 */       column.setCellRenderer(cellRenderer);
/*     */     }
/* 251 */     int lastIndex = this.mdlResults.getColumnCount() - 1;
/* 252 */     TableColumn paramsColumn = this.tblResults.getColumnModel().getColumn(lastIndex);
/* 253 */     paramsColumn.setPreferredWidth(600);
/* 254 */     paramsColumn.setIdentifier(Integer.valueOf(6));
/* 255 */     paramsColumn.setCellRenderer(new ParamsCellRenderer());
/*     */ 
/* 257 */     this.tblResults.setBackground(GreedContext.GLOBAL_BACKGROUND);
/* 258 */     this.tblResults.setSelectionBackground(GreedContext.SELECTION_COLOR);
/* 259 */     this.tblResults.setSelectionMode(0);
/* 260 */     this.tblResults.setAutoResizeMode(3);
/* 261 */     this.tblResults.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseReleased(MouseEvent e) {
/* 264 */         OptimizerPanel.this.tblResults_mouseReleased(e);
/*     */       }
/*     */ 
/*     */       public void mouseClicked(MouseEvent e)
/*     */       {
/* 269 */         OptimizerPanel.this.tblResults_mouseClicked(e);
/*     */       }
/*     */     });
/* 273 */     setLayout(new BorderLayout());
/* 274 */     JScrollPane scpResults = new JScrollPane(this.tblResults);
/* 275 */     add(scpResults, "Center");
/* 276 */     sorter.setTableHeader(this.tblResults.getTableHeader());
/*     */   }
/*     */ 
/*     */   private void tblResults_mouseReleased(MouseEvent event) {
/* 280 */     if (event.isPopupTrigger())
/*     */     {
/* 282 */       int rowIndex = this.tblResults.rowAtPoint(event.getPoint());
/* 283 */       if (rowIndex >= 0)
/*     */       {
/* 285 */         this.tblResults.setRowSelectionInterval(rowIndex, rowIndex);
/*     */ 
/* 288 */         JPopupMenu popupMenu = new JPopupMenu();
/* 289 */         popupMenu.add(new JLocalizableMenuItem("optimizer.item.show.report", this.showReportListener));
/* 290 */         popupMenu.add(new JLocalizableMenuItem("optimizer.item.set.input.parameters", this.setParemetersListener));
/* 291 */         popupMenu.add(new JLocalizableMenuItem("optimizer.item.copy", this.copySelectedListener));
/* 292 */         popupMenu.add(new JLocalizableMenuItem("optimizer.item.copy.all", this.copyAllListener));
/*     */ 
/* 294 */         JLocalizableMenuItem cancelMenuItem = new JLocalizableMenuItem("button.cancel", this.cancelListener);
/* 295 */         popupMenu.add(cancelMenuItem);
/* 296 */         if (!this.executionControl.isExecuting()) {
/* 297 */           cancelMenuItem.setEnabled(false);
/*     */         }
/*     */ 
/* 300 */         popupMenu.show(this.tblResults, event.getX(), event.getY());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void tblResults_mouseClicked(MouseEvent e) {
/* 306 */     if ((!e.isPopupTrigger()) && (e.getClickCount() > 1))
/* 307 */       this.showReportListener.actionPerformed(new ActionEvent(this, 1001, "Report"));
/*     */   }
/*     */ 
/*     */   public void clearData()
/*     */   {
/* 312 */     this.mdlResults.clear();
/*     */   }
/*     */ 
/*     */   public void fireDataUpdated(TesterAccount account) {
/* 316 */     this.mdlResults.fireDataUpdated(account);
/*     */   }
/*     */ 
/*     */   public void fireDataUpdated(ITesterReport report) {
/* 320 */     this.mdlResults.fireDataUpdated(report);
/*     */   }
/*     */ 
/*     */   public void addTesterReport(TesterAccount account, ITesterReport report, HashMap<String, Variable> variables, IStrategy aStrategy) {
/* 324 */     this.mdlResults.addTesterReport(account, report, variables, aStrategy);
/*     */   }
/*     */ 
/*     */   public void removeReport(IStrategy removedStrategy) {
/* 328 */     this.mdlResults.removeReport(removedStrategy);
/*     */   }
/*     */ 
/*     */   public void setExecutionControl(ExecutionControl executionControl) {
/* 332 */     this.executionControl = executionControl;
/*     */   }
/*     */ 
/*     */   public static class OptimizerTableModel extends AbstractTableModel
/*     */   {
/*     */     static final int REPORT_COLUMN_INDEX = -73;
/*     */     static final int ACCOUNT_COLUMN_INDEX = -78;
/*     */     static final int VARIABLES_COLUMN_INDEX = -97;
/*     */     static final int IS_BLOCKED_COLUMN_INDEX = -67;
/*     */     static final int PASS_COLUMN_INDEX = 0;
/*     */     static final int EQUITY_COLUMN_INDEX = 1;
/*     */     static final int PROFIT_COLUMN_INDEX = 2;
/*     */     static final int BALANCE_COLUMN_INDEX = 3;
/*     */     static final int TOTAL_TRADES_COLUMN_INDEX = 4;
/*     */     static final int PROFIT_FACTOR_COLUMN_INDEX = 5;
/*     */     static final int PARAMS_COLUMN_INDEX = 6;
/* 436 */     private List<OptimizerPanel.OptimizerData> rows = new ArrayList();
/* 437 */     private DecimalFormat decFormatter = new DecimalFormat("#,##0.00");
/*     */ 
/*     */     public int getColumnCount()
/*     */     {
/* 441 */       return 7;
/*     */     }
/*     */ 
/*     */     public Class<?> getColumnClass(int columnIndex)
/*     */     {
/* 446 */       switch (columnIndex) {
/*     */       default:
/* 448 */         return Object.class;
/*     */       case 0:
/* 450 */         return Integer.class;
/*     */       case 1:
/* 452 */         return Double.class;
/*     */       case 2:
/* 454 */         return Double.class;
/*     */       case 3:
/* 456 */         return Double.class;
/*     */       case 4:
/* 458 */         return Long.class;
/*     */       case 5:
/* 460 */         return Double.class;
/*     */       case 6:
/* 462 */       }return Object.class;
/*     */     }
/*     */ 
/*     */     public String getColumnName(int columnIndex)
/*     */     {
/* 468 */       switch (columnIndex) {
/*     */       default:
/* 470 */         return "";
/*     */       case 0:
/* 472 */         return LocalizationManager.getText("tester.optimizer.table.column.pass");
/*     */       case 1:
/* 474 */         return LocalizationManager.getText("tester.optimizer.table.column.equity");
/*     */       case 2:
/* 476 */         return LocalizationManager.getText("tester.optimizer.table.column.profit.loss");
/*     */       case 3:
/* 478 */         return LocalizationManager.getText("tester.optimizer.table.column.balance");
/*     */       case 4:
/* 480 */         return LocalizationManager.getText("tester.optimizer.table.column.total.trades");
/*     */       case 5:
/* 482 */         return LocalizationManager.getText("tester.optimizer.table.column.profit.factor");
/*     */       case 6:
/* 484 */       }return LocalizationManager.getText("tester.optimizer.table.column.parameters");
/*     */     }
/*     */ 
/*     */     public int getRowCount()
/*     */     {
/* 490 */       return this.rows.size();
/*     */     }
/*     */ 
/*     */     public String getValueAsString(Object value, int columnIndex)
/*     */     {
/* 495 */       if (value == null) {
/* 496 */         return null;
/*     */       }
/*     */ 
/* 499 */       switch (columnIndex) {
/*     */       default:
/* 501 */         return null;
/*     */       case 0:
/* 503 */         return value.toString();
/*     */       case 4:
/* 505 */         return value.toString();
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 5:
/* 510 */         Double doubleValue = (Double)value;
/* 511 */         return this.decFormatter.format(doubleValue);
/*     */       case 6:
/* 513 */       }List parameterValues = (List)value;
/* 514 */       StringBuffer result = new StringBuffer();
/* 515 */       if (parameterValues != null) {
/* 516 */         for (String[] parameter : parameterValues) {
/* 517 */           if (result.length() > 0) {
/* 518 */             result.append(", ");
/*     */           }
/* 520 */           result.append(parameter[0]).append("=").append(parameter[1]);
/*     */         }
/*     */       }
/* 523 */       return result.toString();
/*     */     }
/*     */ 
/*     */     public Object getValueAt(int rowIndex, int columnIndex)
/*     */     {
/* 529 */       OptimizerPanel.OptimizerData data = getOptimizerData(rowIndex);
/*     */ 
/* 531 */       switch (columnIndex) {
/*     */       default:
/* 533 */         return null;
/*     */       case -73:
/* 536 */         return data.report;
/*     */       case -78:
/* 539 */         return data.account;
/*     */       case -97:
/* 542 */         return data.variables;
/*     */       case -67:
/* 545 */         return Boolean.valueOf(data.blocked);
/*     */       case 0:
/* 548 */         return Integer.valueOf(data.index);
/*     */       case 1:
/* 551 */         return Double.valueOf(data.account.getEquityActual());
/*     */       case 2:
/* 554 */         return Double.valueOf(data.account.getEquityActual() - data.report.getInitialDeposit());
/*     */       case 3:
/* 557 */         return Double.valueOf(data.account.getDeposit());
/*     */       case 5:
/* 560 */         double grossLoss = 0.0D;
/* 561 */         double grossProfit = 0.0D;
/*     */ 
/* 564 */         for (Instrument instrument : Instrument.values()) {
/* 565 */           InstrumentReportData report = data.report.getInstrumentReportData(instrument);
/* 566 */           if (report != null) {
/* 567 */             int size = report.closedOrders.size();
/* 568 */             for (int i = 0; i < size; i++) {
/* 569 */               TesterOrder order = (TesterOrder)report.closedOrders.get(i);
/*     */ 
/* 571 */               double commission = order.getCommissionInUSD();
/* 572 */               double profit = order.getProfitLossInUSD() - commission;
/*     */ 
/* 574 */               if (profit < 0.0D)
/* 575 */                 grossLoss += Math.abs(profit);
/*     */               else {
/* 577 */                 grossProfit += profit;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 583 */         if (grossLoss != 0.0D) {
/* 584 */           return Double.valueOf(grossProfit / grossLoss);
/*     */         }
/* 586 */         return Double.valueOf(grossProfit);
/*     */       case 4:
/* 591 */         long totalTrades = 0L;
/* 592 */         for (Instrument instrument : Instrument.values()) {
/* 593 */           InstrumentReportData instrumentReport = data.report.getInstrumentReportData(instrument);
/* 594 */           if (instrumentReport != null) {
/* 595 */             totalTrades += instrumentReport.ordersTotal;
/*     */           }
/*     */         }
/* 598 */         return Long.valueOf(totalTrades);
/*     */       case 6:
/*     */       }
/* 601 */       return data.report.getParameterValues();
/*     */     }
/*     */ 
/*     */     public OptimizerPanel.OptimizerData getOptimizerData(int rowIndex)
/*     */     {
/* 607 */       return (OptimizerPanel.OptimizerData)this.rows.get(rowIndex);
/*     */     }
/*     */ 
/*     */     public void addTesterReport(TesterAccount account, ITesterReport report, HashMap<String, Variable> variables, IStrategy aStrategy)
/*     */     {
/* 612 */       SwingUtilities.invokeLater(new Runnable(account, report, variables, aStrategy)
/*     */       {
/*     */         public void run() {
/* 615 */           int count = OptimizerPanel.OptimizerTableModel.this.getRowCount();
/* 616 */           OptimizerPanel.OptimizerTableModel.this.rows.add(new OptimizerPanel.OptimizerData(count + 1, this.val$account, this.val$report, this.val$variables, this.val$aStrategy));
/* 617 */           OptimizerPanel.OptimizerTableModel.this.fireTableRowsInserted(count, count);
/*     */         } } );
/*     */     }
/*     */ 
/*     */     public void fireDataUpdated(TesterAccount account) {
/* 623 */       SwingUtilities.invokeLater(new Runnable(account)
/*     */       {
/*     */         public void run() {
/* 626 */           for (int i = 0; i < OptimizerPanel.OptimizerTableModel.this.rows.size(); i++) {
/* 627 */             OptimizerPanel.OptimizerData data = (OptimizerPanel.OptimizerData)OptimizerPanel.OptimizerTableModel.this.rows.get(i);
/* 628 */             if (data.account == this.val$account)
/* 629 */               OptimizerPanel.OptimizerTableModel.this.fireTableRowsUpdated(i, i);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public void fireDataUpdated(ITesterReport report) {
/* 637 */       SwingUtilities.invokeLater(new Runnable(report)
/*     */       {
/*     */         public void run() {
/* 640 */           for (int i = 0; i < OptimizerPanel.OptimizerTableModel.this.rows.size(); i++) {
/* 641 */             OptimizerPanel.OptimizerData data = (OptimizerPanel.OptimizerData)OptimizerPanel.OptimizerTableModel.this.rows.get(i);
/* 642 */             if (data.report == this.val$report)
/* 643 */               OptimizerPanel.OptimizerTableModel.this.fireTableRowsUpdated(i, i);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 651 */       SwingUtilities.invokeLater(new Runnable()
/*     */       {
/*     */         public void run() {
/* 654 */           int size = OptimizerPanel.OptimizerTableModel.this.rows.size();
/* 655 */           OptimizerPanel.OptimizerTableModel.this.rows.clear();
/* 656 */           OptimizerPanel.OptimizerTableModel.this.fireTableRowsDeleted(0, size);
/*     */         } } );
/*     */     }
/*     */ 
/*     */     public void removeReport(IStrategy removedStrategy) {
/* 662 */       SwingUtilities.invokeLater(new Runnable(removedStrategy)
/*     */       {
/*     */         public void run() {
/* 665 */           for (int i = 0; i < OptimizerPanel.OptimizerTableModel.this.rows.size(); i++) {
/* 666 */             OptimizerPanel.OptimizerData row = (OptimizerPanel.OptimizerData)OptimizerPanel.OptimizerTableModel.this.rows.get(i);
/* 667 */             if (row.strategy == this.val$removedStrategy) {
/* 668 */               row.blocked = true;
/* 669 */               row.account.setDeposit(row.account.getEquityActual());
/* 670 */               OptimizerPanel.OptimizerTableModel.this.fireTableRowsUpdated(i, i);
/* 671 */               break;
/*     */             }
/*     */           }
/*     */         } } );
/*     */     }
/*     */ 
/*     */     public void removeReport(int index) {
/* 679 */       SwingUtilities.invokeLater(new Runnable(index)
/*     */       {
/*     */         public void run() {
/* 682 */           if (this.val$index < OptimizerPanel.OptimizerTableModel.this.rows.size()) {
/* 683 */             OptimizerPanel.OptimizerTableModel.this.rows.remove(this.val$index);
/* 684 */             OptimizerPanel.OptimizerTableModel.this.fireTableRowsUpdated(0, OptimizerPanel.OptimizerTableModel.this.rows.size());
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OptimizerData
/*     */   {
/*     */     final int index;
/*     */     final TesterAccount account;
/*     */     final ITesterReport report;
/*     */     final HashMap<String, Variable> variables;
/*     */     final IStrategy strategy;
/*     */     boolean blocked;
/*     */ 
/*     */     OptimizerData(int index, TesterAccount account, ITesterReport report, HashMap<String, Variable> variables, IStrategy aStrategy)
/*     */     {
/* 408 */       this.index = index;
/* 409 */       this.account = account;
/* 410 */       this.report = report;
/* 411 */       this.variables = variables;
/* 412 */       this.strategy = aStrategy;
/* 413 */       this.blocked = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ParamsCellRenderer extends OptimizerPanel.OptimizerCellRenderer
/*     */   {
/*     */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */     {
/* 368 */       List parameterValues = (List)value;
/* 369 */       JLabel result = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*     */ 
/* 371 */       TableColumn paramsColumn = table.getColumn(Integer.valueOf(column));
/* 372 */       if (result.getPreferredSize().width > paramsColumn.getWidth())
/* 373 */         result.setToolTipText(getParametersAsList(parameterValues));
/*     */       else {
/* 375 */         result.setToolTipText(null);
/*     */       }
/*     */ 
/* 378 */       return result;
/*     */     }
/*     */ 
/*     */     private String getParametersAsList(List<String[]> parameterValues) {
/* 382 */       StringBuffer result = new StringBuffer("<html>");
/* 383 */       if (parameterValues != null) {
/* 384 */         for (String[] parameter : parameterValues) {
/* 385 */           if (result.length() > 0) {
/* 386 */             result.append("<BR>");
/*     */           }
/* 388 */           result.append(parameter[0]).append("=").append(parameter[1]);
/*     */         }
/*     */       }
/* 391 */       result.append("</html>");
/* 392 */       return result.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OptimizerCellRenderer extends AbstractTableCellRenderer
/*     */   {
/*     */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */     {
/* 342 */       String asString = getValueAsString(table, value, column);
/*     */ 
/* 344 */       JLabel result = (JLabel)super.getTableCellRendererComponent(table, asString, isSelected, hasFocus, row, column);
/* 345 */       Boolean blocked = (Boolean)table.getValueAt(row, -67);
/* 346 */       result.setEnabled((blocked == null) || (!blocked.booleanValue()));
/* 347 */       return result;
/*     */     }
/*     */ 
/*     */     protected String getValueAsString(JTable table, Object value, int column) {
/* 351 */       int columnIndex = table.getColumnModel().getColumn(column).getModelIndex();
/* 352 */       TableSorter sorter = (TableSorter)table.getModel();
/* 353 */       OptimizerPanel.OptimizerTableModel model = (OptimizerPanel.OptimizerTableModel)sorter.getTableModel();
/* 354 */       return model.getValueAsString(value, columnIndex);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.OptimizerPanel
 * JD-Core Version:    0.6.0
 */