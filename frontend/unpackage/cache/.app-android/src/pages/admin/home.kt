@file:Suppress("UNCHECKED_CAST", "USELESS_CAST", "INAPPLICABLE_JVM_NAME", "UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING", "UNNECESSARY_NOT_NULL_ASSERTION")
package uni.UNIE6393FD
import io.dcloud.uniapp.*
import io.dcloud.uniapp.extapi.*
import io.dcloud.uniapp.framework.*
import io.dcloud.uniapp.runtime.*
import io.dcloud.uniapp.vue.*
import io.dcloud.uniapp.vue.shared.*
import io.dcloud.unicloud.*
import io.dcloud.uts.*
import io.dcloud.uts.Map
import io.dcloud.uts.Set
import io.dcloud.uts.UTSAndroid
import kotlin.properties.Delegates
import io.dcloud.uniapp.extapi.navigateTo as uni_navigateTo
import io.dcloud.uniapp.extapi.reLaunch as uni_reLaunch
import io.dcloud.uniapp.extapi.removeStorageSync as uni_removeStorageSync
import io.dcloud.uniapp.extapi.showModal as uni_showModal
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesAdminHome : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesAdminHome) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesAdminHome
            val _cache = __ins.renderCache
            val goModifyScooter = fun(){
                uni_navigateTo(NavigateToOptions(url = "/pages/admin/modify"))
            }
            val goRevenue = fun(){
                uni_navigateTo(NavigateToOptions(url = "/pages/admin/revenue"))
            }
            val logout = fun(){
                uni_showModal(ShowModalOptions(title = "Logout", content = "Are you sure you want to logout?", success = fun(res){
                    if (res.confirm === true) {
                        uni_removeStorageSync("userInfo")
                        uni_removeStorageSync("token")
                        uni_removeStorageSync("userId")
                        uni_removeStorageSync("role")
                        uni_removeStorageSync("selectedScooterId")
                        uni_showToast(ShowToastOptions(title = "Logged out successfully", icon = "success"))
                        setTimeout(fun(){
                            uni_reLaunch(ReLaunchOptions(url = "/pages/login/login"))
                        }
                        , 800)
                    }
                }
                ))
            }
            return fun(): Any? {
                return _cE("view", _uM("class" to "container"), _uA(
                    _cE("view", _uM("class" to "title-section"), _uA(
                        _cE("text", _uM("class" to "title-text"), "Admin Home"),
                        _cE("text", _uM("class" to "hint-text"), "Manage scooters and revenue")
                    )),
                    _cE("view", _uM("class" to "menu-section"), _uA(
                        _cE("button", _uM("class" to "menu-btn primary", "onClick" to goModifyScooter), " Modify Scooter Information "),
                        _cE("button", _uM("class" to "menu-btn", "onClick" to goRevenue), " View Revenue "),
                        _cE("button", _uM("class" to "menu-btn danger", "onClick" to logout), " Logout ")
                    ))
                ))
            }
        }
        val styles: Map<String, Map<String, Map<String, Any>>> by lazy {
            _nCS(_uA(
                styles0
            ), _uA(
                GenApp.styles
            ))
        }
        val styles0: Map<String, Map<String, Map<String, Any>>>
            get() {
                return _uM("container" to _pS(_uM("display" to "flex", "flexDirection" to "column", "paddingTop" to 24, "paddingRight" to 24, "paddingBottom" to 24, "paddingLeft" to 24, "backgroundColor" to "#f5f5f5")), "title-section" to _pS(_uM("display" to "flex", "flexDirection" to "column", "marginBottom" to 24)), "title-text" to _pS(_uM("fontSize" to 22, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "hint-text" to _pS(_uM("fontSize" to 14, "color" to "#666666")), "menu-section" to _pS(_uM("display" to "flex", "flexDirection" to "column")), "menu-btn" to _uM("" to _uM("paddingTop" to 14, "paddingRight" to 14, "paddingBottom" to 14, "paddingLeft" to 14, "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#dddddd", "borderRightColor" to "#dddddd", "borderBottomColor" to "#dddddd", "borderLeftColor" to "#dddddd", "fontSize" to 16, "fontWeight" to "bold", "backgroundColor" to "#ffffff", "color" to "#333333", "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)", "marginBottom" to 24), ".primary" to _uM("backgroundColor" to "#4CAF50", "borderTopColor" to "#4CAF50", "borderRightColor" to "#4CAF50", "borderBottomColor" to "#4CAF50", "borderLeftColor" to "#4CAF50", "color" to "#ffffff"), ".danger" to _uM("backgroundColor" to "#f44336", "borderTopColor" to "#f44336", "borderRightColor" to "#f44336", "borderBottomColor" to "#f44336", "borderLeftColor" to "#f44336", "color" to "#ffffff")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
