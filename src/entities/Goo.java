
package entities;

import entities.consumables.Buff.SlownessDebuff;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import logic.NonCollidable;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class Goo extends Bullet implements NonCollidable{
    
    final int number;
    private final double lvl;
    
    public Goo(int num, double l){
        super(-1, 0, -1, -1, -1);
        hp = 8;
        number = num;
        lvl = l;
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        velChange(velx*0.96, vely*0.96);
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ((Hero) ob).addBuff(new SlownessDebuff((long)(700*lvl), lvl));
        }else if(ob instanceof Goo) updateBothVelocities(ob);
    }
    
    @Override
    public Goo create(int sx, int sy, double vx, double vy){
        Goo b = new Goo(number, lvl);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    public static Goo create(int num, double l, int sx, int sy, double vx, double vy){
        Goo b = new Goo(num, l);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    public void shoot(Handler h, int sx, int sy, double vx, double vy){
        for(int n=0;n<number;n++) h.addObject(create(sx,sy,vx,vy));
    }
    
    public static void shoot(Handler h, int num, double l, int sx, int sy, double vx, double vy){
        for(int n=0;n<num;n++) h.addObject(create(num, l, sx,sy,vx,vy));
    }
    
    @Override
    public void render(Graphics g, long frameNum){
        g.setColor(new Color(240, 255, 230));
        g.fillRect(x, y, width, height);
    }
    
}
