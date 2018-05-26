
package entities;

import entities.bosses.Boss;
import entities.consumables.Buff.FireDebuff;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import logic.TrailGenerator;
import yoisupiru.Decider;
import yoisupiru.Main;
import static logic.ConstantFields.courseCorrectionFactor;
import logic.NonCollidable;

/**
 *
 * @author Adam Whittaker
 */
public class HomingBullet extends Bullet{
    
    GameObject target;
    
    public HomingBullet(double bs, double dam, double rel, double ht, double cool, GameObject targ){
        super(bs, dam, rel, ht, cool, null);
        target = targ;
    }
    
    public HomingBullet create(int sx, int sy, double vx, double vy, GameObject targ){
        HomingBullet b = new HomingBullet(bulletSpeed, damage, reloadSpeed, bulletHeat, cooldownSpeed, targ);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    public HomingBullet create(int sx, int sy, double vx, double vy, float mult, GameObject targ){
        HomingBullet b = new HomingBullet(bulletSpeed, damage*mult, reloadSpeed, bulletHeat, cooldownSpeed, targ);
        b.x = sx;
        b.y = sy;
        b.velx = vx;
        b.vely = vy;
        return b;
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        courseCorrection();
    }
    
    void courseCorrection(){
        if(target==null) return;
        int dx = target.x+target.width/2, dy = target.y+target.height/2;
        if(dy<y&&vely>-bulletSpeed){
            if(vely-courseCorrectionFactor<-bulletSpeed){
                vely = -bulletSpeed;
            }else vely -= courseCorrectionFactor;
        }else if(dy>y&&vely<bulletSpeed){
            if(vely+courseCorrectionFactor>bulletSpeed){
                vely = bulletSpeed;
            }else vely += courseCorrectionFactor;
        }
        if(dx<x&&velx>-bulletSpeed){
            if(velx-courseCorrectionFactor<-bulletSpeed){
                velx = -bulletSpeed;
            }else velx -= courseCorrectionFactor;
        }else if(dx>x&&velx<bulletSpeed){
            if(velx+courseCorrectionFactor>bulletSpeed){
                velx = bulletSpeed;
            }else velx += courseCorrectionFactor;
        }
    }
    
    public static class CooldownHomingBullet extends HomingBullet{
    
        private long spawnTime;
        
        public CooldownHomingBullet(double bs, double dam, double rel, double ht, double cool, GameObject targ){
            super(bs, dam, rel, ht, cool, targ);
        }
        
        @Override
        public CooldownHomingBullet create(int sx, int sy, double vx, double vy, GameObject targ){
            CooldownHomingBullet b = new CooldownHomingBullet(bulletSpeed, damage, reloadSpeed, bulletHeat, cooldownSpeed, targ);
            b.x = sx;
            b.y = sy;
            b.velx = vx;
            b.vely = vy;
            b.spawnTime = System.currentTimeMillis();
            return b;
        }

        @Override
        public CooldownHomingBullet create(int sx, int sy, double vx, double vy, float mult, GameObject targ){
            CooldownHomingBullet b = new CooldownHomingBullet(bulletSpeed, damage*mult, reloadSpeed, bulletHeat, cooldownSpeed, targ);
            b.x = sx;
            b.y = sy;
            b.velx = vx;
            b.vely = vy;
            b.spawnTime = System.currentTimeMillis();
            return b;
        }
        
        @Override
        public void collision(GameObject ob){
            if(!(ob instanceof Hero && System.currentTimeMillis()-spawnTime<750L))
                super.collision(ob);
        }
    
    }
    
    public static class HealthyHomingBullet extends HomingBullet{
        
        private double clock;
        private final TrailGenerator trail;
    
        public HealthyHomingBullet(double health, double t, double bs, double dam, double rel, double ht, double cool, GameObject targ){
            super(bs, dam, rel, ht, cool, targ);
            hp = health;
            clock = t;
            trail = new TrailGenerator(4, 2, 6, 9, 9, 20, 20, 240);
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            synchronized(Main.soundSystem){
                move();
            }
            courseCorrection();
            clock-=0.01;
            if(clock<=0) hp = -1;
        }
        
        @Override
        public void collision(GameObject ob){
            if(ob instanceof Bullet){
                if(!(ob instanceof NonCollidable)) updateBothVelocities(ob);
                hp -= ((Bullet)ob).damage;
            }else if(!(ob instanceof NonCollidable)&&!(ob instanceof Boss&&((Boss) ob).flythroughMode)){
                hp = -1;
                try{
                    if(ob.resistance.mode.equals(mode)) ob.hurt(damage*ob.resistance.mult);
                    else ob.hurt(damage);
                }catch(NullPointerException e){ob.hurt(damage);}
                if(ob instanceof Hero) ((Hero)ob).addBuff(new FireDebuff(-1, -1, 2, 2000+Decider.r.nextInt(3001)));
            }
        }
        
        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color(20, 20, 240));
            g.fillRect(x, y, width, height);
            trail.paint((Graphics2D)g, x, y);
        }
        
        @Override
        public HealthyHomingBullet create(int sx, int sy, double vx, double vy, GameObject targ){
            HealthyHomingBullet b = new HealthyHomingBullet(hp, clock, bulletSpeed, damage, reloadSpeed, bulletHeat, cooldownSpeed, targ);
            b.x = sx;
            b.y = sy;
            b.velx = vx;
            b.vely = vy;
            return b;
        }

        @Override
        public HealthyHomingBullet create(int sx, int sy, double vx, double vy, float mult, GameObject targ){
            HealthyHomingBullet b = new HealthyHomingBullet(hp, clock, bulletSpeed, damage*mult, reloadSpeed, bulletHeat, cooldownSpeed, targ);
            b.x = sx;
            b.y = sy;
            b.velx = vx;
            b.vely = vy;
            return b;
        }
    
    }
    
}
