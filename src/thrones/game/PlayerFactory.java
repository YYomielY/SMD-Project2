package thrones.game;

import thrones.game.player.*;

public class PlayerFactory {

    private static PlayerFactory instance = null;
    private AbstractPlayer player;

    private PlayerFactory(){};

    public static PlayerFactory getInstance() {
        if (instance == null) {
            instance = new PlayerFactory();
        }
        return instance;
    }

    public AbstractPlayer createPlayer(int playerId, String type) {
        switch (type) {
            case "human":
                player = new HumanPlayer(playerId, type);
                break;
            case "random":
                player = new RandomPlayer(playerId, type);
                break;
            case "smart":
                player = new SmartPlayer(playerId, type);
                break;
            default:
                player = new SimplePlayer(playerId, type);
        }
        return player;
    }
}
