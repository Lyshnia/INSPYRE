package inspyre

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import inspyre.ResponseCodes.Create
import spock.lang.Specification

@Integration
@Rollback
class RegisterControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test save saves user"() {
        given:
        RestBuilder rest = new RestBuilder()

        when: "No Parameters"
        RestResponse resp = rest.post("http://localhost:8080/api/register") {
            accept('application/json')
            contentType('application/json')
            json {

            }
        }

        then: "Should say UnknwnE"
        Create.convert(resp.json.status) == Create.UNKNOWN_ERROR

        when: "Any Parameter is empty"
        resp = rest.post("http://localhost:8080/api/register" +
                "?username={username}&password={password}&email={email}&names={names}") {
            accept('application/json')
            urlVariables([
                    username: 'mavado223',
                    password: '',
                    email   : 'shumpy@gmial.com',
                    names   : 'Pop Caan'
            ])
        }

        then: "Should say UnknwnE"
        Create.convert(resp.json.status) == Create.UNKNOWN_ERROR

        when: "Everything is complete"
        resp = rest.post("http://localhost:8080/api/register" +
                "?username={username}&password={password}&email={email}&names={names}") {
            accept('application/json')
            urlVariables([
                    username: 'mavado223',
                    password: 'hhuiui',
                    email   : 'shumpy@gmial.com',
                    names   : 'Pop Caan'
            ])
        }

        then: "Should say SUCCESS"
        Create.convert(resp.json.status) == Create.SUCCESS

        when: "Duplicate username"
        resp = rest.post("http://localhost:8080/api/register" +
                "?username={username}&password={password}&email={email}&names={names}") {
            accept('application/json')
            urlVariables([
                    username: 'mavado223',
                    password: 'hg',
                    email   : 'shumpy_diff@gmial.com',
                    names   : 'Pop Caan'
            ])
        }

        then: "Should say USERNAME_EXISTS"
        Create.convert(resp.json.status) == Create.USERNAME_EXISTS

        when: "Duplicate Email"
        resp = rest.post("http://localhost:8080/api/register" +
                "?username={username}&password={password}&email={email}&names={names}") {
            accept('application/json')
            urlVariables([
                    username: 'poppy',
                    password: 'ouy',
                    email   : 'shumpy@gmial.com',
                    names   : 'Pop Caan'
            ])
        }

        then: "Should say EMAIL_EXISTS"
        Create.convert(resp.json.status) == Create.EMAIL_EXISTS
    }

    void "test update user"() {
        given:
        RestBuilder rest = new RestBuilder()

        when: "no username"
        RestResponse resp = rest.put("http://localhost:8080/api/register" +
                "?username={username}&gender={gender}&dob={dob}&agerange={agerange}") {
            accept('application/json')
            urlVariables([
                    username: 'mavado2233',
                    gender: 'hhuiui',
                    dob   : 'shumpy@gmial.com',
                    agerange   : 'Pop Caan'
            ])
        }

        then: "Should redirect"
        resp.statusCodeValue == 302

        when: "Everything is complete"
        resp = rest.put("http://localhost:8080/api/register/mavado223" +
                "?gender={gender}&dob={dob}&agerange={agerange}") {
            accept('application/json')
            urlVariables([
                    gender: 'MALE',
                    dob   : '12/12/2012',
                    agerange   : '21-30'
            ])
        }

        then: "Should say SUCCESS"
        Create.convert(resp.json.status) == Create.SUCCESS

        when: "GEO is complete"
        resp = rest.put("http://localhost:8080/api/register/mavado223" +
                "?city={city}&state={state}&country={country}") {
            accept('application/json')
            urlVariables([
                    city: 'Abuja',
                    state   : 'FCT',
                    country   : 'NG'
            ])
        }

        then: "Should say SUCCESS"
        Create.convert(resp.json.status) == Create.SUCCESS
    }

    void "test isComplete"() {
        given:
        RestBuilder rest = new RestBuilder()


        when: "Everything is complete"
        RestResponse resp = rest.get("http://localhost:8080/api/register" +
                "?username={username}") {
            accept('application/json')
            urlVariables([
                    username: 'styl3r'
            ])
        }

        then: "Should say false"
        Create.convert(resp.json.status) == Create.SUCCESS
        !resp.json.isComplete

        when: "GEO is complete"
        resp = rest.get("http://localhost:8080/api/register" +
                "?username={username}") {
            accept('application/json')
            urlVariables([
                    username: 'mavado223'
            ])
        }

        then: "Should say true"
        Create.convert(resp.json.status) == Create.SUCCESS
        resp.json.isComplete

    }


}
