/*     */ package com.dukascopy.calculator.graph;
/*     */ 
/*     */ import com.dukascopy.calculator.ReadOnlyCalculatorApplet;
/*     */ import java.awt.Container;
/*     */ import java.awt.Font;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Iterator;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.Spring;
/*     */ import javax.swing.SpringLayout;
/*     */ import javax.swing.SpringLayout.Constraints;
/*     */ 
/*     */ public class AxisDialog extends JFrame
/*     */   implements ActionListener
/*     */ {
/*     */   private JLabel minimumLabel;
/*     */   private JLabel maximumLabel;
/*     */   private JLabel majorUnitLabel;
/*     */   private JLabel minorUnitLabel;
/*     */   private JTextField minimumTextBox;
/*     */   private JTextField maximumTextBox;
/*     */   private JTextField majorUnitTextBox;
/*     */   private JTextField minorUnitTextBox;
/*     */   private JLabel majorVisible;
/*     */   private JLabel minorVisible;
/*     */   private JCheckBox majorCheckBox;
/*     */   private JCheckBox minorCheckBox;
/*     */   private JButton applyButton;
/*     */   private JButton undoButton;
/*     */   private JButton okButton;
/*     */   private JButton cancelButton;
/*     */   private Spring smallGap;
/*     */   private Spring gap;
/*     */   private Spring buttonWidth;
/*     */   private Spring buttonHeight;
/*     */   private Spring textBoxWidth;
/*     */   private Spring labelWidth;
/*     */   private Spring visibleWidth;
/*     */   private ReadOnlyCalculatorApplet applet;
/*     */   private View view;
/*     */   private Axis axis;
/*     */   private ArrayDeque<AxisData> stack;
/*     */   private final boolean x;
/*     */   SpringLayout springLayout;
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public AxisDialog(ReadOnlyCalculatorApplet applet, View view, Axis axis, boolean x, String title)
/*     */   {
/*  20 */     super(title);
/*  21 */     this.applet = applet;
/*  22 */     this.view = view;
/*  23 */     this.axis = axis;
/*  24 */     this.x = x;
/*  25 */     this.stack = new ArrayDeque();
/*  26 */     this.minimumLabel = new JLabel("Minimum:");
/*  27 */     this.minimumLabel.setHorizontalAlignment(4);
/*  28 */     this.maximumLabel = new JLabel("Maximum:");
/*  29 */     this.maximumLabel.setHorizontalAlignment(4);
/*  30 */     this.majorUnitLabel = new JLabel("Major unit:");
/*  31 */     this.majorUnitLabel.setHorizontalAlignment(4);
/*  32 */     this.minorUnitLabel = new JLabel("Minor unit:");
/*  33 */     this.minorUnitLabel.setHorizontalAlignment(4);
/*     */ 
/*  35 */     this.minimumTextBox = new JTextField();
/*  36 */     this.maximumTextBox = new JTextField();
/*  37 */     this.majorUnitTextBox = new JTextField();
/*  38 */     this.minorUnitTextBox = new JTextField();
/*     */ 
/*  40 */     this.minimumLabel.setLabelFor(this.minimumTextBox);
/*  41 */     this.minimumLabel.setDisplayedMnemonic('n');
/*  42 */     this.minimumLabel.setToolTipText("Minimum value shown on chart");
/*  43 */     this.maximumLabel.setLabelFor(this.maximumTextBox);
/*  44 */     this.maximumLabel.setDisplayedMnemonic('x');
/*  45 */     this.maximumLabel.setToolTipText("Maximum value shown on chart");
/*  46 */     this.majorUnitLabel.setLabelFor(this.majorUnitTextBox);
/*  47 */     this.majorUnitLabel.setDisplayedMnemonic('j');
/*  48 */     this.majorUnitLabel.setToolTipText("Spacing between larger ticks with numerical scale");
/*     */ 
/*  50 */     this.minorUnitLabel.setLabelFor(this.minorUnitTextBox);
/*  51 */     this.minorUnitLabel.setDisplayedMnemonic('i');
/*  52 */     this.minorUnitLabel.setToolTipText("Spacing between smalle ticks");
/*     */ 
/*  54 */     this.majorCheckBox = new JCheckBox("visible", true);
/*  55 */     this.minorCheckBox = new JCheckBox("visible", true);
/*     */ 
/*  57 */     this.applyButton = new JButton("Apply");
/*  58 */     this.applyButton.setMnemonic('A');
/*  59 */     this.applyButton.setToolTipText("Apply changes to graph");
/*  60 */     this.undoButton = new JButton("Undo");
/*  61 */     this.undoButton.setMnemonic('U');
/*  62 */     this.undoButton.setToolTipText("Undo most recently applied change");
/*  63 */     this.okButton = new JButton("OK");
/*  64 */     this.okButton.setMnemonic('O');
/*  65 */     this.okButton.setToolTipText("Apply changes to graph and quit");
/*  66 */     this.cancelButton = new JButton("Cancel");
/*  67 */     this.cancelButton.setMnemonic('C');
/*  68 */     this.cancelButton.setToolTipText("Undo all changes and quit");
/*     */ 
/*  70 */     this.undoButton.setEnabled(false);
/*     */ 
/*  72 */     this.majorCheckBox.setMnemonic('s');
/*  73 */     this.minorCheckBox.setMnemonic('v');
/*     */ 
/*  76 */     this.minimumTextBox.addFocusListener(new FieldFocusListener(this.minimumTextBox, false));
/*     */ 
/*  78 */     this.maximumTextBox.addFocusListener(new FieldFocusListener(this.maximumTextBox, false));
/*     */ 
/*  80 */     this.majorUnitTextBox.addFocusListener(new FieldFocusListener(this.majorUnitTextBox, true));
/*     */ 
/*  82 */     this.minorUnitTextBox.addFocusListener(new FieldFocusListener(this.minorUnitTextBox, true));
/*     */ 
/*  86 */     this.applyButton.addActionListener(new ApplyButtonListener());
/*  87 */     this.okButton.addActionListener(new OKButtonListener());
/*  88 */     this.undoButton.addActionListener(new UndoButtonListener());
/*  89 */     this.cancelButton.addActionListener(new CancelButtonListener());
/*     */ 
/*  91 */     this.springLayout = new SpringLayout();
/*  92 */     setSizes();
/*     */ 
/*  94 */     setDefaultCloseOperation(1);
/*  95 */     setResizable(false);
/*     */ 
/*  97 */     Container contentPane = getContentPane();
/*  98 */     contentPane.setLayout(this.springLayout);
/*     */ 
/* 101 */     contentPane.add(this.minimumLabel);
/* 102 */     contentPane.add(this.maximumLabel);
/* 103 */     contentPane.add(this.majorUnitLabel);
/* 104 */     contentPane.add(this.minorUnitLabel);
/*     */ 
/* 106 */     contentPane.add(this.minimumTextBox);
/* 107 */     contentPane.add(this.maximumTextBox);
/* 108 */     contentPane.add(this.majorUnitTextBox);
/* 109 */     contentPane.add(this.minorUnitTextBox);
/*     */ 
/* 111 */     contentPane.add(this.majorCheckBox);
/* 112 */     contentPane.add(this.minorCheckBox);
/*     */ 
/* 114 */     contentPane.add(this.applyButton);
/* 115 */     contentPane.add(this.undoButton);
/* 116 */     contentPane.add(this.okButton);
/* 117 */     contentPane.add(this.cancelButton);
/*     */ 
/* 119 */     layOut();
/*     */ 
/* 121 */     setVisible(false);
/*     */   }
/*     */ 
/*     */   private void layOut()
/*     */   {
/* 129 */     this.textBoxWidth = Spring.scale(this.buttonWidth, 1.0F);
/* 130 */     this.labelWidth = Spring.scale(this.buttonWidth, 1.0F);
/* 131 */     this.visibleWidth = Spring.scale(this.buttonWidth, 1.0F);
/*     */ 
/* 133 */     SpringLayout.Constraints constraints = this.springLayout.getConstraints(this.minimumLabel);
/*     */ 
/* 135 */     constraints.setWidth(this.labelWidth);
/* 136 */     constraints.setHeight(this.buttonHeight);
/* 137 */     constraints = this.springLayout.getConstraints(this.maximumLabel);
/* 138 */     constraints.setWidth(this.labelWidth);
/* 139 */     constraints.setHeight(this.buttonHeight);
/* 140 */     constraints = this.springLayout.getConstraints(this.majorUnitLabel);
/* 141 */     constraints.setWidth(this.labelWidth);
/* 142 */     constraints.setHeight(this.buttonHeight);
/* 143 */     constraints = this.springLayout.getConstraints(this.minorUnitLabel);
/* 144 */     constraints.setWidth(this.labelWidth);
/* 145 */     constraints.setHeight(this.buttonHeight);
/*     */ 
/* 147 */     constraints = this.springLayout.getConstraints(this.minimumTextBox);
/* 148 */     constraints.setWidth(this.textBoxWidth);
/* 149 */     constraints.setHeight(this.buttonHeight);
/* 150 */     constraints = this.springLayout.getConstraints(this.maximumTextBox);
/* 151 */     constraints.setWidth(this.textBoxWidth);
/* 152 */     constraints.setHeight(this.buttonHeight);
/* 153 */     constraints = this.springLayout.getConstraints(this.majorUnitTextBox);
/* 154 */     constraints.setWidth(this.textBoxWidth);
/* 155 */     constraints.setHeight(this.buttonHeight);
/* 156 */     constraints = this.springLayout.getConstraints(this.minorUnitTextBox);
/* 157 */     constraints.setWidth(this.textBoxWidth);
/* 158 */     constraints.setHeight(this.buttonHeight);
/*     */ 
/* 160 */     constraints = this.springLayout.getConstraints(this.majorCheckBox);
/* 161 */     constraints.setWidth(this.visibleWidth);
/* 162 */     constraints.setHeight(this.buttonHeight);
/* 163 */     constraints = this.springLayout.getConstraints(this.minorCheckBox);
/* 164 */     constraints.setWidth(this.visibleWidth);
/* 165 */     constraints.setHeight(this.buttonHeight);
/*     */ 
/* 167 */     constraints = this.springLayout.getConstraints(this.applyButton);
/* 168 */     constraints.setWidth(this.buttonWidth);
/* 169 */     constraints.setHeight(this.buttonHeight);
/* 170 */     constraints = this.springLayout.getConstraints(this.undoButton);
/* 171 */     constraints.setWidth(this.buttonWidth);
/* 172 */     constraints.setHeight(this.buttonHeight);
/* 173 */     constraints = this.springLayout.getConstraints(this.okButton);
/* 174 */     constraints.setWidth(this.buttonWidth);
/* 175 */     constraints.setHeight(this.buttonHeight);
/* 176 */     constraints = this.springLayout.getConstraints(this.cancelButton);
/* 177 */     constraints.setWidth(this.buttonWidth);
/* 178 */     constraints.setHeight(this.buttonHeight);
/*     */ 
/* 180 */     Container contentPane = getContentPane();
/* 181 */     this.springLayout.putConstraint("North", this.minimumLabel, this.smallGap, "North", contentPane);
/*     */ 
/* 184 */     this.springLayout.putConstraint("West", this.minimumLabel, this.smallGap, "West", contentPane);
/*     */ 
/* 187 */     this.springLayout.putConstraint("North", this.maximumLabel, this.smallGap, "South", this.minimumLabel);
/*     */ 
/* 190 */     this.springLayout.putConstraint("West", this.maximumLabel, this.smallGap, "West", contentPane);
/*     */ 
/* 193 */     this.springLayout.putConstraint("North", this.majorUnitLabel, this.gap, "South", this.maximumLabel);
/*     */ 
/* 196 */     this.springLayout.putConstraint("West", this.majorUnitLabel, this.smallGap, "West", contentPane);
/*     */ 
/* 199 */     this.springLayout.putConstraint("North", this.minorUnitLabel, this.smallGap, "South", this.majorUnitLabel);
/*     */ 
/* 202 */     this.springLayout.putConstraint("West", this.minorUnitLabel, this.smallGap, "West", contentPane);
/*     */ 
/* 206 */     this.springLayout.putConstraint("North", this.minimumTextBox, this.smallGap, "North", contentPane);
/*     */ 
/* 209 */     this.springLayout.putConstraint("West", this.minimumTextBox, this.smallGap, "East", this.minimumLabel);
/*     */ 
/* 212 */     this.springLayout.putConstraint("North", this.maximumTextBox, this.smallGap, "South", this.minimumTextBox);
/*     */ 
/* 215 */     this.springLayout.putConstraint("West", this.maximumTextBox, this.smallGap, "East", this.maximumLabel);
/*     */ 
/* 218 */     this.springLayout.putConstraint("North", this.majorUnitTextBox, this.gap, "South", this.maximumTextBox);
/*     */ 
/* 221 */     this.springLayout.putConstraint("West", this.majorUnitTextBox, this.smallGap, "East", this.majorUnitLabel);
/*     */ 
/* 224 */     this.springLayout.putConstraint("North", this.minorUnitTextBox, this.smallGap, "South", this.majorUnitTextBox);
/*     */ 
/* 227 */     this.springLayout.putConstraint("West", this.minorUnitTextBox, this.smallGap, "East", this.minorUnitLabel);
/*     */ 
/* 231 */     this.springLayout.putConstraint("North", this.majorCheckBox, this.gap, "South", this.maximumTextBox);
/*     */ 
/* 234 */     this.springLayout.putConstraint("West", this.majorCheckBox, this.smallGap, "East", this.majorUnitTextBox);
/*     */ 
/* 237 */     this.springLayout.putConstraint("North", this.minorCheckBox, this.smallGap, "South", this.majorCheckBox);
/*     */ 
/* 240 */     this.springLayout.putConstraint("West", this.minorCheckBox, this.smallGap, "East", this.minorUnitTextBox);
/*     */ 
/* 244 */     this.springLayout.putConstraint("North", this.applyButton, this.gap, "South", this.minorUnitLabel);
/*     */ 
/* 247 */     this.springLayout.putConstraint("West", this.applyButton, this.smallGap, "West", contentPane);
/*     */ 
/* 250 */     this.springLayout.putConstraint("North", this.undoButton, this.gap, "South", this.minorUnitLabel);
/*     */ 
/* 253 */     this.springLayout.putConstraint("West", this.undoButton, this.smallGap, "East", this.applyButton);
/*     */ 
/* 256 */     this.springLayout.putConstraint("North", this.okButton, this.gap, "South", this.minorUnitLabel);
/*     */ 
/* 259 */     this.springLayout.putConstraint("West", this.okButton, this.smallGap, "East", this.undoButton);
/*     */ 
/* 262 */     this.springLayout.putConstraint("North", this.cancelButton, this.gap, "South", this.minorUnitLabel);
/*     */ 
/* 265 */     this.springLayout.putConstraint("West", this.cancelButton, this.smallGap, "East", this.okButton);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent event)
/*     */   {
/* 275 */     setBounds();
/* 276 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private boolean validateFields()
/*     */   {
/* 285 */     return true;
/*     */   }
/*     */ 
/*     */   public void setBounds()
/*     */   {
/* 292 */     if ((this.minimumLabel.getFont().getSize() != this.applet.buttonTextSize()) && 
/* 293 */       (setSizes())) {
/* 294 */       layOut();
/*     */     }
/* 296 */     NumberFormat f = NumberFormat.getNumberInstance();
/* 297 */     if ((f instanceof DecimalFormat)) {
/* 298 */       DecimalFormat d = (DecimalFormat)f;
/* 299 */       d.setNegativePrefix("−");
/*     */     }
/* 301 */     AxisData axisData = (AxisData)this.stack.peekFirst();
/* 302 */     double oldWidth = 0.0D;
/* 303 */     if (axisData == null)
/* 304 */       axisData = new AxisData();
/*     */     else {
/* 306 */       oldWidth = axisData.maximum - axisData.minimum;
/*     */     }
/* 308 */     if (this.x) {
/* 309 */       axisData.minimum = this.view.getTransformation().toModelX(0.0D);
/* 310 */       axisData.maximum = this.view.getTransformation().toModelX(this.view.getWidth());
/*     */     } else {
/* 312 */       axisData.minimum = this.view.getTransformation().toModelY(this.view.getHeight());
/* 313 */       axisData.maximum = this.view.getTransformation().toModelY(0.0D);
/*     */     }
/* 315 */     this.minimumTextBox.setText(f.format(axisData.minimum));
/* 316 */     this.maximumTextBox.setText(f.format(axisData.maximum));
/*     */     double change;
/*     */     Iterator i;
/* 317 */     if (this.stack.size() == 0)
/*     */     {
/* 319 */       if (this.x) {
/* 320 */         axisData.majorUnit = this.view.getTransformation().getXMajorUnit();
/* 321 */         axisData.minorUnit = this.view.getTransformation().getXMinorUnit();
/*     */       } else {
/* 323 */         axisData.minorUnit = this.view.getTransformation().getYMajorUnit();
/* 324 */         axisData.minorUnit = this.view.getTransformation().getYMinorUnit();
/*     */       }
/* 326 */       this.majorUnitTextBox.setText(Double.toString(axisData.majorUnit));
/* 327 */       this.minorUnitTextBox.setText(Double.toString(axisData.minorUnit));
/* 328 */       this.majorCheckBox.setSelected(axisData.majorVisible);
/* 329 */       this.minorCheckBox.setSelected(axisData.minorVisible);
/* 330 */       this.stack.addFirst(axisData);
/*     */     } else {
/* 332 */       double newWidth = axisData.maximum - axisData.minimum;
/* 333 */       change = (newWidth - oldWidth) / 2.0D;
/*     */ 
/* 335 */       i = this.stack.iterator();
/* 336 */       if (i.hasNext())
/* 337 */         for (i.next(); i.hasNext(); ) {
/* 338 */           AxisData a = (AxisData)i.next();
/* 339 */           a.maximum += change;
/* 340 */           a.minimum -= change;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean setSizes()
/*     */   {
/* 356 */     setSize(this.applet.minSize() * 5 + this.applet.buttonWidth() * 4 + this.applet.getFrameInsets().left + this.applet.getFrameInsets().right, this.applet.minSize() * 4 + this.applet.strutSize() * 2 + this.applet.buttonHeight() * 5 + this.applet.getFrameInsets().top + this.applet.getFrameInsets().bottom);
/*     */ 
/* 363 */     boolean result = this.minimumLabel.getFont().getSize() != this.applet.buttonTextSize();
/* 364 */     if (result) {
/* 365 */       this.smallGap = Spring.constant(this.applet.minSize());
/* 366 */       this.gap = Spring.constant(this.applet.strutSize());
/* 367 */       this.buttonWidth = Spring.constant(this.applet.buttonWidth());
/* 368 */       this.buttonHeight = Spring.constant(this.applet.buttonHeight());
/*     */ 
/* 370 */       this.minimumLabel.setFont(this.minimumLabel.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 372 */       this.maximumLabel.setFont(this.maximumLabel.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 374 */       this.majorUnitLabel.setFont(this.majorUnitLabel.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 376 */       this.minorUnitLabel.setFont(this.minorUnitLabel.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 378 */       this.minimumTextBox.setFont(this.minimumTextBox.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 380 */       this.maximumTextBox.setFont(this.maximumTextBox.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 382 */       this.majorUnitTextBox.setFont(this.majorUnitTextBox.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 384 */       this.minorUnitTextBox.setFont(this.minorUnitTextBox.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 386 */       this.majorCheckBox.setFont(this.majorCheckBox.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 388 */       this.minorCheckBox.setFont(this.minorCheckBox.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 390 */       this.applyButton.setFont(this.applyButton.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 392 */       this.undoButton.setFont(this.undoButton.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 394 */       this.okButton.setFont(this.okButton.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */ 
/* 396 */       this.cancelButton.setFont(this.cancelButton.getFont().deriveFont(this.applet.buttonTextSize()));
/*     */     }
/*     */ 
/* 399 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean apply()
/*     */   {
/* 470 */     AxisData axisData = new AxisData();
/*     */ 
/* 472 */     String s = this.minimumTextBox.getText();
/* 473 */     s = s.replace("−", "-");
/*     */     try {
/* 475 */       axisData.minimum = Double.parseDouble(s);
/*     */     }
/*     */     catch (NumberFormatException e) {
/*     */     }
/* 479 */     s = this.maximumTextBox.getText();
/* 480 */     s = s.replace("−", "-");
/*     */     try {
/* 482 */       axisData.maximum = Double.parseDouble(s);
/*     */     }
/*     */     catch (NumberFormatException e) {
/*     */     }
/* 486 */     if (axisData.maximum <= axisData.minimum) {
/* 487 */       JOptionPane.showMessageDialog(this, "The minimum must be less than the maximum.", "Java Scientific Calculator", 0);
/*     */ 
/* 492 */       return false;
/*     */     }
/*     */ 
/* 495 */     s = this.majorUnitTextBox.getText();
/* 496 */     s = s.replace("−", "-");
/*     */     try {
/* 498 */       axisData.majorUnit = Double.parseDouble(s);
/*     */     }
/*     */     catch (NumberFormatException e) {
/*     */     }
/* 502 */     s = this.minorUnitTextBox.getText();
/* 503 */     s = s.replace("−", "-");
/*     */     try {
/* 505 */       axisData.minorUnit = Double.parseDouble(s);
/*     */     }
/*     */     catch (NumberFormatException e) {
/*     */     }
/* 509 */     if (axisData.majorUnit < axisData.minorUnit) {
/* 510 */       JOptionPane.showMessageDialog(this, "The major unit must be no less than the minor unit.", "Java Scientific Calculator", 0);
/*     */ 
/* 516 */       return false;
/*     */     }
/* 518 */     double ratio = axisData.majorUnit / axisData.minorUnit;
/* 519 */     if (ratio != Math.floor(ratio)) {
/* 520 */       JOptionPane.showMessageDialog(this, "The major unit must be an integer multiple the minor unit.", "Java Scientific Calculator", 0);
/*     */ 
/* 526 */       return false;
/*     */     }
/*     */ 
/* 529 */     axisData.majorVisible = this.majorCheckBox.isSelected();
/* 530 */     axisData.minorVisible = this.minorCheckBox.isSelected();
/*     */ 
/* 532 */     if (axisData.equals((AxisData)this.stack.peekFirst())) return true;
/* 533 */     boolean forceUpdate = !axisData.minMaxMatches((AxisData)this.stack.peekFirst());
/*     */ 
/* 535 */     this.stack.addFirst(axisData);
/* 536 */     this.undoButton.setEnabled(true);
/*     */ 
/* 538 */     double origin = (axisData.maximum + axisData.minimum) / 2.0D;
/* 539 */     double width = axisData.maximum - axisData.minimum;
/* 540 */     if (this.x) {
/* 541 */       this.view.getTransformation().setOriginX(origin);
/* 542 */       this.view.getTransformation().setScaleX(this.view.getWidth() / width);
/* 543 */       this.view.getTransformation().setXMajorUnit(axisData.majorUnit);
/* 544 */       this.view.getTransformation().setXMinorUnit(axisData.minorUnit);
/*     */     } else {
/* 546 */       this.view.getTransformation().setOriginY(origin);
/* 547 */       this.view.getTransformation().setScaleY(this.view.getHeight() / width);
/* 548 */       this.view.getTransformation().setYMajorUnit(axisData.majorUnit);
/* 549 */       this.view.getTransformation().setYMinorUnit(axisData.minorUnit);
/*     */     }
/* 551 */     this.axis.setShowMajorUnit(axisData.majorVisible);
/* 552 */     this.axis.setShowMinorUnit(axisData.minorVisible);
/* 553 */     if (forceUpdate) this.view.forceUpdate();
/* 554 */     this.view.repaint();
/* 555 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean undo()
/*     */   {
/* 565 */     if (this.stack.size() <= 1) return false;
/* 566 */     AxisData oldData = (AxisData)this.stack.remove();
/* 567 */     if (this.stack.size() <= 1) {
/* 568 */       this.undoButton.setEnabled(false);
/*     */     }
/* 570 */     NumberFormat f = NumberFormat.getNumberInstance();
/* 571 */     if ((f instanceof DecimalFormat)) {
/* 572 */       DecimalFormat d = (DecimalFormat)f;
/* 573 */       d.setNegativePrefix("−");
/*     */     }
/* 575 */     AxisData axisData = (AxisData)this.stack.peekFirst();
/* 576 */     this.minimumTextBox.setText(f.format(axisData.minimum));
/* 577 */     this.maximumTextBox.setText(f.format(axisData.maximum));
/* 578 */     this.majorUnitTextBox.setText(Double.toString(axisData.majorUnit));
/* 579 */     this.minorUnitTextBox.setText(Double.toString(axisData.minorUnit));
/* 580 */     this.majorCheckBox.setSelected(axisData.majorVisible);
/* 581 */     this.minorCheckBox.setSelected(axisData.minorVisible);
/*     */ 
/* 583 */     double origin = (axisData.maximum + axisData.minimum) / 2.0D;
/* 584 */     double width = axisData.maximum - axisData.minimum;
/* 585 */     if (this.x) {
/* 586 */       this.view.getTransformation().setOriginX(origin);
/* 587 */       this.view.getTransformation().setScaleX(this.view.getWidth() / width);
/* 588 */       this.view.getTransformation().setXMajorUnit(axisData.majorUnit);
/* 589 */       this.view.getTransformation().setXMinorUnit(axisData.minorUnit);
/*     */     } else {
/* 591 */       this.view.getTransformation().setOriginY(origin);
/* 592 */       this.view.getTransformation().setScaleY(this.view.getHeight() / width);
/* 593 */       this.view.getTransformation().setYMajorUnit(axisData.majorUnit);
/* 594 */       this.view.getTransformation().setYMinorUnit(axisData.minorUnit);
/*     */     }
/* 596 */     this.axis.setShowMajorUnit(axisData.majorVisible);
/* 597 */     this.axis.setShowMinorUnit(axisData.minorVisible);
/* 598 */     if (!oldData.minMaxMatches((AxisData)this.stack.peekFirst()))
/* 599 */       this.view.forceUpdate();
/* 600 */     this.view.repaint();
/* 601 */     return true;
/*     */   }
/*     */ 
/*     */   private void cancel()
/*     */   {
/* 609 */     AxisData axisData = (AxisData)this.stack.removeLast();
/* 610 */     double origin = (axisData.maximum + axisData.minimum) / 2.0D;
/* 611 */     double width = axisData.maximum - axisData.minimum;
/* 612 */     if (this.x) {
/* 613 */       this.view.getTransformation().setOriginX(origin);
/* 614 */       this.view.getTransformation().setScaleX(this.view.getWidth() / width);
/* 615 */       this.view.getTransformation().setXMajorUnit(axisData.majorUnit);
/* 616 */       this.view.getTransformation().setXMinorUnit(axisData.minorUnit);
/*     */     } else {
/* 618 */       this.view.getTransformation().setOriginY(origin);
/* 619 */       this.view.getTransformation().setScaleY(this.view.getHeight() / width);
/* 620 */       this.view.getTransformation().setYMajorUnit(axisData.majorUnit);
/* 621 */       this.view.getTransformation().setYMinorUnit(axisData.minorUnit);
/*     */     }
/* 623 */     this.axis.setShowMajorUnit(axisData.majorVisible);
/* 624 */     this.axis.setShowMinorUnit(axisData.minorVisible);
/* 625 */     this.view.forceUpdate();
/* 626 */     this.view.repaint();
/*     */   }
/*     */ 
/*     */   public class CancelButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     public CancelButtonListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent event)
/*     */     {
/* 677 */       AxisDialog.this.cancel();
/* 678 */       AxisDialog.this.setVisible(false);
/*     */ 
/* 680 */       AxisDialog.this.stack.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public class OKButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     public OKButtonListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent event)
/*     */     {
/* 662 */       if (!AxisDialog.this.apply()) return;
/* 663 */       AxisDialog.this.setVisible(false);
/*     */ 
/* 665 */       AxisDialog.this.stack.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public class UndoButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     public UndoButtonListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent event)
/*     */     {
/* 650 */       AxisDialog.this.undo();
/*     */     }
/*     */   }
/*     */ 
/*     */   public class ApplyButtonListener
/*     */     implements ActionListener
/*     */   {
/*     */     public ApplyButtonListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent event)
/*     */     {
/* 638 */       AxisDialog.this.apply();
/*     */     }
/*     */   }
/*     */ 
/*     */   public class FieldFocusListener
/*     */     implements FocusListener
/*     */   {
/*     */     private JTextField textField;
/*     */     private boolean positive;
/*     */ 
/*     */     FieldFocusListener(JTextField textField, boolean positive)
/*     */     {
/* 414 */       this.textField = textField;
/* 415 */       this.positive = positive;
/*     */     }
/*     */ 
/*     */     public void focusGained(FocusEvent focusEvent)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void focusLost(FocusEvent focusEvent)
/*     */     {
/* 428 */       String s = this.textField.getText();
/* 429 */       s = s.replace("−", "-");
/* 430 */       double value = 0.0D;
/*     */       try {
/* 432 */         value = Double.parseDouble(s);
/*     */       } catch (NumberFormatException e) {
/* 434 */         if (this.positive)
/* 435 */           this.textField.setText("1");
/*     */         else
/* 437 */           this.textField.setText("0");
/* 438 */         this.textField.requestFocusInWindow();
/* 439 */         return;
/*     */       }
/* 441 */       if ((this.positive) && (value <= 0.0D)) {
/* 442 */         this.textField.setText("1");
/* 443 */         this.textField.requestFocusInWindow();
/* 444 */         return;
/*     */       }
/*     */ 
/* 447 */       s = s.replace("-", "−");
/* 448 */       this.textField.setText(s);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.graph.AxisDialog
 * JD-Core Version:    0.6.0
 */