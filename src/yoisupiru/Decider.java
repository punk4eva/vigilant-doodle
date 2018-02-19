
package yoisupiru;

import entities.Consumable;
import entities.Enemy;
import entities.Hero.ShootingMode;
import entities.bosses.*;
import entities.consumables.WeaponUpgrade.*;
import entities.consumables.Buff;
import entities.consumables.Buff.*;
import entities.enemies.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.Timer;
import static logic.ConstantFields.minSpawnDelay;
import static logic.ConstantFields.spawnDelay;

/**
 *
 * @author Adam Whittaker
 */
public class Decider implements ActionListener, KeyListener{
    
    public Timer timer;
    public int level = 1;
    private final Handler handler;
    private String mode = "Normal";
    private boolean boss;
    public static final Random r = new Random();
    
    public Decider(Main main){
        handler = main.handler;
        int sp = 1500+spawnDelay;
        timer = new Timer(sp<minSpawnDelay ? minSpawnDelay : sp, this);
        timer.start();
        main.addKeyListener(this);
    }
    
    public void levelChange(int l){
        level = l;
        int sp = 2500 + spawnDelay/l;
        timer = new Timer(sp<minSpawnDelay ? minSpawnDelay : sp, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        switch(r.nextInt(8)){
            case 0: getUpgrade().spawn(handler); break;
            case 1: case 2: getBuff().spawn(handler); break;
            default: if(!boss) spawn();
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
        if(t instanceof Boss){
            ((Boss) t).playTheme();
            boss = true;
            handler.clearEnemies();
        }
        if(t instanceof TheIncinerator){
            t.x = 0;
            t.y = 0;
            ((TheIncinerator) t).spawnFuelTank(handler);
        }
        handler.addObject(t);
    }
    
    public Enemy getEnemy(){
        switch(mode){
            case "Tracker": return new Tracker(handler.hero, level);
            case "Shooter": return new Shooter(level, handler.hero, handler);
            case "Gunner": return new Gunner(level, handler.hero, handler);
            case "Tank": return new Tank(level, handler.hero, handler);
            case "Eviscerator": return new TheEviscerator(handler, handler.hero);
            case "Incinerator": return new TheIncinerator(handler, handler.hero);
            case "Twins": return new TheTwins(handler.hero, handler);
            default: if(level==4) return new TheEviscerator(handler, handler.hero);
                if(level==8) return new TheIncinerator(handler, handler.hero);
                if(level==12) return new TheTwins(handler.hero, handler);
                if(r.nextInt(2+level)<level){
                    if(level<3||r.nextInt(4+level)>level) return new Shooter(level, handler.hero, handler);
                    else if(level<7||r.nextInt(5+level)>level) return new Gunner(level, handler.hero, handler);
                    else return new Grenadier(level, handler.hero, handler);
                }else{
                    if(level<5||r.nextInt(6+level)>level) return new Tracker(handler.hero, level);
                    else if(level<9||r.nextInt(6+level)>level) return new Tank(level, handler.hero, handler);
                    else return new Bomb(level, handler.hero);
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
    
    public Consumable getUpgrade(){
        switch(r.nextInt(7)){
            case 0: return new RegUpgrade(1+r.nextInt(3));
            case 1: return new HpUpgrade(1+r.nextInt(3));
            default: ShootingMode s = getWeapon();
            switch(s){
                case BURST: case SHOTGUN: switch(r.nextInt(5)){
                    case 0: return new SpdUpgrade(1+r.nextInt(3), s);
                    case 1: return new DmgUpgrade(1+r.nextInt(3), s);
                    case 2: return new RldUpgrade(1+r.nextInt(3), s);
                    case 3: return new AmoUpgrade(s);
                    default: return new ColUpgrade(1+r.nextInt(3), s);
                }
                default: switch(r.nextInt(4)){
                    case 0: return new SpdUpgrade(1+r.nextInt(3), s);
                    case 1: return new DmgUpgrade(1+r.nextInt(3), s);
                    case 2: return new RldUpgrade(1+r.nextInt(3), s);
                    default: return new ColUpgrade(1+r.nextInt(3), s);
                }
            }
        }
    }
    
    public Buff getBuff(){
        switch(r.nextInt(6)){
            case 0: return new SpdBuff(1+r.nextInt(3), 3000+(int)(r.nextDouble()*12000));
            case 1: return new DmgBuff(1+r.nextInt(3), 3000+(int)(r.nextDouble()*12000));
            case 2: return new ImmBuff(1+r.nextInt(3), 2000+(int)(r.nextDouble()*7000));
            case 3: return new RegBuff(1+r.nextInt(3), 2000+(int)(r.nextDouble()*7500));
            case 4: return new ShdBuff(1+r.nextInt(3), 3000+(int)(r.nextDouble()*12000));
            default: return new HpBuff(1+r.nextInt(3));
        }
    }
    
    public void pause(){
        if(timer.isRunning()) timer.stop();
        else timer.start();
    }
    
    public void bossSlain(){
        Main.soundSystem.playAbruptLoop("backtrack.wav");
        boss = false;
    }

    @Override
    public synchronized void keyTyped(KeyEvent ke){
        switch(ke.getKeyChar()){
            case '0': mode = "Normal"; break;
            case '1': mode = "Tracker"; break;
            case '2': mode = "Shooter"; break;
            case '3': mode = "Gunner"; break;
            case '4': mode = "Tank"; break;
            case '5': mode = "Eviscerator"; break;
            case '6': mode = "Incinerator"; break;
            case '7': mode = "Twins"; break;
            case 'o': Window.increaseVolume(); break;
            case 'l': Window.decreaseVolume(); break;
        }
    }
    @Override
    public void keyPressed(KeyEvent ke){}
    @Override
    public void keyReleased(KeyEvent ke){}
    
}
