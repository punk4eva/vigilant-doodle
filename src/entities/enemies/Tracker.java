
package entities.enemies;

import entities.Enemy;
import entities.GameObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

/**
 *
 * @author Adam Whittaker
 */
public class Tracker extends Enemy{
    
    public GameObject target;
    
    public Tracker(GameObject targ){
        super("Tracker", 40, 5, 32, 32, 1, 1.5);
        target = targ;
    }
    
    public Tracker(GameObject targ, int level){
        super("Tracker", 10+10*level, 4+2*level, 32, 32, level, level*1.5);
        target = targ;
    }

    @Override
    public void render(Graphics g, long frameNum){
        int col = (int)(((double)hp/maxhp)*254.5);
        try{
            g.setColor(new Color(col, (int)(frameNum%256), col));
        }catch(Exception e){
            g.setColor(new Color(128, 64, 156));
        }
        g.fillRect(x, y, width, height);
        g.setColor(Color.black);
        g.fillRect(x+8, y+8, 16, 16);
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        courseCorrection();
    }
    
    void courseCorrection(){
        if(target.y<y&&vely>-speed){
            if(vely-0.1<-speed){
                vely = -speed;
            }else vely -= 0.1;
        }else if(target.y>y&&vely<speed){
            if(vely+0.1>speed){
                vely = speed;
            }else vely += 0.1;
        }
        if(target.x<x&&velx>-speed){
            if(velx-0.1<-speed){
                velx = -speed;
            }else velx -= 0.1;
        }else if(target.x>x&&velx<speed){
            if(velx+0.1>speed){
                velx = speed;
            }else velx += 0.1;
        }
    }
    
}
