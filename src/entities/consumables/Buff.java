
package entities.consumables;

import entities.Consumable;
import entities.GameObject;
import entities.Hero;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Buff extends Consumable{
    
    protected long time;
    protected final double lvl;
    public String buffSound = "buff.wav"; 
    
    public Buff(String na, int w, int h, long t, int l){
        super(na, w, h);
        time = t;
        lvl = l;
    }
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero && hp!=-1){
            ((Hero) ob).addBuff(this);
            hp = -1;
        }
    }

    public void startTime(){
        time += System.currentTimeMillis();
    }
    
    public boolean isOver(){
        return System.currentTimeMillis()>time;
    }

    public abstract void start(Hero h);

    public abstract void end(Hero h);
    
    public static class SpdBuff extends Buff{
        
        public SpdBuff(int l, long t){
            super("Speed x"+(1.0+0.2*l), 12, 12, t, l);
        }

        @Override
        public void start(Hero h){
            h.multSpeed(1 + lvl*0.2);
        }

        @Override
        public void end(Hero h){
            h.divSpeed(1 + lvl*0.2);
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color(255, (int)(frameNum%50)+175, 0));
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class DmgBuff extends Buff{
        
        public DmgBuff(int l, long t){
            super("Damage x"+(1.0+0.2*l), 12, 12, t, l);
        }

        @Override
        public void start(Hero h){
            h.multDmgMultiplier(1f + (float)(0.2f*lvl));
        }

        @Override
        public void end(Hero h){
            h.divDmgMultiplier(1f + (float)(0.2f*lvl));
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color((int)(frameNum%70)+160, 20, (int)(frameNum%50)+180));
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class ImmBuff extends Buff{
        
        private final long ti;
        
        public ImmBuff(int l, long t){
            super("Immunity", 12, 12, t, l);
            ti = t;
        }

        @Override
        public void start(Hero h){
            h.boostInvulnerability(0.002D*(double)ti);
        }

        @Override
        public void end(Hero h){}

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color((int)(75*lvl), 10, (int)(frameNum%248)));
            g.fillRect(x, y, width, height);
            g.setColor(new Color((int)(75*lvl), (int)(50*lvl), 30));
            g.fillRect(x+2, y+2, width-4, height-4);
        }
        
    }
    
    public static class ShdBuff extends Buff{
        
        public ShdBuff(int l, long t){
            super("Shield "+l, 12, 12, t, l);
        }

        @Override
        public void start(Hero h){
            h.boostAbsorption(0.25*(double)lvl);
        }

        @Override
        public void end(Hero h){
            h.boostAbsorption(-0.25*(double)lvl);
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color(0, (int)(frameNum%50)+20, 255));
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class HpBuff extends Buff{
        
        public HpBuff(int lv){
            super("HpBuff", 12, 12, 1, lv);
        }

        @Override
        public void start(Hero h){
            h.boostHp(lvl*7);
        }

        @Override
        public void end(Hero h){}

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color((int)(lvl*75), (int)(frameNum%20), 0));
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class RegBuff extends Buff{
        
        public RegBuff(int lv, long t){
            super("Regeneration++", 12, 12, t, lv);
        }

        @Override
        public void start(Hero h){
            h.boostRegen(lvl*0.01);
        }

        @Override
        public void end(Hero h){
            h.boostRegen(-lvl*0.01);
        }

        @Override
        public void render(Graphics g, long frameNum){
            g.setColor(new Color((int)(lvl*80), (int)(frameNum%40), 40));
            g.fillRect(x, y, width, height);
        }
        
    }
    
    public static class HpUpgrade extends Buff{
        
        public HpUpgrade(int lv){
            super("HpUpgrade", 12, 12, 1, lv);
            buffSound = "upgrade.wav";
        }

        @Override
        public void start(Hero h){
            h.boostMaxHp(lvl*3);
        }

        @Override
        public void end(Hero h){}

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D) ig;
            g.setColor(new Color((int)(lvl*75), (int)(frameNum%230), 60));
            g.fill(AffineTransform.getRotateInstance(Math.PI/4D, x+width/2, y+height/2).createTransformedShape(new Rectangle(x, y, width, height)));
        }
        
    }
    
    public static class RegUpgrade extends Buff{
        
        public RegUpgrade(int lv){
            super("RegUpgrade", 12, 12, 1, lv);
            buffSound = "upgrade.wav";
        }

        @Override
        public void start(Hero h){
            h.boostRegen(lvl*0.005);
        }

        @Override
        public void end(Hero h){}

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D) ig;
            g.setColor(new Color((int)(lvl*80), (int)(frameNum%80), 40));
            g.fill(AffineTransform.getRotateInstance(Math.PI/4D, x+width/2, y+height/2).createTransformedShape(new Rectangle(x, y, width, height)));
        }
        
    }
    
    public static class LvlUpgrade extends Buff{
        
        public LvlUpgrade(){
            super("LvlUpgrade", 12, 12, 1, 1);
            buffSound = "levelUp.wav";
        }

        @Override
        public void start(Hero h){
            h.tryLevelUp(h.maxxp);
        }

        @Override
        public void end(Hero h){}

        @Override
        public void render(Graphics ig, long frameNum){
            Graphics2D g = (Graphics2D) ig;
            g.setColor(new Color(240, 100+(int)(frameNum%130), 25));
            g.fill(AffineTransform.getRotateInstance(Math.PI/4D, x+width/2, y+height/2).createTransformedShape(new Rectangle(x, y, width, height)));
        }
        
    }
    
}
