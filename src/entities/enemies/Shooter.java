
package entities.enemies;

import entities.Bullet;
import entities.Enemy;
import entities.GameObject;
import entities.Hero;
import entities.Hero.ShootingMode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import static logic.ConstantFields.courseCorrectionFactor;
import logic.Resistance;
import yoisupiru.Decider;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class Shooter extends Enemy{
    
    public GameObject target;
    final Handler handler;
    public Bullet bullet;
    public double clock = 0;

    public Shooter(int level, GameObject targ, Handler hand){
        super("Shooter", 12+12*level, 0, 48, 48, 2, level*2, 4.5, 0, new Resistance(ShootingMode.CONSTANT, 0.88));
        target = targ;
        handler = hand;
        int bd = 5+4*level;
        bullet = new Bullet(4+level, bd<45?bd:45, -1, -1, -1, ShootingMode.CONSTANT);
    }
    
    protected Shooter(String name, double health, double damage, int w, int h, int xp, double sp, GameObject targ, Handler hand, double ms, double md, Resistance res){
        super(name, health, damage, w, h, xp, sp, ms, md, res);
        target = targ;
        handler = hand;
    }

    @Override
    public void render(Graphics g, long frameNum){
        Graphics2D g2d = (Graphics2D)g; 
        g2d.setColor(new Color(32, (int)(frameNum%33), 200));
        g2d.fillRect(x, y, 48, 48);
        g2d.setColor(Color.black);
        g2d.fillRect(x+10, y+10, 28, 28);
        g2d.setColor(new Color((int)(255.0*clock/4), 32, 32));
        Rectangle rect = new Rectangle(x+18, y+18, 12, 12);
        AffineTransform at = new AffineTransform();
        at.rotate(((double)frameNum/10)%(2*Math.PI), rect.x+rect.width/2, rect.y+rect.height/2);
        g2d.fill(at.createTransformedShape(rect));
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        velTick();
        if(Decider.r.nextInt(4)==0&&clock!=-1) clock += 0.25;
        if(clock>=4){
            shoot();
            clock = 0;
        }
    }

    void velTick(){
        if(Math.abs(velx)<speed){
            velx *= Decider.r.nextDouble() * 3d * (double)(Math.abs(target.x-x))/Main.WIDTH;
        }
        if(Math.abs(vely)<speed){
            vely *= Decider.r.nextDouble() * 3d * (double)(Math.abs(target.y-y))/Main.HEIGHT;
        }
        courseCorrection();
    }
    
    @Override
    public void die(Handler handler){
        super.die(handler);
        bullet = null;
        clock = -1;
    }

    void shoot(){
        int cx = x+width/2, cy = y+height/2;
        double vx, vy, dx = target.x+target.width/2, dy = target.y+target.height/2, sx, sy;
        double gradient = Math.abs((dy-(double)cy)/(dx-(double)cx));
        if(dx>cx){
            if(dy<cy){ //1st Quartile
                if(gradient<1.0){
                    sx = cx+36;
                    sy = cy-(int)(gradient*36d);
                    vx = bullet.bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy-36;
                    sx = cx+(int)(36d/gradient);
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
        handler.addObject(bullet.create((int)sx, (int)sy, vx, vy));
    }
    
    void courseCorrection(){
        double dx = target.x+target.width/2, dy = target.y+target.height/2;
        if(dy<y&&vely>-speed){
            if(vely-courseCorrectionFactor<-speed){
                vely = -speed;
            }else vely -= courseCorrectionFactor;
        }else if(dy>y&&vely<speed){
            if(vely+courseCorrectionFactor>speed){
                vely = speed;
            }else vely += courseCorrectionFactor;
        }
        if(dx<x&&velx>-speed){
            if(velx-courseCorrectionFactor<-speed){
                velx = -speed;
            }else velx -= courseCorrectionFactor;
        }else if(dx>x&&velx<speed){
            if(velx+courseCorrectionFactor>speed){
                velx = speed;
            }else velx += courseCorrectionFactor;
        }
    }
    
}
