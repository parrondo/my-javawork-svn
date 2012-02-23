/*    */ package com.dukascopy.dds2.greed.gui.component.message;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.datatransfer.Clipboard;
/*    */ import java.awt.datatransfer.StringSelection;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.text.DateFormat;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.AbstractAction;
/*    */ 
/*    */ public class ActionCopyMessages extends AbstractAction
/*    */ {
/*    */   private MessagePanel messagePanel;
/*    */   private Clipboard clipboard;
/*    */   private DateFormat dateFormat;
/*    */ 
/*    */   public ActionCopyMessages(MessagePanel messagePanel)
/*    */   {
/* 31 */     super("item.copy.message");
/* 32 */     this.messagePanel = messagePanel;
/* 33 */     this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 34 */     this.dateFormat = new SimpleDateFormat("HH:mm:ss");
/* 35 */     this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 40 */     List notifications = getSelectedMessages();
/* 41 */     if (notifications.size() > 0) {
/* 42 */       StringBuffer temp = new StringBuffer();
/*    */ 
/* 44 */       for (Notification notification : notifications) {
/* 45 */         Date date = notification.getTimestamp();
/* 46 */         String time = null != date ? this.dateFormat.format(date) : "";
/* 47 */         temp.append(time).append(" ").append(notification.getContent()).append("\n");
/*    */       }
/*    */ 
/* 50 */       StringSelection contents = new StringSelection(temp.toString());
/* 51 */       this.clipboard.setContents(contents, null);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected List<Notification> getSelectedMessages()
/*    */   {
/* 58 */     MessageList list = this.messagePanel.getMessageList();
/*    */ 
/* 60 */     int[] selectedRows = list.getSelectedRows();
/* 61 */     MessageTableModel model = (MessageTableModel)list.getModel();
/*    */ 
/* 63 */     List result = new LinkedList();
/*    */ 
/* 65 */     for (int i : selectedRows) {
/* 66 */       Notification notification = model.getNotification(i);
/* 67 */       if (notification != null) {
/* 68 */         result.add(notification);
/*    */       }
/*    */     }
/*    */ 
/* 72 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.ActionCopyMessages
 * JD-Core Version:    0.6.0
 */