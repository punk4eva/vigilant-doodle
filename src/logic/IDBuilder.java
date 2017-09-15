
package logic;

/**
 *
 * @author Adam Whittaker
 */
public class IDBuilder{
    
    private static int ID = 0;
    
    public static int genID(){
        return ID++;
    }
    
}
