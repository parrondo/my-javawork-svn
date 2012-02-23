/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*    */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*    */ import java.awt.Container;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.BoxLayout;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class NewsFrame extends BasicDecoratedFrame
/*    */ {
/* 22 */   private static final Dimension SIZE = new Dimension(900, 400);
/*    */ 
/* 24 */   private static NewsFrame instance = null;
/*    */   private final ActionListener actionListener;
/*    */   private final DowJonesNewsPanel newsPanel;
/*    */ 
/*    */   public static NewsFrame getInstance(ActionListener actionListener)
/*    */   {
/* 29 */     if ((instance == null) || (!instance.isDisplayable()))
/* 30 */       instance = new NewsFrame(actionListener);
/*    */     else {
/* 32 */       instance.setState(0);
/*    */     }
/*    */ 
/* 35 */     instance.setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 36 */     return instance;
/*    */   }
/*    */ 
/*    */   public static NewsFrame getInstance() {
/* 40 */     return instance;
/*    */   }
/*    */ 
/*    */   private NewsFrame(ActionListener actionListener) {
/* 44 */     this.actionListener = actionListener;
/* 45 */     setTitle("tab.dowjones.news");
/*    */ 
/* 47 */     JPanel content = new JPanel();
/* 48 */     content.setLayout(new BoxLayout(content, 1));
/*    */ 
/* 50 */     this.newsPanel = new DowJonesNewsPanel();
/* 51 */     HeaderPanel header = new JLocalizableHeaderPanel("tab.dowjones.news", false);
/*    */ 
/* 53 */     content.add(header);
/* 54 */     content.add(this.newsPanel);
/* 55 */     getContentPane().add(content);
/*    */ 
/* 57 */     setSize(SIZE);
/* 58 */     setMinimumSize(SIZE);
/*    */ 
/* 60 */     setVisible(true);
/* 61 */     setDefaultCloseOperation(2);
/*    */   }
/*    */ 
/*    */   public DowJonesNewsPanel getNewsPanel() {
/* 65 */     return this.newsPanel;
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/* 70 */     super.dispose();
/* 71 */     instance = null;
/* 72 */     this.newsPanel.unsubscribe();
/* 73 */     this.actionListener.actionPerformed(null);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.NewsFrame
 * JD-Core Version:    0.6.0
 */