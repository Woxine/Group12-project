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
import io.dcloud.uniapp.extapi.getSystemInfoSync as uni_getSystemInfoSync
import io.dcloud.uniapp.framework.onShow
import io.dcloud.uniapp.extapi.request as uni_request
import io.dcloud.uniapp.extapi.showModal as uni_showModal
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesOrdersOrders : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesOrdersOrders) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesOrdersOrders
            val _cache = __ins.renderCache
            val isLoggedIn = ref(false)
            val isLoading = ref(false)
            val loadError = ref("")
            val bookings = ref(_uA<Booking>())
            val scrollWrapHeight = ref(600)
            val scrollViewHeight = ref(560)
            val isRefreshing = ref(false)
            val formatStatus = fun(status: String?): String {
                if (status == null || status.length == 0) {
                    return "Unknown"
                }
                val s = status.toUpperCase()
                if (s == "CONFIRMED") {
                    return "Confirmed"
                }
                if (s == "PENDING") {
                    return "Pending"
                }
                if (s == "CANCELLED") {
                    return "Cancelled"
                }
                if (s == "COMPLETED") {
                    return "Completed"
                }
                return status
            }
            val formatBikeId = fun(scooterId: String?): String {
                if (scooterId == null || scooterId.length == 0) {
                    return "-"
                }
                if (scooterId.startsWith("SC")) {
                    return scooterId
                }
                return "SC" + scooterId.padStart(3, "0")
            }
            val parseApiBooking = fun(item: UTSJSONObject, index: Number): Booking {
                val rawId = item["id"]
                val id = if (rawId != null) {
                    ("" + rawId)
                } else {
                    null
                }
                val rawScooterId = if (item["scooter_id"] != null) {
                    item["scooter_id"]
                } else {
                    item["scooterId"]
                }
                val scooterId = if (rawScooterId != null) {
                    ("" + rawScooterId)
                } else {
                    ""
                }
                val rawDuration = if (item["duration"] != null) {
                    item["duration"]
                } else {
                    "1H"
                }
                val duration = "" + rawDuration
                val rawStart = if (item["start_time"] != null) {
                    item["start_time"]
                } else {
                    item["startTime"]
                }
                val startTime = if (rawStart != null) {
                    ("" + rawStart)
                } else {
                    "-"
                }
                val rawEnd = if (item["end_time"] != null) {
                    item["end_time"]
                } else {
                    item["endTime"]
                }
                val endTime = if (rawEnd != null) {
                    ("" + rawEnd)
                } else {
                    "-"
                }
                val rawPrice = if (item["total_price"] != null) {
                    item["total_price"]
                } else {
                    item["price"]
                }
                val price = if (rawPrice != null) {
                    (rawPrice as Number)
                } else {
                    0
                }
                val rawStatus = item["status"]
                val statusVal = if (rawStatus != null) {
                    ("" + rawStatus)
                } else {
                    "Unknown"
                }
                return Booking(id = id, scooterId = scooterId, duration = duration, startTime = startTime, endTime = endTime, price = price, status = statusVal)
            }
            val fetchBookings = fun(fromPullRefresh: Boolean){
                val userIdStorage = uni_getStorageSync("userId")
                val tokenStorage = uni_getStorageSync("token")
                val userId = if (userIdStorage != null && userIdStorage != "") {
                    ("" + userIdStorage)
                } else {
                    null
                }
                val token = if (tokenStorage != null && tokenStorage != "") {
                    ("" + tokenStorage)
                } else {
                    null
                }
                if (userId == null || userId.length == 0) {
                    isLoggedIn.value = false
                    return
                }
                if (token == null || token.length == 0) {
                    loadError.value = "Please log in again"
                    isLoggedIn.value = true
                    isLoading.value = false
                    isRefreshing.value = false
                    return
                }
                isLoggedIn.value = true
                if (fromPullRefresh === false) {
                    isLoading.value = true
                }
                loadError.value = ""
                uni_request<Any>(RequestOptions(url = BASE_URL + "/api/v1/users/" + userId + "/bookings", method = "GET", header = object : UTSJSONObject() {
                    var `Content-Type` = "application/json"
                    var Authorization = "Bearer " + token
                }, success = fun(res){
                    if (res.statusCode >= 200 && res.statusCode < 300) {
                        val responseData = res.data
                        if (responseData != null && UTSAndroid.`typeof`(responseData) === "object") {
                            val obj = responseData as UTSJSONObject
                            var arr: UTSArray<Any> = _uA()
                            if (obj["data"] != null && UTSArray.isArray(obj["data"])) {
                                arr = obj["data"] as UTSArray<Any>
                            } else if (UTSArray.isArray(responseData)) {
                                arr = responseData as UTSArray<Any>
                            }
                            val list: UTSArray<Booking> = _uA()
                            run {
                                var i: Number = 0
                                while(i < arr.length){
                                    val item = arr[i] as UTSJSONObject
                                    list.push(parseApiBooking(item, i))
                                    i++
                                }
                            }
                            bookings.value = list
                        } else {
                            bookings.value = _uA()
                        }
                    } else {
                        loadError.value = "Failed to load bookings (HTTP " + res.statusCode + ")"
                    }
                }
                , fail = fun(error){
                    loadError.value = "Network error. Please check your connection."
                    console.error("Fetch bookings failed:", error, " at pages/orders/orders.uvue:205")
                }
                , complete = fun(_){
                    isLoading.value = false
                    isRefreshing.value = false
                }
                ))
            }
            val onPullRefresh = fun(){
                isRefreshing.value = true
                fetchBookings(true)
            }
            val canCancel = fun(status: String?): Boolean {
                if (status == null || status.length === 0) {
                    return true
                }
                val s = status.toUpperCase()
                if (s === "CANCELLED" || s === "COMPLETED") {
                    return false
                }
                return true
            }
            val cancelOrder = fun(booking: Booking){
                val bid = booking.id
                if (bid == null || bid.length === 0) {
                    uni_showToast(ShowToastOptions(title = "Invalid booking", icon = "none"))
                    return
                }
                val tokenRaw = uni_getStorageSync("token")
                val token = if ((tokenRaw != null && tokenRaw !== "")) {
                    ("" + tokenRaw).trim()
                } else {
                    ""
                }
                if (token === "") {
                    uni_showToast(ShowToastOptions(title = "Please login again", icon = "none"))
                    return
                }
                uni_showModal(ShowModalOptions(title = "Cancel Order", content = "Are you sure you want to cancel this booking?", success = fun(res){
                    val confirm = res.confirm === true
                    if (confirm) {
                        uni_request<Any>(RequestOptions(url = BASE_URL + "/api/v1/bookings/" + bid, method = "DELETE", header = object : UTSJSONObject() {
                            var `Content-Type` = "application/json"
                            var Authorization = "Bearer " + token
                        }, success = fun(resp){
                            if (resp.statusCode >= 200 && resp.statusCode < 300) {
                                uni_showToast(ShowToastOptions(title = "Order cancelled", icon = "success"))
                                fetchBookings(false)
                            } else {
                                var msg = "Failed to cancel"
                                val data = resp.data as UTSJSONObject?
                                if (data != null && data["message"] != null) {
                                    msg = "" + data["message"]
                                }
                                uni_showToast(ShowToastOptions(title = msg, icon = "none", duration = 3000))
                            }
                        }
                        , fail = fun(err){
                            uni_showToast(ShowToastOptions(title = "Network error", icon = "none"))
                            console.error("Cancel booking failed:", err, " at pages/orders/orders.uvue:266")
                        }
                        ))
                    }
                }
                ))
            }
            val setScrollHeight = fun(){
                try {
                    val sys = uni_getSystemInfoSync()
                    val h = if (sys.windowHeight != null) {
                        sys.windowHeight
                    } else {
                        600
                    }
                    scrollWrapHeight.value = h
                    scrollViewHeight.value = h - 40
                }
                 catch (e: Throwable) {
                    scrollWrapHeight.value = 600
                    scrollViewHeight.value = 560
                }
            }
            onMounted(fun(){
                setScrollHeight()
                val userId = uni_getStorageSync("userId")
                val hasUser = userId != null && userId != "" && UTSAndroid.`typeof`(userId) === "string"
                if (hasUser) {
                    fetchBookings(false)
                } else {
                    isLoggedIn.value = false
                }
            }
            )
            onShow(fun(){
                setScrollHeight()
                val userId = uni_getStorageSync("userId")
                val hasUser = userId != null && userId != "" && UTSAndroid.`typeof`(userId) === "string"
                if (hasUser) {
                    fetchBookings(false)
                }
            }
            )
            return fun(): Any? {
                val _component_navigator = resolveComponent("navigator")
                return _cE("view", _uM("class" to "container"), _uA(
                    if (isTrue(!isLoggedIn.value)) {
                        _cE("view", _uM("key" to 0, "class" to "login-prompt"), _uA(
                            _cE("image", _uM("src" to "/static/me.png", "mode" to "aspectFit", "class" to "icon-large")),
                            _cE("text", _uM("class" to "prompt-text"), "Please log in to view your bookings"),
                            _cV(_component_navigator, _uM("url" to "/pages/login/login", "open-type" to "navigate"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                return _uA(
                                    _cE("button", _uM("class" to "login-btn"), "Go to Login")
                                )
                            }), "_" to 1))
                        ))
                    } else {
                        _cE("view", _uM("key" to 1, "class" to "scroll-wrap", "style" to _nS(_uM("height" to (scrollWrapHeight.value + "px")))), _uA(
                            _cE("scroll-view", _uM("class" to "scroll-area", "direction" to "vertical", "show-scrollbar" to "true", "enable-back-to-top" to "true", "style" to _nS(_uM("height" to (scrollViewHeight.value + "px"))), "refresher-enabled" to "true", "refresher-triggered" to isRefreshing.value, "onRefresherrefresh" to onPullRefresh), _uA(
                                _cE("view", _uM("class" to "title-section"), _uA(
                                    _cE("text", _uM("class" to "title-text"), "My Bookings")
                                )),
                                if (isTrue(isLoading.value)) {
                                    _cE("view", _uM("key" to 0, "class" to "loading-section"), _uA(
                                        _cE("text", _uM("class" to "loading-text"), "Loading bookings...")
                                    ))
                                } else {
                                    if (loadError.value.length > 0) {
                                        _cE("view", _uM("key" to 1, "class" to "error-section"), _uA(
                                            _cE("text", _uM("class" to "error-text"), _tD(loadError.value), 1),
                                            _cE("button", _uM("class" to "retry-btn", "onClick" to fun(){
                                                fetchBookings(false)
                                            }), "Retry", 8, _uA(
                                                "onClick"
                                            ))
                                        ))
                                    } else {
                                        if (bookings.value.length == 0) {
                                            _cE("view", _uM("key" to 2, "class" to "empty-section"), _uA(
                                                _cE("text", _uM("class" to "empty-text"), "No bookings yet"),
                                                _cE("text", _uM("class" to "empty-hint"), "Go to Bike to make your first booking"),
                                                _cV(_component_navigator, _uM("url" to "/pages/bikelist/bikelist", "open-type" to "reLaunch"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                                    return _uA(
                                                        _cE("button", _uM("class" to "explore-btn"), "Explore Bikes")
                                                    )
                                                }), "_" to 1))
                                            ))
                                        } else {
                                            _cE("view", _uM("key" to 3, "class" to "bookings-list"), _uA(
                                                _cE(Fragment, null, RenderHelpers.renderList(bookings.value, fun(booking, index, __index, _cached): Any {
                                                    return _cE("view", _uM("key" to if (booking.id != null) {
                                                        booking.id
                                                    } else {
                                                        index
                                                    }
                                                    , "class" to "booking-card"), _uA(
                                                        _cE("view", _uM("class" to "card-header"), _uA(
                                                            _cE("text", _uM("class" to "booking-id"), "#" + _tD(if (booking.id != null) {
                                                                booking.id
                                                            } else {
                                                                index + 1
                                                            }
                                                            ), 1),
                                                            _cE("text", _uM("class" to _nC(_uA(
                                                                "status-badge",
                                                                "status-" + formatStatus(booking.status)
                                                            ))), _tD(formatStatus(booking.status)), 3)
                                                        )),
                                                        _cE("view", _uM("class" to "card-body"), _uA(
                                                            _cE("view", _uM("class" to "detail-row"), _uA(
                                                                _cE("text", _uM("class" to "label"), "Bike"),
                                                                _cE("text", _uM("class" to "value"), _tD(formatBikeId(booking.scooterId)), 1)
                                                            )),
                                                            _cE("view", _uM("class" to "detail-row"), _uA(
                                                                _cE("text", _uM("class" to "label"), "Duration"),
                                                                _cE("text", _uM("class" to "value"), _tD(booking.duration), 1)
                                                            )),
                                                            _cE("view", _uM("class" to "detail-row"), _uA(
                                                                _cE("text", _uM("class" to "label"), "Start"),
                                                                _cE("text", _uM("class" to "value"), _tD(booking.startTime), 1)
                                                            )),
                                                            _cE("view", _uM("class" to "detail-row"), _uA(
                                                                _cE("text", _uM("class" to "label"), "End"),
                                                                _cE("text", _uM("class" to "value"), _tD(booking.endTime), 1)
                                                            )),
                                                            _cE("view", _uM("class" to "detail-row"), _uA(
                                                                _cE("text", _uM("class" to "label"), "Total"),
                                                                _cE("text", _uM("class" to "value"), "£" + _tD(booking.price), 1)
                                                            )),
                                                            if (isTrue(canCancel(booking.status))) {
                                                                _cE("view", _uM("key" to 0, "class" to "card-actions"), _uA(
                                                                    _cE("button", _uM("class" to "cancel-order-btn", "onClick" to fun(){
                                                                        cancelOrder(booking)
                                                                    }), "Cancel Order", 8, _uA(
                                                                        "onClick"
                                                                    ))
                                                                ))
                                                            } else {
                                                                _cC("v-if", true)
                                                            }
                                                        ))
                                                    ))
                                                }
                                                ), 128)
                                            ))
                                        }
                                    }
                                }
                            ), 44, _uA(
                                "refresher-triggered"
                            ))
                        ), 4)
                    }
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
                return _uM("container" to _pS(_uM("width" to "100%", "display" to "flex", "flexDirection" to "column", "backgroundColor" to "#f5f5f5", "boxSizing" to "border-box")), "login-prompt" to _pS(_uM("display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 60, "paddingRight" to 24, "paddingBottom" to 60, "paddingLeft" to 24)), "icon-large" to _pS(_uM("width" to 80, "height" to 80, "opacity" to 0.6, "marginBottom" to 24)), "prompt-text" to _pS(_uM("fontSize" to 16, "color" to "#666666", "marginBottom" to 24, "textAlign" to "center")), "login-btn" to _pS(_uM("width" to 200, "height" to 44, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold")), "scroll-wrap" to _pS(_uM("width" to "100%")), "scroll-area" to _pS(_uM("width" to "100%", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 40, "paddingLeft" to 20, "boxSizing" to "border-box")), "title-section" to _pS(_uM("marginBottom" to 20)), "title-text" to _pS(_uM("fontSize" to 22, "fontWeight" to "bold", "color" to "#333333")), "loading-section" to _pS(_uM("paddingTop" to 40, "paddingRight" to 20, "paddingBottom" to 40, "paddingLeft" to 20, "textAlign" to "center")), "error-section" to _pS(_uM("paddingTop" to 40, "paddingRight" to 20, "paddingBottom" to 40, "paddingLeft" to 20, "textAlign" to "center")), "loading-text" to _pS(_uM("fontSize" to 14, "color" to "#999999")), "error-text" to _pS(_uM("fontSize" to 14, "color" to "#f44336", "marginBottom" to 16)), "retry-btn" to _pS(_uM("paddingTop" to 10, "paddingRight" to 24, "paddingBottom" to 10, "paddingLeft" to 24, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "fontWeight" to "bold")), "empty-section" to _pS(_uM("display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 60, "paddingRight" to 24, "paddingBottom" to 60, "paddingLeft" to 24, "backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)")), "empty-text" to _pS(_uM("fontSize" to 18, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "empty-hint" to _pS(_uM("fontSize" to 14, "color" to "#999999", "marginBottom" to 24, "textAlign" to "center")), "explore-btn" to _pS(_uM("paddingTop" to 12, "paddingRight" to 32, "paddingBottom" to 12, "paddingLeft" to 32, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "fontWeight" to "bold")), "bookings-list" to _pS(_uM("display" to "flex", "flexDirection" to "column", "gap" to "16px")), "booking-card" to _pS(_uM("backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "paddingTop" to 16, "paddingRight" to 16, "paddingBottom" to 16, "paddingLeft" to 16, "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)")), "card-header" to _pS(_uM("display" to "flex", "flexDirection" to "row", "justifyContent" to "space-between", "alignItems" to "center", "marginBottom" to 12, "paddingBottom" to 10, "borderBottomWidth" to 1, "borderBottomStyle" to "solid", "borderBottomColor" to "#eeeeee")), "booking-id" to _pS(_uM("fontSize" to 16, "fontWeight" to "bold", "color" to "#333333")), "status-badge" to _pS(_uM("fontSize" to 12, "paddingTop" to 4, "paddingRight" to 10, "paddingBottom" to 4, "paddingLeft" to 10, "borderTopLeftRadius" to 6, "borderTopRightRadius" to 6, "borderBottomRightRadius" to 6, "borderBottomLeftRadius" to 6, "fontWeight" to "bold")), "status-Confirmed" to _pS(_uM("backgroundColor" to "#4CAF50", "color" to "#FFFFFF")), "status-Pending" to _pS(_uM("backgroundColor" to "#FFC107", "color" to "#333333")), "status-Cancelled" to _pS(_uM("backgroundColor" to "#f44336", "color" to "#FFFFFF")), "status-Completed" to _pS(_uM("backgroundColor" to "#2196F3", "color" to "#FFFFFF")), "status-Unknown" to _pS(_uM("backgroundColor" to "#9E9E9E", "color" to "#FFFFFF")), "card-body" to _pS(_uM("display" to "flex", "flexDirection" to "column", "gap" to "6px")), "card-actions" to _pS(_uM("marginTop" to 12, "paddingTop" to 10, "borderTopWidth" to 1, "borderTopStyle" to "solid", "borderTopColor" to "#eeeeee")), "cancel-order-btn" to _pS(_uM("width" to "100%", "paddingTop" to 10, "paddingRight" to 16, "paddingBottom" to 10, "paddingLeft" to 16, "backgroundColor" to "#f44336", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "fontWeight" to "bold")), "detail-row" to _pS(_uM("display" to "flex", "flexDirection" to "row", "justifyContent" to "space-between", "fontSize" to 14)), "label" to _uM(".detail-row " to _uM("color" to "#999999")), "value" to _uM(".detail-row " to _uM("color" to "#333333")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
