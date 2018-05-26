
package logic;

import entities.GameObject;
import java.util.List;

/**
 *
 * @author Adam Whittaker
 */
public interface Twin extends NonCollidable{
    public List<GameObject> getTwinObjects();
}
