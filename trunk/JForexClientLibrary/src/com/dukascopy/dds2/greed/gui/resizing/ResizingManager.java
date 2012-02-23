/*     */ package com.dukascopy.dds2.greed.gui.resizing;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class ResizingManager
/*     */ {
/*  29 */   private static final Map<WeakReference<Resizable>, WeakReference<Object>> CACHE = new HashMap();
/*     */   public static final float DEFAULT_FONT_SIZE = 14.0F;
/*  33 */   private static SizeMode currentSizeMode = SizeMode.SMALL;
/*     */ 
/*     */   public static void addResizable(Resizable resizable)
/*     */   {
/* 110 */     WeakReference resizableRef = new WeakReference(resizable);
/* 111 */     CACHE.put(resizableRef, new WeakReference(resizable.getDefaultSize()));
/*     */ 
/* 113 */     resizable.setSizeMode(getCompCorrectSize(resizableRef));
/*     */   }
/*     */ 
/*     */   private static Object getCompCorrectSize(WeakReference<Resizable> resizableRef) {
/* 117 */     if ((((WeakReference)CACHE.get(resizableRef)).get() instanceof ComponentSize)) {
/* 118 */       return ((ComponentSize)((WeakReference)CACHE.get(resizableRef)).get()).getSize();
/*     */     }
/* 120 */     if (!(((WeakReference)CACHE.get(resizableRef)).get() instanceof Integer)) return Float.valueOf(14.0F);
/* 121 */     if (!CACHE.containsKey(resizableRef)) {
/* 122 */       return ((Resizable)resizableRef.get()).getDefaultSize();
/*     */     }
/* 124 */     return Float.valueOf(new BigDecimal(currentSizeMode.getMultiplayer()).multiply(new BigDecimal(((Integer)((WeakReference)CACHE.get(resizableRef)).get()).intValue())).setScale(0, RoundingMode.HALF_UP).floatValue());
/*     */   }
/*     */ 
/*     */   private static synchronized void fireSizeChanged()
/*     */   {
/* 130 */     for (Map.Entry entry : CACHE.entrySet()) {
/* 131 */       Resizable resizable = (Resizable)((WeakReference)entry.getKey()).get();
/*     */ 
/* 133 */       if (resizable != null)
/* 134 */         resizable.setSizeMode(getCompCorrectSize((WeakReference)entry.getKey()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void setSize(SizeMode sizeMode)
/*     */   {
/* 140 */     currentSizeMode = sizeMode;
/*     */   }
/*     */ 
/*     */   public static void changeSize(SizeMode sizeMode) {
/* 144 */     SwingUtilities.invokeLater(new Runnable(sizeMode)
/*     */     {
/*     */       public void run() {
/* 147 */         ResizingManager.access$100(this.val$sizeMode);
/* 148 */         ResizingManager.access$200();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public static SizeMode getCurrentSizeMode() {
/* 154 */     return currentSizeMode;
/*     */   }
/*     */ 
/*     */   public static boolean isSmallMode() {
/* 158 */     return SizeMode.SMALL.equals(currentSizeMode);
/*     */   }
/*     */ 
/*     */   public static boolean isMediumMode() {
/* 162 */     return SizeMode.MEDIUM.equals(currentSizeMode);
/*     */   }
/*     */ 
/*     */   public static boolean isLargeMode() {
/* 166 */     return SizeMode.LARGE.equals(currentSizeMode);
/*     */   }
/*     */ 
/*     */   public static enum ComponentSize
/*     */   {
/*  56 */     SIZE_180X24(new Dimension(180, 24), new Dimension(180, 35), new Dimension(180, 50)), 
/*  57 */     SIZE_130X24(new Dimension(130, 24), new Dimension(165, 35), new Dimension(200, 50)), 
/*  58 */     SIZE_120X24(new Dimension(120, 24), new Dimension(150, 35), new Dimension(180, 50)), 
/*  59 */     SIZE_110X24(new Dimension(110, 24), new Dimension(138, 35), new Dimension(165, 50)), 
/*  60 */     SIZE_100X24(new Dimension(100, 24), new Dimension(125, 35), new Dimension(150, 50)), 
/*  61 */     SIZE_90X24(new Dimension(90, 24), new Dimension(113, 36), new Dimension(135, 51)), 
/*  62 */     SIZE_80X24(new Dimension(80, 24), new Dimension(100, 35), new Dimension(120, 50)), 
/*  63 */     SIZE_70X24(new Dimension(70, 24), new Dimension(88, 35), new Dimension(105, 50)), 
/*  64 */     SIZE_40X24(new Dimension(40, 24), new Dimension(40, 35), new Dimension(40, 50)), 
/*  65 */     SIZE_30X24(new Dimension(30, 24), new Dimension(38, 35), new Dimension(45, 50)), 
/*  66 */     SIZE_24X24(new Dimension(24, 24), new Dimension(30, 35), new Dimension(36, 50)), 
/*  67 */     SIZE_20X20(new Dimension(20, 20), new Dimension(20, 20), new Dimension(20, 20)), 
/*  68 */     SIZE_33X9(new Dimension(33, 9), new Dimension(45, 15), new Dimension(60, 25)), 
/*  69 */     TOLBAR_BTN_SIZE(new Dimension(30, 28), new Dimension(45, 40), new Dimension(60, 55));
/*     */ 
/*     */     private final Dimension smallSize;
/*     */     private final Dimension mediumSize;
/*     */     private final Dimension largeSize;
/*     */ 
/*  76 */     private ComponentSize(Dimension smallSize, Dimension mediumSize, Dimension largeSize) { this.smallSize = smallSize;
/*  77 */       this.mediumSize = mediumSize;
/*  78 */       this.largeSize = largeSize; }
/*     */ 
/*     */     protected Dimension getSmallSize()
/*     */     {
/*  82 */       return this.smallSize;
/*     */     }
/*     */ 
/*     */     protected Dimension getMediumSize() {
/*  86 */       return this.mediumSize;
/*     */     }
/*     */ 
/*     */     protected Dimension getLargeSize() {
/*  90 */       return this.largeSize;
/*     */     }
/*     */ 
/*     */     public Dimension getSize() {
/*  94 */       if (ResizingManager.SizeMode.SMALL.equals(ResizingManager.currentSizeMode))
/*  95 */         return getSmallSize();
/*  96 */       if (ResizingManager.SizeMode.MEDIUM.equals(ResizingManager.currentSizeMode))
/*  97 */         return getMediumSize();
/*  98 */       if (ResizingManager.SizeMode.LARGE.equals(ResizingManager.currentSizeMode)) {
/*  99 */         return getLargeSize();
/*     */       }
/* 101 */       return getSmallSize();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum SizeMode
/*     */   {
/*  37 */     SMALL("item.small.size.mode", 1.0F), 
/*  38 */     MEDIUM("item.medium.size.mode", 1.25F), 
/*  39 */     LARGE("item.large.size.mode", 1.5F);
/*     */ 
/*     */     public final String key;
/*     */     public final float multiplayer;
/*     */ 
/*  45 */     private SizeMode(String key, float multiplayer) { this.key = key;
/*  46 */       this.multiplayer = multiplayer; }
/*     */ 
/*     */     protected float getMultiplayer()
/*     */     {
/*  50 */       return this.multiplayer;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.resizing.ResizingManager
 * JD-Core Version:    0.6.0
 */