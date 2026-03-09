import { ref } from 'vue'
import { BASE_URL } from '../../common/api.uts'


const __sfc__ = defineComponent({
  __name: 'modify',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const scooterId = ref('')
const status = ref('')
const hourRate = ref('')
const locationLat = ref('')
const locationLng = ref('')
const isSubmitting = ref(false)
const errorMsg = ref('')

const onSubmit = () => {
	if (isSubmitting.value) return
	errorMsg.value = ''

	const idStr = scooterId.value != null ? scooterId.value.trim() : ''
	if (idStr.length === 0) {
		errorMsg.value = 'Please enter scooter ID'
		uni.showToast({ title: 'Please enter scooter ID', icon: 'none' })
		return
	}

	const tokenRaw = uni.getStorageSync('token')
	const token = (tokenRaw != null && tokenRaw !== '') ? ('' + tokenRaw).trim() : ''
	if (token.length === 0) {
		errorMsg.value = 'Please login first'
		uni.showToast({ title: 'Please login first', icon: 'none' })
		return
	}

	const body: UTSJSONObject = {__$originalPosition: new UTSSourceMapPosition("body", "pages/admin/modify.uvue", 107, 8),}
	let hasField = false
	const statusStr = status.value != null ? status.value.trim() : ''
	if (statusStr.length > 0) {
		body['status'] = statusStr
		hasField = true
	}
	const rateStr = hourRate.value != null ? hourRate.value.trim() : ''
	if (rateStr.length > 0) {
		body['hour_rate'] = parseFloat(rateStr)
		hasField = true
	}
	const latStr = locationLat.value != null ? locationLat.value.trim() : ''
	if (latStr.length > 0) {
		const latNum = parseFloat(latStr)
		if (!isNaN(latNum)) {
			body['location_lat'] = Math.round(latNum * 1e6) / 1e6
			hasField = true
		}
	}
	const lngStr = locationLng.value != null ? locationLng.value.trim() : ''
	if (lngStr.length > 0) {
		const lngNum = parseFloat(lngStr)
		if (!isNaN(lngNum)) {
			body['location_lng'] = Math.round(lngNum * 1e6) / 1e6
			hasField = true
		}
	}

	if (hasField === false) {
		errorMsg.value = 'Please enter at least one field to update'
		uni.showToast({ title: 'No fields to update', icon: 'none' })
		return
	}

	isSubmitting.value = true

	uni.request({
		url: BASE_URL + '/api/v1/scooters/' + idStr,
		method: 'PUT',
		data: body,
		header: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + token
		},
		success: (res) => {
			if (res.statusCode >= 200 && res.statusCode < 300) {
				uni.showToast({ title: 'Scooter updated', icon: 'success' })
			} else {
				let msg = 'Update failed'
				const data = res.data as UTSJSONObject | null
				if (data != null) {
					const m = data['message']
					const e = data['error']
					if (m != null) {
						msg = '' + m
					} else if (e != null) {
						msg = '' + e
					}
				}
				errorMsg.value = msg
				uni.showToast({ title: msg, icon: 'none', duration: 3000 })
			}
		},
		fail: () => {
			errorMsg.value = 'Network error'
			uni.showToast({ title: 'Network error', icon: 'none' })
		},
		complete: () => {
			isSubmitting.value = false
		}
	})
}

return (): any | null => {

  return _cE("view", _uM({ class: "container" }), [
    _cE("view", _uM({ class: "title-section" }), [
      _cE("text", _uM({ class: "title-text" }), "Admin Panel"),
      _cE("text", _uM({ class: "hint-text" }), "Modify scooter information")
    ]),
    _cE("view", _uM({ class: "form-section" }), [
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Scooter ID"),
        _cE("input", _uM({
          class: "input",
          modelValue: scooterId.value,
          onInput: ($event: UniInputEvent) => {(scooterId).value = $event.detail.value},
          placeholder: "Enter scooter ID (e.g. 1)",
          "placeholder-class": "input-placeholder",
          type: "number"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
      ]),
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Status (AVAILABLE / RENTED / MAINTENANCE)"),
        _cE("input", _uM({
          class: "input",
          modelValue: status.value,
          onInput: ($event: UniInputEvent) => {(status).value = $event.detail.value},
          placeholder: "AVAILABLE",
          "placeholder-class": "input-placeholder"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
      ]),
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Hour Rate (£ / hour)"),
        _cE("input", _uM({
          class: "input",
          modelValue: hourRate.value,
          onInput: ($event: UniInputEvent) => {(hourRate).value = $event.detail.value},
          placeholder: "e.g. 1.5",
          "placeholder-class": "input-placeholder",
          type: "number"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
      ]),
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Location Latitude (最多6位小数)"),
        _cE("input", _uM({
          class: "input",
          modelValue: locationLat.value,
          onInput: ($event: UniInputEvent) => {(locationLat).value = $event.detail.value},
          placeholder: "e.g. 39.908692",
          "placeholder-class": "input-placeholder",
          type: "text",
          inputmode: "decimal"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
      ]),
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Location Longitude (最多6位小数)"),
        _cE("input", _uM({
          class: "input",
          modelValue: locationLng.value,
          onInput: ($event: UniInputEvent) => {(locationLng).value = $event.detail.value},
          placeholder: "e.g. 116.397477",
          "placeholder-class": "input-placeholder",
          type: "text",
          inputmode: "decimal"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
      ]),
      errorMsg.value.length > 0
        ? _cE("view", _uM({
            key: 0,
            class: "error-row"
          }), [
            _cE("text", _uM({ class: "error-text" }), _tD(errorMsg.value), 1 /* TEXT */)
          ])
        : _cC("v-if", true),
      _cE("button", _uM({
        class: _nC(['submit-btn', isSubmitting.value ? 'submit-btn-disabled' : '']),
        disabled: isSubmitting.value,
        onClick: onSubmit
      }), _tD(isSubmitting.value ? 'Updating...' : 'Update Scooter'), 11 /* TEXT, CLASS, PROPS */, ["disabled"])
    ])
  ])
}
}

})
export default __sfc__
const GenPagesAdminModifyStyles = [_uM([["container", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["paddingTop", 24], ["paddingRight", 24], ["paddingBottom", 24], ["paddingLeft", 24], ["backgroundColor", "#f5f5f5"], ["minHeight", 750]]))], ["title-section", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["marginBottom", 24]]))], ["title-text", _pS(_uM([["fontSize", 22], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 8]]))], ["hint-text", _pS(_uM([["fontSize", 14], ["color", "#666666"]]))], ["form-section", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"]]))], ["form-row", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["marginBottom", 16]]))], ["label", _pS(_uM([["fontSize", 14], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 8]]))], ["input", _pS(_uM([["height", 44], ["paddingTop", 0], ["paddingRight", 12], ["paddingBottom", 0], ["paddingLeft", 12], ["borderTopWidth", 1], ["borderRightWidth", 1], ["borderBottomWidth", 1], ["borderLeftWidth", 1], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#dddddd"], ["borderRightColor", "#dddddd"], ["borderBottomColor", "#dddddd"], ["borderLeftColor", "#dddddd"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["backgroundColor", "#FFFFFF"]]))], ["input-placeholder", _pS(_uM([["color", "#999999"]]))], ["error-row", _pS(_uM([["marginBottom", 12]]))], ["error-text", _pS(_uM([["fontSize", 14], ["color", "#f44336"]]))], ["submit-btn", _pS(_uM([["marginTop", 8], ["paddingTop", 14], ["paddingRight", 14], ["paddingBottom", 14], ["paddingLeft", 14], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]]))], ["submit-btn-disabled", _pS(_uM([["backgroundColor", "#cccccc"]]))]])]
