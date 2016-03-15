package bl4ckscor3.bot.bl4ckb0t.irc;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about an IRC bot
 * @author bl4ckscor3
 */
public class BotConfig
{
	private String nick;
	private String username;
	private String realName;
	private String nickservPassword;
	private String hostName;
	private int port;
	private boolean useSSL;
	private List<Listener> listeners = new ArrayList<Listener>();
	
	/**
	 * @param name The name of the bot
	 */
	public BotConfig setNick(String n)
	{
		nick = n;
		return this;
	}
	
	/**
	 * @param username The username of the bot
	 */
	public BotConfig setUsername(String u)
	{
		username = u;
		return this;
	}
	
	/**
	 * @param realName The real name of the bot
	 */
	public BotConfig setRealName(String rn)
	{
		realName = rn;
		return this;
	}
	
	/**
	 * @param nickservPassword The NickServ password of the bot
	 */
	public BotConfig setNickservPassword(String np)
	{
		nickservPassword = np;
		return this;
	}
	
	/**
	 * @param hostname The hostname of the server for the bot to use
	 */
	public BotConfig setHostName(String h)
	{
		hostName = h;
		return this;
	}
	
	/**
	 * @param port The port of the server for the bot to use
	 */
	public BotConfig setPort(int p)
	{
		port = p;
		return this;
	}
	
	/**
	 * @param use Wether or not to use an SSL connection
	 */
	public BotConfig useSSL(boolean use)
	{
		useSSL = use;
		return this;
	}
	
	/**
	 * @param listener The listener to add
	 */
	public BotConfig addListener(Listener l)
	{
		listeners.add(l);
		return this;
	}
	
	//------------------------------------\\
	
	/**
	 * @return The name of the bot
	 */
	public String getNick()
	{
		return nick;
	}
	
	/**
	 * @return The username of the bot
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * @return The real name of the bot
	 */
	public String getRealName()
	{
		return realName;
	}
	
	/**
	 * @return The NickServ password of the bot
	 */
	public String getNickservPassword()
	{
		return nickservPassword;
	}
	
	/**
	 * @return The hostname of the server for the bot to use
	 */
	public String getHostName()
	{
		return hostName;
	}
	
	/**
	 * @return The port of the server for the bot to use
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * @return Wether or not the connection should use SSL
	 */
	public boolean usesSSL()
	{
		return useSSL;
	}
	
	/**
	 * @return All the listeners of this bot
	 */
	public List<Listener> getListeners()
	{
		return listeners;
	}
}
