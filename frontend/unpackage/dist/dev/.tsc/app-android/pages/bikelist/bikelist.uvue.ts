import { ref } from 'vue';

// 选择状态

const __sfc__ = defineComponent({
  __name: 'bikelist',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const isSelected = ref(false);
// 滑板车ID
const scooterId = ref('SC001');

// 切换选择状态
const toggleSelection = () => {
	isSelected.value = !isSelected.value;
};

// 预订滑板车
const bookScooter = () => {
	if (isSelected.value) {
		console.log('预订滑板车 ID:', scooterId.value, " at pages/bikelist/bikelist.uvue:63");
		// 这里可以添加预订逻辑
		uni.showToast({
			title: 'appoint successfully',
			icon: 'success'
		});
	}
};

return (): any | null => {

const _component_navigator = resolveComponent("navigator")

  return _cE("view", _uM({ class: "container" }), [
    _cE("view", _uM({ class: "scooter-card" }), [
      _cE("image", _uM({
        src: "/static/bikelist.jpg",
        mode: "aspectFit",
        class: "scooter-image"
      })),
      _cE("view", _uM({ class: "card-content" }), [
        _cE("text", _uM({ class: "scooter-id" }), "ID: " + _tD(scooterId.value), 1 /* TEXT */)
      ]),
      _cE("view", _uM({
        class: "checkbox-container",
        onClick: toggleSelection
      }), [
        _cE("view", _uM({
          class: _nC(["checkbox", _uM({ selected: isSelected.value })])
        }), [
          isTrue(isSelected.value)
            ? _cE("text", _uM({
                key: 0,
                class: "checkmark"
              }), "✓")
            : _cC("v-if", true)
        ], 2 /* CLASS */)
      ])
    ]),
    _cE("view", _uM({ class: "booking-section" }), [
      _cV(_component_navigator, _uM({ url: "/pages/price/price" }), _uM({
        default: withSlotCtx((): any[] => [
          _cE("button", _uM({
            class: _nC(['booking-button', _uM({ disabled: !isSelected.value })]),
            disabled: !isSelected.value,
            onClick: bookScooter
          }), " appoint ", 10 /* CLASS, PROPS */, ["disabled"])
        ]),
        _: 1 /* STABLE */
      }))
    ]),
    _cE("view", _uM({ class: "bottom" }), [
      _cV(_component_navigator, _uM({
        url: "/pages/index/index",
        "open-type": "reLaunch",
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
      _cE("view", _uM({ class: "bottom_center" }), [
        _cE("image", _uM({
          src: "/static/bike.png",
          mode: "aspectFit",
          class: "bike_icon"
        })),
        _cE("view", _uM({ class: "center_down" }), [
          _cE("text", _uM({ class: "text3" }), "Bike")
        ])
      ]),
      _cV(_component_navigator, _uM({
        url: "/pages/login/login",
        class: "bottom_right",
        "open-type": "reLaunch"
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
  ])
}
}

})
export default __sfc__
const GenPagesBikelistBikelistStyles = [_uM([["container", _pS(_uM([["width", "100%"], ["height", "100%"], ["paddingBottom", "8%"], ["boxSizing", "border-box"], ["backgroundColor", "#f5f5f5"]]))], ["scooter-card", _pS(_uM([["width", "90%"], ["marginTop", 20], ["marginRight", "auto"], ["marginBottom", 20], ["marginLeft", "auto"], ["paddingTop", 15], ["paddingRight", 15], ["paddingBottom", 15], ["paddingLeft", 15], ["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["boxShadow", "0 4px 12px rgba(0, 0, 0, 0.1)"], ["display", "flex"], ["alignItems", "center"]]))], ["scooter-image", _uM([[".scooter-card ", _uM([["width", 100], ["height", 100], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["marginRight", 15]])]])], ["card-content", _uM([[".scooter-card ", _uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"]])]])], ["scooter-id", _uM([[".scooter-card .card-content ", _uM([["fontSize", 18], ["fontWeight", "bold"], ["color", "#333333"]])]])], ["checkbox-container", _uM([[".scooter-card ", _uM([["width", 30], ["height", 30]])]])], ["checkbox", _uM([[".scooter-card .checkbox-container ", _uM([["width", "100%"], ["height", "100%"], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#cccccc"], ["borderRightColor", "#cccccc"], ["borderBottomColor", "#cccccc"], ["borderLeftColor", "#cccccc"], ["borderTopLeftRadius", 4], ["borderTopRightRadius", 4], ["borderBottomRightRadius", 4], ["borderBottomLeftRadius", 4], ["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["backgroundColor", "#FFFFFF"]])], [".scooter-card .checkbox-container .selected", _uM([["backgroundColor", "#4CAF50"], ["borderTopColor", "#4CAF50"], ["borderRightColor", "#4CAF50"], ["borderBottomColor", "#4CAF50"], ["borderLeftColor", "#4CAF50"]])]])], ["checkmark", _uM([[".scooter-card .checkbox-container .checkbox ", _uM([["color", "#FFFFFF"], ["fontSize", 16], ["fontWeight", "bold"]])]])], ["booking-section", _pS(_uM([["width", "90%"], ["marginTop", 20], ["marginRight", "auto"], ["marginBottom", 20], ["marginLeft", "auto"], ["display", "flex"], ["justifyContent", "center"]]))], ["booking-button", _uM([[".booking-section ", _uM([["width", "100%"], ["height", 45], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]])], [".booking-section .disabled", _uM([["backgroundColor", "#cccccc"], ["color", "#666666"]])]])], ["bottom", _pS(_uM([["width", "100%"], ["height", "8%"], ["backgroundImage", "none"], ["backgroundColor", "#E3E1E1"], ["position", "absolute"], ["bottom", 0], ["borderTopLeftRadius", 25], ["borderTopRightRadius", 25], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0]]))], ["bottom_left", _uM([[".bottom ", _uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginLeft", "5%"]])]])], ["icon2", _uM([[".bottom .bottom_left ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])], [".bottom .bottom_right ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])]])], ["left_down", _uM([[".bottom .bottom_left ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_center", _uM([[".bottom ", _uM([["position", "absolute"], ["left", "40%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"]])]])], ["bike_icon", _uM([[".bottom .bottom_center ", _uM([["position", "absolute"], ["left", "25%"], ["width", "50%"], ["height", "60%"]])]])], ["center_down", _uM([[".bottom .bottom_center ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_right", _uM([[".bottom ", _uM([["position", "absolute"], ["right", "0%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginRight", "5%"]])]])], ["right_down", _uM([[".bottom .bottom_right ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])]])]
