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
import io.dcloud.uniapp.extapi.getLocation as uni_getLocation
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesIndexIndex : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesIndexIndex) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesIndexIndex
            val _cache = __ins.renderCache
            val longitude = ref(116.392325)
            val latitude = ref(40.01811)
            val scale = ref(16)
            val markers = ref(_uA<Marker>())
            val mapContext = ref(null as MapContext?)
            val getCurrentLocation = fun(){
                uni_getLocation(GetLocationOptions(type = "gcj02", success = fun(res){
                    val lng = res.longitude
                    val lat = res.latitude
                    longitude.value = lng
                    latitude.value = lat
                    val temp = UTSAndroid.consoleDebugError(JSON.parse<UTSArray<Marker>>(JSON.stringify(_uA(
                        object : UTSJSONObject() {
                            var id: Number = 1
                            var longitude = lng
                            var latitude = lat
                            var title = "我的位置"
                            var iconPath = "/static/location.png"
                            var width: Number = 32
                            var height: Number = 32
                            var anchor = object : UTSJSONObject() {
                                var x: Number = 0.5
                                var y: Number = 1
                            }
                        }
                    ))), " at pages/index/index.uvue:61")!!
                    markers.value = temp
                    mapContext.value?.moveToLocation(MapContextMoveToLocationOptions(latitude = lat, longitude = lng))
                }
                , fail = fun(err){
                    console.error("定位失败", err, " at pages/index/index.uvue:75")
                    uni_showToast(ShowToastOptions(title = "定位失败，请开启权限", icon = "none"))
                }
                ))
            }
            onMounted(fun(){
                val instance = getCurrentInstance()
                if (instance != null && instance.proxy != null) {
                    mapContext.value = uni_createMapContext("mainMap", instance.proxy!!)
                } else {
                    mapContext.value = uni_createMapContext("mainMap", null)
                }
                getCurrentLocation()
            }
            )
            return fun(): Any? {
                val _component_map = resolveComponent("map")
                val _component_navigator = resolveComponent("navigator")
                return _cE(Fragment, null, _uA(
                    _cE("view", _uM("class" to "slogan1"), _uA(
                        _cE("text", _uM("class" to "text1"), "We offer you a variety of options.")
                    )),
                    _cE("view", _uM("class" to "span1"), _uA(
                        _cE("view", _uM("class" to "span1_left"), _uA(
                            _cE("image", _uM("src" to "/static/broadcast.png", "mode" to "aspectFill", "class" to "icon1"))
                        )),
                        _cE("swiper", _uM("class" to "swiper1", "vertical" to "true", "autoplay" to "true", "circular" to "", "interval" to "2000", "duration" to "1000"), _uA(
                            _cE("swiper-item", null, _uA(
                                _cE("text", _uM("class" to "text2"), "Before cycling, please check the brakes, seat, pedals and other components.")
                            )),
                            _cE("swiper-item", null, _uA(
                                _cE("text", _uM("class" to "text2"), "Remember to return the bike when the ride is over.")
                            ))
                        ))
                    )),
                    _cE("swiper", _uM("class" to "swiper2", "indicator-dots" to true, "autoplay" to true, "interval" to 2000, "duration" to 1000, "indicator-color" to "rgba(255,255,255,0.3)", "indicator-active-color" to "#c0c0c0", "circular" to ""), _uA(
                        _cE("swiper-item", null, _uA(
                            _cE("image", _uM("src" to "/static/MeiTuanbike.jpg", "class" to "swipic1"))
                        )),
                        _cE("swiper-item", null, _uA(
                            _cE("image", _uM("src" to "/static/HelloBike.jpg", "class" to "swipic2"))
                        )),
                        _cE("swiper-item", null, _uA(
                            _cE("image", _uM("src" to "/static/Scooter.jpg", "class" to "swipic3"))
                        )),
                        _cE("swiper-item", null, _uA(
                            _cE("image", _uM("src" to "/static/EleBike.jpg", "class" to "swipic4"))
                        ))
                    )),
                    _cE("view", _uM("class" to "span2"), "------"),
                    _cE("view", _uM("class" to "map_container"), _uA(
                        _cV(_component_map, _uM("id" to "mainMap", "class" to "map", "longitude" to longitude.value, "latitude" to latitude.value, "scale" to scale.value, "markers" to markers.value, "show-location" to ""), null, 8, _uA(
                            "longitude",
                            "latitude",
                            "scale",
                            "markers"
                        ))
                    )),
                    _cE("view", _uM("class" to "bottom"), _uA(
                        _cE("view", _uM("class" to "bottom_left"), _uA(
                            _cE("image", _uM("src" to "/static/home.png", "mode" to "aspectFit", "class" to "icon2")),
                            _cE("view", _uM("class" to "left_down"), _uA(
                                _cE("text", _uM("class" to "text3"), "Home")
                            ))
                        )),
                        _cV(_component_navigator, _uM("url" to "/pages/bikelist/bikelist", "open-type" to "reLaunch", "hover-class" to "navigator-hover", "class" to "bottom_center"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("image", _uM("src" to "/static/bike.png", "mode" to "aspectFit", "class" to "bike_icon")),
                                _cE("view", _uM("class" to "center_down"), _uA(
                                    _cE("text", _uM("class" to "text3"), "Bike")
                                ))
                            )
                        }
                        ), "_" to 1)),
                        _cV(_component_navigator, _uM("url" to "/pages/login/login", "open-type" to "reLaunch", "hover-class" to "navigator-hover", "class" to "bottom_right"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("image", _uM("src" to "/static/me.png", "mode" to "aspectFit", "class" to "icon2")),
                                _cE("view", _uM("class" to "right_down"), _uA(
                                    _cE("text", _uM("class" to "text3"), "Me")
                                ))
                            )
                        }
                        ), "_" to 1))
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
                return _uM("slogan1" to _pS(_uM("display" to "flex", "alignItems" to "center", "height" to 20)), "span1" to _pS(_uM("width" to "100%", "height" to 20)), "span1_left" to _uM(".span1 " to _uM("position" to "absolute", "left" to "1%", "height" to "100%", "width" to "5%", "backgroundColor" to "#F0F8FF")), "icon1" to _uM(".span1 .span1_left " to _uM("width" to "100%", "height" to "100%")), "swiper1" to _uM(".span1 " to _uM("position" to "absolute", "left" to "6%", "height" to "100%", "width" to "94%")), "text2" to _uM(".span1 .swiper1 " to _uM("marginTop" to "auto", "marginRight" to "auto", "marginBottom" to "auto", "marginLeft" to "auto", "fontSize" to 9)), "swiper2" to _pS(_uM("width" to "100%", "height" to 200)), "swipic1" to _uM(".swiper2 " to _uM("width" to "100%")), "swipic2" to _uM(".swiper2 " to _uM("width" to "100%")), "swipic3" to _uM(".swiper2 " to _uM("width" to "100%")), "swipic4" to _uM(".swiper2 " to _uM("width" to "100%")), "map" to _pS(_uM("width" to "100%", "height" to 300)), "bottom" to _pS(_uM("width" to "100%", "height" to "8%", "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "position" to "absolute", "bottom" to 0, "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0)), "bottom_left" to _uM(".bottom " to _uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginLeft" to "5%")), "icon2" to _uM(".bottom .bottom_left " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%"), ".bottom .bottom_right " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%")), "left_down" to _uM(".bottom .bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_center" to _uM(".bottom " to _uM("position" to "absolute", "left" to "40%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%")), "bike_icon" to _uM(".bottom .bottom_center " to _uM("position" to "absolute", "left" to "25%", "width" to "50%", "height" to "60%")), "center_down" to _uM(".bottom .bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_right" to _uM(".bottom " to _uM("position" to "absolute", "right" to "0%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginRight" to "5%")), "right_down" to _uM(".bottom .bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
