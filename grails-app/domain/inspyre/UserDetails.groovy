package inspyre

class UserDetails {

    User user
    String email
    String names
    int ageRange
    Gender gender
    Date dob
    Boolean isLimited = true

    static constraints = {
        user unique: true
        email unique: true
        dob nullable: true
        gender nullable: true
        ageRange nullable: true
    }
}
