package permissions.dispatcher

/**
 * Register some methods handling the user's choice to permanently deny permissions.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.BINARY)
annotation class OnNeverAskAgain(vararg val value: String)