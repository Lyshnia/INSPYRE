package inspyre

import java.time.LocalDate

class UserDetails {

    User user
    String email
    String names
    String ageRange
    Gender gender
    LocalDate dob
    Boolean isLimited = true


    static constraints = {
        user unique: true
        email unique: true
        dob nullable: true
        gender nullable: true
        ageRange nullable: true
    }
}
