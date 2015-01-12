package bl4ckscor3.bot.bl4ckb0t.commands;

import org.pircbotx.hooks.events.MessageEvent;

import bl4ckscor3.bot.bl4ckb0t.localization.L10N;
import bl4ckscor3.bot.bl4ckb0t.util.Utilities;

public class Source implements ICommand<MessageEvent>
{
	@Override
	public void exe(MessageEvent event)
	{
		Utilities.chanMsg(event, L10N.strings.getString("source.hereYouGo") + ": https://github.com/bl4ckscor3/bl4ckb0t");
	}
	
	@Override
	public String getAlias()
	{
		return "source";
	}

	@Override
	public String getSyntax()
	{
		return "-source";
	}

	@Override
	public String[] getUsage()
	{
		return new String[]{"-source || " + L10N.strings.getString("source.explanation")};
	}

	@Override
	public String getNotes()
	{
		return null;
	}
}