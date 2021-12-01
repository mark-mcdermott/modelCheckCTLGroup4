package modelCheckCTL.controller.types.modelCheckRelated;

/**
 * Data structure storing information on whether a model passed validation (had no syntax errors)
 */
public class ValidateModelResults {

    /**
     * {@link Boolean} of whether the model passed validation
     */
    Boolean passValidation;

    /**
     * {@link String} of the original error message, if any. This would have been been set in getKripkeFileObj (or one of its submethods) in Controller.java around line 280.
     */
    String originalErrorMessage;

    /**
     * {@link String} of the filepath of the kripke file. Will just be like kripke.txt if in /resources or like end-to-end-tests/kripke.txt if in a subfolder of /resources
     */
    String kripkeFilepath;

    public ValidateModelResults(Boolean passValidation, String originalErrorMessage, String kripkeFilepath) {
        this.passValidation = passValidation;
        this.originalErrorMessage = originalErrorMessage;
        this.kripkeFilepath = kripkeFilepath;
    }

    public Boolean getPassValidation() {
        return passValidation;
    }

    public String getOriginalErrorMessage() {
        return originalErrorMessage;
    }

    public void setOriginalErrorMessage(String originalErrorMessage) {
        this.originalErrorMessage = originalErrorMessage;
    }

    public void setPassValidation(Boolean passValidation) {
        this.passValidation = passValidation;
    }

    public String getKripkeFilepath() {
        return kripkeFilepath;
    }

    public void setKripkeFilepath(String kripkeFilepath) {
        this.kripkeFilepath = kripkeFilepath;
    }

}
