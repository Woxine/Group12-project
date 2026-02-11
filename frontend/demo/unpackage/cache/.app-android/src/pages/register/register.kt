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
open class GenPagesRegisterRegister : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesRegisterRegister) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesRegisterRegister
            val _cache = __ins.renderCache
            val userTypeOptions = ref(_uA<String>("customer", "staff", "admin"))
            val selectedUserType = ref<String>("")
            val showTypeSelector = ref<Boolean>(false)
            val isStudent = ref<Boolean>(false)
            val toggleTypeSelector = fun(): Unit {
                showTypeSelector.value = !showTypeSelector.value
            }
            val selectUserType = fun(type: String): Unit {
                selectedUserType.value = type
                showTypeSelector.value = false
            }
            val onStudentToggleChange = fun(e: Any): Unit {
                isStudent.value = (e as UTSJSONObject)["value"] as Boolean
            }
            return fun(): Any? {
                val _component_switch = resolveComponent("switch")
                val _component_navigator = resolveComponent("navigator")
                return _cE(Fragment, null, _uA(
                    _cE("view", _uM("class" to "back"), _uA(
                        _cE("image", _uM("src" to "/static/login_back1.jpg", "mode" to "", "class" to "backpic")),
                        _cE("view", _uM("class" to "login_title"), _uA(
                            _cE("text", _uM("class" to "title_text"), "register")
                        )),
                        _cE("view", _uM("class" to "login_container"), _uA(
                            _cE("input", _uM("type" to "text", "placeholder" to "please enter account", "confirm-type" to "next", "class" to "input")),
                            _cE("view", _uM("class" to "span1")),
                            _cE("input", _uM("type" to "text", "placeholder" to "please enter password", "class" to "input")),
                            _cE("view", _uM("class" to "span2")),
                            _cE("input", _uM("type" to "text", "placeholder" to "please check password", "class" to "input")),
                            _cE("view", _uM("class" to "span2")),
                            _cE("view", _uM("class" to "type-selector-container"), _uA(
                                _cE("view", _uM("class" to "selector-header", "onClick" to toggleTypeSelector), _uA(
                                    _cE("text", _uM("class" to "selector-text"), _tD(if (selectedUserType.value !== "") {
                                        selectedUserType.value
                                    } else {
                                        "select user type"
                                    }
                                    ), 1),
                                    _cE("text", _uM("class" to "arrow"), _tD(if (showTypeSelector.value) {
                                        "▲"
                                    } else {
                                        "▼"
                                    }
                                    ), 1)
                                )),
                                if (isTrue(showTypeSelector.value)) {
                                    _cE("view", _uM("key" to 0, "class" to "selector-dropdown"), _uA(
                                        _cE(Fragment, null, RenderHelpers.renderList(userTypeOptions.value, fun(type, index, __index, _cached): Any {
                                            return _cE("view", _uM("key" to index, "class" to "selector-option", "onClick" to fun(){
                                                selectUserType(type)
                                            }), _uA(
                                                _cE("text", _uM("class" to "selector-option"), _tD(type), 1)
                                            ), 8, _uA(
                                                "onClick"
                                            ))
                                        }), 128)
                                    ))
                                } else {
                                    _cC("v-if", true)
                                }
                            )),
                            _cE("view", _uM("class" to "span2")),
                            _cE("view", _uM("class" to "student-toggle"), _uA(
                                _cE("text", _uM("class" to "student-label"), "Are you a student?"),
                                _cV(_component_switch, _uM("checked" to isStudent.value, "onChange" to onStudentToggleChange, "color" to "#3c3c3c", "style" to _nS(_uM("transform" to "scale(0.7)"))), null, 8, _uA(
                                    "checked",
                                    "style"
                                ))
                            )),
                            _cE("view", _uM("class" to "span2")),
                            _cV(_component_navigator, _uM("url" to "/pages/register/register", "open-type" to "reLaunch"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                                return _uA(
                                    _cE("button", _uM("size" to "default", "style" to _nS(_uM("color" to "white", "background-color" to "rgba(60, 60, 60, 0.7)"))), "sign up", 4)
                                )
                            }
                            ), "_" to 1))
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
                return _uM("back" to _pS(_uM("width" to "100%", "height" to "100%")), "backpic" to _uM(".back " to _uM("width" to "100%", "height" to "100%")), "login_title" to _uM(".back " to _uM("position" to "absolute", "top" to 0, "left" to "50%", "transform" to "translateX(-50%)", "zIndex" to 10, "paddingTop" to 10, "paddingRight" to 10, "paddingBottom" to 10, "paddingLeft" to 10, "backgroundColor" to "rgba(0,0,0,0.3)")), "title_text" to _uM(".back .login_title " to _uM("height" to "100%", "fontSize" to 32, "fontWeight" to "bold", "color" to "#FFFFFF", "textShadow" to "1px 1px 3px rgba(0, 0, 0, 0.5)")), "login_container" to _uM(".back " to _uM("position" to "absolute", "top" to "50%", "left" to "50%", "transform" to "translate(-50%, -50%)", "width" to 300, "paddingTop" to 30, "paddingRight" to 30, "paddingBottom" to 30, "paddingLeft" to 30, "backgroundColor" to "rgba(227,225,225,0.46)", "borderTopLeftRadius" to 15, "borderTopRightRadius" to 15, "borderBottomRightRadius" to 15, "borderBottomLeftRadius" to 15, "boxShadow" to "0 10px 30px rgba(0, 0, 0, 0.3)")), "input" to _uM(".back .login_container " to _uM("width" to "100%", "height" to "10%", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "borderTopWidth" to "medium", "borderRightWidth" to "medium", "borderBottomWidth" to "medium", "borderLeftWidth" to "medium", "borderTopStyle" to "none", "borderRightStyle" to "none", "borderBottomStyle" to "none", "borderLeftStyle" to "none", "borderTopColor" to "#000000", "borderRightColor" to "#000000", "borderBottomColor" to "#000000", "borderLeftColor" to "#000000", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "backgroundColor" to "rgba(60,60,60,0.7)", "color" to "#FFFFFF", "fontSize" to 16)), "span1" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10, "backgroundColor" to "#00FFFF")), "span2" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10, "backgroundColor" to "#00FFFF")), "span3" to _uM(".back .login_container " to _uM("width" to "100%", "height" to 10, "backgroundColor" to "#00FFFF")), "type-selector-container" to _uM(".back .login_container " to _uM("width" to "100%", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "backgroundColor" to "rgba(60,60,60,0.7)", "color" to "#FFFFFF", "fontSize" to 16, "position" to "relative")), "selector-header" to _uM(".back .login_container .type-selector-container " to _uM("width" to "100%", "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 20, "paddingLeft" to 20, "display" to "flex", "justifyContent" to "space-between", "alignItems" to "center")), "selector-text" to _uM(".back .login_container .type-selector-container .selector-header " to _uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "color" to "#FFFFFF")), "arrow" to _uM(".back .login_container .type-selector-container .selector-header " to _uM("color" to "#FFFFFF", "fontSize" to 12)), "selector-dropdown" to _uM(".back .login_container .type-selector-container " to _uM("width" to "100%", "maxHeight" to "150rpx", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "backgroundColor" to "rgba(60,60,60,0.9)", "zIndex" to 100)), "selector-option" to _uM(".back .login_container .type-selector-container .selector-dropdown " to _uM("width" to "100%", "paddingTop" to 1.5, "paddingRight" to 1.5, "paddingBottom" to 1.5, "paddingLeft" to 1.5, "color" to "#FFFFFF", "backgroundColor" to "rgba(60,60,60,0.7)", "borderBottomWidth:last-child" to "medium", "borderBottomStyle:last-child" to "none", "borderBottomColor:last-child" to "#000000")), "student-toggle" to _uM(".back .login_container " to _uM("width" to "100%", "paddingTop" to "20rpx", "paddingRight" to "20rpx", "paddingBottom" to "20rpx", "paddingLeft" to "20rpx", "display" to "flex", "justifyContent" to "space-between", "alignItems" to "center", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "backgroundColor" to "rgba(60,60,60,0.7)", "color" to "#FFFFFF", "fontSize" to 16)), "student-label" to _uM(".back .login_container .student-toggle " to _uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "color" to "#FFFFFF")), "bottom" to _pS(_uM("width" to "100%", "height" to "8%", "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "position" to "absolute", "bottom" to 0, "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0)), "bottom_left" to _uM(".bottom " to _uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginLeft" to "5%")), "icon2" to _uM(".bottom .bottom_left " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%"), ".bottom .bottom_right " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%")), "left_down" to _uM(".bottom .bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_center" to _uM(".bottom " to _uM("position" to "absolute", "left" to "40%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%")), "bike_icon" to _uM(".bottom .bottom_center " to _uM("position" to "absolute", "left" to "25%", "width" to "50%", "height" to "60%")), "center_down" to _uM(".bottom .bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_right" to _uM(".bottom " to _uM("position" to "absolute", "right" to "0%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginRight" to "5%")), "right_down" to _uM(".bottom .bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
