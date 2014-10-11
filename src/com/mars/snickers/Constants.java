package com.mars.snickers;

public class Constants {

	private static int deviceWidth;
	private static int deviceHeight;

	private static int fontSizeHeaderSmall = 20;
	private static int fontSizeHeaderMedium = 22;
	private static int fontSizeHeaderLarge = 24;
	private static int fontSizeHeaderExLarge = 26;

	private static int fontSizeSmall = 16;
	private static int fontSizeMedium = 18;
	private static int fontSizeLarge = 20;
	private static int fontSizeExLarge = 22;


	public static int getHeaderFontSize() {
		if(deviceWidth >= 500)
		{
			return fontSizeHeaderExLarge;
		}else if (deviceWidth >= 400) {
			return fontSizeHeaderLarge;
		}
		else if(deviceWidth >= 300)
		{
			return fontSizeHeaderMedium;
		}
		return fontSizeHeaderSmall;
	}

	public static int getFontSize() {
		if(deviceWidth >= 500)
		{
			return fontSizeExLarge;
		}else if (deviceWidth >= 400) {
			return fontSizeLarge;
		}
		else if(deviceWidth >= 300)
		{
			return fontSizeMedium;
		}
		return fontSizeSmall;
	}
	
	

	/**
	 * @return the deviceWidth
	 */
	public static int getDeviceWidth() {
		return deviceWidth;
	}

	/**
	 * @param deviceWidth
	 *            the deviceWidth to set
	 */
	public static void setDeviceWidth(int deviceWidth) {
		Constants.deviceWidth = deviceWidth;
	}

	/**
	 * @return the deviceHeight
	 */
	public static int getDeviceHeight() {
		return deviceHeight;
	}

	/**
	 * @param deviceHeight
	 *            the deviceHeight to set
	 */
	public static void setDeviceHeight(int deviceHeight) {
		Constants.deviceHeight = deviceHeight;
	}

}
