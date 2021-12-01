package modelCheckCTL.controller.types.modelCheckRelated;

/**
 * Data structure storing information from the end to end formula test files (the state to test, the formula and the expected result)
 */
public class EndToEndFormulaFileObj {

    /**
     * {@link String} of name of state to test (ie. s0). The name must start with "s" and have a number after it. The first state in kripke can either be s0 or s1 (ie, the kripke states list can be zero or one based)
     */
    String stateToTest;

    /**
     * {@link String} of the CTL formula to be tested. I believe the formulas in the end to end test files are all well formed, but the don't necessarily have to be
     */
    String formula;

    /**
     * {@link Boolean} of whether the state specified to test is supposed to hold for the formula. True means it is supposed to hold, false mean it is not supposed to hold.
     */
    Boolean expected;

    public EndToEndFormulaFileObj(String stateToTest, String formula, Boolean expected) {
        this.stateToTest = stateToTest;
        this.formula = formula;
        this.expected = expected;
    }

    public void setExpected(Boolean expected) {
        this.expected = expected;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setStateToTest(String stateToTest) {
        this.stateToTest = stateToTest;
    }

    public String getFormula() {
        return formula;
    }

    public Boolean getExpected() {
        return expected;
    }

    public String getStateToTest() {
        return stateToTest;
    }
}
