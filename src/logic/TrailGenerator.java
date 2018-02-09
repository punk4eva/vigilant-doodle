
package logic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import static logic.ConstantFields.graphicsQuality;

/**
 *
 * @author Adam
 */
public class TrailGenerator{

    private final int width, height;
    private int R, G, B, tick;
    public int intensity, capacity;
    public float fadespeed;

    private ArrayList<Trail> trail = new ArrayList<>();

    public void paint(Graphics2D g, int x, int y){
        tick++;
        if(tick >= intensity){
            tick = 0;
            if(trail.size() < capacity){
                trail.add(new Trail(fadespeed, x, y, width, height, R, G, B));
            }
        }
        trail.removeIf(p -> p.expired);
        trail.stream().forEach(p -> p.paint(g));
    }

    public TrailGenerator(float f, int... v){
        intensity = v[0]<4-graphicsQuality?4-(int)graphicsQuality:v[0];
        capacity = v[1]>10d*graphicsQuality?(int)(10d*graphicsQuality):v[1];
        width = v[2];
        height = v[3];
        R = v[4];
        G = v[5];
        B = v[6];
        fadespeed = f;
    }
    
    public void setColor(Color c){
        R = c.getRed();
        G = c.getGreen();
        B = c.getBlue();
    }

    private class Trail{

        private final int x, y, width, height, fadespeed;
        private int R, G, B, alpha = 255;
        private final float fadedelta;
        private float delta;
        private boolean expired = false;

        Trail(float f, int... v){
            x = v[0];
            y = v[1];
            width = v[2];
            height = v[3];
            R = v[4];
            G = v[5];
            B = v[6];
            fadespeed = (int) Math.floor(f);
            fadedelta = f - (float)fadespeed;
        }

        int decrementAlpha(){
            alpha-=fadespeed;
            delta+=fadedelta;
            if(delta>=1){
                alpha--;
                delta--;
            }
            return alpha;
        }

        void paint(Graphics2D g){
            decrementAlpha();
            if(alpha > 0){
                g.setColor(new Color(R, G, B, alpha));
                g.fillRect(x, y, width, height);
            }else{
                expired = true;
            }
        }

    }

}
