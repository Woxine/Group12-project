import { ref, onMounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { BASE_URL } from '../../common/api.uts';

type Scooter = { __$originalPosition?: UTSSourceMapPosition<"Scooter", "pages/bikelist/bikelist.uvue", 66, 6>;
	id: string
	rented: boolean
}

// 滑板车列表（从接口 /api/v1/scooters 同步）

const __sfc__ = defineComponent({
  __name: 'bikelist',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const scooterList = ref<Scooter[]>([]);
const isLoading = ref(false);
const loadError = ref('');
const isRefreshing = ref(false);

const fetchScooters = (fromPullRefresh: boolean) => {
	if (fromPullRefresh === false) {
		isLoading.value = true;
	}
	loadError.value = '';
	uni.request({
		url: BASE_URL + '/api/v1/scooters',
		method: 'GET',
		success: (res) => {
			const ok = (res.statusCode === 200) && (res.data !== null);
			if (ok) {
				const raw = res.data as UTSJSONObject;
				let arr: UTSJSONObject[] = [];
				const hasData = raw['data'] !== null;
				const hasScooters = raw['scooters'] !== null;
				const hasList = raw['list'] !== null;
				if (hasData) {
					arr = raw['data'] as UTSJSONObject[];
				} else if (hasScooters) {
					arr = raw['scooters'] as UTSJSONObject[];
				} else if (Array.isArray(raw)) {
					arr = raw as UTSJSONObject[];
				} else if (hasList) {
					arr = raw['list'] as UTSJSONObject[];
				}
				const list: Scooter[] = [];
				for (let i = 0; i < arr.length; i++) {
					const item = arr[i];
					const rawId = item['id'];
					let id: string = '';
					if (rawId !== null) {
						id = '' + rawId;
					}
					let rented: boolean = false;
					const rawRented = item['rented'];
					const rawStatus = item['status'];
					const rawIsRented = item['is_rented'];
					const rawIsAvailable = item['is_available'];
					const rawAvailable = item['available'];
					const rawScooterStatus = item['scooter_status'];
					const rawAvailability = item['availability'];
					if (rawRented !== null) {
						const v = rawRented;
						rented = (v === true || v === 1 || ('' + v).toLowerCase() === 'true');
					} else if (rawIsRented !== null) {
						const v = rawIsRented;
						rented = (v === true || v === 1 || ('' + v).toLowerCase() === 'true');
					} else if (rawStatus !== null) {
						const s = ('' + rawStatus).toLowerCase();
						rented = (s === 'rented' || s === 'in_use' || s === 'in-use' || s === 'booked' || s === 'occupied');
					} else if (rawScooterStatus !== null) {
						const s = ('' + rawScooterStatus).toLowerCase();
						rented = (s === 'rented' || s === 'in_use' || s === 'in-use' || s === 'booked' || s === 'occupied');
					} else if (rawAvailability !== null) {
						const s = ('' + rawAvailability).toLowerCase();
						rented = (s === 'unavailable' || s === 'rented' || s === 'in_use');
					} else if (rawIsAvailable !== null) {
						const v = rawIsAvailable;
						rented = (v === false || v === 0 || ('' + v).toLowerCase() === 'false');
					} else if (rawAvailable !== null) {
						const v = rawAvailable;
						rented = (v === false || v === 0 || ('' + v).toLowerCase() === 'false');
					}
					list.push({ id, rented });
				}
				scooterList.value = list;
			} else {
				const hasSc: boolean = (res.statusCode !== null);
				const sc = hasSc ? ('' + res.statusCode) : '';
				loadError.value = 'Failed to load scooters (status ' + sc + ')';
			}
		},
		fail: (err) => {
			loadError.value = 'Network error. Please check server.';
			console.error('Fetch scooters failed:', err, " at pages/bikelist/bikelist.uvue:151");
		},
		complete: () => {
			isLoading.value = false;
			isRefreshing.value = false;
		}
	});
};

const onPullRefresh = () => {
	isRefreshing.value = true;
	fetchScooters(true);
};

onMounted(() => {
	fetchScooters(false);
});

// 每次进入/返回本页时重新拉取列表，保证状态与后端同步（如 RENTED）
onShow(() => {
	fetchScooters(false);
});

// 选中的滑板车索引
const selectedIndex = ref<number>(-1);

// 切换选择状态
const toggleSelection = (index: number) => {
	if (selectedIndex.value === index) {
		selectedIndex.value = -1;
	} else {
		selectedIndex.value = index;
	}
};

// 预订滑板车
const bookScooter = () => {
	if (selectedIndex.value !== -1) {
		const selectedScooter = scooterList.value[selectedIndex.value]
		console.log('预订滑板车 ID:', selectedScooter.id, " at pages/bikelist/bikelist.uvue:190");
		
		// 保存选中的自行车ID到本地存储
		try {
			uni.setStorageSync('selectedScooterId', selectedScooter.id)
			console.log('Selected scooter ID saved:', selectedScooter.id, " at pages/bikelist/bikelist.uvue:195")
		} catch (e) {
			console.error('Failed to save selected scooter ID:', e, " at pages/bikelist/bikelist.uvue:197")
		}
		
		uni.showToast({
			title: 'appoint ' + selectedScooter.id + ' successfully',
			icon: 'success'
		});
	}
};

return (): any | null => {

const _component_navigator = resolveComponent("navigator")

  return _cE("view", _uM({ class: "page-wrapper" }), [
    _cE("scroll-view", _uM({
      class: "scroll-container",
      "scroll-y": "true",
      "refresher-enabled": "true",
      "refresher-triggered": isRefreshing.value,
      onRefresherrefresh: onPullRefresh
    }), [
      isTrue(isLoading.value)
        ? _cE("view", _uM({
            key: 0,
            class: "loading-section"
          }), [
            _cE("text", _uM({ class: "loading-text" }), "Loading scooters...")
          ])
        : loadError.value.length > 0
          ? _cE("view", _uM({
              key: 1,
              class: "error-section"
            }), [
              _cE("text", _uM({ class: "error-text" }), _tD(loadError.value), 1 /* TEXT */),
              _cE("button", _uM({
                class: "retry-btn",
                onClick: fetchScooters
              }), "Retry")
            ])
          : _cE(Fragment, _uM({ key: 2 }), RenderHelpers.renderList(scooterList.value, (scooter, index, __index, _cached): any => {
              return _cE("view", _uM({
                key: index,
                class: "scooter-card"
              }), [
                _cE("image", _uM({
                  src: "/static/bikelist.jpg",
                  mode: "aspectFit",
                  class: "scooter-image"
                })),
                _cE("view", _uM({ class: "card-content" }), [
                  _cE("text", _uM({ class: "scooter-id" }), "ID: " + _tD(scooter.id), 1 /* TEXT */),
                  _cE("text", _uM({ class: "scooter-status" }), _tD(scooter.rented ? 'Rented' : 'Available'), 1 /* TEXT */)
                ]),
                _cE("view", _uM({
                  class: "checkbox-container",
                  onClick: () => {toggleSelection(index)}
                }), [
                  _cE("view", _uM({
                    class: _nC(["checkbox", _uM({ selected: selectedIndex.value === index })])
                  }), [
                    selectedIndex.value === index
                      ? _cE("text", _uM({
                          key: 0,
                          class: "checkmark"
                        }), "✓")
                      : _cC("v-if", true)
                  ], 2 /* CLASS */)
                ], 8 /* PROPS */, ["onClick"])
              ])
            }), 128 /* KEYED_FRAGMENT */),
      _cE("view", _uM({ class: "booking-section" }), [
        _cV(_component_navigator, _uM({ url: "/pages/price/price" }), _uM({
          default: withSlotCtx((): any[] => [
            _cE("button", _uM({
              class: _nC(["booking-button", _uM({ disabled: selectedIndex.value === -1 })]),
              disabled: selectedIndex.value === -1,
              onClick: bookScooter
            }), " appoint ", 10 /* CLASS, PROPS */, ["disabled"])
          ]),
          _: 1 /* STABLE */
        }))
      ])
    ], 40 /* PROPS, NEED_HYDRATION */, ["refresher-triggered"]),
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
        url: "/pages/me/me",
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
const GenPagesBikelistBikelistStyles = [_uM([["page-wrapper", _pS(_uM([["width", "100%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["backgroundColor", "#f5f5f5"]]))], ["scroll-container", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["width", "100%"]]))], ["loading-section", _pS(_uM([["paddingTop", 40], ["paddingRight", 20], ["paddingBottom", 40], ["paddingLeft", 20], ["textAlign", "center"]]))], ["error-section", _pS(_uM([["paddingTop", 40], ["paddingRight", 20], ["paddingBottom", 40], ["paddingLeft", 20], ["textAlign", "center"]]))], ["loading-text", _pS(_uM([["fontSize", 14], ["color", "#999999"]]))], ["error-text", _pS(_uM([["fontSize", 14], ["color", "#f44336"], ["display", "flex"], ["marginBottom", 16]]))], ["retry-btn", _pS(_uM([["paddingTop", 10], ["paddingRight", 24], ["paddingBottom", 10], ["paddingLeft", 24], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14]]))], ["container", _pS(_uM([["width", "100%"], ["backgroundColor", "#f5f5f5"]]))], ["scooter-card", _pS(_uM([["width", "90%"], ["marginTop", 20], ["marginRight", "auto"], ["marginBottom", 20], ["marginLeft", "auto"], ["paddingTop", 15], ["paddingRight", 15], ["paddingBottom", 15], ["paddingLeft", 15], ["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["boxShadow", "0 4px 12px rgba(0, 0, 0, 0.1)"], ["display", "flex"], ["alignItems", "center"]]))], ["scooter-image", _uM([[".scooter-card ", _uM([["width", 100], ["height", 100], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["marginRight", 15]])]])], ["card-content", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["display", "flex"], ["flexDirection", "column"]]))], ["scooter-id", _uM([[".card-content ", _uM([["fontSize", 18], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 5]])]])], ["scooter-status", _uM([[".card-content ", _uM([["fontSize", 14], ["color", "#666666"], ["marginBottom", 5]])]])], ["scooter-location", _uM([[".card-content ", _uM([["fontSize", 12], ["color", "#999999"]])]])], ["checkbox-container", _pS(_uM([["width", 30], ["height", 30]]))], ["checkbox", _uM([[".checkbox-container ", _uM([["width", "100%"], ["height", "100%"], ["borderTopWidth", 2], ["borderRightWidth", 2], ["borderBottomWidth", 2], ["borderLeftWidth", 2], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#cccccc"], ["borderRightColor", "#cccccc"], ["borderBottomColor", "#cccccc"], ["borderLeftColor", "#cccccc"], ["borderTopLeftRadius", 4], ["borderTopRightRadius", 4], ["borderBottomRightRadius", 4], ["borderBottomLeftRadius", 4], ["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["backgroundColor", "#FFFFFF"]])], [".checkbox-container .selected", _uM([["backgroundColor", "#4CAF50"], ["borderTopColor", "#4CAF50"], ["borderRightColor", "#4CAF50"], ["borderBottomColor", "#4CAF50"], ["borderLeftColor", "#4CAF50"]])]])], ["checkmark", _uM([[".checkbox-container .checkbox ", _uM([["color", "#FFFFFF"], ["fontSize", 16], ["fontWeight", "bold"]])]])], ["booking-section", _pS(_uM([["width", "90%"], ["marginTop", 20], ["marginRight", "auto"], ["marginBottom", 20], ["marginLeft", "auto"], ["display", "flex"], ["justifyContent", "center"]]))], ["booking-button", _uM([[".booking-section ", _uM([["width", "100%"], ["height", 45], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]])], [".booking-section .disabled", _uM([["backgroundColor", "#cccccc"], ["color", "#666666"]])]])], ["bottom", _pS(_uM([["width", "100%"], ["height", "8%"], ["backgroundImage", "none"], ["backgroundColor", "#E3E1E1"], ["borderTopLeftRadius", 25], ["borderTopRightRadius", 25], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["display", "flex"], ["justifyContent", "space-around"], ["alignItems", "center"], ["flexShrink", 0]]))], ["bottom_left", _pS(_uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["icon2", _uM([[".bottom_left ", _uM([["width", 30], ["height", 30]])], [".bottom_right ", _uM([["width", 30], ["height", 30]])]])], ["left_down", _uM([[".bottom_left ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["bottom_center", _pS(_uM([["position", "relative"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["bike_icon", _uM([[".bottom_center ", _uM([["width", 30], ["height", 30]])]])], ["center_down", _uM([[".bottom_center ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["bottom_right", _pS(_uM([["position", "relative"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["right_down", _uM([[".bottom_right ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["text3", _pS(_uM([["fontSize", 12], ["marginTop", 4]]))]])]
