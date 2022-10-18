package thrones.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.GameOfThrones;

import java.util.Optional;

import static thrones.game.GameOfThrones.NON_SELECTION_VALUE;
import static thrones.game.GameOfThrones.random;

public class SimplePlayer extends AbstractPlayer{

    private Optional<Card> selected;
    private int selectedPileIndex;

    public SimplePlayer(int playerIndex, String playerType) {
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

    @Override
    public boolean checkBasicRules(Hand[] pile, Card currentCard, int pileIndex) {
       if(!super.checkBasicRules(pile, currentCard, pileIndex)){
           return false;
        }
       GameOfThrones.Suit suit = (GameOfThrones.Suit) currentCard.getSuit();
       int teamNum = getPlayerIndex() % 2;

       if(teamNum == pileIndex && (suit.isAttack() || suit.isDefence())){
           return true;
       }
       if(teamNum != pileIndex && suit.isMagic()){
           return true;
       }
       return false;
    }
}
