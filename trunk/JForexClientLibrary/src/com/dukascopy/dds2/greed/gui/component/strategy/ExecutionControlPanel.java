/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlEvent;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlListener;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableSlider;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class ExecutionControlPanel extends JPanel
/*     */ {
/*     */   private JLabel labelTestSpeed;
/*     */   private JButton buttonSlower;
/*     */   private JSlider speedSlider;
/*     */   private JButton buttonFaster;
/*     */   private JLocalizableButton nextTickButton;
/*     */   private JLocalizableButton runButton;
/*     */   private JLocalizableButton pauseButton;
/*     */   private CardLayoutPanel runPauseButtonsPanel;
/*     */   private JLocalizableButton startButton;
/*     */   private JLocalizableButton cancelButton;
/*     */   private CardLayoutPanel startCancelButtonsPanel;
/*     */   private ExecutionControl executionControl;
/*     */   private ActionListener startListener;
/*     */   private ActionListener cancelListener;
/*     */ 
/*     */   public ExecutionControlPanel(ExecutionControl executionControl, ActionListener startListener, ActionListener cancelListener)
/*     */   {
/*  56 */     this.executionControl = executionControl;
/*  57 */     this.startListener = startListener;
/*  58 */     this.cancelListener = cancelListener;
/*     */   }
/*     */ 
/*     */   public void build()
/*     */   {
/*  64 */     this.speedSlider = new JLocalizableSlider(0, 0, 7, 7);
/*  65 */     this.speedSlider.setMajorTickSpacing(1);
/*  66 */     this.speedSlider.setToolTipText("slider.tooltip.speed");
/*     */ 
/*  77 */     this.speedSlider.setPaintTicks(false);
/*  78 */     this.speedSlider.setPaintLabels(false);
/*  79 */     this.speedSlider.setSnapToTicks(true);
/*  80 */     Dimension sliderPreferredSize = this.speedSlider.getPreferredSize();
/*  81 */     sliderPreferredSize.height = (int)(sliderPreferredSize.height * 0.75D);
/*  82 */     this.speedSlider.setPreferredSize(sliderPreferredSize);
/*     */ 
/*  84 */     this.labelTestSpeed = new JLocalizableLabel("label.test.speed");
/*  85 */     this.buttonSlower = new JButton("-");
/*  86 */     this.buttonFaster = new JButton("+");
/*     */ 
/*  88 */     this.startButton = new JLocalizableButton("button.start");
/*  89 */     this.startButton.setEnabled(this.startListener != null);
/*  90 */     this.cancelButton = new JLocalizableButton("button.cancel");
/*  91 */     this.cancelButton.setEnabled(this.cancelListener != null);
/*  92 */     this.runButton = new JLocalizableButton("button.run");
/*  93 */     this.pauseButton = new JLocalizableButton("button.pause");
/*  94 */     this.runPauseButtonsPanel = new CardLayoutPanel();
/*  95 */     this.runPauseButtonsPanel.add(this.runButton, "button.run");
/*  96 */     this.runPauseButtonsPanel.add(this.pauseButton, "button.pause");
/*     */ 
/*  98 */     this.nextTickButton = new JLocalizableButton("button.next.tick.bar");
/*     */ 
/* 100 */     setLayout(new GridBagLayout());
/* 101 */     GridBagConstraints gbc = new GridBagConstraints();
/* 102 */     gbc.fill = 0;
/* 103 */     gbc.anchor = 17;
/* 104 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, this, this.labelTestSpeed);
/* 105 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.buttonSlower);
/* 106 */     gbc.fill = 1;
/* 107 */     GridBagLayoutHelper.add(2, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.speedSlider);
/* 108 */     gbc.fill = 0;
/* 109 */     GridBagLayoutHelper.add(3, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.buttonFaster);
/* 110 */     GridBagLayoutHelper.add(4, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.nextTickButton);
/* 111 */     GridBagLayoutHelper.add(5, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.runPauseButtonsPanel);
/*     */ 
/* 113 */     this.startCancelButtonsPanel = new CardLayoutPanel();
/* 114 */     this.startCancelButtonsPanel.add(this.startButton, "button.start");
/* 115 */     this.startCancelButtonsPanel.add(this.cancelButton, "button.cancel");
/* 116 */     if ((this.startListener != null) || (this.cancelListener != null)) {
/* 117 */       GridBagLayoutHelper.add(6, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.startCancelButtonsPanel);
/*     */     }
/*     */ 
/* 120 */     this.executionControl.addExecutionControlListener(new ExecutionControlListener()
/*     */     {
/*     */       public void stateChanged(ExecutionControlEvent event)
/*     */       {
/* 124 */         ExecutionControlPanel.this.updateExecutionControls();
/*     */       }
/*     */ 
/*     */       public void speedChanged(ExecutionControlEvent event)
/*     */       {
/* 129 */         ExecutionControl control = event.getExecutionControl();
/* 130 */         ExecutionControlPanel.this.speedSlider.setValue(control.getSpeed());
/* 131 */         ExecutionControlPanel.this.updateSliderControls(control.isExecuting());
/*     */       }
/*     */     });
/* 135 */     updateExecutionControls();
/*     */ 
/* 137 */     this.speedSlider.addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/* 139 */         if (ExecutionControlPanel.this.executionControl != null) {
/* 140 */           ExecutionControlPanel.this.executionControl.setSpeed(ExecutionControlPanel.this.speedSlider.getValue());
/* 141 */           ExecutionControlPanel.this.updateSliderControls(ExecutionControlPanel.this.executionControl.isExecuting());
/*     */         }
/*     */       }
/*     */     });
/* 145 */     this.buttonSlower.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 148 */         int value = ExecutionControlPanel.this.speedSlider.getValue();
/* 149 */         if (value > ExecutionControlPanel.this.speedSlider.getMinimum())
/* 150 */           ExecutionControlPanel.this.speedSlider.setValue(value - ExecutionControlPanel.this.speedSlider.getMajorTickSpacing());
/*     */       }
/*     */     });
/* 154 */     this.buttonFaster.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 157 */         int value = ExecutionControlPanel.this.speedSlider.getValue();
/* 158 */         if (value < ExecutionControlPanel.this.speedSlider.getMaximum())
/* 159 */           ExecutionControlPanel.this.speedSlider.setValue(value + ExecutionControlPanel.this.speedSlider.getMajorTickSpacing());
/*     */       }
/*     */     });
/* 163 */     this.startButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 166 */         if (ExecutionControlPanel.this.startListener != null)
/* 167 */           ExecutionControlPanel.this.startListener.actionPerformed(e);
/*     */       }
/*     */     });
/* 171 */     this.cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 174 */         if (ExecutionControlPanel.this.cancelListener != null)
/* 175 */           ExecutionControlPanel.this.cancelListener.actionPerformed(e);
/*     */       }
/*     */     });
/* 179 */     this.runButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 182 */         if ((ExecutionControlPanel.this.executionControl != null) && 
/* 183 */           (ExecutionControlPanel.this.executionControl.isPaused())) {
/* 184 */           ExecutionControlPanel.this.executionControl.run();
/* 185 */           ExecutionControlPanel.this.runPauseButtonsPanel.showComponent("button.pause");
/*     */         }
/*     */       }
/*     */     });
/* 190 */     this.pauseButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 193 */         if ((ExecutionControlPanel.this.executionControl != null) && 
/* 194 */           (!ExecutionControlPanel.this.executionControl.isPaused())) {
/* 195 */           ExecutionControlPanel.this.executionControl.pause();
/* 196 */           ExecutionControlPanel.this.runPauseButtonsPanel.showComponent("button.run");
/*     */         }
/*     */       }
/*     */     });
/* 202 */     this.nextTickButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 204 */         if (ExecutionControlPanel.this.executionControl != null)
/* 205 */           ExecutionControlPanel.this.executionControl.nextTick();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void set(StrategyTestBean strategyTestBean) {
/* 212 */     this.executionControl.setSpeed(strategyTestBean.getExecutionSpeed());
/*     */   }
/*     */ 
/*     */   public void save(StrategyTestBean strategyTestBean) {
/* 216 */     strategyTestBean.setExecutionSpeed(this.executionControl.getSpeed());
/*     */   }
/*     */ 
/*     */   private void updateExecutionControls()
/*     */   {
/* 221 */     this.speedSlider.setValue(this.executionControl.getSpeed());
/*     */ 
/* 223 */     if (this.executionControl.isExecuting()) {
/* 224 */       this.startButton.setEnabled(false);
/* 225 */       this.cancelButton.setEnabled(true);
/* 226 */       this.startCancelButtonsPanel.showComponent("button.cancel");
/* 227 */       updateSliderControls(true);
/* 228 */       this.runButton.setEnabled(true);
/* 229 */       this.pauseButton.setEnabled(true);
/*     */ 
/* 231 */       if (this.executionControl.isPaused())
/*     */       {
/* 233 */         this.runPauseButtonsPanel.showComponent("button.run");
/* 234 */         this.nextTickButton.setEnabled(true);
/*     */       }
/*     */       else {
/* 237 */         this.runPauseButtonsPanel.showComponent("button.pause");
/* 238 */         this.nextTickButton.setEnabled(false);
/*     */       }
/* 240 */       if ((!this.executionControl.isVisualEnabled()) && (!this.executionControl.isOptimization()))
/* 241 */         this.executionControl.setSpeed(7);
/*     */     }
/*     */     else
/*     */     {
/* 245 */       this.startButton.setEnabled(this.executionControl.isStartEnabled());
/* 246 */       this.cancelButton.setEnabled(false);
/* 247 */       this.startCancelButtonsPanel.showComponent("button.start");
/* 248 */       updateSliderControls(false);
/* 249 */       this.runButton.setEnabled(false);
/* 250 */       this.pauseButton.setEnabled(false);
/* 251 */       this.runPauseButtonsPanel.showComponent("button.pause");
/* 252 */       this.nextTickButton.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateSliderControls(boolean isExecuting) {
/* 257 */     if (isExecuting)
/* 258 */       this.speedSlider.setEnabled((this.executionControl.isVisualEnabled()) || (this.executionControl.isOptimization()));
/*     */     else {
/* 260 */       this.speedSlider.setEnabled(this.executionControl.isStartEnabled());
/*     */     }
/* 262 */     this.buttonSlower.setEnabled((this.speedSlider.isEnabled()) && (this.speedSlider.getValue() > this.speedSlider.getMinimum()));
/* 263 */     this.buttonFaster.setEnabled((this.speedSlider.isEnabled()) && (this.speedSlider.getValue() < this.speedSlider.getMaximum()));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.ExecutionControlPanel
 * JD-Core Version:    0.6.0
 */