
package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class Bullet extends GameObject{
    
    final double bulletSpeed;
    final double damage;
    final double reloadSpeed;
    final double bulletHeat;
    final double cooldownSpeed;
    
    public Bullet(double bs, double dam, double rel, double ht, double cool){
        super("bullet", 5, 6, 6);
        timer = new Timer(5, this);
        timer.start();
        bulletSpeed = bs;
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
            updateBothVelocities(ob);
        }else{
            hp = -1;
            ob.hp -= damage;
        }
    }
    
    public Bullet create(int sx, int sy, double vx, double vy){
        Bullet b = new Bullet(bulletSpeed, damage, reloadSpeed, bulletHeat, cooldownSpeed);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    public Bullet create(int sx, int sy, double vx, double vy, double health){
        Bullet b = new Bullet(bulletSpeed, damage, reloadSpeed, bulletHeat, cooldownSpeed);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return health<=0 ? null : b;
    }
    
}
