import { ref, onMounted, getCurrentInstance } from 'vue'
	import { onShow } from '@dcloudio/uni-app'
	import { BASE_URL } from '../../common/api.uts'
	
	
const __sfc__ = defineComponent({
  __name: 'index',
  setup(__props) {
const __ins = getCurrentInstance()!;
const _ctx = __ins.proxy as InstanceType<typeof __sfc__>;
const _cache = __ins.renderCache;

	const longitude = ref(116.392325)
	const latitude = ref(40.01811)
	const scale = ref(16)
	const markers = ref([] as Marker[])
	const mapContext = ref(null as MapContext | null)
	
	const buildNearbyMarkers = (lat: number, lng: number): Marker[] => {
	  return UTSAndroid.consoleDebugError(JSON.parse<Marker[]>(JSON.stringify([
	    { id: 1, longitude: lng - 0.0006, latitude: lat - 0.0007, title: '附近1', iconPath: '/static/location.png', width: 32, height: 32, anchor: { x: 0.5, y: 1 } },
	    { id: 2, longitude: lng + 0.0005, latitude: lat + 0.0005, title: '附近2', iconPath: '/static/location.png', width: 32, height: 32, anchor: { x: 0.5, y: 1 } },
	    { id: 3, longitude: lng - 0.0003, latitude: lat + 0.0004, title: '附近3', iconPath: '/static/location.png', width: 32, height: 32, anchor: { x: 0.5, y: 1 } }
	  ])), " at pages/index/index.uvue:66")!
	}
	
	const fetchScooterMarkers = (nearby: Marker[], lat: number, lng: number) => {
	  uni.request({
	    url: BASE_URL + '/api/v1/scooters',
	    method: 'GET',
	    success: (res) => {
	      if (res.statusCode != 200 || res.data == null) {
	        markers.value = nearby
	        return
	      }
	      const raw = res.data as UTSJSONObject
	      let arr: UTSJSONObject[] = []
	      if (raw['data'] != null) {
	        arr = raw['data'] as UTSJSONObject[]
	      } else if (raw['scooters'] != null) {
	        arr = raw['scooters'] as UTSJSONObject[]
	      } else if (raw['list'] != null) {
	        arr = raw['list'] as UTSJSONObject[]
	      }
	      const scooterMarkers: Marker[] = []
	      let count = 0
	      for (let i = 0; i < arr.length && count < 5; i++) {
	        const item = arr[i] as UTSJSONObject
	        const latVal = item['location_lat'] != null ? item['location_lat'] : item['locationLat']
	        const lngVal = item['location_lng'] != null ? item['location_lng'] : item['locationLng']
	        const numLat = latVal != null ? (latVal as number) : null
	        const numLng = lngVal != null ? (lngVal as number) : null
	        if (numLat != null && numLng != null && !isNaN(numLat) && !isNaN(numLng)) {
	          const rawId = item['id']
	          const sid = rawId != null ? ('SC' + ('' + rawId)) : ('SC' + (count + 1))
	          scooterMarkers.push({
	            id: 10 + count,
	            latitude: numLat,
	            longitude: numLng,
	            title: sid,
	            iconPath: '/static/location.png',
	            width: 32,
	            height: 32,
	            anchor: { x: 0.5, y: 1 }
	          } as Marker)
	          count++
	        }
	      }
	      markers.value = nearby.concat(scooterMarkers)
	    },
	    fail: () => {
	      markers.value = nearby
	    }
	  })
	}
	
	const applyMarkers = (lat: number, lng: number) => {
	  const nearby = buildNearbyMarkers(lat, lng)
	  fetchScooterMarkers(nearby, lat, lng)
	}
	
	const getCurrentLocation = () => {
	  uni.getLocation({
	    type: 'gcj02',
	    success: (res) => {
	      const { longitude: lng, latitude: lat } = res
	      longitude.value = lng
	      latitude.value = lat
	      applyMarkers(lat, lng)
	      mapContext.value?.moveToLocation({ latitude: lat, longitude: lng })
	    },
	    fail: (err) => {
	      console.error('定位失败', err, " at pages/index/index.uvue:139")
	      uni.showToast({ title: '定位失败，请开启权限', icon: 'none' })
	    }
	  })
	}
	
	onMounted(() => {
	  const instance = getCurrentInstance()
	  if (instance != null && instance.proxy != null) {
	    mapContext.value = uni.createMapContext('mainMap', instance.proxy!)
	  } else {
	    mapContext.value = uni.createMapContext('mainMap')
	  }
	  getCurrentLocation()
	})
	
	onShow(() => {
	  applyMarkers(latitude.value, longitude.value)
	})
	
	// 跳转到单车列表页面
	const goToBikeList = () => {
		uni.navigateTo({
			url: '/pages/bikelist/bikelist'
		});
	}

return (): any | null => {

const _component_map = resolveComponent("map")
const _component_navigator = resolveComponent("navigator")

  return _cE(Fragment, null, [
    _cE("view", _uM({ class: "slogan1" }), [
      _cE("text", _uM({ class: "text1" }), "We offer you a variety of options.")
    ]),
    _cE("view", _uM({ class: "span1" }), [
      _cE("view", _uM({ class: "span1_left" }), [
        _cE("image", _uM({
          src: "/static/broadcast.png",
          mode: "aspectFill",
          class: "icon1"
        }))
      ]),
      _cE("swiper", _uM({
        class: "swiper1",
        vertical: "true",
        autoplay: "true",
        circular: "",
        interval: "2000",
        duration: "1000"
      }), [
        _cE("swiper-item", null, [
          _cE("text", _uM({ class: "text2" }), "Before cycling, please check the brakes, seat, pedals and other components.")
        ]),
        _cE("swiper-item", null, [
          _cE("text", _uM({ class: "text2" }), "Remember to return the bike when the ride is over.")
        ])
      ])
    ]),
    _cE("swiper", _uM({
      class: "swiper2",
      "indicator-dots": true,
      autoplay: true,
      interval: 2000,
      duration: 1000,
      "indicator-color": "rgba(255,255,255,0.3)",
      "indicator-active-color": "#c0c0c0",
      circular: ""
    }), [
      _cE("swiper-item", null, [
        _cE("image", _uM({
          src: "/static/MeiTuanbike.jpg",
          class: "swipic1"
        }))
      ]),
      _cE("swiper-item", null, [
        _cE("image", _uM({
          src: "/static/HelloBike.jpg",
          class: "swipic2"
        }))
      ]),
      _cE("swiper-item", null, [
        _cE("image", _uM({
          src: "/static/Scooter.jpg",
          class: "swipic3"
        }))
      ]),
      _cE("swiper-item", null, [
        _cE("image", _uM({
          src: "/static/EleBike.jpg",
          class: "swipic4"
        }))
      ])
    ]),
    _cE("view", _uM({ class: "map_container" }), [
      _cV(_component_map, _uM({
        id: "mainMap",
        class: "map",
        longitude: longitude.value,
        latitude: latitude.value,
        scale: scale.value,
        markers: markers.value,
        "show-location": ""
      }), null, 8 /* PROPS */, ["longitude", "latitude", "scale", "markers"])
    ]),
    _cE("view", _uM({ class: "booking-section" }), [
      _cE("button", _uM({
        class: "booking-button",
        onClick: goToBikeList
      }), " appoint ")
    ]),
    _cE("view", _uM({ class: "bottom" }), [
      _cE("view", _uM({ class: "bottom_left" }), [
        _cE("image", _uM({
          src: "/static/home.png",
          mode: "aspectFit",
          class: "icon2"
        })),
        _cE("view", _uM({ class: "left_down" }), [
          _cE("text", _uM({ class: "text3" }), "Home")
        ])
      ]),
      _cV(_component_navigator, _uM({
        url: "/pages/bikelist/bikelist",
        "open-type": "reLaunch",
        "hover-class": "navigator-hover",
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
      _cV(_component_navigator, _uM({
        url: "/pages/me/me",
        "open-type": "reLaunch",
        "hover-class": "navigator-hover",
        class: "bottom_right"
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
  ], 64 /* STABLE_FRAGMENT */)
}
}

})
export default __sfc__
const GenPagesIndexIndexStyles = [_uM([["slogan1", _pS(_uM([["display", "flex"], ["alignItems", "center"], ["height", 20]]))], ["span1", _pS(_uM([["width", "100%"], ["height", 20]]))], ["span1_left", _uM([[".span1 ", _uM([["position", "absolute"], ["left", "1%"], ["height", "100%"], ["width", "5%"], ["backgroundColor", "#F0F8FF"]])]])], ["icon1", _uM([[".span1 .span1_left ", _uM([["width", "100%"], ["height", "100%"]])]])], ["swiper1", _uM([[".span1 ", _uM([["position", "absolute"], ["left", "6%"], ["height", "100%"], ["width", "94%"]])]])], ["text2", _uM([[".span1 .swiper1 ", _uM([["marginTop", "auto"], ["marginRight", "auto"], ["marginBottom", "auto"], ["marginLeft", "auto"], ["fontSize", 9]])]])], ["swiper2", _pS(_uM([["width", "100%"], ["height", 200]]))], ["swipic1", _uM([[".swiper2 ", _uM([["width", "100%"]])]])], ["swipic2", _uM([[".swiper2 ", _uM([["width", "100%"]])]])], ["swipic3", _uM([[".swiper2 ", _uM([["width", "100%"]])]])], ["swipic4", _uM([[".swiper2 ", _uM([["width", "100%"]])]])], ["map", _pS(_uM([["width", "100%"], ["height", 300]]))], ["booking-section", _pS(_uM([["width", "90%"], ["marginTop", 20], ["marginRight", "auto"], ["marginBottom", 20], ["marginLeft", "auto"], ["display", "flex"], ["justifyContent", "center"]]))], ["booking-button", _uM([[".booking-section ", _uM([["width", "100%"], ["height", 45], ["backgroundColor", "#4CAF50"], ["color", "#FFFFFF"], ["borderTopWidth", "medium"], ["borderRightWidth", "medium"], ["borderBottomWidth", "medium"], ["borderLeftWidth", "medium"], ["borderTopStyle", "none"], ["borderRightStyle", "none"], ["borderBottomStyle", "none"], ["borderLeftStyle", "none"], ["borderTopColor", "#000000"], ["borderRightColor", "#000000"], ["borderBottomColor", "#000000"], ["borderLeftColor", "#000000"], ["borderTopLeftRadius", 8], ["borderTopRightRadius", 8], ["borderBottomRightRadius", 8], ["borderBottomLeftRadius", 8], ["fontSize", 16], ["fontWeight", "bold"]])]])], ["bottom", _pS(_uM([["width", "100%"], ["height", "8%"], ["backgroundImage", "none"], ["backgroundColor", "#E3E1E1"], ["position", "absolute"], ["bottom", 0], ["borderTopLeftRadius", 25], ["borderTopRightRadius", 25], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0]]))], ["bottom_left", _uM([[".bottom ", _uM([["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginLeft", "5%"]])]])], ["icon2", _uM([[".bottom .bottom_left ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])], [".bottom .bottom_right ", _uM([["position", "absolute"], ["left", "20%"], ["width", "60%"], ["height", "60%"]])]])], ["left_down", _uM([[".bottom .bottom_left ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_center", _uM([[".bottom ", _uM([["position", "absolute"], ["left", "40%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"]])]])], ["bike_icon", _uM([[".bottom .bottom_center ", _uM([["position", "absolute"], ["left", "25%"], ["width", "50%"], ["height", "60%"]])]])], ["center_down", _uM([[".bottom .bottom_center ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])], ["bottom_right", _uM([[".bottom ", _uM([["position", "absolute"], ["right", "0%"], ["borderTopLeftRadius", 0], ["borderTopRightRadius", 0], ["borderBottomRightRadius", 0], ["borderBottomLeftRadius", 0], ["width", "20%"], ["height", "100%"], ["marginRight", "5%"]])]])], ["right_down", _uM([[".bottom .bottom_right ", _uM([["display", "flex"], ["justifyContent", "center"], ["alignItems", "center"], ["position", "absolute"], ["bottom", "0%"], ["width", "100%"], ["height", "40%"]])]])]])]
