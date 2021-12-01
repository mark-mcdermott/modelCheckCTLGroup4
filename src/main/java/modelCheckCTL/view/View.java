package modelCheckCTL.view;

import modelCheckCTL.controller.types.kripke.State;
import modelCheckCTL.controller.types.misc.Options;
import modelCheckCTL.controller.types.modelCheckRelated.*;

import java.util.List;
import java.util.Set;

import static modelCheckCTL.utils.Utils.containsStateName;
import static modelCheckCTL.utils.Utils.handleError;

/**
 * Prints the output of the model checking to the terminal
 */
public class View {

    Options options;

    public View(Options options) {
        this.options = options;
    }

    /**
     * First prints the validation results and then the model check results
     * @param validationResults
     * @param modelCheckResults
     * @throws Exception
     */
    public void updateView(ValidationResults validationResults, ModelCheckResults modelCheckResults) throws Exception {
        printValidationResults(validationResults);
        printModelCheckResults(modelCheckResults);
    }

    /**
     * First prints the end to end test results, then the validation results and then the model check results
     * @param validationResults
     * @param modelCheckResults
     * @param allEndToEndTestResults
     * @throws Exception
     */
    public void updateView(ValidationResults validationResults, ModelCheckResults modelCheckResults, AllEndToEndTestResults allEndToEndTestResults) throws Exception {
        printEndToEndTestResults(allEndToEndTestResults);
        printValidationResults(validationResults);
        printModelCheckResults(modelCheckResults);
    }

    /**
     * Prints all end to end tests results only
     * @param allEndToEndTestResults
     * @throws Exception
     */
    public void updateView(AllEndToEndTestResults allEndToEndTestResults) throws Exception {
        printEndToEndTestResults(allEndToEndTestResults);
    }

    /**
     * Prints one end to end test results only
     * @param endToEndTestResult
     * @throws Exception
     */
    public void updateView(EndToEndTestResultWithValidation endToEndTestResult) throws Exception {
        printEndToEndTestResult(endToEndTestResult);
    }

    /**
     * Prints all the end to end test results. There's a lot here and this output is quite long. First prints model validation results (with descriptive error messages with line numbers on failed model validations), then formula validation and then the results of the model checking.
     * @param allEndToEndTestResults
     * @throws Exception
     */
    public void printEndToEndTestResults(AllEndToEndTestResults allEndToEndTestResults) throws Exception {
        List validateModelResultsList = allEndToEndTestResults.getValidateModelResultsList();
        List validateFormulaResultsList = allEndToEndTestResults.getValidateFormulaResultList();
        List endToEndTestResultsList = allEndToEndTestResults.getEndToEndTestResultsList();

        for (Object validateModelResultObj : validateModelResultsList) {
            ValidateModelResults validateModelResults = (ValidateModelResults) validateModelResultObj;
            Boolean passValidation = validateModelResults.getPassValidation();
            String originalErrorMessage = validateModelResults.getOriginalErrorMessage();
            String testFile = validateModelResults.getKripkeFilepath();

            if (!passValidation) {
                originalErrorMessage = originalErrorMessage;
                String newErrorMessage = "❌ failed parsing - " + originalErrorMessage;
                handleError(newErrorMessage,options.getPrintExceptions());
            } else {
                System.out.println("✅ passed parsing - " + testFile);
            }
        }

        for (Object validateFormulaResultsObj : validateFormulaResultsList) {
            modelCheckCTL.controller.types.modelCheckRelated.ValidateFormulaResults validateFormulaResults = (modelCheckCTL.controller.types.modelCheckRelated.ValidateFormulaResults) validateFormulaResultsObj;
            String formula = validateFormulaResults.getFormula();
            String formulaFilename = validateFormulaResults.getFormulaFilename();
            System.out.println("✅ passed parsing - Formula \"" + formula + "\" is well formed (\"" + formulaFilename + "\")");
        }

        for (Object endToEndTestResultObj : endToEndTestResultsList) {
            EndToEndTestResult endToEndTestResult = (EndToEndTestResult) endToEndTestResultObj;
            Boolean actualResult = endToEndTestResult.getActualResult();
            Boolean expectedResult = endToEndTestResult.getExpectedResult();
            Boolean testPass = endToEndTestResult.getTestPass();
            String formula = endToEndTestResult.getFormula();
            String stateToTest = endToEndTestResult.getStateToTest();

            if (testPass) {
                if (actualResult) {
                    System.out.println("✅ passed model checking - " + formula + " holds for " + stateToTest);
                } else if (!actualResult) {
                    System.out.println("✅ passed model checking - " + formula + " does not hold for " + stateToTest);
                }
            } else {
                if (expectedResult) {
                    System.out.println("❌ failed model checking - " + formula + " should hold for " + stateToTest + " but did not.");
                } else {
                    System.out.println("❌ failed model checking - " + formula + " should not hold for " + stateToTest + " but did");
                }
            }

        }
        System.out.println("-- end to end tests done --\n");
    }

    /**
     * Prints one end to end test result.
     * @param endToEndResultsObj
     * @throws Exception
     */
    public void printEndToEndTestResult(EndToEndTestResultWithValidation endToEndResultsObj) throws Exception {
        ValidateModelResults validateModelResults = endToEndResultsObj.getValidateModelResults();
        List validateFormulaResultsList = endToEndResultsObj.getValidateFormulaResultsList();
        List endToEndTestResultsList = endToEndResultsObj.getEndToEndTestResult();

        Boolean passModelValidation = validateModelResults.getPassValidation();
        String originalErrorMessage = validateModelResults.getOriginalErrorMessage();
        String testFile = validateModelResults.getKripkeFilepath();

        if (!passModelValidation) {
            originalErrorMessage = originalErrorMessage;
            String newErrorMessage = "❌ failed parsing - " + originalErrorMessage;
            handleError(newErrorMessage,options.getPrintExceptions());
        } else {
            System.out.println("✅ passed parsing - " + testFile);
        }

        for (Object validateFormulaResultsObj : validateFormulaResultsList) {
            modelCheckCTL.controller.types.modelCheckRelated.ValidateFormulaResults validateFormulaResults = (modelCheckCTL.controller.types.modelCheckRelated.ValidateFormulaResults) validateFormulaResultsObj;
            String formulaForValidation = validateFormulaResults.getFormula();
            String formulaFilename = validateFormulaResults.getFormulaFilename();
            System.out.println("✅ passed parsing - Formula \"" + formulaForValidation + "\" is well formed (\"" + formulaFilename + "\")");
        }

        for (Object endToEndTestResultObj : endToEndTestResultsList) {
            EndToEndTestResult endToEndTestResult = (EndToEndTestResult) endToEndTestResultObj;
            Boolean actualResult = endToEndTestResult.getActualResult();
            Boolean expectedResult = endToEndTestResult.getExpectedResult();
            Boolean testPass = endToEndTestResult.getTestPass();
            String formula = endToEndTestResult.getFormula();
            String stateToTest = endToEndTestResult.getStateToTest();

            if (testPass) {
                if (actualResult) {
                    System.out.println("✅ passed model checking - " + formula + " holds for " + stateToTest);
                } else if (!actualResult) {
                    System.out.println("✅ passed model checking - " + formula + " does not hold for " + stateToTest);
                }
            } else {
                if (expectedResult) {
                    System.out.println("❌ failed model checking - " + formula + " should hold for " + stateToTest + " but did not.");
                } else {
                    System.out.println("❌ failed model checking - " + formula + " should not hold for " + stateToTest + " but did");
                }
            }

        }
    }

    /**
     * Prints the results of validating an individual model/formula/state-to-check
     * @param validationResults
     * @throws Exception
     */
    public void printValidationResults(ValidationResults validationResults) throws Exception {
        ValidateModelResults validateModelResults = validationResults.getValidateModelResults();
        modelCheckCTL.controller.types.modelCheckRelated.ValidateFormulaResults validateFormulaResults = validationResults.getValidateFormulaResults();
        ValidateStateToCheckResults validateStateToCheckResults = validationResults.getValidateStateToCheckResults();

        // model validation vars
        Boolean modelPassValidation = validateModelResults.getPassValidation();
        String modelFilepath = validateModelResults.getKripkeFilepath();
        String modelParsingErrorMessage = validateModelResults.getOriginalErrorMessage();

        // formula validation vars
        Boolean formulaPassValidation = validateFormulaResults.getPassValidation();
        String formula = validateFormulaResults.getFormula();

        Boolean stateToCheckPassValidation = null;
        String stateToCheck = null;
        // state to check validation vars
        if (options.getStateToCheckStr() != null && !options.getStateToCheckStr().equals("")) {
            stateToCheckPassValidation = validateStateToCheckResults.getStateToCheckPass();
            stateToCheck = validateStateToCheckResults.getStateToCheck();
        }

        // print model validation results
        if (modelPassValidation) {
            System.out.println("✅ model passed parsing - no syntax errors in " + modelFilepath);
        } else {
            String newErrorMessage = "❌ failed parsing - " + modelParsingErrorMessage;
            handleError(newErrorMessage,true);
        }

        // print formula validation results
        if (formulaPassValidation) {
            System.out.println("✅ formula passed parsing - \"" + formula + "\" is well formed");
        } else {
            // program halts with exception and detailed error message if formula isn't well formed
        }

        // print state to check validation results
        if (options.getStateToCheckStr() != null && !options.getStateToCheckStr().equals("")) {
            if (stateToCheckPassValidation) {
                System.out.println("✅ state to check " + stateToCheck + " is in the model");
            } else {
                System.out.println("❌ state to check " + stateToCheck + " is not in the model");
            }
        }
    }

    /**
     * Prints the results of an individual model check
     * @param modelCheckResults
     */
    public void printModelCheckResults(ModelCheckResults modelCheckResults) {
        Set statesThatHold = modelCheckResults.getStatesThatHold();
        Set allStates = modelCheckResults.getAllStates();
        String stateToCheck = modelCheckResults.getStateToCheck();
        String formula = modelCheckResults.getFormula();
        Boolean stateToCheckHold = modelCheckResults.getStateToCheckHold();
        if (stateToCheck != null) {
            if (stateToCheckHold) {
                System.out.println("✅ " + stateToCheck + " holds for " + formula);
            } else {
                System.out.println("❌ " + stateToCheck + " does not hold for " + formula);
            }
        } else {
            for (Object stateObj : allStates) {
                State state = (State) stateObj;
                String stateStr = state.toString();
                if (containsStateName(statesThatHold,stateStr)) {
                   System.out.println("✅ " + stateStr + " holds for " + formula);
                } else {
                    System.out.println("❌ " + stateStr + " does not hold for " + formula);
                }
            }
        }
    }

}
