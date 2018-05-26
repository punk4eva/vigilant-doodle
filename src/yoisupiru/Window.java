
package yoisupiru;

import entities.Enemy;
import entities.Hero;
import entities.MeleeHero;
import entities.enemies.Tracker;
import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author Adam Whittaker
 */
public class Window extends Canvas{
    
    public static Main main;
    public final JFrame frame;
    public volatile static float SFXVolume = -100;
    public volatile static float MusicVolume = -100;
    private static float SFXVol, MusicVol;
    private static boolean mute = false;
    
    public Window(int width, int height, String title, Main m){
        frame = new JFrame(title);

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.add(m);
        frame.setVisible(true);
        main = m;
        main.start();
    }
    
    public synchronized static void decreaseVolume(){
        SFXVolume--;
        MusicVolume--;
    }
    
    public synchronized static void increaseVolume(){
        SFXVolume++;
        MusicVolume++;
    }
    
    public synchronized static void mute(){
        System.err.println("mute");
        if(mute){
            SFXVolume = SFXVol;
            MusicVolume = MusicVol;
            Main.soundSystem.resume();
        }else{
            SFXVol = SFXVolume;
            MusicVol = MusicVolume;
            MusicVolume = -100;
            SFXVolume = -100;
            Main.soundSystem.stopBackground();
        }
        mute = !mute;
    }
    
    public static void main(String... args){
        Main m = new Main();
        Hero hero = new Hero(m);
        hero.x = 100;
        hero.y = 100;
        m.handler.addObject(hero);
        //HomingBullet b = new HomingBullet(2, 40, 1, 1, 1, hero);
        //m.handler.addObject(b);
        Enemy en = new Tracker(hero);
        en.x = 500;
        en.y = 500;
        m.handler.addObject(en);
//        try{
//            Thread.sleep(3000);
//            hero.setMeleeMode(new MeleeHero(25000, 5));
//        }catch(InterruptedException e){}
    }
    
}
