
package entities;

import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Enemy extends GameObject{
    
    public int xp;
    public double damage;
    public double speed;
    boolean alive = true;

    public Enemy(String na, double health, double dam, int w, int h, int x, double sp){
        super(na, health, w, h);
        xp = x;
        speed = sp;
        damage = dam;
    }
    
    
    @Override
    public void die(Handler handler){
        super.die(handler);
        alive = false;
        timer.stop();
        timer = null;
    }

    @Override
    public void collision(GameObject ob){
        if(ob instanceof Bullet){
            hp -= ((Bullet) ob).damage;
        }else if(ob instanceof Hero){
            ((Hero) ob).hurt(damage);
        }
    }
    
}
