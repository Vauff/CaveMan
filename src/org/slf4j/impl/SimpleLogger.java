/**
 * Copyright (c) 2004-2012 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.slf4j.impl;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.Util;
import org.slf4j.spi.LocationAwareLogger;

import bl4ckscor3.bot.bl4ckb0t.logging.Logging;

//file modified by bl4ckscor3 to use bl4ckb0t's own logging system + suppress a useless warning by pircbotx

/**
 * <p>Simple implementation of {@link Logger} that sends all enabled log messages,
 * for all defined loggers, to the console ({@code System.err}).
 * The following system properties are supported to configure the behavior of this logger:</p>
 *
 * <ul>
 * <li><code>org.slf4j.simpleLogger.logFile</code> - The output target which can be the <em>path</em> to a file, or
 * the special values "System.out" and "System.err". Default is "System.err".
 *
 * <li><code>org.slf4j.simpleLogger.defaultLogLevel</code> - Default log level for all instances of SimpleLogger.
 * Must be one of ("trace", "debug", "info", "warn", or "error"). If not specified, defaults to "info". </li>
 *
 * <li><code>org.slf4j.simpleLogger.log.<em>a.b.c</em></code> - Logging detail level for a SimpleLogger instance
 * named "a.b.c". Right-side value must be one of "trace", "debug", "info", "warn", or "error". When a SimpleLogger
 * named "a.b.c" is initialized, its level is assigned from this property. If unspecified, the level of nearest parent
 * logger will be used, and if none is set, then the value specified by
 * <code>org.slf4j.simpleLogger.defaultLogLevel</code> will be used.</li>
 *
 * <li><code>org.slf4j.simpleLogger.showDateTime</code> - Set to <code>true</code> if you want the current date and
 * time to be included in output messages. Default is <code>false</code></li>
 *
 * <li><code>org.slf4j.simpleLogger.dateTimeFormat</code> - The date and time format to be used in the output messages.
 * The pattern describing the date and time format is defined by
 * <a href="http://docs.oracle.com/javase/1.5.0/docs/api/java/text/SimpleDateFormat.html"><code>SimpleDateFormat</code></a>.
 * If the format is not specified or is invalid, the number of milliseconds since start up will be output. </li>
 *
 * <li><code>org.slf4j.simpleLogger.showThreadName</code> -Set to <code>true</code> if you want to output the current
 * thread name. Defaults to <code>true</code>.</li>
 *
 * <li><code>org.slf4j.simpleLogger.showLogName</code> - Set to <code>true</code> if you want the Logger instance name
 * to be included in output messages. Defaults to <code>true</code>.</li>
 *
 * <li><code>org.slf4j.simpleLogger.showShortLogName</code> - Set to <code>true</code> if you want the last component
 * of the name to be included in output messages. Defaults to <code>false</code>.</li>
 *
 * <li><code>org.slf4j.simpleLogger.levelInBrackets</code> - Should the level string be output in brackets? Defaults
 * to <code>false</code>.</li>
 *
 * <li><code>org.slf4j.simpleLogger.warnLevelString</code> - The string value output for the warn level. Defaults
 * to <code>WARN</code>.</li>

 * </ul>
 *
 * <p>In addition to looking for system properties with the names specified above, this implementation also checks for
 * a class loader resource named <code>"simplelogger.properties"</code>, and includes any matching definitions
 * from this resource (if it exists).</p>
 *
 * <p>With no configuration, the default output includes the relative time in milliseconds, thread name, the level,
 * logger name, and the message followed by the line separator for the host.  In log4j terms it amounts to the "%r [%t]
 * %level %logger - %m%n" pattern. </p>
 * <p>Sample output follows.</p>
 * <pre>
 * 176 [main] INFO examples.Sort - Populating an array of 2 elements in reverse order.
 * 225 [main] INFO examples.SortAlgo - Entered the sort method.
 * 304 [main] INFO examples.SortAlgo - Dump of integer array:
 * 317 [main] INFO examples.SortAlgo - Element [0] = 0
 * 331 [main] INFO examples.SortAlgo - Element [1] = 1
 * 343 [main] INFO examples.Sort - The next log statement should be an error message.
 * 346 [main] ERROR examples.SortAlgo - Tried to dump an uninitialized array.
 *   at org.log4j.examples.SortAlgo.dump(SortAlgo.java:58)
 *   at org.log4j.examples.Sort.main(Sort.java:64)
 * 467 [main] INFO  examples.Sort - Exiting main method.
 * </pre>
 *
 * <p>This implementation is heavily inspired by
 * <a href="http://commons.apache.org/logging/">Apache Commons Logging</a>'s SimpleLog.</p>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author <a href="mailto:sanders@apache.org">Scott Sanders</a>
 * @author Rod Waldhoff
 * @author Robert Burrell Donkin
 * @author C&eacute;drik LIME
 */
public class SimpleLogger extends MarkerIgnoringBase {

	private static final long serialVersionUID = -632788891211436180L;
	private static final String CONFIGURATION_FILE = "simplelogger.properties";

	private static long START_TIME = System.currentTimeMillis();
	private static final Properties SIMPLE_LOGGER_PROPS = new Properties();

	private static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;

	private static boolean INITIALIZED = false;

	private static int DEFAULT_LOG_LEVEL = LOG_LEVEL_ERROR;
	private static boolean SHOW_DATE_TIME = false;
	private static String DATE_TIME_FORMAT_STR = null;
	private static DateFormat DATE_FORMATTER = null;
	private static boolean SHOW_THREAD_NAME = true;
	private static boolean SHOW_LOG_NAME = true;
	private static boolean SHOW_SHORT_LOG_NAME = false;
	private static String LOG_FILE = "System.err";
	private static boolean LEVEL_IN_BRACKETS = false;
	private static String WARN_LEVEL_STRING = "WARN";

	/** All system properties used by <code>SimpleLogger</code> start with this prefix */
	public static final String SYSTEM_PREFIX = "org.slf4j.simpleLogger.";

	public static final String DEFAULT_LOG_LEVEL_KEY = SYSTEM_PREFIX + "defaultLogLevel";
	public static final String SHOW_DATE_TIME_KEY = SYSTEM_PREFIX + "showDateTime";
	public static final String DATE_TIME_FORMAT_KEY = SYSTEM_PREFIX + "dateTimeFormat";
	public static final String SHOW_THREAD_NAME_KEY = SYSTEM_PREFIX + "showThreadName";
	public static final String SHOW_LOG_NAME_KEY = SYSTEM_PREFIX + "showLogName";
	public static final String SHOW_SHORT_LOG_NAME_KEY = SYSTEM_PREFIX + "showShortLogName";
	public static final String LOG_FILE_KEY = SYSTEM_PREFIX + "logFile";
	public static final String LEVEL_IN_BRACKETS_KEY = SYSTEM_PREFIX + "levelInBrackets";
	public static final String WARN_LEVEL_STRING_KEY = SYSTEM_PREFIX + "warnLevelString";

	public static final String LOG_KEY_PREFIX = SYSTEM_PREFIX + "log.";

	private static String getStringProperty(String name) {
		String prop = null;
		try {
			prop = System.getProperty(name);
		} catch (SecurityException e) {
			; // Ignore
		}
		return (prop == null) ? SIMPLE_LOGGER_PROPS.getProperty(name) : prop;
	}

	private static String getStringProperty(String name, String defaultValue) {
		String prop = getStringProperty(name);
		return (prop == null) ? defaultValue : prop;
	}

	private static boolean getBooleanProperty(String name, boolean defaultValue) {
		String prop = getStringProperty(name);
		return (prop == null) ? defaultValue : "true".equalsIgnoreCase(prop);
	}

	// Initialize class attributes.
	// Load properties file, if found.
	// Override with system properties.
	static void init() {
		INITIALIZED = true;
		loadProperties();

		String defaultLogLevelString = getStringProperty(DEFAULT_LOG_LEVEL_KEY, null);
		if (defaultLogLevelString != null)
			DEFAULT_LOG_LEVEL = stringToLevel(defaultLogLevelString);

		SHOW_LOG_NAME = getBooleanProperty(SHOW_LOG_NAME_KEY, SHOW_LOG_NAME);
		SHOW_SHORT_LOG_NAME = getBooleanProperty(SHOW_SHORT_LOG_NAME_KEY, SHOW_SHORT_LOG_NAME);
		SHOW_DATE_TIME = getBooleanProperty(SHOW_DATE_TIME_KEY, SHOW_DATE_TIME);
		SHOW_THREAD_NAME = getBooleanProperty(SHOW_THREAD_NAME_KEY, SHOW_THREAD_NAME);
		DATE_TIME_FORMAT_STR = getStringProperty(DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_STR);
		LEVEL_IN_BRACKETS = getBooleanProperty(LEVEL_IN_BRACKETS_KEY, LEVEL_IN_BRACKETS);
		WARN_LEVEL_STRING = getStringProperty(WARN_LEVEL_STRING_KEY, WARN_LEVEL_STRING);

		LOG_FILE = getStringProperty(LOG_FILE_KEY, LOG_FILE);

		if (DATE_TIME_FORMAT_STR != null) {
			try {
				DATE_FORMATTER = new SimpleDateFormat(DATE_TIME_FORMAT_STR);
			} catch (IllegalArgumentException e) {
				Util.report("Bad date format in " + CONFIGURATION_FILE + "; will output relative time", e);
			}
		}
	}

	private static void loadProperties() {
		// Add props from the resource simplelogger.properties
		InputStream in = AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
			public InputStream run() {
				ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
				if (threadCL != null) {
					return threadCL.getResourceAsStream(CONFIGURATION_FILE);
				} else {
					return ClassLoader.getSystemResourceAsStream(CONFIGURATION_FILE);
				}
			}
		});
		if (null != in) {
			try {
				SIMPLE_LOGGER_PROPS.load(in);
				in.close();
			} catch (java.io.IOException e) {
				// ignored
			}
		}
	}

	/** The current log level */
	protected int currentLogLevel = LOG_LEVEL_ERROR;
	/** The short name of this simple log instance */
	private transient String shortLogName = null;

	/**
	 * Package access allows only {@link SimpleLoggerFactory} to instantiate
	 * SimpleLogger instances.
	 */
	SimpleLogger(String name) {
		if (!INITIALIZED) {
			init();
		}
		this.name = name;

		String levelString = recursivelyComputeLevelString();
		if (levelString != null) {
			this.currentLogLevel = stringToLevel(levelString);
		} else {
			this.currentLogLevel = DEFAULT_LOG_LEVEL;
		}
	}

	String recursivelyComputeLevelString() {
		String tempName = name;
		String levelString = null;
		int indexOfLastDot = tempName.length();
		while ((levelString == null) && (indexOfLastDot > -1)) {
			tempName = tempName.substring(0, indexOfLastDot);
			levelString = getStringProperty(LOG_KEY_PREFIX + tempName, null);
			indexOfLastDot = String.valueOf(tempName).lastIndexOf(".");
		}
		return levelString;
	}

	private static int stringToLevel(String levelStr) {
		return LOG_LEVEL_ERROR;
	}

	/**
	 * This is our internal implementation for logging regular (non-parameterized)
	 * log messages.
	 *
	 * @param level   One of the LOG_LEVEL_XXX constants defining the log level
	 * @param message The message itself
	 * @param t       The exception whose stack trace should be logged
	 */
	private void log(int level, String message, Throwable t) {
		if (!isLevelEnabled(level)) {
			return;
		}
		
		StringBuilder buf = new StringBuilder(32);

		// Append date-time if so configured
		if (SHOW_DATE_TIME) {
			if (DATE_FORMATTER != null) {
				buf.append(getFormattedDate());
				buf.append(' ');
			} else {
				buf.append(System.currentTimeMillis() - START_TIME);
				buf.append(' ');
			}
		}

		// Append current thread name if so configured
		if (SHOW_THREAD_NAME) {
			buf.append('[');
			buf.append(Thread.currentThread().getName());
			buf.append("] ");
		}

		if (LEVEL_IN_BRACKETS)
			buf.append('[');

		// Append a readable representation of the log level
		buf.append("ERROR");

		if (LEVEL_IN_BRACKETS)
			buf.append(']');
		buf.append(' ');

		// Append the name of the log instance if so configured
		if (SHOW_SHORT_LOG_NAME) {
			if (shortLogName == null)
				shortLogName = computeShortName();
			buf.append(String.valueOf(shortLogName)).append(" - ");
		} else if (SHOW_LOG_NAME) {
			buf.append(String.valueOf(name)).append(" - ");
		}

		// Append the message
		buf.append(message);

		if(buf.toString().contains("org.pircbotx.PircBotX - Exception encountered when parsing line")) //to prevent useless npes from pircbotx' stupidly overnumbered whois requests
			return;

		Logging.severe(buf.toString().split("ERROR ")[1]); //bl4ckb0t's logging to file

		if (t != null) {
			Logging.stackTrace(t);
		}

	}

	private String getFormattedDate() {
		Date now = new Date();
		String dateText;
		synchronized (DATE_FORMATTER) {
			dateText = DATE_FORMATTER.format(now);
		}
		return dateText;
	}

	private String computeShortName() {
		return name.substring(name.lastIndexOf(".") + 1);
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 *
	 * @param level
	 * @param format
	 * @param arg1
	 * @param arg2
	 */
	private void formatAndLog(int level, String format, Object arg1, Object arg2) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 *
	 * @param level
	 * @param format
	 * @param arguments a list of 3 ore more arguments
	 */
	private void formatAndLog(int level, String format, Object... arguments) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	/**
	 * Is the given log level currently enabled?
	 *
	 * @param logLevel is this level enabled?
	 */
	protected boolean isLevelEnabled(int logLevel) {
		// log level are numerically ordered so can use simple numeric
		// comparison
		return (logLevel >= currentLogLevel);
	}

	/** Are {@code error} messages currently enabled? */
	public boolean isErrorEnabled() {
		return isLevelEnabled(LOG_LEVEL_ERROR);
	}

	/**
	 * A simple implementation which always logs messages of level ERROR according
	 * to the format outlined above.
	 */
	public void error(String msg) {
		log(LOG_LEVEL_ERROR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object arg) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_ERROR, format, argArray);
	}

	/** Log a message of level ERROR, including an exception. */
	public void error(String msg, Throwable t) {
		log(LOG_LEVEL_ERROR, msg, t);
	}

	@Override
	public boolean isTraceEnabled()
	{
		return false;
	}

	@Override
	public void trace(String msg)
	{}

	@Override
	public void trace(String format, Object arg)
	{}

	@Override
	public void trace(String format, Object arg1, Object arg2)
	{}

	@Override
	public void trace(String format, Object... arguments)
	{}

	@Override
	public void trace(String msg, Throwable t)
	{}

	@Override
	public boolean isDebugEnabled()
	{
		return false;
	}

	@Override
	public void debug(String msg)
	{}

	@Override
	public void debug(String format, Object arg)
	{}

	@Override
	public void debug(String format, Object arg1, Object arg2)
	{}

	@Override
	public void debug(String format, Object... arguments)
	{}

	@Override
	public void debug(String msg, Throwable t)
	{}

	@Override
	public boolean isInfoEnabled()
	{
		return false;
	}

	@Override
	public void info(String msg)
	{}

	@Override
	public void info(String format, Object arg)
	{}

	@Override
	public void info(String format, Object arg1, Object arg2)
	{}

	@Override
	public void info(String format, Object... arguments)
	{}

	@Override
	public void info(String msg, Throwable t)
	{}

	@Override
	public boolean isWarnEnabled()
	{
		return false;
	}

	@Override
	public void warn(String msg)
	{}

	@Override
	public void warn(String format, Object arg)
	{}

	@Override
	public void warn(String format, Object... arguments)
	{}

	@Override
	public void warn(String format, Object arg1, Object arg2)
	{}

	@Override
	public void warn(String msg, Throwable t)
	{}
}
