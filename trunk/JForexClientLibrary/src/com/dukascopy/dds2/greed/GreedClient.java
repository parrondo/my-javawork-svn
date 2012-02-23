/*     */ package com.dukascopy.dds2.greed;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.actions.AutoConnectAction;
/*     */ import com.dukascopy.dds2.greed.actions.TicketAutoConnectAction;
/*     */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager.Language;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorageImpl;
/*     */ import com.dukascopy.dds2.greed.gui.settings.JForexPreferencesFactory;
/*     */ import com.dukascopy.dds2.greed.gui.settings.PreferencesStorage;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.dds2.greed.util.logging.GreedConsoleHandler;
/*     */ import com.dukascopy.dds2.greed.util.logging.UncaughtExceptionHandler;
/*     */ import com.dukascopy.dds2.greed.util.logging.UncaughtExceptionHandler.AwtHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Locale;
/*     */ import java.util.logging.Handler;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.prefs.PreferencesFactory;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class GreedClient
/*     */ {
/*  28 */   private org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GreedClient.class);
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  35 */     setupLogHandlers();
/*  36 */     setupSystemPoperties();
/*  37 */     java.util.logging.Logger.getLogger("com").setLevel(Level.FINEST);
/*  38 */     new GreedClient(args);
/*     */   }
/*     */ 
/*     */   private static void setupSystemPoperties() {
/*  42 */     System.setProperty(PreferencesFactory.class.getName(), JForexPreferencesFactory.class.getName());
/*     */   }
/*     */ 
/*     */   public GreedClient(String[] args) {
/*  46 */     init(args);
/*     */   }
/*     */ 
/*     */   private void init(String[] args) {
/*     */     try {
/*  51 */       setupPlatformMode(args);
/*  52 */       setUpWorkspacesPathAndLocale();
/*     */ 
/*  54 */       SwingUtilities.invokeAndWait(new Runnable() {
/*     */         public void run() {
/*  56 */           PlatformInitUtils.initLookAndFeel();
/*  57 */           PlatformInitUtils.initStaticValues();
/*     */ 
/*  60 */           String username = System.getProperty("jnlp.client.username");
/*  61 */           String password = System.getProperty("jnlp.client.password");
/*     */ 
/*  63 */           String instanceId = System.getProperty("jnlp.api.sid");
/*  64 */           String apiURL = System.getProperty("jnlp.api.url");
/*  65 */           String ticket = System.getProperty("jnlp.auth.ticket");
/*     */ 
/*  67 */           if ((username != null) && (password != null)) {
/*  68 */             GreedClient.this.doAutoLogin();
/*  69 */             return;
/*  70 */           }if ((username != null) && (instanceId != null) && (apiURL != null) && (ticket != null))
/*     */           {
/*  74 */             GreedClient.this.doAutoLoginFromBackOffice();
/*  75 */             return;
/*     */           }
/*     */ 
/*  78 */           LoginForm loginForm = LoginForm.getInstance();
/*  79 */           loginForm.display();
/*     */         } } );
/*     */     } catch (Exception e) {
/*  83 */       this.LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setupPlatformMode(String[] args)
/*     */   {
/*  98 */     GreedContext.IS_JCLIENT_INVOKED = !PreferencesStorage.isJforexMode();
/*     */ 
/* 100 */     String codeBase = null;
/*     */     try {
/* 102 */       Class basicServiceClass = GreedClient.class.getClassLoader().loadClass("javax.jnlp.BasicService");
/* 103 */       Class serviceManagerClass = GreedClient.class.getClassLoader().loadClass("javax.jnlp.ServiceManager");
/* 104 */       Method lookupMethod = serviceManagerClass.getMethod("lookup", new Class[] { String.class });
/* 105 */       Object bs = lookupMethod.invoke(null, new Object[] { "javax.jnlp.BasicService" });
/* 106 */       Method getCodeBaseMethod = basicServiceClass.getMethod("getCodeBase", new Class[0]);
/* 107 */       Object codeBaseObj = getCodeBaseMethod.invoke(bs, new Object[0]);
/* 108 */       codeBase = codeBaseObj.toString();
/*     */ 
/* 110 */       if ((codeBase != null) && (codeBase.contains("eu-live.dukascopy.com"))) {
/* 111 */         GreedContext.IS_EU_LIVE = true;
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */ 
/* 118 */     String jnlpHref = System.getProperty("jnlp.white.label.href");
/* 119 */     if (jnlpHref != null)
/* 120 */       GreedContext.CURRENT_CLIENT_JNLP_URL = jnlpHref.substring(0, jnlpHref.lastIndexOf("?") + 1) + getJnlpName();
/*     */     else
/* 122 */       GreedContext.CURRENT_CLIENT_JNLP_URL = codeBase + getJnlpName();
/*     */   }
/*     */ 
/*     */   private String getJnlpName()
/*     */   {
/* 127 */     String platformMode = System.getProperty("jnlp.platform.mode");
/*     */ 
/* 129 */     if ((platformMode == null) || ("jclient".equals(platformMode))) {
/* 130 */       return "jclient.jnlp";
/*     */     }
/* 132 */     return "jforex.jnlp";
/*     */   }
/*     */ 
/*     */   private static void setUpWorkspacesPathAndLocale()
/*     */   {
/* 137 */     ClientSettingsStorageImpl.importSystemPrefs();
/* 138 */     Locale locale = LocalizationManager.getSelectedLocale();
/* 139 */     for (LocalizationManager.Language language : LocalizationManager.Language.values())
/* 140 */       if (language.locale.equals(locale)) {
/* 141 */         LocalizationManager.changeLanguage(language);
/* 142 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   private void doAutoLogin()
/*     */   {
/* 149 */     GreedContext.setConfig("jnlp.run.strategy.on.start", Boolean.valueOf(true));
/*     */ 
/* 151 */     AutoConnectAction autoConnectAction = new AutoConnectAction();
/* 152 */     GreedContext.publishEvent(autoConnectAction);
/*     */   }
/*     */ 
/*     */   private void doAutoLoginFromBackOffice() {
/* 156 */     GreedContext.publishEvent(new TicketAutoConnectAction());
/*     */   }
/*     */ 
/*     */   public static void setupLogHandlers() throws InstantiationException, IllegalAccessException {
/* 160 */     Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
/* 161 */     System.setProperty("sun.awt.exception.handler", UncaughtExceptionHandler.AwtHandler.class.getName());
/*     */ 
/* 163 */     java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
/* 164 */     Handler[] handlers = rootLogger.getHandlers();
/* 165 */     for (int i = 0; i < handlers.length; i++) {
/* 166 */       rootLogger.removeHandler(handlers[i]);
/*     */     }
/* 168 */     rootLogger.addHandler(new GreedConsoleHandler());
/*     */ 
/* 170 */     if (!System.getProperty("jnlp.fine.logging", "false").equals("true"))
/*     */     {
/* 172 */       java.util.logging.Logger.getLogger("com").setLevel(Level.WARNING);
/* 173 */       java.util.logging.Logger.getLogger("com.dukascopy.transport").setLevel(Level.INFO);
/*     */     } else {
/* 175 */       java.util.logging.Logger.getLogger("com").setLevel(Level.FINER);
/* 176 */       java.util.logging.Logger.getLogger("com.dukascopy.transport").setLevel(Level.INFO);
/* 177 */       java.util.logging.Logger.getLogger("org.apache.mina").setLevel(Level.INFO);
/* 178 */       java.util.logging.Logger.getLogger("com.dukascopy.charts.data").setLevel(Level.FINEST);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getPackageName(Class c)
/*     */   {
/* 185 */     String fullyQualifiedName = c.getName();
/* 186 */     int lastDot = fullyQualifiedName.lastIndexOf(46);
/* 187 */     if (lastDot == -1) return "";
/* 188 */     return fullyQualifiedName.substring(0, lastDot);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  31 */     Locale.setDefault(Locale.US);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.GreedClient
 * JD-Core Version:    0.6.0
 */