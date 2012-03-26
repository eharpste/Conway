package com.duckcult.conway;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class ConwayDesktop {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new JoglApplication(new Conway(), "Conway", 800, 600, false);
	}

}
