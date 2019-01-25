<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container" style="padding-bottom: 15px;">
      <el-button class="filter-item" type="primary" icon="plus" @click="handleAdd">新增</el-button>
    </div>
    
    <el-table :data="list" v-loading.body="listLoading" element-loading-text="拼命加载中" border fit highlight-current-row style="width: 100%">
      <el-table-column label="代码" width="150">
        <template scope="scope">{{scope.row.code}}</template>
      </el-table-column>
      <el-table-column label="名称">
        <template scope="scope">{{scope.row.name}}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template scope="scope">{{scope.row.read_time}}
          <el-button type="text" style="margin-left:0px;" @click="handleEdit(scope.row)">编辑</el-button>
          &nbsp;&nbsp;&nbsp;
          <el-button type="text" style="margin-left:0px;" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-show="!listLoading" class="pagination-container" style="padding-top: 15px;">
      <el-pagination layout="total" :total="total">
      </el-pagination>
    </div>

    <!-- 新增/编辑 -->
    <el-dialog :title="dlgTitle[dlgStatus]" :visible.sync="dlgEditShow" :close-on-click-modal="false">
      <el-form class="small-space" :model="temp" ref="temp" :rules="rules" label-width="130px">
        <el-form-item label="代码" prop="code">
          <span v-if="dlgStatus == 'update'">{{temp.code}}</span>
          <el-input v-else v-model="temp.code"></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <span v-if="dlgStatus == 'view'">{{temp.name}}</span>
          <el-input v-else v-model="temp.name"></el-input>
        </el-form-item>
        <el-form-item label="所属行业">
          <el-select style="width:100%;" :disabled="dlgStatus=='view'" multiple clearable v-model="custom_type_" placeholder="请选择所属行业">
            <el-option v-for="item in deviceTypeList" :key="item.code" :label="item.name" :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dlgEditShow = false">返 回</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { parseTime } from '@/utils/index'
import { getDictList, saveDict, deleteDict } from '@/api/setting'

export default {
  data() {
    var validateHex4 = (rule, value, callback) => {
      if (/^[0-9A-F]{4}$/.test(value)) {
        callback();
      } else {
        callback(new Error('行业代码范围为0000~FFFF'));
      }
    }

    return {
      list: null,
      listLoading: true,
      total: null,
      listQuery: {
        type: 'manufacturer'
      },
      custom_type_: [],
      deviceTypeList: [],
      dlgStatus: '',
      dlgTitle: {
        view: '查看生产厂家',
        update: '编辑生产厂家',
        create: '创建生产厂家'
      },
      dlgEditShow: false,
      temp: {
        type: 'manufacturer',
        op: 1,
        code: '',
        name: '',
        extra: ''
      },
      rules: {
        code: [
          { required: true, message: '请输入生产厂家代码', trigger: 'blur' },
          { validator: validateHex4, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchData();

    getDictList({type: 'dtype'}).then(response => {
      var items = response.data.items
      for (var a in items) {
        var item = items[a]
        this.deviceTypeList.push({
          code: item.code, name: item.name
        })
      }
    }, error => {})
  },
  methods: {
    resetTemp() {
      this.custom_type_ = [];
      this.temp = {
        type: 'manufacturer',
        op: 1,
        code: '',
        name: '',
        extra: ''
      }
      if (this.$refs['temp'] != undefined) {
        this.$refs['temp'].resetFields()
      }
    },
    fetchData() {
      this.listLoading = true;
      getDictList(this.listQuery).then(response => {
        this.total =response.data.total
        this.list = response.data.items
        this.listLoading = false
      }, error => {});
    },
    handleFilter() {
      this.fetchData()
    },
    handleAdd() {
      this.resetTemp()
      this.dlgStatus = 'create'
      this.dlgEditShow = true
    },
    handleEdit(row) {
      this.resetTemp()
      this.temp = {
        type: 'manufacturer',
        op: 0,
        code: row.code,
        name: row.name,
        extra: row.extra
      }
      this.custom_type_ = []
      if (row.extra != undefined && row.extra.length > 0) {
        var arr = row.extra.split(',')
        for (var a in arr) {
          this.custom_type_.push(arr[a])
        }
      }

      this.dlgStatus = 'update'
      this.dlgEditShow = true
    },
    handleDelete(row) {
      self = this;
      this.$confirm('您是否确认删除 - ' + row.code + ',' + row.name + '？')
        .then(_ => {
          deleteDict({
            type: 'manufacturer',
            code: row.code
          }).then(response => {
            self.$message({ message: response.data, type: 'info' });
            self.fetchData()
          });
        })
        .catch(_ => {
        });
    },
    handleSave() {
      this.$refs['temp'].validate((valid) => {
        if (valid) {
          this.temp.extra = this.custom_type_.join(',');
          saveDict(this.temp).then(response => {
            this.fetchData()
            this.$message({ message: response.data, type: 'info' })
            this.dlgEditShow = false
          }, error => {});
        }
      });
    }
  }
};
</script>
