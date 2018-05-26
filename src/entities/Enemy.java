
package entities;

import logic.Resistance;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Enemy extends GameObject{
    
    public int xp;
    public double damage;
    public double speed;
    public final double MAXSPEED, MAXDAMAGE;
    public boolean forced = false;

    public Enemy(String na, double health, double dam, int w, int h, int x, double sp, double ms, double md, Resistance res){
        super(na, health, w, h, res);
        xp = x;
        MAXSPEED = ms;
        MAXDAMAGE = md;
        speed = sp<ms?sp:ms;
        damage = dam<md?dam:md;
    }

    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ob.hurt(damage);
        }
    }
    
}
