package bl4ckscor3.bot.bl4ckb0t.irc.event;

/**
 * Represents a channel message
 * @author bl4ckscor3
 */
public class MessageEvent extends Event
{
	private String username;
	private String channel;
	private String message;
	
	/**
	 * @param un The username of the user who sent the message
	 * @param c The name of the channel the message got sent in
	 * @param m The message that was sent
	 */
	public MessageEvent(String un, String c, String m)
	{
		username = un;
		channel = c;
		message = m;
	}
	
	/**
	 * @return The username of the user who sent the message
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * @return The name of the channel the message got sent in
	 */
	public String getChannel()
	{
		return channel;
	}
	
	/**
	 * @return The message that was sent
	 */
	public String getMessage()
	{
		return message;
	}
}
