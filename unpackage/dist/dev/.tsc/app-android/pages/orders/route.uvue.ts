import { ref, computed, getCurrentInstance } from 'vue'
import { onLoad, onReady } from '@dcloudio/uni-app'
import { BASE_URL } from '../../common/api.uts'

type LocationObject = { __$originalPosition?: UTSSourceMapPosition<"LocationObject", "pages/orders/route.uvue", 52, 6>; latitude: number, longitude: number }


const __sfc__ = defineComponent({
  __name: 'route',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

const bookingId = ref('')
const loading = ref(true)
const error = ref('')
const startLat = ref(null as number | null)
const startLng = ref(null as number | null)
const endLat = ref(null as number | null)
const endLng = ref(null as number | null)

const longitude = ref(116.397477)
const latitude = ref(39.908692)
const scale = ref(14)
const markers = ref([] as Marker[])
const polylines = ref([] as Polyline[])
const includePoints = ref([] as LocationObject[])
const mapKey = ref(0)
const mapUpdatedOnce = ref(false)
const mapContext = ref(null as MapContext | null)

const hasRoute = computed(() => {
	const slat = startLat.value
	const slng = startLng.value
	const elat = endLat.value
	const elng = endLng.value
	return slat != null && !isNaN(slat) && slng != null && !isNaN(slng) && elat != null && !isNaN(elat) && elng != null && !isNaN(elng)
})

const startLatText = ref('Lat: -')
const startLngText = ref('Lng: -')
const endLatText = ref('Lat: -')
const endLngText = ref('Lng: -')

const zoomIn = () => {
	if (scale.value < 18) {
		scale.value = scale.value + 1
	}
}

const zoomOut = () => {
	if (scale.value > 5) {
		scale.value = scale.value - 1
	}
}

const syncMarkersToMap = () => {
	if (mapContext.value == null) return
	if (markers.value.length == 0) return
	const tempMarkers = UTSAndroid.consoleDebugError(JSON.parse<Marker[]>(JSON.stringify(markers.value)), " at pages/orders/route.uvue:100")!
	const opts = { markers: tempMarkers, clear: true } as MapContextAddMarkersOptions
	mapContext.value?.addMarkers(opts)
}

const onMapUpdated = () => {
	mapUpdatedOnce.value = true
	syncMarkersToMap()
}

const applyRoute = (slat: number, slng: number, elat: number, elng: number) => {
	startLatText.value = 'Lat: ' + slat.toFixed(6)
	startLngText.value = 'Lng: ' + slng.toFixed(6)
	endLatText.value = 'Lat: ' + elat.toFixed(6)
	endLngText.value = 'Lng: ' + elng.toFixed(6)
	const startPoint: LocationObject = { latitude: slat, longitude: slng }
	const endPoint: LocationObject = { latitude: elat, longitude: elng }
	const eps = 0.002
	const samePoint = (slat == elat && slng == elng)
	const endLatForLine = samePoint ? (elat + eps) : elat
	const endLngForLine = samePoint ? (elng + eps) : elng
	const startLinePoint = ({ latitude: slat, longitude: slng } as uts.sdk.modules.DCloudUniMapTencent.LocationObject)
	const endLinePoint = ({ latitude: endLatForLine, longitude: endLngForLine } as uts.sdk.modules.DCloudUniMapTencent.LocationObject)
	const linePoints = [startLinePoint, endLinePoint] as uts.sdk.modules.DCloudUniMapTencent.LocationObject[]
	const rawIncludePoints: LocationObject[] = samePoint ? [startPoint, ({ latitude: endLatForLine, longitude: endLngForLine } as LocationObject)] : [startPoint, endPoint]
	const rawPolylines: Polyline[] = [({ points: linePoints, color: '#FF3B30', width: 10 } as Polyline)]
	polylines.value = rawPolylines
	includePoints.value = UTSAndroid.consoleDebugError(JSON.parse<LocationObject[]>(JSON.stringify(rawIncludePoints)), " at pages/orders/route.uvue:127")!
	longitude.value = (slng + elng) / 2
	latitude.value = (slat + elat) / 2
	scale.value = samePoint ? 17 : 14
	const rawMarkers: Marker[] = [
		{ id: 1, latitude: slat, longitude: slng, title: 'Start', iconPath: '/static/loc.png', width: 40, height: 40, anchor: { x: 0.5, y: 1 }, callout: { content: 'Start', display: 'ALWAYS' } as MapMarkerCallout } as Marker,
		{ id: 2, latitude: elat, longitude: elng, title: 'End', iconPath: '/static/loc.png', width: 40, height: 40, anchor: { x: 0.5, y: 1 }, callout: { content: 'End', display: 'ALWAYS' } as MapMarkerCallout } as Marker
	]
	markers.value = UTSAndroid.consoleDebugError(JSON.parse<Marker[]>(JSON.stringify(rawMarkers)), " at pages/orders/route.uvue:135")!
	mapKey.value = mapKey.value + 1
	console.log('applyRoute:', { slat, slng, elat, elng, markers: markers.value, includePoints: includePoints.value, polylines: polylines.value }, " at pages/orders/route.uvue:137")
	syncMarkersToMap()
	// Some native map implementations don't render markers on the first frame after data binding
	setTimeout(() => {
		if (mapUpdatedOnce.value) {
			mapKey.value = mapKey.value + 1
			syncMarkersToMap()
		}
	}, 200)
}

onReady(() => {
	const instance = getCurrentInstance()
	if (instance != null && instance.proxy != null) {
		mapContext.value = uni.createMapContext('routeMap', instance.proxy!)
	} else {
		mapContext.value = uni.createMapContext('routeMap')
	}
	syncMarkersToMap()
})

const loadBooking = () => {
	const userIdStorage = uni.getStorageSync('userId')
	const tokenStorage = uni.getStorageSync('token')
	const userId = userIdStorage != null && userIdStorage != '' ? ('' + userIdStorage) : null
	const token = tokenStorage != null && tokenStorage != '' ? ('' + tokenStorage) : null
	if (userId == null || token == null || bookingId.value.length == 0) {
		error.value = 'Please log in first'
		loading.value = false
		return
	}
	uni.request({
		url: BASE_URL + '/api/v1/users/' + userId + '/bookings/' + bookingId.value,
		method: 'GET',
		header: {
			'Content-Type': 'application/json',
			'Authorization': 'Bearer ' + token
		},
		success: (res) => {
			if (res.statusCode >= 200 && res.statusCode < 300 && res.data != null) {
				const obj = res.data as UTSJSONObject
				const data = obj['data'] != null ? (obj['data'] as UTSJSONObject) : obj
				const slatVal = data['start_lat']
				const slngVal = data['start_lng']
				const elatVal = data['end_lat']
				const elngVal = data['end_lng']
				let slat = slatVal != null && typeof slatVal === 'number' ? slatVal as number : null
				let slng = slngVal != null && typeof slngVal === 'number' ? slngVal as number : null
				let elat = elatVal != null && typeof elatVal === 'number' ? elatVal as number : null
				let elng = elngVal != null && typeof elngVal === 'number' ? elngVal as number : null
				// If we have start but no end (e.g. auto-completed order), use start as end so map can show
				if (slat != null && slng != null && (elat == null || elng == null)) {
					elat = slat
					elng = slng
				}
				startLat.value = slat
				startLng.value = slng
				endLat.value = elat
				endLng.value = elng
				if (slat != null && slng != null && elat != null && elng != null && !isNaN(slat) && !isNaN(slng) && !isNaN(elat) && !isNaN(elng)) {
					applyRoute(slat, slng, elat, elng)
				}
			} else {
				error.value = 'Failed to load booking'
			}
		},
		fail: () => {
			error.value = 'Network error'
		},
		complete: () => {
			loading.value = false
		}
	})
}

const goBack = () => {
	uni.navigateBack({ fail: () => { uni.navigateTo({ url: '/pages/orders/orders' }) } })
}

onLoad((options: UTSJSONObject) => {
	const parseNum = (v: any | null): number | null => {
		if (v == null) return null
		const raw = ('' + v).trim()
		if (raw.length == 0) return null
		const n = parseFloat(raw)
		return isNaN(n) ? null : n
	}

	const id = options != null && options['bookingId'] != null ? ('' + options['bookingId']) : ''
	bookingId.value = id
	const slatOpt = options != null ? parseNum(options['startLat']) : null
	const slngOpt = options != null ? parseNum(options['startLng']) : null
	let elatOpt = options != null ? parseNum(options['endLat']) : null
	let elngOpt = options != null ? parseNum(options['endLng']) : null
	if (slatOpt != null && slngOpt != null && (elatOpt == null || elngOpt == null)) {
		elatOpt = slatOpt
		elngOpt = slngOpt
	}

	if (slatOpt != null && slngOpt != null && elatOpt != null && elngOpt != null) {
		startLat.value = slatOpt
		startLng.value = slngOpt
		endLat.value = elatOpt
		endLng.value = elngOpt
		applyRoute(slatOpt, slngOpt, elatOpt, elngOpt)
		loading.value = false
		return
	}

	if (id.length > 0) {
		loadBooking()
	} else {
		error.value = 'Missing booking id'
		loading.value = false
	}
})

return (): any | null => {

const _component_map = resolveComponent("map")

  return _cE("view", _uM({ class: "container" }), [
    isTrue(loading.value)
      ? _cE("view", _uM({
          key: 0,
          class: "loading-section"
        }), [
          _cE("text", _uM({ class: "loading-text" }), "Loading route...")
        ])
      : error.value.length > 0
        ? _cE("view", _uM({
            key: 1,
            class: "error-section"
          }), [
            _cE("text", _uM({ class: "error-text" }), _tD(error.value), 1 /* TEXT */),
            _cE("button", _uM({
              class: "back-btn",
              onClick: goBack
            }), "Back")
          ])
        : isTrue(!hasRoute.value)
          ? _cE("view", _uM({
              key: 2,
              class: "no-route-section"
            }), [
              _cE("text", _uM({ class: "no-route-text" }), "Route data not available for this order."),
              _cE("text", _uM({ class: "no-route-hint" }), "Start and end locations are recorded when you start and end the ride."),
              _cE("button", _uM({
                class: "back-btn",
                onClick: goBack
              }), "Back")
            ])
          : _cE("view", _uM({
              key: 3,
              class: "map-wrap"
            }), [
              _cE("view", _uM({ class: "map-section" }), [
                _cV(_component_map, _uM({
                  key: mapKey.value,
                  id: "routeMap",
                  class: "route-map",
                  longitude: longitude.value,
                  latitude: latitude.value,
                  scale: scale.value,
                  markers: markers.value,
                  polyline: polylines.value,
                  "include-points": includePoints.value,
                  onUpdated: onMapUpdated
                }), null, 8 /* PROPS */, ["longitude", "latitude", "scale", "markers", "polyline", "include-points"]),
                _cE("cover-view", _uM({ class: "map-controls" }), [
                  _cE("cover-view", _uM({
                    class: "control-btn",
                    onClick: zoomIn
                  }), "+"),
                  _cE("cover-view", _uM({
                    class: "control-btn",
                    onClick: zoomOut
                  }), "-")
                ])
              ]),
              _cE("view", _uM({ class: "info-section" }), [
                _cE("text", _uM({ class: "info-label" }), "Start"),
                _cE("text", _uM({ class: "info-value" }), _tD(startLatText.value), 1 /* TEXT */),
                _cE("text", _uM({ class: "info-value" }), _tD(startLngText.value), 1 /* TEXT */),
                _cE("text", _uM({ class: "info-label" }), "End"),
                _cE("text", _uM({ class: "info-value" }), _tD(endLatText.value), 1 /* TEXT */),
                _cE("text", _uM({ class: "info-value" }), _tD(endLngText.value), 1 /* TEXT */)
              ]),
              _cE("button", _uM({
                class: "back-btn",
                onClick: goBack
              }), "Back to Orders")
            ])
  ])
}
}

})
export default __sfc__
const GenPagesOrdersRouteStyles = [_uM([["container", _pS(_uM([["width", "100%"], ["height", "100%"], ["display", "flex"], ["flexDirection", "column"], ["backgroundColor", "#f5f5f5"], ["boxSizing", "border-box"]]))], ["loading-section", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 24], ["paddingRight", 24], ["paddingBottom", 24], ["paddingLeft", 24]]))], ["error-section", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 24], ["paddingRight", 24], ["paddingBottom", 24], ["paddingLeft", 24]]))], ["no-route-section", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["display", "flex"], ["flexDirection", "column"], ["alignItems", "center"], ["justifyContent", "center"], ["paddingTop", 24], ["paddingRight", 24], ["paddingBottom", 24], ["paddingLeft", 24]]))], ["loading-text", _pS(_uM([["fontSize", 16], ["color", "#666666"]]))], ["error-text", _pS(_uM([["fontSize", 16], ["color", "#f44336"], ["textAlign", "center"], ["marginBottom", 12]]))], ["no-route-text", _pS(_uM([["fontSize", 16], ["color", "#f44336"], ["textAlign", "center"], ["marginBottom", 12]]))], ["no-route-hint", _pS(_uM([["fontSize", 14], ["color", "#999999"], ["textAlign", "center"], ["marginBottom", 20]]))], ["back-btn", _pS(_uM([["marginTop", 16], ["paddingTop", 10], ["paddingRight", 24], ["paddingBottom", 10], ["paddingLeft", 24], ["backgroundColor", "#2196F3"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 14], ["fontWeight", "bold"]]))], ["map-wrap", _pS(_uM([["flexGrow", 1], ["flexShrink", 1], ["flexBasis", "0%"], ["display", "flex"], ["flexDirection", "column"], ["paddingTop", 16], ["paddingRight", 16], ["paddingBottom", 16], ["paddingLeft", 16]]))], ["map-section", _pS(_uM([["width", "100%"], ["position", "relative"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["overflow", "hidden"], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"], ["marginBottom", 16]]))], ["map-controls", _pS(_uM([["position", "absolute"], ["right", 12], ["bottom", 12], ["display", "flex"], ["flexDirection", "column"], ["zIndex", 10]]))], ["control-btn", _pS(_uM([["width", 36], ["height", 36], ["lineHeight", "36px"], ["textAlign", "center"], ["backgroundColor", "rgba(0,0,0,0.55)"], ["color", "#ffffff"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["marginTop", 8], ["fontSize", 18], ["fontWeight", "bold"]]))], ["route-map", _pS(_uM([["width", "100%"], ["height", 320], ["backgroundColor", "#e5e5e5"]]))], ["info-section", _pS(_uM([["backgroundColor", "#ffffff"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["paddingTop", 16], ["paddingRight", 16], ["paddingBottom", 16], ["paddingLeft", 16], ["marginBottom", 16], ["boxShadow", "0 2px 8px rgba(0, 0, 0, 0.08)"]]))], ["info-label", _pS(_uM([["fontSize", 12], ["color", "#999999"], ["marginBottom", 4]]))], ["info-value", _pS(_uM([["fontSize", 14], ["color", "#333333"], ["marginBottom", 12], ["marginBottom:last-of-type", 0]]))]])]
