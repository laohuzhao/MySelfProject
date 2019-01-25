<template>
  <div class="filter-container" style="background-color:#DCDCDC;padding:5px;">
    <el-row>
      <el-col :span="24">
        <el-row style="background-color:#FFFFFF;border-radius:5px;margin-bottom:10px;">
          <el-col :span="4" style="padding:10px;text-align:center;">
            <el-card :body-style="{ padding: '0px' }" style="text-align:center;">
              <div style="padding: 5px;width:100%;" @click="handlerClickType(1)">
                <div class="bottom clearfix">
                  <el-button type="text" class="button" style="font-size:26px;">
                    <el-badge>{{global.fuel_level_fault}}</el-badge>
                  </el-button>
                </div>
                <span>油位故障</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="4" style="padding:10px;">
            <el-card :body-style="{ padding: '0px' }" style="text-align:center;">
              <div style="padding: 5px;" @click="handlerClickType(2)">
                <div class="bottom clearfix">
                  <el-button type="text" class="button" style="font-size:26px;">
                    <el-badge>{{global.fuel_pressure_fault}}</el-badge>
                  </el-button>
                </div>
                <span>油压故障</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="4" style="padding:10px;">
            <el-card :body-style="{ padding: '0px' }" style="text-align:center;">
              <div style="padding: 5px;" @click="handlerClickType(3)">
                <div class="bottom clearfix">
                  <el-button type="text" class="button" style="font-size:26px;">
                    <el-badge>{{global.shutting}}</el-badge>
                  </el-button>
                </div>
                <span>设备休止</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="4" style="padding:10px;">
            <el-card :body-style="{ padding: '0px' }" style="text-align:center;">
              <div style="padding: 5px;" @click="handlerClickType(4)">
                <div class="bottom clearfix">
                  <el-button type="text" class="button" style="font-size:26px;">
                    <el-badge>{{global.working}}</el-badge>
                  </el-button>
                </div>
                <span>正在润滑</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="4" style="padding:10px;">
            <el-card :body-style="{ padding: '0px' }" style="text-align:center;">
              <div style="padding: 5px;" @click="handlerClickType(5)">
                <div class="bottom clearfix">
                  <el-button type="text" class="button" style="font-size:26px;">
                    <el-badge>{{global.online}}</el-badge>
                  </el-button>
                </div>
                <span>设备在线</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="4" style="padding:10px;">
            <el-card :body-style="{ padding: '0px' }" style="text-align:center;">
              <div style="padding: 5px;" @click="handlerClickType(6)">
                <div class="bottom clearfix">
                  <el-button type="text" class="button" style="font-size:26px;">
                    <el-badge>{{global.offline}}</el-badge>
                  </el-button>
                </div>
                <span>设备离线</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-row style="padding:10px;background-color:#FFFFFF;border-radius:5px;">
      <el-col :span="24">
        <el-input @keyup.enter.native="handleFilter" style="width: 140px;" class="filter-item" placeholder="按公司名进行搜索" v-model="listQuery.corp_name">
        </el-input>
        <el-select style="width:120px;" filterable clearable v-model="listQuery.device_type" placeholder="设备类别">
          <el-option v-for="item in deviceTypeList" :key="item.code" :label="item.name" :value="item.code">
          </el-option>
        </el-select>
        <el-select style="width:130px;" filterable clearable v-model="listQuery.area_code" placeholder="地域">
          <el-option v-for="item in areacode_options" :key="item.code" :label="item.name" :value="item.code">
          </el-option>
        </el-select>
        <el-input @keyup.enter.native="handleFilter" style="width: 100px;" class="filter-item" placeholder="设备标签" v-model="listQuery.tag">
        </el-input>
        <el-button class="filter-item" type="primary" icon="search" @click="handlerFilter">搜索</el-button>
        
        <div style="float:right;padding-top:5px;">
          <icon-svg icon-class="map" style="color:#1E90FF;margin-right:5px;"></icon-svg>
          <a href="#/device/list"><icon-svg icon-class="list"></icon-svg></a>
        </div>

        <div id="baiduMap" class="app-container" :style="mapStyle" ></div>
      </el-col>
    </el-row>
  </div>
</template>

<style>
.el-col-4-- {
  width: 14%;
}
.el-card {
  width:80%;padding-top:0px;marin-top:0px;
}
</style>

<script>
import { dashboard, getDeviceMap } from '@/api/device';
import { fWorkingState, fProcessingState, fFuelLevelState, fFuelPressureState } from '@/api/common';
import { getDictList } from '@/api/setting'

export default {
  name: '',
  data() {
    return {
      global: {
        fuel_level_fault: 0,
        fuel_pressure_fault: 0,
        shutting: 0,
        working: 0,
        online: 0,
        offline: 0
      },
      map: undefined,
      deviceTypeList: [],
      areacode_options: [],
      listQuery: {
        state_x: 0,
        device_id: undefined,
        corp_name: undefined,
        device_type: undefined,
        area_code: undefined,
        tag: undefined
      },
      markerClusterer: undefined,
      mapStyle: 'margin-top:5px;'
    }
  },
  mounted() {
    const height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height
    this.mapStyle = 'margin-top:5px;height:' + (height - 250) + 'px'

    dashboard({}).then(response => {
      this.global.fuel_level_fault = response.data.fuel_level_fault;
      this.global.fuel_pressure_fault = response.data.fuel_pressure_fault;
      this.global.shutting = response.data.shutting;
      this.global.working = response.data.working;
      this.global.online = response.data.online;
      this.global.offline = response.data.offline;
    });
    this.initMap()

    getDictList({ type: 'dtype' }).then(response => {
      if (response.code === 20000) {
        var items = response.data.items
        for (var a in items) {
          var item = items[a]
          this.deviceTypeList.push({
            code: item.code, name: item.code + ',' + item.name
          })
        }
      }
    })

    getDictList({ type: 'areacode_select' }).then(response => {
      var items = response.data.items
      for (var a in items) {
        var item = items[a]
        this.areacode_options.push({
          code: item.id, name: item.code + ',' + item.name
        })
      }
    })
  },
  methods: {
    initMap() {
      this.map = new BMap.Map('baiduMap', { enableMapClick: true });
      //this.map.addControl(new BMap.MapTypeControl());
      this.map.addControl(new BMap.NavigationControl());
      this.map.enableScrollWheelZoom(true);

      this.fetchData();
    },
    addClickHandler(content, marker) {
      var self = this;
      marker.addEventListener('click', function(e) {
        self.openInfo(content, e);
      });
    },
    openInfo(content, e) {
      var p = e.target;
      var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
      var infoWindow = new BMap.InfoWindow(content, {
        width: 250,
        height: 200,
        title: '',
        enableMessage: true
      });
      this.map.openInfoWindow(infoWindow, point);
    },
    fetchData() {
      this.map.clearOverlays();

      getDeviceMap(this.listQuery).then(response => {
        //this.map.clearOverlays();
        this.total = response.data.total;
        this.list = response.data.items;
        let longitude, latitude;
        let pointArr = [];
        let pointIcon = [];
        let tipArr = [];
        for (let i = 0; i < this.list.length; i++) {
          longitude = this.list[i].longitude;
          latitude = this.list[i].latitude;
          if (longitude === undefined || latitude === undefined
            || longitude == '0' || latitude == '0') {
            continue;
          }
          var gpsPoint = new BMap.Point(longitude, latitude);
          if (gpsPoint.lng == 0 || gpsPoint.lat == 0) {
            var marker = new BMap.Marker(gpsPoint);
            this.map.addOverlay(marker);
            continue;
          }
          var content = '设备手机：' + this.list[i].phone_number;
          content += '<br/>设备类别：' + this.list[i].device_type_;
          content += '<br/>用户厂家：' + this.list[i].custom_id_;
          content += '<br/>地 域 码：' + this.list[i].area_code_;
          content += '<br/>组内地址：' + this.list[i].local_address;
          content += '<br/>温　　度：' + this.list[i].temperature + '°C';
          content += '<br/>参　　数：' + this.list[i].p1 + ' ' + this.list[i].p2 + ' ' + this.list[i].p3 + ' ' + this.list[i].p4;
          var working_state = this.list[i].working_state;
          var processing_state = this.list[i].processing_state;
          var fuel_level_state = this.list[i].fuel_level_state;
          var fuel_pressure_state = this.list[i].fuel_pressure_state;
          content += '<br/>工作状态：' + fWorkingState(working_state);
          content += '<br/>运行状态：' + fProcessingState(processing_state);
          content += '<br/>油位状态：' + fFuelLevelState(fuel_level_state);
          content += '<br/>油压状态：' + fFuelPressureState(fuel_pressure_state);
          content += '<br/>所属客户：' + (this.list[i].custom_id_ == undefined ? '未分配' : this.list[i].custom_id_);
          
          //var marker = new BMap.Marker(gpsPoint);
          //this.map.addOverlay(marker);
          //this.addClickHandler(content, marker);
          // 图标颜色
          // 油箱故障（红色）
          // 油压故障（橙色）
          // 运行休止（蓝色）
          // 设备正常，指油箱正常、油压正常和运行正常（绿色）
          var icon_color = ''
          if (working_state == 2) {
            icon_color = 'green'
          } else if (working_state == 3) {
            icon_color = 'red'
          } else if (fuel_pressure_state == 1) {
            icon_color = 'yellow'
          } else if (working_state == 1) {
            icon_color = 'blue'
          } else {
            icon_color = 'gray'
          }
          pointIcon.push(new BMap.Symbol(BMap_Symbol_SHAPE_POINT, {
            scale: 1.1,
            strokeWeight: 2,
            strokeColor: icon_color,
            fillColor: icon_color,
            fillOpacity: 1
          }))
          pointArr.push(gpsPoint);
          tipArr.push(content);
        }
        var self = this;

        // 点聚功能 see: http://developer.baidu.com/map/jsdemo.htm#c1_4
        function markerClustersPoint(markers) {
          if (self.map.getZoom() == 19) {
            // 最高缩放比例时不聚合？？？
            //self.map.clearOverlays()
            //return
          }
          if (self.markerClusterer != undefined) {
            self.markerClusterer.clearMarkers()
          }
          self.markerClusterer = new BMapLib.MarkerClusterer(self.map, {
            markers: markers,
            minClusterSize: 1,
            styles: [{
              url: 'http://api.map.baidu.com/images/blank.gif',
              size: new BMap.Size(0, 0)
            }]
          })
          /*
          // 拿到所有的聚合点
          var mk = markerClusterer._clusters;
          var oldmk = [];
          for (var i = 0; i < mk.length; i++) {
            var mCount = mk[i]._markers.length
            //console.log(mCount)
            //oldmk.push(addMarker(mk[i]._center, mCount))
          }*/
        }

        // 标记marker，显示infowindow
        function addMarker(point, count) {
          var marker = new BMap.Marker(point, {
            icon: new BMap.Symbol(BMap_Symbol_SHAPE_CIRCLE, {
              scale: 10,
              strokeWeight: 1,
              strokeColor: 'white',
              fillColor: 'red',
              fillOpacity: 0.99
            })
          })
          var label = new BMap.Label('[' + count + ']', { offset: new BMap.Size(20,-10) })
          marker.setLabel(label)
          
          var info = [
              "<div class='infotip'>",
                  "<div class='circle'><span style='background-color:blue'>",
                  count,
                  "</span></div>",
                  "<div class='msg'><span class='title'>人数：</span>"+ 10 + "<br/><span class='title'>提示：</span>" + 'this is a test' +"</div><span class='arrows'></span>" ,
              "</div>",
          ].join('');

          var infoWindow = new BMap.InfoWindow(info, {
            maxWidth: 100,
            maxHeight: 50
          })
          marker.addEventListener("click", function() {
            self.map.openInfoWindow(infoWindow, point)
          })
          self.map.addOverlay(marker)
          return marker
        }
        
        // 坐标修正
        var markers = []
        var translateCallback = function (data) {
          if (data.status === 0) {
            for (var i = 0; i < data.points.length; i++) {
              //var marker = new BMap.Marker(data.points[i]);
              var marker = new BMap.Marker(data.points[i], { icon: pointIcon[i] })
              self.addClickHandler(tipArr[i], marker)
              //self.map.addOverlay(marker);
              markers.push(marker)
              //var label = new BMap.Label("修正后的坐标",{offset:new BMap.Size(20,-10)});
              //marker.setLabel(label);
              if (i == 0) {
                self.map.setCenter(data.points[i]);
              }
            }
            // 系统默认
            self.map.clearOverlays()
            if (self.markerClusterer != undefined) {
              self.markerClusterer.clearMarkers()
            }
            self.markerClusterer = new BMapLib.MarkerClusterer(self.map, { markers: markers })
            //console.log(markerClusterer.clearMarkers)
            // 自定义
            //markerClustersPoint(markers)
          }
        }
        if (pointArr.length > 0) {
          this.map.centerAndZoom(pointArr[0], 18);
          var convertor = new BMap.Convertor();
          convertor.translate(pointArr, 1, 5, translateCallback);
        } else {
          this.map.centerAndZoom(new BMap.Point(116.4035, 39.915), 8);
        }
        // 地图缩放重新计算聚合点
        this.map.addEventListener('zoomend', function() {
          self.map.clearOverlays()
          //markerClustersPoint(markers)
        })
      });
    },
    handlerFilter() {
      this.listQuery.state_x = 0
      this.fetchData()
    },
    handlerClickType(n) {
      this.listQuery.state_x = n
      this.fetchData()
    }
  }
}
</script>
