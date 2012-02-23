/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.charts.utils.file.filter.CsvFileFilter;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class SaveMessagesPanel extends JPanel
/*     */ {
/*  46 */   private static final Logger LOGGER = LoggerFactory.getLogger(SaveMessagesPanel.class);
/*     */   private static final String DEFAULT_MESSAGES_FILE_NAME = "tester-messages.csv";
/*  50 */   private JLocalizableCheckBox chbSaveToFile = new JLocalizableCheckBox("label.save.messages.to.file");
/*  51 */   private JTextField txtSaveToFile = new JTextField(20);
/*  52 */   private JButton btnBrowseFile = new JButton("...");
/*     */ 
/*  54 */   private File currentDir = FileSystemView.getFileSystemView().getDefaultDirectory();
/*  55 */   private int lastSelectedOption = 0;
/*     */   private File commitedFileName;
/*     */   private boolean commitedAppendMessages;
/*     */   private boolean saveEnabled;
/*     */ 
/*     */   public SaveMessagesPanel()
/*     */   {
/*  66 */     initUI();
/*     */   }
/*     */ 
/*     */   private void initUI()
/*     */   {
/*  73 */     this.chbSaveToFile.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/*  78 */         if (SaveMessagesPanel.this.isSaveMessagesToFile()) {
/*  79 */           String file = SaveMessagesPanel.this.getMessagesFilePath();
/*  80 */           if (file.length() < 1)
/*  81 */             SaveMessagesPanel.this.setMessagesFilePath(new File(SaveMessagesPanel.this.currentDir, "tester-messages.csv").getAbsolutePath());
/*     */         }
/*     */       }
/*     */     });
/*  86 */     this.btnBrowseFile.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  89 */         SaveMessagesPanel.this.browseFileName();
/*     */       }
/*     */     });
/*  93 */     setLayout(new GridBagLayout());
/*  94 */     add(this.chbSaveToFile, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
/*     */ 
/*  97 */     add(this.txtSaveToFile, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 5, 0, 0), 0, 0));
/*     */ 
/* 100 */     add(this.btnBrowseFile, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 5, 0, 0), 0, 0));
/*     */   }
/*     */ 
/*     */   private void browseFileName()
/*     */   {
/* 110 */     File currentFile = getMessagesFileImpl();
/* 111 */     if (currentFile == null) {
/* 112 */       currentFile = new File(this.currentDir, "tester-messages.csv");
/*     */     }
/*     */ 
/* 115 */     File file = DCFileChooser.saveFile(currentFile.getParent(), currentFile, new CsvFileFilter());
/* 116 */     if (file != null) {
/* 117 */       this.currentDir = file.getParentFile();
/* 118 */       setMessagesFilePath(file.getAbsolutePath());
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getMessagesFilePath()
/*     */   {
/* 127 */     return this.txtSaveToFile.getText().trim();
/*     */   }
/*     */ 
/*     */   private void setMessagesFilePath(String path) {
/* 131 */     this.txtSaveToFile.setText(path);
/*     */   }
/*     */ 
/*     */   private boolean isMessagesFileNameValid() {
/* 135 */     String path = getMessagesFilePath();
/* 136 */     if (path.length() > 0)
/*     */     {
/* 139 */       File file = new File(path);
/* 140 */       if (!file.exists())
/* 141 */         return true;
/*     */       try
/*     */       {
/* 144 */         return file.canWrite();
/*     */       } catch (SecurityException ex) {
/* 146 */         LOGGER.debug("Application cannot write to file " + path, ex);
/* 147 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 152 */     return false;
/*     */   }
/*     */ 
/*     */   protected void enableControls(boolean enable)
/*     */   {
/* 157 */     this.chbSaveToFile.setEnabled((this.saveEnabled) && (enable));
/* 158 */     this.txtSaveToFile.setEnabled(enable);
/* 159 */     this.btnBrowseFile.setEnabled(enable);
/*     */   }
/*     */ 
/*     */   public void addSaveToFileListener(ItemListener listener) {
/* 163 */     this.chbSaveToFile.addItemListener(listener);
/*     */   }
/*     */ 
/*     */   public void removeSaveToFileListener(ItemListener listener) {
/* 167 */     this.chbSaveToFile.removeItemListener(listener);
/*     */   }
/*     */ 
/*     */   public void setSaveEnabled(boolean enabled) {
/* 171 */     this.saveEnabled = enabled;
/* 172 */     if (this.txtSaveToFile.isEnabled())
/* 173 */       this.chbSaveToFile.setEnabled(this.saveEnabled);
/*     */   }
/*     */ 
/*     */   public void set(StrategyTestBean strategyTestBean)
/*     */   {
/* 178 */     this.chbSaveToFile.setSelected(strategyTestBean.isSaveMessagesToFile());
/*     */ 
/* 180 */     String fileName = strategyTestBean.getMessagesFilePath();
/* 181 */     if (fileName != null) {
/* 182 */       File file = new File(fileName);
/* 183 */       setMessagesFilePath(file.getAbsolutePath());
/* 184 */       if (file.getParentFile() != null)
/* 185 */         this.currentDir = file.getParentFile();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save(StrategyTestBean strategyTestBean)
/*     */   {
/* 191 */     strategyTestBean.setSaveMessagesToFile(isSaveMessagesToFile());
/* 192 */     File file = getMessagesFileImpl();
/* 193 */     strategyTestBean.setMessagesFilePath(file == null ? null : file.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   public boolean isSaveMessagesToFile() {
/* 197 */     return this.chbSaveToFile.isSelected();
/*     */   }
/*     */ 
/*     */   public void setSaveMessagesToFile(boolean saveToFile) {
/* 201 */     this.chbSaveToFile.setSelected(saveToFile);
/*     */   }
/*     */ 
/*     */   private File getMessagesFileImpl() {
/* 205 */     if (isMessagesFileNameValid()) {
/* 206 */       return new File(getMessagesFilePath());
/*     */     }
/* 208 */     return null;
/*     */   }
/*     */ 
/*     */   public File getMessagesFile()
/*     */   {
/* 213 */     return this.commitedFileName;
/*     */   }
/*     */ 
/*     */   public boolean appendMessagesFile() {
/* 217 */     return this.commitedAppendMessages;
/*     */   }
/*     */ 
/*     */   public boolean commit()
/*     */   {
/*     */     File messagesFile;
/*     */     boolean appendMessages;
/* 230 */     if (isSaveMessagesToFile()) {
/* 231 */       File messagesFile = getMessagesFileImpl();
/* 232 */       if (messagesFile == null) {
/* 233 */         JOptionPane.showMessageDialog(this, LocalizationManager.getText("joption.pane.invalid.file.name"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/* 234 */         return false;
/*     */       }
/*     */       boolean appendMessages;
/* 237 */       if (messagesFile.exists())
/*     */       {
/* 239 */         String message = MessageFormat.format(LocalizationManager.getText("label.append.overwrite.confirmation"), new Object[] { messagesFile.getName() });
/* 240 */         Object[] options = { LocalizationManager.getText("button.save.option.append"), LocalizationManager.getText("button.save.option.overwrite"), LocalizationManager.getText("button.cancel") };
/*     */ 
/* 246 */         int option = JOptionPane.showOptionDialog(this, message, LocalizationManager.getText("joption.pane.historical.tester"), 0, 3, null, options, options[this.lastSelectedOption]);
/* 247 */         if (option == 0)
/*     */         {
/* 249 */           boolean appendMessages = true;
/* 250 */           this.lastSelectedOption = option;
/* 251 */         } else if (option == 1)
/*     */         {
/* 253 */           boolean appendMessages = false;
/* 254 */           this.lastSelectedOption = option;
/*     */         }
/*     */         else {
/* 257 */           return false;
/*     */         }
/*     */       }
/*     */       else {
/* 261 */         boolean appendMessages = false;
/*     */ 
/* 264 */         messagesFile.getParentFile().mkdirs();
/* 265 */         if (!messagesFile.getParentFile().exists()) {
/* 266 */           String pattern = LocalizationManager.getText("joption.pane.invalid.dir");
/* 267 */           String message = MessageFormat.format(pattern, new Object[] { messagesFile.getAbsolutePath() });
/* 268 */           String title = LocalizationManager.getText("joption.pane.historical.tester");
/* 269 */           JOptionPane.showMessageDialog(this, message, title, 1);
/*     */ 
/* 271 */           return false;
/*     */         }
/*     */         try
/*     */         {
/* 275 */           messagesFile.createNewFile();
/*     */           try {
/* 277 */             FileOutputStream fos = new FileOutputStream(messagesFile);
/*     */             try {
/* 279 */               fos.write(new byte[] { 15, 17, 18 });
/*     */             } finally {
/* 281 */               fos.close();
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 286 */             messagesFile.delete();
/*     */           }
/*     */         } catch (IOException ex) {
/* 289 */           LOGGER.error("Error persisting messages.", ex);
/* 290 */           String pattern = LocalizationManager.getText("joption.pane.error.saving.messages");
/* 291 */           String message = MessageFormat.format(pattern, new Object[] { messagesFile.getAbsolutePath(), ex.getLocalizedMessage() });
/* 292 */           String title = LocalizationManager.getText("joption.pane.historical.tester");
/* 293 */           JOptionPane.showMessageDialog(this, message, title, 1);
/*     */ 
/* 295 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 301 */       messagesFile = null;
/* 302 */       appendMessages = false;
/*     */     }
/* 304 */     this.commitedFileName = messagesFile;
/* 305 */     this.commitedAppendMessages = appendMessages;
/* 306 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.SaveMessagesPanel
 * JD-Core Version:    0.6.0
 */