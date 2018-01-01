
package entities.enemies;

import entities.Bullet;
import entities.GameObject;
import entities.Hero;
import entities.consumables.Buff.FireDebuff;
import java.awt.Color;
import java.awt.Graphics;
import yoisupiru.Decider;

/**
 *
 * @author Adam Whittaker
 */
public class Bomb extends Tracker{

    public Bomb(double level, GameObject targ){
        super("Bomb", 25+level*3, 20+level*6, 12, 12, 2, 1+0.05*level, targ, 2.0, 47);
    }

    @Override
    public void render(Graphics g, long frameNum){
        g.setColor(new Color((int)(100+frameNum%100), (int)(frameNum%60), (int)(frameNum%60)));
        g.fillRect(x, y, width, height);
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ob.hurt(damage);
            ((Hero)ob).addBuff(new FireDebuff(-1, -1, 1, 1000+Decider.r.nextInt(3001)));
            hp = -1;
        }else if(ob instanceof Bullet){
            updateBothVelocities(ob);
        }
    }
    
}
