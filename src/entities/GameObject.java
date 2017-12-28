
package entities;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import logic.Collision;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public abstract class GameObject extends Collision implements ActionListener{
    
    public volatile double maxhp;
    public volatile double hp;
    public final String name;
    public volatile boolean alive = true;
    
    public GameObject(String na, double health, int w, int h){
        name = na;
        maxhp = health;
        hp = health;
        width = w;
        height = h;
    }
    
    public synchronized void tick(Handler handler){
        healthCheck(handler);
    }
    
    public synchronized void move(){
        xChange += velx%1.0;
        yChange += vely%1.0;
        if(xChange>=1){
            x += velx + 1;
            xChange %= 1.0;
        }else x += velx;
        if(yChange>=1){
            y += vely + 1;
            yChange %= 1.0;
        }else y += vely;
    }
    
    public abstract void render(Graphics g, long frameNum);
    
    @Override
    public void actionPerformed(ActionEvent ae){
        synchronized(Main.soundSystem){
            move();
        }
    }
    
    protected synchronized void healthCheck(Handler handler){
        if(hp<1) die(handler);
    }
    
    protected synchronized void boundsCheck(Handler handler){
        if(x+width<=0||x>Main.WIDTH||y+height<=0||y>Main.HEIGHT){
            die(handler);
        }
    }
    
    protected void die(Handler handler){
        alive = false;
        handler.removeObject(this);
    }

    public void hurt(double damage){
        hp -= damage;
    }
    
}
