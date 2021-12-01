package modelCheckCTL.controller.types.kripke;

import java.io.IOException;
import java.util.Set;

import static modelCheckCTL.utils.Utils.*;

/**
 * A data structure representing a Kripke structure. Has a states property and a transitions property. It also contains labels (as a Kripke structure must), but they are properties on the {@link State} objects
 */
public class Kripke {
    private Set states;
    private Set transitions;

    public Kripke(Set states, Set transitions) {
        this.states = states;
        this.transitions = transitions;
    }

    public Set getStates() {
        return states;
    }

    public void setStates(Set states) {
        this.states = states;
    }

    public Set getTransitions() {
        return transitions;
    }

    public void setTransitions(Set transitions) {
        this.transitions = transitions;
    }

    /**
     * If Kripke has content, this returns a multiline {@link String} in a format like:
     * s1, s2, s3, s4;
     * t1 : s1 - s2, (transition t1 is from state s1 to state s2)
     * t2 : s1 - s3,
     * t3 : s3 – s4,
     * t4 : s4 – s2,
     * t5 : s2 – s3;
     * s1 : p q, (propositional atom names are separated by a space; a name consists of letters, it is casesensitive)
     * s2 : q t r,
     * s3 : , (i.e. set of propositional atoms for state s3 is empty)
     * s4 : t;
     * @return
     */
    public String toString() {
        if (states == null) { throw new NullPointerException("states in Kripke are null in toString"); }
        if (transitions == null) { throw new NullPointerException("transitions in Kripke are null in toString"); }
        if (getLabelsStr(states) == null) { throw new NullPointerException("labels in Kripke are null in toString"); }
        String output = "";
        try {
            output = output + getStatesStr(states) + '\n';
            output = output + getTransitionsStr(transitions);
            output = output + getLabelsStr(states);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * If Kripke has content, this prints a multiline {@link String} in a format like:
     * s1, s2, s3, s4;
     * t1 : s1 - s2, (transition t1 is from state s1 to state s2)
     * t2 : s1 - s3,
     * t3 : s3 – s4,
     * t4 : s4 – s2,
     * t5 : s2 – s3;
     * s1 : p q, (propositional atom names are separated by a space; a name consists of letters, it is casesensitive)
     * s2 : q t r,
     * s3 : , (i.e. set of propositional atoms for state s3 is empty)
     * s4 : t;
     */
    public void printKripke() {
        System.out.print(toString());
    }

    /**
     * Check the Kripke for null values (states, transitions and states' labels), throw NullPointerException if found.
     * @throws IOException
     */
    public void checkKripkeForNulls() throws IOException {
        if (getStates() == null) { throw new NullPointerException("kripke states are null in ModelCheckInputs call"); }
        if (getTransitions() == null) { throw new NullPointerException("kripke transitions are null in ModelCheckInputs call"); }
        for (Object stateObj : getStates()) {
            State state = (State) stateObj;
            if (state == null) { throw new NullPointerException("A state in kripke states is null in ModelCheckInputs call"); }
            for (Object labelObj : state.getLabels()) {
                String label = (String) labelObj;
                if (label == null) { throw new NullPointerException("A label in a kripke state is null in ModelCheckInputs call"); }
            }
            for (Object transitionObject : state.getTransitions()) {
                Transition transition = (Transition) transitionObject;
                if (transition == null) { throw new NullPointerException("A transition in a kripke state set is null in ModelCheckInputs call"); }
                if (transition.getFrom().getNumber() != state.getNumber()) { throw new IOException("Invalid transition in a state in Kripke in ModelCheckInputs call. A state may only have transitions that start at that state"); }
            }
        }
        if (getTransitions() == null) { throw new NullPointerException("transitions area null in ModelCheckInputs call"); }
        for (Object transitionObject : getTransitions()) {
            Transition transition = (Transition) transitionObject;
            if (transition == null) { throw new NullPointerException("A transition in kripke transition set is null in ModelCheckInputs call"); }
        }
    }

}
