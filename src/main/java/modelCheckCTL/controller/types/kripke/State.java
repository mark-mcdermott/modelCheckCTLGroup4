package modelCheckCTL.controller.types.kripke;

import java.io.IOException;
import java.util.*;

import static java.lang.Character.isLowerCase;

/**
 * Class for the state data type, representing a node in a directed graph.
 * A Kripke structure will have a set of state, represented formally like
 * S = {s1, s2, s3}
 * (see <a href="https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)#Example">wikipedia.org/wiki/Kripke_structure_(model_checking)#Example</a>)
 *
 * This model checking implementation imports a Kripke structures from text file
 * and the first line of the text file will list the states in the following style:
 * s1, s2, s3, s4;
 *
 * Each state has a number property, which is its number in the Kripke structure.
 * Each state also has a Set of labels (a Set of {@link Character}s) and a Set of
 * {@link Transition}s. The labels and transitions sets can be empty if the state
 * has no labels or transitions, respectively.
 *
 * A state "has" a transition if it is the origin state in the origin/destination
 * pair. So all the transitions that a state s3 would have would have a "from" state
 * of s3, ie {(s3,s4),(s3,s1),(s3,s3)}
 *
 * This class implements Comparable so Utils.getStatesStr() can return the states in numerical order
 */
public class State implements Comparable<State> {

    /**
     * State's {@link Integer} number in a Kripke structure.
     * Note some Kripke implementations will start at s0 and others at s1.
     */
    private Integer number;

    /**
     * {@link Set} of {@link Character}s representing each label or atomic proposition (ie, p)
     * In model checking, a label represents a condition. Ie, label p could be "elevator door is open"
     * Can be empty if state has no labels. Should not be null.
     * See <a href="https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)">wikipedia.org/wiki/Kripke_structure_(model_checking)</a>
     */
    private Set labels;

    /**
     * {@link Set} of {@link Transition}s representing each state this state can transition to in a directed graph.
     * A state "has" a transition if it is the origin state in the origin/destination pair. So all the transitions that a state s3 would have would have a "from" state of s3, ie {(s3,s4),(s3,s1),(s3,s3)}
     * Can be empty. Should not be null.
     * See <a href="https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)">wikipedia.org/wiki/Kripke_structure_(model_checking)</a>
     */
    private Set transitions;

    /**
     * Class constructor specifying the transition's {@link Integer} number in a Kripke structure.
     * Note some implementations of Kripke structures will start at state s0 and others at s1.
     * @param number
     */
    public State(Integer number) {
        this.transitions = new HashSet();
        this.labels = new HashSet();
        this.number = number;
    }

    /**
     * Adds a {@link Transition} representing a state this state can transition to in the directed graph or Kripke structure.
     * The transition is added to the State's {@link Set} of {@link Transition}s.
     * A state "has" a transition if it is the origin state in an origin/destination state pair. So all the transitions that a state s3 would have would have a "from" state of s3, ie {(s3,s4),(s3,s1),(s3,s3)}. This method throws an IOException if a transition is attempted to be added which is a state pair where this state is not the "from" state (the first state) in the pair.
     * Throws NullPointerException if null transition arguments is passed
     * Throws IOException if "from" state of the transition being added is different than the state having the transition added
     * @param transition the {@link Transition} one wants to ass to the state's {@link Set} of transitions
     */
    public void addTransition(modelCheckCTL.controller.types.kripke.Transition transition) throws IOException {
        if (transition == null) { throw new NullPointerException("addTransition() argument is null"); }
        if (transition.getFrom() == null) { throw new NullPointerException("addTransition() argument's from state is null"); }
        if (transition.getTo() == null) { throw new NullPointerException("addTransition() argument's to state is null"); }
        Integer thisStateNum = this.number;
        Integer fromStateNum = transition.getFrom().getNumber();
        if (thisStateNum != fromStateNum) {
            throw new IOException("Attempting to add transition to a state which has a from state other than the state being added to");
        } else {
            this.transitions.add(transition);
        }
    }

    /**
     * Checks if the state has a specific transition, returns true or false
     * Throws NullPointerException if null targetState arguments is passed
     * @param {@link State} to look for
     * @return true if it has the transition
     */
    public Boolean hasTransitionTo(State targetState) {
        if (targetState == null) { throw new NullPointerException("hasTransitionTo() argument is null"); }
        Integer transitionStateNum = targetState.getNumber();
        for (Object transitionObj : this.transitions) {
            modelCheckCTL.controller.types.kripke.Transition thisTransition = (modelCheckCTL.controller.types.kripke.Transition) transitionObj;
            Integer thisTransitionToNum = thisTransition.getTo().getNumber();
            if (thisTransitionToNum == transitionStateNum) {
                return true;
            }
        }
        return false;
    }

    /**
     * Default toString for {@link State} - just returns in format like "s1" or "s32"
     * @return a {@link String} representing a {@link State} in format like "s1" or "s32"
     */
    public String toString() {
        return "s" + getNumber();
    }

    /**
     * Detailed toString for {@link State}, returned the string name and all labels it has in a format like "s2 : q t r" of if state has no labels, just "s2 : " (note a space before and after the colon in both cases and labels are separated by a space).
     * @return a {@link String} representing a {@link State} with labels in format like "s2 : q t r" of if state has no labels, just "s2 : " (note a space before and after the colon in both cases and labels are separated by a space).
     */
    public String toStringDetailed() {
        String labelsStr = getLabelsString();
        if (labelsStr == null) { throw new NullPointerException("labelStr is null in toStringDetailed"); }
        return "s" + number + " : " + labelsStr;
    }

     /**
     * A string used in {@link State}'s toStringDetailed, which shows a state name and its labels.
      * Returns a {@link String} of labels (a label is just a lowercase alpha {@link Character} a-z or usually just p-z).
      * String has the format like "q r t". Labels are separated by a space. A state with no labels will return an empty string here.
     * @return a {@link String} representing a {@link State}'s labels in format like "q t r" of if state has no labels, just "" (labels are sep).
     */
    private String getLabelsString() {
        if (labels == null) { throw new NullPointerException("Trying to get state's label string, but the label set is null."); }
        String labelsStr = "";
        for (Object labelObj : labels ) {
            String thisLabelStr = (String) labelObj;
            labelsStr = labelsStr + thisLabelStr + " ";
        }
        labelsStr = labelsStr.trim();
        return labelsStr;
    }

    /**
     * Checks if a state has a specified label (a label is just a {@link Character} a-z) in its label {@link Set}
     * @param labelToCheck a {@link Character} a-z, must be lowercase
     * @return true if {@link State} has specified label in its label {@link Set}, false if it does not
     * @throws IOException
     */
    public Boolean hasLabel(String labelToCheck) throws IOException {
        if (labelToCheck == null) { throw new NullPointerException("labelToCheck in state hasLabel is null"); }
        // if (!isLowerCase(labelToCheck)) { throw new IOException("labelToCheck is not a lower case letter"); }
        Boolean hasLabel = false;
        for (Object labelObj : labels ) {
            if (labelObj == null) { throw new NullPointerException("label in label set in labelToCheck is null"); }
            // Character label = (Character) labelObj;
            String label = (String) labelObj;
            if (label.equals(labelToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets state's {@link Integer} number in a Kripke structure (ie 1 in state s1).
     * @return state's {@link Integer} number in a Kripke structure (ie 1 in state s1).
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets state's {@link Integer} number in a Kripke structure (ie 1 in state s1).
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * Gets {@link Set} of {@link Character}s representing each label or atomic proposition (ie, p)
     * @return {@link Set} of {@link Character}s representing each label or atomic proposition (ie, p)
     */
    public Set getLabels() {
        return labels;
    }

    /**
     * Sets {@link Set} of {@link Character}s representing each label or atomic proposition (ie, p)
     * @param labels
     */
    public void setLabels(Set labels) {
        this.labels = labels;
    }

    /**
     * Gets {@link Set} of {@link Transition}s representing each state this state can transition to in a directed graph.
     * @return {@link Set} of {@link Transition}s representing each state this state can transition to in a directed graph.
     */
    public Set getTransitions() {
        return transitions;
    }

    public void setTransitions(Set transitions) {
        this.transitions = transitions;
    }

    /**
     * This class implements Comparable so Utils.getStatesStr() can return the states in numerical order
     * @param s
     * @return
     */
    @Override
    public int compareTo(State s) {
      return getNumber().compareTo(s.getNumber());
    }

}
