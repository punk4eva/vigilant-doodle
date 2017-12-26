
package entities.consumables;

import entities.Bullet;
import entities.Consumable;
import entities.GameObject;
import entities.Hero;
import entities.Hero.ShootingMode;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Adam Whittaker
 */
public abstract class WeaponUpgrade extends Consumable{
    
    public final Bullet imprint;
    public final int amount;
    public final ShootingMode gun;
    
    public WeaponUpgrade(String na, int w, int h, Bullet b, ShootingMode s){
        super(na, w, h);
        imprint = b;
        gun = s;
        amount = 0;
    }
    
    public WeaponUpgrade(String na, int w, int h, Bullet b, int a, ShootingMode s){
        super(na, w, h);
        imprint = b;
        amount = a;
        gun = s;
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ShootingMode.upgrade(this);
            hp = -1;
        }
    }
    
    public static class DmgUpgrade extends WeaponUpgrade{

        public DmgUpgrade(double dam, ShootingMode s){
            super("Dmg upgrade", 8, 8, new Bullet(0,0,0,0,0), s);
            switch(s){
                case CONSTANT: color = Color.BLUE; imprint.damage += dam*0.2; break;
                case MACHINE: color = Color.MAGENTA; imprint.damage += dam*0.15; break;
                case BURST: color = Color.YELLOW; imprint.damage += dam*0.17; break;
                case SHOTGUN: color = Color.PINK; imprint.damage += dam; break;
                case GRENADE: color = Color.RED; imprint.damage += dam*4; break;
            }
        }
        
        private Color color;

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class SpdUpgrade extends WeaponUpgrade{

        public SpdUpgrade(double v, ShootingMode s){
            super("Spd upgrade", 8, 8, new Bullet(0,0,0,0,0), s);
            switch(s){
                case CONSTANT: color = Color.BLUE; imprint.bulletSpeed += 0.15*v; break;
                case MACHINE: color = Color.MAGENTA; imprint.bulletSpeed += 0.3*v; break;
                case BURST: color = Color.YELLOW; imprint.bulletSpeed += 0.2*v; break;
                case SHOTGUN: color = Color.PINK; imprint.bulletSpeed += 0.15*v; break;
                case GRENADE: color = Color.RED; imprint.bulletSpeed += 0.05*v; break;
            }
        }
        
        private Color color;

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class RldUpgrade extends WeaponUpgrade{

        public RldUpgrade(double v, ShootingMode s){
            super("Rld upgrade", 8, 8, new Bullet(0,0,0,0,0), s);
            switch(s){
                case CONSTANT: color = Color.BLUE; imprint.reloadSpeed += 0.1*v; break;
                case MACHINE: color = Color.MAGENTA; imprint.reloadSpeed += 0.2*v; break;
                case BURST: color = Color.YELLOW; imprint.reloadSpeed += 0.08*v; break;
                case SHOTGUN: color = Color.PINK; imprint.reloadSpeed += 0.05*v; break;
                case GRENADE: color = Color.RED; imprint.reloadSpeed += 0.035*v; break;
            }
        }
        
        private Color color;

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class ColUpgrade extends WeaponUpgrade{

        public ColUpgrade(double v, ShootingMode s){
            super("Col upgrade", 8, 8, new Bullet(0,0,0,0,0), s);
            switch(s){
                case CONSTANT: color = Color.BLUE; imprint.bulletHeat -= 0.015*v; break;
                case MACHINE: color = Color.MAGENTA; imprint.bulletHeat -= 0.016*v; break;
                case BURST: color = Color.YELLOW; imprint.bulletHeat -= 0.012*v; break;
                case SHOTGUN: color = Color.PINK; imprint.bulletHeat -= 0.01*v; break;
                case GRENADE: color = Color.RED; imprint.bulletHeat -= 0.04*v; break;
            }
        }
        
        private Color color;

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
        
    }
    
}
