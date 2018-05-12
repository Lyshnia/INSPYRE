package inspyre

import grails.plugin.springsecurity.annotation.Secured
import inspyre.ResponseCodes.Create

class RegisterController {
    RegisterService registerService

    static responseFormats = ['json', 'xml']

    @Secured('permitAll')
    def index() {
        if (params.username == null) {
            render view: 'save', model: [status: Create.UNKNOWN_ERROR]
            return
        }
        User user = User.findByUsername(params.username as String)
        if (user == null) {
            render view: 'save', model: [status: Create.UNKNOWN_ERROR]
            return
        }

        boolean isComplete = registerService.isComplete(user)

        [isComplete: isComplete, status: Create.SUCCESS]
    }

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

        String email = params.email
        String names = params.names
        String username = params.username
        String password = params.password

        if (username.empty || password.empty || names.empty || email.empty) {
            render view: 'save', model: [status: Create.UNKNOWN_ERROR]
            return
        }

        HashMap<String, Object> create = registerService.createUser(username, password, email, names)

        if (create.code == Create.SUCCESS) {
            String clientIP = request.getRemoteAddr() ?: request.getHeader("X-Forwarded-For")
            if (clientIP == null) {
                clientIP = request.getHeader("Client-IP")
            }

            registerService.addUserSec(create.user as User, clientIP, request.getHeader("User-Agent"))
        } else {
            render view: 'save', model: [status: create.code]
            return
        }


        [status: Create.SUCCESS]
    }

//    @PreAuthorize("")
    @Secured('permitAll')
    def update() {

        User user = User.findByUsername(params.username as String)
        if (user == null) {
            render view: 'save', model: [status: Create.UNKNOWN_ERROR]
            return
        }

        HashMap<String, String> geo = new HashMap<String, String>()
        HashMap<String, String> person = new HashMap<String, String>()

        ['gender', 'dob', 'agerange'].each {
            if (params.containsKey(it)) {
                person.put(it, params[it].toString())
            }
        }

        boolean[] hasGeo = [false, false, false]
        String[] items = ['city', 'state', 'country']
        for (int i = 0; i < 3; i++) {
            if (params.containsKey(items[i])) {
                hasGeo[i] = true
            }
        }

        if (!hasGeo.contains(false)) {
            geo.put('city', params.city as String)
            geo.put('state', params.state as String)
            geo.put('country', params.country as String)
        }

        log.info("User: ${params.username} || hasGeo: ${geo}")

        Create update = registerService.updateUser(user, person, geo)

        [status: update]
    }


}
