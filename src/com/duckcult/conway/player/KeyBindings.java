package com.duckcult.conway.player;

import java.util.Arrays;

import com.badlogic.gdx.Input;

/**
 * A Class to hold constant key binding setups. 
 * Names correspond to up, left, down, right, previous weapon, next weapon, fire.
 * Note: anything involving Right Shift, or Apostrophe doesn't seem to work. 
 * @author eharpste
 *
 */
public class KeyBindings {
	public static final KeyBindings WASD_QE_SPACE = new KeyBindings(new int [] {Input.Keys.W,Input.Keys.A,Input.Keys.S,Input.Keys.D,Input.Keys.Q,Input.Keys.E,Input.Keys.SPACE});
	public static final KeyBindings WASD_QE_LSHIFT = new KeyBindings(new int [] {Input.Keys.W,Input.Keys.A,Input.Keys.S,Input.Keys.D,Input.Keys.Q,Input.Keys.E,Input.Keys.SHIFT_LEFT});
	public static final KeyBindings WASD_QE_V = new KeyBindings(new int [] {Input.Keys.W,Input.Keys.A,Input.Keys.S, Input.Keys.D,Input.Keys.Q,Input.Keys.E,Input.Keys.V});
	public static final KeyBindings OKLSEMICOLON_IP_N = new KeyBindings(new int [] {Input.Keys.O,Input.Keys.K,Input.Keys.L,Input.Keys.SEMICOLON,Input.Keys.I,Input.Keys.P,Input.Keys.N});
	public static final KeyBindings IJKL_UO_SLASH = new KeyBindings(new int [] {Input.Keys.I,Input.Keys.J,Input.Keys.K,Input.Keys.L,Input.Keys.U,Input.Keys.O,Input.Keys.SLASH});
	//doesn't seem to be working
	public static final KeyBindings OKLSEMICOLON_IP_RSHIFT = new KeyBindings(new int [] {Input.Keys.O,Input.Keys.K,Input.Keys.L,Input.Keys.SEMICOLON,Input.Keys.I,Input.Keys.P,Input.Keys.SHIFT_RIGHT});
	//doesn't seem to be working
	public static final KeyBindings PLSEMICOLONAPOSTROPHE_OLEFTBRACKET_RSHIFT = new KeyBindings(new int [] {Input.Keys.P,Input.Keys.L,Input.Keys.SEMICOLON,Input.Keys.APOSTROPHE,Input.Keys.O,Input.Keys.LEFT_BRACKET,Input.Keys.SHIFT_RIGHT});
	
	public final int [] bindings;
	
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
	
	/**
	 * Checks if a set of KeyBindings are equal. 
	 * Good for determining which binding set is being used if you don't know.
	 * @param check
	 * @return
	 */
	public boolean equals(KeyBindings check) {
		return Arrays.equals(bindings, check.bindings);
	}	
	
	/**
	 * Checks to see if 2 sets of KeyBindings contain the same key.
	 * @param other
	 * @return
	 */
	public boolean checkCollisions(KeyBindings other) {
		for (int key : bindings) {
			for(int otherKey : other.bindings) {
				if(key == otherKey)
					return true;
			}
		}
		return false;
	}
}
