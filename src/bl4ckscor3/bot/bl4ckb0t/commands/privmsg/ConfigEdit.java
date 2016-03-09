package bl4ckscor3.bot.bl4ckb0t.commands.privmsg;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import bl4ckscor3.bot.bl4ckb0t.ConfigurationFile;
import bl4ckscor3.bot.bl4ckb0t.Core;
import bl4ckscor3.bot.bl4ckb0t.commands.BasePrivateCommand;
import bl4ckscor3.bot.bl4ckb0t.util.android.ArrayMap;

public class ConfigEdit extends BasePrivateCommand<PrivateMessageEvent>
{
	@Override
	public void exe(PrivateMessageEvent event, String[] args) throws Exception
	{
		ConfigurationFile config = Core.bot.getConfig();
		ArrayMap<String,String> values = config.values;
		
		if(args[1].equals("set"))
		{
			if(args.length > 2)
			{
				if(args.length > 3)
				{
					if(values.containsKey(args[2]))
					{
						if(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false"))
						{
							List<String> file = FileUtils.readLines(config.config);
							
							for(int i = 0; i < file.size(); i++)
							{
								String s = file.get(i);
								
								if(s.contains("="))
								{
									if(s.split("=")[0].equals(args[2]))
									{
										file.remove(i);
										file.add(i, s.split("=")[0] + "=" + args[3]);
										FileUtils.writeLines(config.config, file);
										event.respond("Value updated. Use " + Colors.BOLD + "config save" + Colors.NORMAL + " to apply the changes.");
									}
								}
							}
						}
						else
							event.respond("The new value needs to be either " + Colors.BOLD + "true" + Colors.NORMAL + " or " + Colors.BOLD + "false" + Colors.NORMAL + ".");
					}
					else
						event.respond("Config value \"" + args[2] +"\" does not exist.");
				}
				else
					event.respond("Please specify a new value (true/false): " + Colors.BOLD + "config set <option> <value>");
			}
			else
				event.respond("Please specify a config option and a new value (true/false): " + Colors.BOLD + "config set <option> <value>");
		}
		else if(args[1].equals("list"))
		{
			String result = "";
			int count = 0;
			
			for(String key : values.keySet())
			{
				if(count == 8)
				{
					count = 0;
					event.respond(result.substring(0, result.lastIndexOf(" | ")));
					result = "";
				}
				
				result += key + "=" + values.get(key) + " | ";
				count++;
			}
		}
		else if(args[1].equals("lookup"))
		{
			if(args.length > 2)
			{
				if(values.containsKey(args[2]))
				{
					boolean b = config.isEnabled(args[2]);
					event.respond("" + b);
				}
				else
					event.respond("Config value does not exist.");
			}
			else
				event.respond("Please specify a config option: " + Colors.BOLD + "config lookup <option>");
		}
		else if(args[1].equals("save"))
		{
			config.values.clear();
			config.populateArrayMap();
			event.respond("Configuration file updated successfully.");
		}
	}

	@Override
	public String getMainAlias()
	{
		return "config ";
	}

	@Override
	public String getConfigEntry()
	{
		return "configEdit";
	}
}
