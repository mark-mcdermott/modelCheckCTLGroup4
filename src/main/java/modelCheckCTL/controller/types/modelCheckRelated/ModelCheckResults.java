package modelCheckCTL.controller.types.modelCheckRelated;

import modelCheckCTL.controller.types.kripke.State;

import java.util.Set;

import static modelCheckCTL.utils.Utils.containsStateName;

/**
 * The results of a single model check run. Contains the necessary details about how the model check went and what was checked exactly.
 */
public class ModelCheckResults {

    /**
     * {@link Set} of {@link State}s that held for the formula
     */
    Set statesThatHold;

    /**
     * {@link Set} of all {@link State}s in the model
     */
    Set allStates;

    /**
     * {@link State} specified to check if it holds for the model. This is optional. If omitted, all states are checked. All the end to end tests contain a state to check, but for user inputted models/formulas, the state to check is optional. Will be null if omitted.
     */
    String stateToCheck;

    /**
     * {@link String} of the CTL formula checked. If the program got this far, the formula was validated and should be well formed.
     */
    String formula;

    /**
     * {@link Boolean} for whether the state to check actually held for the formula. Will be null if the state to check was omitted
     */
    Boolean stateToCheckHold;

    public ModelCheckResults(Set statesThatHold, Set allStates, String stateToCheck, String formula) {
        this.statesThatHold = statesThatHold;
        this.allStates = allStates;
        this.stateToCheck = stateToCheck;
        this.formula = formula;

        if (statesThatHold != null && stateToCheck != null) {
            if (containsStateName(statesThatHold, stateToCheck)) {
                stateToCheckHold = true;
            } else {
                stateToCheckHold = false;
            }
        }

    }

    public String getStateToCheck() {
        return stateToCheck;
    }

    public void setStateToCheck(String stateToCheck) {
        this.stateToCheck = stateToCheck;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Set getAllStates() {
        return allStates;
    }

    public void setStatesThatHold(Set statesThatHold) {
        this.statesThatHold = statesThatHold;
    }

    public Set getStatesThatHold() {
        return statesThatHold;
    }

    public void setAllStates(Set allStates) {
        this.allStates = allStates;
    }

    public Boolean getStateToCheckHold() {
        return stateToCheckHold;
    }

    public void setStateToCheckHold(Boolean stateToCheckHold) {
        this.stateToCheckHold = stateToCheckHold;
    }

}
