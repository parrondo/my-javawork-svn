/*     */ package com.dukascopy.dds2.greed.gui.component.news;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.BasicDecoratedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableHeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTabbedPane;
/*     */ import com.dukascopy.dds2.greed.util.GuiResourceLoader;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class NewsFrame extends BasicDecoratedFrame
/*     */   implements NewsListener
/*     */ {
/*  37 */   private static Logger LOGGER = LoggerFactory.getLogger(NewsFrame.class);
/*     */ 
/*  39 */   private final Dimension DIM = new Dimension(900, 400);
/*     */   private NewsPanel newsPanel;
/*     */   private NewsSearchPanel newsSearchPanel;
/*     */   private JTabbedPane tPane;
/*  44 */   private static NewsFrame newsFrame = null;
/*     */ 
/*     */   public static NewsFrame getInstance() {
/*  47 */     if ((newsFrame == null) || (!newsFrame.isDisplayable()))
/*  48 */       newsFrame = new NewsFrame();
/*     */     else {
/*  50 */       newsFrame.setState(0);
/*     */     }
/*  52 */     return newsFrame;
/*     */   }
/*     */ 
/*     */   private NewsFrame()
/*     */   {
/*  58 */     setTitle("frame.news");
/*     */ 
/*  60 */     JPanel content = new JPanel();
/*  61 */     content.setLayout(new BoxLayout(content, 1));
/*     */ 
/*  63 */     this.newsPanel = new NewsPanel(this);
/*  64 */     this.newsSearchPanel = new NewsSearchPanel();
/*  65 */     this.tPane = new JLocalizableTabbedPane()
/*     */     {
/*     */       public void translate()
/*     */       {
/*  69 */         NewsFrame.this.doTranslateTabbedPane();
/*     */       }
/*     */     };
/*  74 */     this.tPane.setBackground(SystemColor.window);
/*  75 */     this.tPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
/*  76 */     this.tPane.add(LocalizationManager.getText("tab.market.news"), this.newsPanel);
/*  77 */     this.tPane.add(LocalizationManager.getText("tab.news.search"), this.newsSearchPanel);
/*  78 */     this.tPane.setBackgroundAt(0, null);
/*  79 */     this.tPane.setBackgroundAt(1, null);
/*  80 */     this.tPane.addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent changeEvent) {
/*  82 */         NewsFrame.this.tPane.setIconAt(NewsFrame.this.tPane.getSelectedIndex(), null);
/*     */       }
/*     */     });
/*  85 */     HeaderPanel comp = new JLocalizableHeaderPanel("header.news.explorer", false);
/*     */ 
/*  87 */     content.add(comp);
/*  88 */     content.add(this.tPane);
/*  89 */     getContentPane().add(content);
/*  90 */     NewsAdapter newsAdapter = (NewsAdapter)GreedContext.get("newsAdapter");
/*  91 */     newsAdapter.subscribe(this.newsPanel);
/*  92 */     this.newsPanel.newsArrived();
/*     */ 
/*  94 */     setSize(this.DIM);
/*  95 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/*     */ 
/*  97 */     setVisible(true);
/*  98 */     setAlwaysOnTop(true);
/*  99 */     setDefaultCloseOperation(2);
/*     */ 
/* 101 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosed(WindowEvent e) {
/* 103 */         ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void newsArrived() {
/* 109 */     if (this.tPane.getSelectedIndex() == 0)
/* 110 */       return;
/* 111 */     String iconFile = "rc/media/newsframe_new_arrived.png";
/*     */     try
/*     */     {
/* 114 */       Icon icon = GuiResourceLoader.getInstance().loadImageIcon(iconFile);
/* 115 */       this.tPane.setIconAt(0, icon);
/*     */     } catch (Exception e) {
/* 117 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doTranslateTabbedPane()
/*     */   {
/* 123 */     if (this.tPane == null) return;
/* 124 */     this.tPane.setTitleAt(0, LocalizationManager.getText("tab.market.news"));
/* 125 */     this.tPane.setTitleAt(1, LocalizationManager.getText("tab.news.search"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.news.NewsFrame
 * JD-Core Version:    0.6.0
 */