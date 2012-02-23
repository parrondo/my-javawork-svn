/*     */ package com.dukascopy.charts.persistence;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class ThemeManager
/*     */ {
/*     */   public static final String DEFAULT_THEME_NAME = "Default";
/*  14 */   private static final BasicStroke fineDashedStroke = new BasicStroke(1.0F, 0, 2, 0.0F, new float[] { 2.0F }, 0.0F);
/*  15 */   private static final BasicStroke dotedStroke = new BasicStroke(1.0F, 0, 2, 0.0F, new float[] { 1.0F, 2.0F }, 0.0F);
/*     */ 
/*  17 */   private static final List<ITheme> DEFAULTS = new ArrayList();
/*  18 */   private static final List<ITheme> THEMES = new ArrayList();
/*     */   private static ITheme currentTheme;
/*     */ 
/*     */   public static void setDefaultTheme()
/*     */   {
/*  33 */     setTheme("Default");
/*     */   }
/*     */ 
/*     */   public static ITheme getTheme() {
/*  37 */     return currentTheme;
/*     */   }
/*     */ 
/*     */   public static ITheme getDefaultTheme() {
/*  41 */     return getTheme("Default");
/*     */   }
/*     */ 
/*     */   public static ITheme getTheme(String themeName) {
/*  45 */     for (ITheme theme : THEMES) {
/*  46 */       if (theme.getName().equalsIgnoreCase(themeName)) {
/*  47 */         return theme;
/*     */       }
/*     */     }
/*     */ 
/*  51 */     return null;
/*     */   }
/*     */ 
/*     */   public static void setTheme(ITheme theme) {
/*  55 */     currentTheme = theme;
/*     */ 
/*  57 */     if (!isExist(theme.getName()))
/*  58 */       THEMES.add(theme);
/*     */     else
/*  60 */       modify(theme);
/*     */   }
/*     */ 
/*     */   public static void setTheme(String name)
/*     */   {
/*  65 */     for (ITheme theme : THEMES)
/*  66 */       if (theme.getName().equalsIgnoreCase(name)) {
/*  67 */         currentTheme = theme;
/*  68 */         return;
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void add(ITheme theme)
/*     */   {
/*  74 */     if (!isDefault(theme.getName()))
/*  75 */       THEMES.add(theme);
/*     */   }
/*     */ 
/*     */   public static void modify(ITheme modifiedTheme)
/*     */   {
/*  80 */     if (!isDefault(modifiedTheme.getName())) {
/*  81 */       ITheme actualTheme = getTheme(modifiedTheme.getName());
/*  82 */       int index = THEMES.indexOf(actualTheme);
/*  83 */       THEMES.set(index, modifiedTheme);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void remove(String themeName) {
/*  88 */     if (!isDefault(themeName))
/*  89 */       for (ITheme theme : THEMES)
/*  90 */         if (theme.getName().equalsIgnoreCase(themeName)) {
/*  91 */           THEMES.remove(theme);
/*  92 */           setTheme((ITheme)DEFAULTS.get(0));
/*  93 */           return;
/*     */         }
/*     */   }
/*     */ 
/*     */   public static List<ITheme> getThemes()
/*     */   {
/* 100 */     return Collections.unmodifiableList(THEMES);
/*     */   }
/*     */ 
/*     */   public static List<ITheme> getDefaultThemes() {
/* 104 */     return Collections.unmodifiableList(DEFAULTS);
/*     */   }
/*     */ 
/*     */   public static boolean isDefault(String themeName) {
/* 108 */     for (ITheme theme : DEFAULTS) {
/* 109 */       if (theme.getName().equalsIgnoreCase(themeName.trim())) {
/* 110 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 114 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isExist(String themeName) {
/* 118 */     for (ITheme theme : THEMES) {
/* 119 */       if (theme.getName().equalsIgnoreCase(themeName)) {
/* 120 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 124 */     return false;
/*     */   }
/*     */ 
/*     */   private static void initDefaults() {
/* 128 */     DEFAULTS.add(new Theme("Default")
/*     */     {
/*     */     });
/* 168 */     DEFAULTS.add(new Theme("Black on White")
/*     */     {
/*     */     });
/* 200 */     DEFAULTS.add(new Theme("Blue on White")
/*     */     {
/*     */     });
/* 237 */     DEFAULTS.add(new Theme("Green on Black")
/*     */     {
/*     */     });
/* 276 */     DEFAULTS.add(new Theme("Yellow on Black")
/*     */     {
/*     */     });
/* 314 */     DEFAULTS.add(new Theme("Blue and Grey on Grey")
/*     */     {
/*     */     });
/* 367 */     DEFAULTS.add(new Theme("Blue and Grey on White")
/*     */     {
/*     */     });
/* 421 */     DEFAULTS.add(new Theme("Green and Red on White")
/*     */     {
/*     */     });
/* 473 */     DEFAULTS.add(new Theme("Orange and Grey on Grey")
/*     */     {
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  23 */     initDefaults();
/*     */ 
/*  25 */     for (ITheme theme : DEFAULTS) {
/*  26 */       setTheme(theme);
/*     */     }
/*     */ 
/*  29 */     setDefaultTheme();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.persistence.ThemeManager
 * JD-Core Version:    0.6.0
 */