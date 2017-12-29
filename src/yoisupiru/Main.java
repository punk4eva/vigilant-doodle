
package yoisupiru;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import logic.SoundHandler;

/**
 *
 * @author Adam Whittaker
 */
public class Main extends Canvas implements Runnable{
    
    public Handler handler;
    public static Decider decider;
    private Thread thread;
    private volatile boolean running = false, paused = false;
    public Window window;
    static double frameNumber = 10000;
    private static long frameDivisor = 10000;
    public static final int WIDTH, HEIGHT;
    public static final SoundHandler soundSystem = new SoundHandler();
    private Graphics g;
    private BufferStrategy bs;
    static{
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = (int)screen.getWidth();
        HEIGHT = (int)screen.getHeight();
    }   
    
    public Main(){
        soundSystem.playAbruptLoop("backtrack.wav");
    }

    @Override
    public void run(){
        this.requestFocus();
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            long now = System.nanoTime();
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
        handler.render(g, (long)frameNumber);
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

    String getDifficulty(){
        return "Normal";
    }

}
