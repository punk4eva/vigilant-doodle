
package entities;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Enemy extends GameObject{
    
    public int xp;
    public double damage;
    public double speed;

    public Enemy(String na, double health, double dam, int w, int h, int x, double sp){
        super(na, health, w, h);
        xp = x;
        speed = sp;
        damage = dam;
    }

    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ((Hero) ob).hurt(damage);
        }
    }
    
}
