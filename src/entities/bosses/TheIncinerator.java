
package entities.bosses;

import entities.Enemy;
import entities.Fire;
import entities.Fire.LowLagFire;
import entities.GameObject;
import entities.Hero;
import entities.Hero.ShootingMode;
import entities.consumables.Buff;
import entities.consumables.Usable;
import entities.enemies.Bomb;
import entities.enemies.Grenadier;
import entities.enemies.Tracker;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import logic.Collision;
import logic.ConstantFields;
import logic.Resistance;
import yoisupiru.Decider;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class TheIncinerator extends Boss{
    
    private static FuelTank fuelTank;
    private static Fire fire;
    
    private static Color getColor(long frameNum, double num){
        return new Color((int)(152.5+102.5*Math.sin((frameNum+50d*num)/100d)), 0, 0);
    }
    
    public TheIncinerator(Handler h, GameObject targ){
        super("The Incinerator", ConstantFields.hp21+ConstantFields.hp22, 40, "The_Incinerator.wav",
                new Phase1(h, targ), new Phase2(ConstantFields.hp22, h, targ));
        fire = new Fire(7.0, 1, width/2, 194, 0, 6.0, Math.PI/10d, 150, 5000, 3);
        fuelTank = new FuelTank((int)(11d*width/12d), 16);
        ((Phase1)phases[0]).setClock();
    }
    
    @Override
    protected void die(Handler handler){
        alive = false;
        handler.removeObject(this);
        handler.removeObject(fuelTank);
        ((Phase2)phases[1]).wall.deactivate();
    }
    
    @Override
    public void hurt(double dam){
        hp -= (1.0-madnessCoefficient())*dam;
    }
    
    @Override
    public void drop(Handler hand){
        Usable u;
        switch(Decider.r.nextInt(4)){
            case 0: u = new Usable.HealingPotion(8); break;
            case 1: u = new Usable.Shield(8); break;
            case 2: u = new Usable.Hourglass(8); break;
            default: u = new Usable.DeathMissile(8); break;
        }
        u.x = x+width/2-u.width/2;
        u.y = y+height/2-u.height/2;
        hand.addObject(u);
    }
    
    @Override
    protected void nextPhase(int ph, Handler h){
        if(!((Phase1)phases[0]).fireMode.isEmpty()) h.removeObject(fire);
    }
    
    public void spawnFuelTank(Handler h){
        h.addObject(fuelTank);
    }
    
    private abstract static class SharedState extends BossPhase{
        
        protected final Handler handler;
        protected int clock;
        protected String fireMode = "SetSpawn";
        protected long spawnTime;
        protected final GameObject target;
        
        SharedState(double th, Handler h, GameObject targ){
            super(th, Integer.MAX_VALUE, Main.WIDTH, 50, -1, new Resistance(ShootingMode.BURST, 0.65));
            handler = h;
            target = targ;
        }
        
        @Override
        public boolean isColliding(Collision c){
            return super.isColliding(c)||(c.x+c.width>width/2-20&&c.x<width/2+20&&c.y<y+165);
        }
        
        public long timeSince(){
            return System.currentTimeMillis()-spawnTime;
        }
        
    }
    
    private static class Phase1 extends SharedState{
        
        public Phase1(Handler h, GameObject targ){
            super(-1, h, targ);
            velx = 0;
            vely = 6.0;
        }
        
        protected void setClock(){
            if(fireMode.isEmpty()) clock = 400 + Decider.r.nextInt(401) + (int)timeSince()/30;
            else clock = 800 - (int)(600d*madnessCoefficient())+ Decider.r.nextInt(201);
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
        
        void fire(String fMode){
            fire.setDamage(9.0 + 8.0*madnessCoefficient());
            fire.setLevel((int)(madnessCoefficient()*3.0));
            fireMode = fMode;
            fire.velChange(velx, vely);
            handler.addObject(fire);
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            switch(fireMode){
                case "Rotation": fire.velAngleChange(getRotationAngle()); break;
                case "Target": fire.velAngleChange(getAngleForCoords(target.x, target.y)); break;
                case "Bullet": fire.velAngleChange(getBulletAngle(handler)); break;
                case "Converge": fire.setTorque(clock/400d); break;
                case "SetSpawn": spawnTime = System.currentTimeMillis(); fireMode = "Moving"; y = -170; fuelTank.y = -154; break;
                case "Moving": startMove(); return;
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
        
        private void startMove(){
            yChange += 0.2;
            if(yChange>=1){
                y++;
                yChange %= 1.0;
                fuelTank.y = y+16;
            }
            if(y>=0){
                y = 0;
                yChange = 0;
                fireMode = "";
            }
        }
        
    }
    
    private static class Phase2 extends SharedState{

        private final WallOfFire wall;
        private int wallLength = 250, wallHeight;
        
        public Phase2(double th, Handler h, GameObject targ){
            super(th, h, targ);
            wallHeight = 200;
            wall = new WallOfFire(wallHeight, 5);
        }

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D ) ig;
            AffineTransform at = AffineTransform.getRotateInstance(-19d*Math.PI/20d, width/3, y+100);
            
            Rectangle rect = new Rectangle(width/3, y+100, width/3, 100);
            g.setColor(getColor(2*frameNum, 0));
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(width/3+10, y+110, width/3-20, 80);
            g.fill(at.createTransformedShape(rect));
            
            at = AffineTransform.getRotateInstance(9d*Math.PI/10d, 2*width/3, y+100);
            rect = new Rectangle(width/3, y+100, width/3, 100);
            g.setColor(getColor(2*frameNum, 1));
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(width/3+10, y+110, width/3-20, 80);
            g.fill(at.createTransformedShape(rect));
            
            g.setColor(getColor(2*frameNum, 3));
            g.fillRect(width/3, y, width/3, 100);
            g.setColor(Color.BLACK);
            g.fillRect(width/3+10, y+10, width/3-20, 80);
            g.setColor(getColor(2*frameNum, 4));
            g.fillRect(x, y, width, 16);
            g.setColor(getColor(2*frameNum, 2));
            at = AffineTransform.getRotateInstance(Math.PI/4d, width/2, y+100);
            rect = new Rectangle(width/2-50, y+50, 100, 100);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(width/2-40, y+60, 80, 80);
            g.fill(at.createTransformedShape(rect));
            //Row diamonds
            double rx = 13d*width/24d, ry = y+50;
            g.setColor(getColor(2*frameNum, 7));
            at = AffineTransform.getRotateInstance(8+frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            rx = 11d*width/24d;
            g.setColor(getColor(2*frameNum, 8));
            at = AffineTransform.getRotateInstance(7+frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            //2
            g.setColor(getColor(2*frameNum, 9));
            rx = 13d*width/24d+8; ry = y+62;
            at = AffineTransform.getRotateInstance(6+frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            rx = 11d*width/24d-8;
            g.setColor(getColor(2*frameNum, 10));
            at = AffineTransform.getRotateInstance(5+frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            //3
            rx = 13d*width/24d+16; ry = y+74;
            at = AffineTransform.getRotateInstance(4+frameNum/300d*Math.PI/4d, rx, ry);
            g.setColor(getColor(2*frameNum, 11));
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            rx = 11d*width/24d-16;
            g.setColor(getColor(2*frameNum, 12));
            at = AffineTransform.getRotateInstance(3+frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            //4
            rx = 13d*width/24d+8; ry = y+86;
            at = AffineTransform.getRotateInstance(2+frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.setColor(getColor(2*frameNum, 13));
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            rx = 11d*width/24d-8;
            g.setColor(getColor(2*frameNum, 14));
            at = AffineTransform.getRotateInstance(1+frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            //5
            rx = 13d*width/24d; ry = y+98;
            g.setColor(getColor(2*frameNum, 15));
            at = AffineTransform.getRotateInstance(frameNum/300d*Math.PI/4d, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            rx = 11d*width/24d;
            g.setColor(getColor(2*frameNum, 16));
            at = AffineTransform.getRotateInstance(frameNum/300d*Math.PI/4d-1, rx, ry);
            rect = new Rectangle((int)(rx-16), (int)(ry-16), 32, 32);
            g.fill(at.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle((int)(rx-11), (int)(ry-11), 22, 22);
            g.fill(at.createTransformedShape(rect));
            
            g.setColor(new Color(84, 84, 84));
            g.fill3DRect(x+width/2-4, y+165, 8, 24, true);
            g.fill3DRect(x+width/2-6, y+189, 12, 6, true);
            
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            switch(fireMode){
                case "Retracting": retract(); break;
                case "Extending": extend(); break;
                case "RandomWait": startWait(); break;
                case "WaitingToExtend": clock--; if(clock<=0) fireMode = "Extending"; break;
                case "SetSpawn": spawnTime = System.currentTimeMillis(); wall.activate(); break;
            }
            sear();
            updateWall();
            if(!minionsAlive()) fireMode = "Retracting";
        }
        
        void sear(){
            if(target.y<wallHeight) target.hurt(6d+5d*madnessCoefficient());
        }
        
        void spawn(){
            switch(Decider.r.nextInt(3)){
                case 0: spawnTrackers(); break;
                case 1: spawnGrenadiers(); break;
                case 2: spawnBombs(); break;
            }
            fireMode = "RandomWait";
        }
        
        void startWait(){
            clock = 600 + Decider.r.nextInt(201)-(int)(600d*madnessCoefficient());
            fireMode = "WaitingToExtend";
        }
        
        void updateWall(){
            yChange+=0.02*madnessCoefficient();
            if(yChange>=1.0){
                yChange--;
                wall.setY(wallHeight+=1);
            }
        }
        
        void retract(){
            xChange += 0.05d*madnessCoefficient();
            if(xChange>=1.0){
                wallLength--;
                wall.setLength(wallLength);
                xChange--;
                if(wallLength<=5) spawn();
            }
        }
        
        void extend(){
            xChange += 0.05*madnessCoefficient();
            if(xChange>=1.0){
                wallLength++;
                wall.setLength(wallLength);
                xChange--;
                if(wallLength>=250) fireMode = "Waiting";
            }
        }
        
        private class WallOfFire{
            
            final LowLagFire[] wall;
            final int spikes;
            
            WallOfFire(final int y, int a){
                spikes = a;
                wall = new LowLagFire[a];
                int j = 0;
                handler.purge(y);
                for(int n=y;n<spikes*16+y;n+=16){
                    wall[j] = new LowLagFire(Integer.MAX_VALUE, 4, (j%2.0==0?width-1:1), n, (j%2.0==0?-7.0:7.0), 0,   0.0, 100, 1, 8);
                    j++;
                }
            }
            
            void setY(final int y){
                int j = 0;
                for(int n=y;n<spikes*16+y;n+=16){
                    wall[j].y = n;
                    j++;
                }
            }
            
            void activate(){
                for(Fire f : wall) handler.addObject(f);
            }
            
            void deactivate(){
                for(Fire f : wall) handler.removeObject(f);
            }
            
            void setLength(int length){
                for(Fire f : wall) f.setLength(length);
            }
            
        } 
        
        void spawnTrackers(){
            int amount = 2 + Decider.r.nextInt(3) + (int)(3d*madnessCoefficient());
            for(int n=0;n<amount;n++){
                Tracker t = new Tracker(target, 2+(int)(3d*madnessCoefficient())){
                    {forced=true;xp=0;}
                    @Override
                    public void render(Graphics g, long frameNum){
                        int col = (int)(((double)hp/maxhp)*250d);
                        g.setColor(new Color(col>=0?col:0, (int)(frameNum/2%30), 0));
                        g.fillRect(x, y, width, height);
                        g.setColor(Color.black);
                        g.fillRect(x+8, y+8, 16, 16);
                    }
                    @Override
                    public void collision(GameObject ob){
                        if(ob instanceof Hero){
                            ob.hurt(damage);
                            ((Hero)ob).addBuff(new Buff.FireDebuff(-1, -1, 1+(int)Math.round(madnessCoefficient()), (timeSince()/10)+Decider.r.nextInt(2001)-1000));
                        }
                    }
                };
                t.x = fire.x;
                t.y = Decider.r.nextInt(width-100)+50;
                handler.addObject(t);
            }
        }
        
        void spawnGrenadiers(){
            int amount = 1 + Decider.r.nextInt(2) + (int)(3d*madnessCoefficient());
            for(int n=0;n<amount;n++){
                Grenadier t = new Grenadier(2+(int)(3d*madnessCoefficient()), target, handler){
                    {xp=0;}
                    @Override
                    public Color getColor(long fn){
                        return new Color((int)((fn/2d)%255), 0, 20);
                    }
                    @Override
                    public void collision(GameObject ob){
                        if(ob instanceof Hero){
                            ob.hurt(damage);
                            ((Hero)ob).addBuff(new Buff.FireDebuff(-1, -1, 1+(int)Math.round(madnessCoefficient()), (timeSince()/10)+Decider.r.nextInt(2001)-1000));
                        }
                    }
                };
                t.x = fire.x;
                t.y = Decider.r.nextInt(width-100)+50;
                handler.addObject(t);
            }
        }
        
        void spawnBombs(){
            int amount = 2 + Decider.r.nextInt(3) + (int)(3d*madnessCoefficient());
            for(int n=0;n<amount;n++){
                Bomb t = new Bomb(2+(int)(3d*madnessCoefficient()), target){{xp=0;}};
                t.x = fire.x;
                t.y = Decider.r.nextInt(width-100)+50;
                handler.addObject(t);
            }
        }        
        
        boolean minionsAlive(){
            return handler.checkIfExists(ob -> ob.name.startsWith("Tracker")||ob.name.startsWith("Bomb")||ob.name.startsWith("Grenadier"));
        }
        
    }
    
    private static class FuelTank extends Enemy{

        public FuelTank(int ex, int ey){
            super("Fuel Tank", 550, 0, 40, 60, 0, 0, 0, 0, new Resistance(ShootingMode.MISSILE, 0.5));
            x = ex;
            y = ey;
        }
        
        @Override
        public synchronized void tick(Handler handler){
            if(hp<maxhp) hp+=0.03;
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
    
    private static double madnessCoefficient(){
        return fuelTank.hp/fuelTank.maxhp;
    }
        
    private static double getRotationAngle(){
        return Math.PI/(840d/(madnessCoefficient()+1.0)-240d);
    }

    private static double getAngleForCoords(double x, double y){
        double t = obtuseAtan((x-(double)fire.x),(y-(double)fire.y));
        double f = obtuseAtan(fire.velx, fire.vely);
        if(f>t){
            if(f-t>t+2*Math.PI-f) return getRotationAngle();
            return -getRotationAngle();
        }else{
            if(t-f>f+2*Math.PI-t) return -getRotationAngle();
            return getRotationAngle();
        }
    }

    private static double getBulletAngle(Handler handler){
        GameObject ob = handler.getBullet();
        if(ob==null) return 0;
        return 7d*getAngleForCoords(ob.x, ob.y);
    }
    
}
