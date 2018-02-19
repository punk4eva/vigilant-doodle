
package yoisupiru;

import entities.Bullet;
import entities.Consumable;
import entities.Enemy;
import entities.GameObject;
import entities.Hero;
import entities.bosses.Boss;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.Timer;
import static logic.ConstantFields.mobCap;
import static logic.ConstantFields.consCap;
import logic.Twin;

/**
 *
 * @author Adam Whittaker
 */
public class Handler implements ActionListener{
    
    protected volatile int twinsPresent = 0;
    private final List<GameObject> objects = new LinkedList<>();
    private final Timer timer;
    public final Hero hero;
    private volatile LinkedBlockingQueue<Runnable> queuedEvents = new LinkedBlockingQueue<>();
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
                if(mobNum<mobCap||((Enemy)ob).forced) synchronized(objects){
                    if(ob instanceof Twin) twinsPresent++;
                    if(!((Enemy)ob).forced) mobNum++;
                    timer.addActionListener(ob);
                    objects.add(ob);
                }
            }else if(ob instanceof Consumable){
                if(consNum<consCap||((Consumable)ob).forced) synchronized(objects){
                    if(!((Consumable)ob).forced) consNum++;
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
            if(ob instanceof Twin) twinsPresent--;
            mobNum--;
            int l = hero.level;
            hero.tryLevelUp(((Enemy) ob).xp);
            System.err.println("Level: " + hero.level + "\nXP: " + hero.xp + " / " + hero.maxxp);
            if(hero.level!=l){
                Main.decider.levelChange(hero.level);
                Main.soundSystem.playSFX("levelUp.wav");
            }else if(((Enemy) ob).xp!=0) Main.soundSystem.playSFX("Death.wav");
            if(ob instanceof Boss) Main.decider.bossSlain();
        }else if(ob instanceof Hero){
            System.out.println("You died on level " + hero.level + "!");
            try{
                Thread.sleep(1400);
            }catch(InterruptedException e){}
            System.exit(0);
        }else if(ob instanceof Consumable && !((Consumable)ob).forced) consNum--;
    }
    
    public void render(Graphics g, long frameNum){
        synchronized(objects){
            objects.stream().filter(o -> o.alive).forEach((o) -> {
                o.render(g, frameNum);
            });
        }
        hero.drawMessages(g);
    }
    
    private void collisionDetection(GameObject ob){
        objects.stream().filter(o -> ob.isColliding(o)).forEach(o -> {
            ob.collision(o);
        });
    }
    
    public Bullet getBullet(){
        synchronized(objects){
            List<Bullet> list = (List<Bullet>)(Object)objects.stream().filter(ob -> ob instanceof Bullet).collect(Collectors.toList());
            return list.isEmpty()?null:list.get(0);
        }
    }
    
    public boolean checkIfExists(Predicate<GameObject> pred){
        synchronized(objects){
            return getStream().anyMatch(pred);
        }
    }
    
    public void collideEverything(GameObject ob){
        queuedEvents.add(() -> {
            synchronized(objects){
                getStream().forEach(o -> {
                    ob.collision(o);
                });
            }
        });
    }
    
    public Stream<GameObject> getStream(){
        synchronized(objects){
            if(twinsPresent==0) return objects.stream();
            return objects.stream().collect(TwinCollector::new, TwinCollector::accept, TwinCollector::combine).stream();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        synchronized(objects){
            objects.stream().filter(o -> o.alive).forEach(o -> {
                o.tick(this);
            });
            objects.stream().forEach(o -> {
                collisionDetection(o);
            });
        }
        Main.frameNumber+=0.3;
    }
    
    public void pause(){
        if(timer.isRunning()) timer.stop();
        else timer.start();
    }
    
    protected void clearEnemies(){
        queuedEvents.add(() -> {
            synchronized(objects){
                for(Iterator<GameObject> iter=objects.iterator();iter.hasNext();){
                    GameObject ob = iter.next();
                    if(ob instanceof Enemy){
                        timer.removeActionListener(ob);
                        iter.remove();
                        mobNum--;
                    }
                }
            }
        });
    }

    public void purge(int y){
        queuedEvents.add(() -> {
            synchronized(objects){
                for(Iterator<GameObject> iter=objects.iterator();iter.hasNext();){
                    GameObject ob = iter.next();
                    if(ob.y<y && ob instanceof Hero || ob instanceof Bullet){
                        timer.removeActionListener(ob);
                        iter.remove();
                    }
                }
            }
        });
    }
    
    private static class TwinCollector{
        
        private List<GameObject> list = new LinkedList<>();
        
        public void accept(Object ob){
            if(ob instanceof Twin) list.addAll(((Twin) ob).getTwinObjects());
            else list.add((GameObject) ob);
        }
        
        public void combine(TwinCollector tc){
            list.addAll(tc.list);
        }
        
        public Stream<GameObject> stream(){
            return list.stream();
        }
        
    }
    
}
