import { reactive, ref } from 'vue';

// 支付方式列表
type PaymentInfo = { __$originalPosition?: UTSSourceMapPosition<"PaymentInfo", "pages/payment/payment.uvue", 179, 6>;
	cardNumber: string
	cardPassword: string
	alipayAccount: string
	alipayPassword: string
}


const __sfc__ = defineComponent({
  __name: 'payment',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const paymentMethods = ['Bank Card', 'Alipay', 'WeChat Pay'];
const paymentMethodIndex = ref(0);

// 银行列表
const banks = ['ICBC', 'ABC', 'BOC', 'CCB', 'BCM'];
const bankIndex = ref(0);

// 订单金额 - 从本地存储获取或使用默认值
let storedAmount = uni.getStorageSync('totalPrice');
const orderAmount = ref<number>(storedAmount != null && storedAmount != '' ? parseFloat(storedAmount as string) : 1.0);

// 支付信息对象
const paymentInfo = reactive<PaymentInfo>({
	cardNumber: '',
	cardPassword: '',
	alipayAccount: '',
	alipayPassword: ''
});

// 支付处理状态
const isPaymentProcessing = ref(false);

// 显示支付方式选择器
const showPaymentMethodPicker = () => {
	uni.showActionSheet({
		itemList: paymentMethods,
		success: (res) => {
			paymentMethodIndex.value = res.tapIndex;
		}
	});
};

// 显示银行选择器
const showBankPicker = () => {
	uni.showActionSheet({
		itemList: banks,
		success: (res) => {
			bankIndex.value = res.tapIndex;
		}
	});
};

// 格式化卡号（添加空格）
const formatCardNumber = (e: UTSJSONObject) => {
	const detail = e['detail'] as UTSJSONObject | null;
	if (detail == null) return;
	
	let value = detail['value'] as string | null;
	if (value == null) return;
	
	// 移除所有空格
	value = value.replace(/\s/g, '');
	
	// 限制为16位数字
	if (value.length > 16) {
		value = value.substring(0, 16);
	}
	
	paymentInfo.cardNumber = value;
};

// 验证支付信息
const validatePaymentInfo = (): boolean => {
	const method = paymentMethodIndex.value;
	
	if (method === 0) {
		// 银行卡验证
		const cardNum = paymentInfo.cardNumber.replace(/\s/g, '');
		if (cardNum == '' || cardNum.length !== 16) {
			uni.showToast({
				title: 'Please enter a valid 16-digit card number',
				icon: 'none'
			});
			return false;
		}
		
		if (paymentInfo.cardPassword == '' || paymentInfo.cardPassword.length < 6) {
			uni.showToast({
				title: 'Please enter payment password (at least 6 characters)',
				icon: 'none'
			});
			return false;
		}
	} else if (method === 1) {
		// 支付宝验证
		if (paymentInfo.alipayAccount == '' || paymentInfo.alipayAccount.trim().length == 0) {
			uni.showToast({
				title: 'Please enter Alipay account',
				icon: 'none'
			});
			return false;
		}
		
		if (paymentInfo.alipayPassword == '' || paymentInfo.alipayPassword.length < 6) {
			uni.showToast({
				title: 'Please enter Alipay password (at least 6 characters)',
				icon: 'none'
			});
			return false;
		}
	} else if (method === 2) {
		// 微信支付不需要验证，直接可以支付
		return true;
	}
	
	return true;
};

// 支付处理
const onPayment = () => {
	if (isPaymentProcessing.value) return;
	
	if (!validatePaymentInfo()) return;
	
	isPaymentProcessing.value = true;
	
	// 模拟支付请求
	setTimeout(() => {
		const method = paymentMethods[paymentMethodIndex.value];
		uni.showToast({
			title: `Payment successful via ${method}!`,
			icon: 'success',
			duration: 2000
		});
		
		isPaymentProcessing.value = false;
		
		// 支付成功后跳转到首页
		setTimeout(() => {
			uni.reLaunch({
				url: '/pages/index/index'
			});
		}, 2000);
	}, 2000);
};

// 取消支付
const onCancel = () => {
	uni.showModal({
		title: 'Cancel Payment',
		content: 'Are you sure you want to cancel this payment?',
		success: (res) => {
			if (res.confirm) {
				uni.navigateBack();
			}
		}
	});
};

return (): any | null => {

const _component_navigator = resolveComponent("navigator")

  return _cE(Fragment, null, [
    _cE("view", _uM({ class: "back" }), [
      _cE("image", _uM({
        src: "/static/login_back1.jpg",
        mode: "",
        class: "backpic"
      })),
      _cE("view", _uM({ class: "payment_title" }), [
        _cE("text", _uM({
          class: "title_text",
          style: _nS(_uM({"text-align":"center"}))
        }), "💳 Payment", 4 /* STYLE */)
      ]),
      _cE("view", _uM({ class: "order_info" }), [
        _cE("view", _uM({ class: "order_header" }), [
          _cE("text", _uM({ class: "order_label" }), "Order Summary")
        ]),
        _cE("view", _uM({ class: "order_item" }), [
          _cE("text", _uM({ class: "label" }), "Order Total:"),
          _cE("text", _uM({ class: "amount" }), "£" + _tD(orderAmount.value.toFixed(2)), 1 /* TEXT */)
        ])
      ]),
      _cE("scroll-view", _uM({
        "scroll-y": "true",
        class: "payment_scroll"
      }), [
        _cE("view", _uM({ class: "payment_container" }), [
          _cE("view", _uM({ class: "form_group" }), [
            _cE("text", _uM({ class: "label" }), "Payment Method"),
            _cE("view", _uM({
              class: "picker_view",
              onClick: showPaymentMethodPicker
            }), [
              _cE("text", _uM({ class: "picker_text" }), _tD(paymentMethods[paymentMethodIndex.value]), 1 /* TEXT */),
              _cE("text", _uM({ class: "arrow_text" }), "▼")
            ])
          ]),
          _cE("view", _uM({ class: "span1" })),
          paymentMethodIndex.value === 0
            ? _cE("view", _uM({
                key: 0,
                class: "payment_form"
              }), [
                _cE("view", _uM({ class: "form_group" }), [
                  _cE("text", _uM({ class: "label" }), "Bank"),
                  _cE("view", _uM({
                    class: "picker_view",
                    onClick: showBankPicker
                  }), [
                    _cE("text", _uM({ class: "picker_text" }), _tD(banks[bankIndex.value]), 1 /* TEXT */),
                    _cE("text", _uM({ class: "arrow_text" }), "▼")
                  ])
                ]),
                _cE("view", _uM({ class: "span1" })),
                _cE("view", _uM({ class: "form_group" }), [
                  _cE("text", _uM({ class: "label" }), "Card Number"),
                  _cE("input", _uM({
                    modelValue: paymentInfo.cardNumber,
                    onInput: [($event: UniInputEvent) => {(paymentInfo.cardNumber) = $event.detail.value}, formatCardNumber] as Array<any | null>,
                    type: "number",
                    placeholder: "1234 5678 9012 3456",
                    maxlength: "16",
                    "confirm-type": "next",
                    class: "input"
                  }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
                ]),
                _cE("view", _uM({ class: "span1" })),
                _cE("view", _uM({ class: "form_group" }), [
                  _cE("text", _uM({ class: "label" }), "Password"),
                  _cE("input", _uM({
                    modelValue: paymentInfo.cardPassword,
                    onInput: ($event: UniInputEvent) => {(paymentInfo.cardPassword) = $event.detail.value},
                    type: "password",
                    placeholder: "Enter payment password",
                    "confirm-type": "done",
                    class: "input"
                  }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
                ])
              ])
            : paymentMethodIndex.value === 1
              ? _cE("view", _uM({
                  key: 1,
                  class: "payment_form"
                }), [
                  _cE("view", _uM({ class: "form_section_title" }), [
                    _cE("text", _uM({ class: "section_title_text" }), "Alipay Account")
                  ]),
                  _cE("view", _uM({ class: "form_group" }), [
                    _cE("text", _uM({ class: "label" }), "Alipay Account"),
                    _cE("input", _uM({
                      modelValue: paymentInfo.alipayAccount,
                      onInput: ($event: UniInputEvent) => {(paymentInfo.alipayAccount) = $event.detail.value},
                      type: "text",
                      placeholder: "Enter phone number or email",
                      "confirm-type": "next",
                      class: "input"
                    }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
                  ]),
                  _cE("view", _uM({ class: "span1" })),
                  _cE("view", _uM({ class: "form_group" }), [
                    _cE("text", _uM({ class: "label" }), "Password"),
                    _cE("input", _uM({
                      modelValue: paymentInfo.alipayPassword,
                      onInput: ($event: UniInputEvent) => {(paymentInfo.alipayPassword) = $event.detail.value},
                      type: "password",
                      placeholder: "Enter payment password",
                      "confirm-type": "done",
                      class: "input"
                    }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
                  ])
                ])
              : paymentMethodIndex.value === 2
                ? _cE("view", _uM({
                    key: 2,
                    class: "payment_form"
                  }), [
                    _cE("view", _uM({ class: "info_box" }), [
                      _cE("text", _uM({ class: "info_text" }), "WeChat Pay"),
                      _cE("view", _uM({ class: "qr_code_placeholder" }), [
                        _cE("text", _uM({ class: "qr_hint" }), "QR Code")
                      ]),
                      _cE("text", _uM({ class: "info_desc" }), "Scan the QR code with WeChat to complete payment")
                    ])
                  ])
                : _cC("v-if", true),
          _cE("view", _uM({ class: "span2" })),
          _cE("button", _uM({
            onClick: onPayment,
            size: "default",
            disabled: isPaymentProcessing.value,
            class: _nC(["payment_button", _uM({ processing: isPaymentProcessing.value })]),
            style: _nS(_uM({"background-color":"#4CAF50","color":"white","border":"none"}))
          }), _tD(isPaymentProcessing.value ? 'Processing...' : `Pay £${orderAmount.value.toFixed(2)}`), 15 /* TEXT, CLASS, STYLE, PROPS */, ["disabled"]),
          _cE("view", _uM({ class: "span3" })),
          _cE("button", _uM({
            onClick: onCancel,
            size: "default",
            class: "cancel_button"
          }), " Cancel ")
        ])
      ])
    ]),
    _cE("view", _uM({ class: "bottom" }), [
      _cV(_component_navigator, _uM({
        url: "/pages/index/index",
        "open-type": "reLaunch",
        "hover-class": "navigator-hover",
        class: "bottom_left"
      }), _uM({
        default: withSlotCtx((): any[] => [
          _cE("image", _uM({
            src: "/static/home.png",
            mode: "aspectFit",
            class: "icon2"
          })),
          _cE("view", _uM({ class: "left_down" }), [
            _cE("text", _uM({ class: "text3" }), "Home")
          ])
        ]),
        _: 1 /* STABLE */
      })),
      _cV(_component_navigator, _uM({
        url: "/pages/bikelist/bikelist",
        "open-type": "reLaunch",
        "hover-class": "navigator-hover",
        class: "bottom_center"
      }), _uM({
        default: withSlotCtx((): any[] => [
          _cE("image", _uM({
            src: "/static/bike.png",
            mode: "aspectFit",
            class: "bike_icon"
          })),
          _cE("view", _uM({ class: "center_down" }), [
            _cE("text", _uM({ class: "text3" }), "Bike")
          ])
        ]),
        _: 1 /* STABLE */
      })),
      _cV(_component_navigator, _uM({
        url: "/pages/orders/orders",
        "open-type": "reLaunch",
        "hover-class": "navigator-hover",
        class: "bottom_right"
      }), _uM({
        default: withSlotCtx((): any[] => [
          _cE("image", _uM({
            src: "/static/me.png",
            mode: "aspectFit",
            class: "icon2"
          })),
          _cE("view", _uM({ class: "right_down" }), [
            _cE("text", _uM({ class: "text3" }), "Me")
          ])
        ]),
        _: 1 /* STABLE */
      }))
    ])
  ], 64 /* STABLE_FRAGMENT */)
}
}

})
export default __sfc__
const GenPagesPaymentPaymentStyles = [_uM([["back", _pS(_uM([["position", "relative"], ["width", "100%"], ["backgroundColor", "#f5f5f5"], ["display", "flex"], ["flexDirection", "column"], ["overflow", "hidden"], ["boxSizing", "border-box"], ["paddingBottom", "8%"]]))], ["backpic", _pS(_uM([["width", "100%"], ["height", "100%"], ["position", "absolute"], ["top", 0], ["left", 0], ["zIndex", 0], ["opacity", 0.25]]))], ["payment_title", _pS(_uM([["position", "relative"], ["zIndex", 1], ["marginTop", 20], ["marginBottom", 10], ["flexShrink", 0], ["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["title_text", _pS(_uM([["fontSize", 24], ["fontWeight", "bold"], ["color", "rgba(60,60,60,0.95)"], ["fontFamily", "Arial, sans-serif"], ["letterSpacing", 0.5], ["textAlign", "center"]]))], ["order_info", _pS(_uM([["position", "relative"], ["zIndex", 1], ["marginTop", 0], ["marginRight", 20], ["marginBottom", 10], ["marginLeft", 20], ["paddingTop", 12], ["paddingRight", 15], ["paddingBottom", 12], ["paddingLeft", 15], ["backgroundImage", "linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%)"], ["backgroundColor", "rgba(0,0,0,0)"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"], ["borderTopWidth", 1], ["borderRightWidth", 1], ["borderBottomWidth", 1], ["borderLeftWidth", 1], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "rgba(255,255,255,0.8)"], ["borderRightColor", "rgba(255,255,255,0.8)"], ["borderBottomColor", "rgba(255,255,255,0.8)"], ["borderLeftColor", "rgba(255,255,255,0.8)"], ["flexShrink", 0]]))], ["order_header", _pS(_uM([["marginBottom", 8], ["paddingBottom", 6], ["borderBottomWidth", 1], ["borderBottomStyle", "solid"], ["borderBottomColor", "#f0f0f0"]]))], ["order_label", _pS(_uM([["fontSize", 13], ["color", "#666666"], ["letterSpacing", 0.3]]))], ["order_item", _pS(_uM([["display", "flex"], ["justifyContent", "space-between"], ["alignItems", "center"]]))], ["label", _uM([[".order_item ", _uM([["fontSize", 13], ["color", "#666666"]])], [".form_group ", _uM([["fontSize", 14], ["color", "#333333"], ["marginBottom", 10], ["letterSpacing", 0.3]])], [".form_group_half ", _uM([["fontSize", 14], ["color", "#333333"], ["marginBottom", 10], ["letterSpacing", 0.3]])]])], ["amount", _uM([[".order_item ", _uM([["fontSize", 22], ["color", "#4CAF50"], ["fontWeight", "bold"], ["letterSpacing", 0.3]])]])], ["payment_scroll", _pS(_uM([["position", "relative"], ["zIndex", 1], ["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["overflowY", "scroll"], ["WebkitOverflowScrolling", "touch"], ["height", 0], ["minHeight", 0], ["width", "100%"], ["scrollBehavior", "smooth"]]))], ["payment_container", _pS(_uM([["marginTop", 0], ["marginRight", 20], ["marginBottom", 0], ["marginLeft", 20], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 500], ["paddingLeft", 20], ["backgroundImage", "linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%)"], ["backgroundColor", "rgba(0,0,0,0)"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["boxShadow", "0 4px 12px rgba(0, 0, 0, 0.1)"], ["borderTopWidth", 1], ["borderRightWidth", 1], ["borderBottomWidth", 1], ["borderLeftWidth", 1], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "rgba(255,255,255,0.8)"], ["borderRightColor", "rgba(255,255,255,0.8)"], ["borderBottomColor", "rgba(255,255,255,0.8)"], ["borderLeftColor", "rgba(255,255,255,0.8)"], ["boxSizing", "border-box"]]))], ["form_group", _pS(_uM([["marginBottom", 15]]))], ["form_section_title", _pS(_uM([["marginBottom", 15], ["paddingBottom", 10], ["borderBottomWidth", 1], ["borderBottomStyle", "solid"], ["borderBottomColor", "#e8e8e8"]]))], ["section_title_text", _pS(_uM([["fontSize", 16], ["color", "#4CAF50"], ["fontWeight", "bold"], ["letterSpacing", 0.5]]))], ["form_row", _pS(_uM([["display", "flex"], ["gap", "15px"], ["justifyContent", "space-between"]]))], ["form_group_half", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"]]))], ["input", _pS(_uM([["width", "100%"], ["paddingTop", 12], ["paddingRight", 15], ["paddingBottom", 12], ["paddingLeft", 15], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#e0e0e0"], ["borderRightColor", "#e0e0e0"], ["borderBottomColor", "#e0e0e0"], ["borderLeftColor", "#e0e0e0"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 15], ["boxSizing", "border-box"], ["backgroundColor", "#fafafa"], ["transitionProperty", "all"], ["transitionDuration", "0.3s"], ["transitionTimingFunction", "ease"], ["borderTopColor:focus", "#4CAF50"], ["borderRightColor:focus", "#4CAF50"], ["borderBottomColor:focus", "#4CAF50"], ["borderLeftColor:focus", "#4CAF50"], ["outline:focus", "none"], ["backgroundColor:focus", "#ffffff"], ["boxShadow:focus", "0 0 0 3px rgba(76, 175, 80, 0.1)"]]))], ["picker_view", _pS(_uM([["display", "flex"], ["justifyContent", "space-between"], ["alignItems", "center"], ["paddingTop", 8], ["paddingRight", 12], ["paddingBottom", 8], ["paddingLeft", 12], ["backgroundColor", "#fafafa"], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#e0e0e0"], ["borderRightColor", "#e0e0e0"], ["borderBottomColor", "#e0e0e0"], ["borderLeftColor", "#e0e0e0"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["minHeight", 36], ["transitionProperty", "all"], ["transitionDuration", "0.3s"], ["transitionTimingFunction", "ease"], ["backgroundColor:active", "#f0f0f0"], ["borderTopColor:active", "#4CAF50"], ["borderRightColor:active", "#4CAF50"], ["borderBottomColor:active", "#4CAF50"], ["borderLeftColor:active", "#4CAF50"]]))], ["picker_text", _pS(_uM([["fontSize", 13], ["color", "#333333"]]))], ["arrow_text", _pS(_uM([["fontSize", 10], ["color", "#999999"]]))], ["payment_form", _pS(_uM([["marginTop", 0], ["paddingTop", 0]]))], ["info_box", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 50], ["paddingRight", 20], ["paddingBottom", 50], ["paddingLeft", 20], ["backgroundImage", "linear-gradient(135deg, #f9f9f9 0%, #ffffff 100%)"], ["backgroundColor", "rgba(0,0,0,0)"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["marginTop", 15], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "dashed"], ["borderRightStyle", "dashed"], ["borderBottomStyle", "dashed"], ["borderLeftStyle", "dashed"], ["borderTopColor", "#e0e0e0"], ["borderRightColor", "#e0e0e0"], ["borderBottomColor", "#e0e0e0"], ["borderLeftColor", "#e0e0e0"]]))], ["info_text", _pS(_uM([["fontSize", 20], ["color", "#4CAF50"], ["fontWeight", "bold"], ["marginBottom", 20], ["letterSpacing", 0.5]]))], ["qr_code_placeholder", _pS(_uM([["width", 180], ["height", 180], ["backgroundColor", "#f0f0f0"], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "dashed"], ["borderRightStyle", "dashed"], ["borderBottomStyle", "dashed"], ["borderLeftStyle", "dashed"], ["borderTopColor", "#cccccc"], ["borderRightColor", "#cccccc"], ["borderBottomColor", "#cccccc"], ["borderLeftColor", "#cccccc"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["display", "flex"], ["alignItems", "center"], ["justifyContent", "center"], ["marginBottom", 20]]))], ["qr_hint", _pS(_uM([["fontSize", 14], ["color", "#999999"]]))], ["info_desc", _pS(_uM([["fontSize", 13], ["color", "#666666"], ["textAlign", "center"], ["lineHeight", 1.6]]))], ["span1", _pS(_uM([["height", 10]]))], ["span2", _pS(_uM([["height", 15]]))], ["span3", _pS(_uM([["height", 10]]))], ["payment_button", _uM([["", _uM([["width", "100%"], ["height", 50], ["!backgroundColor", "#4CAF50"], ["!color", "#FFFFFF"], ["!borderTopWidth", "medium"], ["!borderRightWidth", "medium"], ["!borderBottomWidth", "medium"], ["!borderLeftWidth", "medium"], ["!borderTopStyle", "none"], ["!borderRightStyle", "none"], ["!borderBottomStyle", "none"], ["!borderLeftStyle", "none"], ["!borderTopColor", "#000000"], ["!borderRightColor", "#000000"], ["!borderBottomColor", "#000000"], ["!borderLeftColor", "#000000"], ["borderTopLeftRadius", 10], ["borderTopRightRadius", 10], ["borderBottomRightRadius", 10], ["borderBottomLeftRadius", 10], ["fontSize", 17], ["fontWeight", "bold"], ["letterSpacing", 0.5], ["boxShadow", "0 4px 12px rgba(76, 175, 80, 0.4)"], ["transitionProperty", "all"], ["transitionDuration", "0.3s"], ["transitionTimingFunction", "ease"], ["transform:active", "scale(0.98)"], ["boxShadow:active", "0 2px 8px rgba(76, 175, 80, 0.2)"]])], [".processing", _uM([["backgroundImage", "linear-gradient(135deg, #81c784 0%, #66bb6a 100%)"], ["backgroundColor", "rgba(0,0,0,0)"], ["opacity", 0.8]])]])], ["cancel_button", _pS(_uM([["width", "100%"], ["height", 50], ["backgroundColor", "rgba(255,255,255,0.9)"], ["color", "#666666"], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#e0e0e0"], ["borderRightColor", "#e0e0e0"], ["borderBottomColor", "#e0e0e0"], ["borderLeftColor", "#e0e0e0"], ["borderTopLeftRadius", 10], ["borderTopRightRadius", 10], ["borderBottomRightRadius", 10], ["borderBottomLeftRadius", 10], ["fontSize", 16], ["letterSpacing", 0.3], ["transitionProperty", "all"], ["transitionDuration", "0.3s"], ["transitionTimingFunction", "ease"], ["backgroundColor:active", "#f5f5f5"], ["borderTopColor:active", "#cccccc"], ["borderRightColor:active", "#cccccc"], ["borderBottomColor:active", "#cccccc"], ["borderLeftColor:active", "#cccccc"], ["transform:active", "scale(0.98)"]]))], ["bottom", _pS(_uM([["width", "100%"], ["height", "8%"], ["backgroundImage", "none"], ["backgroundColor", "#E3E1E1"], ["position", "absolute"], ["bottom", 0], ["borderTopLeftRadius", 25], ["borderTopRightRadius", 25], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["zIndex", 100]]))], ["bottom_left", _pS(_uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginLeft", "5%"], ["position", "absolute"], ["left", 0]]))], ["icon2", _uM([[".bottom_left ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])], [".bottom_right ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])]])], ["left_down", _uM([[".bottom_left ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_center", _pS(_uM([["position", "absolute"], ["left", "40%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"]]))], ["bike_icon", _uM([[".bottom_center ", _uM([["position", "absolute"], ["left", "25%"], ["width", "50%"], ["height", "60%"]])]])], ["center_down", _uM([[".bottom_center ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_right", _pS(_uM([["position", "absolute"], ["right", "0%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginRight", "5%"]]))], ["right_down", _uM([[".bottom_right ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["text3", _pS(_uM([["fontSize", 12], ["marginTop", 4]]))], ["@TRANSITION", _uM([["input", _uM([["property", "all"], ["duration", "0.3s"], ["timingFunction", "ease"]])], ["picker_view", _uM([["property", "all"], ["duration", "0.3s"], ["timingFunction", "ease"]])], ["payment_button", _uM([["property", "all"], ["duration", "0.3s"], ["timingFunction", "ease"]])], ["cancel_button", _uM([["property", "all"], ["duration", "0.3s"], ["timingFunction", "ease"]])]])]])]
