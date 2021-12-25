package com.telenav.fiasco.internal.building.phase.building;

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
 *     <li>{@link BuildStep#BUILDING_INITIALIZE}</li>
 *     <li>{@link BuildStep#BUILDING_GENERATE_SOURCES}</li>
 *     <li>{@link BuildStep#BUILDING_PREPROCESS}</li>
 *     <li>{@link BuildStep#BUILDING_COMPILE}</li>
 *     <li>{@link BuildStep#BUILDING_POSTPROCESS}</li>
 *     <li>{@link BuildStep#BUILDING_BUILD_DOCUMENTATION}</li>
 *     <li>{@link BuildStep#BUILDING_VERIFY}</li>
 * </ol>
 *
 * @author jonathanl (shibo)
 * @see Build
 * @see BuildStep
 * @see Phase
 */
public class BuildingPhase extends BasePhase
{
    private final StringWriter output = new StringWriter();

    /**
     * @param build The build to which this compilation phase belongs
     */
    public BuildingPhase(Build build)
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
