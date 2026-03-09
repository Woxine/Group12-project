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
import io.dcloud.uniapp.extapi.navigateBack as uni_navigateBack
import io.dcloud.uniapp.extapi.reLaunch as uni_reLaunch
import io.dcloud.uniapp.extapi.showActionSheet as uni_showActionSheet
import io.dcloud.uniapp.extapi.showModal as uni_showModal
import io.dcloud.uniapp.extapi.showToast as uni_showToast
open class GenPagesPaymentPayment : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {}
    companion object {
        @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
        var setup: (__props: GenPagesPaymentPayment) -> Any? = fun(__props): Any? {
            val __ins = getCurrentInstance()!!
            val _ctx = __ins.proxy as GenPagesPaymentPayment
            val _cache = __ins.renderCache
            val paymentMethods = _uA(
                "Bank Card",
                "Alipay",
                "WeChat Pay"
            )
            val paymentMethodIndex = ref(0)
            val banks = _uA(
                "ICBC",
                "ABC",
                "BOC",
                "CCB",
                "BCM"
            )
            val bankIndex = ref(0)
            var storedAmount = uni_getStorageSync("totalPrice")
            val orderAmount = ref<Number>(if (storedAmount != null && storedAmount != "") {
                parseFloat(storedAmount as String)
            } else {
                1.0
            }
            )
            val paymentInfo = reactive<PaymentInfo>(PaymentInfo(cardNumber = "", cardPassword = "", alipayAccount = "", alipayPassword = ""))
            val isPaymentProcessing = ref(false)
            val showPaymentMethodPicker = fun(){
                uni_showActionSheet(ShowActionSheetOptions(itemList = paymentMethods, success = fun(res){
                    paymentMethodIndex.value = res.tapIndex
                }
                ))
            }
            val showBankPicker = fun(){
                uni_showActionSheet(ShowActionSheetOptions(itemList = banks, success = fun(res){
                    bankIndex.value = res.tapIndex
                }
                ))
            }
            val formatCardNumber = fun(e: UTSJSONObject){
                val detail = e["detail"] as UTSJSONObject?
                if (detail == null) {
                    return
                }
                var value = detail["value"] as String?
                if (value == null) {
                    return
                }
                value = value.replace(UTSRegExp("\\s", "g"), "")
                if (value.length > 16) {
                    value = value.substring(0, 16)
                }
                paymentInfo.cardNumber = value
            }
            val validatePaymentInfo = fun(): Boolean {
                val method = paymentMethodIndex.value
                if (method === 0) {
                    val cardNum = paymentInfo.cardNumber.replace(UTSRegExp("\\s", "g"), "")
                    if (cardNum == "" || cardNum.length !== 16) {
                        uni_showToast(ShowToastOptions(title = "Please enter a valid 16-digit card number", icon = "none"))
                        return false
                    }
                    if (paymentInfo.cardPassword == "" || paymentInfo.cardPassword.length < 6) {
                        uni_showToast(ShowToastOptions(title = "Please enter payment password (at least 6 characters)", icon = "none"))
                        return false
                    }
                } else if (method === 1) {
                    if (paymentInfo.alipayAccount == "" || paymentInfo.alipayAccount.trim().length == 0) {
                        uni_showToast(ShowToastOptions(title = "Please enter Alipay account", icon = "none"))
                        return false
                    }
                    if (paymentInfo.alipayPassword == "" || paymentInfo.alipayPassword.length < 6) {
                        uni_showToast(ShowToastOptions(title = "Please enter Alipay password (at least 6 characters)", icon = "none"))
                        return false
                    }
                } else if (method === 2) {
                    return true
                }
                return true
            }
            val onPayment = fun(){
                if (isPaymentProcessing.value) {
                    return
                }
                if (!validatePaymentInfo()) {
                    return
                }
                isPaymentProcessing.value = true
                setTimeout(fun(){
                    val method = paymentMethods[paymentMethodIndex.value]
                    uni_showToast(ShowToastOptions(title = "Payment successful via " + method + "!", icon = "success", duration = 2000))
                    isPaymentProcessing.value = false
                    setTimeout(fun(){
                        uni_reLaunch(ReLaunchOptions(url = "/pages/index/index"))
                    }
                    , 2000)
                }
                , 2000)
            }
            val onCancel = fun(){
                uni_showModal(ShowModalOptions(title = "Cancel Payment", content = "Are you sure you want to cancel this payment?", success = fun(res){
                    if (res.confirm) {
                        uni_navigateBack(null)
                    }
                }
                ))
            }
            return fun(): Any? {
                val _component_navigator = resolveComponent("navigator")
                return _cE(Fragment, null, _uA(
                    _cE("view", _uM("class" to "back"), _uA(
                        _cE("image", _uM("src" to "/static/login_back1.jpg", "mode" to "", "class" to "backpic")),
                        _cE("view", _uM("class" to "payment_title"), _uA(
                            _cE("text", _uM("class" to "title_text", "style" to _nS(_uM("text-align" to "center"))), "💳 Payment", 4)
                        )),
                        _cE("view", _uM("class" to "order_info"), _uA(
                            _cE("view", _uM("class" to "order_header"), _uA(
                                _cE("text", _uM("class" to "order_label"), "Order Summary")
                            )),
                            _cE("view", _uM("class" to "order_item"), _uA(
                                _cE("text", _uM("class" to "label"), "Order Total:"),
                                _cE("text", _uM("class" to "amount"), "£" + _tD(orderAmount.value.toFixed(2)), 1)
                            ))
                        )),
                        _cE("scroll-view", _uM("scroll-y" to "true", "class" to "payment_scroll"), _uA(
                            _cE("view", _uM("class" to "payment_container"), _uA(
                                _cE("view", _uM("class" to "form_group"), _uA(
                                    _cE("text", _uM("class" to "label"), "Payment Method"),
                                    _cE("view", _uM("class" to "picker_view", "onClick" to showPaymentMethodPicker), _uA(
                                        _cE("text", _uM("class" to "picker_text"), _tD(paymentMethods[paymentMethodIndex.value]), 1),
                                        _cE("text", _uM("class" to "arrow_text"), "▼")
                                    ))
                                )),
                                _cE("view", _uM("class" to "span1")),
                                if (paymentMethodIndex.value === 0) {
                                    _cE("view", _uM("key" to 0, "class" to "payment_form"), _uA(
                                        _cE("view", _uM("class" to "form_group"), _uA(
                                            _cE("text", _uM("class" to "label"), "Bank"),
                                            _cE("view", _uM("class" to "picker_view", "onClick" to showBankPicker), _uA(
                                                _cE("text", _uM("class" to "picker_text"), _tD(banks[bankIndex.value]), 1),
                                                _cE("text", _uM("class" to "arrow_text"), "▼")
                                            ))
                                        )),
                                        _cE("view", _uM("class" to "span1")),
                                        _cE("view", _uM("class" to "form_group"), _uA(
                                            _cE("text", _uM("class" to "label"), "Card Number"),
                                            _cE("input", _uM("modelValue" to paymentInfo.cardNumber, "onInput" to _uA<Any?>(fun(`$event`: UniInputEvent){
                                                paymentInfo.cardNumber = `$event`.detail.value
                                            }, formatCardNumber), "type" to "number", "placeholder" to "1234 5678 9012 3456", "maxlength" to "16", "confirm-type" to "next", "class" to "input"), null, 40, _uA(
                                                "modelValue",
                                                "onInput"
                                            ))
                                        )),
                                        _cE("view", _uM("class" to "span1")),
                                        _cE("view", _uM("class" to "form_group"), _uA(
                                            _cE("text", _uM("class" to "label"), "Password"),
                                            _cE("input", _uM("modelValue" to paymentInfo.cardPassword, "onInput" to fun(`$event`: UniInputEvent){
                                                paymentInfo.cardPassword = `$event`.detail.value
                                            }, "type" to "password", "placeholder" to "Enter payment password", "confirm-type" to "done", "class" to "input"), null, 40, _uA(
                                                "modelValue",
                                                "onInput"
                                            ))
                                        ))
                                    ))
                                } else {
                                    if (paymentMethodIndex.value === 1) {
                                        _cE("view", _uM("key" to 1, "class" to "payment_form"), _uA(
                                            _cE("view", _uM("class" to "form_section_title"), _uA(
                                                _cE("text", _uM("class" to "section_title_text"), "Alipay Account")
                                            )),
                                            _cE("view", _uM("class" to "form_group"), _uA(
                                                _cE("text", _uM("class" to "label"), "Alipay Account"),
                                                _cE("input", _uM("modelValue" to paymentInfo.alipayAccount, "onInput" to fun(`$event`: UniInputEvent){
                                                    paymentInfo.alipayAccount = `$event`.detail.value
                                                }, "type" to "text", "placeholder" to "Enter phone number or email", "confirm-type" to "next", "class" to "input"), null, 40, _uA(
                                                    "modelValue",
                                                    "onInput"
                                                ))
                                            )),
                                            _cE("view", _uM("class" to "span1")),
                                            _cE("view", _uM("class" to "form_group"), _uA(
                                                _cE("text", _uM("class" to "label"), "Password"),
                                                _cE("input", _uM("modelValue" to paymentInfo.alipayPassword, "onInput" to fun(`$event`: UniInputEvent){
                                                    paymentInfo.alipayPassword = `$event`.detail.value
                                                }, "type" to "password", "placeholder" to "Enter payment password", "confirm-type" to "done", "class" to "input"), null, 40, _uA(
                                                    "modelValue",
                                                    "onInput"
                                                ))
                                            ))
                                        ))
                                    } else {
                                        if (paymentMethodIndex.value === 2) {
                                            _cE("view", _uM("key" to 2, "class" to "payment_form"), _uA(
                                                _cE("view", _uM("class" to "info_box"), _uA(
                                                    _cE("text", _uM("class" to "info_text"), "WeChat Pay"),
                                                    _cE("view", _uM("class" to "qr_code_placeholder"), _uA(
                                                        _cE("text", _uM("class" to "qr_hint"), "QR Code")
                                                    )),
                                                    _cE("text", _uM("class" to "info_desc"), "Scan the QR code with WeChat to complete payment")
                                                ))
                                            ))
                                        } else {
                                            _cC("v-if", true)
                                        }
                                    }
                                }
                                ,
                                _cE("view", _uM("class" to "span2")),
                                _cE("button", _uM("onClick" to onPayment, "size" to "default", "disabled" to isPaymentProcessing.value, "class" to _nC(_uA(
                                    "payment_button",
                                    _uM("processing" to isPaymentProcessing.value)
                                )), "style" to _nS(_uM("background-color" to "#4CAF50", "color" to "white", "border" to "none"))), _tD(if (isPaymentProcessing.value) {
                                    "Processing..."
                                } else {
                                    "Pay \u00A3" + orderAmount.value.toFixed(2)
                                }
                                ), 15, _uA(
                                    "disabled"
                                )),
                                _cE("view", _uM("class" to "span3")),
                                _cE("button", _uM("onClick" to onCancel, "size" to "default", "class" to "cancel_button"), " Cancel ")
                            ))
                        ))
                    )),
                    _cE("view", _uM("class" to "bottom"), _uA(
                        _cV(_component_navigator, _uM("url" to "/pages/index/index", "open-type" to "reLaunch", "hover-class" to "navigator-hover", "class" to "bottom_left"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("image", _uM("src" to "/static/home.png", "mode" to "aspectFit", "class" to "icon2")),
                                _cE("view", _uM("class" to "left_down"), _uA(
                                    _cE("text", _uM("class" to "text3"), "Home")
                                ))
                            )
                        }
                        ), "_" to 1)),
                        _cV(_component_navigator, _uM("url" to "/pages/bikelist/bikelist", "open-type" to "reLaunch", "hover-class" to "navigator-hover", "class" to "bottom_center"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
                            return _uA(
                                _cE("image", _uM("src" to "/static/bike.png", "mode" to "aspectFit", "class" to "bike_icon")),
                                _cE("view", _uM("class" to "center_down"), _uA(
                                    _cE("text", _uM("class" to "text3"), "Bike")
                                ))
                            )
                        }
                        ), "_" to 1)),
                        _cV(_component_navigator, _uM("url" to "/pages/orders/orders", "open-type" to "reLaunch", "hover-class" to "navigator-hover", "class" to "bottom_right"), _uM("default" to withSlotCtx(fun(): UTSArray<Any> {
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
                return _uM("back" to _pS(_uM("position" to "relative", "width" to "100%", "backgroundColor" to "#f5f5f5", "display" to "flex", "flexDirection" to "column", "overflow" to "hidden", "boxSizing" to "border-box", "paddingBottom" to "8%")), "backpic" to _pS(_uM("width" to "100%", "height" to "100%", "position" to "absolute", "top" to 0, "left" to 0, "zIndex" to 0, "opacity" to 0.25)), "payment_title" to _pS(_uM("position" to "relative", "zIndex" to 1, "marginTop" to 20, "marginBottom" to 10, "flexShrink" to 0, "display" to "flex", "justifyContent" to "center", "alignItems" to "center")), "title_text" to _pS(_uM("fontSize" to 24, "fontWeight" to "bold", "color" to "rgba(60,60,60,0.95)", "fontFamily" to "Arial, sans-serif", "letterSpacing" to 0.5, "textAlign" to "center")), "order_info" to _pS(_uM("position" to "relative", "zIndex" to 1, "marginTop" to 0, "marginRight" to 20, "marginBottom" to 10, "marginLeft" to 20, "paddingTop" to 12, "paddingRight" to 15, "paddingBottom" to 12, "paddingLeft" to 15, "backgroundImage" to "linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%)", "backgroundColor" to "rgba(0,0,0,0)", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "boxShadow" to "0 2px 8px rgba(0, 0, 0, 0.08)", "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "rgba(255,255,255,0.8)", "borderRightColor" to "rgba(255,255,255,0.8)", "borderBottomColor" to "rgba(255,255,255,0.8)", "borderLeftColor" to "rgba(255,255,255,0.8)", "flexShrink" to 0)), "order_header" to _pS(_uM("marginBottom" to 8, "paddingBottom" to 6, "borderBottomWidth" to 1, "borderBottomStyle" to "solid", "borderBottomColor" to "#f0f0f0")), "order_label" to _pS(_uM("fontSize" to 13, "color" to "#666666", "letterSpacing" to 0.3)), "order_item" to _pS(_uM("display" to "flex", "justifyContent" to "space-between", "alignItems" to "center")), "label" to _uM(".order_item " to _uM("fontSize" to 13, "color" to "#666666"), ".form_group " to _uM("fontSize" to 14, "color" to "#333333", "marginBottom" to 10, "letterSpacing" to 0.3), ".form_group_half " to _uM("fontSize" to 14, "color" to "#333333", "marginBottom" to 10, "letterSpacing" to 0.3)), "amount" to _uM(".order_item " to _uM("fontSize" to 22, "color" to "#4CAF50", "fontWeight" to "bold", "letterSpacing" to 0.3)), "payment_scroll" to _pS(_uM("position" to "relative", "zIndex" to 1, "flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%", "overflowY" to "scroll", "WebkitOverflowScrolling" to "touch", "height" to 0, "minHeight" to 0, "width" to "100%", "scrollBehavior" to "smooth")), "payment_container" to _pS(_uM("marginTop" to 0, "marginRight" to 20, "marginBottom" to 0, "marginLeft" to 20, "paddingTop" to 20, "paddingRight" to 20, "paddingBottom" to 500, "paddingLeft" to 20, "backgroundImage" to "linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%)", "backgroundColor" to "rgba(0,0,0,0)", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "boxShadow" to "0 4px 12px rgba(0, 0, 0, 0.1)", "borderTopWidth" to 1, "borderRightWidth" to 1, "borderBottomWidth" to 1, "borderLeftWidth" to 1, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "rgba(255,255,255,0.8)", "borderRightColor" to "rgba(255,255,255,0.8)", "borderBottomColor" to "rgba(255,255,255,0.8)", "borderLeftColor" to "rgba(255,255,255,0.8)", "boxSizing" to "border-box")), "form_group" to _pS(_uM("marginBottom" to 15)), "form_section_title" to _pS(_uM("marginBottom" to 15, "paddingBottom" to 10, "borderBottomWidth" to 1, "borderBottomStyle" to "solid", "borderBottomColor" to "#e8e8e8")), "section_title_text" to _pS(_uM("fontSize" to 16, "color" to "#4CAF50", "fontWeight" to "bold", "letterSpacing" to 0.5)), "form_row" to _pS(_uM("display" to "flex", "gap" to "15px", "justifyContent" to "space-between")), "form_group_half" to _pS(_uM("flexGrow" to 1, "flexShrink" to 1, "flexBasis" to "0%")), "input" to _pS(_uM("width" to "100%", "paddingTop" to 12, "paddingRight" to 15, "paddingBottom" to 12, "paddingLeft" to 15, "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#e0e0e0", "borderRightColor" to "#e0e0e0", "borderBottomColor" to "#e0e0e0", "borderLeftColor" to "#e0e0e0", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "fontSize" to 15, "boxSizing" to "border-box", "backgroundColor" to "#fafafa", "transitionProperty" to "all", "transitionDuration" to "0.3s", "transitionTimingFunction" to "ease", "borderTopColor:focus" to "#4CAF50", "borderRightColor:focus" to "#4CAF50", "borderBottomColor:focus" to "#4CAF50", "borderLeftColor:focus" to "#4CAF50", "outline:focus" to "none", "backgroundColor:focus" to "#ffffff", "boxShadow:focus" to "0 0 0 3px rgba(76, 175, 80, 0.1)")), "picker_view" to _pS(_uM("display" to "flex", "justifyContent" to "space-between", "alignItems" to "center", "paddingTop" to 8, "paddingRight" to 12, "paddingBottom" to 8, "paddingLeft" to 12, "backgroundColor" to "#fafafa", "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#e0e0e0", "borderRightColor" to "#e0e0e0", "borderBottomColor" to "#e0e0e0", "borderLeftColor" to "#e0e0e0", "borderTopLeftRadius" to 8, "borderTopRightRadius" to 8, "borderBottomRightRadius" to 8, "borderBottomLeftRadius" to 8, "minHeight" to 36, "transitionProperty" to "all", "transitionDuration" to "0.3s", "transitionTimingFunction" to "ease", "backgroundColor:active" to "#f0f0f0", "borderTopColor:active" to "#4CAF50", "borderRightColor:active" to "#4CAF50", "borderBottomColor:active" to "#4CAF50", "borderLeftColor:active" to "#4CAF50")), "picker_text" to _pS(_uM("fontSize" to 13, "color" to "#333333")), "arrow_text" to _pS(_uM("fontSize" to 10, "color" to "#999999")), "payment_form" to _pS(_uM("marginTop" to 0, "paddingTop" to 0)), "info_box" to _pS(_uM("display" to "flex", "flexDirection" to "column", "alignItems" to "center", "justifyContent" to "center", "paddingTop" to 50, "paddingRight" to 20, "paddingBottom" to 50, "paddingLeft" to 20, "backgroundImage" to "linear-gradient(135deg, #f9f9f9 0%, #ffffff 100%)", "backgroundColor" to "rgba(0,0,0,0)", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "marginTop" to 15, "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "dashed", "borderRightStyle" to "dashed", "borderBottomStyle" to "dashed", "borderLeftStyle" to "dashed", "borderTopColor" to "#e0e0e0", "borderRightColor" to "#e0e0e0", "borderBottomColor" to "#e0e0e0", "borderLeftColor" to "#e0e0e0")), "info_text" to _pS(_uM("fontSize" to 20, "color" to "#4CAF50", "fontWeight" to "bold", "marginBottom" to 20, "letterSpacing" to 0.5)), "qr_code_placeholder" to _pS(_uM("width" to 180, "height" to 180, "backgroundColor" to "#f0f0f0", "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "dashed", "borderRightStyle" to "dashed", "borderBottomStyle" to "dashed", "borderLeftStyle" to "dashed", "borderTopColor" to "#cccccc", "borderRightColor" to "#cccccc", "borderBottomColor" to "#cccccc", "borderLeftColor" to "#cccccc", "borderTopLeftRadius" to 12, "borderTopRightRadius" to 12, "borderBottomRightRadius" to 12, "borderBottomLeftRadius" to 12, "display" to "flex", "alignItems" to "center", "justifyContent" to "center", "marginBottom" to 20)), "qr_hint" to _pS(_uM("fontSize" to 14, "color" to "#999999")), "info_desc" to _pS(_uM("fontSize" to 13, "color" to "#666666", "textAlign" to "center", "lineHeight" to 1.6)), "span1" to _pS(_uM("height" to 10)), "span2" to _pS(_uM("height" to 15)), "span3" to _pS(_uM("height" to 10)), "payment_button" to _uM("" to _uM("width" to "100%", "height" to 50, "!backgroundColor" to "#4CAF50", "!color" to "#FFFFFF", "!borderTopWidth" to "medium", "!borderRightWidth" to "medium", "!borderBottomWidth" to "medium", "!borderLeftWidth" to "medium", "!borderTopStyle" to "none", "!borderRightStyle" to "none", "!borderBottomStyle" to "none", "!borderLeftStyle" to "none", "!borderTopColor" to "#000000", "!borderRightColor" to "#000000", "!borderBottomColor" to "#000000", "!borderLeftColor" to "#000000", "borderTopLeftRadius" to 10, "borderTopRightRadius" to 10, "borderBottomRightRadius" to 10, "borderBottomLeftRadius" to 10, "fontSize" to 17, "fontWeight" to "bold", "letterSpacing" to 0.5, "boxShadow" to "0 4px 12px rgba(76, 175, 80, 0.4)", "transitionProperty" to "all", "transitionDuration" to "0.3s", "transitionTimingFunction" to "ease", "transform:active" to "scale(0.98)", "boxShadow:active" to "0 2px 8px rgba(76, 175, 80, 0.2)"), ".processing" to _uM("backgroundImage" to "linear-gradient(135deg, #81c784 0%, #66bb6a 100%)", "backgroundColor" to "rgba(0,0,0,0)", "opacity" to 0.8)), "cancel_button" to _pS(_uM("width" to "100%", "height" to 50, "backgroundColor" to "rgba(255,255,255,0.9)", "color" to "#666666", "borderTopWidth" to 2, "borderRightWidth" to 2, "borderBottomWidth" to 2, "borderLeftWidth" to 2, "borderTopStyle" to "solid", "borderRightStyle" to "solid", "borderBottomStyle" to "solid", "borderLeftStyle" to "solid", "borderTopColor" to "#e0e0e0", "borderRightColor" to "#e0e0e0", "borderBottomColor" to "#e0e0e0", "borderLeftColor" to "#e0e0e0", "borderTopLeftRadius" to 10, "borderTopRightRadius" to 10, "borderBottomRightRadius" to 10, "borderBottomLeftRadius" to 10, "fontSize" to 16, "letterSpacing" to 0.3, "transitionProperty" to "all", "transitionDuration" to "0.3s", "transitionTimingFunction" to "ease", "backgroundColor:active" to "#f5f5f5", "borderTopColor:active" to "#cccccc", "borderRightColor:active" to "#cccccc", "borderBottomColor:active" to "#cccccc", "borderLeftColor:active" to "#cccccc", "transform:active" to "scale(0.98)")), "bottom" to _pS(_uM("width" to "100%", "height" to "8%", "backgroundImage" to "none", "backgroundColor" to "#E3E1E1", "position" to "absolute", "bottom" to 0, "borderTopLeftRadius" to 25, "borderTopRightRadius" to 25, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "zIndex" to 100)), "bottom_left" to _pS(_uM("borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginLeft" to "5%", "position" to "absolute", "left" to 0)), "icon2" to _uM(".bottom_left " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%"), ".bottom_right " to _uM("position" to "absolute", "left" to "20%", "width" to "60%", "height" to "60%")), "left_down" to _uM(".bottom_left " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_center" to _pS(_uM("position" to "absolute", "left" to "40%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%")), "bike_icon" to _uM(".bottom_center " to _uM("position" to "absolute", "left" to "25%", "width" to "50%", "height" to "60%")), "center_down" to _uM(".bottom_center " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "bottom_right" to _pS(_uM("position" to "absolute", "right" to "0%", "borderTopLeftRadius" to 0, "borderTopRightRadius" to 0, "borderBottomRightRadius" to 0, "borderBottomLeftRadius" to 0, "width" to "20%", "height" to "100%", "marginRight" to "5%")), "right_down" to _uM(".bottom_right " to _uM("display" to "flex", "justifyContent" to "center", "alignItems" to "center", "position" to "absolute", "bottom" to "0%", "width" to "100%", "height" to "40%")), "text3" to _pS(_uM("fontSize" to 12, "marginTop" to 4)), "@TRANSITION" to _uM("input" to _uM("property" to "all", "duration" to "0.3s", "timingFunction" to "ease"), "picker_view" to _uM("property" to "all", "duration" to "0.3s", "timingFunction" to "ease"), "payment_button" to _uM("property" to "all", "duration" to "0.3s", "timingFunction" to "ease"), "cancel_button" to _uM("property" to "all", "duration" to "0.3s", "timingFunction" to "ease")))
            }
        var inheritAttrs = true
        var inject: Map<String, Map<String, Any?>> = _uM()
        var emits: Map<String, Any?> = _uM()
        var props = _nP(_uM())
        var propsNeedCastKeys: UTSArray<String> = _uA()
        var components: Map<String, CreateVueComponent> = _uM()
    }
}
