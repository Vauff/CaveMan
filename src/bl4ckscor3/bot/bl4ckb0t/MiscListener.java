package bl4ckscor3.bot.bl4ckb0t;

import java.io.IOException;
import java.net.MalformedURLException;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import bl4ckscor3.bot.bl4ckb0t.commands.privmsg.BasePrivateCommand;
import bl4ckscor3.bot.bl4ckb0t.logging.Logging;
import bl4ckscor3.bot.bl4ckb0t.misc.LinkTitle;
import bl4ckscor3.bot.bl4ckb0t.misc.SpellingCorrection;
import bl4ckscor3.bot.bl4ckb0t.misc.YouTubeStats;
import bl4ckscor3.bot.bl4ckb0t.util.Lists;
import bl4ckscor3.bot.bl4ckb0t.util.Utilities;

public class MiscListener extends ListenerAdapter<Bot>
{
	@Override
	public void onMessage(MessageEvent<Bot> event) throws Exception
	{
		if(Utilities.isIgnored(event.getUser().getNick()))
		{
			Logging.warn("Ignoring user " + event.getUser().getNick());
			return;
		}

		String message = event.getMessage();

		if(Core.bot.getConfig().getValue("ping"))
		{
			if(event.getMessage().startsWith("-ping"))
			{
				Utilities.chanMsg(event, "Pong!");
				return;
			}
			else if(Core.bot.getConfig().getValue("allowCommandAliases") && event.getMessage().startsWith("-pong"))
			{
				Utilities.chanMsg(event, "Ping!");
				return;
			}
		}

		if(message.startsWith(CMDListener.cmdPrefix))
			return;

		if(message.startsWith("?enabled"))
		{
			if(message.startsWith("?enabled #") && Core.bot.isEnabled())
				Utilities.chanMsg(event, message.split(" ")[1] + ": " + Core.bot.getChannelStates().get(message.split(" ")[1].replace("#", "")));
			else
				Utilities.chanMsg(event, "global: " + Core.bot.isEnabled());
			return;
		}

		if(Core.bot.isEnabled())
		{
			SpellingCorrection.checkForSpellingCorrection(event, message);

			//making sure the above messages dont get added as a latest message
			if(!SpellingCorrection.corrected)
				SpellingCorrection.updateLatestMessage(event.getChannel().getName(), event.getMessage(), event.getUser().getNick());
			else
				SpellingCorrection.corrected = false;

			//sending a welcome back message
			if(Core.bot.getConfig().getValue("showWelcomeBackMsg") && (message.toLowerCase().startsWith("re ") || message.toLowerCase().equals("re")))
				Utilities.chanMsg(event, "wb, " + event.getUser().getNick());
			//youtube recognition
			else if(Core.bot.getConfig().getValue("showYouTubeStats") && (message.contains("www.youtube.com/watch") || message.contains("youtu.be/")))
				YouTubeStats.sendVideoStats(event);
			//checking for urls and sending the title if available
			else
				LinkTitle.checkForLinkAndSendTitle(event);
		}
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<Bot> event) throws Exception
	{
		if(Utilities.isIgnored(event.getUser().getNick()))
		{
			Logging.warn("Ignoring user " + event.getUser().getNick());
			return;
		}

		if(!event.getMessage().startsWith(CMDListener.cmdPrefix))
		{
			for(BasePrivateCommand<PrivateMessageEvent<Bot>> cmd : CMDListener.privCommands)
			{
				if(event.getMessage().startsWith(cmd.getAlias()))
					return;
			}

			for(String user : Lists.getValidUsers())
			{
				Core.bot.sendCustomMessage(user, event.getUser().getNick() + ": " + event.getMessage());
			}
		}
		else
			Utilities.pm(event.getUser().getNick(), "Commands can only be sent through channel messages. Use -help in a channel to get more info.");
	}

	@Override
	public void onConnect(ConnectEvent<Bot> event) throws MalformedURLException, IOException
	{
		Startup.setDefaultChans();
		Logging.info("Joining channels...");
		Core.bot.joinDefaults();
		Logging.info("Bot successfully connected!");
	}

	@Override
	public void onAction(ActionEvent<Bot> event) throws Exception
	{
		if(Utilities.isIgnored(event.getUser().getNick()))
		{
			Logging.warn("Ignoring user " + event.getUser().getNick());
			return;
		}

		if(Core.bot.getConfig().getValue("shrugs") && event.getAction().equals("shrugs"))
			Core.bot.sendCustomMessage(event.getChannel().getName(), "¯\\_(ツ)_/¯");
	}
}