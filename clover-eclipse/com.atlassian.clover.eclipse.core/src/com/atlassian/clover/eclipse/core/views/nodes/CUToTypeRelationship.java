package com.atlassian.clover.eclipse.core.views.nodes;

import com.atlassian.clover.eclipse.core.CloverPlugin;
import org.eclipse.jdt.core.ICompilationUnit;

import static clover.com.google.common.collect.Lists.newArrayList;

public class CUToTypeRelationship extends NodeRelationship {
    @Override
    public Object[] getChildren(Object object, NodeRelationshipFilter filter) {
        try {
            //Ignore all but type definitions (e.g. no imports, package statements)
            return filter.perform(newArrayList(((ICompilationUnit) object).getTypes())); // copy
        } catch (Exception e) {
            CloverPlugin.logError("Unable to collect types in compilation unit " + object, e);
            return new Object[]{};
        }
    }

    @Override
    public boolean includes(Object object) {
        return object instanceof ICompilationUnit;
    }
}
