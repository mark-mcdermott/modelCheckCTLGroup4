package modelCheckCTL;

import modelCheckCTL.controller.types.misc.Options;
import modelCheckCTL.controller.types.misc.TestFiles;
import modelCheckCTL.model.Model;
import modelCheckCTL.view.View;
import modelCheckCTL.controller.Controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        // options set here
        // first four are hard coding the end to end tests
        List kripkesInvalid = Arrays.asList("Broken Model 1.txt", "Broken Model 2.txt", "Broken Model 3.txt", "Broken Model 4.txt", "Broken Model 5.txt", "Broken Model 6.txt", "Broken Model 7.txt");
        List kripkesValid = Arrays.asList("Model 1.txt","Model 2.txt","Model 3.txt","Model 4.txt","Model 5.txt","Model 6.txt","Model 7.txt");
        List formulasStatesExpectedResults = Arrays.asList("Model 1 - Test Formulas.txt","Model 2 - Test Formulas.txt","Model 3 - Test Formulas.txt","Model 4 - Test Formulas.txt","Model 5 - Test Formulas.txt","Model 6 - Test Formulas.txt","Model 7 - Test Formulas.txt");
        TestFiles endToEndTests = new TestFiles(kripkesValid, kripkesInvalid, formulasStatesExpectedResults);
        Boolean printExceptions = true;             // this is whether to print error messages and continue the program (good for prod) or to throw an exception and halt the program there (good for debugging)

        // setup main program
        Options options = new Options(args, endToEndTests, printExceptions);
        Model model = new Model();
        View view = new View(options);
        new Controller(model, view, options);

    }
}
