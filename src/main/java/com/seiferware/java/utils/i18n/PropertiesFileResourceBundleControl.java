package com.seiferware.java.utils.i18n;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertiesFileResourceBundleControl extends ResourceBundle.Control {
	@Override
	public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
		if(format.equals("java.properties")) {
			String bundleName = toBundleName(baseName, locale);
			File f = new File(bundleName + ".properties");
			if(f.exists()) {
				return f.lastModified() > loadTime;
			}
		}
		return super.needsReload(baseName, locale, format, loader, bundle, loadTime);
	}
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
		if(format.equals("java.properties")) {
			String bundleName = toBundleName(baseName, locale);
			File f = new File(bundleName + ".properties");
			if(f.exists()) {
				Reader r = new FileReader(f);
				ResourceBundle result = new PropertyResourceBundle(r);
				r.close();
				return result;
			}
		}
		return super.newBundle(baseName, locale, format, loader, reload);
	}
}
