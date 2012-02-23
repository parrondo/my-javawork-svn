/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.SystemColor;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class HeaderPanel extends JPanel
/*     */ {
/*  34 */   private static final boolean isJForexRunning = GreedContext.isStrategyAllowed();
/*     */ 
/*  36 */   private static int headerDarkGrayLevel = isJForexRunning ? 175 : 130;
/*  37 */   private static int headerLightGrayLevel = isJForexRunning ? 200 : 171;
/*     */ 
/*  39 */   private static final Color GREY_DARKER = new Color(headerDarkGrayLevel, headerDarkGrayLevel, headerDarkGrayLevel);
/*  40 */   private static final Color GREY_LIGHT = new Color(headerLightGrayLevel, headerLightGrayLevel, headerLightGrayLevel);
/*     */ 
/*  43 */   private static Font HEADER_FONT = new Font(null, 1, 24);
/*  44 */   private static Color HEADER_FOREGROUND_COLOR = SystemColor.window;
/*  45 */   private static Color HEADER_BACKGROUND_COLOR = SystemColor.controlShadow;
/*     */ 
/*  47 */   private static Color SHADOW = new Color(113, 111, 100);
/*     */   public static final String _HEADER_FONT = "HEADER_FONT";
/*     */   public static final String _HEADER_FOREGROUND_COLOR = "HEADER_FOREGROUND_COLOR";
/*     */   public static final String _HEADER_BACKGROUND_COLOR = "HEADER_BACKGROUND_COLOR";
/*     */   public static final String _LABEL_FOREGROUND_COLOR = "_LABEL_FOREGROUND_COLOR";
/*     */   public static final String _HEADER_GRADIENT_COLOR_FROM = "HEADER_GRADIENT_COLOR_FROM";
/*     */   public static final String _HEADER_GRADIENT_COLOR_TO = "HEADER_GRADIENT_COLOR_TO";
/*     */   public static final String _HEADER_TITLE = "HEADER_TITLE";
/*     */   public static final String _LABEL_TITLE = "LABEL_TITLE";
/*     */   private String title;
/*  61 */   private Map<String, Object> headerParams = new HashMap();
/*     */ 
/*  63 */   private boolean centeredHeader = false;
/*  64 */   protected boolean showRowCount = false;
/*     */ 
/*  66 */   private JLocalizableLabel rowCountLabel = new JLocalizableLabel("label.total");
/*  67 */   private JLabel rowCount = new JLabel();
/*     */ 
/*  69 */   private static final Font font = new Font(null, 1, 12);
/*     */ 
/*     */   public HeaderPanel(String title, boolean centered) {
/*  72 */     this.centeredHeader = centered;
/*  73 */     construct(title);
/*     */   }
/*     */ 
/*     */   public HeaderPanel(String title) {
/*  77 */     construct(title);
/*     */   }
/*     */ 
/*     */   public HeaderPanel(String title, boolean centered, boolean showRowCount) {
/*  81 */     this.showRowCount = showRowCount;
/*  82 */     this.centeredHeader = centered;
/*  83 */     construct(title);
/*     */   }
/*     */ 
/*     */   private void construct(String title)
/*     */   {
/*  88 */     this.headerParams.clear();
/*  89 */     this.headerParams.put("HEADER_TITLE", title);
/*  90 */     this.headerParams.put("LABEL_TITLE", title);
/*  91 */     this.headerParams.put("HEADER_GRADIENT_COLOR_FROM", GREY_LIGHT);
/*  92 */     this.headerParams.put("HEADER_GRADIENT_COLOR_TO", GREY_DARKER);
/*  93 */     build();
/*     */   }
/*     */ 
/*     */   public void setCustomization(Map<String, Object> headerParams)
/*     */   {
/* 100 */     setParameters(headerParams);
/* 101 */     modify();
/*     */   }
/*     */ 
/*     */   public void clearCustomization()
/*     */   {
/* 107 */     setParameters(null);
/* 108 */     modifyDefault();
/*     */   }
/*     */ 
/*     */   private void setParameters(Map<String, Object> headerParams) {
/* 112 */     if (headerParams == null) {
/* 113 */       this.headerParams.clear();
/* 114 */       return;
/*     */     }
/* 116 */     this.headerParams.putAll(headerParams);
/*     */   }
/*     */ 
/*     */   private void build() {
/* 120 */     this.title = ((String)this.headerParams.get("HEADER_TITLE"));
/*     */ 
/* 122 */     Font headerFont = (Font)this.headerParams.get("HEADER_FONT");
/*     */ 
/* 124 */     Color headerForegroundColor = (Color)this.headerParams.get("HEADER_FOREGROUND_COLOR");
/* 125 */     Color headerBackgroundColor = (Color)this.headerParams.get("HEADER_BACKGROUND_COLOR");
/*     */ 
/* 127 */     setBorder(null);
/* 128 */     if (headerForegroundColor != null) setForeground(headerForegroundColor); else
/* 129 */       setForeground(HEADER_FOREGROUND_COLOR);
/* 130 */     if (headerBackgroundColor != null) setBackground(headerBackgroundColor); else {
/* 131 */       setBackground(HEADER_BACKGROUND_COLOR);
/*     */     }
/* 133 */     int fheight = 0;
/* 134 */     if (headerFont != null)
/*     */     {
/* 136 */       fheight = getFontMetrics(headerFont).getHeight();
/*     */     }
/*     */     else {
/* 139 */       fheight = getFontMetrics(HEADER_FONT).getHeight();
/*     */     }
/*     */ 
/* 142 */     setLayout(new BoxLayout(this, 0));
/* 143 */     add(Box.createHorizontalStrut(5));
/* 144 */     add(Box.createHorizontalStrut(120));
/* 145 */     setPreferredSize(new Dimension(getPreferredSize().width, fheight + 5));
/* 146 */     setMaximumSize(new Dimension(32767, getPreferredSize().height));
/* 147 */     setMinimumSize(new Dimension(32767, getPreferredSize().height));
/*     */ 
/* 149 */     addLabelIfNeeded();
/*     */   }
/*     */ 
/*     */   private void addLabelIfNeeded()
/*     */   {
/* 154 */     if ((this.showRowCount) && (!GreedContext.isStrategyAllowed())) {
/* 155 */       add(Box.createHorizontalGlue());
/* 156 */       add(this.rowCountLabel);
/* 157 */       this.rowCount.setFont(font);
/* 158 */       this.rowCount.setForeground(Color.black);
/*     */ 
/* 160 */       add(this.rowCount);
/* 161 */       add(Box.createHorizontalStrut(30));
/* 162 */       revalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void modify()
/*     */   {
/* 169 */     if (this.headerParams.containsKey("HEADER_TITLE")) {
/* 170 */       this.title = ((String)this.headerParams.get("HEADER_TITLE"));
/*     */     }
/*     */ 
/* 173 */     if (this.headerParams.containsKey("HEADER_FOREGROUND_COLOR")) {
/* 174 */       Color headerForegroundColor = (Color)this.headerParams.get("HEADER_FOREGROUND_COLOR");
/* 175 */       if (headerForegroundColor != null) setForeground(headerForegroundColor);
/*     */     }
/*     */ 
/* 178 */     if (this.headerParams.containsKey("HEADER_BACKGROUND_COLOR")) {
/* 179 */       Color headerBackgroundColor = (Color)this.headerParams.get("HEADER_BACKGROUND_COLOR");
/* 180 */       if (headerBackgroundColor != null) setBackground(headerBackgroundColor);
/*     */     }
/*     */ 
/* 183 */     if (this.headerParams.containsKey("HEADER_FONT")) {
/* 184 */       Font headerFont = (Font)this.headerParams.get("HEADER_FONT");
/* 185 */       int fheight = 0;
/* 186 */       if (headerFont != null) {
/* 187 */         fheight = getFontMetrics(headerFont).getHeight();
/* 188 */         setPreferredSize(new Dimension(getPreferredSize().width, fheight + 5));
/* 189 */         setMaximumSize(new Dimension(32767, getPreferredSize().height));
/* 190 */         setMinimumSize(new Dimension(32767, getPreferredSize().height));
/*     */       }
/*     */     }
/*     */ 
/* 194 */     repaint();
/*     */   }
/*     */ 
/*     */   private void modifyDefault()
/*     */   {
/* 199 */     setForeground(HEADER_FOREGROUND_COLOR);
/* 200 */     setBackground(HEADER_BACKGROUND_COLOR);
/*     */ 
/* 202 */     int fheight = getFontMetrics(HEADER_FONT).getHeight();
/* 203 */     setPreferredSize(new Dimension(getPreferredSize().width, fheight + 5));
/* 204 */     setMaximumSize(new Dimension(32767, getPreferredSize().height));
/* 205 */     setMinimumSize(new Dimension(32767, getPreferredSize().height));
/*     */ 
/* 207 */     repaint();
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g) {
/* 211 */     super.paintComponent(g);
/* 212 */     Graphics2D g2 = (Graphics2D)g;
/* 213 */     Object hint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/* 214 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 215 */     if ((this.headerParams.containsKey("HEADER_GRADIENT_COLOR_FROM")) && (this.headerParams.containsKey("HEADER_GRADIENT_COLOR_TO"))) {
/* 216 */       fillVerticalGradient(g2);
/*     */     }
/*     */ 
/* 219 */     Font headerFont = (Font)this.headerParams.get("HEADER_FONT");
/*     */ 
/* 221 */     int fheight = 0;
/* 222 */     FontMetrics fm = null;
/* 223 */     if (headerFont != null) {
/* 224 */       g2.setFont(headerFont);
/* 225 */       fm = getFontMetrics(headerFont);
/* 226 */       fheight = fm.getHeight();
/*     */     } else {
/* 228 */       g2.setFont(HEADER_FONT);
/* 229 */       fm = getFontMetrics(HEADER_FONT);
/* 230 */       fheight = fm.getHeight();
/*     */     }
/*     */ 
/* 233 */     Dimension size = getSize();
/*     */ 
/* 235 */     int titleY = size.height / 2 + fheight / 2 - 6;
/* 236 */     int titleX = 0;
/* 237 */     if (this.centeredHeader)
/* 238 */       titleX = size.width / 2 - fm.stringWidth(this.title) / 2;
/*     */     else {
/* 240 */       titleX = 40;
/*     */     }
/*     */ 
/* 243 */     g2.setColor(SHADOW);
/* 244 */     g2.drawString(this.title, titleX + 1, titleY + 1);
/*     */ 
/* 246 */     g2.setColor(Color.white);
/* 247 */     g2.drawString(this.title, titleX, titleY);
/* 248 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hint);
/*     */   }
/*     */ 
/*     */   private void fillVerticalGradient(Graphics2D g2) {
/* 252 */     Color headerGradientColorFrom = (Color)this.headerParams.get("HEADER_GRADIENT_COLOR_FROM");
/* 253 */     Color headerGradientColorTo = (Color)this.headerParams.get("HEADER_GRADIENT_COLOR_TO");
/* 254 */     if ((headerGradientColorFrom != null) && (headerGradientColorTo != null)) {
/* 255 */       int x = Double.valueOf(getSize().getWidth()).intValue();
/* 256 */       int y = Double.valueOf(getSize().getHeight()).intValue();
/* 257 */       GradientPaint gp = new GradientPaint(0.0F, 0.0F, headerGradientColorFrom, 0.0F, y, headerGradientColorTo);
/* 258 */       g2.setPaint(gp);
/* 259 */       Rectangle r = new Rectangle(0, 0, x, y);
/* 260 */       g2.fill(r);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/* 265 */     this.title = title;
/* 266 */     repaint();
/*     */   }
/*     */ 
/*     */   public String getTitle() {
/* 270 */     return this.title;
/*     */   }
/*     */ 
/*     */   public boolean isCenteredHeader() {
/* 274 */     return this.centeredHeader;
/*     */   }
/*     */ 
/*     */   public void setCenteredHeader(boolean centeredHeader) {
/* 278 */     this.centeredHeader = centeredHeader;
/*     */   }
/*     */ 
/*     */   public void setRowCount(int rowCount) {
/* 282 */     if (rowCount > 0)
/* 283 */       this.rowCount.setText(String.valueOf(rowCount));
/*     */     else
/* 285 */       this.rowCount.setText("");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.HeaderPanel
 * JD-Core Version:    0.6.0
 */