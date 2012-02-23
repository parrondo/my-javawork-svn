/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class FullAccessWarningDialog extends JDialog
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(FullAccessWarningDialog.class);
/*     */ 
/*  28 */   private boolean accepted = false;
/*     */ 
/*     */   public FullAccessWarningDialog(JFrame owner) {
/*  31 */     super(owner, true);
/*  32 */     setDefaultCloseOperation(2);
/*  33 */     String iconFile = "rc/media/warning.gif";
/*     */     try
/*     */     {
/*  36 */       setIconImage(StratUtils.loadImage(iconFile));
/*     */     } catch (Exception e) {
/*  38 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*  40 */     setTitle("   SECURITY WARNING!    PLEASE READ CAREFULLY.");
/*     */ 
/*  42 */     JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
/*  43 */     mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  44 */     JLabel warningLabel = new JLabel("Please read and accept this agreement:");
/*  45 */     mainPanel.add(warningLabel, "North");
/*     */ 
/*  48 */     JEditorPane warning = new JEditorPane();
/*  49 */     warning.setContentType("text/html");
/*  50 */     warning.setText(getWarningText());
/*     */ 
/*  55 */     warning.setEditable(false);
/*  56 */     mainPanel.add(new JScrollPane(warning), "Center");
/*  57 */     JPanel buttons = new JPanel(new FlowLayout(2, 5, 5));
/*  58 */     JButton acceptButton = new JButton("Accept");
/*  59 */     acceptButton.setFocusable(false);
/*  60 */     JButton cancelButton = new JButton("Cancel");
/*  61 */     cancelButton.setFocusable(false);
/*     */ 
/*  63 */     acceptButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  65 */         FullAccessWarningDialog.access$002(FullAccessWarningDialog.this, true);
/*  66 */         FullAccessWarningDialog.this.dispose();
/*     */       }
/*     */     });
/*  69 */     cancelButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  71 */         FullAccessWarningDialog.access$002(FullAccessWarningDialog.this, false);
/*  72 */         FullAccessWarningDialog.this.dispose();
/*     */       }
/*     */     });
/*  75 */     buttons.add(acceptButton);
/*  76 */     buttons.add(cancelButton);
/*  77 */     mainPanel.add(buttons, "South");
/*  78 */     setContentPane(mainPanel);
/*     */ 
/*  80 */     pack();
/*  81 */     setSize(600, 420);
/*  82 */     setLocationRelativeTo(owner);
/*  83 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private String getWarningText()
/*     */   {
/*  90 */     return "<html><font face=\"Dialog\"><b>WARNING !</b><br>You are going to run a strategy that has full access to the local computer and files.Such a strategy has the privilege to upload any file, executable or not, from the local computer and beyond.Such a privilege can potentially be harmful and must be used cautiously.You should only run such a strategy if you get it from a trusted source; you should not run such a strategy if you are in doubt about the potential consequences of using it.Among others, potential undesirable effects include accessing, reading, writing on strategies from unauthorized sources. Malicious and virus codes may be operated from the local computer and files, and affect the strategies coded. In none instance shall Dukascopy take, nor endorse any responsibility for any consequence arising from the use of this function.</font></html>";
/*     */   }
/*     */ 
/*     */   public boolean isAccepted()
/*     */   {
/* 103 */     return this.accepted;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.FullAccessWarningDialog
 * JD-Core Version:    0.6.0
 */