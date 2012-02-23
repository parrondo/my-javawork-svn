/*     */ package com.dukascopy.dds2.greed.gui.l10n;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.utils.LocalizationPropertyResourceBundle;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIDefaults;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.plaf.FontUIResource;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LocalizationManager
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationManager.class);
/*     */   private static final String RESOURCE_BUNDLE_PATH = "rc/languages/";
/*  29 */   private static final List<WeakReference<Localizable>> CACHE = new ArrayList();
/*  30 */   private static Language currentLanguage = Language.ENGLISH;
/*  31 */   private static ResourceBundle bundle = null;
/*     */   public static final String SELECTED_LOCALE = "selectedLocale";
/*     */ 
/*     */   public static synchronized void addLocalizable(Localizable localizable)
/*     */   {
/*  75 */     CACHE.add(new WeakReference(localizable));
/*  76 */     localizable.localize();
/*     */   }
/*     */ 
/*     */   public static Language getLanguage() {
/*  80 */     return currentLanguage;
/*     */   }
/*     */ 
/*     */   private static synchronized void setLanguage(Language language) {
/*  84 */     currentLanguage = language;
/*  85 */     Locale.setDefault(language.locale);
/*  86 */     JOptionPane.setDefaultLocale(language.locale);
/*     */   }
/*     */ 
/*     */   public static void changeLanguage(Language language) {
/*  90 */     SwingUtilities.invokeLater(new Runnable(language)
/*     */     {
/*     */       public void run() {
/*  93 */         LocalizationManager.access$000(this.val$language);
/*  94 */         LocalizationManager.saveSelectedLocale(this.val$language.locale);
/*  95 */         LocalizationManager.access$100();
/*  96 */         LocalizationManager.access$200();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private static synchronized void loadBundle() {
/*     */     try {
/* 103 */       InputStream inputStream = LocalizationManager.class.getClassLoader().getResourceAsStream("rc/languages/" + currentLanguage.locale.getLanguage() + ".properties");
/*     */ 
/* 105 */       if (inputStream == null) {
/* 106 */         throw new MissingResourceException("Localization file not found", LocalizationManager.class.getSimpleName(), currentLanguage.locale.getLanguage());
/*     */       }
/*     */ 
/* 109 */       bundle = new LocalizationPropertyResourceBundle(new InputStreamReader(inputStream, "UTF-8"));
/*     */     } catch (Exception ex) {
/* 111 */       LOGGER.warn("Unable to load resource bundle for [" + currentLanguage.locale + "] due : " + ex.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void fireLanguageChanged() {
/* 116 */     for (WeakReference weakReference : CACHE) {
/* 117 */       Localizable localizable = (Localizable)weakReference.get();
/*     */ 
/* 119 */       if ((localizable instanceof Component)) {
/* 120 */         ((Component)localizable).setLocale(currentLanguage.locale);
/*     */       }
/*     */ 
/* 123 */       if (localizable != null)
/* 124 */         localizable.localize();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getText(String key)
/*     */   {
/* 130 */     if (key == null) {
/* 131 */       return "";
/*     */     }
/*     */     try
/*     */     {
/* 135 */       return bundle.getString(key);
/*     */     } catch (MissingResourceException ex) {
/* 137 */       LOGGER.warn(ex.getMessage());
/* 138 */     }return key;
/*     */   }
/*     */ 
/*     */   public static String getTextWithArguments(String key, Object[] arguments)
/*     */   {
/* 143 */     if (key == null) {
/* 144 */       return "";
/*     */     }
/*     */     try
/*     */     {
/* 148 */       String pattern = bundle.getString(key);
/* 149 */       return MessageFormat.format(pattern, arguments);
/*     */     } catch (MissingResourceException ex) {
/* 151 */       LOGGER.warn(ex.getMessage());
/* 152 */     }return key;
/*     */   }
/*     */ 
/*     */   public static String getTextWithArgumentKeys(String key, Object[] argumentKeys)
/*     */   {
/* 157 */     if (key == null) {
/* 158 */       return "";
/*     */     }
/*     */     try
/*     */     {
/* 162 */       String pattern = bundle.getString(key);
/* 163 */       Object[] paramsArray = new Object[argumentKeys.length];
/*     */ 
/* 165 */       for (int i = 0; i < argumentKeys.length; i++) {
/*     */         try {
/* 167 */           paramsArray[i] = bundle.getString((String)argumentKeys[i]);
/*     */         } catch (MissingResourceException ex) {
/* 169 */           paramsArray[i] = argumentKeys[i];
/*     */         }
/*     */       }
/*     */ 
/* 173 */       return MessageFormat.format(pattern, paramsArray);
/*     */     } catch (MissingResourceException ex) {
/* 175 */       LOGGER.warn(ex.getMessage());
/* 176 */     }return key;
/*     */   }
/*     */ 
/*     */   public static Locale getSelectedLocale()
/*     */   {
/* 181 */     Preferences node = Preferences.systemRoot().node("common");
/* 182 */     String locale = node.get("selectedLocale", null);
/*     */ 
/* 184 */     if (locale != null) {
/* 185 */       return new Locale(locale);
/*     */     }
/* 187 */     return Locale.ENGLISH;
/*     */   }
/*     */ 
/*     */   public static void saveSelectedLocale(Locale locale) {
/* 191 */     if (locale == null) {
/* 192 */       return;
/*     */     }
/* 194 */     Preferences node = Preferences.systemRoot().node("common");
/* 195 */     node.put("selectedLocale", locale.toString());
/*     */   }
/*     */ 
/*     */   public static void clearCache() {
/* 199 */     CACHE.clear();
/*     */   }
/*     */ 
/*     */   public static Font getDefaultFont(int fontSize) {
/* 203 */     if (Language.JAPANESE.equals(getLanguage())) {
/* 204 */       return new FontUIResource("MS UI Gothic", 0, fontSize);
/*     */     }
/* 206 */     return ((FontUIResource)UIManager.getDefaults().get("Label.font")).deriveFont(new BigDecimal(fontSize).floatValue());
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  71 */     loadBundle();
/*     */   }
/*     */ 
/*     */   public static enum Language
/*     */   {
/*  36 */     ARABIC(new Locale("ar"), "short.menu.item.lang.arabic", "menu.item.lang.arabic", new ResizableIcon("menu_flag_al.png")), 
/*  37 */     BULGARIAN(new Locale("bg"), "short.menu.item.lang.bulgarian", "menu.item.lang.bulgarian", new ResizableIcon("menu_flag_bg.png")), 
/*  38 */     CHINESE(new Locale("cn"), "short.menu.item.lang.china", "menu.item.lang.china", new ResizableIcon("menu_flag_cn.png")), 
/*  39 */     ENGLISH(Locale.ENGLISH, "short.menu.item.lang.english", "menu.item.lang.english", new ResizableIcon("menu_flag_gb.png")), 
/*  40 */     FRENCH(Locale.FRENCH, "short.menu.item.lang.french", "menu.item.lang.french", new ResizableIcon("menu_flag_fr.png")), 
/*  41 */     GERMAN(Locale.GERMAN, "short.menu.item.lang.german", "menu.item.lang.german", new ResizableIcon("menu_flag_de.png")), 
/*  42 */     ITALIAN(Locale.ITALIAN, "short.menu.item.lang.italian", "menu.item.lang.italian", new ResizableIcon("menu_flag_it.png")), 
/*  43 */     JAPANESE(Locale.JAPANESE, "short.menu.item.lang.japanese", "menu.item.lang.japanese", new ResizableIcon("menu_flag_jp.png")), 
/*  44 */     PORTUGUESE(new Locale("pt"), "short.menu.item.lang.portuguese", "menu.item.lang.portuguese", new ResizableIcon("menu_flag_pt.png")), 
/*  45 */     RUSSIAN(new Locale("ru"), "short.menu.item.lang.russian", "menu.item.lang.russian", new ResizableIcon("menu_flag_ru.png")), 
/*  46 */     SPANISH(new Locale("es"), "short.menu.item.lang.spanish", "menu.item.lang.spanish", new ResizableIcon("menu_flag_es.png"));
/*     */ 
/*     */     public final Locale locale;
/*     */     public final String shortKey;
/*     */     public final String longKey;
/*     */     public final Icon icon;
/*     */ 
/*  54 */     private Language(Locale locale, String shortKey, String longKey, Icon icon) { this.locale = locale;
/*  55 */       this.shortKey = shortKey;
/*  56 */       this.longKey = longKey;
/*  57 */       this.icon = icon;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  62 */       return LocalizationManager.getText(this.shortKey);
/*     */     }
/*     */ 
/*     */     public Icon getIcon() {
/*  66 */       return this.icon;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.l10n.LocalizationManager
 * JD-Core Version:    0.6.0
 */