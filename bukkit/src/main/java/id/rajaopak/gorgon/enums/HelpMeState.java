package id.rajaopak.gorgon.enums;

public enum HelpMeState {

    WAITING("waiting"),
    ACCEPTED("accepted"),
    DECLINED("declined"),
    FINISH("finish");

    private final String status;

    HelpMeState(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static HelpMeState fromString(String status) {
        if (status.equalsIgnoreCase(WAITING.getStatus())) {
            return WAITING;
        } else if (status.equalsIgnoreCase(ACCEPTED.getStatus())) {
            return ACCEPTED;
        } else if (status.equalsIgnoreCase(DECLINED.getStatus())) {
            return DECLINED;
        } else if (status.equalsIgnoreCase(FINISH.getStatus())) {
            return FINISH;
        } else {
            return null;
        }
    }
}
