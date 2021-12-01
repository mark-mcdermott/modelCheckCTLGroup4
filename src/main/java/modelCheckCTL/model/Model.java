package modelCheckCTL.model;

import modelCheckCTL.controller.types.modelCheckRelated.AllEndToEndTestResults;
import modelCheckCTL.controller.types.modelCheckRelated.EndToEndTestResultWithValidation;
import modelCheckCTL.controller.types.modelCheckRelated.ModelCheckResults;
import modelCheckCTL.controller.types.modelCheckRelated.ValidationResults;

import java.util.List;

/**
 * Holds the validation results and model check results (both for individual user entered models/formulas) and end to end test results
 * The model is only used in runProgram in Controller.java where it stores the results of validation/model-checking/end-to-end-tests and then the model is immediately sent to the view.
 */
public class Model {

    /**
     * {@link ValidationResults} is a custom object that holds three more custom objects - ValidateModelResults, ValidateFormulaResults and ValidateStateToCheckResults.
     * These contain the results of model validation (making sure there are no syntax errors in the model), formula validation (making sure there are no syntax errors in the formula) and state to check validation (making sure the state specified to check is actually in the model).
     * validationResults has the validation results of a single user entered model/formula/state-to-check.
     */
    ValidationResults validationResults;

    /**
     * {@link ModelCheckResults} is a custom object that holds the details of a model check after its run.
     * It has the states that hold for the property, all the states in the model, the specified state to check (if specified. will be null otherwise), the formula and a Boolean for whether the state to be checked (if specified. will be null otherwise) actually did hold for the property.
     */
    ModelCheckResults modelCheckResults;

    /**
     * {@link AllEndToEndTestResults} is a custom object containing three {@link List}s - validateModelResultsList, validateFormulaResultList, endToEndTestResultsList. validateModelResultsList is a list of the results of validating the test model files (making sure there are no syntax errors in the models) - this contains covers both the valid model files (meant to pass validation) and the invalid model files (meant to fail validation, with descriptive errors including line numbers). validateFormulaResultList is a list of the results of validating the formulas in the test files (making sure there are no syntax errors in the formulas. validateModelResultsList is a list of the results of each model check test in the test files.
     */
    AllEndToEndTestResults allEndToEndTestResults;

    /**
     * {@link EndToEndTestResultWithValidation} is a custom object containing validateModelResults, validateFormulaResult, endToEndTestResults.
     */
    EndToEndTestResultWithValidation endToEndTestResult;

    public ValidationResults getValidationResults() {
        return validationResults;
    }

    public void setValidationResults(ValidationResults validationResults) {
        this.validationResults = validationResults;
    }

    public ModelCheckResults getModelCheckResults() {
        return modelCheckResults;
    }

    public void setModelCheckResults(ModelCheckResults modelCheckResults) {
        this.modelCheckResults = modelCheckResults;
    }

    public AllEndToEndTestResults getAllEndToEndTestResults() {
        return allEndToEndTestResults;
    }

    public void setAllEndToEndTestResults(AllEndToEndTestResults allEndToEndTestResults) {
        this.allEndToEndTestResults = allEndToEndTestResults;
    }

    public void setEndToEndTestResult(EndToEndTestResultWithValidation endToEndTestResult) {
        this.endToEndTestResult = endToEndTestResult;
    }

    public EndToEndTestResultWithValidation getEndToEndTestResult() {
        return endToEndTestResult;
    }
}
