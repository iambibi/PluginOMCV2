package fr.openmc.core.features.city.sub.notation;

import lombok.Getter;

@Getter
public enum NotationNote {
    NOTE_ARCHITECTURAL(40),
    NOTE_COHERENCE(10),

    NOTE_ACTIVITY(5),

    NOTE_PIB(15);
    private final int maxNote;


    NotationNote(int maxNote) {
        this.maxNote = maxNote;
    }
}
