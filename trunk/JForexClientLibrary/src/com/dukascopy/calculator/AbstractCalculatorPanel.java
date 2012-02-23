/*     */ package com.dukascopy.calculator;
/*     */ 
/*     */ import com.dukascopy.calculator.button.AnsButton;
/*     */ import com.dukascopy.calculator.button.BinButton;
/*     */ import com.dukascopy.calculator.button.CalculatorButton;
/*     */ import com.dukascopy.calculator.button.CopyButton;
/*     */ import com.dukascopy.calculator.button.CplxButton;
/*     */ import com.dukascopy.calculator.button.DecButton;
/*     */ import com.dukascopy.calculator.button.DelButton;
/*     */ import com.dukascopy.calculator.button.DownButton;
/*     */ import com.dukascopy.calculator.button.EqualsButton;
/*     */ import com.dukascopy.calculator.button.GraphButton;
/*     */ import com.dukascopy.calculator.button.HexButton;
/*     */ import com.dukascopy.calculator.button.InfoButton;
/*     */ import com.dukascopy.calculator.button.LeftButton;
/*     */ import com.dukascopy.calculator.button.MclButton;
/*     */ import com.dukascopy.calculator.button.MminusButton;
/*     */ import com.dukascopy.calculator.button.ModeButton;
/*     */ import com.dukascopy.calculator.button.MplusButton;
/*     */ import com.dukascopy.calculator.button.OctButton;
/*     */ import com.dukascopy.calculator.button.OffButton;
/*     */ import com.dukascopy.calculator.button.OnButton;
/*     */ import com.dukascopy.calculator.button.OrigButton;
/*     */ import com.dukascopy.calculator.button.PolButton;
/*     */ import com.dukascopy.calculator.button.RCLButton;
/*     */ import com.dukascopy.calculator.button.RightButton;
/*     */ import com.dukascopy.calculator.button.STOButton;
/*     */ import com.dukascopy.calculator.button.SciButton;
/*     */ import com.dukascopy.calculator.button.SclButton;
/*     */ import com.dukascopy.calculator.button.ShiftButton;
/*     */ import com.dukascopy.calculator.button.SminusButton;
/*     */ import com.dukascopy.calculator.button.SplusButton;
/*     */ import com.dukascopy.calculator.button.UpButton;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.util.HashMap;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.Spring;
/*     */ import javax.swing.SpringLayout;
/*     */ import javax.swing.SpringLayout.Constraints;
/*     */ 
/*     */ public abstract class AbstractCalculatorPanel extends JPanel
/*     */   implements Runnable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private Base base;
/*     */   protected Color colour;
/*     */   private Spring xBorderSpring;
/*     */   private Spring yBorderSpring;
/*     */   private Spring strutX;
/*     */   private Spring strutY;
/*     */   private Spring minX;
/*     */   private Spring minY;
/*     */   private Spring buttonWidthSpring;
/*     */   private Spring narrowButtonWidthSpring;
/*     */   private Spring thinButtonWidthSpring;
/*     */   private Spring buttonHeightSpring;
/*     */   private Spring shortButtonHeightSpring;
/*     */   private Spring shortButtonWidthSpring;
/*     */   private Spring displayWidthSpring;
/*     */   private Spring displayHeightSpring;
/*     */   protected final MainCalculatorPanel applet;
/*     */   protected final SpecialButtonType sbt;
/*     */   protected SpringLayout layout;
/*     */   protected Vector<CalculatorButton> buttons;
/*     */   protected HashMap<Character, CalculatorButton> keyMap;
/*     */ 
/*     */   public static AbstractCalculatorPanel createPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*     */   {
/* 105 */     AbstractCalculatorPanel p = null;
/* 106 */     switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[sbt.ordinal()]) {
/*     */     case 1:
/* 108 */       p = new PlainPanel(applet, sbt, colour);
/* 109 */       p.run();
/* 110 */       return p;
/*     */     case 2:
/* 112 */       p = new ShiftPanel(applet, sbt, colour);
/* 113 */       break;
/*     */     case 3:
/* 115 */       p = new StatPanel(applet, sbt, colour);
/* 116 */       break;
/*     */     case 4:
/* 118 */       p = new ShiftStatPanel(applet, sbt, colour);
/* 119 */       break;
/*     */     case 5:
/* 121 */       p = new HexPanel(applet, sbt, colour);
/* 122 */       break;
/*     */     case 6:
/* 124 */       p = new ShiftHexPanel(applet, sbt, colour);
/* 125 */       break;
/*     */     default:
/* 127 */       p = null;
/*     */     }
/* 129 */     Thread t = new Thread(p);
/* 130 */     t.setPriority(1);
/* 131 */     t.start();
/* 132 */     return p;
/*     */   }
/*     */ 
/*     */   public synchronized void run()
/*     */   {
/* 139 */     setUp();
/*     */   }
/*     */ 
/*     */   protected AbstractCalculatorPanel(MainCalculatorPanel applet, SpecialButtonType sbt, Color colour)
/*     */   {
/* 149 */     this.applet = applet;
/* 150 */     this.sbt = sbt;
/* 151 */     this.colour = colour;
/*     */   }
/*     */ 
/*     */   public synchronized void paintComponent(Graphics graphics)
/*     */   {
/* 159 */     super.paintComponent(graphics);
/*     */   }
/*     */ 
/*     */   private void setUp()
/*     */   {
/* 170 */     this.keyMap = new HashMap();
/* 171 */     this.layout = new SpringLayout();
/* 172 */     setLayout(this.layout);
/*     */ 
/* 174 */     this.buttons = new Vector();
/* 175 */     for (int i = 0; i < 45; i++) {
/* 176 */       CalculatorButton button = null;
/* 177 */       if (i == 0) {
/* 178 */         button = new OffButton(this.applet);
/* 179 */       } else if (i == 5) {
/* 180 */         button = new ModeButton(this.applet);
/* 181 */       } else if (i == 10) {
/* 182 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 4:
/*     */         case 6:
/* 186 */           button = new OrigButton(this.applet);
/* 187 */           break;
/*     */         case 3:
/*     */         case 5:
/*     */         default:
/* 189 */           button = new ShiftButton(this.applet);
/* 190 */           break;
/*     */         }
/* 192 */       } else if (i == 14) {
/* 193 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 4:
/*     */         case 6:
/* 197 */           button = new GraphButton(this.applet);
/* 198 */           break;
/*     */         case 3:
/*     */         case 5:
/*     */         default:
/* 200 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 202 */       } else if (i == 15) {
/* 203 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 4:
/*     */         case 6:
/* 207 */           button = new CopyButton(this.applet);
/* 208 */           break;
/*     */         case 1:
/*     */         case 3:
/*     */         case 5:
/* 212 */           button = new STOButton(this.applet);
/* 213 */           break;
/*     */         default:
/* 215 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 217 */       } else if (i == 16) {
/* 218 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 4:
/*     */         case 6:
/* 222 */           button = new SciButton(this.applet);
/* 223 */           break;
/*     */         case 3:
/*     */         case 5:
/*     */         default:
/* 225 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 227 */       } else if (i == 20) {
/* 228 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 1:
/*     */         case 3:
/*     */         case 5:
/* 232 */           button = new RCLButton(this.applet);
/* 233 */           break;
/*     */         case 2:
/*     */         case 4:
/*     */         default:
/* 235 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 237 */       } else if (i == 21) {
/* 238 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 4:
/*     */         case 6:
/* 242 */           button = new PolButton(this.applet);
/* 243 */           break;
/*     */         case 3:
/*     */         case 5:
/*     */         default:
/* 245 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 247 */       } else if (i == 25) {
/* 248 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 6:
/* 251 */           button = new MminusButton(this.applet);
/* 252 */           break;
/*     */         case 3:
/* 254 */           button = new SplusButton(this.applet);
/* 255 */           break;
/*     */         case 4:
/* 257 */           button = new SminusButton(this.applet);
/* 258 */           break;
/*     */         case 5:
/*     */         default:
/* 260 */           button = new MplusButton(this.applet); break;
/*     */         }
/* 262 */       } else if (i == 26) {
/* 263 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 4:
/*     */         case 6:
/* 267 */           button = new CplxButton(this.applet);
/* 268 */           break;
/*     */         case 3:
/*     */         case 5:
/*     */         default:
/* 270 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 272 */       } else if (i == 30) {
/* 273 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 1:
/*     */         case 3:
/*     */         case 5:
/* 277 */           button = new DelButton(this.applet);
/* 278 */           break;
/*     */         case 2:
/*     */         case 4:
/*     */         default:
/* 280 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 282 */       } else if (i == 32) {
/* 283 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 6:
/* 286 */           button = new HexButton(this.applet);
/* 287 */           break;
/*     */         case 3:
/*     */         case 4:
/*     */         case 5:
/*     */         default:
/* 290 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 292 */       } else if (i == 33) {
/* 293 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 6:
/* 296 */           button = new DecButton(this.applet);
/* 297 */           break;
/*     */         case 3:
/*     */         case 4:
/*     */         case 5:
/*     */         default:
/* 300 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 302 */       } else if (i == 34) {
/* 303 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 1:
/*     */         case 3:
/*     */         case 5:
/* 307 */           button = new AnsButton(this.applet);
/* 308 */           break;
/*     */         case 2:
/*     */         case 4:
/*     */         default:
/* 310 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 312 */       } else if (i == 35) {
/* 313 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 6:
/* 316 */           button = new MclButton(this.applet);
/* 317 */           break;
/*     */         case 4:
/* 319 */           button = new SclButton(this.applet);
/* 320 */           break;
/*     */         case 3:
/*     */         case 5:
/*     */         default:
/* 322 */           button = new OnButton(this.applet); break;
/*     */         }
/* 324 */       } else if (i == 37) {
/* 325 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 6:
/* 328 */           button = new BinButton(this.applet);
/* 329 */           break;
/*     */         case 3:
/*     */         case 4:
/*     */         case 5:
/*     */         default:
/* 332 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 334 */       } else if (i == 38) {
/* 335 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 2:
/*     */         case 6:
/* 338 */           button = new OctButton(this.applet);
/* 339 */           break;
/*     */         case 3:
/*     */         case 4:
/*     */         case 5:
/*     */         default:
/* 342 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 344 */       } else if (i == 39) {
/* 345 */         switch (1.$SwitchMap$com$dukascopy$calculator$SpecialButtonType[this.sbt.ordinal()]) {
/*     */         case 1:
/*     */         case 3:
/*     */         case 5:
/* 349 */           button = new EqualsButton(this.applet);
/* 350 */           break;
/*     */         case 2:
/*     */         case 4:
/*     */         default:
/* 352 */           button = new CalculatorButton(this.applet); break;
/*     */         }
/* 354 */       } else if (i == 40) {
/* 355 */         button = new LeftButton(this.applet);
/* 356 */       } else if (i == 41) {
/* 357 */         button = new RightButton(this.applet);
/* 358 */       } else if (i == 42) {
/* 359 */         button = new InfoButton(this.applet);
/* 360 */         button.setVisible(false);
/*     */       }
/* 362 */       else if (i == 43) {
/* 363 */         button = new UpButton(this.applet);
/* 364 */       } else if (i == 44) {
/* 365 */         button = new DownButton(this.applet);
/*     */       } else {
/* 367 */         button = new CalculatorButton(this.applet);
/* 368 */       }this.buttons.add(button);
/* 369 */       add(button);
/*     */     }
/* 371 */     setButtons();
/* 372 */     layoutSprings();
/*     */   }
/*     */ 
/*     */   protected abstract void setButtons();
/*     */ 
/*     */   public synchronized void layoutSprings()
/*     */   {
/* 390 */     this.xBorderSpring = Spring.constant(0, this.applet.strutSize(), 2147483647);
/*     */ 
/* 393 */     this.yBorderSpring = Spring.constant(0, this.applet.strutSize(), 2147483647);
/*     */ 
/* 396 */     this.strutX = Spring.constant(this.applet.strutSize());
/* 397 */     this.strutY = Spring.constant(this.applet.strutSize());
/* 398 */     this.minX = Spring.constant(this.applet.minSize());
/* 399 */     this.minY = Spring.constant(this.applet.minSize());
/* 400 */     this.buttonWidthSpring = Spring.constant(this.applet.buttonWidth());
/* 401 */     this.narrowButtonWidthSpring = Spring.constant(this.applet.buttonWidth() - this.applet.strutSize() * 2 - (this.applet.minSize() + this.applet.buttonHeight()) / 2);
/*     */ 
/* 404 */     this.thinButtonWidthSpring = Spring.constant(this.applet.buttonHeight());
/* 405 */     this.buttonHeightSpring = Spring.constant(this.applet.buttonHeight());
/* 406 */     int width3 = this.applet.buttonHeight();
/* 407 */     if ((this.applet.minSize() - width3) % 2 != 0)
/* 408 */       width3++;
/* 409 */     int width23 = this.applet.buttonWidth() + (this.applet.minSize() - width3) / 2;
/* 410 */     this.shortButtonHeightSpring = Spring.constant(this.applet.buttonHeight() - this.applet.minSize());
/* 411 */     this.shortButtonWidthSpring = Spring.constant(this.applet.buttonHeight() - this.applet.minSize());
/* 412 */     this.displayWidthSpring = Spring.scale(this.buttonWidthSpring, 6.0F);
/* 413 */     this.displayWidthSpring = Spring.sum(this.displayWidthSpring, this.strutX);
/* 414 */     this.displayWidthSpring = Spring.sum(this.displayWidthSpring, Spring.scale(this.minX, 4.0F));
/*     */ 
/* 417 */     this.displayHeightSpring = Spring.constant(this.applet.displayHeight());
/* 418 */     Spring panelWidthSpring = Spring.scale(this.xBorderSpring, 2.0F);
/*     */ 
/* 420 */     panelWidthSpring = Spring.sum(panelWidthSpring, Spring.scale(this.buttonWidthSpring, 8.0F));
/*     */ 
/* 424 */     panelWidthSpring = Spring.sum(panelWidthSpring, Spring.scale(this.strutX, 2.0F));
/*     */ 
/* 427 */     panelWidthSpring = Spring.sum(panelWidthSpring, Spring.scale(this.minX, 5.0F));
/*     */ 
/* 430 */     Spring panelHeightSpring = Spring.scale(this.yBorderSpring, 2.0F);
/*     */ 
/* 432 */     panelHeightSpring = Spring.sum(panelHeightSpring, this.displayHeightSpring);
/*     */ 
/* 434 */     panelHeightSpring = Spring.sum(panelHeightSpring, this.strutY);
/*     */ 
/* 436 */     panelHeightSpring = Spring.sum(panelHeightSpring, Spring.scale(this.buttonHeightSpring, 5.0F));
/*     */ 
/* 440 */     panelHeightSpring = Spring.sum(panelHeightSpring, Spring.scale(this.minY, 4.0F));
/*     */ 
/* 445 */     SpringLayout.Constraints constraints = this.layout.getConstraints(this);
/*     */ 
/* 448 */     constraints.setWidth(panelWidthSpring);
/* 449 */     constraints.setHeight(panelHeightSpring);
/*     */ 
/* 451 */     Spring x2 = Spring.sum(this.xBorderSpring, Spring.sum(this.buttonWidthSpring, this.minX));
/*     */ 
/* 454 */     Spring x3 = Spring.sum(x2, Spring.sum(this.buttonWidthSpring, this.minX));
/*     */ 
/* 456 */     Spring x4 = Spring.sum(x3, Spring.sum(this.buttonWidthSpring, this.strutX));
/*     */ 
/* 458 */     Spring x5 = Spring.sum(x4, Spring.sum(this.buttonWidthSpring, this.minX));
/*     */ 
/* 460 */     Spring x6 = Spring.sum(x5, Spring.sum(this.buttonWidthSpring, this.minX));
/*     */ 
/* 462 */     Spring x7 = Spring.sum(x6, Spring.sum(this.buttonWidthSpring, this.strutX));
/*     */ 
/* 464 */     Spring x8 = Spring.sum(x7, Spring.sum(this.buttonWidthSpring, this.minX));
/*     */ 
/* 466 */     Spring y1 = Spring.sum(this.yBorderSpring, Spring.sum(this.displayHeightSpring, this.strutY));
/*     */ 
/* 469 */     Spring y2 = Spring.sum(y1, Spring.sum(this.buttonHeightSpring, this.minY));
/*     */ 
/* 471 */     Spring y3 = Spring.sum(y2, Spring.sum(this.buttonHeightSpring, this.minY));
/*     */ 
/* 473 */     Spring y4 = Spring.sum(y3, Spring.sum(this.buttonHeightSpring, this.minY));
/*     */ 
/* 475 */     Spring y5 = Spring.sum(y4, Spring.sum(this.buttonHeightSpring, this.minY));
/*     */ 
/* 477 */     for (int i = 0; i < 40; i++) {
/* 478 */       constraints = this.layout.getConstraints((Component)buttons().elementAt(i));
/* 479 */       constraints.setWidth(this.buttonWidthSpring);
/* 480 */       constraints.setHeight(this.buttonHeightSpring);
/* 481 */       if (i < 5)
/* 482 */         constraints.setX(this.xBorderSpring);
/* 483 */       else if (i < 10)
/* 484 */         constraints.setX(x2);
/* 485 */       else if (i < 15)
/* 486 */         constraints.setX(x3);
/* 487 */       else if (i < 20)
/* 488 */         constraints.setX(x4);
/* 489 */       else if (i < 25)
/* 490 */         constraints.setX(x5);
/* 491 */       else if (i < 30)
/* 492 */         constraints.setX(x6);
/* 493 */       else if (i < 35)
/* 494 */         constraints.setX(x7);
/*     */       else
/* 496 */         constraints.setX(x8);
/* 497 */       int r = i % 5;
/* 498 */       if (r == 0)
/* 499 */         constraints.setY(y1);
/* 500 */       else if (r == 1)
/* 501 */         constraints.setY(y2);
/* 502 */       else if (r == 2)
/* 503 */         constraints.setY(y3);
/* 504 */       else if (r == 3)
/* 505 */         constraints.setY(y4);
/*     */       else
/* 507 */         constraints.setY(y5);
/*     */     }
/* 509 */     Spring s4ym = Spring.constant(this.applet.strutSize() + (this.applet.displayHeight() - this.applet.buttonHeight()) / 2);
/*     */ 
/* 511 */     constraints = this.layout.getConstraints((Component)buttons().elementAt(40));
/* 512 */     constraints.setWidth(this.shortButtonWidthSpring);
/* 513 */     constraints.setHeight(this.shortButtonHeightSpring);
/* 514 */     Spring s40x = Spring.sum(Spring.sum(Spring.constant(7), this.displayWidthSpring), Spring.scale(this.strutX, 4.0F));
/* 515 */     constraints.setX(s40x);
/* 516 */     constraints.setY(s4ym);
/*     */ 
/* 525 */     constraints = this.layout.getConstraints((Component)buttons().elementAt(43));
/* 526 */     constraints.setWidth(this.shortButtonWidthSpring);
/* 527 */     constraints.setHeight(this.shortButtonHeightSpring);
/* 528 */     Spring s434x = Spring.sum(Spring.constant(width23), Spring.sum(this.displayWidthSpring, Spring.scale(this.strutX, 2.0F)));
/* 529 */     constraints.setX(s434x);
/* 530 */     constraints.setY(this.strutY);
/*     */ 
/* 532 */     constraints = this.layout.getConstraints((Component)buttons().elementAt(44));
/* 533 */     constraints.setWidth(this.shortButtonWidthSpring);
/* 534 */     constraints.setHeight(this.shortButtonHeightSpring);
/* 535 */     constraints.setX(s434x);
/* 536 */     Spring s44y = Spring.sum(Spring.scale(this.minY, 1.0F), Spring.sum(this.shortButtonHeightSpring, Spring.sum(this.buttonHeightSpring, this.strutY)));
/* 537 */     constraints.setY(s44y);
/*     */ 
/* 539 */     constraints = this.layout.getConstraints((Component)buttons().elementAt(41));
/* 540 */     constraints.setWidth(this.shortButtonWidthSpring);
/* 541 */     constraints.setHeight(this.shortButtonHeightSpring);
/* 542 */     Spring s41x = Spring.sum(Spring.sum(Spring.constant(5), s434x), this.shortButtonWidthSpring);
/* 543 */     constraints.setX(s41x);
/* 544 */     constraints.setY(s4ym);
/*     */   }
/*     */ 
/*     */   public synchronized void setDisplayPanel()
/*     */   {
/* 550 */     remove(this.applet.displayPanel());
/* 551 */     add(this.applet.displayPanel());
/*     */ 
/* 553 */     SpringLayout.Constraints constraints = ((SpringLayout)getLayout()).getConstraints(this.applet.displayPanel());
/*     */ 
/* 556 */     constraints = this.layout.getConstraints(this.applet.displayPanel());
/* 557 */     constraints.setX(this.xBorderSpring);
/* 558 */     constraints.setY(this.yBorderSpring);
/* 559 */     constraints.setWidth(this.displayWidthSpring);
/* 560 */     constraints.setHeight(this.displayHeightSpring);
/*     */   }
/*     */ 
/*     */   public synchronized Vector<CalculatorButton> buttons()
/*     */   {
/* 568 */     return this.buttons;
/*     */   }
/*     */ 
/*     */   public synchronized HashMap<Character, CalculatorButton> keyMap()
/*     */   {
/* 579 */     return this.keyMap;
/*     */   }
/*     */ 
/*     */   protected void createKeyMap()
/*     */   {
/* 588 */     for (CalculatorButton o : this.buttons)
/* 589 */       if (o.shortcut() != 0) {
/* 590 */         if (this.keyMap.containsKey(Character.valueOf(o.shortcut())));
/* 592 */         this.keyMap.put(Character.valueOf(o.shortcut()), o);
/*     */       }
/*     */   }
/*     */ 
/*     */   public Base base()
/*     */   {
/* 602 */     return this.base;
/*     */   }
/*     */ 
/*     */   public void base(Base base)
/*     */   {
/* 610 */     this.base = base;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.AbstractCalculatorPanel
 * JD-Core Version:    0.6.0
 */