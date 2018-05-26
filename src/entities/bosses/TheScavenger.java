package entities.bosses;

import entities.Bullet;
import entities.Enemy;
import entities.Fire;
import entities.GameObject;
import entities.Hero;
import entities.Hero.ShootingMode;
import entities.consumables.Usable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import logic.Collision;
import static logic.Collision.obtuseAtan;
import logic.ConstantFields;
import static logic.ConstantFields.courseCorrectionFactor;
import logic.NonCollidable;
import logic.Resistance;
import static yoisupiru.Decider.r;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class TheScavenger extends Boss{
    
    private static Handler handler;
    private static Hero target;
    private static List<Serpent> serpents = new LinkedList<>();
    private static TheScavenger instance;

    public TheScavenger(Handler h, Hero t){
        super("The Scavenger", ConstantFields.hp41, 80, "The_Scavenger.wav", new Boss.BossPhase(-1, -1, -1, -1, -1, null){
            @Override
            public void render(Graphics g, long frameNum){
                throw new UnsupportedOperationException("Trying to render nonexistance.");
            }
        });
        handler = h;
        target = t;
        instance = this;
        serpents.add(new Serpent());
    }
    
    private static class Serpent extends LinkedList<Segment>{
        
        LinkedList<int[]> coords = new LinkedList<>();
        boolean dissolved = false, firing = false, hurt = false;
        final Fire fire = new Fire(22.0, 4, -1, -1, -1, -1, Math.PI/10d, 95, 7600, 3);
        
        Serpent(){
            for(int n=0;n<ConstantFields.scavengerSegments;n++){
                add(new Segment(((double)ConstantFields.hp41)/((double)ConstantFields.scavengerSegments)));
            }
            stream().forEach(s -> {
                handler.addObject(s);
            });
            Segment start = get(0);
            coords.add(new int[]{start.x, start.y});
        }
        
        Serpent(List<Segment> segments, boolean cascade, boolean dis){
            super(segments);
            if(cascade) for(int n=0;n<size();n++) get(n).num = n;
            dissolved = dis;
            Segment start = get(0);
            coords.add(new int[]{start.x, start.y});
        }
        
        void removeHead(){
            remove();
            if(isEmpty()) serpents.remove(this);
            else if(get(0).num!=0) stream().forEach((s) -> {
                s.num--;
            });
        }
        
        void separate(int num){
            Serpent s1 = new Serpent(subList(0, num), false, dissolved);
            Serpent s2 = new Serpent(subList(num+1, size()), true, dissolved);
            serpents.remove(this);
            serpents.add(s1);
            serpents.add(s2);
            System.err.println("SEPARATE");
        }
        
        void die(Segment s){
            if(s.num==0) removeHead();
            else if(s.num==size()-1) removeLast();
            else separate(s.num);
        }
        
        int[] getCoords(int n){
            if(n<coords.size()) return coords.get(n);
            return coords.getLast();
        }
        
        @Override
        public final boolean add(Segment s){
            s.num = size();
            s.serpent = this;
            return super.add(s);
        }
        
        private void rotateFireTowards(double x, double y){
            double t = obtuseAtan((x-(double)fire.x),(y-(double)fire.y));
            double f = obtuseAtan(fire.velx, fire.vely);
            double mult = Math.PI/180d;
            if(f>t){
                if(f-t<=t+2*Math.PI-f) mult = -mult;
            }else{
                if(t-f>f+2*Math.PI-t) mult = -mult;
            }
            fire.velAngleChange(mult);
        }
        
    }
    
    private static class Segment extends Enemy implements NonCollidable, NonClearable{
        
        int num;
        double mult = Math.PI/560d;
        long clock = 0;
        long waitPeriod = (long)(r.nextDouble()*850);
        long switchTime = 0;
        Serpent serpent;

        public Segment(double health){
            super("Segment", health, 42, 48, 42, 0, 2d, Integer.MAX_VALUE, Integer.MAX_VALUE, new Resistance(ShootingMode.BURST, 0.55));
            forced = true;
            velx = (2d*speed*r.nextDouble())-speed;
            vely = (2d*speed*r.nextDouble())-speed;
        }
        
        @Override
        public void tick(Handler h){
            healthCheck(h);
            long now = System.currentTimeMillis();
            if(x+width<=10||x>Main.WIDTH-10||y+height<=10||y>Main.HEIGHT-10 && now-switchTime>750){
                switchTime = now;
                x = Main.WIDTH-x<-width/2?-width/2:Main.WIDTH-x>Main.WIDTH+width/2?Main.WIDTH+width/2:Main.WIDTH-x;
                y = Main.HEIGHT-y<-height/2?-height/2:Main.HEIGHT-y>Main.HEIGHT+height/2?Main.HEIGHT+height/2:Main.HEIGHT-y;
            }
        }
        
        @Override
        public void hurt(double dam){
            hp -= dam;
            if(r.nextInt(2)==0) serpent.hurt = true;
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            clock++;
            synchronized(Main.soundSystem){
                move();
            }
            if(num==0){
                if(clock%10==0){
                    serpent.coords.addFirst(new int[]{x, y});
                    if(serpent.coords.size()>serpent.size()) serpent.coords.removeLast();
                }
                if(clock>=waitPeriod){
                    clock = 0;
                    waitPeriod = (long)(r.nextDouble()*2000)+1000;
                    if(serpent.dissolved) serpent.dissolved = false;
                    else if(serpent.firing){
                        serpent.firing = false;
                        handler.removeObject(serpent.fire);
                    }else if(r.nextInt(4)==0) serpent.dissolved = true;
                    else{
                        handler.addObject(serpent.fire);
                        serpent.firing = true;
                    }
                }
                if(serpent.dissolved) courseCorrection(target.x+target.width/2, target.y+target.height/2);
                else{
                    if(serpent.firing){
                        serpent.fire.x = x;
                        serpent.fire.y = y;
                        serpent.rotateFireTowards(target.x+target.width/2, target.y+target.height/2);
                    }
                    if(serpent.hurt){
                        serpent.hurt = false;
                        if(r.nextInt(3)==0) mult = -mult;
                        velAngleChange(5d*mult);
                    }else if(Math.min(x, Main.WIDTH-x)+Math.min(y, Main.HEIGHT-y)<45) velAngleChange(7d*mult);
                    else velAngleChange(mult);
                }
            }else{
                if(serpent.dissolved){
                    if(Math.abs(velx)<speed){
                        velx *= r.nextDouble() * 1.5d * (double)(Math.abs(target.x-x))/Main.WIDTH;
                    }
                    if(Math.abs(vely)<speed){
                        vely *= r.nextDouble() * 1.5d * (double)(Math.abs(target.y-y))/Main.HEIGHT;
                    }
                    courseCorrection(target.x+target.width/2, target.y+target.height/2);
                }else{
                    if(hp<1) return;
                    int[] c = serpent.getCoords(num);
                    courseCorrection(c[0], c[1]);
                    if(speed<5 && Math.abs(x-c[0])+Math.abs(y-c[1])>=45) speed *= 4;
                    else if(speed>5 && Math.abs(x-c[0])+Math.abs(y-c[1])<45) speed /= 4;
                }
            }
        }
        
        protected void courseCorrection(double dx, double dy){
            if(dy<y&&vely>-speed){
                if(vely-courseCorrectionFactor<-speed){
                    vely = -speed;
                }else vely -= courseCorrectionFactor * (0.92d + 0.16d*r.nextDouble());
            }else if(dy>y&&vely<speed){
                if(vely+courseCorrectionFactor>speed){
                    vely = speed;
                }else vely += courseCorrectionFactor * (0.92d + 0.16d*r.nextDouble());
            }
            if(dx<x&&velx>-speed){
                if(velx-courseCorrectionFactor<-speed){
                    velx = -speed;
                }else velx -= courseCorrectionFactor * (0.92d + 0.16d*r.nextDouble());
            }else if(dx>x&&velx<speed){
                if(velx+courseCorrectionFactor>speed){
                    velx = speed;
                }else velx += courseCorrectionFactor * (0.92d + 0.16d*r.nextDouble());
            }
        }

        @Override
        public void render(Graphics ig, long frameNum){
            try{
                Graphics2D g = (Graphics2D) ig;
                int[] outerx = {x+24, x+48, x}, outery = {y, y+42, y+42},
                        innerx = {x+24, x+42, x+6}, innery = {y+6, y+36, y+36};
                if(num==0) g.setColor(new Color((int)(120d*x/Main.WIDTH)+50, (int)(120d*y/Main.HEIGHT)+50, 160));
                else g.setColor(new Color((int)(120d*y/Main.HEIGHT)+50, (int)(120d*x/Main.WIDTH)+50, 20));
                g.fillPolygon(outerx, outery, 3);
                g.setColor(Color.black);
                g.fillPolygon(innerx, innery, 3);
                int hpCol = (int)(hp*255d/maxhp);
                g.setColor(new Color(hpCol>255?255:(hpCol<0?0:hpCol), 0, 50));
                g.fillOval(x+19, y+16, 10, 10);
            }catch(IllegalArgumentException e){}
        }
        
        @Override
        public void die(Handler h){
            super.die(h);
            serpent.die(this);
            instance.hp -= maxhp;
        }
        
        @Override
        public void collision(GameObject ob){
            if(ob instanceof Hero){
                if(num==0) ob.hurt(damage * (1d + r.nextDouble()));
                else if(num==serpent.size()-1) ob.hurt(damage * 0.8d);
                else ob.hurt(damage);
            }else if(ob instanceof Bullet){
                if(ob.hp>0){
                    if(num==0) hurt(((Bullet) ob).damage * 0.8);
                    else if(num==serpent.size()-1) hurt(((Bullet) ob).damage * 0.6);
                    else hurt(((Bullet) ob).damage);
                }
                ob.hp = -1;
            }
        }
    
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){}
    
    @Override
    public void render(Graphics g, long fn){
        paintHealthBar(g);
    }
    
    @Override
    public void tick(Handler handler){
        if(hp<1){
            drop(handler);
            die(handler);
        }
    }
    
    @Override
    public boolean isColliding(Collision c){
        return false;
    }
    
    @Override
    public void drop(Handler hand){
        Usable u;
        switch(r.nextInt(4)){
            case 0: u = new Usable.HealingPotion(16); break;
            case 1: u = new Usable.Shield(16); break;
            case 2: u = new Usable.Hourglass(16); break;
            default: u = new Usable.DeathMissile(16); break;
        }
        u.x = x+width/2-u.width/2;
        u.y = y+height/2-u.height/2;
        hand.addObject(u);
    }
    
}

