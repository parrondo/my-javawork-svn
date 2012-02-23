/*     */ package com.dukascopy.dds2.greed.gui.component.javadoc;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.HyperlinkEvent;
/*     */ import javax.swing.event.HyperlinkEvent.EventType;
/*     */ import javax.swing.event.HyperlinkListener;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.html.HTMLEditorKit;
/*     */ import javax.swing.text.html.StyleSheet;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JDocBrowser extends JEditorPane
/*     */   implements HyperlinkListener
/*     */ {
/*  45 */   private static final Logger LOGGER = LoggerFactory.getLogger(JDocBrowser.class);
/*  46 */   private String filePath = null;
/*     */   public static final String OPEN_BROWSER_BOOKMARK = "openBrowser";
/*     */   private OpenListener openListener;
/*     */   private String query;
/*     */ 
/*     */   public JDocBrowser(OpenListener openListener)
/*     */   {
/*  53 */     setEditable(false);
/*  54 */     addHyperlinkListener(this);
/*  55 */     this.openListener = openListener;
/*     */   }
/*     */ 
/*     */   public void open(String query, String filePath, String bookmark)
/*     */   {
/*  60 */     InputStream is = null;
/*  61 */     StringBuilder sb = null;
/*     */     try {
/*  63 */       String content = null;
/*  64 */       if (!filePath.equals(this.filePath))
/*     */       {
/*  66 */         is = IOUtils.getResourceAsStream(filePath);
/*  67 */         content = IOUtils.streamToString(is);
/*  68 */         content = content.replaceAll("\r\n?", "\n");
/*     */ 
/*  71 */         int j = 0;
/*  72 */         int hr1 = -1;
/*  73 */         int hr2 = -1;
/*  74 */         int hr3 = -1;
/*  75 */         int hr4 = -1;
/*     */ 
/*  77 */         for (int i = 0; ; i++) {
/*  78 */           j = content.indexOf("<HR>", j + 4);
/*  79 */           if (j < 0) {
/*     */             break;
/*     */           }
/*  82 */           if (i == 0) {
/*  83 */             hr1 = j;
/*  84 */           } else if (i == 1) {
/*  85 */             hr2 = j;
/*     */           } else {
/*  87 */             hr3 = hr4;
/*  88 */             hr4 = j;
/*     */           }
/*     */         }
/*  91 */         if ((hr1 > 0) && (hr2 > 0) && (hr3 > 0) && (hr4 > 0)) {
/*  92 */           sb = new StringBuilder();
/*  93 */           sb.append(content.substring(0, hr1));
/*  94 */           sb.append(content.substring(hr1 + 4, hr2));
/*  95 */           sb.append(content.substring(hr2 + 4, hr3));
/*  96 */           sb.append(content.substring(hr3 + 4, hr4));
/*  97 */           sb.append(content.substring(hr4 + 4, content.length()));
/*  98 */           content = sb.toString();
/*     */         }
/*     */ 
/* 102 */         content = content.replaceAll("<IMG(.)*?>", "");
/*     */ 
/* 105 */         content = content.replaceAll("BORDER=\"1\"", "BORDER=\"0\"");
/* 106 */         content = content.replaceAll("<DD>\\s*<DL>\\s*</DL>\\s*</DD>", "");
/*     */ 
/* 108 */         int p = content.indexOf("<P>", 0);
/* 109 */         sb = new StringBuilder();
/* 110 */         int start = 0;
/* 111 */         int dl = 0;
/*     */         while (true) {
/* 113 */           dl = content.indexOf("<DL>", dl + 4);
/* 114 */           if ((dl < 0) || (dl > p)) {
/*     */             break;
/*     */           }
/* 117 */           int dlend = content.indexOf("</DL>", dl);
/*     */ 
/* 119 */           boolean isFirst = true;
/* 120 */           int dt = dl;
/*     */           while (true) {
/* 122 */             dt = content.indexOf("<DT>", dt + 4);
/* 123 */             if ((dt < 0) || (dt > dlend)) {
/*     */               break;
/*     */             }
/* 126 */             if (isFirst) {
/* 127 */               isFirst = false; continue;
/*     */             }
/* 129 */             sb.append(content.substring(start, dt));
/* 130 */             sb.append("<br/>");
/* 131 */             start = dt;
/*     */           }
/*     */         }
/*     */ 
/* 135 */         sb.append(content.substring(start, content.length()));
/* 136 */         content = sb.toString();
/*     */ 
/* 139 */         String s = "<TR BGCOLOR=\"white\" CLASS=\"TableRowColor\">";
/* 140 */         String r = "<TR CLASS=\"TableRowColor\">";
/* 141 */         String r2 = "<TR CLASS=\"TableRowColor2\">";
/*     */ 
/* 143 */         sb = new StringBuilder();
/* 144 */         int index = 0;
/* 145 */         int indexOld = 0;
/* 146 */         int row = 0;
/*     */         while (true) {
/* 148 */           index = content.indexOf(s, indexOld);
/* 149 */           if (index < 0) {
/*     */             break;
/*     */           }
/* 152 */           sb.append(content.substring(indexOld, index));
/* 153 */           if (row == 0) {
/* 154 */             sb.append(r);
/* 155 */             row = 1;
/*     */           } else {
/* 157 */             sb.append(r2);
/* 158 */             row = 0;
/*     */           }
/* 160 */           indexOld = index + s.length();
/*     */         }
/* 162 */         sb.append(content.substring(indexOld, content.length()));
/* 163 */         content = sb.toString();
/*     */ 
/* 166 */         for (String queryBit : query.split(" ")) {
/* 167 */           if ((queryBit != null) && (queryBit.length() > 0)) {
/* 168 */             content = highlite(content, queryBit);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 173 */         HTMLEditorKit kit = new HTMLEditorKit();
/* 174 */         setEditorKit(kit);
/*     */ 
/* 176 */         StyleSheet styleSheet = kit.getStyleSheet();
/* 177 */         StyleSheet newStyleSheet = new StyleSheet();
/* 178 */         newStyleSheet.addStyleSheet(styleSheet);
/*     */ 
/* 180 */         loadStyleSheet("rc/javadoc/javadoc_style.css", newStyleSheet);
/*     */ 
/* 182 */         kit.setStyleSheet(newStyleSheet);
/*     */ 
/* 185 */         Document doc = kit.createDefaultDocument();
/* 186 */         setDocument(doc);
/* 187 */         setText(content);
/*     */       }
/*     */ 
/* 191 */       setCaretPosition(0);
/* 192 */       SwingUtilities.invokeLater(new ScrollTo(bookmark));
/*     */ 
/* 195 */       this.filePath = filePath;
/* 196 */       this.query = query;
/*     */     }
/*     */     catch (IOException e) {
/* 199 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/* 201 */       IOUtils.closeQuietly(is);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void loadStyleSheet(String file, StyleSheet s) {
/* 206 */     BufferedReader reader = null;
/*     */     try {
/* 208 */       reader = new BufferedReader(new InputStreamReader(IOUtils.getResourceAsStream(file)));
/*     */ 
/* 210 */       s.loadRules(reader, null);
/*     */     } catch (IOException e) {
/* 212 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/* 214 */       IOUtils.closeQuietly(reader);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void openHref(String href)
/*     */   {
/* 233 */     int sep = href.indexOf("#");
/* 234 */     String bookmark = "";
/* 235 */     String h = "";
/* 236 */     if (sep > 0) {
/* 237 */       h = href.substring(0, sep);
/* 238 */       bookmark = href.substring(sep + 1, href.length());
/*     */     } else {
/* 240 */       h = href;
/*     */     }
/* 242 */     while (h.startsWith("../")) {
/* 243 */       h = h.substring(3);
/*     */     }
/*     */ 
/* 246 */     String newFilePath = new StringBuilder().append("rc/javadoc/").append(h).toString();
/* 247 */     open(this.query, newFilePath, bookmark);
/* 248 */     if (this.openListener != null)
/* 249 */       this.openListener.opened(this.query, newFilePath, bookmark);
/*     */   }
/*     */ 
/*     */   public void hyperlinkUpdate(HyperlinkEvent e)
/*     */   {
/* 254 */     if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
/* 255 */       String href = e.getDescription();
/* 256 */       openHref(href);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String highlite(String contents, String query) {
/* 261 */     StringBuilder sb = new StringBuilder();
/* 262 */     char[] c = contents.toCharArray();
/* 263 */     char[] q = query.toCharArray();
/* 264 */     int index = contents.indexOf("</HEAD>");
/*     */ 
/* 266 */     if (index < 0) {
/* 267 */       return contents;
/*     */     }
/* 269 */     sb.append(contents.substring(0, index));
/* 270 */     boolean first = true;
/*     */ 
/* 272 */     for (int i = index; i < c.length - q.length; i++) {
/* 273 */       if (c[i] == '<') {
/*     */         while (true) {
/* 275 */           i++;
/* 276 */           if (c[i] == '>') {
/*     */             break;
/*     */           }
/* 279 */           if (i >= c.length - q.length) {
/* 280 */             break label197;
/*     */           }
/*     */         }
/*     */       }
/* 284 */       if (equal(c, i, q, 0, q.length)) {
/* 285 */         sb.append(contents.substring(index, i));
/* 286 */         if (first) {
/* 287 */           sb.append("<A NAME=\"openBrowser\"><!-- --></A>");
/* 288 */           first = false;
/*     */         }
/*     */ 
/* 291 */         sb.append("<span style=\"background:#FFCC33\">");
/* 292 */         sb.append(contents.substring(i, i + q.length));
/* 293 */         sb.append("</span>");
/* 294 */         index = i + q.length;
/*     */       }
/*     */     }
/* 297 */     label197: sb.append(contents.substring(index, c.length));
/* 298 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public boolean equal(char[] buff1, int i1, char[] buff2, int i2, int len) {
/* 302 */     if ((i1 + len > buff1.length) || (i2 + len > buff2.length)) {
/* 303 */       return false;
/*     */     }
/* 305 */     for (int i = 0; i < len; i2++) {
/* 306 */       if (Character.toUpperCase(buff1[i1]) != Character.toUpperCase(buff2[i2]))
/* 307 */         return false;
/* 305 */       i++; i1++;
/*     */     }
/*     */ 
/* 310 */     return true;
/*     */   }
/*     */ 
/*     */   class ScrollTo
/*     */     implements Runnable
/*     */   {
/*     */     String bookmark;
/*     */ 
/*     */     ScrollTo(String bookmark)
/*     */     {
/* 222 */       this.bookmark = bookmark;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 226 */       if ((this.bookmark != null) && (this.bookmark.length() > 0))
/* 227 */         JDocBrowser.this.scrollToReference(this.bookmark);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.javadoc.JDocBrowser
 * JD-Core Version:    0.6.0
 */