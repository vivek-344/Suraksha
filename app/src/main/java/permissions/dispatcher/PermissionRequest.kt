package permissions.dispatcher

/**
 * Interface used by [OnShowRationale] methods to allow for continuation
 * or cancellation of a permission request.
 */
interface PermissionRequest {
    fun proceed()
    fun cancel()
}