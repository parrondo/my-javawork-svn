/*     */ package com.dukascopy.dds2.greed.gui.component.popup;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessageList;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessageTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableFrame;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.border.Border;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PopupListener extends MouseAdapter
/*     */ {
/*  45 */   private static final Logger LOGGER = LoggerFactory.getLogger(PopupListener.class);
/*     */   protected JPopupMenu popup;
/*     */ 
/*     */   public PopupListener(JPopupMenu popup)
/*     */   {
/*  54 */     this.popup = popup;
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*  63 */     mayBeShowPopup(e);
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/*  70 */     mayBeShowPopup(e);
/*     */   }
/*     */ 
/*     */   protected void mayBeShowPopup(MouseEvent e)
/*     */   {
/*  78 */     if (e.isPopupTrigger())
/*     */     {
/*  80 */       if (GreedContext.isStrategyAllowed()) {
/*  81 */         MessageList list = (MessageList)e.getSource();
/*     */ 
/*  83 */         if (mustShowStackTrace(list)) {
/*  84 */           JLocalizableMenuItem item = (JLocalizableMenuItem)this.popup.getComponent(this.popup.getComponentCount() - 1);
/*     */ 
/*  86 */           item.setVisible(true);
/*  87 */           item.setAction(new ShowExceptionAction("item.show.full.stack", list));
/*     */         }
/*     */         else
/*     */         {
/*  91 */           JMenuItem item = (JMenuItem)this.popup.getComponent(this.popup.getComponentCount() - 1);
/*  92 */           item.setVisible(false);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  98 */       this.popup.show(e.getComponent(), e.getX(), e.getY());
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean mustShowStackTrace(MessageList list)
/*     */   {
/* 104 */     int[] selectedRows = null;
/*     */ 
/* 106 */     if (list == null) return false;
/*     */ 
/* 108 */     selectedRows = list.getSelectedRows();
/* 109 */     if (selectedRows.length != 1) return false;
/*     */ 
/* 111 */     int row = list.getSelectedRow();
/*     */ 
/* 113 */     MessageTableModel model = (MessageTableModel)list.getModel();
/* 114 */     Notification notification = model.getNotification(row);
/*     */ 
/* 116 */     if (notification == null) return false;
/* 117 */     return notification.getFullStackTrace() != null;
/*     */   }
/*     */ 
/*     */   private void showFulStackTrace(Notification notification)
/*     */   {
/* 161 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 162 */     JLocalizableFrame frame = new JLocalizableFrame("frame.full.stack.trace");
/* 163 */     frame.setLocationRelativeTo(clientForm);
/*     */     try
/*     */     {
/* 166 */       frame.setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */     } catch (Exception e) {
/* 168 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 171 */     JPanel contentPanel = new JPanel();
/* 172 */     contentPanel.setLayout(new BoxLayout(contentPanel, 1));
/*     */ 
/* 174 */     Dimension areaSize = new Dimension(800, 450);
/*     */ 
/* 176 */     JPanel stackTracePanel = new JPanel();
/* 177 */     stackTracePanel.setPreferredSize(areaSize);
/* 178 */     stackTracePanel.setSize(areaSize);
/*     */ 
/* 180 */     BoxLayout bl = new BoxLayout(stackTracePanel, 1);
/* 181 */     stackTracePanel.setLayout(bl);
/*     */ 
/* 184 */     JTextArea area = new JTextArea();
/*     */ 
/* 186 */     JScrollPane scrollPane = new JScrollPane(area);
/* 187 */     scrollPane.setVerticalScrollBarPolicy(20);
/* 188 */     scrollPane.setHorizontalScrollBarPolicy(30);
/*     */ 
/* 190 */     Font font = new Font("Tahoma", 0, 12);
/* 191 */     area.setFont(font);
/*     */ 
/* 193 */     area.setEditable(false);
/*     */ 
/* 195 */     area.setSize(areaSize);
/*     */ 
/* 197 */     area.setText(notification.getFullStackTrace());
/*     */ 
/* 199 */     stackTracePanel.add(scrollPane);
/*     */ 
/* 207 */     Border border = new JRoundedBorder(stackTracePanel);
/* 208 */     stackTracePanel.setBorder(border);
/*     */ 
/* 211 */     JPanel closePanel = new JPanel();
/* 212 */     closePanel.setLayout(new FlowLayout(1));
/*     */ 
/* 214 */     JLocalizableButton closeB = new JLocalizableButton("button.close");
/*     */ 
/* 216 */     Dimension buttonsSize = new Dimension(closeB.getPreferredSize().width + 140, closeB.getPreferredSize().height);
/*     */ 
/* 218 */     closeB.setPreferredSize(buttonsSize);
/* 219 */     closeB.setSize(buttonsSize);
/* 220 */     closeB.setMaximumSize(buttonsSize);
/*     */ 
/* 222 */     closeB.addActionListener(new ActionListener(frame)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 227 */         this.val$frame.setVisible(false);
/* 228 */         this.val$frame.dispose();
/*     */       }
/*     */     });
/* 233 */     closePanel.add(closeB);
/*     */ 
/* 235 */     contentPanel.add(Box.createVerticalStrut(5));
/* 236 */     contentPanel.add(stackTracePanel);
/* 237 */     contentPanel.add(closePanel);
/* 238 */     contentPanel.add(Box.createVerticalStrut(5));
/*     */ 
/* 240 */     frame.getContentPane().add(contentPanel);
/*     */ 
/* 242 */     frame.setSize(800, 400);
/* 243 */     frame.setResizable(false);
/* 244 */     frame.setVisible(true);
/*     */   }
/*     */ 
/*     */   private class ShowExceptionAction extends AbstractAction
/*     */   {
/*     */     private MessageList list;
/*     */ 
/*     */     public MessageList getList()
/*     */     {
/* 129 */       return this.list;
/*     */     }
/*     */ 
/*     */     public void setList(MessageList list)
/*     */     {
/* 134 */       this.list = list;
/*     */     }
/*     */ 
/*     */     public ShowExceptionAction(String name, MessageList list)
/*     */     {
/* 139 */       super();
/* 140 */       this.list = list;
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 146 */       if (this.list == null) return;
/*     */ 
/* 148 */       int selectedRow = this.list.getSelectedRow();
/* 149 */       MessageTableModel model = (MessageTableModel)this.list.getModel();
/* 150 */       Notification notification = model.getNotification(selectedRow);
/*     */ 
/* 153 */       PopupListener.this.showFulStackTrace(notification);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.popup.PopupListener
 * JD-Core Version:    0.6.0
 */