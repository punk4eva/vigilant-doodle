
package logic;

/**
 *
 * @author Adam Whittaker
 */
public class TimeTester{
    
    private static long then;
    private static long now;
    
    public static void then(){
        then = System.currentTimeMillis();
    }
    
    public static void now(){
        now = System.currentTimeMillis();
        System.out.println((now - then) + " milliseconds");
    }
    
}
