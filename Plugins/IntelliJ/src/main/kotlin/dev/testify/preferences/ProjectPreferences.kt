package dev.testify.preferences

private const val SELECTED_SERIALS_PROPERTY = "dev.testify.selecteddevices"

class ProjectPreferences(private val preferenceAccessor: PreferenceAccessor) {

    fun saveSelectedDeviceSerials(serials: List<String>) {
        preferenceAccessor.saveString(SELECTED_SERIALS_PROPERTY, serials.joinToString(separator = " "))
    }

    fun getSelectedDeviceSerials(): List<String> {
        return with(preferenceAccessor.getString(SELECTED_SERIALS_PROPERTY, "")) {
            if (isEmpty()) {
                emptyList()
            } else {
                split(" ")
            }
        }
    }

}
