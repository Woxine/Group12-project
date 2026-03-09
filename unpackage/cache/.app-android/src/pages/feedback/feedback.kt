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
open class GenPagesFeedbackFeedback : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesFeedbackFeedback) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesFeedbackFeedback
            val _cache = __ins.renderCache
            val scooterId = ref("")
            val description = ref("")
            val location = ref("")
            val isSubmitting = ref(false)
            val submitError = ref("")
            val onSubmit = fun(){
                submitError.value = ""
                val sid = if (scooterId.value != null) {
                    scooterId.value.trim()
                } else {
                    ""
                }
                val desc = if (description.value != null) {
                    description.value.trim()
                } else {
                    ""
                }
                val loc = if (location.value != null) {
                    location.value.trim()
                } else {
                    ""
                }
                if (sid.length === 0) {
                    submitError.value = "Please enter Scooter ID"
                    uni_showToast(ShowToastOptions(title = "Please enter Scooter ID", icon = "none"))
                    return
                }
                if (desc.length === 0) {
                    submitError.value = "Please enter a description"
                    uni_showToast(ShowToastOptions(title = "Please enter a description", icon = "none"))
                    return
                }
                isSubmitting.value = true
                val tokenRaw = uni_getStorageSync("token")
                val token = if ((tokenRaw != null && tokenRaw !== "")) {
                    ("" + tokenRaw).trim()
                } else {
                    ""
                }
                val header: UTSJSONObject = object : UTSJSONObject(UTSSourceMapPosition("header", "pages/feedback/feedback.uvue", 79, 8)) {
                    var `Content-Type` = "application/json"
                }
                if (token.length > 0) {
                    header["Authorization"] = "Bearer " + token
                }
                val body: UTSJSONObject = object : UTSJSONObject(UTSSourceMapPosition("body", "pages/feedback/feedback.uvue", 86, 8)) {
                    var scooter_id = sid
                    var description = desc
                }
                if (loc.length > 0) {
                    body["location"] = loc
                }
                uni_request<Any>(RequestOptions(url = BASE_URL + "/api/v1/feedbacks", method = "POST", header = header, data = body, success = fun(res){
                    if (res.statusCode >= 200 && res.statusCode < 300) {
                        uni_showToast(ShowToastOptions(title = "Feedback submitted", icon = "success"))
                        scooterId.value = ""
                        description.value = ""
                        location.value = ""
                    } else {
                        var msg = "Failed to submit feedback"
                        val data = res.data as UTSJSONObject?
                        if (data != null && data["message"] != null) {
                            msg = "" + data["message"]
                        } else if (data != null && data["error"] != null) {
                            msg = "" + data["error"]
                        }
                        submitError.value = msg
                        uni_showToast(ShowToastOptions(title = msg, icon = "none", duration = 3000))
                    }
                }
                , fail = fun(_){
                    submitError.value = "Network error. Please try again."
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
                        _cE("text", _uM("class" to "title-text"), "Submit Feedback"),
                        _cE("text", _uM("class" to "hint-text"), "Report an issue or suggest an improvement")
                    )),
                    _cE("view", _uM("class" to "form-section"), _uA(
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Scooter ID"),
                            _cE("input", _uM("class" to "input", "modelValue" to scooterId.value, "onInput" to fun(`$event`: UniInputEvent){
                                scooterId.value = `$event`.detail.value
                            }
                            , "placeholder" to "e.g. 1 or SC001", "placeholder-class" to "input-placeholder"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            ))
                        )),
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Description"),
                            _cE("textarea", _uM("class" to "textarea", "modelValue" to description.value, "onInput" to fun(`$event`: UniInputEvent){
                                description.value = `$event`.detail.value
                            }
                            , "placeholder" to "Describe your feedback (required)", "placeholder-class" to "input-placeholder", "maxlength" to 500), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            )),
                            _cE("text", _uM("class" to "char-count"), _tD(description.value.length) + "/500", 1)
                        )),
                        _cE("view", _uM("class" to "form-row"), _uA(
                            _cE("text", _uM("class" to "label"), "Location (optional)"),
                            _cE("input", _uM("class" to "input", "modelValue" to location.value, "onInput" to fun(`$event`: UniInputEvent){
                                location.value = `$event`.detail.value
                            }
                            , "placeholder" to "e.g. Street name or area", "placeholder-class" to "input-placeholder"), null, 40, _uA(
                                "modelValue",
                                "onInput"
                            ))
                        )),
                        if (submitError.value.length > 0) {
                            _cE("view", _uM("key" to 0, "class" to "error-row"), _uA(
                                _cE("text", _uM("class" to "error-text"), _tD(submitError.value), 1)
                            ))
                        } else {
                            _cC("v-if", true)
                        }
                        ,
                        _cE("button", _uM("class" to _nC(_uA(
                            "submit-btn",
                            if (isSubmitting.value) {
                                "submit-btn-disabled"
                            } else {
                                ""
                            }
                        )), "disabled" to isSubmitting.value, "onClick" to onSubmit), "Submit Feedback", 10, _uA(
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
                return _uM("container" to _pS(_uM("display" to "flex", "flexDirection" to "column", "paddingTop" to 24, "paddingRight" to 24, "paddingBottom" to 24, "paddingLeft" to 24, "backgroundColor" to "#f5f5f5", "minHeight" to 750)), "title-section" to _pS(_uM("display" to "flex", "flexDirection" to "column", "marginBottom" to 24)), "title-text" to _pS(_uM("fontSize" to 22, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "hint-text" to _pS(_uM("fontSize" to 14, "color" to "#666666")), "form-section" to _pS(_uM("display" to "flex", "flexDirection" to "column", "backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)")), "form-row" to _pS(_uM("display" to "flex", "flexDirection" to "column", "marginBottom" to 16)), "label" to _pS(_uM("fontSize" to 14, "fontWeight" to "bold", "color" to "#333333", "marginBottom" to 8)), "input" to _pS(_uM("height" to 44, "paddingTop" to 0, "paddingRight" to 12, "paddingBottom" to 0, "paddingLeft" to 12, "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#dddddd", "borderRightColor" to "#dddddd", "borderBottomColor" to "#dddddd", "borderLeftColor" to "#dddddd", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "backgroundColor" to "#FFFFFF")), "textarea" to _pS(_uM("minHeight" to 100, "paddingTop" to 12, "paddingRight" to 12, "paddingBottom" to 12, "paddingLeft" to 12, "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#dddddd", "borderRightColor" to "#dddddd", "borderBottomColor" to "#dddddd", "borderLeftColor" to "#dddddd", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 14, "backgroundColor" to "#FFFFFF")), "input-placeholder" to _pS(_uM("color" to "#999999")), "char-count" to _pS(_uM("fontSize" to 12, "color" to "#999999", "marginTop" to 4)), "error-row" to _pS(_uM("marginBottom" to 12)), "error-text" to _pS(_uM("fontSize" to 14, "color" to "#f44336")), "submit-btn" to _pS(_uM("marginTop" to 8, "paddingTop" to 14, "paddingRight" to 14, "paddingBottom" to 14, "paddingLeft" to 14, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold")), "submit-btn-disabled" to _pS(_uM("backgroundColor" to "#cccccc")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
