package com.telenav.fiasco.build;

import static com.telenav.kivakit.kernel.data.validation.ensure.Ensure.fail;

/**
 * The steps in the build lifecycle, in order. The {@link #next()} method returns the next step in the lifecycle order.
 *
 * @author jonathanl (shibo)
 */
public enum BuildStep
{
    // Compile sources
    INITIALIZE,
    GENERATE,
    PREPROCESS,
    COMPILE,
    POSTPROCESS,
    VERIFY,

    // Compile test sources
    TEST_INITIALIZE,
    TEST_GENERATE,
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
