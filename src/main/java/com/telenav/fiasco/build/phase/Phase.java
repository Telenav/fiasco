package com.telenav.fiasco.build.phase;

import com.telenav.fiasco.build.BuildStep;
import com.telenav.fiasco.build.project.Project;
import com.telenav.kivakit.component.Component;

/**
 * A phase is a set of {@link BuildStep}s associated with a {@link Project} build. Calling {@link #nextStep()} advances
 * the build to the next step.
 */
public interface Phase extends Component
{
    /**
     * Move the project build forward a step
     */
    default void nextStep()
    {
        project().nextStep();
    }

    /**
     * @return The project to which this phase belongs
     */
    Project project();
}
