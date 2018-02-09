
package entities.enemies;

import entities.Enemy;
import entities.GameObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import static logic.ConstantFields.courseCorrectionFactor;

/**
 *
 * @author Adam Whittaker
 */
public class Tracker extends Enemy{
    
    public GameObject target;
    
    public Tracker(GameObject targ){
        super("Tracker", 40, 5, 32, 32, 1, 1.5, 7.0, Integer.MAX_VALUE);
        target = targ;
    }
    
    public Tracker(GameObject targ, int level){
        super("Tracker", 10+10*level, 4+2*level, 32, 32, 1, level*1.5, 7.0, Integer.MAX_VALUE);
        target = targ;
    }
    
    protected Tracker(String name, double health, double damage, int w, int h, int xp, double sp, GameObject targ, double ms, double md){
        super(name, health, damage, w, h, xp, sp, ms, md);
        target = targ;
    }

    @Override
    public void render(Graphics g, long frameNum){
        g.setColor(getColor(frameNum));
        g.fillRect(x, y, width, height);
        g.setColor(Color.black);
        g.fillRect(x+8, y+8, 16, 16);
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        courseCorrection();
    }
    
    protected void courseCorrection(){
        double dx = target.x+target.width/2, dy = target.y+target.height/2;
        if(dy<y&&vely>-speed){
            if(vely-courseCorrectionFactor<-speed){
                vely = -speed;
            }else vely -= courseCorrectionFactor;
        }else if(dy>y&&vely<speed){
            if(vely+courseCorrectionFactor>speed){
                vely = speed;
            }else vely += courseCorrectionFactor;
        }
        if(dx<x&&velx>-speed){
            if(velx-courseCorrectionFactor<-speed){
                velx = -speed;
            }else velx -= courseCorrectionFactor;
        }else if(dx>x&&velx<speed){
            if(velx+courseCorrectionFactor>speed){
                velx = speed;
            }else velx += courseCorrectionFactor;
        }
    }
    
    protected Color getColor(long frameNum){
        int col = (int)((hp/maxhp)*250d);
        return new Color(col>0?col:0, (int)(frameNum%256), col>0?col:0);
    }
    
}
