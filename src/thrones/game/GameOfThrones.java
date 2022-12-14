package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import thrones.game.player.AbstractPlayer;
import thrones.game.properties.PropertiesInitializer;
import thrones.game.properties.PropertiesLoader;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class GameOfThrones extends CardGame {


    static public int seed;
    static public Random random;

    // return a random Card from Hand
    public static Card randomCard(Hand hand) {
        assert !hand.isEmpty() : " random card from empty hand.";
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
        Hand pack = deck.toHand(false); //发牌
        assert pack.getNumberOfCards() == 52 : " Starting pack is not 52 cards.";
        // Remove 4 Aces
        List<Card> aceCards = pack.getCardsWithRank(Rank.ACE);
        for (Card card : aceCards) {
            card.removeFromHand(false);
        }
        assert pack.getNumberOfCards() == 48 : " Pack without aces is not 48 cards.";
        // Give each player 3 heart cards
        for (int i = 0; i < nbPlayers; i++) {
            for (int j = 0; j < 3; j++) {
                List<Card> heartCards = pack.getCardsWithSuit(Suit.HEARTS);
                int x = random.nextInt(heartCards.size());
                Card randomCard = heartCards.get(x);
                randomCard.removeFromHand(false);
                hands[i].insert(randomCard, false);
            }
        }
        assert pack.getNumberOfCards() == 36 : " Pack without aces and hearts is not 36 cards.";
        // Give each player 9 of the remaining cards
        for (int i = 0; i < nbCardsPerPlayer; i++) {
            for (int j = 0; j < nbPlayers; j++) {
                assert !pack.isEmpty() : " Pack has prematurely run out of cards.";
                Card dealt = randomCard(pack); //get a random card from pack
                dealt.removeFromHand(false); //remove it
                hands[j].insert(dealt, false); //give it to a player
            }
        }
        for (int j = 0; j < nbPlayers; j++) {
            assert hands[j].getNumberOfCards() == 12 : " Hand does not have twelve cards."; //assert every player have 12 cards
        }
    }

    private final String version = "1.0";
    public final int nbPlayers = 4;
    public final int nbStartCards = 9;
	public final int nbPlays = 6;
	public final int nbRounds = 3;
    private final int handWidth = 400;
    private final int pileWidth = 40;
    private Deck deck = new Deck(Suit.values(), Rank.values(), "cover"); ///??????? cover image?
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(25, 25),
            new Location(575, 125)
    };
    private final Location[] pileLocations = {
            new Location(350, 280),
            new Location(350, 430)
    };
    private final Location[] pileStatusLocations = {
            new Location(250, 200),
            new Location(250, 520)
    };

    private Actor[] pileTextActors = { null, null };
    private Actor[] scoreActors = {null, null, null, null};
    private final int watchingTime = 5000; //???
    private Hand[] hands;
    private Hand[] piles;
    private final String[] playerTeams = { "[Players 0 & 2]", "[Players 1 & 3]"};
    private int nextStartingPlayer = random.nextInt(nbPlayers); //random a player to start

    private int[] scores = new int[nbPlayers]; //why not team number?????

    Font bigFont = new Font("Arial", Font.BOLD, 36); //UI
    Font smallFont = new Font("Arial", Font.PLAIN, 10);

    // boolean[] humanPlayers = { true, false, false, false};
    private AbstractPlayer[] players = new AbstractPlayer[nbPlayers];
    // boolean[] humanPlayer = { true, false, false, false}; decide a player

    //for testing
    private void initPlayers(){
        for(int i = 0; i < nbPlayers; i++){
            players[i] = PlayerFactory.getInstance().createPlayer(i, "simple");
        }
    }




    //////!!!we can seperate the ui settings and the initial part
    private void initScore() {
        //showing the player P0(player number)-0 (score)
        for (int i = 0; i < nbPlayers; i++) {
             scores[i] = 0;
            String text = "P" + i + "-0"; //UI
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }

        String text = "Attack: 0 - Defence: 0";
        for (int i = 0; i < pileTextActors.length; i++) { //UI
            pileTextActors[i] = new TextActor(text, Color.WHITE, bgColor, smallFont);
            addActor(pileTextActors[i], pileStatusLocations[i]);
        }
    }
    //in the player level, update the score
    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        String text = "P" + player + "-" + scores[player]; //duplicate UI setting
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }
    //in the game level
    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            updateScore(i);
        }
        System.out.println(playerTeams[0] + " score = " + scores[0] + "; " + playerTeams[1] + " score = " + scores[1]);
    }

    private Optional<Card> selected;
    public final static int NON_SELECTION_VALUE = -1; ////
    private int selectedPileIndex = NON_SELECTION_VALUE;

    private final int UNDEFINED_INDEX = -1;
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;
    private void setupGame() {
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        dealingOut(hands, nbPlayers, nbStartCards);

        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true); //show the modification
            players[i].setHandCard(hands[i]);//set handCard for players
            System.out.println("hands[" + i + "]: " + canonical(hands[i]));
        }
/*
        for (final Hand currentHand : hands) {
            // Set up human player for interaction
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
        */
        // graphics: set location of each player
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            hands[i].setView(this, layouts[i]);
            hands[i].draw();
        }
        // End graphics
    }

    private void resetPile() {
        if (piles != null) {
            for (Hand pile : piles) {
                pile.removeAll(true);
            }
        }
        piles = new Hand[2]; //make the 2 isolate
        for (int i = 0; i < 2; i++) {
            piles[i] = new Hand(deck);
            piles[i].setView(this, new RowLayout(pileLocations[i], 8 * pileWidth)); //UI
            piles[i].draw();
            final Hand currentPile = piles[i];
            final int pileIndex = i;
            piles[i].addCardListener(new CardAdapter() { //settings
                public void leftClicked(Card card) {
                    selectedPileIndex = pileIndex;
                    currentPile.setTouchEnabled(false);
                }
            });
        }

        updatePileRanks();
    }

    /*private void pickACorrectSuit(int playerIndex, boolean isCharacter) {
        Hand currentHand = hands[playerIndex];
        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < currentHand.getCardList().size(); i++) {
            Card card = currentHand.getCardList().get(i);
            Suit suit = (Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && random.nextInt(3) == 0) {
            selected = Optional.empty();
        } else {
            selected = Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
        }
    }*/

    /*private void selectRandomPile() {
        selectedPileIndex = random.nextInt(2);
    }*/
    /*能不能在playCard中use!!
   private void waitForCorrectSuit(int playerIndex, boolean isCharacter) {
        if (players[playerIndex].getHandCard().isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            players[playerIndex].getHandCard().setTouchEnabled(true);
            do {
                if (selected == null) {
                    delay(100);
                    continue;
                }
                //Suit suit = selected.isPresent() ? (Suit) selected.get().getSuit() : null;
                if ( players[playerIndex].isLegalPlay(isCharacter, selected)){
                    break;
                } else {
                    selected = null;
                    players[playerIndex].getHandCard().setTouchEnabled(true);
                }
                delay(100);
            } while (true);
        }
    }
     */

    /*
    private void waitForPileSelection() {
        selectedPileIndex = NON_SELECTION_VALUE;
        for (Hand pile : piles) {
            pile.setTouchEnabled(true);
        }
        while(selectedPileIndex == NON_SELECTION_VALUE) {
            delay(100);
        }
        for (Hand pile : piles) {
            pile.setTouchEnabled(false);
        }
    }*/

    //change!! get the rank of the current pile card (game?)
    private int[] calculatePileRanks(int pileIndex) {
        Hand currentPile = piles[pileIndex];
        int i = currentPile.isEmpty() ? 0 : ((Rank) currentPile.get(0).getRank()).getRankValue();
        return new int[] { i, i }; //first one is attack value, second one is defence value
    }

    //UI showing the attack and defence details
    private void updatePileRankState(int pileIndex, int attackRank, int defenceRank) {
        TextActor currentPile = (TextActor) pileTextActors[pileIndex];
        removeActor(currentPile);
        String text = playerTeams[pileIndex] + " Attack: " + attackRank + " - Defence: " + defenceRank;
        pileTextActors[pileIndex] = new TextActor(text, Color.WHITE, bgColor, smallFont);
        addActor(pileTextActors[pileIndex], pileStatusLocations[pileIndex]);
    }

    //game related GOT
    private void updatePileRanks() {
        for (int j = 0; j < piles.length; j++) {
            int[] ranks = calculatePileRanks(j);
            updatePileRankState(j, ranks[ATTACK_RANK_INDEX], ranks[DEFENCE_RANK_INDEX]);
        }
    }

    private int getPlayerIndex(int index) {
        return index % nbPlayers;
    }  //GOT

    private void executeAPlay() {
        resetPile();

        nextStartingPlayer = getPlayerIndex(nextStartingPlayer);
        if (hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) == 0)
            nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        assert hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) != 0 : " Starting player has no hearts.";

        // 1: play the first 2 hearts
        for (int i = 0; i < 2; i++) {
            int playerIndex = getPlayerIndex(nextStartingPlayer + i);
            setStatusText("Player " + playerIndex + " select a Heart card to play");


            selected = players[playerIndex].playCard(true);

            /*
            if (players[playerIndex].getClass().getSimpleName().equals("HumanPlayer")) {
                waitForCorrectSuit(playerIndex, true); //play character
            } else {
                selected = players[playerIndex].playCard(true, selected);
            }
             */

            int pileIndex = playerIndex % 2;
            assert selected.isPresent() : " Pass returned on selection of character.";
            System.out.println("Player " + playerIndex + " plays " + canonical(selected.get()) + " on pile " + pileIndex);
            selected.get().setVerso(false);
            selected.get().transfer(piles[pileIndex], true); // transfer to pile (includes graphic effect)
            updatePileRanks();
        }

        // 2: play the remaining nbPlayers * nbRounds - 2
        int remainingTurns = nbPlayers * nbRounds - 2;
        int nextPlayer = nextStartingPlayer + 2;

        while(remainingTurns > 0) {
            nextPlayer = getPlayerIndex(nextPlayer);
            setStatusText("Player" + nextPlayer + " select a non-Heart card to play.");
            /*if (humanPlayers[nextPlayer]) {
                waitForCorrectSuit(nextPlayer, false);
            } else {
                pickACorrectSuit(nextPlayer, false);
            }*/
            selected = players[nextPlayer].playCard(false);

            if (selected.isPresent()) {
                setStatusText("Selected: " + canonical(selected.get()) + ". Player" + nextPlayer + " select a pile to play the card.");

                selectedPileIndex = players[nextPlayer].selectPile(piles);

                /*
                if (players[nextPlayer].getClass().getName().equals("HumanPlayer")) {
                    waitForPileSelection();
                } else {
                    selectedPileIndex = players[nextPlayer].selectPile();
                }
                */
                if(selectedPileIndex == NON_SELECTION_VALUE){
                    setStatusText("Pass.");
                }else {
                    System.out.println("Player " + nextPlayer + " plays " + canonical(selected.get()) + " on pile " + selectedPileIndex);
                    selected.get().setVerso(false);
                    selected.get().transfer(piles[selectedPileIndex], true); // transfer to pile (includes graphic effect)
                    updatePileRanks();
                }
            } else {
                setStatusText("Pass.");
            }
            nextPlayer ++;
            remainingTurns--;
        }

        // 3: calculate winning & update scores for players
        updatePileRanks();
        int[] pile0Ranks = calculatePileRanks(0);
        int[] pile1Ranks = calculatePileRanks(1);
        System.out.println("piles[0]: " + canonical(piles[0]));
        System.out.println("piles[0] is " + "Attack: " + pile0Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile0Ranks[DEFENCE_RANK_INDEX]);
        System.out.println("piles[1]: " + canonical(piles[1]));
        System.out.println("piles[1] is " + "Attack: " + pile1Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile1Ranks[DEFENCE_RANK_INDEX]);

        Rank pile0CharacterRank = (Rank) piles[0].getCardList().get(0).getRank();
        Rank pile1CharacterRank = (Rank) piles[1].getCardList().get(0).getRank();
        String character0Result;
        String character1Result;

        //duplicate? combine
        if (pile0Ranks[ATTACK_RANK_INDEX] > pile1Ranks[DEFENCE_RANK_INDEX]) {
            scores[0] += pile1CharacterRank.getRankValue();
            scores[2] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 succeeded.";
        } else {
            scores[1] += pile1CharacterRank.getRankValue();
            scores[3] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 failed.";
        }

        if (pile1Ranks[ATTACK_RANK_INDEX] > pile0Ranks[DEFENCE_RANK_INDEX]) {
            scores[1] += pile0CharacterRank.getRankValue();
            scores[3] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack on character 0 succeeded.";
        } else {
            scores[0] += pile0CharacterRank.getRankValue();
            scores[2] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack character 0 failed.";
        }
        updateScores();
        System.out.println(character0Result);
        System.out.println(character1Result);
        setStatusText(character0Result + " " + character1Result);

        // 5: discarded all cards on the piles
        nextStartingPlayer += 1;
        delay(watchingTime);
    }


    public GameOfThrones() {
        super(700, 700, 30);

        setTitle("Game of Thrones (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();
        initPlayers();

        setupGame();
        for (int i = 0; i < nbPlays; i++) {
            executeAPlay();
            updateScores();
        }

        String text;
        if (scores[0] > scores[1]) {
            text = "Players 0 and 2 won.";
        } else if (scores[0] == scores[1]) {
            text = "All players drew.";
        } else {
            text = "Players 1 and 3 won.";
        }
        System.out.println("Result: " + text);
        setStatusText(text);

        refresh();
    }

    public static final String DEFAULT_PROPERTIES_PATH = "properties/got.properties";
    public static final int DEFAULT_PLAYER_NUM = 4;


}
