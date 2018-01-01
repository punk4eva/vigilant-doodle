
package entities.enemies;

import entities.GameObject;
import entities.HomingBullet;
import entities.HomingBullet.HealthyHomingBullet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import yoisupiru.Decider;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class Grenadier extends Shooter{
    
    public Grenadier(double level, GameObject targ, Handler hand){
        super("Grenadier", 18+4d*level, 2*level, 32, 32, 5, level/4.0d, targ, hand, 2.3, 26);
        bullet = new HealthyHomingBullet(5+2d*level, 4, 1.0+0.05*level, 5+5d*level, -1, -1, -1, targ);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){
        move();
        velTick();
        if(Decider.r.nextInt(8)==0) clock += 0.05;
        if(clock>=1&&clock!=-1){
            shoot();
            clock = 0;
        }
    }
    
    @Override
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
        handler.addObject(((HomingBullet)bullet).create((int)sx, (int)sy, vx, vy, target));
    }
    
    @Override
    public void render(Graphics g, long frameNum){
        Graphics2D g2d = (Graphics2D)g;
        Rectangle rect = new Rectangle(x, y, width, height);
        AffineTransform at = AffineTransform.getRotateInstance(Math.PI/4d, rect.x+rect.width/2, rect.y+rect.height/2);
        g2d.setColor(getColor(frameNum));
        g2d.fill(at.createTransformedShape(rect));
        g2d.setColor(Color.black);
        rect = new Rectangle(x+7, y+7, 18, 18);
        g2d.fill(at.createTransformedShape(rect));
        g2d.setColor(new Color((int)(255.0*clock), 32, 32));
        g2d.fillOval(x+10, y+10, 12, 12);
        g2d.setColor(Color.black);
        g2d.fillOval(x+13, y+13, 6, 6);
    }
    
    protected Color getColor(long frameNum){
        return new Color((int)((frameNum/2)%233), 156, (int)((frameNum/2)%233));
    }
    
}
