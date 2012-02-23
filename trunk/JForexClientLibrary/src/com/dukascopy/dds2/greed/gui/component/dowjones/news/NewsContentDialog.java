/*     */ package com.dukascopy.dds2.greed.gui.component.dowjones.news;
/*     */ 
/*     */ import com.dukascopy.api.INewsMessage;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.TimeZone;
/*     */ import java.util.Vector;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.text.html.HTMLEditorKit;
/*     */ import javax.swing.text.html.StyleSheet;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class NewsContentDialog extends JDialog
/*     */ {
/*  38 */   private static final Logger LOGGER = LoggerFactory.getLogger(NewsContentDialog.class);
/*     */ 
/*  40 */   private static final Image DIALOG_ICON = GuiUtilsAndConstants.PLATFPORM_ICON.getImage();
/*  41 */   private static final Dimension SIZE = new Dimension(600, 300);
/*  42 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat() {  } ;
/*  43 */   private static NewsContentDialog instance = null;
/*     */   private final String newsId;
/*  47 */   private final Vector<INewsMessage> news = new Vector();
/*     */   private final JProgressBar progressBar;
/*     */   private final JTabbedPane tabbedPane;
/*     */ 
/*  52 */   public NewsContentDialog(String newsId) { super((JFrame)GreedContext.get("clientGui"), "News", false);
/*  53 */     this.newsId = newsId;
/*  54 */     instance = this;
/*     */ 
/*  56 */     setIconImage(DIALOG_ICON);
/*     */ 
/*  58 */     setLayout(new BorderLayout());
/*     */ 
/*  60 */     setSize(SIZE);
/*  61 */     setMinimumSize(SIZE);
/*  62 */     setResizable(true);
/*     */ 
/*  64 */     this.progressBar = new JProgressBar()
/*     */     {
/*     */     };
/*  67 */     add(this.progressBar, "North");
/*     */ 
/*  69 */     this.tabbedPane = new JTabbedPane();
/*  70 */     add(this.tabbedPane, "Center");
/*     */ 
/*  72 */     setLocationRelativeTo(null);
/*  73 */     setVisible(true);
/*     */ 
/*  75 */     addWindowListener(new WindowAdapter()
/*     */     {
/*     */       public void windowClosing(WindowEvent e) {
/*  78 */         NewsContentDialog.access$002(null);
/*     */       }
/*     */ 
/*     */       public void windowClosed(WindowEvent e)
/*     */       {
/*  83 */         NewsContentDialog.access$002(null);
/*     */       }
/*     */     }); }
/*     */ 
/*     */   public static NewsContentDialog getCurrentInstance() {
/*  89 */     return instance;
/*     */   }
/*     */ 
/*     */   public boolean isShowing(String newsId) {
/*  93 */     return (isVisible()) && (this.newsId.equals(newsId));
/*     */   }
/*     */ 
/*     */   public void add(INewsMessage newsMessage)
/*     */   {
/* 100 */     String content = newsMessage.getContent();
/* 101 */     if ((content != null) && (!content.isEmpty())) {
/* 102 */       this.news.add(newsMessage);
/*     */ 
/* 104 */       if (this.news.size() > 1) {
/* 105 */         Collections.sort(this.news, new NewsComparator(null));
/*     */       }
/*     */ 
/* 108 */       rebuild();
/*     */     } else {
/* 110 */       this.progressBar.setVisible(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void rebuild() {
/* 115 */     if (this.tabbedPane.getTabCount() > 0) {
/* 116 */       this.tabbedPane.removeAll();
/*     */     }
/*     */ 
/* 119 */     for (INewsMessage newsMessage : this.news)
/* 120 */       this.tabbedPane.add(DATE_FORMAT.format(Long.valueOf(newsMessage.getPublishDate())), new NewsContentPanel(newsMessage));
/*     */   }
/*     */ 
/*     */   private static class NewsComparator
/*     */     implements Comparator<INewsMessage>
/*     */   {
/*     */     public int compare(INewsMessage m1, INewsMessage m2)
/*     */     {
/* 167 */       if (m1.getPublishDate() == m2.getPublishDate()) {
/* 168 */         return 0;
/*     */       }
/* 170 */       if (m1.getPublishDate() > m2.getPublishDate()) {
/* 171 */         return -1;
/*     */       }
/* 173 */       return 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class NewsContentPanel extends JPanel
/*     */   {
/* 128 */     private static final HTMLEditorKit EDITOR_KIT = new HTMLEditorKit() { } ;
/*     */ 
/*     */     public NewsContentPanel(INewsMessage newsMessage)
/*     */     {
/* 137 */       super();
/*     */ 
/* 139 */       JTextPane textPane = new JTextPane()
/*     */       {
/*     */       };
/*     */       try
/*     */       {
/* 148 */         textPane.setDocument(EDITOR_KIT.createDefaultDocument());
/*     */ 
/* 150 */         StringBuilder text = new StringBuilder("<html><body>");
/* 151 */         text.append("<p id=\"header\">").append(newsMessage.getHeader()).append("</p>");
/* 152 */         text.append(newsMessage.getContent());
/* 153 */         text.append("</body></html>");
/*     */ 
/* 155 */         textPane.setText(text.toString());
/*     */       } catch (Exception ex) {
/* 157 */         NewsContentDialog.LOGGER.error("Error while creating news content panel", ex);
/*     */       }
/*     */ 
/* 160 */       add(new JScrollPane(textPane), "Center");
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.news.NewsContentDialog
 * JD-Core Version:    0.6.0
 */