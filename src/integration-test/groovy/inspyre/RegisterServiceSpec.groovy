package inspyre

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import inspyre.ResponseCodes.Create
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Integration
@Rollback
class RegisterServiceSpec extends Specification {
    @Autowired
    RegisterService service

    User userGlobal

    def setup() {
        service.createUser("jiayi112", "pass", "jyi11@sas.com", "Jia Yi U.")
        userGlobal = User.findByUsername("jiayi112")
    }

    def cleanup() {
        UserDetails.findByUser(userGlobal).delete()
        UserRole.findByUser(userGlobal).delete()
        User.findByUsername("jiayi112").delete()

    }

    void "test createUser creates user"() {
        given: "Create user details"
        HashMap<String, Object> create = service.createUser("styl3r9", "pass", "mas@sas.com", "Jerry U.")

        expect: "user was created successfully"
        create.code == Create.SUCCESS
        create.user == User.findByUsername("styl3r9")
        UserDetails.findByUser(create.user as User) == UserDetails.findByEmail("mas@sas.com")


        when: "Create user with existing email"
        HashMap<String, Object> createEE = service.createUser("iHaveExistingEmail", "pass", "mas@sas.com", "Jerry U.")

        then: "user was not created successfully"
        createEE.code == Create.EMAIL_EXISTS

        when: "Create user with existing email"
        HashMap<String, Object> createEU = service.createUser("styl3r9", "pass", "tootoo@sas.com", "Jerry U.")

        then: "user was not created successfully"
        createEU.code == Create.USERNAME_EXISTS
    }

    void "test AddSecUser saves"() {
        given:
        service.addUserSec(userGlobal, "10.199.212.801", "MacOS blach 123")

        when:
        UserSecData userSecData = UserSecData.findByUser(userGlobal)

        then:
        userSecData != null
        userSecData.registrationIp == "10.199.212.801"
        userSecData.regClient == "MacOS blach 123"
    }

    void "test UpdateUser updates user details based on whats supplied "() {
        given: "Age and Gender without DOB"
        HashMap<String, String> person = ['gender': 'FEMALE', 'agerange': '11-20']

        when:
        Create updateAG = service.updateUser(userGlobal, person)

        then:
        updateAG == Create.SUCCESS
        UserDetails.findByUser(userGlobal).gender == Gender.FEMALE
        UserDetails.findByUser(userGlobal).ageRange == '11-20'

        when: "DOB is incorrect format"

        Create updateDOB = service.updateUser(userGlobal, ['dob': '2001/13/12'])

        then:
        updateDOB == Create.INVALID_DOB

        when: "DOB is correct"
        Create updateDOBc = service.updateUser(userGlobal, ['dob': '04/06/1995'])

        then:
        updateDOBc == Create.SUCCESS
        UserDetails.findByUser(userGlobal).dob == LocalDate.parse('04/06/1995', DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    }

    void "test UpdateUser updates user Geo"() {
        given: "All detail must be available"
        HashMap<String, String> geo = ['city': 'Abuja', 'state': 'F.C.T', 'country': 'NG']

        when:
        Create updateGeo = service.updateUser(userGlobal, null, geo)

        then:
        updateGeo == Create.SUCCESS
    }

    void "test isComplete"() {
        given:
        boolean isComplete = service.isComplete(userGlobal)

        expect:
        !isComplete

        when: "Everything is complete"
        HashMap<String, String> person = ['gender': 'FEMALE', 'agerange': '11-20', 'dob': '20/12/2012']
        service.updateUser(userGlobal, person)
        HashMap<String, String> geo = ['city': 'Abuja', 'state': 'F.C.T', 'country': 'NG']
        service.updateUser(userGlobal, null, geo)

        and:
        boolean isNowComplete = service.isComplete(userGlobal)
        UserDetails userDetails = UserDetails.findByUser(userGlobal)

        then:
        isNowComplete
        !userDetails.isLimited

    }
}
