package modelCheckCTL.controller.types.misc;

import java.util.List;

/**
 * Class for storing the names of the test files in an organized manner
 * The test file directory has kripkes and models (both valid and invalid versions of both).
 * The test files are ackwardly named -
 *  - valid kripkes: Model <num>.txt
 *  - invalid kripkes: Broken Model <num>.txt
 *  - models: Model <num>.txt (contain valid and invalid models)
 */
public class TestFiles {
    List kripkesValid;
    List kripkesInvalid;
    List formulas;

    public TestFiles(List kripkesValid, List kripkesInvalid, List formulas) {
        this.kripkesValid = kripkesValid;
        this.kripkesInvalid = kripkesInvalid;
        this.formulas = formulas;
    }

    public List getKripkesInvalid() {
        return kripkesInvalid;
    }

    public List getKripkesValid() {
        return kripkesValid;
    }

    public List getFormulas() {
        return formulas;
    }
}
