// Copyright (c) 2015-2016 K Team. All Rights Reserved.
package org.kframework.kore.compile;

import org.kframework.compile.ConfigurationInfo;
import org.kframework.compile.ConfigurationInfoFromModule;
import org.kframework.compile.LabelInfo;
import org.kframework.compile.LabelInfoFromModule;
import org.kframework.definition.Definition;
import org.kframework.definition.DefinitionTransformer;
import org.kframework.definition.Module;
import org.kframework.definition.Sentence;

/**
 * Apply the configuration concretization process.
 * The implicit {@code <k>} cell is added by another stage, AddImplicitComputationCell.
 * <p>
 * The input may freely use various configuration abstractions
 * and Full K flexibilites. See {@link IncompleteCellUtils} for a
 * description of the expected term structure.
 * The output will represent cells in
 * strict accordance with their declared fixed-arity productions.
 * <p>
 * This is a simple composition of the
 * {@link AddTopCellToRules}, {@link AddParentCells},
 * {@link CloseCells}, and {@link SortCells} passes,
 * see their documentation for details on the transformations.
 */
public class ConcretizeCells {
    final ConfigurationInfo configurationInfo;
    final LabelInfo labelInfo;
    final SortInfo sortInfo;
    final Module module;

    final AddParentCells addParentCells;
    final CloseCells closeCells;
    final SortCells sortCells;
    private final AddTopCellToRules addRootCell;

    public static Definition transformDefinition(Definition input) {
        ConfigurationInfoFromModule configInfo = new ConfigurationInfoFromModule(input.mainModule());
        LabelInfo labelInfo = new LabelInfoFromModule(input.mainModule());
        SortInfo sortInfo = SortInfo.fromModule(input.mainModule());
        return DefinitionTransformer.fromSentenceTransformer(
                new ConcretizeCells(configInfo, labelInfo, sortInfo, input.mainModule())::concretize,
                "concretizing configuration"
        ).apply(input);
    }

    public ConcretizeCells(ConfigurationInfo configurationInfo, LabelInfo labelInfo, SortInfo sortInfo, Module module) {
        this.configurationInfo = configurationInfo;
        this.labelInfo = labelInfo;
        this.sortInfo = sortInfo;
        this.module = module;
        addRootCell = new AddTopCellToRules(configurationInfo, labelInfo);
        addParentCells = new AddParentCells(configurationInfo, labelInfo);
        closeCells = new CloseCells(configurationInfo, sortInfo, labelInfo);
        sortCells = new SortCells(configurationInfo, labelInfo, module);
    }

    public Sentence concretize(Sentence s) {
        s = addRootCell.addImplicitCells(s);
        s = addParentCells.concretize(s);
        s = closeCells.close(s);

        s = sortCells.preprocess(s);
        s = sortCells.sortCells(s);
        s = sortCells.postprocess(s);
        return s;
    }
}
