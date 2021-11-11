package com.telenav.fiasco.internal.building;

import com.telenav.fiasco.runtime.Build;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

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
    INITIALIZE,
    RESOLVE_DEPENDENCIES,
    GENERATE_SOURCES,
    PREPROCESS,
    COMPILE,
    POSTPROCESS,
    COMPILE_DOCUMENTATION,
    VERIFY,

    // Compile test sources
    TEST_INITIALIZE,
    TEST_RESOLVE_DEPENDENCIES,
    TEST_GENERATE_SOURCES,
    TEST_PREPROCESS,
    TEST_COMPILE,
    TEST_POSTPROCESS,
    TEST_VERIFY,

    // Run Tests
    TEST_RUN_TESTS,

    // Package output files
    PACKAGE_INITIALIZE,
    PACKAGE_PREPROCESS,
    PACKAGE_COMPILE,
    PACKAGE_POSTPROCESS,
    PACKAGE_VERIFY,

    // Install packages
    PACKAGE_INSTALL,
    PACKAGE_DEPLOY;

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
