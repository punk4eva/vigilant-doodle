
package entities;

import java.awt.event.ActionEvent;

/**
 *
 * @author Adam Whittaker
 */
public class HomingBullet extends Bullet{
    
    GameObject target;
    
    public HomingBullet(double bs, double dam, double rel, double ht, double cool, GameObject targ){
        super(bs, dam, rel, ht, cool);
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
            if(vely-0.1<-bulletSpeed){
                vely = -bulletSpeed;
            }else vely -= 0.1;
        }else if(dy>y&&vely<bulletSpeed){
            if(vely+0.1>bulletSpeed){
                vely = bulletSpeed;
            }else vely += 0.1;
        }
        if(dx<x&&velx>-bulletSpeed){
            if(velx-0.1<-bulletSpeed){
                velx = -bulletSpeed;
            }else velx -= 0.1;
        }else if(dx>x&&velx<bulletSpeed){
            if(velx+0.1>bulletSpeed){
                velx = bulletSpeed;
            }else velx += 0.1;
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
    
}
