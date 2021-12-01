package modelCheckCTL.controller.types.modelCheckRelated;

/**
 * Data structure holding the results of validating a state to check (checking if the state to check is in the model)
 */
public class ValidateStateToCheckResults {

    /**
     * {@link Boolean} of whether the state to check is in the model or not
     */
    Boolean stateToCheckPass;

    /**
     * {@link String} of the state to check
     */
    String stateToCheck;

    public ValidateStateToCheckResults(Boolean stateToCheckPass, String stateToCheck) {
        this.stateToCheckPass = stateToCheckPass;
        this.stateToCheck = stateToCheck;
    }

    public Boolean getStateToCheckPass() {
        return stateToCheckPass;
    }

    public void setStateToCheckPass(Boolean stateToCheckPass) {
        this.stateToCheckPass = stateToCheckPass;
    }

    public String getStateToCheck() {
        return stateToCheck;
    }

    public void setStateToCheck(String stateToCheck) {
        this.stateToCheck = stateToCheck;
    }

}
