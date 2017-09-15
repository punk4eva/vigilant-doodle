
package yoisupiru;

import entities.Enemy;
import entities.Gunner;
import entities.Shooter;
import entities.Tank;
import entities.Tracker;
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
    private static final Random r = new Random();
    
    public Decider(Main main){
        handler = main.handler;
        timer = new Timer(9000, this);
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
        spawn();
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

    @Override
    public void keyTyped(KeyEvent ke){
        switch(ke.getKeyChar()){
            case '0': mode = "Normal"; break;
            case '1': mode = "Tracker"; break;
            case '2': mode = "Shooter"; break;
            case '3': mode = "Gunner"; break;
            case '4': mode = "Tank"; break;
        }
    }
    @Override
    public void keyPressed(KeyEvent ke){}
    @Override
    public void keyReleased(KeyEvent ke){}
    
}
