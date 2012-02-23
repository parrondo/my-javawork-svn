/*     */ package com.dukascopy.dds2.greed.gui.component.message;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.popup.PopupListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MessagePanel extends AbstractMessagePanel
/*     */ {
/*  32 */   private static Logger LOGGER = LoggerFactory.getLogger(MessagePanel.class);
/*     */   public static final String ID_JT_MESSAGEPANEL = "ID_JT_MESSAGEPANEL";
/*     */   protected MessageList messageList;
/*     */   protected JScrollPane scroll;
/*     */   protected boolean showTime;
/*  40 */   private ActionClearMessage actClearMessages = new ActionClearMessage(this);
/*  41 */   private ActionCopyMessages actCopyMessages = new ActionCopyMessages(this);
/*     */ 
/*     */   public MessagePanel(boolean showTime)
/*     */   {
/*  45 */     setName("ID_JT_MESSAGEPANEL");
/*  46 */     this.showTime = showTime;
/*     */   }
/*     */ 
/*     */   public AbstractAction getActionClearLog() {
/*  50 */     return this.actClearMessages;
/*     */   }
/*     */ 
/*     */   public AbstractAction getActionCopyMessages() {
/*  54 */     return this.actCopyMessages;
/*     */   }
/*     */ 
/*     */   public void build() {
/*  58 */     setLayout(new BorderLayout());
/*  59 */     this.messageList = new MessageList(this.showTime);
/*  60 */     this.scroll = new JScrollPane(this.messageList);
/*  61 */     this.scroll.setVerticalScrollBarPolicy(22);
/*  62 */     this.scroll.setHorizontalScrollBarPolicy(30);
/*  63 */     this.scroll.setPreferredSize(new Dimension(0, 100));
/*  64 */     this.scroll.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  65 */     this.scroll.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/*  66 */     this.messageList.setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  67 */     this.messageList.setSelectionBackground(GreedContext.SELECTION_COLOR);
/*  68 */     this.messageList.getTableHeader().setReorderingAllowed(false);
/*  69 */     this.messageList.getTableHeader().setResizingAllowed(false);
/*     */ 
/*  72 */     JPopupMenu mnuPopup = new JPopupMenu();
/*  73 */     mnuPopup.add(new JLocalizableMenuItem(this.actCopyMessages));
/*  74 */     mnuPopup.add(new JLocalizableMenuItem(this.actClearMessages));
/*     */ 
/*  76 */     mnuPopup.add(new JLocalizableMenuItem("item.show.full.stack"));
/*     */ 
/*  79 */     PopupListener listener = new PopupListener(mnuPopup);
/*  80 */     this.messageList.addMouseListener(listener);
/*     */ 
/*  82 */     if (!this.showTime)
/*  83 */       add(this.scroll);
/*     */   }
/*     */ 
/*     */   public void postMessage(Notification message)
/*     */   {
/*  88 */     postMessage(message, false);
/*     */   }
/*     */ 
/*     */   public void postMessage(Notification message, boolean isLocal) {
/*  92 */     message.setContent(message.getContent().trim());
/*  93 */     this.messageList.addMessage(message);
/*  94 */     notifyTabComponent(message);
/*  95 */     showMessages();
/*     */ 
/*  97 */     if ((!isLocal) && (GreedContext.isActivityLoggingEnabled()) && (this.showTime)) {
/*  98 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*  99 */       String msg = message.getContent();
/* 100 */       if (message.getServerTimestamp() == null) {
/* 101 */         logger.add(msg);
/*     */       } else {
/* 103 */         SimpleDateFormat gmtDF = new SimpleDateFormat("yyyyMMddHHmmss");
/* 104 */         gmtDF.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */         try
/*     */         {
/* 107 */           Date serverTime = gmtDF.parse(message.getServerTimestamp());
/* 108 */           logger.add(msg, Long.toString(serverTime.getTime()));
/*     */         } catch (ParseException e) {
/* 110 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void postMessage(String message) {
/* 117 */     postMessage(message, false);
/*     */   }
/*     */ 
/*     */   public void postMessage(String message, boolean isLocal) {
/* 121 */     postMessage(new Notification(null, message), isLocal);
/*     */   }
/*     */ 
/*     */   public void clearMessageLog() {
/* 125 */     this.messageList.clearMessages();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 129 */     return this.messageList.getRowCount() == 0;
/*     */   }
/*     */ 
/*     */   protected void showMessages() {
/* 133 */     if ((this.showTime) && (!this.scroll.isShowing())) {
/* 134 */       add(this.scroll);
/* 135 */       doLayout();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMessageList(MessageList messageList)
/*     */   {
/* 146 */     removeAll();
/* 147 */     this.messageList = messageList;
/* 148 */     build();
/*     */   }
/*     */ 
/*     */   public MessageList getMessageList() {
/* 152 */     return this.messageList;
/*     */   }
/*     */ 
/*     */   public JScrollPane getScroll() {
/* 156 */     return this.scroll;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.MessagePanel
 * JD-Core Version:    0.6.0
 */