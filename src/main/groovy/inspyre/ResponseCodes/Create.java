package inspyre.ResponseCodes;

public enum Create {
    SUCCESS(9000),
    EMAIL_EXISTS(9010),
    USERNAME_EXISTS(9011),
    UNKNOWN_ERROR(9099),
    INVALID_DOB(9020),
    INVALID_COUNTRY(9021);

    private final int id;

    Create(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

}
