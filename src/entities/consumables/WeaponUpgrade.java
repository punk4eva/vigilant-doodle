
package entities.consumables;

import entities.Bullet;
import entities.Consumable;
import entities.GameObject;
import entities.Hero;
import entities.Hero.ShootingMode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Adam Whittaker
 */
public abstract class WeaponUpgrade extends Consumable{
    
    public final Bullet imprint;
    public final int amount;
    public final ShootingMode gun;
    protected Color color;
    
    public WeaponUpgrade(String na, int w, int h, Bullet b, ShootingMode s, Color col){
        super(na, w, h);
        imprint = b;
        gun = s;
        amount = 0;
        color = col;
    }
    
    public WeaponUpgrade(String na, int w, int h, Bullet b, int a, ShootingMode s, Color col){
        super(na, w, h);
        imprint = b;
        amount = a;
        color = col;
        gun = s;
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero && hp!=-1){
            ShootingMode.upgrade(this);
            hp = -1;
        }
    }
    
    public static class DmgUpgrade extends WeaponUpgrade{

        public DmgUpgrade(double dam, ShootingMode s){
            super("Dmg upgrade", 16, 16, new Bullet(0,0,0,0,0), s, null);
            switch(s){
                case CONSTANT: color = new Color(0, 20, (int)(70*dam));      imprint.damage += dam*0.2; break;
                case MACHINE: color = new Color(240, (int)(30*dam), 240);    imprint.damage += dam*0.15; break;
                case BURST: color = new Color(248, (int)(50*dam)+100, 10);   imprint.damage += dam*0.17; break;
                case SHOTGUN: color = new Color(242, 160, 90+(int)(30*dam)); imprint.damage += dam; break;
                case GRENADE: color = new Color(243, 5+(int)(5*dam), 8);     imprint.damage += dam*4; break;
            }
        }
        
    }
    
    public static class SpdUpgrade extends WeaponUpgrade{

        public SpdUpgrade(double v, ShootingMode s){
            super("Spd upgrade", 16, 16, new Bullet(0,0,0,0,0), s, null);
            switch(s){
                case CONSTANT: color = new Color(0, 20, (int)(70*v));       imprint.bulletSpeed += 0.15*v; break;
                case MACHINE: color = new Color(240, (int)(30*v), 240);     imprint.bulletSpeed += 0.3*v; break;
                case BURST: color = new Color(248, (int)(50*v)+100, 10);    imprint.bulletSpeed += 0.2*v; break;
                case SHOTGUN: color = new Color(242, 160, 90+(int)(30*v));  imprint.bulletSpeed += 0.15*v; break;
                case GRENADE: color = new Color(243, 5+(int)(5*v), 8);     imprint.bulletSpeed += 0.05*v; break;
            }
        }
        
    }
    
    public static class RldUpgrade extends WeaponUpgrade{

        public RldUpgrade(double v, ShootingMode s){
            super("Rld upgrade", 16, 16, new Bullet(0,0,0,0,0), s, null);
            switch(s){
                case CONSTANT: color = new Color(0, 20, (int)(70*v));       imprint.reloadSpeed += 0.1*v; break;
                case MACHINE: color = new Color(240, (int)(30*v), 240);     imprint.reloadSpeed += 0.2*v; break;
                case BURST: color = new Color(248, (int)(50*v)+100, 10);    imprint.reloadSpeed += 0.08*v; break;
                case SHOTGUN: color = new Color(242, 160, 90+(int)(30*v));  imprint.reloadSpeed += 0.05*v; break;
                case GRENADE: color = new Color(243, 5+(int)(5*v), 8);     imprint.reloadSpeed += 0.035*v; break;
            }
        }
        
    }
    
    public static class ColUpgrade extends WeaponUpgrade{

        public ColUpgrade(double v, ShootingMode s){
            super("Col upgrade", 16, 16, new Bullet(0,0,0,0,0), s, null);
            switch(s){
                case CONSTANT: color = new Color(0, 20, 30+(int)(70*v));       imprint.bulletHeat -= 0.015*v; break;
                case MACHINE: color = new Color(240, (int)(30*v), 255);     imprint.bulletHeat -= 0.016*v; break;
                case BURST: color = new Color(248, (int)(50*v)+100, 40);    imprint.bulletHeat -= 0.012*v; break;
                case SHOTGUN: color = new Color(242, 160, 120+(int)(30*v));  imprint.bulletHeat -= 0.01*v; break;
                case GRENADE: color = new Color(243, 5+(int)(5*v), 38);     imprint.bulletHeat -= 0.04*v; break;
            }
        }
        
    }
    
    public static class AmoUpgrade extends WeaponUpgrade{

        public AmoUpgrade(ShootingMode s){
            super("Amo upgrade", 16, 16, new Bullet(0,0,0,0,0), 1, s, null);
            switch(s){
                case BURST: color = new Color(248, 150, 40); break;
                case SHOTGUN: color = new Color(242, 160, 150); break;
                default: throw new IllegalStateException("AmoUpgrade call.");
            }
        }
        
    }
    
    @Override
    public void render(Graphics ig, long frameNum){
        Graphics2D g = (Graphics2D) ig;
        g.setColor(color);
        Rectangle rect = new Rectangle(x, y, width, height);
        AffineTransform at = AffineTransform.getRotateInstance(((double)frameNum/20.0)%(2.0*Math.PI), x+width/2, y+height/2);
        g.fill(at.createTransformedShape(rect));
    }
    
}
