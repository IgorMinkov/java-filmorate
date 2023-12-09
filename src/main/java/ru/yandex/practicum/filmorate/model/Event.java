package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event implements Comparable<Event> {

    private Long eventId;

    @NotNull
    private Long userId;

    @NotNull
    private Long entityId;

    @NotNull
    private EventType eventType;

    @NotNull
    private OperationType operation;

    @PastOrPresent
    private Long timestamp;

    public Map<String, Object> toMap() {

        Map<String, Object> eventProperties = new HashMap<>();

        eventProperties.put("event_id", eventId);
        eventProperties.put("user_id", userId);
        eventProperties.put("entity_id", entityId);
        eventProperties.put("event_type", eventType.getName());
        eventProperties.put("operation_type", operation.getName());
        eventProperties.put("time_stamp", timestamp);

        return eventProperties;
    }

    public enum EventType {
        FRIEND("FRIEND"), LIKE("LIKE"), REVIEW("REVIEW");

        private final String name;

        EventType(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }

        @JsonCreator
        public static EventType fromName(String name) {

            if (name == null) {
                return null;
            }

            switch (name) {
                case "FRIEND": {
                    return FRIEND;
                }

                case "LIKE": {
                    return LIKE;
                }

                case "REVIEW": {
                    return REVIEW;
                }

                default: {
                    throw new ValidationException(String.format("Неизвестный тип события: '%s'", name));
                }
            }
        }
    }

    public enum OperationType {
        ADD("ADD"), UPDATE("UPDATE"), REMOVE("REMOVE");

        private final String name;

        OperationType(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return name;
        }

        @JsonCreator
        public static OperationType fromName(String name) {

            if (name == null) {
                return null;
            }

            switch (name) {
                case "ADD": {
                    return ADD;
                }

                case "UPDATE": {
                    return UPDATE;
                }

                case "REMOVE": {
                    return REMOVE;
                }
                default: {
                    throw new ValidationException(String.format("Неизвестная операция: '%s'", name));
                }
            }
        }
    }

    @Override
    public int compareTo(Event otherEvent) {
        return this.getTimestamp().compareTo(otherEvent.getTimestamp());
    }
}
