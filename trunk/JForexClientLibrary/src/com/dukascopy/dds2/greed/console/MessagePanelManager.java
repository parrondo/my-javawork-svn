/*     */ package com.dukascopy.dds2.greed.console;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JPanel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MessagePanelManager
/*     */ {
/*  34 */   private static final Logger LOGGER = LoggerFactory.getLogger(MessagePanelManager.class);
/*     */   private static MessagePanelManager instance;
/*  37 */   private static Map<String, MessagePanelWrapper> messagePanelsMap = new ConcurrentHashMap();
/*     */ 
/*     */   public static MessagePanelManager getInstance()
/*     */   {
/*  44 */     return instance;
/*     */   }
/*     */ 
/*     */   public MessagePanel getPanel(String key) {
/*  48 */     return getPanel(key, key);
/*     */   }
/*     */ 
/*     */   public MessagePanel getPanel(String key, String title) {
/*  52 */     return getPanel(key, title, false);
/*     */   }
/*     */ 
/*     */   public MessagePanel getPanel(String key, String title, boolean select)
/*     */   {
/*  60 */     MessagePanelWrapper wrapper = (MessagePanelWrapper)messagePanelsMap.get(key);
/*  61 */     if (wrapper == null) {
/*  62 */       int panelId = IdManager.getInstance().getNextChartId();
/*     */       try
/*     */       {
/*  65 */         MessagePanel panel = (MessagePanel)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public MessagePanel run() {
/*  68 */             return new MessagePanel(true);
/*     */           }
/*     */         });
/*  73 */         panel.build();
/*     */ 
/*  75 */         JLocalizableButton btnCopyMessage = new JLocalizableButton();
/*  76 */         btnCopyMessage.setAction(panel.getActionCopyMessages());
/*  77 */         JLocalizableButton btnClear = new JLocalizableButton();
/*  78 */         btnClear.setAction(panel.getActionClearLog());
/*     */ 
/*  80 */         JPanel pnlButtonsInner = new JPanel(new GridLayout(1, 0, 5, 0));
/*  81 */         pnlButtonsInner.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  82 */         pnlButtonsInner.add(btnCopyMessage);
/*  83 */         pnlButtonsInner.add(btnClear);
/*     */ 
/*  85 */         JPanel pnlButtons = new JPanel(new BorderLayout());
/*  86 */         pnlButtons.add(pnlButtonsInner, "West");
/*     */ 
/*  88 */         panel.add(pnlButtons, "North");
/*     */ 
/*  90 */         wrapper = new MessagePanelWrapper(panel, panelId, title);
/*  91 */         putPanel(key, wrapper);
/*     */       } catch (PrivilegedActionException ex) {
/*  93 */         LOGGER.error(ex.getMessage(), ex);
/*     */ 
/*  95 */         return null;
/*     */       }
/*     */     }
/*  98 */     JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 100 */     clientFormLayoutManager.addMessagesPanel(wrapper.getPanelId(), wrapper.getMessagePanel(), title, false, true, true, select, false);
/* 101 */     return wrapper.getMessagePanel();
/*     */   }
/*     */ 
/*     */   public String getKeyByPanelId(int panelId)
/*     */   {
/* 106 */     String result = null;
/* 107 */     Iterator keysIterator = messagePanelsMap.keySet().iterator();
/*     */ 
/* 109 */     while (keysIterator.hasNext())
/*     */     {
/* 111 */       String key = (String)keysIterator.next();
/* 112 */       MessagePanelWrapper wrapper = (MessagePanelWrapper)messagePanelsMap.get(key);
/*     */ 
/* 114 */       if ((wrapper != null) && (wrapper.getPanelId() == panelId)) {
/* 115 */         result = key;
/* 116 */         break;
/*     */       }
/*     */     }
/* 119 */     return result;
/*     */   }
/*     */ 
/*     */   public void putPanel(String key, MessagePanelWrapper panel) {
/* 123 */     messagePanelsMap.put(key, panel);
/*     */   }
/*     */ 
/*     */   public void removePanel(String key)
/*     */   {
/* 128 */     MessagePanelWrapper wrapper = (MessagePanelWrapper)messagePanelsMap.get(key);
/* 129 */     if (wrapper != null) {
/* 130 */       JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 132 */       clientFormLayoutManager.closeMessagesPanel(wrapper);
/*     */     }
/*     */ 
/* 135 */     messagePanelsMap.remove(key);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  40 */     instance = new MessagePanelManager();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.console.MessagePanelManager
 * JD-Core Version:    0.6.0
 */