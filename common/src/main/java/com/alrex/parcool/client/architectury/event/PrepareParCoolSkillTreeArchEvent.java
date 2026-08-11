package com.alrex.parcool.client.architectury.event;

import com.alrex.parcool.api.client.skilltree.SkillTree;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

import java.util.List;

public interface PrepareParCoolSkillTreeArchEvent {
    Event<PrepareParCoolSkillTreeArchEvent> EVENT = EventFactory.createLoop();

    void onPrepareParCoolSkillTree(List<SkillTree> skillTrees);
}
