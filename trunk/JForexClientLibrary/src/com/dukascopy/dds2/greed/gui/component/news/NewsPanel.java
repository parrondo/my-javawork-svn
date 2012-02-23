/*     */ package com.dukascopy.dds2.greed.gui.component.news;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.GetNewsAction;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup;
/*     */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup.MarketNews;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class NewsPanel extends JPanel
/*     */   implements NewsListener
/*     */ {
/*  36 */   private static Logger LOGGER = LoggerFactory.getLogger(NewsPanel.class);
/*     */   private NewsTable newsTable;
/*     */   private NewsListener decorator;
/*     */ 
/*     */   public NewsPanel(NewsListener newsListener)
/*     */   {
/*  43 */     this.decorator = newsListener;
/*  44 */     setLayout(new BorderLayout());
/*  45 */     this.newsTable = new NewsTable(new NewsTableModel());
/*  46 */     this.newsTable.getSelectionModel().setSelectionMode(0);
/*  47 */     TableColumnModel model = this.newsTable.getTableHeader().getColumnModel();
/*  48 */     model.getColumn(0).setHeaderValue(LocalizationManager.getText("column.date.time"));
/*  49 */     model.getColumn(1).setHeaderValue(LocalizationManager.getText("column.headline"));
/*     */ 
/*  51 */     JScrollPane scroll = new JScrollPane(this.newsTable);
/*  52 */     scroll.setVerticalScrollBarPolicy(22);
/*  53 */     scroll.setHorizontalScrollBarPolicy(30);
/*  54 */     scroll.setPreferredSize(new Dimension(0, 100));
/*  55 */     scroll.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  56 */     scroll.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/*  57 */     this.newsTable.setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  58 */     this.newsTable.setSelectionBackground(GreedContext.SELECTION_COLOR);
/*  59 */     this.newsTable.getTableHeader().setReorderingAllowed(false);
/*  60 */     this.newsTable.getTableHeader().setResizingAllowed(false);
/*     */ 
/*  62 */     this.newsTable.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent mouseEvent) {
/*  64 */         if ((mouseEvent.getClickCount() == 2) && 
/*  65 */           (GreedContext.isDukascopyPlatform))
/*  66 */           NewsPanel.this.viewDetailedNews();
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent mouseEvent)
/*     */       {
/*  72 */         NewsPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent mouseEvent) {
/*  76 */         NewsPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/*  80 */     add(scroll);
/*     */   }
/*     */ 
/*     */   private void viewDetailedNews() {
/*  84 */     int rowSelected = this.newsTable.getSelectedRow();
/*  85 */     MarketNewsMessageGroup.MarketNews news = ((NewsTableModel)this.newsTable.getModel()).getNews(rowSelected);
/*  86 */     if (null == news) return;
/*     */ 
/*  89 */     String baseUrl = GreedContext.getStringProperty("services1.url");
/*  90 */     String detailsUrl = GreedContext.getStringProperty("news.details.url");
/*  91 */     String login = (String)GreedContext.getConfig("account_name");
/*  92 */     String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/*  93 */     if (null == authorization) {
/*  94 */       LOGGER.warn("unable to generate md5 hash");
/*  95 */       return;
/*     */     }
/*  97 */     GuiUtilsAndConstants.openURL(baseUrl + detailsUrl + news.getOrdIndex() + "&" + authorization);
/*     */   }
/*     */ 
/*     */   public boolean getNews()
/*     */   {
/* 106 */     String baseUrl = GreedContext.getStringProperty("news.host.url");
/* 107 */     String newsUrl = GreedContext.getStringProperty("news.url");
/* 108 */     String login = (String)GreedContext.getConfig("account_name");
/* 109 */     String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/* 110 */     if (null == authorization) {
/* 111 */       LOGGER.warn("unable to generate md5 hash");
/* 112 */       return false;
/*     */     }
/* 114 */     String urlNews = baseUrl + newsUrl + "?" + authorization;
/*     */ 
/* 118 */     GreedContext.publishEvent(new GetNewsAction(this, urlNews));
/* 119 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean showNews(String newsString) {
/*     */     try {
/* 124 */       newsString = newsString.replaceAll("\\\"", "\\\\\"");
/* 125 */       ProtocolMessage newsMessage = ProtocolMessage.parse(newsString);
/* 126 */       if (null == newsMessage) return false;
/* 127 */       List currentNews = ((MarketNewsMessageGroup)newsMessage).getMarketNewsList();
/* 128 */       Collections.reverse(currentNews);
/*     */ 
/* 130 */       populateNewsTable(currentNews);
/* 131 */       return true;
/*     */     } catch (Exception e) {
/* 133 */       LOGGER.warn("Unable to parse news string: " + newsString);
/* 134 */     }return false;
/*     */   }
/*     */ 
/*     */   public void populateNewsTable(List<MarketNewsMessageGroup.MarketNews> currentNews)
/*     */   {
/* 143 */     NewsTableModel model = (NewsTableModel)this.newsTable.getModel();
/* 144 */     model.addNews(currentNews);
/* 145 */     model.fireTableDataChanged();
/* 146 */     this.decorator.newsArrived();
/*     */   }
/*     */ 
/*     */   public void newsArrived() {
/* 150 */     populateNewsTable(((NewsAdapter)GreedContext.get("newsAdapter")).getNews());
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 154 */     NewsTableModel model = (NewsTableModel)this.newsTable.getModel();
/* 155 */     model.clear();
/* 156 */     model.fireTableDataChanged();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.news.NewsPanel
 * JD-Core Version:    0.6.0
 */