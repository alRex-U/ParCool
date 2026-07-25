package com.alrex.parcool.api.client.skilltree;

import net.minecraftforge.eventbus.api.Event;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PrepareParCoolSkillTreeEvent extends Event {
    private final LinkedList<SkillTree> skillTrees = new LinkedList<>();

    public PrepareParCoolSkillTreeEvent() {
    }

    public void addSkillTree(SkillTree skillTree) {
        skillTrees.add(skillTree);
    }

    public List<SkillTree> getSkillTrees() {
        return Collections.unmodifiableList(skillTrees);
    }
}
