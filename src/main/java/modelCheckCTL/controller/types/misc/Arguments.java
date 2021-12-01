package modelCheckCTL.controller.types.misc;

import modelCheckCTL.controller.types.modelCheckRelated.FormulaInputSource;

import java.io.IOException;

import static modelCheckCTL.controller.types.modelCheckRelated.FormulaInputSource.FILE;
import static modelCheckCTL.controller.types.modelCheckRelated.FormulaInputSource.ARGUMENT;
import static modelCheckCTL.utils.Utils.isStateName;
import static modelCheckCTL.utils.Utils.isTxtFile;

/**
 * Data type for storing the command line arguments for the Kripke filename, the optional state to check, the model input source (file/argument) and either the model filename or the model, depending on the model input source specified.
 * This class does basic error checking for null values and text file names which don't end in ".txt". This class does not check if the model is well formed and does not read the specified files to get the kripke or the model.
 */
public class Arguments {

    /**
     * Filename of .txt file containing the kripke structure. Don't include the full path, just the filename. The file needs to be in the src/main/resources directory.
     * Constructor checks to ensure the filename it ends in .txt and has at least one character before the period.
     * This class only stores the filename. It leaves the heavy lifting (like reading the file and checking if the Kripke syntax is valid) to other downstream classes.
     * Altough this class doesn't check the syntax, the Kripke will later be checked and will need to be in the following format:
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
     */
    String kripkeFilename;

    /**
     * Name of the state to check, like "s0" or "s13". Constructor checks to ensure stateToCheckStr starts with lowercase "s" and next has an integer after that.
     * This property is optional and should be null until assigned a state string. When null, all states are just checked instead.
     */
    String stateToCheckStr;

    /**
     *  Simple enum value that's either FILE or ARGUMENT. Refers to whether user specified the -f or -a flag.
     *  FILE means the model is supplied in a textfile specified after the -f flag in the command line arguments.
     *  ARGUMENT means the model itself is hardcoded in the command line argument after the -a flag.
     */
    FormulaInputSource formulaInputSource;

    /**
     * Optional .txt {@link String} filename specifying location of the file. If this if omitted then the model properties must be supplied instead.
     * Constructor checks to ensure the filename it ends in .txt and has at least one character before the period.
     * This class only stores the filename. It leaves the heavy lifting (like reading the file and checking if the model syntax is valid) to other downstream classes.
     * But when the file is read and the model is parsed, the model will have to use the following flavor of CTL operators:
     * not, and, or, ->, EX, AX, EF, AF, EG, AG, E[ p U q ], A[ p U q ]].
     */
    String formulaFilename;

    /**
     * {@link String} specifying the CTL model property to check.
     * This class only stores the model. It leaves the heavy lifting (like parsing the model and checking for valid CTL syntax) to other downstream classes.
     * But when the model is parsed, it will have to use the following flavor of CTL operators:
     * not, and, or, ->, EX, AX, EF, AF, EG, AG, E[ p U q ], A[ p U q ]].
     */
    String formula;

    /**
     * {@Boolean} specifying that the end to end tests should be run
     */
    Boolean runEndToEndTests;

    /**
     * {@Boolean} specifying to only run the microwave example
     */
    Boolean runOnlyMicrowave;

    /**
     * {@Intger} specifying the end to end test number to run (optional)
     */
    Integer endToEndTestNum;

    public Arguments(Boolean runEndToEndTests, Integer endToEndTestNum, Boolean runOnlyMicrowave) throws IOException {
        this.runEndToEndTests = runEndToEndTests;
        this.endToEndTestNum = endToEndTestNum;
        this.runOnlyMicrowave = runOnlyMicrowave;
    }

    /**
     * Constructor including state to check (the other constructor omits the state to check), the kripke file name,the model input source and the model input string.
     * @param kripkeFilename {@link String} Filename of .txt file containing the kripke structure. Don't include the full path, just the filename. The file needs to be in the src/main/resources directory.
     * @param stateToCheckStr {@link String} name of the state to check, like "s0" or "s13". Constructor checks to ensure stateToCheckStr starts with lowercase "s" and next has an integer after that.
     * @param formulaInputSource Simple {@link FormulaInputSource} enum value that's either FILE or ARGUMENT. Refers to whether user specified the -f or -a flag. FILE means the model is supplied in a textfile specified after the -f flag in the command line arguments. ARGUMENT means the model itself is hardcoded in the command line argument after the -a flag.
     * @param modelInputStr {@link String} representing either the .txt filename where the model is supplied or is actual CTL model string itself. Whether this string is the filename or the model is determined by the above modelInputSource param - FILE means filename and ARGUMENT means the model itself.
     * @param runEndToEndTests {@Boolean} specifying that the end to end tests should be run
     * @param endToEndTestNum {@Integer} specifying the end to end test number to run (optional)
     * @param runOnlyMicrowave {@Boolean} specifying to only run the microwave example
     * @throws IOException
     */
    public Arguments(String kripkeFilename, String stateToCheckStr, FormulaInputSource formulaInputSource, String modelInputStr, Boolean runEndToEndTests, Integer endToEndTestNum, Boolean runOnlyMicrowave) throws IOException {
        if (kripkeFilename == null || stateToCheckStr == null || formulaInputSource == null || formulaInputSource == null) {
            throw new NullPointerException("Arguments param is null");
        } else if (kripkeFilename == "" || stateToCheckStr == "" || modelInputStr == "") {
            throw new IOException("Arguments param is empty string");
        } else if (!isTxtFile(kripkeFilename)) {
            throw new IOException("kripke filename in Arguments isn't a .txt filename.");
        }
        this.kripkeFilename = kripkeFilename;
        if (!isStateName(stateToCheckStr)) {
            throw new IOException("Specified state to check is not a state name. State name must be \"s\" followed by an integer. ie, \"s0\" or \"s1\".");
        }
        this.stateToCheckStr = stateToCheckStr;
        this.formulaInputSource = formulaInputSource;
        if (formulaInputSource == FILE) {
            if (!isTxtFile(modelInputStr)) {
                throw new IOException("modelInputStr filename in Arguments isn't a .txt filename.");
            }
            this.formulaFilename = modelInputStr;
            this.formula = null;
        } else if (formulaInputSource == ARGUMENT){
            if (isTxtFile(modelInputStr)) { throw new IOException("Arguments specified ARGUMENT model input, but modelInputStr is a .txt filename"); }
            this.formula = modelInputStr;
            this.formulaFilename = null;
        }
        this.runEndToEndTests = runEndToEndTests;
        this.endToEndTestNum = endToEndTestNum;
        this.runOnlyMicrowave = runOnlyMicrowave;
    }

    /**
     * Constructor that omits state to check (the other constructor includes the state to check), the kripke file name,the model input source and the model input string.
     * @param kripkeFilename {@link String} Filename of .txt file containing the kripke structure. Don't include the full path, just the filename. The file needs to be in the src/main/resources directory.
     * @param formulaInputSource Simple {@link FormulaInputSource} enum value that's either FILE or ARGUMENT. Refers to whether user specified the -f or -a flag. FILE means the model is supplied in a textfile specified after the -f flag in the command line arguments. ARGUMENT means the model itself is hardcoded in the command line argument after the -a flag.
     * @param modelInputStr {@link String} representing either the .txt filename where the model is supplied or is actual CTL model string itself. Whether this string is the filename or the model is determined by the above modelInputSource param - FILE means filename and ARGUMENT means the model itself.
     * @param runEndToEndTests {@Boolean} specifying that the end to end tests should be run
     * @param endToEndTestNum {@Integer} specifying the end to end test number to run (optional)
     * @param runOnlyMicrowave {@Boolean} specifying to only run the microwave example
     * @throws IOException
     */
    public Arguments(String kripkeFilename, FormulaInputSource formulaInputSource, String modelInputStr, Boolean runEndToEndTests, Integer endToEndTestNum, Boolean runOnlyMicrowave) throws IOException {
        if (kripkeFilename == null || formulaInputSource == null || formulaInputSource == null) {
            throw new NullPointerException("Arguments param is null");
        } else if (kripkeFilename == "" || modelInputStr == "") {
            throw new IOException("Arguments param is empty string");
        } else if (!isTxtFile(kripkeFilename)) {
            throw new IOException("kripke filename in Arguments isn't a .txt filename.");
        }
        this.kripkeFilename = kripkeFilename;
        this.formulaInputSource = formulaInputSource;
        if (formulaInputSource == FILE) {
            if (!isTxtFile(modelInputStr)) {
                throw new IOException("modelInputStr filename in Arguments isn't a .txt filename.");
            }
            this.formulaFilename = modelInputStr;
            this.formula = null;
        } else if (formulaInputSource == ARGUMENT){
            if (isTxtFile(modelInputStr)) { throw new IOException("Arguments specified ARGUMENT model input, but modelInputStr is a .txt filename"); }
            this.formula = modelInputStr;
            this.formulaFilename = null;
        }
        this.runEndToEndTests = runEndToEndTests;
        this.endToEndTestNum = endToEndTestNum;
        this.runOnlyMicrowave = runOnlyMicrowave;
    }

    public String getFormula() {
        return formula;
    }

    public FormulaInputSource getFormulaInputSource() {
        return formulaInputSource;
    }

    public String getKripkeFilename() {
        return kripkeFilename;
    }

    public String getFormulaFilename() {
        return formulaFilename;
    }

    public String getStateToCheckStr() {
        return stateToCheckStr;
    }

    public Boolean getRunEndToEndTests() {
        return runEndToEndTests;
    }

    public Integer getEndToEndTestNum() {
        return endToEndTestNum;
    }

    public Boolean getRunOnlyMicrowave() {
        return runOnlyMicrowave;
    }
}
