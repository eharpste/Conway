package com.duckcult.conway.player.weapons;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.duckcult.conway.player.Ship;

public class BeamWeapon extends Weapon {
	private boolean firing = false;
	private float timeFiring = 0.0f;
	private float beamTime = 2.5f;
	
	public BeamWeapon(){
		name = "Tripple";
		rofDelay=3f;
		timeSinceFire = rofDelay;
	}
	
	@Override
	public ArrayList<Shot> fire(Ship origin) {
		ArrayList<Shot> ret = new ArrayList<Shot>();
		if(!firing && timeSinceFire >= rofDelay) {
			firing = true;
			timeSinceFire=0.0f;
		}
		if(firing) {
			BasicShot b = new BasicShot(origin.getRect());
			b.setOriginInfo(origin.getPlayerNumber(), origin.getWeaponMode(), origin.getColor());
			ret.add(b);
		}
		return ret;
	}

	public void update(float deltaTime) {
		if(firing) {
			timeFiring += deltaTime;
			if(timeFiring >=beamTime) {
				firing = false;
				timeFiring = 0.0f;
			}
		}
		else {
			timeSinceFire += deltaTime;
		}
	}
}
