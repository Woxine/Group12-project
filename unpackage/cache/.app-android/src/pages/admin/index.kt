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
open class GenPagesAdminIndex : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesAdminIndex) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesAdminIndex
            val _cache = __ins.renderCache
            val scooterId = ref("")
            val status = ref("")
            val hourRate = ref("")
            val locationLat = ref("")
            val locationLng = ref("")
            val isSubmitting = ref(false)
            val errorMsg = ref("")
            val onSubmit = fun(){
                if (isSubmitting.value) {
                    return
                }
                errorMsg.value = ""
                val idStr = if (scooterId.value != null) {
                    scooterId.value.trim()
                } else {
                    ""
                }
                if (idStr.length === 0) {
                    errorMsg.value = "Please enter scooter ID"
                    uni_showToast(ShowToastOptions(title = "Please enter scooter ID", icon = "none"))
                    return
                }
                val tokenRaw = uni_getStorageSync("token")
                val token = if ((tokenRaw != null && tokenRaw !== "")) {
                    ("" + tokenRaw).trim()
                } else {
                    ""
                }
                if (token.length === 0) {
                    errorMsg.value = "Please login first"
                    uni_showToast(ShowToastOptions(title = "Please login first", icon = "none"))
                    return
                }
                val body: UTSJSONObject = object : UTSJSONObject(UTSSourceMapPosition("body", "pages/admin/index.uvue", 105, 8)) {
                }
                var hasField = false
                val statusStr = if (status.value != null) {
                    status.value.trim()
                } else {
                    ""
                }
                if (statusStr.length > 0) {
                    body["status"] = statusStr
                    hasField = true
                }
                val rateStr = if (hourRate.value != null) {
                    hourRate.value.trim()
                } else {
                    ""
                }
                if (rateStr.length > 0) {
                    body["hour_rate"] = parseFloat(rateStr)
                    hasField = true
                }
                val latStr = if (locationLat.value != null) {
                    locationLat.value.trim()
                } else {
                    ""
                }
                if (latStr.length > 0) {
                    body["location_lat"] = parseFloat(latStr)
                    hasField = true
                }
                val lngStr = if (locationLng.value != null) {
                    locationLng.value.trim()
                } else {
                    ""
                }
                if (lngStr.length > 0) {
                    body["location_lng"] = parseFloat(lngStr)
                    hasField = true
                }
                if (hasField === false) {
                    errorMsg.value = "Please enter at least one field to update"
                    uni_showToast(ShowToastOptions(title = "No fields to update", icon = "none"))
                    return
                }
                isSubmitting.value = true
                uni_request<Any>(RequestOptions(url = BASE_URL + "/api/v1/scooters/" + idStr, method = "PUT", data = body, header = object : UTSJSONObject() {
                    var `Content-Type` = "application/json"
                    var Authorization = "Bearer " + token
                }, success = fun(res){
                    if (res.statusCode >= 200 && res.statusCode < 300) {
                        uni_showToast(ShowToastOptions(title = "Scooter updated", icon = "success"))
                    } else {
                        var msg = "Update failed"
                        val data = res.data as UTSJSONObject?
                        if (data != null) {
                            val m = data["message"]
                            val e = data["error"]
                            if (m != null) {
                                msg = "" + m
                            } else if (e != null) {
                                msg = "" + e
                            }
                        }
                        errorMsg.value = msg
                        uni_showToast(ShowToastOptions(title = msg, icon = "none", duration = 3000))
                    }
                }
                , fail = fun(_){
                    errorMsg.value = "Network error"
                    uni_showToast(ShowToastOptions(title = "Network error", icon = "none"))
                }
                , complete = fun(_){
                    isSubmitting.value = false
                }
                ))
            }
            return fun(): Any? {
                return _cE("view", _uM("class" to "container"), _uA(
                    _cE("view", _uM("class" to "title-section"), _uA(
                        _cE("text", _uM("class" to "title-text"), "Admin Panel"),
                        _cE("text", _uM("class" to "hint-text"), "Modify scooter information")
                    )),
                    _cE("view", _uM("class" to "form-section"), _uA(
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Scooter ID"),
                            _cE("input", _uM("class" to "input", "modelValue" to scooterId.value, "onInput" to fun(`$event`: UniInputEvent){
                                scooterId.value = `$event`.detail.value
                            }
                            , "placeholder" to "Enter scooter ID (e.g. 1)", "placeholder-class" to "input-placeholder", "type" to "number"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            ))
                        )),
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Status (AVAILABLE / RENTED / MAINTENANCE)"),
                            _cE("input", _uM("class" to "input", "modelValue" to status.value, "onInput" to fun(`$event`: UniInputEvent){
                                status.value = `$event`.detail.value
                            }
                            , "placeholder" to "AVAILABLE", "placeholder-class" to "input-placeholder"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            ))
                        )),
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Hour Rate (£ / hour)"),
                            _cE("input", _uM("class" to "input", "modelValue" to hourRate.value, "onInput" to fun(`$event`: UniInputEvent){
                                hourRate.value = `$event`.detail.value
                            }
                            , "placeholder" to "e.g. 1.5", "placeholder-class" to "input-placeholder", "type" to "number"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            ))
                        )),
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Location Latitude"),
                            _cE("input", _uM("class" to "input", "modelValue" to locationLat.value, "onInput" to fun(`$event`: UniInputEvent){
                                locationLat.value = `$event`.detail.value
                            }
                            , "placeholder" to "optional", "placeholder-class" to "input-placeholder", "type" to "number"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            ))
                        )),
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Location Longitude"),
                            _cE("input", _uM("class" to "input", "modelValue" to locationLng.value, "onInput" to fun(`$event`: UniInputEvent){
                                locationLng.value = `$event`.detail.value
                            }
                            , "placeholder" to "optional", "placeholder-class" to "input-placeholder", "type" to "number"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            ))
                        )),
                        if (errorMsg.value.length > 0) {
                            _cE("view", _uM("key" to 0, "class" to "error-row"), _uA(
                                _cE("text", _uM("class" to "error-text"), _tD(errorMsg.value), 1)
                            ))
                        } else {
                            _cC("v-if", true)
                        }
                        ,
                        _cE("button", _uM("class" to "submit-btn", "disabled" to isSubmitting.value, "onClick" to onSubmit), _tD(if (isSubmitting.value) {
                            "Updating..."
                        } else {
                            "Update Scooter"
                        }
                        ), 9, _uA(
                            "disabled"
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
                return _uM("container" to _pS(_uM("display" to "flex", "flexDirection" to "column", "paddingTop" to 24, "paddingRight" to 24, "paddingBottom" to 24, "paddingLeft" to 24, "backgroundColor" to "#f5f5f5")), "title-section" to _pS(_uM("display" to "flex", "flexDirection" to "column", "marginBottom" to 24)), "title-text" to _pS(_uM("fontSize" to 22, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "hint-text" to _pS(_uM("fontSize" to 14, "color" to "#666666")), "form-section" to _pS(_uM("display" to "flex", "flexDirection" to "column", "backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)")), "form-row" to _pS(_uM("display" to "flex", "flexDirection" to "column", "marginBottom" to 16)), "label" to _pS(_uM("fontSize" to 14, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "input" to _pS(_uM("height" to 44, "paddingTop" to 0, "paddingRight" to 12, "paddingBottom" to 0, "paddingLeft" to 12, "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#dddddd", "borderRightColor" to "#dddddd", "borderBottomColor" to "#dddddd", "borderLeftColor" to "#dddddd", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "backgroundColor" to "#FFFFFF")), "input-placeholder" to _pS(_uM("color" to "#999999")), "error-row" to _pS(_uM("marginBottom" to 12)), "error-text" to _pS(_uM("fontSize" to 14, "color" to "#f44336")), "submit-btn" to _pS(_uM("marginTop" to 8, "paddingTop" to 14, "paddingRight" to 14, "paddingBottom" to 14, "paddingLeft" to 14, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
