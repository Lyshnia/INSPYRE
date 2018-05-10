package inspyre

class UserDetails {

    User user
    String email
    String names
    List ageRange
    Gender gender
    Date dob
    Boolean isLimited = true

    static hasMany = [ageRange: int]

    static constraints = {
        user unique: true
        email unique: true
        dob nullable: true
        gender nullable: true
        ageRange nullable: true
    }
}
