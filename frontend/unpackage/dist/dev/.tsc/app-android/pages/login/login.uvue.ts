import { reactive, ref } from 'vue';
import { buildApiUrl } from '../../common/apiConfig.uts';

// 响应式数据对象，用于存储用户输入
type UserInfo = { __$originalPosition?: UTSSourceMapPosition<"UserInfo", "pages/login/login.uvue", 63, 6>;
	email: string
	password: string
}

const __sfc__ = defineComponent({
  __name: 'login',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const userInfo = reactive<UserInfo>({
	email: '',
	password: ''
});

// 控制提交状态，防止重复提交
const isSubmitting = ref(false);

const DEBUG_ENDPOINT = 'http://127.0.0.1:7704/ingest/fb6b8af2-8829-46b4-89bb-12721b669ea9';
const DEBUG_SESSION_ID = '98f075';
const DEBUG_RUN_ID = 'login-timeout-run-1';
const LOGIN_URL = buildApiUrl('/api/v1/auth/login');

const sendDebugLog = (hypothesisId: string, location: string, message: string, data: UTSJSONObject) => {
	// #region agent log
	uni.request({
		url: DEBUG_ENDPOINT,
		method: 'POST',
		header: {
			'Content-Type': 'application/json',
			'X-Debug-Session-Id': DEBUG_SESSION_ID
		},
		data: {
			sessionId: DEBUG_SESSION_ID,
			runId: DEBUG_RUN_ID,
			hypothesisId: hypothesisId,
			location: location,
			message: message,
			data: data,
			timestamp: Date.now()
		},
		fail: () => {}
	});
	// #endregion
}

// 定义提交方法
const onSubmit = () => {
	sendDebugLog(
		'H1',
		'pages/login/login.uvue:onSubmit:entry',
		'Login submit triggered',
		{
			emailLength: userInfo.email.length,
			hasPassword: userInfo.password != null && userInfo.password != '',
			isSubmitting: isSubmitting.value
		}
	);

	// 防止重复提交
	if (isSubmitting.value) return;

	// 简单校验
	if (userInfo.email == null || userInfo.email == '' || userInfo.password == null || userInfo.password == '') {
		uni.showToast({
			title: 'Please fill in both email and password.',
			icon: 'none'
		});
		return;
	}

	isSubmitting.value = true;
	sendDebugLog(
		'H2',
		'pages/login/login.uvue:onSubmit:beforeRequest',
		'About to call uni.request',
		{
			url: LOGIN_URL,
			method: 'POST'
		}
	);

	uni.getNetworkType({
		success: (ret) => {
			sendDebugLog(
				'H3',
				'pages/login/login.uvue:onSubmit:getNetworkType:success',
				'Network type fetched',
				{
					networkType: ret.networkType
				}
			);
		},
		fail: (ret) => {
			sendDebugLog(
				'H3',
				'pages/login/login.uvue:onSubmit:getNetworkType:fail',
				'Failed to fetch network type',
				{
					errMsg: ret.errMsg
				}
			);
		}
	});

	uni.request({
		url: LOGIN_URL, // 请求的 API 地址
		method: 'POST',
		data: {
			email: userInfo.email,
			password: userInfo.password
		},
		header: {
			'Content-Type': 'application/json'
		},
		success: (res) => {
			sendDebugLog(
				'H4',
				'pages/login/login.uvue:uni.request:success',
				'Request reached success callback',
				{
					statusCode: res.statusCode
				}
			);
			console.log('Login API Response:', res.data, " at pages/login/login.uvue:181");
			
			// 后端返回结构为 { "data": { "token": "...", "userId": "...", "role": "...", "name": "...", "email": "..." } }
			const ret = res.data as UTSJSONObject
			
			// 只要状态码是 200，且 data 字段存在，就认为成功
			if (res.statusCode === 200) {
				// 检查是一层结构还是两层结构
				// 如果后端直接返回对象，或者包了一层 'data'
				let loginData: UTSJSONObject | null = null;
				
				if (ret['data'] != null) {
					loginData = ret['data'] as UTSJSONObject;
				} else {
					// 兼容性尝试：也许直接返回了字段
					loginData = ret;
				}
				
				console.log('Login Data:', JSON.stringify(loginData), " at pages/login/login.uvue:199");
				
				// 打印所有可能的字段
				if (loginData != null) {
					console.log('id field:', loginData['id'], " at pages/login/login.uvue:203");
					console.log('userId field:', loginData['userId'], " at pages/login/login.uvue:204");
					console.log('name field:', loginData['name'], " at pages/login/login.uvue:205");
					console.log('email field:', loginData['email'], " at pages/login/login.uvue:206");
					console.log('token field:', loginData['token'], " at pages/login/login.uvue:207");
				}
				
				if (loginData != null) {
					const token = loginData['token'] as string | null
					if (token != null) {
						// 保存 token（使用同步方法确保立即保存）
						try {
							uni.setStorageSync('token', token)
							console.log('Token saved successfully', " at pages/login/login.uvue:216")
						} catch (e) {
							console.error('Failed to save token:', e, " at pages/login/login.uvue:218")
						}
						
						// 保存用户ID（优先使用登录返回的 id / userId，其次使用已有的本地 userId，最后才退回到 email）
						const existingUserId = uni.getStorageSync('userId')
						let userIdToSave: string = ''
						const rawLoginId = loginData['id'] != null ? loginData['id'] : loginData['userId']
						if (rawLoginId != null) {
							userIdToSave = '' + rawLoginId
						} else if (existingUserId != null && existingUserId != '') {
							userIdToSave = '' + existingUserId
						} else {
							userIdToSave = userInfo.email
						}
						
						try {
							uni.setStorageSync('userId', userIdToSave)
							console.log('User ID saved:', userIdToSave, " at pages/login/login.uvue:235")
						} catch (e) {
							console.error('Failed to save user ID:', e, " at pages/login/login.uvue:237")
						}
					
						// 保存用户信息
						const userRole = loginData['role'] != null ? loginData['role'] as string : 'CUSTOMER'
						try {
							uni.setStorageSync('role', userRole)
							console.log('Role saved:', userRole, " at pages/login/login.uvue:244")
						} catch (e) {
							console.error('Failed to save role:', e, " at pages/login/login.uvue:246")
						}

						const userName = loginData['name'] != null ? loginData['name'] as string : userInfo.email
						const userEmail = userInfo.email
						
						const userInfoData = {__$originalPosition: new UTSSourceMapPosition("userInfoData", "pages/login/login.uvue", 252, 13),
							name: userName,
							email: userEmail
						}
						
						try {
							const userInfoStr = JSON.stringify(userInfoData)
							uni.setStorageSync('userInfo', userInfoStr)
							console.log('User info saved:', userInfoStr, " at pages/login/login.uvue:260")
						} catch (e) {
							console.error('Failed to save user info:', e, " at pages/login/login.uvue:262")
						}
						
						uni.showToast({
							title: 'Login successful!',
							icon: 'success'
						});
						
						// 跳转到首页
						setTimeout(() => {
							uni.reLaunch({ url: '/pages/index/index' });
						}, 1500);
						return;
					}
				}
			} 
			
			// 失败流程
			const retObj = res.data as UTSJSONObject
			// 默认错误消息
			let msg = 'Login failed. Please check your credentials.';
			
			// 尝试提取后端详细错误信息
			// 后端通常返回 { "message": "...", "error": "..." }
			const backendMsg = retObj['message'];
			const backendErr = retObj['error'];
			
			if (backendMsg != null) {
				msg = backendMsg as string;
			} else if (backendErr != null) {
				msg = `Error: ${backendErr}`;
			}
			
			console.error('Login failed. Status:', res.statusCode, 'Response:', JSON.stringify(retObj), " at pages/login/login.uvue:295");
			
			uni.showToast({
				title: msg,
				icon: 'none',
				duration: 3000
			});
		},
		fail: (err) => {
			let errSnapshot = ''
			try {
				errSnapshot = JSON.stringify(err)
			} catch (e) {
				errSnapshot = '' + err
			}
			sendDebugLog(
				'H2',
				'pages/login/login.uvue:uni.request:fail',
				'Request failed in fail callback',
				{
					errorText: '' + err,
					errorSnapshot: errSnapshot
				}
			);
			console.error('Login request failed:', err, " at pages/login/login.uvue:319");
			uni.showToast({
				title: 'Network error, please try again later.',
				icon: 'none'
			});
		},
		complete: () => {
			sendDebugLog(
				'H5',
				'pages/login/login.uvue:uni.request:complete',
				'Request complete callback reached',
				{
					isSubmittingBeforeReset: isSubmitting.value
				}
			);
			// 请求完成后，无论成功失败都重置提交状态
			isSubmitting.value = false;
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
      _cE("view", _uM({ class: "login_title" }), [
        _cE("text", _uM({ class: "title_text" }), "log in")
      ]),
      _cE("view", _uM({ class: "login_container" }), [
        _cE("input", _uM({
          modelValue: userInfo.email,
          onInput: ($event: UniInputEvent) => {(userInfo.email) = $event.detail.value},
          type: "text",
          placeholder: "please enter your email",
          "confirm-type": "next",
          class: "input"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"]),
        _cE("view", _uM({ class: "span1" })),
        _cE("input", _uM({
          modelValue: userInfo.password,
          onInput: ($event: UniInputEvent) => {(userInfo.password) = $event.detail.value},
          type: "password",
          placeholder: "please enter your password",
          class: "input"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"]),
        _cE("view", _uM({ class: "span2" })),
        _cE("button", _uM({
          onClick: onSubmit,
          size: "default",
          disabled: isSubmitting.value,
          style: _nS(_uM({"color":"white","background-color":"rgba(60, 60, 60, 0.7)"}))
        }), _tD(isSubmitting.value ? 'Logging in...' : 'log in'), 13 /* TEXT, STYLE, PROPS */, ["disabled"]),
        _cE("view", _uM({ class: "span3" })),
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
const GenPagesLoginLoginStyles = [_uM([["back", _pS(_uM([["width", "100%"], ["height", "100%"]]))], ["backpic", _uM([[".back ", _uM([["width", "100%"], ["height", "100%"]])]])], ["login_title", _uM([[".back ", _uM([["position", "absolute"], ["top", 80], ["left", "50%"], ["transform", "translateX(-50%)"], ["zIndex", 10], ["paddingTop", 10], ["paddingRight", 10], ["paddingBottom", 10], ["paddingLeft", 10], ["backgroundColor", "rgba(0,0,0,0.3)"]])]])], ["title_text", _uM([[".back .login_title ", _uM([["height", "100%"], ["fontSize", 32], ["fontWeight", "bold"], ["color", "#FFFFFF"], ["textShadow", "1px 1px 3px rgba(0, 0, 0, 0.5)"]])]])], ["login_container", _uM([[".back ", _uM([["position", "absolute"], ["top", "50%"], ["left", "50%"], ["transform", "translate(-50%, -50%)"], ["width", 300], ["paddingTop", 30], ["paddingRight", 30], ["paddingBottom", 30], ["paddingLeft", 30], ["backgroundColor", "rgba(227,225,225,0.46)"], ["borderTopLeftRadius", 15], ["borderTopRightRadius", 15], ["borderBottomRightRadius", 15], ["borderBottomLeftRadius", 15], ["boxShadow", "0 10px 30px rgba(0, 0, 0, 0.3)"]])]])], ["input", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", "10%"], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["backgroundColor", "rgba(60,60,60,0.7)"], ["color", "#FFFFFF"], ["fontSize", 16], ["boxSizing", "border-box"]])]])], ["span1", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", 10]])]])], ["span2", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", 10]])]])], ["span3", _uM([[".back .login_container ", _uM([["width", "100%"], ["height", 10]])]])], ["bottom", _pS(_uM([["width", "100%"], ["height", 80], ["backgroundImage", "none"], ["backgroundColor", "#E3E1E1"], ["borderTopLeftRadius", 25], ["borderTopRightRadius", 25], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["display", "flex"], ["justifyContent", "space-around"], ["alignItems", "center"], ["flexShrink", 0], ["position", "fixed"], ["bottom", 0], ["left", 0], ["zIndex", 10]]))], ["bottom_left", _uM([[".bottom ", _uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "33.33%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]])]])], ["icon2", _uM([[".bottom .bottom_left ", _uM([["width", 30], ["height", 30]])], [".bottom .bottom_right ", _uM([["width", 30], ["height", 30]])]])], ["left_down", _uM([[".bottom .bottom_left ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["bottom_center", _uM([[".bottom ", _uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "33.33%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]])]])], ["bike_icon", _uM([[".bottom .bottom_center ", _uM([["width", 30], ["height", 30]])]])], ["center_down", _uM([[".bottom .bottom_center ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])], ["bottom_right", _uM([[".bottom ", _uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "33.33%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["justifyContent", "center"], ["alignItems", "center"]])]])], ["right_down", _uM([[".bottom .bottom_right ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["height", "40%"], ["width", "100%"]])]])]])]
