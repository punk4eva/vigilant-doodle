
package entities.bosses;

import entities.Enemy;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public class Boss extends Enemy{

    private final String bgp;
    private final BossPhase[] phases;
    private int phaseNum = 0;
    private final static int w = Main.WIDTH/3, sy = Main.HEIGHT*6/7;
    
    public Boss(String na, double health, int x, String bgp_, BossPhase... ph){
        super(na, health, ph[0].damage, ph[0].width, ph[0].height, x, ph[0].speed);
        bgp = bgp_;
        phases = ph;
    }

    @Override
    public void render(Graphics g, long frameNum){
        phases[phaseNum].render(g, frameNum);
        paintHealthBar(g);
    }
    
    @Override
    public void tick(Handler handler){
        if(phaseNum==phases.length-1){
            if(hp<1) die(handler);
        }else if(hp<=phases[phaseNum+1].triggerHealth){
            phaseNum++;
            width = phases[phaseNum].width;
            height = phases[phaseNum].height;
            y = phases[phaseNum].y;
            x = phases[phaseNum].x;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){
        phases[phaseNum].actionPerformed(ae);
        x = phases[phaseNum].x;
        y = phases[phaseNum].y;
    }
    
    protected abstract class BossPhase extends Enemy{

        private final double triggerHealth;
        
        public BossPhase(double th, double dam, int w, int h, double sp){
            super(null, 1, dam, w, h, 0, sp);
            triggerHealth = th;
        }
        
    }
    
    private void paintHealthBar(Graphics g){
        g.setColor(Color.YELLOW);
        g.drawString(name, w+2, sy-20);
        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(w, sy, w, 12, false);
        g.setColor(Color.RED);
        g.fill3DRect(w+1, sy+1, (int)(hp/maxhp*(w-2D)), 10, true);
        g.setColor(Color.GRAY);
        for(int n=1;n<phases.length;n++) g.fill3DRect((int)((phases[n].triggerHealth/maxhp)*w+w), sy-2, 3, 16, true);
    }
    
    public void playTheme(){
        Main.soundSystem.playAbruptLoop(bgp);
    }
    
}
