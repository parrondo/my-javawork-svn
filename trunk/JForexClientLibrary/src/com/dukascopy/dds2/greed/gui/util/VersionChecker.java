/*     */ package com.dukascopy.dds2.greed.gui.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Calendar;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.Timer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class VersionChecker
/*     */ {
/*  25 */   private static final Logger LOGGER = LoggerFactory.getLogger(VersionChecker.class);
/*     */   private static final String MAIN_TRUE = "main=\"true\"";
/*     */   private static final String START_STRING = "jForex-";
/*     */   private static final String FINISH_STRING = ".jar";
/*     */   private static final int ONCE_PER_DAY = 86400000;
/*     */   private static final int ONCE_PER_WEEK = 604800000;
/*     */   private static final int SHOWING_HOUR = 12;
/*     */   private static final int SHOWING_MINUTE = 0;
/*  37 */   private static int initDelay = 0;
/*     */ 
/*     */   public static void startPeriodicalCheck()
/*     */   {
/*  41 */     if (!GreedContext.CURRENT_CLIENT_JNLP_URL.startsWith("null")) {
/*  42 */       Calendar currentGMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  43 */       initDelay = (int)(getNextSundayPM12().getTimeInMillis() - currentGMT.getTimeInMillis());
/*  44 */       initTimer();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void initTimer()
/*     */   {
/*  51 */     ActionListener versionChecker = new ActionListener() {
/*     */       public void actionPerformed(ActionEvent evt) {
/*  53 */         VersionChecker.chekVersionInBackground();
/*     */       }
/*     */     };
/*  57 */     Timer checkTimer = new Timer(604800000, versionChecker);
/*  58 */     checkTimer.setRepeats(true);
/*     */     try
/*     */     {
/*  61 */       checkTimer.setInitialDelay(initDelay);
/*  62 */       checkTimer.setDelay(604800000);
/*  63 */       checkTimer.start();
/*     */     } catch (Exception e) {
/*  65 */       LOGGER.error("Wrong init delay.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void chekVersionInBackground()
/*     */   {
/*  71 */     chekVersion(true);
/*     */   }
/*     */ 
/*     */   public static void chekVersion() {
/*  75 */     chekVersion(false);
/*     */   }
/*     */ 
/*     */   public static void chekVersion(boolean inBackground)
/*     */   {
/*  80 */     String lastestVersion = getLastestVersion();
/*  81 */     boolean isFineVersion = (lastestVersion == null) || (GreedContext.CLIENT_VERSION.equals(lastestVersion));
/*     */ 
/*  83 */     SwingUtilities.invokeLater(new Runnable(isFineVersion, inBackground)
/*     */     {
/*     */       public void run()
/*     */       {
/*  87 */         if (!this.val$isFineVersion)
/*  88 */           VersionChecker.access$000();
/*  89 */         else if (!this.val$inBackground)
/*  90 */           VersionChecker.access$100();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static void showFineVersionDialog()
/*     */   {
/*  99 */     JOptionPane.showMessageDialog((ClientForm)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.lastest.version"), LocalizationManager.getText("joption.pane.message"), 1);
/*     */   }
/*     */ 
/*     */   private static void showNewVersionDialog()
/*     */   {
/* 108 */     Object[] options = { LocalizationManager.getText("button.validation.yes"), LocalizationManager.getText("button.validation.no") };
/*     */ 
/* 111 */     int result = JOptionPane.showOptionDialog((ClientForm)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.new.client.available.message"), LocalizationManager.getText("joption.pane.new.client.available.title"), 0, 3, null, options, LocalizationManager.getText("button.validation.no"));
/*     */ 
/* 120 */     if (result == 0)
/* 121 */       PlatformInitUtils.reloadOpenedPlatformFromSite();
/*     */   }
/*     */ 
/*     */   private static String getLastestVersion()
/*     */   {
/* 128 */     InputStream inputStream = null;
/*     */     try
/*     */     {
/* 132 */       URL url = new URL(GreedContext.CURRENT_CLIENT_JNLP_URL);
/* 133 */       inputStream = url.openStream();
/* 134 */       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
/*     */       String line;
/* 136 */       while ((line = bufferedReader.readLine()) != null)
/* 137 */         if (line.contains("main=\"true\"")) {
/* 138 */           start = line.indexOf("jForex-") + "jForex-".length();
/* 139 */           int finish = line.indexOf(".jar");
/* 140 */           String str1 = line.substring(start, finish);
/*     */           return str1;
/*     */         }
/*     */     }
/*     */     catch (MalformedURLException mue)
/*     */     {
/* 145 */       LOGGER.error(mue.getMessage(), mue);
/* 146 */       start = null;
/*     */       return start;
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 148 */       LOGGER.error(ioe.getMessage(), ioe);
/* 149 */       int start = null;
/*     */       return start;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 152 */         inputStream.close();
/*     */       } catch (Exception e) {
/* 154 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */ 
/*     */   private static Calendar getNextSundayPM12() {
/* 161 */     Calendar showingTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 162 */     long currentTime = showingTime.getTimeInMillis();
/*     */ 
/* 164 */     showingTime.set(7, 1);
/*     */ 
/* 166 */     if (currentTime > showingTime.getTimeInMillis()) {
/* 167 */       showingTime.add(7, 7);
/*     */     }
/* 169 */     showingTime.set(11, 12);
/* 170 */     showingTime.set(12, 0);
/*     */ 
/* 172 */     return showingTime;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.VersionChecker
 * JD-Core Version:    0.6.0
 */