package legacyexperiment.model.statemachine;

/**
 * Action that could possibly be performed on a StateFulObject.
 */
public interface Action {
    public boolean canPerform();
    public void perform();
}
