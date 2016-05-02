package bl4ckscor3.bot.bl4ckb0t;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import bl4ckscor3.bot.bl4ckb0t.commands.channel.Changelog;
import bl4ckscor3.bot.bl4ckb0t.logging.Logging;
import bl4ckscor3.bot.bl4ckb0t.util.Lists;
import bl4ckscor3.bot.bl4ckb0t.util.Utilities;

public class Startup
{
	/**
	 * Calls all methods in this class
	 */
	public static void callMethods() throws MalformedURLException, IOException
	{
		getChangelog();
		setAllowedUsers();
		setIgnoredUsers();
		setValidUsers();
		Core.bot.getConfig().populateArrayMap();
		CMDListener.setupCMDs();
	}
	
	/**
	 * Retrieves the changelog of the bot and saves it in the list Changelog.versions
	 */
	private static void getChangelog() throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/bl4ckscor3/bl4ckb0t/master/CHANGELOG.md").openStream()));
		String line = "";
		String currentVersion = "";
		boolean wip = false;

		while((line = reader.readLine()) != null)
		{
			line = line.replace("#", "").replace("*", "");
			
			if(Utilities.startsWithNumber(line))
			{
				if(line.endsWith("_WIP"))
				{
					wip = true;
					continue;
				}

				wip = false;
				currentVersion = line;
				Changelog.versions.put(currentVersion, new ArrayList<String>());
			}

			if(line.startsWith("-") && !line.startsWith("---") && !wip)
				Changelog.versions.get(currentVersion).add(line);
		}
		
		reader.close();
		Logging.info("All versions added to changelog list.");
	}

	/**
	 * Sets the default channels of the bot
	 */
	public static void setDefaultChans() throws MalformedURLException, IOException
	{
		if(Core.bot.isDevelopment())
		{
			Lists.addDefaultChan("#maunztesting");
			return;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://www.dropbox.com/s/8huobu7s0d9me3g/caveman%20chans.txt?dl=1").openStream()));

		for(String s : reader.readLine().split(","))
		{
			Lists.addDefaultChan(s);
		}

		reader.close();
	}

	/**
	 * Sets the users with a permission level of 2
	 */
	private static void setAllowedUsers() throws MalformedURLException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://www.dropbox.com/s/yf2nyiapu89hxq1/allowedUsers.txt?dl=1").openStream()));

		for(String s : reader.readLine().split(","))
		{
			Lists.addAllowedUser(s);
		}

		reader.close();
	}

	/**
	 * Sets the users with a permission level of 3
	 */
	private static void setValidUsers() throws MalformedURLException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://www.dropbox.com/s/mbms0dnop11f0gz/validUsers.txt?dl=1").openStream()));

		for(String s : reader.readLine().split(","))
		{
			Lists.addValidUser(s);
		}
		
		reader.close();
	}

	/**
	 * Sets the user the bot ignores
	 */
	private static void setIgnoredUsers() throws MalformedURLException, IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://www.dropbox.com/s/19tx2mak6zcjedr/ignoredUsers.txt?dl=1").openStream()));

		for(String s : reader.readLine().split(","))
		{
			Lists.addIgnoredUser(s);
		}
		
		reader.close();
	}
}
