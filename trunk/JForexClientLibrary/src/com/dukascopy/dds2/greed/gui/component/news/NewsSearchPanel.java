/*     */ package com.dukascopy.dds2.greed.gui.component.news;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.GetNewsAction;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*     */ import com.dukascopy.dds2.greed.util.CollectionUtils;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JSpinnerDateEditor;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.TreeMap;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.Timer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class NewsSearchPanel extends JPanel
/*     */   implements NewsListener, NewsViaHttp
/*     */ {
/*  70 */   private static final Logger LOGGER = LoggerFactory.getLogger(NewsSearchPanel.class);
/*     */ 
/*  72 */   private String[][] subjects = { { "", LocalizationManager.getText("combo.all.subjects") }, { "N/FXMT", LocalizationManager.getText("combo.foreign.ex.market.talk") }, { "N/CAL", LocalizationManager.getText("combo.calendar") }, { "N/CBK", LocalizationManager.getText("combo.central.banks") }, { "N/CBI", LocalizationManager.getText("combo.cantral.bank.investition") }, { "N/PET", LocalizationManager.getText("combo.crude.oil") }, { "N/COP", LocalizationManager.getText("combo.currency.options") }, { "N/DJFX", LocalizationManager.getText("combo.dj.forex") }, { "N/IEJ", LocalizationManager.getText("combo.eco.indicators") }, { "N/ECO", LocalizationManager.getText("combo.eco.news.int") }, { "N/DJG7", LocalizationManager.getText("combo.g7.forex.wire") }, { "N/GPC", LocalizationManager.getText("combo.gold.price") }, { "N/IRT", LocalizationManager.getText("combo.interest.rate.tables") }, { "N/IFX", LocalizationManager.getText("combo.intraday.forex") } };
/*     */ 
/*  89 */   private Interval[] INTERVALS = { new Interval(LocalizationManager.getText("combo.last.10.min"), "last10m", false), new Interval(LocalizationManager.getText("combo.last.30.min"), "last30m", false), new Interval(LocalizationManager.getText("combo.last.hour"), "last60m", false), new Interval(LocalizationManager.getText("combo.today"), "today", false), new Interval(LocalizationManager.getText("combo.specific.date"), "date", true) };
/*     */ 
/*  97 */   private TreeMap<String, String> subjectTypes = CollectionUtils.createTreeMapFromArrays(this.subjects, true);
/*     */   private static final String DATE_FORMAT = "dd.MM.yyyy";
/* 100 */   private final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd.MM.yyyy");
/*     */   private final JComboBox cbInterval;
/*     */   private final JDateChooser dateChooser;
/* 106 */   private String subjectText = null;
/*     */   private JTextField keyword;
/*     */   private JButton bSearch;
/*     */   private final NewsPanel newsPanel;
/*     */   private Timer timer;
/*     */   JComboBox subjectList;
/*     */ 
/*     */   public NewsSearchPanel()
/*     */   {
/* 119 */     this.simpleDataFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 120 */     setLayout(new BoxLayout(this, 1));
/* 121 */     int INSET = 5;
/* 122 */     int HEIGHT = 30;
/*     */ 
/* 125 */     JPanel controlPanel = new JPanel();
/* 126 */     controlPanel.setLayout(new BoxLayout(controlPanel, 0));
/*     */ 
/* 135 */     controlPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/*     */ 
/* 142 */     controlPanel.setMaximumSize(new Dimension(32767, 30));
/* 143 */     controlPanel.add(new JLocalizableLabel("label.interval"));
/* 144 */     controlPanel.add(Box.createHorizontalStrut(5));
/*     */ 
/* 147 */     this.cbInterval = new JLocalizableComboBox(this.INTERVALS, ResizingManager.ComponentSize.SIZE_120X24)
/*     */     {
/*     */       public void translate() {
/* 150 */         NewsSearchPanel.this.dotranslateIntervalsComboBox();
/*     */       }
/*     */     };
/* 156 */     controlPanel.add(this.cbInterval);
/* 157 */     controlPanel.add(Box.createHorizontalStrut(10));
/*     */ 
/* 160 */     JLabel label = new JLocalizableLabel("label.date");
/* 161 */     controlPanel.add(label);
/* 162 */     controlPanel.add(Box.createHorizontalStrut(5));
/* 163 */     JSpinnerDateEditor spinnerDateEditor = new JSpinnerDateEditor();
/* 164 */     spinnerDateEditor.setDateFormatString("dd.MM.yyyy");
/* 165 */     this.dateChooser = new JDateChooser(new Date(), "dd.MM.yyyy", spinnerDateEditor);
/* 166 */     this.dateChooser.setMaximumSize(new Dimension(120, 32767));
/* 167 */     controlPanel.add(this.dateChooser);
/* 168 */     controlPanel.add(Box.createHorizontalStrut(10));
/* 169 */     label.setEnabled(this.INTERVALS[this.cbInterval.getSelectedIndex()].isDate());
/* 170 */     this.dateChooser.setEnabled(this.INTERVALS[this.cbInterval.getSelectedIndex()].isDate());
/*     */ 
/* 173 */     controlPanel.add(new JLocalizableLabel("label.subject"));
/* 174 */     controlPanel.add(Box.createHorizontalStrut(5));
/*     */ 
/* 176 */     String[] subjectNames = (String[])this.subjectTypes.keySet().toArray(new String[this.subjectTypes.size()]);
/* 177 */     this.subjectList = new JLocalizableComboBox(subjectNames, ResizingManager.ComponentSize.SIZE_180X24)
/*     */     {
/*     */       public void translate() {
/* 180 */         NewsSearchPanel.this.dotranslateSubjectsComboBox();
/*     */       }
/*     */     };
/* 186 */     this.subjectList.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 188 */         JComboBox cb = (JComboBox)e.getSource();
/* 189 */         String subjectName = (String)cb.getSelectedItem();
/* 190 */         NewsSearchPanel.this.updateSubject(subjectName);
/*     */       }
/*     */     });
/* 195 */     controlPanel.add(this.subjectList);
/* 196 */     controlPanel.add(Box.createHorizontalStrut(10));
/*     */ 
/* 199 */     controlPanel.add(new JLocalizableLabel("label.keyword"));
/* 200 */     controlPanel.add(Box.createHorizontalStrut(5));
/* 201 */     this.keyword = new JTextField(15);
/* 202 */     this.keyword.setFont(label.getFont());
/* 203 */     this.keyword.setMaximumSize(new Dimension(75, 32767));
/* 204 */     this.keyword.setPreferredSize(new Dimension(75, 19));
/*     */ 
/* 206 */     controlPanel.add(this.keyword);
/* 207 */     controlPanel.add(Box.createHorizontalStrut(10));
/*     */ 
/* 210 */     this.bSearch = new JLocalizableButton("button.search", ResizingManager.ComponentSize.SIZE_80X24);
/* 211 */     this.bSearch.setOpaque(false);
/* 212 */     controlPanel.add(this.bSearch);
/*     */ 
/* 215 */     controlPanel.add(Box.createHorizontalGlue());
/*     */ 
/* 218 */     this.newsPanel = new NewsPanel(this);
/*     */ 
/* 221 */     add(controlPanel);
/* 222 */     add(this.newsPanel);
/*     */ 
/* 225 */     this.cbInterval.addActionListener(new ActionListener(label) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 227 */         this.val$label.setEnabled(NewsSearchPanel.this.INTERVALS[NewsSearchPanel.this.cbInterval.getSelectedIndex()].isDate());
/* 228 */         NewsSearchPanel.this.dateChooser.setEnabled(NewsSearchPanel.this.INTERVALS[NewsSearchPanel.this.cbInterval.getSelectedIndex()].isDate());
/*     */       }
/*     */     });
/* 232 */     this.bSearch.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 234 */         NewsSearchPanel.this.search();
/*     */       }
/*     */     });
/* 238 */     this.timer = new Timer(5000, new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 240 */         NewsSearchPanel.this.bSearch.setEnabled(true);
/*     */       }
/*     */     });
/* 243 */     this.timer.setRepeats(false);
/*     */   }
/*     */ 
/*     */   private void updateSubject(String subjectName) {
/* 247 */     this.subjectText = ((String)this.subjectTypes.get(subjectName));
/*     */   }
/*     */ 
/*     */   private boolean search() {
/* 251 */     this.newsPanel.clear();
/* 252 */     String baseUrl = GreedContext.getStringProperty("services1.url");
/* 253 */     String searchUrl = GreedContext.getStringProperty("news.url");
/* 254 */     String sInterval = "&search=1&interval=" + this.INTERVALS[this.cbInterval.getSelectedIndex()].getInterval();
/* 255 */     String sDate = "&date=" + this.simpleDataFormat.format(this.dateChooser.getDate());
/* 256 */     String sSubject = "";
/* 257 */     String sKeyword = "";
/*     */     try {
/* 259 */       sKeyword = "&keyword=" + URLEncoder.encode(this.keyword.getText(), "ISO-8859-1");
/*     */     } catch (UnsupportedEncodingException e) {
/* 261 */       LOGGER.error("unable to urlencode keyword: " + this.keyword.getText() + " error: " + e.getMessage());
/* 262 */       this.keyword.setText("");
/*     */     }
/*     */ 
/* 268 */     if ((this.subjectText != null) && (this.subjectText.trim().length() > 0)) {
/* 269 */       sSubject = "&subject=" + this.subjectText.trim();
/*     */     }
/*     */ 
/* 272 */     String login = (String)GreedContext.getConfig("account_name");
/* 273 */     String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/* 274 */     if (null == authorization) {
/* 275 */       LOGGER.warn("unable to generate md5 hash");
/* 276 */       return false;
/*     */     }
/*     */ 
/* 279 */     StringBuffer sb = new StringBuffer(sInterval);
/* 280 */     if (this.INTERVALS[this.cbInterval.getSelectedIndex()].isDate()) {
/* 281 */       sb.append(sDate);
/*     */     }
/* 283 */     if (0 != this.keyword.getText().length()) {
/* 284 */       sb.append(sKeyword);
/*     */     }
/* 286 */     sb.append(sSubject);
/*     */ 
/* 288 */     String searchRequest = new StringBuffer(baseUrl).append(searchUrl).append("?").append(authorization).append(sb).toString();
/*     */ 
/* 294 */     LOGGER.debug("News search request:" + searchRequest);
/* 295 */     GreedContext.publishEvent(new GetNewsAction(this, searchRequest));
/* 296 */     this.bSearch.setEnabled(false);
/* 297 */     setCursor(Cursor.getPredefinedCursor(3));
/* 298 */     this.timer.start();
/* 299 */     return true;
/*     */   }
/*     */ 
/*     */   public void newsArrived() {
/* 303 */     this.bSearch.setEnabled(true);
/* 304 */     setCursor(Cursor.getDefaultCursor());
/* 305 */     if (this.timer.isRunning()) this.timer.stop(); 
/*     */   }
/*     */ 
/*     */   public boolean parseNews(String newsString)
/*     */   {
/* 309 */     this.newsPanel.showNews(newsString);
/* 310 */     newsArrived();
/* 311 */     return true;
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/* 316 */     this.dateChooser.cleanup();
/* 317 */     super.finalize();
/*     */   }
/*     */ 
/*     */   private void doRefreshIntervals()
/*     */   {
/* 349 */     this.INTERVALS[0].setLabel(LocalizationManager.getText("combo.last.10.min"));
/* 350 */     this.INTERVALS[1].setLabel(LocalizationManager.getText("combo.last.30.min"));
/* 351 */     this.INTERVALS[2].setLabel(LocalizationManager.getText("combo.last.hour"));
/* 352 */     this.INTERVALS[3].setLabel(LocalizationManager.getText("combo.today"));
/* 353 */     this.INTERVALS[4].setLabel(LocalizationManager.getText("combo.specific.date"));
/*     */   }
/*     */ 
/*     */   private void doRefreshSubjects() {
/* 357 */     String[][] subjects = { { "", LocalizationManager.getText("combo.all.subjects") }, { "N/FXMT", LocalizationManager.getText("combo.foreign.ex.market.talk") }, { "N/CAL", LocalizationManager.getText("combo.calendar") }, { "N/CBK", LocalizationManager.getText("combo.central.banks") }, { "N/CBI", LocalizationManager.getText("combo.cantral.bank.investition") }, { "N/PET", LocalizationManager.getText("combo.crude.oil") }, { "N/COP", LocalizationManager.getText("combo.currency.options") }, { "N/DJFX", LocalizationManager.getText("combo.dj.forex") }, { "N/IEJ", LocalizationManager.getText("combo.eco.indicators") }, { "N/ECO", LocalizationManager.getText("combo.eco.news.int") }, { "N/DJG7", LocalizationManager.getText("combo.g7.forex.wire") }, { "N/GPC", LocalizationManager.getText("combo.gold.price") }, { "N/IRT", LocalizationManager.getText("combo.interest.rate.tables") }, { "N/IFX", LocalizationManager.getText("combo.intraday.forex") } };
/*     */ 
/* 373 */     this.subjects = subjects;
/* 374 */     this.subjectTypes = CollectionUtils.createTreeMapFromArrays(subjects, true);
/*     */ 
/* 376 */     int selected = this.subjectList.getSelectedIndex();
/*     */ 
/* 378 */     String[] subjectNames = (String[])this.subjectTypes.keySet().toArray(new String[this.subjectTypes.size()]);
/* 379 */     DefaultComboBoxModel dcbm = new DefaultComboBoxModel(subjectNames);
/*     */ 
/* 381 */     this.subjectList.setModel(dcbm);
/* 382 */     this.subjectList.setSelectedIndex(selected);
/*     */   }
/*     */ 
/*     */   private void dotranslateIntervalsComboBox() {
/* 386 */     if (this.cbInterval == null) {
/* 387 */       return;
/*     */     }
/*     */ 
/* 390 */     doRefreshIntervals();
/*     */ 
/* 392 */     this.cbInterval.revalidate();
/* 393 */     this.cbInterval.repaint();
/*     */   }
/*     */ 
/*     */   private void dotranslateSubjectsComboBox() {
/* 397 */     if (this.subjectList == null) {
/* 398 */       return;
/*     */     }
/*     */ 
/* 401 */     doRefreshSubjects();
/*     */ 
/* 403 */     this.subjectList.revalidate();
/* 404 */     this.subjectList.repaint();
/*     */   }
/*     */ 
/*     */   private class Interval
/*     */   {
/*     */     private String label;
/*     */     private final String interval;
/*     */     private final boolean hasDate;
/*     */ 
/*     */     public Interval(String label, String interval, boolean hasDate)
/*     */     {
/* 326 */       this.interval = interval;
/* 327 */       this.label = label;
/* 328 */       this.hasDate = hasDate;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 332 */       return this.label;
/*     */     }
/*     */ 
/*     */     String getInterval() {
/* 336 */       return this.interval;
/*     */     }
/*     */ 
/*     */     boolean isDate() {
/* 340 */       return this.hasDate;
/*     */     }
/*     */ 
/*     */     public void setLabel(String newLabel) {
/* 344 */       this.label = newLabel;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.news.NewsSearchPanel
 * JD-Core Version:    0.6.0
 */