package bl4ckscor3.bot.bl4ckb0t.irc.event;

/**
 * Represents a private message
 * @author bl4ckscor3
 */
public class PrivateMessageEvent extends Event
{
	private String username;
	private String message;
	
	/**
	 * @param un The username of the user who sent the message
	 * @param m The message that was sent
	 */
	public PrivateMessageEvent(String un, String m)
	{
		username = un;
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
	 * @return The message that was sent
	 */
	public String getMessage()
	{
		return message;
	}
}
