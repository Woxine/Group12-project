import { ref } from 'vue';

// 用户类型选项

const __sfc__ = defineComponent({
  __name: 'register',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const userTypeOptions = ref<string[]>(['customer', 'staff', 'admin']);
const selectedUserType = ref<string>('');
const showTypeSelector = ref<boolean>(false);
const isStudent = ref<boolean>(false);

// 切换用户类型选择器显示状态
const toggleTypeSelector = (): void => {
	showTypeSelector.value = !showTypeSelector.value;
};

// 选择用户类型
const selectUserType = (type: string): void => {
	selectedUserType.value = type;
	showTypeSelector.value = false;
};

// 学生身份切换事件
const onStudentToggleChange = (e: any): void => {
	// 在 UTS 中，switch 的 change 事件参数结构为 e.value
	isStudent.value = (e as UTSJSONObject).value as boolean;
};

return (): any | null => {

const _component_switch = resolveComponent("switch")
const _component_navigator = resolveComponent("navigator")

  return _cE(Fragment, null, [
    _cE("view", _uM({ class: "back" }), [
      _cE("image", _uM({
        src: "/static/login_back1.jpg",
        mode: "",
        class: "backpic"
      })),
      _cE("view", _uM({ class: "login_title" }), [
        _cE("text", _uM({ class: "title_text" }), "register")
      ]),
      _cE("view", _uM({ class: "login_container" }), [
        _cE("input", _uM({
          type: "text",
          placeholder: "please enter account",
          "confirm-type": "next",
          class: "input"
        })),
        _cE("view", _uM({ class: "span1" })),
        _cE("input", _uM({
          type: "text",
          placeholder: "please enter password",
          class: "input"
        })),
        _cE("view", _uM({ class: "span2" })),
        _cE("input", _uM({
          type: "text",
          placeholder: "please check password",
          class: "input"
        })),
        _cE("view", _uM({ class: "span2" })),
        _cE("view", _uM({ class: "type-selector-container" }), [
          _cE("view", _uM({
            class: "selector-header",
            onClick: toggleTypeSelector
          }), [
            _cE("text", _uM({ class: "selector-text" }), _tD(selectedUserType.value !== '' ? selectedUserType.value : 'select user type'), 1 /* TEXT */),
            _cE("text", _uM({ class: "arrow" }), _tD(showTypeSelector.value ? '▲' : '▼'), 1 /* TEXT */)
          ]),
          isTrue(showTypeSelector.value)
            ? _cE("view", _uM({
                key: 0,
                class: "selector-dropdown"
              }), [
                _cE(Fragment, null, RenderHelpers.renderList(userTypeOptions.value, (type, index, __index, _cached): any => {
                  return _cE("view", _uM({
                    key: index,
                    class: "selector-option",
                    onClick: () => {selectUserType(type)}
                  }), [
                    _cE("text", _uM({ class: "selector-option" }), _tD(type), 1 /* TEXT */)
                  ], 8 /* PROPS */, ["onClick"])
                }), 128 /* KEYED_FRAGMENT */)
              ])
            : _cC("v-if", true)
        ]),
        _cE("view", _uM({ class: "span2" })),
        _cE("view", _uM({ class: "student-toggle" }), [
          _cE("text", _uM({ class: "student-label" }), "Are you a student?"),
          _cV(_component_switch, _uM({
            checked: isStudent.value,
            onChange: onStudentToggleChange,
            color: "#3c3c3c",
            style: _nS(_uM({"transform":"scale(0.7)"}))
          }), null, 8 /* PROPS */, ["checked", "style"])
        ]),
        _cE("view", _uM({ class: "span2" })),
        _cV(_component_navigator, _uM({
          url: "/pages/register/register",
          "open-type": "reLaunch"
        }), _uM({
          default: withSlotCtx((): any[] => [
            _cE("button", _uM({
              size: "default",
              style: _nS(_uM({"color":"white","background-color":"rgba(60, 60, 60, 0.7)"}))
            }), "sign up", 4 /* STYLE */)
          ]),
          _: 1 /* STABLE */
        }))
      ])
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
      _cV(_component_navigator, _uM({
        url: "/pages/bikelist/bikelist",
        "open-type": "reLaunch",
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
      _cE("view", _uM({ class: "bottom_right" }), [
        _cE("image", _uM({
          src: "/static/me.png",
          mode: "aspectFit",
          class: "icon2"
        })),
        _cE("view", _uM({ class: "right_down" }), [
          _cE("text", _uM({ class: "text3" }), "Me")
        ])
      ])
    ])
  ], 64 /* STABLE_FRAGMENT */)
}
}

})
export default __sfc__
const GenPagesRegisterRegisterStyles = [_uM([["back", _pS(_uM([["width", "100%"], ["height", "100%"]]))], ["backpic", _uM([[".back ", _uM([["width", "100%"], ["height", "100%"]])]])], ["login_title", _uM([[".back ", _uM([["position", "absolute"], ["top", 0], ["left", "50%"], ["transform", "translateX(-50%)"], ["zIndex", 10], ["paddingTop", 10], ["paddingRight", 10], ["paddingBottom", 10], ["paddingLeft", 10], ["backgroundColor", "rgba(0,0,0,0.3)"]])]])], ["title_text", _uM([[".back .login_title ", _uM([["height", "100%"], ["fontSize", 32], ["fontWeight", "bold"], ["color", "#FFFFFF"], ["textShadow", "1px 1px 3px rgba(0, 0, 0, 0.5)"]])]])], ["login_container", _uM([[".back ", _uM([["position", "absolute"], ["top", "50%"], ["left", "50%"], ["transform", "translate(-50%, -50%)"], ["width", 300], ["paddingTop", 30], ["paddingRight", 30], ["paddingBottom", 30], ["paddingLeft", 30], ["backgroundColor", "rgba(227,225,225,0.46)"], ["borderTopLeftRadius", 15], ["borderTopRightRadius", 15], ["borderBottomRightRadius", 15], ["borderBottomLeftRadius", 15], ["boxShadow", "0 10px 30px rgba(0, 0, 0, 0.3)"]])]])], ["input", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", "10%"], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["backgroundColor", "rgba(60,60,60,0.7)"], ["color", "#FFFFFF"], ["fontSize", 16]])]])], ["span1", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", 10], ["backgroundColor", "#00FFFF"]])]])], ["span2", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", 10], ["backgroundColor", "#00FFFF"]])]])], ["span3", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", 10], ["backgroundColor", "#00FFFF"]])]])], ["type-selector-container", _uM([[".back .login_container ", _uM([["width", "100%"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["backgroundColor", "rgba(60,60,60,0.7)"], ["color", "#FFFFFF"], ["fontSize", 16], ["position", "relative"]])]])], ["selector-header", _uM([[".back .login_container .type-selector-container ", _uM([["width", "100%"], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["display", "flex"], ["justifyContent", "space-between"], ["alignItems", "center"]])]])], ["selector-text", _uM([[".back .login_container .type-selector-container .selector-header ", _uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["color", "#FFFFFF"]])]])], ["arrow", _uM([[".back .login_container .type-selector-container .selector-header ", _uM([["color", "#FFFFFF"], ["fontSize", 12]])]])], ["selector-dropdown", _uM([[".back .login_container .type-selector-container ", _uM([["width", "100%"], ["maxHeight", "150rpx"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["backgroundColor", "rgba(60,60,60,0.9)"], ["zIndex", 100]])]])], ["selector-option", _uM([[".back .login_container .type-selector-container .selector-dropdown ", _uM([["width", "100%"], ["paddingTop", 1.5], ["paddingRight", 1.5], ["paddingBottom", 1.5], ["paddingLeft", 1.5], ["color", "#FFFFFF"], ["backgroundColor", "rgba(60,60,60,0.7)"], ["borderBottomWidth:last-child", "medium"], ["borderBottomStyle:last-child", "none"], ["borderBottomColor:last-child", "#000000"]])]])], ["student-toggle", _uM([[".back .login_container ", _uM([["width", "100%"], ["paddingTop", "20rpx"], ["paddingRight", "20rpx"], ["paddingBottom", "20rpx"], ["paddingLeft", "20rpx"], ["display", "flex"], ["justifyContent", "space-between"], ["alignItems", "center"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["backgroundColor", "rgba(60,60,60,0.7)"], ["color", "#FFFFFF"], ["fontSize", 16]])]])], ["student-label", _uM([[".back .login_container .student-toggle ", _uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["color", "#FFFFFF"]])]])], ["bottom", _pS(_uM([["width", "100%"], ["height", "8%"], ["backgroundImage", "none"], ["backgroundColor", "#E3E1E1"], ["position", "absolute"], ["bottom", 0], ["borderTopLeftRadius", 25], ["borderTopRightRadius", 25], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0]]))], ["bottom_left", _uM([[".bottom ", _uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginLeft", "5%"]])]])], ["icon2", _uM([[".bottom .bottom_left ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])], [".bottom .bottom_right ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])]])], ["left_down", _uM([[".bottom .bottom_left ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_center", _uM([[".bottom ", _uM([["position", "absolute"], ["left", "40%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"]])]])], ["bike_icon", _uM([[".bottom .bottom_center ", _uM([["position", "absolute"], ["left", "25%"], ["width", "50%"], ["height", "60%"]])]])], ["center_down", _uM([[".bottom .bottom_center ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_right", _uM([[".bottom ", _uM([["position", "absolute"], ["right", "0%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginRight", "5%"]])]])], ["right_down", _uM([[".bottom .bottom_right ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])]])]
