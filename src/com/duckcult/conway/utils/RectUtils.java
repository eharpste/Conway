package com.duckcult.conway.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Rectangle;

public class RectUtils {
	public static Mesh toMesh(Rectangle rect,Color color, float depth) {
		Mesh m = new Mesh(true,4,4,
				new VertexAttribute(Usage.Position,3,"a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
			m.setVertices(new float[] {rect.x, rect.y, depth, color.toFloatBits(),
									   rect.x+rect.width, rect.y, depth, color.toFloatBits(),
									   rect.x, rect.y+rect.height, depth, color.toFloatBits(),
									   rect.x+rect.width, rect.y+rect.height, depth, color.toFloatBits() });
			m.setIndices(new short[] {0,1,2,3});
			return m;
	}
	
	public static Rectangle aboveCentered(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x-scaledWidth/2f+origin.width/2f;
		float scaledY = origin.y+origin.height;
		return new Rectangle(scaledX,scaledY,scaledWidth,scaledHeight);
	}
	
	public static Rectangle bellowCentered(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x-scaledWidth/2f+origin.width/2f;
		float scaledY = origin.y-scaledHeight;
		return new Rectangle(scaledX,scaledY,scaledWidth,scaledHeight);
	}
	
	public static Rectangle centeredRight(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x+origin.width;
		float scaledY = origin.y-scaledHeight/2f+origin.height/2f;
		return new Rectangle(scaledX,scaledY,scaledWidth,scaledHeight);
	}
	
	public static Rectangle centeredLeft(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x-scaledWidth;
		float scaledY = origin.y-scaledHeight/2f+origin.height/2f;
		return new Rectangle(scaledX,scaledY,scaledWidth,scaledHeight);	
	}
	
	public static Rectangle aboveLeft(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x-scaledWidth;
		float scaledY = origin.y+origin.height;
		return new Rectangle(scaledX, scaledY, scaledWidth, scaledHeight);
	}
	
	public static Rectangle aboveRight(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x+origin.width;
		float scaledY = origin.y+origin.height;
		return new Rectangle(scaledX, scaledY, scaledWidth, scaledHeight);
	}
	
	public static Rectangle bellowLeft(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x-scaledWidth;
		float scaledY = origin.y-scaledHeight;
		return new Rectangle(scaledX, scaledY, scaledWidth, scaledHeight);
	}
	
	public static Rectangle bellowRight(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x+origin.width;
		float scaledY = origin.y-scaledHeight;
		return new Rectangle(scaledX, scaledY, scaledWidth, scaledHeight);
	}
	
	public static Rectangle centered(Rectangle origin, float scale) {
		float scaledWidth = origin.width*scale;
		float scaledHeight = origin.height*scale;
		float scaledX = origin.x-scaledWidth/2f+origin.width/2f;
		float scaledY = origin.y-scaledHeight/2f+origin.height/2f;
		return new Rectangle(scaledX,scaledY,scaledWidth,scaledHeight);
	}
}
