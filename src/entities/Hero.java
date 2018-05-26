
package entities;

import entities.HomingBullet.CooldownHomingBullet;
import entities.consumables.Buff;
import entities.consumables.Usable;
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
import java.util.stream.Stream;
import logic.ConstantFields;
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
    public double speed = 3.0, defenseBonus = 1.0;
    public boolean slow = false;
    public final double MAXSPEED = 4.5;
    private final double IMMUNITYLENGTH;
    private boolean shooting = false;
    public int xp = 0, maxxp = 5;
    public double regen = 0.01;
    public double invulnerability = 0;
    private MeleeHero melee = null;
    public ShootingMode shootingMode = ShootingMode.CONSTANT;
    private ShootingMode modes[] = ShootingMode.values();
    private int shootingModeIndex = 2;
    private double reloadStatus = 10.0;
    private double weaponHeat = 0;
    public double damageAbsorption = 0;
    public float damageMult = 1;
    public double coolingMult = 1;
    private LinkedList<Integer> currentKeys = new LinkedList<>();
    private double aimx=-1, aimy=-1;
    private final List<Buff> buffs = new LinkedList<>();
    private final List<Usable> usables = new LinkedList<>();
    public static enum ShootingMode{
        BURST(new Bullet(8.2, 0.8, 0.5, 1.8, 0.2, null)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler, float m){
                for(int n=0;n<bulletAmount*2;n+=2) handler.addObject(bullet.create((int)(sx+vx*n), (int)(sy+vy*n), vx, vy, m));
            }
        },
        SHOTGUN(new Bullet(7, 4, 0.25, 2.5, 0.18, null)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler, float m){
                for(int n=-bulletAmount/2;n<bulletAmount/2+1;n++) handler.addObject(bullet.create(sx, sy, vx, vy, m));
            }            
        },
        CONSTANT(new Bullet(7, 1, 1, 0.2, 0.2, null)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler, float m){
                handler.addObject(bullet.create(sx, sy, vx, vy, m));
            }
        },
        MACHINE(new Bullet(14, 0.7, 2, 0.4, 0.2, null)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler, float m){
                handler.addObject(bullet.create(sx, sy, vx, vy, m));
            }
        },
        MISSILE(new CooldownHomingBullet(2, 40, 0.125, 6.0, 0.15, null)){
            @Override
            void shoot(int sx, int sy, double vx, double vy, Handler handler, float m){
                handler.addObject(((HomingBullet)bullet).create(sx, sy, vx, vy, m, findNearestEnemy(handler, handler.hero)));
            }
        };
        static{
            BURST.bullet.mode = BURST;
            SHOTGUN.bullet.mode = SHOTGUN;
            CONSTANT.bullet.mode = CONSTANT;
            MACHINE.bullet.mode = MACHINE;
            MISSILE.bullet.mode = MISSILE;
        }
        
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
            Main.soundSystem.playSFX("upgrade.wav");
        }
        
        void shoot(int sx, int sy, double vx, double vy, Handler ha, float mult){
            throw new IllegalStateException("Unoverriden method!");
        }
        
        public Enemy findNearestEnemy(Handler handler, Hero h){
            Stream<GameObject> stream = handler.getStream();
            synchronized(stream){
                List l = stream.filter(go -> go instanceof Enemy).sorted((a, b) -> {
                    int da = Math.abs(a.x+a.y-h.x-h.y), db = Math.abs(b.x+b.y-h.x-h.y);
                    if(da==db) return 0;
                    if(da>db) return 1;
                    return -1;
                }).collect(Collectors.toList());
                return l.isEmpty() ? null : (Enemy)l.get(0);
            }
        }
        
    }

    public Hero(Main main){
        super("Hero", 30, 48, 48);
        main.addKeyListener(this);
        main.addMouseListener(this);
        main.addMouseMotionListener(this);
        main.addMouseWheelListener(this);
        main.handler = new Handler(this);
        main.decider = new Decider(main);
        main.window = new Window(Main.WIDTH, Main.HEIGHT, "Supiru", main);
        switch(main.getDifficulty()){
            case "Easy":
                regen = 0.025;
                IMMUNITYLENGTH = 0.8;
                damageMult = 1.1f;
                damageAbsorption = 0.3;
                defenseBonus = 0.85;
                break;
            case "Normal":
                regen = 0.015;
                IMMUNITYLENGTH = 0.7;
                damageAbsorption = 0.15;
                defenseBonus = 0.88;
                break;
            case "Hard":
                regen = 0.01;
                IMMUNITYLENGTH = 0.6;
                defenseBonus = 0.94;
                break;
            case "Brutal": default:
                regen = 0.008;
                IMMUNITYLENGTH = 0.5;
                break;
        }
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
            if(speed>MAXSPEED) speed = MAXSPEED;
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
        if(melee==null){
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(getHealthColor());
            g2d.fillRect(x, y, 48, 48);
            g2d.setColor(Color.black);
            g2d.fillRect(x+10, y+10, 28, 28);
            g2d.setColor(getHeatColor());
            Rectangle rect = new Rectangle(x+18, y+18, 12, 12);
            AffineTransform at = new AffineTransform();
            at.rotate(Math.PI/4, rect.x+rect.width/2, rect.y+rect.height/2);
            g2d.fill(at.createTransformedShape(rect));
        }else melee.render(g, frameNum, x, y);
    }
    
    public void drawMessages(Graphics g){
        g.setColor(ConstantFields.buffColor);
        g.drawString(shootingMode.name() + (shootingMode.level==0 ? "" : " +" + shootingMode.level), 8, 18);
        int n = 38;
        synchronized(buffs){ for(Buff b : buffs){
            g.drawString(b.name, 8, n);
            n+=20;
        }}
        g.setColor(ConstantFields.itemColor);
        n = Main.HEIGHT-58;
        synchronized(usables){ for(Usable u : usables){
            g.drawString(u.name, 8, n);
            n-=20;
        }}
    }
    
    private Color getHealthColor(){
        return Color.getHSBColor(shooting?0.11F:0.0F, 0.9F, (float)(hp/maxhp));
    }
    
    private Color getHeatColor(){
        return Color.getHSBColor((float)(0.5+weaponHeat)/10F, 0.9F, 0.8F);
    }
    
    @Override
    public void collision(GameObject ob){
        if(melee!=null) melee.collision(ob);
    }
    
    void shoot(Handler handler){
        reloadStatus = 0;
        weaponHeat += shootingMode.bullet.bulletHeat;
        int cx = x+width/2, cy = y+height/2;
        double vx, vy;
        int sx, sy;
        double gradient = Math.abs((aimy-(double)cy)/(aimx-(double)cx));
        if(aimx>cx){
            if(aimy<cy){ //1st Quartile
                if(gradient<1.0){
                    sx = cx+36;
                    sy = cy-(int)(gradient*36d);
                    vx = shootingMode.bullet.bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy-36;
                    sx = cx+(int)(36d/gradient);
                    vy = -shootingMode.bullet.bulletSpeed;
                    vx = vy/-gradient;
                }
            }else{ //2nd Quartile
                if(gradient<1.0){
                    sx = cx+36;
                    sy = cy+(int)(gradient*36d);
                    vx = shootingMode.bullet.bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy+36;
                    sx = cx+(int)(36d/gradient);
                    vy = shootingMode.bullet.bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }else{
            if(aimy>cy){ //3rd Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy+(int)(gradient*36d);
                    vx = -shootingMode.bullet.bulletSpeed;
                    vy = vx*-gradient;
                }else{
                    sy = cy+36;
                    sx = cx-(int)(36d/gradient);
                    vy = shootingMode.bullet.bulletSpeed;
                    vx = vy/-gradient;
                }
            }else{ //4th Quartile
                if(gradient<1.0){
                    sx = cx-36;
                    sy = cy-(int)(gradient*36d);
                    vx = -shootingMode.bullet.bulletSpeed;
                    vy = vx*gradient;
                }else{
                    sy = cy-36;
                    sx = cx-(int)(36d/gradient);
                    vy = -shootingMode.bullet.bulletSpeed;
                    vx = vy/gradient;
                }
            }
        }
        createBullet(sx, sy, vx, vy, handler);
    }
    
    public void createBullet(int sx, int sy, double vx, double vy, Handler handler){
        shootingMode.shoot(sx, sy, vx, vy, handler, damageMult);
        Main.soundSystem.playSFX(shootingMode.toString().toLowerCase()+".wav");
    }
    
    @Override
    public synchronized void actionPerformed(ActionEvent ae){
        super.actionPerformed(ae);
        tickWeapon();
        if(hp<maxhp) hp+=regen;
        if(invulnerability>0) invulnerability -= 0.01;
    }
    
    @Override
    public void hurt(double dam){
        if(invulnerability<=0){
            if(shooting) hp -= dam*(1-damageAbsorption);
            else hp -= dam*(1-damageAbsorption)*defenseBonus;
            invulnerability = IMMUNITYLENGTH;
        }
    }
    
    public void addBuff(Buff b){
        if(!(isOnFire()&&b.name.startsWith("On Fire"))&&!(isSlow()&&b.name.startsWith("Speed x0")))synchronized(buffs){
            buffs.add(b);
            b.startTime();
            b.start(this);
            if(b.buffSound!=null) Main.soundSystem.playSFX(b.buffSound);
        }
    }
    
    public void consume(Consumable c){}
    
    public void removeBuff(Buff b){
        synchronized(buffs){
            buffs.remove(b);
            b.end(this);
        }
    }
    
    public boolean isOnFire(){
        synchronized(buffs){
            return buffs.stream().anyMatch(b -> b.name.startsWith("On Fire"));
        }
    }
    
    public boolean isSlow(){
        synchronized(buffs){
            return buffs.stream().anyMatch(b -> b.name.startsWith("Speed x0"));
        }
    }
    
    private void checkBuffs(){
        synchronized(buffs){
            for(Iterator<Buff> iter=buffs.iterator();iter.hasNext();){
                Buff b = iter.next();
                if(b.isOver()){
                    iter.remove();
                    b.end(this);
                }
            }
        }
    }
    
    public void boostAbsorption(double a){
        damageAbsorption += a;
        if(damageAbsorption>1) damageAbsorption = 1;
        else if(damageAbsorption<0) damageAbsorption = 0;
    }
    
    public void boostInvulnerability(double i){
        invulnerability += i;
    }
    
    public void boostSpeed(double sp){
        speed += sp;
    }
    
    public void addUsable(Usable u){
        usables.add(u);
    }
    
    public void useUsable(Handler ha){
        if(!usables.isEmpty()) usables.remove(0).use(ha, this, (int)aimx, (int)aimy);
    }
    
    public void multSpeed(double sp){
        speed *= sp;
    }
    
    public void divSpeed(double sp){
        speed /= sp;
    }
    
    public void boostHp(double h){
        hp += h;
        if(hp>maxhp) hp = maxhp;
    }
    
    public void boostMaxHp(double h){
        maxhp += h;
        hp += h;
        if(hp>maxhp) hp = maxhp;
    }
    
    public void boostRegen(double r){
        regen += r;
    }
    
    public void setRegen(double r){regen = r;}
    
    public void setCoolingMult(double m){
        coolingMult = m;
    }
    
    public void multDmgMultiplier(float m){
        damageMult *= m;
    }
    
    public void divDmgMultiplier(float m){
        damageMult /= m;
    }
    
    public void setMeleeMode(MeleeHero f){
        melee = f;
    }
    
    @Override
    protected void boundsCheck(Handler h){
        if(melee!=null){
            if(melee.boundsCheck(x, y, this)) melee = null;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me){
        if(ConstantFields.pointAndTeleport){
            x=me.getX()-width/2;
            y=me.getY()-height/2;
        }
    }
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
    public void keyTyped(KeyEvent ke){
        switch(ke.getKeyChar()){
            case 'f': useUsable(Window.main.handler);
                break;
            case 'm': Window.mute();
                break;
            case 'b': if(Window.main.handler.getClockSpeed()==5)
                    Window.main.handler.setClockSpeed(20);
                else Window.main.handler.setClockSpeed(5);
                break;
            case 'v': Main.decider.actionPerformed(null);
                break;
        }
    }
    @Override
    public void keyPressed(KeyEvent ke){
        if(ke.getKeyCode()==ConstantFields.PAUSE) Window.main.pause();
        else if(!currentKeys.contains(ke.getKeyCode())){
            currentKeys.add(ke.getKeyCode());
            if(ke.getKeyCode()==ConstantFields.SHOOT) shooting = true;
        }
    }
    @Override
    public void keyReleased(KeyEvent ke){
        currentKeys.remove(new Integer(ke.getKeyCode()));
        if(ke.getKeyCode()==ConstantFields.SHOOT) shooting = false;
    }
    
    private void tickKeys(Handler handler){
        boolean w = currentKeys.contains(ConstantFields.UP);
        boolean a = currentKeys.contains(ConstantFields.LEFT);
        boolean s = currentKeys.contains(ConstantFields.DOWN);
        boolean d = currentKeys.contains(ConstantFields.RIGHT);
        if(melee==null){
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
        }else{
            melee.track(aimx, aimy, x, y);
            velx = melee.velx;
            vely = melee.vely;
        }
        if(currentKeys.contains(ConstantFields.SHOOT)){
            if(melee!=null) melee.push(handler);
            else if(reloadStatus>=10&&weaponHeat<=0) shoot(handler);
        }
    }
    
    private void tickWeapon(){
        if(reloadStatus<10) reloadStatus+=shootingMode.bullet.reloadSpeed;
        if(weaponHeat>0) weaponHeat-=shootingMode.bullet.cooldownSpeed*coolingMult;
    }
      
}
