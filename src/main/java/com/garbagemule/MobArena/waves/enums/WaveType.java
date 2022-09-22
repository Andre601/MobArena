package com.garbagemule.MobArena.waves.enums;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.message.MessageKey;

public enum WaveType
{
    DEFAULT {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(MessageKey.WAVE_DEFAULT, "" + wave);
        }
    },

    SPECIAL {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(MessageKey.WAVE_SPECIAL, "" + wave);
        }
    },

    SWARM {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(MessageKey.WAVE_SWARM, "" + wave);
        }
    },

    SUPPLY {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(MessageKey.WAVE_SUPPLY, "" + wave);
        }
    },

    BOSS {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(MessageKey.WAVE_BOSS, "" + wave);
        }
    },

    UPGRADE {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(MessageKey.WAVE_UPGRADE, "" + wave);
        }
    };

    public abstract void announce(Arena arena, int wave);

    public static WaveType fromString(String string) {
        try {
            return WaveType.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
