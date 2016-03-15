package bl4ckscor3.bot.bl4ckb0t;

import bl4ckscor3.bot.bl4ckb0t.irc.Listener;
import bl4ckscor3.bot.bl4ckb0t.irc.event.MessageEvent;
import bl4ckscor3.bot.bl4ckb0t.irc.event.PrivateMessageEvent;

/**
 * A temporary listener class
 * @author bl4ckscor3
 */
public class TempListener extends Listener
{
	@Override
	public void onMessage(MessageEvent event)
	{
		TempMain.bot.sendMessage(event.getChannel(), event.getMessage());
	}
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		TempMain.bot.sendMessage(event.getUsername(), event.getMessage());
	}
}
