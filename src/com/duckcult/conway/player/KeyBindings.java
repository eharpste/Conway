package com.duckcult.conway.player;

import com.badlogic.gdx.Input;

/**
 * A Class to hold constant key binding setups. Names correspond to up, left, down, right, previous weapon, next weapon, fire. 
 * @author eharpste
 *
 */
public class KeyBindings {
	public static final KeyBindings WASD_QE_SPACE = new KeyBindings(new int [] {Input.Keys.W,Input.Keys.A,Input.Keys.S,Input.Keys.D,Input.Keys.Q,Input.Keys.E,Input.Keys.SPACE});
	public static final KeyBindings WASD_QE_LSHIFT = new KeyBindings(new int [] {Input.Keys.W,Input.Keys.A,Input.Keys.S,Input.Keys.D,Input.Keys.Q,Input.Keys.E,Input.Keys.SHIFT_LEFT});
	public static final KeyBindings IJKL_UO_SLASH = new KeyBindings(new int [] {Input.Keys.I,Input.Keys.J,Input.Keys.K,Input.Keys.L,Input.Keys.U,Input.Keys.O,Input.Keys.SLASH});
	
	private int [] bindings;
	
	private KeyBindings(int [] bindings){
		this.bindings = bindings;
	}
	
	public int up() {return bindings[0];}
	public int left(){return bindings[1];}
	public int down(){return bindings[2];}
	public int right(){return bindings[3];}
	public int prevWeapon(){return bindings[4];}
	public int nextWeapon(){return bindings[5];}
	public int fire(){return bindings[6];}
}
