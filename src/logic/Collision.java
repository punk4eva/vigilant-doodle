
package logic;

import entities.GameObject;
import static yoisupiru.Decider.r;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Collision{
    
    public final int ID;
    public int IDOfRitchocket = -1;
    public int x, y;
    public double xChange, yChange;
    public double velx, vely;
    public int width, height;
    
    public Collision(){
        ID = IDBuilder.genID();
    }
    

    public abstract void collision(GameObject ob);
    
    public boolean isColliding(Collision c){
        try{
            return c.ID!=ID&&
                x+width>=c.x&&
                c.width+c.x>=x&&
                y+height>=c.y&&
                c.height+c.y>=y;
        }catch(NullPointerException e){return false;}
    }
    
    public void updateBothVelocities(Collision c){
        if(c.IDOfRitchocket == ID) return;
        double nvx = velx, nvy = vely;
        if(c.velx*velx>0) velx = r.nextDouble()*c.velx;
        else velx += r.nextDouble()*c.velx;
        if(c.vely*vely>0) vely = r.nextDouble()*c.vely;
        else vely += r.nextDouble()*c.vely;
        c.velChange((c.velx+nvx) - velx, (c.vely+nvy) - vely);
        c.IDOfRitchocket = ID;
        IDOfRitchocket = c.ID;
    }
    
    public void updateOtherVelocity(Collision c){
        double vx = velx, vy = vely;
        updateBothVelocities(c);
        velx = vx;
        vely = vy;
    }
    
    public void velChange(double vx, double vy){
        velx = vx;
        vely = vy;
    }
    
    public void velAngleChange(double r){
        double vx = velx;
        velx = Math.cos(r)*velx-Math.sin(r)*vely;
        vely = Math.sin(r)*vx+Math.cos(r)*vely;
    }
    
    public void velPolarChange(double r, double theta){
        velx = r*Math.cos(theta);
        vely = r*Math.sin(theta);
    }
    
    public static double obtuseAtan(double x, double y){
        if(x>0&&y>=0) return Math.atan(y/x);
        if(x<=0&&y>0) return Math.atan(-x/y)+Math.PI/2d;
        if(x<0&&y<=0) return Math.atan(y/x)+Math.PI/2d;
        if(x>=0&&y<0) return Math.atan(-x/y)+3d*Math.PI/2d;
        return Integer.MIN_VALUE;
    }
    
    public static double sqrt(double x) {
        double xhalf = 0.5d * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5d - xhalf * x * x);
        return 1d/x;
    }
    
    public void setVelAngle(double theta){
        double r = sqrt(velx*velx + vely*vely);
        velx = r*Math.cos(theta);
        vely = r*Math.sin(theta);
    }
    
}
