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
import io.dcloud.uniapp.extapi.navigateTo as uni_navigateTo
import io.dcloud.uniapp.extapi.reLaunch as uni_reLaunch
import io.dcloud.uniapp.extapi.request as uni_request
import io.dcloud.uniapp.extapi.showActionSheet as uni_showActionSheet
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesPricePrice : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesPricePrice) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesPricePrice
            val _cache = __ins.renderCache
            val durations = ref(_uA<DurationItem>(DurationItem(time = "1 Hour", price = 1), DurationItem(time = "4 Hours", price = 1), DurationItem(time = "1 Day", price = 1), DurationItem(time = "1 Week", price = 1)))
            val startTime = ref<String>("")
            val endTime = ref<String>("")
            val durationCode = ref<String>("")
            val selectStartTime = fun(){
                val now = Date()
                val currentHour = now.getHours().toString(10).padStart(2, "0")
                val currentMinute = now.getMinutes().toString(10).padStart(2, "0")
                val currentDate = now.getFullYear() + "-" + (now.getMonth() + 1).toString(10).padStart(2, "0") + "-" + now.getDate().toString(10).padStart(2, "0")
                uni_showActionSheet(ShowActionSheetOptions(itemList = _uA(
                    "Start Now",
                    "1 Hour Later",
                    "2 Hours Later",
                    "3 Hours Later"
                ), success = fun(res){
                    if (res.tapIndex === 0) {
                        startTime.value = currentDate + " " + currentHour + ":" + currentMinute
                    } else if (res.tapIndex === 1) {
                        val time1 = Date(now.getTime() + 3600000)
                        val hour1 = time1.getHours().toString(10).padStart(2, "0")
                        val minute1 = time1.getMinutes().toString(10).padStart(2, "0")
                        startTime.value = currentDate + " " + hour1 + ":" + minute1
                    } else if (res.tapIndex === 2) {
                        val time2 = Date(now.getTime() + 7200000)
                        val hour2 = time2.getHours().toString(10).padStart(2, "0")
                        val minute2 = time2.getMinutes().toString(10).padStart(2, "0")
                        startTime.value = currentDate + " " + hour2 + ":" + minute2
                    } else if (res.tapIndex === 3) {
                        val time3 = Date(now.getTime() + 10800000)
                        val hour3 = time3.getHours().toString(10).padStart(2, "0")
                        val minute3 = time3.getMinutes().toString(10).padStart(2, "0")
                        startTime.value = currentDate + " " + hour3 + ":" + minute3
                    }
                }
                ))
            }
            val selectEndTime = fun(){
                if (startTime.value == "") {
                    uni_showToast(ShowToastOptions(title = "Please select start time first", icon = "none"))
                    return
                }
                val now = Date()
                val currentDate = now.getFullYear() + "-" + (now.getMonth() + 1).toString(10).padStart(2, "0") + "-" + now.getDate().toString(10).padStart(2, "0")
                uni_showActionSheet(ShowActionSheetOptions(itemList = _uA(
                    "1 Hour Later",
                    "4 Hours Later",
                    "1 Day Later",
                    "1 Week Later"
                ), success = fun(res){
                    var endDateTime: Date? = null
                    var duration: String = ""
                    if (res.tapIndex === 0) {
                        endDateTime = Date(now.getTime() + 3600000)
                        duration = "1H"
                    } else if (res.tapIndex === 1) {
                        endDateTime = Date(now.getTime() + 14400000)
                        duration = "4H"
                    } else if (res.tapIndex === 2) {
                        endDateTime = Date(now.getTime() + 86400000)
                        duration = "1D"
                    } else if (res.tapIndex === 3) {
                        endDateTime = Date(now.getTime() + 604800000)
                        duration = "1W"
                    }
                    if (endDateTime != null) {
                        val endDate = endDateTime.getFullYear() + "-" + (endDateTime.getMonth() + 1).toString(10).padStart(2, "0") + "-" + endDateTime.getDate().toString(10).padStart(2, "0")
                        val endHour = endDateTime.getHours().toString(10).padStart(2, "0")
                        val endMinute = endDateTime.getMinutes().toString(10).padStart(2, "0")
                        endTime.value = endDate + " " + endHour + ":" + endMinute
                        durationCode.value = duration
                    }
                }
                ))
            }
            val confirm = fun(){
                if (startTime.value == "") {
                    uni_showToast(ShowToastOptions(title = "Please select start time", icon = "none"))
                    return
                }
                if (endTime.value == "") {
                    uni_showToast(ShowToastOptions(title = "Please select end time", icon = "none"))
                    return
                }
                val userInfoStr = uni_getStorageSync("userInfo")
                val token = uni_getStorageSync("token")
                val userIdStorage = uni_getStorageSync("userId")
                var userId: String = ""
                var userName: String = ""
                if (userIdStorage != null && userIdStorage != "") {
                    userId = "" + userIdStorage
                }
                if (userInfoStr != null && userInfoStr != "" && UTSAndroid.`typeof`(userInfoStr) === "string") {
                    try {
                        val userInfo = UTSAndroid.consoleDebugError(JSON.parse<UserInfo__1>(userInfoStr as String), " at pages/price/price.uvue:200")!!!!
                        if (userInfo != null) {
                            val nameValue = userInfo["name"] as String
                            userName = if (nameValue.length > 0) {
                                nameValue
                            } else {
                                "Guest"
                            }
                        }
                    }
                     catch (e: Throwable) {
                        console.error("Failed to parse user info:", e, " at pages/price/price.uvue:206")
                    }
                }
                if (token == null || token == "") {
                    uni_showToast(ShowToastOptions(title = "Please login first", icon = "none"))
                    return
                }
                console.log("Token:", token, " at pages/price/price.uvue:218")
                console.log("User ID:", userId, " at pages/price/price.uvue:219")
                console.log("User Name:", userName, " at pages/price/price.uvue:220")
                val selectedScooterId = uni_getStorageSync("selectedScooterId")
                var scooterId: String = if (selectedScooterId != null) {
                    selectedScooterId as String
                } else {
                    "SC001"
                }
                var numericScooterId: String = ""
                run {
                    var i: Number = 0
                    while(i < scooterId.length){
                        val ch = scooterId.charAt(i)
                        if (ch >= "0" && ch <= "9") {
                            numericScooterId += ch
                        }
                        i++
                    }
                }
                if (numericScooterId.length == 0) {
                    numericScooterId = scooterId
                }
                val startDate = Date(startTime.value)
                val endDate = Date(endTime.value)
                val durationMs = endDate.getTime() - startDate.getTime()
                val durationHours = durationMs / 3600000
                val totalPrice = durationHours * 1
                console.log("Start Time:", startTime.value, " at pages/price/price.uvue:246")
                console.log("End Time:", endTime.value, " at pages/price/price.uvue:247")
                console.log("Duration Hours:", durationHours, " at pages/price/price.uvue:248")
                console.log("Total Price:", totalPrice, " at pages/price/price.uvue:249")
                if (userId == null || userId == "") {
                    uni_showToast(ShowToastOptions(title = "User information missing, please login again before booking", icon = "none"))
                    return
                }
                val bookingData: UTSJSONObject = _uO("__\$originalPosition" to UTSSourceMapPosition("bookingData", "pages/price/price.uvue", 261, 8), "user_id" to userId, "userId" to userId, "scooter_id" to numericScooterId, "start_time" to startTime.value, "end_time" to endTime.value, "duration" to durationCode.value, "total_price" to totalPrice, "status" to "CONFIRMED")
                console.log("Booking info:", bookingData, " at pages/price/price.uvue:272")
                console.log("User ID value:", userId, "Type:", UTSAndroid.`typeof`(userId), " at pages/price/price.uvue:272")
                console.log("Scooter ID value:", scooterId, "Type:", UTSAndroid.`typeof`(scooterId), " at pages/price/price.uvue:273")
                console.log("Total Price value:", totalPrice, "Type:", UTSAndroid.`typeof`(totalPrice), " at pages/price/price.uvue:274")
                uni_request<Any>(RequestOptions(url = buildApiUrl("/api/v1/bookings"), method = "POST", data = bookingData, header = object : UTSJSONObject() {
                    var `Content-Type` = "application/json"
                    var Authorization = "Bearer " + token
                }, success = fun(res){
                    console.log("Booking API Response:", res, " at pages/price/price.uvue:285")
                    console.log("Status Code:", res.statusCode, " at pages/price/price.uvue:286")
                    console.log("Response Data:", res.data, " at pages/price/price.uvue:287")
                    if (res.statusCode >= 200 && res.statusCode < 300) {
                        console.log("Booking successfully saved to database", " at pages/price/price.uvue:290")
                        uni_showToast(ShowToastOptions(title = "Booking successful, proceed to payment", icon = "success"))
                        setTimeout(fun(){
                            uni_navigateTo(NavigateToOptions(url = "/pages/payment/payment"))
                        }, 1000)
                    } else {
                        console.error("Booking failed:", res.statusCode, " at pages/price/price.uvue:303")
                        val errorData = res.data as UTSJSONObject?
                        var errorMessage = "Booking failed. Please try again."
                        if (errorData != null) {
                            console.error("Error details:", JSON.stringify(errorData), " at pages/price/price.uvue:308")
                            if (errorData["message"] != null) {
                                errorMessage = errorData["message"] as String
                            } else if (errorData["error"] != null) {
                                errorMessage = errorData["error"] as String
                            } else if (errorData["msg"] != null) {
                                errorMessage = errorData["msg"] as String
                            }
                        }
                        uni_showToast(ShowToastOptions(title = errorMessage, icon = "none", duration = 3000))
                    }
                }
                , fail = fun(error){
                    console.error("Network error during booking:", error, " at pages/price/price.uvue:330")
                    uni_showToast(ShowToastOptions(title = "Network error. Please check your connection and try again.", icon = "none", duration = 3000))
                }
                ))
            }
            val cancel = fun(){
                startTime.value = ""
                endTime.value = ""
                uni_showToast(ShowToastOptions(title = "Cancelled", icon = "none"))
                uni_reLaunch(ReLaunchOptions(url = "/pages/index/index"))
            }
            return fun(): Any? {
                return _cE("view", _uM("class" to "container"), _uA(
                    _cE("scroll-view", _uM("scroll-y" to "true", "class" to "scroll-area"), _uA(
                        _cE("view", _uM("class" to "title"), _uA(
                            _cE("text", _uM("class" to "title-text"), "Select Booking Duration")
                        )),
                        _cE("view", _uM("class" to "duration-cards"), _uA(
                            _cE(Fragment, null, RenderHelpers.renderList(durations.value, fun(duration, index, __index, _cached): Any {
                                return _cE("view", _uM("key" to index, "class" to "card"), _uA(
                                    _cE("view", _uM("class" to "card-header"), _uA(
                                        _cE("text", _uM("class" to "duration"), _tD(duration.time), 1)
                                    )),
                                    _cE("view", _uM("class" to "card-body"), _uA(
                                        _cE("text", _uM("class" to "price"), "£" + _tD(duration.price), 1)
                                    ))
                                ))
                            }
                            ), 128)
                        )),
                        _cE("view", _uM("class" to "time-section"), _uA(
                            _cE("view", _uM("class" to "section-title"), _uA(
                                _cE("text", _uM("class" to "section-title-text"), "Start Time")
                            )),
                            _cE("view", _uM("class" to "time-input", "onClick" to selectStartTime), _uA(
                                _cE("text", _uM("class" to "time-label"), "Select Start Time"),
                                _cE("text", _uM("class" to "time-value"), _tD(if (startTime.value.length > 0) {
                                    startTime.value
                                } else {
                                    "Not Selected"
                                }
                                ), 1)
                            ))
                        )),
                        _cE("view", _uM("class" to "time-section"), _uA(
                            _cE("view", _uM("class" to "section-title"), _uA(
                                _cE("text", _uM("class" to "section-title-text"), "End Time")
                            )),
                            _cE("view", _uM("class" to "time-input", "onClick" to selectEndTime), _uA(
                                _cE("text", _uM("class" to "time-label"), "Select End Time"),
                                _cE("text", _uM("class" to "time-value"), _tD(if (endTime.value.length > 0) {
                                    endTime.value
                                } else {
                                    "Not Selected"
                                }
                                ), 1)
                            ))
                        )),
                        _cE("view", _uM("class" to "button-group"), _uA(
                            _cE("button", _uM("class" to "confirm-btn", "onClick" to confirm), "Confirm"),
                            _cE("button", _uM("class" to "cancel-btn", "onClick" to cancel), "Cancel")
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
                return _uM("container" to _pS(_uM("width" to "100%", "height" to "100%", "backgroundColor" to "#f5f5f5", "paddingTop" to 0, "paddingRight" to 0, "paddingBottom" to 0, "paddingLeft" to 0, "boxSizing" to "border-box", "display" to "flex", "flexDirection" to "column")), "scroll-area" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "width" to "100%", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxSizing" to "border-box")), "title" to _pS(_uM("textAlign" to "center", "marginBottom" to 30)), "title-text" to _pS(_uM("fontSize" to 24, "fontWeight" to "bold", "color" to "#333333")), "duration-cards" to _pS(_uM("display" to "flex", "flexDirection" to "column", "marginBottom" to 30)), "card" to _uM("" to _uM("backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxShadow" to "0 4px 12px rgba(0, 0, 0, 0.1)", "transitionProperty" to "all", "transitionDuration" to "0.3s", "transitionTimingFunction" to "ease", "marginBottom" to 15), ".selected" to _uM("backgroundColor" to "#e8f5e9", "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#4CAF50", "borderRightColor" to "#4CAF50", "borderBottomColor" to "#4CAF50", "borderLeftColor" to "#4CAF50", "transform" to "translateY(-2px)", "boxShadow" to "0 6px 16px rgba(76, 175, 80, 0.2)")), "card-header" to _pS(_uM("textAlign" to "center", "marginBottom" to 10)), "duration" to _pS(_uM("fontSize" to 18, "fontWeight" to "bold", "color" to "#333333")), "card-body" to _pS(_uM("textAlign" to "center")), "price" to _pS(_uM("fontSize" to 20, "fontWeight" to "bold", "color" to "#4CAF50")), "time-section" to _pS(_uM("backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "marginBottom" to 20, "boxShadow" to "0 4px 12px rgba(0, 0, 0, 0.1)")), "section-title" to _pS(_uM("marginBottom" to 12, "textAlign" to "left")), "section-title-text" to _pS(_uM("fontSize" to 14, "fontWeight" to "bold", "color" to "#666666")), "time-input" to _pS(_uM("backgroundColor" to "#f9f9f9", "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#dddddd", "borderRightColor" to "#dddddd", "borderBottomColor" to "#dddddd", "borderLeftColor" to "#dddddd", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "paddingTop" to 12, "paddingRight" to 15, "paddingBottom" to 12, "paddingLeft" to 15, "display" to "flex", "justifyContent" to "space-between", "alignItems" to "center")), "time-label" to _pS(_uM("fontSize" to 12, "color" to "#999999")), "time-value" to _pS(_uM("fontSize" to 14, "color" to "#333333", "fontWeight" to "bold")), "button-group" to _pS(_uM("display" to "flex", "flexDirection" to "column", "alignItems" to "center", "marginTop" to 20, "marginBottom" to 20)), "confirm-btn" to _pS(_uM("width" to "80%", "height" to 45, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold", "marginBottom" to 15)), "cancel-btn" to _pS(_uM("width" to "80%", "height" to 45, "backgroundColor" to "#f44336", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold")), "@TRANSITION" to _uM("card" to _uM("property" to "all", "duration" to "0.3s", "timingFunction" to "ease")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
