package com.stardust.view.accessibility

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Rect
import androidx.annotation.Keep
import android.view.accessibility.AccessibilityNodeInfo

import com.stardust.automator.UiObject

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Stardust on 2017/3/10.
 */
@Keep
class NodeInfo(resources: Resources?, node: UiObject, var parent: NodeInfo?) {

    private val children = ArrayList<NodeInfo>()
    val boundsInScreen = Rect()
    val boundsInParent = Rect()

    var id: String? = null
    var idHex: String? = null
    var fullId: String? = null
    var desc: String? = null
    var className: String? = null
    var packageName: String? = null
    var text: String
    var depth: Int = 0
    var drawingOrder: Int = 0
    var accessibilityFocused: Boolean = false
    var checked: Boolean = false
    var clickable: Boolean = false
    var contextClickable: Boolean = false
    var dismissable: Boolean = false
    var editable: Boolean = false
    var enabled: Boolean = false
    var focusable: Boolean = false
    var longClickable: Boolean = false
    var row: Int = 0
    var column: Int = 0
    var rowCount: Int = 0
    var columnCount: Int = 0
    var rowSpan: Int = 0
    var columnSpan: Int = 0
    var selected: Boolean = false
    var scrollable: Boolean = false
    var bounds: String
    var checkable: Boolean = false
    var focused: Boolean = false
    var visibleToUser: Boolean = false
    var indexInParent: Int = 0


    init {
        fullId = node.viewIdResourceName
        id = simplifyId(fullId)
        desc = node.desc()
        className = node.className()
        packageName = node.packageName()
        text = node.text()

        depth = node.depth()
        drawingOrder = node.drawingOrder

        row = node.row()
        column = node.column()
        rowCount = node.rowCount()
        columnCount = node.columnCount()
        rowSpan = node.rowSpan()
        columnSpan = node.columnSpan()

        accessibilityFocused = node.isAccessibilityFocused
        checked = node.isChecked
        checkable = node.isCheckable
        clickable = node.isClickable
        contextClickable = node.isContextClickable
        dismissable = node.isDismissable
        enabled = node.isEnabled
        editable = node.isEditable
        focusable = node.isFocusable
        focused = node.focused()
        longClickable = node.isLongClickable
        selected = node.isSelected
        scrollable = node.isScrollable
        visibleToUser = node.visibleToUser()
        node.getBoundsInScreen(boundsInScreen)
        node.getBoundsInParent(boundsInParent)
        bounds = boundsToString(boundsInScreen)
        indexInParent = node.indexInParent()
        if (resources != null && packageName != null && fullId != null) {
            idHex = "0x" + Integer.toHexString(resources.getIdentifier(fullId, null, null))
        }
    }

    private fun simplifyId(idResourceName: String?): String? {
        if (idResourceName == null)
            return null
        val i = idResourceName.indexOf('/')
        return idResourceName.substring(i + 1)
    }

    fun getChildren(): List<NodeInfo> {
        return children
    }

    override fun toString(): String {
        return className + "{" +
                "childCount=" + children.size +
                ", mBoundsInScreen=" + boundsInScreen +
                ", mBoundsInParent=" + boundsInParent +
                ", id='" + id + '\''.toString() +
                ", desc='" + desc + '\''.toString() +
                ", packageName='" + packageName + '\''.toString() +
                ", text='" + text + '\''.toString() +
                ", depth=" + depth +
                ", drawingOrder=" + drawingOrder +
                ", accessibilityFocused=" + accessibilityFocused +
                ", checked=" + checked +
                ", clickable=" + clickable +
                ", contextClickable=" + contextClickable +
                ", dismissable=" + dismissable +
                ", editable=" + editable +
                ", enabled=" + enabled +
                ", focusable=" + focusable +
                ", longClickable=" + longClickable +
                ", row=" + row +
                ", column=" + column +
                ", rowCount=" + rowCount +
                ", columnCount=" + columnCount +
                ", rowSpan=" + rowSpan +
                ", columnSpan=" + columnSpan +
                ", selected=" + selected +
                ", scrollable=" + scrollable +
                ", bounds='" + bounds + '\''.toString() +
                ", checkable=" + checkable +
                ", focused=" + focused +
                ", visibleToUser=" + visibleToUser +
                ", parent=" + parent?.className +
                '}'.toString()
    }

    companion object {

        fun boundsToString(rect: Rect): String {
            return rect.toString().replace('-', ',').replace(" ", "").substring(4)
        }


        internal fun capture(resourcesCache: HashMap<String, Resources>, context: Context, uiObject: UiObject, parent: NodeInfo?): NodeInfo {
            val pkg = uiObject.packageName()
            var resources: Resources? = null
            if (pkg != null) {
                resources = resourcesCache[pkg]
                if (resources == null) {
                    try {
                        resources = context.packageManager.getResourcesForApplication(pkg)
                        resourcesCache[pkg] = resources
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }

                }
            }
            val nodeInfo = NodeInfo(resources, uiObject, parent)
            val childCount = uiObject.childCount
            for (i in 0 until childCount) {
                val child = uiObject.child(i)
                if (child != null) {
                    nodeInfo.children.add(capture(resourcesCache, context, child, nodeInfo))
                }
            }
            return nodeInfo
        }

        fun capture(context: Context, root: AccessibilityNodeInfo): NodeInfo {
            val r = UiObject.createRoot(root)
            val resourcesCache = HashMap<String, Resources>()
            return capture(resourcesCache, context, r, null)
        }
    }
}
