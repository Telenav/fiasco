package com.telenav.fiasco.internal.building.phase.building;

import com.telenav.fiasco.internal.building.Phase;
import com.telenav.fiasco.internal.building.ProjectTrait;
import com.telenav.fiasco.runtime.Dependency;
import com.telenav.kivakit.core.interfaces.lifecycle.Initializable;
import com.telenav.kivakit.core.language.mixin.Mixin;
import com.telenav.kivakit.core.messaging.messages.MessageException;

/**
 * <b>Not public API</b>
 *
 * <p>
 * {@link Mixin} for {@link BuildingPhase}
 * </p>
 *
 * @author jonathanl (shibo)
 */
public interface BuildingPhaseMixin extends
        Initializable,
        Mixin,
        Phase,
        Dependency,
        ProjectTrait,
        ToolsMixin
{
    default void buildDocumentation()
    {
        onBuildDocumentation();
    }

    default BuildingPhase buildPhaseMixin()
    {
        return mixin(BuildingPhaseMixin.class, () -> listenTo(new BuildingPhase(parentBuild())));
    }

    default void buildingPhase()
    {
        tryFinallyThrow(this::initialize, this::nextStep);
        tryFinallyThrow(this::resolveDependencies, this::nextStep);
        tryFinallyThrow(this::generateSources, this::nextStep);
        tryFinallyThrow(this::preprocess, this::nextStep);
        tryFinallyThrow(this::compile, this::nextStep);
        tryFinallyThrow(this::postprocess, this::nextStep);
        tryFinallyThrow(this::buildDocumentation, this::nextStep);
        tryFinallyThrow(this::verify, this::nextStep);
    }

    default void compile()
    {
        onCompile();
    }

    default void generateSources()
    {
        onGenerateSources();
    }

    @Override
    default void initialize()
    {
        onInitialize();
    }

    @Override
    default void nextStep()
    {
        buildPhaseMixin().nextStep();
    }

    default void onBuildDocumentation()
    {
    }

    default void onCompile()
    {
        javaCompiler().compile(sourceFolder());
    }

    default void onGenerateSources()
    {
    }

    @Override
    default void onInitialize()
    {
    }

    default void onPostprocess()
    {
    }

    default void onPreprocess()
    {
    }

    default void onResolveDependencies()
    {
        librarian().resolveTransitiveDependencies(this);
    }

    default void onVerify()
    {
    }

    default void postprocess()
    {
        onPostprocess();
    }

    default void preprocess()
    {
        onPreprocess();
    }

    default void resolveDependencies()
    {
        try
        {
            onResolveDependencies();
        }
        catch (MessageException e)
        {
            transmit(e.messageObject().cause(null));
        }
        catch (Exception e)
        {
            problem(e, "Unable to resolve dependencies");
        }
    }

    default void verify()
    {
        onVerify();
    }
}
