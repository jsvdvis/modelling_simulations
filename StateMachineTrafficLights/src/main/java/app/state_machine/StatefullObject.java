package app.state_machine;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A State can return a list of possible Actions based on its current state.
 */
public abstract class StatefullObject<T> implements HasState<T> {
    private final HashMap<T, List<Constructor<?>>> actionConstructors;
    private T stateType;

    public StatefullObject(T stateType) {
        this.stateType = stateType;
        this.actionConstructors = this.getAllActionConstructorsPerStates();
    }

    public void setStateType(T stateType) {
        this.stateType = stateType;
    }

    public List<Constructor<?>> getActionsBasedOnState() {
        if (this.actionConstructors.containsKey(this.stateType)) {
            return this.actionConstructors.get(this.stateType);
        }
        return new ArrayList<>();
    }

    abstract protected HashMap<T, List<Constructor<?>>> getAllActionConstructorsPerStates();
}
