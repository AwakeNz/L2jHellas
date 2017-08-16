/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jhellas.util;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.l2jhellas.gameserver.ThreadPoolManager;
import com.l2jhellas.gameserver.model.L2Object;
import com.l2jhellas.gameserver.model.Location;
import com.l2jhellas.gameserver.model.actor.L2Character;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.taskmanager.MemoryWatchOptimize;

/**
 * General Utility functions related to Game Server
 */
public final class Util
{
	private final static Logger _log = Logger.getLogger(Util.class.getName());

	private static final NumberFormat ADENA_FORMATTER = NumberFormat.getIntegerInstance(Locale.ENGLISH);
	
	public static void handleIllegalPlayerAction(L2PcInstance actor, String message, int punishment)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new IllegalPlayerAction(actor, message, punishment), 5000);
	}

	public static String getRelativePath(File base, File file)
	{
		return file.toURI().getPath().substring(base.toURI().getPath().length());
	}

	/** Return degree value of object 2 to the horizontal line with object 1 being the origin */
	public static double calculateAngleFrom(L2Object obj1, L2Object obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	/** Return degree value of object 2 to the horizontal line with object 1 being the origin */
	public final static double calculateAngleFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
			angleTarget = 360 + angleTarget;
		return angleTarget;
	}

	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2)
	{
		return calculateDistance(x1, y1, 0, x2, y2, 0, false);
	}

	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis)
	{
		double dx = (double) x1 - x2;
		double dy = (double) y1 - y2;

		if (includeZAxis)
		{
			double dz = z1 - z2;
			return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
		}
		else
			return Math.sqrt((dx * dx) + (dy * dy));
	}

	public static double calculateDistance(L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if (obj1 == null || obj2 == null)
			return 1000000;
		return calculateDistance(obj1.getPosition().getX(), obj1.getPosition().getY(), obj1.getPosition().getZ(), obj2.getPosition().getX(), obj2.getPosition().getY(), obj2.getPosition().getZ(), includeZAxis);
	}

	public static String capitalizeFirst(String str)
	{
		str = str.trim();

		if (str.length() > 0 && Character.isLetter(str.charAt(0)))
			return str.substring(0, 1).toUpperCase() + str.substring(1);

		return str;
	}

	public static void printSection(String print)
	{
		int maxlength = 79;
		print = "-[ " + print + " ]";
		int slen = print.length();
		if (slen > maxlength)
		{
			_log.info(print);
			return;
		}
		int i;
		for (i = 0; i < (maxlength - slen); i++)
			print = "=" + print;
		_log.info(print);
	}

	public static String capitalizeWords(String str)
	{
		char[] charArray = str.toCharArray();
		String result = "";

		// Capitalize the first letter in the given string!
		charArray[0] = Character.toUpperCase(charArray[0]);

		for (int i = 0; i < charArray.length; i++)
		{
			if (Character.isWhitespace(charArray[i]))
				charArray[i + 1] = Character.toUpperCase(charArray[i + 1]);

			result += Character.toString(charArray[i]);
		}

		return result;
	}

	public static boolean checkIfInRange(int range, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if (obj1 == null || obj2 == null)
			return false;
		if (range == -1)
			return true; // not limited

		int rad = 0;
		if (obj1 instanceof L2Character)
			rad += ((L2Character) obj1).getTemplate().collisionRadius;
		if (obj2 instanceof L2Character)
			rad += ((L2Character) obj2).getTemplate().collisionRadius;

		double dx = obj1.getX() - obj2.getX();
		double dy = obj1.getY() - obj2.getY();

		if (includeZAxis)
		{
			double dz = obj1.getZ() - obj2.getZ();
			double d = dx * dx + dy * dy + dz * dz;

			return d <= range * range + 2 * range * rad + rad * rad;
		}
		else
		{
			double d = dx * dx + dy * dy;

			return d <= range * range + 2 * range * rad + rad * rad;
		}
	}

	public static double convertHeadingToDegree(int heading)
	{
		double degree = heading / 182.044444444;
		return degree;
	}

	public final static int calculateHeadingFrom(L2Object obj1, L2Object obj2)
	{
		return calculateHeadingFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}

	public final static int calculateHeadingFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
			angleTarget = 360 + angleTarget;
		return (int) (angleTarget * 182.044444444);
	}

	public final static int calculateHeadingFrom(double dx, double dy)
	{
		double angleTarget = Math.toDegrees(Math.atan2(dy, dx));
		if (angleTarget < 0)
			angleTarget = 360 + angleTarget;
		return (int) (angleTarget * 182.044444444);
	}

	public static int countWords(String str)
	{
		return str.trim().split(" ").length;
	}

	/**
	 * Returns a delimited string for an given array of string elements.<BR>
	 * (Based on implode() in PHP)
	 * 
	 * @param String
	 *        [] strArray
	 * @param String
	 *        strDelim
	 * @return String implodedString
	 */
	public static String implodeString(String[] strArray, String strDelim)
	{
		String result = "";

		for (String strValue : strArray)
			result += strValue + strDelim;

		return result;
	}

	/**
	 * Returns a delimited string for an given collection of string elements.<BR>
	 * (Based on implode() in PHP)
	 * 
	 * @param Collection
	 *        &lt;String&gt; strCollection
	 * @param String
	 *        strDelim
	 * @return String implodedString
	 */
	public static String implodeString(Collection<String> strCollection, String strDelim)
	{
		return implodeString(strCollection.toArray(new String[strCollection.size()]), strDelim);
	}

	/**
	 * Returns the rounded value of val to specified number of digits
	 * after the decimal point.<BR>
	 * (Based on round() in PHP)
	 * 
	 * @param float val
	 * @param int numPlaces
	 * @return float roundedVal
	 */
	public static float roundTo(float val, int numPlaces)
	{
		if (numPlaces <= 1)
			return Math.round(val);

		float exponent = (float) Math.pow(10, numPlaces);

		return (Math.round(val * exponent) / exponent);
	}

	public static boolean isAlphaNumeric(String text)
	{
		if (text == null)
			return false;
		boolean result = true;
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (!Character.isLetterOrDigit(chars[i]))
			{
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * returns how many processors are installed on this system.
	 */
	private static void printCpuInfo()
	{
		_log.info("");
		_log.info("Hardware/Software Info");
		_log.info("Avaible Processor's: " + Runtime.getRuntime().availableProcessors());
		_log.info("Processor(s) Identifier: " + System.getenv("PROCESSOR_IDENTIFIER"));
	}

	/**
	 * returns the operational system server is running on it.
	 */
	private static void printOSInfo()
	{
		_log.info("");
		_log.info("OS: " + System.getProperty("os.name") + " Build: " + System.getProperty("os.version"));
		
		if (System.getProperty("os.arch").equalsIgnoreCase("x86"))
			_log.info("OS Architecture: 32Bit System.");
		else
			_log.info("OS Architecture: 64Bit System.");
	}

	/**
	 * returns JAVA Runtime Environment properties
	 */
	private static void printJreInfo()
	{
		_log.info("");
		_log.info("Java Platform Information");
		_log.info("Java Runtime  Name: " + System.getProperty("java.runtime.name"));
		_log.info("Java Version: " + System.getProperty("java.version"));
		_log.info("Java Class Version: " + System.getProperty("java.class.version"));
	}

	/**
	 * returns general info related to machine
	 */
	public static void printRuntimeInfo()
	{
		// 1024 * 1024 = 1048576
		_log.info("Runtime Information");
		_log.info("Maximum Memory Size: " + (Runtime.getRuntime().maxMemory() / 1048576) + "MB");
		_log.info("Total Memory Size: " + (Runtime.getRuntime().totalMemory() / 1048576) + "MB");
		_log.info("Used Memory Size: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + "MB");
		_log.info("Free Memory Size: " + (Runtime.getRuntime().freeMemory() / 1048576) + "MB");
	}

	/**
	 * calls time service to get system time.
	 */
	private static void printSystemTime()
	{
		// Instantiates Date Object
		Date dateInfo = new Date();

		// generates a simple date format
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");

		// generates String that will get the formatter info with values
		String dayInfo = df.format(dateInfo);

		_log.info("System Time: " + dayInfo);
	}

	/**
	 * gets system JVM properties.
	 */
	private static void printJvmInfo()
	{
		_log.info("");
		_log.info("Virtual Machine Information (JVM)");
		_log.info("JVM Name: " + System.getProperty("java.vm.name"));
		_log.info("JVM installation directory: " + System.getProperty("java.home"));
		_log.info("JVM version: " + System.getProperty("java.vm.version"));
		_log.info("JVM Vendor: " + System.getProperty("java.vm.vendor"));
		_log.info("JVM Info: " + System.getProperty("java.vm.info"));
	}

	/**
	 * prints all other methods.
	 */
	public static void printGeneralSystemInfo()
	{
		printSystemTime();
		printOSInfo();
		printCpuInfo();
		//printRuntimeInfo();
		printJreInfo();
		printJvmInfo();
	}

	public static String reverseColor(String color)
	{
		char[] ch1 = color.toCharArray();
		char[] ch2 = new char[6];
		ch2[0] = ch1[4];
		ch2[1] = ch1[5];
		ch2[2] = ch1[2];
		ch2[3] = ch1[3];
		ch2[4] = ch1[0];
		ch2[5] = ch1[1];

		return new String(ch2);
	}

	public static int convertMinutesToMiliseconds(int minutesToConvert)
	{
		return minutesToConvert * 60000;
	}

	public static boolean isInternalIP(String ipAddress)
	{
		return (ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") || ipAddress.startsWith("127.0.0.1"));
	}

	public static String printData(byte[] data, int len)
	{
		StringBuilder result = new StringBuilder();

		int counter = 0;

		for (int i = 0; i < len; i++)
		{
			if (counter % 16 == 0)
			{
				result.append(fillHex(i, 4) + ": ");
			}

			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16)
			{
				result.append("   ");

				int charpoint = i - 15;
				for (int a = 0; a < 16; a++)
				{
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80)
					{
						result.append((char) t1);
					}
					else
					{
						result.append('.');
					}
				}

				result.append("\n");
				counter = 0;
			}
		}

		int rest = data.length % 16;
		if (rest > 0)
		{
			for (int i = 0; i < 17 - rest; i++)
			{
				result.append("   ");
			}

			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++)
			{
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80)
				{
					result.append((char) t1);
				}
				else
				{
					result.append('.');
				}
			}

			result.append("\n");
		}

		return result.toString();
	}

	public static String fillHex(int data, int digits)
	{
		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++)
		{
			number = "0" + number;
		}

		return number;
	}
	
	public static String formatAdena(long amount)
	{
		synchronized (ADENA_FORMATTER)
		{
			return ADENA_FORMATTER.format(amount);
		}
	}

	public final static int convertDegreeToClientHeading(double degree)
	{
		if (degree < 0)
			degree = 360 + degree;
		
		return (int) (degree * 182.044444444);
	}

	/**
	 * Format the given date on the given format
	 * @param date : the date to format.
	 * @param format : the format to correct by.
	 * @return a string representation of the formatted date.
	 */
	public static String formatDate(Date date, String format)
	{
		final DateFormat dateFormat = new SimpleDateFormat(format);
		if (date != null)
			return dateFormat.format(date);
		
		return null;
	}
	
	public static String formatDate(long date, String format)
	{
		final DateFormat dateFormat = new SimpleDateFormat(format);
		if (date > 0)
			return dateFormat.format(date);
		
		return null;
	}
	
	/**
	 * Faster calculation than checkIfInRange if distance is short and collisionRadius isn't needed. Not for long distance checks (potential teleports, far away castles, etc)
	 * @param radius The radius to use as check.
	 * @param obj1 The position 1 to make check on.
	 * @param obj2 The postion 2 to make check on.
	 * @param includeZAxis Include Z check or not.
	 * @return true if both objects are in the given radius.
	 */
	public static boolean checkIfInShortRadius(int radius, L2Object obj1, L2Object obj2, boolean includeZAxis)
	{
		if (obj1 == null || obj2 == null)
			return false;
		
		if (radius == -1)
			return true; // not limited
			
		int dx = obj1.getX() - obj2.getX();
		int dy = obj1.getY() - obj2.getY();
		
		if (includeZAxis)
		{
			int dz = obj1.getZ() - obj2.getZ();
			return dx * dx + dy * dy + dz * dz <= radius * radius;
		}
		
		return dx * dx + dy * dy <= radius * radius;
	}
	/**
	 * Verify if the given text matches with the regex pattern.
	 * @param text : the text to test.
	 * @param regex : the regex pattern to make test with.
	 * @return true if matching.
	 */
	public static boolean isValidName(String text, String regex)
	{
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(regex);
		}
		catch (PatternSyntaxException e) // case of illegal pattern
		{
			pattern = Pattern.compile(".*");
		}
		
		Matcher regexp = pattern.matcher(text);
		
		return regexp.matches();
	}
	
	/**
	 * Child of isValidName, with regular pattern for players' name.
	 * @param text : the text to test.
	 * @return true if matching.
	 */
	public static boolean isValidPlayerName(String text)
	{
		return isValidName(text, "^[A-Za-z0-9]{1,16}$");
	}
	
	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} contains only numbers, {@code false} otherwise
	 */
	public static boolean isDigit(String text)
	{
		if (text == null)
			return false;
		
		return text.matches("[0-9]+");
	}
	
	/**
	 * @param string the initial word to scramble.
	 * @return an anagram of the given string.
	 */
	public static String scrambleString(String string)
	{
		List<String> letters = Arrays.asList(string.split(""));
		Collections.shuffle(letters);
		
		StringBuilder sb = new StringBuilder(string.length());
		for (String c : letters)
			sb.append(c);
		
		return sb.toString();
	}
	
	/**
	 * @param <T> The Object type.
	 * @param array - the array to look into.
	 * @param obj - the object to search for.
	 * @return {@code true} if the array contains the object, {@code false} otherwise.
	 */
	public static <T> boolean contains(T[] array, T obj)
	{
		if (array == null || array.length == 0)
			return false;
		
		for (T element : array)
			if (element.equals(obj))
				return true;
		
		return false;
	}
	
	/**
	 * @param <T> The Object type.
	 * @param array1 - the array to look into.
	 * @param array2 - the array to search for.
	 * @return {@code true} if both arrays contains a similar value.
	 */
	public static <T> boolean contains(T[] array1, T[] array2)
	{
		if (array1 == null || array1.length == 0)
			return false;
		
		if (array2 == null || array2.length == 0)
			return false;
		
		for (T element1 : array1)
		{
			for (T element2 : array2)
				if (element2.equals(element1))
					return true;
		}
		return false;
	}
	
	/**
	 * @param array - the array to look into.
	 * @param obj - the integer to search for.
	 * @return {@code true} if the array contains the integer, {@code false} otherwise.
	 */
	public static boolean contains(int[] array, int obj)
	{
		if (array == null || array.length == 0)
			return false;
		
		for (int element : array)
			if (element == obj)
				return true;
		
		return false;
	}

	public static int max(int value1, int value2, int... values)
	{
		int max = Math.max(value1, value2);
		for (int value : values)
		{
			if (max < value)
			{
				max = value;
			}
		}
		return max;
	}

	public static double getAngleDifference(L2Object obj, L2Object src)
	{
		double diff = Util.calculateAngleFrom(src, obj) - Util.convertHeadingToDegree(src.getHeading());
		
		while (diff > +180)
			diff -= 360;
		
		while (diff < -180)
			diff += 360;
		
		return Math.abs(diff);
	}

	public final static int calculateNormalHeading(int x1, int y1, int x2, int y2)
	{
		final double distance = calculateDistance(x1, y1, x2, y2);
		return calculateHeadingFrom((x2 - x1) / distance, (y2 - y1) / distance);
	}

	public final static double calculateDistance(Location loc, Location loc2)
	{
		return calculateDistance(loc.getX(), loc.getY(), 0, loc2.getX(), loc2.getY(), 0, false);
	}
	
	public final static double calculateDistance(int x1, int y1, int x2, int y2)
	{
		return calculateDistance(x1, y1, 0, x2, y2, 0, false);
	}	
	
	public static long gc(int i, int delay)
	{
		long freeMemBefore = MemoryWatchOptimize.getMemFree();
		Runtime rt = Runtime.getRuntime();
		rt.gc();
		while(--i > 0)
		{
			try
			{
				Thread.sleep(delay);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			rt.gc();
		}
		rt.runFinalization();
		return MemoryWatchOptimize.getMemFree() - freeMemBefore;
	}
}