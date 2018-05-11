package inspyre

import grails.gorm.transactions.Transactional
import inspyre.ResponseCodes.Create

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Transactional
class RegisterService {

    /**
     * Create user with minimal requirements
     *
     * @param username
     * @param password
     * @param email
     * @param names
     * @return [code: Create, user: User?]
     */
    HashMap<String, Object> createUser(String username, String password, String email, String names) {
        Create code
        User user = null

        //Check if username exists
        if (User.findByUsername(username)) {
            code = Create.USERNAME_EXISTS
        } else if (UserDetails.findByEmail(email)) {
            code = Create.EMAIL_EXISTS
        } else {

            //Create SSR User
            user = new User(username: username, password: password).save()
            //Add User Role
            Role role = Role.findOrSaveWhere(authority: 'ROLE_USER')
            UserRole.create(user, role)

            //Add userDetails
            new UserDetails(user: user, names: names, email: email).save()

            sendEmail(email)
            code = Create.SUCCESS
        }

        return new HashMap<String, Object>(code: code, user: user)
    }

    /**
     * Add user security stuff like IP and Client details
     * @param user
     * @param ip
     * @param browser
     */
    void addUserSec(User user, String ip, String browser) {

        new UserSecData(user: user, registrationIp: ip, regClient: browser, regDate: new Date()).save()
    }

    /**
     * Send welcome email only
     * @param to
     */
    void sendEmail(String to) {

    }

    /**
     * Add "After Registration" details
     * @param user
     * @param person
     * @param geo
     * @return
     */
    Create updateUser(User user, HashMap<String, String> person, HashMap<String, String> geo = null) {

        Create Status = Create.SUCCESS

        if (person != null && person.size() > 0) {
            UserDetails userDetails = UserDetails.findByUser(user)

            if (person.containsKey('gender')) {
                userDetails.gender = person['gender'].toUpperCase() as Gender
            }

            if (person.containsKey('dob')) {
                try {
                    userDetails.dob = LocalDate.parse(person['dob'], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (DateTimeParseException e) {
                    return Create.INVALID_DOB
                }
            }

            if (person.containsKey('agerange')) {
                userDetails.ageRange  = person['agerange']
            }

            if (!userDetails.validate()) {
                return Create.UNKNOWN_ERROR
            }

            Status = userDetails.save() ? Create.SUCCESS : Create.UNKNOWN_ERROR

        }

        if (geo != null && geo.size() > 0) {
            UserGeo userGeo = UserGeo.findOrSaveWhere(user:  user)

            userGeo.city = geo['city']
            userGeo.state = geo['state']
            userGeo.country = geo['country']
            //TODO: add country validator

            if (!userGeo.validate()) {
                return Create.UNKNOWN_ERROR
            }

            Status = userGeo.save() ? Create.SUCCESS : Create.UNKNOWN_ERROR
        }

        return Status
    }

    /**
     * Check if user has completed all details then lift limitation
     * @param user
     * @return
     */
    boolean isComplete(User user) {

        // Check if Geo is available
        if (!UserGeo.findByUser(user))
            return false

        UserDetails userDetails = UserDetails.findByUser(user)

        if (userDetails.gender == null || userDetails.dob == null || userDetails.ageRange == null)
            return false

        userDetails.isLimited = false
        userDetails.save()

        return true
    }
}
