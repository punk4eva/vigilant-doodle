
package entities.bosses;

import entities.GameObject;
import java.awt.Graphics;
import yoisupiru.Handler;

/**
 *
 * @author Adam Whittaker
 */
public class TheIncinerator extends Boss{
    
    public TheIncinerator(Handler h, GameObject targ, int hp1, int hp2){
        super("The Incinerator", hp1+hp2, 50, "The_Incinerator.wav");
    }
    
    private static class Phase1 extends BossPhase{

        public Phase1(double th, double dam, int w, int h, double sp){
            super(th, dam, w, h, sp);
        }

        @Override
        public void render(Graphics g, long frameNum){
            throw new UnsupportedOperationException("Not supported yet.");
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
    
}
