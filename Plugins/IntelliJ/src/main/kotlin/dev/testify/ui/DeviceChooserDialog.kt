package dev.testify.ui

import com.android.ddmlib.IDevice
//import com.developerphil.adbidea.ObjectGraph
//import com.developerphil.adbidea.preference.ProjectPreferences
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Disposer
import dev.testify.preferences.ProjectPreferences
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidBundle
//import org.joor.Reflect
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * https://android.googlesource.com/platform/tools/adt/idea/+/refs/heads/mirror-goog-studio-master-dev/android/src/com/android/tools/idea/run/DeviceChooserDialog.java
 */
class DeviceChooserDialog(
    facet: AndroidFacet,
    val projectPreferences: ProjectPreferences
) : DialogWrapper(facet.module.project, true) {

    lateinit var myPanel: JPanel
    lateinit var myDeviceChooserWrapper: JPanel
    lateinit var useSameDeviceSCheckBox: JCheckBox

    private val myProject: Project
    private val myDeviceChooser: MyDeviceChooser
//    private val projectPreferences: ProjectPreferences

    val selectedDevices: Array<IDevice>
        get() = myDeviceChooser.selectedDevices

    init {
        title = AndroidBundle.message("choose.device.dialog.title")
        myProject = facet.module.project
// TODO: Not needed       projectPreferences = myProject.getService(ObjectGraph::class.java).projectPreferences
        okAction.isEnabled = false
        myDeviceChooser = MyDeviceChooser(true, okAction, facet, null)
        Disposer.register(myDisposable, myDeviceChooser)
        myDeviceChooser.addListener(object : DeviceChooserListener {
            override fun selectedDevicesChanged() {
                updateOkButton()
            }
        })
        myDeviceChooserWrapper.add(myDeviceChooser.panel)
        myDeviceChooser.init(projectPreferences.getSelectedDeviceSerials())
        init()
        updateOkButton()
    }

    private fun persistSelectedSerialsToPreferences() {
        projectPreferences.saveSelectedDeviceSerials(myDeviceChooser.selectedDevices.map { it.serialNumber }.toList())
    }

    private fun updateOkButton() {
        okAction.isEnabled = selectedDevices.isNotEmpty()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return try {
            myDeviceChooser.preferredFocusComponent
        } catch (e: NoSuchMethodError) { // that means that we are probably on a preview version of android studio or in intellij 13

//            Reflect.on(myDeviceChooser).call("getDeviceTable").get<JComponent>()
            null
        }
    }

    override fun doOKAction() {
        myDeviceChooser.finish()
        persistSelectedSerialsToPreferences()
        super.doOKAction()
    }

    override fun getDimensionServiceKey() = javaClass.canonicalName
    override fun createCenterPanel(): JComponent = myPanel

    fun useSameDevices() = useSameDeviceSCheckBox.isSelected
}
