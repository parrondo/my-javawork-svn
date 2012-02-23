/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ParseFilenameFilter;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.util.Arrays;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ 
/*     */ public class FileParameterOptimizer extends AbstractLookupParameterOptimizer
/*     */ {
/*     */   private String fileType;
/*     */ 
/*     */   public FileParameterOptimizer(String fileType, boolean mandatory, boolean readOnly, File value)
/*     */   {
/*  53 */     super(new FileParameterField(fileType, mandatory), value, mandatory, readOnly);
/*  54 */     this.fileType = fileType;
/*     */   }
/*     */ 
/*     */   protected void setValue(Component mainComponent, Object value)
/*     */   {
/*  59 */     ((FileParameterField)mainComponent).setValue(value);
/*     */   }
/*     */ 
/*     */   protected Object getValue(Component mainComponent)
/*     */   {
/*  64 */     return ((FileParameterField)mainComponent).getValue();
/*     */   }
/*     */ 
/*     */   protected String valueToString(Object value)
/*     */   {
/*  69 */     return ((FileParameterField)this.mainComponent).valueToString(value);
/*     */   }
/*     */ 
/*     */   protected void validateValue(Component mainComponent) throws CommitErrorException
/*     */   {
/*  74 */     ((FileParameterField)mainComponent).validateValue();
/*     */   }
/*     */ 
/*     */   protected Object[] showDialog(Component parent, Object[] values)
/*     */   {
/*  79 */     FileParameterField field = (FileParameterField)this.mainComponent;
/*  80 */     if (field.isComboBox()) {
/*  81 */       return showDialogWithList(parent, field.getAllFiles(), values);
/*     */     }
/*  83 */     return showDialogWithLookup(parent, values);
/*     */   }
/*     */ 
/*     */   private Object[] showDialogWithLookup(Component parent, Object[] values)
/*     */   {
/*  89 */     AbstractParameterOptimizerDialog dialog = new AbstractParameterOptimizerDialog(parent, "optimizer.dialog.select.elements.title", new FileParameterField(this.fileType, false))
/*     */     {
/*     */       protected File getValue(FileParameterOptimizer.FileParameterField editor)
/*     */       {
/*  93 */         return editor.getValue();
/*     */       }
/*     */ 
/*     */       String getValueAsString(File value)
/*     */       {
/*  98 */         return FileParameterOptimizer.this.valueToString(value);
/*     */       }
/*     */     };
/*     */     Object[] result;
/*     */     Object[] result;
/* 102 */     if (values == null) {
/* 103 */       result = dialog.showModal(null);
/*     */     } else {
/* 105 */       File[] elements = new File[values.length];
/* 106 */       System.arraycopy(values, 0, elements, 0, values.length);
/* 107 */       result = dialog.showModal(elements);
/*     */     }
/* 109 */     return result;
/*     */   }
/*     */ 
/*     */   private Object[] showDialogWithList(Component parent, Object[] allElements, Object[] selected)
/*     */   {
/* 114 */     Window window = SwingUtilities.getWindowAncestor(parent);
/*     */ 
/* 116 */     AbstractArrayParameterDialog dialog = new AbstractArrayParameterDialog(window, allElements)
/*     */     {
/*     */       protected Object getValueAsString(Object object) {
/* 119 */         return FileParameterOptimizer.this.valueToString(object);
/*     */       }
/*     */     };
/* 122 */     dialog.setModal(true);
/* 123 */     dialog.setTitle(LocalizationManager.getText("optimizer.dialog.select.elements.title"));
/* 124 */     dialog.pack();
/* 125 */     dialog.setLocationRelativeTo(parent);
/* 126 */     return dialog.showModal(selected);
/*     */   }
/*     */ 
/*     */   private static class FileParameterField extends JPanel
/*     */   {
/*     */     private static final String FILE_NOT_SELECTED = "NOT SELECTED";
/*     */     private static File lastSelectedFile;
/*     */     private JTextField txtFileName;
/*     */     private JComboBox cmbFiles;
/*     */     private boolean mandatory;
/*     */ 
/*     */     FileParameterField(String fileType, boolean mandatory)
/*     */     {
/* 179 */       this.mandatory = mandatory;
/* 180 */       if ((fileType == null) || (fileType.trim().isEmpty())) {
/* 181 */         this.txtFileName = new JTextField();
/* 182 */         this.txtFileName.setColumns(15);
/*     */ 
/* 184 */         int height = this.txtFileName.getPreferredSize().height;
/*     */ 
/* 186 */         JButton fileSelectButton = new JButton("...");
/* 187 */         fileSelectButton.setMargin(new Insets(0, 0, 0, 0));
/* 188 */         fileSelectButton.setPreferredSize(new Dimension(height, height));
/* 189 */         fileSelectButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 193 */             File selectedFile = FileParameterOptimizer.FileParameterField.this.getValue();
/*     */             DCFileChooser fc;
/*     */             DCFileChooser fc;
/* 195 */             if (selectedFile != null) {
/* 196 */               String path = selectedFile.getParent();
/*     */               DCFileChooser fc;
/* 197 */               if (path != null) {
/* 198 */                 fc = DCFileChooser.createDCFileChooser(path, selectedFile);
/*     */               } else {
/* 200 */                 path = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
/* 201 */                 fc = DCFileChooser.createDCFileChooser(path, selectedFile);
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/*     */               DCFileChooser fc;
/* 203 */               if (FileParameterOptimizer.FileParameterField.lastSelectedFile != null) {
/* 204 */                 fc = DCFileChooser.createDCFileChooser(FileParameterOptimizer.FileParameterField.lastSelectedFile.getParent(), null);
/*     */               } else {
/* 206 */                 String path = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
/* 207 */                 fc = DCFileChooser.createDCFileChooser(path, null);
/*     */               }
/*     */             }
/* 210 */             if (fc.showOpenDialog(FileParameterOptimizer.FileParameterField.this) == 0) {
/* 211 */               File file = fc.getSelectedFile();
/* 212 */               FileParameterOptimizer.FileParameterField.access$002(file);
/* 213 */               FileParameterOptimizer.FileParameterField.this.setValue(file);
/*     */             }
/*     */           }
/*     */         });
/* 218 */         setLayout(new BorderLayout(0, 0));
/* 219 */         add(this.txtFileName, "Center");
/* 220 */         add(fileSelectButton, "East");
/*     */       }
/*     */       else {
/* 223 */         File strategyDir = FilePathManager.getInstance().getFilesForStrategiesDir();
/* 224 */         FilenameFilter parseFilter = new ParseFilenameFilter(fileType);
/* 225 */         File[] filteredFileNames = strategyDir.listFiles(parseFilter);
/* 226 */         Arrays.sort(filteredFileNames);
/*     */ 
/* 228 */         this.cmbFiles = new JComboBox(filteredFileNames);
/* 229 */         this.cmbFiles.addItemListener(new Object()
/*     */         {
/*     */           public void itemStateChanged(ItemEvent e) {
/* 232 */             JComboBox combo = (JComboBox)e.getItemSelectable();
/* 233 */             Object selected = combo.getSelectedItem();
/* 234 */             if ((selected instanceof File))
/* 235 */               combo.setForeground(UIManager.getColor("ComboBox.foreground"));
/*     */             else
/* 237 */               combo.setForeground(Color.GRAY);
/*     */           }
/*     */         });
/* 241 */         this.cmbFiles.setEditable(false);
/* 242 */         this.cmbFiles.setRenderer(new FileParameterOptimizer.FileComboboxRenderer());
/*     */ 
/* 244 */         setLayout(new BorderLayout());
/* 245 */         add(this.cmbFiles, "North");
/*     */       }
/*     */ 
/* 248 */       if ((!mandatory) && (this.cmbFiles != null))
/* 249 */         this.cmbFiles.insertItemAt("NOT SELECTED", 0);
/*     */     }
/*     */ 
/*     */     public File[] getAllFiles()
/*     */     {
/* 254 */       File[] result = new File[this.cmbFiles.getItemCount()];
/* 255 */       for (int i = 0; i < result.length; i++) {
/* 256 */         result[i] = ((File)this.cmbFiles.getItemAt(i));
/*     */       }
/* 258 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean isComboBox() {
/* 262 */       return this.cmbFiles != null;
/*     */     }
/*     */ 
/*     */     public void validateValue() throws CommitErrorException {
/* 266 */       if (this.txtFileName != null) {
/* 267 */         if ((this.mandatory) && 
/* 268 */           (this.txtFileName.getText().trim().length() < 1)) {
/* 269 */           throw new CommitErrorException("optimizer.dialog.error.file.name.must.be.specified");
/*     */         }
/*     */ 
/*     */       }
/* 273 */       else if ((this.cmbFiles != null) && 
/* 274 */         (this.mandatory)) {
/* 275 */         Object selected = this.cmbFiles.getSelectedItem();
/* 276 */         if (!(selected instanceof File))
/* 277 */           throw new CommitErrorException("optimizer.dialog.error.file.must.be.selected");
/*     */       }
/*     */     }
/*     */ 
/*     */     String valueToString(Object value)
/*     */     {
/* 284 */       File file = (File)value;
/* 285 */       if (this.txtFileName != null) {
/* 286 */         return value == null ? "" : file.getPath();
/*     */       }
/* 288 */       return value == null ? "" : file.getName();
/*     */     }
/*     */ 
/*     */     void setValue(Object value)
/*     */     {
/* 293 */       if (this.txtFileName != null) {
/* 294 */         this.txtFileName.setText(valueToString(value));
/*     */       }
/* 296 */       else if (this.cmbFiles != null)
/* 297 */         if (value != null)
/* 298 */           this.cmbFiles.setSelectedItem(value);
/*     */         else
/* 300 */           this.cmbFiles.setSelectedIndex(-1);
/*     */     }
/*     */ 
/*     */     File getValue()
/*     */     {
/* 306 */       if (this.txtFileName != null) {
/* 307 */         String path = this.txtFileName.getText().trim();
/* 308 */         if (path.length() < 1) {
/* 309 */           return null;
/*     */         }
/* 311 */         return new File(path);
/*     */       }
/*     */ 
/* 314 */       if (this.cmbFiles != null) {
/* 315 */         Object selected = this.cmbFiles.getSelectedItem();
/* 316 */         if ((selected instanceof File)) {
/* 317 */           return (File)selected;
/*     */         }
/* 319 */         return null;
/*     */       }
/*     */ 
/* 323 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FileComboboxRenderer extends JLabel
/*     */     implements ListCellRenderer
/*     */   {
/*     */     FileComboboxRenderer()
/*     */     {
/* 134 */       setOpaque(true);
/* 135 */       setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
/*     */     }
/*     */ 
/*     */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */     {
/*     */       String text;
/*     */       String text;
/* 141 */       if ((value instanceof File))
/* 142 */         text = ((File)value).getName();
/*     */       else {
/* 144 */         text = LocalizationManager.getText("optimizer.file.not.selected");
/*     */       }
/*     */ 
/* 147 */       if (isSelected) {
/* 148 */         setBackground(list.getSelectionBackground());
/* 149 */         setForeground(list.getSelectionForeground());
/*     */       }
/*     */       else {
/* 152 */         setBackground(list.getBackground());
/* 153 */         if ((value instanceof File))
/* 154 */           setForeground(list.getForeground());
/*     */         else {
/* 156 */           setForeground(Color.GRAY);
/*     */         }
/*     */       }
/*     */ 
/* 160 */       setFont(list.getFont());
/* 161 */       setText(text);
/* 162 */       return this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.FileParameterOptimizer
 * JD-Core Version:    0.6.0
 */