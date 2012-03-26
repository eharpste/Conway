package com.duckcult.conway.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.duckcult.conway.World;

public class GameScreen extends ConwayScreen {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_LEVEL_END = 3;
	static final int GAME_OVER = 4;
	
	private World world;
	private OrthographicCamera guiCam;
}
