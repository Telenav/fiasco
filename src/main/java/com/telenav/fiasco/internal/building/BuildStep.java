package com.telenav.fiasco.internal.building;

import com.telenav.fiasco.runtime.Build;

import static com.telenav.kivakit.core.data.validation.ensure.Ensure.fail;

/**
 * <b>Not public API</b>
 *
 * <p>
 * The steps in the build lifecycle, in order. The {@link #next()} method returns the next step in the lifecycle order.
 * </p>
 *
 * @author jonathanl (shibo)
 * @see Build
 */
public enum BuildStep
{
    // Initialize Fiasco
    FIASCO_STARTUP,

    // Compile sources
    BUILDING_INITIALIZE,
    BUILDING_RESOLVE_DEPENDENCIES,
    BUILDING_GENERATE_SOURCES,
    BUILDING_PREPROCESS,
    BUILDING_COMPILE,
    BUILDING_POSTPROCESS,
    BUILDING_BUILD_DOCUMENTATION,
    BUILDING_VERIFY,

    // Compile test sources
    TESTING_INITIALIZE,
    TESTING_RESOLVE_DEPENDENCIES,
    TESTING_GENERATE_SOURCES,
    TESTING_PREPROCESS,
    TESTING_COMPILE,
    TESTING_POSTPROCESS,
    TESTING_RUN_TESTS,
    TESTING_VERIFY,

    // Package output files
    PACKAGING_INITIALIZE,
    PACKAGING_PREPROCESS,
    PACKAGING_BUILD,
    PACKAGING_POSTPROCESS,
    PACKAGING_VERIFY,

    // Install packages
    INSTALLATION_INSTALL,
    INSTALLATION_DEPLOY;

    public BuildStep next()
    {
        var at = ordinal();
        for (var step : values())
        {
            if (step.ordinal() == at + 1)
            {
                return step;
            }
        }
        return fail("No next step from $", this);
    }
}
