package inspyre

class BootStrap {

    def init = { servletContext ->

        environments {
            development {
                def role1 = new Role(authority: "ROLE_ADMIN").save flush: true
                def user1 = new User(username: "styl3r9", password: "pass").save flush: true
                UserRole.create(user1, role1)

                def role2 = new Role(authority: "ROLE_USER").save flush: true
                def user2 = new User(username: "styl3r", password: "pass").save flush: true
                UserRole.create(user2, role2)
            }
        }

    }
    def destroy = {
    }
}
