/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter;
/*     */ 
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.connector.IConnectorManager;
/*     */ import com.dukascopy.api.connector.IConnectorManager.ConnectorKeys;
/*     */ import com.dukascopy.dds2.greed.connector.ConnectorManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.ButtonMouseListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.CommonUIConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesButton;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesComboBoxUI;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesSpinnerUI;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesTextField;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesTextFieldAndButton;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyComponentWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyLabel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbarUIConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.CollectionParameterWrapper;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JTextFieldDateEditor;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ComboBoxModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StrategyParameterPanelBuilder
/*     */   implements IStrategyParameterPanelBuilder
/*     */ {
/*  58 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyParameterPanelBuilder.class);
/*     */   private StrategyParameterChangeNotifier changeNotifier;
/*     */   private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
/*     */ 
/*     */   public StrategyParameterPanel buildParameterPanel(StrategyParameterLocal strategyParameter, StrategyParameterChangeNotifier changeNotifier)
/*     */   {
/*  66 */     this.changeNotifier = changeNotifier;
/*     */ 
/*  68 */     String name = strategyParameter.getName();
/*  69 */     String description = strategyParameter.getDescription();
/*     */ 
/*  71 */     Class type = strategyParameter.getType();
/*  72 */     Object value = strategyParameter.getValue();
/*  73 */     boolean datetimeAsLong = strategyParameter.isDateAsLong();
/*     */ 
/*  75 */     StrategyLabel nameField = createNameField(name, description);
/*     */ 
/*  77 */     JComponent valueField = null;
/*     */ 
/*  79 */     if ((type.isEnum()) && ((value instanceof Enum))) {
/*  80 */       Enum valueEnum = (Enum)value;
/*  81 */       CollectionParameterWrapper collectionParameterWrapper = new CollectionParameterWrapper(type.getEnumConstants(), valueEnum);
/*  82 */       valueField = createCollectionField(collectionParameterWrapper);
/*  83 */     } else if (((Long.TYPE.equals(type)) || (Long.class.equals(type))) && (datetimeAsLong)) {
/*  84 */       valueField = createDateChooser(value, datetimeAsLong, name, type);
/*  85 */     } else if (((value instanceof File)) || (type.isAssignableFrom(File.class))) {
/*  86 */       valueField = createFileField(value);
/*  87 */     } else if ((value instanceof CollectionParameterWrapper)) {
/*  88 */       valueField = createCollectionField(value);
/*  89 */     } else if ((type.equals(Integer.TYPE)) || (type.equals(Integer.class)) || (type.equals(Double.TYPE)) || (type.equals(Double.class)) || (((!Long.TYPE.equals(type)) && (!Long.class.equals(type))) || ((!datetimeAsLong) || (type.equals(Short.TYPE)) || (type.equals(Short.class)))))
/*     */     {
/*  94 */       valueField = createNumberField(value, strategyParameter.getStepSize());
/*  95 */     } else if ((value instanceof String)) {
/*  96 */       valueField = createStringField(value);
/*  97 */     } else if ((Calendar.class.isAssignableFrom(type)) || (Date.class.isAssignableFrom(type))) {
/*  98 */       valueField = createDateChooser(value, datetimeAsLong, name, type);
/*  99 */     } else if ((type.equals(Boolean.TYPE)) || (type.equals(Boolean.class))) {
/* 100 */       valueField = createBooleanField(value);
/* 101 */     } else if ((value instanceof Period)) {
/* 102 */       valueField = createPeriodField(value);
/* 103 */     } else if (type.equals(IChart.class)) {
/* 104 */       valueField = createChartsField(value);
/*     */     } else {
/* 106 */       LOGGER.warn("Unsupported type : [" + type + "]");
/* 107 */       valueField = createStringField("!UNSUPPORTED TYPE!");
/*     */     }
/* 109 */     valueField.setEnabled(!strategyParameter.isReadOnly());
/* 110 */     StrategyParameterPanel parameterPanel = new StrategyParameterPanel(strategyParameter, nameField, new StrategyComponentWrapper(valueField));
/*     */ 
/* 113 */     return parameterPanel;
/*     */   }
/*     */ 
/*     */   public void updateParameterPanel(StrategyParameterLocal strategyParameter, StrategyParameterPanel strategyPanel)
/*     */   {
/* 118 */     JComponent component = strategyPanel.getValueComponent();
/* 119 */     setValueToComponent(component, strategyParameter.getValue());
/*     */   }
/*     */ 
/*     */   private void setValueToComponent(JComponent component, Object value)
/*     */   {
/* 124 */     this.changeNotifier.setEnabled(false);
/*     */ 
/* 126 */     if ((component instanceof JTextField))
/* 127 */       ((JTextField)component).setText((String)value);
/* 128 */     else if ((component instanceof StrategiesTextFieldAndButton)) {
/* 129 */       if ((value instanceof File))
/* 130 */         ((StrategiesTextFieldAndButton)component).getTextField().setText(((File)value).getPath());
/*     */       else
/* 132 */         ((StrategiesTextFieldAndButton)component).getTextField().setText((String)value);
/*     */     }
/* 134 */     else if ((component instanceof JComboBox)) {
/* 135 */       if ((value instanceof CollectionParameterWrapper))
/* 136 */         ((JComboBox)component).setSelectedItem(((CollectionParameterWrapper)value).getSelectedValue());
/*     */       else
/* 138 */         ((JComboBox)component).setSelectedItem(value);
/*     */     }
/* 140 */     else if ((component instanceof JCheckBox))
/* 141 */       ((JCheckBox)component).setSelected(((Boolean)value).booleanValue());
/* 142 */     else if ((component instanceof JSpinner))
/* 143 */       ((JSpinner)component).setValue(value);
/* 144 */     else if ((component instanceof JDateChooser)) {
/* 145 */       if ((value instanceof Calendar))
/* 146 */         ((JDateChooser)component).setCalendar((Calendar)value);
/* 147 */       else if ((value instanceof Date))
/* 148 */         ((JDateChooser)component).setDate((Date)value);
/* 149 */       else if ((value instanceof Long)) {
/* 150 */         ((JDateChooser)component).setDate(new Date(((Long)value).longValue()));
/*     */       }
/*     */     }
/*     */ 
/* 154 */     this.changeNotifier.setEnabled(true);
/*     */   }
/*     */ 
/*     */   private JPanel createFileField(Object value)
/*     */   {
/* 159 */     String text = "";
/*     */ 
/* 161 */     if ((value != null) && ((value instanceof File)))
/* 162 */       text = ((File)value).getPath();
/*     */     else {
/* 164 */       text = (String)value;
/*     */     }
/*     */ 
/* 167 */     StrategiesTextField textField = new StrategiesTextField(text);
/*     */ 
/* 169 */     StrategiesButton fileSelectButton = new StrategyParameterButton(StrategiesToolbarUIConstants.STRATEGIES_PROPERTIES_OPEN_ICON, StrategiesToolbarUIConstants.STRATEGIES_PROPERTIES_OPEN_ICON);
/*     */ 
/* 172 */     StrategiesTextFieldAndButton resultPanel = new StrategiesTextFieldAndButton(textField, fileSelectButton);
/*     */ 
/* 174 */     fileSelectButton.addActionListener(new ActionListener(resultPanel, textField)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 178 */         JFileChooser fileChooser = new JFileChooser();
/* 179 */         int ret = fileChooser.showDialog(this.val$resultPanel, "Select file");
/* 180 */         if (ret == 0) {
/* 181 */           File file = fileChooser.getSelectedFile();
/* 182 */           this.val$textField.setText(file.getPath());
/*     */         }
/*     */       }
/*     */     });
/* 187 */     textField.getDocument().addDocumentListener(new DocumentListener()
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e)
/*     */       {
/* 191 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e)
/*     */       {
/* 196 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e)
/*     */       {
/* 201 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 205 */     return resultPanel;
/*     */   }
/*     */ 
/*     */   private JComboBox createPeriodField(Object value) {
/* 209 */     Period period = (Period)value;
/* 210 */     JComboBox comboBox = new JComboBox(Period.values());
/* 211 */     comboBox.setUI(new StrategiesComboBoxUI());
/*     */ 
/* 213 */     comboBox.setSelectedItem(period);
/*     */ 
/* 215 */     comboBox.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 218 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 222 */     return comboBox;
/*     */   }
/*     */ 
/*     */   private JCheckBox createBooleanField(Object value) {
/* 226 */     JCheckBox checkBox = new JCheckBox("", ((Boolean)value).booleanValue());
/* 227 */     checkBox.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 230 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 233 */     return checkBox;
/*     */   }
/*     */ 
/*     */   private JComboBox createCollectionField(Object value) {
/* 237 */     CollectionParameterWrapper collectionWrapper = (CollectionParameterWrapper)value;
/* 238 */     JComboBox comboBox = new JComboBox(collectionWrapper.getValues());
/* 239 */     comboBox.setUI(new StrategiesComboBoxUI());
/* 240 */     comboBox.setSelectedItem(collectionWrapper.getSelectedValue());
/*     */ 
/* 242 */     comboBox.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 245 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 249 */     return comboBox;
/*     */   }
/*     */ 
/*     */   private JComboBox createChartsField(Object value) {
/* 253 */     IChart chartObj = (IChart)value;
/*     */ 
/* 255 */     IConnectorManager manager = ConnectorManager.getInstance();
/* 256 */     ComboBoxModel model = (ComboBoxModel)manager.get(IConnectorManager.ConnectorKeys.CHART_COMBO_MODEL);
/*     */ 
/* 258 */     JComboBox comboBox = new JComboBox(model);
/* 259 */     comboBox.setUI(new StrategiesComboBoxUI());
/* 260 */     comboBox.setSelectedItem(chartObj);
/*     */ 
/* 262 */     comboBox.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 265 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 269 */     return comboBox;
/*     */   }
/*     */ 
/*     */   private JSpinner createNumberField(Object value, double stepSize) {
/* 273 */     SpinnerNumberModel numberModel = new SpinnerNumberModel();
/* 274 */     numberModel.setValue(value);
/* 275 */     if (stepSize > 0.0D) {
/* 276 */       numberModel.setStepSize(new Double(stepSize));
/*     */     }
/* 278 */     JSpinner spinner = new JSpinner(numberModel);
/* 279 */     spinner.setUI(new StrategiesSpinnerUI());
/*     */ 
/* 281 */     spinner.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 284 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 288 */     return spinner;
/*     */   }
/*     */ 
/*     */   private JDateChooser createDateChooser(Object value, boolean datetimeAsLong, String fieldName, Class<?> fieldClass) {
/* 292 */     JDateChooser dateChooser = new JDateChooser();
/*     */ 
/* 294 */     if (Calendar.class.isAssignableFrom(fieldClass))
/* 295 */       dateChooser.setCalendar((Calendar)value);
/* 296 */     else if (Date.class.isAssignableFrom(fieldClass))
/* 297 */       dateChooser.setDate((Date)value);
/* 298 */     else if ((datetimeAsLong) && 
/* 299 */       (value != null)) {
/* 300 */       dateChooser.setDate(new Date(((Long)value).longValue()));
/*     */     }
/*     */ 
/* 304 */     JButton button = dateChooser.getCalendarButton();
/*     */ 
/* 306 */     button.setPreferredSize(StrategyParameterButton.DEFAULT_SIZE);
/* 307 */     button.setMinimumSize(StrategyParameterButton.DEFAULT_SIZE);
/* 308 */     button.setIcon(CommonUIConstants.CALENDAR_ICON);
/* 309 */     button.setBorder(BorderFactory.createEmptyBorder());
/* 310 */     button.addMouseListener(new ButtonMouseListener(button));
/*     */ 
/* 312 */     JTextFieldDateEditor editor = (JTextFieldDateEditor)dateChooser.getDateEditor();
/* 313 */     editor.setDateFormatString("dd-MM-yyyy HH:mm:ss");
/* 314 */     editor.setMargin(CommonUIConstants.DEFAULT_COMPONENT_INSETS);
/*     */ 
/* 316 */     editor.getDocument().addDocumentListener(new DocumentListener()
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e)
/*     */       {
/* 320 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e)
/*     */       {
/* 325 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e)
/*     */       {
/* 330 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 334 */     return dateChooser;
/*     */   }
/*     */ 
/*     */   private JTextField createStringField(Object value)
/*     */   {
/* 394 */     StrategiesTextField textField = new StrategiesTextField((String)value);
/*     */ 
/* 396 */     textField.getDocument().addDocumentListener(new DocumentListener()
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e)
/*     */       {
/* 400 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e)
/*     */       {
/* 405 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e)
/*     */       {
/* 410 */         StrategyParameterPanelBuilder.this.changeNotifier.parameterChanged();
/*     */       }
/*     */     });
/* 414 */     return textField;
/*     */   }
/*     */ 
/*     */   private StrategyLabel createNameField(String name, String description) {
/* 418 */     StrategyLabel nameLabel = new StrategyLabel(name);
/* 419 */     nameLabel.setToolTipText(description);
/* 420 */     return nameLabel;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterPanelBuilder
 * JD-Core Version:    0.6.0
 */