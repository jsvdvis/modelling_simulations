package app.state_machine;

/**
 * Action that could possibly be performed on a StateFulObject.
 */
public interface Action {
    public boolean canPerform();
    public void perform();
}
