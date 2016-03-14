package bl4ckscor3.bot.bl4ckb0t.irc;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Class to override the println method so it also writes everything to the console
 * @author bl4ckscor3
 */
public class CustomPrintWriter extends PrintWriter
{
	public CustomPrintWriter(OutputStream out, boolean autoFlush)
	{
		super(out, autoFlush);
	}
	
	@Override
	public void println(String x)
	{
		super.println(x);
		System.out.println(x); //TODO: Adapt to Logging framework
	}
}
