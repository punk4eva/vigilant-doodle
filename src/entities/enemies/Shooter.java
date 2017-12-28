
package entities.enemies;

import entities.Bullet;
import entities.Enemy;
import entities.GameObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class Shooter extends Enemy{
    
    public GameObject target;
    private final Handler handler;
    public Bullet bullet;
    public double clock = 0;

    public Shooter(int level, GameObject targ, Handler hand){
        super("Shooter", 12+12*level, 0, 48, 48, 2*level, level*2);
        target = targ;
        handler = hand;
        bullet = new Bullet(4+level, 5+4*level, -1, -1, -1);
    }
    
    protected Shooter(String name, double health, int damage, int w, int h, int xp, double sp, GameObject targ, Handler hand){
        super(name, health, damage, w, h, xp, sp);
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
        if(r.nextInt(4)==0&&clock!=-1) clock += 0.25;
        if(clock>=4){
            shoot();
            clock = 0;
        }
    }

    void velTick(){
        if(Math.abs(velx)<speed){
            velx *= r.nextDouble() * 3 * (Math.abs(target.x-x))/Main.WIDTH;
        }
        if(Math.abs(vely)<speed){
            vely *= r.nextDouble() * 3 * (Math.abs(target.y-y))/Main.HEIGHT;
        }
        courseCorrection();
    }
    
    @Override
    public void die(Handler handler){
        super.die(handler);
        System.out.println("Die");
        bullet = null;
        clock = -1;
    }

    void shoot(){
        int cx = x+width/2, cy = y+height/2;
        double vx, vy;
        int sx, sy;
        double gradient = Math.abs(((double)target.y-cy)/(target.x-cx));
        if(target.x>cx){
            if(target.y<cy){ //1st Quartile
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
                    sy = cy+(int)(gradient*36);
                    vx = bullet.bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy+36;
                    sx = cx+(int)(36/gradient);
                    vy = bullet.bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }else{
            if(target.y>cy){ //3rd Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy+(int)(gradient*36);
                    vx = -bullet.bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy+36;
                    sx = cx-(int)(36/gradient);
                    vy = bullet.bulletSpeed;
                    vx = vy/-gradient;
                }
            }else{ //4th Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy-(int)(gradient*36);
                    vx = -bullet.bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy-36;
                    sx = cx-(int)(36/gradient);
                    vy = -bullet.bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }
        handler.addObject(bullet.create(sx, sy, vx, vy));
    }
    
    void courseCorrection(){
        if(target.y<y&&vely>-speed){
            if(vely-0.1<-speed){
                vely = -speed;
            }else vely -= 0.1;
        }else if(target.y>y&&vely<speed){
            if(vely+0.1>speed){
                vely = speed;
            }else vely += 0.1;
        }
        if(target.x<x&&velx>-speed){
            if(velx-0.1<-speed){
                velx = -speed;
            }else velx -= 0.1;
        }else if(target.x>x&&velx<speed){
            if(velx+0.1>speed){
                velx = speed;
            }else velx += 0.1;
        }
    }
    
}
