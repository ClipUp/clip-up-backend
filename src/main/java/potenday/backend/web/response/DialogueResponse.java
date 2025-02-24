package potenday.backend.web.response;

import potenday.backend.domain.Dialogue;

record DialogueResponse(
    String speaker,
    Long startTime,
    Long endTime,
    String text
) {

    static DialogueResponse from(Dialogue dialogue) {
        return new DialogueResponse(dialogue.getSpeaker(), dialogue.getStartTime(), dialogue.getEndTime(), dialogue.getText());
    }

}
