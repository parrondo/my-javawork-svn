/*     */ package com.dukascopy.calculator.button;
/*     */ 
/*     */ import com.dukascopy.calculator.AngleType;
/*     */ import com.dukascopy.calculator.MainCalculatorPanel;
/*     */ import com.dukascopy.calculator.OObject;
/*     */ import com.dukascopy.calculator.Parser;
/*     */ import com.dukascopy.calculator.function.AFunction;
/*     */ import com.dukascopy.calculator.function.Ans;
/*     */ import com.dukascopy.calculator.function.Container;
/*     */ import com.dukascopy.calculator.function.I;
/*     */ import com.dukascopy.calculator.function.LParen;
/*     */ import com.dukascopy.calculator.function.Mean;
/*     */ import com.dukascopy.calculator.function.NullPObject;
/*     */ import com.dukascopy.calculator.function.Numeral;
/*     */ import com.dukascopy.calculator.function.PObject;
/*     */ import com.dukascopy.calculator.function.PopStDev;
/*     */ import com.dukascopy.calculator.function.RFunction;
/*     */ import com.dukascopy.calculator.function.RParen;
/*     */ import com.dukascopy.calculator.function.StDev;
/*     */ import com.dukascopy.calculator.function.Trig;
/*     */ import com.dukascopy.calculator.function.Variable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JButton;
/*     */ 
/*     */ public class CalculatorButton extends JButton
/*     */   implements ActionListener, Localizable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected MainCalculatorPanel mainCalculatorPanel;
/*     */   private PObject pobject;
/*     */   private char shortcut;
/*     */   private String toolTipKey;
/*     */ 
/*     */   protected CalculatorButton()
/*     */   {
/*     */   }
/*     */ 
/*     */   private void setup(MainCalculatorPanel mainCalculatorPanel, PObject pobject)
/*     */   {
/*  66 */     this.mainCalculatorPanel = mainCalculatorPanel;
/*  67 */     setPObject(pobject);
/*  68 */     setText();
/*  69 */     setTextSize();
/*  70 */     addActionListener(this);
/*     */ 
/*  72 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   public void setPObject(PObject p)
/*     */   {
/*  81 */     if (p == null) {
/*  82 */       p = NullPObject.instance();
/*     */     }
/*     */ 
/*  85 */     setPobject(p);
/*  86 */     setText(p);
/*     */ 
/*  88 */     setShortcut(p.shortcut());
/*  89 */     setToolTipKey(p.tooltip());
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/*  94 */     if (getToolTipKey() != null)
/*  95 */       if ("<i>&#960;</i>".equals(getToolTipKey())) {
/*  96 */         setToolTipText(getToolTipKey());
/*     */       }
/*  98 */       else if (getToolTipKey().isEmpty()) {
/*  99 */         setToolTipText("");
/*     */       }
/* 101 */       else if ("i".equals(getToolTipKey())) {
/* 102 */         setToolTipText(getToolTipKey());
/*     */       }
/*     */       else
/* 105 */         setToolTipText(LocalizationManager.getText(getToolTipKey()));
/*     */   }
/*     */ 
/*     */   public void setToolTipText(String text)
/*     */   {
/* 111 */     String strSpace = "space";
/*     */ 
/* 113 */     if (text.length() > 0) {
/* 114 */       if (getShortcut() == ' ') {
/* 115 */         super.setToolTipText(new StringBuilder().append("<html>").append(text).append(" [").append(strSpace).append("]</html>").toString());
/*     */       }
/* 117 */       else if (getShortcut() != 0) {
/* 118 */         super.setToolTipText(new StringBuilder().append("<html>").append(text).append(" [").append(getShortcut()).append("]</html>").toString());
/*     */       }
/*     */       else {
/* 121 */         super.setToolTipText(new StringBuilder().append("<html>").append(text).append("</html>").toString());
/*     */       }
/*     */ 
/*     */     }
/* 125 */     else if (getShortcut() == ' ') {
/* 126 */       super.setToolTipText(new StringBuilder().append("<html>[").append(strSpace).append("]</html>").toString());
/*     */     }
/* 128 */     else if (getShortcut() != 0) {
/* 129 */       super.setToolTipText(new StringBuilder().append("<html>[").append(getShortcut()).append("]</html>").toString());
/*     */     }
/*     */     else
/* 132 */       return;
/*     */   }
/*     */ 
/*     */   public CalculatorButton(MainCalculatorPanel applet)
/*     */   {
/* 145 */     setup(applet, NullPObject.instance());
/*     */   }
/*     */ 
/*     */   public CalculatorButton(MainCalculatorPanel applet, PObject pobject)
/*     */   {
/* 157 */     setup(applet, pobject);
/*     */   }
/*     */ 
/*     */   public void setText()
/*     */   {
/* 164 */     setText(this.pobject);
/*     */   }
/*     */ 
/*     */   protected void setText(PObject p)
/*     */   {
/* 176 */     StringBuilder s = new StringBuilder();
/* 177 */     s.append("<html><strong>");
/* 178 */     if (p != null) {
/* 179 */       s.append(p.shortName());
/*     */     }
/* 181 */     s.append("</strong></html>");
/* 182 */     setText(s.toString());
/*     */   }
/*     */ 
/*     */   protected MainCalculatorPanel getMainCalculatorPanel()
/*     */   {
/* 189 */     return this.mainCalculatorPanel;
/*     */   }
/*     */ 
/*     */   public void setTextSize()
/*     */   {
/* 198 */     setFont(getFont().deriveFont(getMainCalculatorPanel().buttonTextSize()));
/*     */   }
/*     */ 
/*     */   public void setAngleType(AngleType angleType)
/*     */   {
/* 208 */     if ((this.pobject instanceof Trig))
/* 209 */       ((Trig)this.pobject).setScale(angleType);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent actionEvent)
/*     */   {
/* 223 */     synchronized (this.mainCalculatorPanel) {
/* 224 */       if (getMainCalculatorPanel().getMode() != 0) {
/* 225 */         getMainCalculatorPanel().setMode(this.pobject);
/* 226 */         getMainCalculatorPanel().requestFocus();
/* 227 */         return;
/*     */       }
/* 229 */       if ((this.pobject instanceof Numeral)) {
/* 230 */         char c = this.pobject.shortcut();
/* 231 */         switch (1.$SwitchMap$com$dukascopy$calculator$Base[getMainCalculatorPanel().getBase().ordinal()]) {
/*     */         case 1:
/* 233 */           if ((c != '2') && (c != '3') && (c != '4') && (c != '5') && (c != '6') && (c != '7')) break;
/* 234 */           return;
/*     */         case 2:
/* 237 */           if ((c != '8') && (c != '9')) break; return;
/*     */         case 3:
/* 240 */           if ((c != 'A') && (c != 'B') && (c != 'C') && (c != 'D') && (c != 'E') && (c != 'F'))
/*     */             break;
/* 242 */           return;
/*     */         case 4:
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 249 */       if (((this.pobject instanceof NullPObject)) || (this.pobject == null))
/* 250 */         return;
/* 251 */       if ((this.pobject instanceof Mean)) {
/* 252 */         add(getMainCalculatorPanel().statMean());
/* 253 */       } else if ((this.pobject instanceof StDev)) {
/* 254 */         add(getMainCalculatorPanel().statSampleStDev());
/* 255 */       } else if ((this.pobject instanceof PopStDev)) {
/* 256 */         add(getMainCalculatorPanel().statPopulationStDev());
/*     */       } else {
/* 258 */         setAngleType(this.mainCalculatorPanel.getAngleType());
/* 259 */         add(this.pobject);
/*     */       }
/* 261 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 262 */       if (getMainCalculatorPanel().getShift())
/* 263 */         getMainCalculatorPanel().setShift(false);
/* 264 */       getMainCalculatorPanel().requestFocus();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void add(PObject p)
/*     */   {
/* 276 */     if (!(getMainCalculatorPanel().getValue() instanceof OObject))
/* 277 */       return;
/* 278 */     OObject value = getMainCalculatorPanel().getValue();
/* 279 */     if ((getMainCalculatorPanel().getParser().isEmpty()) && (!(p instanceof Numeral)) && (!(p instanceof Variable)) && (!(p instanceof AFunction)) && (!(p instanceof RFunction)) && (!(p instanceof LParen)) && (!(p instanceof RParen)) && (!(p instanceof I)) && (!(p instanceof Container)))
/*     */     {
/* 288 */       Ans ans = new Ans();
/* 289 */       ans.setValue(value);
/* 290 */       getMainCalculatorPanel().insert(ans);
/* 291 */       getMainCalculatorPanel().updateDisplay(true, true);
/* 292 */       getMainCalculatorPanel().insert(p);
/*     */     } else {
/* 294 */       getMainCalculatorPanel().insert(p);
/*     */     }
/*     */   }
/*     */ 
/*     */   public char shortcut()
/*     */   {
/* 302 */     return this.shortcut;
/*     */   }
/*     */ 
/*     */   public String getToolTipKey() {
/* 306 */     return this.toolTipKey;
/*     */   }
/*     */ 
/*     */   public void setToolTipKey(String toolTipKey) {
/* 310 */     this.toolTipKey = toolTipKey;
/* 311 */     localize();
/*     */   }
/*     */ 
/*     */   public char getShortcut() {
/* 315 */     return this.shortcut;
/*     */   }
/*     */ 
/*     */   public void setShortcut(char shortcut) {
/* 319 */     this.shortcut = shortcut;
/*     */   }
/*     */ 
/*     */   public PObject getPobject() {
/* 323 */     return this.pobject;
/*     */   }
/*     */ 
/*     */   public void setPobject(PObject pobject) {
/* 327 */     this.pobject = pobject;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.button.CalculatorButton
 * JD-Core Version:    0.6.0
 */