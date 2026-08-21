/**
 * Signals that the chatbot cannot carry out what the user asked for.
 *
 * <p>Using a dedicated exception type (rather than, say, printing the message on
 * the spot) keeps the detection of a problem separate from the reporting of it:
 * each command method simply throws with a helpful message, and the main loop is
 * the single place that decides how errors are shown to the user.
 *
 * <p>It extends {@link Exception} rather than {@link RuntimeException} so the
 * compiler forces every caller to deal with it, which makes it hard to forget a
 * case as more commands are added.
 */
public class DavidGogginsException extends Exception {
    /**
     * @param message the explanation shown to the user, ideally saying both what
     *                went wrong and how to type the command correctly
     */
    public DavidGogginsException(String message) {
        super(message);
    }
}
