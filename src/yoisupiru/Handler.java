
package yoisupiru;

import entities.Enemy;
import entities.GameObject;
import entities.Hero;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import javax.swing.Timer;

/**
 *
 * @author Adam Whittaker
 */
public class Handler implements ActionListener{
    
    public volatile LinkedList<GameObject> objects = new LinkedList<>();
    private volatile LinkedList<GameObject> newObjects = new LinkedList<>();
    private volatile LinkedList<GameObject> oldObjects = new LinkedList<>();
    private volatile Timer movementTimer;
    volatile Hero hero;
    
    public Handler(Hero h){
        hero = h;
        movementTimer = new Timer(5, this);
        movementTimer.start();
    }
    
    public synchronized void addObject(GameObject ob){
        newObjects.add(ob);
    }
    
    public synchronized void removeObject(GameObject ob){
        oldObjects.add(ob);
        if(ob instanceof Enemy){
            int l = hero.level;
            hero.tryLevelUp(((Enemy) ob).xp);
            System.err.println("Level: " + hero.level + "\nXP: " + hero.xp + " / " + hero.maxxp);
            if(hero.level!=l){
                Main.decider.levelChange(l);
                Main.soundSystem.playSFX("levelUp.wav");
            }else Main.soundSystem.playSFX("Death.wav");
        }else if(ob instanceof Hero){
            System.out.println("You died on level " + hero.level + "!\nE57: " + E57 + "\nE65:" + E65 + "\nE71: " + E71 + "\nE79: " + E79 + "\nE88: " + E88);
            System.exit(0);
        }
    }
    
    public synchronized void render(Graphics g, long frameNum){
        try{
            objects.stream().forEach((o) -> {
                o.render(g, frameNum);
            });
        }catch(NullPointerException e){System.err.println("Error 00111001");E57++;}
    }
    
    public synchronized void tick(){
        try{
            objects.stream().forEach(o -> {
                o.tick(this);
            });
        }catch(NullPointerException e){System.err.println("Error 01000001");E65++;}
        try{
            objects.addAll(newObjects);
            objects.removeAll(oldObjects);
            newObjects.clear();
            oldObjects.clear();
        }catch(ArrayIndexOutOfBoundsException e){System.err.println("Error 01000111");E71++;}
    }
    
    private synchronized void collisionDetection(GameObject ob){
        try{
            objects.stream().filter(o -> ob.isColliding(o)).forEach(o -> {
                ob.collision(o);
            });
        }catch(NullPointerException e){System.err.println("Error 01001111");E79++;}
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        try{
            objects.stream().forEach(o -> {
                collisionDetection(o);
            });
        }catch(ConcurrentModificationException e){System.err.println("Error 01011000");E88++;}
    }
    
    private int E57=0, E65=0, E71=0, E79=0, E88=0;
    
}
