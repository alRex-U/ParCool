package com.alrex.parcool.extern.paraglider;

import net.minecraft.resources.ResourceLocation;
import tictim.paraglider.api.ParagliderAPI;

public interface ParCoolPlayerStates {
    ResourceLocation FAST_RUNNING = ParagliderAPI.id("fast_running");
    int FAST_RUNNING_STAMINA_DELTA = -15;
    double FAST_RUNNING_PRIORITY = 3;


    ResourceLocation DODGE = ParagliderAPI.id("dodge");
    int DODGE_STAMINA_DELTA = -15;
    double DODGE_PRIORITY = 4;

    ResourceLocation FAST_SWIMMING = ParagliderAPI.id("fast_swimming");
    int FAST_SWIMMING_STAMINA_DELTA = -15;


    ResourceLocation CLING_TO_CLIFF = ParagliderAPI.id("cling_to_cliff");
    int CLING_TO_CLIFF_STAMINA_DELTA = -5;

    ResourceLocation CLIMB_UP = ParagliderAPI.id("climb_up");
    int CLIMB_UP_STAMINA_DELTA = -10;

    ResourceLocation BREAKFALL = ParagliderAPI.id("breakfall");
    int BREAKFALL_STAMINA_DELTA = -20;

    ResourceLocation ROLL = ParagliderAPI.id("roll");
    int ROLL_STAMINA_DELTA = -15;

    ResourceLocation HORIZONTAL_WALL_RUN = ParagliderAPI.id("horizontal_wall_run");
    int HORIZONTAL_WALL_RUN_STAMINA_DELTA = -10;

    ResourceLocation VERTICAL_WALL_RUN = ParagliderAPI.id("vertical_wall_run");
    int VERTICAL_WALL_RUN_STAMINA_DELTA = -12;
    ResourceLocation VAULT = ParagliderAPI.id("vault");
    int VAULT_STAMINA_DELTA = -12;

    ResourceLocation CAT_LEAP = ParagliderAPI.id("cat_leap");
    int CAT_LEAP_STAMINA_DELTA = -15;
}
