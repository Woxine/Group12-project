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
import io.dcloud.uniapp.extapi.getNetworkType as uni_getNetworkType
import io.dcloud.uniapp.extapi.getStorageSync as uni_getStorageSync
import io.dcloud.uniapp.extapi.reLaunch as uni_reLaunch
import io.dcloud.uniapp.extapi.request as uni_request
import io.dcloud.uniapp.extapi.setStorageSync as uni_setStorageSync
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesLoginLogin : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesLoginLogin) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesLoginLogin
            val _cache = __ins.renderCache
            val userInfo = reactive<UserInfo>(UserInfo(email = "", password = ""))
            val isSubmitting = ref(false)
            val DEBUG_ENDPOINT = "http://127.0.0.1:7704/ingest/fb6b8af2-8829-46b4-89bb-12721b669ea9"
            val DEBUG_SESSION_ID = "98f075"
            val DEBUG_RUN_ID = "login-timeout-run-1"
            val LOGIN_URL = buildApiUrl("/api/v1/auth/login")
            val sendDebugLog = fun(hypothesisId: String, location: String, message: String, data: UTSJSONObject){
                uni_request<Any>(RequestOptions(url = DEBUG_ENDPOINT, method = "POST", header = object : UTSJSONObject() {
                    var `Content-Type` = "application/json"
                    var `X-Debug-Session-Id` = DEBUG_SESSION_ID
                }, data = _uO("sessionId" to DEBUG_SESSION_ID, "runId" to DEBUG_RUN_ID, "hypothesisId" to hypothesisId, "location" to location, "message" to message, "data" to data, "timestamp" to Date.now()), fail = fun(_){}))
            }
            val onSubmit = fun(){
                sendDebugLog("H1", "pages/login/login.uvue:onSubmit:entry", "Login submit triggered", object : UTSJSONObject() {
                    var emailLength = userInfo.email.length
                    var hasPassword = userInfo.password != null && userInfo.password != ""
                    var isSubmitting = isSubmitting.value
                })
                if (isSubmitting.value) {
                    return
                }
                if (userInfo.email == null || userInfo.email == "" || userInfo.password == null || userInfo.password == "") {
                    uni_showToast(ShowToastOptions(title = "Please fill in both email and password.", icon = "none"))
                    return
                }
                isSubmitting.value = true
                sendDebugLog("H2", "pages/login/login.uvue:onSubmit:beforeRequest", "About to call uni.request", object : UTSJSONObject() {
                    var url = LOGIN_URL
                    var method = "POST"
                })
                uni_getNetworkType(GetNetworkTypeOptions(success = fun(ret){
                    sendDebugLog("H3", "pages/login/login.uvue:onSubmit:getNetworkType:success", "Network type fetched", object : UTSJSONObject() {
                        var networkType = ret.networkType
                    })
                }
                , fail = fun(ret){
                    sendDebugLog("H3", "pages/login/login.uvue:onSubmit:getNetworkType:fail", "Failed to fetch network type", object : UTSJSONObject() {
                        var errMsg = ret.errMsg
                    })
                }
                ))
                uni_request<Any>(RequestOptions(url = LOGIN_URL, method = "POST", data = object : UTSJSONObject() {
                    var email = userInfo.email
                    var password = userInfo.password
                }, header = object : UTSJSONObject() {
                    var `Content-Type` = "application/json"
                }, success = fun(res){
                    sendDebugLog("H4", "pages/login/login.uvue:uni.request:success", "Request reached success callback", object : UTSJSONObject() {
                        var statusCode = res.statusCode
                    })
                    console.log("Login API Response:", res.data, " at pages/login/login.uvue:181")
                    val ret = res.data as UTSJSONObject
                    if (res.statusCode === 200) {
                        var loginData: UTSJSONObject? = null
                        if (ret["data"] != null) {
                            loginData = ret["data"] as UTSJSONObject
                        } else {
                            loginData = ret
                        }
                        console.log("Login Data:", JSON.stringify(loginData), " at pages/login/login.uvue:199")
                        if (loginData != null) {
                            console.log("id field:", loginData["id"], " at pages/login/login.uvue:203")
                            console.log("userId field:", loginData["userId"], " at pages/login/login.uvue:204")
                            console.log("name field:", loginData["name"], " at pages/login/login.uvue:205")
                            console.log("email field:", loginData["email"], " at pages/login/login.uvue:206")
                            console.log("token field:", loginData["token"], " at pages/login/login.uvue:207")
                        }
                        if (loginData != null) {
                            val token = loginData["token"] as String?
                            if (token != null) {
                                try {
                                    uni_setStorageSync("token", token)
                                    console.log("Token saved successfully", " at pages/login/login.uvue:216")
                                }
                                 catch (e: Throwable) {
                                    console.error("Failed to save token:", e, " at pages/login/login.uvue:218")
                                }
                                val existingUserId = uni_getStorageSync("userId")
                                var userIdToSave: String = ""
                                val rawLoginId = if (loginData["id"] != null) {
                                    loginData["id"]
                                } else {
                                    loginData["userId"]
                                }
                                if (rawLoginId != null) {
                                    userIdToSave = "" + rawLoginId
                                } else if (existingUserId != null && existingUserId != "") {
                                    userIdToSave = "" + existingUserId
                                } else {
                                    userIdToSave = userInfo.email
                                }
                                try {
                                    uni_setStorageSync("userId", userIdToSave)
                                    console.log("User ID saved:", userIdToSave, " at pages/login/login.uvue:235")
                                }
                                 catch (e: Throwable) {
                                    console.error("Failed to save user ID:", e, " at pages/login/login.uvue:237")
                                }
                                val userRole = if (loginData["role"] != null) {
                                    loginData["role"] as String
                                } else {
                                    "CUSTOMER"
                                }
                                try {
                                    uni_setStorageSync("role", userRole)
                                    console.log("Role saved:", userRole, " at pages/login/login.uvue:244")
                                }
                                 catch (e: Throwable) {
                                    console.error("Failed to save role:", e, " at pages/login/login.uvue:246")
                                }
                                val userName = if (loginData["name"] != null) {
                                    loginData["name"] as String
                                } else {
                                    userInfo.email
                                }
                                val userEmail = userInfo.email
                                val userInfoData: UTSJSONObject = object : UTSJSONObject(UTSSourceMapPosition("userInfoData", "pages/login/login.uvue", 252, 13)) {
                                    var name = userName
                                    var email = userEmail
                                }
                                try {
                                    val userInfoStr = JSON.stringify(userInfoData)
                                    uni_setStorageSync("userInfo", userInfoStr)
                                    console.log("User info saved:", userInfoStr, " at pages/login/login.uvue:260")
                                }
                                 catch (e: Throwable) {
                                    console.error("Failed to save user info:", e, " at pages/login/login.uvue:262")
                                }
                                uni_showToast(ShowToastOptions(title = "Login successful!", icon = "success"))
                                setTimeout(fun(){
                                    uni_reLaunch(ReLaunchOptions(url = "/pages/index/index"))
                                }
                                , 1500)
                                return
                            }
                        }
                    }
                    val retObj = res.data as UTSJSONObject
                    var msg = "Login failed. Please check your credentials."
                    val backendMsg = retObj["message"]
                    val backendErr = retObj["error"]
                    if (backendMsg != null) {
                        msg = backendMsg as String
                    } else if (backendErr != null) {
                        msg = "Error: " + backendErr
                    }
                    console.error("Login failed. Status:", res.statusCode, "Response:", JSON.stringify(retObj), " at pages/login/login.uvue:295")
                    uni_showToast(ShowToastOptions(title = msg, icon = "none", duration = 3000))
                }
                , fail = fun(err){
                    var errSnapshot = ""
                    try {
                        errSnapshot = JSON.stringify(err)
                    }
                     catch (e: Throwable) {
                        errSnapshot = "" + err
                    }
                    sendDebugLog("H2", "pages/login/login.uvue:uni.request:fail", "Request failed in fail callback", object : UTSJSONObject() {
                        var errorText = "" + err
                        var errorSnapshot = errSnapshot
                    })
                    console.error("Login request failed:", err, " at pages/login/login.uvue:319")
                    uni_showToast(ShowToastOptions(title = "Network error, please try again later.", icon = "none"))
                }
                , complete = fun(_){
                    sendDebugLog("H5", "pages/login/login.uvue:uni.request:complete", "Request complete callback reached", object : UTSJSONObject() {
                        var isSubmittingBeforeReset = isSubmitting.value
                    })
                    isSubmitting.value = false
                }
                ))
            }
            return fun(): Any? {
                val _component_navigator = resolveComponent("navigator")
                return _cE(Fragment, null, _uA(
                    _cE("view", _uM("class" to "back"), _uA(
                        _cE("image", _uM("src" to "/static/login_back1.jpg", "mode" to "", "class" to "backpic")),
                        _cE("view", _uM("class" to "login_title"), _uA(
                            _cE("text", _uM("class" to "title_text"), "log in")
                        )),
                        _cE("view", _uM("class" to "login_container"), _uA(
                            _cE("input", _uM("modelValue" to userInfo.email, "onInput" to fun(`$event`: UniInputEvent){
                                userInfo.email = `$event`.detail.value
                            }
                            , "type" to "text", "placeholder" to "please enter your email", "confirm-type" to "next", "class" to "input"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            )),
                            _cE("view", _uM("class" to "span1")),
                            _cE("input", _uM("modelValue" to userInfo.password, "onInput" to fun(`$event`: UniInputEvent){
                                userInfo.password = `$event`.detail.value
                            }
                            , "type" to "password", "placeholder" to "please enter your password", "class" to "input"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            )),
                            _cE("view", _uM("class" to "span2")),
                            _cE("button", _uM("onClick" to onSubmit, "size" to "default", "disabled" to isSubmitting.value, "style" to _nS(_uM("color" to "white", "background-color" to "rgba(60, 60, 60, 0.7)"))), _tD(if (isSubmitting.value) {
                                "Logging in..."
                            } else {
                                "log in"
                            }
                            ), 13, _uA(
                                "disabled"
                            )),
                            _cE("view", _uM("class" to "span3")),
                            _cV(_component_navigator, _uM("url" to "/pages/register/register", "open-type" to "reLaunch"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                return _uA(
                                    _cE("button", _uM("size" to "default", "style" to _nS(_uM("color" to "white", "background-color" to "rgba(60, 60, 60, 0.7)"))), "sign up", 4)
                                )
                            }
                            ), "_" to 1))
                        ))
                    )),
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
                ), 64)
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
                return _uM("back" to _pS(_uM("width" to "100%", "height" to "100%")), "backpic" to _uM(".back " to _uM("width" to "100%", "height" to "100%")), "login_title" to _uM(".back " to _uM("position" to "absolute", "top" to 80, "left" to "50%", "transform" to "translateX(-50%)", "zIndex" to 10, "paddingTop" to 10, "paddingRight" to 10, "paddingBottom" to 10, "paddingLeft" to 10, "backgroundColor" to "rgba(0,0,0,0.3)")), "title_text" to _uM(".back .login_title " to _uM("height" to "100%", "fontSize" to 32, "fontWeight" to "bold", "color" to "#FFFFFF", "textShadow" to "1px 1px 3px rgba(0, 0, 0, 0.5)")), "login_container" to _uM(".back " to _uM("position" to "absolute", "top" to "50%", "left" to "50%", "transform" to "translate(-50%, -50%)", "width" to 300, "paddingTop" to 30, "paddingRight" to 30, "paddingBottom" to 30, "paddingLeft" to 30, "backgroundColor" to "rgba(227,225,225,0.46)", "borderTopLeftRadius" to 15, "borderTopRightRadius" to 15, "borderBottomRightRadius" to 15, "borderBottomLeftRadius" to 15, "boxShadow" to "0 10px 30px rgba(0, 0, 0, 0.3)")), "input" to _uM(".back .login_container " to _uM("width" to "100%", "height" to "10%", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "backgroundColor" to "rgba(60,60,60,0.7)", "color" to "#FFFFFF", "fontSize" to 16, "boxSizing" to "border-box")), "span1" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10)), "span2" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10)), "span3" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10)), "bottom" to _pS(_uM("width" to "100%", "height" to 80, "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "display" to "flex", "justifyContent" to "space-around", "alignItems" to "center", "flexShrink" to 0, "position" to "fixed", "bottom" to 0, "left" to 0, "zIndex" to 10)), "bottom_left" to _uM(".bottom " to _uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "33.33%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "icon2" to _uM(".bottom .bottom_left " to _uM("width" to 30, "height" to 30), ".bottom .bottom_right " to _uM("width" to 30, "height" to 30)), "left_down" to _uM(".bottom .bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "bottom_center" to _uM(".bottom " to _uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "33.33%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "bike_icon" to _uM(".bottom .bottom_center " to _uM("width" to 30, "height" to 30)), "center_down" to _uM(".bottom .bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "bottom_right" to _uM(".bottom " to _uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "33.33%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "right_down" to _uM(".bottom .bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
