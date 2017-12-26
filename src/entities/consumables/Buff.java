
package entities.consumables;

import entities.Consumable;
import entities.GameObject;
import entities.Hero;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Buff extends Consumable{
    
    private long time;
    
    public Buff(String na, int w, int h, long t){
        super(na, w, h);
        time = t;
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ((Hero) ob).addBuff(this);
            hp = -1;
        }
    }

    public void startTime(){
        time += System.currentTimeMillis();
    }
    
    public boolean isOver(){
        return System.currentTimeMillis()>time;
    }

    public abstract void start();

    public abstract void end();
    
}
