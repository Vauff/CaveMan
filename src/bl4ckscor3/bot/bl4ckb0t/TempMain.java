package bl4ckscor3.bot.bl4ckb0t;

import bl4ckscor3.bot.bl4ckb0t.irc.Bot;
import bl4ckscor3.bot.bl4ckb0t.irc.BotConfig;
import bl4ckscor3.bot.bl4ckb0t.util.Passwords;

/**
 * A temporary class with a main method to test the bot
 * @author bl4ckscor3
 */
public class TempMain
{
	public static Bot bot;
	
	public static void main(String[] args)
	{
		BotConfig config = new BotConfig()
				.setNick("bl4ckb0t1")
				.setUsername("bl4ckb0t2")
				.setRealName("bl4ckb0t3")
				.setNickservPassword(Passwords.NICKSERV.getPassword())
				.setHostName("irc.esper.net")
				.setPort(6697)
				.useSSL(true)
				.addListener(new TempListener());

		bot = new Bot(config);
		bot.start();
	}
}
