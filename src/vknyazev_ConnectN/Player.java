package vknyazev_ConnectN;

import java.util.Scanner;

public class Player {
	private String name;
	private char color;

	/**
	 * @param name the name to set
	 * @param color the color to set
	 */
	Player(String name, char color) {
		this.name = name;
		this.color = color;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the color
	 */
	public char getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(char color) {
		this.color = color;
	}

	
}
