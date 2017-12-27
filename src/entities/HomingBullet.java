
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
        if(target.y<y&&vely>-bulletSpeed){
            if(vely-0.1<-bulletSpeed){
                vely = -bulletSpeed;
            }else vely -= 0.1;
        }else if(target.y>y&&vely<bulletSpeed){
            if(vely+0.1>bulletSpeed){
                vely = bulletSpeed;
            }else vely += 0.1;
        }
        if(target.x<x&&velx>-bulletSpeed){
            if(velx-0.1<-bulletSpeed){
                velx = -bulletSpeed;
            }else velx -= 0.1;
        }else if(target.x>x&&velx<bulletSpeed){
            if(velx+0.1>bulletSpeed){
                velx = bulletSpeed;
            }else velx += 0.1;
        }
    }
    
}
