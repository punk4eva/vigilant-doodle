
package yoisupiru;

import entities.Enemy;
import entities.Hero.ShootingMode;
import entities.consumables.WeaponUpgrade;
import entities.consumables.WeaponUpgrade.*;
import entities.enemies.Gunner;
import entities.enemies.Shooter;
import entities.enemies.Tank;
import entities.enemies.Tracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author Adam Whittaker
 */
public class Decider implements ActionListener, KeyListener{
    
    public Timer timer;
    public int level = 1;
    private final Handler handler;
    private String mode = "Normal";
    public static final Random r = new Random();
    
    public Decider(Main main){
        handler = main.handler;
        timer = new Timer(7500, this);
        timer.start();
        main.addKeyListener(this);
    }
    
    public void levelChange(int l){
        level = l;
        timer = new Timer(2500 + 10000/l, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        if(r.nextInt(5)!=0) spawn();
        else{
            System.out.println("Upgrade");
            getUpgrade().spawn(handler);
        }
    }
    
    private void spawn(){
        System.out.println("Spawn");
        Enemy t = getEnemy();
        switch(r.nextInt(4)){
            case 0: //top
                t.y = 32;
                t.x = r.nextInt(Main.WIDTH-64) + 32;
                break;
            case 1: //bottom
                t.y = Main.HEIGHT - 32;
                t.x = r.nextInt(Main.WIDTH-64) + 32;
                break;
            case 2: //left
                t.x = 32;
                t.y = r.nextInt(Main.HEIGHT-64) + 32;
                break;
            default: //right
                t.x = Main.HEIGHT - 32;
                t.y = r.nextInt(Main.HEIGHT-64) + 32;
        }
        handler.addObject(t);
    }
    
    public Enemy getEnemy(){
        switch(mode){
            case "Tracker": return new Tracker(handler.hero, level);
            case "Shooter": return new Shooter(level, handler.hero, handler);
            case "Gunner": return new Gunner(level, handler.hero, handler);
            case "Tank": return new Tank(level, handler.hero, handler);
            default: if(r.nextInt(2+level)<level){
                    if(level<3||r.nextInt(4+level)>level) return new Shooter(level, handler.hero, handler);
                    else return new Gunner(level, handler.hero, handler);
                }else{
                    if(level<4||r.nextInt(5+level)>level) return new Tracker(handler.hero, level);
                    else return new Tank(level, handler.hero, handler);
                }
        }
    }
    
    public ShootingMode getWeapon(){
        switch(r.nextInt(5)){
            case 0: return ShootingMode.CONSTANT;
            case 1: return ShootingMode.MACHINE;
            case 2: return ShootingMode.BURST;
            case 3: return ShootingMode.GRENADE;
            default: return ShootingMode.SHOTGUN;
        }
    }
    
    public WeaponUpgrade getUpgrade(){
        switch(r.nextInt(4)){
            case 0: return new SpdUpgrade(1+r.nextInt(3), getWeapon());
            case 1: return new DmgUpgrade(1+r.nextInt(3), getWeapon());
            case 2: return new RldUpgrade(1+r.nextInt(3), getWeapon());
            default: return new ColUpgrade(1+r.nextInt(3), getWeapon());
        }
    }
    
    public void pause(){
        if(timer.isRunning()) timer.stop();
        else timer.start();
    }

    @Override
    public synchronized void keyTyped(KeyEvent ke){
        switch(ke.getKeyChar()){
            case '0': mode = "Normal"; break;
            case '1': mode = "Tracker"; break;
            case '2': mode = "Shooter"; break;
            case '3': mode = "Gunner"; break;
            case '4': mode = "Tank"; break;
            case 'o': Window.increaseVolume(); break;
            case 'l': Window.decreaseVolume(); break;
        }
    }
    @Override
    public void keyPressed(KeyEvent ke){}
    @Override
    public void keyReleased(KeyEvent ke){}
    
}
