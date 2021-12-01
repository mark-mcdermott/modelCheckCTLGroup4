package modelCheckCTL.controller.types.modelCheckRelated;

import java.util.List;

/**
 * AllEndToEndTestResults is a custom object containing three {@link List}s - validateModelResultsList, validateFormulaResultList, endToEndTestResultsList. validateModelResultsList is a list of the results of validating the test model files (making sure there are no syntax errors in the models) - this contains covers both the valid model files (meant to pass validation) and the invalid model files (meant to fail validation, with descriptive errors including line numbers). validateFormulaResultList is a list of the results of validating the formulas in the test files (making sure there are no syntax errors in the formulas. validateModelResultsList is a list of the results of each model check test in the test files.
 */
public class AllEndToEndTestResults {

    /**
     * validateModelResultsList is a list of the results of validating the test model files (making sure there are no syntax errors in the models) - this contains covers both the valid model files (meant to pass validation) and the invalid model files (meant to fail validation, with descriptive errors including line numbers)
     */
    List validateModelResultsList;

    /**
     * validateFormulaResultList is a list of the results of validating the formulas in the test files (making sure there are no syntax errors in the formulas
     */
    List validateFormulaResultList;

    /**
     *  endToEndTestResultsList is a list of the results of individual model checking tests of the test files. These each contain the expected outcome, the actual outcome, whether the test passed or failed as well as all the details of the test like the formula, the state to check and the {@link ModelCheckResults} which has the states that hold as well as all the states in the model.
     */
    List endToEndTestResultsList;

    public AllEndToEndTestResults(List validateModelResultsList, List validateFormulaResultList, List endToEndTestResultsList) {
        this.validateModelResultsList = validateModelResultsList;
        this.validateFormulaResultList = validateFormulaResultList;
        this.endToEndTestResultsList = endToEndTestResultsList;
    }

    public List getEndToEndTestResultsList() {
        return endToEndTestResultsList;
    }

    public List getValidateFormulaResultList() {
        return validateFormulaResultList;
    }

    public List getValidateModelResultsList() {
        return validateModelResultsList;
    }

    public void setEndToEndTestResultsList(List endToEndTestResultsList) {
        this.endToEndTestResultsList = endToEndTestResultsList;
    }

    public void setValidateFormulaResultList(List validateFormulaResultList) {
        this.validateFormulaResultList = validateFormulaResultList;
    }

    public void setValidateModelResultsList(List validateModelResultsList) {
        this.validateModelResultsList = validateModelResultsList;
    }

}
