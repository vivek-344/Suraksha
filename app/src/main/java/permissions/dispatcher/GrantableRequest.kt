package permissions.dispatcher

interface GrantableRequest : PermissionRequest {
    fun grant()
}