package com.alrex.parcool.client.skilltree;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.skilltree.trees.ParCoolCrawlSkillTree;
import com.alrex.parcool.client.skilltree.trees.ParCoolHangSkillTree;
import com.alrex.parcool.client.skilltree.trees.ParCoolRunningSkillTree;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ParCoolSkillTrees {
    private static final List<SkillTree> TREES = List.of(
            new ParCoolRunningSkillTree(),
            new ParCoolHangSkillTree(),
            new ParCoolCrawlSkillTree()
    );

    public static void onPrepareTrees(List<SkillTree> skillTrees) {
        skillTrees.addAll(TREES);
    }
}
