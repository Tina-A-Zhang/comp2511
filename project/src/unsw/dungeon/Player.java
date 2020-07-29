package unsw.dungeon;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * The player entity
 *
 * @author Robert Clifton-Everest
 *
 */
public class Player extends Entity implements Observer, Subject{
    /**
     * This is the dungeon
     */
    private Dungeon dungeon;
    /**
     * This is the sword that player is wearing
     */
    private Sword sword;
    /**
     * This is sign of whether the player collected a potion
     */
    private boolean potion;
    /**
     * This is the end time of the potion
     */
    private LocalDateTime end;
    /**
     * This is the key that player is wearing
     */
    private Key key;
    private BooleanProperty isDestroyed;
    /**
     * This is a list of treasure collected by the player
     */
    private ArrayList<Treasure> treasures = new ArrayList<Treasure>();
    /**
     * This is a list of observers who watch the player
     */
    private ArrayList<Observer> observers = new ArrayList<Observer>();

    /**
     * Create a player positioned in square (x,y)
     * @param x
     * @param y
     */
    public Player(Dungeon dungeon, int x, int y) {
        super(x, y);
        this.dungeon = dungeon;
        this.potion = false;
        this.isDestroyed = new SimpleBooleanProperty(false);
    }

    public void moveUp() {
        if (getY() > 0){
            if (notCollid(getX(), (getY() - 1))) {
                y().set(getY() - 1);
            }
        }
    }

    public void moveDown() {
        if (getY() < dungeon.getHeight() - 1) {
            if (notCollid(getX(), (getY() + 1))) {
                y().set(getY() + 1);
            }
        }
    }

    public void moveLeft() {
        if (getX() > 0) {
            if (notCollid((getX() - 1), getY())) {
                x().set(getX() - 1);
            }
        }
    }

    public void moveRight() {
        if (getX() < dungeon.getWidth() - 1) {
            if (notCollid((getX() + 1), getY())) {
                x().set(getX() + 1);
            }
        }
    }

    public void setPosition(int x, int y) {
        if (x < dungeon.getWidth())
            x().set(x);
        if (y < dungeon.getHeight())
            y().set(y);
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public Sword getSword() {
        return sword;
    }

    public void setSword(Observer obs) {
        if (obs instanceof Sword && getSword() == null) {
            this.sword = (Sword) obs;
        }
        if (obs == null) {
            this.sword = null;
        }
    }

    public void useSword() {
        sword.use();
        if (sword.getTime() == 0) {
            setSword(null);
        }
    }

    public boolean hasPotion() {
        return potion;
    }

    public void setPotion(Observer obs) {
        if (obs instanceof Potion) {
            this.end = LocalDateTime.now().plusSeconds(15);
            this.potion = true;
            return;
        }
        if (obs == null) {
            this.potion = false;
        }
    }

    public Key getKey(){
        return key;
    }

    public void setKey(Observer obs) {
        if (obs instanceof Key && getKey() == null) {
            this.key = (Key) obs;
        } else if (obs == null) {
            this.key = null;
        }
    }

    public BooleanProperty IsDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(Boolean destroyed) {
        super.destroy();
        this.isDestroyed.set(destroyed);
    }
    public ArrayList<Treasure> getTreasures() {
        return treasures;
    }

    public void addTreasures(Treasure treasure) {
        this.treasures.add(treasure);
    }

    @Override
    public boolean notCollid(int x, int y) {
        for (Observer obs : observers) {
            if (((Entity) obs).getX() == x && ((Entity) obs).getY() == y) {
                setSword(obs);
                setKey(obs);
                setPotion(obs);
                if (obs instanceof Treasure) {
                    addTreasures((Treasure) obs);
                }
                return obs.Moveable(this);
            }
        }
        return true;
    }

    @Override
    public void attach(Observer obs) {
        if (obs instanceof Floorswitch) {
            return;
        }
        this.observers.add(obs);
    }

    @Override
    public void detach(Observer obs) {
        this.observers.remove(obs);
    }

    @Override
    public boolean Moveable(Subject obj) {
        if (obj instanceof Enemy) {
            LocalDateTime now = LocalDateTime.now();
            if (hasPotion() && end.isAfter(now)) {
                return false;
            }
            return ((Observer) obj).Moveable(this);
        }
        return false;
    }
}
