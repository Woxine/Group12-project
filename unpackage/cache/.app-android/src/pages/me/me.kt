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
import io.dcloud.uniapp.extapi.getStorageSync as uni_getStorageSync
import io.dcloud.uniapp.extapi.reLaunch as uni_reLaunch
import io.dcloud.uniapp.extapi.removeStorageSync as uni_removeStorageSync
import io.dcloud.uniapp.extapi.showModal as uni_showModal
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesMeMe : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesMeMe) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesMeMe
            val _cache = __ins.renderCache
            val userName = ref<String>("")
            val userEmail = ref<String>("")
            val orders = ref(_uA<Order>())
            val isLoading = ref(false)
            val getUserInfo = fun(){
                val userInfoStr = uni_getStorageSync("userInfo")
                console.log("Retrieved userInfoStr from storage:", userInfoStr, " at pages/me/me.uvue:102")
                if (userInfoStr != null && userInfoStr != "" && UTSAndroid.`typeof`(userInfoStr) === "string") {
                    try {
                        val userInfo = UTSAndroid.consoleDebugError(JSON.parse<UserInfo__2>(userInfoStr as String), " at pages/me/me.uvue:106")!!!!
                        console.log("Parsed userInfo:", userInfo, " at pages/me/me.uvue:107")
                        if (userInfo != null) {
                            if (userInfo["name"] != null) {
                                userName.value = userInfo["name"] as String
                                console.log("Set userName to:", userName.value, " at pages/me/me.uvue:112")
                            }
                            if (userInfo["email"] != null) {
                                userEmail.value = userInfo["email"] as String
                                console.log("Set userEmail to:", userEmail.value, " at pages/me/me.uvue:116")
                            }
                        }
                    } catch (e: Throwable) {
                        console.error("Failed to parse user info:", e, " at pages/me/me.uvue:120")
                    }
                } else {
                    console.log("No user info found in storage or invalid format", " at pages/me/me.uvue:123")
                }
            }
            val loadMockOrders = fun(){
                val mockOrdersData = _uA(
                    Order(id = "1", bikeId = "SC001", duration = "1 hour", price = 1, date = "2024-02-10", status = "Completed"),
                    Order(id = "2", bikeId = "SC002", duration = "4 hours", price = 1, date = "2024-02-08", status = "Completed"),
                    Order(id = "3", bikeId = "SC003", duration = "1 day", price = 1, date = "2024-02-05", status = "Pending")
                ) as UTSArray<Order>
                orders.value = mockOrdersData
            }
            val handleLogout = fun(){
                uni_showModal(ShowModalOptions(title = "Logout", content = "Are you sure you want to logout?", success = fun(res){
                    if (res.confirm) {
                        uni_removeStorageSync("userInfo")
                        uni_removeStorageSync("token")
                        uni_removeStorageSync("userId")
                        uni_removeStorageSync("selectedScooterId")
                        userName.value = ""
                        userEmail.value = ""
                        orders.value = _uA()
                        uni_showToast(ShowToastOptions(title = "Logged out successfully", icon = "success"))
                        uni_reLaunch(ReLaunchOptions(url = "/pages/me/me"))
                    }
                }
                ))
            }
            onMounted(fun(){
                getUserInfo()
                loadMockOrders()
            }
            )
            return fun(): Any? {
                val _component_navigator = resolveComponent("navigator")
                return _cE("view", _uM("class" to "container"), _uA(
                    if (userName.value.length == 0) {
                        _cE("view", _uM("key" to 0, "class" to "login-prompt-section"), _uA(
                            _cE("view", _uM("class" to "not-logged-wrapper"), _uA(
                                _cE("image", _uM("src" to "/static/me.png", "mode" to "aspectFit", "class" to "large-icon")),
                                _cE("text", _uM("class" to "welcome-text"), "Welcome to Bike Sharing"),
                                _cE("text", _uM("class" to "login-hint-text"), "Please log in to view your profile and bookings"),
                                _cV(_component_navigator, _uM("url" to "/pages/login/login", "open-type" to "navigate"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                    return _uA(
                                        _cE("button", _uM("class" to "login-btn"), "Go to Login")
                                    )
                                }), "_" to 1)),
                                _cE("view", _uM("class" to "divider-text"), _uA(
                                    _cE("text", null, "Don't have an account?")
                                )),
                                _cV(_component_navigator, _uM("url" to "/pages/register/register", "open-type" to "navigate"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                    return _uA(
                                        _cE("button", _uM("class" to "register-btn"), "Create Account")
                                    )
                                }), "_" to 1))
                            ))
                        ))
                    } else {
                        _cE("scroll-view", _uM("key" to 1, "class" to "logged-in-container", "scroll-y" to "true"), _uA(
                            _cE("view", _uM("class" to "profile-section"), _uA(
                                _cE("view", _uM("class" to "profile-header"), _uA(
                                    _cE("image", _uM("src" to "/static/me.png", "mode" to "aspectFit", "class" to "avatar")),
                                    _cE("view", _uM("class" to "user-info"), _uA(
                                        _cE("text", _uM("class" to "user-name"), _tD(userName.value), 1),
                                        _cE("text", _uM("class" to "user-email"), _tD(userEmail.value), 1)
                                    ))
                                ))
                            )),
                            _cE("view", _uM("class" to "menu-list"), _uA(
                                _cV(_component_navigator, _uM("url" to "/pages/orders/orders", "open-type" to "navigate", "class" to "menu-item"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                    return _uA(
                                        _cE("text", _uM("class" to "menu-text"), "My Orders"),
                                        _cE("text", _uM("class" to "menu-arrow"), "›")
                                    )
                                }
                                ), "_" to 1)),
                                _cV(_component_navigator, _uM("url" to "/pages/feedback/feedback", "open-type" to "navigate", "class" to "menu-item"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                    return _uA(
                                        _cE("text", _uM("class" to "menu-text"), "Feedback"),
                                        _cE("text", _uM("class" to "menu-arrow"), "›")
                                    )
                                }
                                ), "_" to 1))
                            )),
                            _cE("view", _uM("class" to "action-section"), _uA(
                                _cE("button", _uM("class" to "logout-btn", "onClick" to handleLogout), "Logout")
                            ))
                        ))
                    }
                    ,
                    _cE("view", _uM("class" to "bottom"), _uA(
                        _cV(_component_navigator, _uM("url" to "/pages/index/index", "open-type" to "reLaunch", "class" to "bottom_left"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("image", _uM("src" to "/static/home.png", "mode" to "aspectFit", "class" to "icon2")),
                                _cE("view", _uM("class" to "left_down"), _uA(
                                    _cE("text", _uM("class" to "text3"), "Home")
                                ))
                            )
                        }
                        ), "_" to 1)),
                        _cV(_component_navigator, _uM("url" to "/pages/bikelist/bikelist", "open-type" to "reLaunch", "class" to "bottom_center"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("image", _uM("src" to "/static/bike.png", "mode" to "aspectFit", "class" to "bike_icon")),
                                _cE("view", _uM("class" to "center_down"), _uA(
                                    _cE("text", _uM("class" to "text3"), "Bike")
                                ))
                            )
                        }
                        ), "_" to 1)),
                        _cE("view", _uM("class" to "bottom_right"), _uA(
                            _cE("image", _uM("src" to "/static/me.png", "mode" to "aspectFit", "class" to "icon2")),
                            _cE("view", _uM("class" to "right_down"), _uA(
                                _cE("text", _uM("class" to "text3"), "Me")
                            ))
                        ))
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
                return _uM("container" to _pS(_uM("width" to "100%", "height" to "100%", "backgroundColor" to "#f5f5f5", "boxSizing" to "border-box", "display" to "flex", "flexDirection" to "column")), "login-prompt-section" to _pS(_uM("width" to "100%", "flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "display" to "flex", "justifyContent" to "center", "alignItems" to "center")), "not-logged-wrapper" to _pS(_uM("display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 40, "paddingRight" to 20, "paddingBottom" to 40, "paddingLeft" to 20, "backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "marginTop" to 20, "marginRight" to 20, "marginBottom" to 20, "marginLeft" to 20, "boxShadow" to "0 4px 12px rgba(0, 0, 0, 0.1)")), "large-icon" to _pS(_uM("width" to 80, "height" to 80, "marginBottom" to 20, "opacity" to 0.6)), "welcome-text" to _pS(_uM("fontSize" to 24, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 10, "textAlign" to "center")), "login-hint-text" to _pS(_uM("fontSize" to 14, "color" to "#999999", "textAlign" to "center", "marginBottom" to 30)), "login-btn" to _pS(_uM("width" to "100%", "height" to 45, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold", "marginBottom" to 15)), "divider-text" to _pS(_uM("fontSize" to 12, "color" to "#999999", "marginBottom" to 15)), "register-btn" to _pS(_uM("width" to "100%", "height" to 45, "backgroundColor" to "rgba(76,175,80,0.2)", "color" to "#4CAF50", "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#4CAF50", "borderRightColor" to "#4CAF50", "borderBottomColor" to "#4CAF50", "borderLeftColor" to "#4CAF50", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold")), "logged-in-container" to _pS(_uM("width" to "100%", "flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "paddingBottom" to 90)), "profile-section" to _pS(_uM("width" to "100%", "backgroundColor" to "#FFFFFF", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxSizing" to "border-box", "borderBottomWidth" to 1, "borderBottomStyle" to "solid", "borderBottomColor" to "#e0e0e0")), "menu-list" to _pS(_uM("width" to "100%", "marginTop" to 12, "backgroundColor" to "#FFFFFF", "boxSizing" to "border-box")), "menu-item" to _pS(_uM("width" to "100%", "paddingTop" to 14, "paddingRight" to 20, "paddingBottom" to 14, "paddingLeft" to 20, "boxSizing" to "border-box", "display" to "flex", "flexDirection" to "row", "alignItems" to "center", "justifyContent" to "space-between", "borderTopWidth" to 1, "borderTopStyle" to "solid", "borderTopColor" to "#f0f0f0", "borderTopWidth:first-child" to 0)), "menu-text" to _pS(_uM("fontSize" to 14, "color" to "#333333")), "menu-arrow" to _pS(_uM("fontSize" to 18, "color" to "#cccccc")), "profile-header" to _pS(_uM("display" to "flex", "alignItems" to "center")), "avatar" to _pS(_uM("width" to 60, "height" to 60, "borderTopLeftRadius" to 30, "borderTopRightRadius" to 30, "borderBottomRightRadius" to 30, "borderBottomLeftRadius" to 30, "backgroundColor" to "#e0e0e0", "marginRight" to 15)), "user-info" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "display" to "flex", "flexDirection" to "column")), "user-name" to _pS(_uM("fontSize" to 16, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 5)), "user-email" to _pS(_uM("fontSize" to 12, "color" to "#999999")), "orders-section" to _pS(_uM("width" to "100%", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxSizing" to "border-box")), "section-title" to _pS(_uM("fontSize" to 16, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 15, "paddingBottom" to 10, "borderBottomWidth" to 2, "borderBottomStyle" to "solid", "borderBottomColor" to "#4CAF50")), "orders-list" to _pS(_uM("display" to "flex", "flexDirection" to "column")), "order-card" to _pS(_uM("backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "paddingTop" to 15, "paddingRight" to 15, "paddingBottom" to 15, "paddingLeft" to 15, "marginBottom" to 12, "boxShadow" to "0 2px 4px rgba(0, 0, 0, 0.1)")), "order-header" to _pS(_uM("display" to "flex", "justifyContent" to "space-between", "alignItems" to "center", "marginBottom" to 10)), "order-id" to _pS(_uM("fontSize" to 14, "fontWeight" to "bold", "color" to "#333333")), "order-status" to _uM("" to _uM("fontSize" to 12, "paddingTop" to 4, "paddingRight" to 8, "paddingBottom" to 4, "paddingLeft" to 8, "borderTopLeftRadius" to 4, "borderTopRightRadius" to 4, "borderBottomRightRadius" to 4, "borderBottomLeftRadius" to 4, "fontWeight" to "bold"), ".Completed" to _uM("backgroundColor" to "#4CAF50", "color" to "#FFFFFF"), ".Pending" to _uM("backgroundColor" to "#FFC107", "color" to "#FFFFFF"), ".Cancelled" to _uM("backgroundColor" to "#f44336", "color" to "#FFFFFF")), "order-details" to _pS(_uM("display" to "flex", "flexDirection" to "column")), "detail-row" to _pS(_uM("display" to "flex", "justifyContent" to "space-between", "paddingTop" to 5, "paddingRight" to 0, "paddingBottom" to 5, "paddingLeft" to 0, "fontSize" to 12)), "label" to _pS(_uM("color" to "#999999", "fontWeight" to "bold")), "value" to _pS(_uM("color" to "#333333", "fontWeight" to "bold")), "empty-state" to _pS(_uM("display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 40, "paddingRight" to 20, "paddingBottom" to 40, "paddingLeft" to 20, "backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "boxShadow" to "0 2px 4px rgba(0, 0, 0, 0.1)")), "empty-text" to _pS(_uM("fontSize" to 16, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "empty-hint" to _pS(_uM("fontSize" to 12, "color" to "#999999", "marginBottom" to 20, "textAlign" to "center")), "explore-btn" to _pS(_uM("width" to "100%", "paddingTop" to 10, "paddingRight" to 20, "paddingBottom" to 10, "paddingLeft" to 20, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "fontWeight" to "bold")), "action-section" to _pS(_uM("width" to "90%", "marginTop" to 30, "marginRight" to "auto", "marginBottom" to 40, "marginLeft" to "auto")), "logout-btn" to _pS(_uM("width" to "100%", "paddingTop" to 12, "paddingRight" to 12, "paddingBottom" to 12, "paddingLeft" to 12, "backgroundColor" to "rgba(60,60,60,0.7)", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "fontWeight" to "bold")), "bottom" to _pS(_uM("width" to "100%", "height" to 80, "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "display" to "flex", "justifyContent" to "space-around", "alignItems" to "center", "flexShrink" to 0, "position" to "fixed", "bottom" to 0, "left" to 0, "zIndex" to 10)), "bottom_left" to _pS(_uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "33.33%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "icon2" to _uM(".bottom_left " to _uM("width" to 30, "height" to 30), ".bottom_right " to _uM("width" to 30, "height" to 30)), "left_down" to _uM(".bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "bottom_center" to _pS(_uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "33.33%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "bike_icon" to _uM(".bottom_center " to _uM("width" to 30, "height" to 30)), "center_down" to _uM(".bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "bottom_right" to _pS(_uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "33.33%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "right_down" to _uM(".bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "text3" to _pS(_uM("fontSize" to 12, "marginTop" to 4)))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
