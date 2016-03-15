package bl4ckscor3.bot.bl4ckb0t.irc;

import bl4ckscor3.bot.bl4ckb0t.irc.event.Event;
import bl4ckscor3.bot.bl4ckb0t.irc.event.MessageEvent;
import bl4ckscor3.bot.bl4ckb0t.irc.event.PrivateMessageEvent;

/**
 * Handles events
 * @author bl4ckscor3
 */
public class Listener
{
	/**
	 * Executes event
	 * @param cfg The configuration of the bot
	 * @param event The event to execute
	 */
	public static void executeEvent(BotConfig cfg, Event event)
	{
		for(Listener l : cfg.getListeners())
		{
			if(event instanceof MessageEvent)
				l.onMessage((MessageEvent)event);
			else if(event instanceof PrivateMessageEvent)
				l.onPrivateMessage((PrivateMessageEvent)event);
		}
	}
	
	/**
	 * Triggered when a channel message is sent
	 * @param event The event
	 */
	public void onMessage(MessageEvent event){}
	
	/**
	 * Triggered when a private message is sent
	 * @param event The event
	 */
	public void onPrivateMessage(PrivateMessageEvent event){}
}
