
package yoisupiru;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private boolean running = false;
    public Window window;
    
    private static long frameNumber = 10000;
    private static long frameDivisor = 10000;
    public static final int WIDTH = 860, HEIGHT = WIDTH / 12 * 9;
    public static final SoundHandler soundSystem = new SoundHandler();
    
    public Main(){
        //soundSystem.playAbruptLoop("backtrack.wav");
    }

    @Override
    public synchronized void run(){
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            for(double d = delta; d >= 1; d--){
                handler.tick();
            }
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
    
    public synchronized void render(int frameInSec){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(4);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if(frameInSec%16==0){
            frameNumber = (frameNumber+1) % frameDivisor;
        }
        handler.render(g, frameNumber);
        g.dispose();
        bs.show();
    }

    public synchronized void start(){
        thread = new Thread(this);
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

}
