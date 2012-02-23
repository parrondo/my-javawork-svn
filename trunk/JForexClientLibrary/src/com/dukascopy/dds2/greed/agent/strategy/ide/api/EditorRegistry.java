package com.dukascopy.dds2.greed.agent.strategy.ide.api;

import com.dukascopy.dds2.greed.agent.strategy.ide.impl.EditorStatusBar;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.io.File;

public abstract interface EditorRegistry
{
  public abstract Editor openEditor(int paramInt, File paramFile, EditorStatusBar paramEditorStatusBar, ActionListener paramActionListener, ServiceSourceType paramServiceSourceType, String paramString1, String paramString2);

  public abstract Editor openEditor(int paramInt, File paramFile, EditorStatusBar paramEditorStatusBar, ActionListener paramActionListener, FocusListener paramFocusListener, ServiceSourceType paramServiceSourceType, String paramString1, String paramString2);

  public abstract Editor getEditor(int paramInt);

  public abstract void removeReference(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.api.EditorRegistry
 * JD-Core Version:    0.6.0
 */