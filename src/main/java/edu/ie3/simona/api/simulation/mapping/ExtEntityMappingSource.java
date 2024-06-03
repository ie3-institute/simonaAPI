package edu.ie3.simona.api.simulation.mapping;

public abstract class ExtEntityMappingSource {

    protected final ExtEntityFactory factory;

    public ExtEntityMappingSource() {
        this.factory = new ExtEntityFactory();
    }
}
