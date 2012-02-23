/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.util.LookAndFeelSpecific;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Shape;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.geom.Point2D.Double;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.plaf.ComponentUI;
/*     */ import javax.swing.plaf.basic.BasicButtonUI;
/*     */ import javax.swing.text.View;
/*     */ 
/*     */ public class JRoundedButtonUI extends BasicButtonUI
/*     */   implements MouseListener, KeyListener, FocusListener, LookAndFeelSpecific
/*     */ {
/*     */   protected JButton myButton;
/*  22 */   private Shape shape = null;
/*  23 */   private boolean pressed = false;
/*  24 */   private boolean mouseOver = false;
/*     */   private String text;
/*  27 */   private int startX = 1;
/*  28 */   private int startY = 1;
/*  29 */   private int myWidht = 0;
/*  30 */   private int myHeight = 0;
/*     */ 
/*  32 */   private static final Color lightGray = new Color(230, 230, 230);
/*  33 */   private static Color col1 = null;
/*  34 */   private static Color col2 = null;
/*     */   private static FontMetrics fm;
/*  37 */   private static final Color lightCreme = new Color(250, 248, 243);
/*     */   private static GradientPaint pressedGradient;
/*     */   private static GradientPaint unPressedGradient;
/*  42 */   int i = 0;
/*     */ 
/*     */   public void installUI(JComponent c)
/*     */   {
/*  48 */     this.myButton = ((JButton)c);
/*     */ 
/*  50 */     this.myButton.addMouseListener(this);
/*  51 */     this.myButton.addFocusListener(this);
/*  52 */     this.myButton.addKeyListener(this);
/*  53 */     this.myButton.setFocusable(true);
/*     */ 
/*  55 */     super.installUI(this.myButton);
/*     */ 
/*  57 */     if (METAL) {
/*  58 */       if ((this.myButton.getText() != null) && (!"".equals(this.myButton.getText())))
/*  59 */         this.myButton.setBorderPainted(false);
/*     */     }
/*     */     else {
/*  62 */       this.myButton.setBorderPainted(false);
/*     */     }
/*     */ 
/*  65 */     if (col1 == null) {
/*  66 */       col1 = this.myButton.getBackground().brighter();
/*     */     }
/*     */ 
/*  69 */     if (col2 == null)
/*     */     {
/*  71 */       col2 = this.myButton.getBackground().darker();
/*     */     }
/*     */ 
/*  74 */     fm = this.myButton.getFontMetrics(c.getFont());
/*     */   }
/*     */ 
/*     */   public void uninstallUI(JComponent c)
/*     */   {
/*  79 */     super.uninstallUI(c);
/*     */   }
/*     */ 
/*     */   public static ComponentUI createUI(JComponent c) {
/*  83 */     return new JRoundedButtonUI();
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g, JComponent c)
/*     */   {
/*  89 */     this.text = ((JButton)c).getText();
/*     */ 
/*  91 */     if (CLASSIC)
/*     */     {
/*  93 */       Icon icon = ((JButton)c).getIcon();
/*  94 */       if (((icon == null) || (icon.toString().contains("FrameButtonIcon"))) && ((this.text == null) || (this.text.trim().equals("")))) {
/*  95 */         ((JButton)c).setBorderPainted(true);
/*  96 */         super.paint(g, c);
/*  97 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 103 */     Graphics2D g2 = (Graphics2D)g;
/* 104 */     g2.setColor(c.getBackground());
/*     */ 
/* 107 */     c.setVisible(true);
/*     */ 
/* 109 */     this.startX = 0;
/* 110 */     this.startY = 1;
/*     */ 
/* 112 */     this.myWidht = (c.getSize().width - 2);
/* 113 */     this.myHeight = (c.getSize().height - 3);
/*     */ 
/* 116 */     if (c.hasFocus()) {
/* 117 */       paintRegularButton(g2, c);
/* 118 */       onMouseOver(g2, c);
/* 119 */       drawFocusableRenctangle(g2);
/*     */     } else {
/* 121 */       paintRegularButton(g2, c);
/* 122 */       onMouseOver(g2, c);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void paintRegularButton(Graphics2D g2, JComponent c)
/*     */   {
/* 128 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 130 */     g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/*     */ 
/* 133 */     GradientPaint gp = null;
/* 134 */     Point2D.Double pt1 = new Point2D.Double(0.0D, 2.0D);
/* 135 */     Point2D.Double pt2 = new Point2D.Double(0.0D, this.myButton.getSize().height);
/*     */ 
/* 137 */     pressedGradient = new GradientPaint(pt1, col2, pt2, col1, false);
/* 138 */     unPressedGradient = new GradientPaint(pt1, col1, pt2, col2, false);
/*     */ 
/* 140 */     if (this.pressed)
/* 141 */       gp = pressedGradient;
/*     */     else {
/* 143 */       gp = unPressedGradient;
/*     */     }
/*     */ 
/* 147 */     g2.setPaint(gp);
/*     */ 
/* 151 */     if (this.text == null) this.text = "";
/* 152 */     Rectangle2D area = fm.getStringBounds(this.text, g2);
/*     */ 
/* 157 */     if (this.myWidht < area.getWidth() + 3.0D) {
/* 158 */       this.myWidht += 1;
/* 159 */       this.myHeight += 1;
/* 160 */       this.startX = 0;
/* 161 */       this.startY = 0;
/*     */     }
/*     */ 
/* 164 */     g2.fillRoundRect(this.startX, this.startY, this.myWidht, this.myHeight, 10, 10);
/*     */ 
/* 167 */     if (c.isEnabled())
/* 168 */       g2.setColor(SystemColor.GRAY);
/*     */     else {
/* 170 */       g2.setColor(SystemColor.lightGray);
/*     */     }
/* 172 */     g2.drawRoundRect(this.startX, this.startY, this.myWidht, this.myHeight, 10, 10);
/*     */ 
/* 175 */     if (c.isEnabled())
/* 176 */       g2.setColor(Color.black);
/*     */     else {
/* 178 */       g2.setColor(Color.gray);
/*     */     }
/* 180 */     if (c.isEnabled()) {
/* 181 */       if ((this.myButton.getIcon() != null) && ((this.myButton.getText() == null) || ("".equals(this.myButton.getText())))) {
/* 182 */         int iconX = this.myWidht / 2 - this.myButton.getIcon().getIconWidth() / 2 + 1;
/* 183 */         int iconY = this.myHeight / 2 - this.myButton.getIcon().getIconHeight() / 2 + 2;
/* 184 */         this.myButton.getIcon().paintIcon(this.myButton, g2, iconX, iconY);
/* 185 */       } else if ((this.myButton.getText() != null) && (!"".equals(this.myButton.getText())) && (this.myButton.getIcon() != null)) {
/* 186 */         int iconX = this.startX + 4;
/* 187 */         int iconY = this.startY + 3;
/* 188 */         this.myButton.getIcon().paintIcon(this.myButton, g2, iconX, iconY);
/*     */       }
/*     */     }
/* 191 */     else if ((this.myButton.getDisabledIcon() != null) && ((this.myButton.getText() == null) || ("".equals(this.myButton.getText())))) {
/* 192 */       int iconX = this.myWidht / 2 - this.myButton.getDisabledIcon().getIconWidth() / 2 + 1;
/* 193 */       int iconY = this.myHeight / 2 - this.myButton.getDisabledIcon().getIconHeight() / 2 + 2;
/* 194 */       this.myButton.getDisabledIcon().paintIcon(this.myButton, g2, iconX, iconY);
/* 195 */     } else if ((this.myButton.getText() != null) && (!"".equals(this.myButton.getText())) && (this.myButton.getIcon() != null)) {
/* 196 */       int iconX = this.startX + 4;
/* 197 */       int iconY = this.startY + 3;
/* 198 */       this.myButton.getDisabledIcon().paintIcon(this.myButton, g2, iconX, iconY);
/*     */     }
/*     */ 
/* 202 */     AbstractButton b = (AbstractButton)c;
/*     */ 
/* 204 */     Rectangle textRect = new Rectangle();
/* 205 */     Rectangle iconRect = new Rectangle();
/* 206 */     Rectangle viewRect = new Rectangle();
/*     */ 
/* 208 */     String s = layout(b, fm, b.getWidth(), b.getHeight(), textRect, viewRect, iconRect);
/*     */ 
/* 210 */     if ((this.text != null) && (!this.text.equals(""))) {
/* 211 */       View v = (View)c.getClientProperty("html");
/* 212 */       if (v != null)
/* 213 */         v.paint(g2, textRect);
/*     */       else
/* 215 */         paintText(g2, b, textRect, this.text);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String layout(AbstractButton b, FontMetrics fm, int width, int height, Rectangle textRect, Rectangle viewRect, Rectangle iconRect)
/*     */   {
/* 235 */     Insets i = b.getInsets();
/* 236 */     viewRect.x = i.left;
/* 237 */     viewRect.y = i.top;
/* 238 */     viewRect.width = (width - (i.right + viewRect.x));
/* 239 */     viewRect.height = (height - (i.bottom + viewRect.y));
/*     */ 
/* 241 */     textRect.x = (textRect.y = textRect.width = textRect.height = 0);
/* 242 */     iconRect.x = (iconRect.y = iconRect.width = iconRect.height = 0);
/*     */ 
/* 244 */     return SwingUtilities.layoutCompoundLabel(b, fm, b.getText(), b.getIcon(), b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect, iconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());
/*     */   }
/*     */ 
/*     */   private void onMouseOver(Graphics2D g2, JComponent c)
/*     */   {
/* 254 */     if ((c.isEnabled()) && (this.mouseOver))
/*     */     {
/* 256 */       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 258 */       g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/*     */ 
/* 262 */       g2.setColor(lightCreme);
/* 263 */       g2.drawRoundRect(this.startX + 1, this.startY + 1, this.myWidht - 2, this.myHeight - 2, 10, 10);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawFocusableRenctangle(Graphics2D g2)
/*     */   {
/* 269 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 271 */     g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/*     */ 
/* 274 */     g2.setColor(SystemColor.gray);
/*     */ 
/* 276 */     int startX = this.startX + 2;
/* 277 */     int startY = this.startY + 2;
/*     */ 
/* 279 */     int finishX = startX + this.myWidht - 4;
/* 280 */     int finishY = startY + this.myHeight - 4;
/*     */ 
/* 282 */     double dashlength = 0.0D;
/* 283 */     double spacelength = 2.0D;
/*     */ 
/* 285 */     drawDashedLine(g2, startX, startY, finishX, startY, dashlength, spacelength);
/* 286 */     drawDashedLine(g2, startX, finishY, finishX, finishY, dashlength, spacelength);
/* 287 */     drawDashedLine(g2, startX, startY, startX, finishY, dashlength, spacelength);
/* 288 */     drawDashedLine(g2, finishX, startY, finishX, finishY, dashlength, spacelength);
/*     */   }
/*     */ 
/*     */   public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2, double dashlength, double spacelength)
/*     */   {
/* 293 */     if ((x1 == x2) && (y1 == y2)) {
/* 294 */       g.drawLine(x1, y1, x2, y2);
/* 295 */       return;
/*     */     }
/* 297 */     double linelength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
/* 298 */     double yincrement = (y2 - y1) / (linelength / (dashlength + spacelength));
/* 299 */     double xincdashspace = (x2 - x1) / (linelength / (dashlength + spacelength));
/* 300 */     double yincdashspace = (y2 - y1) / (linelength / (dashlength + spacelength));
/* 301 */     double xincdash = (x2 - x1) / (linelength / dashlength);
/* 302 */     double yincdash = (y2 - y1) / (linelength / dashlength);
/* 303 */     int counter = 0;
/* 304 */     for (double i = 0.0D; i < linelength - dashlength; i += dashlength + spacelength) {
/* 305 */       g.drawLine((int)(x1 + xincdashspace * counter), (int)(y1 + yincdashspace * counter), (int)(x1 + xincdashspace * counter + xincdash), (int)(y1 + yincdashspace * counter + yincdash));
/*     */ 
/* 309 */       counter++;
/*     */     }
/* 311 */     if ((dashlength + spacelength) * counter <= linelength)
/* 312 */       g.drawLine((int)(x1 + xincdashspace * counter), (int)(y1 + yincdashspace * counter), x2, y2);
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/* 322 */     if (this.myButton.isEnabled()) {
/* 323 */       this.pressed = true;
/* 324 */       this.myButton.requestFocus();
/* 325 */       this.myButton.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/* 330 */     if (this.myButton.isEnabled()) {
/* 331 */       this.pressed = false;
/* 332 */       this.myButton.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e) {
/* 337 */     if (this.myButton.isEnabled()) {
/* 338 */       this.mouseOver = true;
/* 339 */       this.myButton.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e) {
/* 344 */     if (this.myButton.isEnabled()) {
/* 345 */       this.mouseOver = false;
/* 346 */       this.myButton.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e) {
/* 351 */     if (this.myButton.isEnabled()) {
/* 352 */       int key = e.getKeyCode();
/* 353 */       if ((this.myButton.hasFocus()) && (key == 10)) {
/* 354 */         this.pressed = true;
/* 355 */         this.myButton.repaint();
/*     */       }
/* 357 */       if ((this.myButton.hasFocus()) && (key == 9)) {
/* 358 */         this.myButton.requestFocus(false);
/* 359 */         this.myButton.repaint();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyReleased(KeyEvent e) {
/* 365 */     if (this.myButton.isEnabled()) {
/* 366 */       int key = e.getKeyCode();
/* 367 */       if ((this.myButton.hasFocus()) && (key == 10)) {
/* 368 */         this.pressed = false;
/* 369 */         this.myButton.repaint();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void keyTyped(KeyEvent e) {
/*     */   }
/*     */ 
/*     */   public void focusGained(FocusEvent e) {
/* 378 */     if (this.myButton.isEnabled())
/* 379 */       this.myButton.repaint();
/*     */   }
/*     */ 
/*     */   public void focusLost(FocusEvent e)
/*     */   {
/* 385 */     if (this.myButton.isEnabled())
/* 386 */       this.myButton.repaint();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.JRoundedButtonUI
 * JD-Core Version:    0.6.0
 */