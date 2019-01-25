<template>
  <div class="app-container calendar-list-container" id="deviceList">
    <div class="filter-container" style="padding-bottom: 15px;">
      <el-input @keyup.enter.native="handleFilter" style="width: 100px;" class="filter-item" placeholder="按公司名进行搜索" v-model="listQuery.corp_name">
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
      <el-button class="filter-item" type="primary" icon="search" @click="handleFilter">搜索</el-button>
      <el-button v-if="auth.sys == true" class="filter-item" type="primary" :disabled="!this.auth.edit" icon="plus" @click="handleNew">新增设备</el-button>
      <el-button v-if="auth.sys == true" class="filter-item" type="primary" :disabled="!this.auth.edit" @click="handleImportExcel">批量导入</el-button>
      <el-button class="filter-item" type="primary" :disabled="!this.auth.at" @click="handleBatchLube">批量打油</el-button>
      <el-button class="filter-item" type="primary" :disabled="!this.auth.edit" @click="handleExportExcel(1)">导出</el-button>
      <el-button class="filter-item" type="primary" :disabled="!this.auth.edit" @click="handleExportExcel(2)">全部导出</el-button>

      <div style="float:right;padding-top:10px;">
        <a href="#/device/map"><icon-svg icon-class="map"></icon-svg></a>
        <icon-svg icon-class="list" style="color:#1E90FF;margin-left:5px;"></icon-svg>
      </div>
    </div>
    
    <!-- 进度条 -->
    <el-progress v-show="waiting" style="z-index:100000;" :text-inside="true" :percentage="waiting_n" :show-text="false"></el-progress>

    <el-table :data="list" v-loading.body="listLoading" @filter-change="filterHeader" 
      element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%"
      @selection-change="handleSelectionChange" @header-click="headerClick">
      <el-table-column type="selection" width="30" align="center">
      </el-table-column>
      <el-table-column label="手机号" width="90">
        <template scope="scope">{{scope.row.phone_number}}</template>
      </el-table-column>
      <el-table-column label="设备用户">
        <template scope="scope">{{scope.row.custom_id_}}</template>
      </el-table-column>
      <el-table-column label="设备类别" width="60">
        <template scope="scope">{{scope.row.device_type_}}</template>
      </el-table-column>
      <el-table-column label="生成厂家">
        <template scope="scope">{{scope.row.manufacturer_}}</template>
      </el-table-column>
      <el-table-column label="地域码" width="60">
        <template scope="scope">{{scope.row.area_code_}}</template>
      </el-table-column>
      <el-table-column label="设备流水号" width="70">
        <template scope="scope">{{scope.row.local_address}}</template>
      </el-table-column>
      <el-table-column label="设备标签" width="85">
        <template scope="scope">{{scope.row.tag}}</template>
      </el-table-column>
      <el-table-column label="温度" width="40" align="right">
        <template scope="scope">{{scope.row.temperature}}</template>
      </el-table-column>
      <el-table-column label="润滑次数" width="55" align="right">
        <template scope="scope">{{scope.row.count_num}}</template>
      </el-table-column>
      <el-table-column prop="processing_state" label="运行状态" width="75"align="center" column-key="processing_state" :filter-multiple="false" :filters="[{text:'休止',value:'1'},{text:'润滑状态',value:'2'},{text:'低温保护',value:'4'},{text:'状态设置',value:'5'}]">
        <template scope="scope">{{scope.row.processing_state_}}</template>
      </el-table-column>
      <el-table-column prop="fuel_level_state" label="油位状态" width="75" align="center" column-key="fuel_level_state" :filter-multiple="false" :filters="[{text:'正常液位',value:'0'},{text:'预警液位',value:'1'},{text:'低液位',value:'2'}]">
        <template scope="scope">{{scope.row.fuel_level_state_}}</template>
      </el-table-column>
      <el-table-column prop="fuel_pressure_state" label="油压状态" width="75" align="center" column-key="fuel_pressure_fault" :filter-multiple="false" :filters="[{text:'正常',value:'0'},{text:'报警',value:'1'},{text:'润滑到压',value:'2'},{text:'润滑不到压',value:'3'}]">
        <template scope="scope">{{scope.row.fuel_pressure_state_}}</template>
      </el-table-column>
      <el-table-column label="休止时间" width="60" align="center">
        <template scope="scope">
          <span v-if="scope.row.processing_state == 1">{{scope.row.stop_time}}</span>
          <span v-else style="font-weight:700;color:red;">{{scope.row.stop_time}}</span>
        </template>
      </el-table-column>
      <el-table-column label="润滑时间" width="60" align="center">
        <template scope="scope">
          <span v-if="scope.row.processing_state == 2">{{scope.row.run_time}}</span>
          <span v-else style="font-weight:700;color:red;">{{scope.row.run_time}}</span>
        </template>
      </el-table-column>
      <el-table-column label="到压时间" width="60" align="center">
        <template scope="scope">
          <span>{{scope.row.pressure_time}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="通信状态" width="75" column-key="status" :filter-multiple="false" :filters="[{text:'未初始化',value:'10'},{text:'在线',value:'20'},{text:'离线',value:'30'}]">
        <template scope="scope">{{scope.row.status_}}</template>
      </el-table-column>
      <el-table-column label="远程操作" width="195">
        <template scope="scope">
          <el-button type="text" style="margin-left:0px;" @click="handleAlarm(scope.row)">查看</el-button><font color=gray>&nbsp;|&nbsp;</font>
          <el-button type="text" style="margin-left:0px;" @click="handleAt('LUBE', '打油', scope.row)" :disabled="scope.row.at_lube == 0 ">
            <span v-if="scope.row.at_lube == 1">打油</span>
            <span v-else-if="scope.row.at_lube == 2" style="color:red;">打油</span>
            <span v-else>打油</span>
          </el-button><font color=gray>&nbsp;|&nbsp;</font>
          <el-button type="text" style="margin-left:0px;" @click="handleAt('STOP', '停止运行', scope.row)" :disabled="scope.row.dis_at_stop">停止润滑</el-button><font color=gray>&nbsp;|&nbsp;</font>
          <el-button type="text" style="margin-left:0px;" @click="handleLocation(scope.row)">定位</el-button>&nbsp;
          <el-popover placement="bottom" width="300" v-model="scope.row.popMoreCmd" >
            <div style="margin: 0">
              <el-button v-if="auth.sys == true" type="text" @click="handleInit(scope.row)" :disabled="scope.row.dis_at_init">设备初始化</el-button>
              <font v-if="auth.sys == true" color=gray>&nbsp;&nbsp;|&nbsp;</font>
              <el-button type="text" @click="handleAt('UPDATE', '同步', scope.row)" :disabled="scope.row.dis_at_sync">同步</el-button><font color=gray>&nbsp;&nbsp;|&nbsp;</font>
              <el-button v-if="auth.sys == true" type="text" @click="handleAlloc(scope.row)"  :disabled="!auth.edit">分配</el-button>
              <font v-if="auth.sys == true" color=gray>&nbsp;&nbsp;|&nbsp;</font>
              <el-button type="text" @click="handleEditParams(scope.row)" :disabled="scope.row.dis_at_params">设置参数</el-button><font color=gray>&nbsp;&nbsp;|&nbsp;</font>
              <el-button v-if="auth.at || auth.type == 0 || auth.type == 1" type="text" @click="handleShowSetId(scope.row)">设置ID</el-button>
              <font v-if="auth.type == 0 || auth.type == 1" color=gray>&nbsp;&nbsp;|&nbsp;</font>
              <el-button v-if="auth.edit || auth.type == 0 || auth.type == 1" type="text" @click="handleEditTag(scope.row)">编辑标签</el-button>
            </div>
            <el-button slot="reference" type="text">
              <i class="el-icon-arrow-down" style="color:gray;"></i>
            </el-button>
	        </el-popover>
        </template>
      </el-table-column>
    </el-table>

    <div v-show="!listLoading" class="pagination-container" style="padding-top: 15px;">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page"
        :page-sizes="[10,20,30, 50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <!-- 新增设备 -->
    <el-dialog :title="dlgNewTitle" :visible.sync="dlgNewShow" :close-on-click-modal="false">
      <el-form :model="newDevice" ref="newDevice" :rules="rules_new" label-width="120px">
        <el-form-item label="设备手机号" prop="phone_number">
          <el-input v-model="newDevice.phone_number"></el-input>
        </el-form-item>
        <el-form-item v-if="dlgNewTitle == '新增设备'" label="所属客户" prop="custom_id">
          <el-select style="width:100%;" v-model="newDevice.custom_id" filterable remote placeholder="请输入关键词进行搜索" :remote-method="remoteSearchCustom" :loading="loading">
            <el-option v-for="item in customSearchList" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备标签" prop="tag_">
          <el-select style="width:100%;" v-model="newDevice.tag_" multiple filterable allow-create default-first-option placeholder="请输入设备标签">
            <el-option v-for="item in tag_options" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dlgNewShow = false">取 消</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分配" :model="deviceAlloc" :visible.sync="dlgAllocShow" :close-on-click-modal="false">
      <el-form>
        <el-form-item>
          <el-radio-group v-model="deviceAlloc.alloc_type" @change="handleCustomSelectChange">
            <el-row>
              <el-col :span="24">
                <el-radio label="to">转移至
                  <el-select :disabled="dis_select_custom" style="width:100%;" v-model="deviceAlloc.custom_id" filterable remote placeholder="请输入关键词进行搜索" :remote-method="remoteSearchCustom" :loading="loading">
                    <el-option v-for="item in customSearchList" :key="item.id" :label="item.name" :value="item.id">
                    </el-option>
                  </el-select>
                </el-radio>
              </el-col>
            </el-row>
            <el-row style="margin-top:15px;">
              <el-col :span="24">
                <el-radio label="cancel">取消关联</el-radio>
              </el-col>
            </el-row>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-checkbox style="color:red;" v-model="deviceAlloc.reset_data">是否重置数据</el-checkbox>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dlgAllocShow = false">取 消</el-button>
        <el-button type="primary" @click="allocSave">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="设置参数" :visible.sync="dlgParamsShow" :close-on-click-modal="false">
      <el-form label-width="150px" :model="deviceParams">
        <el-form-item label="参数名">
          <div style="color:red;width:30%;float:left;text-align:center;font-weight:700;">当前值</div>
          <div style="color:red;width:30%;float:left;text-align:center;font-weight:700;">修改</div>
        </el-form-item>
        <el-form-item label="P1">
          <el-input :disabled="true" style="width:30%;" v-model="deviceParams.p1_"></el-input>
          <el-input style="width:30%;" v-model="deviceParams.p1"></el-input>&nbsp;(1~30小时)
        </el-form-item>
        <el-form-item label="P2">
          <el-input :disabled="true" style="width:30%;" v-model="deviceParams.p2_"></el-input>
          <el-input style="width:30%;" v-model="deviceParams.p2"></el-input>&nbsp;(0~99)
        </el-form-item>
        <el-form-item label="P3">
          <el-input :disabled="true" style="width:30%;" v-model="deviceParams.p3_"></el-input>
          <el-input style="width:30%;" v-model="deviceParams.p3"></el-input>&nbsp;(1~99分钟)
        </el-form-item>
        <el-form-item label="P4">
          <el-input :disabled="true" style="width:30%;" v-model="deviceParams.p4_"></el-input>
          <el-input style="width:30%;" v-model="deviceParams.p4"></el-input>&nbsp;(-50℃~0℃)
        </el-form-item>
        <el-form-item label="版本号">
          <el-input :disabled="true" style="width:30%;" v-model="deviceParams.version_"></el-input>
          <el-input style="width:30%;" v-model="deviceParams.version"></el-input>&nbsp;(0,1,2,3,4,5,6,7)
        </el-form-item>
        <el-form-item label="主动上传周期">
          <el-input :disabled="true" style="width:30%;" v-model="deviceParams.upload_period_"></el-input>
          <el-input style="width:30%;" v-model="deviceParams.upload_period"></el-input>&nbsp;(30~255秒)
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <font color=red>没有变化则无需填写</font>&nbsp;
        <el-button type="primary" @click="handleSaveParams(1)">修改参数</el-button>
        <el-button type="primary" @click="handleSaveParams(2)">修改版本号</el-button>
        <el-button type="primary" @click="handleSaveParams(3)">修改上次周期</el-button>
      </div>
    </el-dialog>

    <!-- 设置ID -->
    <el-dialog :title="dlgSetIdTitle" :visible.sync="dlgSetIdShow" :close-on-click-modal="false">
      <el-form label-width="150px" :model="dlgSetIdData">
        <el-form-item label="项目">
          <div style="color:red;width:30%;float:left;text-align:center;font-weight:700;">当前值</div>
          <div style="color:red;width:30%;float:left;text-align:center;font-weight:700;">修改</div>
        </el-form-item>
        <el-form-item label="设备类别">
          <el-input :disabled="true" style="width:30%;" v-model="dlgSetIdData.device_type_"></el-input>
          <el-input :disabled="!(auth.type == 0)" style="width:30%;" v-model="dlgSetIdData.device_type"></el-input> (0000~FFFF)
        </el-form-item>
        <el-form-item label="用户编码" style="display:none;">
          <el-input :disabled="true" style="width:30%;" v-model="dlgSetIdData.device_user_"></el-input>
          <el-input :disabled="!(auth.type == 0)" style="width:30%;" v-model="dlgSetIdData.device_user"></el-input> (0000~FFFF)
        </el-form-item>
        <el-form-item label="生成厂家">
          <el-input :disabled="true" style="width:30%;" v-model="dlgSetIdData.manufacturer_"></el-input>
          <el-input :disabled="!(auth.type == 0)" style="width:30%;" v-model="dlgSetIdData.manufacturer"></el-input> (0000~FFFF)
        </el-form-item>
        <el-form-item label="地域码">
          <el-input :disabled="true" style="width:30%;" v-model="dlgSetIdData.area_code_"></el-input>
          <el-input style="width:30%;" v-model="dlgSetIdData.area_code"></el-input> (0000~FFFF)
        </el-form-item>
        <el-form-item label="设备流水号">
          <el-input :disabled="true" style="width:30%;" v-model="dlgSetIdData.local_address_"></el-input>
          <el-input style="width:30%;" v-model="dlgSetIdData.local_address"></el-input> (0000~FFFF)
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <font color=red>没有变化则无需填写</font>&nbsp;
        <el-button type="primary" @click="handleSetId()">确认设置</el-button>
      </div>
    </el-dialog>

    <!-- 设备初始化 -->
    <el-dialog title="设备初始化" :visible.sync="dlgInitShow" :close-on-click-modal="false">
      <el-form :model="initData" ref="initData" :rules="rules_init" label-width="190px">
        <el-form-item label="设备手机号" class="init_item">
          {{initData.phone_number}}
        </el-form-item>
        <el-form-item label="设备类型" class="init_item" prop="device_type">
          <el-select v-model="initData.device_type" placeholder="请选择" style="width:100%;" @change="onDeviceTypeChange">
            <el-option v-for="item in deviceTypeList" :key="item.code" :label="item.name" :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户编码" class="init_item" prop="device_user" style="display:none;">
          <el-select v-model="initData.device_user" placeholder="请选择" style="width:100%;">
            <el-option v-for="item in deviceUserList" :key="item.code" :label="item.name" :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生产厂家" class="init_item" prop="manufacturer">
          <el-select v-model="initData.manufacturer" placeholder="请选择" style="width:100%;" @change="onManufacturerChange">
            <el-option v-for="item in manufacturer_options" :key="item.code" :label="item.name" :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地域" class="init_item" prop="area_id">
          <el-select filterable v-model="initData.area_id" placeholder="请选择" style="width:100%;">
            <el-option v-for="item in areacode_options" :key="item.code" :label="item.name" :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备流水号(0000~FFFF)" class="init_item" prop="local_address">
          <el-input v-model="initData.local_address"></el-input>
        </el-form-item>
        <el-form-item label="设备编号(ID)" class="init_item">
          {{initData.generate_id}}
        </el-form-item>
        <div style="font-weight:700;font-size:16px;color:#1f2d3d;margin-top:15px;margin-bottom:15px;">参数初始化</div>
        <el-row>
            <el-col :span="12">
              <el-form-item label-width="120px" label="P1"  class="init_item">
                <el-input-number v-model="initData.p1" :min="0" :max="30"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label-width="120px" label="P2" class="init_item">
                <el-input-number v-model="initData.p2" :min="0" :max="99"></el-input-number>
              </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
              <el-form-item label-width="120px" label="P3" class="init_item">
                <el-input-number v-model="initData.p3" :min="0" :max="99"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label-width="120px" label="P4" class="init_item">
                <el-input-number v-model="initData.p4" :min="-50" :max="0"></el-input-number>
              </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
              <el-form-item label-width="120px" label="版本" class="init_item">
                <el-input-number v-model="initData.version" :min="0" :max="7"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label-width="120px" label="上传周期" class="init_item">
                <el-input-number v-model="initData.upload_period" :min="30" :max="65535"></el-input-number>
              </el-form-item>
            </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dlgInitShow = false">取 消</el-button>
        <el-button type="primary" @click="handleInitDevice">确 定</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="报警日志" :visible.sync="dlgAlarmShow" size="large">
      <el-table :data="alarmData.list">
        <el-table-column label="设备号" width="150">
          <template scope="scope">{{scope.row.device_id}}</template>
        </el-table-column>
        <el-table-column label="预警类型" width="120">
          <template scope="scope">{{scope.row.type_}}</template>
        </el-table-column>
        <el-table-column label="报警提醒">
          <template scope="scope">{{scope.row.content}}</template>
        </el-table-column>
        <el-table-column label="报警时间" width="180">
          <template scope="scope">{{scope.row.add_time}}</template>
        </el-table-column>
      </el-table>
      <div v-show="!listLoading" class="pagination-container" style="padding-top: 15px;">
        <el-pagination @size-change="handleSizeChange2" @current-change="handleCurrentChange2" :current-page.sync="alarmData.listQuery.page"
          :page-sizes="[10,20,30, 50]" :page-size="alarmData.listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="alarmData.total">
        </el-pagination>
      </div>
    </el-dialog>

    <el-dialog title="地图" :visible.sync="dlgMapShow">
      <div id="baiduMap" class="app-container" style="height:350px;width:100%;margin-top:5px;"></div>
    </el-dialog>

    <el-dialog title="批量导入" :visible.sync="dlgImport">
      <el-upload :action="postFileUrl" 
        accept="csv"
        :show-file-list="true"
        :on-success="importResult"
        :file-list="fileList">
        <el-button size="small" type="primary">点击上传要导入的设备</el-button>
        <div slot="tip" class="el-upload__tip">
          只能上传csv格式的文件，且不超过1MB
          &nbsp;&nbsp;(<a :href="tpl_url" target="_blank" style="color:green;">导入模板</a>)
        </div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { Loading } from 'element-ui';
import { getDeviceList, saveDevice, saveParams, setId, saveAlloc, initDevice, atCmd, searchCustom } from '@/api/device';
import { fWorkingState, fProcessingState, fFuelLevelState, fFuelPressureState } from '@/api/common';
import { getDictList } from '@/api/setting'
import { getMessageList } from '@/api/message';
function sec_to_time(s) {
  var t
  if (s == undefined || s == '') {
    t = '00:00'
  } else if (s > -1) {
    var hour = Math.floor(s/3600);
    var min = Math.floor(s/60) % 60;
    var sec = s % 60;
    if(hour < 10) {
        t = '0'+ hour + ":";
    } else {
        t = hour + ":";
    }

    if(min < 10){t += "0";}
    t += min + ":";
    if(sec < 10){t += "0";}
    t += sec.toFixed(2);
  }
  return t;
}
export default {
  computed: {
    ...mapGetters([
      'name',
      'roles',
      'user_type'
    ])
  },
  data() {
    var validateMobile = (rule, value, callback) => {
      if (!(/^1\d{10}$/.test(value))) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };
    var validateHex4 = (rule, value, callback) => {
      if (/^[0-9A-F]{4}$/.test(value)) {
        callback();
      } else {
        callback(new Error('设备流水号范围为0000~FFFF'));
      }
    };

    return {
      list: null,
      listLoading: true,
      total: null,
      refresh_seconds: 60,
      timer: 0,
      timer_process: undefined,
      areacode_options: [],
      listQuery: {
        page: 1,
        limit: 10,
        device_id: undefined,
        corp_name: undefined,
        working_state: undefined,
        processing_state: undefined,
        fuel_level_state: undefined,
        fuel_pressure_state: undefined,
        device_type: undefined,
        area_code: undefined,
        status: undefined,
        tag: undefined
      },
      alarmData: {
        list: null,
        total: null,
        listQuery: {
          page: 1,
          limit: 10,
          device_id: undefined
        }
      },
      tag_options: [],
      newDevice: {
        device_id: '',
        phone_number: '',
        custom_id: '',
        tag_: [],
        tag: '',
        link_: ''
      },
      editParamsType: '', // 当前要设置的设备参数类型
      editParamsTitle: {
        p: '修改参数',
        v: '修改版本号',
        u: '修改上传周期'
      },
      dis_select_custom: true,
      deviceAlloc: {// 设备分配
        id: '',
        alloc_type: '',
        custom_id: '',
        reset_data: false
      },
      deviceParams: {
        id: '',
        type: '',
        p1_: '',
        p1: '',
        p2_: '',
        p2: '',
        p3_: '',
        p3: '',
        p4_: '',
        p4: '',
        version_: '',
        version: '',
        upload_period_: '',
        upload_period: ''
      },
      initData: {
        id: '',
        phone_number: '',
        device_type: '',
        manufacturer: '',
        device_user: '',
        area_id: '',
        area_code: '',
        local_address: '',
        p1: '',
        p2: '',
        p3: '',
        p4: '',
        version: '',
        upload_period: ''
      },
      rules_new: {
        phone_number: [
          { required: true, message: '请输入设备手机号', trigger: 'blur' },
          { validator: validateMobile, trigger: 'blur' }
        ],
        device_id: [
          { required: true, message: '请输入设备编号', trigger: 'blur' }
        ]
      },
      rules_init: {
        local_address: [
          { required: true, message: '请输入设备流水号', trigger: 'blur' },
          { validator: validateHex4, trigger: 'blur' }
        ]
      },
      deviceTypeList_all: [],
      deviceTypeList: [],
      manufacturer_options: [],
      manufacturer_devicetype: {},
      deviceUserList: [],
      dlgNewTitle: '',
      dlgNewShow: false,
      dlgAllocShow: false,
      dlgParamsShow: false,
      dlgInitShow: false,
      dlgAlarmShow: false,
      dlgMapShow: false,
      dlgImport: false,
      loading: false,
      loadingMask: undefined,
      customSearchList: [],
      map: undefined,
      fileList: [],
      postFileUrl: process.env.BASE_API + '/device/import',
      tpl_url: process.env.BASE_API + '/template.csv',
      selectIDS: '',
      waiting: false,
      waiting_n: 0,
      // 权限
      auth: {
        edit: true,
        at: true,
        sys: false,
        type: -1
      },
      //areacode_: {},
      //areacode_id: {},
      //areacode_val: {},
      dlgSetIdTitle: '',
      dlgSetIdShow: false,
      dlgSetIdData: {
        id: '',
        device_type_: '',
        device_type: '',
        manufacturer_: '',
        manufacturer: '',
        device_user_: '',
        device_user: '',
        area_code_: '',
        area_code: '',
        local_address_: '',
        local_address: ''
      }
    }
  },
  watch: {
    initData: {
      handler(val, oldVal) {
        this.initData.generate_id = this.initData.device_type
          + this.initData.manufacturer
          + this.initData.area_code
          + this.initData.local_address;
      },
      deep: true
    }
  },
  created() {
    this.auth.edit = this.roles.indexOf('device:edit') > -1
    this.auth.at = this.roles.indexOf('device:at') > -1
    if ('SYS' == this.user_type) {
      this.auth.sys = true
      this.auth.type = 0
    } else if ('CUSTOM' == this.user_type) {
      this.auth.type = 1
    } else if ('CUSTOM_USER' == this.user_type) {
      this.auth.type = 2
    }

    this.fetchData();
    getDictList({ type: 'dtype' }).then(response => {
      var items = response.data.items
      for (var a in items) {
        var item = items[a]
        this.deviceTypeList_all.push({
          code: item.code, name: item.code + ',' + item.name
        })
        this.deviceTypeList.push({
          code: item.code, name: item.code + ',' + item.name
        })
      }
    })
    getDictList({ type: 'manufacturer' }).then(response => {
      var items = response.data.items
      for (var a in items) {
        var item = items[a]
        this.manufacturer_options.push({
          code: item.code, name: item.code + ',' + item.name
        })
        this.manufacturer_devicetype[item.code] = item.extra
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
      this.map.addControl(new BMap.NavigationControl());
      this.map.enableScrollWheelZoom(true);
    },
    fetchData() {
      var self = this;
      this.listLoading = true;
      getDeviceList(this.listQuery).then(response => {
        this.total = response.data.total
        this.list = response.data.items
        this.refresh_seconds = response.data.refresh_seconds
        if (this.timer == 0 && this.refresh_seconds > 0) {
          this.timer = 1
          setInterval(function() {
            self.fetchData()
          }, this.refresh_seconds * 1000)
        }
        
        for (var i = 0; i < this.list.length; i++) {
          this.list[i].popMoreCmd = false;
          var status = this.list[i].status;
          this.list[i].status_ = status === 10 ? '未初始化' : status === 20 ? '在线' : '离线';
          if (status === 10) {
            this.list[i].working_state_ = '-';
            this.list[i].processing_state_ = '-';
            this.list[i].fuel_level_state_ = '-';
            this.list[i].fuel_tank_state_ = '-';
            this.list[i].count_num = '-';
            this.list[i].temperature = '-';
          } else {
            if (this.list[i].temperature !== undefined && this.list[i].temperature.length > 0) {
              this.list[i].temperature = this.list[i].temperature + '°C';
            } else {
              this.list[i].temperature = '-';
            }
            
            var working_state = this.list[i].working_state;
            var processing_state = this.list[i].processing_state;
            var fuel_level_state = this.list[i].fuel_level_state;
            var fuel_pressure_state = this.list[i].fuel_pressure_state;
            this.list[i].working_state_ = fWorkingState(working_state);
            this.list[i].processing_state_ = fProcessingState(processing_state);
            this.list[i].fuel_level_state_ = fFuelLevelState(fuel_level_state);
            this.list[i].fuel_pressure_state_ = fFuelPressureState(fuel_pressure_state);
            var generate_id = this.list[i].generate_id
            // 1若设备在线，且“运行状态”为“休止”，且“油压状态”和“油位状态”皆为正常，打油enabled，打油标为蓝色，其余case皆为disabled
            // 2若设备在线，且“运行状态”为“休止”，且“油压状态”和“油位状态”并不全为正常（即“油压状态”出现了“预警油压”或者“油位状态”出现了“预警”或者“低”），
            //  打油enabled，打油标为红色（单击打油之后跳出警告对话框，大意为：目前出现XXXX故障，继续打油会出现问题，请问是否要继续，
            //  然后两个按钮，继续，取消，点继续按照正常打油流程走），其余case皆为disabled，
            if (status == 20 && processing_state == 1) {
              if (fuel_level_state != 1 && fuel_pressure_state != 1) {
                // 蓝色
                this.list[i].at_lube = 1;
              } else {
                // 红色
                this.list[i].at_lube = 2;
              }
            } else {
              this.list[i].at_lube = 0;
            }
            // 若设备在线，同步enabled，其余case皆为disabled
            if (status == 20) {
              this.list[i].dis_at_sync = false;
            } else {
              this.list[i].dis_at_sync = true;
            }
            // 若设备在线，且ID是0000000000000000，且运行状态为非“低温保护”，且“油压状态”和“油位状态”为非报警状态，
            // 初始化enabled，其余case皆为disabled。
            if (status == 20 && generate_id == '0000000000000000' 
              && processing_state != 4
              && fuel_level_state != 1
              && fuel_pressure_state != 1) {
              this.list[i].dis_at_init = false
            } else {
              this.list[i].dis_at_init = true
            }
            // 若设备在线，且“运行状态”为“润滑状态”，停止润滑enabled，其余情形disabled
            if (status == 20 && processing_state == 2) {
              this.list[i].dis_at_stop = false
            } else {
              this.list[i].dis_at_stop = true
            }
            // 若设备在线，且运行状态为非“低温保护”，且“油压状态”和“油位状态”为非报警状态，
            // 修改参数/修改版本号/修改上传周期enabled，其余情形disabled。
            if (status == 20 && processing_state != 4 && fuel_level_state != 1 && fuel_pressure_state != 1) {
              this.list[i].dis_at_params = false
            } else {
              this.list[i].dis_at_params = true
            }
          }
          if (!this.auth.at) {
            this.list[i].at_lube = 0
            this.list[i].dis_at_sync = true
            this.list[i].dis_at_init = true
            this.list[i].dis_at_stop = true
            this.list[i].dis_at_params = true
          }
          if (this.list[i].status == 10) {
            this.list[i].at_lube = 0
            this.list[i].dis_at_sync = true
            this.list[i].dis_at_stop = true
            this.list[i].dis_at_params = true
          }
        }
        this.listLoading = false;
      });
    },
    remoteSearchCustom(keyword_) {
      this.loading = true;
      if (keyword_ !== '') {
        this.customSearchList = [];
        searchCustom({ keyword: keyword_ }).then(response => {
          this.loading = false;
          if (response.code === 20000) {
            this.customSearchList = response.data.items;
          }
        });
      } else {
        this.customSearchList = [];
        this.loading = false;
      }
    },
    handleSizeChange(val) {
      this.listQuery.limit = val;
      this.fetchData();
    },
    handleCurrentChange(val) {
      this.listQuery.page = val;
      this.fetchData();
    },
    filterHeader(filters) {
      if (filters.processing_state !== undefined && filters.processing_state.length > 0) {
        this.listQuery.processing_state = filters.processing_state[0];
      } else {
        this.listQuery.processing_state = undefined;
      }
      if (filters.fuel_level_state !== undefined && filters.fuel_level_state.length > 0) {
        this.listQuery.fuel_level_state = filters.fuel_level_state[0];
      } else {
        this.listQuery.fuel_level_state = undefined;
      }
      if (filters.fuel_pressure_fault !== undefined && filters.fuel_pressure_fault.length > 0) {
        this.listQuery.fuel_pressure_fault = filters.fuel_pressure_fault[0];
      } else {
        this.listQuery.fuel_pressure_fault = undefined;
      }
      if (filters.status !== undefined && filters.status.length > 0) {
        this.listQuery.status = filters.status[0];
      } else {
        this.listQuery.status = undefined;
      }
      this.fetchData();
    },
    handleFilter() {
      this.fetchData();
    },
    resetTemp() {
      this.newDevice = {
        device_id: '',
        phone_number: '',
        custom_id: '',
        tag_: [],
        tag: '',
        link_: ''
      }
    },
    // 新增设备
    handleNew() {
      this.dlgNewTitle = '新增设备'
      this.resetTemp();
      this.dlgNewShow = true;
    },
    handleEditTag(row) {
      this.dlgNewTitle = '编辑设备 - ' + row.phone_number
      this.resetTemp()
      this.newDevice = {
        device_id: row.device_id,
        phone_number: row.phone_number,
        custom_id: row.custom_id,
        tag_: [],
        tag: row.tag,
        link_: ''
      }
      if (row.tag != undefined && row.tag.length > 0) {
        var arr = row.tag.split(',')
        for (var a in arr) {
          this.newDevice.tag_.push(arr[a])
        }
      }
      this.dlgNewShow = true
    },
    // 保存
    handleSave() {
      const self = this;
      this.$refs.newDevice.validate((valid) => {
        if (valid) {
          self.newDevice.tag = self.newDevice.tag_.join(',')
          var params = {
            device_id: self.newDevice.device_id,
            phone_number: self.newDevice.phone_number,
            custom_id: self.newDevice.custom_id,
            tag: self.newDevice.tag
          }
          saveDevice(params).then(response => {
            if (response.code === 20000) {
              self.$message({ message: '保存成功', type: 'success' });
              self.fetchData();
            } else {
              self.$message({ message: response.data, type: 'error' });
            }
            self.dlgNewShow = false;
            self.$refs.newDevice.resetFields();
          }, error => {
          })
        } else {
          return false;
        }
      });
    },
    loading_(show, tip) {
      if (this.timer_process != undefined) {
        clearInterval(this.timer_process)
      }
      if (this.loadingMask != undefined) {
        this.loadingMask.close()
        this.loadingMask = undefined
      }
      var self = this;
      if (show) {
        this.loadingMask = Loading.service({
          text: tip,
          //text: tip + '0秒',
          fullscreen: true
        });
        this.waiting_n = 0
        this.waiting = true
        var idx = 0
        var sec = 5000
        this.timer_process = setInterval(function() {
          //loadingMask.setText(tip + (++idx) + '秒')
          if (self.waiting_n <= 90) {
            self.waiting_n++
          }
        }, sec / 90);
      } else {
        this.waiting_n = 100
        this.waiting = false
      }
    },
    handleAt(type_, tip, row) {
      const self = this;
      var msg = '您是否确认' + tip + '?';
      var okText = '确定';
      var cancelText = '取消';
      if (type_ == 'LUBE') { // 打油
        var errMsg = '';
        // 油位油压非正常时提示：目前出现XXXX故障，继续打油会出现问题，请问是否要继续
        // fuel_level_state 油位状态：0-正常液位；1-预警液位；2-低液位
        if (row.fuel_level_state == 1) {
          errMsg = '油位故障(预警液位)'
        } else if (row.fuel_level_state == 2) {
          errMsg = '油位故障(低液位)'
        }
        // fuel_pressure_state 油压状态：0-正常；1-报警；2-润滑到压；3-润滑不到压
        if (row.fuel_pressure_state == 1) {
          if (errMsg.length > 0) {
            errMsg += '、'
          }
          errMsg += '油压故障(报警)'
        } else if (row.fuel_pressure_state == 2) {
          if (errMsg.length > 0) {
            errMsg += '、'
          }
          errMsg += '油压故障(润滑到压)'
        } else if (row.fuel_pressure_state == 3) {
          if (errMsg.length > 0) {
            errMsg += '、'
          }
          errMsg += '油压故障(润滑不到压)'
        }
        if (errMsg.length > 0) {
          okText = '继续'
          msg = '目前出现' + errMsg + '，继续打油会出现问题，请问是否要继续？'
        }
      }
      this.$confirm(msg, '提示', {
        confirmButtonText: okText,
        cancelButtonText: cancelText,
        type: 'info'
      }).then(() => {
        tip = tip + '中，请稍等...'
        this.loading_(true, tip)
        atCmd({ type: type_, id: row.device_id }).then(response => {
          self.loading_(false)
          if (response.code === 20000) {
            self.$message({ message: response.data, type: 'success' });
            self.fetchData();
          } else {
            self.$message({ message: response.data, type: 'error' });
          }
        }, error => {
          this.loading_(false)
        });
      }).catch((err) => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });
    },
    handleAlloc(row) {
      this.deviceAlloc.id = row.device_id;
      this.deviceAlloc.alloc_type = '';
      this.deviceAlloc.custom_id = '';
      this.deviceAlloc.reset_data = false;

      this.dlgAllocShow = true;
    },
    handleCustomSelectChange(val) {
      if (val === 'to') {
        this.dis_select_custom = false
      } else {
        this.dis_select_custom = true
      }
    },
    allocSave() {
      const self = this;
      this.loading_(true, '正在分配，请稍等...')
      self.dlgAllocShow = false;
      saveAlloc(this.deviceAlloc).then(response => {
        this.loading_(false, '正在分配，请稍等...')
        self.$message({ message: response.data, type: 'success' });
        self.fetchData();
      }, error => {
        this.loading_(false, '正在分配，请稍等...')
      });
    },
    handleEditParams(row) {
      this.deviceParams = {
        id: row.device_id,
        p1_: row.p1,
        p1: '',
        p2_: row.p2,
        p2: '',
        p3_: row.p3,
        p3: '',
        p4_: row.p4,
        p4: '',
        version_: row.version,
        version: '',
        upload_period_: row.upload_period,
        upload_period: ''
      };
      row.popMoreCmd = false;
      this.dlgParamsShow = true;
    },
    handleSaveParams(n) {
      this.dlgParamsShow = false;
      this.loading_(true, '正在设置参数，请稍等...')

      var params = {}
      if (n == 1) {
        params = {
          type: 'p',
          id: this.deviceParams.id,
          p1_: this.deviceParams.p1_,
          p1: this.deviceParams.p1,
          p2_: this.deviceParams.p2_,
          p2: this.deviceParams.p2,
          p3_: this.deviceParams.p3_,
          p3: this.deviceParams.p3,
          p4_: this.deviceParams.p4_,
          p4: this.deviceParams.p4
        }
      } else if (n == 2) {
        params = {
          type: 'v',
          id: this.deviceParams.id,
          version_: this.deviceParams.version_,
          version: this.deviceParams.version
        }
      } else if (n == 3) {
        params = {
          type: 'u',
          id: this.deviceParams.id,
          upload_period_: this.deviceParams.upload_period_,
          upload_period: this.deviceParams.upload_period
        }
      }
      
      saveParams(params).then(response => {
        this.loading_(false, '正在设置参数，请稍等...')
        this.$message({ message: response.data, type: 'success' });
        this.fetchData();
      }, error => {
        this.loading_(false, '正在设置参数，请稍等...')
      });
    },
    handleShowSetId(row) {
      this.dlgSetIdData = {
        id: row.device_id,
        device_type_: row.device_type,
        device_type: '',
        manufacturer_: row.manufacturer,
        manufacturer: '',
        device_user_: row.device_user,
        device_user: '',
        area_code_: row.area_code,
        area_code: '',
        local_address_: row.local_address,
        local_address: ''
      };
      row.popMoreCmd = false;
      this.dlgSetIdTitle = '设置ID - 【' + row.phone_number + '】'
      this.dlgSetIdShow = true;
    },
    handleSetId() {
      this.loading_(true, '正在设置ID，请稍等...')

      this.dlgSetIdShow = false;
      setId(this.dlgSetIdData).then(response => {
        this.$message({ message: response.data, type: 'success' });
        this.fetchData();
        this.loading_(false, '正在设置ID，请稍等...')
      }, _ => {
        this.loading_(false, '正在设置ID，请稍等...')
      });
    },
    onDeviceTypeChange(val) {
      this.manufacturer_options = []
      getDictList({ type: 'manufacturer', extra: val }).then(response => {
        var items = response.data.items.map(v => {
          this.manufacturer_options.push({
            code: v.code, name: v.code + ',' + v.name
          })
        })
      })
    },
    onManufacturerChange(val) {
      this.deviceTypeList = []
      if (this.manufacturer_devicetype[val] != undefined) {
        var code_list = this.manufacturer_devicetype[val]
        for (var a in this.deviceTypeList_all) {
          var item = this.deviceTypeList_all[a]
          if (code_list.indexOf(item.code) >= 0) {
            this.deviceTypeList.push(item)
          }
        }
      }
    },
    handleInit(row) {
      row.popMoreCmd = false;

      this.initData = {
        id: row.device_id,
        phone_number: row.phone_number,
        device_type: row.device_type,
        manufacturer: row.manufacturer,
        device_user: row.device_user,
        area_code: row.area_code,
        area_id: row.area_id,
        local_address: row.local_address,
        p1: row.p1,
        p2: row.p2,
        p3: row.p3,
        p4: row.p4,
        version: row.version,
        upload_period: row.upload_period
      };
      
      this.dlgInitShow = true;
    },
    handleInitDevice() {
      this.$refs['initData'].validate((valid) => {
        if (valid) {
          this.loading_(true, '正在初始化，请稍等...')
          initDevice(this.initData).then(response => {
            this.loading_(false, '正在初始化，请稍等...')
            if (response.code === 20000) {
              this.$message({ message: response.data, type: 'success' });
              this.fetchData();
            } else {
              this.$message({ message: response.data, type: 'error' });
            }
          }, error => {
            this.loading_(false, '正在初始化，请稍等...')
          });
          this.dlgInitShow = false;
        }
      });

    },
    queryAlarm() {
      getMessageList(this.alarmData.listQuery).then(response => {
        this.alarmData.total = response.data.total;
        this.alarmData.list = response.data.items;
        for (var i = 0; i < this.alarmData.list.length; i++) {
          var type = this.alarmData.list[i].type;
          this.alarmData.list[i].type_ = type == 10 ? '休止'
              : type == 31 || type == 32 || type == 33 ? '油箱故障'
              : type == 50 ? '低温保护'
              : type == 40 ? '设置参数'
              : '未知-' + type;
        }
      }, error => {});
    },
    handleAlarm(row) {
      this.dlgAlarmShow = true;
      this.alarmData.listQuery.device_id = row.device_id;
      this.queryAlarm();
    },
    handleSizeChange2(val) {
      this.alarmData.listQuery.limit = val;
      this.queryAlarm();
    },
    handleCurrentChange2(val) {
      this.alarmData.listQuery.page = val;
      this.queryAlarm();
    },
    handleLocation(row) {
      var longitude = row.longitude;
      var latitude = row.latitude;
      if (longitude === undefined || longitude.length == 0 || latitude === undefined) {
        this.$message({ message: '未定位成功', type: 'error' });
        return;
      }
      // 坐标修正 GPS->baidu map http://lbsyun.baidu.com/jsdemo.htm?a#a5_2
      self = this;
      this.dlgMapShow = true;
      setTimeout(function() {
        self.initMap();

        var gpsPoint = new BMap.Point(longitude, latitude);
        self.map.centerAndZoom(gpsPoint, 15);

        if (gpsPoint.lng == 0 && gpsPoint.lat == 0) {
          var marker = new BMap.Marker(gpsPoint);
          self.map.addOverlay(marker);
        }
        //var marker = new BMap.Marker(gpsPoint);
        //var labelgps = new BMap.Label("未转换的GPS坐标",{offset:new BMap.Size(20,-10)});
        //marker.setLabel(labelgps);
        //self.map.addOverlay(marker);

        //坐标转换完之后的回调函数
        var translateCallback = function (data) {
          if (data.status === 0) {
            
            var marker = new BMap.Marker(data.points[0]);
            self.map.addOverlay(marker);
            //var label = new BMap.Label("修正后的坐标",{offset:new BMap.Size(20,-10)});
            //marker.setLabel(label);
            self.map.setCenter(data.points[0]);
          }
        };

        var convertor = new BMap.Convertor();
        var pointArr = [];
        pointArr.push(gpsPoint);
        convertor.translate(pointArr, 1, 5, translateCallback);
      }, 200);
    },
    handleSelectionChange(val) {
      var ids = [];
      for (var a in val) {
        ids.push(val[a].device_id);
      }
      this.selectIDS = ids.join(',');
    },
    handleImportExcel() {
      this.dlgImport = true;
    },
    importResult(response, file, fileList) {
      if (response.code = 20000) {
        this.dlgImport = false;
        this.$message({ message: response.data, type: 'info' });
        this.fetchData();
      } else {
        this.$message({ message: response.data, type: 'error' });
      }
    },
    handleExportExcel(n) {
      if (n == 1) {
        if (this.selectIDS.length == 0) {
          this.$message({ message: '您未选择要导出的设备', type: 'error' });
        } else {
          window.open(process.env.BASE_API + '/device/down?ids=' + this.selectIDS)
        }
      } else {
        window.open(process.env.BASE_API + '/device/down?ids=ALL')
      }
    },
    handleBatchLube() {
      const self = this;
      const msg = '您是否确认对选中的设备进行打油?';
      this.$confirm(msg, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.loading_(true, '批量打油中，请稍等...')
        atCmd({ type: 'LUBE', ids: self.selectIDS }).then(response => {
          this.loading_(false, '批量打油中，请稍等...')
          if (response.code === 20000) {
            self.$message({ message: response.data, type: 'success' });
            self.fetchData();
          } else {
            self.$message({ message: response.data, type: 'error' });
          }
        }, error => {
          this.loading_(false, '批量打油中，请稍等...')
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消操作'
        });
      });
    },
    headerClick() {
    }
  }
};
</script>

<style>
.el-progress__text {
}
.el-loading-spinner svg {
  display: none
}
.el-table th>.cell {
}
.el-table .cell {
  padding-left: 3px;
  padding-right: 3px;
  font-size: 8px;
}
</style>

<style scoped>
.init_item {
  margin-bottom: 5px;
}
.el-dialog__body {
  padding: 10px 20px;
}
.el-button, .el-textarea__inner {
  font-size: 12px;
}
</style>