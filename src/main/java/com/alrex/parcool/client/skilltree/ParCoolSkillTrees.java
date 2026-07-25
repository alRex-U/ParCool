package com.alrex.parcool.client.skilltree;

import com.alrex.parcool.api.client.skilltree.PrepareParCoolSkillTreeEvent;
import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.client.skilltree.trees.ParCoolCrawlSkillTree;
import com.alrex.parcool.client.skilltree.trees.ParCoolHangSkillTree;
import com.alrex.parcool.client.skilltree.trees.ParCoolRunningSkillTree;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ParCoolSkillTrees {
    private static final List<SkillTree> TREES = List.of(
            new ParCoolRunningSkillTree(),
            new ParCoolHangSkillTree(),
            new ParCoolCrawlSkillTree()
    );

    @SubscribeEvent
    public static void onPrepareTrees(PrepareParCoolSkillTreeEvent event) {
        for (var tree : TREES) {
            event.addSkillTree(tree);
        }
    }
}
