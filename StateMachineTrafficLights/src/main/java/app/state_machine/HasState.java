package app.state_machine;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Object that holds state.
 * @param <T> State types.
 */
public interface HasState<T> {
    List<Constructor<?>> getActionsBasedOnState();
    void setStateType(T stateType);
}
