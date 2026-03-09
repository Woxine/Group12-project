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
import io.dcloud.uniapp.extapi.createMapContext as uni_createMapContext
import io.dcloud.uniapp.extapi.getStorageSync as uni_getStorageSync
import io.dcloud.uniapp.extapi.navigateBack as uni_navigateBack
import io.dcloud.uniapp.extapi.navigateTo as uni_navigateTo
import io.dcloud.uniapp.framework.onLoad
import io.dcloud.uniapp.framework.onReady
import io.dcloud.uniapp.extapi.request as uni_request
open class GenPagesOrdersRoute : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesOrdersRoute) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesOrdersRoute
            val _cache = __ins.renderCache
            val bookingId = ref("")
            val loading = ref(true)
            val error = ref("")
            val startLat = ref(null as Number?)
            val startLng = ref(null as Number?)
            val endLat = ref(null as Number?)
            val endLng = ref(null as Number?)
            val longitude = ref(116.397477)
            val latitude = ref(39.908692)
            val scale = ref(14)
            val markers = ref(_uA<Marker>())
            val polylines = ref(_uA<Polyline>())
            val includePoints = ref(_uA<LocationObject>())
            val mapKey = ref(0)
            val mapUpdatedOnce = ref(false)
            val mapContext = ref(null as MapContext?)
            val hasRoute = computed(fun(): Boolean {
                val slat = startLat.value
                val slng = startLng.value
                val elat = endLat.value
                val elng = endLng.value
                return slat != null && !isNaN(slat) && slng != null && !isNaN(slng) && elat != null && !isNaN(elat) && elng != null && !isNaN(elng)
            }
            )
            val startLatText = ref("Lat: -")
            val startLngText = ref("Lng: -")
            val endLatText = ref("Lat: -")
            val endLngText = ref("Lng: -")
            val zoomIn = fun(){
                if (scale.value < 18) {
                    scale.value = scale.value + 1
                }
            }
            val zoomOut = fun(){
                if (scale.value > 5) {
                    scale.value = scale.value - 1
                }
            }
            val syncMarkersToMap = fun(){
                if (mapContext.value == null) {
                    return
                }
                if (markers.value.length == 0) {
                    return
                }
                val tempMarkers = UTSAndroid.consoleDebugError(JSON.parse<UTSArray<Marker>>(JSON.stringify(markers.value)), " at pages/orders/route.uvue:100")!!
                val opts = MapContextAddMarkersOptions(markers = tempMarkers, clear = true)
                mapContext.value?.addMarkers(opts)
            }
            val onMapUpdated = fun(){
                mapUpdatedOnce.value = true
                syncMarkersToMap()
            }
            val applyRoute = fun(slat: Number, slng: Number, elat: Number, elng: Number){
                startLatText.value = "Lat: " + slat.toFixed(6)
                startLngText.value = "Lng: " + slng.toFixed(6)
                endLatText.value = "Lat: " + elat.toFixed(6)
                endLngText.value = "Lng: " + elng.toFixed(6)
                val startPoint = LocationObject(latitude = slat, longitude = slng)
                val endPoint = LocationObject(latitude = elat, longitude = elng)
                val eps: Number = 0.002
                val samePoint = (slat == elat && slng == elng)
                val endLatForLine = if (samePoint) {
                    (elat + eps)
                } else {
                    elat
                }
                val endLngForLine = if (samePoint) {
                    (elng + eps)
                } else {
                    elng
                }
                val startLinePoint = (uts.sdk.modules.DCloudUniMapTencent.LocationObject(latitude = slat, longitude = slng))
                val endLinePoint = (uts.sdk.modules.DCloudUniMapTencent.LocationObject(latitude = endLatForLine, longitude = endLngForLine))
                val linePoints = _uA<uts.sdk.modules.DCloudUniMapTencent.LocationObject>(startLinePoint, endLinePoint)
                val rawIncludePoints: UTSArray<LocationObject> = if (samePoint) {
                    _uA(
                        startPoint,
                        (LocationObject(latitude = endLatForLine, longitude = endLngForLine))
                    )
                } else {
                    _uA(
                        startPoint,
                        endPoint
                    )
                }
                val rawPolylines = _uA(
                    (Polyline(points = linePoints, color = "#FF3B30", width = 10))
                ) as UTSArray<Polyline>
                polylines.value = rawPolylines
                includePoints.value = UTSAndroid.consoleDebugError(JSON.parse<UTSArray<LocationObject>>(JSON.stringify(rawIncludePoints)), " at pages/orders/route.uvue:127")!!
                longitude.value = (slng + elng) / 2
                latitude.value = (slat + elat) / 2
                scale.value = if (samePoint) {
                    17
                } else {
                    14
                }
                val rawMarkers = _uA(
                    Marker(id = 1, latitude = slat, longitude = slng, title = "Start", iconPath = "/static/loc.png", width = 40, height = 40, anchor = Anchor(x = 0.5, y = 1), callout = MapMarkerCallout(content = "Start", display = "ALWAYS")),
                    Marker(id = 2, latitude = elat, longitude = elng, title = "End", iconPath = "/static/loc.png", width = 40, height = 40, anchor = Anchor(x = 0.5, y = 1), callout = MapMarkerCallout(content = "End", display = "ALWAYS"))
                ) as UTSArray<Marker>
                markers.value = UTSAndroid.consoleDebugError(JSON.parse<UTSArray<Marker>>(JSON.stringify(rawMarkers)), " at pages/orders/route.uvue:135")!!
                mapKey.value = mapKey.value + 1
                console.log("applyRoute:", _uO("slat" to slat, "slng" to slng, "elat" to elat, "elng" to elng, "markers" to markers.value, "includePoints" to includePoints.value, "polylines" to polylines.value), " at pages/orders/route.uvue:137")
                syncMarkersToMap()
                setTimeout(fun(){
                    if (mapUpdatedOnce.value) {
                        mapKey.value = mapKey.value + 1
                        syncMarkersToMap()
                    }
                }
                , 200)
            }
            onReady(fun(){
                val instance = getCurrentInstance()
                if (instance != null && instance.proxy != null) {
                    mapContext.value = uni_createMapContext("routeMap", instance.proxy!!)
                } else {
                    mapContext.value = uni_createMapContext("routeMap", null)
                }
                syncMarkersToMap()
            }
            )
            val loadBooking = fun(){
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
                if (userId == null || token == null || bookingId.value.length == 0) {
                    error.value = "Please log in first"
                    loading.value = false
                    return
                }
                uni_request<Any>(RequestOptions(url = BASE_URL + "/api/v1/users/" + userId + "/bookings/" + bookingId.value, method = "GET", header = object : UTSJSONObject() {
                    var `Content-Type` = "application/json"
                    var Authorization = "Bearer " + token
                }, success = fun(res){
                    if (res.statusCode >= 200 && res.statusCode < 300 && res.data != null) {
                        val obj = res.data as UTSJSONObject
                        val data = if (obj["data"] != null) {
                            (obj["data"] as UTSJSONObject)
                        } else {
                            obj
                        }
                        val slatVal = data["start_lat"]
                        val slngVal = data["start_lng"]
                        val elatVal = data["end_lat"]
                        val elngVal = data["end_lng"]
                        var slat = if (slatVal != null && UTSAndroid.`typeof`(slatVal) === "number") {
                            slatVal as Number
                        } else {
                            null
                        }
                        var slng = if (slngVal != null && UTSAndroid.`typeof`(slngVal) === "number") {
                            slngVal as Number
                        } else {
                            null
                        }
                        var elat = if (elatVal != null && UTSAndroid.`typeof`(elatVal) === "number") {
                            elatVal as Number
                        } else {
                            null
                        }
                        var elng = if (elngVal != null && UTSAndroid.`typeof`(elngVal) === "number") {
                            elngVal as Number
                        } else {
                            null
                        }
                        if (slat != null && slng != null && (elat == null || elng == null)) {
                            elat = slat
                            elng = slng
                        }
                        startLat.value = slat
                        startLng.value = slng
                        endLat.value = elat
                        endLng.value = elng
                        if (slat != null && slng != null && elat != null && elng != null && !isNaN(slat) && !isNaN(slng) && !isNaN(elat) && !isNaN(elng)) {
                            applyRoute(slat, slng, elat, elng)
                        }
                    } else {
                        error.value = "Failed to load booking"
                    }
                }
                , fail = fun(_){
                    error.value = "Network error"
                }
                , complete = fun(_){
                    loading.value = false
                }
                ))
            }
            val goBack = fun(){
                uni_navigateBack(NavigateBackOptions(fail = fun(_){
                    uni_navigateTo(NavigateToOptions(url = "/pages/orders/orders"))
                }
                ))
            }
            onLoad(fun(options: UTSJSONObject){
                val parseNum = fun(v: Any?): Number? {
                    if (v == null) {
                        return null
                    }
                    val raw = ("" + v).trim()
                    if (raw.length == 0) {
                        return null
                    }
                    val n = parseFloat(raw)
                    return if (isNaN(n)) {
                        null
                    } else {
                        n
                    }
                }
                val id = if (options != null && options["bookingId"] != null) {
                    ("" + options["bookingId"])
                } else {
                    ""
                }
                bookingId.value = id
                val slatOpt = if (options != null) {
                    parseNum(options["startLat"])
                } else {
                    null
                }
                val slngOpt = if (options != null) {
                    parseNum(options["startLng"])
                } else {
                    null
                }
                var elatOpt = if (options != null) {
                    parseNum(options["endLat"])
                } else {
                    null
                }
                var elngOpt = if (options != null) {
                    parseNum(options["endLng"])
                } else {
                    null
                }
                if (slatOpt != null && slngOpt != null && (elatOpt == null || elngOpt == null)) {
                    elatOpt = slatOpt
                    elngOpt = slngOpt
                }
                if (slatOpt != null && slngOpt != null && elatOpt != null && elngOpt != null) {
                    startLat.value = slatOpt
                    startLng.value = slngOpt
                    endLat.value = elatOpt
                    endLng.value = elngOpt
                    applyRoute(slatOpt, slngOpt, elatOpt, elngOpt)
                    loading.value = false
                    return
                }
                if (id.length > 0) {
                    loadBooking()
                } else {
                    error.value = "Missing booking id"
                    loading.value = false
                }
            }
            )
            return fun(): Any? {
                val _component_map = resolveComponent("map")
                return _cE("view", _uM("class" to "container"), _uA(
                    if (isTrue(loading.value)) {
                        _cE("view", _uM("key" to 0, "class" to "loading-section"), _uA(
                            _cE("text", _uM("class" to "loading-text"), "Loading route...")
                        ))
                    } else {
                        if (error.value.length > 0) {
                            _cE("view", _uM("key" to 1, "class" to "error-section"), _uA(
                                _cE("text", _uM("class" to "error-text"), _tD(error.value), 1),
                                _cE("button", _uM("class" to "back-btn", "onClick" to goBack), "Back")
                            ))
                        } else {
                            if (isTrue(!hasRoute.value)) {
                                _cE("view", _uM("key" to 2, "class" to "no-route-section"), _uA(
                                    _cE("text", _uM("class" to "no-route-text"), "Route data not available for this order."),
                                    _cE("text", _uM("class" to "no-route-hint"), "Start and end locations are recorded when you start and end the ride."),
                                    _cE("button", _uM("class" to "back-btn", "onClick" to goBack), "Back")
                                ))
                            } else {
                                _cE("view", _uM("key" to 3, "class" to "map-wrap"), _uA(
                                    _cE("view", _uM("class" to "map-section"), _uA(
                                        _cV(_component_map, _uM("key" to mapKey.value, "id" to "routeMap", "class" to "route-map", "longitude" to longitude.value, "latitude" to latitude.value, "scale" to scale.value, "markers" to markers.value, "polyline" to polylines.value, "include-points" to includePoints.value, "onUpdated" to onMapUpdated), null, 8, _uA(
                                            "longitude",
                                            "latitude",
                                            "scale",
                                            "markers",
                                            "polyline",
                                            "include-points"
                                        )),
                                        _cE("cover-view", _uM("class" to "map-controls"), _uA(
                                            _cE("cover-view", _uM("class" to "control-btn", "onClick" to zoomIn), "+"),
                                            _cE("cover-view", _uM("class" to "control-btn", "onClick" to zoomOut), "-")
                                        ))
                                    )),
                                    _cE("view", _uM("class" to "info-section"), _uA(
                                        _cE("text", _uM("class" to "info-label"), "Start"),
                                        _cE("text", _uM("class" to "info-value"), _tD(startLatText.value), 1),
                                        _cE("text", _uM("class" to "info-value"), _tD(startLngText.value), 1),
                                        _cE("text", _uM("class" to "info-label"), "End"),
                                        _cE("text", _uM("class" to "info-value"), _tD(endLatText.value), 1),
                                        _cE("text", _uM("class" to "info-value"), _tD(endLngText.value), 1)
                                    )),
                                    _cE("button", _uM("class" to "back-btn", "onClick" to goBack), "Back to Orders")
                                ))
                            }
                        }
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
                return _uM("container" to _pS(_uM("width" to "100%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "backgroundColor" to "#f5f5f5", "boxSizing" to "border-box")), "loading-section" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 24, "paddingRight" to 24, "paddingBottom" to 24, "paddingLeft" to 24)), "error-section" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 24, "paddingRight" to 24, "paddingBottom" to 24, "paddingLeft" to 24)), "no-route-section" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 24, "paddingRight" to 24, "paddingBottom" to 24, "paddingLeft" to 24)), "loading-text" to _pS(_uM("fontSize" to 16, "color" to "#666666")), "error-text" to _pS(_uM("fontSize" to 16, "color" to "#f44336", "textAlign" to "center", "marginBottom" to 12)), "no-route-text" to _pS(_uM("fontSize" to 16, "color" to "#f44336", "textAlign" to "center", "marginBottom" to 12)), "no-route-hint" to _pS(_uM("fontSize" to 14, "color" to "#999999", "textAlign" to "center", "marginBottom" to 20)), "back-btn" to _pS(_uM("marginTop" to 16, "paddingTop" to 10, "paddingRight" to 24, "paddingBottom" to 10, "paddingLeft" to 24, "backgroundColor" to "#2196F3", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "fontWeight" to "bold")), "map-wrap" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "display" to "flex", "flexDirection" to "column", "paddingTop" to 16, "paddingRight" to 16, "paddingBottom" to 16, "paddingLeft" to 16)), "map-section" to _pS(_uM("width" to "100%", "position" to "relative", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "overflow" to "hidden", "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)", "marginBottom" to 16)), "map-controls" to _pS(_uM("position" to "absolute", "right" to 12, "bottom" to 12, "display" to "flex", "flexDirection" to "column", "zIndex" to 10)), "control-btn" to _pS(_uM("width" to 36, "height" to 36, "lineHeight" to "36px", "textAlign" to "center", "backgroundColor" to "rgba(0,0,0,0.55)", "color" to "#ffffff", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "marginTop" to 8, "fontSize" to 18, "fontWeight" to "bold")), "route-map" to _pS(_uM("width" to "100%", "height" to 320, "backgroundColor" to "#e5e5e5")), "info-section" to _pS(_uM("backgroundColor" to "#ffffff", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "paddingTop" to 16, "paddingRight" to 16, "paddingBottom" to 16, "paddingLeft" to 16, "marginBottom" to 16, "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)")), "info-label" to _pS(_uM("fontSize" to 12, "color" to "#999999", "marginBottom" to 4)), "info-value" to _pS(_uM("fontSize" to 14, "color" to "#333333", "marginBottom" to 12, "marginBottom:last-of-type" to 0)))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
