
package entities.bosses;

import entities.GameObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import yoisupiru.Decider;

/**
 *
 * @author Adam Whittaker
 */
public class TheEviscerator extends Boss{
    
    public TheEviscerator(GameObject targ){
        super("The Eviscerator", 800, 30, "The_Eviscerator.wav", 
                new Phase1(targ, 26, 6));
    }
    
    private static class Phase1 extends BossPhase{
        
        final GameObject target;
        boolean lunging = false;
        long clock = 0;
        long waitPeriod = (long)(Decider.r.nextDouble()*850);
        
        public Phase1(GameObject targ, double dam, double sp){
            super(-1, dam, 160, 160, sp);
            target = targ;
        }

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D)ig;
            
            g.setColor(new Color(33, 229, 22));
            g.fillOval(x, y, width, height);
            g.setColor(Color.black);
            g.fillOval(x+8, y+8, 144, 144);
            
            AffineTransform at1, at2, a45 = AffineTransform.getRotateInstance(Math.PI/4.0, x+width/2, y+height/2);
            double r1 = -((double)frameNum/2)%(2*Math.PI), r2 = ((double)frameNum/2)%(2*Math.PI);
            
            g.setColor(new Color(32, (int)(frameNum%233), 50));
            Rectangle rect = new Rectangle(x+width/2-16, y-16, 32, 32);
            at1 = AffineTransform.getRotateInstance(r1, rect.x+16, rect.y+16);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(new Color(32, (int)(frameNum%233), 50));
            rect = new Rectangle(x+width/2-16, y+height-16, 32, 32);
            at1 = AffineTransform.getRotateInstance(r1, rect.x+16, rect.y+16);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(new Color(32, (int)(frameNum%233), 50));
            rect = new Rectangle(x-16, y+height/2-16, 32, 32);
            at2 = AffineTransform.getRotateInstance(r2, rect.x+16, rect.y+16);
            g.fill(at2.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(at2.createTransformedShape(rect));
            
            g.setColor(new Color(32, (int)(frameNum%233), 50));
            rect = new Rectangle(x+width-16, y+height/2-16, 32, 32);
            at2 = AffineTransform.getRotateInstance(r2, rect.x+16, rect.y+16);
            g.fill(at2.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(at2.createTransformedShape(rect));
            
            g.setColor(new Color((int)frameNum%100, 75+target.y%50, (int)(100*hp/maxhp)+40));
            rect = new Rectangle(x+width/2-40, y+height/2-40, 80, 80);
            g.fill(a45.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(x+width/2-30, y+height/2-30, 60, 60);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(new Color(32, 183, 89));
            g.fillOval(x+width/2-15, y+height/2-15, 30, 30);
            g.setColor(Color.BLACK);
            g.fillOval(x+width/2-6, y+height/2-6, 12, 12);
        }
        
        void courseCorrection(){
            if(target.y<y&&vely>-speed){
                if(vely-0.1<-speed){
                    vely = -speed;
                }else vely -= 0.1;
            }else if(target.y>y&&vely<speed){
                if(vely+0.1>speed){
                    vely = speed;
                }else vely += 0.1;
            }
            if(target.x<x&&velx>-speed){
                if(velx-0.1<-speed){
                    velx = -speed;
                }else velx -= 0.1;
            }else if(target.x>x&&velx<speed){
                if(velx+0.1>speed){
                    velx = speed;
                }else velx += 0.1;
            }
        }
        
        void lunge(){
            int cx = x+width/2, cy = y+height/2;
            double gradient = Math.abs(((double)target.y-cy)/(target.x-cx));
            if(target.x>cx){
                if(target.y<cy){ //1st Quartile
                    if(gradient<1.0){
                        velx = speed*1.5;
                        vely = velx*-gradient;
                    }else{
                        vely = -speed*1.5;
                        velx = vely/-gradient;
                    }
                }else{ //2nd Quartile
                    if(gradient<1.0){
                        velx = speed*1.5;
                        vely = velx*gradient;
                    }else{
                        vely = speed*1.5;
                        velx = vely/gradient;
                    }
                }
            }else{
                if(target.y>cy){ //3rd Quartile
                    if(gradient<1.0){
                        velx = -speed*1.5;
                        vely = velx*-gradient;
                    }else{
                        vely = speed*1.5;
                        velx = vely/-gradient;
                    }
                }else{ //4th Quartile
                    if(gradient<1.0){
                        velx = -speed*1.5;
                        vely = velx*gradient;
                    }else{
                        vely = -speed*1.5;
                        velx = vely/gradient;
                    }
                }
            }
        }
        
        @Override
        public synchronized void actionPerformed(ActionEvent ae){
            super.actionPerformed(ae);
            if(lunging){
                if(clock==0) lunge();
                clock++;
                if(clock>=25) lunging = false;
            }else{
                courseCorrection();
                clock++;
                if(clock>=waitPeriod){
                    waitPeriod = (long)(Decider.r.nextDouble()*850);
                    lunging = true;
                    clock = 0;
                }
            }
        }
    
    }
    
}
