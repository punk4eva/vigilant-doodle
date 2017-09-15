
package entities;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import logic.Collision;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public abstract class GameObject extends Collision implements ActionListener{
    
    public volatile Timer timer;
    public volatile String animString;
    public volatile double maxhp;
    public volatile double hp;
    public final String name;
    
    public GameObject(String na, double health, int w, int h){
        name = na;
        maxhp = health;
        hp = health;
        width = w;
        height = h;
    }
    
    public GameObject(String na, double health, int delay, int w, int h){
        name = na;
        maxhp = health;
        timer = new Timer(delay, this);
        hp = health;
        width = w;
        height = h;
    }
    
    public void tick(Handler handler){
        healthCheck(handler);
    }
    
    public void move(){
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
    public synchronized void actionPerformed(ActionEvent ae){
        move();
    }
    
    protected void healthCheck(Handler handler){
        if(hp<1) die(handler);
    }
    
    protected void boundsCheck(Handler handler){
        if(x+width<=0||x>Main.WIDTH||y+height<=0||y>Main.HEIGHT){
            die(handler);
        }
    }
    
    protected void die(Handler handler){
        animString = "dying";
        handler.removeObject(this);
    }
    
}
