package com.github.java2uml.core.reflection;

import java.util.Hashtable;

public class Options {
	
	private static Hashtable<String, Object> options;
	
	public static final String OPTION_EXTENTION			= "extention";
	public static final String OPTION_IMPLEMENTATION 	= "implementation";
	public static final String OPTION_ASSOCIATION 		= "association";
	public static final String OPTION_COMPOSITION 		= "composition";
	public static final String OPTION_AGGREGATION 		= "aggregation";
	public static final String OPTION_HEADER 			= "direction";
	public static final String OPTION_LOLLITOP 			= "lollipop";
	public static final String OPTION_DIRECTION			= "direction";
	public static final String OPTION_DIRECTION_VERTICAL 	= "v";
	public static final String OPTION_DIRECTION_HORIZONTAL 	= "h";
	
	static {
		options = new Hashtable<>();
		init();
	}
	
	/**
	 * Инициализация таблицы
	 * @param args
	 */
	public static void init(final String[] args) {
		// здесь разбираем аргументы заполняем таблицу
	}
	
	/**
	 * Инициализация таблицы значениями по умолчанию
	 * @param key
	 * @return
	 */
	public static void init() {
		options.put(OPTION_EXTENTION, true);
		options.put(OPTION_ASSOCIATION, true);
		options.put(OPTION_COMPOSITION, true);
		options.put(OPTION_AGGREGATION, true);
		options.put(OPTION_IMPLEMENTATION, true);
		options.put(OPTION_HEADER, "Seabattle Java Project");
		options.put(OPTION_DIRECTION, OPTION_DIRECTION_HORIZONTAL);
		options.put(OPTION_LOLLITOP, true);
	}
	
	public static Object get(final String key) {
		if (options.containsKey(key)) {
			return options.get(key);
		}
		return null;
	}
}