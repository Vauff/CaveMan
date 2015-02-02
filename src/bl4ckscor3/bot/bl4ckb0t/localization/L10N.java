package bl4ckscor3.bot.bl4ckb0t.localization;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import bl4ckscor3.bot.bl4ckb0t.core.Core;
import bl4ckscor3.bot.bl4ckb0t.util.Utilities;

public class L10N
{
	private static String language = "en";
	private static String country = "US";
	public static String langName = parseLangName(language + "_" + country);
	public static Locale currentLocale = new Locale(language, country);
	public static ResourceBundle strings = ResourceBundle.getBundle("bl4ckscor3.bot.bl4ckb0t.localization.bb", currentLocale);
	public static HashMap<String, String> chanLangs = new HashMap<String, String>();

	/**
	 * Changes the localization of the bot
	 * @param l - The language code
	 * @param c - The country code
	 * @param channel - The channel the language gets changed in
	 */
	public static void changeLocalization(String l, String c, String channel)
	{
		language = l;
		country = c;
		langName = parseLangName(language + "_" + country);
		update();
		
		if(chanLangs.containsKey(channel))
			chanLangs.replace(channel, chanLangs.get(channel), langName);
		else
			chanLangs.put(langName, channel);
	}

	/**
	 * Updates which language is used
	 */
	public static void update()
	{
		currentLocale = new Locale(language, country);
		strings = ResourceBundle.getBundle("bl4ckscor3.bot.bl4ckb0t.localization.bb", currentLocale);
	}
	
	/**
	 * Creates the language's name
	 */
	private static String parseLangName(String code)
	{
		if(code.equals("en_US"))
			return "english";
		else if(code.equals("de_DE"))
			return "german";
		else
			return "default";
	}
	
	/**
	 * Creates the language's code
	 * @param language - The language to return the code from
	 * @param code - The code to return (0 = language, 1 = country)
	 */
	public static String parseLangCode(String language, int code) throws ArrayIndexOutOfBoundsException
	{
		String langCode = "";

		switch(language)
		{
			case "english":
				langCode = "en_US";
				break;
			case "german":
				langCode = "de_DE";
				break;
			default:
				langCode = "none";
		}
		
		return langCode.split("_")[code];
	}
}