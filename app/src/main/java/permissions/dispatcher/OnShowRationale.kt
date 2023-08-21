package permissions.dispatcher

/**
 * Register some methods which explain why permissions are needed.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.BINARY)
annotation class OnShowRationale(vararg val value: String)