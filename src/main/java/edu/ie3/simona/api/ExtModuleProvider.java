package edu.ie3.simona.api;

import edu.ie3.simona.api.module.ExtModule;
import edu.ie3.simona.api.module.ModuleType;

import java.util.EnumMap;

public interface ExtModuleProvider {
    
    EnumMap<ModuleType, ExtModule> getModules();
    
}
