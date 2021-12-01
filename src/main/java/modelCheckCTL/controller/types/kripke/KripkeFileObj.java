package modelCheckCTL.controller.types.kripke;

import modelCheckCTL.controller.types.kripke.Kripke;

import java.util.Set;

/**
 * A bookkeeping data structure containing a {@link Kripke} object as well as all associated "meta data" such as the kripke file's original filepath, the states and transitions in the kripke, as well as any errors (including line numbers) that occurred while parsing the kripke file
 */
public class KripkeFileObj {

    /**
     * A {@link String} of either just the kripke filename (ie, kripke.txt) if it's in the /resources folder or the path and the filename (ie, end-to-end-tests/kripke.txt) if it's in a subfolder under the resources folder)
     */
    String kripkeFilepath;

    /**
     * A {@link Kripke} containing the states (which in turn can contain labels) and transitions of the model
     */
    Kripke kripke;

    /**
     * The {@link Set} of all {@link State}s in the kripke
     */
    Set states;

    /**
     * The {@link Set} of all {@link Transition}s in the kripke
     */
    Set transitions;

    /**
     * Any error message which occurred while parsing the kripke file. Will be null if no error occurred.
     */
    String errorMessage;

    /**
     * Line num of any error which occurred in parsing the kripke file. Will be null if no error occurred.
     */
    int lineNum;

    public void setKripke(Kripke kripke) {
        this.kripke = kripke;
    }

    public void setTransitions(Set transitions) {
        this.transitions = transitions;
    }

    public void setStates(Set states) {
        this.states = states;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Kripke getKripke() {
        return kripke;
    }

    public Set getTransitions() {
        return transitions;
    }

    public Set getStates() {
        return states;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getKripkeFilepath() {
        return kripkeFilepath;
    }

    public void setKripkeFilepath(String kripkeFilepath) {
        this.kripkeFilepath = kripkeFilepath;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

}
