package bl4ckscor3.bot.bl4ckb0t.commands.channel;

import org.pircbotx.hooks.events.MessageEvent;

import bl4ckscor3.bot.bl4ckb0t.core.Bot;
import bl4ckscor3.bot.bl4ckb0t.localization.L10N;
import bl4ckscor3.bot.bl4ckb0t.util.Utilities;

public class XColor extends BaseCommand<MessageEvent<Bot>>
{
	@Override
	public void exe(MessageEvent<Bot> event)
	{
		String[] colors =
			{
				"Grey",
				"Black",
				"Blue",
				"Dark Green",
				"Red",
				"Red",
				"Purple",
				"Brown",
				"Yellow",
				"Light Green",
				"Dark Aqua",
				"Lighter Aqua",
				"Pink"
			};

		for(int i = 0; i < 14; i++)
		{
			if(i < 4)
				Utilities.notice(event, "%C" + i + ": " + colors[i]);
			else if(i > 4 && i < 12)
				Utilities.notice(event, "%C" + i + ": " + colors[i]);
			else if(i == 13)
				Utilities.notice(event, "%C" + i + ": " + colors[i - 1]);
		}
	}

	@Override
	public String getAlias() 
	{
		return "xcolor";
	}

	@Override
	public String getSyntax(MessageEvent<Bot> event)
	{
		return "-xcolor";
	}

	@Override
	public String[] getUsage(MessageEvent<Bot> event)
	{
		return new String[]{"-xcolor || " + L10N.getString("xcolor.explanation", event)};
	}

	@Override
	public String getNotes(MessageEvent<Bot> event)
	{
		return L10N.getString("xcolor.notes", event);
	}
	
	@Override
	public int getPermissionLevel()
	{
		return 1;
	}
}
