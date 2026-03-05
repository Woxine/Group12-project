import { ref } from 'vue'
import { BASE_URL } from '../../common/api.uts'


const __sfc__ = defineComponent({
  __name: 'feedback',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const scooterId = ref('')
const description = ref('')
const location = ref('')
const isSubmitting = ref(false)
const submitError = ref('')

const onSubmit = () => {
	submitError.value = ''
	const sid = scooterId.value != null ? scooterId.value.trim() : ''
	const desc = description.value != null ? description.value.trim() : ''
	const loc = location.value != null ? location.value.trim() : ''

	if (sid.length === 0) {
		submitError.value = 'Please enter Scooter ID'
		uni.showToast({ title: 'Please enter Scooter ID', icon: 'none' })
		return
	}
	if (desc.length === 0) {
		submitError.value = 'Please enter a description'
		uni.showToast({ title: 'Please enter a description', icon: 'none' })
		return
	}

	isSubmitting.value = true

	const tokenRaw = uni.getStorageSync('token')
	const token = (tokenRaw != null && tokenRaw !== '') ? ('' + tokenRaw).trim() : ''
	const header: UTSJSONObject = {__$originalPosition: new UTSSourceMapPosition("header", "pages/feedback/feedback.uvue", 79, 8),
		'Content-Type': 'application/json'
	}
	if (token.length > 0) {
		header['Authorization'] = 'Bearer ' + token
	}

	const body: UTSJSONObject = {__$originalPosition: new UTSSourceMapPosition("body", "pages/feedback/feedback.uvue", 86, 8),
		'scooter_id': sid,
		'description': desc
	}
	if (loc.length > 0) {
		body['location'] = loc
	}

	uni.request({
		url: BASE_URL + '/api/v1/feedbacks',
		method: 'POST',
		header: header,
		data: body,
		success: (res) => {
			if (res.statusCode >= 200 && res.statusCode < 300) {
				uni.showToast({ title: 'Feedback submitted', icon: 'success' })
				scooterId.value = ''
				description.value = ''
				location.value = ''
			} else {
				let msg = 'Failed to submit feedback'
				const data = res.data as UTSJSONObject | null
				if (data != null && data['message'] != null) {
					msg = '' + data['message']
				} else if (data != null && data['error'] != null) {
					msg = '' + data['error']
				}
				submitError.value = msg
				uni.showToast({ title: msg, icon: 'none', duration: 3000 })
			}
		},
		fail: () => {
			submitError.value = 'Network error. Please try again.'
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
      _cE("text", _uM({ class: "title-text" }), "Submit Feedback"),
      _cE("text", _uM({ class: "hint-text" }), "Report an issue or suggest an improvement")
    ]),
    _cE("view", _uM({ class: "form-section" }), [
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Scooter ID"),
        _cE("input", _uM({
          class: "input",
          modelValue: scooterId.value,
          onInput: ($event: UniInputEvent) => {(scooterId).value = $event.detail.value},
          placeholder: "e.g. 1 or SC001",
          "placeholder-class": "input-placeholder"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
      ]),
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Description"),
        _cE("textarea", _uM({
          class: "textarea",
          modelValue: description.value,
          onInput: ($event: UniInputEvent) => {(description).value = $event.detail.value},
          placeholder: "Describe your feedback (required)",
          "placeholder-class": "input-placeholder",
          maxlength: 500
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"]),
        _cE("text", _uM({ class: "char-count" }), _tD(description.value.length) + "/500", 1 /* TEXT */)
      ]),
      _cE("view", _uM({ class: "form-row" }), [
        _cE("text", _uM({ class: "label" }), "Location (optional)"),
        _cE("input", _uM({
          class: "input",
          modelValue: location.value,
          onInput: ($event: UniInputEvent) => {(location).value = $event.detail.value},
          placeholder: "e.g. Street name or area",
          "placeholder-class": "input-placeholder"
        }), null, 40 /* PROPS, NEED_HYDRATION */, ["modelValue", "onInput"])
      ]),
      submitError.value.length > 0
        ? _cE("view", _uM({
            key: 0,
            class: "error-row"
          }), [
            _cE("text", _uM({ class: "error-text" }), _tD(submitError.value), 1 /* TEXT */)
          ])
        : _cC("v-if", true),
      _cE("button", _uM({
        class: "submit-btn",
        disabled: isSubmitting.value,
        onClick: onSubmit
      }), "Submit Feedback", 8 /* PROPS */, ["disabled"])
    ])
  ])
}
}

})
export default __sfc__
const GenPagesFeedbackFeedbackStyles = [_uM([["container", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["paddingTop", 24], ["paddingRight", 24], ["paddingBottom", 24], ["paddingLeft", 24], ["backgroundColor", "#f5f5f5"]]))], ["title-section", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["marginBottom", 24]]))], ["title-text", _pS(_uM([["fontSize", 22], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 8]]))], ["hint-text", _pS(_uM([["fontSize", 14], ["color", "#666666"]]))], ["form-section", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["backgroundColor", "#FFFFFF"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"]]))], ["form-row", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["marginBottom", 16]]))], ["label", _pS(_uM([["fontSize", 14], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 8]]))], ["input", _pS(_uM([["height", 44], ["paddingTop", 0], ["paddingRight", 12], ["paddingBottom", 0], ["paddingLeft", 12], ["borderTopWidth", 1], ["borderRightWidth", 1], ["borderBottomWidth", 1], ["borderLeftWidth", 1], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#dddddd"], ["borderRightColor", "#dddddd"], ["borderBottomColor", "#dddddd"], ["borderLeftColor", "#dddddd"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["backgroundColor", "#FFFFFF"]]))], ["textarea", _pS(_uM([["minHeight", 100], ["paddingTop", 12], ["paddingRight", 12], ["paddingBottom", 12], ["paddingLeft", 12], ["borderTopWidth", 1], ["borderRightWidth", 1], ["borderBottomWidth", 1], ["borderLeftWidth", 1], ["borderTopStyle", "solid"], ["borderRightStyle", "solid"], ["borderBottomStyle", "solid"], ["borderLeftStyle", "solid"], ["borderTopColor", "#dddddd"], ["borderRightColor", "#dddddd"], ["borderBottomColor", "#dddddd"], ["borderLeftColor", "#dddddd"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["backgroundColor", "#FFFFFF"]]))], ["input-placeholder", _pS(_uM([["color", "#999999"]]))], ["char-count", _pS(_uM([["fontSize", 12], ["color", "#999999"], ["marginTop", 4]]))], ["error-row", _pS(_uM([["marginBottom", 12]]))], ["error-text", _pS(_uM([["fontSize", 14], ["color", "#f44336"]]))], ["submit-btn", _pS(_uM([["marginTop", 8], ["paddingTop", 14], ["paddingRight", 14], ["paddingBottom", 14], ["paddingLeft", 14], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]]))]])]
