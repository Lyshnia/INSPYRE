package inspyre

import grails.plugin.springsecurity.annotation.Secured
import grails.rest.*
import grails.converters.*
import inspyre.ResponseCodes.Create

class RegisterController {
    static responseFormats = ['json', 'xml']

    @Secured('ROLE_ADMIN')
    def index() {}

    @Secured('isFullyAuthenticated()')
    def show(User user) {

        [user: user]
    }

    @Secured('permitAll')
    def save() {


        if (params['username'] == null || params['password'] == null
                || params['names'] == null || params['email'] == null) {

            render view: 'save', model: [status: Create.UNKNOWN_ERROR]
            return
        }

        if (params['username'].empty || params['password'].empty
                || params['names'].empty || params['email'].empty) {

            render view: 'save', model: [status: Create.UNKNOWN_ERROR]
            return
        }


        [status: Create.SUCCESS]
    }
}
