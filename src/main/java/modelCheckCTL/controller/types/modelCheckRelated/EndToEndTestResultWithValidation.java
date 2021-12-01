package modelCheckCTL.controller.types.modelCheckRelated;

import java.util.List;

public class EndToEndTestResultWithValidation {
    ValidateModelResults validateModelResults;
    List validateFormulaResultsList;
    List endToEndTestResult;

    public EndToEndTestResultWithValidation(ValidateModelResults validateModelResults, List validateFormulaResultsList, List endToEndTestResult) {
        this.validateModelResults = validateModelResults;
        this.validateFormulaResultsList = validateFormulaResultsList;
        this.endToEndTestResult = endToEndTestResult;
    }

    public ValidateModelResults getValidateModelResults() {
        return validateModelResults;
    }

    public List getValidateFormulaResultsList() {
        return validateFormulaResultsList;
    }

    public List getEndToEndTestResult() {
        return endToEndTestResult;
    }

}
