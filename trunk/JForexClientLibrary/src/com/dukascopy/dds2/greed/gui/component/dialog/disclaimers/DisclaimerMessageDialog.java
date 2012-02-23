/*     */ package com.dukascopy.dds2.greed.gui.component.dialog.disclaimers;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.IDisclaimer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public abstract class DisclaimerMessageDialog extends JLocalizableDialog
/*     */   implements IDisclaimer
/*     */ {
/*  17 */   private static final Dimension BUTTON_SIZE = new Dimension(125, 23);
/*     */   private final String message;
/*     */   private final Object[] msgParams;
/*     */   private final String windowTitle;
/*     */   private final String discRevVersion;
/*     */   private final String discTitle;
/*     */   private JPanel alerterPanel;
/*     */   private JPanel checkPanel;
/*     */   private JPanel controlPanel;
/*     */   private JLocalizableButton agreeButton;
/*     */   private JLocalizableButton notAgreeButton;
/*     */   private JLocalizableButton closeButton;
/*     */   private ImageIcon discLogo;
/*     */   protected JLocalizableCheckBox continueDiclaimingCheck;
/*     */ 
/*     */   public DisclaimerMessageDialog(String message, Object[] msgParams, String windowTitle, String discRevVersion, String discTitle, ImageIcon discLogo)
/*     */   {
/*  45 */     this.message = message;
/*  46 */     this.windowTitle = windowTitle;
/*  47 */     this.msgParams = msgParams;
/*  48 */     this.discTitle = discTitle;
/*  49 */     this.discRevVersion = discRevVersion;
/*  50 */     this.discLogo = discLogo;
/*     */ 
/*  52 */     build();
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/*  57 */     initAlertPanel();
/*  58 */     initCheckPanel();
/*  59 */     initControlPanel();
/*     */ 
/*  61 */     if (this.discRevVersion != null) {
/*  62 */       setParamKeys(new String[] { this.discRevVersion });
/*     */     }
/*  64 */     setTitle(this.windowTitle);
/*     */ 
/*  66 */     JPanel container = new JPanel();
/*  67 */     container.setLayout(new GridBagLayout());
/*     */ 
/*  69 */     GridBagConstraints c = new GridBagConstraints();
/*  70 */     c.insets = new Insets(0, 10, 5, 10);
/*  71 */     c.weightx = 1.0D; c.weighty = 0.5D;
/*  72 */     c.anchor = 17;
/*  73 */     c.fill = 2;
/*  74 */     c.gridwidth = 0;
/*  75 */     int gridY = 0;
/*     */ 
/*  77 */     c.gridx = 0; c.gridy = (gridY++);
/*  78 */     c.ipady = 5;
/*  79 */     container.add(Box.createRigidArea(new Dimension(0, 8)));
/*     */ 
/*  81 */     container.add(this.alerterPanel, c);
/*     */ 
/*  83 */     c.fill = 1;
/*  84 */     c.ipady = 0;
/*  85 */     c.gridx = 0; c.gridy = (gridY++);
/*  86 */     c.fill = 2;
/*  87 */     c.gridwidth = 0;
/*  88 */     if (this.checkPanel != null) container.add(this.checkPanel, c);
/*     */ 
/*  90 */     c.fill = 1;
/*  91 */     c.ipady = 0;
/*  92 */     c.gridx = 0; c.gridy = (gridY++);
/*  93 */     c.fill = 2;
/*  94 */     c.gridwidth = 0;
/*  95 */     container.add(this.controlPanel, c);
/*     */ 
/*  97 */     setContentPane(container);
/*     */ 
/*  99 */     pack();
/*     */ 
/* 101 */     setDefaultCloseOperation(2);
/* 102 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 103 */     setModal(true);
/* 104 */     setResizable(false);
/*     */   }
/*     */ 
/*     */   private void initAlertPanel()
/*     */   {
/* 109 */     this.alerterPanel = new JPanel();
/* 110 */     this.alerterPanel.setLayout(new BoxLayout(this.alerterPanel, 1));
/*     */ 
/* 112 */     boolean isMsgHeader = !"".equalsIgnoreCase(LocalizationManager.getText(this.discTitle));
/* 113 */     int topInset = 5;
/*     */ 
/* 115 */     if (isMsgHeader) {
/* 116 */       JLocalizableLabel header = new JLocalizableLabel(this.discTitle);
/* 117 */       header.setHorizontalAlignment(0);
/* 118 */       header.setBackground(Color.black);
/* 119 */       header.setFont(new Font("ARIAL", 1, 14));
/*     */ 
/* 121 */       JPanel headerPanel = new JPanel(new BorderLayout());
/* 122 */       headerPanel.setAlignmentX(0.0F);
/*     */ 
/* 124 */       if (this.discLogo != null) {
/* 125 */         JLabel logo = new JLabel(this.discLogo);
/* 126 */         logo.setHorizontalAlignment(2);
/* 127 */         headerPanel.add(logo, "North");
/* 128 */         topInset = 10;
/*     */       }
/*     */ 
/* 131 */       headerPanel.add(header, "Center");
/* 132 */       headerPanel.setBorder(BorderFactory.createEmptyBorder(topInset, 0, 5, 0));
/*     */ 
/* 134 */       this.alerterPanel.add(headerPanel);
/*     */     }
/*     */ 
/* 137 */     this.alerterPanel.add(new JLocalizableLabel(this.message, this.msgParams));
/*     */   }
/*     */ 
/*     */   protected void initCheckPanel()
/*     */   {
/* 142 */     this.checkPanel = new JPanel();
/* 143 */     GridLayout gl = new GridLayout(1, 2);
/* 144 */     gl.setHgap(5);
/* 145 */     this.checkPanel.setLayout(gl);
/*     */ 
/* 147 */     this.continueDiclaimingCheck = new JLocalizableCheckBox("disclaimer.checbox.message");
/* 148 */     this.continueDiclaimingCheck.setSelected(false);
/* 149 */     this.checkPanel.add(this.continueDiclaimingCheck);
/*     */ 
/* 151 */     this.checkPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 3, 0));
/*     */   }
/*     */ 
/*     */   private void initControlPanel() {
/* 155 */     this.controlPanel = new JPanel();
/* 156 */     this.agreeButton = new JLocalizableButton("disclaimer.button.agree");
/* 157 */     this.notAgreeButton = new JLocalizableButton("disclaimer.button.not.agree");
/* 158 */     this.closeButton = new JLocalizableButton("disclaimer.button.close");
/*     */ 
/* 160 */     this.agreeButton.setPreferredSize(BUTTON_SIZE);
/* 161 */     this.notAgreeButton.setPreferredSize(BUTTON_SIZE);
/* 162 */     this.closeButton.setPreferredSize(BUTTON_SIZE);
/*     */ 
/* 164 */     this.closeButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 166 */         DisclaimerMessageDialog.this.dispose();
/*     */       }
/*     */     });
/* 169 */     this.controlPanel = new JPanel(new BorderLayout())
/*     */     {
/*     */     };
/* 178 */     this.controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 3, 0));
/*     */   }
/*     */ 
/*     */   private void editControlPanel()
/*     */   {
/* 183 */     this.closeButton.setVisible(false);
/* 184 */     if (isTempAccepted()) {
/* 185 */       if (this.checkPanel != null) this.checkPanel.setVisible(true);
/* 186 */       if (this.checkPanel != null) this.continueDiclaimingCheck.setSelected(false);
/* 187 */       this.agreeButton.setVisible(true);
/* 188 */       this.notAgreeButton.setVisible(true);
/* 189 */       this.closeButton.setVisible(false);
/*     */     }
/* 191 */     if (isPermAccepted()) {
/* 192 */       if (this.checkPanel != null) this.checkPanel.setVisible(false);
/* 193 */       this.agreeButton.setVisible(false);
/* 194 */       this.notAgreeButton.setVisible(false);
/* 195 */       this.closeButton.setVisible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void showDisclaimer() {
/* 200 */     editControlPanel();
/* 201 */     setVisible(true);
/* 202 */     repaint();
/*     */   }
/*     */   protected void addAgreeBtnActionListener(ActionListener listener) {
/* 205 */     this.agreeButton.addActionListener(listener);
/*     */   }
/*     */   protected void addNotAgreeBtnActionListener(ActionListener listener) {
/* 208 */     this.notAgreeButton.addActionListener(listener);
/*     */   }
/*     */   protected JPanel getCheckPanel() {
/* 211 */     return this.checkPanel;
/*     */   }
/*     */ 
/*     */   public boolean isContinueDiclaimingSelected() {
/* 215 */     if (this.checkPanel != null) {
/* 216 */       return this.continueDiclaimingCheck.isSelected();
/*     */     }
/* 218 */     return false;
/*     */   }
/*     */ 
/*     */   public abstract boolean isPermAccepted();
/*     */ 
/*     */   public abstract boolean isTempAccepted();
/*     */ 
/*     */   public boolean isAccepted()
/*     */   {
/* 230 */     return (isTempAccepted()) || (isPermAccepted());
/*     */   }
/*     */ 
/*     */   public abstract void showDialog();
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.DisclaimerMessageDialog
 * JD-Core Version:    0.6.0
 */