package bl4ckscor3.bot.bl4ckb0t.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import bl4ckscor3.bot.bl4ckb0t.commands.*;
import bl4ckscor3.bot.bl4ckb0t.localization.L10N;
import bl4ckscor3.bot.bl4ckb0t.misc.LinkTitle;
import bl4ckscor3.bot.bl4ckb0t.misc.SpellingCorrection;
import bl4ckscor3.bot.bl4ckb0t.misc.YouTubeStats;
import bl4ckscor3.bot.bl4ckb0t.util.Utilities;

public class Listener extends ListenerAdapter 
{
	private final String p = "-";
	public static boolean enabled = true;
	public boolean isCounting = false;
	public static final LinkedList<ICommand> commands = new LinkedList<>();
	
	public Listener()
	{
		commands.add(new Calculate());
		commands.add(new ChangeNick());
		commands.add(new Decide());
		commands.add(new Disable());
		commands.add(new Draw());
		commands.add(new Enable());
		commands.add(new ETS2MPUpdate());
		commands.add(new Help());
		commands.add(new Join());
		commands.add(new Kick());
		commands.add(new Forge());
		commands.add(new Language());
		commands.add(new Leave());
		commands.add(new Leet());
		commands.add(new ListChans());
		commands.add(new LongURL());
		commands.add(new RandomLetter());
		commands.add(new RandomNumber());
		commands.add(new Scramble());
		commands.add(new Select());
		commands.add(new Source());
		commands.add(new Stop());
		commands.add(new Twitch());
		commands.add(new Twitter());
		commands.add(new Vowels());
		commands.add(new Weather());
		commands.add(new XColor());
		commands.add(new YouTube());

		Help.setupHelpMenu(commands);
	}

	@Override
	public void onMessage(MessageEvent event) throws Exception
	{
		String cmdName = Utilities.toArgs(event.getMessage())[0];

		L10N.setLocalization(event.getChannel().getName(), L10N.chanLangs.containsKey(event.getChannel().getName()) ? L10N.getLocalization(event.getChannel().getName()) : "english");
		misc(event);

		if(!cmdName.startsWith(p))
			return;

		if(enabled)
		{
			for(ICommand cmd : commands)
			{
				if(cmdName.equalsIgnoreCase(p + cmd.getAlias()))
				{
					cmd.exe(event);
					return;
				}
			}
		}
		else
		{
			for(ICommand cmd : commands)
			{
				if((cmd instanceof Enable || cmd instanceof Disable) && event.getMessage().equalsIgnoreCase(p + cmd.getAlias()))
				{
					cmd.exe(event);
					return;
				}
			}
		}
	}	

	public void misc(MessageEvent event) throws Exception
	{
		String message = event.getMessage();
		
		if(message.startsWith(p))
			return;

		if(message.equals("?enabled"))
		{
			Utilities.chanMsg(event, "" + enabled);
			return;
		}

		if(enabled)
		{
			SpellingCorrection.checkForSpellingCorrection(event, message);
			
			//making sure the above messages dont get added as a latest message
			if(!SpellingCorrection.corrected)
				SpellingCorrection.updateLatestMessage(event.getChannel().getName(), event.getMessage(), event.getUser().getNick());
			else
				SpellingCorrection.corrected = false;
			
			//sending a welcome back message
			if(message.toLowerCase().startsWith("re ") || message.toLowerCase().equals("re"))
			{
				Utilities.chanMsg(event, "wb, " + event.getUser().getNick());
				return;
			}

			//youtube recognition
			if(message.contains("www.youtube.com/watch") || message.contains("youtu.be/"))
			{
				YouTubeStats.sendVideoStats(event);
				return;
			}

			//checking for urls and sending the title if available
			LinkTitle.checkForLinkAndSendTitle(event);
		}
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent event) throws MalformedURLException, IOException
	{
		if(enabled)
		{
			if(Utilities.isValidUser(event))
			{
				String[] args = Utilities.toArgs(event.getMessage());
				StringBuilder builder = new StringBuilder();
				String msg;

				if(args[0].equalsIgnoreCase("join"))
				{
					if(args.length > 2)
						Core.bot.sendIRC().joinChannel(args[1].startsWith("#") ? args[1] : "#" + args[1], args[2]);
					else
						Core.bot.sendIRC().joinChannel(args[1].startsWith("#") ? args[1] : "#" + args[1]);
				}
				else if(args[0].equalsIgnoreCase("leave"))
					Core.bot.sendRaw().rawLine("PART " + args[1] + " :" + L10N.strings.getString("part"));
				else if(args[0].startsWith("#"))
				{
					for(int i = 1; i < args.length; i++)
					{
						builder.append(args[i] + " ");
					}

					msg = builder.toString();

					if(args[1].startsWith("*"))
						Core.bot.sendIRC().action(args[0], msg.substring(1));
					else
						Core.bot.sendIRC().message(args[0], msg);
				}
				else if(args[0].equalsIgnoreCase("msg"))
				{
					for(int i = 2; i < args.length; i++)
					{
						builder.append(args[i] + " ");
					}

					msg = builder.toString();
					Core.bot.sendIRC().message(args[1], msg);
				}
			}
			else
			{
				for(String user : Utilities.getValidUsers())
				{
					Core.bot.sendIRC().message(user, event.getUser().getNick() + " just sent me this message: " + event.getMessage());
				}
			}
		}
	}

	@Override
	public void onConnect(ConnectEvent event) throws Exception
	{
		String[] channelsToJoin = Utilities.getAutoJoinChans();

		for(String s : channelsToJoin)
		{
			if(s.equals("#akino_germany"))
				Core.bot.sendIRC().joinChannel(s, Core.password);
			else
				Core.bot.sendIRC().joinChannel(s);
		}
	}
}