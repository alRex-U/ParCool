package com.alrex.parcool.client.skilltree.trees;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import com.alrex.parcool.common.action.ParCoolActions;

public class ParCoolCrawlSkillTree extends SkillTree {
    public ParCoolCrawlSkillTree() {
        super(new Entry<>(ParCoolActions.CRAWL,
                new Entry<>(ParCoolActions.SLIDE),
                new Entry<>(ParCoolActions.HIDE_IN_BLOCK),
                new Entry<>(ParCoolActions.CHARGE_JUMP)
        ));
    }
}
