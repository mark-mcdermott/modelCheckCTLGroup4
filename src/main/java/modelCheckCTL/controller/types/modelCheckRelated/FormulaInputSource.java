package modelCheckCTL.controller.types.modelCheckRelated;

/**
 * Enumerates the two ways the model string (ie, "EXp") can be provided to the program - 1) either hard coded as an command line argument when this program is run or 2) in a input text file containing the model string
 */
public enum FormulaInputSource {
    ARGUMENT,
    FILE
}
