
package entities.bosses;

import entities.Bullet;
import entities.Fire;
import entities.GameObject;
import entities.Goo;
import entities.Hero;
import entities.Hero.ShootingMode;
import entities.HomingBullet.HealthyHomingBullet;
import entities.consumables.Buff.FireDebuff;
import entities.consumables.Buff.SlownessDebuff;
import entities.consumables.Usable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import logic.Collision;
import static logic.ConstantFields.hp31;
import static logic.ConstantFields.hp32;
import static logic.ConstantFields.hp33;
import static logic.ConstantFields.hp34;
import logic.NonCollidable;
import logic.Resistance;
import logic.Twin;
import yoisupiru.Decider;
import static yoisupiru.Decider.r;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class TheTwins extends Boss implements Twin{
    
    private static TheRavager ravager;
    private static TheDevastator devastator;
    private static Hero target;
    private static Handler handler;
    private static final double scsize = Main.WIDTH+Main.HEIGHT;
    private static HealthyHomingBullet bullet;
    private static List<GameObject> twinList;
    
    public TheTwins(Hero targ, Handler h){
        super("The Twins", 1, 60, "The_Twins.wav", new BossPhase(-1, -1, -1, -1, -1, null){
            @Override
            public void render(Graphics g, long frameNum){
                throw new UnsupportedOperationException("Trying to render nonexistance.");
            }
        });
        target = targ;
        handler = h;
        bullet = new HealthyHomingBullet(40, 5, 1.2, 60, -1, -1, -1, target){
            @Override
            public void collision(GameObject ob){
                if(ob instanceof Bullet){
                    if(!(ob instanceof NonCollidable)) updateBothVelocities(ob);
                    hp -= ((Bullet)ob).damage;
                }else if(ob instanceof Hero){
                    hp = -1;
                    ob.hurt(damage);
                    ((Hero)ob).addBuff(new FireDebuff(-1, -1, 2, 2000+Decider.r.nextInt(3001)));
                }
            }
        };
        ravager = new TheRavager();
        ravager.x = x;
        ravager.y = y;
        devastator = new TheDevastator();
        devastator.x = x;
        devastator.y = y;
        twinList = new ArrayList<>();
        twinList.add(ravager);
        twinList.add(devastator);
    }
    
    private static double[] getShootVelocity(int cx, int cy, double bulletSpeed){
        double vx, vy, dx = target.x+target.width/2, dy = target.y+target.height/2, sx, sy;
        double gradient = Math.abs((dy-(double)cy)/(dx-(double)cx));
        if(dx>cx){
            if(dy<cy){ //1st Quartile
                if(gradient<1.0){
                    sx = cx+36;
                    sy = cy-(int)(gradient*36d);
                    vx = bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy-36;
                    sx = cx+(int)(36d/gradient);
                    vy = -bulletSpeed;
                    vx = vy/-gradient;
                }
            }else{ //2nd Quartile
                if(gradient<1.0){
                    sx = cx+36;
                    sy = cy+(int)(gradient*36d);
                    vx = bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy+36;
                    sx = cx+(int)(36d/gradient);
                    vy = bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }else{
            if(dy>cy){ //3rd Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy+(int)(gradient*36d);
                    vx = -bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy+36;
                    sx = cx-(int)(36d/gradient);
                    vy = bulletSpeed;
                    vx = vy/-gradient;
                }
            }else{ //4th Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy-(int)(gradient*36d);
                    vx = -bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy-36;
                    sx = cx-(int)(36d/gradient);
                    vy = -bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }
        return new double[]{sx, sy, vx, vy};
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){
        if(ravager.alive) ravager.actionPerformed(ae);
        if(devastator.alive) devastator.actionPerformed(ae);
    }
    
    @Override
    public void render(Graphics g, long fn){
        if(ravager.alive) ravager.render(g, fn);
        if(devastator.alive) devastator.render(g, fn);
        paintHealthBar(g);
    }
    
    @Override
    public void tick(Handler handler){
        if(ravager.alive) ravager.tick(handler);
        if(devastator.alive) devastator.tick(handler);
    }
    
    @Override
    public void collision(GameObject ob){
        if(ravager.alive&&ravager.isColliding(ob)) ravager.collision(ob);
        else if(devastator.alive) devastator.collision(ob);
    }
    
    @Override
    public boolean isColliding(Collision c){
        return (ravager.alive&&ravager.isColliding(c))||(devastator.alive&&devastator.isColliding(c));
    }
    
    @Override
    protected void paintHealthBar(Graphics g){
        if(devastator.alive){
            g.setColor(Color.YELLOW);
            g.drawString("The Devastator", w+2, sy-20);
            g.setColor(Color.DARK_GRAY);
            g.fill3DRect(w, sy, w, 12, false);
            g.setColor(Color.RED);
            g.fill3DRect(w+1, sy+1, (int)(devastator.hp/devastator.maxhp*(w-2D)), 10, true);
            g.setColor(Color.GRAY);
            for(int n=1;n<devastator.phases.length;n++) g.fill3DRect((int)((devastator.phases[n].triggerHealth/devastator.maxhp)*w+w), sy-2, 3, 16, true);
        }
        if(ravager.alive){
            g.setColor(Color.YELLOW);
            g.drawString("The Ravager", w+2, sy-64);
            g.setColor(Color.DARK_GRAY);
            g.fill3DRect(w, sy-44, w, 12, false);
            g.setColor(Color.RED);
            g.fill3DRect(w+1, sy-43, (int)(ravager.hp/ravager.maxhp*(w-2D)), 10, true);
            g.setColor(Color.GRAY);
            for(int n=1;n<ravager.phases.length;n++) g.fill3DRect((int)((ravager.phases[n].triggerHealth/ravager.maxhp)*w+w), sy-46, 3, 16, true);
        }
    }

    @Override
    public List<GameObject> getTwinObjects(){
        return twinList;
    }
    
    private static class TheRavager extends Boss{
        
        static final Fire fire = new Fire(18.0, 2, -1, -1, -1, -1, Math.PI/10d, 75, 5000, 3);

        public TheRavager(){
            super("The Ravager", hp31+hp32, 0, "Why are you playing the Ravager?", new RPhase1(), new RPhase2());
            System.out.println("R: " + phases[1].triggerHealth + ", " + maxhp);
        }
        
        static class RPhase1 extends BossPhase{

            public RPhase1(){
                super(-1, -5, 80, 70, 9.0, new Resistance(ShootingMode.MACHINE, 0.85));
                velx = (r.nextDouble()*2d*speed)-speed;
                vely = (r.nextDouble()*2d*speed)-speed;
            }
            
            boolean firing = false, lunging = false;
            long clock = 0;
            long waitPeriod = (long)(Decider.r.nextDouble()*850);
            
            @Override
            public void collision(GameObject ob){
                if(ob instanceof Hero){
                    if(ob.hp+5>ob.maxhp) ob.hp = ob.maxhp;
                    else ob.hp += 5;
                }else if(ob instanceof Bullet && ob.hp>0){
                    ob.hp = -1;
                    ravager.hp-=((Bullet) ob).damage*(1d + r.nextDouble()*(0.5+(target.isOnFire()?0:0.5)));
                }
            }
            
            public double distMult(){
                return 1d-(Math.abs(x-target.x)+Math.abs(y-target.y))/scsize;
            }
            
            void courseCorrection(){
                double t = obtuseAtan((double)target.x-(double)x,(double)target.y-(double)y);
                double f = obtuseAtan(velx, vely);
                double mult = 1;
                if(f>t){
                    if(f-t<=t+2*Math.PI-f) mult = -1;
                }else{
                    if(t-f>f+2*Math.PI-t) mult = -1;
                }
                if(distMult()>0.7) mult = -mult;
                velAngleChange(mult*Math.PI/18d);
            }
            
            @Override
            public synchronized void actionPerformed(ActionEvent ae){
                super.actionPerformed(ae);
                clock++;
                if(Math.abs(x-target.x)+Math.abs(y-target.y)<120&&!lunging){
                    lunging = true;
                    clock = 0;
                }
                if(lunging){
                    if(clock==0){
                        double c[] = getShootVelocity(x+width/2, y+height/2, speed);
                        velx = c[2];
                        vely = c[3];
                        velAngleChange(Math.PI);
                    }else if(clock>=24) lunging = false;
                }else courseCorrection();
                if(firing){
                    double[] c = getShootVelocity(x+width/2, y+height/2, 7.5);
                    fire.x = (int)c[0];
                    fire.y = (int)c[1];
                    fire.velx = c[2];
                    fire.vely = c[3];
                    if(clock==1){
                        handler.addObject(fire);
                    }
                }
                if(clock>=waitPeriod){
                    waitPeriod = (long)(Decider.r.nextDouble()*2000d/distMult());
                    if(firing){
                        handler.removeObject(fire);
                        firing = false;
                    }else firing = true;
                    clock = 0;
                }
            }

            @Override
            public void render(Graphics ig, long frameNum){
                Graphics2D g = (Graphics2D) ig;
                double r1 = -((double)frameNum/2)%(2*Math.PI), r2 = ((double)frameNum/2.5)%(2*Math.PI);
                AffineTransform at1 = AffineTransform.getRotateInstance(r1, x+width/2, y+height/2),
                        at2 = AffineTransform.getRotateInstance(r2, x+width/2, y+height/2);
                int[] outerx = {x+20, x+20, x, x+20, x+20, x+40, x+60, x+60, x+80, x+60, x+60, x+40}, 
                        outery = {y, y+17, y+35, y+52, y+70, y+52, y+70, y+52, y+35, y+17, y, y+17};
                g.setColor(new Color(229, 52, (int)((frameNum/3)%150)));
                g.fill(at1.createTransformedShape(new Polygon(outerx, outery, 6)));
                g.setColor(Color.BLACK);
                Rectangle rect = new Rectangle(x+36, y+15, 8, 40);
                g.fill(at2.createTransformedShape(rect));
                at2.concatenate(AffineTransform.getQuadrantRotateInstance(1, x+width/2, y+height/2));
                g.fill(at2.createTransformedShape(rect));
            }
            
        }
        
        static class RPhase2 extends BossPhase{
            
            long clock = 0;
            boolean healing = false;
            long spawnTime = -1;
            long waitPeriod = (long)(Decider.r.nextDouble()*850);

            public RPhase2(){
                super(hp32, 30, 80, 70, 7.5, new Resistance(ShootingMode.CONSTANT, 0.8));
                velx = (r.nextDouble()*2d*speed)-speed;
                vely = (r.nextDouble()*2d*speed)-speed;
            }
            
            @Override
            public synchronized void actionPerformed(ActionEvent ae){
                super.actionPerformed(ae);
                if(spawnTime==-1) spawnTime = System.currentTimeMillis();
                clock++;
                if(healing){
                    if(devastator.alive){
                        if(devastator.hp<devastator.maxhp) devastator.hp+=0.2;
                    }else if(ravager.hp<ravager.maxhp) ravager.hp+=0.2;
                }
                if(clock>=waitPeriod){
                    healing = !healing;
                    waitPeriod = (long)(Decider.r.nextDouble()*850);
                    double c[] = getShootVelocity(x+width/2, y+height/2, bullet.bulletSpeed);
                    handler.addObject(bullet.create((int)c[0], (int)c[1], c[2], c[3], target));
                    clock = 0;
                }
            }

            @Override
            public void render(Graphics ig, long frameNum){
                Graphics2D g = (Graphics2D) ig;
                int now = (int)((1d-((System.currentTimeMillis()-spawnTime)/60000d))*250d);
                now = now>255?255:(now<0?0:now);
                double r1 = -((double)frameNum/2)%(2*Math.PI), r2 = ((double)frameNum/2.5)%(2*Math.PI);
                AffineTransform at1 = AffineTransform.getRotateInstance(r1, x+width/2, y+height/2),
                        at2 = AffineTransform.getRotateInstance(r2, x+width/2, y+height/2);
                int[] outerx = {x+20, x+20, x, x+20, x+20, x+40, x+60, x+60, x+80, x+60, x+60, x+40}, 
                        outery = {y, y+17, y+35, y+52, y+70, y+52, y+70, y+52, y+35, y+17, y, y+17};
                g.setColor(new Color(100, 152, (int)((frameNum/4)%150)+50, now));
                g.fill(at1.createTransformedShape(new Polygon(outerx, outery, 6)));
                g.setColor(new Color(0, 0, 0, now));
                Rectangle rect = new Rectangle(x+36, y+15, 8, 40);
                g.fill(at2.createTransformedShape(rect));
                g.fill(at1.createTransformedShape(rect));
            }
            
            @Override
            public void collision(GameObject ob){
                if(ob instanceof Bullet && ob.hp>0){
                    ob.hp = -1;
                    ravager.hp-=((Bullet) ob).damage;
                }
            }
            
        }
        
        @Override
        public void tick(Handler handler){
            if(x+width<=30||x>Main.WIDTH-30||y+height<=30||y>Main.HEIGHT-30){
                x = Main.WIDTH-x<-width/2?-width/2:Main.WIDTH-x>Main.WIDTH+width/2?Main.WIDTH+width/2:Main.WIDTH-x;
                y = Main.HEIGHT-y<-height/2?-height/2:Main.HEIGHT-y>Main.HEIGHT+height/2?Main.HEIGHT+height/2:Main.HEIGHT-y;
            }
            if(phaseNum==phases.length-1){
                if(hp<1) alive = false;
            }else if(hp<=phases[phaseNum+1].triggerHealth){
                phaseNum++;
                hp = phases[phaseNum].triggerHealth;
                damage = phases[phaseNum].damage;
            }
        }
        
        @Override
        public void collision(GameObject ob){
            phases[phaseNum].collision(ob);
        }
        
        @Override
        public void render(Graphics g, long frameNum){
            phases[phaseNum].render(g, frameNum);
        }
        
        @Override
        public void die(Handler h){
            super.die(h);
            twinList.remove(ravager);
        }
    
    }
    
    private static class TheDevastator extends Boss{

        public TheDevastator(){
            super("The Devastator", hp33+hp34, 0, "Why are you playing the Devastator?", new DPhase1(7), new DPhase2(9));
        }
        
        @Override
        public void tick(Handler handler){
            if(phaseNum==phases.length-1){
                if(hp<1) alive = false;
            }else if(hp<=phases[phaseNum+1].triggerHealth){
                phaseNum++;
                hp = phases[phaseNum].triggerHealth;
                damage = phases[phaseNum].damage;
            }
        }
        
        static class DPhase1 extends BossPhase{
            
            long clock = 0;
            long spawnTime = -1;
            long waitPeriod = (long)(Decider.r.nextDouble()*850);

            public DPhase1(double sp){
                super(-1, 10, 80, 70, sp, new Resistance(ShootingMode.BURST, 0.72));
                velx = (r.nextDouble()*2d*speed)-speed;
                vely = (r.nextDouble()*2d*speed)-speed;
            }
            
            @Override
            public synchronized void actionPerformed(ActionEvent ae){
                super.actionPerformed(ae);
                if(spawnTime==-1) spawnTime = System.currentTimeMillis();
                clock++;
                if(x<=20&&velx<0||x+width>=Main.WIDTH-20&&velx>0) velx = -velx;
                else if(y<=20&&vely<0||y+height>=Main.HEIGHT-20&&vely>0) vely = -vely;
                if(clock>=waitPeriod){
                    waitPeriod = (long)(Decider.r.nextDouble()*850);
                    long now = System.currentTimeMillis() - spawnTime;
                    if(r.nextInt(4)==0){
                        double c[] = getShootVelocity(x+width/2, y+height/2, bullet.bulletSpeed);
                        handler.addObject(bullet.create((int)c[0], (int)c[1], c[2], c[3], target));
                    }else{
                        double[] c = getShootVelocity(x+width/2,y+height/2, 1d+2d*Decider.r.nextDouble()+(now/22500d));
                        handler.addObject(new BouncingTrap(now/7500L+1500L, 1+2.3d*Decider.r.nextDouble(), (int)c[0], (int)c[1], c[2], c[3]));
                    }
                    clock = 0;
                }
            }

            @Override
            public void render(Graphics ig, long frameNum){
                Graphics2D g = (Graphics2D) ig;
                int[] outerx = {x+20, x, x+20, x+60, x+80, x+60}, 
                        outery = {y, y+35, y+70, y+70, y+35, y},
                        innerx = {x+24, x+8, x+24, x+56, x+72, x+56}, 
                        innery = {y+7, y+35, y+63, y+63, y+35, y+7};
                g.setColor(new Color(229, 12, 212));
                g.fillPolygon(outerx, outery, 6);
                g.setColor(Color.BLACK);
                g.fillPolygon(innerx, innery, 6);
                
                double r1 = -((double)frameNum/3)%(2*Math.PI), r2 = ((double)frameNum/4)%(2*Math.PI);
                AffineTransform at1 = AffineTransform.getRotateInstance(r1, x+width/2, y+height/2),
                        at2 = AffineTransform.getRotateInstance(r2, x+width/2, y+height/2);
                g.setColor(new Color(32, (int)(frameNum%233), 50));
                g.fill(at1.createTransformedShape(new Rectangle(x+24, y+19, 32, 32)));
                g.setColor(Color.BLACK);
                g.fill(at2.createTransformedShape(new Rectangle(x+28, y+23, 24, 24)));
            }
            
            @Override
            public void collision(GameObject ob){
                if(ob instanceof Hero){
                    ob.hurt(damage);
                }else if(ob instanceof Bullet && ob.IDOfRitchocket!=ID && !(ob instanceof HealthyHomingBullet)){
                    if(r.nextInt(4)==0){
                        updateOtherVelocity(ob);
                    }else if(ob.hp>0){
                        devastator.hp-=((Bullet)ob).damage*(0.65+r.nextDouble()*0.35);
                        ob.hp = -1;
                    }
                }
            }
            
        }
        
        static class DPhase2 extends BossPhase{
            
            boolean lunging = false;
            long clock = 0, startTime = -1;
            long waitPeriod = (long)(r.nextDouble()*850);
            
            public DPhase2(double sp){
                super(hp34, 40, 80, 70, sp, new Resistance(ShootingMode.MISSILE, 0.77));
                velx = (r.nextDouble()*2d*speed)-speed;
                vely = (r.nextDouble()*2d*speed)-speed;
                x = r.nextInt(Main.WIDTH);
                y = r.nextInt(Main.HEIGHT);
            }
            
            void circle(){
                if(clock%3==0){
                    setVelAngle(obtuseAtan(target.x-x, target.y-y));
                    velAngleChange(Math.PI/2);
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
            public void collision(GameObject ob){
                if(ob instanceof Bullet && ob.hp>0){
                    ob.hp = -1;
                    devastator.hp-=((Bullet) ob).damage;
                }
            }
            
            @Override
            public synchronized void actionPerformed(ActionEvent ae){
                super.actionPerformed(ae);
                if(startTime==-1) startTime = System.currentTimeMillis();
                if(lunging){
                    if(clock==0) lunge();
                    clock++;
                    if(clock>=25) lunging = false;
                }else{
                    circle();
                    clock++;
                    if(clock>=waitPeriod){
                        waitPeriod = (long)(r.nextDouble()*850);
                        if(target.isSlow()) lunging = true;
                        else{
                            double now = System.currentTimeMillis() - startTime;
                            double[] c = getShootVelocity(x+width/2, y+height/2, 5+now/18000L);
                            Goo.shoot(handler, (int)(14+now/9000L), 2, (int)c[0], (int)c[1], c[2], c[3]);
                        }
                        clock = 0;
                    }
                }
            }

            @Override
            public void render(Graphics ig, long frameNum){
                Graphics2D g = (Graphics2D) ig;
                double r1 = -((double)frameNum/2)%(2*Math.PI), r2 = ((double)frameNum/2.5)%(2*Math.PI);
                AffineTransform at1 = AffineTransform.getRotateInstance(r1, x+width/2, y+height/2),
                        at2 = AffineTransform.getRotateInstance(r2, x+width/2, y+height/2);
                int[] outerx = {x+20, x, x+20, x+60, x+80, x+60}, 
                        outery = {y, y+35, y+70, y+70, y+35, y},
                        innerx = {x+24, x+8, x+24, x+56, x+72, x+56}, 
                        innery = {y+7, y+35, y+63, y+63, y+35, y+7};
                g.setColor(new Color(229, 12, 212));
                g.fill(at1.createTransformedShape(new Polygon(outerx, outery, 6)));
                g.setColor(Color.BLACK);
                g.fill(at1.createTransformedShape(new Polygon(innerx, innery, 6)));
                
                g.setColor(new Color(32, (int)(frameNum%233), 50));
                g.fill(at2.createTransformedShape(new Rectangle(x+24, y+19, 32, 32)));
                g.setColor(Color.BLACK);
                g.fill(at2.createTransformedShape(new Rectangle(x+28, y+23, 24, 24)));
            }
            
        }
        
        @Override
        public void collision(GameObject ob){
            phases[phaseNum].collision(ob);
        }
        
        @Override
        public void render(Graphics g, long frameNum){
            phases[phaseNum].render(g, frameNum);
        }
        
        @Override
        public void die(Handler h){
            super.die(h);
            twinList.remove(devastator);
        }
        
    }
    
    private static class BouncingTrap extends SlownessDebuff{
    
        public BouncingTrap(long t, double l, int sx, int sy, double vx, double vy){
            super(t, l);
            hp = 18;
            forced = true;
            x = sx;
            y = sy;
            velx = vx;
            vely = vy;
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            synchronized(Main.soundSystem){
                move();
            }
            hp-=0.01;
            if(x<=0||x+width>=Main.WIDTH) velx = -velx;
            else if(y<=0||y+height>=Main.HEIGHT) vely = -vely;
        }
    
    }
    
    @Override
    public void drop(Handler hand){
        Usable u;
        switch(Decider.r.nextInt(4)){
            case 0: u = new Usable.HealingPotion(12); break;
            case 1: u = new Usable.Shield(12); break;
            case 2: u = new Usable.Hourglass(12); break;
            default: u = new Usable.DeathMissile(12); break;
        }
        u.x = x+width/2-u.width/2;
        u.y = y+height/2-u.height/2;
        hand.addObject(u);
    }
    
}
