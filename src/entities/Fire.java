
package entities;

import entities.consumables.Buff;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import logic.Collision;
import static logic.ConstantFields.graphicsQuality;
import logic.TrailGenerator;
import yoisupiru.Decider;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class Fire extends Enemy{

    protected final LinkedList<Particle> particles = new LinkedList<>();
    protected double torque;
    protected final int duration, intensity;
    protected int clock = 0, fireLevel, length;


    public Fire(double dam, int lvl, int _x, int _y, double vx, double vy, double to, int len, int dur, int in){
        super("Fire", 1, dam, -1, -1, 0, 7.0, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
        velx = vx;
        vely = vy;
        forced = true;
        fireLevel = lvl;
        torque = to;
        length = len;
        intensity = in<4-graphicsQuality?4-(int)graphicsQuality:in;
        duration = dur;
        x = _x;
        y = _y;
    }

    @Override
    public void render(Graphics g, long frameNum){
        synchronized(particles){ 
            particles.stream().forEach(p -> {
                p.render(g, frameNum);
            });
            particles.removeIf(p -> !p.alive);
        }
    }

    @Override
    public void hurt(double damage){}

    @Override
    public void boundsCheck(Handler h){}
    
    @Override
    public boolean isColliding(Collision c){
        synchronized(particles){
            return particles.stream().anyMatch(p -> p.isColliding(c));
        }
    }
    
    public void setTorque(double t){
        torque = t;
    }
    
    public void setLevel(int lvl){
        fireLevel = lvl;
    }
    
    public void setDamage(double dam){
        damage = dam;
    }

    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ob.hurt(damage);
            ((Hero)ob).addBuff(new Buff.FireDebuff(-1, -1, fireLevel, duration+Decider.r.nextInt(2001)-1000));
        }else if(ob instanceof Bullet){
            ob.hp = -1;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        clock++;
        synchronized(particles){
            particles.stream().forEach(p -> p.actionPerformed(ae));
        }
        if(clock>=intensity){
            clock = 0;
            double[] vector = getRandVelocity();
            synchronized(particles){
                particles.add(new Particle(length, x, y, vector[0], vector[1], vector[2]));
            }
        }
    }

    protected double[] getRandVelocity(){
        double to = (Decider.r.nextDouble()*2*torque)-torque,
            vx = Math.cos(to)*velx-Math.sin(to)*vely,
            vy = Math.sin(to)*velx+Math.cos(to)*vely;
        return new double[]{vx, vy, -to};
    }

    public void setLength(int i){
        length = i;
    }

    protected static class Particle extends GameObject{

        protected double ttl;
        protected final double max, torque, tInc;
        protected TrailGenerator trail;

        public Particle(int t, int _x, int _y, double vx, double vy, double to){
            super("Fire", 1, 8, 8);
            ttl = t;
            torque = to;
            max = t;
            x = _x;
            y = _y;
            velx = vx;
            vely = vy;
            tInc = torque/max;
            trail = new TrailGenerator(4, 2, 12, width, height, -1, -1, -1);
        }

        @Override
        public void render(Graphics g, long frameNum){
            ttl--;
            if(ttl<=0) alive = false;
            else{
                int b = (int)(1000d*ttl/max-754d),
                        gr = (int)(700d*ttl/max-454d);
                Color col = new Color(246, gr<0?0:gr, b<0?0:b);
                g.setColor(col);
                trail.setColor(col);
                g.fillRect(x, y, width, height);
                trail.paint((Graphics2D)g, x, y);
            }
        }

        @Override
        public void collision(GameObject ob){throw new IllegalStateException("Should not be called!");}

        @Override
        public void actionPerformed(ActionEvent ae){
            velAngleChange(tInc);
            super.actionPerformed(ae);
        }

    }
    
    public static class LowLagFire extends Fire{
    
        public LowLagFire(double dam, int lvl, int _x, int _y, double vx, double vy, double to, int len, int dur, int in){
            super(dam, lvl, _x, _y, vx, vy, to, len, dur, in);
        }
    
        @Override
        public void actionPerformed(ActionEvent ae){
            clock++;
            synchronized(particles){
                particles.stream().forEach(p -> p.actionPerformed(ae));
            }
            if(clock>=intensity){
                clock = 0;
                double[] vector = getRandVelocity();
                synchronized(particles){
                    particles.add(new LowLagParticle(length, x, y, vector[0], vector[1], vector[2]));
                }
            }
        }
        
        protected static class LowLagParticle extends Particle{
        
            public LowLagParticle(int t, int _x, int _y, double vx, double vy, double to){
                super(t, _x, _y, vx, vy, to);
                //trail = new TrailGenerator(5, 4, 10, width, height, -1, -1, -1);
            }
            
            @Override
            public void render(Graphics g, long frameNum){
                ttl--;
                if(ttl<=0) alive = false;
                else{
                    int b = (int)(1000d*ttl/max-754d),
                            gr = (int)(700d*ttl/max-454d);
                    Color col = new Color(246, gr<0?0:gr, b<0?0:b);
                    g.setColor(col);
                    g.fillRect(x, y, width, height);
                }
            }
        
        }
        
    }

}
