package com.telenav.fiasco.build.phase;

import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.FiascoBuild;
import com.telenav.kivakit.component.Component;

/**
 * A phase is a set of {@link BuildStep}s associated with a {@link FiascoBuild} build. Calling {@link #nextStep()}
 * advances the build to the next step.
 */
public interface Phase extends Component
{
    /**
     * @return The project to which this phase belongs
     */
    FiascoBuild build();

    /**
     * Move the project build forward a step
     */
    default void nextStep()
    {
        build().nextStep();
    }
}
