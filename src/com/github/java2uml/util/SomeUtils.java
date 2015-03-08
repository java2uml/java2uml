package com.github.java2uml.util;

public class SomeUtils {
	/**
	 * Получение пакета класса
	 * @param path
	 * @param d - разделитель (".", "/")
	 * @return
	 */
	public static String getPackageName(String path, String d) {
		if (path == null || d == null) 
			return "";
		int ndx = path.lastIndexOf(d);
		if (ndx == -1) {
			return "";
		}
		return path.substring(0, ndx);
	}
	
	/**
	 * Получение имени класса без пакета
	 * @param path
	 * @param d - разделитель (".", "/")
	 * @return
	 */
	public static String getClassName(String path, String d) {
		if (path == null || d == null) 
			return "";
		int ndx = path.lastIndexOf(d);
		if (ndx == -1) {
			return path;
		}
		return path.substring(ndx+1, path.length());
	}
	
	public static boolean isJar(String path) {
		if (path == null) return false;
		return path.matches(".+\\.jar$");
	}
	
	public static boolean isJava(String path) {
		if (path == null) return false;
		return path.matches(".+\\.java$");
	}
	
	public static boolean isClass(String path) {
		if (path == null) return false;
		return path.matches(".+\\.class$");
	}	
}