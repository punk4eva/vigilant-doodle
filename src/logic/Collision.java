
package logic;

import entities.GameObject;
import static yoisupiru.Decider.r;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Collision{
    
    public final int ID;
    private int IDOfRitchocket = -1;
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
    
}
