package thrones.game.decorator;

import thrones.game.CardHandler.Suit;

public class Decorator {
    protected Suit decoratedSuit;

    public Decorator(Suit decoratedSuit) {
        this.decoratedSuit = decoratedSuit;
    }

    public void doEffect(){
        decoratedSuit.doEffect();
    }
}
