package thrones.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.Hand;
import thrones.game.GameOfThrones;

import java.util.Optional;

import static thrones.game.GameOfThrones.NON_SELECTION_VALUE;
import thrones.game.CardHandler.*;

public class HumanPlayer extends AbstractPlayer{

    private Optional<Card> selected;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    public HumanPlayer(int playerIndex, String playerType) {
        super(playerIndex, playerType);
    }

    private void setInteraction(){
        // Set up human player for interaction
        Hand currentHand = getHandCard();
        currentHand.addCardListener(new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                selected = Optional.of(card);
                currentHand.setTouchEnabled(false);
            }
            public void rightClicked(Card card) {
                selected = Optional.empty(); // Don't care which card we right-clicked for player to pass
                currentHand.setTouchEnabled(false);
            }
        });
    }

    private void setPileInteraction(Hand[] piles){
        for(int i = 0; i < 2; i ++){
            Hand currentPile = piles[i];
            int pileIndex = i;
            currentPile.addCardListener(new CardAdapter() {
                public void leftDoubleClicked(Card card) {
                    selectedPileIndex = pileIndex;
                    currentPile.setTouchEnabled(false);
                }
            });
        }
    }

    @Override
    public Optional<Card> playCard(boolean isCharacter) {
        setInteraction();

        if (getHandCard().isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            getHandCard().setTouchEnabled(true);
            do {
                if (selected == null) {
                    GameOfThrones.delay(100);
                    continue;
                }
                if (checkCharacter(isCharacter, selected)){
                    break;
                } else {
                    selected = null;
                    getHandCard().setTouchEnabled(true);
                }
                GameOfThrones.delay(100);
            } while (true);
        }
        return selected;
    }

    @Override
    public int selectPile(Hand[] piles) {
        setPileInteraction(piles);

        for (Hand pile : piles) {
            pile.setTouchEnabled(true);
        }
        while(selectedPileIndex == NON_SELECTION_VALUE) {
            GameOfThrones.delay(100);
        }

        for (Hand pile : piles) {
            pile.setTouchEnabled(false);
        }
        return selectedPileIndex;
    }

}
