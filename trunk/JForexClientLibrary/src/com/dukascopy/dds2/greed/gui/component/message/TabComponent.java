/*    */ package com.dukascopy.dds2.greed.gui.component.message;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.ButtonTabPanelForBottomPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager.Language;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JLabel;
/*    */ 
/*    */ public class TabComponent extends ButtonTabPanelForBottomPanel
/*    */ {
/* 17 */   private final Color WARN_COLOR = new Color(188, 142, 3);
/*    */ 
/* 19 */   private String currentPriority = null;
/*    */   private final Font infoFont;
/*    */   private final Color infoColor;
/* 22 */   private boolean selected = false;
/*    */   private Font boldedFont;
/*    */ 
/*    */   public TabComponent(int id, String text, ActionListener actionListener)
/*    */   {
/* 26 */     super(id, text, actionListener);
/* 27 */     this.infoFont = getFont();
/* 28 */     this.infoColor = getForeground();
/* 29 */     this.boldedFont = new Font(this.infoFont.getName(), 1, this.infoFont.getSize());
/*    */   }
/*    */ 
/*    */   public void messageAdded(Notification message) {
/* 33 */     if (this.selected) {
/* 34 */       return;
/*    */     }
/*    */ 
/* 37 */     Font neededFont = LocalizationManager.getLanguage().equals(LocalizationManager.Language.CHINESE) ? this.infoFont : this.boldedFont;
/*    */ 
/* 39 */     if (this.currentPriority == null) {
/* 40 */       if ("ERROR".equals(message.getPriority())) {
/* 41 */         this.textLabel.setForeground(Color.RED);
/* 42 */         this.textLabel.setFont(neededFont);
/* 43 */         this.currentPriority = "ERROR";
/* 44 */       } else if ("WARNING".equals(message.getPriority())) {
/* 45 */         this.textLabel.setForeground(Color.YELLOW);
/* 46 */         this.textLabel.setFont(neededFont);
/* 47 */         this.currentPriority = "WARNING";
/*    */       } else {
/* 49 */         this.textLabel.setForeground(this.infoColor);
/* 50 */         this.textLabel.setFont(neededFont);
/* 51 */         this.currentPriority = "INFO";
/*    */       }
/* 53 */     } else if (this.currentPriority.equals("INFO")) {
/* 54 */       if ("ERROR".equals(message.getPriority())) {
/* 55 */         this.textLabel.setForeground(Color.RED);
/* 56 */         this.textLabel.setFont(neededFont);
/* 57 */         this.currentPriority = "ERROR";
/* 58 */       } else if ("WARNING".equals(message.getPriority())) {
/* 59 */         this.textLabel.setForeground(this.WARN_COLOR);
/* 60 */         this.textLabel.setFont(neededFont);
/* 61 */         this.currentPriority = "WARNING";
/*    */       }
/* 63 */     } else if ((this.currentPriority.equals("WARNING")) && 
/* 64 */       ("ERROR".equals(message.getPriority()))) {
/* 65 */       this.textLabel.setForeground(Color.RED);
/* 66 */       this.textLabel.setFont(neededFont);
/* 67 */       this.currentPriority = "ERROR";
/*    */     }
/*    */   }
/*    */ 
/*    */   public void tabSelected()
/*    */   {
/* 73 */     this.selected = true;
/* 74 */     this.textLabel.setForeground(this.infoColor);
/* 75 */     this.textLabel.setFont(this.infoFont);
/* 76 */     this.currentPriority = null;
/*    */   }
/*    */ 
/*    */   public void tabDeselected() {
/* 80 */     this.selected = false;
/*    */   }
/*    */ 
/*    */   public void setCloseButtonEnabled(boolean enabled) {
/* 84 */     if (enabled) {
/* 85 */       if (!isCloseButtonEnabled()) {
/* 86 */         add(this.closeTabButton);
/*    */       }
/*    */     }
/* 89 */     else if (isCloseButtonEnabled())
/* 90 */       remove(this.closeTabButton);
/*    */   }
/*    */ 
/*    */   public boolean isCloseButtonEnabled()
/*    */   {
/* 96 */     return isAncestorOf(this.closeTabButton);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.TabComponent
 * JD-Core Version:    0.6.0
 */