package com.duckcult.conway.weapons;

import com.badlogic.gdx.utils.Array;
import com.duckcult.conway.player.Ship;

public class QuadShotWeapon extends Weapon {
	private boolean straight;
	public QuadShotWeapon() {
		name = "Quad";
		rofDelay=.75f;
		straight = true;
		timeSinceFire = rofDelay;
	}
	
	@Override
	public Array<Shot> fire(Ship origin) {
		Array<Shot> ret = new Array<Shot>();
		if(timeSinceFire>=rofDelay && hasAmmo()) {
			if(straight){
				//up
				ret.add(new BasicShot(origin));
				//left
				ret.add(new BasicShot(origin.getRect().x-origin.getRect().width/2,origin.getRect().y+origin.getRect().height/4,-BasicShot.DEFAULT_SPEED,0f,origin));
				//right
				ret.add(new BasicShot(origin.getRect().x+origin.getRect().width,origin.getRect().y+origin.getRect().height/4,BasicShot.DEFAULT_SPEED,0f,origin));
				//down
				ret.add(new BasicShot(origin.getRect().x+origin.getRect().width/4,origin.getRect().y-origin.getRect().height/2,0f,-BasicShot.DEFAULT_SPEED,origin));
				straight = false;
			}
			else {
				float mag = getDiagonal(BasicShot.DEFAULT_SPEED);
				//up left
				ret.add(new BasicShot(origin.getRect().x-origin.getRect().width/2,origin.getRect().y+origin.getRect().height,-mag,mag,origin));
				//up right
				ret.add(new BasicShot(origin.getRect().x+origin.getRect().width,origin.getRect().y+origin.getRect().height,mag,mag,origin));
				//bottom left
				ret.add(new BasicShot(origin.getRect().x-origin.getRect().width/2,origin.getRect().y-origin.getRect().height/2,-mag,-mag,origin));
				//bottom right
				ret.add(new BasicShot(origin.getRect().x+origin.getRect().width,origin.getRect().y-origin.getRect().height/2,mag,-mag,origin));
				straight = true;
			}
			timeSinceFire = 0.0f;
			ammo--;
		}
		return ret;
	}
}
