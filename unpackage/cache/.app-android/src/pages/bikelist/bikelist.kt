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
import io.dcloud.uniapp.framework.onShow
import io.dcloud.uniapp.extapi.request as uni_request
import io.dcloud.uniapp.extapi.setStorageSync as uni_setStorageSync
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesBikelistBikelist : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesBikelistBikelist) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesBikelistBikelist
            val _cache = __ins.renderCache
            val scooterList = ref(_uA<Scooter>())
            val isLoading = ref(false)
            val loadError = ref("")
            val isRefreshing = ref(false)
            val fetchScooters = fun(fromPullRefresh: Boolean){
                if (fromPullRefresh === false) {
                    isLoading.value = true
                }
                loadError.value = ""
                uni_request<Any>(RequestOptions(url = BASE_URL + "/api/v1/scooters", method = "GET", success = fun(res){
                    val ok = (res.statusCode === 200) && (res.data != null)
                    if (ok) {
                        val raw = res.data as UTSJSONObject
                        var arr: UTSArray<UTSJSONObject> = _uA()
                        val hasData = raw["data"] != null
                        val hasScooters = raw["scooters"] != null
                        val hasList = raw["list"] != null
                        if (hasData) {
                            arr = raw["data"] as UTSArray<UTSJSONObject>
                        } else if (hasScooters) {
                            arr = raw["scooters"] as UTSArray<UTSJSONObject>
                        } else if (UTSArray.isArray(raw)) {
                            arr = raw as UTSArray<UTSJSONObject>
                        } else if (hasList) {
                            arr = raw["list"] as UTSArray<UTSJSONObject>
                        }
                        val list: UTSArray<Scooter> = _uA()
                        run {
                            var i: Number = 0
                            while(i < arr.length){
                                val item = arr[i]
                                val rawId = item["id"]
                                var id: String = ""
                                if (rawId != null) {
                                    id = "" + rawId
                                }
                                var rented: Boolean = false
                                val rawRented = item["rented"]
                                val rawStatus = item["status"]
                                val rawIsRented = item["is_rented"]
                                val rawIsAvailable = item["is_available"]
                                val rawAvailable = item["available"]
                                val rawScooterStatus = item["scooter_status"]
                                val rawAvailability = item["availability"]
                                if (rawRented != null) {
                                    val v = rawRented
                                    rented = (v === true || v === 1 || ("" + v).toLowerCase() === "true")
                                } else if (rawIsRented != null) {
                                    val v = rawIsRented
                                    rented = (v === true || v === 1 || ("" + v).toLowerCase() === "true")
                                } else if (rawStatus != null) {
                                    val s = ("" + rawStatus).toLowerCase()
                                    rented = (s === "rented" || s === "in_use" || s === "in-use" || s === "booked" || s === "occupied")
                                } else if (rawScooterStatus != null) {
                                    val s = ("" + rawScooterStatus).toLowerCase()
                                    rented = (s === "rented" || s === "in_use" || s === "in-use" || s === "booked" || s === "occupied")
                                } else if (rawAvailability != null) {
                                    val s = ("" + rawAvailability).toLowerCase()
                                    rented = (s === "unavailable" || s === "rented" || s === "in_use")
                                } else if (rawIsAvailable != null) {
                                    val v = rawIsAvailable
                                    rented = (v === false || v === 0 || ("" + v).toLowerCase() === "false")
                                } else if (rawAvailable != null) {
                                    val v = rawAvailable
                                    rented = (v === false || v === 0 || ("" + v).toLowerCase() === "false")
                                }
                                list.push(Scooter(id = id, rented = rented))
                                i++
                            }
                        }
                        scooterList.value = list
                    } else {
                        val hasSc: Boolean = (res.statusCode != null)
                        val sc = if (hasSc) {
                            ("" + res.statusCode)
                        } else {
                            ""
                        }
                        loadError.value = "Failed to load scooters (status " + sc + ")"
                    }
                }
                , fail = fun(err){
                    loadError.value = "Network error. Please check server."
                    console.error("Fetch scooters failed:", err, " at pages/bikelist/bikelist.uvue:151")
                }
                , complete = fun(_){
                    isLoading.value = false
                    isRefreshing.value = false
                }
                ))
            }
            val onPullRefresh = fun(){
                isRefreshing.value = true
                fetchScooters(true)
            }
            onMounted(fun(){
                fetchScooters(false)
            }
            )
            onShow(fun(){
                fetchScooters(false)
            }
            )
            val selectedIndex = ref<Number>(-1)
            val toggleSelection = fun(index: Number){
                if (selectedIndex.value === index) {
                    selectedIndex.value = -1
                } else {
                    selectedIndex.value = index
                }
            }
            val bookScooter = fun(){
                if (selectedIndex.value !== -1) {
                    val selectedScooter = scooterList.value[selectedIndex.value]
                    console.log("预订滑板车 ID:", selectedScooter.id, " at pages/bikelist/bikelist.uvue:190")
                    try {
                        uni_setStorageSync("selectedScooterId", selectedScooter.id)
                        console.log("Selected scooter ID saved:", selectedScooter.id, " at pages/bikelist/bikelist.uvue:195")
                    }
                     catch (e: Throwable) {
                        console.error("Failed to save selected scooter ID:", e, " at pages/bikelist/bikelist.uvue:197")
                    }
                    uni_showToast(ShowToastOptions(title = "appoint " + selectedScooter.id + " successfully", icon = "success"))
                }
            }
            return fun(): Any? {
                val _component_navigator = resolveComponent("navigator")
                return _cE("view", _uM("class" to "page-wrapper"), _uA(
                    _cE("scroll-view", _uM("class" to "scroll-container", "scroll-y" to "true", "refresher-enabled" to "true", "refresher-triggered" to isRefreshing.value, "onRefresherrefresh" to onPullRefresh), _uA(
                        if (isTrue(isLoading.value)) {
                            _cE("view", _uM("key" to 0, "class" to "loading-section"), _uA(
                                _cE("text", _uM("class" to "loading-text"), "Loading scooters...")
                            ))
                        } else {
                            if (loadError.value.length > 0) {
                                _cE("view", _uM("key" to 1, "class" to "error-section"), _uA(
                                    _cE("text", _uM("class" to "error-text"), _tD(loadError.value), 1),
                                    _cE("button", _uM("class" to "retry-btn", "onClick" to fetchScooters), "Retry")
                                ))
                            } else {
                                _cE(Fragment, _uM("key" to 2), RenderHelpers.renderList(scooterList.value, fun(scooter, index, __index, _cached): Any {
                                    return _cE("view", _uM("key" to index, "class" to "scooter-card"), _uA(
                                        _cE("image", _uM("src" to "/static/bikelist.jpg", "mode" to "aspectFit", "class" to "scooter-image")),
                                        _cE("view", _uM("class" to "card-content"), _uA(
                                            _cE("text", _uM("class" to "scooter-id"), "ID: " + _tD(scooter.id), 1),
                                            _cE("text", _uM("class" to "scooter-status"), _tD(if (scooter.rented) {
                                                "Rented"
                                            } else {
                                                "Available"
                                            }
                                            ), 1)
                                        )),
                                        _cE("view", _uM("class" to "checkbox-container", "onClick" to fun(){
                                            toggleSelection(index)
                                        }
                                        ), _uA(
                                            _cE("view", _uM("class" to _nC(_uA(
                                                "checkbox",
                                                _uM("selected" to (selectedIndex.value === index))
                                            ))), _uA(
                                                if (selectedIndex.value === index) {
                                                    _cE("text", _uM("key" to 0, "class" to "checkmark"), "✓")
                                                } else {
                                                    _cC("v-if", true)
                                                }
                                            ), 2)
                                        ), 8, _uA(
                                            "onClick"
                                        ))
                                    ))
                                }
                                ), 128)
                            }
                        }
                        ,
                        _cE("view", _uM("class" to "booking-section"), _uA(
                            _cV(_component_navigator, _uM("url" to "/pages/price/price"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                return _uA(
                                    _cE("button", _uM("class" to _nC(_uA(
                                        "booking-button",
                                        _uM("disabled" to (selectedIndex.value === -1))
                                    )), "disabled" to (selectedIndex.value === -1), "onClick" to bookScooter), " appoint ", 10, _uA(
                                        "disabled"
                                    ))
                                )
                            }
                            ), "_" to 1))
                        ))
                    ), 40, _uA(
                        "refresher-triggered"
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
                        _cE("view", _uM("class" to "bottom_center"), _uA(
                            _cE("image", _uM("src" to "/static/bike.png", "mode" to "aspectFit", "class" to "bike_icon")),
                            _cE("view", _uM("class" to "center_down"), _uA(
                                _cE("text", _uM("class" to "text3"), "Bike")
                            ))
                        )),
                        _cV(_component_navigator, _uM("url" to "/pages/me/me", "class" to "bottom_right", "open-type" to "reLaunch"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("image", _uM("src" to "/static/me.png", "mode" to "aspectFit", "class" to "icon2")),
                                _cE("view", _uM("class" to "right_down"), _uA(
                                    _cE("text", _uM("class" to "text3"), "Me")
                                ))
                            )
                        }
                        ), "_" to 1))
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
                return _uM("page-wrapper" to _pS(_uM("width" to "100%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "backgroundColor" to "#f5f5f5")), "scroll-container" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "width" to "100%")), "loading-section" to _pS(_uM("paddingTop" to 40, "paddingRight" to 20, "paddingBottom" to 40, "paddingLeft" to 20, "textAlign" to "center")), "error-section" to _pS(_uM("paddingTop" to 40, "paddingRight" to 20, "paddingBottom" to 40, "paddingLeft" to 20, "textAlign" to "center")), "loading-text" to _pS(_uM("fontSize" to 14, "color" to "#999999")), "error-text" to _pS(_uM("fontSize" to 14, "color" to "#f44336", "display" to "flex", "marginBottom" to 16)), "retry-btn" to _pS(_uM("paddingTop" to 10, "paddingRight" to 24, "paddingBottom" to 10, "paddingLeft" to 24, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14)), "container" to _pS(_uM("width" to "100%", "backgroundColor" to "#f5f5f5")), "scooter-card" to _pS(_uM("width" to "90%", "marginTop" to 20, "marginRight" to "auto", "marginBottom" to 20, "marginLeft" to "auto", "paddingTop" to 15, "paddingRight" to 15, "paddingBottom" to 15, "paddingLeft" to 15, "backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "boxShadow" to "0 4px 12px rgba(0, 0, 0, 0.1)", "display" to "flex", "alignItems" to "center")), "scooter-image" to _uM(".scooter-card " to _uM("width" to 100, "height" to 100, "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "marginRight" to 15)), "card-content" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "display" to "flex", "flexDirection" to "column")), "scooter-id" to _uM(".card-content " to _uM("fontSize" to 18, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 5)), "scooter-status" to _uM(".card-content " to _uM("fontSize" to 14, "color" to "#666666", "marginBottom" to 5)), "scooter-location" to _uM(".card-content " to _uM("fontSize" to 12, "color" to "#999999")), "checkbox-container" to _pS(_uM("width" to 30, "height" to 30)), "checkbox" to _uM(".checkbox-container " to _uM("width" to "100%", "height" to "100%", "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#cccccc", "borderRightColor" to "#cccccc", "borderBottomColor" to "#cccccc", "borderLeftColor" to "#cccccc", "borderTopLeftRadius" to 4, "borderTopRightRadius" to 4, "borderBottomRightRadius" to 4, "borderBottomLeftRadius" to 4, "display" to "flex", "justifyContent" to "center", "alignItems" to "center", "backgroundColor" to "#FFFFFF"), ".checkbox-container .selected" to _uM("backgroundColor" to "#4CAF50", "borderTopColor" to "#4CAF50", "borderRightColor" to "#4CAF50", "borderBottomColor" to "#4CAF50", "borderLeftColor" to "#4CAF50")), "checkmark" to _uM(".checkbox-container .checkbox " to _uM("color" to "#FFFFFF", "fontSize" to 16, "fontWeight" to "bold")), "booking-section" to _pS(_uM("width" to "90%", "marginTop" to 20, "marginRight" to "auto", "marginBottom" to 20, "marginLeft" to "auto", "display" to "flex", "justifyContent" to "center")), "booking-button" to _uM(".booking-section " to _uM("width" to "100%", "height" to 45, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold"), ".booking-section .disabled" to _uM("backgroundColor" to "#cccccc", "color" to "#666666")), "bottom" to _pS(_uM("width" to "100%", "height" to "8%", "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "display" to "flex", "justifyContent" to "space-around", "alignItems" to "center", "flexShrink" to 0)), "bottom_left" to _pS(_uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "icon2" to _uM(".bottom_left " to _uM("width" to 30, "height" to 30), ".bottom_right " to _uM("width" to 30, "height" to 30)), "left_down" to _uM(".bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "bottom_center" to _pS(_uM("position" to "relative", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "bike_icon" to _uM(".bottom_center " to _uM("width" to 30, "height" to 30)), "center_down" to _uM(".bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "bottom_right" to _pS(_uM("position" to "relative", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "display" to "flex", "flexDirection" to "column", "justifyContent" to "center", "alignItems" to "center")), "right_down" to _uM(".bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "height" to "40%", "width" to "100%")), "text3" to _pS(_uM("fontSize" to 12, "marginTop" to 4)))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
