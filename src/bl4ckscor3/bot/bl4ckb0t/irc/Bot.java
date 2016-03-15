package bl4ckscor3.bot.bl4ckb0t.irc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.Security;

import javax.net.ssl.SSLSocket;

import bl4ckscor3.bot.bl4ckb0t.irc.event.MessageEvent;
import bl4ckscor3.bot.bl4ckb0t.irc.event.PrivateMessageEvent;

/**
 * Represents an IRC bot with a configuration and more
 * @author bl4ckscor3
 */
public class Bot
{
	private BotConfig config;
	private Connection conn;
	
	public Bot(BotConfig cfg)
	{
		config = cfg;
	}
	
	public BotConfig getConfig()
	{
		return config;
	}
	
	//------------------------------------\\
	
	/**
	 * Sends a message to a user or channel
	 * @param recipient Who to sent the message to
 	 * @param message The message to send
	 */
	public void sendMessage(String recipient, String message)
	{
		conn.out.println(String.format("PRIVMSG %s :%s", recipient, message));
	}
	
	//------------------------------------\\
	
	/**
	 * Starts the bot on a new thread
	 */
	public void start()
	{
		if(conn == null)
		{
			conn = new Connection(config);
			conn.establish();
		}
	}
	
	/**
	 * Establishes a connection with an IRC server and also manages events
	 * @author bl4ckscor3
	 */
	private class Connection
	{
		private BotConfig config;
		private Socket socket;
		private BufferedReader in;
		private CustomPrintWriter out;
		
		public Connection(BotConfig cfg)
		{
			config = cfg;
		}
		
		@SuppressWarnings("restriction")
		public void establish()
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

					if(read.isEmpty())
						continue;
					
					System.out.println(read);

					if(read.startsWith("PING"))
						out.println(read.replace("PING", "PONG"));
					else if(read.equals((":%s MODE %s :+" + (config.usesSSL() ? "Z" : "") + "i").replace("%s", config.getNick())))
					{
						out.println("PRIVMSG nickserv :identify " + config.getNickservPassword());
						out.println("JOIN #bl4ckb0tTest");
					}

					String[] content = read.split(" ");

					if(content[1].equals("PRIVMSG"))
					{
						if(content[2].startsWith("#"))
							Listener.executeEvent(config, new MessageEvent(content[0].split("!")[0].substring(1), content[2], read.split(":")[2]));
						else
							Listener.executeEvent(config, new PrivateMessageEvent(content[0].split("!")[0].substring(1), read.split(":")[2]));
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
