package thrones.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.GameOfThrones;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static thrones.game.GameOfThrones.NON_SELECTION_VALUE;
import static thrones.game.GameOfThrones.random;

public class RandomPlayer extends AbstractPlayer{

    private Optional<Card> selected;
    private int selectedPileIndex;

    public RandomPlayer(int playerIndex, String playerType) {
        super(playerIndex, playerType);
    }

    @Override
    public Optional<Card> playCard(boolean isCharacter) {
        selected = randomPickCard(isCharacter);
        return selected;
    }

    @Override
    public int selectPile(Hand[] plies) {
        selectedPileIndex = random.nextInt(2);
        if(checkBasicRules(plies, selected.get(), selectedPileIndex)){
            return selectedPileIndex;
        }
        return NON_SELECTION_VALUE;
    }


}
