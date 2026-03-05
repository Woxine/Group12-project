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
import io.dcloud.uniapp.extapi.connectSocket as uni_connectSocket
import io.dcloud.uniapp.extapi.exit as uni_exit
import io.dcloud.uniapp.extapi.showToast as uni_showToast
val runBlock1 = run {
    __uniConfig.getAppStyles = fun(): Map<String, Map<String, Map<String, Any>>> {
        return GenApp.styles
    }
}
fun initRuntimeSocket(hosts: String, port: String, id: String): UTSPromise<SocketTask?> {
    if (hosts == "" || port == "" || id == "") {
        return UTSPromise.resolve(null)
    }
    return hosts.split(",").reduce<UTSPromise<SocketTask?>>(fun(promise: UTSPromise<SocketTask?>, host: String): UTSPromise<SocketTask?> {
        return promise.then(fun(socket): UTSPromise<SocketTask?> {
            if (socket != null) {
                return UTSPromise.resolve(socket)
            }
            return tryConnectSocket(host, port, id)
        }
        )
    }
    , UTSPromise.resolve(null))
}
val SOCKET_TIMEOUT: Number = 500
fun tryConnectSocket(host: String, port: String, id: String): UTSPromise<SocketTask?> {
    return UTSPromise(fun(resolve, reject){
        val socket = uni_connectSocket(ConnectSocketOptions(url = "ws://" + host + ":" + port + "/" + id, fail = fun(_) {
            resolve(null)
        }
        ))
        val timer = setTimeout(fun(){
            socket.close(CloseSocketOptions(code = 1006, reason = "connect timeout"))
            resolve(null)
        }
        , SOCKET_TIMEOUT)
        socket.onOpen(fun(e){
            clearTimeout(timer)
            resolve(socket)
        }
        )
        socket.onClose(fun(e){
            clearTimeout(timer)
            resolve(null)
        }
        )
        socket.onError(fun(e){
            clearTimeout(timer)
            resolve(null)
        }
        )
    }
    )
}
fun initRuntimeSocketService(): UTSPromise<Boolean> {
    val hosts: String = "10.69.22.172,192.168.133.1,192.168.30.1,192.168.31.214,127.0.0.1"
    val port: String = "8090"
    val id: String = "app-android_zPOv8G"
    if (hosts == "" || port == "" || id == "") {
        return UTSPromise.resolve(false)
    }
    var socketTask: SocketTask? = null
    __registerWebViewUniConsole(fun(): String {
        return "!function(){\"use strict\";\"function\"==typeof SuppressedError&&SuppressedError;var e=[\"log\",\"warn\",\"error\",\"info\",\"debug\"],n=e.reduce((function(e,n){return e[n]=console[n].bind(console),e}),{}),t=null,r=new Set,o={};function i(e){if(null!=t){var n=e.map((function(e){if(\"string\"==typeof e)return e;var n=e&&\"promise\"in e&&\"reason\"in e,t=n?\"UnhandledPromiseRejection: \":\"\";if(n&&(e=e.reason),e instanceof Error&&e.stack)return e.message&&!e.stack.includes(e.message)?\"\".concat(t).concat(e.message,\"\\n\").concat(e.stack):\"\".concat(t).concat(e.stack);if(\"object\"==typeof e&&null!==e)try{return t+JSON.stringify(e)}catch(e){return t+String(e)}return t+String(e)})).filter(Boolean);n.length>0&&t(JSON.stringify(Object.assign({type:\"error\",data:n},o)))}else e.forEach((function(e){r.add(e)}))}function a(e,n){try{return{type:e,args:u(n)}}catch(e){}return{type:e,args:[]}}function u(e){return e.map((function(e){return c(e)}))}function c(e,n){if(void 0===n&&(n=0),n>=7)return{type:\"object\",value:\"[Maximum depth reached]\"};switch(typeof e){case\"string\":return{type:\"string\",value:e};case\"number\":return function(e){return{type:\"number\",value:String(e)}}(e);case\"boolean\":return function(e){return{type:\"boolean\",value:String(e)}}(e);case\"object\":try{return function(e,n){if(null===e)return{type:\"null\"};if(function(e){return e.\$&&s(e.\$)}(e))return function(e,n){return{type:\"object\",className:\"ComponentPublicInstance\",value:{properties:Object.entries(e.\$.type).map((function(e){return f(e[0],e[1],n+1)}))}}}(e,n);if(s(e))return function(e,n){return{type:\"object\",className:\"ComponentInternalInstance\",value:{properties:Object.entries(e.type).map((function(e){return f(e[0],e[1],n+1)}))}}}(e,n);if(function(e){return e.style&&null!=e.tagName&&null!=e.nodeName}(e))return function(e,n){return{type:\"object\",value:{properties:Object.entries(e).filter((function(e){var n=e[0];return[\"id\",\"tagName\",\"nodeName\",\"dataset\",\"offsetTop\",\"offsetLeft\",\"style\"].includes(n)})).map((function(e){return f(e[0],e[1],n+1)}))}}}(e,n);if(function(e){return\"function\"==typeof e.getPropertyValue&&\"function\"==typeof e.setProperty&&e.\$styles}(e))return function(e,n){return{type:\"object\",value:{properties:Object.entries(e.\$styles).map((function(e){return f(e[0],e[1],n+1)}))}}}(e,n);if(Array.isArray(e))return{type:\"object\",subType:\"array\",value:{properties:e.map((function(e,t){return function(e,n,t){var r=c(e,t);return r.name=\"\".concat(n),r}(e,t,n+1)}))}};if(e instanceof Set)return{type:\"object\",subType:\"set\",className:\"Set\",description:\"Set(\".concat(e.size,\")\"),value:{entries:Array.from(e).map((function(e){return function(e,n){return{value:c(e,n)}}(e,n+1)}))}};if(e instanceof Map)return{type:\"object\",subType:\"map\",className:\"Map\",description:\"Map(\".concat(e.size,\")\"),value:{entries:Array.from(e.entries()).map((function(e){return function(e,n){return{key:c(e[0],n),value:c(e[1],n)}}(e,n+1)}))}};if(e instanceof Promise)return{type:\"object\",subType:\"promise\",value:{properties:[]}};if(e instanceof RegExp)return{type:\"object\",subType:\"regexp\",value:String(e),className:\"Regexp\"};if(e instanceof Date)return{type:\"object\",subType:\"date\",value:String(e),className:\"Date\"};if(e instanceof Error)return{type:\"object\",subType:\"error\",value:e.message||String(e),className:e.name||\"Error\"};var t=void 0,r=e.constructor;r&&r.get\$UTSMetadata\$&&(t=r.get\$UTSMetadata\$().name);var o=Object.entries(e);(function(e){return e.modifier&&e.modifier._attribute&&e.nodeContent})(e)&&(o=o.filter((function(e){var n=e[0];return\"modifier\"!==n&&\"nodeContent\"!==n})));return{type:\"object\",className:t,value:{properties:o.map((function(e){return f(e[0],e[1],n+1)}))}}}(e,n)}catch(e){return{type:\"object\",value:{properties:[]}}}case\"undefined\":return{type:\"undefined\"};case\"function\":return function(e){return{type:\"function\",value:\"function \".concat(e.name,\"() {}\")}}(e);case\"symbol\":return function(e){return{type:\"symbol\",value:e.description}}(e);case\"bigint\":return function(e){return{type:\"bigint\",value:String(e)}}(e)}}function s(e){return e.type&&null!=e.uid&&e.appContext}function f(e,n,t){var r=c(n,t);return r.name=e,r}var l=null,p=[],y={},g=\"---BEGIN:EXCEPTION---\",d=\"---END:EXCEPTION---\";function v(e){null!=l?l(JSON.stringify(Object.assign({type:\"console\",data:e},y))):p.push.apply(p,e)}var m=/^\\s*at\\s+[\\w/./-]+:\\d+\$/;function b(){function t(e){return function(){for(var t=[],r=0;r<arguments.length;r++)t[r]=arguments[r];var o=function(e,n,t){if(t||2===arguments.length)for(var r,o=0,i=n.length;o<i;o++)!r&&o in n||(r||(r=Array.prototype.slice.call(n,0,o)),r[o]=n[o]);return e.concat(r||Array.prototype.slice.call(n))}([],t,!0);if(o.length){var u=o[o.length-1];\"string\"==typeof u&&m.test(u)&&o.pop()}if(n[e].apply(n,o),\"error\"===e&&1===t.length){var c=t[0];if(\"string\"==typeof c&&c.startsWith(g)){var s=g.length,f=c.length-d.length;return void i([c.slice(s,f)])}if(c instanceof Error)return void i([c])}v([a(e,t)])}}return function(){var e=console.log,n=Symbol();try{console.log=n}catch(e){return!1}var t=console.log===n;return console.log=e,t}()?(e.forEach((function(e){console[e]=t(e)})),function(){e.forEach((function(e){console[e]=n[e]}))}):function(){}}function _(e){var n={type:\"WEB_INVOKE_APPSERVICE\",args:{data:{name:\"console\",arg:e}}};return window.__uniapp_x_postMessageToService?window.__uniapp_x_postMessageToService(n):window.__uniapp_x_.postMessageToService(JSON.stringify(n))}!function(){if(!window.__UNI_CONSOLE_WEBVIEW__){window.__UNI_CONSOLE_WEBVIEW__=!0;var e=\"[web-view]\".concat(window.__UNI_PAGE_ROUTE__?\"[\".concat(window.__UNI_PAGE_ROUTE__,\"]\"):\"\");b(),function(e,n){if(void 0===n&&(n={}),l=e,Object.assign(y,n),null!=e&&p.length>0){var t=p.slice();p.length=0,v(t)}}((function(e){_(e)}),{channel:e}),function(e,n){if(void 0===n&&(n={}),t=e,Object.assign(o,n),null!=e&&r.size>0){var a=Array.from(r);r.clear(),i(a)}}((function(e){_(e)}),{channel:e}),window.addEventListener(\"error\",(function(e){i([e.error])})),window.addEventListener(\"unhandledrejection\",(function(e){i([e])}))}}()}();"
    }
    , fun(data: String){
        socketTask?.send(SendSocketMessageOptions(data = data))
    }
    )
    return UTSPromise.resolve().then(fun(): UTSPromise<Boolean> {
        return initRuntimeSocket(hosts, port, id).then(fun(socket): Boolean {
            if (socket == null) {
                return false
            }
            socketTask = socket
            return true
        }
        )
    }
    ).`catch`(fun(): Boolean {
        return false
    }
    )
}
val runBlock2 = run {
    initRuntimeSocketService()
}
var firstBackTime: Number = 0
open class GenApp : BaseApp {
    constructor(__ins: ComponentInternalInstance) : super(__ins) {
        onLaunch(fun(_: OnLaunchOptions) {
            console.log("App Launch", " at App.uvue:7")
        }
        , __ins)
        onAppShow(fun(_: OnShowOptions) {
            console.log("App Show", " at App.uvue:10")
        }
        , __ins)
        onAppHide(fun() {
            console.log("App Hide", " at App.uvue:13")
        }
        , __ins)
        onLastPageBackPress(fun() {
            console.log("App LastPageBackPress", " at App.uvue:17")
            if (firstBackTime == 0) {
                uni_showToast(ShowToastOptions(title = "再按一次退出应用", position = "bottom"))
                firstBackTime = Date.now()
                setTimeout(fun(){
                    firstBackTime = 0
                }, 2000)
            } else if (Date.now() - firstBackTime < 2000) {
                firstBackTime = Date.now()
                uni_exit(null)
            }
        }
        , __ins)
        onExit(fun() {
            console.log("App Exit", " at App.uvue:34")
        }
        , __ins)
    }
    companion object {
        val styles: Map<String, Map<String, Map<String, Any>>> by lazy {
            _nCS(_uA(
                styles0
            ))
        }
        val styles0: Map<String, Map<String, Map<String, Any>>>
            get() {
                return _uM("uni-row" to _pS(_uM("flexDirection" to "row")), "uni-column" to _pS(_uM("flexDirection" to "column")))
            }
    }
}
val GenAppClass = CreateVueAppComponent(GenApp::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "app", name = "", inheritAttrs = true, inject = Map(), props = Map(), propsNeedCastKeys = _uA(), emits = Map(), components = Map(), styles = GenApp.styles)
}
, fun(instance): GenApp {
    return GenApp(instance)
}
)
open class AnchorPoint (
    @JsonNotNull
    open var x: Number,
    @JsonNotNull
    open var y: Number,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("AnchorPoint", "pages/index/index.uvue", 58, 7)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return AnchorPointReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class AnchorPointReactiveObject : AnchorPoint, IUTSReactive<AnchorPoint> {
    override var __v_raw: AnchorPoint
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: AnchorPoint, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(x = __v_raw.x, y = __v_raw.y) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): AnchorPointReactiveObject {
        return AnchorPointReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var x: Number
        get() {
            return _tRG(__v_raw, "x", __v_raw.x, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("x")) {
                return
            }
            val oldValue = __v_raw.x
            __v_raw.x = value
            _tRS(__v_raw, "x", oldValue, value)
        }
    override var y: Number
        get() {
            return _tRG(__v_raw, "y", __v_raw.y, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("y")) {
                return
            }
            val oldValue = __v_raw.y
            __v_raw.y = value
            _tRS(__v_raw, "y", oldValue, value)
        }
}
open class Marker (
    @JsonNotNull
    open var id: Number,
    @JsonNotNull
    open var longitude: Number,
    @JsonNotNull
    open var latitude: Number,
    @JsonNotNull
    open var title: String,
    @JsonNotNull
    open var iconPath: String,
    @JsonNotNull
    open var width: Number,
    @JsonNotNull
    open var height: Number,
    @JsonNotNull
    open var anchor: AnchorPoint,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("Marker", "pages/index/index.uvue", 63, 7)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return MarkerReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class MarkerReactiveObject : Marker, IUTSReactive<Marker> {
    override var __v_raw: Marker
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: Marker, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(id = __v_raw.id, longitude = __v_raw.longitude, latitude = __v_raw.latitude, title = __v_raw.title, iconPath = __v_raw.iconPath, width = __v_raw.width, height = __v_raw.height, anchor = __v_raw.anchor) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): MarkerReactiveObject {
        return MarkerReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var id: Number
        get() {
            return _tRG(__v_raw, "id", __v_raw.id, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("id")) {
                return
            }
            val oldValue = __v_raw.id
            __v_raw.id = value
            _tRS(__v_raw, "id", oldValue, value)
        }
    override var longitude: Number
        get() {
            return _tRG(__v_raw, "longitude", __v_raw.longitude, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("longitude")) {
                return
            }
            val oldValue = __v_raw.longitude
            __v_raw.longitude = value
            _tRS(__v_raw, "longitude", oldValue, value)
        }
    override var latitude: Number
        get() {
            return _tRG(__v_raw, "latitude", __v_raw.latitude, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("latitude")) {
                return
            }
            val oldValue = __v_raw.latitude
            __v_raw.latitude = value
            _tRS(__v_raw, "latitude", oldValue, value)
        }
    override var title: String
        get() {
            return _tRG(__v_raw, "title", __v_raw.title, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("title")) {
                return
            }
            val oldValue = __v_raw.title
            __v_raw.title = value
            _tRS(__v_raw, "title", oldValue, value)
        }
    override var iconPath: String
        get() {
            return _tRG(__v_raw, "iconPath", __v_raw.iconPath, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("iconPath")) {
                return
            }
            val oldValue = __v_raw.iconPath
            __v_raw.iconPath = value
            _tRS(__v_raw, "iconPath", oldValue, value)
        }
    override var width: Number
        get() {
            return _tRG(__v_raw, "width", __v_raw.width, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("width")) {
                return
            }
            val oldValue = __v_raw.width
            __v_raw.width = value
            _tRS(__v_raw, "width", oldValue, value)
        }
    override var height: Number
        get() {
            return _tRG(__v_raw, "height", __v_raw.height, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("height")) {
                return
            }
            val oldValue = __v_raw.height
            __v_raw.height = value
            _tRS(__v_raw, "height", oldValue, value)
        }
    override var anchor: AnchorPoint
        get() {
            return _tRG(__v_raw, "anchor", __v_raw.anchor, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("anchor")) {
                return
            }
            val oldValue = __v_raw.anchor
            __v_raw.anchor = value
            _tRS(__v_raw, "anchor", oldValue, value)
        }
}
typealias MapContext = Any
val GenPagesIndexIndexClass = CreateVueComponent(GenPagesIndexIndex::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesIndexIndex.inheritAttrs, inject = GenPagesIndexIndex.inject, props = GenPagesIndexIndex.props, propsNeedCastKeys = GenPagesIndexIndex.propsNeedCastKeys, emits = GenPagesIndexIndex.emits, components = GenPagesIndexIndex.components, styles = GenPagesIndexIndex.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesIndexIndex.setup(props as GenPagesIndexIndex)
    }
    )
}
, fun(instance, renderer): GenPagesIndexIndex {
    return GenPagesIndexIndex(instance, renderer)
}
)
val BASE_URL = "http://192.168.31.214:8080"
open class Scooter (
    @JsonNotNull
    open var id: String,
    @JsonNotNull
    open var rented: Boolean = false,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("Scooter", "pages/bikelist/bikelist.uvue", 66, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return ScooterReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class ScooterReactiveObject : Scooter, IUTSReactive<Scooter> {
    override var __v_raw: Scooter
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: Scooter, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(id = __v_raw.id, rented = __v_raw.rented) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): ScooterReactiveObject {
        return ScooterReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var id: String
        get() {
            return _tRG(__v_raw, "id", __v_raw.id, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("id")) {
                return
            }
            val oldValue = __v_raw.id
            __v_raw.id = value
            _tRS(__v_raw, "id", oldValue, value)
        }
    override var rented: Boolean
        get() {
            return _tRG(__v_raw, "rented", __v_raw.rented, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("rented")) {
                return
            }
            val oldValue = __v_raw.rented
            __v_raw.rented = value
            _tRS(__v_raw, "rented", oldValue, value)
        }
}
val GenPagesBikelistBikelistClass = CreateVueComponent(GenPagesBikelistBikelist::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesBikelistBikelist.inheritAttrs, inject = GenPagesBikelistBikelist.inject, props = GenPagesBikelistBikelist.props, propsNeedCastKeys = GenPagesBikelistBikelist.propsNeedCastKeys, emits = GenPagesBikelistBikelist.emits, components = GenPagesBikelistBikelist.components, styles = GenPagesBikelistBikelist.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesBikelistBikelist.setup(props as GenPagesBikelistBikelist)
    }
    )
}
, fun(instance, renderer): GenPagesBikelistBikelist {
    return GenPagesBikelistBikelist(instance, renderer)
}
)
open class UserInfo (
    @JsonNotNull
    open var email: String,
    @JsonNotNull
    open var password: String,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("UserInfo", "pages/login/login.uvue", 63, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return UserInfoReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class UserInfoReactiveObject : UserInfo, IUTSReactive<UserInfo> {
    override var __v_raw: UserInfo
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: UserInfo, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(email = __v_raw.email, password = __v_raw.password) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UserInfoReactiveObject {
        return UserInfoReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var email: String
        get() {
            return _tRG(__v_raw, "email", __v_raw.email, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("email")) {
                return
            }
            val oldValue = __v_raw.email
            __v_raw.email = value
            _tRS(__v_raw, "email", oldValue, value)
        }
    override var password: String
        get() {
            return _tRG(__v_raw, "password", __v_raw.password, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("password")) {
                return
            }
            val oldValue = __v_raw.password
            __v_raw.password = value
            _tRS(__v_raw, "password", oldValue, value)
        }
}
val GenPagesLoginLoginClass = CreateVueComponent(GenPagesLoginLogin::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesLoginLogin.inheritAttrs, inject = GenPagesLoginLogin.inject, props = GenPagesLoginLogin.props, propsNeedCastKeys = GenPagesLoginLogin.propsNeedCastKeys, emits = GenPagesLoginLogin.emits, components = GenPagesLoginLogin.components, styles = GenPagesLoginLogin.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesLoginLogin.setup(props as GenPagesLoginLogin)
    }
    )
}
, fun(instance, renderer): GenPagesLoginLogin {
    return GenPagesLoginLogin(instance, renderer)
}
)
open class FormData (
    @JsonNotNull
    open var name: String,
    @JsonNotNull
    open var email: String,
    @JsonNotNull
    open var password: String,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("FormData", "pages/register/register.uvue", 79, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return FormDataReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class FormDataReactiveObject : FormData, IUTSReactive<FormData> {
    override var __v_raw: FormData
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: FormData, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(name = __v_raw.name, email = __v_raw.email, password = __v_raw.password) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): FormDataReactiveObject {
        return FormDataReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var name: String
        get() {
            return _tRG(__v_raw, "name", __v_raw.name, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("name")) {
                return
            }
            val oldValue = __v_raw.name
            __v_raw.name = value
            _tRS(__v_raw, "name", oldValue, value)
        }
    override var email: String
        get() {
            return _tRG(__v_raw, "email", __v_raw.email, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("email")) {
                return
            }
            val oldValue = __v_raw.email
            __v_raw.email = value
            _tRS(__v_raw, "email", oldValue, value)
        }
    override var password: String
        get() {
            return _tRG(__v_raw, "password", __v_raw.password, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("password")) {
                return
            }
            val oldValue = __v_raw.password
            __v_raw.password = value
            _tRS(__v_raw, "password", oldValue, value)
        }
}
val GenPagesRegisterRegisterClass = CreateVueComponent(GenPagesRegisterRegister::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesRegisterRegister.inheritAttrs, inject = GenPagesRegisterRegister.inject, props = GenPagesRegisterRegister.props, propsNeedCastKeys = GenPagesRegisterRegister.propsNeedCastKeys, emits = GenPagesRegisterRegister.emits, components = GenPagesRegisterRegister.components, styles = GenPagesRegisterRegister.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesRegisterRegister.setup(props as GenPagesRegisterRegister)
    }
    )
}
, fun(instance, renderer): GenPagesRegisterRegister {
    return GenPagesRegisterRegister(instance, renderer)
}
)
open class DurationItem (
    @JsonNotNull
    open var time: String,
    @JsonNotNull
    open var price: Number,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("DurationItem", "pages/price/price.uvue", 58, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return DurationItemReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class DurationItemReactiveObject : DurationItem, IUTSReactive<DurationItem> {
    override var __v_raw: DurationItem
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: DurationItem, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(time = __v_raw.time, price = __v_raw.price) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): DurationItemReactiveObject {
        return DurationItemReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var time: String
        get() {
            return _tRG(__v_raw, "time", __v_raw.time, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("time")) {
                return
            }
            val oldValue = __v_raw.time
            __v_raw.time = value
            _tRS(__v_raw, "time", oldValue, value)
        }
    override var price: Number
        get() {
            return _tRG(__v_raw, "price", __v_raw.price, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("price")) {
                return
            }
            val oldValue = __v_raw.price
            __v_raw.price = value
            _tRS(__v_raw, "price", oldValue, value)
        }
}
open class UserInfo__1 (
    @JsonNotNull
    open var name: String,
    @JsonNotNull
    open var email: String,
) : UTSObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("UserInfo", "pages/price/price.uvue", 63, 6)
    }
}
val GenPagesPricePriceClass = CreateVueComponent(GenPagesPricePrice::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesPricePrice.inheritAttrs, inject = GenPagesPricePrice.inject, props = GenPagesPricePrice.props, propsNeedCastKeys = GenPagesPricePrice.propsNeedCastKeys, emits = GenPagesPricePrice.emits, components = GenPagesPricePrice.components, styles = GenPagesPricePrice.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesPricePrice.setup(props as GenPagesPricePrice)
    }
    )
}
, fun(instance, renderer): GenPagesPricePrice {
    return GenPagesPricePrice(instance, renderer)
}
)
open class Order (
    @JsonNotNull
    open var id: String,
    @JsonNotNull
    open var bikeId: String,
    @JsonNotNull
    open var duration: String,
    @JsonNotNull
    open var price: Number,
    @JsonNotNull
    open var date: String,
    @JsonNotNull
    open var status: String,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("Order", "pages/me/me.uvue", 79, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return OrderReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class OrderReactiveObject : Order, IUTSReactive<Order> {
    override var __v_raw: Order
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: Order, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(id = __v_raw.id, bikeId = __v_raw.bikeId, duration = __v_raw.duration, price = __v_raw.price, date = __v_raw.date, status = __v_raw.status) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): OrderReactiveObject {
        return OrderReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var id: String
        get() {
            return _tRG(__v_raw, "id", __v_raw.id, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("id")) {
                return
            }
            val oldValue = __v_raw.id
            __v_raw.id = value
            _tRS(__v_raw, "id", oldValue, value)
        }
    override var bikeId: String
        get() {
            return _tRG(__v_raw, "bikeId", __v_raw.bikeId, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("bikeId")) {
                return
            }
            val oldValue = __v_raw.bikeId
            __v_raw.bikeId = value
            _tRS(__v_raw, "bikeId", oldValue, value)
        }
    override var duration: String
        get() {
            return _tRG(__v_raw, "duration", __v_raw.duration, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("duration")) {
                return
            }
            val oldValue = __v_raw.duration
            __v_raw.duration = value
            _tRS(__v_raw, "duration", oldValue, value)
        }
    override var price: Number
        get() {
            return _tRG(__v_raw, "price", __v_raw.price, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("price")) {
                return
            }
            val oldValue = __v_raw.price
            __v_raw.price = value
            _tRS(__v_raw, "price", oldValue, value)
        }
    override var date: String
        get() {
            return _tRG(__v_raw, "date", __v_raw.date, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("date")) {
                return
            }
            val oldValue = __v_raw.date
            __v_raw.date = value
            _tRS(__v_raw, "date", oldValue, value)
        }
    override var status: String
        get() {
            return _tRG(__v_raw, "status", __v_raw.status, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("status")) {
                return
            }
            val oldValue = __v_raw.status
            __v_raw.status = value
            _tRS(__v_raw, "status", oldValue, value)
        }
}
open class UserInfo__2 (
    @JsonNotNull
    open var name: String,
    @JsonNotNull
    open var email: String,
) : UTSObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("UserInfo", "pages/me/me.uvue", 88, 6)
    }
}
val GenPagesMeMeClass = CreateVueComponent(GenPagesMeMe::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesMeMe.inheritAttrs, inject = GenPagesMeMe.inject, props = GenPagesMeMe.props, propsNeedCastKeys = GenPagesMeMe.propsNeedCastKeys, emits = GenPagesMeMe.emits, components = GenPagesMeMe.components, styles = GenPagesMeMe.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesMeMe.setup(props as GenPagesMeMe)
    }
    )
}
, fun(instance, renderer): GenPagesMeMe {
    return GenPagesMeMe(instance, renderer)
}
)
open class Booking (
    open var id: String? = null,
    @JsonNotNull
    open var scooterId: String,
    @JsonNotNull
    open var duration: String,
    @JsonNotNull
    open var startTime: String,
    @JsonNotNull
    open var endTime: String,
    @JsonNotNull
    open var price: Number,
    @JsonNotNull
    open var status: String,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("Booking", "pages/orders/orders.uvue", 87, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return BookingReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class BookingReactiveObject : Booking, IUTSReactive<Booking> {
    override var __v_raw: Booking
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: Booking, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(id = __v_raw.id, scooterId = __v_raw.scooterId, duration = __v_raw.duration, startTime = __v_raw.startTime, endTime = __v_raw.endTime, price = __v_raw.price, status = __v_raw.status) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): BookingReactiveObject {
        return BookingReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var id: String?
        get() {
            return _tRG(__v_raw, "id", __v_raw.id, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("id")) {
                return
            }
            val oldValue = __v_raw.id
            __v_raw.id = value
            _tRS(__v_raw, "id", oldValue, value)
        }
    override var scooterId: String
        get() {
            return _tRG(__v_raw, "scooterId", __v_raw.scooterId, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("scooterId")) {
                return
            }
            val oldValue = __v_raw.scooterId
            __v_raw.scooterId = value
            _tRS(__v_raw, "scooterId", oldValue, value)
        }
    override var duration: String
        get() {
            return _tRG(__v_raw, "duration", __v_raw.duration, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("duration")) {
                return
            }
            val oldValue = __v_raw.duration
            __v_raw.duration = value
            _tRS(__v_raw, "duration", oldValue, value)
        }
    override var startTime: String
        get() {
            return _tRG(__v_raw, "startTime", __v_raw.startTime, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("startTime")) {
                return
            }
            val oldValue = __v_raw.startTime
            __v_raw.startTime = value
            _tRS(__v_raw, "startTime", oldValue, value)
        }
    override var endTime: String
        get() {
            return _tRG(__v_raw, "endTime", __v_raw.endTime, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("endTime")) {
                return
            }
            val oldValue = __v_raw.endTime
            __v_raw.endTime = value
            _tRS(__v_raw, "endTime", oldValue, value)
        }
    override var price: Number
        get() {
            return _tRG(__v_raw, "price", __v_raw.price, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("price")) {
                return
            }
            val oldValue = __v_raw.price
            __v_raw.price = value
            _tRS(__v_raw, "price", oldValue, value)
        }
    override var status: String
        get() {
            return _tRG(__v_raw, "status", __v_raw.status, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("status")) {
                return
            }
            val oldValue = __v_raw.status
            __v_raw.status = value
            _tRS(__v_raw, "status", oldValue, value)
        }
}
val GenPagesOrdersOrdersClass = CreateVueComponent(GenPagesOrdersOrders::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesOrdersOrders.inheritAttrs, inject = GenPagesOrdersOrders.inject, props = GenPagesOrdersOrders.props, propsNeedCastKeys = GenPagesOrdersOrders.propsNeedCastKeys, emits = GenPagesOrdersOrders.emits, components = GenPagesOrdersOrders.components, styles = GenPagesOrdersOrders.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesOrdersOrders.setup(props as GenPagesOrdersOrders)
    }
    )
}
, fun(instance, renderer): GenPagesOrdersOrders {
    return GenPagesOrdersOrders(instance, renderer)
}
)
val GenPagesFeedbackFeedbackClass = CreateVueComponent(GenPagesFeedbackFeedback::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesFeedbackFeedback.inheritAttrs, inject = GenPagesFeedbackFeedback.inject, props = GenPagesFeedbackFeedback.props, propsNeedCastKeys = GenPagesFeedbackFeedback.propsNeedCastKeys, emits = GenPagesFeedbackFeedback.emits, components = GenPagesFeedbackFeedback.components, styles = GenPagesFeedbackFeedback.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesFeedbackFeedback.setup(props as GenPagesFeedbackFeedback)
    }
    )
}
, fun(instance, renderer): GenPagesFeedbackFeedback {
    return GenPagesFeedbackFeedback(instance, renderer)
}
)
open class PaymentInfo (
    @JsonNotNull
    open var cardNumber: String,
    @JsonNotNull
    open var cardPassword: String,
    @JsonNotNull
    open var alipayAccount: String,
    @JsonNotNull
    open var alipayPassword: String,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("PaymentInfo", "pages/payment/payment.uvue", 163, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return PaymentInfoReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class PaymentInfoReactiveObject : PaymentInfo, IUTSReactive<PaymentInfo> {
    override var __v_raw: PaymentInfo
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: PaymentInfo, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(cardNumber = __v_raw.cardNumber, cardPassword = __v_raw.cardPassword, alipayAccount = __v_raw.alipayAccount, alipayPassword = __v_raw.alipayPassword) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): PaymentInfoReactiveObject {
        return PaymentInfoReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var cardNumber: String
        get() {
            return _tRG(__v_raw, "cardNumber", __v_raw.cardNumber, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("cardNumber")) {
                return
            }
            val oldValue = __v_raw.cardNumber
            __v_raw.cardNumber = value
            _tRS(__v_raw, "cardNumber", oldValue, value)
        }
    override var cardPassword: String
        get() {
            return _tRG(__v_raw, "cardPassword", __v_raw.cardPassword, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("cardPassword")) {
                return
            }
            val oldValue = __v_raw.cardPassword
            __v_raw.cardPassword = value
            _tRS(__v_raw, "cardPassword", oldValue, value)
        }
    override var alipayAccount: String
        get() {
            return _tRG(__v_raw, "alipayAccount", __v_raw.alipayAccount, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("alipayAccount")) {
                return
            }
            val oldValue = __v_raw.alipayAccount
            __v_raw.alipayAccount = value
            _tRS(__v_raw, "alipayAccount", oldValue, value)
        }
    override var alipayPassword: String
        get() {
            return _tRG(__v_raw, "alipayPassword", __v_raw.alipayPassword, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("alipayPassword")) {
                return
            }
            val oldValue = __v_raw.alipayPassword
            __v_raw.alipayPassword = value
            _tRS(__v_raw, "alipayPassword", oldValue, value)
        }
}
val GenPagesPaymentPaymentClass = CreateVueComponent(GenPagesPaymentPayment::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesPaymentPayment.inheritAttrs, inject = GenPagesPaymentPayment.inject, props = GenPagesPaymentPayment.props, propsNeedCastKeys = GenPagesPaymentPayment.propsNeedCastKeys, emits = GenPagesPaymentPayment.emits, components = GenPagesPaymentPayment.components, styles = GenPagesPaymentPayment.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesPaymentPayment.setup(props as GenPagesPaymentPayment)
    }
    )
}
, fun(instance, renderer): GenPagesPaymentPayment {
    return GenPagesPaymentPayment(instance, renderer)
}
)
val GenPagesAdminModifyClass = CreateVueComponent(GenPagesAdminModify::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesAdminModify.inheritAttrs, inject = GenPagesAdminModify.inject, props = GenPagesAdminModify.props, propsNeedCastKeys = GenPagesAdminModify.propsNeedCastKeys, emits = GenPagesAdminModify.emits, components = GenPagesAdminModify.components, styles = GenPagesAdminModify.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesAdminModify.setup(props as GenPagesAdminModify)
    }
    )
}
, fun(instance, renderer): GenPagesAdminModify {
    return GenPagesAdminModify(instance, renderer)
}
)
val GenPagesAdminHomeClass = CreateVueComponent(GenPagesAdminHome::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesAdminHome.inheritAttrs, inject = GenPagesAdminHome.inject, props = GenPagesAdminHome.props, propsNeedCastKeys = GenPagesAdminHome.propsNeedCastKeys, emits = GenPagesAdminHome.emits, components = GenPagesAdminHome.components, styles = GenPagesAdminHome.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesAdminHome.setup(props as GenPagesAdminHome)
    }
    )
}
, fun(instance, renderer): GenPagesAdminHome {
    return GenPagesAdminHome(instance, renderer)
}
)
open class DurationRow (
    @JsonNotNull
    open var durationType: String,
    @JsonNotNull
    open var totalRevenue: String,
    @JsonNotNull
    open var totalOrders: Number,
) : UTSReactiveObject(), IUTSSourceMap {
    override fun `__$getOriginalPosition`(): UTSSourceMapPosition? {
        return UTSSourceMapPosition("DurationRow", "pages/admin/revenue.uvue", 38, 6)
    }
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return DurationRowReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class DurationRowReactiveObject : DurationRow, IUTSReactive<DurationRow> {
    override var __v_raw: DurationRow
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: DurationRow, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(durationType = __v_raw.durationType, totalRevenue = __v_raw.totalRevenue, totalOrders = __v_raw.totalOrders) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): DurationRowReactiveObject {
        return DurationRowReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var durationType: String
        get() {
            return _tRG(__v_raw, "durationType", __v_raw.durationType, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("durationType")) {
                return
            }
            val oldValue = __v_raw.durationType
            __v_raw.durationType = value
            _tRS(__v_raw, "durationType", oldValue, value)
        }
    override var totalRevenue: String
        get() {
            return _tRG(__v_raw, "totalRevenue", __v_raw.totalRevenue, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("totalRevenue")) {
                return
            }
            val oldValue = __v_raw.totalRevenue
            __v_raw.totalRevenue = value
            _tRS(__v_raw, "totalRevenue", oldValue, value)
        }
    override var totalOrders: Number
        get() {
            return _tRG(__v_raw, "totalOrders", __v_raw.totalOrders, __v_isReadonly, __v_isShallow)
        }
        set(value) {
            if (!__v_canSet("totalOrders")) {
                return
            }
            val oldValue = __v_raw.totalOrders
            __v_raw.totalOrders = value
            _tRS(__v_raw, "totalOrders", oldValue, value)
        }
}
val GenPagesAdminRevenueClass = CreateVueComponent(GenPagesAdminRevenue::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesAdminRevenue.inheritAttrs, inject = GenPagesAdminRevenue.inject, props = GenPagesAdminRevenue.props, propsNeedCastKeys = GenPagesAdminRevenue.propsNeedCastKeys, emits = GenPagesAdminRevenue.emits, components = GenPagesAdminRevenue.components, styles = GenPagesAdminRevenue.styles, setup = fun(props: ComponentPublicInstance): Any? {
        return GenPagesAdminRevenue.setup(props as GenPagesAdminRevenue)
    }
    )
}
, fun(instance, renderer): GenPagesAdminRevenue {
    return GenPagesAdminRevenue(instance, renderer)
}
)
fun createApp(): UTSJSONObject {
    val app = createSSRApp(GenAppClass)
    return _uO("app" to app)
}
fun main(app: IApp) {
    definePageRoutes()
    defineAppConfig()
    (createApp()["app"] as VueApp).mount(app, GenUniApp())
}
open class UniAppConfig : io.dcloud.uniapp.appframe.AppConfig {
    override var name: String = "demo"
    override var appid: String = "__UNI__E6393FD"
    override var versionName: String = "1.0.0"
    override var versionCode: String = "100"
    override var uniCompilerVersion: String = "4.87"
    constructor() : super() {}
}
fun definePageRoutes() {
    __uniRoutes.push(UniPageRoute(path = "pages/index/index", component = GenPagesIndexIndexClass, meta = UniPageMeta(isQuit = true), style = _uM("navigationBarTitleText" to "uni-app x")))
    __uniRoutes.push(UniPageRoute(path = "pages/bikelist/bikelist", component = GenPagesBikelistBikelistClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "bikelist")))
    __uniRoutes.push(UniPageRoute(path = "pages/login/login", component = GenPagesLoginLoginClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "login")))
    __uniRoutes.push(UniPageRoute(path = "pages/register/register", component = GenPagesRegisterRegisterClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "register")))
    __uniRoutes.push(UniPageRoute(path = "pages/price/price", component = GenPagesPricePriceClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "price")))
    __uniRoutes.push(UniPageRoute(path = "pages/me/me", component = GenPagesMeMeClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "me")))
    __uniRoutes.push(UniPageRoute(path = "pages/orders/orders", component = GenPagesOrdersOrdersClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "orders")))
    __uniRoutes.push(UniPageRoute(path = "pages/feedback/feedback", component = GenPagesFeedbackFeedbackClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "feedback")))
    __uniRoutes.push(UniPageRoute(path = "pages/payment/payment", component = GenPagesPaymentPaymentClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "payment")))
    __uniRoutes.push(UniPageRoute(path = "pages/admin/modify", component = GenPagesAdminModifyClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "Admin")))
    __uniRoutes.push(UniPageRoute(path = "pages/admin/home", component = GenPagesAdminHomeClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "Admin")))
    __uniRoutes.push(UniPageRoute(path = "pages/admin/revenue", component = GenPagesAdminRevenueClass, meta = UniPageMeta(isQuit = false), style = _uM("navigationBarTitleText" to "Admin")))
}
val __uniLaunchPage: Map<String, Any?> = _uM("url" to "pages/index/index", "style" to _uM("navigationBarTitleText" to "uni-app x"))
fun defineAppConfig() {
    __uniConfig.entryPagePath = "/pages/index/index"
    __uniConfig.globalStyle = _uM("navigationBarTextStyle" to "black", "navigationBarTitleText" to "uni-app x", "navigationBarBackgroundColor" to "#F8F8F8", "backgroundColor" to "#F8F8F8")
    __uniConfig.getTabBarConfig = fun(): Map<String, Any>? {
        return null
    }
    __uniConfig.tabBar = __uniConfig.getTabBarConfig()
    __uniConfig.conditionUrl = ""
    __uniConfig.uniIdRouter = _uM()
    __uniConfig.ready = true
}
open class GenUniApp : UniAppImpl() {
    open val vm: GenApp?
        get() {
            return getAppVm() as GenApp?
        }
    open val `$vm`: GenApp?
        get() {
            return getAppVm() as GenApp?
        }
}
fun getApp(): GenUniApp {
    return getUniApp() as GenUniApp
}
