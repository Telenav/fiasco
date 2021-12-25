package com.telenav.fiasco.internal.building.phase.compilation;

import com.telenav.fiasco.internal.building.BuildStep;
import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.phase.BasePhase;
import com.telenav.fiasco.runtime.Build;

import java.io.StringWriter;

/**
 * <b>Not public API</b>
 * <p>
 * Executes the steps in the compilation phase of a build:
 *
 * <ol>
 *     <li>{@link BuildStep#INITIALIZE}</li>
 *     <li>{@link BuildStep#GENERATE_SOURCES}</li>
 *     <li>{@link BuildStep#PREPROCESS}</li>
 *     <li>{@link BuildStep#COMPILE}</li>
 *     <li>{@link BuildStep#POSTPROCESS}</li>
 *     <li>{@link BuildStep#VERIFY}</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see Build
 * @see BuildStep
 * @see Phase
 */
public class BuildPhase extends BasePhase
{
    private final StringWriter output = new StringWriter();

    /**
     * @param build The build to which this compilation phase belongs
     */
    public BuildPhase(Build build)
    {
        super(build);
    }

    /**
     * @return The output of compilation
     */
    public StringWriter output()
    {
        return output;
    }
}
