/*      */ package com.dukascopy.dds2.greed.agent.strategy;
/*      */ 
/*      */ import com.dukascopy.api.Configurable;
/*      */ import com.dukascopy.api.IChart;
/*      */ import com.dukascopy.api.IStrategy;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.connector.IConnectorManager;
/*      */ import com.dukascopy.api.connector.IConnectorManager.ConnectorKeys;
/*      */ import com.dukascopy.api.impl.execution.IControlUI;
/*      */ import com.dukascopy.api.impl.execution.Task;
/*      */ import com.dukascopy.api.impl.execution.TaskParameter;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.params.Preset;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.params.PresetsModel;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.params.XMLPresets;
/*      */ import com.dukascopy.dds2.greed.connector.ConnectorManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.DoubleSpinnerModel;
/*      */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*      */ import com.toedter.calendar.IDateEditor;
/*      */ import com.toedter.calendar.JDateChooser;
/*      */ import com.toedter.calendar.JSpinnerDateEditor;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Insets;
/*      */ import java.awt.LayoutManager;
/*      */ import java.awt.Point;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.event.MouseWheelListener;
/*      */ import java.beans.PropertyChangeEvent;
/*      */ import java.beans.PropertyChangeListener;
/*      */ import java.io.File;
/*      */ import java.io.FilenameFilter;
/*      */ import java.lang.reflect.Field;
/*      */ import java.text.MessageFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.Callable;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.FutureTask;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.ComboBoxEditor;
/*      */ import javax.swing.ComboBoxModel;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSpinner;
/*      */ import javax.swing.JSpinner.DefaultEditor;
/*      */ import javax.swing.JSpinner.NumberEditor;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.RepaintManager;
/*      */ import javax.swing.SpinnerModel;
/*      */ import javax.swing.SpinnerNumberModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.DocumentEvent;
/*      */ import javax.swing.event.DocumentListener;
/*      */ import javax.swing.text.Document;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class ParametersPanel extends JPanel
/*      */   implements ActionListener
/*      */ {
/*   92 */   private static final Logger LOGGER = LoggerFactory.getLogger(ParametersPanel.class);
/*      */   public static final String DIMENSION_PROPERTY_KEY = "com.dukascopy.ParametersDialog.size";
/*      */   private static final String DIALOG_ICON_FILE = "rc/media/logo_empty_titlebar.png";
/*      */   private static final String PRESET_SAVE_ICON_FILE = "rc/media/dialog_strategy_save_preset_active.png";
/*      */   private static final String PRESET_DELETE_ICON_FILE = "rc/media/dialog_strategy_delete_preset_active.png";
/*      */   private static final String FORMAT_INT = "#";
/*      */   private static final String FORMAT_LONG = "#";
/*      */   private static final String FORMAT_DOUBLE = "0.0###################";
/*      */   private static final int FLUSH_THREAD_SLEEP_TIME = 300;
/*  123 */   private static final JLabel PRESET_LABEL = new JLabel("<html><b>Preset</b></html>");
/*  124 */   private static final JLabel VALUE_LABEL = new JLabel("<html><b>Value</b></html>");
/*  125 */   private static final JLabel VARIABLE_LABEL = new JLabel("<html><b>Variable</b></html>");
/*      */   private static final String CLIENT_PROPERTY_TYPE = "type";
/*      */   private static final String CLIENT_PROPERTY_DATE_AS_LONG = "dateAsLong";
/*      */   private static final String CLIENT_PROPERTY_DEFAULT = "default";
/*      */   private static final String CLIENT_PROPERTY_FIELD_NAME = "fieldName";
/*      */   private static final String CLIENT_PROPERTY_FIELD_TITLE = "fieldTitle";
/*      */   private static final String CLIENT_PROPERTY_STEP_SIZE = "stepSize";
/*      */   private static final String CLIENT_PROPERTY_FILE_TYPE = "fileType";
/*      */   private static final String CLIENT_PROPERTY_FIELD = "field";
/*      */   private static final String CLIENT_PROPERTY_OBLIGATORY = "obligatory";
/*      */   private static final String PRESET_DEFAULT_NAME = "Default";
/*      */   private static final String FILE_NOT_SELECTED = "Not Selected";
/*  140 */   private static final Dimension DIALOG_SIZE = new Dimension(450, 450);
/*  141 */   private static final Dimension DEFAULT_FIELD_SIZE = new Dimension(150, 20);
/*  142 */   private static final Dimension DEFAULT_FIELD_LABELSIZE = new Dimension(150, 20);
/*      */   private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
/*  145 */   private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
/*      */ 
/*  147 */   private List<JComponent> componentsList = null;
/*      */   private final IStrategy plainTarget;
/*      */   private final boolean isEditDialog;
/*  152 */   private boolean isAnyParameterDetected = false;
/*      */ 
/*  154 */   private Task<?> returnCode = null;
/*      */ 
/*  156 */   private final List<String[]> values = new ArrayList();
/*      */   private JComboBox presetsCombo;
/*      */   private JButton deletePresetButton;
/*      */   private JButton savePresetButton;
/*  163 */   private boolean blockOtherOperations = false;
/*      */   private PresetsModel presetsModel;
/*  169 */   private File binaryFile = null;
/*      */   private final JDialog parentDialog;
/*      */ 
/*      */   public ParametersPanel(JDialog parentDialog, List<StrategyRunParameter> params, boolean isEditDialog)
/*      */   {
/*  174 */     this.plainTarget = null;
/*  175 */     this.isEditDialog = isEditDialog;
/*  176 */     this.parentDialog = parentDialog;
/*  177 */     setIconImage();
/*      */ 
/*  179 */     setLayout(new BorderLayout());
/*  180 */     createParametersUI(this, params, new HashMap());
/*      */   }
/*      */ 
/*      */   public ParametersPanel(JDialog parentDialog, IStrategy target, boolean isEditDialog, File binaryFile) {
/*  184 */     this.plainTarget = target;
/*  185 */     this.isEditDialog = isEditDialog;
/*  186 */     this.parentDialog = parentDialog;
/*  187 */     this.binaryFile = binaryFile;
/*  188 */     setIconImage();
/*  189 */     setLayout(new BorderLayout());
/*      */ 
/*  191 */     this.presetsModel = XMLPresets.loadModel(target, binaryFile);
/*      */ 
/*  193 */     if (this.presetsModel == null)
/*      */     {
/*  195 */       return;
/*      */     }
/*      */ 
/*  198 */     add(new JPanel(new BorderLayout())
/*      */     {
/*      */     }
/*      */     , "North");
/*      */ 
/*  208 */     List params = new LinkedList();
/*  209 */     Map fieldMap = new HashMap();
/*      */ 
/*  211 */     Field[] fields = this.plainTarget.getClass().getFields();
/*  212 */     for (Field field : fields) {
/*  213 */       Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*  214 */       if (configurable == null)
/*      */         continue;
/*  216 */       String name = field.getName();
/*  217 */       String uiName = configurable.value();
/*      */       try {
/*  219 */         StrategyRunParameter param = new StrategyRunParameter(name, field.getType(), field.get(target));
/*  220 */         param.setTitle(uiName);
/*  221 */         param.setDescription(!ObjectUtils.isNullOrEmpty(configurable.description()) ? configurable.description() : uiName);
/*  222 */         param.setMandatory(configurable.obligatory());
/*  223 */         param.setReadOnly(configurable.readOnly());
/*  224 */         param.setStepSize(configurable.stepSize());
/*  225 */         param.setFileType(configurable.fileType());
/*  226 */         param.setDateAsLong(configurable.datetimeAsLong());
/*      */ 
/*  228 */         params.add(param);
/*  229 */         fieldMap.put(name, field);
/*      */       }
/*      */       catch (IllegalArgumentException ex) {
/*  232 */         String message = MessageFormat.format("Error getting value from field {0} ({1}).", new Object[] { name, uiName });
/*  233 */         LOGGER.debug(message, ex);
/*      */       }
/*      */       catch (IllegalAccessException ex) {
/*  236 */         String message = MessageFormat.format("Error getting value from field {0} ({1}).", new Object[] { name, uiName });
/*  237 */         LOGGER.debug(message, ex);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  242 */     createParametersUI(this, params, fieldMap);
/*      */   }
/*      */ 
/*      */   public void saveModel() {
/*  246 */     XMLPresets.saveModel(this.plainTarget, this.presetsModel, this.binaryFile);
/*      */   }
/*      */ 
/*      */   private void createParametersUI(JPanel root, List<StrategyRunParameter> params, Map<String, Field> fields) {
/*  250 */     JPanel paramsPanel = createParamsPanel(params, fields);
/*      */ 
/*  252 */     if (!this.isAnyParameterDetected)
/*      */     {
/*  254 */       return;
/*      */     }
/*      */ 
/*  257 */     JScrollPane mainScrollPane = new JScrollPane(new JPanel(new BorderLayout(), paramsPanel)
/*      */     {
/*      */     }
/*      */     , 20, 30);
/*      */ 
/*  263 */     mainScrollPane.setBorder(new JRoundedBorder(mainScrollPane));
/*  264 */     root.add(Box.createHorizontalStrut(10), "West");
/*  265 */     root.add(Box.createHorizontalStrut(10), "East");
/*      */ 
/*  267 */     root.add(mainScrollPane, "Center");
/*  268 */     root.add(createButtonsPanel(), "South");
/*      */   }
/*      */ 
/*      */   private void setIconImage() {
/*  272 */     ImageIcon icon = null;
/*      */     try {
/*  274 */       icon = StratUtils.loadImageIcon("rc/media/logo_empty_titlebar.png");
/*      */     } catch (Exception e) {
/*  276 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/*  279 */     if (icon != null)
/*  280 */       this.parentDialog.setIconImage(icon.getImage());
/*      */   }
/*      */ 
/*      */   private JPanel createPresetsPanel()
/*      */   {
/*  285 */     JPanel panel = new JPanel(new GridBagLayout());
/*      */ 
/*  287 */     Arrays.sort(this.presetsModel.getAllPresets().toArray());
/*  288 */     this.presetsCombo = new JComboBox(this.presetsModel.getAllPresets().toArray());
/*  289 */     this.presetsCombo.setSelectedItem(this.presetsModel.getDefaultPreset());
/*  290 */     this.presetsCombo.setEditable(true);
/*      */ 
/*  292 */     this.presetsCombo.addItemListener(new ItemListener() {
/*      */       public void itemStateChanged(ItemEvent e) {
/*  294 */         if ((!ParametersPanel.this.blockOtherOperations) && 
/*  295 */           (e.getStateChange() == 1) && 
/*  296 */           ((ParametersPanel.this.presetsCombo.getSelectedItem() instanceof Preset)))
/*  297 */           ParametersPanel.this.setPreset();
/*      */       }
/*      */     });
/*  303 */     this.presetsCombo.addMouseWheelListener(new ComboboxMouseWheelListener(this.presetsCombo));
/*  304 */     this.presetsCombo.setPreferredSize(DEFAULT_FIELD_SIZE);
/*      */ 
/*  306 */     JTextField comboTextField = (JTextField)this.presetsCombo.getEditor().getEditorComponent();
/*      */ 
/*  308 */     comboTextField.getDocument().addDocumentListener(new DocumentListener()
/*      */     {
/*      */       public void changedUpdate(DocumentEvent e) {
/*  311 */         ParametersPanel.this.checkNewComboBoxText();
/*      */       }
/*      */       public void removeUpdate(DocumentEvent e) {
/*  314 */         ParametersPanel.this.checkNewComboBoxText();
/*      */       }
/*      */       public void insertUpdate(DocumentEvent e) {
/*  317 */         ParametersPanel.this.checkNewComboBoxText();
/*      */       }
/*      */     });
/*  322 */     ImageIcon iconDelete = StratUtils.loadImageIcon("rc/media/dialog_strategy_delete_preset_active.png");
/*  323 */     ImageIcon iconSave = StratUtils.loadImageIcon("rc/media/dialog_strategy_save_preset_active.png");
/*      */ 
/*  325 */     this.deletePresetButton = new JButton(iconDelete);
/*  326 */     this.savePresetButton = new JButton(iconSave);
/*      */ 
/*  328 */     this.deletePresetButton.setMargin(new Insets(0, 0, 0, 0));
/*  329 */     this.savePresetButton.setMargin(new Insets(0, 0, 0, 0));
/*      */ 
/*  331 */     this.deletePresetButton.setToolTipText(Action.PRESET_DELETE.getLabel());
/*  332 */     this.savePresetButton.setToolTipText(Action.PRESET_SAVE.getLabel());
/*      */ 
/*  334 */     this.deletePresetButton.setActionCommand(Action.PRESET_DELETE.name());
/*  335 */     this.savePresetButton.setActionCommand(Action.PRESET_SAVE.name());
/*      */ 
/*  337 */     this.deletePresetButton.addActionListener(this);
/*  338 */     this.savePresetButton.addActionListener(this);
/*      */ 
/*  340 */     if (this.presetsModel.getDefaultPreset().getName().equals("Default")) {
/*  341 */       this.deletePresetButton.setEnabled(false);
/*  342 */       this.savePresetButton.setEnabled(false);
/*      */     }
/*      */ 
/*  345 */     GridBagConstraints constraints = new GridBagConstraints();
/*      */ 
/*  347 */     constraints.insets = new Insets(10, 5, 5, 5);
/*  348 */     constraints.ipadx = 2;
/*  349 */     constraints.ipady = 2;
/*      */ 
/*  351 */     constraints.gridx = 0;
/*  352 */     constraints.gridy = 0;
/*  353 */     constraints.anchor = 13;
/*  354 */     panel.add(PRESET_LABEL, constraints);
/*      */ 
/*  356 */     constraints.gridx += 1;
/*  357 */     constraints.anchor = 17;
/*  358 */     panel.add(this.presetsCombo, constraints);
/*      */ 
/*  360 */     constraints.gridx += 1;
/*  361 */     constraints.insets = new Insets(10, 1, 5, 1);
/*  362 */     panel.add(this.savePresetButton, constraints);
/*      */ 
/*  364 */     constraints.gridx += 1;
/*  365 */     panel.add(this.deletePresetButton, constraints);
/*      */ 
/*  367 */     constraints.gridx = 0;
/*  368 */     constraints.gridy = 1;
/*  369 */     constraints.insets = new Insets(5, 5, 5, 5);
/*  370 */     constraints.anchor = 13;
/*  371 */     panel.add(VARIABLE_LABEL, constraints);
/*      */ 
/*  373 */     constraints.gridx += 1;
/*  374 */     constraints.anchor = 17;
/*  375 */     panel.add(VALUE_LABEL, constraints);
/*      */ 
/*  377 */     return panel;
/*      */   }
/*      */ 
/*      */   private JPanel createButtonsPanel() {
/*  381 */     JPanel panel = new JPanel(new GridBagLayout());
/*      */ 
/*  383 */     GridBagConstraints constraints = new GridBagConstraints();
/*      */ 
/*  385 */     constraints.insets = new Insets(5, 10, 10, 12);
/*  386 */     constraints.ipadx = 2;
/*  387 */     constraints.ipady = 2;
/*  388 */     constraints.gridx = 0;
/*  389 */     constraints.gridy = 0;
/*      */     JLocalizableButton runButton;
/*      */     JLocalizableButton runButton;
/*  392 */     if (this.isEditDialog)
/*  393 */       runButton = new JLocalizableButton("strategy.parameters.dialog.button.set");
/*      */     else {
/*  395 */       runButton = new JLocalizableButton("strategy.parameters.dialog.button.run");
/*      */     }
/*  397 */     runButton.addActionListener(this);
/*  398 */     runButton.setActionCommand(Action.RUN.name());
/*  399 */     runButton.setFocusCycleRoot(true);
/*      */ 
/*  401 */     this.parentDialog.getRootPane().setDefaultButton(runButton);
/*      */ 
/*  403 */     runButton.setPreferredSize(new Dimension(70, 20));
/*  404 */     panel.add(runButton, constraints);
/*      */ 
/*  406 */     constraints.gridx += 1;
/*  407 */     constraints.insets = new Insets(5, 12, 10, 12);
/*      */ 
/*  409 */     JLocalizableButton cancelButton = new JLocalizableButton("strategy.parameters.dialog.button.cancel");
/*  410 */     cancelButton.addActionListener(this);
/*  411 */     cancelButton.setActionCommand(Action.CANCEL.name());
/*  412 */     cancelButton.setPreferredSize(new Dimension(70, 20));
/*  413 */     panel.add(cancelButton, constraints);
/*      */ 
/*  415 */     return panel;
/*      */   }
/*      */ 
/*      */   private JPanel createParamsPanel(List<StrategyRunParameter> parameters, Map<String, Field> fields) {
/*  419 */     JPanel panel = new JPanel(new GridBagLayout());
/*      */ 
/*  421 */     GridBagConstraints gbc = new GridBagConstraints();
/*  422 */     gbc.insets = new Insets(2, 5, 2, 5);
/*  423 */     gbc.ipadx = 2;
/*  424 */     gbc.ipady = 2;
/*  425 */     gbc.gridy = 0;
/*  426 */     gbc.gridx = 0;
/*      */ 
/*  428 */     this.componentsList = new ArrayList();
/*      */ 
/*  430 */     for (StrategyRunParameter parameter : parameters) {
/*  431 */       this.isAnyParameterDetected = true;
/*  432 */       gbc.anchor = 13;
/*      */ 
/*  434 */       String fieldName = parameter.getName();
/*  435 */       String fieldTitle = parameter.getTitle();
/*      */ 
/*  437 */       if ((fieldTitle == null) || (fieldTitle.isEmpty())) {
/*  438 */         fieldTitle = fieldName;
/*      */       }
/*  440 */       String description = parameter.getDescription();
/*  441 */       JLabel label = new JLabel(new StringBuilder().append("<html><font family=Verdana>").append(fieldTitle).append("</font>").append(parameter.isMandatory() ? "<sup><font family=Verdana color=Red>*</font></sup>" : "").append("</html>").toString(), null, 4);
/*  442 */       label.setPreferredSize(DEFAULT_FIELD_LABELSIZE);
/*  443 */       label.setMaximumSize(DEFAULT_FIELD_LABELSIZE);
/*  444 */       label.setToolTipText(description);
/*  445 */       JComponent component = createControl(this.plainTarget, parameter);
/*  446 */       component.setEnabled(!parameter.isReadOnly());
/*  447 */       if (component != null) {
/*  448 */         component.putClientProperty("fieldTitle", fieldTitle);
/*  449 */         component.putClientProperty("stepSize", Double.valueOf(parameter.getStepSize()));
/*  450 */         component.putClientProperty("fileType", parameter.getFileType());
/*      */ 
/*  452 */         gbc.gridy += 1;
/*  453 */         gbc.gridx = 0;
/*  454 */         panel.add(label, gbc);
/*      */ 
/*  456 */         if ((component instanceof JTextField)) {
/*  457 */           ((JTextField)component).getDocument().addDocumentListener(new DocumentListener()
/*      */           {
/*      */             public void changedUpdate(DocumentEvent e) {
/*  460 */               ParametersPanel.this.componentWasChanged();
/*      */             }
/*      */             public void removeUpdate(DocumentEvent e) {
/*  463 */               ParametersPanel.this.componentWasChanged();
/*      */             }
/*      */             public void insertUpdate(DocumentEvent e) {
/*  466 */               ParametersPanel.this.componentWasChanged();
/*      */             } } );
/*      */         }
/*  470 */         else if ((component instanceof JSpinner)) {
/*  471 */           ((JSpinner)component).addChangeListener(new ChangeListener() {
/*      */             public void stateChanged(ChangeEvent e) {
/*  473 */               ParametersPanel.this.componentWasChanged();
/*      */             } } );
/*  476 */         } else if ((component instanceof JComboBox)) {
/*  477 */           ((JComboBox)component).addItemListener(new ItemListener()
/*      */           {
/*      */             public void itemStateChanged(ItemEvent e) {
/*  480 */               if (e.getStateChange() == 1)
/*  481 */                 ParametersPanel.this.componentWasChanged();
/*      */             }
/*      */           });
/*  486 */           component.addMouseWheelListener(new ComboboxMouseWheelListener((JComboBox)component));
/*  487 */         } else if ((component instanceof JCheckBox)) {
/*  488 */           ((JCheckBox)component).addActionListener(new ActionListener()
/*      */           {
/*      */             public void actionPerformed(ActionEvent e) {
/*  491 */               ParametersPanel.this.componentWasChanged();
/*      */             } } );
/*      */         }
/*  495 */         else if ((component instanceof JDateChooser)) {
/*  496 */           ((JDateChooser)component).getDateEditor().addPropertyChangeListener(new PropertyChangeListener()
/*      */           {
/*      */             public void propertyChange(PropertyChangeEvent evt) {
/*  499 */               ParametersPanel.this.componentWasChanged();
/*      */             }
/*      */           });
/*      */         }
/*      */ 
/*  505 */         component.putClientProperty("field", fields.get(fieldName));
/*  506 */         component.putClientProperty("type", parameter.getVariable().getType());
/*  507 */         component.putClientProperty("fieldName", fieldName);
/*  508 */         component.putClientProperty("dateAsLong", Boolean.valueOf(parameter.isDateAsLong()));
/*  509 */         component.putClientProperty("obligatory", Boolean.valueOf(parameter.isMandatory()));
/*  510 */         this.componentsList.add(component);
/*      */ 
/*  512 */         if ((parameter.getVariable().getType().equals(File.class)) && 
/*  513 */           ((component instanceof JTextField)))
/*      */         {
/*  515 */           JPanel fieldPanel = new JPanel(new FlowLayout(0, 0, 0));
/*      */ 
/*  517 */           JButton fileSelectButton = new JButton("...");
/*  518 */           fileSelectButton.setMargin(new Insets(0, 0, 0, 0));
/*      */ 
/*  520 */           fileSelectButton.addActionListener(new FileSelectButtonActionListner(this.parentDialog, (JTextField)component));
/*      */ 
/*  522 */           fileSelectButton.setPreferredSize(new Dimension(20, 20));
/*      */ 
/*  524 */           fieldPanel.add(component);
/*  525 */           fieldPanel.add(Box.createHorizontalStrut(4));
/*  526 */           fieldPanel.add(fileSelectButton);
/*  527 */           fieldPanel.setPreferredSize(DEFAULT_FIELD_SIZE);
/*      */ 
/*  529 */           component = fieldPanel;
/*      */         }
/*      */ 
/*  533 */         gbc.gridx = 1;
/*  534 */         panel.add(component, gbc);
/*      */       }
/*      */     }
/*      */ 
/*  538 */     gbc.gridx = 0;
/*  539 */     gbc.gridy += 1;
/*  540 */     panel.add(new JLabel("\n"), gbc);
/*      */ 
/*  542 */     return panel;
/*      */   }
/*      */ 
/*      */   private void componentWasChanged() {
/*  546 */     if (this.presetsCombo == null) {
/*  547 */       return;
/*      */     }
/*      */ 
/*  550 */     Preset selectedPreset = this.presetsModel.getPreset(((JTextField)this.presetsCombo.getEditor().getEditorComponent()).getText());
/*  551 */     if ((selectedPreset == null) || (selectedPreset.getName().toLowerCase().equals("Default")) || (selectedPreset.getName().equals(""))) {
/*  552 */       return;
/*      */     }
/*      */ 
/*  555 */     if (!this.savePresetButton.isEnabled())
/*  556 */       this.savePresetButton.setEnabled(true);
/*      */   }
/*      */ 
/*      */   private void checkNewComboBoxText()
/*      */   {
/*  561 */     String text = ((JTextField)this.presetsCombo.getEditor().getEditorComponent()).getText();
/*      */ 
/*  563 */     if (("Default".equalsIgnoreCase(text)) || (text.trim().isEmpty())) {
/*  564 */       this.savePresetButton.setEnabled(false);
/*  565 */       this.deletePresetButton.setEnabled(false);
/*  566 */       return;
/*      */     }
/*      */ 
/*  569 */     if (!this.savePresetButton.isEnabled()) {
/*  570 */       this.savePresetButton.setEnabled(true);
/*      */     }
/*      */ 
/*  573 */     this.deletePresetButton.setEnabled(false);
/*      */   }
/*      */ 
/*      */   public List<String[]> getValues() {
/*  577 */     return this.values;
/*      */   }
/*      */ 
/*      */   public void setParameters(Map<String, Variable> parameters) {
/*  581 */     if ((this.isAnyParameterDetected) && 
/*  582 */       (parameters != null)) {
/*  583 */       this.presetsCombo.setSelectedIndex(-1);
/*  584 */       setVariables(parameters);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<StrategyRunParameter> getParameters()
/*      */   {
/*  590 */     List result = new LinkedList();
/*  591 */     if (this.componentsList != null) {
/*  592 */       for (JComponent component : this.componentsList) {
/*  593 */         String name = (String)component.getClientProperty("fieldName");
/*  594 */         Class fieldType = (Class)component.getClientProperty("type");
/*      */         try {
/*  596 */           Object value = getFieldValue(component, fieldType);
/*      */ 
/*  598 */           StrategyRunParameter parameter = new StrategyRunParameter(name, fieldType, value);
/*  599 */           Boolean dateAsLong = (Boolean)component.getClientProperty("dateAsLong");
/*  600 */           parameter.setDateAsLong(dateAsLong == null ? false : dateAsLong.booleanValue());
/*      */ 
/*  602 */           Boolean mandatory = (Boolean)component.getClientProperty("obligatory");
/*  603 */           parameter.setMandatory(mandatory == null ? false : mandatory.booleanValue());
/*      */ 
/*  605 */           String fileType = (String)component.getClientProperty("fileType");
/*  606 */           parameter.setFileType(fileType);
/*      */ 
/*  608 */           Double stepSize = (Double)component.getClientProperty("stepSize");
/*  609 */           if (stepSize != null) {
/*  610 */             parameter.setStepSize(stepSize.doubleValue());
/*      */           }
/*      */ 
/*  613 */           String uiName = (String)component.getClientProperty("fieldTitle");
/*  614 */           parameter.setTitle(uiName);
/*      */ 
/*  616 */           result.add(parameter);
/*      */         } catch (IllegalArgumentException e) {
/*  618 */           LOGGER.debug(new StringBuilder().append("Error getting value for field : ").append(name).toString(), e);
/*      */         }
/*      */       }
/*      */     }
/*  622 */     return result;
/*      */   }
/*      */ 
/*      */   private JComponent createControl(IStrategy target, StrategyRunParameter parameter) {
/*  626 */     Variable var = parameter.getVariable();
/*  627 */     Class fieldType = var.getType();
/*  628 */     String fieldName = parameter.getName();
/*  629 */     boolean datetimeAsLong = parameter.isDateAsLong();
/*      */ 
/*  631 */     if (IChart.class.equals(fieldType)) {
/*  632 */       IConnectorManager manager = ConnectorManager.getInstance();
/*  633 */       ComboBoxModel model = (ComboBoxModel)manager.get(IConnectorManager.ConnectorKeys.CHART_COMBO_MODEL);
/*  634 */       return createComboBox(var.getValue(), fieldName, IChart.class, target, model);
/*      */     }
/*      */ 
/*  641 */     if (String.class.equals(fieldType)) {
/*  642 */       JTextField fieldEditor = new JTextField();
/*  643 */       fieldEditor.setPreferredSize(DEFAULT_FIELD_SIZE);
/*      */       try
/*      */       {
/*  646 */         Object fieldValue = var.getValue();
/*  647 */         Variable variable = getPresetVariable(fieldName);
/*  648 */         if ((!this.isEditDialog) && (variable != null)) {
/*  649 */           Object value = variable.getValue();
/*  650 */           if (value != null)
/*  651 */             fieldEditor.setText(String.valueOf(value));
/*      */         }
/*      */         else {
/*  654 */           fieldEditor.setText((String)fieldValue);
/*      */         }
/*  656 */         fieldEditor.putClientProperty("default", fieldValue);
/*  657 */         fieldEditor.putClientProperty("type", String.class);
/*  658 */         fieldEditor.putClientProperty("dateAsLong", Boolean.FALSE);
/*      */       } catch (Exception e) {
/*  660 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */ 
/*  663 */       return fieldEditor;
/*      */     }
/*      */ 
/*  666 */     if ((Integer.TYPE.equals(fieldType)) || (Integer.class.equals(fieldType))) {
/*  667 */       int stepSize = 1;
/*  668 */       int configurableStepSize = (int)parameter.getStepSize();
/*  669 */       if (configurableStepSize > 0) {
/*  670 */         stepSize = configurableStepSize;
/*      */       }
/*      */ 
/*  673 */       return createSpinner(var.getValue(), fieldName, fieldType, target, new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(-2147483648), Integer.valueOf(2147483647), Integer.valueOf(stepSize)), "#");
/*      */     }
/*      */ 
/*  685 */     if (((Long.TYPE.equals(fieldType)) || (Long.class.equals(fieldType))) && (!datetimeAsLong)) {
/*  686 */       long stepSize = 1L;
/*  687 */       long configurableStepSize = ()parameter.getStepSize();
/*  688 */       if (configurableStepSize > 0L) {
/*  689 */         stepSize = configurableStepSize;
/*      */       }
/*      */ 
/*  692 */       return createSpinner(var.getValue(), fieldName, fieldType, target, new SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(-9223372036854775808L), Long.valueOf(9223372036854775807L), Long.valueOf(stepSize)), "#");
/*      */     }
/*      */ 
/*  702 */     if (datetimeAsLong) {
/*  703 */       return createDateChooser(var.getValue(), datetimeAsLong, fieldName, fieldType, target);
/*      */     }
/*      */ 
/*  706 */     if ((Double.TYPE.equals(fieldType)) || (Double.class.equals(fieldType))) {
/*  707 */       double stepSize = 0.5D;
/*  708 */       if (parameter.getStepSize() > 0.0D) {
/*  709 */         stepSize = parameter.getStepSize();
/*      */       }
/*      */ 
/*  712 */       return createSpinner(var.getValue(), fieldName, fieldType, target, new DoubleSpinnerModel(0.0D, (-1.0D / 0.0D), 1.7976931348623157E+308D, stepSize), "0.0###################");
/*      */     }
/*      */ 
/*  720 */     if (Boolean.TYPE.equals(fieldType)) {
/*  721 */       JCheckBox fieldEditor = new JCheckBox();
/*  722 */       fieldEditor.setPreferredSize(DEFAULT_FIELD_SIZE);
/*      */       try {
/*  724 */         boolean fieldValue = ((Boolean)var.getValue()).booleanValue();
/*  725 */         Variable variable = getPresetVariable(fieldName);
/*  726 */         if ((!this.isEditDialog) && (variable != null))
/*  727 */           fieldEditor.setSelected(((Boolean)variable.getValue()).booleanValue());
/*      */         else {
/*  729 */           fieldEditor.setSelected(fieldValue);
/*      */         }
/*  731 */         fieldEditor.putClientProperty("default", new Boolean(fieldValue));
/*  732 */         fieldEditor.putClientProperty("type", fieldType);
/*  733 */         fieldEditor.putClientProperty("dateAsLong", Boolean.FALSE);
/*      */       } catch (Exception e) {
/*  735 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  737 */       return fieldEditor;
/*      */     }
/*      */ 
/*  740 */     if (Boolean.class.equals(fieldType)) {
/*  741 */       JCheckBox fieldEditor = new JCheckBox();
/*  742 */       fieldEditor.setPreferredSize(DEFAULT_FIELD_SIZE);
/*      */       try {
/*  744 */         Boolean fieldValue = (Boolean)var.getValue();
/*  745 */         Variable variable = getPresetVariable(fieldName);
/*  746 */         if ((!this.isEditDialog) && (variable != null)) {
/*  747 */           Boolean value = (Boolean)variable.getValue();
/*  748 */           fieldEditor.setSelected((value != null) && (value.booleanValue()));
/*      */         } else {
/*  750 */           fieldEditor.setSelected((fieldValue != null) && (fieldValue.booleanValue()));
/*      */         }
/*  752 */         fieldEditor.putClientProperty("default", fieldValue);
/*  753 */         fieldEditor.putClientProperty("type", fieldType);
/*  754 */         fieldEditor.putClientProperty("dateAsLong", Boolean.FALSE);
/*      */       } catch (Exception e) {
/*  756 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  758 */       return fieldEditor;
/*      */     }
/*      */ 
/*  761 */     if (Period.class.equals(fieldType)) {
/*  762 */       return createComboBox(var.getValue(), fieldName, Period.class, target, Period.values());
/*      */     }
/*      */ 
/*  769 */     if (Instrument.class.equals(fieldType)) {
/*  770 */       return createComboBox(var.getValue(), fieldName, Instrument.class, target, Instrument.values());
/*      */     }
/*      */ 
/*  777 */     if (File.class.equals(fieldType)) {
/*      */       try {
/*  779 */         File fieldValue = (File)var.getValue();
/*  780 */         String fileType = parameter.getFileType();
/*  781 */         if ((fileType == null) || (fileType.trim().isEmpty())) {
/*  782 */           JTextField fileText = new JTextField();
/*  783 */           Variable variable = getPresetVariable(fieldName);
/*  784 */           if ((!this.isEditDialog) && (variable != null) && (variable.getValue() != null))
/*  785 */             fileText.setText(((File)variable.getValue()).getPath());
/*  786 */           else if (fieldValue != null)
/*  787 */             fileText.setText(fieldValue.getPath());
/*      */           else {
/*  789 */             fileText.setText("");
/*      */           }
/*  791 */           fileText.setPreferredSize(new Dimension(128, 20));
/*  792 */           fileText.putClientProperty("default", "");
/*  793 */           fileText.putClientProperty("type", File.class);
/*  794 */           fileText.putClientProperty("dateAsLong", Boolean.FALSE);
/*  795 */           return fileText;
/*      */         }
/*  797 */         File strategyDir = FilePathManager.getInstance().getFilesForStrategiesDir();
/*  798 */         FilenameFilter parseFilter = new ParseFilenameFilter(fileType);
/*  799 */         String[] filteredFileNames = strategyDir.list(parseFilter);
/*  800 */         Arrays.sort(filteredFileNames);
/*  801 */         JComboBox fileCombo = new JComboBox(filteredFileNames);
/*  802 */         fileCombo.addItem("Not Selected");
/*      */ 
/*  804 */         Variable variable = getPresetVariable(fieldName);
/*  805 */         if ((!this.isEditDialog) && (variable != null)) {
/*  806 */           File file = (File)variable.getValue();
/*  807 */           if ((file != null) && ("Default".equalsIgnoreCase(((Preset)this.presetsCombo.getSelectedItem()).getName()))) {
/*  808 */             file = new File(new StringBuilder().append(FilePathManager.getInstance().getFilesForStrategiesDir()).append(File.separator).append(file.getName()).toString());
/*  809 */             variable.setValue(file);
/*      */           }
/*  811 */           if ((file == null) || (!file.isFile())) {
/*  812 */             fileCombo.setSelectedItem("Not Selected");
/*      */           } else {
/*  814 */             String fileNameFromModel = file.getName();
/*  815 */             if (Arrays.asList(filteredFileNames).contains(fileNameFromModel))
/*  816 */               fileCombo.setSelectedItem(fileNameFromModel);
/*      */             else
/*  818 */               fileCombo.setSelectedItem("Not Selected");
/*      */           }
/*      */         }
/*  821 */         else if ((fieldValue != null) && (fieldValue.isFile())) {
/*  822 */           fileCombo.setSelectedItem(fieldValue.getName());
/*      */         } else {
/*  824 */           fileCombo.setSelectedItem("Not Selected");
/*      */         }
/*  826 */         fileCombo.setPreferredSize(DEFAULT_FIELD_SIZE);
/*  827 */         fileCombo.putClientProperty("type", File.class);
/*  828 */         fileCombo.putClientProperty("default", "Not Selected");
/*  829 */         fileCombo.putClientProperty("dateAsLong", Boolean.FALSE);
/*  830 */         return fileCombo;
/*      */       }
/*      */       catch (Exception e) {
/*  833 */         LOGGER.error(e.getMessage(), e);
/*  834 */         return null;
/*      */       }
/*      */     }
/*      */ 
/*  838 */     if (fieldType.isEnum()) {
/*  839 */       JComboBox fieldEditor = new JComboBox(fieldType.getEnumConstants());
/*  840 */       fieldEditor.setPreferredSize(DEFAULT_FIELD_SIZE);
/*      */       try {
/*  842 */         Object fieldValue = var.getValue();
/*  843 */         Variable variable = getPresetVariable(fieldName);
/*      */ 
/*  845 */         if ((!this.isEditDialog) && (variable != null))
/*  846 */           fieldEditor.setSelectedItem(variable.getValue());
/*      */         else {
/*  848 */           fieldEditor.setSelectedItem(fieldValue);
/*      */         }
/*  850 */         fieldEditor.putClientProperty("default", fieldValue);
/*  851 */         fieldEditor.putClientProperty("type", fieldType);
/*  852 */         fieldEditor.putClientProperty("dateAsLong", Boolean.FALSE);
/*      */       } catch (Exception e) {
/*  854 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  856 */       return fieldEditor;
/*      */     }
/*      */ 
/*  859 */     if ((Calendar.class.isAssignableFrom(fieldType)) || (java.util.Date.class.isAssignableFrom(fieldType))) {
/*  860 */       return createDateChooser(var.getValue(), datetimeAsLong, fieldName, fieldType, target);
/*      */     }
/*      */ 
/*  863 */     return null;
/*      */   }
/*      */ 
/*      */   private static Class<?> getWrapperClass(Class<?> objectClass) {
/*  867 */     if (objectClass.isPrimitive()) {
/*  868 */       if (objectClass == Integer.TYPE) {
/*  869 */         return Integer.class;
/*      */       }
/*  871 */       if (objectClass == Long.TYPE) {
/*  872 */         return Long.class;
/*      */       }
/*  874 */       if (objectClass == Double.TYPE) {
/*  875 */         return Double.class;
/*      */       }
/*  877 */       if (objectClass == Boolean.TYPE) {
/*  878 */         return Boolean.class;
/*      */       }
/*      */     }
/*      */ 
/*  882 */     return objectClass;
/*      */   }
/*      */ 
/*      */   private JSpinner createSpinner(Object fieldValue, String fieldName, Class<?> fieldClass, IStrategy strategy, SpinnerNumberModel spinnerNumberModel, String format) {
/*  886 */     JSpinner spinner = new JSpinner(spinnerNumberModel, format) {
/*      */       protected JComponent createEditor(SpinnerModel model) {
/*  888 */         return new JSpinner.NumberEditor(this, this.val$format);
/*      */       }
/*      */ 
/*      */       public Dimension getPreferredSize() {
/*  892 */         return ParametersPanel.DEFAULT_FIELD_SIZE;
/*      */       }
/*      */     };
/*  896 */     ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setHorizontalAlignment(2);
/*  897 */     spinner.addMouseWheelListener(new SpinnerMouseWheelListener(spinner));
/*      */ 
/*  899 */     spinner.setPreferredSize(DEFAULT_FIELD_SIZE);
/*  900 */     Variable variable = getPresetVariable(fieldName);
/*      */ 
/*  902 */     if ((!this.isEditDialog) && (variable != null)) {
/*  903 */       spinner.setValue(variable.getValue());
/*      */     }
/*  905 */     else if (fieldValue != null) {
/*  906 */       spinner.setValue(fieldValue);
/*      */     }
/*      */ 
/*  909 */     spinner.putClientProperty("default", fieldValue);
/*  910 */     spinner.putClientProperty("type", fieldClass);
/*  911 */     spinner.putClientProperty("dateAsLong", Boolean.FALSE);
/*      */ 
/*  913 */     return spinner;
/*      */   }
/*      */ 
/*      */   private JComboBox createComboBox(Object fieldValue, String fieldName, Class<?> fieldClass, IStrategy strategy, Object[] items) {
/*  917 */     JComboBox comboBox = new JComboBox(items);
/*  918 */     comboBox.setPreferredSize(DEFAULT_FIELD_SIZE);
/*  919 */     Variable variable = getPresetVariable(fieldName);
/*      */ 
/*  921 */     if ((!this.isEditDialog) && (variable != null))
/*  922 */       comboBox.setSelectedItem(variable.getValue());
/*  923 */     else if (fieldValue != null)
/*  924 */       comboBox.setSelectedItem(fieldValue);
/*      */     else {
/*  926 */       comboBox.setSelectedIndex(0);
/*      */     }
/*      */ 
/*  929 */     comboBox.putClientProperty("default", fieldValue);
/*  930 */     comboBox.putClientProperty("type", fieldClass);
/*  931 */     comboBox.putClientProperty("dateAsLong", Boolean.FALSE);
/*      */ 
/*  933 */     return comboBox;
/*      */   }
/*      */ 
/*      */   private JComboBox createComboBox(Object fieldValue, String fieldName, Class<?> fieldClass, IStrategy strategy, ComboBoxModel model) {
/*  937 */     JComboBox comboBox = new JComboBox(model);
/*  938 */     comboBox.setPreferredSize(DEFAULT_FIELD_SIZE);
/*  939 */     Variable variable = getPresetVariable(fieldName);
/*      */ 
/*  941 */     if ((!this.isEditDialog) && (variable != null))
/*  942 */       comboBox.setSelectedItem(variable.getValue());
/*  943 */     else if (fieldValue != null)
/*  944 */       comboBox.setSelectedItem(fieldValue);
/*      */     else {
/*  946 */       comboBox.setSelectedIndex(0);
/*      */     }
/*      */ 
/*  949 */     comboBox.putClientProperty("default", fieldValue);
/*  950 */     comboBox.putClientProperty("type", fieldClass);
/*  951 */     comboBox.putClientProperty("dateAsLong", Boolean.FALSE);
/*      */ 
/*  953 */     return comboBox;
/*      */   }
/*      */ 
/*      */   private JDateChooser createDateChooser(Object fieldValue, boolean datetimeAsLong, String fieldName, Class<?> fieldClass, IStrategy strategy) {
/*  957 */     JSpinnerDateEditor spinnerDateEditor = new JSpinnerDateEditor();
/*  958 */     spinnerDateEditor.setDateFormatString("dd-MM-yyyy HH:mm:ss");
/*      */ 
/*  960 */     JDateChooser dateChooser = new JDateChooser(spinnerDateEditor);
/*  961 */     dateChooser.setDateFormatString("dd-MM-yyyy HH:mm:ss");
/*  962 */     dateChooser.setPreferredSize(DEFAULT_FIELD_SIZE);
/*      */ 
/*  964 */     if (!this.isEditDialog) {
/*  965 */       Variable variable = getPresetVariable(fieldName);
/*      */ 
/*  967 */       if (Calendar.class.isAssignableFrom(fieldClass)) {
/*  968 */         if ((variable != null) && (variable.getValue() != null))
/*  969 */           dateChooser.setCalendar((Calendar)variable.getValue());
/*  970 */       } else if (java.util.Date.class.isAssignableFrom(fieldClass)) {
/*  971 */         if (variable != null)
/*  972 */           dateChooser.setDate((java.util.Date)variable.getValue());
/*  973 */       } else if ((datetimeAsLong) && 
/*  974 */         (variable != null) && (variable.getValue() != null)) {
/*  975 */         dateChooser.setDate(new java.util.Date(((Long)variable.getValue()).longValue()));
/*      */       }
/*      */ 
/*      */     }
/*  979 */     else if (Calendar.class.isAssignableFrom(fieldClass)) {
/*  980 */       dateChooser.setCalendar((Calendar)fieldValue);
/*  981 */     } else if (java.util.Date.class.isAssignableFrom(fieldClass)) {
/*  982 */       dateChooser.setDate((java.util.Date)fieldValue);
/*  983 */     } else if (datetimeAsLong) {
/*  984 */       dateChooser.setDate(new java.util.Date(((Long)fieldValue).longValue()));
/*      */     }
/*      */ 
/*  988 */     dateChooser.putClientProperty("default", fieldValue);
/*  989 */     dateChooser.putClientProperty("type", fieldClass);
/*  990 */     dateChooser.putClientProperty("dateAsLong", Boolean.valueOf(datetimeAsLong));
/*  991 */     return dateChooser;
/*      */   }
/*      */ 
/*      */   private Variable getPresetVariable(String fieldName) {
/*  995 */     if ((this.presetsModel != null) && (this.presetsModel.getDefaultVariableMap() != null)) {
/*  996 */       return (Variable)this.presetsModel.getDefaultVariableMap().get(fieldName);
/*      */     }
/*  998 */     return null;
/*      */   }
/*      */ 
/*      */   public void actionPerformed(ActionEvent e)
/*      */   {
/* 1003 */     if (this.blockOtherOperations) {
/* 1004 */       return;
/*      */     }
/*      */ 
/* 1007 */     Action action = Action.valueOf(e.getActionCommand());
/*      */ 
/* 1009 */     switch (12.$SwitchMap$com$dukascopy$dds2$greed$agent$strategy$ParametersPanel$Action[action.ordinal()]) {
/*      */     case 1:
/* 1011 */       boolean checkFail = false;
/* 1012 */       for (JComponent component : this.componentsList) {
/*      */         try {
/* 1014 */           setControlField(component, true);
/*      */         } catch (Exception ex) {
/* 1016 */           checkFail = true;
/* 1017 */           flushField(component);
/*      */         }
/*      */       }
/*      */ 
/* 1021 */       if (checkFail) {
/* 1022 */         return;
/*      */       }
/*      */ 
/* 1025 */       this.returnCode = new TaskParameter(this.plainTarget, (IControlUI)this.parentDialog, this.componentsList);
/*      */ 
/* 1036 */       this.parentDialog.dispose();
/* 1037 */       break;
/*      */     case 2:
/* 1039 */       setVisible(false);
/* 1040 */       this.parentDialog.dispose();
/* 1041 */       break;
/*      */     case 3:
/* 1043 */       saveCurrentPreset();
/* 1044 */       saveModel();
/* 1045 */       break;
/*      */     case 4:
/* 1047 */       deletePreset();
/* 1048 */       saveModel();
/* 1049 */       break;
/*      */     default:
/* 1051 */       LOGGER.warn("Unsupported action : []", action);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void deletePreset() {
/* 1056 */     Preset preset = this.presetsModel.getPreset(((JTextField)this.presetsCombo.getEditor().getEditorComponent()).getText());
/* 1057 */     if ((preset == null) || ("Default".equalsIgnoreCase(preset.getName()))) {
/* 1058 */       this.savePresetButton.setEnabled(false);
/* 1059 */       this.deletePresetButton.setEnabled(false);
/* 1060 */       return;
/*      */     }
/*      */ 
/* 1064 */     if (this.presetsCombo.getItemCount() < 2) {
/* 1065 */       this.presetsCombo.setEnabled(false);
/* 1066 */       return;
/*      */     }
/*      */ 
/* 1070 */     this.presetsModel.deletePreset(preset);
/*      */ 
/* 1072 */     this.presetsCombo.removeItem(preset);
/*      */   }
/*      */ 
/*      */   private void setPreset()
/*      */   {
/* 1077 */     Preset selectedPreset = (Preset)this.presetsCombo.getSelectedItem();
/*      */ 
/* 1080 */     HashMap variableMap = this.presetsModel.getVariableMap(selectedPreset);
/*      */ 
/* 1082 */     setVariables(variableMap);
/*      */ 
/* 1084 */     this.savePresetButton.setEnabled(false);
/*      */ 
/* 1086 */     if ("Default".equalsIgnoreCase(selectedPreset.getName()))
/* 1087 */       this.deletePresetButton.setEnabled(false);
/*      */     else
/* 1089 */       this.deletePresetButton.setEnabled(true);
/*      */   }
/*      */ 
/*      */   private void setVariables(Map<String, Variable> variableMap)
/*      */   {
/* 1094 */     for (JComponent currentComponent : this.componentsList) {
/* 1095 */       String fieldName = (String)currentComponent.getClientProperty("fieldName");
/* 1096 */       Variable modelVariable = (Variable)variableMap.get(fieldName);
/*      */ 
/* 1098 */       if ((currentComponent instanceof JCheckBox)) {
/* 1099 */         ((JCheckBox)currentComponent).setSelected(((Boolean)modelVariable.getValue()).booleanValue());
/* 1100 */       } else if ((currentComponent instanceof JSpinner)) {
/* 1101 */         if (modelVariable.getValue() != null)
/* 1102 */           ((JSpinner)currentComponent).setValue(modelVariable.getValue());
/*      */         else
/* 1104 */           ((JSpinner)currentComponent).setValue(Integer.valueOf(0));
/*      */       }
/* 1106 */       else if ((currentComponent instanceof JComboBox)) {
/* 1107 */         Class variableClass = modelVariable.getType();
/* 1108 */         if (variableClass.isEnum())
/* 1109 */           ((JComboBox)currentComponent).setSelectedItem(modelVariable.getValue());
/* 1110 */         else if (variableClass.equals(File.class)) {
/* 1111 */           if ((modelVariable.getValue() == null) || (!((File)modelVariable.getValue()).isFile())) {
/* 1112 */             ((JComboBox)currentComponent).setSelectedItem("Not Selected");
/*      */           } else {
/* 1114 */             String fileName = ((File)modelVariable.getValue()).getName();
/* 1115 */             ((JComboBox)currentComponent).setSelectedItem(fileName);
/*      */           }
/*      */         }
/* 1118 */         else ((JComboBox)currentComponent).setSelectedItem(modelVariable.getValue());
/*      */       }
/* 1120 */       else if ((currentComponent instanceof JDateChooser)) {
/* 1121 */         if (modelVariable.getValue() != null) {
/* 1122 */           if (Calendar.class.isAssignableFrom(modelVariable.getType()))
/* 1123 */             ((JDateChooser)currentComponent).setCalendar((Calendar)modelVariable.getValue());
/* 1124 */           else if (java.util.Date.class.isAssignableFrom(modelVariable.getType()))
/* 1125 */             ((JDateChooser)currentComponent).setDate((java.util.Date)modelVariable.getValue());
/*      */         }
/*      */         else {
/* 1128 */           ((JDateChooser)currentComponent).setDate(null);
/*      */         }
/*      */       }
/* 1131 */       else if (modelVariable.getValue() != null) {
/* 1132 */         ((JTextField)currentComponent).setText(modelVariable.getValue().toString());
/*      */       } else {
/* 1134 */         ((JTextField)currentComponent).setText("");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void saveCurrentPreset()
/*      */   {
/* 1141 */     if (!checkForTypeCompatibility()) {
/* 1142 */       return;
/*      */     }
/*      */ 
/* 1145 */     String comboText = ((JTextField)this.presetsCombo.getEditor().getEditorComponent()).getText();
/*      */ 
/* 1147 */     Preset presetByText = this.presetsModel.getPreset(comboText);
/*      */ 
/* 1150 */     if ((presetByText != null) && ("Default".equalsIgnoreCase(presetByText.getName()))) {
/* 1151 */       this.savePresetButton.setEnabled(false);
/* 1152 */       this.deletePresetButton.setEnabled(false);
/* 1153 */       return;
/*      */     }
/*      */ 
/* 1156 */     if (presetByText == null)
/*      */     {
/* 1158 */       Preset newPreset = new Preset(comboText, Preset.USER_ADDED);
/*      */ 
/* 1160 */       HashMap variablesMap = new HashMap();
/*      */ 
/* 1162 */       for (JComponent currentComponent : this.componentsList) {
/* 1163 */         String fieldName = (String)currentComponent.getClientProperty("fieldName");
/* 1164 */         Class fieldClass = (Class)currentComponent.getClientProperty("type");
/* 1165 */         Object obligatoryObj = currentComponent.getClientProperty("obligatory");
/* 1166 */         boolean obligatory = obligatoryObj == null ? false : ((Boolean)obligatoryObj).booleanValue();
/* 1167 */         Object fieldValue = null;
/*      */ 
/* 1169 */         if (String.class.equals(fieldClass))
/* 1170 */           fieldValue = ((JTextField)currentComponent).getText();
/* 1171 */         else if ((Integer.TYPE.equals(fieldClass)) || (Integer.class.equals(fieldClass)))
/* 1172 */           fieldValue = ((JSpinner)currentComponent).getValue();
/* 1173 */         else if ((Long.TYPE.equals(fieldClass)) || (Long.class.equals(fieldClass)))
/* 1174 */           fieldValue = ((JSpinner)currentComponent).getValue();
/* 1175 */         else if ((Double.TYPE.equals(fieldClass)) || (Double.class.equals(fieldClass)))
/* 1176 */           fieldValue = ((JSpinner)currentComponent).getValue();
/* 1177 */         else if ((Boolean.TYPE.equals(fieldClass)) || (Boolean.class.equals(fieldClass)))
/* 1178 */           fieldValue = Boolean.valueOf(((JCheckBox)currentComponent).isSelected());
/* 1179 */         else if (Instrument.class.equals(fieldClass))
/* 1180 */           fieldValue = ((JComboBox)currentComponent).getSelectedItem();
/* 1181 */         else if (Period.class.equals(fieldClass))
/* 1182 */           fieldValue = ((JComboBox)currentComponent).getSelectedItem();
/* 1183 */         else if (File.class.equals(fieldClass)) {
/* 1184 */           if ((currentComponent instanceof JTextField)) {
/* 1185 */             fieldValue = new File(((JTextField)currentComponent).getText());
/*      */           } else {
/* 1187 */             String fileName = (String)((JComboBox)currentComponent).getSelectedItem();
/* 1188 */             if (!fileName.equals("Not Selected")) {
/* 1189 */               String stratFilesPath = new StringBuilder().append(FilePathManager.getInstance().getFilesForStrategiesDir().getPath()).append(File.separator).toString();
/* 1190 */               fieldValue = new File(new StringBuilder().append(stratFilesPath).append(fileName).toString());
/*      */             } else {
/* 1192 */               fieldValue = new File("");
/*      */             }
/*      */           }
/* 1195 */         } else if (java.util.Date.class.isAssignableFrom(fieldClass))
/* 1196 */           fieldValue = ((JDateChooser)currentComponent).getDate();
/* 1197 */         else if (Calendar.class.isAssignableFrom(fieldClass))
/* 1198 */           fieldValue = ((JDateChooser)currentComponent).getCalendar();
/* 1199 */         else if (fieldClass.isEnum()) {
/* 1200 */           fieldValue = ((JComboBox)currentComponent).getSelectedItem();
/*      */         }
/*      */ 
/* 1203 */         validate(fieldValue, getWrapperClass(fieldClass), fieldName, obligatory);
/*      */ 
/* 1205 */         Variable variable = new Variable(fieldValue, fieldClass);
/* 1206 */         variablesMap.put(fieldName, variable);
/*      */       }
/*      */ 
/* 1210 */       this.presetsModel.addPreset(newPreset, variablesMap);
/*      */ 
/* 1212 */       this.blockOtherOperations = true;
/* 1213 */       this.presetsCombo.addItem(newPreset);
/* 1214 */       this.presetsCombo.setSelectedItem(newPreset);
/* 1215 */       this.blockOtherOperations = false;
/*      */     }
/*      */     else {
/* 1218 */       saveMainPreset(presetByText);
/*      */     }
/*      */ 
/* 1221 */     this.savePresetButton.setEnabled(false);
/* 1222 */     this.deletePresetButton.setEnabled(true);
/*      */   }
/*      */ 
/*      */   private void validate(Object fieldValue, Class<?> fieldClass, String variableName, boolean obligatory) throws IllegalArgumentException {
/* 1226 */     if ((fieldValue != null) && (!fieldClass.isAssignableFrom(fieldValue.getClass())) && (!fieldClass.getClass().isInstance(fieldValue.getClass()))) {
/* 1227 */       throw new IllegalArgumentException(new StringBuilder().append("Value of field [").append(variableName).append("] is not instance of ").append(fieldClass).append(" : ").append(fieldValue.getClass()).toString());
/*      */     }
/* 1229 */     if ((obligatory) && (fieldClass.equals(String.class)) && ((fieldValue == null) || (fieldValue.equals("")))) {
/* 1230 */       throw new IllegalArgumentException(new StringBuilder().append("Value of field [").append(variableName).append("] is not set").toString());
/*      */     }
/* 1232 */     if ((obligatory) && (fieldClass.equals(File.class)) && ((fieldValue == null) || (fieldValue.equals("Not Selected")) || (fieldValue.equals(new File(""))))) {
/* 1233 */       throw new IllegalArgumentException(new StringBuilder().append("Value of field [").append(variableName).append("] is not set").toString());
/*      */     }
/* 1235 */     if ((obligatory) && (fieldValue == null))
/* 1236 */       throw new IllegalArgumentException(new StringBuilder().append("Value of field [").append(variableName).append("] is not set").toString());
/*      */   }
/*      */ 
/*      */   void saveMainPreset(Preset presetToSave)
/*      */   {
/* 1242 */     HashMap variablesMap = this.presetsModel.getVariableMap(presetToSave);
/* 1243 */     for (JComponent currentComponent : this.componentsList) {
/* 1244 */       String variableName = (String)currentComponent.getClientProperty("fieldName");
/* 1245 */       Class fieldType = (Class)currentComponent.getClientProperty("type");
/*      */ 
/* 1247 */       Variable modelVariable = (Variable)variablesMap.get(variableName);
/* 1248 */       Object fieldValue = getComponentValue(currentComponent, fieldType);
/*      */ 
/* 1250 */       modelVariable.setValue(fieldValue);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object getComponentValue(JComponent component, Class<?> fieldType)
/*      */   {
/*      */     Object fieldValue;
/*      */     Object fieldValue;
/* 1257 */     if (fieldType.equals(String.class)) {
/* 1258 */       fieldValue = ((JTextField)component).getText();
/*      */     }
/*      */     else
/*      */     {
/*      */       Object fieldValue;
/* 1259 */       if ((fieldType.equals(Integer.TYPE)) || (fieldType.equals(Integer.class))) {
/* 1260 */         fieldValue = ((JSpinner)component).getValue();
/*      */       }
/*      */       else
/*      */       {
/*      */         Object fieldValue;
/* 1261 */         if ((fieldType.equals(Long.TYPE)) || (fieldType.equals(Long.class))) {
/* 1262 */           fieldValue = ((JSpinner)component).getValue();
/*      */         }
/*      */         else
/*      */         {
/*      */           Object fieldValue;
/* 1263 */           if ((fieldType.equals(Double.TYPE)) || (fieldType.equals(Double.class))) {
/* 1264 */             fieldValue = ((JSpinner)component).getValue();
/*      */           }
/*      */           else
/*      */           {
/*      */             Object fieldValue;
/* 1265 */             if ((fieldType.equals(Boolean.TYPE)) || (fieldType.equals(Boolean.class))) {
/* 1266 */               fieldValue = Boolean.valueOf(((JCheckBox)component).isSelected());
/*      */             }
/*      */             else
/*      */             {
/*      */               Object fieldValue;
/* 1267 */               if (fieldType.equals(Instrument.class)) {
/* 1268 */                 fieldValue = ((JComboBox)component).getSelectedItem();
/*      */               }
/*      */               else
/*      */               {
/*      */                 Object fieldValue;
/* 1269 */                 if (fieldType.equals(Period.class)) {
/* 1270 */                   fieldValue = ((JComboBox)component).getSelectedItem();
/*      */                 }
/*      */                 else
/*      */                 {
/*      */                   Object fieldValue;
/* 1271 */                   if (fieldType.equals(File.class))
/*      */                   {
/*      */                     Object fieldValue;
/* 1272 */                     if ((component instanceof JTextField)) {
/* 1273 */                       fieldValue = new File(((JTextField)component).getText());
/*      */                     } else {
/* 1275 */                       String fileName = (String)((JComboBox)component).getSelectedItem();
/*      */                       Object fieldValue;
/* 1276 */                       if (!fileName.equals("Not Selected")) {
/* 1277 */                         String stratFilesPath = new StringBuilder().append(FilePathManager.getInstance().getFilesForStrategiesDir().getPath()).append(File.separator).toString();
/* 1278 */                         fieldValue = new File(new StringBuilder().append(stratFilesPath).append(fileName).toString());
/*      */                       } else {
/* 1280 */                         fieldValue = new File("");
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   else
/*      */                   {
/*      */                     Object fieldValue;
/* 1283 */                     if (java.util.Date.class.isAssignableFrom(fieldType)) {
/* 1284 */                       fieldValue = ((JDateChooser)component).getDate();
/*      */                     }
/*      */                     else
/*      */                     {
/*      */                       Object fieldValue;
/* 1285 */                       if (Calendar.class.isAssignableFrom(fieldType)) {
/* 1286 */                         fieldValue = ((JDateChooser)component).getCalendar();
/*      */                       }
/*      */                       else
/*      */                       {
/*      */                         Object fieldValue;
/* 1287 */                         if (fieldType.isEnum()) {
/* 1288 */                           fieldValue = ((JComboBox)component).getSelectedItem();
/*      */                         }
/*      */                         else {
/* 1291 */                           LOGGER.debug(new StringBuilder().append("Unsupported field: ").append(fieldType).toString());
/* 1292 */                           fieldValue = null;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1295 */     return fieldValue;
/*      */   }
/*      */ 
/*      */   private boolean checkForTypeCompatibility() {
/* 1299 */     for (JComponent currentComponent : this.componentsList) {
/*      */       try {
/* 1301 */         setControlField(currentComponent, true);
/*      */       } catch (Exception e) {
/* 1303 */         LOGGER.error(e.getMessage(), e);
/* 1304 */         flushField(currentComponent);
/* 1305 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1309 */     return true;
/*      */   }
/*      */ 
/*      */   public void setControlField(JComponent component, boolean justCheckDoNotSetFields) throws Exception {
/* 1313 */     Class fieldType = (Class)component.getClientProperty("type");
/* 1314 */     String fieldName = (String)component.getClientProperty("fieldName");
/* 1315 */     Object obligatoryObj = component.getClientProperty("obligatory");
/* 1316 */     boolean obligatory = obligatoryObj == null ? false : ((Boolean)obligatoryObj).booleanValue();
/*      */     Object fieldValue;
/*      */     Object fieldValue;
/* 1319 */     if (SwingUtilities.isEventDispatchThread()) {
/* 1320 */       fieldValue = getFieldValue(component, fieldType);
/*      */     } else {
/* 1322 */       Callable callable = new Callable(component, fieldType) {
/*      */         public Object call() throws Exception {
/* 1324 */           return ParametersPanel.this.getFieldValue(this.val$component, this.val$fieldType);
/*      */         }
/*      */       };
/* 1327 */       FutureTask task = new FutureTask(callable);
/* 1328 */       SwingUtilities.invokeAndWait(task);
/*      */       try {
/* 1330 */         fieldValue = task.get();
/*      */       } catch (ExecutionException e) {
/* 1332 */         throw ((Exception)e.getCause());
/*      */       }
/*      */     }
/*      */ 
/* 1336 */     validate(fieldValue, getWrapperClass(fieldType), fieldName, obligatory);
/*      */ 
/* 1338 */     if (justCheckDoNotSetFields) {
/* 1339 */       return;
/*      */     }
/*      */ 
/* 1342 */     Field field = (Field)component.getClientProperty("field");
/* 1343 */     field.set(this.plainTarget, fieldValue);
/* 1344 */     this.values.add(new String[] { fieldName, getValueAsString(fieldValue) });
/*      */   }
/*      */ 
/*      */   private String getValueAsString(Object value)
/*      */   {
/* 1349 */     if (value == null)
/* 1350 */       return "";
/* 1351 */     if ((value instanceof Calendar))
/* 1352 */       return this.dateFormatter.format(((Calendar)value).getTime());
/* 1353 */     if ((value instanceof java.util.Date)) {
/* 1354 */       return this.dateFormatter.format((java.util.Date)value);
/*      */     }
/* 1356 */     return value.toString();
/*      */   }
/*      */ 
/*      */   private Object getFieldValue(JComponent component, Class<?> fieldType) throws IllegalArgumentException
/*      */   {
/* 1361 */     Boolean timeAsLong = (Boolean)component.getClientProperty("dateAsLong");
/* 1362 */     boolean datetimeAsLong = timeAsLong == null ? false : timeAsLong.booleanValue();
/*      */ 
/* 1364 */     Object fieldValue = null;
/* 1365 */     if (IChart.class.equals(fieldType)) {
/* 1366 */       fieldValue = ((JComboBox)component).getSelectedItem();
/* 1367 */     } else if (String.class.equals(fieldType)) {
/* 1368 */       fieldValue = ((JTextField)component).getText();
/* 1369 */     } else if ((Integer.TYPE.equals(fieldType)) || (Integer.class.equals(fieldType))) {
/* 1370 */       fieldValue = ((JSpinner)component).getValue();
/* 1371 */     } else if (((Long.TYPE.equals(fieldType)) || (Long.class.equals(fieldType))) && (!datetimeAsLong)) {
/* 1372 */       fieldValue = ((JSpinner)component).getValue();
/* 1373 */     } else if (((Long.TYPE.equals(fieldType)) || (Long.class.equals(fieldType))) && (datetimeAsLong)) {
/* 1374 */       if (((JDateChooser)component).getDate() != null)
/* 1375 */         fieldValue = Long.valueOf(((JDateChooser)component).getDate().getTime());
/*      */     }
/* 1377 */     else if ((Double.TYPE.equals(fieldType)) || (Double.class.equals(fieldType))) {
/* 1378 */       fieldValue = ((JSpinner)component).getValue();
/* 1379 */     } else if (Period.class.equals(fieldType)) {
/* 1380 */       fieldValue = ((JComboBox)component).getSelectedItem();
/* 1381 */     } else if (Instrument.class.equals(fieldType)) {
/* 1382 */       fieldValue = ((JComboBox)component).getSelectedItem();
/* 1383 */     } else if (File.class.equals(fieldType)) {
/* 1384 */       String stratFilesDir = new StringBuilder().append(FilePathManager.getInstance().getFilesForStrategiesDir().getPath()).append(File.separator).toString();
/* 1385 */       if ((component instanceof JTextField)) {
/* 1386 */         fieldValue = new File(((JTextField)component).getText());
/*      */       } else {
/* 1388 */         String fileName = (String)((JComboBox)component).getSelectedItem();
/* 1389 */         if (!fileName.equals("Not Selected"))
/* 1390 */           fieldValue = new File(new StringBuilder().append(stratFilesDir).append(fileName).toString());
/*      */         else
/* 1392 */           fieldValue = new File("");
/*      */       }
/*      */     }
/* 1395 */     else if ((Boolean.TYPE.equals(fieldType)) || (Boolean.class.equals(fieldType))) {
/* 1396 */       fieldValue = Boolean.valueOf(((JCheckBox)component).isSelected());
/* 1397 */     } else if (java.util.Date.class.isAssignableFrom(fieldType)) {
/* 1398 */       if (((JDateChooser)component).getDate() != null) {
/* 1399 */         if (java.sql.Date.class.equals(fieldType)) {
/* 1400 */           fieldValue = new java.sql.Date(((JDateChooser)component).getDate().getTime());
/*      */         }
/* 1402 */         else if (java.util.Date.class.equals(fieldType))
/* 1403 */           fieldValue = ((JDateChooser)component).getDate();
/*      */       }
/*      */     }
/* 1406 */     else if (Calendar.class.isAssignableFrom(fieldType)) {
/* 1407 */       fieldValue = ((JDateChooser)component).getCalendar();
/* 1408 */     } else if (fieldType.isEnum()) {
/* 1409 */       fieldValue = ((JComboBox)component).getSelectedItem();
/*      */     } else {
/* 1411 */       throw new IllegalArgumentException(new StringBuilder().append("Unsupported field type : ").append(fieldType).toString());
/*      */     }
/* 1413 */     return fieldValue;
/*      */   }
/*      */ 
/*      */   public void flushField(JComponent fieldEditor) {
/* 1417 */     Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
/*      */ 
/* 1419 */     if ((fieldEditor instanceof JDateChooser)) {
/* 1420 */       fieldEditor = ((JDateChooser)fieldEditor).getDateEditor().getUiComponent();
/*      */     }
/*      */ 
/* 1423 */     Border originalBorder = fieldEditor.getBorder();
/*      */     try {
/* 1425 */       fieldEditor.setBorder(redBorder);
/* 1426 */       fieldEditor.repaint();
/* 1427 */       RepaintManager.currentManager(fieldEditor).paintDirtyRegions();
/* 1428 */       Thread.sleep(300L);
/*      */ 
/* 1430 */       fieldEditor.setBorder(originalBorder);
/* 1431 */       fieldEditor.repaint();
/* 1432 */       RepaintManager.currentManager(fieldEditor).paintDirtyRegions();
/* 1433 */       Thread.sleep(300L);
/*      */ 
/* 1435 */       fieldEditor.setBorder(redBorder);
/* 1436 */       fieldEditor.repaint();
/* 1437 */       RepaintManager.currentManager(fieldEditor).paintDirtyRegions();
/* 1438 */       Thread.sleep(300L);
/*      */     } catch (Exception e) {
/*      */     }
/*      */     finally {
/* 1442 */       fieldEditor.setBorder(originalBorder);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Dimension getDimension() {
/* 1447 */     String property = System.getProperty("com.dukascopy.ParametersDialog.size");
/* 1448 */     if ((property == null) || (property.trim().isEmpty())) {
/* 1449 */       return null;
/*      */     }
/* 1451 */     String[] params = property.split(",");
/* 1452 */     if (params.length != 2)
/* 1453 */       return null;
/*      */     try
/*      */     {
/* 1456 */       return new Dimension(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
/*      */     } catch (Exception ex) {
/* 1458 */       LOGGER.error("Unable to create dimension", ex);
/* 1459 */     }return null;
/*      */   }
/*      */ 
/*      */   public boolean isAnyParameterDetected()
/*      */   {
/* 1465 */     return this.isAnyParameterDetected;
/*      */   }
/*      */ 
/*      */   public Task<?> getReturnCode() {
/* 1469 */     return this.returnCode;
/*      */   }
/*      */ 
/*      */   private class ComboboxMouseWheelListener
/*      */     implements MouseWheelListener
/*      */   {
/*      */     private final JComboBox source;
/*      */ 
/*      */     public ComboboxMouseWheelListener(JComboBox source)
/*      */     {
/* 1512 */       this.source = source;
/*      */     }
/*      */ 
/*      */     public void mouseWheelMoved(MouseWheelEvent e) {
/* 1516 */       if (e.getScrollType() == 0) {
/* 1517 */         int selectedIndex = this.source.getSelectedIndex();
/* 1518 */         if (e.getWheelRotation() == 1)
/*      */         {
/* 1520 */           int itemCount = this.source.getItemCount();
/* 1521 */           if (selectedIndex + 1 == itemCount) {
/* 1522 */             return;
/*      */           }
/* 1524 */           int newIndex = selectedIndex + 1;
/* 1525 */           if (newIndex < this.source.getItemCount()) {
/* 1526 */             this.source.setSelectedIndex(newIndex);
/*      */           } else {
/* 1528 */             itemCount--; this.source.setSelectedIndex(itemCount);
/*      */           }
/*      */         }
/*      */         else {
/* 1532 */           if (selectedIndex == 0) {
/* 1533 */             return;
/*      */           }
/* 1535 */           int newIndex = selectedIndex - 1;
/* 1536 */           if (newIndex > 0)
/* 1537 */             this.source.setSelectedIndex(newIndex);
/*      */           else
/* 1539 */             this.source.setSelectedIndex(0);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class FileSelectButtonActionListner
/*      */     implements ActionListener
/*      */   {
/*      */     private final JDialog parent;
/*      */     private final JTextField textField;
/*      */ 
/*      */     public FileSelectButtonActionListner(JDialog parent, JTextField wherePutFileName)
/*      */     {
/* 1479 */       this.parent = parent;
/* 1480 */       this.textField = wherePutFileName;
/*      */     }
/*      */ 
/*      */     public void actionPerformed(ActionEvent e)
/*      */     {
/* 1485 */       JFileChooser fc = new JFileChooser() {
/*      */         protected JDialog createDialog(Component parent) throws HeadlessException {
/* 1487 */           JDialog dialog = super.createDialog(parent);
/* 1488 */           dialog.setLocation(parent.getLocation().x - 40, parent.getLocation().y + 40);
/* 1489 */           return dialog;
/*      */         }
/*      */       };
/* 1493 */       File filePrevious = new File(this.textField.getText());
/* 1494 */       if (filePrevious.isFile())
/* 1495 */         fc.setSelectedFile(filePrevious);
/* 1496 */       else if (filePrevious.isDirectory()) {
/* 1497 */         fc.setSelectedFile(filePrevious);
/*      */       }
/*      */ 
/* 1500 */       fc.setFileSelectionMode(0);
/* 1501 */       int res = fc.showOpenDialog(this.parent);
/* 1502 */       if (res == 0)
/* 1503 */         this.textField.setText(fc.getSelectedFile().getPath());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static enum Action
/*      */   {
/*   97 */     PRESET_DELETE("Delete Preset"), 
/*   98 */     PRESET_SAVE("Save Preset"), 
/*   99 */     RUN("Run"), 
/*  100 */     CANCEL("Cancel");
/*      */ 
/*      */     private String label;
/*      */ 
/*  105 */     private Action(String label) { this.label = label; }
/*      */ 
/*      */     public String getLabel()
/*      */     {
/*  109 */       return this.label;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ParametersPanel
 * JD-Core Version:    0.6.0
 */