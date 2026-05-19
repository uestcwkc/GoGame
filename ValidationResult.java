package chess.game;

/** 落子合法性校验结果，携带是否合法和错误原因。 */
public class ValidationResult {
    private final boolean valid;
    private final String  message;

    private ValidationResult(boolean valid, String message) {
        this.valid = valid; this.message = message;
    }
    public static ValidationResult ok()             { return new ValidationResult(true,  ""); }
    public static ValidationResult fail(String msg) { return new ValidationResult(false, msg); }

    public boolean isValid()    { return valid; }
    public String  getMessage() { return message; }
}
