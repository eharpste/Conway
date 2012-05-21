package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.duckcult.conway.player.Ship;

public class SpiralWeapon extends Weapon {
	private float angle;
	private static int angleStep = 15;
	public SpiralWeapon() {
		name = "Spiral";
		rofDelay=.1f;
		angle = 0.0f;
		timeSinceFire = rofDelay;
	}
	
	@Override
	public ArrayList<Shot> fire(Ship origin) {
		ArrayList<Shot> ret = new ArrayList<Shot>();
		if(timeSinceFire>=rofDelay) {
			Vector2 mags = vectorComponents(BasicShot.DEFAULT_SPEED,angle);
			ret.add(new BasicShot(origin.getRect().x+origin.getRect().width/4, origin.getRect().y+origin.getRect().height/4,mags.x,mags.y,origin));
			timeSinceFire = 0.0f;
			if(angle <  345      )
				angle+=angleStep;
			else
				angle = 0.0f;
		}
		return ret;
	}
}
