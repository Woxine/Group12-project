import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { BASE_URL } from '../../common/api.uts'

type Booking = { __$originalPosition?: UTSSourceMapPosition<"Booking", "pages/orders/orders.uvue", 87, 6>;
	id: string | null
	scooterId: string
	duration: string
	startTime: string
	endTime: string
	price: number
	status: string
}


const __sfc__ = defineComponent({
  __name: 'orders',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const isLoggedIn = ref(false)
const isLoading = ref(false)
const loadError = ref('')
const bookings = ref<Booking[]>([])
const scrollWrapHeight = ref(600)
const scrollViewHeight = ref(560)
const isRefreshing = ref(false)

const formatStatus = (status: string | null): string => {
	if (status == null || status.length == 0) return 'Unknown'
	const s = status.toUpperCase()
	if (s == 'CONFIRMED') return 'Confirmed'
	if (s == 'PENDING') return 'Pending'
	if (s == 'CANCELLED') return 'Cancelled'
	if (s == 'COMPLETED') return 'Completed'
	return status
}

const formatBikeId = (scooterId: string | null): string => {
	if (scooterId == null || scooterId.length == 0) return '-'
	if (scooterId.startsWith('SC')) return scooterId
	return 'SC' + scooterId.padStart(3, '0')
}

const parseApiBooking = (item: UTSJSONObject, index: number): Booking => {
	const rawId = item['id']
	const id = rawId != null ? ('' + rawId) : null
	const rawScooterId = item['scooter_id'] != null ? item['scooter_id'] : item['scooterId']
	const scooterId = rawScooterId != null ? ('' + rawScooterId) : ''
	const rawDuration = item['duration'] != null ? item['duration'] : '1H'
	const duration = '' + rawDuration
	const rawStart = item['start_time'] != null ? item['start_time'] : item['startTime']
	const startTime = rawStart != null ? ('' + rawStart) : '-'
	const rawEnd = item['end_time'] != null ? item['end_time'] : item['endTime']
	const endTime = rawEnd != null ? ('' + rawEnd) : '-'
	const rawPrice = item['total_price'] != null ? item['total_price'] : item['price']
	const price = rawPrice != null ? (rawPrice as number) : 0
	const rawStatus = item['status']
	const statusVal = rawStatus != null ? ('' + rawStatus) : 'Unknown'
	return {
		id: id,
		scooterId: scooterId,
		duration: duration,
		startTime: startTime,
		endTime: endTime,
		price: price,
		status: statusVal
	}
}

const fetchBookings = (fromPullRefresh: boolean) => {
	const userIdStorage = uni.getStorageSync('userId')
	const tokenStorage = uni.getStorageSync('token')
	const userId = userIdStorage != null && userIdStorage != '' ? ('' + userIdStorage) : null
	const token = tokenStorage != null && tokenStorage != '' ? ('' + tokenStorage) : null

	if (userId == null || userId.length == 0) {
		isLoggedIn.value = false
		return
	}

	if (token == null || token.length == 0) {
		loadError.value = 'Please log in again'
		isLoggedIn.value = true
		isLoading.value = false
		isRefreshing.value = false
		return
	}

	isLoggedIn.value = true
	if (fromPullRefresh === false) {
		isLoading.value = true
	}
	loadError.value = ''

	uni.request({
		url: BASE_URL + '/api/v1/users/' + userId + '/bookings',
		method: 'GET',
		header: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + token
		},
		success: (res) => {
			if (res.statusCode >= 200 && res.statusCode < 300) {
				const responseData = res.data
				if (responseData != null && typeof responseData === 'object') {
					const obj = responseData as UTSJSONObject
					let arr: any[] = []
					if (obj['data'] != null && Array.isArray(obj['data'])) {
						arr = obj['data'] as any[]
					} else if (Array.isArray(responseData)) {
						arr = responseData as any[]
					}
					const list: Booking[] = []
					for (let i = 0; i < arr.length; i++) {
						const item = arr[i] as UTSJSONObject
						list.push(parseApiBooking(item, i))
					}
					bookings.value = list
				} else {
					bookings.value = []
				}
			} else {
				loadError.value = 'Failed to load bookings (HTTP ' + res.statusCode + ')'
			}
		},
		fail: (error) => {
			loadError.value = 'Network error. Please check your connection.'
			console.error('Fetch bookings failed:', error, " at pages/orders/orders.uvue:205")
		},
		complete: () => {
			isLoading.value = false
			isRefreshing.value = false
		}
	})
}

const onPullRefresh = () => {
	isRefreshing.value = true
	fetchBookings(true)
}

const canCancel = (status: string | null): boolean => {
	if (status == null || status.length === 0) return true
	const s = status.toUpperCase()
	if (s === 'CANCELLED' || s === 'COMPLETED') return false
	return true
}

const cancelOrder = (booking: Booking) => {
	const bid = booking.id
	if (bid == null || bid.length === 0) {
		uni.showToast({ title: 'Invalid booking', icon: 'none' })
		return
	}
	const tokenRaw = uni.getStorageSync('token')
	const token = (tokenRaw != null && tokenRaw !== '') ? ('' + tokenRaw).trim() : ''
	if (token === '') {
		uni.showToast({ title: 'Please login again', icon: 'none' })
		return
	}
	uni.showModal({
		title: 'Cancel Order',
		content: 'Are you sure you want to cancel this booking?',
		success: (res) => {
			const confirm = res.confirm === true
			if (confirm) {
				uni.request({
					url: BASE_URL + '/api/v1/bookings/' + bid,
					method: 'DELETE',
					header: {
						'Content-Type': 'application/json',
						'Authorization': 'Bearer ' + token
					},
					success: (resp) => {
						if (resp.statusCode >= 200 && resp.statusCode < 300) {
							uni.showToast({ title: 'Order cancelled', icon: 'success' })
							fetchBookings(false)
						} else {
							let msg = 'Failed to cancel'
							const data = resp.data as UTSJSONObject | null
							if (data !== null && data['message'] !== null) {
								msg = '' + data['message']
							}
							uni.showToast({ title: msg, icon: 'none', duration: 3000 })
						}
					},
					fail: (err) => {
						uni.showToast({ title: 'Network error', icon: 'none' })
						console.error('Cancel booking failed:', err, " at pages/orders/orders.uvue:266")
					}
				})
			}
		}
	})
}

const setScrollHeight = () => {
	try {
		const sys = uni.getSystemInfoSync()
		const h = sys.windowHeight != null ? sys.windowHeight : 600
		scrollWrapHeight.value = h
		scrollViewHeight.value = h - 40
	} catch (e) {
		scrollWrapHeight.value = 600
		scrollViewHeight.value = 560
	}
}

onMounted(() => {
	setScrollHeight()
	const userId = uni.getStorageSync('userId')
	const hasUser = userId != null && userId != '' && typeof userId === 'string'
	if (hasUser) {
		fetchBookings(false)
	} else {
		isLoggedIn.value = false
	}
})

onShow(() => {
	setScrollHeight()
	const userId = uni.getStorageSync('userId')
	const hasUser = userId != null && userId != '' && typeof userId === 'string'
	if (hasUser) {
		fetchBookings(false)
	}
})

return (): any | null => {

const _component_navigator = resolveComponent("navigator")

  return _cE("view", _uM({ class: "container" }), [
    isTrue(!isLoggedIn.value)
      ? _cE("view", _uM({
          key: 0,
          class: "login-prompt"
        }), [
          _cE("image", _uM({
            src: "/static/me.png",
            mode: "aspectFit",
            class: "icon-large"
          })),
          _cE("text", _uM({ class: "prompt-text" }), "Please log in to view your bookings"),
          _cV(_component_navigator, _uM({
            url: "/pages/login/login",
            "open-type": "navigate"
          }), _uM({
            default: withSlotCtx((): any[] => [
              _cE("button", _uM({ class: "login-btn" }), "Go to Login")
            ]),
            _: 1 /* STABLE */
          }))
        ])
      : _cE("view", _uM({
          key: 1,
          class: "scroll-wrap",
          style: _nS(_uM({ height: scrollWrapHeight.value + 'px' }))
        }), [
          _cE("scroll-view", _uM({
            class: "scroll-area",
            direction: "vertical",
            "show-scrollbar": "true",
            "enable-back-to-top": "true",
            style: _nS(_uM({ height: scrollViewHeight.value + 'px' })),
            "refresher-enabled": "true",
            "refresher-triggered": isRefreshing.value,
            onRefresherrefresh: onPullRefresh
          }), [
            _cE("view", _uM({ class: "title-section" }), [
              _cE("text", _uM({ class: "title-text" }), "My Bookings")
            ]),
            isTrue(isLoading.value)
              ? _cE("view", _uM({
                  key: 0,
                  class: "loading-section"
                }), [
                  _cE("text", _uM({ class: "loading-text" }), "Loading bookings...")
                ])
              : loadError.value.length > 0
                ? _cE("view", _uM({
                    key: 1,
                    class: "error-section"
                  }), [
                    _cE("text", _uM({ class: "error-text" }), _tD(loadError.value), 1 /* TEXT */),
                    _cE("button", _uM({
                      class: "retry-btn",
                      onClick: () => {fetchBookings(false)}
                    }), "Retry", 8 /* PROPS */, ["onClick"])
                  ])
                : bookings.value.length == 0
                  ? _cE("view", _uM({
                      key: 2,
                      class: "empty-section"
                    }), [
                      _cE("text", _uM({ class: "empty-text" }), "No bookings yet"),
                      _cE("text", _uM({ class: "empty-hint" }), "Go to Bike to make your first booking"),
                      _cV(_component_navigator, _uM({
                        url: "/pages/bikelist/bikelist",
                        "open-type": "reLaunch"
                      }), _uM({
                        default: withSlotCtx((): any[] => [
                          _cE("button", _uM({ class: "explore-btn" }), "Explore Bikes")
                        ]),
                        _: 1 /* STABLE */
                      }))
                    ])
                  : _cE("view", _uM({
                      key: 3,
                      class: "bookings-list"
                    }), [
                      _cE(Fragment, null, RenderHelpers.renderList(bookings.value, (booking, index, __index, _cached): any => {
                        return _cE("view", _uM({
                          key: booking.id != null ? booking.id : index,
                          class: "booking-card"
                        }), [
                          _cE("view", _uM({ class: "card-header" }), [
                            _cE("text", _uM({ class: "booking-id" }), "#" + _tD(booking.id != null ? booking.id : index + 1), 1 /* TEXT */),
                            _cE("text", _uM({
                              class: _nC(['status-badge', 'status-' + formatStatus(booking.status)])
                            }), _tD(formatStatus(booking.status)), 3 /* TEXT, CLASS */)
                          ]),
                          _cE("view", _uM({ class: "card-body" }), [
                            _cE("view", _uM({ class: "detail-row" }), [
                              _cE("text", _uM({ class: "label" }), "Bike"),
                              _cE("text", _uM({ class: "value" }), _tD(formatBikeId(booking.scooterId)), 1 /* TEXT */)
                            ]),
                            _cE("view", _uM({ class: "detail-row" }), [
                              _cE("text", _uM({ class: "label" }), "Duration"),
                              _cE("text", _uM({ class: "value" }), _tD(booking.duration), 1 /* TEXT */)
                            ]),
                            _cE("view", _uM({ class: "detail-row" }), [
                              _cE("text", _uM({ class: "label" }), "Start"),
                              _cE("text", _uM({ class: "value" }), _tD(booking.startTime), 1 /* TEXT */)
                            ]),
                            _cE("view", _uM({ class: "detail-row" }), [
                              _cE("text", _uM({ class: "label" }), "End"),
                              _cE("text", _uM({ class: "value" }), _tD(booking.endTime), 1 /* TEXT */)
                            ]),
                            _cE("view", _uM({ class: "detail-row" }), [
                              _cE("text", _uM({ class: "label" }), "Total"),
                              _cE("text", _uM({ class: "value" }), "£" + _tD(booking.price), 1 /* TEXT */)
                            ]),
                            isTrue(canCancel(booking.status))
                              ? _cE("view", _uM({
                                  key: 0,
                                  class: "card-actions"
                                }), [
                                  _cE("button", _uM({
                                    class: "cancel-order-btn",
                                    onClick: () => {cancelOrder(booking)}
                                  }), "Cancel Order", 8 /* PROPS */, ["onClick"])
                                ])
                              : _cC("v-if", true)
                          ])
                        ])
                      }), 128 /* KEYED_FRAGMENT */)
                    ])
          ], 44 /* STYLE, PROPS, NEED_HYDRATION */, ["refresher-triggered"])
        ], 4 /* STYLE */)
  ])
}
}

})
export default __sfc__
const GenPagesOrdersOrdersStyles = [_uM([["container", _pS(_uM([["width", "100%"], ["display", "flex"], ["flexDirection", "column"], ["backgroundColor", "#f5f5f5"], ["boxSizing", "border-box"]]))], ["login-prompt", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 60], ["paddingRight", 24], ["paddingBottom", 60], ["paddingLeft", 24]]))], ["icon-large", _pS(_uM([["width", 80], ["height", 80], ["opacity", 0.6], ["marginBottom", 24]]))], ["prompt-text", _pS(_uM([["fontSize", 16], ["color", "#666666"], ["marginBottom", 24], ["textAlign", "center"]]))], ["login-btn", _pS(_uM([["width", 200], ["height", 44], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]]))], ["scroll-wrap", _pS(_uM([["width", "100%"]]))], ["scroll-area", _pS(_uM([["width", "100%"], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 40], ["paddingLeft", 20], ["boxSizing", "border-box"]]))], ["title-section", _pS(_uM([["marginBottom", 20]]))], ["title-text", _pS(_uM([["fontSize", 22], ["fontWeight", "bold"], ["color", "#333333"]]))], ["loading-section", _pS(_uM([["paddingTop", 40], ["paddingRight", 20], ["paddingBottom", 40], ["paddingLeft", 20], ["textAlign", "center"]]))], ["error-section", _pS(_uM([["paddingTop", 40], ["paddingRight", 20], ["paddingBottom", 40], ["paddingLeft", 20], ["textAlign", "center"]]))], ["loading-text", _pS(_uM([["fontSize", 14], ["color", "#999999"]]))], ["error-text", _pS(_uM([["fontSize", 14], ["color", "#f44336"], ["marginBottom", 16]]))], ["retry-btn", _pS(_uM([["paddingTop", 10], ["paddingRight", 24], ["paddingBottom", 10], ["paddingLeft", 24], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["fontWeight", "bold"]]))], ["empty-section", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 60], ["paddingRight", 24], ["paddingBottom", 60], ["paddingLeft", 24], ["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"]]))], ["empty-text", _pS(_uM([["fontSize", 18], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 8]]))], ["empty-hint", _pS(_uM([["fontSize", 14], ["color", "#999999"], ["marginBottom", 24], ["textAlign", "center"]]))], ["explore-btn", _pS(_uM([["paddingTop", 12], ["paddingRight", 32], ["paddingBottom", 12], ["paddingLeft", 32], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["fontWeight", "bold"]]))], ["bookings-list", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["gap", "16px"]]))], ["booking-card", _pS(_uM([["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["paddingTop", 16], ["paddingRight", 16], ["paddingBottom", 16], ["paddingLeft", 16], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"]]))], ["card-header", _pS(_uM([["display", "flex"], ["flexDirection", "row"], ["justifyContent", "space-between"], ["alignItems", "center"], ["marginBottom", 12], ["paddingBottom", 10], ["borderBottomWidth", 1], ["borderBottomStyle", "solid"], ["borderBottomColor", "#eeeeee"]]))], ["booking-id", _pS(_uM([["fontSize", 16], ["fontWeight", "bold"], ["color", "#333333"]]))], ["status-badge", _pS(_uM([["fontSize", 12], ["paddingTop", 4], ["paddingRight", 10], ["paddingBottom", 4], ["paddingLeft", 10], ["borderTopLeftRadius", 6], ["borderTopRightRadius", 6], ["borderBottomRightRadius", 6], ["borderBottomLeftRadius", 6], ["fontWeight", "bold"]]))], ["status-Confirmed", _pS(_uM([["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"]]))], ["status-Pending", _pS(_uM([["backgroundColor", "#FFC107"], ["color", "#333333"]]))], ["status-Cancelled", _pS(_uM([["backgroundColor", "#f44336"], ["color", "#FFFFFF"]]))], ["status-Completed", _pS(_uM([["backgroundColor", "#2196F3"], ["color", "#FFFFFF"]]))], ["status-Unknown", _pS(_uM([["backgroundColor", "#9E9E9E"], ["color", "#FFFFFF"]]))], ["card-body", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["gap", "6px"]]))], ["card-actions", _pS(_uM([["marginTop", 12], ["paddingTop", 10], ["borderTopWidth", 1], ["borderTopStyle", "solid"], ["borderTopColor", "#eeeeee"]]))], ["cancel-order-btn", _pS(_uM([["width", "100%"], ["paddingTop", 10], ["paddingRight", 16], ["paddingBottom", 10], ["paddingLeft", 16], ["backgroundColor", "#f44336"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["fontWeight", "bold"]]))], ["detail-row", _pS(_uM([["display", "flex"], ["flexDirection", "row"], ["justifyContent", "space-between"], ["fontSize", 14]]))], ["label", _uM([[".detail-row ", _uM([["color", "#999999"]])]])], ["value", _uM([[".detail-row ", _uM([["color", "#333333"]])]])]])]
