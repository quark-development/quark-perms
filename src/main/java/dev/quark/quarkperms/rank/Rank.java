package dev.quark.quarkperms.rank;

import lombok.Data;

import java.util.List;

@Data
public class Rank {

    private final String name;

    private String prefix;
    private int priority;
    private boolean isDefault = false;

    private List<Rank> inheritance;
    private List<String> permissions;

    public void reset() {
        prefix = null; priority = -1; inheritance = null; permissions = null;
    }

}
