package inspyre

class UserSecData {

    User user
    Date regDate
    String registrationIp
    String regClient
    String lgnClient
    String lgnIp
    Date lgnDate
    String lastLgnIp
    String lastLgnClient
    Date lastLgnDate

    static constraints = {
        user unique: true
        lgnClient nullable: true
        lgnIp nullable: true
        lgnDate nullable: true
        lastLgnIp nullable: true
        lastLgnClient nullable: true
        lastLgnDate nullable: true
    }

    def beforeUpdate() {
        if (lgnIp != null) {
            lastLgnIp = lgnIp
        }
        if (lgnClient != null) {
            lastLgnClient = lgnClient
        }
        if (lgnDate != null) {
            lastLgnDate = lgnDate
        }
    }
}
