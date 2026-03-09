import { ref, onMounted } from 'vue'
import { buildApiUrl } from '../../common/apiConfig.uts'

type DurationRow = { __$originalPosition?: UTSSourceMapPosition<"DurationRow", "pages/admin/revenue.uvue", 38, 6>;
	durationType: string
	totalRevenue: string
	totalOrders: number
}


const __sfc__ = defineComponent({
  __name: 'revenue',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const rows = ref<DurationRow[]>([])
const isLoading = ref(false)

const loadData = () => {
	if (isLoading.value) return
	isLoading.value = true

	const tokenRaw = uni.getStorageSync('token')
	const token = (tokenRaw != null && tokenRaw !== '') ? ('' + tokenRaw).trim() : ''

	if (token.length === 0) {
		uni.showToast({ title: 'Please login first', icon: 'none' })
		isLoading.value = false
		return
	}

	uni.request({
		url: buildApiUrl('/api/v1/admin/revenue/duration-week'),
		method: 'GET',
		header: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + token
		},
		success: (res) => {
			if (res.statusCode >= 200 && res.statusCode < 300) {
				const root = res.data as UTSJSONObject
				const result: DurationRow[] = []

				const dataField = root['data']
				if (dataField != null && Array.isArray(dataField)) {
					const arr = dataField as UTSJSONObject[]
					for (let i = 0; i < arr.length; i++) {
						const item = arr[i]
						const dtRaw = item['durationType']
						const trRaw = item['totalRevenue']
						const toRaw = item['totalOrders']
						const dt = dtRaw != null ? ('' + dtRaw) : 'Unknown'
						const trStr = trRaw != null ? ('' + trRaw) : '0'
						const toInt = toRaw != null ? parseInt('' + toRaw) : 0
						result.push({
							durationType: dt,
							totalRevenue: trStr,
							totalOrders: toInt
						})
					}
				} else if (Array.isArray(root as any)) {
					const arr = (root as any) as UTSJSONObject[]
					for (let i = 0; i < arr.length; i++) {
						const item = arr[i]
						const dtRaw = item['durationType']
						const trRaw = item['totalRevenue']
						const toRaw = item['totalOrders']
						const dt = dtRaw != null ? ('' + dtRaw) : 'Unknown'
						const trStr = trRaw != null ? ('' + trRaw) : '0'
						const toInt = toRaw != null ? parseInt('' + toRaw) : 0
						result.push({
							durationType: dt,
							totalRevenue: trStr,
							totalOrders: toInt
						})
					}
				}

				rows.value = result
			} else {
				uni.showToast({ title: 'Failed to load revenue', icon: 'none' })
			}
		},
		fail: () => {
			uni.showToast({ title: 'Network error', icon: 'none' })
		},
		complete: () => {
			isLoading.value = false
		}
	})
}

onMounted(() => {
	loadData()
})

return (): any | null => {

  return _cE("view", _uM({ class: "container" }), [
    _cE("view", _uM({ class: "title-section" }), [
      _cE("text", _uM({ class: "title-text" }), "Weekly Revenue by Duration"),
      _cE("text", _uM({ class: "hint-text" }), "Current week grouped by rental duration")
    ]),
    isTrue(isLoading.value)
      ? _cE("view", _uM({
          key: 0,
          class: "card"
        }), [
          _cE("text", _uM({ class: "loading-text" }), "Loading...")
        ])
      : rows.value.length == 0
        ? _cE("view", _uM({
            key: 1,
            class: "card"
          }), [
            _cE("text", _uM({ class: "empty-text" }), "No bookings for this week.")
          ])
        : _cE("view", _uM({
            key: 2,
            class: "card"
          }), [
            _cE(Fragment, null, RenderHelpers.renderList(rows.value, (row, index, __index, _cached): any => {
              return _cE("view", _uM({
                key: index,
                class: "row"
              }), [
                _cE("view", _uM({ class: "row-left" }), [
                  _cE("text", _uM({ class: "duration-text" }), _tD(row.durationType), 1 /* TEXT */),
                  _cE("text", _uM({ class: "orders-text" }), _tD(row.totalOrders) + " orders", 1 /* TEXT */)
                ]),
                _cE("view", _uM({ class: "row-right" }), [
                  _cE("text", _uM({ class: "revenue-text" }), "£" + _tD(row.totalRevenue), 1 /* TEXT */)
                ])
              ])
            }), 128 /* KEYED_FRAGMENT */)
          ]),
    _cE("button", _uM({
      class: "reload-btn",
      disabled: isLoading.value,
      onClick: loadData
    }), _tD(isLoading.value ? 'Loading...' : 'Reload'), 9 /* TEXT, PROPS */, ["disabled"])
  ])
}
}

})
export default __sfc__
const GenPagesAdminRevenueStyles = [_uM([["container", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["paddingTop", 24], ["paddingRight", 24], ["paddingBottom", 24], ["paddingLeft", 24], ["backgroundColor", "#f5f5f5"]]))], ["title-section", _pS(_uM([["display", "flex"], ["flexDirection", "column"], ["marginBottom", 24]]))], ["title-text", _pS(_uM([["fontSize", 22], ["fontWeight", "bold"], ["color", "#333333"], ["marginBottom", 8]]))], ["hint-text", _pS(_uM([["fontSize", 14], ["color", "#666666"]]))], ["card", _pS(_uM([["backgroundColor", "#ffffff"], ["borderTopLeftRadius", 12], ["borderTopRightRadius", 12], ["borderBottomRightRadius", 12], ["borderBottomLeftRadius", 12], ["paddingTop", 20], ["paddingRight", 20], ["paddingBottom", 20], ["paddingLeft", 20], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"], ["marginBottom", 24]]))], ["row", _pS(_uM([["display", "flex"], ["flexDirection", "row"], ["justifyContent", "space-between"], ["alignItems", "center"], ["marginBottom", 12], ["marginBottom:last-child", 0]]))], ["row-left", _pS(_uM([["display", "flex"], ["flexDirection", "column"]]))], ["duration-text", _pS(_uM([["fontSize", 16], ["fontWeight", "bold"], ["color", "#333333"]]))], ["orders-text", _pS(_uM([["fontSize", 12], ["color", "#666666"], ["marginTop", 4]]))], ["revenue-text", _pS(_uM([["fontSize", 16], ["fontWeight", "bold"], ["color", "#4CAF50"]]))], ["loading-text", _pS(_uM([["fontSize", 14], ["color", "#666666"]]))], ["empty-text", _pS(_uM([["fontSize", 14], ["color", "#666666"]]))], ["reload-btn", _pS(_uM([["paddingTop", 14], ["paddingRight", 14], ["paddingBottom", 14], ["paddingLeft", 14], ["backgroundColor", "#4CAF50"], ["color", "#ffffff"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["borderTopWidth", 0], ["borderRightWidth", 0], ["borderBottomWidth", 0], ["borderLeftWidth", 0], ["fontSize", 16], ["fontWeight", "bold"]]))]])]
