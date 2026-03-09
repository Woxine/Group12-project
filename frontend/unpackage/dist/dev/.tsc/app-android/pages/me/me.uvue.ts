import { ref, onMounted } from 'vue'
import { buildApiUrl } from '../../common/apiConfig.uts'

type Order = { __$originalPosition?: UTSSourceMapPosition<"Order", "pages/me/me.uvue", 83, 6>;
	id: string
	bikeId: string
	duration: string
	price: number
	date: string
	status: string
}

type UserInfo = { __$originalPosition?: UTSSourceMapPosition<"UserInfo", "pages/me/me.uvue", 92, 6>;
	name: string
	email: string
}


const __sfc__ = defineComponent({
  __name: 'me',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const userName = ref<string>('')
const userEmail = ref<string>('')
const userRole = ref<string>('')
const orders = ref<Order[]>([])
const isLoading = ref(false)

// Get user info from storage or session
const getUserInfo = () => {
	// Try to get from local storage
	const userInfoStr = uni.getStorageSync('userInfo')
	console.log('Retrieved userInfoStr from storage:', userInfoStr, " at pages/me/me.uvue:107")
	
	if (userInfoStr != null && userInfoStr != '' && typeof userInfoStr === 'string') {
		try {
			const userInfo = UTSAndroid.consoleDebugError(JSON.parse<UserInfo>(userInfoStr), " at pages/me/me.uvue:111")!!
			console.log('Parsed userInfo:', userInfo, " at pages/me/me.uvue:112")
			
			if (userInfo != null) {
				if (userInfo['name'] != null) {
					userName.value = userInfo['name'] as string
					console.log('Set userName to:', userName.value, " at pages/me/me.uvue:117")
				}
				if (userInfo['email'] != null) {
					userEmail.value = userInfo['email'] as string
					console.log('Set userEmail to:', userEmail.value, " at pages/me/me.uvue:121")
				}
			}
		} catch (e) {
			console.error('Failed to parse user info:', e, " at pages/me/me.uvue:125")
		}
	} else {
		console.log('No user info found in storage or invalid format', " at pages/me/me.uvue:128")
	}

	const roleRaw = uni.getStorageSync('role')
	if (roleRaw != null && roleRaw !== '') {
		userRole.value = '' + roleRaw
	}
}

// Fetch user orders
const fetchOrders = () => {
	if (userName.value == '') {
		return
	}

	isLoading.value = true

	uni.request({
		url: buildApiUrl('/api/v1/orders'),
		method: 'GET',
		header: {
			'Content-Type': 'application/json'
		},
		success: (res) => {
			if (res.statusCode >= 200 && res.statusCode < 300) {
				const responseData = res.data
				if (responseData != null && typeof responseData === 'object') {
					const responseObj = responseData as UTSJSONObject
					const ordersData = responseObj['data']
					if (ordersData != null && Array.isArray(ordersData)) {
						const parsedOrders: Order[] = []
						for (let i = 0; i < ordersData.length; i++) {
							const orderItem = ordersData[i] as UTSJSONObject
							const order: Order = {
								id: (orderItem['id'] != null ? orderItem['id'] : i + 1) as string,
								bikeId: (orderItem['bikeId'] != null ? orderItem['bikeId'] : 'SC000' + (i + 1)) as string,
								duration: (orderItem['duration'] != null ? orderItem['duration'] : '1小时') as string,
								price: (orderItem['price'] != null ? orderItem['price'] as number : 1),
								date: (orderItem['date'] != null ? orderItem['date'] : new Date().toLocaleDateString()) as string,
								status: (orderItem['status'] != null ? orderItem['status'] : 'Completed') as string
							}
							parsedOrders.push(order)
						}
						orders.value = parsedOrders
					}
				}
			} else {
				console.error('Failed to fetch orders:', res.statusCode, " at pages/me/me.uvue:175")
				uni.showToast({
					title: 'Failed to load orders',
					icon: 'none'
				})
			}
		},
		fail: (error) => {
			console.error('Network error:', error, " at pages/me/me.uvue:183")
			// Load mock data for demo
			loadMockOrders()
		},
		complete: () => {
			isLoading.value = false
		}
	})
}

// Load mock orders for demo
const loadMockOrders = () => {
		const mockOrdersData: Order[] = [
		{
			id: '1',
			bikeId: 'SC001',
			duration: '1 hour',
			price: 1,
			date: '2024-02-10',
			status: 'Completed'
		},
		{
			id: '2',
			bikeId: 'SC002',
			duration: '4 hours',
			price: 1,
			date: '2024-02-08',
			status: 'Completed'
		},
		{
			id: '3',
			bikeId: 'SC003',
			duration: '1 day',
			price: 1,
			date: '2024-02-05',
			status: 'Pending'
		}
	]
	orders.value = mockOrdersData
}

// Handle logout
const handleLogout = () => {
	uni.showModal({
		title: 'Logout',
		content: 'Are you sure you want to logout?',
		success: (res) => {
			if (res.confirm) {
				// Clear all user data
				uni.removeStorageSync('userInfo')
				uni.removeStorageSync('token')
				uni.removeStorageSync('userId')
				uni.removeStorageSync('role')
				uni.removeStorageSync('selectedScooterId')
				
				// Reset page state
				userName.value = ''
				userEmail.value = ''
				userRole.value = ''
				orders.value = []
				
				uni.showToast({
					title: 'Logged out successfully',
					icon: 'success'
				})
				
				// Navigate to me page (will show not logged in state)
				uni.reLaunch({
					url: '/pages/me/me'
				})
			}
		}
	})
}

onMounted(() => {
	getUserInfo()
	// Load mock orders for now
	loadMockOrders()
	// Uncomment this line when backend is ready
	// fetchOrders()
})

return (): any | null => {

const _component_navigator = resolveComponent("navigator")

  return _cE("view", _uM({ class: "container" }), [
    userName.value.length == 0
      ? _cE("view", _uM({
          key: 0,
          class: "login-prompt-section"
        }), [
          _cE("view", _uM({ class: "not-logged-wrapper" }), [
            _cE("image", _uM({
              src: "/static/me.png",
              mode: "aspectFit",
              class: "large-icon"
            })),
            _cE("text", _uM({ class: "welcome-text" }), "Welcome to Bike Sharing"),
            _cE("text", _uM({ class: "login-hint-text" }), "Please log in to view your profile and bookings"),
            _cV(_component_navigator, _uM({
              url: "/pages/login/login",
              "open-type": "navigate"
            }), _uM({
              default: withSlotCtx((): any[] => [
                _cE("button", _uM({ class: "login-btn" }), "Go to Login")
              ]),
              _: 1 /* STABLE */
            })),
            _cE("view", _uM({ class: "divider-text" }), [
              _cE("text", null, "Don't have an account?")
            ]),
            _cV(_component_navigator, _uM({
              url: "/pages/register/register",
              "open-type": "navigate"
            }), _uM({
              default: withSlotCtx((): any[] => [
                _cE("button", _uM({ class: "register-btn" }), "Create Account")
              ]),
              _: 1 /* STABLE */
            }))
          ])
        ])
      : _cE("scroll-view", _uM({
          key: 1,
          class: "logged-in-container",
          "scroll-y": "true"
        }), [
          _cE("view", _uM({ class: "profile-section" }), [
            _cE("view", _uM({ class: "profile-header" }), [
              _cE("image", _uM({
                src: "/static/me.png",
                mode: "aspectFit",
                class: "avatar"
              })),
              _cE("view", _uM({ class: "user-info" }), [
                _cE("text", _uM({ class: "user-name" }), _tD(userName.value), 1 /* TEXT */),
                _cE("text", _uM({ class: "user-email" }), _tD(userEmail.value), 1 /* TEXT */)
              ])
            ])
          ]),
          _cE("view", _uM({ class: "menu-list" }), [
            _cV(_component_navigator, _uM({
              url: "/pages/orders/orders",
              "open-type": "navigate",
              class: "menu-item"
            }), _uM({
              default: withSlotCtx((): any[] => [
                _cE("text", _uM({ class: "menu-text" }), "My Orders"),
                _cE("text", _uM({ class: "menu-arrow" }), "›")
              ]),
              _: 1 /* STABLE */
            })),
            _cV(_component_navigator, _uM({
              url: "/pages/feedback/feedback",
              "open-type": "navigate",
              class: "menu-item"
            }), _uM({
              default: withSlotCtx((): any[] => [
                _cE("text", _uM({ class: "menu-text" }), "Feedback"),
                _cE("text", _uM({ class: "menu-arrow" }), "›")
              ]),
              _: 1 /* STABLE */
            })),
            userRole.value === 'ADMIN'
              ? _cV(_component_navigator, _uM({
                  key: 0,
                  url: "/pages/admin/home",
                  "open-type": "navigate",
                  class: "menu-item"
                }), _uM({
                  default: withSlotCtx((): any[] => [
                    _cE("text", _uM({ class: "menu-text admin-text" }), "Admin Panel"),
                    _cE("text", _uM({ class: "menu-arrow" }), "›")
                  ]),
                  _: 1 /* STABLE */
                }))
              : _cC("v-if", true)
          ]),
          _cE("view", _uM({ class: "action-section" }), [
            _cE("button", _uM({
              class: "logout-btn",
              onClick: handleLogout
            }), "Logout")
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
  ])
}
}

})
export default __sfc__
const GenPagesMeMeStyles = [_uM([["container", _pS(_uM([["width", "100%"], ["backgroundColor", "#f5f5f5"], ["boxSizing", "border-box"], ["display", "flex"], ["flexDirection", "column"]]))], ["login-prompt-section", _pS(_uM([["width", "100%"], ["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["not-logged-wrapper", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 40], ["paddingRight", 20], ["paddingBottom", 40], ["paddingLeft", 20], ["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["marginTop", 20], ["marginRight", 20], ["marginBottom", 20], ["marginLeft", 20], ["boxShadow", "0 4px 12px rgba(0, 0, 0, 0.1)"]]))], ["large-icon", _pS(_uM([["width", 80], ["height", 80], ["marginBottom", 20], ["opacity", 0.6]]))], ["welcome-text", _pS(_uM([["fontSize", 24], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 10], ["textAlign", "center"]]))], ["login-hint-text", _pS(_uM([["fontSize", 14], ["color", "#999999"], ["textAlign", "center"], ["marginBottom", 30]]))], ["login-btn", _pS(_uM([["width", "100%"], ["height", 45], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"], ["marginBottom", 15]]))], ["divider-text", _pS(_uM([["marginBottom", 15]]))], ["register-btn", _pS(_uM([["width", "100%"], ["height", 45], ["backgroundColor", "rgba(76,175,80,0.2)"], ["color", "#4CAF50"], ["borderTopWidth", 1], ["borderRightWidth", 1], ["borderBottomWidth", 1], ["borderLeftWidth", 1], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#4CAF50"], ["borderRightColor", "#4CAF50"], ["borderBottomColor", "#4CAF50"], ["borderLeftColor", "#4CAF50"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]]))], ["logged-in-container", _pS(_uM([["width", "100%"], ["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["overflowY", "auto"], ["paddingBottom", 90]]))], ["profile-section", _pS(_uM([["width", "100%"], ["backgroundColor", "#FFFFFF"], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["boxSizing", "border-box"], ["borderBottomWidth", 1], ["borderBottomStyle", "solid"], ["borderBottomColor", "#e0e0e0"]]))], ["menu-list", _pS(_uM([["width", "100%"], ["marginTop", 12], ["backgroundColor", "#FFFFFF"], ["boxSizing", "border-box"]]))], ["menu-item", _pS(_uM([["width", "100%"], ["paddingTop", 14], ["paddingRight", 20], ["paddingBottom", 14], ["paddingLeft", 20], ["boxSizing", "border-box"], ["display", "flex"], ["flexDirection", "row"], ["alignItems", "center"], ["justifyContent", "space-between"], ["borderTopWidth", 1], ["borderTopStyle", "solid"], ["borderTopColor", "#f0f0f0"], ["borderTopWidth:first-child", 0]]))], ["menu-text", _pS(_uM([["fontSize", 14], ["color", "#333333"]]))], ["menu-arrow", _pS(_uM([["fontSize", 18], ["color", "#cccccc"]]))], ["admin-text", _pS(_uM([["color", "#4CAF50"], ["fontWeight", "bold"]]))], ["profile-header", _pS(_uM([["display", "flex"], ["alignItems", "center"]]))], ["avatar", _pS(_uM([["width", 60], ["height", 60], ["backgroundColor", "#e0e0e0"], ["marginRight", 15]]))], ["user-info", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["display", "flex"], ["flexDirection", "column"]]))], ["user-name", _pS(_uM([["fontSize", 16], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 5]]))], ["user-email", _pS(_uM([["fontSize", 12], ["color", "#999999"]]))], ["orders-section", _pS(_uM([["width", "100%"], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["boxSizing", "border-box"]]))], ["section-title", _pS(_uM([["fontSize", 16], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 15], ["paddingBottom", 10], ["borderBottomWidth", 2], ["borderBottomStyle", "solid"], ["borderBottomColor", "#4CAF50"]]))], ["orders-list", _pS(_uM([["display", "flex"], ["flexDirection", "column"]]))], ["order-card", _pS(_uM([["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["paddingTop", 15], ["paddingRight", 15], ["paddingBottom", 15], ["paddingLeft", 15], ["marginBottom", 12], ["boxShadow", "0 2px 4px rgba(0, 0, 0, 0.1)"]]))], ["order-header", _pS(_uM([["display", "flex"], ["justifyContent", "space-between"], ["alignItems", "center"], ["marginBottom", 10]]))], ["order-id", _pS(_uM([["fontSize", 14], ["fontWeight", "bold"], ["color", "#333333"]]))], ["order-status", _uM([["", _uM([["fontSize", 12], ["paddingTop", 4], ["paddingRight", 8], ["paddingBottom", 4], ["paddingLeft", 8], ["borderTopLeftRadius", 4], ["borderTopRightRadius", 4], ["borderBottomRightRadius", 4], ["borderBottomLeftRadius", 4], ["fontWeight", "bold"]])], [".Completed", _uM([["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"]])], [".Pending", _uM([["backgroundColor", "#FFC107"], ["color", "#FFFFFF"]])], [".Cancelled", _uM([["backgroundColor", "#f44336"], ["color", "#FFFFFF"]])]])], ["order-details", _pS(_uM([["display", "flex"], ["flexDirection", "column"]]))], ["detail-row", _pS(_uM([["display", "flex"], ["justifyContent", "space-between"], ["paddingTop", 5], ["paddingRight", 0], ["paddingBottom", 5], ["paddingLeft", 0], ["fontSize", 12]]))], ["label", _pS(_uM([["color", "#999999"], ["fontWeight", "bold"]]))], ["value", _pS(_uM([["color", "#333333"], ["fontWeight", "bold"]]))], ["empty-state", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 40], ["paddingRight", 20], ["paddingBottom", 40], ["paddingLeft", 20], ["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["boxShadow", "0 2px 4px rgba(0, 0, 0, 0.1)"]]))], ["empty-text", _pS(_uM([["fontSize", 16], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 8]]))], ["empty-hint", _pS(_uM([["fontSize", 12], ["color", "#999999"], ["marginBottom", 20], ["textAlign", "center"]]))], ["explore-btn", _pS(_uM([["width", "100%"], ["paddingTop", 10], ["paddingRight", 20], ["paddingBottom", 10], ["paddingLeft", 20], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["fontWeight", "bold"]]))], ["action-section", _pS(_uM([["width", "90%"], ["marginTop", 30], ["marginRight", "auto"], ["marginBottom", 40], ["marginLeft", "auto"]]))], ["logout-btn", _pS(_uM([["width", "100%"], ["paddingTop", 12], ["paddingRight", 12], ["paddingBottom", 12], ["paddingLeft", 12], ["backgroundColor", "rgba(60,60,60,0.7)"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["fontWeight", "bold"]]))], ["bottom", _pS(_uM([["width", "100%"], ["height", 80], ["backgroundImage", "none"], ["backgroundColor", "#E3E1E1"], ["borderTopLeftRadius", 25], ["borderTopRightRadius", 25], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["display", "flex"], ["justifyContent", "space-around"], ["alignItems", "center"], ["flexShrink", 0], ["position", "fixed"], ["bottom", 0], ["left", 0], ["zIndex", 10]]))], ["bottom_left", _pS(_uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "33.33%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["icon2", _uM([[".bottom_left ", _uM([["width", 30], ["height", 30]])], [".bottom_right ", _uM([["width", 30], ["height", 30]])]])], ["left_down", _uM([[".bottom_left ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["bottom_center", _pS(_uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "33.33%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["bike_icon", _uM([[".bottom_center ", _uM([["width", 30], ["height", 30]])]])], ["center_down", _uM([[".bottom_center ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["bottom_right", _pS(_uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "33.33%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]]))], ["right_down", _uM([[".bottom_right ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["text3", _pS(_uM([["fontSize", 12], ["marginTop", 4]]))]])]
