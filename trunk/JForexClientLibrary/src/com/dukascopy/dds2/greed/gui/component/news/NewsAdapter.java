/*     */ package com.dukascopy.dds2.greed.gui.component.news;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.GetNewsAction;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup;
/*     */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup.MarketNews;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import javax.swing.Timer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class NewsAdapter
/*     */   implements NewsViaHttp
/*     */ {
/*  24 */   private static Logger LOGGER = LoggerFactory.getLogger(NewsAdapter.class);
/*     */ 
/*  26 */   private LinkedList<MarketNewsMessageGroup.MarketNews> news = new LinkedList();
/*  27 */   private List<NewsListener> subscribers = new ArrayList();
/*  28 */   private int MAX_NEWS = 20;
/*  29 */   private final int ONE_MINUTE = 60000;
/*     */ 
/*     */   public NewsAdapter(int maxNews) {
/*  32 */     this.MAX_NEWS = maxNews;
/*     */   }
/*     */ 
/*     */   public void initHttpFeed() {
/*  36 */     Timer timer = new Timer(60000, new ActionListener() {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/*  38 */         NewsAdapter.this.receiveNews();
/*     */       }
/*     */     });
/*  41 */     timer.setRepeats(true);
/*  42 */     timer.start();
/*  43 */     receiveNews();
/*     */   }
/*     */ 
/*     */   public void updateNewsNonReversed(List<MarketNewsMessageGroup.MarketNews> freshNews)
/*     */   {
/*  51 */     for (MarketNewsMessageGroup.MarketNews aGoodNews : freshNews) {
/*  52 */       this.news.addLast(aGoodNews);
/*  53 */       if (this.news.size() > this.MAX_NEWS) {
/*  54 */         this.news.removeFirst();
/*     */       }
/*     */     }
/*  57 */     informSubscribers();
/*     */   }
/*     */ 
/*     */   public void updateNews(List<MarketNewsMessageGroup.MarketNews> freshNews)
/*     */   {
/*  65 */     for (MarketNewsMessageGroup.MarketNews aGoodNews : freshNews) {
/*  66 */       this.news.addFirst(aGoodNews);
/*  67 */       if (this.news.size() > this.MAX_NEWS) {
/*  68 */         this.news.removeLast();
/*     */       }
/*     */     }
/*  71 */     informSubscribers();
/*     */   }
/*     */ 
/*     */   private boolean receiveNews() {
/*  75 */     String baseUrl = GreedContext.getStringProperty("news.host.url");
/*  76 */     String newsUrl = GreedContext.getStringProperty("news.url");
/*  77 */     String login = (String)GreedContext.getConfig("account_name");
/*  78 */     String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/*  79 */     if (null == authorization) {
/*  80 */       LOGGER.warn("unable to generate md5 hash");
/*  81 */       return false;
/*     */     }
/*  83 */     if ((baseUrl == null) || (newsUrl == null)) {
/*  84 */       LOGGER.warn("News URL is not available.");
/*  85 */       return false;
/*     */     }
/*     */ 
/*  88 */     LOGGER.info(" baseUrl " + baseUrl);
/*  89 */     LOGGER.info(" newsUrl " + newsUrl);
/*  90 */     LOGGER.info(" authorization " + authorization);
/*  91 */     String urlNewsString = baseUrl + newsUrl + "?" + authorization;
/*     */ 
/*  96 */     GreedContext.publishEvent(new GetNewsAction(this, urlNewsString));
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean parseNews(String newsString)
/*     */   {
/*     */     try
/*     */     {
/* 107 */       newsString = newsString.replaceAll("\\\"", "\\\\\"");
/* 108 */       ProtocolMessage newsMessage = ProtocolMessage.parse(newsString);
/* 109 */       if (null == newsMessage) return false;
/* 110 */       List currentNews = ((MarketNewsMessageGroup)newsMessage).getMarketNewsList();
/*     */ 
/* 112 */       updateNewsNonReversed(currentNews);
/*     */ 
/* 116 */       return true;
/*     */     } catch (Exception e) {
/* 118 */       LOGGER.warn("Unable to parse news string: " + newsString);
/* 119 */     }return false;
/*     */   }
/*     */ 
/*     */   public void subscribe(NewsListener subscriber)
/*     */   {
/* 124 */     this.subscribers.add(subscriber);
/*     */   }
/*     */ 
/*     */   public void unsubscribe(NewsListener subscriber) {
/* 128 */     this.subscribers.remove(subscriber);
/*     */   }
/*     */ 
/*     */   private void informSubscribers() {
/* 132 */     for (NewsListener subscriber : this.subscribers)
/* 133 */       subscriber.newsArrived();
/*     */   }
/*     */ 
/*     */   public List<MarketNewsMessageGroup.MarketNews> getNews()
/*     */   {
/* 138 */     return this.news;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.news.NewsAdapter
 * JD-Core Version:    0.6.0
 */