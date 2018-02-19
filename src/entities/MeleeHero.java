
package entities;

import static entities.bosses.TheIncinerator.obtuseAtan;
import java.awt.Color;
import java.awt.Graphics;
import logic.Collision;
import static logic.ConstantFields.courseCorrectionFactor;
import static logic.ConstantFields.meleeAccFactor;
import static logic.ConstantFields.meleeRotationFactor;
import static yoisupiru.Decider.r;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class MeleeHero extends GameObject{
    
    final double duration, maxpush = 100, maxSpeed, length;
    double push = 0, lives = 3, pushfactor;
    long pushtime = 0;
    
    public MeleeHero(int dur, double ms){
        super("MeleeHero", 3, 72, 72);
        duration = dur>1000000?Integer.MAX_VALUE:dur+System.currentTimeMillis();
        length = dur;
        maxSpeed = ms;
    }

    public void render(Graphics g, long frameNum, int x, int y){
        g.setColor(Color.RED);
        g.fillArc(x, y, width, height, 90, getAngle());
        g.setColor(Color.BLACK);
        g.fillOval(x+8, y+8, width-16, height-16);
        
        g.setColor(Color.CYAN);
        g.fillArc(x+16, y+16, width-32, height-32, -90, -(int)(360d*push/maxpush));
        g.setColor(Color.BLACK);
        g.fillOval(x+24, y+24, width-48, height-48);
        
        if(lives>3||lives<0) System.err.println("HP: "+lives);
        g.setColor(new Color((int)(lives*84), 0, 0));
        g.fillOval(x+32, y+32, width-64, height-64);
    }

    @Override
    public void collision(GameObject ob){
        if(ob instanceof Bullet){
            ob.hp = 5;
            updateOtherVelocity(ob);
            hp += ((Bullet) ob).damage;
            System.out.println("Collide with bullet");
        }else if(ob instanceof Enemy){
            if(Math.abs(velx)+Math.abs(vely)>Math.abs(ob.velx)+Math.abs(ob.vely)||push==0){
                updateOtherVelocity(ob);
            }else if(pushtime+750>System.currentTimeMillis()) ob.updateOtherVelocity(this);
            System.out.println("Collide");
        }
    }
    
    public void track(double dx, double dy, int x, int y){
        if(push<maxpush) push += 0.1;
        if(dy<y&&vely>-maxSpeed){
            if(vely-courseCorrectionFactor<-maxSpeed){
                vely = -maxSpeed;
            }else vely -= courseCorrectionFactor;
        }else if(dy>y&&vely<maxSpeed){
            if(vely+courseCorrectionFactor>maxSpeed){
                vely = maxSpeed;
            }else vely += courseCorrectionFactor;
        }
        if(dx<x&&velx>-maxSpeed){
            if(velx-courseCorrectionFactor<-maxSpeed){
                velx = -maxSpeed;
            }else velx -= courseCorrectionFactor;
        }else if(dx>x&&velx<maxSpeed){
            if(velx+courseCorrectionFactor>maxSpeed){
                velx = maxSpeed;
            }else velx += courseCorrectionFactor;
        }
    }
    
    protected boolean boundsCheck(int x, int y, Hero h){
        if(x+width<=0||x>Main.WIDTH||y+height<=0||y>Main.HEIGHT){
            h.x = Main.WIDTH-x<-width/2?-width/2:Main.WIDTH-x>Main.WIDTH+width/2?Main.WIDTH+width/2:Main.WIDTH-x;
            h.y = Main.HEIGHT-y<-height/2?-height/2:Main.HEIGHT-y>Main.HEIGHT+height/2?Main.HEIGHT+height/2:Main.HEIGHT-y;
            lives--;
            if(lives<=0) return true;
        }
        return System.currentTimeMillis()>duration&&duration!=Integer.MAX_VALUE;
    }
    
    protected void push(Handler h){
        if(push>=maxpush*0.6d){
            double p = 1d + 2.5d*(push-0.6d*maxpush)/(0.4d*maxpush);
            push = 0;
            pushtime = System.currentTimeMillis();
            pushfactor = p;
            h.collideEverything(this);
            pushfactor = 1.4;
        }
    }
    
    private int getAngle(){
        if(duration==Integer.MAX_VALUE) return 360;
        return (int)(360d*(duration-System.currentTimeMillis())/length);
    }

    @Override
    public void render(Graphics g, long frameNum){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void updateBothVelocities(Collision c){
//        System.out.println("P: " + (System.currentTimeMillis()-pushtime));
//        if(pushtime+750>System.currentTimeMillis()&&push!=0) return; 
//        System.err.println("cvx1: " + c.velx + " cvy1: " + c.vely);
//        double nvx = velx, nvy = vely;
//        if(c.velx*velx>0) velx = r.nextDouble()*c.velx;
//        else velx += r.nextDouble()*c.velx;
//        if(c.vely*vely>0) vely = r.nextDouble()*c.vely;
//        else vely += r.nextDouble()*c.vely;
//        c.velChange((c.velx+nvx) - velx, (c.vely+nvy) - vely);
//        pushtime = System.currentTimeMillis();
//        System.err.println("cvx2: " + c.velx + " cvy2: " + c.vely);
        c.velChange(pushfactor*velx, pushfactor*vely);
        c.velAngleChange(r.nextDouble()*2d*Math.PI);
    }
    
}
