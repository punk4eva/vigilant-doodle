
package entities.enemies;

import entities.Bullet;
import entities.GameObject;
import entities.Hero;
import entities.Hero.ShootingMode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import logic.Resistance;
import yoisupiru.Decider;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class Gunner extends Shooter{
    
    public Gunner(int level, GameObject targ, Handler hand){
        super("Gunner", 15*(level+1), level, 48, 48, 3, 2.75+0.25*(double)level, targ, hand, 5.5, 21, new Resistance(ShootingMode.BURST, 0.85));
        bullet = new Bullet(8+level, 3+2*level, -1, -1, -1, ShootingMode.MACHINE);
    }
    
    public Gunner(String name, double health, int damage, int w, int h, int xp, double sp, GameObject targ, Handler hand, double ms, double md, Resistance res){
        super(name, health, damage, w, h, xp, sp, targ, hand, ms, md, res);
    }
    
    @Override
    public void render(Graphics g, long frameNum){
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform at = new AffineTransform();
        Rectangle rect = new Rectangle(x, y, 48, 48);
        at.rotate((-((double)frameNum/2)%(2*Math.PI)), rect.x+rect.width/2, rect.y+rect.height/2);
        g2d.setColor(new Color(32, (int)(frameNum%233), 50));
        g2d.fill(at.createTransformedShape(rect));
        g2d.setColor(Color.black);
        rect = new Rectangle(x+10, y+10, 28, 28);
        g2d.fill(at.createTransformedShape(rect));
        g2d.setColor(new Color((int)(255.0*clock), 32, 32));
        rect = new Rectangle(x+18, y+18, 12, 12);
        at = new AffineTransform();
        at.rotate(((double)frameNum/2)%(2*Math.PI), rect.x+rect.width/2, rect.y+rect.height/2);
        g2d.fill(at.createTransformedShape(rect));
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        move();
        velTick();
        if(Decider.r.nextInt(4)==0) clock += 0.2;
        if(clock>=1&&clock!=-1){
            shoot();
            clock = 0;
        }
    }
    
}
