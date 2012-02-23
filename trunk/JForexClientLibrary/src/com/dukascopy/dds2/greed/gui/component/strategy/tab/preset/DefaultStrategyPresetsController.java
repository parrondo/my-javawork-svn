/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.preset;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesTextFieldAndButton;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategyLabel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParamsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.CollectionParameterWrapper;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.prefs.InvalidPreferencesFormatException;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTextField;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ public class DefaultStrategyPresetsController
/*     */   implements IStrategyPresetsController
/*     */ {
/*  55 */   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStrategyPresetsController.class);
/*     */ 
/*     */   public StrategyPreset getStrategyPresetBy(List<StrategyPreset> strategyPresets, String presetId)
/*     */   {
/*  59 */     for (StrategyPreset strategyPreset : strategyPresets) {
/*  60 */       if (strategyPreset.getId().equals(presetId)) {
/*  61 */         return strategyPreset;
/*     */       }
/*     */     }
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   public void savePreset(StrategyNewBean strategyBean, StrategyPresetsComboBoxModel comboBoxModel, StrategyParamsPanel parametersContainerPanel)
/*     */   {
/*  70 */     if (parametersValid(parametersContainerPanel))
/*     */     {
/*  72 */       Object selectedObject = comboBoxModel.getSelectedItem();
/*  73 */       StrategyPreset strategyPreset = null;
/*     */ 
/*  75 */       if ((selectedObject instanceof String)) {
/*  76 */         String presetName = (String)selectedObject;
/*  77 */         comboBoxModel.getStrategyPresets();
/*  78 */         strategyPreset = getStrategyPresetBy(comboBoxModel.getStrategyPresets(), presetName);
/*  79 */         if (strategyPreset == null)
/*  80 */           strategyPreset = createPreset(comboBoxModel, parametersContainerPanel, presetName);
/*     */       }
/*     */       else {
/*  83 */         strategyPreset = (StrategyPreset)selectedObject;
/*     */       }
/*     */ 
/*  86 */       if ((strategyPreset != null) && (!"DEFAULT_PRESET_ID".equals(strategyPreset.getId())))
/*     */       {
/*  88 */         if (strategyPreset.isModified()) {
/*  89 */           strategyPreset.saveCurrentState();
/*     */         }
/*     */ 
/*  92 */         List strategyPresets = comboBoxModel.getStrategyPresets();
/*  93 */         savePresets(strategyBean, strategyPresets);
/*     */ 
/*  95 */         comboBoxModel.setSelectedItem(strategyPreset);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void deletePreset(StrategyNewBean strategyBean, StrategyPresetsComboBoxModel comboBoxModel)
/*     */   {
/* 102 */     StrategyPreset selectedItem = (StrategyPreset)comboBoxModel.getSelectedItem();
/* 103 */     if ("DEFAULT_PRESET_ID".equals(selectedItem.getId())) {
/* 104 */       return;
/*     */     }
/* 106 */     comboBoxModel.removeElement(selectedItem);
/* 107 */     savePresets(strategyBean, comboBoxModel.getStrategyPresets());
/*     */   }
/*     */ 
/*     */   public StrategyPreset createPreset(StrategyPresetsComboBoxModel comboBoxModel, StrategyParamsPanel parametersContainerPanel, String presetName)
/*     */   {
/* 112 */     List strategyParameters = retrievePresetParameters(presetName, parametersContainerPanel);
/* 113 */     StrategyPreset strategyPreset = new StrategyPreset(presetName, presetName, strategyParameters);
/* 114 */     comboBoxModel.addElement(strategyPreset);
/* 115 */     comboBoxModel.setSelectedItem(strategyPreset);
/* 116 */     return strategyPreset;
/*     */   }
/*     */ 
/*     */   public void updatePreset(StrategyParamsPanel parametersContainerPanel, StrategyPreset strategyPreset)
/*     */   {
/* 122 */     if ("DEFAULT_PRESET_ID".equals(strategyPreset.getId())) {
/* 123 */       return;
/*     */     }
/*     */ 
/* 126 */     for (StrategyParameterPanel parameterPanel : parametersContainerPanel.getParams()) {
/* 127 */       String parameterId = parameterPanel.getParameterId();
/* 128 */       Class type = parameterPanel.getParameterType();
/* 129 */       boolean dateTimeAsLong = parameterPanel.isDateTimeAsLong();
/*     */ 
/* 131 */       StrategyParameterLocal strategyParameter = strategyPreset.getStrategyParameterBy(parameterId);
/* 132 */       if (strategyParameter != null) {
/* 133 */         JComponent component = parameterPanel.getValueComponent();
/* 134 */         strategyParameter.setValue(extractComponentValue(component, type, dateTimeAsLong));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<StrategyParameterLocal> retrievePresetParameters(String presetName, StrategyParamsPanel parametersContainerPanel)
/*     */   {
/* 141 */     List strategyParameters = new ArrayList();
/*     */ 
/* 143 */     if (parametersContainerPanel.getParams() != null)
/*     */     {
/* 145 */       for (StrategyParameterPanel parameterPanel : parametersContainerPanel.getParams())
/*     */       {
/* 147 */         String id = parameterPanel.getParameterId();
/* 148 */         JLabel nameLabel = parameterPanel.getNameComponent();
/* 149 */         String name = nameLabel.getText();
/* 150 */         Class type = parameterPanel.getParameterType();
/* 151 */         double stepSize = parameterPanel.getParameterStepSize();
/* 152 */         boolean mandatory = parameterPanel.isMandatory();
/* 153 */         boolean readOnly = parameterPanel.isReadOnly();
/* 154 */         String description = parameterPanel.getDescription();
/* 155 */         boolean dateTimeAsLong = parameterPanel.isDateTimeAsLong();
/*     */ 
/* 157 */         StrategyParameterLocal strategyParameter = new StrategyParameterLocal(presetName, id, name, description, mandatory, readOnly, stepSize, type, null, dateTimeAsLong);
/* 158 */         JComponent component = parameterPanel.getValueComponent();
/* 159 */         strategyParameter.setValue(extractComponentValue(component, type, dateTimeAsLong));
/*     */ 
/* 161 */         strategyParameters.add(strategyParameter);
/*     */       }
/*     */     }
/*     */ 
/* 165 */     return strategyParameters;
/*     */   }
/*     */ 
/*     */   private StrategyPreset createDefaultPreset(StrategyNewBean strategyBean) {
/* 169 */     IStrategy strategy = strategyBean.getStrategy();
/* 170 */     if (strategy == null) {
/* 171 */       return null;
/*     */     }
/* 173 */     Field[] fields = strategy.getClass().getFields();
/* 174 */     List strategyParameters = new ArrayList();
/* 175 */     for (Field field : fields) {
/* 176 */       Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 177 */       if (configurable == null) continue;
/*     */       try {
/* 179 */         String fieldName = configurable.value() != null ? configurable.value() : field.getName();
/* 180 */         Class fieldType = field.getType();
/* 181 */         Object fieldValue = field.get(strategy);
/* 182 */         double stepSize = configurable.stepSize();
/* 183 */         boolean mandatory = configurable.obligatory();
/* 184 */         boolean readOnly = configurable.readOnly();
/* 185 */         String description = !ObjectUtils.isNullOrEmpty(configurable.description()) ? configurable.description() : fieldName;
/* 186 */         boolean dateTimeAsLong = configurable.datetimeAsLong();
/*     */ 
/* 188 */         if (fieldType.isEnum()) {
/* 189 */           StrategyParameterLocal strategyParameter = new StrategyParameterLocal("DEFAULT_PRESET_ID", field.getName(), fieldName, description, mandatory, readOnly, stepSize, fieldType, new CollectionParameterWrapper(fieldType.getEnumConstants(), fieldValue), dateTimeAsLong);
/*     */ 
/* 191 */           strategyParameters.add(strategyParameter);
/*     */         } else {
/* 193 */           StrategyParameterLocal strategyParameter = new StrategyParameterLocal("DEFAULT_PRESET_ID", field.getName(), fieldName, description, mandatory, readOnly, stepSize, fieldType, fieldValue, dateTimeAsLong);
/* 194 */           strategyParameters.add(strategyParameter);
/*     */         }
/*     */       }
/*     */       catch (Exception ex) {
/* 198 */         LOGGER.error(ex.getMessage(), ex);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 203 */     StrategyPreset strategyPreset = new StrategyPreset("DEFAULT_PRESET_ID", "Default Preset", strategyParameters);
/* 204 */     return strategyPreset;
/*     */   }
/*     */ 
/*     */   public void savePresets(StrategyNewBean strategyBean, List<StrategyPreset> strategyPresets)
/*     */   {
/* 210 */     Collections.sort(strategyPresets, new StrategyPresetsComparatorByName(null));
/*     */ 
/* 212 */     strategyBean.setStrategyPresets(strategyPresets);
/*     */ 
/* 214 */     Preferences userRoot = Preferences.userRoot();
/*     */     try
/*     */     {
/* 217 */       Preferences presetsNode = userRoot.node("presets");
/*     */ 
/* 219 */       for (StrategyPreset strategyPreset : strategyPresets)
/*     */       {
/* 221 */         if ("DEFAULT_PRESET_ID".equals(strategyPreset.getId()))
/*     */         {
/*     */           continue;
/*     */         }
/* 225 */         if (strategyPreset.isModified()) {
/* 226 */           strategyPreset = strategyPreset.getInitialState();
/*     */         }
/*     */ 
/* 229 */         presetNode = presetsNode.node("preset".concat("@").concat(strategyPreset.getId()));
/* 230 */         presetNode.put("id", strategyPreset.getId());
/* 231 */         presetNode.put("name", strategyPreset.getName());
/*     */ 
/* 233 */         List strategyParameters = strategyPreset.getStrategyParameters();
/*     */ 
/* 235 */         for (StrategyParameterLocal strategyParameter : strategyParameters) {
/* 236 */           Preferences parameterNode = presetNode.node("parameter".concat("@").concat(strategyParameter.getId()));
/*     */ 
/* 238 */           parameterNode.put("id", strategyParameter.getId());
/*     */ 
/* 240 */           Class type = strategyParameter.getType();
/* 241 */           parameterNode.put("type", type.getName());
/*     */ 
/* 243 */           Object value = strategyParameter.getValue();
/*     */ 
/* 245 */           if ((value instanceof CollectionParameterWrapper)) {
/* 246 */             CollectionParameterWrapper parameterWrapper = (CollectionParameterWrapper)value;
/* 247 */             Object selectedValue = parameterWrapper.getSelectedValue();
/* 248 */             parameterNode.put("value", ((Enum)selectedValue).name());
/* 249 */           } else if ((value instanceof Date)) {
/* 250 */             Date date = (Date)value;
/* 251 */             parameterNode.put("value", String.valueOf(date.getTime()));
/* 252 */           } else if ((value instanceof Calendar)) {
/* 253 */             Calendar calendar = (Calendar)value;
/* 254 */             parameterNode.put("value", String.valueOf(calendar.getTime().getTime()));
/* 255 */           } else if ((value instanceof Period)) {
/* 256 */             parameterNode.put("value", ((Period)value).name());
/*     */           } else {
/* 258 */             parameterNode.put("value", value.toString());
/*     */           }
/*     */         }
/*     */       }
/*     */       Preferences presetNode;
/* 262 */       File oldPresetsFile = getPresetsFile(strategyBean);
/* 263 */       if (oldPresetsFile.exists()) {
/* 264 */         oldPresetsFile.delete();
/*     */       }
/* 266 */       File file = strategyBean.getStrategyBinaryFile();
/* 267 */       File directory = file.getParentFile();
/* 268 */       File presetsFile = new File(directory, strategyBean.getName().concat(".xml"));
/* 269 */       presetsFile.createNewFile();
/* 270 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(presetsFile));
/* 271 */       presetsNode.exportSubtree(os);
/* 272 */       presetsNode.removeNode();
/* 273 */       os.close();
/*     */     }
/*     */     catch (Exception ex) {
/* 276 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<StrategyPreset> loadPresets(StrategyNewBean strategyBean)
/*     */   {
/* 283 */     List strategyPresets = new ArrayList();
/*     */ 
/* 285 */     StrategyPreset defaultPreset = createDefaultPreset(strategyBean);
/* 286 */     if (defaultPreset == null) {
/* 287 */       return strategyPresets;
/*     */     }
/* 289 */     strategyPresets.add(defaultPreset);
/*     */ 
/* 291 */     List defaultParams = defaultPreset.getStrategyParameters();
/*     */ 
/* 293 */     File presetsFile = getPresetsFile(strategyBean);
/*     */ 
/* 295 */     if ((presetsFile == null) || (!presetsFile.exists())) {
/* 296 */       return strategyPresets;
/*     */     }
/*     */     try
/*     */     {
/* 300 */       Preferences.importPreferences(new FileInputStream(presetsFile));
/* 301 */       Preferences userPreferences = Preferences.userRoot();
/*     */ 
/* 303 */       Preferences presetsNode = userPreferences.node("presets");
/* 304 */       for (String presetNode : presetsNode.childrenNames())
/*     */       {
/* 306 */         Preferences preset = presetsNode.node(presetNode);
/* 307 */         String presetId = preset.get("id", null);
/* 308 */         String name = preset.get("name", null);
/*     */ 
/* 310 */         List strategyParameters = new ArrayList();
/*     */ 
/* 313 */         for (StrategyParameterLocal defaultParam : defaultParams)
/*     */         {
/* 315 */           StrategyParameterLocal parameter = null;
/* 316 */           String parameterNodeName = "parameter".concat("@").concat(defaultParam.getId());
/*     */ 
/* 318 */           boolean addDefaultParam = false;
/*     */ 
/* 320 */           if (preset.nodeExists(parameterNodeName))
/*     */           {
/* 322 */             Preferences parameterNode = preset.node(parameterNodeName);
/*     */ 
/* 324 */             String paramId = parameterNode.get("id", null);
/* 325 */             String paramName = defaultParam.getName();
/* 326 */             String paramType = parameterNode.get("type", null);
/* 327 */             String paramValue = parameterNode.get("value", null);
/*     */ 
/* 329 */             Class typeClass = null;
/*     */             try {
/* 331 */               typeClass = getPrimitiveClass(paramType);
/*     */             } catch (IllegalArgumentException ex) {
/* 333 */               typeClass = Class.forName(paramType);
/*     */             }
/*     */ 
/* 337 */             if (typeClass.equals(defaultParam.getType())) {
/* 338 */               Object resultValue = getValueByType(paramValue, typeClass);
/* 339 */               parameter = new StrategyParameterLocal(presetId, paramId, paramName, defaultParam.getDescription(), defaultParam.isMandatory(), defaultParam.isReadOnly(), defaultParam.getStepSize(), typeClass, resultValue, defaultParam.isDateAsLong());
/*     */             }
/*     */             else
/*     */             {
/* 350 */               addDefaultParam = true;
/*     */             }
/*     */           }
/*     */           else {
/* 354 */             addDefaultParam = true;
/*     */           }
/*     */ 
/* 357 */           if (addDefaultParam) {
/* 358 */             parameter = new StrategyParameterLocal(presetId, defaultParam.getId(), defaultParam.getName(), defaultParam.getDescription(), defaultParam.isMandatory(), defaultParam.isReadOnly(), defaultParam.getStepSize(), defaultParam.getType(), defaultParam.getValue(), defaultParam.isDateAsLong());
/*     */           }
/*     */ 
/* 370 */           strategyParameters.add(parameter);
/*     */         }
/*     */ 
/* 376 */         StrategyPreset strategyPreset = new StrategyPreset(presetId, name, strategyParameters);
/* 377 */         strategyPresets.add(strategyPreset);
/*     */       }
/* 379 */       presetsNode.removeNode();
/*     */     }
/*     */     catch (InvalidPreferencesFormatException ex) {
/* 382 */       LOGGER.warn("Failed to load presets from xml file as preferences, will try to load in old XML format");
/*     */ 
/* 385 */       fillPresetsFromOldFormat(presetsFile, strategyPresets, defaultParams);
/*     */ 
/* 388 */       if (strategyPresets.size() > 1)
/* 389 */         savePresets(strategyBean, strategyPresets);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 393 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */ 
/* 396 */     Collections.sort(strategyPresets, new StrategyPresetsComparatorByName(null));
/*     */ 
/* 398 */     return strategyPresets;
/*     */   }
/*     */ 
/*     */   private Object getValueByType(String paramValue, Class typeClass)
/*     */   {
/* 404 */     if (typeClass.isEnum())
/*     */     {
/* 406 */       Enum[] enumConstants = (Enum[])(Enum[])typeClass.getEnumConstants();
/* 407 */       for (Enum enumConstant : enumConstants)
/* 408 */         if ((enumConstant.toString().equals(paramValue)) || (enumConstant.name().equals(paramValue))) {
/* 409 */           CollectionParameterWrapper wrapper = new CollectionParameterWrapper(enumConstants, enumConstant);
/* 410 */           return wrapper;
/*     */         }
/*     */     }
/*     */     else {
/* 414 */       if (typeClass.isAssignableFrom(Byte.class))
/* 415 */         return new Byte(paramValue);
/* 416 */       if (typeClass.equals(Byte.TYPE))
/* 417 */         return Byte.valueOf(Byte.parseByte(paramValue));
/* 418 */       if (typeClass.isAssignableFrom(Short.class))
/* 419 */         return new Short(paramValue);
/* 420 */       if (typeClass.equals(Short.TYPE))
/* 421 */         return Short.valueOf(Short.parseShort(paramValue));
/* 422 */       if (typeClass.isAssignableFrom(Integer.class))
/* 423 */         return new Integer(paramValue);
/* 424 */       if (typeClass.equals(Integer.TYPE))
/* 425 */         return Integer.valueOf(Integer.parseInt(paramValue));
/* 426 */       if (typeClass.isAssignableFrom(Long.class))
/* 427 */         return new Long(paramValue);
/* 428 */       if (typeClass.equals(Long.TYPE))
/* 429 */         return Long.valueOf(Long.parseLong(paramValue));
/* 430 */       if (typeClass.isAssignableFrom(Double.class))
/* 431 */         return new Double(paramValue);
/* 432 */       if (typeClass.equals(Double.TYPE))
/* 433 */         return Double.valueOf(Double.parseDouble(paramValue));
/* 434 */       if (typeClass.isAssignableFrom(Boolean.class))
/* 435 */         return Boolean.valueOf(paramValue);
/* 436 */       if (typeClass.equals(Boolean.TYPE))
/* 437 */         return Boolean.valueOf(Boolean.parseBoolean(paramValue));
/* 438 */       if (typeClass.isAssignableFrom(String.class))
/* 439 */         return String.valueOf(paramValue);
/* 440 */       if (typeClass.isAssignableFrom(Date.class))
/* 441 */         return new Date(Long.parseLong(paramValue));
/* 442 */       if (typeClass.isAssignableFrom(Calendar.class)) {
/* 443 */         Date date = new Date(Long.parseLong(paramValue));
/* 444 */         Calendar cal = Calendar.getInstance();
/* 445 */         cal.setTime(date);
/* 446 */         return cal;
/* 447 */       }if (typeClass.isAssignableFrom(Period.class)) {
/* 448 */         Period[] values = Period.values();
/* 449 */         for (int i = 0; i < values.length; i++) {
/* 450 */           Period period = values[i];
/* 451 */           if ((paramValue.equals(period.toString())) || (paramValue.equals(period.name()))) {
/* 452 */             return period;
/*     */           }
/*     */         }
/* 455 */         return null;
/* 456 */       }if (typeClass.isAssignableFrom(File.class)) {
/* 457 */         return new File(paramValue);
/*     */       }
/*     */     }
/* 460 */     LOGGER.warn("Unsupported type : [" + typeClass.getName() + "] , returning toString() implementation");
/*     */ 
/* 462 */     return paramValue.toString();
/*     */   }
/*     */ 
/*     */   public File getPresetsFile(StrategyNewBean strategyBean) {
/* 466 */     File file = strategyBean.getStrategyBinaryFile();
/* 467 */     if (file == null) {
/* 468 */       return null;
/*     */     }
/* 470 */     File directory = file.getParentFile();
/*     */ 
/* 472 */     String presetsFileName = strategyBean.getName().concat(".xml");
/*     */ 
/* 474 */     File presetsFile = new File(directory, presetsFileName);
/* 475 */     if (!file.exists()) {
/* 476 */       return null;
/*     */     }
/* 478 */     return presetsFile;
/*     */   }
/*     */ 
/*     */   public static Object extractComponentValue(JComponent component, Class<?> fieldClass, boolean dateTimeAsLong)
/*     */   {
/* 483 */     Object value = null;
/*     */ 
/* 485 */     if ((component instanceof JSpinner))
/* 486 */       value = ((JSpinner)component).getValue();
/* 487 */     else if ((component instanceof JTextField))
/* 488 */       value = ((JTextField)component).getText();
/* 489 */     else if ((component instanceof JComboBox))
/* 490 */       value = ((JComboBox)component).getSelectedItem();
/* 491 */     else if ((component instanceof JDateChooser)) {
/* 492 */       if (Calendar.class.isAssignableFrom(fieldClass)) {
/* 493 */         value = ((JDateChooser)component).getCalendar();
/*     */       } else {
/* 495 */         Date date = ((JDateChooser)component).getDate();
/* 496 */         if (((Long.TYPE.equals(fieldClass)) || (Long.class.equals(fieldClass))) && (dateTimeAsLong) && (date != null))
/* 497 */           value = Long.valueOf(date.getTime());
/*     */         else
/* 499 */           value = date;
/*     */       }
/*     */     }
/* 502 */     else if ((component instanceof JCheckBox))
/* 503 */       value = Boolean.valueOf(((JCheckBox)component).isSelected());
/* 504 */     else if ((component instanceof StrategiesTextFieldAndButton)) {
/* 505 */       value = ((StrategiesTextFieldAndButton)component).getTextField().getText();
/*     */     }
/*     */ 
/* 508 */     return value;
/*     */   }
/*     */ 
/*     */   public boolean parametersValid(StrategyParamsPanel parametersContainerPanel)
/*     */   {
/* 517 */     boolean valid = true;
/* 518 */     StringBuffer errorMessage = new StringBuffer();
/*     */ 
/* 521 */     if (parametersContainerPanel == null) {
/* 522 */       return true;
/*     */     }
/* 524 */     for (StrategyParameterPanel parameterPanel : parametersContainerPanel.getParams())
/*     */     {
/* 526 */       Class type = parameterPanel.getParameterType();
/* 527 */       boolean dateTimeAsLong = parameterPanel.isDateTimeAsLong();
/*     */ 
/* 529 */       Object value = extractComponentValue(parameterPanel.getValueComponent(), type, dateTimeAsLong);
/* 530 */       if ((parameterPanel.isMandatory()) && ((value == null) || (value.toString().length() == 0))) {
/* 531 */         errorMessage.append("Mandatory parameter \"" + parameterPanel.getNameComponent().getText() + "\"  is not set. \n");
/* 532 */         valid = false;
/*     */       }
/*     */     }
/*     */ 
/* 536 */     if (errorMessage.length() > 0)
/*     */     {
/* 538 */       JOptionPane.showMessageDialog(parametersContainerPanel, errorMessage.toString(), "Strategy parameters", 0);
/*     */     }
/*     */ 
/* 545 */     return valid;
/*     */   }
/*     */ 
/*     */   private Class<?> getPrimitiveClass(String name) {
/* 549 */     if (name.equals("byte"))
/* 550 */       return Byte.TYPE;
/* 551 */     if (name.equals("short"))
/* 552 */       return Short.TYPE;
/* 553 */     if (name.equals("int"))
/* 554 */       return Integer.TYPE;
/* 555 */     if (name.equals("long"))
/* 556 */       return Long.TYPE;
/* 557 */     if (name.equals("double"))
/* 558 */       return Double.TYPE;
/* 559 */     if (name.equals("boolean"))
/* 560 */       return Boolean.TYPE;
/* 561 */     if (name.equals("void")) {
/* 562 */       return Void.TYPE;
/*     */     }
/* 564 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */   private void fillPresetsFromOldFormat(File presetsFile, List<StrategyPreset> strategyPresets, List<StrategyParameterLocal> defaultParams)
/*     */   {
/*     */     try
/*     */     {
/* 590 */       DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/* 591 */       DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/* 592 */       Document doc = docBuilder.parse(presetsFile);
/* 593 */       if (doc == null) {
/* 594 */         return;
/*     */       }
/*     */ 
/* 597 */       doc.getDocumentElement().normalize();
/*     */ 
/* 599 */       NodeList listOfPresets = doc.getElementsByTagName("preset");
/*     */ 
/* 601 */       for (int i = 0; i < listOfPresets.getLength(); i++) {
/* 602 */         Node presetNode = listOfPresets.item(i);
/* 603 */         if (presetNode.getNodeType() != 1)
/*     */           continue;
/* 605 */         Element preset = (Element)presetNode;
/* 606 */         String presetId = preset.getAttribute("name");
/*     */ 
/* 608 */         NodeList variableList = preset.getElementsByTagName("variable");
/* 609 */         List presetParameters = new ArrayList();
/*     */ 
/* 611 */         for (int j = 0; j < variableList.getLength(); j++) {
/* 612 */           Node variableNode = variableList.item(j);
/* 613 */           Element variableElement = (Element)variableNode;
/*     */ 
/* 615 */           String paramName = readXMLParameter(variableElement, "name");
/* 616 */           String paramValue = readXMLParameter(variableElement, "value");
/* 617 */           String paramType = readXMLParameter(variableElement, "type");
/*     */ 
/* 619 */           if ((paramName == null) || (paramValue == null) || (paramType == null))
/*     */             continue;
/* 621 */           Class typeClass = null;
/*     */           try
/*     */           {
/* 624 */             typeClass = getPrimitiveClass(paramType);
/*     */           }
/*     */           catch (IllegalArgumentException ex) {
/* 627 */             if (paramType.startsWith("enum"))
/* 628 */               typeClass = Class.forName(paramType.split("#")[1]);
/*     */             else {
/* 630 */               typeClass = Class.forName(paramType);
/*     */             }
/*     */           }
/*     */ 
/* 634 */           Object resultValue = getValueByType(paramValue, typeClass);
/* 635 */           StrategyParameterLocal parameter = new StrategyParameterLocal(presetId, paramName, paramName, paramName, false, false, 0.0D, typeClass, resultValue, false);
/* 636 */           presetParameters.add(parameter);
/*     */         }
/*     */ 
/* 641 */         List finalPresetParameters = new ArrayList(defaultParams.size());
/* 642 */         boolean differsFromDefault = false;
/*     */ 
/* 645 */         for (StrategyParameterLocal defaultParam : defaultParams) {
/* 646 */           StrategyParameterLocal param = getParameter(defaultParam.getId(), defaultParam.getType(), presetParameters);
/* 647 */           if (param != null) {
/* 648 */             param.setName(defaultParam.getName());
/* 649 */             param.setMandatory(defaultParam.isMandatory());
/* 650 */             param.setStepSize(defaultParam.getStepSize());
/* 651 */             finalPresetParameters.add(param);
/* 652 */             differsFromDefault = true;
/*     */           } else {
/* 654 */             finalPresetParameters.add(defaultParam);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 659 */         if (differsFromDefault) {
/* 660 */           StrategyPreset newPreset = new StrategyPreset(presetId, presetId, finalPresetParameters);
/* 661 */           strategyPresets.add(newPreset);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (SAXParseException err)
/*     */     {
/* 667 */       LOGGER.warn(err.getMessage(), err);
/*     */     } catch (SAXException e) {
/* 669 */       Exception x = e.getException();
/* 670 */       x = x == null ? e : x;
/* 671 */       LOGGER.warn(x.getMessage(), x);
/*     */     } catch (Exception ex) {
/* 673 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private StrategyParameterLocal getParameter(String paramId, Class<?> paramType, List<StrategyParameterLocal> presetParams)
/*     */   {
/* 679 */     for (StrategyParameterLocal param : presetParams) {
/* 680 */       if ((param.getId().equals(paramId)) && (param.getType().equals(paramType))) {
/* 681 */         return param;
/*     */       }
/*     */     }
/* 684 */     return null;
/*     */   }
/*     */ 
/*     */   private static String readXMLParameter(Element from, String paramName) {
/* 688 */     NodeList nameAttributeList = from.getElementsByTagName(paramName);
/* 689 */     Element nameElement = (Element)nameAttributeList.item(0);
/* 690 */     if ((nameElement != null) && (nameElement.getChildNodes() != null) && (nameElement.getChildNodes().item(0) != null))
/*     */     {
/* 693 */       return nameElement.getChildNodes().item(0).getNodeValue().trim();
/*     */     }
/* 695 */     return null;
/*     */   }
/*     */ 
/*     */   private class StrategyPresetsComparatorByName
/*     */     implements Comparator<StrategyPreset>
/*     */   {
/*     */     private StrategyPresetsComparatorByName()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int compare(StrategyPreset o1, StrategyPreset o2)
/*     */     {
/* 580 */       return o1.getName().compareTo(o2.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   private class StrategyParametersComparatorByName
/*     */     implements Comparator<StrategyParameterLocal>
/*     */   {
/*     */     private StrategyParametersComparatorByName()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int compare(StrategyParameterLocal o1, StrategyParameterLocal o2)
/*     */     {
/* 571 */       return o1.getName().compareTo(o2.getName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.DefaultStrategyPresetsController
 * JD-Core Version:    0.6.0
 */