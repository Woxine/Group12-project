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
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesBikelistBikelist : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesBikelistBikelist) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesBikelistBikelist
            val _cache = __ins.renderCache
            val isSelected = ref(false)
            val scooterId = ref("SC001")
            val toggleSelection = fun(){
                isSelected.value = !isSelected.value
            }
            val bookScooter = fun(){
                if (isSelected.value) {
                    console.log("预订滑板车 ID:", scooterId.value, " at pages/bikelist/bikelist.uvue:63")
                    uni_showToast(ShowToastOptions(title = "appoint successfully", icon = "success"))
                }
            }
            return fun(): Any? {
                val _component_navigator = resolveComponent("navigator")
                return _cE("view", _uM("class" to "container"), _uA(
                    _cE("view", _uM("class" to "scooter-card"), _uA(
                        _cE("image", _uM("src" to "/static/bikelist.jpg", "mode" to "aspectFit", "class" to "scooter-image")),
                        _cE("view", _uM("class" to "card-content"), _uA(
                            _cE("text", _uM("class" to "scooter-id"), "ID: " + _tD(scooterId.value), 1)
                        )),
                        _cE("view", _uM("class" to "checkbox-container", "onClick" to toggleSelection), _uA(
                            _cE("view", _uM("class" to _nC(_uA(
                                "checkbox",
                                _uM("selected" to isSelected.value)
                            ))), _uA(
                                if (isTrue(isSelected.value)) {
                                    _cE("text", _uM("key" to 0, "class" to "checkmark"), "✓")
                                } else {
                                    _cC("v-if", true)
                                }
                            ), 2)
                        ))
                    )),
                    _cE("view", _uM("class" to "booking-section"), _uA(
                        _cV(_component_navigator, _uM("url" to "/pages/price/price"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("button", _uM("class" to _nC(_uA(
                                    "booking-button",
                                    _uM("disabled" to !isSelected.value)
                                )), "disabled" to !isSelected.value, "onClick" to bookScooter), " appoint ", 10, _uA(
                                    "disabled"
                                ))
                            )
                        }
                        ), "_" to 1))
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
                        _cV(_component_navigator, _uM("url" to "/pages/login/login", "class" to "bottom_right", "open-type" to "reLaunch"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
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
                return _uM("container" to _pS(_uM("width" to "100%", "height" to "100%", "paddingBottom" to "8%", "boxSizing" to "border-box", "backgroundColor" to "#f5f5f5")), "scooter-card" to _pS(_uM("width" to "90%", "marginTop" to 20, "marginRight" to "auto", "marginBottom" to 20, "marginLeft" to "auto", "paddingTop" to 15, "paddingRight" to 15, "paddingBottom" to 15, "paddingLeft" to 15, "backgroundColor" to "#FFFFFF", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "boxShadow" to "0 4px 12px rgba(0, 0, 0, 0.1)", "display" to "flex", "alignItems" to "center")), "scooter-image" to _uM(".scooter-card " to _uM("width" to 100, "height" to 100, "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "marginRight" to 15)), "card-content" to _uM(".scooter-card " to _uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%")), "scooter-id" to _uM(".scooter-card .card-content " to _uM("fontSize" to 18, "fontWeight" to "bold", "color" to "#333333")), "checkbox-container" to _uM(".scooter-card " to _uM("width" to 30, "height" to 30)), "checkbox" to _uM(".scooter-card .checkbox-container " to _uM("width" to "100%", "height" to "100%", "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#cccccc", "borderRightColor" to "#cccccc", "borderBottomColor" to "#cccccc", "borderLeftColor" to "#cccccc", "borderTopLeftRadius" to 4, "borderTopRightRadius" to 4, "borderBottomRightRadius" to 4, "borderBottomLeftRadius" to 4, "display" to "flex", "justifyContent" to "center", "alignItems" to "center", "backgroundColor" to "#FFFFFF"), ".scooter-card .checkbox-container .selected" to _uM("backgroundColor" to "#4CAF50", "borderTopColor" to "#4CAF50", "borderRightColor" to "#4CAF50", "borderBottomColor" to "#4CAF50", "borderLeftColor" to "#4CAF50")), "checkmark" to _uM(".scooter-card .checkbox-container .checkbox " to _uM("color" to "#FFFFFF", "fontSize" to 16, "fontWeight" to "bold")), "booking-section" to _pS(_uM("width" to "90%", "marginTop" to 20, "marginRight" to "auto", "marginBottom" to 20, "marginLeft" to "auto", "display" to "flex", "justifyContent" to "center")), "booking-button" to _uM(".booking-section " to _uM("width" to "100%", "height" to 45, "backgroundColor" to "#4CAF50", "color" to "#FFFFFF", "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 16, "fontWeight" to "bold"), ".booking-section .disabled" to _uM("backgroundColor" to "#cccccc", "color" to "#666666")), "bottom" to _pS(_uM("width" to "100%", "height" to "8%", "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "position" to "absolute", "bottom" to 0, "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0)), "bottom_left" to _uM(".bottom " to _uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginLeft" to "5%")), "icon2" to _uM(".bottom .bottom_left " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%"), ".bottom .bottom_right " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%")), "left_down" to _uM(".bottom .bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_center" to _uM(".bottom " to _uM("position" to "absolute", "left" to "40%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%")), "bike_icon" to _uM(".bottom .bottom_center " to _uM("position" to "absolute", "left" to "25%", "width" to "50%", "height" to "60%")), "center_down" to _uM(".bottom .bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_right" to _uM(".bottom " to _uM("position" to "absolute", "right" to "0%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginRight" to "5%")), "right_down" to _uM(".bottom .bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
