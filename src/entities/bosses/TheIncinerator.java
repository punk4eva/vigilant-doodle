
package entities.bosses;

import entities.Enemy;
import entities.Fire;
import entities.GameObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import logic.Collision;
import yoisupiru.Decider;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class TheIncinerator extends Boss{
    
    private static FuelTank fuelTank;
    private final Handler handler;
    private final GameObject target;
    private static Fire fire;
    
    private static Color getColor(long frameNum, double num){
        return new Color((int)(152.5+102.5*Math.sin((frameNum+50d*num)/100d)), 0, 0);
    }
    
    public TheIncinerator(Handler h, GameObject targ, int hp1, int hp2){
        super("The Incinerator", hp1+hp2, 40, "The_Incinerator.wav",
                new Phase1(hp1, Integer.MAX_VALUE, h, targ));
        fire = new Fire(7.0, 1, width/2, 194, 0, 6.0, Math.PI/10d, 150, 5000, 3);
        fuelTank = new FuelTank((int)(11d*width/12d), 16);
        handler = h;
        target = targ;
        ((Phase1)phases[0]).setClock();
    }
    
    @Override
    protected void die(Handler handler){
        alive = false;
        handler.removeObject(this);
        handler.removeObject(fuelTank);
    }
    
    public void spawnFuelTank(){
        handler.addObject(fuelTank);
    }
    
    private static class Phase1 extends BossPhase{

        private final Handler handler;
        private int clock;
        private String fireMode = "SetSpawn";
        private long spawnTime;
        private final GameObject target;
        
        public Phase1(double th, double dam, Handler h, GameObject targ){
            super(th, dam, Main.WIDTH, 50, -1);
            handler = h;
            target = targ;
            velx = 0;
            vely = 6.0;
        }

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D ) ig;
            AffineTransform at = AffineTransform.getRotateInstance(-19d*Math.PI/20d, width/3, y+100);
            
            Rectangle rect = new Rectangle(width/3, y+100, width/3, 100);
            g.setColor(getColor(frameNum, 0));
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(width/3+10, y+110, width/3-20, 80);
            g.fill(at.createTransformedShape(rect));
            
            at = AffineTransform.getRotateInstance(9d*Math.PI/10d, 2*width/3, y+100);
            rect = new Rectangle(width/3, y+100, width/3, 100);
            g.setColor(getColor(frameNum, 1));
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(width/3+10, y+110, width/3-20, 80);
            g.fill(at.createTransformedShape(rect));
            
            g.setColor(getColor(frameNum, 2));
            at = AffineTransform.getRotateInstance(Math.PI/4d, width/2, y+100);
            rect = new Rectangle(width/2-50, y+50, 100, 100);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(width/2-40, y+60, 80, 80);
            g.fill(at.createTransformedShape(rect));
            g.setColor(getColor(frameNum, 3));
            g.fillRect(width/3, y, width/3, 100);
            g.setColor(Color.BLACK);
            g.fillRect(width/3+10, y+10, width/3-20, 80);
            g.setColor(getColor(frameNum, 4));
            g.fillRect(x, y, width, 16);
            
            at = AffineTransform.getRotateInstance(Math.PI/4d, 5d*width/12d, y+50);
            rect = new Rectangle(5*width/12-16, y+34, 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(5*width/12-11, y+39, 22, 22);
            g.fill(at.createTransformedShape(rect));
            g.setColor(getColor(frameNum, 5));
            at = AffineTransform.getRotateInstance(Math.PI/4d, width/2, y+50);
            rect = new Rectangle(width/2-16, y+34, 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(width/2-11, y+39, 22, 22);
            g.fill(at.createTransformedShape(rect));
            g.setColor(getColor(frameNum, 6));
            at = AffineTransform.getRotateInstance(Math.PI/4d, 7d*width/12d, y+50);
            rect = new Rectangle(7*width/12-16, y+34, 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(7*width/12-11, y+39, 22, 22);
            g.fill(at.createTransformedShape(rect));
            
            g.setColor(new Color(84, 84, 84));
            g.fill3DRect(x+width/2-4, y+165, 8, 24, true);
            g.fill3DRect(x+width/2-6, y+189, 12, 6, true);
            
        }
        
        @Override
        public boolean isColliding(Collision c){
            return super.isColliding(c)||(c.x+c.width>width/2-20&&c.x<width/2+20&&c.y<y+165);
        }
        
        void fire(String fMode){
            fire.setDamage(9.0 + 7.0*madnessCoefficient());
            fire.setLevel((int)(madnessCoefficient()*3.0));
            fireMode = fMode;
            System.err.println(fMode);
            fire.velChange(velx, vely);
            handler.addObject(fire);
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            switch(fireMode){
                case "Rotation": fire.velAngleChange(getRotationAngle()); break;
                case "Target": fire.velAngleChange(getAngleForCoords(target.x, target.y)); break;
                case "Bullet": fire.velAngleChange(getBulletAngle()); break;
                case "Converge": fire.setTorque(clock/400d); break;
                case "SetSpawn": spawnTime = System.currentTimeMillis(); break;
            }
            clock--;
            if(clock<=0){
                setClock();
                if(fireMode.isEmpty()){
                    switch(Decider.r.nextInt(4)){
                        case 0: fire("Rotation"); break;
                        case 1: fire("Target"); break;
                        case 2: fire("Bullet"); break;
                        case 3: fire("Converge"); break;
                    }
                }else{
                    handler.removeObject(fire);
                    fireMode = "";
                }
            }
        }
        
        private void setClock(){
            if(fireMode.isEmpty()) clock = 400 + Decider.r.nextInt(401) + (int)(System.currentTimeMillis()-spawnTime)/30;
            else clock = 800 - (int)(600d*madnessCoefficient())+ Decider.r.nextInt(201);
        }
        
        private double madnessCoefficient(){
            return fuelTank.hp/fuelTank.maxhp;
        }
        
        private double getRotationAngle(){
            return Math.PI/(840d/(madnessCoefficient()+1.0)-240d);
        }
        
        private double getAngleForCoords(double x, double y){
            double t = obtuseAtan((x-(double)fire.x),(y-(double)fire.y));
            double f = obtuseAtan(fire.velx, fire.vely);
            System.out.println("T: " + t + "   F: " + f);
            if(f-t>t+2d*Math.PI-f) return -getRotationAngle();
            else return getRotationAngle();
        }
        
        private double getBulletAngle(){
            GameObject ob = handler.getBullet();
            if(ob==null) return 0;
            return 7d*getAngleForCoords(ob.x, ob.y);
        }
        
        private double obtuseAtan(double x, double y){
            if(x>0&&y>=0) return Math.atan(y/x);
            if(x<=0&&y>0) return Math.atan(-x/y)+Math.PI/2d;
            if(x<0&&y<=0) return Math.atan(y/x)+Math.PI/2d;
            if(x>=0&&y<0) return Math.atan(-x/y)+3d*Math.PI/2d;
            return Integer.MIN_VALUE;
        }
        
    }
    
    private static class Phase2 extends BossPhase{

        public Phase2(double th, double dam, int w, int h, double sp){
            super(th, dam, w, h, sp);
        }

        @Override
        public void render(Graphics g, long frameNum){
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    private static class FuelTank extends Enemy{

        public FuelTank(int ex, int ey){
            super("Fuel Tank", 400, 0, 40, 60, 0, 0, 0, 0);
            x = ex;
            y = ey;
        }
        
        @Override
        public synchronized void tick(Handler handler){
            if(hp<maxhp) hp+=0.05;
        }
        
        @Override
        public void hurt(double damage){
            hp -= damage;
            if(hp<0) hp = 0;
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color(244, 244, 171));
            g.fillRect(x, y, width, height);
            g.setColor(Color.DARK_GRAY);
            g.fill3DRect(x-2, y+30, width+4, 12, false);
            g.setColor(Color.RED);
            g.fill3DRect(x-1, y+31, (int)(hp/maxhp*((double)width+2D)), 10, true);
        }
    
    }
    
}
