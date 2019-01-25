<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="padding-bottom: 15px;">
      <el-input v-model="listQuery.device_id" style="width: 200px;" class="filter-item" placeholder="请输入设备号码">
      </el-input>
      <el-date-picker v-model="time2" type="datetimerange" :picker-options="pickerOptions" placeholder="选择时间范围" align="left">
      </el-date-picker>
      <el-button class="filter-item" type="primary" icon="search" @click="handleFilter">搜索</el-button>
      <el-button class="filter-item" type="primary" @click="handleExportExcel"><icon-svg icon-class="excel"></icon-svg>&nbsp;导出Excel</el-button>
    </div>
    
    <el-table :data="list" v-loading.body="listLoading" @filter-change="filterHeader" element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
      <el-table-column label="设备号" width="150">
        <template scope="scope">{{scope.row.device_id}}</template>
      </el-table-column>
      <el-table-column label="预警类型" width="120" prop="type" column-key="type" :filters="[{text:'油压故障',value:'油压故障'},{text:'油位故障',value:'油位故障'},{text:'油压油位故障',value:'油压油位故障'},{text:'低温保护',value:'低温保护'}]">
        <template scope="scope">{{scope.row.type_}}</template>
      </el-table-column>
      <el-table-column label="报警提醒">
        <template scope="scope">{{scope.row.content}}</template>
      </el-table-column>
      <el-table-column label="报警时间" width="180">
        <template scope="scope">{{scope.row.add_time}}</template>
      </el-table-column>
      <el-table-column label="查看时刻" width="180">
        <template scope="scope">{{scope.row.read_time}}
          <el-button v-if="scope.row.read_time == undefined" type="text" style="margin-left:0px;" @click="handleRead(scope.row)">确认已读</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-show="!listLoading" class="pagination-container" style="padding-top: 15px;">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="listQuery.page"
        :page-sizes="[10,20,30, 50]" :page-size="listQuery.limit" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import { parseTime } from '@/utils/index';
import { getMessageList, readMsg } from '@/api/message';

export default {
  data() {
    return {
      list: null,
      listLoading: true,
      total: null,
      listQuery: {
        page: 1,
        limit: 10,
        type: undefined,
        device_id: undefined,
        from_time: undefined,
        to_time: undefined
      },
      time2: undefined,
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      }
    }
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getMessageList(this.listQuery).then(response => {
        this.total = response.data.total;
        this.list = response.data.items;
        this.listLoading = false;

        for (var i = 0; i < this.list.length; i++) {
          var type = this.list[i].type;
          this.list[i].type_ = type;
        }
      }, error => {});
    },
    handleFilter() {
      if (this.time2 == undefined || this.time2.length != 2) {
        this.listQuery.from_time = undefined;
        this.listQuery.to_time = undefined;
      } else {
        if (this.time2[0] != undefined && this.time2[1] != undefined) {
          this.listQuery.from_time = parseTime(this.time2[0]);
          this.listQuery.to_time = parseTime(this.time2[1]);
        } else {
          this.listQuery.from_time = undefined;
          this.listQuery.to_time = undefined;
        }
      }
      this.fetchData();
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
      if (filters.type !== undefined && filters.type.length > 0) {
        this.listQuery.type = filters.type.join(',');
      } else {
        this.listQuery.type = undefined;
      }
      this.fetchData();
    },
    handleRead(row) {
      readMsg({id: row.id}).then(response => {
        if (response.code === 20000) {
          this.fetchData();
        }
      });
    },
    handleExportExcel() {
      var params = ''
      if (this.listQuery.device_id != undefined) {
        params += '&device_id=' + this.listQuery.device_id;
      }
      if (this.listQuery.from_time != undefined) {
        params += '&from_time=' + this.listQuery.from_time;
      }
      if (this.listQuery.to_time != undefined) {
        params += '&to_time=' + this.listQuery.to_time;
      }
      if (params.length > 0) {
        params = '?' + params.substring(1);
      }
      window.open(process.env.BASE_API + '/message/down' + params);
    }
  }
};
</script>
