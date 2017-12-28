
package yoisupiru;

import entities.Consumable;
import entities.Enemy;
import entities.GameObject;
import entities.Hero;
import entities.bosses.Boss;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.Timer;

/**
 *
 * @author Adam Whittaker
 */
public class Handler implements ActionListener{
    
    public final List<GameObject> objects = new LinkedList<>();
    private final Timer timer;
    volatile Hero hero;
    private volatile LinkedBlockingQueue<Runnable> queuedEvents = new LinkedBlockingQueue<>();
    private final int MOBCAP = 12;
    private final int CONSCAP = 12;
    private int mobNum = 0, consNum = 0;
    
    public Handler(Hero h){
        hero = h;
        timer = new Timer(5, this);
        timer.start();
        new Thread(() -> {
            while(true) try{
                queuedEvents.take().run();
            }catch(InterruptedException ex){}
        }).start();
    }
    
    public void addObject(GameObject ob){
        queuedEvents.add(() -> {
            if(ob instanceof Enemy){
                if(mobNum<MOBCAP) synchronized(objects){
                    mobNum++;
                    timer.addActionListener(ob);
                    objects.add(ob);
                }
            }else if(ob instanceof Consumable){
                if(consNum<CONSCAP&&!((Consumable)ob).forced) synchronized(objects){
                    consNum++;
                    timer.addActionListener(ob);
                    objects.add(ob);
                }
            }else{
                synchronized(objects){
                    timer.addActionListener(ob);
                    objects.add(ob);
                }
            }
        });
    }
    
    public void removeObject(GameObject ob){
        queuedEvents.add(() -> {
            synchronized(objects){
                timer.removeActionListener(ob);
                objects.remove(ob);
            }
        });
        if(ob instanceof Enemy){
            mobNum--;
            int l = hero.level;
            hero.tryLevelUp(((Enemy) ob).xp);
            System.err.println("Level: " + hero.level + "\nXP: " + hero.xp + " / " + hero.maxxp);
            if(hero.level!=l){
                Main.decider.levelChange(hero.level);
                Main.soundSystem.playSFX("levelUp.wav");
            }else Main.soundSystem.playSFX("Death.wav");
            if(ob instanceof Boss) Main.decider.bossSlain();
        }else if(ob instanceof Hero){
            System.out.println("You died on level " + hero.level + "!");
            System.exit(0);
        }else if(ob instanceof Consumable && !((Consumable)ob).forced) consNum--;
    }
    
    public void render(Graphics g, long frameNum){
        synchronized(objects){
            objects.stream().filter(o -> o.alive).forEach((o) -> {
                o.render(g, frameNum);
            });
        }
    }
    
    public void tick(){
        synchronized(objects){
            objects.stream().filter(o -> o.alive).forEach(o -> {
                o.tick(this);
            });
        }
    }
    
    private void collisionDetection(GameObject ob){
        objects.stream().filter(o -> ob.isColliding(o)).forEach(o -> {
            ob.collision(o);
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        synchronized(objects){
            objects.stream().forEach(o -> {
                collisionDetection(o);
            });
        }
    }
    
    public void pause(){
        if(timer.isRunning()) timer.stop();
        else timer.start();
    }
    
}
