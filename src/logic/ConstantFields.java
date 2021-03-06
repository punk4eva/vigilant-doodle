
package logic;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Adam Whittaker
 */
public class ConstantFields{
    
    public static double graphicsQuality;
    
    public static int UP = KeyEvent.VK_W;
    public static int DOWN = KeyEvent.VK_S;
    public static int LEFT = KeyEvent.VK_A;
    public static int RIGHT = KeyEvent.VK_D;
    public static int SHOOT = KeyEvent.VK_SPACE;
    public static int PAUSE = KeyEvent.VK_ESCAPE;
    public static int VOLUME_UP = KeyEvent.VK_O;
    public static int VOLUME_DOWN = KeyEvent.VK_L;
    public static int USE_ITEM = KeyEvent.VK_F;
    
    public static Color buffColor;
    public static Color itemColor;
    
    public static int hp11;
    public static int hp12;
    public static int hp13;
    public static int hp21;
    public static int hp22;
    public static int hp31;
    public static int hp32;
    public static int hp33;
    public static int hp34;
    public static int hp41;
    
    public static int scavengerSegments;
    public static boolean printFields;
    public static boolean pointAndTeleport;
    public static int minSpawnDelay, spawnDelay;
    public static double courseCorrectionFactor;
    public static int mobCap, consCap;
    public static double meleeAccFactor, meleeRotationFactor;
    
    static{
        try(BufferedReader r = new BufferedReader(new FileReader(new File("src/config.txt")))){
            String line;
            while((line = r.readLine()) != null){
                if(line.startsWith("buffColor")) buffColor = new Color((int)Long.parseLong(line.substring(12), 16));
                else if(line.startsWith("itemColor")) itemColor = new Color((int)Long.parseLong(line.substring(12), 16));
                else if(line.startsWith("printFields")) printFields = Boolean.parseBoolean(line.substring(14));
                else if(line.startsWith("hp11")) hp11 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp12")) hp12 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp13")) hp13 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp21")) hp21 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp22")) hp22 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp31")) hp31 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp32")) hp32 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp33")) hp33 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp34")) hp34 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("hp41")) hp41 = Integer.parseInt(line.substring(7));
                else if(line.startsWith("mobCap")) mobCap = Integer.parseInt(line.substring(9));
                else if(line.startsWith("consCap")) consCap = Integer.parseInt(line.substring(10));
                else if(line.startsWith("graphicsQuality")) graphicsQuality = Double.parseDouble(line.substring(18));
                else if(line.startsWith("scavengerSegments")) scavengerSegments = Integer.parseInt(line.substring(20));
                else if(line.startsWith("meleeAccFactor")) meleeAccFactor = Double.parseDouble(line.substring(17));
                else if(line.startsWith("meleeRotationFactor")) meleeRotationFactor = Double.parseDouble(line.substring(22));
                else if(line.startsWith("pointAndTeleport")) pointAndTeleport = Boolean.parseBoolean(line.substring(19));
                else if(line.startsWith("courseCorrectionFactor")) courseCorrectionFactor = Double.parseDouble(line.substring(25));
                else if(line.startsWith("minSpawnDelay")) minSpawnDelay = Integer.parseInt(line.substring(16));
                else if(line.startsWith("spawnDelay")) spawnDelay = Integer.parseInt(line.substring(13));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        if(graphicsQuality>3) graphicsQuality = 3;
        else if(graphicsQuality<0) graphicsQuality = 1;
        if(printFields){
            System.out.println("graphicsQuality = " + graphicsQuality);
            System.out.println("buffColor = " + buffColor);
            System.out.println("itemColor = " + itemColor);
            System.out.println("hp11 = " + hp11);
            System.out.println("hp12 = " + hp12);
            System.out.println("hp13 = " + hp13);
            System.out.println("hp21 = " + hp21);
            System.out.println("hp22 = " + hp22);
            System.out.println("hp31 = " + hp31);
            System.out.println("hp32 = " + hp32);
            System.out.println("hp33 = " + hp33);
            System.out.println("hp34 = " + hp34);
            System.out.println("hp41 = " + hp41);
            System.out.println("scavengerSegments = " + scavengerSegments);
            System.out.println("pointAndTeleport = " + pointAndTeleport);
            System.out.println("minSpawnDelay = " + minSpawnDelay);
            System.out.println("spawnDelay = " + spawnDelay);
            System.out.println("courseCorrectionFactor = " + courseCorrectionFactor);
            System.out.println("meleeAccFactor = " + meleeAccFactor);
            System.out.println("meleeRotationFactor = " + meleeRotationFactor);
            System.out.println("mobCap = " + mobCap);
            System.out.println("consCap = " + consCap);
        }
    }
    
}
