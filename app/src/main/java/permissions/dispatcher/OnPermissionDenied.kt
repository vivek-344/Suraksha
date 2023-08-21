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
annotation class OnPermissionDenied(vararg val value: String)