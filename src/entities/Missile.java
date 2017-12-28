
package entities;

import entities.bosses.Boss;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import logic.TrailGenerator;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class Missile extends Bullet{
    
    private final TrailGenerator trail;
    
    public Missile(double bs, double dam, double rel, double ht, double cool){
        super(bs, dam, rel, ht, cool);
        width = 9;
        height = 9;
        trail = new TrailGenerator(4, 2, 6, 9, 9, 240, 20, 20);
    }
    
    @Override
    public void render(Graphics g, long frameNum){
        g.setColor(new Color(240, 20, 20));
        g.fillRect(x, y, width, height);
        trail.paint((Graphics2D)g, x, y);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){
        synchronized(Main.soundSystem){
            move();
        }
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Bullet){
            if(!(ob instanceof Missile)) updateOtherVelocity(ob);
        }else if(!(ob instanceof Consumable)&&!(ob instanceof Boss&&((Boss) ob).flythroughMode)){
            hp = -1;
            ob.hp -= damage;
        }
    }
    
    @Override
    public Missile create(int sx, int sy, double vx, double vy){
        Missile b = new Missile(bulletSpeed, damage, reloadSpeed, bulletHeat, cooldownSpeed);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    @Override
    public Missile create(int sx, int sy, double vx, double vy, float mult){
        Missile b = new Missile(bulletSpeed, damage*mult, reloadSpeed, bulletHeat, cooldownSpeed);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
}
