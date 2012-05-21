package com.duckcult.conway.utils;

/**
 * libGdx's Color class has no functionality for dealing with colors in an HSV perspective so I wrote this to alter saturation an brightness.
 * Really it just borrows the methods from java.awt.color and then recasts the Color to the libGdx Color class.
 * @author eharpste
 *
 */
public class ColorUtils {
	/**
	 * Desaturates the given color by the given percentage.
	 * Basically it translates the RGB color into an HSV representation and then multiplies the S component by percent.
	 * @param c			A LibGdx Color to desaturate.
	 * @param percent	The percentage you want to desaturate the color by.
	 * @return	The given color desaturated by the given percentage.
	 */
	public static com.badlogic.gdx.graphics.Color desaturate (com.badlogic.gdx.graphics.Color c, float percent) {
		float [] hsbvals = java.awt.Color.RGBtoHSB((int)(255*c.r), (int)(255*c.g), (int)(255*c.b), null);
		hsbvals[1] *= percent;
		java.awt.Color tempColor = java.awt.Color.getHSBColor(hsbvals[0],hsbvals[1],hsbvals[2]);
		return new com.badlogic.gdx.graphics.Color(((float)tempColor.getRed())/255f,((float)tempColor.getGreen())/255f,((float)tempColor.getBlue())/255f,c.a);
	}
	
	/**
	 * Darkens the given color by the given percentage.
	 * Basically it translates the RGB color into an HSV representation and then multiplies the V component by percent.
	 * @param c			A LibGdx Color to darken.
	 * @param percent	The percentage you want to darken the color by.
	 * @return	The given color darkened by the given percentage.
	 */
	public static com.badlogic.gdx.graphics.Color darken (com.badlogic.gdx.graphics.Color c, float percent) {
		float [] hsbvals = java.awt.Color.RGBtoHSB((int)(255*c.r), (int)(255*c.g), (int)(255*c.b), null);
		hsbvals[2] *= percent;
		java.awt.Color tempColor = java.awt.Color.getHSBColor(hsbvals[0],hsbvals[1],hsbvals[2]);
		return new com.badlogic.gdx.graphics.Color(((float)tempColor.getRed())/255f,((float)tempColor.getGreen())/255f,((float)tempColor.getBlue())/255f,c.a);
	}
}