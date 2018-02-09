
package entities.bosses;

import entities.Bullet;
import entities.Consumable;
import entities.GameObject;
import entities.Hero;
import entities.Missile;
import entities.consumables.Buff;
import entities.consumables.Usable;
import entities.consumables.Usable.*;
import entities.enemies.Gunner;
import entities.enemies.Tracker;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import logic.ConstantFields;
import static logic.ConstantFields.courseCorrectionFactor;
import yoisupiru.Decider;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class TheEviscerator extends Boss{
    
    public TheEviscerator(Handler h, GameObject targ){
        super("The Eviscerator", ConstantFields.hp11+ConstantFields.hp12+ConstantFields.hp13, 20, "The_Eviscerator.wav", 
                new Phase1(targ, 26, 6), new Phase2(h, targ,ConstantFields.hp13+ConstantFields.hp12,ConstantFields.hp12),
                new Phase3(h, targ, ConstantFields.hp13, 27, 7));
        ((Phase2)phases[1]).instance = this;
    }
    
    @Override
    public void collision(GameObject ob){
        if(phaseNum!=1) super.collision(ob);
    }
    
    @Override
    public void drop(Handler hand){
        ((Phase3) phases[2]).disarmTraps();
        Usable u;
        switch(Decider.r.nextInt(3)){
            case 0: u = new HealingPotion(4); break;
            case 1: u = new Shield(4); break;
            default: u = new DeathMissile(4); break;
        }
        u.x = x+width/2-u.width/2;
        u.y = y+height/2-u.height/2;
        hand.addObject(u);
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
            Color col = new Color(32, (int)(frameNum%233), 50);
            
            g.setColor(new Color(33, 229, 22));
            g.fillOval(x, y, width, height);
            g.setColor(Color.black);
            g.fillOval(x+8, y+8, 144, 144);
            
            AffineTransform at1, at2, a45 = AffineTransform.getRotateInstance(Math.PI/4.0, x+width/2, y+height/2);
            double r1 = -((double)frameNum/2)%(2*Math.PI), r2 = ((double)frameNum/2)%(2*Math.PI);
            
            g.setColor(col);
            Rectangle rect = new Rectangle(x+width/2-16, y-16, 32, 32);
            at1 = AffineTransform.getRotateInstance(r1, rect.x+16, rect.y+16);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(col);
            rect = new Rectangle(x+width/2-16, y+height-16, 32, 32);
            at1 = AffineTransform.getRotateInstance(r1, rect.x+16, rect.y+16);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(at1.createTransformedShape(rect));
            
            g.setColor(col);
            rect = new Rectangle(x-16, y+height/2-16, 32, 32);
            at2 = AffineTransform.getRotateInstance(r2, rect.x+16, rect.y+16);
            g.fill(at2.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(at2.createTransformedShape(rect));
            
            g.setColor(col);
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
            int dx = target.x+target.width/2, dy = target.y+target.height/2;
            if(dy<y&&vely>-speed){
                if(vely-courseCorrectionFactor<-speed){
                    vely = -speed;
                }else vely -= courseCorrectionFactor;
            }else if(dy>y&&vely<speed){
                if(vely+courseCorrectionFactor>speed){
                    vely = speed;
                }else vely += courseCorrectionFactor;
            }
            if(dx<x&&velx>-speed){
                if(velx-courseCorrectionFactor<-speed){
                    velx = -speed;
                }else velx -= courseCorrectionFactor;
            }else if(dx>x&&velx<speed){
                if(velx+courseCorrectionFactor>speed){
                    velx = speed;
                }else velx += courseCorrectionFactor;
            }
        }
        
        void lunge(){
            double cx = x+width/2, cy = y+height/2, dx = target.x+target.width/2, dy = target.y+target.height/2;
            double gradient = Math.abs((dy-cy)/(dx-cx));
            if(dx>cx){
                if(dy<cy){ //1st Quartile
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
                if(dy>cy){ //3rd Quartile
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
    
    private static class Phase2 extends BossPhase{
    
        final GameObject target;
        final Missile bullet = new Missile(1.2, 60, -1, -1, -1){
            @Override
            public void collision(GameObject ob){
                if(ob instanceof Bullet){
                    if(!(ob instanceof Missile)) updateOtherVelocity(ob);
                }else if(!(ob instanceof Consumable)&&!(ob instanceof Boss)&&!(ob instanceof Minion)){
                    hp = -1;
                    ob.hp -= damage;
                }
            }
        };
        private final Handler handler;
        int minsLeft = 4;
        private int clock = Decider.r.nextInt(601) + 150;
        protected boolean minionActive = false;
        private final double healthPerMinion;
        private Boss instance;
        private long spawnTime = -1;
        
        public Phase2(Handler h, GameObject targ, double th, double actHealth){
            super(th, 3, 120, 120, 0.5, true);
            target = targ;
            handler = h;
            healthPerMinion = actHealth/4d;
            changeDirection();
        }

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D)ig;
            
            g.setColor(new Color(33, 229, 22));
            g.fillOval(x, y, width, height);
            g.setColor(Color.black);
            g.fillOval(x+8, y+8, 104, 104);
            
            AffineTransform a45;
            
            if(minsLeft == 4){
                a45 = AffineTransform.getRotateInstance(Math.PI/4.0, x-16, y+height/2);
                g.setColor(new Color(64, (int)(frameNum%233), 60));
                Rectangle rect = new Rectangle(x-32, y+height/2-16, 32, 32);
                g.fill(a45.createTransformedShape(rect));
                g.setColor(Color.BLACK);
                rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
                g.fill(a45.createTransformedShape(rect));
            }
            if(minsLeft >= 3){
                a45 = AffineTransform.getRotateInstance(Math.PI/4.0, x, y+height/2);
                g.setColor(new Color((int)(frameNum%233), 64, 60));
                Rectangle rect = new Rectangle(x-16, y+height/2-16, 32, 32);
                g.fill(a45.createTransformedShape(rect));
                g.setColor(Color.BLACK);
                rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
                g.fill(a45.createTransformedShape(rect));
            }
            if(minsLeft >= 2){
                a45 = AffineTransform.getRotateInstance(Math.PI/4.0, x+width+16, y+height/2);
                g.setColor(new Color(60, 64, (int)(frameNum%233)));
                Rectangle rect = new Rectangle(x+width, y+height/2-16, 32, 32);
                g.fill(a45.createTransformedShape(rect));
                g.setColor(Color.BLACK);
                rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
                g.fill(a45.createTransformedShape(rect));
            }
            if(minsLeft >= 1){
                a45 = AffineTransform.getRotateInstance(Math.PI/4.0, x+width, y+height/2);
                g.setColor(new Color(60+(int)(frameNum%120), 164, 80));
                Rectangle rect = new Rectangle(x+width-16, y+height/2-16, 32, 32);
                g.fill(a45.createTransformedShape(rect));
                g.setColor(Color.BLACK);
                rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
                g.fill(a45.createTransformedShape(rect));
            }
            
            a45 = AffineTransform.getRotateInstance(Math.PI/4.0, x+width/2, y+height/2);
            g.setColor(new Color((int)frameNum%100, 75+target.y%50, (int)(100*hp/maxhp)+40));
            Rectangle rect = new Rectangle(x+width/2-40, y+height/2-40, 80, 80);
            g.fill(a45.createTransformedShape(rect));
            g.setColor(Color.BLACK);
            rect = new Rectangle(x+width/2-30, y+height/2-30, 60, 60);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(new Color(32, 183, 89));
            g.fillOval(x+width/2-15, y+height/2-15, 30, 30);
            g.setColor(Color.BLACK);
            g.fillOval(x+width/2-6, y+height/2-6, 12, 12);
            
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            super.actionPerformed(ae);
            if(spawnTime==-1) spawnTime = System.currentTimeMillis();
            clock--;
            isNearEdge();
            if(clock<=0){
                setClock();
                changeDirection();
                if(!minionActive) switch(Decider.r.nextInt(3)){
                    case 0: spawnMinion(); return;
                    case 1: spawnTracker(); return;
                    default: fireMissile();
                }else if(Decider.r.nextInt(2)==0) fireMissile();
                else spawnTracker();
            }
        }
        
        private void setClock(){
            int sub = (int)(System.currentTimeMillis()-spawnTime)/113;
            clock = 150 + Decider.r.nextInt(601-(sub<400?sub:400));
        }
        
        private void changeDirection(){
            velx = Decider.r.nextDouble()*2-1;
            vely = Decider.r.nextDouble()*2-1;
        }
        
        private void isNearEdge(){
            if(x<50) velx = Decider.r.nextDouble();
            else if(x+width>Main.WIDTH-50) velx = -Decider.r.nextDouble();
            if(y<50) vely = Decider.r.nextDouble();
            else if(y+height>Main.HEIGHT-50) vely = -Decider.r.nextDouble();
        }
        
        void spawnTracker(){
            Tracker t = new Tracker(target, 5-minsLeft/2);
            t.x = x;
            t.y = y;
            handler.addObject(t);
        }
        
        void spawnMinion(){
            handler.addObject(new Minion(5-minsLeft));
            minsLeft--;
            minionActive = true;
        }
        
        void fireMissile(){
            int cx = x+width/2, cy = y+height/2;
            double vx, vy, sx, sy, dx = target.x+target.width/2, dy = target.y+target.height/2;
            double gradient = Math.abs((dy-cy)/(dx-cx));
            if(dx>cx){
                if(dy<cy){ //1st Quartile
                    if(gradient<1.0){
                        sx = cx+36;
                        sy = cy-(int)(gradient*36);
                        vx = bullet.bulletSpeed;
                        vy = vx*-gradient;
                    }else{
                        sy = cy-36;
                        sx = cx+(int)(36/gradient);
                        vy = -bullet.bulletSpeed;
                        vx = vy/-gradient;
                    }
                }else{ //2nd Quartile
                    if(gradient<1.0){
                        sx = cx+36;
                        sy = cy+(int)(gradient*36);
                        vx = bullet.bulletSpeed;
                        vy = vx*gradient;
                    }else{
                        sy = cy+36;
                        sx = cx+(int)(36/gradient);
                        vy = bullet.bulletSpeed;
                        vx = vy/gradient;
                    }
                }
            }else{
                if(dy>cy){ //3rd Quartile
                    if(gradient<1.0){
                        sx = cx-36;
                        sy = cy+(int)(gradient*36);
                        vx = -bullet.bulletSpeed;
                        vy = vx*-gradient;
                    }else{
                        sy = cy+36;
                        sx = cx-(int)(36/gradient);
                        vy = bullet.bulletSpeed;
                        vx = vy/-gradient;
                    }
                }else{ //4th Quartile
                    if(gradient<1.0){
                        sx = cx-36;
                        sy = cy-(int)(gradient*36);
                        vx = -bullet.bulletSpeed;
                        vy = vx*gradient;
                    }else{
                        sy = cy-36;
                        sx = cx-(int)(36/gradient);
                        vy = -bullet.bulletSpeed;
                        vx = vy/gradient;
                    }
                }
            }
            handler.addObject(bullet.create((int)sx, (int)sy, vx-0.2, vy+0.2));
            handler.addObject(bullet.create((int)sx, (int)sy, vx, vy));
            handler.addObject(bullet.create((int)sx, (int)sy, vx+0.2, vy-0.2));
        }
        
        void takeDamage(double dam){
            instance.hp -= dam;
        }
        
        private class Minion extends Gunner{

            final int minionNum;

            public Minion(int mN){
                super("Evisceration Machine", Phase2.this.healthPerMinion, mN, 48, 48, 0, 2.75+0.25*mN, Phase2.this.target, Phase2.this.handler, 10000, 10000);
                minionNum = mN;
                forced = true;
                x = Phase2.this.x;
                y = Phase2.this.y;
                bullet = new Bullet(8+mN, 3+5*mN, -1, -1, -1);
            }
            
            @Override
            public void render(Graphics g, long frameNum){
                Graphics2D g2d = (Graphics2D)g;
                Color color;
                switch(minionNum){
                    case 1: color = new Color(64, (int)(frameNum%233), 60); break;
                    case 2: color = new Color((int)(frameNum%233), 64, 60); break;
                    case 3: color = new Color(60, 64, (int)(frameNum%233)); break;
                    default: color = new Color(60+(int)(frameNum%120), 164, 80); break;
                }
                AffineTransform at = new AffineTransform();
                Rectangle rect = new Rectangle(x, y, 48, 48);
                at.rotate((-((double)frameNum/2)%(2*Math.PI)), rect.x+rect.width/2, rect.y+rect.height/2);
                g2d.setColor(color);
                g2d.fill(at.createTransformedShape(rect));
                g2d.setColor(Color.black);
                rect = new Rectangle(x+10, y+10, 28, 28);
                g2d.fill(at.createTransformedShape(rect));
                g2d.setColor(new Color((int)(255.0*clock), 32, 32));
                rect = new Rectangle(x+18, y+18, 12, 12);
                at = new AffineTransform();
                at.rotate(((double)frameNum/2)%(2*Math.PI), rect.x+rect.width/2, rect.y+rect.height/2);
                g2d.fill(at.createTransformedShape(rect));
            }
            
            @Override
            public void die(Handler h){
                alive = false;
                Phase2.this.minionActive = false;
                Phase2.this.takeDamage(maxhp);
                handler.removeObject(this);
            }
            
        }
        
    }
    
    private static class Phase3 extends BossPhase{
        
        final GameObject target;
        final Handler handler;
        private long spawnTime = -1;
        private boolean lunging = false;
        private int clock = 0;
        private LinkedList<Trap> traps = new LinkedList<>();
        
        public Phase3(Handler h, GameObject targ, double th, double dam, double sp){
            super(th, dam, 150, 150, sp);
            target = targ;
            handler = h;
        }

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D)ig;
            
            Color col = new Color(32, (int)(frameNum%233), 50);
            g.setColor(new Color(33, 229, 22));
            g.fillOval(x, y, width, height);
            g.setColor(Color.black);
            g.fillOval(x+8, y+8, 134, 134);
            
            AffineTransform aMid = AffineTransform.getRotateInstance(((double)frameNum/3)%(2*Math.PI), x+width/2, y+height/2);
            g.setColor(col);
            Rectangle rect = new Rectangle(x+width/2-16, y-16, 40, 40);
            AffineTransform a45 = AffineTransform.getRotateInstance(Math.PI/4d, rect.x+16, rect.y+16);
            a45.concatenate(aMid);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(col);
            rect = new Rectangle(x+width/2-16, y+height-16, 40, 40);
            a45 = AffineTransform.getRotateInstance(Math.PI/4d, rect.x+16, rect.y+16);
            a45.concatenate(aMid);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(col);
            rect = new Rectangle(x-16, y+height/2-16, 40, 40);
            a45 = AffineTransform.getRotateInstance(Math.PI/4d, rect.x+16, rect.y+16);
            a45.concatenate(aMid);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(col);
            rect = new Rectangle(x+width-16, y+height/2-16, 40, 40);
            a45 = AffineTransform.getRotateInstance(Math.PI/4d, rect.x+16, rect.y+16);
            a45.concatenate(aMid);
            g.fill(a45.createTransformedShape(rect));
            
            g.setColor(Color.BLACK);
            rect = new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
            g.fill(a45.createTransformedShape(rect));
            
            a45 = AffineTransform.getRotateInstance(Math.PI/4d, x+width/2, y+width/2);
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
        
        private void setTraps(){
            spawnTime = System.currentTimeMillis();
            for(int n=0, max=Decider.r.nextInt(5)+3;n<max;n++){
                Trap t = new Trap();
                traps.add(t);
                t.spawn(handler);
            }
        }
        
        private void lunge(){
            double cx = x+width/2, cy = y+height/2, dx = target.x+target.width/2, dy = target.y+target.height/2;
            double gradient = Math.abs((dy-cy)/(dx-cx));
            if(dx>cx){
                if(dy<cy){ //1st Quartile
                    if(gradient<1.0){
                        velx = speed*1.2*getTimeMult();
                        vely = velx*-gradient;
                    }else{
                        vely = -speed*1.2*getTimeMult();
                        velx = vely/-gradient;
                    }
                }else{ //2nd Quartile
                    if(gradient<1.0){
                        velx = speed*1.2*getTimeMult();
                        vely = velx*gradient;
                    }else{
                        vely = speed*1.2*getTimeMult();
                        velx = vely/gradient;
                    }
                }
            }else{
                if(dy>cy){ //3rd Quartile
                    if(gradient<1.0){
                        velx = -speed*1.2*getTimeMult();
                        vely = velx*-gradient;
                    }else{
                        vely = speed*1.2*getTimeMult();
                        velx = vely/-gradient;
                    }
                }else{ //4th Quartile
                    if(gradient<1.0){
                        velx = -speed*1.2*getTimeMult();
                        vely = velx*gradient;
                    }else{
                        vely = -speed*1.2*getTimeMult();
                        velx = vely/gradient;
                    }
                }
            }
        }
        
        private double getTimeMult(){
            long delta = System.currentTimeMillis() - spawnTime;
            return (double)(delta/45000L)+1d;
        }
        
        private void disarmTraps(){
            traps.stream().forEach((trap) -> {
                trap.hp = -1;
            });
        }
        
        @Override
        public synchronized void actionPerformed(ActionEvent ae){
            super.actionPerformed(ae);
            if(spawnTime==-1) setTraps();
            clock++;
            if(clock>=85){
                clock = 0;
                lunge();
            }
        }
        
        private class Trap extends Buff{

            public Trap(){
                super("Trap", 16, 16, 1, Decider.r.nextInt(3)+2);
            }

            @Override
            public void start(Hero h){
                h.hurt(-h.maxhp/lvl);
            }

            @Override
            public void end(Hero h){}

            @Override
            public void render(Graphics g, long frameNum){
                g.setColor(new Color((int)(50*lvl), (int)(50*lvl), (int)(50*lvl)));
                g.fill3DRect(x, y, width, height, true);
            }
        
        }
        
    }
    
}
