
package logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import static yoisupiru.Decider.r;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public abstract class BackgroundEffect{
    
    public Color col;
    public int x, y = 0, size;
    private double dx, dy;
    public double depth;
    
    public BackgroundEffect(int s){
        x = r.nextInt(Main.WIDTH);
        depth = 0.4+r.nextDouble();
        int b = r.nextInt(30) + 40;
        col = new Color(b,b,b);
        size = s;
    }
    
    public abstract void paint(Graphics2D g);
    
    public void tick(double velx, double vely){
        double xC = velx*depth, yC = vely*depth;
        dx += xC%1.0;
        dy += yC%1.0;
        if(dx>=1){
            x += xC + 1;
            dx--;
        }else x += xC;
        if(dy>=1){
            y += yC + 1;
            dy--;
        }else y += yC;
    }
    
    public boolean outOfBounds(){
        return x+size<=0||x>Main.WIDTH||y+size<=0||y>Main.HEIGHT;
    }
    
    
    public static class Star extends BackgroundEffect{
        
        public Star(){
            super(r.nextInt(7) + 4);
        }

        @Override
        public void paint(Graphics2D ig){
            ig.setColor(col);
            ig.fillRect(x, y, size, size);
        }
    
    }
    
    public static class BlackHole extends BackgroundEffect{
        
        public BlackHole(){
            super(38);
        }

        @Override
        public void paint(Graphics2D g){
            g.setColor(col);
            g.fillOval(x, y, 38, 38);
            g.fillPolygon(new int[]{x+14,x,x}, new int[]{y,y+14,y}, 3);
            g.fillPolygon(new int[]{x+38,x+24,x+38}, new int[]{y+24,y+38,y+38}, 3);
            g.setColor(Color.BLACK);
            g.fillOval(x+3, y+3, 32, 32);
        }
        
    }
    
    public static class Galaxy extends BackgroundEffect{
        
        public Galaxy(){
            super(64);
        }
        
        @Override
        public void paint(Graphics2D g){
            g.setColor(col);
            g.fillOval(x, y, 64, 64);
            g.setColor(Color.BLACK);
            g.fillOval(x+6, y+6, 52, 52);
            g.setColor(col);
            g.fill(AffineTransform.getRotateInstance(Math.PI/4d, x+32, y+32).createTransformedShape(new Rectangle(x+27, y-6, 12, 74)));
        }
    
    }
    
    public static class EffectManager{
        
        public EffectManager(int c, double i){
            cap = c;
            intensity = i;
        }
        
        public static List<BackgroundEffect> effects = new ArrayList<>();
        public int cap;
        public double intensity, velx = 0, vely = 0.75;
        
        public void setVelocity(double vx, double vy){
            velx = vx;
            vely = vy;
        }
        
        public synchronized void spawn(){
            double d = r.nextDouble();
            if(d<0.95) effects.add(new Star());
            else if(d<0.985) effects.add(new Galaxy());
            else effects.add(new BlackHole());
        }
        
        public synchronized void paintAndTick(Graphics ig){
            Graphics2D g = (Graphics2D) ig;
            /*if(effects.size()<cap) */if(r.nextDouble()<intensity) spawn();
            effects.stream().forEach(e -> {
                e.paint(g);
                e.tick(velx, vely);
            });
            effects.removeIf(e -> e.outOfBounds());
        }
        
        public synchronized void paint(Graphics ig){
            Graphics2D g = (Graphics2D) ig;
            effects.stream().forEach(e -> {
                e.paint(g);
            });
        }
        
    }
    
}
