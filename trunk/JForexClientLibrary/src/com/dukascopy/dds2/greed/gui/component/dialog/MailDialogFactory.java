/*     */ package com.dukascopy.dds2.greed.gui.component.dialog;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.text.MessageFormat;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.Timer;
/*     */ 
/*     */ public class MailDialogFactory
/*     */ {
/*     */   private static final long COUNTDOWN_START_SHOWING = 180000L;
/*     */ 
/*     */   public static MailDialog createMailDialog(Component parent, String multiLineMessage, long ttl, String accountName, String stage)
/*     */   {
/* 148 */     MailDialog mailDialog = new MailDialog(parent, multiLineMessage, ttl, accountName, stage);
/* 149 */     return mailDialog;
/*     */   }
/*     */ 
/*     */   private static String formatCountdown(long ttl) {
/* 153 */     String result = "";
/* 154 */     long[] minutesAndSeconds = getMinutesAndSecondsFromMs(ttl);
/*     */ 
/* 156 */     if (minutesAndSeconds[0] == 0L)
/* 157 */       result = MessageFormat.format(LocalizationManager.getText("mail.second.countdown"), new Object[] { Long.valueOf(minutesAndSeconds[1]) });
/* 158 */     else if (ttl <= 180000L) {
/* 159 */       result = MessageFormat.format(LocalizationManager.getText("mail.first.countdown"), new Object[] { Long.valueOf(minutesAndSeconds[0]), Long.valueOf(minutesAndSeconds[1]) });
/*     */     }
/* 161 */     return result;
/*     */   }
/*     */ 
/*     */   public static long[] getMinutesAndSecondsFromMs(long milliseconds)
/*     */   {
/* 167 */     long[] result = new long[2];
/*     */ 
/* 169 */     long totalSeconds = milliseconds / 1000L;
/* 170 */     long minutes = totalSeconds / 60L;
/*     */ 
/* 172 */     long remainedSeconds = totalSeconds - minutes * 60L;
/*     */ 
/* 174 */     result[0] = minutes;
/* 175 */     result[1] = remainedSeconds;
/* 176 */     return result;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 180 */     Timer.setLogTimers(true);
/* 181 */     String message = "foo bar ##baz dsds bar##boom sdds boom##blah sds blah ##\",'][(){}##&#34;&#44;&#39;&#93;&#91;&#40;&#41;&#123;&#125;".replaceAll("##", "<br>");
/* 182 */     MailDialog mailDialog = createMailDialog(null, message, 181000L, "accountName", "stage");
/* 183 */     mailDialog.updateWithResult();
/*     */   }
/*     */ 
/*     */   public static class MailDialog
/*     */   {
/*     */     private JDialog mailDialog;
/*     */     private JOptionPane optionPane;
/*     */     private MailDialogFactory.MailPanel messagePanel;
/*     */     private Timer closeTimer;
/*     */ 
/*     */     public MailDialog(Component parent, String multiLineMessage, long ttl, String accountName, String stage)
/*     */     {
/*  99 */       this.messagePanel = new MailDialogFactory.MailPanel(multiLineMessage, ttl);
/*     */ 
/* 101 */       JOptionPane jop = new JOptionPane(this.messagePanel, 1);
/* 102 */       String title = LocalizationManager.getTextWithArguments("mail.dialog.title", new Object[] { accountName, stage });
/*     */ 
/* 104 */       JDialog dialog = jop.createDialog(parent, title);
/* 105 */       this.messagePanel.setDialogParent(dialog);
/*     */ 
/* 107 */       if (ttl > -1L) {
/* 108 */         int delay = (int)ttl;
/* 109 */         ActionListener dialogCloser = new ActionListener(dialog) {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 111 */             MailDialogFactory.MailDialog.this.messagePanel.stopTimer();
/* 112 */             this.val$dialog.dispose();
/*     */           }
/*     */         };
/* 116 */         this.closeTimer = new Timer(delay, dialogCloser);
/* 117 */         this.closeTimer.setRepeats(false);
/* 118 */         this.closeTimer.setInitialDelay(delay);
/* 119 */         this.closeTimer.start();
/*     */       }
/*     */ 
/* 122 */       dialog.setAlwaysOnTop(true);
/* 123 */       dialog.setDefaultCloseOperation(2);
/* 124 */       dialog.setVisible(true);
/*     */ 
/* 126 */       this.mailDialog = dialog;
/* 127 */       this.optionPane = jop;
/*     */     }
/*     */ 
/*     */     public Object getDialogValue() {
/* 131 */       return this.optionPane.getValue();
/*     */     }
/*     */ 
/*     */     public Object updateWithResult()
/*     */     {
/* 136 */       Object result = getDialogValue();
/*     */ 
/* 138 */       this.messagePanel.stopTimer();
/* 139 */       if (this.closeTimer != null) {
/* 140 */         this.closeTimer.stop();
/*     */       }
/* 142 */       this.mailDialog.dispose();
/* 143 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MailPanel extends JPanel
/*     */   {
/*     */     private JLabel countdownMessageLabel;
/*     */     private JDialog dialogParent;
/*     */     private Timer countdownTimer;
/*     */ 
/*     */     public void setDialogParent(JDialog dialogParent)
/*     */     {
/*  51 */       this.dialogParent = dialogParent;
/*     */     }
/*     */ 
/*     */     public void refresh() {
/*  55 */       if (this.dialogParent != null)
/*  56 */         this.dialogParent.pack();
/*     */     }
/*     */ 
/*     */     public void stopTimer()
/*     */     {
/*  61 */       if (this.countdownTimer != null)
/*  62 */         this.countdownTimer.stop();
/*     */     }
/*     */ 
/*     */     public MailPanel(String multiLineMessage, long ttl)
/*     */     {
/*  67 */       setLayout(new BoxLayout(this, 1));
/*  68 */       add(Box.createRigidArea(new Dimension(0, 10)));
/*  69 */       add(new JLabel("<html><p align=\"justify\">" + multiLineMessage + "</p></html>"));
/*     */ 
/*  71 */       setCountdownMessageLabel(new JLabel());
/*  72 */       if (ttl > -1L) {
/*  73 */         add(Box.createRigidArea(new Dimension(0, 25)));
/*  74 */         this.countdownMessageLabel.setText(MailDialogFactory.access$000(ttl));
/*  75 */         add(this.countdownMessageLabel);
/*     */ 
/*  77 */         MailDialogFactory.ActionListenerImplementation countdownUpdater = new MailDialogFactory.ActionListenerImplementation((int)ttl, this.countdownMessageLabel, this, null);
/*  78 */         this.countdownTimer = new Timer(1000, countdownUpdater);
/*  79 */         this.countdownTimer.setRepeats(true);
/*  80 */         this.countdownTimer.setInitialDelay(1000);
/*  81 */         this.countdownTimer.start();
/*     */       } else {
/*  83 */         add(Box.createRigidArea(new Dimension(0, 10)));
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setCountdownMessageLabel(JLabel countdownMessageLabel) {
/*  88 */       this.countdownMessageLabel = countdownMessageLabel;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ActionListenerImplementation
/*     */     implements ActionListener
/*     */   {
/*     */     private int delay;
/*     */     private final JLabel countdownLabel;
/*     */     private final MailDialogFactory.MailPanel mailPanel;
/*     */ 
/*     */     private ActionListenerImplementation(int delay, JLabel countdownMessage, MailDialogFactory.MailPanel mailPanel)
/*     */     {
/*  32 */       this.delay = delay;
/*  33 */       this.countdownLabel = countdownMessage;
/*  34 */       this.mailPanel = mailPanel;
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent evt) {
/*  38 */       this.delay -= 1000;
/*  39 */       this.countdownLabel.setText(MailDialogFactory.access$000(this.delay));
/*  40 */       this.mailPanel.refresh();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.MailDialogFactory
 * JD-Core Version:    0.6.0
 */