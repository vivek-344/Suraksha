package permissions.dispatcher

/**
 * Register some methods which permissions are needed.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.BINARY)
annotation class NeedsPermission(vararg val value: String, val maxSdkVersion: Int = 0)