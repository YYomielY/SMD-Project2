package thrones.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.GameOfThrones;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static thrones.game.GameOfThrones.random;
import thrones.game.CardHandler.*;

public abstract class AbstractPlayer {

    protected int playerIndex;
    protected String playerType;
    protected Hand handCard;

    protected int score;

    public AbstractPlayer(int playerIndex, String playerType) {
        this.playerIndex = playerIndex;
        this.playerType = playerType;
        this.score = 0;
        this.handCard = null;
    }

    public abstract Optional<Card> playCard(boolean isCharacter);

    public abstract int selectPile(Hand[] plies);

    public boolean checkBasicRules(Hand[] pile, Card currentCard, int pileIndex) {
        int cardNum  = pile[pileIndex].getNumberOfCards();

        if((pile[pileIndex].getNumberOfCardsWithSuit(GameOfThrones.Suit.HEARTS) == 0
                && cardNum == 0) && currentCard.getSuit() != GameOfThrones.Suit.HEARTS){
            return false;
        }else if(cardNum > 0 && currentCard.getSuit() == GameOfThrones.Suit.HEARTS){
            return false;
        }else if(cardNum == 1 && currentCard.getSuit() == GameOfThrones.Suit.DIAMONDS){
            return false;
        }
        return true;
    }


    public boolean checkCharacter(boolean isCharacter, Optional<Card> selected){

        GameOfThrones.Suit suit = selected.isPresent() ? (GameOfThrones.Suit) selected.get().getSuit() : null;
        if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                !isCharacter && (suit == null || !suit.isCharacter())) { //// If we don't want character, can pass or suit must not be character
            return true;
        }else
            return false;
    }
    //public boolean checkCharacter(Hand[] pile) 检查是否需要在这里放heart？
    public Optional<Card> randomPickCard(boolean isCharacter){
        Optional<Card> selected;
        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < getHandCard().getCardList().size(); i++) {
            Card card = getHandCard().getCardList().get(i);
            GameOfThrones.Suit suit = (GameOfThrones.Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && random.nextInt(3) == 0) {
            selected = Optional.empty();
        } else {
            selected = Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
        }
        return selected;
    }



    public void setHandCard(Hand handCard) {
        this.handCard = handCard;
    }

    public Hand getHandCard() {
        return handCard;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }
}
