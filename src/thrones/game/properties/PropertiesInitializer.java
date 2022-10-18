package thrones.game.properties;


import thrones.game.PlayerFactory;
import thrones.game.player.AbstractPlayer;

import java.util.Properties;
import java.util.Random;

public class PropertiesInitializer {
    private static int DEFAULT_SEED = 130006;
    private static int DEFAULT_WATCHING_TIME = 5000;

    //private static PropertiesInitializer instance = null;

    private int seed;
    private int watchingTime;
    private Random random;
    private AbstractPlayer[] players;

    public PropertiesInitializer (String propertiesPath, int nbPlayers){

        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        this.seed = Integer.parseInt(properties.getProperty("seed", String.valueOf(DEFAULT_SEED)));
        this.watchingTime = Integer.parseInt(properties.getProperty("watchingTime",
                String.valueOf(DEFAULT_WATCHING_TIME)));
        this.random = new Random(seed);
        players = new AbstractPlayer[nbPlayers];

        for(int i = 0; i < nbPlayers; i++){
            String playerType = properties.getProperty(String.format("players.%d", i), "");
            players[i] = PlayerFactory.getInstance().createPlayer(i, playerType);
        }
    }

    public int getSeed() {
        return seed;
    }

    public int getWatchingTime() {
        return watchingTime;
    }

    public AbstractPlayer[] getPlayer() {
        return players;
    }

    public Random getRandom() {
        return random;
    }
}
