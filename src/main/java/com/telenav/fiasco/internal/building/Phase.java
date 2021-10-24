package com.telenav.fiasco.internal.building;

import com.telenav.fiasco.runtime.Build;
import com.telenav.kivakit.component.Component;

/**
 * <b>Not public API</b>
 * <p>
 * A phase is a set of {@link BuildStep}s associated with a {@link Build} build. Calling {@link #nextStep()} advances
 * the build to the next step.
 */
public interface Phase extends Component
{
    /**
     * @return The project to which this phase belongs
     */
    Build build();

    /**
     * Move the project build forward a step
     */
    default void nextStep()
    {
        build().nextStep();
    }
}
