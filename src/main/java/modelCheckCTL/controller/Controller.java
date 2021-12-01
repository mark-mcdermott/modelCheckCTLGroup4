package modelCheckCTL.controller;

import modelCheckCTL.controller.ctl.Validator.ParseException;
import modelCheckCTL.controller.ctl.Validator.Validator;
import modelCheckCTL.controller.types.kripke.KripkeFileObj;
import modelCheckCTL.controller.types.misc.Options;
import modelCheckCTL.controller.types.misc.TestFiles;
import modelCheckCTL.controller.types.modelCheckRelated.*;
import modelCheckCTL.model.Model;
import modelCheckCTL.controller.types.kripke.Kripke;
import modelCheckCTL.controller.types.kripke.State;
import modelCheckCTL.controller.types.kripke.Transition;
import modelCheckCTL.view.View;
import modelCheckCTL.controller.ctl.Parser.Parser;

import java.io.*;
import java.util.*;


import static modelCheckCTL.controller.types.modelCheckRelated.FormulaInputSource.ARGUMENT;
import static modelCheckCTL.controller.types.modelCheckRelated.FormulaInputSource.FILE;
import static modelCheckCTL.utils.Utils.*;
import static modelCheckCTL.utils.Utils.contains;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.*;

/**
 * Runs end to end tests, reads input files, validates the model and formula, and runs the model checker
 */
public class Controller {

    /**
     * The model (MVC "model", not CTL "model" in this case) stores the kripke filename, the ctl model to test, the state to check, the command line arguments and the Kripke data
     */
    private Model model;

    /**
     * The view in this version is just a rudimentary command line output that says if the formula holds for the model and tries to give meaningful error messages if anything failed along the way.
     * Hoping to add Java Swing and Jung to this in a future version for a GUI and directed graph visualization.
     */
    private View view;

    /**
     * name of test file directory inside resources/
     */
    private String testFilesDir = "end-to-end-tests";


    /**
     * Kicks off the program after being called from Main
     * @param model The model (MVC "model", not CTL "model" in this case) stores the Kripke filename, the ctl model to test, the state to check, the command line arguments and the Kripke data
     * @param view The view in this version is just a rudimentary command line output that says if the state(s) hold for the model and tries to give meaningful error messages if anything failed along the way.
     * @param options Two command line arguments are mandatory: -k <kripke file> specifying the Kripke filename and then either -a <model> or -f <model filename>. There is an optional -s <state name> argument specifying a state to check.
     * @throws IOException
     */
    public Controller(Model model, View view, Options options) throws Exception {
        if (model == null || view == null || options == null) { throw new NullPointerException("A param to Controller constructor is null"); }
        this.model = model;
        this.view = view;
        runProgram(options);
    }




    // MAIN PROGRAM

    /**
     * This is the meat and potatoes of the program - all the major function calls are here. Proecesses the arguments, runs tests, runs the model checking
     * @param options Two command line arguments are mandatory: -k <kripke file> specifying the Kripke filename and then either -a <formula> or -f <formula filename>. There is an optional -s <state name> argument specifying a state to check.
     * @throws IOException
     */
    public void runProgram(Options options) throws Exception {

        // declare vars
        Boolean runEndToEndTests = options.getRunEndToEndTests();
        Boolean runAllEndToEndTests = options.getRunAllEndToEndTests();
        Boolean runOnlyEndToEndTests = options.getRunOnlyEndToEndTests();
        Boolean runOnlyMicrowave = options.getRunOnlyMicrowave();
        Set statesThatHold = null;
        Set allStates = null;
        if (!runOnlyEndToEndTests) {
            if (runOnlyMicrowave) {
                EndToEndTestResultWithValidation endToEndTestResult = runEndToEndTest("Microwave.txt", "Microwave - Test Formulas.txt", options);
                model.setEndToEndTestResult(endToEndTestResult);
            } else {
                allStates = getKripkeFileObj(options.getKripkeFilepath()).getStates();
            }
        }
        String stateToCheck = options.getStateToCheckStr();
        // String formula = options.getFormula();

        // check for null options param and run end to end tests, if specified
        if (options == null) { throw new NullPointerException("runProgram options param is null"); }
        if (runEndToEndTests) {
            if (runAllEndToEndTests) {
                AllEndToEndTestResults allEndToEndTestResults = runAllEndToEndTests(options);
                model.setAllEndToEndTestResults(allEndToEndTestResults);
            } else {
                Integer numTestFile = options.getEndToEndTestNum() - 1;
                TestFiles testFiles = options.getEndToEndTests();
                String kripkeFilepath = (String) testFiles.getKripkesValid().get(numTestFile);
                String formulasFilename = (String) testFiles.getFormulas().get(numTestFile);
                EndToEndTestResultWithValidation endToEndTestResult = runEndToEndTest(kripkeFilepath, formulasFilename, options);
                model.setEndToEndTestResult(endToEndTestResult);
            }
        }

        if (!options.getRunOnlyEndToEndTests() && !options.getRunOnlyMicrowave()) {
            // run validation (validate the model, the formula and the state to check)
            ValidationResults validationResults = validateModelFormulaAndStateToCheck(options);
            model.setValidationResults(validationResults);

            // run model checking
            statesThatHold = modelCheck(options.getKripkeFilepath(), getFormula(options));
            ModelCheckResults modelCheckResults = new ModelCheckResults(statesThatHold, allStates, stateToCheck, getFormula(options));
            model.setModelCheckResults(modelCheckResults);
        }

        // update view
        if (runOnlyEndToEndTests || runOnlyMicrowave) {
            if (options.getRunAllEndToEndTests()) {
                view.updateView(model.getAllEndToEndTestResults());
            } else {
                view.updateView(model.getEndToEndTestResult());
            }
        } else {
            if (runEndToEndTests) {
                view.updateView(model.getValidationResults(), model.getModelCheckResults(), model.getAllEndToEndTestResults());
            } else {
                view.updateView(model.getValidationResults(), model.getModelCheckResults());
            }
        }

    }




    // MODEL CHECKING

    /**
     * Model checks a single supplied formula on a single supplied model. Returns the {@link Set} of {@link State}s which hold for the formula.
     * @param kripkeFilepath {@link String} of the file/filepath of the kripke file. Will be like kripke.txt if the file is in /resources or like end-to-end-tests/kripe.txt if in a subfolder of /resources.
     * @param formula {@link String} CTL formula to model check
     * @return the {@link Set} of {@link State}s which hold for the formula.
     * @throws IOException
     * @throws modelCheckCTL.controller.ctl.Parser.ParseException
     */
    public Set modelCheck(String kripkeFilepath, String formula) throws IOException, modelCheckCTL.controller.ctl.Parser.ParseException {
        KripkeFileObj kripkeFileObj = getKripkeFileObj(kripkeFilepath);
        ModelCheckInputs modelCheckInputs = new ModelCheckInputs(kripkeFileObj.getKripke(), formula);
        Parser parser = new Parser(modelCheckInputs);
        Set statesThatHold = parser.Parse();
        return statesThatHold;
    }




    // VALIDATION (OF INDIVIDUAL MODEL/FORMULA/STATES, NOT VALIDATION OF THE END TO END TESTS)

    /**
     * Validates an individual model, formula and state to check for syntax errors (in the case of model/formula) and checks if the state to check is in the model
     * @param options {@link Options} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @return A {@link ValidationResults} results object, which contains three custom results objects, one for the model validation results, one for the formula validation results and one for the state to check validation results
     * @throws Exception
     */
    public ValidationResults validateModelFormulaAndStateToCheck(Options options) throws Exception {
        ValidateModelResults validateModelResults = validateModel(options);
        ValidateFormulaResults validateFormulaResults = validateFormula(getFormula(options));
        ValidateStateToCheckResults validateStateToCheckResults = null;
        if (options.getStateToCheckStr() != null) {
            validateStateToCheckResults = validateStateToCheck(options.getStateToCheckStr(), getKripkeFileObj(options.getKripkeFilepath()).getStates());
        }
        ValidationResults validationResults = new ValidationResults(validateModelResults,validateFormulaResults,validateStateToCheckResults);
        return validationResults;
    }

    /**
     * Validates an individual model to check for any syntax errors
     * @param options {@link Options} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @return A {@link ValidateModelResults} results object, which contains a Boolean for whether the model passed validation or not, the kripke filepath and any original error message which may have occurred in the original parsing of the kripke file
     * @throws Exception
     */
    public ValidateModelResults validateModel(Options options) throws Exception {
        Boolean passValidation = null;
        String originalErrorMessage = "";
        String kripkeFilePath = options.getKripkeFilepath();
        KripkeFileObj kripkeFileObj = getKripkeFileObj(kripkeFilePath);
        if (kripkeFileObj.getErrorMessage() != null) {
            passValidation = false;
            originalErrorMessage = kripkeFileObj.getErrorMessage();
        } else {
            passValidation = true;
        }
        ValidateModelResults validateModelResults = new ValidateModelResults(passValidation,originalErrorMessage,kripkeFilePath);
        return validateModelResults;
    }

    /**
     * Validates an individual formula to check for any syntax errors (ie, whether it is well formed)
     * @param formula {@link String} the formula to check for syntax errors
     * @return A {@link ValidateFormulaResults} results object, which contains a Boolean for whether the formula passed validation or not, the formula, the formula filename and an errors which occured during validation
     * @throws Exception
     */
    public ValidateFormulaResults validateFormula(String formula) throws UnsupportedEncodingException, ParseException {
        Boolean passValidation = null;
        String error = "";
        InputStream stringStream = new ByteArrayInputStream(formula.getBytes("UTF-8"));
        Validator validator = new Validator(stringStream);
        validator.Validate();
        passValidation = true;
        return new ValidateFormulaResults(passValidation,formula,error);
    }

    /**
     * Checks whether the specified {@link State} to check is in the model
     * @param {@link String} Name of the state to check (ie, s0)
     * @param {@link Set} of all {@link State}s in the model
     * @return A {@link ValidateStateToCheckResults} results object, which contains a Boolean for whether the state to check is in the model and also the name of the state to check
     * @throws Exception
     */
    public ValidateStateToCheckResults validateStateToCheck(String stateToCheck, Set statesInModel) {
        Boolean stateToCheckPass = null;
        if (containsStateName(statesInModel, stateToCheck)) {
            stateToCheckPass = true;
        } else {
            stateToCheckPass = false;
        }
        return new ValidateStateToCheckResults(stateToCheckPass, stateToCheck);
    }




    // PARSING INPUT FILES

    /**
     * Gets the user inputted CTL formula, regardless if formula was supplied directly in the command line arguments or in a file containing just the formula
     * @param options {@link Options} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @return {@link String} CTL formula, ie. EXp
     * @throws IOException
     */
    public String getFormula(Options options) throws IOException {
        String formula = "";
        if (options.getFormulaInputSource() == FILE) {
            formula = getFormulaFromUserFile(options.getFormulaInputFilename());
        } else if (options.getFormulaInputSource() == ARGUMENT) {
            formula = options.getFormula();
        }
        if (formula.equals("")) {
            throw new IOException("formula in getFormula() appears blank");
        }
        return formula;
    }

    /**
     * Parses a specified text file (which only contains a CTL formula in the first line and has no additional lines) for a CTL formula and returns the formula. Does not check to make sure the formula is well formed (that happens later).
     * Read input stream line by line with BufferedReader approach from https://stackoverflow.com/a/55420102, accessed 9/18/21
     * @param filename {@link String} of filename to find the CTL formula. This file should be in /resources
     * @return {@link String} of the CTL formula in the text file (ie, EXp)
     * @throws IOException
     */
    public String getFormulaFromUserFile(String filename) throws IOException {
        String formula = "";
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            String stateToCheck = "";
            Boolean expected = null;
            if (inputStream == null) {
                throw new IllegalArgumentException("file not found! " + filename);
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String rawLine = "";
                    while ((rawLine = reader.readLine()) != null) {
                        formula = rawLine;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (formula.equals("")) {
            throw new IOException("formula in " + filename + " appears blank (getFormulaFromUserFile())");
        }
        return formula;
    }



    /**
     * Parses a text file containing information for a specific Kripke structure and returns a {@link Kripke} with all that info.
     * Kripke text file must be in a format like this:
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
     * @param kripkeFilepath {@link String} filename of a kripke text file
     * @return A {@link Kripke} object
     * @throws IOException
     */
    public KripkeFileObj getKripkeFileObj(String kripkeFilepath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = null;
        inputStream = classLoader.getResourceAsStream(kripkeFilepath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        Set states = new HashSet<State>();
        Set transitions  = new HashSet<Transition>();
        Boolean parsedKripkeLabelsLine = false;
        KripkeFileObj kripkeFileObj = new KripkeFileObj();
        kripkeFileObj.setKripkeFilepath(kripkeFilepath);
        kripkeFileObj.setStates(states);
        kripkeFileObj.setTransitions(transitions);
        kripkeFileObj.setLineNum(1);
        while (reader.ready()) {
            String line = reader.readLine();
            line = line.trim();
            char firstChar = line.charAt(0);

            // first line should always be state line
            if (kripkeFileObj.getLineNum() == 1) {
                line = removeByteOrderMark(line);
                kripkeFileObj = parseKripkeStates(kripkeFileObj, line); }
            // after first line, if it starts with a "t", it's a transition line
            else if (firstChar == 't') {
                if (kripkeFileObj.getErrorMessage() == null) {
                    kripkeFileObj = parseKripkeTransitionLine(kripkeFileObj, line);
                }
            }
            // and if it starts with an s it's a labels line
            else if (firstChar == 's') {
                kripkeFileObj = parseKripkeLabelsLine(kripkeFileObj, line);
                parsedKripkeLabelsLine = true;
            }
            int curLineNum = kripkeFileObj.getLineNum();
            curLineNum++;
            kripkeFileObj.setLineNum(curLineNum);
        }
        Kripke kripke = new Kripke(kripkeFileObj.getStates(), kripkeFileObj.getTransitions());
        if (parsedKripkeLabelsLine == false) {
            kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": no labels line found (a label line example could be: \"s1 : p;\").");
        }
        kripkeFileObj.setKripke(kripke);
        return kripkeFileObj;
    }

    /**
     * Gets a {@link Set} of labels from a line in the Kripke text file. Return is void void because it's modifying an existing state (adding labels to it), which is why all the states are passed as a param. The line must be in one the following type of formats:
     * s1 : p q, (propositional atom names are separated by a space; a name consists of letters, it is casesensitive)
     * s2 : q t r,
     * s3 : , (i.e. set of propositional atoms for state s3 is empty)
     * s4 : t; (will end in a comma if not the last line, or a semicolon if it is the last line)
     * @param line {@link String} a labels line from a Kripke text file. Must be in a format like "s2 : q t r,". Case sensitive and spaces matter. Can end in a comma or semicolon.
     * @param kripkeFileObj
     * @throws IOException
     */
    private static KripkeFileObj parseKripkeLabelsLine(KripkeFileObj kripkeFileObj, String line) throws IOException {
        String[] lineArr = line.split(" ",0);
        String stateName = lineArr[0];
        stateName = stateName.replace(",","");
        Integer stateNum = parseInt(stateName.replace("s",""));
        if (!contains(kripkeFileObj.getStates(),new State(stateNum))) { kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": state \"" + stateName + "\" not found in kripke states."); }
        Set labels = new HashSet<Character>();
        lineArr[lineArr.length - 1] = lineArr[lineArr.length - 1].replace(",","");
        lineArr[lineArr.length - 1] = lineArr[lineArr.length - 1].replace(";","");
        int lineArrElemNum = 0;
        for (Object lineArrElemObj : lineArr) {
            String lineArrElem = (String) lineArrElemObj;
            if (lineArrElemNum != 0 && lineArrElemNum != 1) { // skip state name and colon and start at labels
                String label = lineArrElem;
                label = label.replace(",","");
                label = label.replace(";","");
                if (labels.contains(label)) { kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": label \"" + label + "\" already exists in state \"" + stateName + "\"."); }
                labels.add(label);
            }
            lineArrElemNum++;
        }
        if (contains(kripkeFileObj.getStates(),new State(stateNum))) {
            getState(stateNum, kripkeFileObj.getStates()).setLabels(labels);
        }
        return kripkeFileObj;
    }

    /**
     * Gets all the states in a Kripke from the text file of the Kripke. The line must be in a format like this: "s1, s2, s3, s4;" where the states are separated by a comma and a space and the last state is followed by a semicolon.
     * @param line {@link String} line from a Kripke text file (the first line). The line must be in a format like this: "s1, s2, s3, s4;" where the states are separated by a comma and a space and the last state is followed by a semicolon.
     * @return A {@link Set} representing all the {@link State}s specified in the line.
     */
    private static KripkeFileObj parseKripkeStates(KripkeFileObj kripkeFileObj, String line) throws IOException {
        Set states = new HashSet<State>();
        line = line.trim();
        String[] stateStrings = line.split(",",0);
        for (Object stateObj : stateStrings) {
            String stateStr = (String) stateObj;
            stateStr = stateStr.trim();
            stateStr = stateStr.replace(",","");
            stateStr = stateStr.replace(";","");
            stateStr = stateStr.replace("s","");
            Integer stateInt = parseInt(stateStr);
            State newState = new State(stateInt);
            if (contains(states, newState)) {
                kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line 1: duplicate state \"" + newState.toString() + "\" found.");
            }
            states.add(newState);
        }
        kripkeFileObj.setStates(states);
       return kripkeFileObj;
    }

    /**
     * Gets a single transition from one line of the Kripke text file. The line must be in the format like "t4 : s4 – s2,", which is the transition name and the from and to states of the transition respectively. The spaces matter and the transition line must end in a comma or semicolon.
     * The from and to states are actual references to the states in the Kripe. Ie, they have the same memory address and are not a copy. For a text output that shouldn't matter, but for a graphical output rendering the directed graph, that will be necessary.
     * @param line One {@link String} line of the Kripke text file. Any line that starts with "t" is a transition. The line must be in the format like "t4 : s4 – s2,", which is the transition name and the from and to states of the transition respectively. The spaces matter and the transition line must end in a comma or semicolon.
     * @return A {@link Transition} of the transition name and its from and to states.
     */
    private static KripkeFileObj parseKripkeTransitionLine(KripkeFileObj kripkeFileObj, String line) throws IOException {
        String[] transitionLineArr = line.split(" ",0); // ie, ["t1",":","s1","-","s2,"]
        String transitionName = transitionLineArr[0];
        if (transitionLineArr.length > 1) {
            if (!transitionLineArr[1].equals(":")) {
                kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": no colon found in transition line (a correct transition line example could be: \"t1 : s1 – s2;\").");
            }
        }
        if (kripkeFileObj.getErrorMessage() == null) {
            String fromName = transitionLineArr[2];
            if (transitionLineArr.length < 4) {
                kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": no destination state found in transition line (a correct transition line example could be: \"t1 : s1 – s2;\").");
            }
            if (kripkeFileObj.getErrorMessage() == null) {
                String toName = transitionLineArr[4];
                toName = toName.replace(",", "");
                toName = toName.replace(";", "");
                Integer transitionNum = parseInt(transitionName.replace("t", ""));
                Integer fromNum = parseInt(fromName.replace("s", ""));
                Integer toNum = parseInt(toName.replace("s", ""));
                State fromState = new State(fromNum);
                if (!contains(kripkeFileObj.getStates(),fromState)) { kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": transition from state (\"" + fromName + "\") not found in kripke states."); }
                State toState = new State(toNum);
                if (!contains(kripkeFileObj.getStates(),toState)) { kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": transition to state (\"" + toName + "\") not found in kripke states."); }
                Transition newTransition = new Transition(transitionNum, fromState, toState);
                for (int i = 1; i <= kripkeFileObj.getTransitions().size(); i++) {
                    Transition thisTransition = getTransition(i, kripkeFileObj.getTransitions());
                    if (thisTransition.getFrom().getNumber() == newTransition.getFrom().getNumber()) {
                        if (thisTransition.getTo().getNumber() == newTransition.getTo().getNumber()) {
                            kripkeFileObj.setErrorMessage(kripkeFileObj.getKripkeFilepath() + ": Syntax error on line " + kripkeFileObj.getLineNum() + ": duplicate transition found (\"" + thisTransition.toStringDetailed() + "\" and \"" + newTransition.toStringDetailed() + "\").");
                        }
                    }
                }
                fromState = getState(fromNum,kripkeFileObj.getStates());
                fromState.addTransition(newTransition);
                kripkeFileObj.getTransitions().add(newTransition);
            }
        }
        return kripkeFileObj;
    }




    // END TO END TESTS

    /**
     * Runs all the end to end tests in the /resources folder. Validates the models and formulas to make sure they don't contain syntax errors and checks if the states to check are in the models. Then model checks all the test files.
     * @param {@link Option} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @return {@link AllEndToEndTestResults} object which has three {@link List}s - one of the results of the model validations, one of the results of the formula validations and one with the results of the model checks
     * @throws Exception
     */
    private AllEndToEndTestResults runAllEndToEndTests(Options options) throws Exception {
        List validateModelResultsList = validateEndToEndTestModels(options);
        List validateFormulaResultList = validateEndToEndFormulas(options);
        List endToEndTestResultsList = modelCheckEndToEndTests(options);
        AllEndToEndTestResults allEndToEndTestResults = new AllEndToEndTestResults(validateModelResultsList, validateFormulaResultList, endToEndTestResultsList);
        return allEndToEndTestResults;
    }

    /**
     * Runs one end to end tests in the /resources folder. Validates the models and formulas to make sure they don't contain syntax errors and checks if the states to check are in the models. Then model checks all the test files.
     * @param {@link Option} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @return {@link AllEndToEndTestResults} object which has three {@link List}s - one of the results of the model validations, one of the results of the formula validations and one with the results of the model checks
     * @throws Exception
     */
    private EndToEndTestResultWithValidation runEndToEndTest(String kripkeFilepath, String formulasFilename, Options options) throws Exception {
        ValidateModelResults validateModelResults = validateEndToEndTestModel(kripkeFilepath, options);
        List validateFormulaResultsList = validateEndToEndFormulaFile(formulasFilename, new ArrayList());
        List endToEndTestResults = modelCheckEndToEndTest(kripkeFilepath, formulasFilename, options);
        EndToEndTestResultWithValidation endToEndTestResultWithValidation = new EndToEndTestResultWithValidation(validateModelResults, validateFormulaResultsList, endToEndTestResults);
        return endToEndTestResultWithValidation;
    }

    /**
     * Checks the CTL models in the end to end tests to see if they're well formed.
     * @param {@link Option} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @throws IOException
     */
    public List validateEndToEndTestModels(Options options) throws Exception {
        TestFiles testFiles = options.getEndToEndTests();
        List validateModelResultsList = new ArrayList();

        for (Object testFilesObj : testFiles.getKripkesValid()) {
            String testFile = (String) testFilesObj;
            ValidateModelResults validateModelResults = validateEndToEndTestModel(testFile, options);
            validateModelResultsList.add(validateModelResults);
        }

        for (Object testFilesObj : testFiles.getKripkesInvalid()) {
            String testFile = (String) testFilesObj;
            ValidateModelResults validateModelResults = validateEndToEndTestModel(testFile, options);
            validateModelResultsList.add(validateModelResults);
        }
        return validateModelResultsList;
    }

     /**
     * Checks a CTL end to end model to see if they're well formed.
     * @param {@link Option} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @throws IOException
     */
    public ValidateModelResults validateEndToEndTestModel(String kripkeFilepath, Options options) throws Exception {
            Boolean modelValidationPass = null;
            String originalErrorMessage = "";
            KripkeFileObj kripkeFileObj = getKripkeFileObj(kripkeFilepath);

            if (kripkeFileObj.getErrorMessage() != null) {
                modelValidationPass = false;
                originalErrorMessage = kripkeFileObj.getErrorMessage();
            } else {
                modelValidationPass = true;
            }
            ValidateModelResults validateModelResults = new ValidateModelResults(modelValidationPass,originalErrorMessage,kripkeFilepath);
            return validateModelResults;
    }

    /**
     * This creates a {@link List} of {@link EndToEndFormulaFileObj}s for the end to end tests. Parses each test file and pulls out all the formulas, states to check and the expected result from each
     * Read input stream line by line approach with the BufferedReader from https://stackoverflow.com/a/55420102, accessed 9/18/21
     * @param formulasFilename A {@link String} filename for formula files. Ie, "Model 1 - Test Formulas.txt"
     * @param {@link Option} object with options specified by user in the command line arguments as well as the hardcoded options at the top of Main.java
     * @return a {@link List} of {@link EndToEndFormulaFileObj}s with details from the test formula files (formulas, states to check and the expected result)
     * @throws IOException
     */
    public List getFormulaFileObjList(String formulasFilename, Options options) throws IOException {
        List formulaFileObjs = new ArrayList();
        ClassLoader classLoader = getClass().getClassLoader();

        // try (InputStream inputStream = classLoader.getResourceAsStream(options.getTestFilesDir() + "/" + formulasFilename)) {
        try (InputStream inputStream = classLoader.getResourceAsStream(formulasFilename)) {
            String formula = "";
            String stateToCheck = "";
            Boolean expected = null;
            if (inputStream == null) {
                throw new IllegalArgumentException("file not found! " + formulasFilename);
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String rawLine = "";
                    while ((rawLine = reader.readLine()) != null) {
                        String rawLineForStateToCheck = rawLine;
                        String[] lineArr = rawLineForStateToCheck.split(";",0);
                        stateToCheck = lineArr[0].trim();
                        stateToCheck = stateToCheck.replaceAll("\uFEFF", "");
                        formula = lineArr[1];
                        expected = parseBoolean(lineArr[2]);
                        EndToEndFormulaFileObj endToEndFormulaFileObj = new EndToEndFormulaFileObj(stateToCheck, formula, expected);
                        formulaFileObjs.add(endToEndFormulaFileObj);
                    }
                }
            }
            return formulaFileObjs;
        }
    }

    /**
     * Validates the formulas in the end to end tests (ie, checks to make sure they're well formed / do not contain syntax errors)
     * Read input stream line by line approach with the BufferedReader from https://stackoverflow.com/a/55420102, accessed 9/18/21
     * @param options (@link Options) object containing user entered options in command line arguments as well as the options hard coded at the top of Main.java
     * @return a {@link List} of {@link ValidateFormulaResults} objects, each containing the results of one formula validation
     * @throws IOException
     * @throws ParseException
     */
    private List validateEndToEndFormulas(Options options) throws IOException, ParseException {
        TestFiles testFilesObj = options.getEndToEndTests();
        List passedFormulas = new ArrayList();
        List validateFormulaResultList = new ArrayList();
        for (Object formulasFileObj : testFilesObj.getFormulas()) {
            String formulasFilename = (String) formulasFileObj;

            // ValidateFormulaResults validateFormulaResults = validateEndToEndFormulaFile(formulasFilename, passedFormulas);
            List validateFormulaResultsList = validateEndToEndFormulaFile(formulasFilename, passedFormulas);
            for (Object validateFormulaResultsObj : validateFormulaResultList) {
                ValidateFormulaResults validateFormulaResults = (ValidateFormulaResults) validateFormulaResultsObj;
                passedFormulas.add(validateFormulaResults.getFormula());
            }
        }
        return validateFormulaResultList;
    }


    /**
     * Validates the formula in an end to end tests (ie, checks to make sure it's well formed / does not contain syntax errors)
     * Read input stream line by line approach with the BufferedReader from https://stackoverflow.com/a/55420102, accessed 9/18/21
     * @return a {@link List} of {@link ValidateFormulaResults} objects, each containing the results of one formula validation
     * @throws IOException
     * @throws ParseException
     */
    private List validateEndToEndFormulaFile(String formulasFilename, List passedFormulas) throws IOException, ParseException {
        List validateFormulaResultsList = new ArrayList();
        // ValidateFormulaResults validateFormulaResults = null;
        ClassLoader classLoader = getClass().getClassLoader();
        Boolean validateFormulaPass = null;
        String ctlFormula = "";
        String error = "";

        try (InputStream inputStream = classLoader.getResourceAsStream(formulasFilename)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("file not found! " + formulasFilename);
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String rawLine = reader.readLine();
                    while ((rawLine = reader.readLine()) != null) {
                        rawLine = rawLine.replaceAll("s\\d+;", "");
                        rawLine = rawLine.replaceAll(";True", "");
                        ctlFormula = rawLine.replaceAll(";False", "");
                        ctlFormula = ctlFormula.replaceAll("\\ufeff", "");
                        if (!passedFormulas.contains(ctlFormula)) {
                            InputStream stringStream = new ByteArrayInputStream(ctlFormula.getBytes("UTF-8"));
                            Validator validator = new Validator(stringStream);
                            validateFormulaPass = true;
                            validator.Validate();
                            passedFormulas.add(ctlFormula);
                            ValidateFormulaResults validateFormulaResults = new ValidateFormulaResults(validateFormulaPass,ctlFormula,error,formulasFilename);
                            if (validateFormulaResults.getPassValidation()) {
                                if (!passedFormulas.contains(validateFormulaResults.getFormula())) {
                                    passedFormulas.add(validateFormulaResults.getFormula());
                                }
                            }
                            validateFormulaResultsList.add(validateFormulaResults);
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return validateFormulaResultsList;
    }

    /**
     * Model checks all the end to end tests
     * @param options (@link Options) object containing user entered options in command line arguments as well as the options hard coded at the top of Main.java
     * @return a {@link List} of {@link EndToEndTestResult} objects, each containing the results of one model check test
     * @throws IOException
     * @throws ParseException
     */
    private List modelCheckEndToEndTests(Options options) throws IOException, modelCheckCTL.controller.ctl.Parser.ParseException {
        List endToEndTestResultsList = new ArrayList();
        // TestFiles testFilesObj = getTestFiles(testFilesDir);
        TestFiles testFilesObj = options.getEndToEndTests();
        List kripkeFiles = testFilesObj.getKripkesValid();
        List formulaFiles = testFilesObj.getFormulas();

        for (int i=0; i<kripkeFiles.size(); i++) {
            Object kripkeFilenameObj = kripkeFiles.get(i);
            String kripkeFilename = (String) kripkeFilenameObj;
            Object formulaFilenameObj = formulaFiles.get(i);
            String formulaFilename = (String) formulaFilenameObj;

            List newEndToEndTestResultList = new ArrayList();
            List thisEndToEndTestResultsList = modelCheckEndToEndTest(kripkeFilename, formulaFilename, options);
            for (Object thisEndToEndTestResultObj : thisEndToEndTestResultsList) {
                EndToEndTestResult thisEndToEndTestResult = (EndToEndTestResult) thisEndToEndTestResultObj;
                newEndToEndTestResultList.add(thisEndToEndTestResult);
            }
            for (Object endToEndTestResultsObj : endToEndTestResultsList) {
                EndToEndTestResult endToEndTestResults = (EndToEndTestResult) endToEndTestResultsObj;
                newEndToEndTestResultList.add(endToEndTestResults);
            }

            endToEndTestResultsList = newEndToEndTestResultList;
        }
        return endToEndTestResultsList;
    }


    /**
     * Model checks an end to end test
     * @param kripkeFilename {@link String} specifying the name of the kripke file
     * @param formulaFilename {@link String} specifying the name of the formula file (contains formulas, states to check and expected results)
     * @param options {@link Options} object containing user entered options in command line arguments as well as the options hard coded at the top of Main.java
     * @return a {@link List} of {@link EndToEndTestResult} objects, each containing the results of one model check test
     * @throws IOException
     * @throws ParseException
     */
    private List modelCheckEndToEndTest(String kripkeFilename, String formulaFilename, Options options) throws IOException, modelCheckCTL.controller.ctl.Parser.ParseException {
        List endToEndTestResultsList = new ArrayList();
        List formulaFileObjList = getFormulaFileObjList(formulaFilename, options);
        int numToTest = formulaFileObjList.size();
        int numTested = 0;
        while (numTested < numToTest) {
            Set statesThatHold = null;
            Set allStates = null;
            String stateToCheck = "";
            String formula = "";
            Boolean stateToCheckHold = null;
            Boolean expectedResult = null;
            Boolean actualResult = null;
            Boolean testPass = null;
            ModelCheckResults modelCheckResults;
            EndToEndTestResult endToEndTestResult;
            String kripkeFilepath = kripkeFilename;
            KripkeFileObj kripkeFileObj = getKripkeFileObj(kripkeFilepath);
            EndToEndFormulaFileObj endToEndFormulaFileObj = (EndToEndFormulaFileObj) formulaFileObjList.get(numTested);
            Kripke kripke = kripkeFileObj.getKripke();
            allStates = kripke.getStates();
            formula = endToEndFormulaFileObj.getFormula();
            stateToCheck = endToEndFormulaFileObj.getStateToTest();
            expectedResult = endToEndFormulaFileObj.getExpected();
            statesThatHold = modelCheck(kripkeFilepath, formula);
            modelCheckResults = new ModelCheckResults(statesThatHold, allStates, stateToCheck, formula);
            actualResult = null;
            if (containsStateName(statesThatHold, endToEndFormulaFileObj.getStateToTest())) {
                actualResult = true;
            } else {
                actualResult = false;
            }
            if (actualResult == expectedResult) {
                testPass = true;
            } else {
                testPass = false;
            }
            endToEndTestResult = new EndToEndTestResult(modelCheckResults,expectedResult,actualResult,testPass,formula, stateToCheck);
            endToEndTestResultsList.add(endToEndTestResult);
            numTested++;
        }
        return endToEndTestResultsList;
    }

}
