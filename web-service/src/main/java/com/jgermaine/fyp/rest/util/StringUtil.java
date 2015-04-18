package com.jgermaine.fyp.rest.util;

public final class StringUtil {
	
	/**
	 * Determines whether a string is not null and not empty
	 * 
	 * @param value
	 * @return isValid
	 */
	public static boolean isNotNullOrEmpty(String value) {
		return value != null && !value.isEmpty();
	}
}
