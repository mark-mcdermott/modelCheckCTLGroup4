# CTL Model Checker 

CTL model checker written in Java and using a JavaCC parser for Texas State CS 5392 Formal Methods Fall '21, taught by Dr. Podorozhny.

## How To Run 
1) Install Java 14 (if you have a higher version it probably works, but I haven't tried)
    - Go to https://www.oracle.com/java/technologies/javase/jdk14-archive-downloads.html
    - You will need to login with an oracle account and will need to create one if you don't have one already
    - I downloaded the macOS Installer after logging in
    - Double click the downloaded file and follow the prompts to install Java 14
2) Install Maven (you can skip this if you only want to run the program and don't want to change the code and don't want to run the unit tests)
    - download the binary tar.gz archive from https://maven.apache.org/download.cgi
    - in finder double click the downloaded file to unzip it
    - in terminal cd to the folder the unzipped file
    - `chown -R root:wheel ./apache-maven*`
    - `sudo mv ./apache-maven* /opt/apache-maven` (you will have to enter your password for the sudo to work)
    - Add mvn to your path. In Big Sur, this is done by:
    - `cd ~`
    - do `ls -la` and look for `.zshrc`
    - if `.zshrc` isn't there, do `touch .zshrc`
    - do `nano .zshrc`
    - in nano add this line `export PATH=$PATH:/opt/apache-maven/bin`
    - press control + x to exit and y and then enter to save
    - quit (not close, but quit) terminal
    - reopen terminal
    - type `mvn -version` and if everything worked the response should start with something like `Apache Maven 3.8.3`
3) Clone the repo  
   - `git clone cs5392-group-project-kusupati-liu-mcdermott-namala`
4) Cd into project: `cd cs5392-group-project-kusupati-liu-mcdermott-namala`
5) Cd into /out folder: `cd out`
6) Run the program
    - `java -jar modelCheckingCTL.jar -k <kripke file> [-s <state to check>] -af <formula> [-e [<end to end test number>]] [-m]` (note the "-af" flag is signifying that you must choose either -a or -f. `-af` does not actually work.)
    - The arguments in `[ ]` are optional
    - The model flag takes either `-a` for specifying the formula in directly in the argument (inside quotes) or `-f` for specifying a file which contains the formula. Can must choose either -f or -a, you cannot use both and you cannot do -af. `-e` is to specify to run the end to end tests. The `-e` flag may be used alone and in that case will run all the end to end tests. You may also do `-e <test num>`, which will run only the end to end test number specified, ie `-e 1` will run the first end to end test ("Model 1.txt" and "Model 1 - Test Formulas.txt"). You may also just use the `-m` flag which will run the microwave example.  
    - The `-s` argument for state to check is optional. If omitted, all states are checked.
    - An alternative way to run the program is to only run the end to end tests, which is specified by - `java -jar modelCheckingCTL.jar -e`
    - Some command line examples:
        - `java -jar modelCheckingCTL.jar -k kripke.txt -a "EXp"`
        - `java -jar modelCheckingCTL.jar -k kripke.txt -s s0 -a "EXp" -e`
        - `java -jar modelCheckingCTL.jar -k kripke.txt -s s13 -f model.txt` 
        - `java -jar modelCheckingCTL.jar -e`
        - `java -jar modelCheckingCTL.jar -e 2`
        - `java -jar modelCheckingCTL.jar -m`
    - any kripke or formula files in the command line arguments need to be located in src/main/resources. You can see there is already a formula.txt and a kripke.txt there. You may modify these files or create your own here.
7) Run the unit tests (totally optional)
   - `mvn test`
    
## Development Notes
- To get this running in IntelliJ:
    - Open IntelliJ
    - Open the top level program folder `cs5392-group-project-kusupati-liu-mcdermott-namala`
    - Set the project java SDK to java 14:
    - file -> project structure -> project sdk on the right -> set to java 14.0.2 if not already set to that.
    - Click the green hammer icon at the top to do the initial build
    - Open src/main/java/dev.markmcd/Main
    - Right click the word `Main` on line 15 and select "Run Main.main()"
    - This throws a null pointer exception, which is fine right now
    - Click "Main" at the top of the screen to the right of the green hammer
    - Click Edit Configurations
    - On the right side under "Build and run" in the Program Arguments field, enter `-k kripke.txt -a "EXp" -e` and click Apply and Ok.
    - Click the green play icon to the right of the green hammer and the "Main" configuration.
- JavaCC is used as a compiler/parser here in two instances - once for the validator and once for the parser
    - Validator
        - The validator just checks for well formed CTL models. It is like a much simplified version of the parser.
        - The validator is located at src/main/java/dev/markmcd/controller/ctl/Validator
    - Parser 
        - The parser does the heavy lifting for the CTL work. This is where the SAT algorithms are run and evaluated.
        - The parser is located at src/main/java/dev/markmcd/controller/ctl/Parser
    - Both the validator and the parser use generated files, so the development workflow with them is a little strange.
        - You'll need to <a href="https://javacc.github.io/javacc/#download">download and install JavaCC</a>. I'm on mac (11.4 Big Sur) and I believe these were my install steps:
            - I downloaded the source zipfile at <a href="https://github.com/javacc/javacc/archive/javacc-7.0.10.zip">https://github.com/javacc/javacc/archive/javacc-7.0.10.zip</a>
            - I unzipped the source zipfile in my home directory (`/Users/markmcdermott`)
            - I opened my etc/paths file with `sudo vi etc/paths` 
            - I added this line to the end of my paths file: `/Users/markmcdermott/javacc-javacc-7.0.10/scripts`
            - Then I quit terminal and reopened it 
        - Once JavaCC is installed, you can modify and then and regenerating the grammar file. These steps will modify the parser files, but the same steps will work for the validator as well:
            - `cd` into the parser directory (src/main/java/dev/markmcd/controller/ctl/Parser)
            - Open Parser.jj in your IDE or text editor.
            - All you modifications will happen only in this .jj file.
            - After you're done your changes, in terminal in the parser directory (the one containing Parser.jj), type `javacc Parser.jj`. This will generate about eight files in this same directory.
            - Since every time you regenerate your compiler files they overwrite the previously generated files, you can't make modifications in the generated files or they'll be overwritten the next time you regenerate them.  
- a few helpful maven commands after you make development changes:
    - `mvn package` This creates the jar file in /out
    - `mvn clean` This removes extra directories that get created like /target - so it's good to `mvn clean` before you commit
- if you want to run your own custom end to end test:
    - go to /src/main/resources/end-to-end-tests
    - model your two files on `Model 1.txt` and `Model 1 - Test Formulas.txt` and the first will be your kripke and the second will contain the states/formulas/expected-results that you want to run on your kripke
    - the easiest way might be to delete all other files in the /end-to-end-tests folder except your two files
    - you can leave the other files in there if you want - in that case, perhaps it would be best to use the same file naming structure and name your files `Model 8.txt` and `Model 8 - Test Formulas.txt` if the last test currently in the folder is 7.
    - you can then run your test by changing your directory to the top level /out folder and then running `java -jar modelCheckingCTL.jar -e`
            