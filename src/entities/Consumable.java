
package entities;

import java.awt.event.ActionEvent;
import logic.NonCollidable;
import yoisupiru.Decider;
import yoisupiru.Handler;
import yoisupiru.Main;

/**
 *
 * @author Adam Whittaker
 */
public abstract class Consumable extends GameObject implements NonCollidable{
    
    public boolean forced = false;
    
    public Consumable(String na, int w, int h){
        super(na, 1, w, h);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){}
    
    @Override
    public void collision(GameObject ob){
        if(ob instanceof Hero){
            ((Hero) ob).consume(this);
            hp = -1;
        }
    }
    
    public void spawn(Handler handler){
        x = Decider.r.nextInt(Main.WIDTH-width);
        y = Decider.r.nextInt(Main.HEIGHT-height);
        handler.addObject(this);
    }
    
}
