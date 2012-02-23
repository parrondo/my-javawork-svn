/*     */ package com.dukascopy.dds2.greed.connector;
/*     */ 
/*     */ import com.dukascopy.api.connector.IBox;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ 
/*     */ public class MQLAlertDialog extends JDialog
/*     */   implements ActionListener
/*     */ {
/*  54 */   private JPanel myPanel = null;
/*  55 */   private JButton okButton = null;
/*     */ 
/*  57 */   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
/*     */ 
/*  59 */   private DataContainer listModel = null;
/*     */ 
/*  61 */   private JLabel frontLabel = new JLabel("");
/*  62 */   private JList jlist = null;
/*     */ 
/*     */   public MQLAlertDialog(IBox box, String title)
/*     */   {
/*  67 */     super((JFrame)null, false);
/*     */ 
/*  70 */     this.myPanel = new JPanel(new FlowLayout(1));
/*  71 */     getContentPane().add(this.myPanel);
/*  72 */     setTitle(" Alert form " + title);
/*     */ 
/*  74 */     JPanel labelPanel = new JPanel(new BorderLayout(10, 10));
/*     */ 
/* 109 */     labelPanel.add(this.frontLabel, "Center");
/* 110 */     labelPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 120), 1));
/* 111 */     labelPanel.setPreferredSize(new Dimension(380, 100));
/* 112 */     this.myPanel.add(labelPanel);
/*     */ 
/* 117 */     this.listModel = new DataContainer();
/* 118 */     this.jlist = new JList(this.listModel);
/* 119 */     this.jlist.setSelectionMode(0);
/* 120 */     this.jlist.setVisibleRowCount(5);
/* 121 */     this.jlist.addListSelectionListener(new ListSelectionListener()
/*     */     {
/*     */       public void valueChanged(ListSelectionEvent e)
/*     */       {
/* 125 */         int index = e.getFirstIndex();
/* 126 */         String string = (String)MQLAlertDialog.this.listModel.getElementAt(index);
/* 127 */         string = string.substring(19);
/* 128 */         string = string.replace("\n", "<br>");
/* 129 */         MQLAlertDialog.this.frontLabel.setText("<html><font face=\"Dialog\"><b>" + string + "</b></font></html>");
/*     */       }
/*     */     });
/* 133 */     JScrollPane scrollPane = new JScrollPane(this.jlist);
/* 134 */     scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 120), 1));
/* 135 */     scrollPane.setPreferredSize(new Dimension(380, 150));
/* 136 */     this.myPanel.add(scrollPane);
/* 137 */     this.okButton = new JButton("OK");
/* 138 */     this.okButton.addActionListener(this);
/* 139 */     this.myPanel.add(this.okButton);
/* 140 */     pack();
/* 141 */     int WIDTH = 400;
/* 142 */     int HEIGHT = 320;
/* 143 */     setSize(WIDTH, HEIGHT);
/* 144 */     Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
/* 145 */     setLocation((screenDim.width - WIDTH) / 2, (screenDim.height - HEIGHT) / 2);
/*     */   }
/*     */ 
/*     */   public void show(long time, String message)
/*     */   {
/* 152 */     this.listModel.addString(this.simpleDateFormat.format(new Date(time)) + " | " + message);
/* 153 */     this.jlist.setSelectionInterval(0, 0);
/* 154 */     String string = message;
/*     */ 
/* 156 */     string = string.replace("\n", "<br>");
/*     */ 
/* 158 */     this.frontLabel.setText("<html><font face=\"Dialog\"><b>" + string + "</b></font></html>");
/* 159 */     if (!isVisible())
/* 160 */       setVisible(true);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 165 */     if (this.okButton == e.getSource())
/*     */     {
/* 167 */       setVisible(false);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.MQLAlertDialog
 * JD-Core Version:    0.6.0
 */