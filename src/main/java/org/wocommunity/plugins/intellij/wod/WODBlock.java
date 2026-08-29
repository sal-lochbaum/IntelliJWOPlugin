package org.wocommunity.plugins.intellij.wod;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.formatter.common.AbstractBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wocommunity.plugins.intellij.psi.wod.WODTypes;

import java.util.ArrayList;
import java.util.List;

final class WODBlock extends AbstractBlock {
    private final SpacingBuilder spacingBuilder;

    WODBlock(@NotNull ASTNode node,
             @Nullable Alignment alignment,
             @Nullable Indent indent,
             @NotNull SpacingBuilder spacingBuilder) {
        super(node, null, alignment);
        this.spacingBuilder = spacingBuilder;
        this.myIndent = indent;
    }

    private final Indent myIndent;

    @Override
    protected @NotNull List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            IElementType type = child.getElementType();
            if (type == TokenType.WHITE_SPACE) {
                continue;
            }
            blocks.add(new WODBlock(child, null, childIndent(type), spacingBuilder));
        }
        return blocks;
    }

    private static @Nullable Indent childIndent(@NotNull IElementType type) {
        if (type == WODTypes.WOD_ASSIGNMENT_LIST) {
            return Indent.getNormalIndent();
        }
        return Indent.getNoneIndent();
    }

    @Override
    public @Nullable Indent getIndent() {
        return myIndent;
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
