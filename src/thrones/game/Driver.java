package thrones.game;

import java.util.Random;

import static thrones.game.GameOfThrones.seed;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/got.properties";
    public static final int DEFAULT_PLAYER_NUM = 4;

    public static void main(String[] args) {

         /*System.out.println("Working Directory = " + System.getProperty("user.dir"));
         final PropertiesInitializer propertiesInitializer;

        if (args == null || args.length == 0) {
            propertiesInitializer = new PropertiesInitializer(DEFAULT_PROPERTIES_PATH, DEFAULT_PLAYER_NUM);
        } else {
            propertiesInitializer = new PropertiesInitializer(args[0], DEFAULT_PLAYER_NUM);
        }
        GameOfThrones.seed = propertiesInitializer.getSeed();
        //System.out.println("Seed = " + seed);
        GameOfThrones.random = propertiesInitializer.getRandom();
          */
        /**players 需要 initial 需要一个facade来设置GOT？？？？*/

        seed = 130006;
        System.out.println("Seed = " + seed);
        GameOfThrones.random = new Random(seed);
        new GameOfThrones();
    }
}