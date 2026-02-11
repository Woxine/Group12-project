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
import io.dcloud.uniapp.extapi.reLaunch as uni_reLaunch
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesPricePrice : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesPricePrice) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesPricePrice
            val _cache = __ins.renderCache
            val durations = ref(_uA<DurationItem>(DurationItem(time = "1小时", price = 1), DurationItem(time = "4小时", price = 1), DurationItem(time = "1天", price = 1), DurationItem(time = "1周", price = 1)))
            val selectedIndex = ref<Number>(-1)
            val selectDuration = fun(index: Number){
                selectedIndex.value = index
            }
            val confirm = fun(){
                if (selectedIndex.value !== -1) {
                    val selectedDuration = durations.value[selectedIndex.value]
                    console.log("选择了", selectedDuration.time, "费用", selectedDuration.price, "英镑", " at pages/price/price.uvue:60")
                    uni_showToast(ShowToastOptions(title = "已选择" + selectedDuration.time, icon = "success"))
                } else {
                    uni_showToast(ShowToastOptions(title = "请选择预订时长", icon = "none"))
                }
            }
            val cancel = fun(){
                selectedIndex.value = -1
                uni_showToast(ShowToastOptions(title = "已取消", icon = "none"))
                uni_reLaunch(ReLaunchOptions(url = "/pages/index/index"))
            }
            return fun(): Any? {
                return _cE("view", _uM("class" to "container"), _uA(
                    _cE("view", _uM("class" to "title"), _uA(
                        _cE("text", _uM("class" to "title-text"), "选择预订时长")
                    )),
                    _cE("view", _uM("class" to "duration-cards"), _uA(
                        _cE(Fragment, null, RenderHelpers.renderList(durations.value, fun(duration, index, __index, _cached): Any {
                            return _cE("view", _uM("key" to index, "class" to _nC(_uA(
                                "card",
                                _uM("selected" to (selectedIndex.value === index))
                            )), "onClick" to fun(){
                                selectDuration(index)
                            }
                            ), _uA(
                                _cE("view", _uM("class" to "card-header"), _uA(
                                    _cE("text", _uM("class" to "duration"), _tD(duration.time), 1)
                                )),
                                _cE("view", _uM("class" to "card-body"), _uA(
                                    _cE("text", _uM("class" to "price"), "£" + _tD(duration.price), 1)
                                ))
                            ), 10, _uA(
                                "onClick"
                            ))
                        }
                        ), 128)
                    )),
                    _cE("view", _uM("class" to "button-group"), _uA(
                        _cE("button", _uM("class" to "confirm-btn", "onClick" to confirm), "确定"),
                        _cE("button", _uM("class" to "cancel-btn", "onClick" to cancel), "取消")
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
                return _uM("container" to _pS(_uM("width" to "100%", "height" to "100%", "backgroundColor" to "#f5f5f5", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxSizing" to "border-box")), "title" to _pS(_uM("textAlign" to "center", "marginBottom" to 30)), "title-text" to _pS(_uM("fontSize" to 24, "fontWeight" to "bold", "color" to "#333333")), "duration-cards" to _pS(_uM("display" to "flex", "flexDirection" to "column")), "card" to _uM("" to _uM("backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "boxShadow" to "0 4px 12px rgba(0, 0, 0, 0.1)", "transitionProperty" to "all", "transitionDuration" to "0.3s", "transitionTimingFunction" to "ease", "marginBottom" to 15), ".selected" to _uM("backgroundColor" to "#e8f5e9", "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#4CAF50", "borderRightColor" to "#4CAF50", "borderBottomColor" to "#4CAF50", "borderLeftColor" to "#4CAF50", "transform" to "translateY(-2px)", "boxShadow" to "0 6px 16px rgba(76, 175, 80, 0.2)")), "card-header" to _pS(_uM("textAlign" to "center", "marginBottom" to 10)), "duration" to _pS(_uM("fontSize" to 18, "fontWeight" to "bold", "color" to "#333333")), "card-body" to _pS(_uM("textAlign" to "center")), "price" to _pS(_uM("fontSize" to 20, "fontWeight" to "bold", "color" to "#4CAF50")), "button-group" to _pS(_uM("display" to "flex", "flexDirection" to "column", "alignItems" to "center", "marginTop" to 40)), "confirm-btn" to _pS(_uM("width" to "80%", "height" to 45, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold", "marginBottom" to 15)), "cancel-btn" to _pS(_uM("width" to "80%", "height" to 45, "backgroundColor" to "#f44336", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold")), "@TRANSITION" to _uM("card" to _uM("property" to "all", "duration" to "0.3s", "timingFunction" to "ease")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
