package modelCheckCTL.controller.types.modelCheckRelated;

/**
 * Data structure for storing the results of one end to end test. Stores the ModelCheckResults object (which contains states that hold, all states, the state to check, the formula, and a boolean of whether the state to test holds for the formula) as well as the expected result of the test, the actual result, a Boolean for whether the test passed or failed as well as some test details like the formula and the state to test
 */
public class EndToEndTestResult {

    /**
     * A data structure containing states that hold, all states, the optional state to check, the formula, and a boolean of whether the optional state to test holds for the formula
     */
    modelCheckCTL.controller.types.modelCheckRelated.ModelCheckResults modelCheckResults;

    /**
     * A {@link Boolean} of the expected result of the test. This is pulled from the test file. True here means the state specified to test was supposed to hold for the formula, false means it was expected not to hold.
     */
    Boolean expectedResult;

    /**
     * A {@link Boolean} of the actual result of the test. True here means the state specified to test held for the formula, false means it did not hold
     */
    Boolean actualResult;

    /**
     * A {@link Boolean} of whether the actual result matched the expected result of the test. True here means the actual matched the expected, false means the actual did not match the expected
     */
    Boolean testPass;

    /**
     * A {@link String} of the CTL formula tested. Ie, EXp
     */
    String formula;

    /**
     * A {@link String} of the state to test. Ie, s0
     */
    String stateToTest;

    public EndToEndTestResult(modelCheckCTL.controller.types.modelCheckRelated.ModelCheckResults modelCheckResults, Boolean expectedResult, Boolean actualResult, Boolean testPass, String formula, String stateToTest) {
        this.modelCheckResults = modelCheckResults;
        this.expectedResult = expectedResult;
        this.actualResult = actualResult;
        this.testPass = testPass;
        this.formula = formula;
        this.stateToTest = stateToTest;
    }

    public modelCheckCTL.controller.types.modelCheckRelated.ModelCheckResults getModelCheckResults() {
        return modelCheckResults;
    }

    public void setModelCheckResults(modelCheckCTL.controller.types.modelCheckRelated.ModelCheckResults modelCheckResults) {
        this.modelCheckResults = modelCheckResults;
    }

    public Boolean getActualResult() {
        return actualResult;
    }

    public void setActualResult(Boolean actualResult) {
        this.actualResult = actualResult;
    }

    public Boolean getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(Boolean expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Boolean getTestPass() {
        return testPass;
    }

    public void setTestPass(Boolean testPass) {
        this.testPass = testPass;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getStateToTest() {
        return stateToTest;
    }

    public void setStateToTest(String stateToTest) {
        this.stateToTest = stateToTest;
    }

}
