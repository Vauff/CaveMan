package bl4ckscor3.bot.bl4ckb0tX.commands;

import java.io.IOException;

import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputIRC;

import bl4ckscor3.bot.bl4ckb0tX.core.Core;
import bl4ckscor3.bot.bl4ckb0tX.core.Listener;
import bl4ckscor3.bot.bl4ckb0tX.util.Utilities;

public class Stop implements ICommand<MessageEvent>
{
	@Override
	public void exe(MessageEvent event) throws IOException, IrcException
	{
		if(Utilities.validUser(event))
		{
			if(event.getMessage().equalsIgnoreCase(event.getBot().getNick() + ", sleep!"))
			{
				Utilities.chanMsg(event, "k");
				Core.bot.sendRaw().rawLine("QUIT :My master sent me to sleep!");
				return;
			}

			String[] args = Utilities.toArgs(event.getMessage());

			if(args.length == 2)
			{
				switch(args[1])
				{
					case "yes":
						Utilities.chanMsg(event, "I will reboot, sir");
						Core.bot.sendRaw().rawLine("QUIT :My master sent me to sleep!");
						Core.createBot(); //creating another instance
						break;
					case "no":
						Utilities.chanMsg(event, "You wished that I don't reboot. Do you still like me?");
						Core.bot.sendRaw().rawLine("QUIT :My master sent me to sleep!");
						break;
					default:
						Utilities.notice(event, "Should I reboot? I cannot disconnect if I don't know that :(");
				}
			}
			else
			{
				switch(args.length)
				{
					case 1:
						Utilities.respond(event, "please tell me if I should reboot. Example: -stop no", true);
						break;
					case 3:
						Utilities.respond(event, "please only tell me if I should reboot and nothing else. Example: -stop no", true);
				}
			}
		}
		else
			Utilities.sorry(event);
	}

	@Override
	public String getAlias()
	{
		return "stop";
	}

	@Override
	public String getSyntax()
	{
		return "-stop <yes|no>";
	}

	@Override
	public String[] getUsage()
	{
		return new String[]
				{
				"-stop yes || Stops the bot and restarts it.",
				"-stop no || Stops the bot but doesn't restart it."
				};
	}

	@Override
	public String getNotes()
	{
		return "Only useable by OPs.";
	}
}
