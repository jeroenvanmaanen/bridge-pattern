package org.leialearns.bridge.crossings.far;

import org.leialearns.bridge.BridgeFactory;
import org.leialearns.bridge.FarObject;
import org.leialearns.bridge.crossings.api.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RootDTO implements FarObject<Root> {

    @Autowired
    @Qualifier(value = "rootFactory")
    private BridgeFactory rootFactory;

    public Root getNearObject() {
        return (Root) rootFactory.getNearObject(this);
    }

    public Root declareNearType() {
        throw new UnsupportedOperationException("This method is for declaration only");
    }

}
