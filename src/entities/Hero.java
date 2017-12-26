
package entities;

import entities.consumables.Buff;
import entities.consumables.WeaponUpgrade;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import logic.KeyBindings;
import yoisupiru.Decider;
import yoisupiru.Handler;
import yoisupiru.Main;
import yoisupiru.Window;

/**
 *
 * @author Adam Whittaker
 */
public class Hero extends GameObject implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{
    
    public int level = 1;
    public double speed = 3.0;
    public int xp = 0, maxxp = 5;
    private double regen = 0.01;
    private double invulnerability = 0;
    private boolean meleeMode = false;
    public ShootingMode shootingMode = ShootingMode.CONSTANT;
    private ShootingMode modes[] = ShootingMode.values();
    private int shootingModeIndex = 2;
    private double reloadStatus = 10.0;
    private double weaponHeat = 0;
    private LinkedList<Integer> currentKeys = new LinkedList<>();
    private int aimx=-1, aimy=-1;
    private final List<Buff> buffs = new LinkedList<>();
    public static enum ShootingMode{
        BURST(new Bullet(8.2, 0.8, 0.5, 1.8, 0.2)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler){
                for(int n=0;n<10;n+=2) handler.addObject(bullet.create((int)(sx+vx*n), (int)(sy+vy*n), vx, vy));
            }
        },
        SHOTGUN(new Bullet(7, 4, 0.25, 2.5, 0.18)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler){
                for(int n=-bulletAmount/2;n<bulletAmount/2+1;n++) handler.addObject(bullet.create(sx, sy, vx, vy));
            }            
        },
        CONSTANT(new Bullet(7, 1, 1, 0.2, 0.2)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler){
                handler.addObject(bullet.create(sx, sy, vx, vy));
            }
        },
        MACHINE(new Bullet(14, 0.7, 2, 0.4, 0.2)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler){
                handler.addObject(bullet.create(sx, sy, vx, vy));
            }
        },
        GRENADE(new HomingBullet(2, 40, 0.125, 6.0, 0.15, null)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler){
                handler.addObject(((HomingBullet)bullet).create(sx, sy, vx, vy, findNearestEnemy(handler)));
            }
        };
        
        public final Bullet bullet;
        private int level = 0;
        protected int bulletAmount = 5;
        ShootingMode(Bullet b){
            bullet = b;
        }
        
        public static void upgrade(WeaponUpgrade u){
            u.gun.level++;
            u.gun.bullet.upgrade(u.imprint);
            u.gun.bulletAmount += u.amount;
        }
        
        void shoot(int sx, int sy, double vx, double vy, Handler ha){
            throw new IllegalStateException("Unoverriden method!");
        }
        
        public Enemy findNearestEnemy(Handler handler){
            synchronized(handler.objects){
                List l = handler.objects.stream().filter(go -> go instanceof Enemy).sorted((a, b) -> {
                    int da = a.x+a.y, db = b.x+b.y;
                    if(da==db) return 0;
                    if(da>db) return 1;
                    return -1;
                }).collect(Collectors.toList());
                return l.isEmpty() ? null : (Enemy)l.get(0);
            }
        }
        
    }

    public Hero(Main main){
        super("Hero", 20, 48, 48);
        main.addKeyListener(this);
        main.addMouseListener(this);
        main.addMouseMotionListener(this);
        main.addMouseWheelListener(this);
        main.handler = new Handler(this);
        main.decider = new Decider(main);
        main.window = new Window(Main.WIDTH, Main.HEIGHT, "Supiru", main);
    }
    
    public void tryLevelUp(int exp){
        xp += exp;
        while(xp>=maxxp){
            xp-=maxxp;
            maxxp+=5;
            regen += 0.01;
            level++;
            maxhp+=8;
            hp+=8;
            speed *= 1.05;
        }
    }

    @Override
    public void tick(Handler handler){
        tickKeys(handler);
        checkBuffs();
        super.tick(handler);
    }

    @Override
    public void render(Graphics g, long frameNum){
        Graphics2D g2d = (Graphics2D)g;
        drawWeapon(g2d);
        g2d.setColor(getHealthColor());
        g2d.fillRect(x, y, 48, 48);
        g2d.setColor(Color.black);
        g2d.fillRect(x+10, y+10, 28, 28);
        g2d.setColor(getHeatColor());
        Rectangle rect = new Rectangle(x+18, y+18, 12, 12);
        AffineTransform at = new AffineTransform();
        at.rotate(Math.PI/4, rect.x+rect.width/2, rect.y+rect.height/2);
        g2d.fill(at.createTransformedShape(rect));
    }
    
    public void drawWeapon(Graphics g){
        g.setColor(Color.yellow);
        g.drawString(shootingMode.name() + (shootingMode.level==0 ? "" : " +" + shootingMode.level), 8, 18);
    }
    
    private Color getHealthColor(){
        return Color.getHSBColor(0, 0.9F, (float)(hp/maxhp));
    }
    
    private Color getHeatColor(){
        return Color.getHSBColor((float)(0.5+weaponHeat)/10F, 0.9F, 0.8F);
    }
    
    @Override
    public void collision(GameObject ob){
        
    }
    
    void shoot(Handler handler){
        reloadStatus = 0;
        weaponHeat += shootingMode.bullet.bulletHeat;
        int cx = x+width/2, cy = y+height/2;
        double vx, vy;
        int sx, sy ;
        double gradient = Math.abs(((double)aimy-cy)/(aimx-cx));
        if(aimx>cx){
            if(aimy<cy){ //1st Quartile
                if(gradient<1.0){
                    sx = cx+36;
                    sy = cy-(int)(gradient*36);
                    vx = shootingMode.bullet.bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy-36;
                    sx = cx+(int)(36/gradient);
                    vy = -shootingMode.bullet.bulletSpeed;
                    vx = vy/-gradient;
                }
            }else{ //2nd Quartile
                if(gradient<1.0){
                    sx = cx+36;
                    sy = cy+(int)(gradient*36);
                    vx = shootingMode.bullet.bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy+36;
                    sx = cx+(int)(36/gradient);
                    vy = shootingMode.bullet.bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }else{
            if(aimy>cy){ //3rd Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy+(int)(gradient*36);
                    vx = -shootingMode.bullet.bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy+36;
                    sx = cx-(int)(36/gradient);
                    vy = shootingMode.bullet.bulletSpeed;
                    vx = vy/-gradient;
                }
            }else{ //4th Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy-(int)(gradient*36);
                    vx = -shootingMode.bullet.bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy-36;
                    sx = cx-(int)(36/gradient);
                    vy = -shootingMode.bullet.bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }
        createBullet(sx, sy, vx, vy, handler);
    }
    
    public void createBullet(int sx, int sy, double vx, double vy, Handler handler){
        shootingMode.shoot(sx, sy, vx, vy, handler);
        Main.soundSystem.playSFX(shootingMode.toString().toLowerCase()+".wav");
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        tickWeapon();
        if(hp<maxhp) hp+=regen;
        if(invulnerability>0) invulnerability -= 0.01;
    }
    
    public void hurt(double dam){
        if(invulnerability<=0) hp -= dam;
        invulnerability = 1;
    }
    
    public void consume(Consumable c){
        
    }
    
    public void addBuff(Buff b){
        synchronized(buffs){
            buffs.add(b);
            b.startTime();
            b.start();
        }
    }
    
    public void removeBuff(Buff b){
        synchronized(buffs){
            buffs.remove(b);
            b.end();
        }
    }
    
    private void checkBuffs(){
        synchronized(buffs){
            for(Iterator<Buff> iter=buffs.iterator();iter.hasNext();){
                Buff b = iter.next();
                if(b.isOver()){
                    iter.remove();
                    b.end();
                }
            }
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me){x=me.getX()-width/2;y=me.getY()-height/2;}
    @Override
    public void mousePressed(MouseEvent me){}
    @Override
    public void mouseReleased(MouseEvent me){}
    @Override
    public void mouseEntered(MouseEvent me){}
    @Override
    public void mouseExited(MouseEvent me){}
    @Override
    public void mouseDragged(MouseEvent me){}
    @Override
    public void mouseMoved(MouseEvent me){
        aimx = me.getX();
        aimy = me.getY();
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe){
        switch(mwe.getWheelRotation()){
            case 1: shootingModeIndex = (shootingModeIndex+1)%modes.length;
                break;
            default: shootingModeIndex = (shootingModeIndex-1)%modes.length;
                if(shootingModeIndex==-1) shootingModeIndex = modes.length-1;
        }
        shootingMode = modes[shootingModeIndex];   
    }
    @Override
    public void keyTyped(KeyEvent ke){}
    @Override
    public void keyPressed(KeyEvent ke){
        if(ke.getKeyCode()==KeyBindings.PAUSE) Window.main.pause();
        else if(!currentKeys.contains(ke.getKeyCode())) currentKeys.add(ke.getKeyCode());
    }
    @Override
    public void keyReleased(KeyEvent ke){
        currentKeys.remove(new Integer(ke.getKeyCode()));
    }
    
    private void tickKeys(Handler handler){
        boolean w = currentKeys.contains(KeyBindings.UP);
        boolean a = currentKeys.contains(KeyBindings.LEFT);
        boolean s = currentKeys.contains(KeyBindings.DOWN);
        boolean d = currentKeys.contains(KeyBindings.RIGHT);
        if(w&&!s&&y>0){
            vely = -speed;
        }else if(s&&!w&&y+height<Main.HEIGHT-24){
            vely = speed;
        }else vely = 0;
        if(a&&!d&&x>0){
            velx = -speed;
        }else if(d&&!a&&x+width<Main.WIDTH){
            velx = speed;
        }else velx = 0;
        if(currentKeys.contains(KeyBindings.SHOOT)&&reloadStatus>=10&&weaponHeat<=0){
            shoot(handler);
        }
    }
    
    private void tickWeapon(){
        if(reloadStatus<10) reloadStatus+=shootingMode.bullet.reloadSpeed;
        if(weaponHeat>0) weaponHeat-=shootingMode.bullet.cooldownSpeed;
    }
    
    
       
}
