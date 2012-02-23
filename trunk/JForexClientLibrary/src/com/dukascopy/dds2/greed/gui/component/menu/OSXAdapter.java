/*     */ package com.dukascopy.dds2.greed.gui.component.menu;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OSXAdapter
/*     */   implements InvocationHandler
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(OSXAdapter.class);
/*     */   protected Object targetObject;
/*     */   protected Method targetMethod;
/*     */   protected String proxySignature;
/*     */   static Object macOSXApplication;
/*     */ 
/*     */   public static void setQuitHandler(Object target, Method quitHandler)
/*     */   {
/*  39 */     setHandler(new OSXAdapter("handleQuit", target, quitHandler));
/*     */   }
/*     */ 
/*     */   public static void setAboutHandler(Object target, Method aboutHandler)
/*     */   {
/*  45 */     boolean enableAboutMenu = (target != null) && (aboutHandler != null);
/*  46 */     if (enableAboutMenu) {
/*  47 */       setHandler(new OSXAdapter("handleAbout", target, aboutHandler));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  52 */       Method enableAboutMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledAboutMenu", new Class[] { Boolean.TYPE });
/*  53 */       enableAboutMethod.invoke(macOSXApplication, new Object[] { Boolean.valueOf(enableAboutMenu) });
/*     */     } catch (Exception ex) {
/*  55 */       LOGGER.error("OSXAdapter could not access the About Menu");
/*  56 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setPreferencesHandler(Object target, Method prefsHandler)
/*     */   {
/*  63 */     boolean enablePrefsMenu = (target != null) && (prefsHandler != null);
/*  64 */     if (enablePrefsMenu) {
/*  65 */       setHandler(new OSXAdapter("handlePreferences", target, prefsHandler));
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  70 */       Method enablePrefsMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledPreferencesMenu", new Class[] { Boolean.TYPE });
/*  71 */       enablePrefsMethod.invoke(macOSXApplication, new Object[] { Boolean.valueOf(enablePrefsMenu) });
/*     */     } catch (Exception ex) {
/*  73 */       LOGGER.error("OSXAdapter could not access the About Menu");
/*  74 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setFileHandler(Object target, Method fileHandler)
/*     */   {
/*  82 */     setHandler(new OSXAdapter("handleOpenFile", target, fileHandler)
/*     */     {
/*     */       public boolean callTarget(Object appleEvent)
/*     */       {
/*  86 */         if (appleEvent != null) {
/*     */           try {
/*  88 */             Method getFilenameMethod = appleEvent.getClass().getDeclaredMethod("getFilename", (Class[])null);
/*  89 */             String filename = (String)getFilenameMethod.invoke(appleEvent, (Object[])null);
/*  90 */             this.targetMethod.invoke(this.targetObject, new Object[] { filename });
/*     */           } catch (Exception ex) {
/*  92 */             OSXAdapter.LOGGER.debug(ex.getMessage(), ex);
/*     */           }
/*     */         }
/*  95 */         return true;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static void setHandler(OSXAdapter adapter) {
/*     */     try {
/* 103 */       Class applicationClass = Class.forName("com.apple.eawt.Application");
/* 104 */       if (macOSXApplication == null) {
/* 105 */         macOSXApplication = applicationClass.getConstructor((Class[])null).newInstance((Object[])null);
/*     */       }
/* 107 */       Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
/* 108 */       Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", new Class[] { applicationListenerClass });
/*     */ 
/* 110 */       Object osxAdapterProxy = Proxy.newProxyInstance(OSXAdapter.class.getClassLoader(), new Class[] { applicationListenerClass }, adapter);
/* 111 */       addListenerMethod.invoke(macOSXApplication, new Object[] { osxAdapterProxy });
/*     */     } catch (ClassNotFoundException cnfe) {
/* 113 */       LOGGER.error("This version of Mac OS X does not support the Apple EAWT.  ApplicationEvent handling has been disabled (" + cnfe + ")");
/*     */     } catch (Exception ex) {
/* 115 */       LOGGER.error("Mac OS X Adapter could not talk to EAWT:");
/* 116 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected OSXAdapter(String proxySignature, Object target, Method handler)
/*     */   {
/* 123 */     this.proxySignature = proxySignature;
/* 124 */     this.targetObject = target;
/* 125 */     this.targetMethod = handler;
/*     */   }
/*     */ 
/*     */   public boolean callTarget(Object appleEvent)
/*     */     throws InvocationTargetException, IllegalAccessException
/*     */   {
/* 132 */     Object result = this.targetMethod.invoke(this.targetObject, (Object[])null);
/* 133 */     return (result == null) || (Boolean.valueOf(result.toString()).booleanValue());
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 139 */     if (isCorrectMethod(method, args)) {
/* 140 */       boolean handled = callTarget(args[0]);
/* 141 */       setApplicationEventHandled(args[0], handled);
/*     */     }
/*     */ 
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   protected boolean isCorrectMethod(Method method, Object[] args)
/*     */   {
/* 150 */     return (this.targetMethod != null) && (this.proxySignature.equals(method.getName())) && (args.length == 1);
/*     */   }
/*     */ 
/*     */   protected void setApplicationEventHandled(Object event, boolean handled)
/*     */   {
/* 156 */     if (event != null)
/*     */       try {
/* 158 */         Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", new Class[] { Boolean.TYPE });
/*     */ 
/* 160 */         setHandledMethod.invoke(event, new Object[] { Boolean.valueOf(handled) });
/*     */       } catch (Exception ex) {
/* 162 */         LOGGER.error("OSXAdapter was unable to handle an ApplicationEvent: " + event, ex);
/*     */       }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.menu.OSXAdapter
 * JD-Core Version:    0.6.0
 */