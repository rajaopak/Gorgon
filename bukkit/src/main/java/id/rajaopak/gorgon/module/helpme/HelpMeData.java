package id.rajaopak.gorgon.module.helpme;

import id.rajaopak.gorgon.enums.HelpMeState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class HelpMeData {

    private final UUID helpMeUUID;
    private final UUID senderUUID;
    private final String senderName;
    private final String message;
    private final Instant sendTime;
    private final String serverName;
    private UUID staffUUID;
    private String staffName;
    private Instant acceptedTime;
    private HelpMeState state = HelpMeState.WAITING;

    @RequiredArgsConstructor
    @Getter
    public static class HelpMeDataBuilder {
        private final UUID helpMeUUID;
        private final UUID senderUUID;
        private final String senderName;
        private final String message;
        private final Timestamp sendTime;
        private final String serverName;
        private final UUID staffUUID;
        private final String staffName;
        private final Timestamp acceptedTime;
        private final HelpMeState state;

        public HelpMeData build() {
            HelpMeData data = new HelpMeData(helpMeUUID, senderUUID, senderName, message, sendTime.toInstant(), serverName);

            data.setStaffName(staffName);
            data.setStaffUUID(staffUUID);
            data.setState(state);
            data.setAcceptedTime(acceptedTime.toInstant());

            return data;
        }
    }
}
