
package yoisupiru;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.concurrent.Semaphore;
import logic.SoundHandler;

/**
 *
 * @author Adam Whittaker
 */
public class Main extends Canvas implements Runnable, MouseListener{
    
    public Handler handler;
    public static Decider decider;
    private Thread thread;
    private volatile boolean running = false, paused = false;
    private String difficulty = "";
    public Window window;
    static double frameNumber = 10000;
    private static long frameDivisor = 10000;
    public static final int WIDTH, HEIGHT;
    public static final SoundHandler soundSystem = new SoundHandler();
    private Graphics g;
    private BufferStrategy bs;
    private final Semaphore semaphore = new Semaphore(0);
    static{
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int)screen.getWidth();
        HEIGHT = (int)screen.getHeight();
    }   
    
    public Main(){
        soundSystem.playAbruptLoop("backtrack.wav");
        addMouseListener(this);
    }

    @Override
    public void run(){
        requestFocus();
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            synchronized(soundSystem){ while(paused) try{
                soundSystem.wait();
            }catch(InterruptedException e){}}
            render(frames);
            frames++;
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }
    
    public void render(int frameInSec){
        bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(4);
            return;
        }
        g = bs.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        frameNumber %= frameDivisor;
        if(difficulty.equals("Selecting")) paintDifficultyMenu(g);
        else if(!handler.hero.alive) paintDeathMessage(g);
        else handler.render(g, (long)frameNumber);
        g.dispose();
        bs.show();
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.setName("Run Thread");
        thread.start();
        running = true;
    }

    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void pause(){
        paused = !paused;
        handler.pause();
        decider.pause();
        if(!paused) synchronized(soundSystem){
            soundSystem.notify();
        }
    }

    public String getDifficulty(){
        difficulty = "Selecting";
        try{
            semaphore.acquire();
        }catch(InterruptedException e){}
        return difficulty;
    }
    
    private void paintDifficultyMenu(Graphics g){
        g.setColor(Color.WHITE);
        g.drawRect(WIDTH/3, 50, WIDTH/3, 100);
        g.drawRect(WIDTH/3, 170, WIDTH/3, 100);
        g.drawRect(WIDTH/3, 290, WIDTH/3, 100);
        g.drawRect(WIDTH/3, 410, WIDTH/3, 100);
        g.drawString("EASY", WIDTH/3+32, 70);
        g.drawString(" * 250% regeneration speed", WIDTH/3+32, 90);
        g.drawString(" * Deal 10% more damage", WIDTH/3+32, 110);
        g.drawString(" * Enemies deal 70% damage", WIDTH/3+32, 130);
        g.drawString(" * Much longer immunity length", WIDTH/3+232, 90);
        g.drawString(" * 15% standing defense", WIDTH/3+232, 110);
        g.drawString("NORMAL", WIDTH/3+32, 190);
        g.drawString(" * 150% regeneration speed", WIDTH/3+32, 210);
        g.drawString(" * Longer immunity length", WIDTH/3+32, 230);
        g.drawString(" * Enemies deal 85% damage", WIDTH/3+32, 250);
        g.drawString(" * 12% standing defense", WIDTH/3+232, 210);
        g.drawString("HARD", WIDTH/3+32, 310);
        g.drawString(" * 100% regeneration speed", WIDTH/3+32, 330);
        g.drawString(" * Enemies deal full damage", WIDTH/3+32, 350);
        g.drawString(" * 6% standing defense", WIDTH/3+32, 370);
        g.drawString("BRUTAL", WIDTH/3+32, 430);
        g.drawString(" * 80% regeneration speed", WIDTH/3+32, 450);
        g.drawString(" * Shorter immunity length", WIDTH/3+32, 470);
        g.drawString(" * No standing defense", WIDTH/3+32, 490);
    }
    
    private void paintDeathMessage(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
        g.drawString("YOU DIED!", WIDTH/3, HEIGHT/3);
    }

    @Override
    public void mouseClicked(MouseEvent me){
        if(difficulty.equals("Selecting")){
            int x=me.getX(),y=me.getY();
            if(x<WIDTH/3||x>2*WIDTH/3) return;
            if(y>50&&y<150) difficulty = "Easy";
            if(y>170&&y<270) difficulty = "Normal";
            if(y>290&&y<390) difficulty = "Hard";
            if(y>410&&y<510) difficulty = "Brutal";
            if(!difficulty.equals("Selecting")) semaphore.release();
        }
    }
    @Override
    public void mousePressed(MouseEvent me){}
    @Override
    public void mouseReleased(MouseEvent me){}
    @Override
    public void mouseEntered(MouseEvent me){}
    @Override
    public void mouseExited(MouseEvent me){}

}
