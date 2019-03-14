package cn.greatoo.easymill.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Translator {
	
	private static Locale locale = new Locale("nl");
	private static ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
	
	private Translator() {
		//setLanguageNL();
	}
	
	//TODO - remove this if we decide to use language files outside the program
	public static void setLanguageNL() {
		locale = new Locale("nl");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageEN() {
		locale = new Locale("en");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageDE() {
		locale = new Locale("de");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageSE() {
		locale = new Locale("se");
		messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageFR() {
	    locale = new Locale("fr");
	    messages = ResourceBundle.getBundle("messages", locale);
	}
	
	public static void setLanguageCN() {
		locale = new Locale("cn");
		messages = ResourceBundle.getBundle("messages", locale);
	}

	//获取翻译值
	public static String getTranslation(final String key) {
		String k = "";
		try {
			k = new String(messages.getString(key).getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return k;
	}
	
	public static Locale getLocale() {
		return locale;
	}
}
