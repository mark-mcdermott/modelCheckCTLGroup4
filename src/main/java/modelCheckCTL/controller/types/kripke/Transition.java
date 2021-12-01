package modelCheckCTL.controller.types.kripke;

/**
 * Class for the transition data type, representing a pair of states that are connected
 * by an arrow in a directed graph. There is always a from-state and a to-state.
 * A graph transition is the same as a relation in math or in a Kripke
 * structure.
 *
 * A Kripke could have a set of relations like (this is using academic
 * notation): R = {(s1, s2), (s2, s1) (s2, s3), (s3, s3)}
 * (from https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)#Example)
 * Each pair in parenthesis is a transition.
 *
 * Transitions in this program are imported from a Kripke text file.
 * The transition portion of the Kripke text file would look like:
 * t1 : s1 - s2,
 * t2 : s1 - s3,
 * t3 : s3 - s4,
 * t4 : s4 - s2,
 * t5 : s2 - s3;
 *
 * In the above kripke file transition list, each transition has a number,
 * such as 1 in t1. This is the <code>number</code> property in this class.
 * The left state above is the from state (the <code>from</code> property)
 * and the right state is the to state (the <code>to</code> property.
 *
 * Transitions live in two places - in a HashSet in a Kripke structure and
 * also in a HashSet in a State. Since they live in two places, be aware the
 * possiblity for "stale" data exists. The States' transitions should
 * probably be the source of truth and the Kripke's set of transitions
 * should always be updated after a State transition is altered, added or
 * deleted.
 *
 * This implements Comparable so Utils.getTransitionsStr() can return the transitions in numerical order
 */
public class Transition implements Comparable<Transition> {

    /**
     * The {@link Integer} number of the transition in the Kripke's set of transitions (ie, t1).
     * Optional when testing, but always include number when importing transitions from a Kripke text file.
     */
    private Integer number;

    /**
     * Origin {@link State} in a transition's (origin,destination) state pair.
     */
    private State from;

    /**
     * Destination {@link State} in a transition's (origin,destination) state pair.
     */
    private State to;

    /**
     * Preferred class constructor specifying the transition's number in a Kripke structure.
     * as well as the origin and destination state pair.
     * @param number
     * @param from
     * @param to
     */
    public Transition(Integer number, State from, State to) {
        this.number = number;
        this.from = from;
        this.to = to;
    }

    /**
     * Use only for testing.
     * Class constructor which doesn't specify the transition's number
     * in a Kripke structure. Only origin and destination state pair are
     * specified.
     * @param from
     * @param to
     */
    public Transition(State from, State to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the {@link Integer} number of the transition in the Kripke's set of transitions (ie, t1).
     * @return the {@link Integer} number of the transition in the Kripke's set of transitions (ie, t1).
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Gets the origin {@link State} in a transition's (origin,destination) state pair.
     * @return the origin {@link State} in a transition's (origin,destination) state pair.
     */
    public State getFrom() {
        return from;
    }


    /**
     * Gets the destination {@link State} in a transition's (origin,destination) state pair.
     * @return the destination {@link State} in a transition's (origin,destination) state pair.
     */
    public State getTo() {
        return to;
    }

    /**
     * Default toString implementation, simply providing the transition number preceeded by "t" (ie, "t1")
     * @return a {@link String} in a format like "t1", "t2", etc.
     */
    public String toString() {
        return "t" + number;
    }

    /**
     * toString implementation used for listing transitions in a Kripke structure.
     * This is used in Utils.getTransitionsStr
     * @return a {@link String} in a format like "t1 : s1 - s2"
     */
    public String toStringDetailed() {
        return "t" + number + " : " + from + " - " + to;
    }

    /**
     * implements Comparable so Utils.getTransitionsStr() can return the transitions in numerical order
     * @param t
     * @return
     */
    @Override
    public int compareTo(Transition t) {
      return getNumber().compareTo(t.getNumber());
    }

    public Transition copy() {
        Transition transitionCopy = new Transition(this.number,this.from,this.to);
        return transitionCopy;
    }

}
