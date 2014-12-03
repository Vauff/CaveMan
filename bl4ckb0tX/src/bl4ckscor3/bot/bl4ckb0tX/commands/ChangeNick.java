package bl4ckscor3.bot.bl4ckb0tX.commands;

import java.io.IOException;
import java.net.MalformedURLException;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputIRC;

import bl4ckscor3.bot.bl4ckb0tX.core.Core;
import bl4ckscor3.bot.bl4ckb0tX.util.Utilities;

public class ChangeNick implements ICommand<MessageEvent>
{
	@Override
	public void exe(MessageEvent event) throws MalformedURLException, IOException
	{
		String[] args = Utilities.toArgs(event.getMessage());

		if(args.length == 2)
		{
			if(Utilities.isValidUser(event))
			{
				if(!args[1].equalsIgnoreCase("d"))
					Core.bot.sendIRC().changeNick(args[1]);
				else
					Core.bot.sendIRC().changeNick("bl4ckb0t");
			}
			else
				Utilities.sorry(event);
		}
		else
			Utilities.sendHelp(event.getUser().getNick(), getSyntax(), getUsage(), getNotes());
	}

	@Override
	public String getAlias()
	{
		return "changenick";
	}

	@Override
	public String getSyntax()
	{
		return "-changenick <newName>";
	}

	@Override
	public String[] getUsage()
	{
		return new String[]{"-changenick <newName> || Changes the name of bl4ckb0t to the specified one."};
	}

	@Override
	public String getNotes()
	{
		return "If the name is \"d\" the name changes to the default one (bl4ckb0t). | This command is only useable by OPs.";
	}
}
