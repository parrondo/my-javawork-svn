/*     */ package com.dukascopy.dds2.greed.gui.component.table;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ 
/*     */ public final class HeaderUI
/*     */ {
/*  11 */   public static final Color xpBgColor = new Color(235, 234, 219);
/*  12 */   public static final Color xpCol1 = new Color(236, 233, 216);
/*  13 */   public static final Color xpCol2 = new Color(203, 199, 184);
/*  14 */   public static final Color xpCol3 = new Color(214, 210, 194);
/*  15 */   public static final Color xpCol4 = new Color(226, 222, 205);
/*  16 */   public static final Color xpWhite = new Color(255, 255, 255);
/*  17 */   public static final Color xpCol5 = new Color(199, 197, 178);
/*     */ 
/*  21 */   public static final Color classicDark = new Color(64, 64, 64);
/*  22 */   public static final Color classicMiddle = new Color(128, 128, 128);
/*  23 */   public static final Color classicWhite = new Color(255, 255, 255);
/*     */ 
/*  27 */   public static final Color metalBlue = new Color(122, 128, 153);
/*     */ 
/*  31 */   public static final Color vistaCol1 = new Color(240, 240, 240);
/*  32 */   public static final Color vistaCol2 = new Color(251, 251, 251);
/*  33 */   public static final Color vistaCol3 = new Color(213, 213, 213);
/*  34 */   public static final Color vistaCol4 = new Color(227, 228, 230);
/*  35 */   public static final Color vistaCol5 = new Color(250, 251, 252);
/*  36 */   public static final Color vistaCol6 = new Color(240, 240, 240);
/*  37 */   public static final Color vistaBgLightBlue = new Color(245, 246, 248);
/*     */ 
/*  41 */   public static final Color macStartColor = new Color(246, 246, 246);
/*  42 */   public static final Color macFinishColor = new Color(228, 228, 228);
/*  43 */   public static final Color macBgColor = new Color(245, 245, 245);
/*  44 */   public static final Color macLine = new Color(242, 242, 242, 100);
/*  45 */   public static final Color macDarkLine = new Color(131, 131, 131);
/*     */ 
/*     */   public static void xpHeaderCheckBoxStyle(Graphics g, CheckBoxHeader header)
/*     */   {
/*  54 */     int endY = header.getHeight() - 1;
/*  55 */     int endX = header.getWidth();
/*     */ 
/*  72 */     g.setColor(xpCol1);
/*  73 */     g.drawLine(0, endY, endX, endY);
/*     */ 
/*  75 */     g.setColor(xpCol2);
/*  76 */     g.drawLine(0, endY - 1, endX, endY - 1);
/*     */ 
/*  78 */     g.setColor(xpCol3);
/*  79 */     g.drawLine(0, endY - 2, endX, endY - 2);
/*     */ 
/*  81 */     g.setColor(xpCol4);
/*  82 */     g.drawLine(0, endY - 3, endX, endY - 3);
/*     */ 
/*  87 */     g.setColor(xpCol1);
/*  88 */     g.drawLine(endX - 1, 0, endX - 1, endY);
/*     */ 
/*  90 */     g.setColor(xpWhite);
/*  91 */     g.drawLine(endX - 2, 3, endX - 2, endY - 5);
/*     */ 
/*  93 */     g.setColor(xpCol5);
/*  94 */     g.drawLine(endX - 3, 3, endX - 3, endY - 5);
/*     */   }
/*     */ 
/*     */   public static void windowsClassicHeaderCheckBoxStyle(Graphics g, CheckBoxHeader header)
/*     */   {
/* 100 */     int endY = header.getHeight() - 1;
/* 101 */     int endX = header.getWidth();
/*     */ 
/* 106 */     g.setColor(classicDark);
/* 107 */     g.drawLine(0, endY, endX, endY);
/*     */ 
/* 109 */     g.setColor(classicDark);
/* 110 */     g.drawLine(endX - 1, 0, endX - 1, endY);
/*     */ 
/* 114 */     g.setColor(classicWhite);
/* 115 */     g.drawLine(0, 0, endX - 2, 0);
/*     */ 
/* 117 */     g.setColor(classicWhite);
/* 118 */     g.drawLine(0, 0, 0, endY - 1);
/*     */ 
/* 122 */     g.setColor(classicMiddle);
/* 123 */     g.drawLine(1, endY - 1, endX - 3, endY - 1);
/*     */ 
/* 125 */     g.setColor(classicMiddle);
/* 126 */     g.drawLine(endX - 2, 1, endX - 2, endY - 1);
/*     */ 
/* 132 */     int chBheight = 9;
/* 133 */     int ckbStartY = 5;
/* 134 */     int ckbStartX = 15;
/*     */ 
/* 136 */     int finishX = ckbStartX + 9 + 1;
/* 137 */     int finishY = ckbStartY + 9 + 1;
/*     */ 
/* 140 */     g.setColor(header.getBackground());
/* 141 */     g.drawLine(finishX, ckbStartY, finishX, finishY);
/* 142 */     g.drawLine(ckbStartX, finishY, finishX, finishY);
/*     */ 
/* 144 */     g.setColor(classicDark);
/* 145 */     g.drawLine(ckbStartX, ckbStartY, finishX - 1, ckbStartY);
/* 146 */     g.drawLine(ckbStartX, ckbStartY, ckbStartX, finishY - 1);
/*     */ 
/* 150 */     g.setColor(classicWhite);
/* 151 */     g.drawLine(ckbStartX - 1, finishY + 1, finishX + 1, finishY + 1);
/* 152 */     g.drawLine(finishX + 1, finishY, finishX + 1, ckbStartY - 1);
/*     */ 
/* 154 */     g.setColor(classicMiddle);
/* 155 */     g.drawLine(ckbStartX - 1, ckbStartY - 1, finishX, ckbStartY - 1);
/* 156 */     g.drawLine(ckbStartX - 1, ckbStartY - 1, ckbStartX - 1, finishY);
/*     */   }
/*     */ 
/*     */   public static void metalHeaderCheckBoxStyle(Graphics g, CheckBoxHeader header)
/*     */   {
/* 163 */     int endY = header.getHeight() - 1;
/* 164 */     int endX = header.getWidth();
/*     */ 
/* 166 */     g.setColor(metalBlue);
/* 167 */     g.drawLine(1, endY, endX, endY);
/* 168 */     g.drawLine(endX - 1, endY, endX - 1, 0);
/*     */ 
/* 170 */     g.setColor(classicWhite);
/* 171 */     g.drawLine(0, 0, endX - 2, 0);
/* 172 */     g.drawLine(0, 0, 0, endY - 1);
/*     */   }
/*     */ 
/*     */   public static void vistaHeaderCheckBoxStyle(Graphics g, CheckBoxHeader header) {
/* 176 */     int endY = header.getHeight() - 1;
/* 177 */     int endX = header.getWidth();
/*     */ 
/* 180 */     int ckbHeight = 15;
/* 181 */     int ckbStartY = 5;
/* 182 */     int ckbStartX = 14;
/*     */ 
/* 192 */     g.setColor(classicWhite);
/* 193 */     g.fillRect(0, 0, endX, ckbStartY + 1);
/* 194 */     g.fillRect(0, 0, ckbStartX, 9);
/* 195 */     g.fillRect(ckbHeight + ckbStartX - 2, 0, endX, 9);
/*     */ 
/* 201 */     g.setColor(vistaCol1);
/* 202 */     g.drawLine(0, endY, endX, endY);
/*     */ 
/* 204 */     g.setColor(vistaCol2);
/* 205 */     g.drawLine(0, endY - 1, endX, endY - 1);
/*     */ 
/* 207 */     g.setColor(vistaCol3);
/* 208 */     g.drawLine(0, endY - 2, endX, endY - 2);
/*     */ 
/* 213 */     g.setColor(vistaCol5);
/* 214 */     g.drawLine(0, 9, 0, endY - 3);
/*     */ 
/* 216 */     g.setColor(vistaCol1);
/* 217 */     g.drawLine(endX - 1, 0, endX - 1, endY);
/*     */ 
/* 219 */     g.setColor(vistaCol4);
/* 220 */     g.drawLine(endX - 2, 9, endX - 2, endY - 3);
/*     */ 
/* 222 */     g.setColor(vistaCol5);
/* 223 */     g.drawLine(endX - 3, 9, endX - 3, endY - 3);
/*     */ 
/* 226 */     g.setColor(vistaCol6);
/* 227 */     g.drawLine(endX - 3, 0, endX - 3, 9);
/*     */   }
/*     */ 
/*     */   public static void macOsHeaderCheckBoxStyle(Graphics g, CheckBoxHeader header)
/*     */   {
/* 234 */     int endY = header.getHeight() - 1;
/* 235 */     int endX = header.getWidth();
/*     */ 
/* 237 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 239 */     GradientPaint gradient = new GradientPaint(endX / 2, 0.0F, macStartColor, endX / 2, 2.0F, macFinishColor, true);
/* 240 */     g2.setPaint(gradient);
/* 241 */     g2.fillRect(0, 0, endX, 5);
/*     */ 
/* 243 */     g2.setColor(macLine);
/* 244 */     g2.drawLine(0, 6, 0, endY);
/* 245 */     g2.drawLine(0, endY - 2, endX, endY - 2);
/* 246 */     g2.drawLine(endX - 2, 0, endX - 2, endY);
/*     */ 
/* 248 */     g2.setColor(classicWhite);
/* 249 */     g2.drawLine(0, endY, endX, endY);
/*     */ 
/* 251 */     g2.setColor(macDarkLine);
/* 252 */     g2.drawLine(0, endY - 1, endX, endY - 1);
/* 253 */     g2.drawLine(endX - 1, 0, endX - 1, endY - 2);
/*     */   }
/*     */ 
/*     */   public static void xpHeader4ScrollerStyle(Graphics g, ScrollPaneHeaderRenderer header)
/*     */   {
/* 259 */     int endY = header.getHeight() - 1;
/* 260 */     int endX = header.getWidth();
/*     */ 
/* 268 */     g.setColor(xpBgColor);
/* 269 */     g.fillRect(0, 0, endX, endY);
/*     */ 
/* 273 */     g.setColor(xpCol1);
/* 274 */     g.drawLine(0, endY, endX, endY);
/*     */ 
/* 276 */     g.setColor(xpCol2);
/* 277 */     g.drawLine(0, endY - 1, endX, endY - 1);
/*     */ 
/* 279 */     g.setColor(xpCol3);
/* 280 */     g.drawLine(0, endY - 2, endX, endY - 2);
/*     */ 
/* 282 */     g.setColor(xpCol4);
/* 283 */     g.drawLine(0, endY - 3, endX, endY - 3);
/*     */   }
/*     */ 
/*     */   public static void windowsClassicHeader4ScrollerStyle(Graphics g, ScrollPaneHeaderRenderer header)
/*     */   {
/* 301 */     int endY = header.getHeight() - 1;
/* 302 */     int endX = header.getWidth();
/*     */ 
/* 307 */     g.setColor(classicDark);
/* 308 */     g.drawLine(0, endY, endX, endY);
/*     */ 
/* 310 */     g.setColor(classicDark);
/* 311 */     g.drawLine(endX - 1, 0, endX - 1, endY);
/*     */ 
/* 315 */     g.setColor(classicWhite);
/* 316 */     g.drawLine(0, 0, endX - 2, 0);
/*     */ 
/* 318 */     g.setColor(classicWhite);
/* 319 */     g.drawLine(0, 0, 0, endY - 1);
/*     */ 
/* 323 */     g.setColor(classicMiddle);
/* 324 */     g.drawLine(1, endY - 1, endX, endY - 1);
/*     */ 
/* 326 */     g.setColor(classicMiddle);
/* 327 */     g.drawLine(endX - 2, 1, endX - 2, endY - 1);
/*     */   }
/*     */ 
/*     */   public static void metalHeader4ScrollerStyle(Graphics g, ScrollPaneHeaderRenderer header)
/*     */   {
/* 334 */     int endY = header.getHeight() - 1;
/* 335 */     int endX = header.getWidth();
/*     */ 
/* 337 */     g.setColor(metalBlue);
/* 338 */     g.drawLine(1, endY, endX, endY);
/*     */ 
/* 341 */     g.setColor(classicWhite);
/* 342 */     g.drawLine(0, 0, endX - 2, 0);
/* 343 */     g.drawLine(0, 0, 0, endY - 1);
/*     */   }
/*     */ 
/*     */   public static void vistaHeader4ScrollerStyle(Graphics g, ScrollPaneHeaderRenderer header) {
/* 347 */     int endY = header.getHeight() - 1;
/* 348 */     int endX = header.getWidth();
/*     */ 
/* 351 */     int ckbHeight = 15;
/*     */ 
/* 353 */     int ckbStartX = 14;
/*     */ 
/* 363 */     g.setColor(classicWhite);
/* 364 */     g.fillRect(0, 0, endX, endY);
/* 365 */     g.fillRect(0, 0, ckbStartX, 9);
/* 366 */     g.fillRect(ckbHeight + ckbStartX - 2, 0, endX, 9);
/*     */ 
/* 372 */     g.setColor(vistaCol1);
/* 373 */     g.drawLine(0, endY, endX, endY);
/*     */ 
/* 375 */     g.setColor(vistaCol2);
/* 376 */     g.drawLine(0, endY - 1, endX, endY - 1);
/*     */ 
/* 378 */     g.setColor(vistaCol3);
/* 379 */     g.drawLine(0, endY - 2, endX, endY - 2);
/*     */ 
/* 384 */     g.setColor(vistaCol5);
/* 385 */     g.drawLine(0, 9, 0, endY - 3);
/*     */ 
/* 387 */     g.setColor(vistaCol1);
/* 388 */     g.drawLine(endX - 1, 0, endX - 1, endY);
/*     */ 
/* 390 */     g.setColor(vistaCol4);
/* 391 */     g.drawLine(endX - 2, 9, endX - 2, endY - 3);
/*     */ 
/* 393 */     g.setColor(vistaCol5);
/* 394 */     g.drawLine(endX - 3, 9, endX - 3, endY - 3);
/*     */ 
/* 397 */     g.setColor(vistaCol6);
/* 398 */     g.drawLine(endX - 3, 0, endX - 3, 9);
/*     */   }
/*     */ 
/*     */   public static void macOsHeader4ScrollerStyle(Graphics g, ScrollPaneHeaderRenderer header)
/*     */   {
/* 405 */     int endY = header.getHeight() - 1;
/* 406 */     int endX = header.getWidth();
/*     */ 
/* 408 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 410 */     g2.setColor(macBgColor);
/* 411 */     g2.drawRect(0, 0, endX, endY);
/*     */ 
/* 413 */     GradientPaint gradient = new GradientPaint(endX / 2, 0.0F, macStartColor, endX / 2, 2.0F, macFinishColor, true);
/* 414 */     g2.setPaint(gradient);
/* 415 */     g2.fillRect(0, 0, endX, 5);
/*     */ 
/* 417 */     g2.setColor(macLine);
/* 418 */     g2.drawLine(0, 6, 0, endY);
/* 419 */     g2.drawLine(0, endY - 2, endX, endY - 2);
/* 420 */     g2.drawLine(endX - 2, 0, endX - 2, endY);
/*     */ 
/* 422 */     g2.setColor(classicWhite);
/* 423 */     g2.drawLine(0, endY, endX, endY);
/*     */ 
/* 425 */     g2.setColor(macDarkLine);
/* 426 */     g2.drawLine(0, endY - 1, endX, endY - 1);
/* 427 */     g2.drawLine(endX - 1, 0, endX - 1, endY - 2);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.HeaderUI
 * JD-Core Version:    0.6.0
 */