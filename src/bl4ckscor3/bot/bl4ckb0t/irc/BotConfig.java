package bl4ckscor3.bot.bl4ckb0t.irc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.Security;

import javax.net.ssl.SSLSocket;

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
	
	//------------------------------------\\
	
	public void start()
	{
		new Connection(this);
	}
	
	//------------------------------------\\
	
	/**
	 * Establishes a connection with an IRC server and also manages events
	 * @author bl4ckscor3
	 */
	private class Connection
	{
		private Socket socket;
		private BufferedReader in;
		private CustomPrintWriter out;
		
		@SuppressWarnings("restriction")
		public Connection(BotConfig config)
		{
			try
			{
				if(config.usesSSL())
				{
					UtilSSLSocketFactory sslFactory;
					
					Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
					sslFactory = new UtilSSLSocketFactory().trustAllCertificates();
					socket = (SSLSocket)sslFactory.createSocket(config.getHostName(), config.getPort());
				}
				else
					socket = new Socket(config.getHostName(), config.getPort());
	
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new CustomPrintWriter(socket.getOutputStream(), true);

				out.println(String.format("USER %s %s %s :%s", config.getNick(), "canopus.uberspace.de", config.getNick(), config.getRealName()));
				out.println("NICK " + config.getNick());

				while(true)
				{
					String read = in.readLine();

					System.out.println(read);
					
					if(read.startsWith("PING"))
						out.println(read.replace("PING", "PONG"));
					else if(read.equals((":%s MODE %s :+" + (config.usesSSL() ? "Z" : "") + "i").replace("%s", config.getNick())))
						out.println("PRIVMSG nickserv :identify " + config.getNickservPassword());
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
