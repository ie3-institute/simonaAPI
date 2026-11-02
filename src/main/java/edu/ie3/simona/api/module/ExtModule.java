package edu.ie3.simona.api.module;

import org.apache.pekko.actor.typed.scaladsl.ActorContext;

/**
 * External module.
 */
public abstract class ExtModule {
    
    private final ModuleType type;
    private final ActorContext<?> context;

    protected ExtModule(ModuleType type, ActorContext<?> context) {
        this.type = type;
        this.context = context;
    }


    public ModuleType getType() {
        return type;
    }
    
    public ActorContext<?> getContext()  {
        return context;
    }

    public org.apache.pekko.actor.typed.javadsl.ActorContext<?> getJavaContext()  {
        return context.asJava();
    }
}
