package com.alrex.parcool.client.skilltree.trees;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.common.action.ParCoolActions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParCoolRunningSkillTree extends SkillTree {
    public ParCoolRunningSkillTree() {
        super(new Entry<>(
                ParCoolActions.FAST_RUN,
                new Entry<>(ParCoolActions.FAST_SWIM),
                new Entry<>(ParCoolActions.VAULT,
                        new Entry<>(ParCoolActions.BREAKFALL)
                ),
                new Entry<>(ParCoolActions.TRICK_JUMP,
                        new Entry<>(ParCoolActions.DODGE)
                ),
                new Entry<>(ParCoolActions.DIVE,
                        new Entry<>(ParCoolActions.SKYDIVE)
                ),
                new Entry<>(ParCoolActions.HORIZONTAL_WALL_RUN),
                new Entry<>(ParCoolActions.WALL_JUMP,
                        new Entry<>(ParCoolActions.WALL_RUN,
                                new Entry<>(ParCoolActions.CASTAWAY)
                        )
                )
        ));
    }
}
