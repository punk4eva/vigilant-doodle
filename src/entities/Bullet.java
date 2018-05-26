
package entities;

import entities.Hero.ShootingMode;
import entities.bosses.Boss;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import logic.NonCollidable;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class Bullet extends GameObject{
    
    public double bulletSpeed;
    public double damage;
    public double reloadSpeed;
    public double bulletHeat;
    public double cooldownSpeed;
    public ShootingMode mode;
    
    public Bullet(double bs, double dam, double rel, double ht, double cool, ShootingMode m){
        super("bullet", 5, 6, 6);
        bulletSpeed = bs;
        mode = m;
        damage = dam;
        reloadSpeed = rel;
        bulletHeat = ht;
        cooldownSpeed = cool;
    }

    @Override
    public void tick(Handler handler){
        super.tick(handler);
        super.boundsCheck(handler);
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        hp-=0.01;
    }

    @Override
    public void render(Graphics g, long frameNum){
        g.setColor(new Color(40, 255, 0));
        g.fillRect(x, y, width, height);
    }

    @Override
    public void collision(GameObject ob){
        if(ob instanceof Bullet){
            if(!(ob instanceof NonCollidable)) updateBothVelocities(ob);
        }else if(!(ob instanceof NonCollidable)&&!(ob instanceof Boss&&((Boss) ob).flythroughMode)){
            hp = -1;
            try{
                if(ob.resistance.mode.equals(mode)) ob.hurt(damage*ob.resistance.mult);
                else ob.hurt(damage);
            }catch(NullPointerException e){ob.hurt(damage);}
        }
    }
    
    public Bullet create(int sx, int sy, double vx, double vy){
        Bullet b = new Bullet(bulletSpeed, damage, reloadSpeed, bulletHeat, cooldownSpeed, mode);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    public Bullet create(int sx, int sy, double vx, double vy, float mult){
        Bullet b = new Bullet(bulletSpeed, damage*mult, reloadSpeed, bulletHeat, cooldownSpeed, mode);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    public void upgrade(Bullet b){
        bulletSpeed += b.bulletSpeed;
        damage += b.damage;
        reloadSpeed += b.reloadSpeed;
        bulletHeat += b.bulletHeat;
        cooldownSpeed += b.cooldownSpeed;
    }
    
}
