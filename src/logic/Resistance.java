
package logic;

import entities.Hero.ShootingMode;

/**
 *
 * @author Adam Whittaker
 */
public class Resistance{
    
    public Resistance(ShootingMode m, double mu){
        mode = m;
        mult = mu;
    }
    
    public final ShootingMode mode;
    public final double mult;
    
}
