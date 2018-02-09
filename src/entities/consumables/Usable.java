
package entities.consumables;

import entities.Bullet;
import entities.Consumable;
import entities.GameObject;
import entities.Hero;
import entities.Missile;
import entities.bosses.Boss;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import static logic.ConstantFields.courseCorrectionFactor;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Usable extends Consumable{
    
    public Usable(String na, int w, int h){
        super(na, w, h);
        forced = true;
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero && hp!=-1){
            ((Hero) ob).addUsable(this);
            hp = -1;
        }
    }
    
    public abstract void use(Handler ha, Hero h, int ax, int ay);
    
    public static class HealingPotion extends Usable{

        final int healthRestore;
        
        public HealingPotion(int lvl){
            super("Healing Potion", 18, 18);
            healthRestore = lvl*7;
        }

        @Override
        public void use(Handler ha, Hero h, int ax, int ay){
            h.boostHp(healthRestore);
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color(255, (int)(frameNum%75), (int)(frameNum%75)));
            g.fillOval(x, y, width, height);
        }
        
    }
    
    public static class DeathMissile extends Usable{

        final ActualBullet bullet;
        
        public DeathMissile(int lvl){
            super("Death Missile", 18, 18);
            bullet = new ActualBullet(lvl*30);
        }

        @Override
        public void use(Handler handler, Hero h, int ax, int ay){
            int cx = h.x+h.width/2, cy = h.y+h.height/2;
            double vx, vy, sx, sy, dx = ax, dy = ay;
            double gradient = Math.abs(((double)dy-cy)/(dx-cx));
            if(dx>cx){
                if(dy<cy){ //1st Quartile
                    if(gradient<1.0){
                        sx = cx+36;
                        sy = cy-(int)(gradient*36);
                        vx = bullet.bulletSpeed;
                        vy = vx*-gradient;
                    }else{
                        sy = cy-36;
                        sx = cx+(int)(36/gradient);
                        vy = -bullet.bulletSpeed;
                        vx = vy/-gradient;
                    }
                }else{ //2nd Quartile
                    if(gradient<1.0){
                        sx = cx+36;
                        sy = cy+(int)(gradient*36d);
                        vx = bullet.bulletSpeed;
                        vy = vx*gradient;
                    }else{
                        sy = cy+36;
                        sx = cx+(int)(36d/gradient);
                        vy = bullet.bulletSpeed;
                        vx = vy/gradient;
                    }
                }
            }else{
                if(dy>cy){ //3rd Quartile
                    if(gradient<1.0){
                        sx = cx-36;
                        sy = cy+(int)(gradient*36d);
                        vx = -bullet.bulletSpeed;
                        vy = vx*-gradient;
                    }else{
                        sy = cy+36;
                        sx = cx-(int)(36d/gradient);
                        vy = bullet.bulletSpeed;
                        vx = vy/-gradient;
                    }
                }else{ //4th Quartile
                    if(gradient<1.0){
                        sx = cx-36;
                        sy = cy-(int)(gradient*36d);
                        vx = -bullet.bulletSpeed;
                        vy = vx*gradient;
                    }else{
                        sy = cy-36;
                        sx = cx-(int)(36d/gradient);
                        vy = -bullet.bulletSpeed;
                        vx = vy/gradient;
                    }
                }
            }
            handler.addObject(bullet.create((int)sx, (int)sy, vx, vy, h.shootingMode.findNearestEnemy(handler, h)));
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color(150+(int)(frameNum%75), 150+(int)(frameNum%75), 150+(int)(frameNum%75)));
            g.fillOval(x, y, width, height);
        }
        
        private static class ActualBullet extends Missile{
            
            GameObject target;
            
            public ActualBullet(double dam){
                super(7, dam, -1, -1, -1);
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
                    if(velx+0.1>bulletSpeed){
                        velx = bulletSpeed;
                    }else velx += courseCorrectionFactor;
                }
            }
            
            public ActualBullet create(int sx, int sy, double vx, double vy, GameObject targ){
                ActualBullet b = new ActualBullet(damage);
                b.x = sx;
                b.y = sy;
                b.target = targ;
                b.velx = vx;
                b.vely = vy;
                return b;
            }
            
            @Override
            public synchronized void actionPerformed(ActionEvent ae){
                super.actionPerformed(ae);
                courseCorrection();
            }
            
        }
    
    }
    
    public static class Shield extends Usable{

        private final int ti;
        
        public Shield(int lvl){
            super("Shield", 18, 18);
            ti = 12*lvl;
        }

        @Override
        public void use(Handler ha, Hero h, int ax, int ay){
            h.boostInvulnerability(ti);
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color((int)(frameNum%100), 213, 199));
        }
        
    }
    
}
