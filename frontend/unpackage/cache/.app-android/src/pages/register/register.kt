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
import io.dcloud.uniapp.extapi.request as uni_request
import io.dcloud.uniapp.extapi.setStorageSync as uni_setStorageSync
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesRegisterRegister : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesRegisterRegister) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesRegisterRegister
            val _cache = __ins.renderCache
            val formData = ref<FormData>(FormData(name = "", email = "", password = ""))
            val confirmPassword = ref("")
            val isSubmitting = ref(false)
            val serverUrl = buildApiUrl("/api/v1/users")
            val isNameValid = computed(fun(): Boolean {
                return formData.value.name.trim().length > 0
            }
            )
            val isEmailValid = computed(fun(): Boolean {
                if (formData.value.email == null || formData.value.email == "") {
                    return false
                }
                val emailRegex = UTSRegExp("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}\$", "")
                return emailRegex.test(formData.value.email.trim())
            }
            )
            val isPasswordValid = computed(fun(): Boolean {
                return formData.value.password.length >= 6
            }
            )
            val passwordsMatch = computed(fun(): Boolean {
                return formData.value.password == confirmPassword.value
            }
            )
            val isFormValid = computed(fun(): Boolean {
                return (isNameValid.value && isEmailValid.value && isPasswordValid.value && passwordsMatch.value)
            }
            )
            val showToast = fun(message: String): Unit {
                uni_showToast(ShowToastOptions(title = message, icon = "none", duration = 3000))
            }
            val handleRegister = fun(): UTSPromise<Unit> {
                return wrapUTSPromise(suspend w1@{
                        if (!isNameValid.value) {
                            showToast("Please enter your name.")
                            return@w1
                        }
                        if (!isEmailValid.value) {
                            showToast("Please enter a valid email address.")
                            return@w1
                        }
                        if (!isPasswordValid.value) {
                            showToast("Password must be at least 6 characters long.")
                            return@w1
                        }
                        if (!passwordsMatch.value) {
                            showToast("Passwords do not match.")
                            return@w1
                        }
                        if (isSubmitting.value) {
                            return@w1
                        }
                        val payload: UTSJSONObject = object : UTSJSONObject(UTSSourceMapPosition("payload", "pages/register/register.uvue", 162, 8)) {
                            var name = formData.value.name.trim()
                            var email = formData.value.email.trim()
                            var password = formData.value.password
                        }
                        console.log("Attempting to register with payload:", object : UTSJSONObject() {
                            var name = payload["name"]
                            var email = payload["email"]
                            var password = "[REDACTED]"
                        }, " at pages/register/register.uvue:168")
                        try {
                            isSubmitting.value = true
                            uni_request<Any>(RequestOptions(url = serverUrl, method = "POST", data = payload, header = object : UTSJSONObject() {
                                var `Content-Type` = "application/json"
                            }, timeout = 15000, success = fun(response){
                                console.log("Registration API success response:", response, " at pages/register/register.uvue:186")
                                if (response.statusCode >= 200 && response.statusCode < 300) {
                                    val resultObj = response.data as UTSJSONObject
                                    console.log("Registration successful:", resultObj, " at pages/register/register.uvue:191")
                                    var regData: UTSJSONObject? = null
                                    if (resultObj["data"] != null) {
                                        regData = resultObj["data"] as UTSJSONObject
                                    } else {
                                        regData = resultObj
                                    }
                                    if (regData != null && regData["id"] != null) {
                                        val rawRegId = regData["id"]
                                        val registeredId = "" + rawRegId
                                        try {
                                            uni_setStorageSync("userId", registeredId)
                                            console.log("Saved userId from register:", registeredId, " at pages/register/register.uvue:207")
                                        }
                                         catch (e: Throwable) {
                                            console.error("Failed to save userId from register:", e, " at pages/register/register.uvue:209")
                                        }
                                    }
                                    var welcomeName = formData.value.name
                                    if (regData != null && regData["name"] != null) {
                                        welcomeName = regData["name"] as String
                                    }
                                    showToast("Registration successful! Welcome, " + welcomeName + "!")
                                    setTimeout(fun(){
                                        uni_navigateTo(NavigateToOptions(url = "/pages/login/login"))
                                    }, 1500)
                                } else {
                                    var errorMessage = ""
                                    when (response.statusCode) {
                                        400 -> 
                                            errorMessage = "Invalid request data. "
                                        409 -> 
                                            errorMessage = "Account already exists. "
                                        422 -> 
                                            errorMessage = "Validation error. "
                                        500 -> 
                                            errorMessage = "Server error. "
                                        else -> 
                                            errorMessage = "Error " + response.statusCode + ". "
                                    }
                                    val responseData = response.data
                                    if (responseData != null) {
                                        if (UTSAndroid.`typeof`(responseData) === "object") {
                                            val responseObj = responseData as UTSJSONObject
                                            val messageFromServer = responseObj["message"] as String?
                                            val errorFromServer = responseObj["error"] as String?
                                            val detailsFromServer = responseObj["details"] as String?
                                            if (messageFromServer != null) {
                                                errorMessage += messageFromServer
                                            } else if (errorFromServer != null) {
                                                errorMessage += errorFromServer
                                            } else if (detailsFromServer != null) {
                                                errorMessage += detailsFromServer
                                            } else {
                                                try {
                                                    errorMessage += JSON.stringify(responseData)
                                                } catch (e: Throwable) {
                                                    errorMessage += "[Unable to parse response]"
                                                }
                                            }
                                        } else if (UTSAndroid.`typeof`(responseData) == "string") {
                                            errorMessage += responseData
                                        }
                                    } else {
                                        errorMessage += "Server returned empty response."
                                    }
                                    console.error("Registration failed (HTTP Status):", errorMessage, " at pages/register/register.uvue:273")
                                    showToast(errorMessage)
                                }
                            }
                            , fail = fun(error){
                                console.error("Network error during registration:", error, " at pages/register/register.uvue:278")
                                if (error.errMsg != null && error.errMsg.includes("request:fail")) {
                                    showToast("Failed to connect to server. Please check your network connection.")
                                } else if (error.errMsg != null && error.errMsg.includes("timeout")) {
                                    showToast("Request timed out. Please check your server and try again.")
                                } else {
                                    showToast("A network error occurred. Please check your connection and try again.")
                                }
                            }
                            , complete = fun(_){
                                isSubmitting.value = false
                            }
                            ))
                        }
                         catch (error: Throwable) {
                            console.error("Unexpected error during registration attempt:", error, " at pages/register/register.uvue:294")
                            showToast("An unexpected error occurred.")
                            isSubmitting.value = false
                        }
                })
            }
            return fun(): Any? {
                val _component_navigator = resolveComponent("navigator")
                return _cE(Fragment, null, _uA(
                    _cE("view", _uM("class" to "back"), _uA(
                        _cE("image", _uM("src" to "/static/login_back1.jpg", "mode" to "", "class" to "backpic")),
                        _cE("view", _uM("class" to "login_title"), _uA(
                            _cE("text", _uM("class" to "title_text"), "register")
                        )),
                        _cE("view", _uM("class" to "login_container"), _uA(
                            _cE("input", _uM("modelValue" to formData.value.name, "onInput" to fun(`$event`: UniInputEvent){
                                formData.value.name = `$event`.detail.value
                            }
                            , "type" to "text", "placeholder" to "please enter your name", "confirm-type" to "next", "class" to "input"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            )),
                            _cE("view", _uM("class" to "span1")),
                            _cE("input", _uM("modelValue" to formData.value.email, "onInput" to fun(`$event`: UniInputEvent){
                                formData.value.email = `$event`.detail.value
                            }
                            , "type" to "text", "placeholder" to "please enter account (email)", "confirm-type" to "next", "class" to "input"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            )),
                            _cE("view", _uM("class" to "span1")),
                            _cE("input", _uM("modelValue" to formData.value.password, "onInput" to fun(`$event`: UniInputEvent){
                                formData.value.password = `$event`.detail.value
                            }
                            , "type" to "password", "placeholder" to "please enter password", "class" to "input"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            )),
                            _cE("view", _uM("class" to "span2")),
                            _cE("input", _uM("modelValue" to confirmPassword.value, "onInput" to fun(`$event`: UniInputEvent){
                                confirmPassword.value = `$event`.detail.value
                            }
                            , "type" to "password", "placeholder" to "please check password", "class" to "input"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            )),
                            _cE("view", _uM("class" to "span2")),
                            _cE("view", _uM("class" to "validation-info"), _uA(
                                _cE("text", _uM("class" to _nC(_uA(
                                    "validation-message",
                                    _uM("valid" to isNameValid.value, "invalid" to (formData.value.name != "" && !isNameValid.value))
                                ))), _tD(if (formData.value.name != "") {
                                    if (isNameValid.value) {
                                        "✓ Name is valid"
                                    } else {
                                        "✗ Name cannot be empty"
                                    }
                                } else {
                                    "Name required"
                                }
                                ), 3),
                                _cE("text", _uM("class" to _nC(_uA(
                                    "validation-message",
                                    _uM("valid" to isEmailValid.value, "invalid" to (formData.value.email != "" && !isEmailValid.value))
                                ))), _tD(if (formData.value.email != "") {
                                    if (isEmailValid.value) {
                                        "✓ Valid email format"
                                    } else {
                                        "✗ Invalid email format"
                                    }
                                } else {
                                    "Email required"
                                }
                                ), 3),
                                _cE("text", _uM("class" to _nC(_uA(
                                    "validation-message",
                                    _uM("valid" to isPasswordValid.value, "invalid" to (formData.value.password != "" && !isPasswordValid.value))
                                ))), _tD(if (formData.value.password != "") {
                                    if (isPasswordValid.value) {
                                        "✓ Password meets requirements"
                                    } else {
                                        "✗ Password must be at least 6 characters"
                                    }
                                } else {
                                    "Password required"
                                }
                                ), 3),
                                _cE("text", _uM("class" to _nC(_uA(
                                    "validation-message",
                                    _uM("valid" to passwordsMatch.value, "invalid" to (confirmPassword.value != "" && !passwordsMatch.value))
                                ))), _tD(if (confirmPassword.value != "") {
                                    if (passwordsMatch.value) {
                                        "✓ Passwords match"
                                    } else {
                                        "✗ Passwords do not match"
                                    }
                                } else {
                                    "Confirm password"
                                }
                                ), 3)
                            )),
                            _cE("view", _uM("class" to "span2")),
                            _cE("button", _uM("onClick" to handleRegister, "disabled" to (isSubmitting.value || !isFormValid.value), "size" to "default", "style" to _nS(_uM("color" to "white", "background-color" to "rgba(60, 60, 60, 0.7)"))), _tD(if (isSubmitting.value) {
                                "Registering..."
                            } else {
                                "Sign Up"
                            }
                            ), 13, _uA(
                                "disabled"
                            ))
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
                return _uM("back" to _pS(_uM("width" to "100%", "height" to "100%")), "backpic" to _uM(".back " to _uM("width" to "100%", "height" to "100%")), "login_title" to _uM(".back " to _uM("position" to "absolute", "top" to 0, "left" to "50%", "transform" to "translateX(-50%)", "zIndex" to 10, "paddingTop" to 10, "paddingRight" to 10, "paddingBottom" to 10, "paddingLeft" to 10, "backgroundColor" to "rgba(0,0,0,0.3)")), "title_text" to _uM(".back .login_title " to _uM("height" to "100%", "fontSize" to 32, "fontWeight" to "bold", "color" to "#FFFFFF", "textShadow" to "1px 1px 3px rgba(0, 0, 0, 0.5)")), "login_container" to _uM(".back " to _uM("position" to "absolute", "top" to "50%", "left" to "50%", "transform" to "translate(-50%, -50%)", "width" to 300, "paddingTop" to 30, "paddingRight" to 30, "paddingBottom" to 30, "paddingLeft" to 30, "backgroundColor" to "rgba(227,225,225,0.46)", "borderTopLeftRadius" to 15, "borderTopRightRadius" to 15, "borderBottomRightRadius" to 15, "borderBottomLeftRadius" to 15, "boxShadow" to "0 10px 30px rgba(0, 0, 0, 0.3)")), "input" to _uM(".back .login_container " to _uM("width" to "100%", "height" to "10%", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "backgroundColor" to "rgba(60,60,60,0.7)", "color" to "#FFFFFF", "fontSize" to 16)), "span1" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10, "backgroundColor" to "rgba(0,0,0,0)")), "span2" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10, "backgroundColor" to "rgba(0,0,0,0)")), "validation-info" to _uM(".back .login_container " to _uM("marginTop" to 10, "marginRight" to 0, "marginBottom" to 10, "marginLeft" to 0, "paddingTop" to 10, "paddingRight" to 10, "paddingBottom" to 10, "paddingLeft" to 10, "backgroundColor" to "rgba(0,0,0,0.1)", "borderTopLeftRadius" to 5, "borderTopRightRadius" to 5, "borderBottomRightRadius" to 5, "borderBottomLeftRadius" to 5)), "validation-message" to _uM(".back .login_container " to _uM("fontSize" to 12, "marginBottom" to 5), ".back .login_container .valid" to _uM("color" to "#2ecc71", "fontWeight" to "bold"), ".back .login_container .invalid" to _uM("color" to "#e74c3c", "fontWeight" to "bold")), "server-config" to _uM(".back .login_container " to _uM("marginTop" to 20, "paddingTop" to 10, "paddingRight" to 10, "paddingBottom" to 10, "paddingLeft" to 10, "backgroundColor" to "rgba(255,255,255,0.2)", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8)), "config-label" to _uM(".back .login_container .server-config " to _uM("color" to "#FFFFFF", "fontSize" to 14, "marginBottom" to 5)), "config-input" to _uM(".back .login_container .server-config " to _uM("width" to "100%", "paddingTop" to 10, "paddingRight" to 10, "paddingBottom" to 10, "paddingLeft" to 10, "borderTopLeftRadius" to 5, "borderTopRightRadius" to 5, "borderBottomRightRadius" to 5, "borderBottomLeftRadius" to 5, "backgroundColor" to "rgba(255,255,255,0.8)", "fontSize" to 14)), "bottom" to _pS(_uM("width" to "100%", "height" to "8%", "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "position" to "absolute", "bottom" to 0, "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0)), "bottom_left" to _uM(".bottom " to _uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginLeft" to "5%")), "icon2" to _uM(".bottom .bottom_left " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%"), ".bottom .bottom_right " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%")), "left_down" to _uM(".bottom .bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_center" to _uM(".bottom " to _uM("position" to "absolute", "left" to "40%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%")), "bike_icon" to _uM(".bottom .bottom_center " to _uM("position" to "absolute", "left" to "25%", "width" to "50%", "height" to "60%")), "center_down" to _uM(".bottom .bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_right" to _uM(".bottom " to _uM("position" to "absolute", "right" to "0%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginRight" to "5%")), "right_down" to _uM(".bottom .bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "text3" to _pS(_uM("fontSize" to 12)))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
