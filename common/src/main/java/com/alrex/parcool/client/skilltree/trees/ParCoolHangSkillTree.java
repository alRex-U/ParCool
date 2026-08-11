package com.alrex.parcool.client.skilltree.trees;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.common.action.ParCoolActions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParCoolHangSkillTree extends SkillTree {
    public ParCoolHangSkillTree() {
        super(new Entry<>(ParCoolActions.HANG_ON,
                new Entry<>(ParCoolActions.CLIMB_UP, new Entry<>(ParCoolActions.POLE_CLIMB)),
                new Entry<>(ParCoolActions.SLIDE_DOWN),
                new Entry<>(ParCoolActions.HANG_DOWN),
                new Entry<>(ParCoolActions.RIDE_ZIPLINE)
        ));
    }
}
