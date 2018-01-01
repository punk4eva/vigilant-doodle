
package entities.enemies;

import entities.Enemy;
import entities.GameObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import yoisupiru.Decider;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class Tank extends Enemy{

    protected GameObject target;
    private final Handler handler;
    public double clock = 0;
    
    public Tank(int level, GameObject targ, Handler hand){
        super("Tank", 40+40*level, 20+5*level, 80, 80, 4, (double)level/4.5d, 1.8, 115);
        target = targ;
        handler = hand;
    }

    @Override
    public void render(Graphics g1, long frameNum){
        Graphics2D g = (Graphics2D)g1;
        g.setColor(new Color((int)frameNum%100, 75+target.y%50, (int)(100*hp/maxhp)+40));
        g.fillOval(x, y, width, height);
        g.setColor(Color.black);
        g.fillOval(x+8, y+8, 64, 64);
        AffineTransform at = new AffineTransform();
        at.rotate((frameNum/2)%(2*Math.PI), x+width/2, y+height/2);
        Rectangle rect = new Rectangle(x+24, y+24, 32, 32);
        g.setColor(new Color(((int)((100*hp/maxhp)+40)%235), 75+(target.x%50), (int)frameNum%100));
        g.fill(at.createTransformedShape(rect));
        rect = new Rectangle(x+32, y+32, 16, 16);
        g.setColor(Color.black);
        g.fill(at.createTransformedShape(rect));
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        if(Decider.r.nextInt(4)==0&&clock!=-1){
            clock += 0.25;
            courseCorrection();
        }
        if(clock>=16){
            velTick();
            Tracker t = new Tracker(target, (int)(hp/20));
            t.x = x;
            t.y = y;
            handler.addObject(t);
            clock = 0;
        }
    }

    void velTick(){
        if(Math.abs(velx)<speed){
            velx *= (Decider.r.nextDouble()+0.7);
        }
        if(Math.abs(vely)<speed){
            vely *= (Decider.r.nextDouble()+0.7);
        }
    }
    
    @Override
    public void die(Handler handler){
        super.die(handler);
        clock = -1;
    }
    
    void courseCorrection(){
        double dx = target.x+target.width/2, dy = target.y+target.height/2;
        if(dy<y&&vely>-speed){
            if(vely-0.1<-speed){
                vely = -speed;
            }else vely -= 0.1;
        }else if(dy>y&&vely<speed){
            if(vely+0.1>speed){
                vely = speed;
            }else vely += 0.1;
        }
        if(dx<x&&velx>-speed){
            if(velx-0.1<-speed){
                velx = -speed;
            }else velx -= 0.1;
        }else if(dx>x&&velx<speed){
            if(velx+0.1>speed){
                velx = speed;
            }else velx += 0.1;
        }
    }
    
}
