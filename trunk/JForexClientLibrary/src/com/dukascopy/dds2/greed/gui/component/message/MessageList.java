/*     */ package com.dukascopy.dds2.greed.gui.component.message;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MessageList extends JLocalizableTable
/*     */   implements IFontMonospceable
/*     */ {
/*  39 */   private static final Logger LOGGER = LoggerFactory.getLogger(MessageList.class);
/*     */   public static final String ID_JT_MESSAGELIST = "ID_JT_MESSAGELIST";
/*     */   MessageTableModel model;
/*     */ 
/*     */   public MessageList(boolean showTime)
/*     */   {
/*  49 */     super(new MessageTableModel());
/*  50 */     setName("ID_JT_MESSAGELIST");
/*     */ 
/*  52 */     ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
/*  53 */     this.model = ((MessageTableModel)getModel());
/*     */ 
/*  59 */     if (showTime) {
/*  60 */       getColumnModel().getColumn(0).setMaxWidth(150);
/*  61 */       getColumnModel().getColumn(0).setPreferredWidth(150);
/*     */     } else {
/*  63 */       TableColumn column = getColumnModel().getColumn(0);
/*  64 */       getColumnModel().removeColumn(column);
/*     */     }
/*     */ 
/*  68 */     addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e) {
/*  71 */         if ((e.getButton() == 1) && 
/*  72 */           (e.getClickCount() == 2))
/*  73 */           MessageList.this.doShowCompilingErrorLineInEditor();
/*     */       }
/*     */     });
/*  79 */     translate();
/*  80 */     FontManager.addFontMonospaceable(this);
/*     */   }
/*     */ 
/*     */   public void initFont()
/*     */   {
/*  85 */     if (GreedContext.get("settingsStorage") != null)
/*     */       try {
/*  87 */         AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*     */           public Object run() throws Exception {
/*  89 */             MessageList.this.setMonoSpacedFont(((ClientSettingsStorage)GreedContext.get("settingsStorage")).isConsoleFontMonospaced());
/*  90 */             return null;
/*     */           } } );
/*     */       } catch (PrivilegedActionException ex) {
/*  94 */         LOGGER.error("Error while setting mono spaced font", ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void addMessage(Notification message)
/*     */   {
/* 100 */     this.model.addMessage(message);
/* 101 */     ListSelectionModel lsm = getSelectionModel();
/* 102 */     lsm.removeSelectionInterval(0, 0);
/*     */   }
/*     */ 
/*     */   public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
/* 106 */     Component cell = super.prepareRenderer(renderer, row, column);
/* 107 */     Notification notification = this.model.getNotification(row);
/*     */ 
/* 109 */     if (notification != null) {
/* 110 */       if ("WARNING".equals(notification.getPriority()))
/* 111 */         cell.setBackground(GuiUtilsAndConstants.WARN_COLOR);
/* 112 */       else if ("ERROR".equals(notification.getPriority()))
/* 113 */         cell.setBackground(GuiUtilsAndConstants.ERR_COLOR);
/* 114 */       else if ("INFOCLIENT".equals(notification.getPriority()))
/* 115 */         cell.setBackground(GuiUtilsAndConstants.INFOCLIENT_COLOR);
/* 116 */       else if ("NOTIFCLIENT".equals(notification.getPriority()))
/* 117 */         cell.setBackground(GuiUtilsAndConstants.NOTIFCLIENT_COLOR);
/*     */       else
/* 119 */         cell.setBackground(GreedContext.GLOBAL_BACKGROUND);
/*     */     }
/*     */     else {
/* 122 */       cell.setBackground(GreedContext.GLOBAL_BACKGROUND);
/*     */     }
/*     */ 
/* 126 */     if (inSelected(row)) {
/* 127 */       cell.setBackground(getSelectionBackground());
/*     */ 
/* 129 */       cell.setForeground(GreedContext.SELECTION_FG_COLOR);
/*     */     }
/*     */     else {
/* 132 */       cell.setForeground(Color.BLACK);
/*     */     }
/*     */ 
/* 137 */     if (column == getColumnCount() - 1) {
/* 138 */       ((JComponent)cell).setToolTipText((String)getValueAt(row, column));
/*     */     }
/*     */ 
/* 141 */     return cell;
/*     */   }
/*     */ 
/*     */   public boolean inSelected(int x) {
/* 145 */     for (int i : getSelectedRows())
/* 146 */       if (i == x) return true;
/* 147 */     return false;
/*     */   }
/*     */ 
/*     */   private void doShowCompilingErrorLineInEditor()
/*     */   {
/* 152 */     int columnSelected = getColumnModel().getSelectedColumnCount();
/* 153 */     if (1 != columnSelected) {
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     MessageTableModel model = (MessageTableModel)getModel();
/* 158 */     int selectedRow = getSelectedRow();
/* 159 */     Notification notification = model.getNotification(selectedRow);
/* 160 */     String text = notification.getContent();
/*     */ 
/* 162 */     int panelId = notification.getPanelId();
/* 163 */     if (panelId == -1) return;
/*     */ 
/* 165 */     String key = "(at line";
/*     */ 
/* 167 */     int index = text.indexOf(key);
/* 168 */     if (index == -1) {
/* 169 */       return;
/*     */     }
/*     */ 
/* 172 */     int startIndex = index + key.length() + 1;
/*     */ 
/* 174 */     int endIndex = text.indexOf(41, startIndex);
/* 175 */     String number = text.substring(startIndex, endIndex);
/*     */ 
/* 177 */     int lineNumber = -1;
/*     */     try {
/* 179 */       lineNumber = Integer.parseInt(number);
/*     */     } catch (NumberFormatException ex) {
/* 181 */       LOGGER.info(ex.getMessage());
/* 182 */       return;
/*     */     }
/*     */ 
/* 185 */     IChartTabsAndFramesController chartTabsAndFramesController = ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController();
/* 186 */     chartTabsAndFramesController.selectLineNumber(panelId, lineNumber);
/*     */   }
/*     */ 
/*     */   public void set_model(MessageTableModel _model)
/*     */   {
/* 191 */     this.model = _model;
/*     */   }
/*     */ 
/*     */   public MessageTableModel get_model() {
/* 195 */     return this.model;
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/* 200 */     if (getColumnModel().getColumnCount() > 1) {
/* 201 */       getColumnModel().getColumn(0).setHeaderValue(LocalizationManager.getText("column.time"));
/* 202 */       getColumnModel().getColumn(1).setHeaderValue(LocalizationManager.getText("column.message"));
/*     */     } else {
/* 204 */       getColumnModel().getColumn(0).setHeaderValue(LocalizationManager.getText("column.message"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearMessages() {
/* 209 */     get_model().clear();
/*     */   }
/*     */ 
/*     */   public void setMonoSpacedFont(boolean isMonoSpaced) {
/* 213 */     int currSize = getFont().getSize();
/*     */     Font font;
/*     */     Font font;
/* 216 */     if (isMonoSpaced)
/* 217 */       font = new Font("Monospaced", 0, currSize);
/*     */     else {
/* 219 */       font = LocalizationManager.getDefaultFont(getFont().getSize());
/*     */     }
/*     */ 
/* 222 */     setFont(font);
/* 223 */     repaint();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.MessageList
 * JD-Core Version:    0.6.0
 */