/*     */ package com.dukascopy.dds2.greed.gui.component.popup;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.ClearMessageLogAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessageList;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessageTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.JPopupMenu;
/*     */ 
/*     */ public class MessagesPopupMenu extends JPopupMenu
/*     */ {
/*  35 */   private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
/*     */ 
/*     */   public MessagesPopupMenu()
/*     */   {
/*  42 */     build();
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/*  49 */     this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/*  51 */     Action copyAction = new CopyAction("item.copy.message");
/*  52 */     JLocalizableMenuItem copyItem = new JLocalizableMenuItem(copyAction);
/*  53 */     add(copyItem);
/*     */ 
/*  55 */     JLocalizableMenuItem clearMessages = new JLocalizableMenuItem(new AbstractAction("item.clear.all") {
/*     */       public void actionPerformed(ActionEvent e) {
/*  57 */         GreedContext.publishEvent(new ClearMessageLogAction((MessageList)MessagesPopupMenu.this.getInvoker()));
/*     */       }
/*     */     });
/*  61 */     add(clearMessages);
/*     */ 
/*  63 */     if (GreedContext.isStrategyAllowed()) {
/*  64 */       JLocalizableMenuItem showExceptionItem = new JLocalizableMenuItem();
/*  65 */       showExceptionItem.setVisible(false);
/*  66 */       add(showExceptionItem);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CopyAction extends AbstractAction
/*     */   {
/*  75 */     private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/*     */ 
/*     */     public CopyAction(String name)
/*     */     {
/*  82 */       super();
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/*  90 */       MessageList list = (MessageList)MessagesPopupMenu.this.getInvoker();
/*     */ 
/*  92 */       int[] selectedRows = list.getSelectedRows();
/*  93 */       MessageTableModel model = (MessageTableModel)list.getModel();
/*     */ 
/*  95 */       String temp = "";
/*  96 */       Notification notification = null;
/*     */ 
/*  98 */       for (int i : selectedRows) {
/*  99 */         notification = model.getNotification(i);
/* 100 */         if (notification != null) {
/* 101 */           Date date = notification.getTimestamp();
/* 102 */           String time = null != date ? MessagesPopupMenu.this.dateFormat.format(date) : "";
/* 103 */           temp = temp + time + " " + notification.getContent() + "\n";
/*     */         }
/*     */       }
/*     */ 
/* 107 */       StringSelection contents = new StringSelection(temp);
/* 108 */       this.clipboard.setContents(contents, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.popup.MessagesPopupMenu
 * JD-Core Version:    0.6.0
 */