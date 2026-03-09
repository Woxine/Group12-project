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
import io.dcloud.uniapp.extapi.request as uni_request
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesAdminRevenue : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesAdminRevenue) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesAdminRevenue
            val _cache = __ins.renderCache
            val rows = ref(_uA<DurationRow>())
            val isLoading = ref(false)
            val loadData = fun(){
                if (isLoading.value) {
                    return
                }
                isLoading.value = true
                val tokenRaw = uni_getStorageSync("token")
                val token = if ((tokenRaw != null && tokenRaw !== "")) {
                    ("" + tokenRaw).trim()
                } else {
                    ""
                }
                if (token.length === 0) {
                    uni_showToast(ShowToastOptions(title = "Please login first", icon = "none"))
                    isLoading.value = false
                    return
                }
                uni_request<Any>(RequestOptions(url = buildApiUrl("/api/v1/admin/revenue/duration-week"), method = "GET", header = object : UTSJSONObject() {
                    var `Content-Type` = "application/json"
                    var Authorization = "Bearer " + token
                }, success = fun(res){
                    if (res.statusCode >= 200 && res.statusCode < 300) {
                        val root = res.data as UTSJSONObject
                        val result: UTSArray<DurationRow> = _uA()
                        val dataField = root["data"]
                        if (dataField != null && UTSArray.isArray(dataField)) {
                            val arr = dataField as UTSArray<UTSJSONObject>
                            run {
                                var i: Number = 0
                                while(i < arr.length){
                                    val item = arr[i]
                                    val dtRaw = item["durationType"]
                                    val trRaw = item["totalRevenue"]
                                    val toRaw = item["totalOrders"]
                                    val dt = if (dtRaw != null) {
                                        ("" + dtRaw)
                                    } else {
                                        "Unknown"
                                    }
                                    val trStr = if (trRaw != null) {
                                        ("" + trRaw)
                                    } else {
                                        "0"
                                    }
                                    val toInt = if (toRaw != null) {
                                        parseInt("" + toRaw)
                                    } else {
                                        0
                                    }
                                    result.push(DurationRow(durationType = dt, totalRevenue = trStr, totalOrders = toInt))
                                    i++
                                }
                            }
                        } else if (UTSArray.isArray(root as Any)) {
                            val arr = (root as Any) as UTSArray<UTSJSONObject>
                            run {
                                var i: Number = 0
                                while(i < arr.length){
                                    val item = arr[i]
                                    val dtRaw = item["durationType"]
                                    val trRaw = item["totalRevenue"]
                                    val toRaw = item["totalOrders"]
                                    val dt = if (dtRaw != null) {
                                        ("" + dtRaw)
                                    } else {
                                        "Unknown"
                                    }
                                    val trStr = if (trRaw != null) {
                                        ("" + trRaw)
                                    } else {
                                        "0"
                                    }
                                    val toInt = if (toRaw != null) {
                                        parseInt("" + toRaw)
                                    } else {
                                        0
                                    }
                                    result.push(DurationRow(durationType = dt, totalRevenue = trStr, totalOrders = toInt))
                                    i++
                                }
                            }
                        }
                        rows.value = result
                    } else {
                        uni_showToast(ShowToastOptions(title = "Failed to load revenue", icon = "none"))
                    }
                }
                , fail = fun(_){
                    uni_showToast(ShowToastOptions(title = "Network error", icon = "none"))
                }
                , complete = fun(_){
                    isLoading.value = false
                }
                ))
            }
            onMounted(fun(){
                loadData()
            }
            )
            return fun(): Any? {
                return _cE("view", _uM("class" to "container"), _uA(
                    _cE("view", _uM("class" to "title-section"), _uA(
                        _cE("text", _uM("class" to "title-text"), "Weekly Revenue by Duration"),
                        _cE("text", _uM("class" to "hint-text"), "Current week grouped by rental duration")
                    )),
                    if (isTrue(isLoading.value)) {
                        _cE("view", _uM("key" to 0, "class" to "card"), _uA(
                            _cE("text", _uM("class" to "loading-text"), "Loading...")
                        ))
                    } else {
                        if (rows.value.length == 0) {
                            _cE("view", _uM("key" to 1, "class" to "card"), _uA(
                                _cE("text", _uM("class" to "empty-text"), "No bookings for this week.")
                            ))
                        } else {
                            _cE("view", _uM("key" to 2, "class" to "card"), _uA(
                                _cE(Fragment, null, RenderHelpers.renderList(rows.value, fun(row, index, __index, _cached): Any {
                                    return _cE("view", _uM("key" to index, "class" to "row"), _uA(
                                        _cE("view", _uM("class" to "row-left"), _uA(
                                            _cE("text", _uM("class" to "duration-text"), _tD(row.durationType), 1),
                                            _cE("text", _uM("class" to "orders-text"), _tD(row.totalOrders) + " orders", 1)
                                        )),
                                        _cE("view", _uM("class" to "row-right"), _uA(
                                            _cE("text", _uM("class" to "revenue-text"), "£" + _tD(row.totalRevenue), 1)
                                        ))
                                    ))
                                }
                                ), 128)
                            ))
                        }
                    }
                    ,
                    _cE("button", _uM("class" to "reload-btn", "disabled" to isLoading.value, "onClick" to loadData), _tD(if (isLoading.value) {
                        "Loading..."
                    } else {
                        "Reload"
                    }
                    ), 9, _uA(
                        "disabled"
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
                return _uM("container" to _pS(_uM("display" to "flex", "flexDirection" to "column", "paddingTop" to 24, "paddingRight" to 24, "paddingBottom" to 24, "paddingLeft" to 24, "backgroundColor" to "#f5f5f5")), "title-section" to _pS(_uM("display" to "flex", "flexDirection" to "column", "marginBottom" to 24)), "title-text" to _pS(_uM("fontSize" to 22, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "hint-text" to _pS(_uM("fontSize" to 14, "color" to "#666666")), "card" to _pS(_uM("backgroundColor" to "#ffffff", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)", "marginBottom" to 24)), "row" to _pS(_uM("display" to "flex", "flexDirection" to "row", "justifyContent" to "space-between", "alignItems" to "center", "marginBottom" to 12, "marginBottom:last-child" to 0)), "row-left" to _pS(_uM("display" to "flex", "flexDirection" to "column")), "duration-text" to _pS(_uM("fontSize" to 16, "fontWeight" to "bold", "color" to "#333333")), "orders-text" to _pS(_uM("fontSize" to 12, "color" to "#666666", "marginTop" to 4)), "revenue-text" to _pS(_uM("fontSize" to 16, "fontWeight" to "bold", "color" to "#4CAF50")), "loading-text" to _pS(_uM("fontSize" to 14, "color" to "#666666")), "empty-text" to _pS(_uM("fontSize" to 14, "color" to "#666666")), "reload-btn" to _pS(_uM("paddingTop" to 14, "paddingRight" to 14, "paddingBottom" to 14, "paddingLeft" to 14, "backgroundColor" to "#4CAF50", "color" to "#ffffff", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "borderTopWidth" to 0, "borderRightWidth" to 0, "borderBottomWidth" to 0, "borderLeftWidth" to 0, "fontSize" to 16, "fontWeight" to "bold")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
