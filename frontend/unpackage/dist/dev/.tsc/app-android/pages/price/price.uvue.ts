import { ref } from 'vue';

// 定义类型
type DurationItem = { __$originalPosition?: UTSSourceMapPosition<"DurationItem", "pages/price/price.uvue", 35, 6>;
	time: string;
	price: number;
}

// 预订时长数据

const __sfc__ = defineComponent({
  __name: 'price',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const durations = ref<DurationItem[]>([
	{ time: '1小时', price: 1 },
	{ time: '4小时', price: 1 },
	{ time: '1天', price: 1 },
	{ time: '1周', price: 1 }
]);

// 当前选中的索引
const selectedIndex = ref<number>(-1);

// 选择时长
const selectDuration = (index: number) => {
	selectedIndex.value = index;
};

// 确认选择
const confirm = () => {
	if (selectedIndex.value !== -1) {
		const selectedDuration = durations.value[selectedIndex.value];
		console.log('选择了', selectedDuration.time, '费用', selectedDuration.price, '英镑', " at pages/price/price.uvue:60");
		uni.showToast({
			title: `已选择${selectedDuration.time}`,
			icon: 'success'
		});
	} else {
		uni.showToast({
			title: '请选择预订时长',
			icon: 'none'
		});
	}
};

// 取消选择
const cancel = () => {
	selectedIndex.value = -1;
	uni.showToast({
		title: '已取消',
		icon: 'none'
	});
	// 返回首页
	uni.reLaunch({
		url: '/pages/index/index'
	});
};

return (): any | null => {

  return _cE("view", _uM({ class: "container" }), [
    _cE("view", _uM({ class: "title" }), [
      _cE("text", _uM({ class: "title-text" }), "选择预订时长")
    ]),
    _cE("view", _uM({ class: "duration-cards" }), [
      _cE(Fragment, null, RenderHelpers.renderList(durations.value, (duration, index, __index, _cached): any => {
        return _cE("view", _uM({
          key: index,
          class: _nC(["card", _uM({ selected: selectedIndex.value === index })]),
          onClick: () => {selectDuration(index)}
        }), [
          _cE("view", _uM({ class: "card-header" }), [
            _cE("text", _uM({ class: "duration" }), _tD(duration.time), 1 /* TEXT */)
          ]),
          _cE("view", _uM({ class: "card-body" }), [
            _cE("text", _uM({ class: "price" }), "£" + _tD(duration.price), 1 /* TEXT */)
          ])
        ], 10 /* CLASS, PROPS */, ["onClick"])
      }), 128 /* KEYED_FRAGMENT */)
    ]),
    _cE("view", _uM({ class: "button-group" }), [
      _cE("button", _uM({
        class: "confirm-btn",
        onClick: confirm
      }), "确定"),
      _cE("button", _uM({
        class: "cancel-btn",
        onClick: cancel
      }), "取消")
    ])
  ])
}
}

})
export default __sfc__
const GenPagesPricePriceStyles = [_uM([["container", _pS(_uM([["width", "100%"], ["height", "100%"], ["backgroundColor", "#f5f5f5"], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["boxSizing", "border-box"]]))], ["title", _pS(_uM([["textAlign", "center"], ["marginBottom", 30]]))], ["title-text", _pS(_uM([["fontSize", 24], ["fontWeight", "bold"], ["color", "#333333"]]))], ["duration-cards", _pS(_uM([["display", "flex"], ["flexDirection", "column"]]))], ["card", _uM([["", _uM([["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["boxShadow", "0 4px 12px rgba(0, 0, 0, 0.1)"], ["transitionProperty", "all"], ["transitionDuration", "0.3s"], ["transitionTimingFunction", "ease"], ["marginBottom", 15]])], [".selected", _uM([["backgroundColor", "#e8f5e9"], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#4CAF50"], ["borderRightColor", "#4CAF50"], ["borderBottomColor", "#4CAF50"], ["borderLeftColor", "#4CAF50"], ["transform", "translateY(-2px)"], ["boxShadow", "0 6px 16px rgba(76, 175, 80, 0.2)"]])]])], ["card-header", _pS(_uM([["textAlign", "center"], ["marginBottom", 10]]))], ["duration", _pS(_uM([["fontSize", 18], ["fontWeight", "bold"], ["color", "#333333"]]))], ["card-body", _pS(_uM([["textAlign", "center"]]))], ["price", _pS(_uM([["fontSize", 20], ["fontWeight", "bold"], ["color", "#4CAF50"]]))], ["button-group", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["marginTop", 40]]))], ["confirm-btn", _pS(_uM([["width", "80%"], ["height", 45], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"], ["marginBottom", 15]]))], ["cancel-btn", _pS(_uM([["width", "80%"], ["height", 45], ["backgroundColor", "#f44336"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]]))], ["@TRANSITION", _uM([["card", _uM([["property", "all"], ["duration", "0.3s"], ["timingFunction", "ease"]])]])]])]
