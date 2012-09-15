package com.duckcult.conway;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class ConwayDesktop {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new JoglApplication(new Conway(), "Conway", Conway.screenWidth, Conway.screenHeight, false);
	}

}
