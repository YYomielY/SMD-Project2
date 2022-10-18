package thrones.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.Optional;

public class SmartPlayer extends AbstractPlayer{

    public SmartPlayer(int playerIndex, String playerType) {
        super(playerIndex, playerType);
    }

    @Override
    public Optional<Card> playCard(boolean isCharacter) {
        return Optional.empty();
    }

    @Override
    public int selectPile(Hand[] plies) {
        return 0;
    }


}
