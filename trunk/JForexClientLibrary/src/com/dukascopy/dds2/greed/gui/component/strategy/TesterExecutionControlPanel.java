/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlEvent;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlListener;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.JWindow;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class TesterExecutionControlPanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  43 */   private ResizableIcon START_ICON = new ResizableIcon("tester_play.png");
/*  44 */   private ResizableIcon START_ICON_FADED = new ResizableIcon("tester_play_inactive.png");
/*  45 */   private ResizableIcon CANCEL_ICON = new ResizableIcon("tester_stop.png");
/*  46 */   private ResizableIcon CANCEL_ICON_FADED = new ResizableIcon("tester_stop_inactive.png");
/*  47 */   private ResizableIcon PAUSE_ICON = new ResizableIcon("tester_pause.png");
/*  48 */   private ResizableIcon PAUSE_ICON_FADED = new ResizableIcon("tester_pause_inactive.png");
/*  49 */   private ResizableIcon RUN_ICON = new ResizableIcon("tester_play.png");
/*  50 */   private ResizableIcon RUN_ICON_FADED = new ResizableIcon("tester_play_inactive.png");
/*  51 */   private ResizableIcon NEXT_TICK_ICON = new ResizableIcon("tester_next_tick.png");
/*  52 */   private ResizableIcon NEXT_TICK_ICON_FADED = new ResizableIcon("tester_next_tick_inactive.png");
/*     */ 
/*  54 */   private ExecutionControl executionControl = null;
/*  55 */   private ActionListener startListener = null;
/*  56 */   private ActionListener cancelListener = null;
/*     */ 
/*  58 */   private int actualMaximumSpeed = 79;
/*     */ 
/*  60 */   private int toolTipWindowWidth = 30;
/*  61 */   private int toolTipWindowHeight = 15;
/*     */ 
/*  63 */   private JButton nextTickButton = null;
/*     */ 
/*  65 */   private JButton startButton = null;
/*  66 */   private JButton cancelButton = null;
/*     */ 
/*  68 */   private JButton runButton = null;
/*  69 */   private JButton pauseButton = null;
/*     */ 
/*  71 */   private CardLayoutPanel startCancelButtonsPanel = null;
/*  72 */   private CardLayoutPanel runPauseButtonsPanel = null;
/*     */ 
/*  74 */   private int currentSpeed = this.actualMaximumSpeed;
/*     */   private TesterSlider slider;
/*  78 */   private boolean vmEnabled = false;
/*     */   private JLabel textLabel;
/*     */   private JLocalizableLabel maxLabel;
/*     */   private JLocalizableLabel minLabel;
/*     */ 
/*     */   public TesterExecutionControlPanel(ExecutionControl executionControl, ActionListener startListener, ActionListener cancelListener)
/*     */   {
/*  86 */     this.executionControl = executionControl;
/*  87 */     this.startListener = startListener;
/*  88 */     this.cancelListener = cancelListener;
/*     */ 
/*  90 */     build();
/*     */   }
/*     */ 
/*     */   public void setVMEnabled(boolean vmEnabled) {
/*  94 */     this.vmEnabled = vmEnabled;
/*  95 */     this.slider.setEnabled(vmEnabled);
/*  96 */     this.slider.setValue(this.actualMaximumSpeed);
/*  97 */     this.textLabel.setEnabled(vmEnabled);
/*  98 */     this.minLabel.setEnabled(vmEnabled);
/*  99 */     this.maxLabel.setEnabled(vmEnabled);
/*     */   }
/*     */ 
/*     */   private void build() {
/* 103 */     this.startCancelButtonsPanel = new CardLayoutPanel();
/* 104 */     this.runPauseButtonsPanel = new CardLayoutPanel();
/*     */ 
/* 106 */     this.nextTickButton = createButton("button.next.tick.bar", this.NEXT_TICK_ICON, this.NEXT_TICK_ICON_FADED);
/*     */ 
/* 108 */     this.startButton = createButton("button.start", this.START_ICON, this.START_ICON_FADED);
/* 109 */     this.cancelButton = createButton("button.stop", this.CANCEL_ICON, this.CANCEL_ICON_FADED);
/*     */ 
/* 111 */     this.runButton = createButton("button.resume", this.RUN_ICON, this.RUN_ICON_FADED);
/* 112 */     this.pauseButton = createButton("button.pause", this.PAUSE_ICON, this.PAUSE_ICON_FADED);
/*     */ 
/* 114 */     this.startCancelButtonsPanel.add(this.startButton, "button.start");
/* 115 */     this.startCancelButtonsPanel.add(this.cancelButton, "button.stop");
/*     */ 
/* 117 */     this.runPauseButtonsPanel.add(this.runButton, "button.run");
/* 118 */     this.runPauseButtonsPanel.add(this.pauseButton, "button.pause");
/*     */ 
/* 120 */     setLayout(new GridBagLayout());
/* 121 */     GridBagConstraints gbc = new GridBagConstraints();
/* 122 */     gbc.fill = 0;
/* 123 */     gbc.anchor = 21;
/*     */ 
/* 125 */     this.textLabel = new JLocalizableLabel("label.test.speed");
/* 126 */     this.textLabel.setEnabled(false);
/* 127 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 10, 0, gbc, this, this.textLabel);
/*     */ 
/* 129 */     this.slider = new TesterSlider(0, 0, this.actualMaximumSpeed, this.currentSpeed);
/* 130 */     this.slider.setFocusable(false);
/* 131 */     this.slider.setEnabled(false);
/*     */ 
/* 133 */     JLabel toolTipLabel = new JLabel("", 0);
/* 134 */     toolTipLabel.setOpaque(false);
/*     */ 
/* 136 */     JWindow toolTipWindow = new JWindow(SwingUtilities.getWindowAncestor(this));
/* 137 */     toolTipWindow.setSize(this.toolTipWindowWidth, this.toolTipWindowHeight);
/* 138 */     toolTipWindow.setContentPane(toolTipLabel);
/* 139 */     toolTipWindow.setVisible(false);
/*     */ 
/* 141 */     this.slider.addMouseListener(new MouseAdapter(toolTipLabel, toolTipWindow)
/*     */     {
/*     */       public void mousePressed(MouseEvent e)
/*     */       {
/* 146 */         int sliderValue = TesterExecutionControlPanel.this.slider.getValue() / 10 + 1;
/* 147 */         this.val$toolTipLabel.setText(String.valueOf(sliderValue) + " X");
/*     */ 
/* 149 */         this.val$toolTipWindow.setLocation(TesterExecutionControlPanel.this.slider.calculateToolTipLocation());
/* 150 */         this.val$toolTipWindow.setVisible(true);
/* 151 */         TesterExecutionControlPanel.this.slider.repaint();
/*     */       }
/*     */ 
/*     */       public void mouseReleased(MouseEvent e)
/*     */       {
/* 156 */         this.val$toolTipWindow.setVisible(false);
/* 157 */         TesterExecutionControlPanel.this.slider.repaint();
/*     */       }
/*     */     });
/* 162 */     this.slider.addChangeListener(new ChangeListener(toolTipLabel, toolTipWindow)
/*     */     {
/*     */       public void stateChanged(ChangeEvent e)
/*     */       {
/* 167 */         int sliderValue = TesterExecutionControlPanel.this.slider.getValue() / 10 + 1;
/* 168 */         this.val$toolTipLabel.setText(String.valueOf(sliderValue) + " X");
/*     */ 
/* 170 */         TesterExecutionControlPanel.access$102(TesterExecutionControlPanel.this, sliderValue - 1);
/* 171 */         TesterExecutionControlPanel.this.executionControl.setSpeed(TesterExecutionControlPanel.this.currentSpeed);
/*     */ 
/* 173 */         if (TesterExecutionControlPanel.this.slider.isShowing()) {
/* 174 */           this.val$toolTipWindow.setLocation(TesterExecutionControlPanel.this.slider.calculateToolTipLocation());
/*     */         }
/*     */ 
/* 177 */         TesterExecutionControlPanel.this.slider.repaint();
/*     */       }
/*     */     });
/* 181 */     this.minLabel = new JLocalizableLabel("label.min");
/* 182 */     this.minLabel.setEnabled(false);
/* 183 */     this.maxLabel = new JLocalizableLabel("label.max");
/* 184 */     this.maxLabel.setEnabled(false);
/*     */ 
/* 186 */     gbc.anchor = 21;
/*     */ 
/* 188 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 2, 0, 0, 0, gbc, this, this.minLabel);
/* 189 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 2, 0, 0, 0, gbc, this, this.slider);
/* 190 */     GridBagLayoutHelper.add(3, 0, 0.0D, 0.0D, 1, 1, 2, 0, 2, 0, gbc, this, this.maxLabel);
/* 191 */     GridBagLayoutHelper.add(4, 0, 0.0D, 0.0D, 1, 1, 2, 0, 0, 0, gbc, this, this.nextTickButton);
/* 192 */     GridBagLayoutHelper.add(5, 0, 0.0D, 0.0D, 1, 1, 2, 0, 0, 0, gbc, this, this.runPauseButtonsPanel);
/* 193 */     GridBagLayoutHelper.add(6, 0, 0.0D, 0.0D, 1, 1, 2, 0, 0, 0, gbc, this, this.startCancelButtonsPanel);
/*     */ 
/* 195 */     this.executionControl.setSpeed(7);
/* 196 */     this.executionControl.addExecutionControlListener(new ExecutionControlListener()
/*     */     {
/*     */       public void stateChanged(ExecutionControlEvent event)
/*     */       {
/* 200 */         TesterExecutionControlPanel.this.updateExecutionControls();
/*     */       }
/*     */ 
/*     */       public void speedChanged(ExecutionControlEvent event)
/*     */       {
/* 205 */         ExecutionControl control = event.getExecutionControl();
/* 206 */         TesterExecutionControlPanel.access$102(TesterExecutionControlPanel.this, control.getSpeed());
/* 207 */         TesterExecutionControlPanel.this.slider.setValue((TesterExecutionControlPanel.this.currentSpeed + 1) * 10 - 1);
/*     */       }
/*     */     });
/* 211 */     updateExecutionControls();
/*     */ 
/* 213 */     this.startButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 216 */         TesterExecutionControlPanel.this.startListener.actionPerformed(e);
/*     */       }
/*     */     });
/* 219 */     this.cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 222 */         if (TesterExecutionControlPanel.this.cancelListener != null)
/* 223 */           TesterExecutionControlPanel.this.cancelListener.actionPerformed(e);
/*     */       }
/*     */     });
/* 227 */     this.runButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 230 */         if ((TesterExecutionControlPanel.this.executionControl != null) && 
/* 231 */           (TesterExecutionControlPanel.this.executionControl.isPaused())) {
/* 232 */           TesterExecutionControlPanel.this.executionControl.run();
/* 233 */           TesterExecutionControlPanel.this.runPauseButtonsPanel.showComponent("button.pause");
/*     */         }
/*     */       }
/*     */     });
/* 238 */     this.pauseButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 241 */         if ((TesterExecutionControlPanel.this.executionControl != null) && 
/* 242 */           (!TesterExecutionControlPanel.this.executionControl.isPaused())) {
/* 243 */           TesterExecutionControlPanel.this.executionControl.pause();
/* 244 */           TesterExecutionControlPanel.this.runPauseButtonsPanel.showComponent("button.run");
/*     */         }
/*     */       }
/*     */     });
/* 250 */     this.nextTickButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 252 */         if (TesterExecutionControlPanel.this.executionControl != null)
/* 253 */           TesterExecutionControlPanel.this.executionControl.nextTick();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void updateExecutionControls()
/*     */   {
/* 280 */     this.currentSpeed = this.executionControl.getSpeed();
/*     */ 
/* 282 */     if (this.executionControl.isExecuting()) {
/* 283 */       this.startButton.setEnabled(false);
/* 284 */       this.cancelButton.setEnabled(true);
/* 285 */       this.startCancelButtonsPanel.showComponent("button.stop");
/*     */ 
/* 287 */       this.runButton.setEnabled(this.vmEnabled);
/* 288 */       this.pauseButton.setEnabled(this.vmEnabled);
/*     */ 
/* 290 */       if (this.executionControl.isPaused())
/*     */       {
/* 292 */         this.runPauseButtonsPanel.showComponent("button.run");
/* 293 */         this.nextTickButton.setEnabled(true);
/*     */       }
/*     */       else {
/* 296 */         this.runPauseButtonsPanel.showComponent("button.pause");
/* 297 */         this.nextTickButton.setEnabled(false);
/*     */       }
/* 299 */       if ((!this.vmEnabled) && (!this.executionControl.isOptimization())) {
/* 300 */         this.currentSpeed = 7;
/* 301 */         this.executionControl.setSpeed(7);
/*     */       }
/*     */     } else {
/* 304 */       this.startButton.setEnabled(this.executionControl.isStartEnabled());
/* 305 */       this.cancelButton.setEnabled(false);
/* 306 */       this.startCancelButtonsPanel.showComponent("button.start");
/* 307 */       this.runButton.setEnabled(false);
/* 308 */       this.pauseButton.setEnabled(false);
/* 309 */       this.runPauseButtonsPanel.showComponent("button.pause");
/* 310 */       this.nextTickButton.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getSpeedValueLabel()
/*     */   {
/* 320 */     switch (this.currentSpeed) { case 0:
/* 321 */       return "1 X";
/*     */     case 1:
/* 322 */       return "2 X";
/*     */     case 2:
/* 323 */       return "3 X";
/*     */     case 3:
/* 324 */       return "4 X";
/*     */     case 4:
/* 325 */       return "5 X";
/*     */     case 5:
/* 326 */       return "6 X";
/*     */     case 6:
/* 327 */       return "7 X";
/*     */     case 7:
/* 328 */       return "8 X"; }
/* 329 */     return "1 X";
/*     */   }
/*     */ 
/*     */   private JButton createButton(String key, ResizableIcon icon, ResizableIcon disabledIcon)
/*     */   {
/* 347 */     JLocalizableButton button = new JLocalizableButton();
/* 348 */     button.setToolTipKey(key);
/* 349 */     button.setIcon(icon);
/* 350 */     button.setDisabledIcon(disabledIcon);
/*     */ 
/* 352 */     Dimension size = new Dimension(icon.getIconWidth() + 8, icon.getIconHeight() + 8);
/*     */ 
/* 354 */     button.setMinimumSize(size);
/* 355 */     button.setMaximumSize(size);
/* 356 */     button.setPreferredSize(size);
/*     */ 
/* 358 */     return button;
/*     */   }
/*     */ 
/*     */   class TesterSlider extends JSlider
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     public TesterSlider(int orientation, int min, int max, int value) {
/* 367 */       super(min, max, value);
/*     */     }
/*     */ 
/*     */     public Point calculateToolTipLocation()
/*     */     {
/* 372 */       Point sliderLocation = getLocationOnScreen();
/* 373 */       sliderLocation.y += TesterExecutionControlPanel.this.toolTipWindowHeight;
/*     */ 
/* 376 */       double oneTickInPixels = (getWidth() - 12) / (getMaximum() + 1);
/* 377 */       double sliderPlace = oneTickInPixels * (getValue() + 1);
/*     */ 
/* 379 */       int additionalWidth = (int)Math.round(sliderPlace) - TesterExecutionControlPanel.this.toolTipWindowWidth - 5;
/* 380 */       sliderLocation.x += additionalWidth;
/*     */ 
/* 383 */       return sliderLocation;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterExecutionControlPanel
 * JD-Core Version:    0.6.0
 */