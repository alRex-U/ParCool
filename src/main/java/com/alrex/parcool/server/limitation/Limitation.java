package com.alrex.parcool.server.limitation;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.config.ParCoolConfig;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class Limitation {

    //Whether this limitation is applied
    private boolean enabled = false;
    private final ID id;
    private final EnumMap<ParCoolConfig.Server.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Server.Booleans.class);
    private final EnumMap<ParCoolConfig.Server.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Server.Integers.class);
    private final EnumMap<ParCoolConfig.Server.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Server.Doubles.class);
    private final ActionLimitation[] actionLimitations = new ActionLimitation[ActionList.ACTIONS.size()];

    public Limitation(ID id) {
        this.id = id;
        for (int i = 0; i < actionLimitations.length; i++) {
            actionLimitations[i] = new ActionLimitation(true, 0);
        }
    }


    public boolean isEnabled() {
        return enabled;
    }

    public ID getID() {
        return id;
    }

    public boolean isPermitted(Class<? extends Action> action) {
        return actionLimitations[ActionList.getIndexOf(action)].isPossible();
    }

    public int getLeastStaminaConsumption(Class<? extends Action> action) {
        return actionLimitations[ActionList.getIndexOf(action)].getLeastStaminaConsumption();
    }

    public boolean get(ParCoolConfig.Server.Booleans item) {
        Boolean value = booleans.get(item);
        return value != null ? value : item.DefaultValue;
    }

    public int get(ParCoolConfig.Server.Integers item) {
        Integer value = integers.get(item);
        return value != null ? value : item.DefaultValue;
    }

    public double get(ParCoolConfig.Server.Doubles item) {
        Double value = doubles.get(item);
        return value != null ? value : item.DefaultValue;
    }

    public void set(ParCoolConfig.Server.Booleans item, boolean value) {
        booleans.put(item, value);
    }

    public void set(ParCoolConfig.Server.Integers item, int value) {
        integers.put(item, value);
    }

    public void set(ParCoolConfig.Server.Doubles item, double value) {
        doubles.put(item, value);
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public void setPossibilityOf(Class<? extends Action> action, boolean value) {
        int index = ActionList.getIndexOf(action);
        actionLimitations[index] =
                new ActionLimitation(
                        value,
                        actionLimitations[index].getLeastStaminaConsumption()
                );
    }

    public void setLeastStaminaConsumption(Class<? extends Action> action, int value) {
        int index = ActionList.getIndexOf(action);
        actionLimitations[index] =
                new ActionLimitation(
                        actionLimitations[index].isPossible(),
                        value
                );
    }

    public void setAllDefault() {
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            booleans.put(item, item.DefaultValue);
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            integers.put(item, item.DefaultValue);
        }
        for (int i = 0; i < actionLimitations.length; i++) {
            actionLimitations[i] = new ActionLimitation(true, 0);
        }
    }

    public void readFromServerConfig() {
        enabled = ParCoolConfig.Server.LimitationEnabled.get();
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            booleans.put(item, item.get());
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            integers.put(item, item.get());
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            doubles.put(item, item.get());
        }
        for (int i = 0; i < actionLimitations.length; i++) {
            actionLimitations[i] = new ActionLimitation(
                    ParCoolConfig.Server.getPermissionOf(ActionList.getByIndex(i)),
                    ParCoolConfig.Server.getLeastStaminaConsumptionOf(ActionList.getByIndex(i))
            );
        }
    }

    public void saveTo(JsonWriter writer) {
        LimitationJson json = new LimitationJson();
        json.imposed = enabled;
        json.booleans = new LinkedList<>();
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            LimitationJson.BooleanItem booleanItem = new LimitationJson.BooleanItem();
            booleanItem.name = item.Path;
            booleanItem.value = get(item);
            json.booleans.add(booleanItem);
        }
        json.integers = new LinkedList<>();
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            LimitationJson.IntegerItem integerItem = new LimitationJson.IntegerItem();
            integerItem.name = item.Path;
            integerItem.value = get(item);
            json.integers.add(integerItem);
        }
        json.doubles = new LinkedList<>();
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            LimitationJson.DoubleItem doubleItem = new LimitationJson.DoubleItem();
            doubleItem.name = item.Path;
            doubleItem.value = get(item);
            json.doubles.add(doubleItem);
        }
        json.actions = new LinkedList<>();
        for (int i = 0; i < actionLimitations.length; i++) {
            ActionLimitation limitation = actionLimitations[i];
            if (limitation == null) continue;
            Class<? extends Action> action = ActionList.getByIndex(i);
            LimitationJson.ActionPermission permission = new LimitationJson.ActionPermission();
            permission.name = action.getSimpleName();
            permission.permitted = limitation.isPossible();
            permission.stamina_consumption = limitation.getLeastStaminaConsumption();
            json.actions.add(permission);
        }
        Gson gson = new Gson();
        gson.toJson(json, LimitationJson.class, writer);
    }

    public void loadFrom(JsonReader reader) {
        Gson gson = new Gson();
        LimitationJson parsed = gson.fromJson(reader, LimitationJson.class);
        enabled = parsed.imposed;
        for (LimitationJson.BooleanItem item : parsed.booleans) {
            for (ParCoolConfig.Server.Booleans configItem : ParCoolConfig.Server.Booleans.values()) {
                if (configItem.getPath().equals(item.name)) {
                    booleans.put(configItem, item.value);
                    break;
                }
            }
        }
        for (LimitationJson.IntegerItem item : parsed.integers) {
            for (ParCoolConfig.Server.Integers configItem : ParCoolConfig.Server.Integers.values()) {
                if (configItem.getPath().equals(item.name)) {
                    integers.put(configItem, item.value);
                    break;
                }
            }
        }
        for (LimitationJson.DoubleItem item : parsed.doubles) {
            for (ParCoolConfig.Server.Doubles configItem : ParCoolConfig.Server.Doubles.values()) {
                if (configItem.getPath().equals(item.name)) {
                    doubles.put(configItem, item.value);
                    break;
                }
            }
        }
        for (LimitationJson.ActionPermission item : parsed.actions) {
            for (int i = 0; i < ActionList.ACTIONS.size(); i++) {
                if (ActionList.ACTIONS.get(i).getSimpleName().equals(item.name)) {
                    actionLimitations[i] = new ActionLimitation(item.permitted, item.stamina_consumption);
                }
            }
        }
    }

    private static class LimitationJson {
        public Boolean imposed;
        public List<BooleanItem> booleans;
        public List<IntegerItem> integers;
        public List<DoubleItem> doubles;
        public List<ActionPermission> actions;

        public static class ActionPermission {
            public String name;
            public Boolean permitted;
            public Integer stamina_consumption;
        }

        public static class DoubleItem {
            public String name;
            public Double value;
        }

        public static class IntegerItem {
            public String name;
            public Integer value;
        }

        public static class BooleanItem {
            public String name;
            public Boolean value;
        }
    }

    public static class ID implements Comparable<ID> {
        public final String group;
        public final String name;

        public ID(String group, String name) {
            this.group = group;
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ID) {
                ID other = (ID) obj;
                return group.equals(other.group) && name.equals(other.name);
            }
            return false;
        }

        @Override
        public int compareTo(ID o) {
            int groupCompare = group.compareTo(o.group);
            if (groupCompare != 0) return groupCompare;
            return name.compareTo(o.name);
        }
    }
}
