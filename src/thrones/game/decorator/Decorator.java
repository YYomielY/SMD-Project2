package thrones.game.decorator;

import thrones.game.GameOfThrones;

public class Decorator {
    protected GameOfThrones.Suit decoratedSuit;

    public Decorator(GameOfThrones.Suit decoratedSuit) {
        this.decoratedSuit = decoratedSuit;
    }

    public void doEffect(){
        decoratedSuit.doEffect();
    }
}
