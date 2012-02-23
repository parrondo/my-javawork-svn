package com.dukascopy.dds2.greed.connector;

import com.dukascopy.api.JFException;
import com.dukascopy.dds2.greed.connector.helpers.ExternalEngine;
import java.io.File;

public abstract interface IConverter
{
  public abstract StringBuilder convert(File paramFile, String paramString, ExternalEngine paramExternalEngine)
    throws JFException;

  public abstract StringBuilder convert(File paramFile, String paramString)
    throws JFException;

  public abstract boolean convert(File paramFile)
    throws JFException;

  public abstract boolean convert(File paramFile, ExternalEngine paramExternalEngine)
    throws JFException;

  public abstract boolean convert(StringBuilder paramStringBuilder, String paramString1, String paramString2)
    throws JFException;

  public abstract boolean convert(StringBuilder paramStringBuilder, String paramString)
    throws JFException;

  public abstract boolean convert(StringBuilder paramStringBuilder, String paramString1, String paramString2, ExternalEngine paramExternalEngine)
    throws JFException;

  public abstract String[] getCompilationResultLines();

  public abstract String[] getCompilationErrors();

  public abstract String[] getCompilationWarnings();

  public abstract StringBuilder getConvertionResult();

  public abstract String getCurrentIncludePath();

  public abstract void setCurrentIncludePath(String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.IConverter
 * JD-Core Version:    0.6.0
 */