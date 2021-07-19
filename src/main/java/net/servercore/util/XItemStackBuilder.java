package net.servercore.util;

import com.cryptomorin.xseries.XMaterial;

public class XItemStackBuilder extends ItemStackBuilder {

    public XItemStackBuilder(XMaterial material) {
        super(material.parseMaterial());
    }
}
